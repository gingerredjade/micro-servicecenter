package com.nci.gis.controller.test.tile;


import io.swagger.annotations.ApiModelProperty;

/**
 * 地图瓦片服务输出对象
 *
 * Created by JHy on 2019/8/22.
 */
public class MapTileOutputData {

	@ApiModelProperty(value = "地图瓦片服务结果数据")
	byte[] data;

	//无参构造函数。不要删除！！
	public MapTileOutputData() {}

	public MapTileOutputData(byte[] data) {
		this.data = data;
	}

	public byte[] getResult() {
		return data;
	}

	public void setResult(byte[] data) {
		this.data = data;
	}
}
