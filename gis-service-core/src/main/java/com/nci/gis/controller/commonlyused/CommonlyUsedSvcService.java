package com.nci.gis.controller.commonlyused;

import com.nci.common.AppParams;
import com.nci.constants.*;
import com.nci.constants.ServiceTypes.*;
import com.nci.gis.conf.GisAppServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nci.gis.common.ClassUtils.getParameterClass;

/**
 * 服务类-通用适配服务
 *
 * @since 1.0.0 2019年11月06日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Service
public class CommonlyUsedSvcService {

	private static final Logger _logger = LoggerFactory.getLogger(CommonlyUsedSvcService.class);

	// 约定的数据处理方法
	private static final String PROCESS_METHOD = DATA_PROCESS_METHOD.value;

	// 注入自定义配置，gisAppServiceConfig.toString()可获取到所有内容
	private final GisAppServiceConfig gisAppServiceConfig;

	public CommonlyUsedSvcService(GisAppServiceConfig gisAppServiceConfig) {
		this.gisAppServiceConfig = gisAppServiceConfig;
	}

	public Object exec (String org, String svctype, String auxParams,
						HttpServletRequest request,
						HttpServletResponse response) {

		/*
		 * 0-- 监测配置参数
		 * 		org、 svctype
		 */
		if (org.isEmpty()) {
			String errStr = "org cannot be null";
			_logger.error(errStr);
			return buildErrorOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else if (svctype.isEmpty()) {
			String errStr = "svctype cannot be null";
			_logger.error(errStr);
			return buildErrorOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}

		/*
		 * 1-- 获取配置参数信息
		 * 		server-prefix、
		 * 		server-handler-class
		 */
		List<Map<String, String>> svcConfigArr = getConfigInfo(svctype);
		String[] confArr = getServerConf(svcConfigArr, org);
		String SERVER_PREFIX = confArr[0];

		Map<String, String> cusConfMap = gisAppServiceConfig.getCus();
		String SERVER_HANDLER_CLASS = cusConfMap.get(GisAppConfKey.SERVER_HANDLER_CLASS_KEY);

		if (SERVER_PREFIX.isEmpty()) {
			String errStr = "GIS Web服务器服务请求串前缀为空，请检查server-prefix配置";
			_logger.error(errStr);
			return buildErrorOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		} else {
			if (SERVER_PREFIX.endsWith("/")) {
				int indexFlag = SERVER_PREFIX.lastIndexOf("/");
				SERVER_PREFIX = SERVER_PREFIX.substring(0, indexFlag);
			}
		}

		if (SERVER_HANDLER_CLASS.isEmpty()) {
			String errStr = "服务实现类未指定，请检查cus.server-handler-class配置";
			_logger.error(errStr);
			return buildErrorOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}


		/*
		 * 2-- 根据配置的应用服务提供前缀、服务实现接口
		 * 		通过反射机制获取请求结果
		 */
		byte[] result;

		// 2--1 准备参数
		AppParams appParams = new AppParams();
		appParams.put(SERVICE_WEBSERVER_PREFIX.value, SERVER_PREFIX);

		Class<?> clazz;

		// 2--2 通过反射获取对象
		try {
			clazz = Class.forName(SERVER_HANDLER_CLASS);
			Object impl = clazz.newInstance();

			// 2--3 获取对象的方法
			Class<?>[] parameters = getParameterClass(impl, PROCESS_METHOD);
			Method m = clazz.getMethod(PROCESS_METHOD, parameters);

			// 2--4 反射调用方法
			Object[] objects = new Object[4];
			objects[0] = request;
			objects[1] = response;
			objects[2] = appParams;
			objects[3] = null;

			result = (byte[]) m.invoke(clazz.newInstance(),objects);

			/*
			 * 3-- 封装应答
			 * 		根据请求的Content-Type(MediaType)，为HTTP响应传输对象打数据格式标签
			 */
			java.io.OutputStream os = response.getOutputStream();
			os.write(result);
		} catch (ClassNotFoundException
			| IllegalAccessException
			| InstantiationException
			| NoSuchMethodException
			| InvocationTargetException
			| IOException e) {
			_logger.error("URL Request failed,org=[{}],svctype=[{}],auxParams=[{}],err info is {}.",
				org, svctype, auxParams, e.getMessage());
			String errStr = "URL Request failed." + e.getMessage();
			return buildErrorOutputData(StateCodes.STATE_CODE_FAILED, errStr);
		}
		return null;
	}

	/**
	 * 根据状态码、消息内容构建网络分析服务输出数据结果
	 *
	 * @param code	状态码
	 * @param msg	消息内容
	 * @return		返回值
	 */
	private static Object buildErrorOutputData(int code, String msg) {
		CommonlyUsedSvcOutputData commonlyUsedSvcOutputData = new CommonlyUsedSvcOutputData();
		commonlyUsedSvcOutputData.setCode(code);
		commonlyUsedSvcOutputData.setMsg(msg);
		return commonlyUsedSvcOutputData;
	}

	/**
	 * 根据服务类型获取其包含的所有配置项信息
	 *
	 * @param svctype 服务类型如gsmaptile等
	 * @return 返回值
	 */
	private List<Map<String, String>> getConfigInfo(String svctype) {
		List<Map<String, String>> svcConfigArr = new ArrayList<>();
		if (svctype.equalsIgnoreCase(GEOMETRY_SERVICE.value)) {
			svcConfigArr = gisAppServiceConfig.getGsgeometry();
		} else if (svctype.equalsIgnoreCase(PLACENAME_SERVICE.value)) {
			svcConfigArr = gisAppServiceConfig.getGsplacename();
		} else if (svctype.equalsIgnoreCase(NETWORKANALYSE_SERVICE.value)) {
			svcConfigArr = gisAppServiceConfig.getGsnetworkanalyze();
		} else if (svctype.equalsIgnoreCase(MAP_SERVICE.value)) {
			svcConfigArr = gisAppServiceConfig.getGsmap();
		} else if (svctype.equalsIgnoreCase(MAPTILE_SERVICE.value)) {
			svcConfigArr = gisAppServiceConfig.getGsmaptile();
		} else if (svctype.equalsIgnoreCase(OGC_SERVICE_WMS.value)) {
			svcConfigArr = gisAppServiceConfig.getWms();
		} else if (svctype.equalsIgnoreCase(OGC_SERVICE_WMTS.value)) {
			svcConfigArr = gisAppServiceConfig.getWmts();
		} else if (svctype.equalsIgnoreCase(TOPOGANALYSE_SERVICE.value)) {
			svcConfigArr = gisAppServiceConfig.getGstopanalyze();
		}
		return svcConfigArr;
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

}
