package com.nci.entity;

/**
 * 定义返回的消息实体
 *
 * Created by JHy on 2019/7/23.
 */
public class RespondMessage {
    private String status;      // 状态 fail、success
    private String message;     // 消息内容

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static final String STATUS_FAILED = "failed";
	public static final String STATUS_SUCCESS = "success";
}
