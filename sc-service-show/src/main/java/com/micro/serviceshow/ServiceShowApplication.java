package com.micro.serviceshow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 应用启动入口类
 *
 * @since 1.0.0 2019年09月08日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceShowApplication {
	private  static Logger logger = LoggerFactory.getLogger(ServiceShowApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(ServiceShowApplication.class, args);
		logger.info("[ServiceShow微服务] 已启动");
	}

}
