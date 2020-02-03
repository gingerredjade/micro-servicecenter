package com.micro.controller.geometry;

import com.micro.common.AppParams;
import com.micro.common.AuxParams;
import com.micro.common.DataProcFunction;
import com.micro.constants.SERVICE_MAPPING_IDENTITY;
import com.micro.constants.SERVICE_WEBSERVER_PREFIX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 几何计算服务处理类
 *
 * @since 1.0.0 2019年09月12日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
public class GeometryHandler implements DataProcFunction<String> {

	private static final Logger _logger = LoggerFactory.getLogger(GeometryHandler.class);


	/**
	 * 获取经过处理的请求数据
	 *
	 * @param request     REST请求参数
	 * @param __appParams 用于应用请求控制的参数
	 * @param __auxParams 用于数据处理控制的扩展参数，若为null将被忽略
	 * @return			  返回值
	 * @throws Exception  异常信息
	 */
	@Override
	public String processData(HttpServletRequest request, AppParams __appParams, AuxParams __auxParams) throws Exception {

		/*
		 * 1-- 根据请求参数,构造请求URI
		 */
		String svcMappingIdentity = __appParams.get(SERVICE_MAPPING_IDENTITY.value).toString();
		String URLStr = buildRequestURL(
			__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
			__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
			request);
		_logger.info("【几何计算服务-{}】Current Request URL = [{}].", svcMappingIdentity, URLStr);

		/*
		 * 2-- 执行REST请求
		 */
		RestTemplate restTemplate = new RestTemplate();
		String resultData;
		try {
			resultData = restTemplate.getForObject(URLStr, String.class);
		} catch (Exception e) {
			_logger.error("【几何计算服务-{}】URL Request failed,url=[{}].error info=[{}]", svcMappingIdentity, URLStr, e.getMessage());
			throw new Exception(e.getMessage());
		}

		_logger.info("【几何计算服务-{}】URL Request success,[{}].", svcMappingIdentity, URLStr);
		return resultData;
	}


	/**
	 * 构建几何计算服务后端实际服务的请求URL
	 * 	形如protocol://hostname[:port]/xxx/maps/svcs/geometry/length[?format=value1&points=value2&srs=value3][&auxParams=auxParamsVal]
	 *
	 * @param gisServerUrl	服务器基础URL
	 * @param svcMapping	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/svcs/geometry/");
		sb.append(svcMapping);
		sb.append("?");

		Map<String, String[]> params = request.getParameterMap();

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);
			sb.append(key);
			sb.append("=");
			sb.append(val[0]);
			sb.append("&");
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


}
