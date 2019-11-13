package com.nci.gis.controller.commonlyused;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能服务-通用适配服务REST
 *
 * @since 1.0.0 2019年11月06日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "适配服务通用API",
	value = "CommonlyUsedSvcController", description = "多方服务动态适配通用接口")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class CommonlyUsedSvcController {

	private static final Logger _logger = LoggerFactory.getLogger(CommonlyUsedSvcController.class);

	private final CommonlyUsedSvcService commonlyUsedSvcService;

	public CommonlyUsedSvcController(CommonlyUsedSvcService commonlyUsedSvcService) {
		this.commonlyUsedSvcService = commonlyUsedSvcService;
	}

	@ApiOperation(value = "执行服务", notes = "获取服务对应的信息。")
	@RequestMapping(value = "/cus/{svctype}/{org}/**", method = RequestMethod.GET)
	public Object execute(

		@ApiParam(name = "svctype", required = true,
			allowableValues = "gsplacename,gsnetworkanalyze,gsgeometry," +
				"gstopanalyze,gsmaptile,gsmap" +
				"wms,wmts",
			value = "服务类型。")
		@PathVariable String svctype,

		@ApiParam(name = "org", required = true, allowableValues = "cetc15,sm,ev,mc,nav,gt",
			value = "服务提供机构标识。<br />" +
				"15所：cetc15<br />" +
				"超图:sm<br />" +
				"国遥新天地:ev<br />" +
				"星球时空:mc<br />" +
				"四维图新:nav<br />" +
				"庚图:gt<br />")
		@PathVariable String org,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request,
		HttpServletResponse response) {

		_logger.info("current params: svctype={}, org={},auxParams={}", svctype, org, auxParams);
		return commonlyUsedSvcService.exec(org, svctype, auxParams, request, response);
	}


}
