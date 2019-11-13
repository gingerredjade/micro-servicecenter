package com.nci.gis.controller.ogc.ows.wmts;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * OGC规范-网络地图瓦片服务（WMTS-Web Map Tile Service）输出对象
 *
 * Created by JHy on 2019/9/24.
 */
@Data
@ApiModel(description = "网络地图瓦片服务响应实体")
class WMTSOutputData extends OutputData<String> {
	WMTSOutputData() {}
}
