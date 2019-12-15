package com.nci.gis.controller.data.networkanalyze;

import com.nci.constants.ServiceTypes.NETWORKANALYSE_SERVICE;
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
 * 功能服务-网络分析服务REST
 *
 * @since 1.0.0 2019年09月12日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "网络分析服务",
	value = "NetworkAnalyzeController", description = "获取目标点间的最优路径信息")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class NetworkAnalyzeController {

	private static final Logger _logger = LoggerFactory.getLogger(NetworkAnalyzeController.class);

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_NETWORKANALYZE = NETWORKANALYSE_SERVICE.value;

	private final NetworkAnalyzeService networkAnalyzeService;

	@Autowired
	public NetworkAnalyzeController(NetworkAnalyzeService networkAnalyzeService) {
		this.networkAnalyzeService = networkAnalyzeService;
	}

	@ApiOperation(value = "执行网络分析服务", notes = "获取目标点间的最优路径信息。")
	@RequestMapping(value = {SERVICE_MAPPING_NETWORKANALYZE+"/{org}"}, method = RequestMethod.GET)
	public NetworkAnalyzeOutputData execute(

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
			value = "服务版本。<br />整型，例如1000，,1001。")
		@RequestParam(value = "version", defaultValue = "") String version,

		@ApiParam(name = "points", required = true,
			value = "网络分析参考点。参数点列表，成对出现；<br />" +
				"第一对表示起始点，第二对表示终止点；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"起点和止点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递。起止点以'起点,止点'形式传递。")
		@RequestParam(value = "points", defaultValue = "") String points,

		@ApiParam(name = "srs", required = true,
			value = "空间参考，空间参考编码。")
		@RequestParam(value = "srs", defaultValue = "") String srs,

		@ApiParam(name = "avoidPoints",
			value = "回避点列表。回避点列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个回避点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "avoidPoints", defaultValue = "") String avoidPoints,

		@ApiParam(name = "passPoints",
			value = "必经点列表。必经点列表，成对出现；<br />" +
				"&lt;x&gt;为x坐标,双精度浮点型；<br />" +
				"&lt;y&gt;为y坐标,双精度浮点型；<br />" +
				"每个必经点以&lt;x&gt;&#x5F;&lt;y&gt;形式传递，多个点中间以逗号分隔。")
		@RequestParam(value = "passPoints", defaultValue = "") String passPoints,

		@ApiParam(name = "naviMode", allowableValues = "1,2,3",
			value = "路径规划模式，默认1。<br />" +
				"1：高速优先；<br />" +
				"2：距离最短；<br />" +
				"3：不走高速。")
		@RequestParam(value = "naviMode", defaultValue = "1") String naviMode,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest request) {

		return networkAnalyzeService.execNetwork(org, format, alias, version,
			points, srs, avoidPoints, passPoints, naviMode, auxParams, request);
	}


}
