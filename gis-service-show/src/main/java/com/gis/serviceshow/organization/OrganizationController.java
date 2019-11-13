package com.gis.serviceshow.organization;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by MystFoo on 2019/10/24.
 */
@RestController
@EnableAutoConfiguration
@CrossOrigin
@RequestMapping(value = "organization")
public class OrganizationController {
	@Autowired
	OrganizationDAO organizationDAO;

	@GetMapping(value = "findAllOrganization")
	@ApiOperation("查询所有机构信息")
	public List<Organization> findAll() {
		return organizationDAO.findAll();
	}

	@GetMapping(value = "save")
	@ApiOperation("添加机构信息")
	public String save(Organization organization) {
		organizationDAO.save(organization);
		return "添加成功";
	}

}
