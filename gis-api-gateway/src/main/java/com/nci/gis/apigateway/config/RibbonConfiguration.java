package com.nci.gis.apigateway.config;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import org.springframework.context.annotation.Bean;

/**
 * 配置Spring Bean，检测实例是否已经关闭
 * 		IPing：覆盖默认检查状态机制
 * 		IRule：更改默认负载均衡策略
 *
 * Ribbon功能会ping服务并根据结果应用负载平衡
 *
 *
 * Created by JHy
 * 2019-10-29 14:34
 */
public class RibbonConfiguration {

	@Bean
	public IPing ribbonPing(final IClientConfig config) {
		// PingUrl检查服务是否存活，更改默认URL指向/actuator/health，false标志只是表示端点不是安全的
		return new PingUrl(false, "/actuator/health");
	}

	@Bean
	public IRule ribbonRule(final IClientConfig config) {
		// AvailabilityFilteringRule是默认的RoundRobbinRule的替代规则
		// AvailabilityFilteringRule会循环遍历实例，使用ping检查目标的可用性，以便在目标没有响应时跳过某些实例
		return new AvailabilityFilteringRule();
	}


}
