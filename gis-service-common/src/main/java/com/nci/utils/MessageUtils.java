package com.nci.utils;

import com.alibaba.fastjson.JSONObject;
import com.nci.entity.RespondMessage;
import com.nci.entity.ReturnMessage;


/**
 * 消息操作工具类
 *
 * Created by JHy on 2019/5/13.
 */
public class MessageUtils {

	/**
	 * 构造响应信息
	 *
	 * @param status 状态码
	 * @param msg 消息
	 * @return 返回值RespondMessage.toJSON
	 */
	public static String buildInfo(String status, String msg) {
		RespondMessage rm = new RespondMessage();
		rm.setStatus(status);
		rm.setMessage(msg);
		return JSONObject.toJSONString(rm);
	}

	/**
	 * 构造响应信息
	 *
	 * @param code 编码
	 * @param msg 消息
	 * @param data 数据
	 * @return 返回值ReturnMessage.toJSON
	 */
	public static String buildInfo(int code, String msg, Object data) {
		ReturnMessage rm = new ReturnMessage();
		rm.setCode(code);
		rm.setMsg(msg);
		rm.setData(data);
		return JSONObject.toJSONString(rm);
	}
}
