package com.nci.gis.common.dynamic.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * 动态JAR加载器
 *
 * @since 1.0.0 2019年11月12日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
public class DynamicJarClassLoader extends URLClassLoader {

	public DynamicJarClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public DynamicJarClassLoader(URL[] urls) {
		super(urls);
	}

	public DynamicJarClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}
}
