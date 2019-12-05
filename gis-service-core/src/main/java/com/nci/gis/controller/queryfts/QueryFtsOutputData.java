package com.nci.gis.controller.queryfts;

import com.nci.gis.common.entity.OutputData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 空间查询服务输出对象（全文检索ES）
 *
 * Created by JHy on 2019/11/22.
 */
@Data
@ApiModel(description = "空间查询服务响应实体")
public class QueryFtsOutputData extends OutputData<String> {

	public QueryFtsOutputData() {}

}
