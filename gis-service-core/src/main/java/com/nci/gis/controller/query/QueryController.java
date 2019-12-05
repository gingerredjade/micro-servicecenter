package com.nci.gis.controller.query;

import com.nci.constants.ServiceTypes.QUERY_SERVICE;
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
 * 功能服务-空间查询服务REST（三期模式）
 *
 * @since 1.0.0 2019年12月04日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "空间查询服务",
	value = "QueryController", description = "获取符合查询条件的矢量目标信息")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class QueryController {

	private static final Logger _logger = LoggerFactory.getLogger(QueryController.class);

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_QUERY = QUERY_SERVICE.value;

	private final QueryService queryService;
	@Autowired
	public QueryController(QueryService queryService) {
		this.queryService = queryService;
	}

	@ApiOperation(value = "执行空间查询服务", notes = "获取符合查询条件的矢量目标信息。")
	@RequestMapping(value = {SERVICE_MAPPING_QUERY+"/{org}"}, method = RequestMethod.GET)
	public QueryOutputData execute(
		@ApiParam(name = "org", required = true, allowableValues = "cetc15,sm,ev,mc,nav,gt",
			value = "服务提供机构标识。<br />" +
				"15所：cetc15<br />" +
				"超图:sm<br />" +
				"国遥新天地:ev<br />" +
				"星球时空:mc<br />" +
				"四维图新:nav<br />" +
				"庚图:gt<br />")
		@PathVariable String org,

		@ApiParam(name = "format", required = true,
			value = "应答数据编码。")
		@RequestParam(value = "format", defaultValue = "json") String format,

		@ApiParam(name = "alias", required = true,
			value = "服务别名。字符串型，表示请求的服务实例别名，如“BJMAP”")
		@RequestParam(value = "alias", defaultValue = "") String alias,

		@ApiParam(name = "version", required = true,
			value = "服务版本。整型，表示请求的服务实例版本，如1000")
		@RequestParam(value = "version", defaultValue = "") String version,


		// 空间查询
		@ApiParam(name = "geo", required = true,
			value = "几何类型。整型，建议使用1或4<br />" +
				"1:点；<br />" +
				"2:折线；<br />" +
				"4：多边形；<br />" +
				"8：椭圆边界线；<br />" +
				"16：椭圆；<br />" +
				"32：任意边界面；<br />" +
				"64：多点；<br />" +
				"128：多线；<br />" +
				"256：多面；<br />" +
				"512：复合目标；<br />" +
				"1024：矩形；<br />" +
				"524287：所有几何类型；")
		@RequestParam(value = "geo", defaultValue = "4") String geo,

		@ApiParam(name = "points", required = true,
			value = "目标点列表。<br />" +
				"点的列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个点中间以逗号分隔。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码，如MGIS:4490。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "tolarence",
			value = "查询容差，默认为0，双精度浮点型")
		@RequestParam(value = "tolarence", defaultValue = "") String tolarence,

		@ApiParam(name = "relation",
			value = "空间关系(相交、相离、包含等)，默认为1，表示相交")
		@RequestParam(value = "relation", defaultValue = "") String relation,

		@ApiParam(name = "feature",
			value = "查询的feature类型(可以只查询某一类型的Feature)，默认为524287，表示查询所有Feature")
		@RequestParam(value = "feature", defaultValue = "") String feature,

		@ApiParam(name = "number",
			value = "整型。若有大量查询结果时，可设定返回目标个数,默认全部返回")
		@RequestParam(value = "number", defaultValue = "") String number,


		// 属性查询
		@ApiParam(name = "search",
			value = "查询语句。字符串型，属性查询条件，若为空则不进行属性查询")
		@RequestParam(value = "search", defaultValue = "") String search,

		HttpServletRequest request) {

		return queryService.execQuery(org, format, alias, version,

			// 空间查询条件参数
			geo, points, srs, tolarence, relation, feature,

			// 结果定制参数
			number,

			// 属性查询参数
			search,

			request);
	}

}
