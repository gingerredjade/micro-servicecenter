package com.nci.gis.eureka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 注册中心Eureka服务端启动主类
 *
 * Created by JHy on 2019/8/12.
 */
@SpringBootApplication
@EnableEurekaServer
public class GisRegisterCenterApplication {
	private static Logger logger = LoggerFactory.getLogger(GisRegisterCenterApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GisRegisterCenterApplication.class, args);
		logger.info("[GisRegisterCenterApplication微服务] 已启动");

		/*// Eureka是个war包，保证由Spring Boot启动的应用包含内嵌Web容器
		new SpringApplicationBuilder(EurekaServerApplication.class).web(WebApplicationType.SERVLET).run(args);*/
	}

}
