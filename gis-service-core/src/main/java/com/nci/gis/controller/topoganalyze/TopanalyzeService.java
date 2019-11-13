package com.nci.gis.controller.topoganalyze;

import com.nci.common.AppParams;
import com.nci.constants.*;
import com.nci.gis.common.TopServiceMappings;
import com.nci.gis.common.dynamic.classloader.SpringContextUtil;
import com.nci.gis.conf.GisAppServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.nci.gis.common.ClassUtils.getParameterClass;

/**
 * 服务类-地形分析服务
 *
 * @since 1.0.0 2019年10月23日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Service
public class TopanalyzeService {

	private static final Logger _logger = LoggerFactory.getLogger(TopanalyzeService.class);

	// 约定的数据处理方法
	private static final String PROCESS_METHOD = DATA_PROCESS_METHOD.value;
	private static final String SERVICE_MAPPING_TOPANALYZE_DISTANCE = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_DISTANCE;
	private static final String SERVICE_MAPPING_TOPANALYZE_AREA = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_AREA;
	private static final String SERVICE_MAPPING_TOPANALYZE_AREAOFRING = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_AREAOFRING;
	private static final String SERVICE_MAPPING_TOPANALYZE_POINTELEVATION = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_POINTELEVATION;
	private static final String SERVICE_MAPPING_TOPANALYZE_ELEVATION = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_ELEVATION;
	private static final String SERVICE_MAPPING_TOPANALYZE_VOLUME = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_VOLUME;
	private static final String SERVICE_MAPPING_TOPANALYZE_P2PSIGHT = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_P2PSIGHT;
	private static final String SERVICE_MAPPING_TOPANALYZE_VIEWSIGHT = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_VIEWSIGHT;
	private static final String SERVICE_MAPPING_TOPANALYZE_SLOPE = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_SLOPE;
	private static final String SERVICE_MAPPING_TOPANALYZE_ASPECT = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_ASPECT;
	private static final String SERVICE_MAPPING_TOPANALYZE_PROFILE = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_PROFILE;

	// 注入自定义配置，gisAppServiceConfig.toString()可获取到所有内容
	private final GisAppServiceConfig gisAppServiceConfig;

	@Autowired
	public TopanalyzeService(GisAppServiceConfig gisAppServiceConfig) {
		this.gisAppServiceConfig = gisAppServiceConfig;
	}

	public TopanalyzeOutputData execDistance(
		String org,
		String format, String alias, String version, String geo, String points,
		String srs, String auxParams,
		HttpServletRequest request) {

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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> topanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(topanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_DISTANCE);

		/* Java Mode or Spring Mode */
		Class<?> clazz;
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring反射类库 反射方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
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
				result = (String) m.invoke(clazz.newInstance(),objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-地表距离量算】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}],geo=[{}],points=[{}],srs=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, alias, version, geo, points, srs, auxParams);
			String errStr = "【地形分析服务-地表距离量算】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public TopanalyzeOutputData execArea(
		String org,
		String format, String alias, String version, String geo,
		String points, String srs, String auxParams,
		HttpServletRequest request) {

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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> topanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(topanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_AREA);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring反射类库 反射方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
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
				result = (String) m.invoke(clazz.newInstance(),objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-多边形地表面积量算】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}],geo=[{}],points=[{}],srs=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, alias, version, geo, points, srs, auxParams);
			String errStr = "【地形分析服务-多边形地表面积量算】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public TopanalyzeOutputData execAreaOfRing(
		String org,
		String format, String alias, String version,
		String radius, String points, String srs, String auxParams,
		HttpServletRequest request) {
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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> wmstopanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(wmstopanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_AREAOFRING);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (String) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (String) m.invoke(clazz.newInstance(),objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-圆形地表面积量算】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}],radius=[{}],points=[{}],srs=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, alias, version, radius, points, srs, auxParams);
			String errStr = "【地形分析服务-圆形地表面积量算】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public TopanalyzeOutputData execPointElevation(
		String org,
		String format, String alias, String version,
		String points, String srs, String auxParams,
		HttpServletRequest request) {

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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> wmstopanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(wmstopanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_POINTELEVATION);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (String) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (String) m.invoke(clazz.newInstance(),objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-点高程量算】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}],points=[{}],srs=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, alias, version, points, srs, auxParams);
			String errStr = "【地形分析服务-点高程量算】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}


	public TopanalyzeOutputData execElevation(
		String org,
		String format, String alias, String version, String geo,
		String points, String srs, String dResolutionH, String dResolutionW,
		String auxParams, HttpServletRequest request) {

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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> wmstopanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(wmstopanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_ELEVATION);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (String) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (String) m.invoke(clazz.newInstance(),objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-区域高程量算】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}]," +
				"geo=[{}],points=[{}],srs=[{}],dResolutionH=[{}],dResolutionW=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, alias, version, geo, points, srs, dResolutionH, dResolutionW, auxParams);
			String errStr = "【地形分析服务-区域高程量算】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public TopanalyzeOutputData execVolume(
		String org,
		String format, String alias, String version, String geo, String points,
		String srs, String dResolutionH, String dResolutionW, String refHeight, String auxParams,
		HttpServletRequest request) {
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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> wmstopanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(wmstopanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_VOLUME);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (String) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (String) m.invoke(clazz.newInstance(),objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-挖方填方】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}]," +
				"geo=[{}],points=[{}],srs=[{}],dResolutionH=[{}],dResolutionW=[{}],refHeight=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, alias, version, geo, points, srs, dResolutionH, dResolutionW, refHeight, auxParams);
			String errStr = "【地形分析服务-挖方填方】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public TopanalyzeOutputData execP2PSight(
		String org,
		String format, String alias, String version, String geo,
		String points, String srs, String dResolutionH, String dResolutionW,
		String drawPic, String observheight, String targetHeight,
		String picWidth, String picHeight, String auxParams,
		HttpServletRequest request) {

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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> wmstopanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(wmstopanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_P2PSIGHT);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (String) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (String) m.invoke(clazz.newInstance(), objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-两点通视】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}],geo=[{}],points=[{}],srs=[{}]," +
					"dResolutionH=[{}],dResolutionW=[{}],drawPic=[{}],observheight=[{}],targetHeight=[{}],picWidth=[{}],picHeight=[{}],auxParams=[{}]."
					+ e.getMessage(), org, format, alias, version, geo, points, srs, dResolutionH, dResolutionW, drawPic,
				observheight, targetHeight, picWidth, picHeight, auxParams);
			String errStr = "【地形分析服务-两点通视】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public TopanalyzeOutputData execViewSight(
		String org,
		String format, String alias, String version, String points, String radius, String srs,
		String dResolutionH, String dResolutionW, String drawPic, String observheight,
		String picWidth, String picHeight, String auxParams,
		HttpServletRequest request) {

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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> wmstopanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(wmstopanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_VIEWSIGHT);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (String) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (String) m.invoke(clazz.newInstance(),objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-区域通视】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}],points=[{}],radius=[{}],srs=[{}]," +
					"dResolutionH=[{}],dResolutionW=[{}],drawPic=[{}],observheight=[{}],picWidth=[{}],picHeight=[{}],auxParams=[{}]."
					+ e.getMessage(), org, format, alias, version, points, radius, srs, dResolutionH, dResolutionW,
				drawPic, observheight, picWidth, picHeight, auxParams);
			String errStr = "【地形分析服务-区域通视】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public TopanalyzeOutputData execSlope(
		String org,
		String format, String alias, String version, String geo, String points,
		String srs, String drawPic, String picWidth, String picHeight,
		String dResolutionH, String dResolutionW, String slopeLevel, String auxParams,
		HttpServletRequest request) {

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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> wmstopanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(wmstopanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_SLOPE);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (String) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (String) m.invoke(clazz.newInstance(),objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-坡度分析】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}],geo=[{}],points=[{}],srs=[{}]," +
				"dResolutionH=[{}],dResolutionW=[{}],drawPic=[{}],picWidth=[{}],picHeight=[{}],slopeLevel=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, alias, version, geo, points, srs, dResolutionH, dResolutionW, drawPic, picWidth, picHeight, slopeLevel, auxParams);
			String errStr = "【地形分析服务-坡度分析】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public TopanalyzeOutputData execAspect(
		String org,
		String format, String alias, String version, String geo, String points, String srs,
		String drawPic, String picWidth, String picHeight, String dResolutionH, String dResolutionW,
		String auxParams, HttpServletRequest request) {

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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> wmstopanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(wmstopanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_ASPECT);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (String) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (String) m.invoke(clazz.newInstance(),objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-坡向分析】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}],geo=[{}],points=[{}],srs=[{}]," +
				"drawPic=[{}],picWidth=[{}],picHeight=[{}],dResolutionH=[{}],dResolutionW=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, alias, version, geo, points, srs, drawPic, picWidth, picHeight, dResolutionH, dResolutionW, auxParams);
			String errStr = "【地形分析服务-坡向分析】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public TopanalyzeOutputData execProfile(
		String org,
		String format, String alias, String version, String geo, String points, String srs,
		String drawPic, String picWidth, String picHeight, String dResolutionH, String dResolutionW,
		String pointNum, String auxParams, HttpServletRequest request) {

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
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("alias", aliasArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}*/

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> wmstopanaConfigArr = gisAppServiceConfig.getGstopanalyze();
		String[] confArr = getServerConf(wmstopanaConfigArr, org);

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
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_TOPANALYZE_PROFILE);

		/* Java Mode or Spring Mode */
		boolean springMode = SpringContextUtil.hasBean(SERVER_HANDLER_CLASS);
		Class<?> clazz;

		Object[] objects = new Object[3];
		objects[0] = request;
		objects[1] = appParams;
		objects[2] = null;

		try {
			if (springMode) {
				/* Spring方式 */
				// 2--2 通过反射获取对象
				Object beanObj = SpringContextUtil.getBean(SERVER_HANDLER_CLASS);
				clazz = beanObj.getClass();

				// 2--3 获取对象的方法
				Class<?>[] paramTypes = getParameterClass(beanObj, PROCESS_METHOD);
				Method method = ReflectionUtils.findMethod(clazz, PROCESS_METHOD, paramTypes);

				// 2--4 反射调用方法
				if (method != null) {
					result = (String) ReflectionUtils.invokeMethod(method, beanObj, objects);
				}
			} else {
				/* Java方式 */
				// 2--2 通过反射获取对象
				clazz = Class.forName(SERVER_HANDLER_CLASS);
				Object impl = clazz.newInstance();

				// 2--3 获取对象的方法
				Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
				Method m = clazz.getMethod(PROCESS_METHOD, parameters);

				// 2--4 反射调用方法
				result = (String) m.invoke(clazz.newInstance(),objects);
			}
		} catch (ClassNotFoundException|
			IllegalAccessException|
			InstantiationException|
			NoSuchMethodException|
			InvocationTargetException e) {
			_logger.error("【地形分析服务-断面分析】URL Request failed,org=[{}],format=[{}],alias=[{}],version=[{}],geo=[{}],points=[{}],srs=[{}]," +
					"drawPic=[{}],picWidth=[{}],picHeight=[{}],dResolutionH=[{}],dResolutionW=[{}],pointNum=[{}],auxParams=[{}]."
					+ e.getMessage(), org, format, alias, version, geo, points, srs,
				drawPic, picWidth, picHeight, dResolutionH, dResolutionW, pointNum, auxParams);
			String errStr = "【地形分析服务-断面分析】URL Request failed." + e.getMessage();
			return buildTopanalyzeOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildTopanalyzeOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	/**
	 * 根据状态码、消息内容构建地形分析服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @return		返回值
	 */
	private static TopanalyzeOutputData buildTopanalyzeOutputData(int code, String msg) {
		TopanalyzeOutputData topanalyzeOutputData = new TopanalyzeOutputData();
		topanalyzeOutputData.setCode(code);
		topanalyzeOutputData.setMsg(msg);
		return topanalyzeOutputData;
	}


	/**
	 * 根据状态码、消息内容、数据构建地形分析服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @param data	数据内容
	 * @return		返回值
	 */
	private static TopanalyzeOutputData buildTopanalyzeOutputData(int code, String msg, String data) {
		TopanalyzeOutputData topanalyzeOutputData = new TopanalyzeOutputData();
		topanalyzeOutputData.setCode(code);
		topanalyzeOutputData.setMsg(msg);
		topanalyzeOutputData.setResultBody(data);
		return topanalyzeOutputData;
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
