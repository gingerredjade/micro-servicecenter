package com.nci.gis.controller.test.tile;

import com.nci.entity.RespondMessage;
import com.nci.utils.MessageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Map;


/**
 * 功能服务-地图瓦片服务REST
 *
 * @since 1.0.0 2019年08月22日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(value = "MapTileController", description = "地图瓦片服务主要用于获取网络化的地图瓦片信息")
//@RestController
@EnableAutoConfiguration
@CrossOrigin
public class MapTileController {
	private static final Logger _logger = LoggerFactory.getLogger(MapTileController.class);

	// 约定的服务资源标识
	private static final String SERVICE_MAPPING_MAPTILE = "gsmaptile";

	// GIS Web服务器地址，形如“http://192.168.56.157:8999/mswss/maps/standardsvcs/”
	@Value("${gisapp.webserver.prefix}")
	private String GIS_WEBSERVER_PREFIX;


	@ApiOperation(value = "执行地图瓦片服务",
		notes = "按需求及约定传递参数，服务应答为符合请求条件的、图片化的、符合《地理信息网络地图显示要求》中制定的瓦片地图的分块与组织规范的地图瓦片。")
	@RequestMapping(value = {SERVICE_MAPPING_MAPTILE}, method = RequestMethod.GET)
	public MapTileOutputData executeGetMapTile(

		@ApiParam(name = "format", required = false, allowableValues = "image",
			value = "应答数据编码")
		@RequestParam(value = "format", defaultValue = "") String format,


		@ApiParam(name = "alias", required = true,
			value = "服务实例别名。字符串型，用于表示请求的服务实例别名，如”demchina_MGIS102089” ")
		@RequestParam(value = "alias", defaultValue = "") String alias,


		@ApiParam(name = "version", required = true,
			value = "服务实例版本。整型，用于表示请求的服务实例版本，如1000 ")
		@RequestParam(value = "version", defaultValue = "") String version,


		@ApiParam(name = "namespace", required = true,
			value = "瓦片图层命名空间。字符串型 ")
		@RequestParam(value = "namespace", defaultValue = "") String namespace,


		@ApiParam(name = "layerAlias", required = true,
			value = "瓦片图层别名。字符串型 ")
		@RequestParam(value = "layerAlias", defaultValue = "") String layerAlias,


		@ApiParam(name = "dataType", required = true, allowableValues = "1,2,3",
			value = "瓦片图层数据格式类型。整型，可取值包括：1-数据面片 2-矢量显示瓦片 3-地图瓦片\n" +
			"DLG、DEM数据该值设置为3；影像数据该值设置为1； ")
		@RequestParam(value = "dataType", defaultValue = "") String dataType,


		@ApiParam(name = "dataFormat", required = true, allowableValues = "1,2",
			value = "瓦片图层格式。整型，可取值包括：1表示jpg/jpeg；2表示png ")
		@RequestParam(value = "dataFormat", defaultValue = "") String dataFormat,


		@ApiParam(name = "appType", required = true, allowableValues = "1,2",
			value = "应用类型。整型，可取值包括：1表示pc；2表示phone ")
		@RequestParam(value = "appType", defaultValue = "") String appType,


		@ApiParam(name = "dataVersion", required = true,
			value = "数据版本。字符串型，new表示最新版本数据，使用默认new ")
		@RequestParam(value = "dataVersion", defaultValue = "new") String dataVersion,


		@ApiParam(name = "datascale", required = false,
			value = "数据比例尺。整型，当瓦片数据对应的的原始数据为矢量数据时可设置该参数 ")
		@RequestParam(value = "datascale", defaultValue = "") String datascale,


		@ApiParam(name = "dataresolution", required = false,
			value = "数据分辨率。浮点数，当瓦片数据对应的的原始数据为栅格数据时可设置该参数 ")
		@RequestParam(value = "dataresolution", defaultValue = "") String dataresolution,


		@ApiParam(name = "level", required = true,
			value = "金字塔级别。整型 ")
		@RequestParam(value = "level", defaultValue = "") String level,


		@ApiParam(name = "row", required = true,
			value = "瓦片所属行号。整型 ")
		@RequestParam(value = "row", defaultValue = "") String row,


		@ApiParam(name = "col", required = true,
			value = "瓦片所属列号。整型 ")
		@RequestParam(value = "col", defaultValue = "") String col,


		@ApiParam(name = "imageType", required = false, allowableValues = "png,jpg",
			value = "应答图片的格式。字符串型，可取值包括:png、jpg ")
		@RequestParam(value = "imageType", defaultValue = "application/octet-stream") String imageType,

		HttpServletRequest request,
		HttpServletResponse response) throws MalformedURLException {


		/**
		 * 0-- 监测gis web server配置
		 */
		if (GIS_WEBSERVER_PREFIX.isEmpty()) {
			String errMsg = "GIS Web服务器服务请求串前缀为空，请检查";
			String errStr = MessageUtils.buildErrorInfo(RespondMessage.STATUS_FAILED, errMsg);
			_logger.error(errStr);
			//return errMsg;
		} else {
			if (GIS_WEBSERVER_PREFIX.endsWith("/")) {
				int indexFlag = GIS_WEBSERVER_PREFIX.lastIndexOf("/");
				GIS_WEBSERVER_PREFIX = GIS_WEBSERVER_PREFIX.substring(0, indexFlag);
			}
		}


		/**
		 * 1-- 获取所有请求参数,构造请求URI
		 */
		String URLStr = buildRequestURL(GIS_WEBSERVER_PREFIX, SERVICE_MAPPING_MAPTILE, request);
		_logger.info("【地图瓦片服务】Current Request URL = [{}].", URLStr);


		/**
		 * 2-- 执行REST请求
		 */
		byte[] resultData = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			resultData = restTemplate.getForObject(URLStr, byte[].class);

			/*testByte2FileImage(resultData,alias,version,level,row,col);		落地测试用*/
			_logger.info("【地图瓦片服务】URL Request success,[{}].", URLStr);
		} catch (Exception e) {
			_logger.error("【地图瓦片服务】URL Request failed,[{}]." + e.getMessage(), URLStr);
		}


		/**
		 * 3-- 封装应答
		 */
		MapTileOutputData result = new MapTileOutputData(resultData);
		return result;
	}


	/**
	 * 构建请求URL--GIS WEB服务器的REST URL
	 * @param gisServerUrl	GIS WEB服务器服务地址
	 *                         形如“http://192.168.56.157:8999/mswss/maps/standardsvcs/”
	 * @param svcMapping	约定的服务资源标识
	 * @param request		前端请求信息
	 * @return
	 */
	private static String buildRequestURL(String gisServerUrl, String svcMapping, HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append(gisServerUrl);
		sb.append("/");
		sb.append(svcMapping);
		sb.append("?");

		Map<String, String[]> params = request.getParameterMap();

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			String key = entry.getKey();
			String[] val = entry.getValue();
			_logger.info("[{}]=[{}].", key, val[0]);
			_logger.info("{}={}.", key, val[0]);
			sb.append(key + "=" + val[0] + "&");
		}
		sb.deleteCharAt(sb.lastIndexOf("&"));
		String URLStr = sb.toString();
		return URLStr;
	}

	/**
	 * 将通过REST获取到的地图瓦片数据流写入文件进行准确性测试
	 * @param bytes
	 * @param alias
	 * @param version
	 * @param level
	 * @param row
	 * @param col
	 */
	private static void testByte2FileImage(byte[] bytes,
										   String alias, String version, String level, String row, String col) {
		StringBuilder sb = new StringBuilder();
		sb.append("E:/");
		sb.append(alias+"-"+version+"-"+level+"-"+row+"-"+col);
		sb.append(".jpg");
		File file = new File(sb.toString());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



}
