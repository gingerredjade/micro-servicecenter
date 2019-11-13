//package com.nci.gis.apigateway.filter;
//
//import com.google.common.util.concurrent.RateLimiter;
//import com.nci.gis.apigateway.exception.RateLimitException;
//import com.netflix.zuul.ZuulFilter;
//import org.springframework.stereotype.Component;
//
//import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
//import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVLET_DETECTION_FILTER_ORDER;
//
///**
// * 限流拦截器（令牌桶限流）
// * 		API访问保护，防止恶意攻击
// *
// * Created by JHy
// * 2019-08-28 23:44
// */
//@Component
//public class RateLimitFilter extends ZuulFilter{
//
//	// Google提供的令牌桶限流算法（with guava）。100表示每秒钟往里面放100个令牌
//	private static final RateLimiter RATE_LIMITER = RateLimiter.create(100);
//
//	/**
//	 * to classify a filter by type. Standard types in Zuul are "pre" for pre-routing filtering,
//	 * "route" for routing to an origin, "post" for post-routing filters, "error" for error handling.
//	 * We also support a "static" type for static responses see  StaticResponseFilter.
//	 * Any filterType made be created or added and run by calling FilterProcessor.runFilters(type)
//	 *
//	 * @return A String representing that type
//	 */
//	@Override
//	public String filterType() {
//		// 在前置过滤器里操作
//		return PRE_TYPE;
//	}
//
//	/**
//	 * filterOrder() must also be defined for a filter. Filters may have the same  filterOrder if precedence is not
//	 * important for a filter. filterOrders do not need to be sequential.
//	 *
//	 * @return the int order of a filter
//	 */
//	@Override
//	public int filterOrder() {
//		// 限流应放在最靠前的位置
//		return SERVLET_DETECTION_FILTER_ORDER - 1;
//	}
//
//	/**
//	 * a "true" return from this method means that the run() method should be invoked
//	 *
//	 * @return true if the run() method should be invoked. false will not invoke the run() method
//	 */
//	@Override
//	public boolean shouldFilter() {
//		return true;
//	}
//
//	/**
//	 * if shouldFilter() is true, this method will be invoked. this method is the core method of a ZuulFilter
//	 *
//	 * @return Some arbitrary artifact may be returned. Current implementation ignores it.
//	 */
//	@Override
//	public Object run() {
//		// 没有拿到令牌时的处理
//		if (!RATE_LIMITER.tryAcquire()) {
//			throw new RateLimitException();
//		}
//
//		return null;
//	}
//}
