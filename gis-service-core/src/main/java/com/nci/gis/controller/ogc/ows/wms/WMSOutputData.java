package com.nci.gis.controller.ogc.ows.wms;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * OGC规范-网络地图服务（WMS-Web Map Service）输出对象
 *
 * Created by JHy on 2019/9/20.
 */
@Data
@ApiModel(description = "网络地图服务响应实体")
class WMSOutputData extends OutputData<String> {

	WMSOutputData() {

	}

}
