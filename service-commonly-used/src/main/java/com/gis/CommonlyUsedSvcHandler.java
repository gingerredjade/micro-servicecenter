package com.gis;

import com.nci.common.AppParams;
import com.nci.common.AuxParams;
import com.nci.common.CommonUsedDataProc;
import com.nci.constants.SERVICE_WEBSERVER_PREFIX;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 通用适配服务-服务处理类-common_used_svc
 *
 * @since 1.0.0 2019年11月06日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
public class CommonlyUsedSvcHandler implements CommonUsedDataProc<Object> {

	private static final Logger _logger = LoggerFactory.getLogger(CommonlyUsedSvcHandler.class);

	/**
	 * 获取经过处理的请求数据
	 *
	 * @param request     REST请求参数
	 * @param __appParams 用于应用请求控制的参数
	 * @param __auxParams 用于数据处理控制的扩展参数，若为null将被忽略
	 * @throws Exception 异常信息
	 * @return 返回值
	 */
	public Object processData(HttpServletRequest request, HttpServletResponse response, AppParams __appParams, AuxParams __auxParams) throws Exception {
		String URLStr = "";
		try {
			/*
			 * 1-- 根据请求参数,构造请求URI
			 */
			URLStr = buildRequestURL(
				__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
				null,
				request);
			_logger.info("Current Request URL = [{}].", URLStr);


			/*
			 * 2-- 执行REST请求
			 */
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(URLStr);

			CloseableHttpResponse resp = httpClient.execute(httpGet);
			HttpEntity entity = resp.getEntity();

			Header contentType = entity.getContentType();
			if (contentType != null) {
				response.setHeader(contentType.getName(), contentType.getValue());
			}

			/*String contentCharset = EntityUtils.getContentCharSet(entity);
			Header contentEncoding = entity.getContentEncoding();*/

			// 获取返回实体
			byte[] contentArr = EntityUtils.toByteArray(entity);

			_logger.info("URL Request success,[{}].", URLStr);
			return contentArr;
		} catch (IOException e) {
			_logger.error("URL Request failed,url=[{}].error info=[{}]", URLStr, e.getMessage());
			throw new Exception(e.getMessage());
		}
	}


	/**
	 * 构建后端实际服务的请求URL
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

		String org = reqUriParams[3];
		int orgIndex = requestURI.indexOf(org);

		String additionalURL = requestURI.substring(orgIndex+org.length());

		sb.append(additionalURL);

		if (queryString != null && queryString.length() != 0) {
			sb.append("?");
			sb.append(queryString);
		}

		return sb.toString();
	}

}
