package com.nci.gis.common.dynamic.classloader;

import com.nci.entity.RespondMessage;
import com.nci.gis.common.DeleteFileUtils;
import com.nci.gis.common.ErrorUtils;
import com.nci.gis.common.UploadFileUtils;
import com.nci.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * 服务类Service-自定义类装加器
 *
 * @since 1.0.0 2019年11月14日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Service
public class ClassLoaderService {

	private static final Logger _logger = LoggerFactory.getLogger(ClassLoaderService.class);


	@Value(value = "${3rd.upload-path}")
	String thirdPartyUploadPath;


	/**
	 * 获取所有加载的beans
	 *
	 * @return 返回所有已加载的beans
	 */
	public List<Map<String, Object>> beans() {
		return SpringContextUtil.getAllBean();
	}


	/**
	 * 卸载已经加载的指定模块（即jar）
	 *
	 * @param moduleName 模块（jar）名称
	 * @return 返回删除后，所有已加载的beans
	 */
	public List<Map<String, Object>> unloadModule(String moduleName) {
		if (ClassLoaderResponsity.getInstance().containsClassLoader(moduleName)) {
			ClassLoaderResponsity.getInstance().removeClassLoader(moduleName);
		}
		return beans();
	}

	/**
	 * 卸载指定目录下的所有模块（即jar）
	 *
	 * @param dir 待删除模块（jar）存放目录
	 * @return 返回删除后，所有已加载的beans
	 */
	public List<Map<String, Object>> unloadModuleInDir(String dir) {
		File directory = new File(dir);

		/*
		 * 1-- 参数检测，判断是否为文件夹
		 */
		if (!directory.exists()) {
			_logger.error("[{}] not found.", dir);
			return ErrorUtils.buildErrorMapList("[" + dir + "] not found.");
		}

		/*
		 * 2-- 获取待删除的所有moduleName
		 */
		List<String> jarModuleNamePathList = new ArrayList<>();
		recursionJarModuleNamePaths(dir, jarModuleNamePathList);

		/*
		 * 3-- 遍历卸载所有待删除的模块（jar）
		 */
		if (jarModuleNamePathList.size() > 0) {
			for (String path : jarModuleNamePathList) {
				unloadModule(path);
			}
		}

		_logger.info("unload jar in [{}] success.", dir);
		return SpringContextUtil.getAllBean();
	}


	/**
	 * 装载某个指定全路径的jar包
	 *
	 * @param jarPath jar包全路径，如'<jar-path>/xxxx.jar'
	 * @return  返回Spring应用上下文中所有Bean，不含非Spring应用的类
	 */
	public List<?> loadJar(String jarPath) {
		File jar = new File(jarPath);
		if (!jar.exists()) {
			_logger.error("jar file [{}] not found.", jarPath);
			return ErrorUtils.buildErrorList("jar file [" + jarPath + "] not found.");
		}
		if (!jar.getPath().endsWith(".jar")) {
			_logger.error("[{}] is not a jar.", jarPath);
			return ErrorUtils.buildErrorList("[" + jarPath + "] is not a jar.");
		}
		URI uri = jar.toURI();

		//String moduleName = jarPath.substring(jarPath.lastIndexOf("/")+1, jarPath.lastIndexOf("."));
		String jarName = jar.getName();
		String moduleName = jarName.substring(0, jarName.lastIndexOf(".jar"));
		try {
			if (ClassLoaderResponsity.getInstance().containsClassLoader(moduleName)) {
				ClassLoaderResponsity.getInstance().removeClassLoader(moduleName);
			}

			ModuleClassLoader classLoader = new ModuleClassLoader(
				new URL[]{uri.toURL()}, Thread.currentThread().getContextClassLoader());

			SpringContextUtil.getBeanFactory().setBeanClassLoader(classLoader);
			Thread.currentThread().setContextClassLoader(classLoader);
			classLoader.initBean();
			ClassLoaderResponsity.getInstance().addClassLoader(moduleName, classLoader);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		_logger.info("load [{}] success.", jarPath);
		return SpringContextUtil.getAllBean();
	}


	/**
	 * 装载指定路径下的所有jar包，包含子文件夹和子jar
	 *
	 * @param jarDir jar存放路径
	 * @return 返回Spring应用上下文中所有Bean，不含非Spring应用的类
	 */
	public List<?> loadJarInDir(String jarDir) {
		File directory = new File(jarDir);

		/*
		 * 1-- 参数检测，判断是否为文件夹
		 *  -目录不存在：返回异常
		 * 	-jar文件：直接用单个jar解析的方法去解析
		 *  -文件夹：遍历装载
		 */
		if (!directory.exists()) {
			_logger.error("[{}] not found.", jarDir);
			return ErrorUtils.buildErrorList("[" + jarDir + "] not found.");
		}

		if (!directory.isDirectory()) {
			String fPath = directory.getPath();
			if (fPath.endsWith(".jar")) {

				// 装载某个指定全路径的jar包
				loadJar(jarDir);
			} else {
				_logger.error("[{}] error parameter, must be a directory! or a whole jar path.", jarDir);
				return ErrorUtils.buildErrorList("[" + jarDir + "] error parameter, must be a directory! or a whole jar path.");
			}
		}

		/*
		 * 2-- 若是文件夹，循环遍历加载当前文件夹下所有jar
		 */
		List<String> jarPathList = new ArrayList<>();
		recursionJarPaths(jarDir, jarPathList);
		if (jarPathList.size() > 0) {
			for (String path : jarPathList) {
				loadJar(path);
			}
		}

		_logger.info("load jar in [{}] success.", jarDir);
		return SpringContextUtil.getAllBean();
	}


	/**
	 * 辅助函数，遍历指定目录下的所有jar文件，获取所有jar资源的文件全路径
	 *
	 * @param storeDir jar存放目录
	 * @param pathList jar文件全路径列表
	 */
	private void recursionJarPaths(String storeDir, List<String> pathList) {
		File file = new File(storeDir);
		File[] jarsArray = file.listFiles();
		if (jarsArray != null && jarsArray.length > 0) {
			for (File jarFile : jarsArray) {
				if (jarFile.isDirectory()) {
					recursionJarPaths(jarFile.getAbsolutePath(), pathList);
				} else {
					if (jarFile.getPath().endsWith(".jar")) {
						pathList.add(jarFile.getAbsolutePath());
					}
				}
			}
		}
	}


	/**
	 * 辅助函数，遍历指定目录下的所有jar文件对应的自定义moduleName，获取所有jar资源对应的moduleName
	 *
	 * @param storeDir jar存放目录
	 * @param moduleNameList moduleName列表
	 */
	private void recursionJarModuleNamePaths(String storeDir, List<String> moduleNameList) {
		File file = new File(storeDir);
		File[] jarsArray = file.listFiles();
		if (jarsArray != null && jarsArray.length > 0) {
			for (File jarFile : jarsArray) {
				if (jarFile.isDirectory()) {
					recursionJarModuleNamePaths(jarFile.getAbsolutePath(), moduleNameList);
				} else {
					if (jarFile.getPath().endsWith(".jar")) {
						/*String absPath = jarFile.getAbsolutePath();
						int flag = absPath.lastIndexOf(".jar");*/
						String jfName = jarFile.getName();
						int flag = jfName.lastIndexOf(".jar");
						String moduleName = jarFile.getName().substring(0, flag);
						moduleNameList.add(moduleName);
					}
				}
			}
		}
	}



	/* 以下为适配用户使用流程的服务接口 **/

	/**
	 * 上传指定jar file
	 *
	 * @param jarFile jar文件
	 * @return 返回上传结果
	 */
	public String uploadJarFile(MultipartFile jarFile) {
		if (jarFile.isEmpty()) {
			return MessageUtils.buildInfo(RespondMessage.STATUS_FAILED,
				"jar file can not be null.");
		}

		String destPath = "";
		try {
			destPath = UploadFileUtils.uploadFile(jarFile, thirdPartyUploadPath);

			_logger.info("upload success!dest path is [{}].", destPath);
			return MessageUtils.buildInfo(RespondMessage.STATUS_SUCCESS,
				"upload success!dest path is [" + destPath + "].");
		} catch (IOException e) {
			_logger.error("upload failed! {}", e.getMessage());
			return  MessageUtils.buildInfo(RespondMessage.STATUS_FAILED,
				"upload failed!dest path is [" + destPath + "]. " + e.getMessage());
		}
	}


	/**
	 * 获取指定目录下的所有jar的文件名（包含后缀）
	 *
	 * @return 返回所有jar的文件名
	 */
	public List<String> getAllJars() {
		List<String> jarPathList = new ArrayList<>();
		jarPaths(thirdPartyUploadPath, jarPathList);
		return jarPathList;
	}


	/**
	 * 辅助函数，遍历指定目录下的所有jar文件对应的文件名（包含后缀）
	 *
	 * @param storeDir jar存放目录
	 * @param pathList jar文件名集合
	 */
	private void jarPaths(String storeDir, List<String> pathList) {
		File file = new File(storeDir);
		File[] jarsArray = file.listFiles();
		if (jarsArray != null && jarsArray.length > 0) {
			for (File jarFile : jarsArray) {
				if (jarFile.isDirectory()) {
					jarPaths(jarFile.getAbsolutePath(), pathList);
				} else {
					if (jarFile.getPath().endsWith(".jar")) {
						pathList.add(jarFile.getName());
						//pathList.add(jarFile.getAbsolutePath());
						/*String a = jarFile.getPath();*/
					}
				}
			}
		}
	}


	/**
	 * 装载某个指定的jar包
	 * 	其加载路径通过配置配置
	 * 	使用方法：
	 * 			需要先上传jar包-->装载jar包
	 *
	 * @param jarName jar包文件名，如'xxxx.jar'
	 * @return  返回Spring应用上下文中所有Bean，不含非Spring应用的类
	 */
	public List<?> loadJarByName(String jarName) {
		String jarPath = thirdPartyUploadPath + File.separator + jarName;
		_logger.info("current jar name=[{}], jar path=[{}]",
			jarName, jarPath);
		return loadJar(jarPath);
	}


	/**
	 * 装载配置文件配置路径下的所有jar包，包含子文件夹和子jar
	 *
	 * @return 返回Spring应用上下文中所有Bean，不含非Spring应用的类
	 */
	public List<?> loadAllJar() {
		return loadJarInDir(thirdPartyUploadPath);
	}

	/**
	 * 卸载指定jar包
	 *
	 * @param jarName jar name，应不含'.jar'后缀
	 * @return 返回删除后，所有已加载的beans
	 */
	public List<Map<String, Object>> unloadJarByName(String jarName) {
		if (jarName.endsWith(".jar")) {
			int flag = jarName.indexOf(".jar");
			jarName = jarName.substring(0, flag);
		}
		return unloadModule(jarName);
	}

	/**
	 * 卸载配置文件配置路径下的所有jar包，包含子文件夹和子jar
	 *
	 * @return 返回删除后，所有已加载的beans
	 */
	public List<Map<String, Object>> unloadAllJar() {
		return unloadModuleInDir(thirdPartyUploadPath);
	}

	/**
	 * 根据文件名删除文件
	 *
	 * @param jarName jar文件名
	 * @return 返回是否删除成功
	 */
	public boolean delJar(String jarName) {
		if (!jarName.endsWith(".jar")) {
			jarName = jarName + ".jar";
		}
		String curPath = thirdPartyUploadPath + File.separator + jarName;

		File file = new File(curPath);
		File absoluteFile = file.getAbsoluteFile();
		if (!absoluteFile.exists()) {
			_logger.error("jarName=[{}],jarPath=[{}],not exist,delete failed.", jarName, curPath);
			return false;
		}

		if (file.isDirectory()) {
			return DeleteFileUtils.deleteDir(file.getAbsolutePath());
		} else if (file.isFile()) {
			return DeleteFileUtils.deleteFile(file.getAbsolutePath());
		} else {
			return false;
		}
	}


	/**
	 * 根据文件名列表批量删除文件
	 *
	 * @param jarNameList jar文件名列表
	 * @return 返回是否删除成功
	 */
	public boolean delJarList(List<String> jarNameList) {
		if (jarNameList.isEmpty()) {
			_logger.error("[{}] is null, delete failed.", jarNameList);
			return false;
		}

		boolean result = false;
		for (String jarName : jarNameList) {
			result = delJar(jarName);
		}
		return result;
	}

	/**
	 * 删除指定目录下的所有文件，但不删除该目录
	 *
	 * @return 返回是否删除成功
	 */
	public boolean deleteFileinDir() {
		String dir = thirdPartyUploadPath;
		return DeleteFileUtils.deleteFileIndir(dir);
	}

}
