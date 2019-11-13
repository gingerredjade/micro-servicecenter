//package com.nci.gis.apigateway.config;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.Arrays;
//
///**
// * 跨域配置 全局设置（与具体接口跨域只能二选一）
// * Created by JHy
// * 2019-08-27 23:04
// * C - Cross  O - Origin  R - Resource  S - Sharing
// */
//@Configuration
//public class CorsConfig {
//
//	@Bean
//	public CorsFilter corsFilter() {
//		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		final CorsConfiguration config = new CorsConfiguration();
//
//		config.setAllowCredentials(true); 				// 设置是否支持Cookie跨域
//		config.setAllowedOrigins(Arrays.asList("*")); 	// 原始域，如http:www.a.com
//		config.setAllowedHeaders(Arrays.asList("*"));	// 设置允许的头
//		config.setAllowedMethods(Arrays.asList("*"));	// 设置允许的方法
//		config.setMaxAge(300l);							// 设置缓存时间，在这时间段里对于相同的跨域请求就不再进行检查了
//
//		source.registerCorsConfiguration("/**", config);
//		return new CorsFilter(source);
//	}
//}
