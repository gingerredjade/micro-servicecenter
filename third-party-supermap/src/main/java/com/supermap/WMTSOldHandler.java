package com.supermap;

import com.alibaba.fastjson.JSONObject;
import com.micro.common.AppParams;
import com.micro.common.AuxParams;
import com.micro.common.DataProcFunction;
import com.micro.constants.SERVICE_FUNCTION_IDENTITY;
import com.micro.constants.SERVICE_MAPPING_IDENTITY;
import com.micro.constants.SERVICE_WEBSERVER_PREFIX;
import com.micro.constants.ServiceTypes.OGC_SERVICE_WMTS;
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
import java.util.Map;

/**
 * OGC规范-网络地图瓦片服务（WMTS-Web Map Tile Service）处理类-超图SuperMap
 *
 * @since 1.0.0 2019年10月31日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
public class WMTSOldHandler implements DataProcFunction<Object> {

	private static final Logger _logger = LoggerFactory.getLogger(WMTSOldHandler.class);

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

		String URLStr = "";
		try {
			if (svcFunc.equalsIgnoreCase(OGC_SERVICE_WMTS.REQUEST_CAPABILITIES)) {

				/*
				 * 1-- 根据请求参数,构造请求URI
				 */
				URLStr = buildRequestURL4Capabilities(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				_logger.info("【SuperMap OGC-WMTS服务】Current Request URL = [{}].", URLStr);


				/*
				 * 2-- 执行REST请求
				 */
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet httpGet = new HttpGet(URLStr);

				CloseableHttpResponse resp = httpClient.execute(httpGet);
				HttpEntity entity = resp.getEntity();

				// 获取返回实体
				String content = EntityUtils.toString(entity, "utf-8");

				_logger.info("【SuperMap OGC-WMTS服务】URL Request success,[{}].", URLStr);
				return content;
			} else if ((svcFunc.equalsIgnoreCase(OGC_SERVICE_WMTS.REQUEST_TILE))) {

				/*
				 * 1-- 根据请求参数,构造请求URI
				 */
				URLStr = buildRequestURL4Tile(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				_logger.info("【SuperMap OGC-WMTS服务】Current Request URL = [{}].", URLStr);


				/*
				 * 2-- 执行REST请求
				 */
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet httpGet = new HttpGet(URLStr);

				CloseableHttpResponse resp = httpClient.execute(httpGet);
				HttpEntity entity = resp.getEntity();

				// 获取返回实体
				byte[] content = EntityUtils.toByteArray(entity);

				_logger.info("【SuperMap OGC-WMTS服务】URL Request success,[{}].", URLStr);
				return content;
			}
		} catch (IOException e) {
			_logger.error("【SuperMap OGC-WMTS服务】URL Request failed,url=[{}].error info=[{}]", URLStr, e.getMessage());
			throw new Exception(e.getMessage());
		}
		return null;
	}


	/**
	 * 构建超图的OGC-WMTS服务后端实际服务的请求URL
	 * 		形如：http://192.168.1.120:8090/iserver/services/map-world/wmts100?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=World&STYLE=default&TILEMATRIXSET=GlobalCRS84Scale_World&TILEMATRIX=2&TILEROW=0&TILECOL=3&FORMAT=image/png
	 *
	 * @param gisServerUrl	超图GIS服务器基础URL
	 * @param svcMapping	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4Tile(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		String auxParams = request.getParameter("auxParams");
		JSONObject jsonObject = JSONObject.parseObject(auxParams);
		String a = (String) jsonObject.get("a");
		String b = (String) jsonObject.get("b");


		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/services/");
		sb.append(a);
		sb.append("/");
		sb.append(b);
		sb.append("?");

		Map<String, String[]> params = request.getParameterMap();

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equalsIgnoreCase("auxParams")) {
				sb.append(key);
				sb.append("=");
				sb.append(val[0]);
				sb.append("&");
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	private static String buildRequestURL4Capabilities(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/services/");

		String auxParams = request.getParameter("auxParams");
		if (auxParams != null && auxParams.length() != 0) {
			JSONObject jsonObject = JSONObject.parseObject(auxParams);
			String a = (String) jsonObject.get("a");
			String b = (String) jsonObject.get("b");

			sb.append(a);
			sb.append("/");
			sb.append(b);
		}
		return sb.toString();
	}


}
