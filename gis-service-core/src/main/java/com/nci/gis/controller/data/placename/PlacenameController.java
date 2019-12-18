package com.nci.gis.controller.data.placename;

import com.nci.constants.ServiceTypes.PLACENAME_SERVICE;
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
 * 功能服务-地名检索服务REST（三期模式）
 *
 * @since 1.0.0 2019年09月12日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "地名检索服务",
	value = "PlacenameController", description = "根据检索条件，对地名数据进行检索操作")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class PlacenameController {
	private static final Logger _logger = LoggerFactory.getLogger(PlacenameController.class);

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_PLACENAME = PLACENAME_SERVICE.value;

	private final PlacenameService placenameService;

	@Autowired
	public PlacenameController(PlacenameService placenameService) {
		this.placenameService = placenameService;
	}

	@ApiOperation(value = "执行地名检索服务", notes = "根据检索条件，检索地名数据。")
	@RequestMapping(value = {SERVICE_MAPPING_PLACENAME+"/{org}"}, method = RequestMethod.GET)
	public PlacenameOutputData execute(

		@ApiParam(name = "org", required = true, allowableValues = "cetc15,sm,ev,mc,nav,gt",
			value = "服务提供机构标识。<br />" +
				"15所：cetc15<br />" +
				"超图:sm<br />" +
				"国遥新天地:ev<br />" +
				"星球时空:mc<br />" +
				"四维图新:nav<br />" +
				"庚图:gt<br />")
		@PathVariable String org,

		@ApiParam(name = "format", allowableValues = "json",
			value = "应答数据编码")
		@RequestParam(value = "format", defaultValue = "json") String format,

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型，表示请求的服务实例别名，如“guangdong”")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。整型，表示请求的服务实例版本，如1000")
		@RequestParam(value = "version", defaultValue = "") String version,

		@ApiParam(name = "search", required = true,
			value = "检索条件。字符串型，用于表示检索条件。")
		@RequestParam(value = "search", defaultValue = "") String search,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

		return placenameService.execPlacename(org, format,
			alias, version, search, auxParams, request);
	}

}