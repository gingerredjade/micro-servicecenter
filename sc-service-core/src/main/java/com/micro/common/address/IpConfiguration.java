package com.micro.common.address;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 获取当前项目的IP、PORT的配置类
 *
 * Created by JHy on 2019/8/12.
 */
@Component
public class IpConfiguration implements ApplicationListener<WebServerInitializedEvent> {

	private int serverPort;

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {
		this.serverPort = event.getWebServer().getPort();
	}

	public int getPort() {
		return this.serverPort;
	}


}
