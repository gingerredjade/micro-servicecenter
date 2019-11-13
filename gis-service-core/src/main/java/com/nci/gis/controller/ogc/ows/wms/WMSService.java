package com.nci.gis.controller.ogc.ows.wms;

import com.nci.common.AppParams;
import com.nci.constants.*;
import com.nci.constants.ServiceTypes.OGC_SERVICE_WMS;
import com.nci.gis.common.dynamic.classloader.SpringContextUtil;
import com.nci.gis.conf.GisAppServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.nci.gis.common.ClassUtils.getParameterClass;

/**
 * 服务类-OGC规范-网络地图服务（WMS-Web Map Service）
 *
 * @since 1.0.0 2019年10月23日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Service
public class WMSService {

	private static final Logger _logger = LoggerFactory.getLogger(WMSService.class);

	// 约定的数据处理方法
	private static final String PROCESS_METHOD = DATA_PROCESS_METHOD.value;

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_WMS = OGC_SERVICE_WMS.value;

	// 注入自定义配置，gisAppServiceConfig.toString()可获取到所有内容
	private final GisAppServiceConfig gisAppServiceConfig;

	@Autowired
	public WMSService(GisAppServiceConfig gisAppServiceConfig) {
		this.gisAppServiceConfig = gisAppServiceConfig;
	}

	public String execWms(String org, String service, String request, String version,
						  String layers, String bbox, String crs, String format,
						  String width, String height, String styles, String auxParams,
						  HttpServletRequest req,
						  HttpServletResponse response) {
		/*
		 * 0-- 预备参数准备
		 */
		/*// 0--1 解析请求中的供应商参数
		String supplier = getProviderFlag(version);
		if (supplier.isEmpty()) {
			supplier = ProviderDef.PROVIDER_CETC15;
		}

		// 0--2 去掉请求参数中的供应商信息并替换参数值
		version = getOriginParamValue(version);

		String[] versionArr = new String[1];
		versionArr[0] = version;
		Map<String, String[]> mapParams = req.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("version", versionArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildWMSOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage()).toString();
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> wmsConfigArr = gisAppServiceConfig.getWms();
		String[] confArr = getServerConf(wmsConfigArr, org);

		String SERVER_PREFIX = confArr[0];
		String SERVER_HANDLER_CLASS = confArr[1];


		/*
		 * 1-- 监测配置参数
		 * 		webserver.prefix、
		 * 		webserver.interface-impl
		 */
		if (SERVER_PREFIX.isEmpty()) {
			String errStr = "GIS Web服务器服务请求串前缀为空，请检查server-prefix配置";
			_logger.error(errStr);
			return buildWMSOutputData(StateCodes.STATE_CODE_FAILED, errStr).toString();
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildWMSOutputData(StateCodes.STATE_CODE_FAILED, errStr).toString();
		}

		// 检测request=GetMap时的必填参数
		if (request.equalsIgnoreCase(OGC_SERVICE_WMS.REQUEST_CAPABILITIES)) {
			getWmsCapabilities(req, response, SERVER_PREFIX, SERVER_HANDLER_CLASS);
			_logger.info("Params=[service={},request={},version={}]", service, request, version);
		} else if (request.equalsIgnoreCase(OGC_SERVICE_WMS.REQUEST_MAP)) {
			if (layers.isEmpty() || bbox.isEmpty() || crs.isEmpty() ||
				format.isEmpty() || width.isEmpty() || height.isEmpty() ) {
				String errStr = "Invalid request URL! The parameters is half-baked!";
				_logger.error(errStr);
				return buildWMSOutputData(StateCodes.STATE_CODE_FAILED, errStr).toString();
			}
			_logger.info("Params=[service={},request={},version={},layers={},bbox={},crs={},format={},width={},height={},styles={},auxParams={}]",
				service, request, version, layers, bbox, crs, format, width, height, styles, auxParams);
			getWmsMap(req, response, SERVER_PREFIX, SERVER_HANDLER_CLASS);
		}
		return null;
	}

	private static void getWmsCapabilities(
		HttpServletRequest req, HttpServletResponse response,
		String SERVER_PREFIX, String SERVER_HANDLER_CLASS) {

		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 *
		 *  自动区分应用内服务库（Java反射方式调用）/外部服务库（Spring反射类库方式调用）
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_WMS);
		appParams.put(SERVICE_FUNCTION_IDENTITY.value, OGC_SERVICE_WMS.REQUEST_CAPABILITIES);

		/* Java Mode or Spring Mode */
		Class<?> clazz;
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);

		Object[] objects = new Object[3];
		objects[0] = req;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring反射类库 反射方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				//Class<?>[] paramTypes = new Class[] {HttpServletRequest.class, AppParams.class, AuxParams.class};
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (String) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java反射方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (String) m.invoke(clazz.newInstance(), objects);
			}

			/*
			 * 3-- 封装应答
			 * 		根据请求的Content-Type(MediaType)，为HTTP响应传输对象打数据格式标签
			 */
			response.setContentType(MediaType.APPLICATION_XML_VALUE);
			String characterEncoding = req.getCharacterEncoding();
			response.setCharacterEncoding(characterEncoding);
			PrintWriter printWriter = response.getWriter();
			if (result != null && result.length() != 0) {
				printWriter.write(result);
			}
			printWriter.flush();
		} catch (ClassNotFoundException|
			NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException |
			InstantiationException|
			IOException e) {
			String errStr = "【OGC-WMS-GetCapabilities服务】URL Request failed." + e.getMessage();
			_logger.error(errStr);
		}
	}


	private static void getWmsMap(
		HttpServletRequest req, HttpServletResponse response,
		String SERVER_PREFIX, String SERVER_HANDLER_CLASS) {

		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		// 2--0 MIME Type、编码
		String formatVal = req.getParameter("format");
		if (formatVal.isEmpty()) {
			formatVal = "image/jpg";
		}

		String encoding = "gb2312";

		byte[] result = null;

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_WMS);
		appParams.put(SERVICE_FUNCTION_IDENTITY.value, OGC_SERVICE_WMS.REQUEST_MAP);

		/* Java Mode or Spring Mode */
		Class<?> clazz;
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);

		Object[] objects = new Object[3];
		objects[0] = req;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring反射类库 反射方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				//Class<?>[] paramTypes = new Class[] {HttpServletRequest.class, AppParams.class, AuxParams.class};
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (byte[]) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java反射方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (byte[]) m.invoke(clazz.newInstance(), objects);
			}

			/*
			 * 3-- 封装应答
			 */
			response.setContentType(String.format("%s; charset=%s", formatVal, encoding));
			if (result != null) {
				response.setContentLength(result.length);
			}

			java.io.OutputStream os = response.getOutputStream();
			os.write(result);
		} catch (ClassNotFoundException|
			NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException |
			InstantiationException|
			IOException e) {
			String errStr = "【OGC-WMS-GetMap服务】URL Request failed." + e.getMessage();
			_logger.error(errStr);
		}
	}


	/**
	 * 根据状态码、消息内容构建OGC-WMS服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @return		返回值
	 */
	private static WMSOutputData buildWMSOutputData(int code, String msg) {
		WMSOutputData wmsOutputData = new WMSOutputData();
		wmsOutputData.setCode(code);
		wmsOutputData.setMsg(msg);
		return wmsOutputData;
	}


	/**
	 * 根据状态码、消息内容、数据构建OGC-WMS服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @param data	数据内容
	 * @return		返回值
	 */
	private static WMSOutputData buildWMSOutputData(int code, String msg, String data) {
		WMSOutputData wmsOutputData = new WMSOutputData();
		wmsOutputData.setCode(code);
		wmsOutputData.setMsg(msg);
		wmsOutputData.setResultBody(data);
		return wmsOutputData;
	}


	/**
	 * 通过配置信息列表遍历符合条件的GIS服务器前缀和数据处理类
	 *
	 * @param list 配置信息列表
	 * @return 	   返回值
	 */
	private String[] getServerConf(List<Map<String, String>> list, String supplier) {
		String[] serverConf = new String[2];
		String serverPrefix;
		String serverHandlerCls;

		for (Map<String, String> wmsConfig : list) {
			for (Map.Entry<String, String> entry : wmsConfig.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key.equalsIgnoreCase("author") &&
					value.equalsIgnoreCase(supplier)){

					serverPrefix = wmsConfig.get(GisAppConfKey.SERVER_PREFIX_KEY);
					serverHandlerCls = wmsConfig.get(GisAppConfKey.SERVER_HANDLER_CLASS_KEY);

					serverConf[0] = serverPrefix;
					serverConf[1] = serverHandlerCls;
				}
			}
		}
		return serverConf;
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


}
