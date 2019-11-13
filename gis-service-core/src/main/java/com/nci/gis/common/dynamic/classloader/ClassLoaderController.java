package com.nci.gis.common.dynamic.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 自定义类加载器控制器
 *
 * @since 1.0.0 2019年11月12日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@RestController
@RequestMapping(value = "/cls/loader")
public class ClassLoaderController {

	private static Logger _logger = LoggerFactory.getLogger(ClassLoaderController.class);

	@GetMapping(value = "/getAllbeans")
	public List<Map<String, Object>> beans() {
		return SpringContextUtil.getAllBean();
	}

	@GetMapping(value = "/delModule")
	public List<Map<String, Object>> deleteModule(String moduleName) {
		if (ClassLoaderResponsity.getInstance().containsClassLoader(moduleName)) {
			ClassLoaderResponsity.getInstance().removeClassLoader(moduleName);
		}
		return beans();
	}

	@GetMapping(value = "/loadJar")
	public List<?> loadJar(String jarPath) {
		File jar = new File(jarPath);
		URI uri = jar.toURI();

		String moduleName = jarPath.substring(jarPath.lastIndexOf("/")+1, jarPath.lastIndexOf("."));
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
		_logger.info("load [{}] success", jarPath);
		return SpringContextUtil.getAllBean();
	}

	@GetMapping(value = "/invoke")
	public Object invokeBean(String beanName) {
		Object bean = SpringContextUtil.getBean(beanName);
		Class<?> beanCls = bean.getClass();

		// 获得类的所有方法
		Method[] methods = beanCls.getMethods();

		Class<?>[] parameters = new Class<?>[1];
		Class<?> cls = "name参数".getClass();
		parameters[0] = cls;

		// 调用不含参方法
		Method method1 = ReflectionUtils.findMethod(SpringContextUtil.getBean(beanName).getClass(), "testnoparam");

		// 调用含参方法
		Method method = ReflectionUtils.findMethod(SpringContextUtil.getBean(beanName).getClass(), "testtest", String.class);
		Object result = ReflectionUtils.invokeMethod(method, SpringContextUtil.getBean(beanName), "哈哈哈哈哈哈");
		return result;
	}

}
