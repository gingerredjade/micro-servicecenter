package com.micro.common.dynamic.classloader;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 自定义类装载器，动态JAR加载器（加载外部jar包）
 *
 * @since 1.0.0 2019年11月12日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
public class ModuleClassLoader extends URLClassLoader {

	private static Logger _logger = LoggerFactory.getLogger(ModuleClassLoader.class);

	// 属于本类加载器加载的jar包
	private JarFile jarFile;

	// 保存已经加载过的Class对象
	private Map<String, Class> cacheClassMap = new HashMap<>();

	// 保存本类加载器加载的class字节码
	private Map<String, byte[]> classBytesMap = new HashMap<>();

	// 需要注册的Spring Bean的name集合
	private List<String> registeredBean = new ArrayList<>();


	/**
	 * 构造函数
	 *
	 * @param urls      URL数组
	 * @param parent	类加载器
	 */
	public ModuleClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);

		URL url = urls[0];
		String path = url.getPath();
		try {
			jarFile = new JarFile(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 初始化类加载器
		init();
	}


	/**
	 * 重写loadClass方法，改写loadClass方式
	 *
	 * @param name	类名
	 * @return      返回值
	 * @throws ClassNotFoundException	异常信息
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (findLoadedClass(name) == null ) {
			return super.loadClass(name);
		} else {
			return cacheClassMap.get(name);
		}
	}


	/**
	 * 初始化类加载器，保存字节码
	 * 		不支持加载jar中jar
	 */
	private void init() {

		/*
		 * 1-- 解析jar包每一项
		 */
		Enumeration<JarEntry> entryEnumeration = jarFile.entries();
		InputStream inputStream = null;

		try {
			while (entryEnumeration.hasMoreElements()) {
				JarEntry jarEntry = entryEnumeration.nextElement();
				String name = jarEntry.getName();

				// 此处添加了路径扫描限制
				if (name.endsWith(".class")) {
					String className = name.replace(".class", "").replaceAll("/", ".");
					inputStream = jarFile.getInputStream(jarEntry);

					int bufferSize = 4096;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[bufferSize];
					int bytesNumRead;
					while ( (bytesNumRead = inputStream.read(buffer)) != -1 ) {
						baos.write(buffer, 0 , bytesNumRead);
					}
					byte[] classBytes = baos.toByteArray();
					classBytesMap.put(className, classBytes);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			 if (inputStream != null) {
				 try {
					 inputStream.close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		}

		/*
		 * 2-- 将jar中的每一个class字节码进行Class载入
		 */
		for (Map.Entry<String, byte[]> entry : classBytesMap.entrySet()) {
			String key = entry.getKey();
			Class<?> aClass = null;
			try {
				aClass = loadClass(key);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			cacheClassMap.put(key, aClass);
		}
	}


	/**
	 * 初始化Bean
	 */
	public void initBean() {
		for (Map.Entry<String, Class> entry : cacheClassMap.entrySet()) {
			String className = entry.getKey();
			Class<?> cla = entry.getValue();

			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(cla);
			BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

			// 设置当前Bean定义对象是单例的
			beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

			// 将变量首字母置小写(取消此设置)
			String beanName = StringUtils.uncapitalize(className);
			/*beanName = beanName.substring(beanName.lastIndexOf(".") + 1);
			beanName = StringUtils.uncapitalize(beanName);*/

			// 注册Bean
			SpringContextUtil.getBeanFactory().registerBeanDefinition(beanName, beanDefinition);
			registeredBean.add(beanName);
			_logger.info("registered bean: [{}]", beanName);
		}
	}


	/**
	 * 获取当前类加载器注册的Bean
	 * 在移除当前类加载器的时候需要手动删除这些注册的Bean
	 *
	 * @return 返回值
	 */
	public List<String> getRegisteredBean() {
		return registeredBean;
	}


	/**
	 * 判断class对象是否带有Spring的注解
	 *
	 * @param cla jar中的每一个class类
	 * @return 返回值true是Spring Bean，false不是Spring Bean
	 */
	public boolean isSpringBeanClass(Class<?> cla) {
		if (cla == null) {
			return false;
		}

		// 是否是接口
		if (cla.isInterface()) {
			return false;
		}

		// 是否是抽象类
		if (Modifier.isAbstract(cla.getModifiers())) {
			return false;
		}

		if (cla.getAnnotation(Component.class) != null ||
			cla.getAnnotation(Repository.class) != null ||
			cla.getAnnotation(Service.class) != null) {
			return true;
		}
		return false;
	}

}
