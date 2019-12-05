package com.nci.gis.controller.query;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 空间查询服务输出对象
 *
 * Created by JHy on 2019/11/22.
 */
@Data
@ApiModel(description = "空间查询服务响应实体")
public class QueryOutputData extends OutputData<String> {

	public QueryOutputData() {}

}
