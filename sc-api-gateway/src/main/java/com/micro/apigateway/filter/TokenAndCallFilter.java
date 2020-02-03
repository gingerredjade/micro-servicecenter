package com.micro.apigateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.micro.apigateway.service.ServiceControlImpl;
import com.micro.constants.ServiceTypes.*;
import com.micro.entity.RespondMessage;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 服务请求拦截器
 * 		（通过登录token、服务状态进行判断）
 *
 * 	继承ZuulFilter，并实现4个接口，用来进行请求过滤
 *
 *
 * Created by JHy
 * 2019-10-11 15:34
 */
@Component
public class TokenAndCallFilter extends ZuulFilter {

	private static final Logger _logger = LoggerFactory.getLogger(TokenAndCallFilter.class);

	// 因将RestTemplate作为Bean了，可直接注入进来
	private final RestTemplate restTemplate;

	// 注入服务访问控制类
	private final ServiceControlImpl serviceControl;

	public TokenAndCallFilter(RestTemplate restTemplate, ServiceControlImpl serviceControl) {
		this.restTemplate = restTemplate;
		this.serviceControl = serviceControl;
	}

	// 是否开启网关校验功能
	@Value("${authority.enabled}")
	private Boolean AUTHORITY_ENABLED;

	// 是否启用访问权限校验
	@Value("${authority.access.enabled}")
	private Boolean ACCESS_ENABLED;


	/**
	 * 返回过滤器类型，决定了过滤器在请求的哪个生命周期中执行
	 * 	pre请求之前的filter
	 * 	route处理请求，进行路由
	 * 	post请求处理完成后执行的filter
	 * 	error出现错误时执行的filter
	 *
	 * @return 返回值
	 */
    @Override
    public String filterType() {
    	// 这里做参数校验，使用PRE_TYPE
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
    	// 数值越小的优先级越高,官方推荐写法
        return PRE_DECORATION_FILTER_ORDER - 1;
    }

	/**
	 * shouldFilter判断该过滤器是够需要被执行
	 * 	依配置：
	 * 		true表示该过滤器对所有请求都会生效
	 * 		false表示该过滤器不启用
	 *
	 * @return 返回值
	 */
	@Override
    public boolean shouldFilter() {
		return AUTHORITY_ENABLED;
	}

	/**
	 * 过滤器的具体逻辑
	 *
	 * @return 返回值
	 */
	@Override
    public Object run() {

    	// 从RequestContext对象获取上下文、请求
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        try {

        	/*
        	 * 0-- 获取请求真实IP
        	 */
        	String remoteAddr = request.getRemoteAddr();


			/*
			 * 1-- 从请求中获取用于服务状态监测的参数
			 * 		发布状态的服务均进行后续访问处理
			 * 		非发布状态的服务，若用户传递了token进行后续访问处理
			 */
			String reqUri = request.getRequestURI();
			String[] reqUriArr = reqUri.split("/");
			List<String> uriArr = Arrays.asList(reqUriArr);

			// 检测服务发布状态(同时存储访问记录)
			boolean status;
			if (uriArr.contains(MAP_SERVICE.value)) {
				status = getMapSvcStatus(request, null);
			} else if (uriArr.contains(GEOMETRY_SERVICE.value)) {
				status = getGeometrySvcStatus(request, null);
			} else {
				status = true;
				/*RespondMessage rm = new RespondMessage();
				rm.setStatus("[" + HttpStatus.BAD_REQUEST.value() + "]  " + HttpStatus.BAD_REQUEST.getReasonPhrase());
				rm.setMessage("服务请求错误，请检查请求正确性。");

				requestContext.setSendZuulResponse(false);
				requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
				requestContext.setResponseBody(JSONObject.toJSONString(rm));
				requestContext.getResponse().setContentType("application/json;charset=UTF-8");*/
			}


			// 开启访问权限校验情况下,验证服务状态
			if (ACCESS_ENABLED) {
				if (!status) {
					//这里从url参数里获取, 也可以从cookie, header里获取
					String token = request.getParameter("token");
					if (StringUtils.isEmpty(token)) {
						RespondMessage rm = new RespondMessage();
						rm.setStatus("[" + HttpStatus.UNAUTHORIZED.value() + "]  " + HttpStatus.UNAUTHORIZED.getReasonPhrase());
						rm.setMessage("无权访问该服务。【访问权限说明】1：登录用户；2：该服务已成功发布。");

						requestContext.setSendZuulResponse(false);
						requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
						requestContext.setResponseBody(JSONObject.toJSONString(rm));
						requestContext.getResponse().setContentType("application/json;charset=UTF-8");
					}
				}
			}
		} catch (Exception e) {
			requestContext.set("error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			requestContext.set("error.exception", e);
		}

        return null;
    }


	/**
	 * 获取地图服务是否处于某状态
	 * 写入访问记录
	 *
	 * @param request		请求
	 * @param statusFlag	状态标识，注册/审核/发布
	 * @return	返回值true/false
	 */
    private Boolean getMapSvcStatus(HttpServletRequest request, String statusFlag) {
    	String reqURI = request.getRequestURI();

		String svctype = MAP_SERVICE.value;
		String svcname = request.getParameter("alias");

		String organizationidentity = getOrgFromURI(svctype, reqURI);

		// 1-- 接收到访问信号先记录统计数据
		boolean flag = serviceControl.serviceAccessIncrease(svctype, organizationidentity, svcname);
		if (flag) {
			_logger.info("Access Increase, svctype=[{}], organizationidentity=[{}], svcname=[{}]",
				svctype, organizationidentity, svcname);
		}

		// 2-- 判断服务的状态
		return serviceControl.getServiceStatus(svctype, organizationidentity, svcname, null);
	}


	/**
	 * 获取几何计算服务是否处于某状态
	 * 写入访问记录
	 *
	 * @param request		请求
	 * @param statusFlag	状态标识，注册/审核/发布
	 * @return	返回值true/false
	 */
	private Boolean getGeometrySvcStatus(HttpServletRequest request, String statusFlag) {
		String svctype = GEOMETRY_SERVICE.value;

		String reqURI = request.getRequestURI();
		String organizationidentity = getOrgFromURI(svctype, reqURI);

		// 1-- 接收到访问信号先记录统计数据
		boolean flag = serviceControl.serviceAccessIncrease(svctype, organizationidentity, null);
		if (flag) {
			_logger.info("Access Increase, svctype=[{}], organizationidentity=[{}]",
				svctype, organizationidentity);
		}

		// 2-- 判断服务的状态
		String svcTypeNode = getGeometrySvcTypenode(request.getRequestURI());
		return serviceControl.getServiceStatus(svctype, organizationidentity, null, svcTypeNode);
	}


	/**
	 * 解析参数中携带的提供商标识
	 *
	 * @param value 参数值
	 * @return 返回值
	 */
	private static String getProviderFlag(String value) {
		int indexFlag = value.lastIndexOf("-");
		return value.substring(indexFlag+1, value.length());
	}


	/**
	 * 去除参数中的提供商标识
	 * @param value 参数值
	 * @return 返回值
	 */
	private static String getOriginParamValue(String value) {
		int indexFlag = value.lastIndexOf("-");
		return value.substring(0, indexFlag);
	}


	/**
	 * 获取几何计算服务的服务子类型
	 *
	 * @param reqUri 请求URI
	 * @return 返回的几何计算服务子类型
	 */
	public static String getGeometrySvcTypenode(String reqUri) {
		String serviceTypeNode = "";

		if (reqUri.contains(GEOMETRY_SERVICE.SUBTYPE_AREA)) {
			serviceTypeNode = GEOMETRY_SERVICE.SUBTYPE_AREA;
		} else if (reqUri.contains(GEOMETRY_SERVICE.SUBTYPE_AZIMUTH)) {
			serviceTypeNode = GEOMETRY_SERVICE.SUBTYPE_AZIMUTH;
		} else if (reqUri.contains(GEOMETRY_SERVICE.SUBTYPE_BUFFER)) {
			serviceTypeNode = GEOMETRY_SERVICE.SUBTYPE_BUFFER;
		} else if (reqUri.contains(GEOMETRY_SERVICE.SUBTYPE_LENGTH)) {
			serviceTypeNode = GEOMETRY_SERVICE.SUBTYPE_LENGTH;
		} else if (reqUri.contains(GEOMETRY_SERVICE.SUBTYPE_SRSCONVERSION)) {
			serviceTypeNode = GEOMETRY_SERVICE.SUBTYPE_SRSCONVERSION;
		}

		return serviceTypeNode;
	}


	/**
	 * 从请求URI中获取服务提供机构标识
	 *
	 * @param uri 请求URI
	 * @return 返回值
	 */
	private static String getOrgFromURI(String svctype, String uri) {
		String[] uriParams = uri.split("/");
		//List<String> uriParamsArr = Arrays.asList(uriParams);

		if (svctype.equalsIgnoreCase(GEOMETRY_SERVICE.value)) {
			return uriParams[4];
		}
		return uriParams[3];
	}

	private static void requestMethodTest(HttpServletRequest request) {
		/*String pathInfo = request.getPathInfo();
		String pathTranslated = request.getPathTranslated();
		String reqSessionId = request.getRequestedSessionId();
		String remoteUser = request.getRemoteUser();*/

		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		StringBuffer requestURL = request.getRequestURL();
		String requestURLStr = requestURL.toString();
		String remoteAddr = request.getRemoteAddr();
		String remoteHost = request.getRemoteHost();
		int remotePort = request.getRemotePort();
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String localName = request.getLocalName();
		String localAddr = request.getLocalAddr();
		int localPort = request.getLocalPort();
		String contentType = request.getContentType();
		String characterEncoding = request.getCharacterEncoding();
		String contextPath = request.getContextPath();
		String servletPath = request.getServletPath();
	}

}
