package com.nci.gis.controller.catalog;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 目录服务输出对象
 *
 * Created by JHy on 2019/11/22.
 */
@Data
@ApiModel(description = "目录服务响应实体")
class CatalogOuputData extends OutputData<String> {
	CatalogOuputData() {}
}
