package com.nci.gis.common;

import java.lang.reflect.Method;

public class ClassUtils {

	/**
	 * 通过类、类方法名，获取方法对应的参数信息
	 *
	 * @param obj 类实例
	 * @param methodName 方法名
	 * @return 返回值
	 */
	public static Class<?>[] getParameterClass(Object obj, String methodName) {
		Class<?> clazz = obj.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method m : methods) {

			/*
			 * 跳过非指定的方法
			 */
			if (!m.getName().equals(methodName)) {
				continue;
			}

			/*
			 * 获取参数类型的数组，里面有参数的个数和参数的类型
			 */
			return m.getParameterTypes();
		}
		return null;
	}
}
