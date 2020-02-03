package com.supermap;

import com.micro.common.AppParams;
import com.micro.common.AuxParams;
import com.micro.common.DataProcFunction;
import com.micro.constants.SERVICE_FUNCTION_IDENTITY;
import com.micro.constants.SERVICE_MAPPING_IDENTITY;
import com.micro.constants.SERVICE_WEBSERVER_PREFIX;
import com.micro.constants.ServiceTypes.OGC_SERVICE_WMS;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * OGC规范-网络地图服务（WMS-Web Map Service）处理类-超图SuperMap
 *
 * @since 1.0.0 2019年10月31日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
public class WMSHandler implements DataProcFunction<Object> {

	private static final Logger _logger = LoggerFactory.getLogger(WMSHandler.class);

	/**
	 * 获取经过处理的请求数据
	 *
	 * @param request     REST请求参数
	 * @param __appParams 用于应用请求控制的参数
	 * @param __auxParams 用于数据处理控制的扩展参数，若为null将被忽略
	 * @throws Exception 异常信息
	 * @return 返回值
	 */
	public Object processData(HttpServletRequest request, AppParams __appParams, AuxParams __auxParams) throws Exception {
		String svcFunc = __appParams.get(SERVICE_FUNCTION_IDENTITY.value).toString();

		// 模拟浏览器
		/*httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebkit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
		httpGet.setHeader("Host", "supermap.com");
		httpGet.setHeader("Origin", "http://supermap.com");
		httpGet.setHeader("Referer", "");
		httpGet.setHeader("X-Requested-With", "XMLHttpRequest");*/

		String URLStr = "";
		try {
			if (svcFunc.equalsIgnoreCase(OGC_SERVICE_WMS.REQUEST_CAPABILITIES)) {

				/*
				 * 1-- 根据请求参数,构造请求URI
				 */
				URLStr = buildRequestURL4Capabilities(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				_logger.info("【SuperMap OGC-WMS服务】Current Request URL = [{}].", URLStr);


				/*
				 * 2-- 执行REST请求
				 */
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet httpGet = new HttpGet(URLStr);

				CloseableHttpResponse resp = httpClient.execute(httpGet);
				HttpEntity entity = resp.getEntity();

				// 获取返回实体
				String content = EntityUtils.toString(entity, "utf-8");

				_logger.info("【SuperMap OGC-WMS服务】URL Request success,[{}].", URLStr);
				return content;
			} else if ((svcFunc.equalsIgnoreCase(OGC_SERVICE_WMS.REQUEST_MAP))) {

				/*
				 * 1-- 根据请求参数,构造请求URI
				 */
				URLStr = buildRequestURL4Map(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				_logger.info("【SuperMap OGC-WMS服务】Current Request URL = [{}].", URLStr);


				/*
				 * 2-- 执行REST请求
				 */
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet httpGet = new HttpGet(URLStr);

				CloseableHttpResponse resp = httpClient.execute(httpGet);
				HttpEntity entity = resp.getEntity();

				// 获取返回实体
				byte[] content = EntityUtils.toByteArray(entity);

				_logger.info("【SuperMap OGC-WMS服务】URL Request success,[{}].", URLStr);
				return content;
			}
		} catch (IOException e) {
			_logger.error("【SuperMap OGC-WMS服务】URL Request failed,url=[{}].error info=[{}]", URLStr, e.getMessage());
			throw new Exception(e.getMessage());
		}
		return null;
	}


	/**
	 * 构建超图的OGC-WMS服务后端实际服务的请求URL
	 * 		形如：http://192.168.1.120:8090/iserver/services/map-china400/wms130/China?LAYERS=China&VERSION=1.3.0&EXCEPTIONS=INIMAGE&SERVICE=WMS&REQUEST=GetMap&STYLES=&FORMAT=image%2Fpng&CRS=EPSG%3A3857&BBOX=10018754.17,9.3132257461548e-10,15028131.255,5009377.085&WIDTH=256&HEIGHT=256
	 *
	 * @param gisServerUrl	超图GIS服务器基础URL
	 * @param svcMapping	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4Map(String gisServerUrl, String svcMapping, HttpServletRequest request) {
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


	private static String buildRequestURL4Capabilities(String gisServerUrl, String svcMapping, HttpServletRequest request) {
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
