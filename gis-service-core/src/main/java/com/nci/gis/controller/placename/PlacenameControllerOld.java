package com.nci.gis.controller.placename;

import com.nci.common.AppParams;
import com.nci.constants.*;
import com.nci.constants.ServiceTypes.PLACENAME_SERVICE;
import com.nci.gis.conf.GisAppServiceConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.nci.gis.common.ClassUtils.getParameterClass;

/**
 * 功能服务-地名检索服务REST
 *
 * @since 1.0.0 2019年09月12日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "地名检索服务",
	value = "PlacenameControllerOld", description = "根据检索条件，对地名数据进行检索操作")
//@RestController
@EnableAutoConfiguration
@CrossOrigin
public class PlacenameControllerOld {
	private static final Logger _logger = LoggerFactory.getLogger(PlacenameControllerOld.class);

	// 约定的数据处理方法
	private static final String PROCESS_METHOD = DATA_PROCESS_METHOD.value;

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_PLACENAME = PLACENAME_SERVICE.value;

	// 注入自定义配置，gisAppServiceConfig.toString()可获取到所有内容
	@Autowired
	private GisAppServiceConfig gisAppServiceConfig;


	@ApiOperation(value = "执行地名检索服务", notes = "根据检索条件，检索地名数据。")
	@RequestMapping(value = {SERVICE_MAPPING_PLACENAME}, method = RequestMethod.GET)
	public PlacenameOutputData execute(

		@ApiParam(name = "format", allowableValues = "json",
			value = "应答数据编码")
		@RequestParam(value = "format", defaultValue = "json") String format,

		@ApiParam(name = "searchInfo", required = true,
			value = "检索条件。字符串型，用于表示检索条件。<br />" +
				"该参数携带提供商标识。<br />" +
				"传递参数时需在原始参数值后加上服务提供商标识，形如：原始参数值-cetc15。<br />" +
				"15所：cetc15<br />" +
				"超图:sm<br />" +
				"国遥新天地:ev<br />" +
				"星球时空:mc<br />" +
				"四维图新:nav<br />" +
				"庚图:gt<br />")
		@RequestParam(value = "searchInfo", defaultValue = "") String searchInfo,

		@ApiParam(name = "featureNum",
			value = "查询目标最大个数。正整数。")
		@RequestParam(value = "featureNum", defaultValue = "") String featureNum,

		@ApiParam(name = "featureType",
			value = "一般不填，使用默认值。<br />" +
				"查询目标几何类型。<br />" +
				"整型，为空时值为524287，表示查询所有目标，一般使用默认。")
		@RequestParam(value = "featureType", defaultValue = "") String featureType,

		@ApiParam(name = "names",
			value = "一般不填，使用默认值。<br />" +
				"查询到的目标属性字段列表。<br />" +
				"属性字段列表，用于设置要获取的查询目标的属性字段，多个属性字段之间以逗号分隔。")
		@RequestParam(value = "names", defaultValue = "") String names,


		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {


		/*
		 * 0-- 预备参数准备
		 */
		// 0--1 解析请求中的供应商参数
		String supplier = getProviderFlag(searchInfo);
		if (supplier.isEmpty()) {
			supplier = ProviderDef.PROVIDER_CETC15;
		}

		// 0--2 去掉请求参数中的供应商信息并替换参数值
		searchInfo = getOriginParamValue(searchInfo);

		String[] searchInfoArr = new String[1];
		searchInfoArr[0] = searchInfo;
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("searchInfo", searchInfoArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildPlacenameOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> placenameConfigArr = gisAppServiceConfig.getGsplacename();
		String[] confArr = getServerConf(placenameConfigArr, supplier);

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
			return buildPlacenameOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildPlacenameOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result;

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_PLACENAME);

		Class<?> clazz;
		try {
			// 2--2 通过反射获取对象
			clazz = Class.forName(SERVER_HANDLER_CLASS);
			Object impl = clazz.newInstance();

			// 2--3 获取对象的方法
			Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
			Method m = clazz.getMethod(PROCESS_METHOD, parameters);

			// 2--4 反射调用方法
			Object[] objects = new Object[3];
			objects[0] = request;
			objects[1] = appParams;
			objects[2] = null;

			result = (String) m.invoke(clazz.newInstance(), objects);
		} catch (ClassNotFoundException|
			NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException|
			InstantiationException e) {
			_logger.error("【地名检索服务】URL Request failed,format=[{}],searchInfo=[{}],featureNum=[{}],featureType=[{}],names=[{}],auxParams=[{}]." + e.getMessage(),
				format, searchInfo, featureNum, featureType, names, auxParams);
			String errStr = "【地名检索服务】URL Request failed." + e.getMessage();
			return buildPlacenameOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildPlacenameOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}


	/**
	 * 根据状态码、消息内容构建地名检索服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @return		返回值
	 */
	private static PlacenameOutputData buildPlacenameOutputData(int code, String msg) {
		PlacenameOutputData placenameOutputData = new PlacenameOutputData();
		placenameOutputData.setCode(code);
		placenameOutputData.setMsg(msg);
		return placenameOutputData;
	}


	/**
	 * 根据状态码、消息内容、数据构建地名检索服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @param data	数据内容
	 * @return		返回值
	 */
	private static PlacenameOutputData buildPlacenameOutputData(int code, String msg, String data) {
		PlacenameOutputData placenameOutputData = new PlacenameOutputData();
		placenameOutputData.setCode(code);
		placenameOutputData.setMsg(msg);
		placenameOutputData.setResultBody(data);
		return placenameOutputData;
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
