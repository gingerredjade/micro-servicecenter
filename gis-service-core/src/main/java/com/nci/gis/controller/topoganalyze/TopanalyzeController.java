package com.nci.gis.controller.topoganalyze;

import com.nci.gis.common.TopServiceMappings;
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
 * 功能服务-地形分析服务REST
 *
 * @since 1.0.0 2019年09月17日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "地形分析服务",
	value = "TopanalyzeController",
	description = "基于高程数据进行地形分析，提供的操作具体包括：" +
		"地表距离量算、地表面积量算、高程量算、挖方填方、两点/区域通视分析、坡度/坡向分析、断面分析等")
@RestController
@EnableAutoConfiguration
@CrossOrigin
@RequestMapping(value = "gstopanalyze")
public class TopanalyzeController {

	private static final Logger _logger = LoggerFactory.getLogger(TopanalyzeController.class);

	// 约定的数据处理方法
	private static final String SERVICE_MAPPING_TOPANALYZE_DISTANCE = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_DISTANCE;
	private static final String SERVICE_MAPPING_TOPANALYZE_AREA = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_AREA;
	private static final String SERVICE_MAPPING_TOPANALYZE_AREAOFRING = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_AREAOFRING;
	private static final String SERVICE_MAPPING_TOPANALYZE_POINTELEVATION = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_POINTELEVATION;
	private static final String SERVICE_MAPPING_TOPANALYZE_ELEVATION = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_ELEVATION;
	private static final String SERVICE_MAPPING_TOPANALYZE_VOLUME = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_VOLUME;
	private static final String SERVICE_MAPPING_TOPANALYZE_P2PSIGHT = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_P2PSIGHT;
	private static final String SERVICE_MAPPING_TOPANALYZE_VIEWSIGHT = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_VIEWSIGHT;
	private static final String SERVICE_MAPPING_TOPANALYZE_SLOPE = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_SLOPE;
	private static final String SERVICE_MAPPING_TOPANALYZE_ASPECT = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_ASPECT;
	private static final String SERVICE_MAPPING_TOPANALYZE_PROFILE = TopServiceMappings.SERVICE_MAPPING_TOPANALYZE_PROFILE;

	private final TopanalyzeService topanalyzeService;

	@Autowired
	public TopanalyzeController(TopanalyzeService topanalyzeService) {
		this.topanalyzeService = topanalyzeService;
	}


	@ApiOperation(value = "执行地表距离量算", notes = "地表距离量算，用于基于DEM数据，量算目标点间的地表距离。")
	@RequestMapping(value = {SERVICE_MAPPING_TOPANALYZE_DISTANCE+"/{org}"}, method = RequestMethod.GET)
	public TopanalyzeOutputData execDistance(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "geo", required = true, allowableValues = "2",
			value = "几何类型。<br />整型，必须为折线，即2。")
		@RequestParam(value = "geo", defaultValue = "2") String geo,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"点的列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return topanalyzeService.execDistance(org, format, alias, version, geo, points,
				srs, auxParams,	request);
		}


	@ApiOperation(value = "执行多边形地表面积量算", notes = "多边形地表面积量算，用于基于DEM数据，量算给定多边形范围内的地表面积。")
	@RequestMapping(value = {SERVICE_MAPPING_TOPANALYZE_AREA+"/{org}"}, method = RequestMethod.GET)
	public TopanalyzeOutputData execArea(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")

		@RequestParam(value = "alias", defaultValue = "") String alias,
		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "geo", required = true, allowableValues = "4",
			value = "几何类型。<br />整型，必须为多边形，即4。")
		@RequestParam(value = "geo", defaultValue = "4") String geo,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"点的列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return topanalyzeService.execArea(org, format, alias, version, geo,
				points, srs, auxParams, request);
		}


	@ApiOperation(value = "执行圆形地表面积量算", notes = "圆形地表面积量算，用于基于DEM数据，量算给定圆形范围内的地表面积。")
	@RequestMapping(value = {SERVICE_MAPPING_TOPANALYZE_AREAOFRING+"/{org}"}, method = RequestMethod.GET)
	public TopanalyzeOutputData execAreaOfRing(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "radius", required = true,
			value = "量算半径。双精度浮点型。")
		@RequestParam(value = "radius", defaultValue = "") String radius,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"点的列表，成对出现（圆面积量算列表里只有一个点）；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return topanalyzeService.execAreaOfRing(org, format, alias, version,
				radius, points, srs, auxParams, request);
		}


	@ApiOperation(value = "执行点高程量算", notes = "点高程量算，用于基于DEM数据，量算目标点的高程值。")
	@RequestMapping(value = {SERVICE_MAPPING_TOPANALYZE_POINTELEVATION+"/{org}"}, method = RequestMethod.GET)
	public TopanalyzeOutputData execPointElevation(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。（点高程量算，应是一个点）<br />" +
				"点的列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return topanalyzeService.execPointElevation(org, format, alias, version,
				points, srs, auxParams, request);
		}


	@ApiOperation(value = "执行区域高程量算", notes = "区域高程量算，用于基于DEM数据，量算给定多边形范围内的高程值。")
	@RequestMapping(value = {SERVICE_MAPPING_TOPANALYZE_ELEVATION+"/{org}"}, method = RequestMethod.GET)
	public TopanalyzeOutputData execElevation(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "geo", required = true, allowableValues = "4",
			value = "几何类型。<br />必须为多边形，即4。")
		@RequestParam(value = "geo", defaultValue = "4") String geo,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"点的列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "dResolutionH", required = true,
			value = "纵向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionH", defaultValue = "4891.9691") String dResolutionH,

		@ApiParam(name = "dResolutionW", required = true,
			value = "横向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionW", defaultValue = "4891.9691") String dResolutionW,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return topanalyzeService.execElevation(org, format, alias, version, geo,
				points, srs, dResolutionH, dResolutionW, auxParams, request);
		}


	@ApiOperation(value = "执行挖方填方", notes = "挖方填方，用于基于DEM数据，量算给定多边形范围内在指定参照高度下的的挖方填方值。")
	@RequestMapping(value = {SERVICE_MAPPING_TOPANALYZE_VOLUME+"/{org}"}, method = RequestMethod.GET)
	public TopanalyzeOutputData execVolume(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "geo", required = true, allowableValues = "4",
			value = "几何类型。<br />必须为多边形，即4。")
		@RequestParam(value = "geo", defaultValue = "4") String geo,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"点的列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "dResolutionH", required = true,
			value = "纵向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionH", defaultValue = "4891.9691") String dResolutionH,

		@ApiParam(name = "dResolutionW", required = true,
			value = "横向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionW", defaultValue = "4891.9691") String dResolutionW,

		@ApiParam(name = "refHeight",
			value = "参照高度，双精度浮点型。参照高度，默认值为0。")
		@RequestParam(value = "refHeight", defaultValue = "0") String refHeight,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return topanalyzeService.execVolume(org, format, alias, version, geo, points,
				srs, dResolutionH, dResolutionW, refHeight, auxParams, request);
		}


	@ApiOperation(value = "执行两点通视", notes = "两点通视，用于基于DEM数据，分析两点间的可视情况。")
	@RequestMapping(value = "/p2pSight/{org}", method = RequestMethod.GET)
	public TopanalyzeOutputData execP2PSight(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "geo", required = true, allowableValues = "2",
			value = "几何类型。<br />必须为折线，即2。")
		@RequestParam(value = "geo", defaultValue = "2") String geo,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"点的列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "dResolutionH", required = true,
			value = "纵向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionH", defaultValue = "4891.9691") String dResolutionH,

		@ApiParam(name = "dResolutionW", required = true,
			value = "横向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionW", defaultValue = "4891.9691") String dResolutionW,

		@ApiParam(name = "drawPic", required = true, allowableValues = "true,false",
			value = "是否要绘制结果示意图，布尔值，默认值为false。")
		@RequestParam(value = "drawPic", defaultValue = "false") String drawPic,

		@ApiParam(name = "observheight",
			value = "观察者高度，双精度浮点型。观察者高度，单位米。")
		@RequestParam(value = "observheight", defaultValue = "") String observheight,

		@ApiParam(name = "targetHeight",
			value = "目标高度，双精度浮点型。目标高度，单位米，默认0。")
		@RequestParam(value = "targetHeight", defaultValue = "0") String targetHeight,

		@ApiParam(name = "picWidth", required = true,
			value = "要绘制的结果示意图的宽度。整型，单位像素。")
		@RequestParam(value = "picWidth", defaultValue = "0") String picWidth,

		@ApiParam(name = "picHeight", required = true,
			value = "要绘制的结果示意图的高度。整型，单位像素。")
		@RequestParam(value = "picHeight", defaultValue = "0") String picHeight,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return topanalyzeService.execP2PSight(org, format, alias, version, geo,
				points, srs, dResolutionH, dResolutionW,
				drawPic, observheight, targetHeight,
				picWidth, picHeight, auxParams, request);
		}


	@ApiOperation(value = "执行区域通视", notes = "区域通视，用于基于DEM数据，分析目标点的给定区域范围内的可视情况。")
	@RequestMapping(value = {SERVICE_MAPPING_TOPANALYZE_VIEWSIGHT+"/{org}"}, method = RequestMethod.GET)
	public TopanalyzeOutputData execViewSight(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。(区域通视应为一个点)<br />" +
				"点的列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "radius", required = true,
			value = "分析范围半径，双精度浮点数。分析范围半径，单位米。")
		@RequestParam(value = "radius", defaultValue = "") String radius,


		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "dResolutionH", required = true,
			value = "纵向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionH", defaultValue = "4891.9691") String dResolutionH,

		@ApiParam(name = "dResolutionW", required = true,
			value = "横向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionW", defaultValue = "4891.9691") String dResolutionW,

		@ApiParam(name = "drawPic", required = true, allowableValues = "true,false",
			value = "是否要绘制结果示意图，布尔值，默认值为false。")
		@RequestParam(value = "drawPic", defaultValue = "false") String drawPic,

		@ApiParam(name = "observheight",
			value = "观察者高度，双精度浮点型。观察者高度，单位米。")
		@RequestParam(value = "observheight", defaultValue = "") String observheight,

		@ApiParam(name = "picWidth", required = true,
			value = "要绘制的结果示意图的宽度。整型，单位像素。")
		@RequestParam(value = "picWidth", defaultValue = "0") String picWidth,

		@ApiParam(name = "picHeight", required = true,
			value = "要绘制的结果示意图的高度。整型，单位像素。")
		@RequestParam(value = "picHeight", defaultValue = "0") String picHeight,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return topanalyzeService.execViewSight(org, format, alias, version, points, radius, srs,
				dResolutionH, dResolutionW, drawPic, observheight,
				picWidth, picHeight, auxParams, request);
		}


	@ApiOperation(value = "执行坡度分析", notes = "坡度分析，用于基于DEM数据，分析给定多边形区域范围内的坡度分布。")
	@RequestMapping(value = {SERVICE_MAPPING_TOPANALYZE_SLOPE+"/{org}"}, method = RequestMethod.GET)
	public TopanalyzeOutputData execSlope(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "geo", required = true, allowableValues = "4",
			value = "几何类型。<br />必须为多边形，即4。")
		@RequestParam(value = "geo", defaultValue = "4") String geo,

		@ApiParam(name = "points", required = true,
			value = "分析多边形范围点列表。<br />" +
				"点的列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "drawPic", required = true, allowableValues = "true,false",
			value = "是否要绘制结果示意图，布尔值，此处必须填true。")
		@RequestParam(value = "drawPic", defaultValue = "true") String drawPic,

		@ApiParam(name = "picWidth", required = true,
			value = "要绘制的结果示意图的宽度，单位像素,需要drawPic值为true，整型。")
		@RequestParam(value = "picWidth", defaultValue = "256") String picWidth,

		@ApiParam(name = "picHeight", required = true,
			value = "要绘制的结果示意图的高度，单位像素，需要drawPic值为true，整型。")
		@RequestParam(value = "picHeight", defaultValue = "256") String picHeight,

		@ApiParam(name = "dResolutionH", required = true,
			value = "纵向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionH", defaultValue = "4891.9691") String dResolutionH,

		@ApiParam(name = "dResolutionW", required = true,
			value = "横向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionW", defaultValue = "4891.9691") String dResolutionW,

		@ApiParam(name = "slopeLevel", required = true,
			value = "灰度级数（4-12），整型。该参数不影响结果。")
		@RequestParam(value = "slopeLevel", defaultValue = "12") String slopeLevel,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,


		HttpServletRequest request) {

			return topanalyzeService.execSlope(org, format, alias, version, geo, points,
				srs, drawPic, picWidth, picHeight,
				dResolutionH, dResolutionW, slopeLevel, auxParams, request);
		}


	@ApiOperation(value = "执行坡向分析", notes = "坡向分析，用于基于DEM数据，分析给定多边形区域范围内的坡向分析。")
	@RequestMapping(value = {SERVICE_MAPPING_TOPANALYZE_ASPECT+"/{org}"}, method = RequestMethod.GET)
	public TopanalyzeOutputData execAspect(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "geo", required = true, allowableValues = "4",
			value = "几何类型。<br />必须为多边形，即4。")
		@RequestParam(value = "geo", defaultValue = "4") String geo,

		@ApiParam(name = "points", required = true,
			value = "分析多边形范围点列表。<br />" +
				"点的列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "drawPic", required = true, allowableValues = "true,false",
			value = "是否要绘制结果示意图，布尔值，此处必须填true。")
		@RequestParam(value = "drawPic", defaultValue = "true") String drawPic,

		@ApiParam(name = "picWidth", required = true,
			value = "要绘制的结果示意图的宽度，单位像素,需要drawPic值为true，整型。")
		@RequestParam(value = "picWidth", defaultValue = "256") String picWidth,

		@ApiParam(name = "picHeight", required = true,
			value = "要绘制的结果示意图的高度，单位像素，需要drawPic值为true，整型。")
		@RequestParam(value = "picHeight", defaultValue = "256") String picHeight,

		@ApiParam(name = "dResolutionH", required = true,
			value = "纵向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionH", defaultValue = "4891.9691") String dResolutionH,

		@ApiParam(name = "dResolutionW", required = true,
			value = "横向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionW", defaultValue = "4891.9691") String dResolutionW,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return topanalyzeService.execAspect(org, format, alias, version, geo, points, srs,
				drawPic, picWidth, picHeight, dResolutionH, dResolutionW,
				auxParams, request);
		}


	@ApiOperation(value = "执行断面分析", notes = "断面分析，用于基于DEM数据，分析两点间断面情况。")
	@RequestMapping(value = {SERVICE_MAPPING_TOPANALYZE_PROFILE+"/{org}"}, method = RequestMethod.GET)
	public TopanalyzeOutputData execProfile(

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

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型。")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。<br />整型，如1000,1001。")
		@RequestParam(value = "version", defaultValue = "1000") String version,

		@ApiParam(name = "geo", required = true, allowableValues = "2",
			value = "几何类型。<br />必须为折线，即2。")
		@RequestParam(value = "geo", defaultValue = "2") String geo,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。\n" +
				"点的列表，成对出现；\n" +
				"&lt;x&gt;为x坐标,双精度浮点型；\n" +
				"&lt;y&gt;为y坐标,双精度浮点型；\n" +
				"每个点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "drawPic", required = true, allowableValues = "true,false",
			value = "是否要绘制结果示意图，布尔值，默认false。")
		@RequestParam(value = "drawPic", defaultValue = "false") String drawPic,

		@ApiParam(name = "picWidth", required = true,
			value = "要绘制的结果示意图的宽度，单位像素,需要drawPic值为true，整型。")
		@RequestParam(value = "picWidth", defaultValue = "256") String picWidth,

		@ApiParam(name = "picHeight", required = true,
			value = "要绘制的结果示意图的高度，单位像素，需要drawPic值为true，整型。")
		@RequestParam(value = "picHeight", defaultValue = "256") String picHeight,

		@ApiParam(name = "dResolutionH", required = true,
			value = "纵向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionH", defaultValue = "4891.9691") String dResolutionH,

		@ApiParam(name = "dResolutionW", required = true,
			value = "横向分辨率，双精度浮点型，如：4891.9691。该参数不影响结果，传递不为空即可。")
		@RequestParam(value = "dResolutionW", defaultValue = "4891.9691") String dResolutionW,

		@ApiParam(name = "pointNum",
			value = "返回的断面分析结果点个数，整型。返回的断面分析结果点串个数，默认100，默认即可。")
		@RequestParam(value = "pointNum", defaultValue = "") String pointNum,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return topanalyzeService.execProfile(org, format, alias, version, geo, points, srs,
				drawPic, picWidth, picHeight, dResolutionH, dResolutionW,
				pointNum, auxParams, request);
		}

}
