package com.micro.controller.test;

import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 输入数据模型
 *
 * Created by JHy on 2019/08/12.
 */

public class TestInputData implements Serializable {

	private static final Logger _logger = LoggerFactory.getLogger(TestInputData.class);


	@ApiModelProperty(value = "检索条件，字符串型")
	String searchInfo;

	@ApiModelProperty(value = "查询目标最大个数，正整数")
	String featureNum;

	public String getSearchInfo() {
		return searchInfo;
	}

	public void setSearchInfo(String searchInfo) {
		this.searchInfo = searchInfo;
	}

	public String getFeatureNum() {
		return featureNum;
	}

	public void setFeatureNum(String featureNum) {
		this.featureNum = featureNum;
	}


}
