package com.nci.gis.controller.function.maptile;

import com.nci.constants.ServiceTypes.MAPTILE_SERVICE;
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
 * 功能服务-地图瓦片服务资源 REST
 *
 * @since 1.0.0 2019年09月24日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "地图瓦片服务",
	value = "MapTileController", description = "获取网格化的地图瓦片信息")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class MapTileController {

	private static final Logger _logger = LoggerFactory.getLogger(MapTileController.class);

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_MAPTILE = MAPTILE_SERVICE.value;

	private final MapTileService mapTileService;

	@Autowired
	public MapTileController(MapTileService mapTileService) {
		this.mapTileService = mapTileService;
	}

	@ApiOperation(value = "执行地图瓦片服务-Tile资源操作",
		notes = "获取网格化的地图瓦片信息（直接输出图片）。")
	@RequestMapping(value = {SERVICE_MAPPING_MAPTILE+"/{org}"}, method = RequestMethod.GET)
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

		@ApiParam(name = "format", allowableValues = "image",
			value = "应答数据编码，请求的瓦片格式。")
		@RequestParam(value = "format", defaultValue = "image") String format,

		@ApiParam(name = "alias", required = true,
			value = "服务实例别名，字符串型，用于表示请求的服务实例别名，如”BJ”。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务实例版本，整型，用于表示请求的服务实例版本，如1000。")
		@RequestParam(value = "version", defaultValue = "") String version,

		@ApiParam(name = "namespace", required = true,
			value = "瓦片图层命名空间，字符串型。")
		@RequestParam(value = "namespace", defaultValue = "") String namespace,

		@ApiParam(name = "layerAlias", required = true,
			value = "瓦片图层别名，字符串型。")
		@RequestParam(value = "layerAlias", defaultValue = "") String layerAlias,

		@ApiParam(name = "level", required = true,
			value = "金字塔级别，整型。")
		@RequestParam(value = "level", defaultValue = "") String level,

		@ApiParam(name = "row", required = true,
			value = "瓦片所属行号，整型。")
		@RequestParam(value = "row", defaultValue = "") String row,

		@ApiParam(name = "col", required = true,
			value = "瓦片所属列号，整型。")
		@RequestParam(value = "col", defaultValue = "") String col,

		@ApiParam(name = "imageType", required = true, allowableValues = "png,jpg,bmp",
			value = "应答图片的格式，字符串型，可取值包括:png、jpg、bmp。")
		@RequestParam(value = "imageType", defaultValue = "") String imageType,

		@ApiParam(name = "dataType", allowableValues = "1,2,3", required = true,
			value = "瓦片图层数据格式类型，整型，可取值包括1,2,3。<br />" +
				"1-数据面片<br /> " +
				"2-矢量显示瓦片<br /> " +
				"3-地图瓦片<br />" +
				"注：DLG、DEM数据该值设置为3；影像数据该值设置为1；")
		@RequestParam(value = "dataType", defaultValue = "") String dataType,

		@ApiParam(name = "dataFormat", allowableValues = "1,2", required = true,
			value = "瓦片图层格式，整型，可取值包括1,2。<br />" +
				"1表示jpg或者jpeg<br /> " +
				"2表示png<br />")
		@RequestParam(value = "dataFormat", defaultValue = "") String dataFormat,

		@ApiParam(name = "appType", allowableValues = "1,2", required = true,
			value = "应用类型，整型。可取值包括：1表示pc；2表示phone。")
		@RequestParam(value = "appType", defaultValue = "") String appType,

		@ApiParam(name = "dataVersion", required = false,
			value = "数据版本，字符串型，new表示最新版本数据。<br />" +
				"提供商为cetc15时需填new。<br />" +
				"提供商为mc时填写对应版本如v1.0，无版本则不填。")
		@RequestParam(value = "dataVersion", defaultValue = "") String dataVersion,

		@ApiParam(name = "datascale",
			value = "数据比例尺，整型，当瓦片数据对应的的原始数据为矢量数据时可设置该参数。")
		@RequestParam(value = "datascale", defaultValue = "") String datascale,

		@ApiParam(name = "dataresolution",
			value = "数据分辨率，浮点数，当瓦片数据对应的的原始数据为栅格数据时可设置该参数。")
		@RequestParam(value = "dataresolution", defaultValue = "") String dataresolution,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,


		HttpServletRequest req,
		HttpServletResponse response) {

		return mapTileService.execMapTile(org, format, alias, version, namespace, layerAlias,
			level, row, col, imageType, dataType, dataFormat, appType, dataVersion,
			datascale, dataresolution, auxParams,
			req, response);
	}

}
