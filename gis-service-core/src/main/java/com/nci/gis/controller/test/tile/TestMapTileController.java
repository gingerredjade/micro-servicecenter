package com.nci.gis.controller.test.tile;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 功能服务-地图瓦片服务REST测试Controller
 *
 * @since 1.0.0 2019年08月22日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Api(value = "TestMapTileController",description = "地图瓦片服务调用测试")
//@RestController
public class TestMapTileController {

	private static final Logger _logger = LoggerFactory.getLogger(TestMapTileController.class);

	@Autowired
	RestTemplate restTemplate;

	@GetMapping("/getMapTile")
	public void getMapTile() {

		/*保留查看用
		URL url = new URL(URIStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/octet-stream");*/


		MapTileOutputData response = restTemplate.getForObject("http://GIS-SERVICE-CORE/gsmaptile?alias=demchina_MGIS102089&version=1000&namespace=demchina_1000&layerAlias=top-all&dataType=3&dataFormat=1&appType=1&dataVersion=new&level=1&row=0&col=1", MapTileOutputData.class);
		_logger.info("response={}",response);

		StringBuilder sb = new StringBuilder();
		sb.append("E:/");
		sb.append("demchina_MGIS102089-1000-demchina_1000-top-all-1-0-1");
		sb.append(".jpg");
		File file = new File(sb.toString());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(response.getResult());
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
