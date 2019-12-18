package com.nci.gis.controller.data.placenamefts;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 地名检索服务输出对象（全文检索ES）（信服模式）
 *
 * Created by JHy on 2019/8/22.
 */
@Data
@ApiModel(description = "地名检索服务响应实体")
class PlacenameFtsOutputData extends OutputData<String> {

	//无参构造函数。不要删除！！
	PlacenameFtsOutputData() {
	}

	/*@ApiModelProperty(value = "地名检索服务结果数据")
	String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}*/
}
