package com.nci.gis.controller.ogc.ows.wmts;

import com.nci.common.AppParams;
import com.nci.common.AuxParams;
import com.nci.common.DataProcFunction;
import com.nci.constants.SERVICE_FUNCTION_IDENTITY;
import com.nci.constants.SERVICE_MAPPING_IDENTITY;
import com.nci.constants.SERVICE_WEBSERVER_PREFIX;
import com.nci.constants.ServiceTypes.OGC_SERVICE_WMTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * OGC规范-网络地图瓦片服务（WMTS-Web Map Tile Service）处理类-三期模式
 *
 * @since 1.0.0 2019年09月24日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
public class WMTSHandler implements DataProcFunction<Object> {

	private static final Logger _logger = LoggerFactory.getLogger(WMTSHandler.class);

	/**
	 * 获取经过处理的请求数据
	 *
	 * @param request     REST请求参数
	 * @param __appParams 用于应用请求控制的参数
	 * @param __auxParams 用于数据处理控制的扩展参数，若为null将被忽略
	 * @throws Exception 异常信息
	 * @return 返回值
	 */
	@Override
	public Object processData(HttpServletRequest request, AppParams __appParams, AuxParams __auxParams) throws Exception {
		String svcFunc = __appParams.get(SERVICE_FUNCTION_IDENTITY.value).toString();

		/*
		 * 1-- 根据请求参数,构造请求URI
		 */
		String URLStr = buildRequestURL(
			__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
			__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
			request);
		_logger.info("【OGC-WMTS服务】Current Request URL = [{}].", URLStr);

		/*
		 * 2-- 执行REST请求
		 */
		RestTemplate restTemplate = new RestTemplate();
		try {
			if (svcFunc.equalsIgnoreCase(OGC_SERVICE_WMTS.REQUEST_CAPABILITIES)) {
				String res = restTemplate.getForObject(URLStr, String.class);
				_logger.info("【OGC-WMTS服务】URL Request success,[{}].", URLStr);
				return res;
			} else if ((svcFunc.equalsIgnoreCase(OGC_SERVICE_WMTS.REQUEST_TILE))) {
				byte[] res = restTemplate.getForObject(URLStr, byte[].class);
				_logger.info("【OGC-WMTS服务】URL Request success,[{}].", URLStr);
				return res;
			}
		} catch (Exception e) {
			_logger.error("【OGC-WMTS服务】URL Request failed,url=[{}].error info=[{}]", URLStr, e.getMessage());
			throw new Exception(e.getMessage());
		}
		return null;
	}


	/**
	 * 构建OGC-WMTS服务后端实际服务的请求URL
	 *
	 * @param gisServerUrl	GIS服务器基础URL
	 * @param svcMapping	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);

		String queryString = request.getQueryString();
		String requestURI = request.getRequestURI();
		String[] reqUriParams = requestURI.split("/");

		String org = reqUriParams[2];
		int orgIndex = requestURI.indexOf(org);

		String additionalURL = requestURI.substring(orgIndex+org.length());

		sb.append(additionalURL);
		sb.append("?");
		sb.append(queryString);

		return sb.toString();
	}
}
