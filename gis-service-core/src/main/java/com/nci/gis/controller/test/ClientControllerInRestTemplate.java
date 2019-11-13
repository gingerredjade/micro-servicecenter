package com.nci.gis.controller.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 测试用-客户端测试Controller
 *
 * @since 1.0.0 2019年08月22日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
//@RestController
public class ClientControllerInRestTemplate {

	private static final Logger _logger = LoggerFactory.getLogger(ClientControllerInRestTemplate.class);

	// Spring Cloud提供了LoadBalancerClient对象
	@Autowired
	private LoadBalancerClient loadBalancerClient;

	// 因为采用第三种方式使用RestTemplate，RestTemplate作为Bean了，可以直接注入进来。
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/getServerMsg/**")
	public String getServerMsg() {
		/**
		 * 1.第一种方式
		 * 		直接使用RestTemplate，url写死
		 * 		IP端口都写死了,线上肯定不行
		 * 		对方可能启动了多个实例,存在多个地址,不适用
		 */
		/*RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject("http://localhost:8999/msg", String.class);
		_logger.info("response={}",response);
		return response;*/

		/**
		 * 2.第二种方式
		 *		利用LoadBalancerClient对象，首先通过应用名字获取url
		 *		拿到地址之后通过RestTemplate对象发送请求
		 */
		/*ServiceInstance serviceInstance = loadBalancerClient.choose("GIS-SERVICE-CORE");
		String url = String.format("http://%s:%s", serviceInstance.getHost(), serviceInstance.getPort() + "/msg");
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(url, String.class);

		_logger.info("response={}",response);
		return response;*/

		/**
		 * 3.第三种方式（推荐）
		 * 		利用@LoadBalanced，可在RestTemplate理论使用应用名字
		 * 		写一个RestTemplateConfig，把RestTemplate作为一个Bean给配置上去
		 */
		// 注意第一种方式中使用的是服务的IP、PORT，这里使用应用的名称即可！
		String response = restTemplate.getForObject("http://GIS-SERVICE-CORE/msg", String.class);
		_logger.info("response={}",response);
		return response;
	}

}
