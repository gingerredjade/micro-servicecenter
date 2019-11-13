package com.nci.gis.controller.map;

import com.nci.constants.ServiceTypes.MAP_SERVICE;
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
 * 功能服务-地图显示服务REST
 *
 * @since 1.0.0 2019年09月18日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "地图显示服务", value = "MapController",
	description = "根据用户指定的地图、区域、投影方式、图片大小等请求条件，返回相应的地图资源")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class MapController {

	private static final Logger _logger = LoggerFactory.getLogger(MapController.class);

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_MAP = MAP_SERVICE.value;

	final
	private MapService mapService;

	@Autowired
	public MapController(MapService mapService) {
		this.mapService = mapService;
	}

	@ApiOperation(value = "执行地图显示服务",
		notes = "根据用户指定的地图、区域、投影方式、图片大小等请求条件，返回相应的地图资源（直接输出图片）。")
	@RequestMapping(value = {SERVICE_MAPPING_MAP+"/{org}"}, method = RequestMethod.GET)
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

		@ApiParam(name = "format", required = true, allowableValues = "image",
			value = "应答数据编码。")
		@RequestParam(value = "format", defaultValue = "image") String format,

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "bound", required = true,
			value = "请求的地理区域范围。<br />" +
				"bound=&lt;top&gt;,&lt;left&gt;,&lt;bottom&gt;,&lt;right&gt;,&lt;spatialReference&gt;。  <br />" +
				"&lt;top&gt;为矩形坐标的上边界，双精度浮点型；<br />" +
				"&lt;left&gt;为矩形坐标的左边界，双精度浮点型；<br />" +
				"&lt;bottom&gt;为矩形坐标的下边界，双精度浮点型；<br />" +
				"&lt;right&gt;为矩形坐标的右边界，双精度浮点型；<br />" +
				"&lt;spatialReference&gt;空间参考编码，字符串型。")
		@RequestParam(value = "bound", defaultValue = "") String bound,

		@ApiParam(name = "layers", required = true,
			value = "请求的图层别名集合。如：layers=&lt;layer&gt;,&lt;layer&gt;,&lt;layer&gt;,…。")
		@RequestParam(value = "layers", defaultValue = "") String layers,

		@ApiParam(name = "width",
			value = "返回的地图图片宽（像素个数），整型，默认256。")
		@RequestParam(value = "width", defaultValue = "") String width,

		@ApiParam(name = "height",
			value = "返回的地图图片高（像素个数），整型，默认256。")
		@RequestParam(value = "height", defaultValue = "") String height,

		@ApiParam(name = "imageType", allowableValues = "0,1,2,4",
			value = "地图图片类型，整型，默认PNG，默认即可。<br />" +
				"0（未知图片类型）<br />" +
				"1（PNG）<br />" +
				"2（JPG/JPEG）<br />" +
				"4（BMP）。")
		@RequestParam(value = "imageType", defaultValue = "1") String imageType,

		@ApiParam(name = "transparent", allowableValues = "true,false",
			value = "背景是否透明，默认为false,即不透明，布尔型。")
		@RequestParam(value = "transparent", defaultValue = "false") String transparent,

		@ApiParam(name = "bgcolor",
			value = "返回的地图图片资源背景色，该值由RGB组成。默认值为0xFFFFFF即白色，整型。")
		@RequestParam(value = "bgcolor", defaultValue = "16777215") String bgcolor,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request,
		HttpServletResponse response) {

		return mapService.execMap(org, format, alias, version, bound, layers, width, height,
			imageType, transparent, bgcolor, auxParams, request, response);
	}

}
