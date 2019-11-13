package com.gis.serviceshow.svccatalog;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by gis on 2019/10/14.
 */
@RestController
@EnableAutoConfiguration
@CrossOrigin
@RequestMapping(value = "svccstatistical")
public class SvcStatisticalController {

	@Autowired
	private  SvcCatalogDAO svcCatalogDAO;


	@GetMapping(value = "getcountbyorg")
	@ApiOperation("统计各机构服务数量")
	public List getCountByORG(){
		return svcCatalogDAO.countByORG();
	}


	@GetMapping(value = "getcountbytype")
	@ApiOperation("统计各类型服务数量")
	public List getCountByType(){
		return svcCatalogDAO.countByType();
	}

}
