package com.gis.serviceshow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 应用启动入口类
 *
 * @since 1.0.0 2019年09月08日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GisServiceShowApplication {
	private  static Logger logger = LoggerFactory.getLogger(GisServiceShowApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(GisServiceShowApplication.class, args);
		logger.info("[GisServiceShow微服务] 已启动");
	}

}
