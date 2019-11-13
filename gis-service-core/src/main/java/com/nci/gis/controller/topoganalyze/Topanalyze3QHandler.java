package com.nci.gis.controller.topoganalyze;


import com.nci.common.AppParams;
import com.nci.common.AuxParams;
import com.nci.common.DataProcFunction;
import com.nci.constants.SERVICE_MAPPING_IDENTITY;
import com.nci.constants.SERVICE_WEBSERVER_PREFIX;
import com.nci.gis.common.TopServiceMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 地形分析服务处理类-三期模式
 *
 * @since 1.0.0 2019年09月17日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
public class Topanalyze3QHandler implements DataProcFunction<String> {

	private static final Logger _logger = LoggerFactory.getLogger(Topanalyze3QHandler.class);


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
		String URLStr;
		String svcMapping = __appParams.get(SERVICE_MAPPING_IDENTITY.value).toString();
		switch (svcMapping) {
			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_DISTANCE:
				URLStr = buildRequestURL4Distance(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_AREA:
				URLStr = buildRequestURL4Area(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_AREAOFRING:
				URLStr = buildRequestURL4AreaOfRing(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_POINTELEVATION:
				URLStr = buildRequestURL4PointElevation(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_ELEVATION:
				URLStr = buildRequestURL4Elevation(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_VOLUME:
				URLStr = buildRequestURL4Volume(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_P2PSIGHT:
				URLStr = buildRequestURL4P2PSight(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_VIEWSIGHT:
				URLStr = buildRequestURL4ViewSight(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_SLOPE:
				URLStr = buildRequestURL4Slope(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_ASPECT:
				URLStr = buildRequestURL4Aspect(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			case TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_PROFILE:
				URLStr = buildRequestURL4Profile(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
				break;

			default:
				URLStr = buildRequestURL(
					__appParams.get(SERVICE_WEBSERVER_PREFIX.value).toString(),
					__appParams.get(SERVICE_MAPPING_IDENTITY.value).toString(),
					request);
		}
		_logger.info("【地形分析服务】Current Request URL = [{}].", URLStr);


		/*
		 * 2-- 执行REST请求
		 */
		RestTemplate restTemplate = new RestTemplate();
		String resultData;
		try {
			resultData = restTemplate.getForObject(URLStr, String.class);
		} catch (Exception e) {
			_logger.error("【地形分析服务】URL Request failed,url=[{}].error info=[{}]", URLStr, e.getMessage());
			throw new Exception(e.getMessage());
		}

		_logger.info("【地形分析服务】URL Request success,[{}].", URLStr);
		return resultData;
	}


	/**
	 * 构建 [地形分析服务] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");


		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {
				sb.append(key);
				sb.append("=");
				sb.append(val[0]);
				sb.append("&");
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}

	/**
	 * 构建 [地形分析服务-地表距离量算] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4Distance(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");


		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append(key);
					sb.append("=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	/**
	 * 构建 [地形分析服务-多边形地表面积量算] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4Area(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append(key);
					sb.append("=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	/**
	 * 构建 [地形分析服务-圆形地表面积量算] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4AreaOfRing(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append("point=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	/**
	 * 构建 [地形分析服务-点高程量算] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4PointElevation(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append("point=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	/**
	 * 构建 [地形分析服务-区域高程量算] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4Elevation(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append(key);
					sb.append("=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	/**
	 * 构建 [地形分析服务-挖方填方] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4Volume(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append(key);
					sb.append("=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	/**
	 * 构建 [地形分析服务-两点通视] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4P2PSight(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append(key);
					sb.append("=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	/**
	 * 构建 [地形分析服务-区域通视] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4ViewSight(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append("point=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	/**
	 * 构建 [地形分析服务-坡度分析] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4Slope(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append(key);
					sb.append("=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	/**
	 * 构建 [地形分析服务-坡向分析] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4Aspect(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append(key);
					sb.append("=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


	/**
	 * 构建 [地形分析服务-断面分析] 后端实际服务的请求URL
	 *
	 * @param gisServerUrl 	服务器基础URL
	 * @param svcMapping 	服务标识
	 * @param request		请求参数
	 * @return				返回值
	 */
	private static String buildRequestURL4Profile(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		String alias = params.get("alias")[0];
		String version = params.get("version")[0];

		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/maps/services/gstopanalyze");
		sb.append("/");
		sb.append(alias);
		sb.append("/");
		sb.append(version);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);

			if (!key.equals("alias") && !key.equals("version")) {

				// 匹配三期的points值格式
				if (key.equals("points")) {
					String valnew = val[0].replace("_", ",");
					sb.append(key);
					sb.append("=");
					sb.append(valnew);
					sb.append("&");
				} else {
					sb.append(key);
					sb.append("=");
					sb.append(val[0]);
					sb.append("&");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		return sb.toString();
	}


}
