package com.micro.apigateway;

import com.micro.apigateway.config.RibbonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 服务网关启动主类
 * 		RibbonClients指向Ribbon功能的配置
 *
 * Created by JHy on 2019/8/26.
 */
@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@EnableEurekaClient
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class ApiGatewayApplication {
	private static Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
		logger.info("[ApiGatewayApplication微服务] 已启动");
	}

//    @ConfigurationProperties("zuul")
//    @RefreshScope
//    public ZuulProperties zuulProperties() {
//        return new ZuulProperties();
//    }


}
