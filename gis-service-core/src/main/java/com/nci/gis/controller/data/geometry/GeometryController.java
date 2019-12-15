package com.nci.gis.controller.data.geometry;

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
 * 功能服务-几何计算服务REST
 *
 * @since 1.0.0 2019年09月12日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "几何计算服务",
	value = "GeometryController",
	description = "提供距离量算、面积量算、方位角量算、缓冲区分析、坐标转换等与具体地理资源无关的几何计算功能")
@RestController
@EnableAutoConfiguration
@CrossOrigin
@RequestMapping(value = "gsgeometry")
public class GeometryController {

	private static final Logger _logger = LoggerFactory.getLogger(GeometryController.class);

	private static final String SERVICE_MAPPING_GEOMETRY_LENGTH = "length";
	private static final String SERVICE_MAPPING_GEOMETRY_AREA = "area";
	private static final String SERVICE_MAPPING_GEOMETRY_AZIMUTH = "azimuth";
	private static final String SERVICE_MAPPING_GEOMETRY_BUFFER = "buffer";
	private static final String SERVICE_MAPPING_GEOMETRY_SRSCONVERSION = "srsConversion";

	private final GeometryService geometryService;

	@Autowired
	public GeometryController(GeometryService geometryService) {
		this.geometryService = geometryService;
	}

	@ApiOperation(value = "执行距离量算", notes = "距离量算，用于获取目标点间的距离之和。")
	@RequestMapping(value = {SERVICE_MAPPING_GEOMETRY_LENGTH+"/{org}"}, method = RequestMethod.GET)
	public GeometryOutputData execLength(

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

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"点的列表，成对出现，形如points=&lt;x&gt;,&lt;y&gt;,&lt;x&gt;,&lt;y&gt;,…；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

			return  geometryService.execLength(org, format, points, srs,
				auxParams, request);
		}


	@ApiOperation(value = "执行面积量算", notes = "面积量算，用于获取目标点围成的多边形面积。")
	@RequestMapping(value = {SERVICE_MAPPING_GEOMETRY_AREA+"/{org}"}, method = RequestMethod.GET)
	public GeometryOutputData execArea(

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

		@ApiParam(name = "geo", required = true, allowableValues = "4",
			value = "几何类型。整型，可取值为：面(4)类型。")
		@RequestParam(value = "geo", defaultValue = "4") String geo,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"点的列表，成对出现，形如points=&lt;x&gt;,&lt;y&gt;,&lt;x&gt;,&lt;y&gt;,…；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

		return geometryService.execArea(org, format, geo, points, srs, auxParams,
			request);
	}


	@ApiOperation(value = "执行面方位角量算", notes = "方位角量算，用于获取参照点相对于起始点的方位角，单位为度。")
	@RequestMapping(value = {SERVICE_MAPPING_GEOMETRY_AZIMUTH+"/{org}"}, method = RequestMethod.GET)
	public GeometryOutputData execAzimuth(

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

		@ApiParam(name = "pointBegin", required = true,
			value = "起始点。<br />" +
				"形如：&lt;x&gt;,&lt;y&gt;<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；")
		@RequestParam(value = "pointBegin", defaultValue = "") String pointBegin,

		@ApiParam(name = "pointEnd", required = true,
			value = "参照点。<br />" +
				"形如：&lt;x&gt;,&lt;y&gt;<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；")
		@RequestParam(value = "pointEnd", defaultValue = "") String pointEnd,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

		return geometryService.execAzimuth(org, format, pointBegin, pointEnd, srs, auxParams,
			request);
	}


	@ApiOperation(value = "执行缓冲区分析", notes = "缓冲区分析，主要用于获取点、线、多边形等几何形状的缓冲区信息。")
	@RequestMapping(value = {SERVICE_MAPPING_GEOMETRY_BUFFER+"/{org}"}, method = RequestMethod.GET)
	public GeometryOutputData execBuffer(

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

		@ApiParam(name = "distances", required = true,
			value = "缓冲区宽度或半径。整数数组,以“，”隔开，若为线目标则需两个值，点目标和面目标为一个值")
		@RequestParam(value = "distances", defaultValue = "") String distances,

		@ApiParam(name = "smooth", required = true,
			value = "圆弧拟合度。4—120之间取值，建议值为12。")
		@RequestParam(value = "smooth", defaultValue = "") String smooth,

		@ApiParam(name = "geo", required = true, allowableValues = "1,2,4",
			value = "几何类型。整型，可取值包括点(1)、线(2)、面(4)类型。")
		@RequestParam(value = "geo", defaultValue = "") String geo,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"成对出现，形如points=&lt;x&gt;,&lt;y&gt;,&lt;x&gt;,&lt;y&gt;,...<br />" +
				"<x>为x坐标,双精度浮点型；<br />" +
				"<y>为y坐标,双精度浮点型；<br />" +
				"若为点目标，则只有一组（X,Y），且几何类型为1")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，字符串型。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

		return geometryService.execBuffer(org, format, distances, smooth, geo,
			points, srs, auxParams, request);
	}


	@ApiOperation(value = "执行空间参考转换", notes = "空间参考转换，用于获取进行空间参考转换后的几何目标数据。")
	@RequestMapping(value = {SERVICE_MAPPING_GEOMETRY_SRSCONVERSION+"/{org}"}, method = RequestMethod.GET)
	public GeometryOutputData execSrsConversion(

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

		@ApiParam(name = "geo", required = true, allowableValues = "1,2,4",
			value = "几何类型。整型，可取值包括点(1)、线(2)、面(4)类型。")
		@RequestParam(value = "geo", defaultValue = "") String geo,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"成对出现，形如points=&lt;x&gt;,&lt;y&gt;,&lt;x&gt;,&lt;y&gt;,…；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"若为点目标，则只有一组（X,Y），且几何类型为1")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "初始空间参考编码，字符串型。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "toSrs", required = true,
			value = "目标空间参考编码，字符串型。")
		@RequestParam(value = "toSrs", defaultValue = "") String toSrs,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

		return geometryService.execSrsConversion(org, format, geo, points,
			srs, toSrs, auxParams, request);
	}

}
