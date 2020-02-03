package com.micro.controller.ogc.ows.wmts;

import com.micro.constants.ServiceTypes.OGC_SERVICE_WMTS;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OGC规范-网络地图瓦片服务（WMTS-Web Map Tile Service） REST
 *
 * @since 1.0.0 2019年09月24日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
@Api(tags = "网络地图瓦片服务WMTS",
	value = "WMTSController", description = "该服务遵循OGC规范，用于获取地图瓦片信息")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class WMTSController {

	private static final Logger _logger = LoggerFactory.getLogger(WMTSController.class);

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_WMTS = OGC_SERVICE_WMTS.value;

	private final WMTSService wmtsService;

	@Autowired
	public WMTSController(WMTSService wmtsService) {
		this.wmtsService = wmtsService;
	}

	@ApiOperation(value = "执行WMTS操作",
		notes = "WMTS服务，该服务遵循OGC规范，用于获取地图瓦片信息。<br />" +
			"该功能操作遵循OGC的WMTS规范，提供2个功能操作，即GetCapbilities操作、GetTile操作。<br /><br />" +
			"注意：<br />" +
			"GetCapabilities:用于获取地图瓦片服务的元数据信息，该功能操作遵循OGC的WMTS规范，请求的封装方式为KVP，应答为XML文件。<br />" +
			"GetTile:用于根据地图瓦片URI，获取该地图瓦片URI对应的地图瓦片数据，该功能操作遵循OGC的WMTS规范，请求的封装方式为KVP。<br /><br />" +
			"当request=GetCapabilities时，输出XML格式数据，传递必填参数service、request、version即可。<br />" +
			"当request=GetTile时，直接输出图片，需传递的参数为：service、request、version、layer、style、format、tilematrixset、tilematrix、tilerow、tilecol。")
	@RequestMapping(value = {SERVICE_MAPPING_WMTS+"/{org}/**"}, method = RequestMethod.GET)
	@ResponseBody
	public String execute(

		@ApiParam(name = "org", required = true, allowableValues = "cetc15,sm,ev,mc,nav,gt",
			value = "服务提供机构标识。<br />" +
				"15所：cetc15<br />" +
				"超图:sm<br />" +
				"国遥新天地:ev<br />" +
				"星球时空:mc<br />" +
				"四维图新:nav<br />" +
				"庚图:gt<br />")
		@PathVariable String org,

		@ApiParam(name = "service", required = true, allowableValues = "wmts",
			value = "请求服务，固定值wmts。")
		@RequestParam(value = "service", defaultValue = "wmts") String service,

		@ApiParam(name = "request", required = true, allowableValues = "GetCapabilities,GetTile",
			value = "请求名称，固定值GetCapabilities或GetTile。")
		@RequestParam(value = "request", defaultValue = "GetCapabilities") String request,

		@ApiParam(name = "version", required = true,
			value = "服务版本，如1.0.0。")
		@RequestParam(value = "version", defaultValue = "") String version,

		@ApiParam(name = "format", allowableValues = "image/jpg,image/png",
			value = "request=GetTile时必填。<br />应答格式，请求的瓦片格式。")
		@RequestParam(value = "format", defaultValue = "") String format,

		@ApiParam(name = "layer",
			value = "request=GetTile时必填。<br />" +
				"地图瓦片图层标识，用于唯一标识一个地图瓦片图层，取值来源于GetCapabilities里的元数据信息。")
		@RequestParam(value = "layer", defaultValue = "") String layer,

		@ApiParam(name = "style",
			value = "request=GetTile时必填。<br />" +
				"瓦片图层显示风格，默认请填default。")
		@RequestParam(value = "style", defaultValue = "") String style,

		@ApiParam(name = "tilematrixset",
			value = "request=GetTile时必填。<br />" +
				"瓦片矩阵集，用于标识瓦片图层所使用的分块方式，可选值包括3857，112015，4978，4490，4214，4610。<br />" +
				"其中3857对应空间参考EPSG:3857即墨卡托_WGS84_0_0空间参考；<br />" +
				"112015对应空间参考MGIS:112015，即墨卡托_CGCS2000_0_0空间参考；<br />" +
				"4978对应空间参考MGIS:4978，即WGS84空间参考；<br />" +
				"4490对应空间参考MGIS:4490，即CGCS2000空间参考；<br />" +
				"4214对应空间参考MGIS:4214级北京54空间参考；<br />" +
				"4610对应空间参考MGIS:4610即西安80空间参考。")
		@RequestParam(value = "tilematrixset", defaultValue = "") String tilematrixset,

		@ApiParam(name = "tilematrix",
			value = "request=GetTile时必填。<br />瓦片图层显示级别，整型。")
		@RequestParam(value = "tilematrix", defaultValue = "") String tilematrix,

		@ApiParam(name = "tilerow",
			value = "request=GetTile时必填。<br />瓦片所属行号，整型。")
		@RequestParam(value = "tilerow", defaultValue = "") String tilerow,

		@ApiParam(name = "tilecol",
			value = "request=GetTile时必填。<br />瓦片所属列号，整型。")
		@RequestParam(value = "tilecol", defaultValue = "") String tilecol,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,


		HttpServletRequest req,
		HttpServletResponse response) {

		return wmtsService.execWmts(org, service, request, version, format, layer,
			style, tilematrixset, tilematrix, tilerow, tilecol, auxParams,
			req, response);
	}

}
