package com.nci.gis.controller.maptile;

import com.nci.common.AppParams;
import com.nci.constants.*;
import com.nci.constants.ServiceTypes.MAPTILE_SERVICE;
import com.nci.gis.common.dynamic.classloader.SpringContextUtil;
import com.nci.gis.conf.GisAppServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.nci.gis.common.ClassUtils.getParameterClass;

/**
 * 服务类-地图瓦片服务资源
 *
 * @since 1.0.0 2019年10月23日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Service
public class MapTileService {

	private static final Logger _logger = LoggerFactory.getLogger(MapTileService.class);

	// 约定的数据处理方法
	private static final String PROCESS_METHOD = DATA_PROCESS_METHOD.value;

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_MAPTILE = MAPTILE_SERVICE.value;

	// 注入自定义配置，gisAppServiceConfig.toString()可获取到所有内容
	private final GisAppServiceConfig gisAppServiceConfig;

	@Autowired
	public MapTileService(GisAppServiceConfig gisAppServiceConfig) {
		this.gisAppServiceConfig = gisAppServiceConfig;
	}


	public String execMapTile(String org, String format, String alias, String version,
							  String namespace, String layerAlias,
							  String level, String row, String col,
							  String imageType, String dataType, String dataFormat,
							  String appType, String dataVersion, String datascale,
							  String dataresolution, String auxParams,
							  HttpServletRequest req,
							  HttpServletResponse response) {

		/*
		 * 0-- 预备参数准备
		 */
		/*// 0--1 解析请求中的供应商参数
		String supplier = getProviderFlag(alias);
		if (supplier.isEmpty()) {
			supplier = ProviderDef.PROVIDER_CETC15;
		}

		// 0--2 去掉请求参数中的供应商信息并替换参数值
		alias = getOriginParamValue(alias);

		String[] aliasArr = new String[1];
		aliasArr[0] = alias;
		Map<String, String[]> mapParams = req.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.info("Params=[org={},format={},alias={},version={},namespace={},layerAlias={},level={},row={},col={},imageType={}," +
					"dataType={},dataFormat={},appType={},dataVersion={},datascale={},dataresolution={},auxParams={}]",
				org, format, alias, version, namespace, layerAlias, level, row, col, imageType,
				dataType, dataFormat, appType, dataVersion, datascale, dataresolution, auxParams);
			_logger.error(e.getMessage());
			return buildMapTileOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage()).toString();
		}*/

		_logger.info("Params=[org={},format={},alias={},version={},namespace={},layerAlias={},level={},row={},col={},imageType={}," +
				"dataType={},dataFormat={},appType={},dataVersion={},datascale={},dataresolution={},auxParams={}]",
			org, format, alias, version, namespace, layerAlias, level, row, col, imageType,
			dataType, dataFormat, appType, dataVersion, datascale, dataresolution, auxParams);

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> maptileConfigArr = gisAppServiceConfig.getGsmaptile();
		String[] confArr = getServerConf(maptileConfigArr, org);

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
			return buildMapTileOutputData(StateCodes.STATE_CODE_FAILED, errStr).toString();
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildMapTileOutputData(StateCodes.STATE_CODE_FAILED, errStr).toString();
		}

		getTile(req, response, SERVER_PREFIX, SERVER_HANDLER_CLASS);
		return null;
	}

	private static void getTile(
		HttpServletRequest req, HttpServletResponse response,
		String SERVCIE_WEBSERVER_PREFIX, String SERVCIE_WEBSERVER_INTERFACE_IMPL_CLASS) {

		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		// 2--0 MIME Type、编码
		String format = req.getParameter("format");
		String imageType = req.getParameter("imageType");
		String mimeType = format + "/" + imageType;

		String encoding = "gb2312";

		byte[] result = null;

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVCIE_WEBSERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_MAPTILE);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVCIE_WEBSERVER_INTERFACE_IMPL_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = req;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVCIE_WEBSERVER_INTERFACE_IMPL_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (byte[]) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVCIE_WEBSERVER_INTERFACE_IMPL_CLASS);
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
			response.setContentType(String.format("%s; charset=%s", mimeType, encoding));
			if (result != null) {
				response.setContentLength(result.length);

				java.io.OutputStream os = response.getOutputStream();
				os.write(result);
			}
		} catch (ClassNotFoundException|
			NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException |
			InstantiationException|
			IOException e) {
			String errStr = "【地图瓦片服务-Tile资源】URL Request failed." + e.getMessage();
			_logger.error(errStr);
		}
	}


	/**
	 * 根据状态码、消息内容构建地图瓦片服务-Tile资源输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @return		返回值
	 */
	private static MapTileOutputData buildMapTileOutputData(int code, String msg) {
		MapTileOutputData mapTileOutputData = new MapTileOutputData();
		mapTileOutputData.setCode(code);
		mapTileOutputData.setMsg(msg);
		return mapTileOutputData;
	}


	/**
	 * 根据状态码、消息内容、数据构建地图瓦片服务-Tile资源输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @param data	数据内容
	 * @return		返回值
	 */
	private static MapTileOutputData buildMapTileOutputData(int code, String msg, String data) {
		MapTileOutputData mapTileOutputData = new MapTileOutputData();
		mapTileOutputData.setCode(code);
		mapTileOutputData.setMsg(msg);
		mapTileOutputData.setResultBody(data);
		return mapTileOutputData;
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



