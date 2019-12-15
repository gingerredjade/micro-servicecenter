package com.nci.gis.controller.data.bingyao;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 数据服务-综合兵要服务REST
 *
 * Created by JHy on 2019/8/12.
 */
@Api(value = "BYController",description = "兵要数据服务相关的API，主要用于根据服务请求条件，获取对应的综合兵要信息。")
//@RestController
@EnableAutoConfiguration
//@RequestMapping("gsbingyao")
@CrossOrigin
public class BYController {

	private static final Logger _logger = LoggerFactory.getLogger(BYController.class);


	/**
	 *
	 * @param startPos	起始点
	 * @param endPos	终止点
	 * @param request	请求参数
	 * @param async		是否异步
	 * @return			返回值
	 */
	@ApiOperation(value = "执行兵要数据服务", notes = "首先，设置导航起点、导航终点、必经点（多个、可空）、避让点（多个、可空），执行导航规划")
	@RequestMapping(value = "gsbingyao",method = RequestMethod.GET)
	public String executeBYSearch(
		@ApiParam(name = "startPos", required = true, value = "起始点，经度、纬度之间英文格式逗号分隔")
		@RequestParam(value = "startPos", defaultValue = "") String startPos,

		@ApiParam(name = "endPos", required = true, value = "")
		@RequestParam(value = "endPos", defaultValue = "") String endPos,

		HttpServletRequest request,

		@RequestParam(defaultValue = "true") boolean async){


		/*
		 * 1-- 获取所有请求参数
		 */
		Map<String, String[]> params = request.getParameterMap();

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);
		}



		/*
		 * 2-- 构造请求URI
		 */
		// 拼接所有不空的参数


		/*
		 * 3-- 执行请求
		 */


		/*
		 * 4-- 封装应答
		 */



		return null;
	}



}
