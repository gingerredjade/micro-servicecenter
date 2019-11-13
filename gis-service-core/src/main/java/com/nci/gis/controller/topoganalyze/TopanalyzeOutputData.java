package com.nci.gis.controller.topoganalyze;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 地形分析服务输出对象
 *
 * Created by JHy on 2019/9/17.
 */
@Data
@ApiModel(description = "地形分析服务响应实体")
class TopanalyzeOutputData extends OutputData<String> {

	TopanalyzeOutputData() {

	}

}
