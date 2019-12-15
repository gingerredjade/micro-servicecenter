package com.nci.gis.controller.data.metadata;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 元数据服务输出对象
 *
 * Created by JHy on 2019/11/22.
 */
@Data
@ApiModel(description = "元数据服务响应实体")
public class MetadataOutputData extends OutputData<String> {
	MetadataOutputData() {}
}
