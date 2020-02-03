package com.micro.apigateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置.
 * RestTemplate对象已经配置成Bean了，使用时可直接通过注解注入RestTemplate。
 *
 * Created by JHy on 2019/8/22.
 */
@Component
public class RestTemplateConfig {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
