package com.micro.common.dynamic.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 在系统启动时自动加载指定目录中的所有jar
 *
 * @since 1.0.0 2019年11月19日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
@Component
@Order
public class AutomaticLoadJar implements CommandLineRunner {

	private static Logger _logger = LoggerFactory.getLogger(AutomaticLoadJar.class);

	private final ClassLoaderService classLoaderService;
	@Autowired
	public AutomaticLoadJar(ClassLoaderService classLoaderService) {
		this.classLoaderService = classLoaderService;
	}

	@Override
	public void run(String... args) throws Exception {
		// 加载所有jar
		_logger.info("start load 3rd jars...");
		classLoaderService.loadAllJar();
		_logger.info("finish load 3rd jars...");
	}
}
