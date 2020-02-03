package com.micro.controller.test;

import com.alibaba.fastjson.JSONObject;
import com.micro.entity.RespondMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

/**
 * 测试REST
 *
 * Created by JHy on 2019/8/12.
 */
@Api(value = "BingyaoController",description = "测试用。")
//@RestController
@EnableAutoConfiguration
@CrossOrigin
public class TestController {

	private static final Logger _logger = LoggerFactory.getLogger(TestController.class);


	/**
	 * REST请求测试接口
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "helloworld",method = RequestMethod.GET)
	public String helloworld(
		@ApiParam(name = "startPos", required = true, value = "起始点，经度、纬度之间英文格式逗号分隔,如：116.203,40.227或者116.216,40.191")
		@RequestParam(value = "startPos", defaultValue = "") String startPos,

		@ApiParam(name = "endPos", required = true, value = "终止点，经度、纬度之间英文格式逗号分隔,如：116.61,39.906或者116.388,40.06")
		@RequestParam(value = "endPos", defaultValue = "") String endPos,

		HttpServletRequest req){

		Map<String, String[]> params = req.getParameterMap();
		Iterator it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = entry.getKey().toString();
			String val = entry.getValue().toString();
			_logger.info("[{}]=[{}].", key, val);
		}

		String ip = req.getRemoteAddr();
		RespondMessage resultMessage = new RespondMessage();
		resultMessage.setMessage("hello:"+ip);
		resultMessage.setStatus("success");
		String result = JSONObject.toJSONString(resultMessage);
		return result;
	}


	@ApiOperation(value = "执行", notes = "首先，设置起点、终点，执行")
	@PostMapping("/gsbingyao")
	public String execute(
		@RequestBody TestInputData inputData,
		@RequestParam(defaultValue = "true") boolean async,
		HttpServletRequest request,
		HttpServletResponse response) throws IntrospectionException, InvocationTargetException, IllegalAccessException {

		/**
		 * 1-- 获取所有参数信息
		 */
		/**
		 * 1--1 获取所有请求参数
		 */
		Map<String, String[]> params = request.getParameterMap();
		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);
		}

		/**
		 * 1--2 遍历一个对象（RequestBody对应的对象）的所有属性及值
		 */
		BeanInfo beanInfo = Introspector.getBeanInfo(inputData.getClass(), Object.class);
		if (beanInfo != null) {
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : propertyDescriptors) {
				// 获得属性名称
				String pName = pd.getName();
				// 调用该名称对应的getter方法
				Object pVal = new PropertyDescriptor(pd.getName(),TestInputData.class).getReadMethod().invoke(inputData);
				String val = pVal.toString();
				//new PropertyDescriptor(pd.getName(),TestInputData.class).getWriteMethod().invoke(inputData,new Object[]{"1"});
			}
		}

		/**
		 * 2-- 构造请求URI
		 */

		/**
		 * 3-- 执行请求
		 */

		/**
		 * 4-- 封装应答
		 */
		return null;
	}

}
