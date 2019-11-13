package com.nci.gis.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通用返回值类,统一数据返回格式
 *
 * Created by JHy on 2019/9/12.
 */
@Data
@ApiModel(description = "通用响应实体")
@SuppressWarnings("serial")
public class OutputData<T> {

	@ApiModelProperty(value = "状态码，0成功，其他失败")
	private int code;

	@ApiModelProperty(value = "消息内容，成功为success，失败则描述异常消息")
	private String msg;

	@ApiModelProperty(value = "返回结果数据，通常为了应对不同返回值类型，将其声明为泛型类型")
	private T resultBody;


	public OutputData() {
	}

	public OutputData(T resultBody) {
		this.resultBody = resultBody;
	}

	public OutputData(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getResultBody() {
		return resultBody;
	}

	public void setResultBody(T resultBody) {
		this.resultBody = resultBody;
	}
}
