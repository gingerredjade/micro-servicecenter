package com.nci.gis.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 错误消息构建工具类
 *
 * @since 1.0.0 2019年11月18日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
public class ErrorUtils {

	/**
	 * 根据错误信息构建错误结果
	 *
	 * @param str 错误信息
	 * @return 返回错误结果 List<String>
	 */
	public static List<String> buildErrorList (String str) {
		List<String> err = new ArrayList<>();
		err.add(str);
		return err;
	}

	/**
	 * 根据错误信息构建错误结果
	 *
	 * @param str 错误信息
	 * @return 返回错误结果 List<Map<String, Object>>
	 */
	public static List<Map<String, Object>> buildErrorMapList (String str) {
		List<Map<String, Object>> err = new ArrayList<>();
		Map<String, Object> errMap = new HashMap<>();
		errMap.put("error", str);
		return err;
	}

	/**
	 * 根据错误信息构建错误结果
	 *
	 * @param str 错误信息
	 * @return 返回错误结果 String
	 */
	public static String buildErrorString (String str) {
		return str;
	}

}
