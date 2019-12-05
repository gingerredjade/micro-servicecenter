package com.nci.gis.controller.catalog;

import com.nci.constants.ServiceTypes.CATALOG_SERVICE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能服务-服务目录服务REST
 *
 * @since 1.0.0 2019年11月22日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "服务目录服务",
	value = "CatalogController", description = "获取各类地理信息数据服务以及各类地理信息功能服务的目录信息")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class CatalogController {

	private static final Logger _logger = LoggerFactory.getLogger(CatalogController.class);

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_CATALOG = CATALOG_SERVICE.value;

	private final CatalogService catalogService;

	@Autowired
	public CatalogController(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@ApiOperation(value = "执行服务目录服务", notes = "获取服务的目录信息。")
	@RequestMapping(value = {SERVICE_MAPPING_CATALOG+"/service/{org}"}, method = RequestMethod.GET)
	public CatalogOuputData execute(

		@ApiParam(name = "org", required = true, allowableValues = "cetc15,sm,ev,mc,nav,gt",
			value = "服务提供机构标识。<br />" +
				"15所：cetc15<br />" +
				"超图:sm<br />" +
				"国遥新天地:ev<br />" +
				"星球时空:mc<br />" +
				"四维图新:nav<br />" +
				"庚图:gt<br />")
		@PathVariable String org,

		@ApiParam(name = "format",
			value = "应答数据编码。")
		@RequestParam(value = "format", defaultValue = "json") String format,

		@ApiParam(name = "type",
			value = "地理信息服务类型。字符串型。")
		@RequestParam(value = "type", defaultValue = "") String type,

		@ApiParam(name = "alias",
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version",
			value = "服务版本。<br />整型，例如1000,1001。")
		@RequestParam(value = "version", defaultValue = "") String version,

		@ApiParam(name = "number",
			value = "应答数据个数。整型，表示接收响应结果的个数。")
		@RequestParam(value = "number", defaultValue = "") String number,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {
		return catalogService.execCatalog(org, format, type, alias, version, number, auxParams, request);
	}


}
