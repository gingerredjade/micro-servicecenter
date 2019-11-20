package com.nci.gis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;


/**
 * 启动主类
 *
 * Created by JHy on 2019/8/12.
 */
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@Configuration
public class GisServiceCoreApplication {
	private  static Logger logger = LoggerFactory.getLogger(GisServiceCoreApplication.class);

    public static void main(String[] args) {
        SpringApplication.run( GisServiceCoreApplication.class, args );
		logger.info("[GisServiceCoreApplication微服务] 已启动");
    }


}
