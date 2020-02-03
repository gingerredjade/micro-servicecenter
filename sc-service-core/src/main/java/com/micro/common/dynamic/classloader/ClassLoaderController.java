package com.micro.common.dynamic.classloader;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 自定义类加载器控制器
 *
 * @since 1.0.0 2019年11月18日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
@Api(tags = "JAR动态管理服务-recommend",
	value = "ClassLoaderController", description = "动态装载、卸载外部JAR模块")
@RestController
@RequestMapping(value = "/cls/loader")
@EnableAutoConfiguration
@CrossOrigin
public class ClassLoaderController {

	private static Logger _logger = LoggerFactory.getLogger(ClassLoaderController.class);

	private final ClassLoaderService classLoaderService;

	@Autowired
	public ClassLoaderController(ClassLoaderService classLoaderService) {
		this.classLoaderService = classLoaderService;
	}


	@ApiOperation(value = "执行所有上传文件的获取", notes = "获取所有已经上传的模块（jar文件）。")
	@GetMapping(value = "/getAlljars")
	public List<String> jars() {
		return classLoaderService.getAllJars();
	}


	@ApiOperation(value = "执行模块（jar）上传", notes = "上传指定模块（jar文件），重复上传会覆盖。")
	@PostMapping(value = "/uploadJar")
	public String loadJar(
		@ApiParam(name = "jarFile", required = true, value = "jar文件。")
		@RequestParam(value = "jarFile") MultipartFile jarFile) {

		return classLoaderService.uploadJarFile(jarFile);
	}


	@ApiOperation(value = "执行模块（jar）加载", notes = "加载某个指定模块（jar文件）。")
	@GetMapping(value = "/loadJar")
	public List<?> loadJar(
		@ApiParam(name = "jarName", required = true,
			value = "jar文件名称（含'.jar'后缀）。<br />" +
				"如：AppMain.jar。<br />" +
				"注意：需要包括后缀'.jar'")
		@RequestParam(value = "jarName", defaultValue = "") String jarName) {

		return classLoaderService.loadJarByName(jarName);
	}


	@ApiOperation(value = "执行模块（jar）加载", notes = "加载已上传的所有模块（jar文件）。")
	@GetMapping(value = "/loadAllJar")
	public List<?> loadJarInDir() {
		return classLoaderService.loadAllJar();
	}


	@ApiOperation(value = "执行模块（jar）卸载", notes = "卸载已加载的指定模块（jar文件）。")
	@GetMapping(value = "/unloadModule")
	public List<Map<String, Object>> unloadModule(
		@ApiParam(name = "jarName", required = true,
			value = "jar文件名称（不含'.jar'后缀）。<br />" +
				"如AppMain。<br />" +
				"注意：参数中不应包含后缀'.jar'。")
		@RequestParam(value = "jarName", defaultValue = "") String jarName) {

		return classLoaderService.unloadJarByName(jarName);
	}


	@ApiOperation(value = "执行模块（jar）卸载", notes = "卸载已上传的所有模块（jar文件）。")
	@GetMapping(value = "/unloadAllModule")
	public List<Map<String, Object>> unloadModuleIndir() {
		return classLoaderService.unloadAllJar();
	}


	@ApiOperation(value = "执行beans获取", notes = "获取所有beans，可用于检测模块（jar）是否load成功。")
	@GetMapping(value = "/getAllbeans")
	public List<Map<String, Object>> beans() {
		return classLoaderService.beans();
	}


	/* 参数太长，该方式接收不了
	@ApiOperation(value = "执行jar删除",
		notes = "删除上传的指定jar文件。")
	@DeleteMapping(value = "/delJar/{jarName}")
	public boolean delUploadedJar(
		@ApiParam(name = "jarName", value = "jar文件名列表。<br />" +
			"注意：参数中不应包含后缀'.jar'。")
		@PathVariable String jarName) {
		return classLoaderService.delJar(jarName);
	}*/

	@ApiOperation(value = "执行jar文件删除",
		notes = "删除上传的指定jar文件。")
	@DeleteMapping(value = "/delJar")
	public boolean delUploadedJar(
		@ApiParam(name = "jarName", required = true,
			value = "jar文件名列表。<br />" +
				"注意：参数中不应包含后缀'.jar'。")
		@RequestParam(value = "jarName") String jarName) {
		return classLoaderService.delJar(jarName);
	}

	@ApiOperation(value = "执行jar文件删除",
		notes = "批量删除上传的指定jar文件。")
	@PostMapping(value = "/delJars")
	public boolean delUploadedJars(
		@ApiParam(name = "jarNameList", required = true,
			value = "jar文件名列表。<br />" +
				"注意：jarNameList中的每个jarName不应含有'.jar'后缀。")
		@RequestParam(value = "jarNameList") List<String> jarNameList) {

		return classLoaderService.delJarList(jarNameList);
	}


	@ApiOperation(value = "执行jar文件删除", notes = "删除上传目录下的所有jar文件、文件。")
	@DeleteMapping(value = "/delAllJar")
	public boolean delUploadedAll() {
		return classLoaderService.deleteFileinDir();
	}


}
