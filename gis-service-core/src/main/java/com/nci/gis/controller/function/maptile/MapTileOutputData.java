package com.nci.gis.controller.function.maptile;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 地图瓦片服务输出对象
 *
 * Created by JHy on 2019/9/19.
 */
@Data
@ApiModel(description = "地图瓦片服务响应实体")
class MapTileOutputData extends OutputData<String> {

	MapTileOutputData() {

	}

}
