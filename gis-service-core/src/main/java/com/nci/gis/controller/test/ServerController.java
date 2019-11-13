package com.nci.gis.controller.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用-服务端测试Controller
 *
 * Created by JHy on 2019/8/22.
 */
//@RestController
public class ServerController {

	@GetMapping("/msg")
	public String msg() {
		return "this is test msg.";
	}

}
