package com.nci.gis.controller.data.placename;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 地名检索服务输出对象
 *
 * Created by JHy on 2019/8/22.
 */
@Data
@ApiModel(description = "地名检索服务响应实体")
class PlacenameOutputData extends OutputData<String> {

	//无参构造函数。不要删除！！
	PlacenameOutputData() {
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
