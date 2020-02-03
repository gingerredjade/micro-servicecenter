package com.micro.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 文件删除工具类
 *
 * @since 1.0.0 2019年11月19日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
public class DeleteFileUtils {

	private static Logger _logger = LoggerFactory.getLogger(DeleteFileUtils.class);

	/**
	 * 自动判断是文件夹或者文件，并进行删除操作
	 *
	 * @param path 文件夹或文件全路径
	 * @return true：删除成功；false：删除失败
	 */
	public static boolean deletePower(String path) {
		File file = new File(path);
		if (!file.exists()) {
			_logger.error("[{path}] not exist, delete failed.");
			return false;
		} else {
			if (file.isFile()) {
				return deleteFile(path);
			}
			if (file.isDirectory()) {
				return deleteDir(path);
			}
			return false;
		}
	}


	/**
	 * 根据文件全路径删除指定的文件
	 *
	 * @param filePath 文件全路径
	 * @return true：删除成功；false：删除失败
	 */
	public static boolean deleteFile(String filePath) {

		try {
			File file = new File(filePath);
			if (file.exists() && file.isFile()) {
				boolean delFlag = file.delete();
				if (delFlag) {
					_logger.info("file [{}] delete success.", filePath);
					return true;
				} else {
					_logger.error("file [{}] delete failed.", filePath);
					return false;
				}
			} else {
				_logger.error("file [{}] not exist, delete failed.", filePath);
				return false;
			}
		} catch(Exception e) {
			e.getMessage();
		}
		return false;
	}


	/**
	 * 删除指定的目录以及目录下的所有子文件
	 *
	 * @param dir 待删除目录路径
	 * @return true：删除成功；false：删除失败
	 */
	public static boolean deleteDir(String dir) {

		// 目录不以分隔符结尾则自动添加分隔符
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}

		// 根据指定路径创建对应File对象
		File file = new File(dir);

		if (!file.exists() || !file.isDirectory()) {
			_logger.error("delete failed, dir=[{}]", dir);
			return false;
		}

		File[] fileArray = file.listFiles();
		if (fileArray != null && fileArray.length > 0) {
			for (File curFile : fileArray) {
				deletePower(curFile.getAbsolutePath());
			}
		}

		if (file.delete()) {
			_logger.info("dir=[{}] delete success.", dir);
			return true;
		}
		return false;
	}


	/**
	 * 删除指定目录下的所有子文件
	 *
	 * @param dir 指定目录路径
	 * @return true：删除成功；false：删除失败
	 */
	public static boolean deleteFileIndir(String dir) {
		// 目录不以分隔符结尾则自动添加分隔符
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}

		// 根据指定路径创建对应File对象
		File file = new File(dir);

		if (!file.exists() || !file.isDirectory()) {
			_logger.error("delete failed, dir=[{}]", dir);
			return false;
		}

		File[] fileArray = file.listFiles();
		if (fileArray != null && fileArray.length > 0) {
			for (File curFile : fileArray) {
				deletePower(curFile.getAbsolutePath());
			}
			return true;
		} else {
			_logger.error("delete failed, dir={}, is null.", dir);
			return false;
		}
	}


}
