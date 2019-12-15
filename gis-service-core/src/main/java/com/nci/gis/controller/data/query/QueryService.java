package com.nci.gis.controller.data.query;

import com.nci.common.AppParams;
import com.nci.constants.*;
import com.nci.constants.ServiceTypes.QUERY_SERVICE;
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
 * 服务类-空间查询服务
 *
 * @since 1.0.0 2019年12月04日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Service
public class QueryService {

	private static final Logger _logger = LoggerFactory.getLogger(QueryService.class);

	// 约定的数据处理方法
	private static final String PROCESS_METHOD = DATA_PROCESS_METHOD.value;

	// 注入自定义配置，gisAppServiceConfig.toString()可获取到所有内容
	private final GisAppServiceConfig gisAppServiceConfig;

	@Autowired
	public QueryService(GisAppServiceConfig gisAppServiceConfig) {
		this.gisAppServiceConfig = gisAppServiceConfig;
	}

	public QueryOutputData execQuery(
		String org, String format, String alias, String version,

		// 空间查询条件参数
		String geo, String points, String srs, String tolarence, String relation, String feature,

		// 结果定制参数
		String number,

		// 属性查询条件参数
		String search,

		HttpServletRequest request) {

		// 0-- 遍历配置获取服务供应商对应的GIS服务器信息
		List<Map<String, String>> queryConfigArr = gisAppServiceConfig.getGsquery();
		String[] confArr = getServerConf(queryConfigArr, org);

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
			return buildQueryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查server-handler-class配置";
			_logger.error(errStr);
			return buildQueryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		String result = "";

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);
		appParams.put(SERVICE_MAPPING_IDENTITY.value, QUERY_SERVICE.value);

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
			_logger.error("【空间查询服务】URL Request failed,org=[{}],format=[{}],alias=[{}]," +
				"version=[{}],geo=[{}],points=[{}],srs=[{}],tolarence=[{}],relation=[{}]," +
				"feature=[{}],number=[{}],search=[{}]."
				+ e.getMessage(), org, format, alias, version, geo, points, srs, tolarence,
				relation, feature, number, search);
			String errStr = "【空间查询服务】URL Request failed." + e.getMessage();
			return buildQueryOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 3-- 封装应答
		 */
		return buildQueryOutputData(
			StateCodes.STATE_CODE_SUCCESS, StateCodes.STATE_STR_SUCCESS, result);
	}

	/**
	 * 根据状态码、消息内容构建空间查询服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @return		返回值
	 */
	private static QueryOutputData buildQueryOutputData(int code, String msg) {
		QueryOutputData queryOutputData = new QueryOutputData();
		queryOutputData.setCode(code);
		queryOutputData.setMsg(msg);
		return queryOutputData;
	}


	/**
	 * 根据状态码、消息内容、数据构建空间查询服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @param data	数据内容
	 * @return		返回值
	 */
	private static QueryOutputData buildQueryOutputData(int code, String msg, String data) {
		QueryOutputData queryOutputData = new QueryOutputData();
		queryOutputData.setCode(code);
		queryOutputData.setMsg(msg);
		queryOutputData.setResultBody(data);
		return queryOutputData;
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

		for (Map<String, String> config : list) {
			for (Map.Entry<String, String> entry : config.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key.equalsIgnoreCase("author") &&
					value.equalsIgnoreCase(supplier)){

					serverPrefix = config.get(GisAppConfKey.SERVER_PREFIX_KEY);
					serverHandlerCls = config.get(GisAppConfKey.SERVER_HANDLER_CLASS_KEY);

					serverConf[0] = serverPrefix;
					serverConf[1] = serverHandlerCls;
				}
			}
		}
		return serverConf;
	}

}
