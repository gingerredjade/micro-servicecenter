package com.nci.gis.controller.data.geometry;

import com.nci.common.AppParams;
import com.nci.constants.*;
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
 * 服务类-几何计算服务
 *
 * @since 1.0.0 2019年10月23日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Service
public class GeometryService {

	private static final Logger _logger = LoggerFactory.getLogger(GeometryService.class);

	// 约定的数据处理方法
	private static final String PROCESS_METHOD = DATA_PROCESS_METHOD.value;
	private static final String SERVICE_MAPPING_GEOMETRY_LENGTH = "length";
	private static final String SERVICE_MAPPING_GEOMETRY_AREA = "area";
	private static final String SERVICE_MAPPING_GEOMETRY_AZIMUTH = "azimuth";
	private static final String SERVICE_MAPPING_GEOMETRY_BUFFER = "buffer";
	private static final String SERVICE_MAPPING_GEOMETRY_SRSCONVERSION = "srsConversion";

	// 注入自定义配置，gisAppServiceConfig.toString()可获取到所有内容
	private final GisAppServiceConfig gisAppServiceConfig;

	@Autowired
	public GeometryService(GisAppServiceConfig gisAppServiceConfig) {
		this.gisAppServiceConfig = gisAppServiceConfig;
	}

	public GeometryOutputData execLength(
		String org,
		String format, String points, String srs,
		String auxParams, HttpServletRequest request) {
		/*
		 * 0-- 预备参数准备
		 */
		/*// 0--1 解析请求中的供应商参数
		String supplier = getProviderFlag(srs);
		if (supplier.isEmpty()) {
			supplier = ProviderDef.PROVIDER_CETC15;
		}

		// 0--2 去掉请求参数中的供应商信息并替换参数值
		srs = getOriginParamValue(srs);*/

		String[] srsArr = new String[1];
		srsArr[0] = srs;
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("srs", srsArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> geometryConfigArr = gisAppServiceConfig.getGsgeometry();
		String[] confArr = getServerConf(geometryConfigArr, org);

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
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_GEOMETRY_LENGTH);

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
			_logger.error("【几何计算服务-距离量算】URL Request failed,org=[{}],format=[{}],points=[{}],srs=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, points, srs, auxParams);
			String errStr = "【几何计算服务-距离量算】URL Request failed." + e.getMessage();
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildGeometryOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public GeometryOutputData execArea(
		String org,
		String format, String geo, String points, String srs, String auxParams,
		HttpServletRequest request) {

		/*
		 * 0-- 预备参数准备
		 */
		/*// 0--1 解析请求中的供应商参数
		String supplier = getProviderFlag(srs);
		if (supplier.isEmpty()) {
			supplier = ProviderDef.PROVIDER_CETC15;
		}

		// 0--2 去掉请求参数中的供应商信息并替换参数值
		srs = getOriginParamValue(srs);*/

		String[] srsArr = new String[1];
		srsArr[0] = srs;
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("srs", srsArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> geometryConfigArr = gisAppServiceConfig.getGsgeometry();
		String[] confArr = getServerConf(geometryConfigArr, org);

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
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_GEOMETRY_AREA);

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
			_logger.error("【几何计算服务-面积量算】URL Request failed,org=[{}],format=[{}],geo=[{}],points=[{}],srs=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, geo, points, srs, auxParams);
			String errStr = "【几何计算服务-面积量算】URL Request failed." + e.getMessage();
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildGeometryOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public GeometryOutputData execAzimuth(
		String org,
		String format, String pointBegin, String pointEnd, String srs, String auxParams,
		HttpServletRequest request) {

		/*
		 * 0-- 预备参数准备
		 */
		/*// 0--1 解析请求中的供应商参数
		String supplier = getProviderFlag(srs);
		if (supplier.isEmpty()) {
			supplier = ProviderDef.PROVIDER_CETC15;
		}

		// 0--2 去掉请求参数中的供应商信息并替换参数值
		srs = getOriginParamValue(srs);*/

		String[] srsArr = new String[1];
		srsArr[0] = srs;
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("srs", srsArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> geometryConfigArr = gisAppServiceConfig.getGsgeometry();
		String[] confArr = getServerConf(geometryConfigArr, org);

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
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_GEOMETRY_AZIMUTH);

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
			_logger.error("【几何计算服务-方位角量算】URL Request failed,org=[{}],format=[{}],pointBegin=[{}],pointEnd=[{}],srs=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, pointBegin, pointEnd, srs, auxParams);
			String errStr = "【几何计算服务-方位角量算】URL Request failed." + e.getMessage();
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildGeometryOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}


	public GeometryOutputData execBuffer(
		String org,
		String format, String distances, String smooth, String geo,
		String points, String srs, String auxParams,
		HttpServletRequest request) {

		/*
		 * 0-- 预备参数准备
		 */
		/*// 0--1 解析请求中的供应商参数
		String supplier = getProviderFlag(srs);
		if (supplier.isEmpty()) {
			supplier = ProviderDef.PROVIDER_CETC15;
		}

		// 0--2 去掉请求参数中的供应商信息并替换参数值
		srs = getOriginParamValue(srs);*/

		String[] srsArr = new String[1];
		srsArr[0] = srs;
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("srs", srsArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> geometryConfigArr = gisAppServiceConfig.getGsgeometry();
		String[] confArr = getServerConf(geometryConfigArr, org);

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
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_GEOMETRY_BUFFER);

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
			_logger.error("【几何计算服务-缓冲区分析】URL Request failed,org=[{}],format=[{}],distances=[{}],smooth=[{}],geo=[{}],points=[{}],srs=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, distances, smooth, geo, points, srs, auxParams);
			String errStr = "【几何计算服务-缓冲区分析】URL Request failed." + e.getMessage();
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildGeometryOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	public GeometryOutputData execSrsConversion(
		String org,
		String format, String geo, String points,
		String srs, String toSrs, String auxParams,
		HttpServletRequest request) {

		/*
		 * 0-- 预备参数准备
		 */
		/*// 0--1 解析请求中的供应商参数
		String supplier = getProviderFlag(srs);
		if (supplier.isEmpty()) {
			supplier = ProviderDef.PROVIDER_CETC15;
		}

		// 0--2 去掉请求参数中的供应商信息并替换参数值
		srs = getOriginParamValue(srs);*/

		String[] srsArr = new String[1];
		srsArr[0] = srs;
		Map<String, String[]> mapParams = request.getParameterMap();
		try {
			Method method = mapParams.getClass().getMethod("setLocked", boolean.class);
			method.invoke(mapParams, Boolean.FALSE);
			mapParams.put("srs", srsArr);
			method.invoke(mapParams, Boolean.TRUE);
		} catch (NoSuchMethodException|
			IllegalAccessException|
			InvocationTargetException e) {
			_logger.error(e.getMessage());
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, e.getMessage());
		}

		// 0--3 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> geometryConfigArr = gisAppServiceConfig.getGsgeometry();
		String[] confArr = getServerConf(geometryConfigArr, org);

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
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, SERVICE_MAPPING_GEOMETRY_SRSCONVERSION);

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
			_logger.error("【几何计算服务-空间参考转换】URL Request failed,org=[{}],format=[{}],geo=[{}],points=[{}],srs=[{}],toSrs=[{}],auxParams=[{}]."
				+ e.getMessage(), org, format, geo, points, srs, toSrs, auxParams);
			String errStr = "【几何计算服务-空间参考转换】URL Request failed." + e.getMessage();
			return buildGeometryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildGeometryOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	/**
	 * 根据状态码、消息内容构建几何计算服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @return		返回值
	 */
	private static GeometryOutputData buildGeometryOutputData(int code, String msg) {
		GeometryOutputData geometryOutputData = new GeometryOutputData();
		geometryOutputData.setCode(code);
		geometryOutputData.setMsg(msg);
		return geometryOutputData;
	}


	/**
	 * 根据状态码、消息内容、数据构建几何计算服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @param data	数据内容
	 * @return		返回值c
	 */
	private static GeometryOutputData buildGeometryOutputData(int code, String msg, String data) {
		GeometryOutputData geometryOutputData = new GeometryOutputData();
		geometryOutputData.setCode(code);
		geometryOutputData.setMsg(msg);
		geometryOutputData.setResultBody(data);
		return geometryOutputData;
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
