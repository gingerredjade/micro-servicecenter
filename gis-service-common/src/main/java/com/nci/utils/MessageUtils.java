package com.nci.utils;

import com.alibaba.fastjson.JSONObject;
import com.nci.entity.RespondMessage;


/**
 * 消息操作工具类
 *
 * Created by JHy on 2019/5/13.
 */
public class MessageUtils {

	/**
	 * 构造响应的错误信息
	 * @param status
	 * @param errMsg
	 * @return
	 */
	public static String buildErrorInfo(String status, String errMsg) {
		RespondMessage rm = new RespondMessage();
		rm.setStatus(status);
		rm.setMessage(errMsg);
		String errStr = JSONObject.toJSONString(rm);
		return errStr;
	}
}
