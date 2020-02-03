package com.micro.common.dynamic.classloader;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring Context工具类SpringContextUtil
 *		注：在Spring框架中，使用Java的反射去实例化一个服务类的时候获取不到该类的对象
 *				使用Spring提供的ReflectionUtils来反射服务类，执行服务类中的方法
 *
 *
 * @since 1.0.0 2019年11月12日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

	/**
	 * 获取Bean工厂，用来实现动态注入Bean
	 * 不能使用其他类加载器加载Bean，否则会出现异常：类未找到，类未定义
	 *
	 * @return 返回值
	 */
	public static DefaultListableBeanFactory getBeanFactory() {
		return (DefaultListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
	}

	/**
	 * 获取Spring应用上下文中所有Bean
	 * 		注意。不包含非Spring应用的类
	 *
	 * @return 返回的所有Bean列表
	 */
	public static List<Map<String, Object>> getAllBean() {
		List<Map<String, Object>> list = new ArrayList<>();
		String[] beans = getApplicationContext().getBeanDefinitionNames();
		for (String beanName : beans) {
			Class<?> beanType = getApplicationContext().getType(beanName);
			Map<String, Object> map = new HashMap<>();

			map.put("BeanName", beanName);
			map.put("beanType", beanType);
			map.put("package", beanType.getPackage());
			list.add(map);
		}
		return list;
	}

	// Spring应用上下文环境(对象实例)
	private static ApplicationContext applicationContext;

	/**
	 * 实现ApplicationContextAware接口的回调方法，设置上下文环境
	 *
	 * @param applicationContext 应用上下文
	 * @throws BeansException BeansException异常
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 获取ApplicationContext
	 *
	 * @return 返回上下文对象
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 通过Bean Name获取Bean对象
	 *
	 * @param name BeanName
	 * @return 返回值
	 */
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}

	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> clazz) {
		return getApplicationContext().getBean(name, clazz);
	}

	/**
	 * 判断Spring应用上下文环境是否含有指定Bean
	 *
	 * @param name BeanName
	 * @return 返回值true/false
	 */
	public static boolean hasBean(String name) {
		return applicationContext.containsBean(name);
	}
}
