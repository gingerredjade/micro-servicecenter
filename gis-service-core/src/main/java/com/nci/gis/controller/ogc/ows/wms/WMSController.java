package com.nci.gis.controller.ogc.ows.wms;

import com.nci.constants.ServiceTypes.OGC_SERVICE_WMS;
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
 * 功能服务-OGC规范-网络地图服务（WMS-Web Map Service） REST
 *
 * @since 1.0.0 2019年09月20日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(tags = "网络地图服务WMS",
	value = "WMSController", description = "提供共享地图数据。该服务遵循OGC规范，根据客户端的请求，提供地图图像以及地图服务")
@RestController
@EnableAutoConfiguration
@CrossOrigin
public class WMSController {

	private static final Logger _logger = LoggerFactory.getLogger(WMSController.class);

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_WMS = OGC_SERVICE_WMS.value;

	private final WMSService wmsService;

	@Autowired
	public WMSController(WMSService wmsService) {
		this.wmsService = wmsService;
	}

	@ApiOperation(value = "执行WMS操作",
		notes = "WMS服务，提供共享地图数据。该服务遵循OGC规范，根据客户端的请求，提供地图图像以及地图服务。<br />" +
			"向客户端提供当前地图服务器可以提供的空间信息类型和范围、服务图层信息、缺省返回格式等服务描述信息。<br />" +
			"该功能操作遵循OGC的WMS规范，提供2个功能操作，即GetCapbilities操作、GetMap操作。<br /><br />" +
			"注意：<br />" +
			"当request=GetCapabilities时，输出XML格式数据，传递必填参数service、request、version即可。<br />" +
			"当request=GetMap时，直接输出图片，需传递的参数为：service、request、version、layers、bbox、srs、format、width、height。")
	@RequestMapping(value = {SERVICE_MAPPING_WMS+"/{org}/**"}, method = RequestMethod.GET)
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

		@ApiParam(name = "service", required = true, allowableValues = "wms",
			value = "请求服务，固定值wms。")
		@RequestParam(value = "service", defaultValue = "wms") String service,

		@ApiParam(name = "request", required = true, allowableValues = "GetCapabilities,GetMap",
		value = "请求名称，固定值GetCapabilities或GetMap。")
		@RequestParam(value = "request", defaultValue = "GetCapabilities") String request,

		@ApiParam(name = "version", required = true,
			value = "服务版本，如1.3.0。")
		@RequestParam(value = "version", defaultValue = "") String version,

		@ApiParam(name = "layers",
			value = "request=GetMap时必填。<br />" +
				"图层，取值来源于GetCapabilities里的元数据信息，格式为“服务别名_服务版本:图层别名”。")
		@RequestParam(value = "layers", defaultValue = "") String layers,

		@ApiParam(name = "bbox",
			value = "request=GetMap时必填。<br />" +
				"边框范围，格式为minx,mixy,maxx,maxy。单位为经纬度,如bbox=-180,-90,180,90。")
		@RequestParam(value = "bbox", defaultValue = "") String bbox,

		@ApiParam(name = "crs",
			value = "request=GetMap时必填。<br />" +
				"坐标参考系，可选值包括3857，112015，4978，4490，4214，4610。<br />" +
				"其中3857对应空间参考EPSG:3857即墨卡托_WGS84_0_0空间参考；<br />" +
				"112015对应空间参考MGIS:112015，即墨卡托_CGCS2000_0_0空间参考；<br />" +
				"4978对应空间参考MGIS:4978，即WGS84空间参考；<br />" +
				"4490对应空间参考MGIS:4490，即CGCS2000空间参考；<br />" +
				"4214对应空间参考MGIS:4214级北京54空间参考；<br />" +
				"4610对应空间参考MGIS:4610即西安80空间参考。")
		@RequestParam(value = "crs", defaultValue = "") String crs,

		@ApiParam(name = "format", allowableValues = "image/jpg,image/png",
			value = "request=GetMap时必填。<br />应答格式，请求的瓦片格式。")
		@RequestParam(value = "format", defaultValue = "") String format,

		@ApiParam(name = "width",
			value = "request=GetMap时必填。<br />地图图像宽度，请求的图片大小宽度，单位为像素。")
		@RequestParam(value = "width", defaultValue = "") String width,

		@ApiParam(name = "height",
			value = "request=GetMap时必填。<br />地图图像高度，请求的图片大小高度，单位为像素。")
		@RequestParam(value = "height", defaultValue = "") String height,

		@ApiParam(name = "styles",
			value = "风格，默认可填population。")
		@RequestParam(value = "styles", defaultValue = "") String styles,

		@ApiParam(name = "auxParams",
			value = "扩展参数。提供扩展参数的填充，统一以JSON格式组织参数。")
		@RequestParam(value = "auxParams", defaultValue = "") String auxParams,

		HttpServletRequest req,
		HttpServletResponse response) {

		return wmsService.execWms(org, service, request, version, layers, bbox,
			crs, format, width, height, styles, auxParams,
			req, response);
	}

}
