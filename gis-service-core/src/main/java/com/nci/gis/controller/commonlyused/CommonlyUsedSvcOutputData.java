package com.nci.gis.controller.commonlyused;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 通用适配服务错误信息输出对象
 *
 * Created by JHy on 2019/11/06.
 */
@Data
@ApiModel(description = "通用适配服务错误响应实体")
public class CommonlyUsedSvcOutputData extends OutputData<String> {

	CommonlyUsedSvcOutputData() {

	}

}
