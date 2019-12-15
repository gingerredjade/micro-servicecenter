package com.nci.gis.controller.data.geometry;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 几何计算服务输出对象
 *
 * Created by JHy on 2019/9/16.
 */
@Data
@ApiModel(description = "几何计算服务响应实体")
class GeometryOutputData extends OutputData<String> {

	GeometryOutputData() {

	}

}
