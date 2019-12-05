package com.nci.gis.controller.metadata;

import com.nci.constants.ServiceTypes.METADATA_SERVICE;
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
 * 功能服务-元数据服务REST
 *
 * @since 1.0.0 2019年11月22日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "元数据服务",
	value = "MetadataController", description = "获取元数据信息")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class MetadataController {

	private static final Logger _logger = LoggerFactory.getLogger(MetadataController.class);

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_METADATA = METADATA_SERVICE.value;

	private final MetadataService metadataService;

	@Autowired
	public MetadataController(MetadataService metadataService) {
		this.metadataService = metadataService;
	}

	@ApiOperation(value = "执行元数据服务", notes = "获取元数据服务信息。")
	@RequestMapping(value = {SERVICE_MAPPING_METADATA+"/{org}"}, method = RequestMethod.GET)
	public MetadataOutputData execute(

		@ApiParam(name = "org", required = true, allowableValues = "cetc15,sm,ev,mc,nav,gt",
			value = "服务提供机构标识。<br />" +
				"15所：cetc15<br />" +
				"超图:sm<br />" +
				"国遥新天地:ev<br />" +
				"星球时空:mc<br />" +
				"四维图新:nav<br />" +
				"庚图:gt<br />")
		@PathVariable String org,

		@ApiParam(name = "format", required = true, allowableValues = "json",
			value = "应答数据编码。")
		@RequestParam(value = "format", defaultValue = "json") String format,

		@ApiParam(name = "alias",
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version",
			value = "服务版本。<br />整型，例如1000，1001。")
		@RequestParam(value = "version", defaultValue = "") String version,

		@ApiParam(name = "type",
			value = "地理信息服务类型。字符串型。")
		@RequestParam(value = "type", defaultValue = "") String type,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

		return metadataService.execMD(org, format, alias, version, type, auxParams, request);
	}

}
