package com.nci.gis.controller.function.map;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 地图显示服务输出对象
 *
 * Created by JHy on 2019/9/18.
 */
@Data
@ApiModel(description = "地图显示服务响应实体")
class MapOutputData extends OutputData<String> {

	MapOutputData() {

	}
}
