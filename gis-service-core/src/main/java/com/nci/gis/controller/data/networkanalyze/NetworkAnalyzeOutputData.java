package com.nci.gis.controller.data.networkanalyze;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 网络分析服务输出对象
 *
 * Created by JHy on 2019/9/16.
 */
@Data
@ApiModel(description = "网络分析服务响应实体")
class NetworkAnalyzeOutputData extends OutputData<String> {

	NetworkAnalyzeOutputData() {

	}

}
