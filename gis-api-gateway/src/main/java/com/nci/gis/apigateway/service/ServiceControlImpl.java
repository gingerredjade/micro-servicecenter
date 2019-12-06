package com.nci.gis.apigateway.service;

import com.alibaba.fastjson.JSONObject;
import com.nci.gis.apigateway.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 服务访问控制检测类
 *
 * Created by JHy on 2019/10/10.
 */
@Component
public class ServiceControlImpl {

	private static final Logger _logger = LoggerFactory.getLogger(ServiceControlImpl.class);

	// Spring Cloud提供了LoadBalancerClient对象
	private final LoadBalancerClient loadBalancerClient;

	// 因RestTemplate作为Bean了，可直接注入进来
	private final RestTemplate restTemplate;

	// 通过constructor方式注入LoadBalancerClient、RestTemplate
	public ServiceControlImpl(LoadBalancerClient loadBalancerClient, RestTemplate restTemplate) {
		this.loadBalancerClient = loadBalancerClient;
		this.restTemplate = restTemplate;
	}


	/**
	 * 服务状态检测
	 *
	 * @param serviceType 服务类型
	 * @param organizationidentity 服务提供机构标识
	 * @param serviceName 服务名称
	 * @param svctypenode 服务子类型
	 * @return	返回值
	 */
	public Boolean getServiceStatus(String serviceType, String organizationidentity, String serviceName, String svctypenode) {
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		sb.append("svctype=");
		sb.append(serviceType);
		sb.append("&organizationidentity=");
		sb.append(organizationidentity);

		if (serviceName != null && serviceName.length() != 0) {
			sb.append("&svcname=");
			sb.append(serviceName);
		} else {
			//sb.append("&svcname=");
		}

		if (svctypenode != null && svctypenode.length() != 0) {
			sb.append("&svctypenode=");
			sb.append(svctypenode);
		} else {
			//sb.append("&svctypenode=");
		}

		boolean response = false;
		try{
			String str = restTemplate.getForObject(
				"http://GIS-SERVICE-SHOW/svccatalog/checksvc"+sb.toString(),
				String.class);

			if (str != null && str.length() != 0) {
				JSONObject jsonObject = JSONObject.parseObject(str);
				if (!jsonObject.isEmpty()) {
					int releaseState = jsonObject.getInteger("releaseState");
					if (releaseState == Constants.RELEASE_DONE) {
						response = true;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		_logger.info("service status response={}",response);
		return response;
	}


	/**
	 * 记录服务访问量
	 *
	 * @param serviceType 服务类型
	 * @param organizationidentity 服务提供机构标识
	 * @param serviceName 服务名称
	 * @return 返回值
	 */
	public Boolean serviceAccessIncrease(String serviceType, String organizationidentity, String serviceName) {
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		sb.append("svctype=");
		sb.append(serviceType);
		sb.append("&organizationIdentity=");
		sb.append(organizationidentity);

		if (serviceName != null && serviceName.length() != 0) {
			sb.append("&svcname=");
			sb.append(serviceName);
		}

		Boolean response = restTemplate.getForObject(
			"http://GIS-SERVICE-SHOW/statistical/save"+sb.toString(),
			Boolean.class);

		_logger.info("response={}",response);
		return response;
	}


}
