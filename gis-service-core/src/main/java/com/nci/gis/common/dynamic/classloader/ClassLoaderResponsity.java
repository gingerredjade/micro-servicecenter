package com.nci.gis.common.dynamic.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义类加载器操作管理类
 *
 * @since 1.0.0 2019年11月12日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
public class ClassLoaderResponsity {

	private static Logger _logger = LoggerFactory.getLogger(ClassLoaderResponsity.class);

	private ClassLoaderResponsity() {}

	private Map<String, ModuleClassLoader> responsityMap = new ConcurrentHashMap<>();

	public void addClassLoader(String moduleName, ModuleClassLoader moduleClassLoader) {
		responsityMap.put(moduleName, moduleClassLoader);
	}

	public boolean containsClassLoader(String key) {
		return responsityMap.containsKey(key);
	}

	public ModuleClassLoader getClassLoader(String key) {
		return responsityMap.get(key);
	}

	public void removeClassLoader(String moduleName) {
		ModuleClassLoader moduleClassLoader = responsityMap.get(moduleName);

		try {
			List<String> registeredBean = moduleClassLoader.getRegisteredBean();
			for (String beanName : registeredBean) {
				_logger.info("delete Bean：{}", beanName);
				SpringContextUtil.getBeanFactory().removeBeanDefinition(beanName);
			}

			moduleClassLoader.close();
			responsityMap.remove(moduleName);
		} catch (IOException e) {
			_logger.error("delete ：[{}] module error, error info is {}", moduleName, e.getMessage());
		}
	}

	private static class ClassloaderResponsityHolder {
		private static ClassLoaderResponsity instance = new ClassLoaderResponsity();
	}

	public static ClassLoaderResponsity getInstance() {
		return ClassloaderResponsityHolder.instance;
	}



}
