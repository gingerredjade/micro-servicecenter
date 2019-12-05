package com.nci.gis.controller.queryfts;

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
 * 功能服务-空间查询服务REST 信服模式（全文检索ES）
 *
 * @since 1.0.0 2019年11月22日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "空间查询服务",
	value = "QueryController", description = "获取符合查询条件的矢量目标信息")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class QueryFtsController {

	private static final Logger _logger = LoggerFactory.getLogger(QueryFtsController.class);

	// 约定的服务资源标识
	public static final String SERVICE_MAPPING_QUERY = "gsqueryfts";

	private final QueryFtsService queryFtsService;
	@Autowired
	public QueryFtsController(QueryFtsService queryFtsService) {
		this.queryFtsService = queryFtsService;
	}

	@ApiOperation(value = "执行空间查询服务", notes = "获取符合查询条件的矢量目标信息。")
	@RequestMapping(value = {SERVICE_MAPPING_QUERY+"/{org}"}, method = RequestMethod.GET)
	public QueryFtsOutputData execute(
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

		@ApiParam(name = "layerId",
			value = "查询的图层标识。字符串型，若为空，则对所有矢量图层进行查询")
		@RequestParam(value = "layerId", defaultValue = "") String layerId,

		// 属性查询条件参数
		@ApiParam(name = "attrSearchMatchType",
			value = "【属性查询时必填】" +
				"属性查询匹配关系。<br />" +
				"属性查询匹配关系，字符串格式，取值如下：<br />" +
				"must：必须匹配；<br />" +
				"mustnot：必须不匹配；<br />" +
				"should：可匹配可不匹配")
		@RequestParam(value = "attrSearchMatchType", defaultValue = "") String attrSearchMatchType,

		@ApiParam(name = "attrSearchType",
			value = "【属性查询时必填】" +
				"属性查询类型。<br />" +
				"属性查询类型，字符串格式，取值如下：<br />" +
				"in：in查询；<br />" +
				"match：匹配查询；<br />" +
				"range：范围查询")
		@RequestParam(value = "attrSearchType", defaultValue = "") String attrSearchType,

		@ApiParam(name = "attr",
			value = "匹配查询条件信息。<br />" +
				"字符串型，当attrSearchType=match时，该值表示匹配查询的查询字段值，" +
				"格式为'<b>属性\\`值</b>'对，<br />" +
				"多个属性查询条件以'<b>key1\\`value1,key2\\`value2,key3\\`value3</b>'形式传递")
		@RequestParam(value = "attr", defaultValue = "") String attr,

		@ApiParam(name = "inkey",
			value = "in查询时查询字段名称。<br />" +
				"字符串型，当attrSearchType=in时，该值表示in查询的查询字段名称，不能为空")
		@RequestParam(value = "inkey", defaultValue = "") String inkey,

		@ApiParam(name = "inval",
			value = "in查询时查询条件信息。<br />" +
				"字符串型，当attrSearchType=in时，该值表示in查询的查询字段值，不能为空")
		@RequestParam(value = "inval", defaultValue = "") String inval,

		@ApiParam(name = "rangekey",
			value = "范围查询时查询字段名称。<br />" +
				"字符串型，当attrSearchType=range时，该值表示范围查询的查询字段名称，不能为空")
		@RequestParam(value = "rangekey", defaultValue = "") String rangekey,

		@ApiParam(name = "gteval",
			value = "范围查询中的大于等于条件。<br />" +
				"字符串型，当attrSearchType=range时，该值表示范围查询的查询大于等于条件")
		@RequestParam(value = "gteval", defaultValue = "") String gteval,

		@ApiParam(name = "lteVal",
			value = "范围查询中的小于等于条件。<br />" +
				"字符串型，当attrSearchType=range时，该值表示范围查询的查询小于等于条件")
		@RequestParam(value = "lteVal", defaultValue = "") String lteVal,

		// 空间查询条件参数
		@ApiParam(name = "geoSearchType",
			value = "【空间查询时必填】" +
				"空间查询类型。<br />" +
				"字符串型，取值如下<br />" +
				"circle：圆形范围查询；<br />" +
				"rect：矩形范围查询；<br />" +
				"polygon：多边形范围查询；<br />" +
				"mulpolygon：多个多边形范围查询")
		@RequestParam(value = "geoSearchType", defaultValue = "") String geoSearchType,

		@ApiParam(name = "spatialRel",
			value = "空间位置关系。<br />" +
				"整型，为空时表示相交关系，使用空间查询时可指定，一般默认")
		@RequestParam(value = "spatialRel", defaultValue = "") String spatialRel,

		@ApiParam(name = "distance",
			value = "圆形范围查询半径。<br />" +
				"空间查询类型geoSearchType为圆形范围查询时，不能为空，表示查询半径，单位为千米，浮点型（double）")
		@RequestParam(value = "distance", defaultValue = "") String distance,

		@ApiParam(name = "points",
			value = "【空间查询时必填】" +
				"空间查询点目标。<br />" +
				"描述空间查询形状的点列表信息，包含若干个点。<br />" +
				"圆形范围查询包含一个点，表示圆心；<br />" +
				"矩形范围查询包含2个点，表示左上点和右下点；<br />" +
				"多边形范围查询时，要求首尾点相同，且围成多边形的线之间不能交叉；<br />" +
				"多个多边形查询包含多个多边形点列表。<br />" +
				"点列表所含各点由<x><y>组成，<br />" +
				"其中<x>为x坐标,双精度浮点型；<br />" +
				"<y>为y坐标,双精度浮点型，<br />" +
				"点的x、y坐标用下划线隔开，多个点之间以逗号隔开")
		@RequestParam(value = "points", defaultValue = "") String points,

		// 结果定制参数
		@ApiParam(name = "featureType",
			value = "查询目标几何类型。整型，一般默认即可")
		@RequestParam(value = "featureType", defaultValue = "") String featureType,

		@ApiParam(name = "featureNum",
			value = "查询目标最大个数。整型，正整数")
		@RequestParam(value = "featureNum", defaultValue = "") String featureNum,

		@ApiParam(name = "names",
			value = "查询到的目标属性字段列表。<br />" +
				"属性字段列表，用于设置要获取的查询目标的属性字段")
		@RequestParam(value = "names", defaultValue = "") String names,

		HttpServletRequest request) {

		return queryFtsService.execQuery(org, format, layerId,
			attrSearchMatchType, attrSearchType, attr,
			inkey, inval, rangekey, gteval, lteVal,

			// 空间查询条件参数
			geoSearchType, spatialRel, distance, points,

			// 结果定制参数
			featureType, featureNum, names,

			request);
	}

}
