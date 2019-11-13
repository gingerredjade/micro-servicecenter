package com.nci.entity;

/**
 * 定义返回的消息实体
 *
 * Created by JHy on 2019/7/23.
 */
public class ReturnMessage {

	private int code;       // 状态码 0成功、其他失败
	private String msg;     // 消息内容
	private Object data;	// 数据

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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
