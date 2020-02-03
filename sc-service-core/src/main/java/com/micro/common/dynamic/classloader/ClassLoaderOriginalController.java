package com.micro.common.dynamic.classloader;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 自定义类加载器控制器
 *
 * @since 1.0.0 2019年11月12日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
@Api(tags = "JAR动态管理服务-original",
	value = "ClassLoaderController", description = "动态装载、卸载外部JAR")
//@RestController
@RequestMapping(value = "/cls/loader/original")
@CrossOrigin
public class ClassLoaderOriginalController {

	private static Logger _logger = LoggerFactory.getLogger(ClassLoaderOriginalController.class);

	private final ClassLoaderService classLoaderService;

	@Autowired
	public ClassLoaderOriginalController(ClassLoaderService classLoaderService) {
		this.classLoaderService = classLoaderService;
	}

	@ApiOperation(value = "执行beans获取", notes = "获取所有beans。")
	@GetMapping(value = "/getAllbeans")
	public List<Map<String, Object>> beans() {
		return classLoaderService.beans();
	}

	@ApiOperation(value = "执行模块（jar）卸载", notes = "卸载已加载的指定模块。")
	@GetMapping(value = "/unloadModule")
	public List<Map<String, Object>> unloadModule(
		@ApiParam(name = "moduleName", required = true,
			value = "模块（jar）名称。<br />" +
				"如加载的是：E:\\loadjar\\AppMain.jar，<br />" +
				"则卸载时需指定模块名为AppMain。")
		@RequestParam(value = "moduleName", defaultValue = "") String moduleName) {

		return classLoaderService.unloadModule(moduleName);
	}

	@ApiOperation(value = "执行模块（jar）卸载", notes = "卸载指定目录下的模块。")
	@GetMapping(value = "/unloadModuleInDir")
	public List<Map<String, Object>> unloadModuleIndir(
		@ApiParam(name = "dir", required = true,
			value = "模块（jar）存放目录。<br />" +
				"如E:\\loadjar\\。")
		@RequestParam(value = "dir", defaultValue = "") String dir) {

		return classLoaderService.unloadModuleInDir(dir);
	}

	@ApiOperation(value = "执行模块（jar）加载", notes = "加载某个指定jar包。")
	@GetMapping(value = "/loadJar")
	public List<?> loadJar(
		@ApiParam(name = "jarPath", required = true,
			value = "jar全路径。<br />" +
				"如：E:\\loadjar\\AppMain.jar。")
		@RequestParam(value = "jarPath", defaultValue = "") String jarPath) {

		return classLoaderService.loadJar(jarPath);
	}


	@ApiOperation(value = "执行模块（jar）加载", notes = "加载指定路径下的所有jar包。")
	@GetMapping(value = "/loadJarDir")
	public List<?> loadJarInDir(
		@ApiParam(name = "jarDir", required = true,
			value = "jar存放目录。<br />" +
				"如：E:\\loadjar\\。" +
				"同样支持<br />" +
				"jar全路径<br />。" +
				"如：E:\\loadjar\\AppMain.jar。")
		@RequestParam(value = "jarDir", defaultValue = "") String jarDir) {

		return classLoaderService.loadJarInDir(jarDir);
	}

	@GetMapping(value = "/invoke")
	public Object invokeBean(
		@ApiParam(name = "beanName", required = true,
			value = "调用的方法所属bean名。" +
				"如调用E:\\loadjar\\AppMain.jar中的某个接口，接口在TestLoader中，参数应为com.test.TestLoader。<br />" +
				"该接口仅供测试不提供公开使用。")
		@RequestParam(value = "beanName", defaultValue = "") String beanName) {

		Object bean = SpringContextUtil.getBean(beanName);
		Class<?> beanCls = bean.getClass();

		// 获得类的所有方法
		Method[] methods = beanCls.getMethods();

		Class<?>[] parameters = new Class<?>[1];
		Class<?> cls = "name参数".getClass();
		parameters[0] = cls;

		// 调用不含参方法
		Method method1 = ReflectionUtils.findMethod(SpringContextUtil.getBean(beanName).getClass(), "testnoparam");

		// 调用含参方法
		Method method = ReflectionUtils.findMethod(SpringContextUtil.getBean(beanName).getClass(), "testtest", String.class);
		Object result = ReflectionUtils.invokeMethod(method, SpringContextUtil.getBean(beanName), "哈哈哈哈哈哈");
		return result;
	}

}
