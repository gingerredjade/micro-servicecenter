package com.nci.gis.controller.query;

import com.nci.common.AppParams;
import com.nci.common.AuxParams;
import com.nci.common.DataProcFunction;
import com.nci.constants.SERVICE_MAPPING_IDENTITY;
import com.nci.constants.SERVICE_WEBSERVER_PREFIX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 空间查询服务处理类-三期模式
 *
 * @since 1.0.0 2019年12月05日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
public class QueryHandler implements DataProcFunction<String> {

	private static final Logger _logger = LoggerFactory.getLogger(QueryHandler.class);

	/**
	 * 获取经过处理的请求数据
	 *
	 * @param request     REST请求参数
	 * @param __appParams 用于应用请求控制的参数
	 * @param __auxParams 用于数据处理控制的扩展参数，若为null将被忽略
	 * @return				返回值
	 * @throws Exception	异常信息
	 */
	@Override
	public String processData(HttpServletRequest request, AppParams __appParams, AuxParams __auxParams) throws Exception {
		/*
		 * 1-- 根据请求参数,构造请求URI
		 */
		String URLStr = buildRequestURL(
			__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
			__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
			request);
		_logger.info("【空间查询服务】Current Request URL = [{}].", URLStr);

		/*
		 * 2-- 执行REST请求
		 */
		RestTemplate restTemplate = new RestTemplate();
		String resultData;
		try {
			resultData = restTemplate.getForObject(URLStr, String.class);
		} catch (Exception e) {
			_logger.error("【空间查询服务】URL Request failed,url=[{}].error info=[{}]", URLStr, e.getMessage());
			throw new Exception(e.getMessage());
		}

		_logger.info("【空间查询服务】URL Request success,[{}].", URLStr);
		return resultData;
	}

	/**
	 * 构建空间查询服务后端实际服务的请求URL
	 *
	 * @param gisServerUrl	GIS服务器基础URL
	 * @param svcMapping	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL(String gisServerUrl, String svcMapping, HttpServletRequest request) {

		String alias = request.getParameter("alias");
		String version = request.getParameter("version");

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/");
		sb.append(svcMapping);

		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);

		sb.append("?");

		Map<String, String[]> params = request.getParameterMap();

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			if (!key.equals("alias") && !key.equals("version") && !key.equals("org")) {
				String[] val = entry.getValue();
				_logger.info("[{}]=[{}].", key, val[0]);

				sb.append(key);
				sb.append("=");
				sb.append(val[0]);
				sb.append("&");
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}
}
