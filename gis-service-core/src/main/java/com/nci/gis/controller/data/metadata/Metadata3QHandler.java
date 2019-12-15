package com.nci.gis.controller.data.metadata;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nci.common.AppParams;
import com.nci.common.AuxParams;
import com.nci.common.DataProcFunction;
import com.nci.constants.SERVICE_WEBSERVER_PREFIX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * 元数据服务处理类-三期模式
 *
 * @since 1.0.0 2019年12月05日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
public class Metadata3QHandler implements DataProcFunction<String> {

	private static final Logger _logger = LoggerFactory.getLogger(Metadata3QHandler.class);

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
		 * 1-- 根据请求参数,构造后端服务请求URI
		 */
		String svcType = request.getParameter("type");
		String alias = request.getParameter("alias");
		String version = request.getParameter("version");

		JSONObject svcIdObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("serviceCode", svcType);
		obj.put("serviceAlias", alias);
		obj.put("serviceVersion", version);
		jsonArray.add(obj);
		svcIdObj.put("svcID", jsonArray);
		String q = svcIdObj.toString();

		String URLStr = __appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString() +
			"/maps/services?c=gis3.services.gs.catalog.ICatalog&m=getServicesCatalogByIdentifier&t=&f=json&q={q}";
		_logger.info("【元数据服务】Current Request URL = [{}]. q=[{}]", URLStr, q);

		/*
		 * 2-- 执行REST请求
		 */
		RestTemplate restTemplate = new RestTemplate();
		String resultData;
		try {
			ResponseEntity<String> entity = restTemplate.exchange(URLStr, HttpMethod.GET, null, String.class, q);
			resultData = entity.getBody();
		} catch (Exception e) {
			_logger.error("【元数据服务】URL Request failed,url=[{}],q=[{}].error info=[{}]", URLStr, q, e.getMessage());
			throw new Exception(e.getMessage());
		}

		_logger.info("【元数据服务】URL Request success,[{}]. q=[{}]", URLStr, q);
		return resultData;
	}
}
