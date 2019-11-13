package com.gis.serviceshow.userinfo;

import com.gis.serviceshow.organization.Organization;
import com.gis.serviceshow.organization.OrganizationDAO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@EnableAutoConfiguration
@CrossOrigin
@RequestMapping(value = "userinfo")
public class UserInfoController {

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Autowired
	private OrganizationDAO organizationDAO;

	@GetMapping(value = "login")
	@ApiOperation("登录")
	public UserInfo login(String username, String password) {
		UserInfo ui = userInfoRepository.findByNameAndPassword(username, password);
		if (ui != null) {
			//记录登录时间和IP
			ui.setLoginTime(new Date());
			ui.setLoginIP("196.128.55.111");//暂时写死
			userInfoRepository.save(ui);
			return ui;
		}
		return null;
	}

	@GetMapping(value = "findAllUserInfo")
	@ApiOperation("查询所有用户信息")
	public Page<UserInfo> findAll(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page - 1, size);
		return userInfoRepository.findAll(pageRequest);
	}

	@GetMapping(value = "getUserInfo")
	@ApiOperation("通过用户ID查找")
	public UserInfo getUserInfo(Integer userId) {
		Optional<UserInfo> userInfoOptional = userInfoRepository.findById(userId);
		if (userInfoOptional.isPresent()) {
			return userInfoOptional.get();
		}
		return null;
	}

	@GetMapping(value = "findUserWithCondition")
	@ApiOperation("条件分页查询用户")
	public Page<UserInfo> findWithConditions(
		@ApiParam(name = "page", value = "页码")
		@RequestParam(value = "page", required = true) int page,
		@ApiParam(name = "size", value = "页面容量")
		@RequestParam(value = "size", required = true) int size,
		@ApiParam(name = "username", value = "用户名")
		@RequestParam(value = "username", required = false) String username

	) {
		PageRequest pageRequest = PageRequest.of(page - 1, size);
		if (username == null) {
			username = "";
		}
		return userInfoRepository.findWithCondition(username, pageRequest);
	}

	@GetMapping(value = "getByNameAndPwd")
	@ApiOperation("通过用户名密码查找")
	public UserInfo getUserInfo(String username, String pwd) {
		return userInfoRepository.findByNameAndPassword(username, pwd);
	}

	@GetMapping(value = "del")
	@ApiOperation("删除")
	public String delUserInfoById(int[] userIds) {
		List deleteFailedId = new ArrayList();
		for (int userId : userIds) {
			if (!userInfoRepository.findById(userId).isPresent()) {
				deleteFailedId.add(userId);
			} else {
				userInfoRepository.deleteById(userId);
			}
		}
		if (deleteFailedId.size() == 0) {
			return "删除成功";
		} else {
			return "用户ID:" + StringUtils.join(deleteFailedId.toArray(), ",") + "删除失败";
		}

	}


	/*@GetMapping(value = "save")
	@ApiOperation("注册")
	public String save(@RequestParam(value = "adminname", required = true) String adminname,
					   @RequestParam(value = "username", required = true) String username,
					   @RequestParam(value = "password", required = true) String password,
					   @RequestParam(value = "authfind", required = true) Integer authfind,
					   @RequestParam(value = "authregister", required = true) Integer authregister,
					   @RequestParam(value = "authaudit", required = true) Integer authaudit,
					   @RequestParam(value = "authrelase", required = true) Integer authrelase,
					   @RequestParam(value = "authadmin", required = true) Integer authadmin) {
		UserInfo adminui = userInfoRepository.findByName(adminname);
		if (adminui.getAuthadmin() != 1) {
			return "权限不足";
		}
		UserInfo ui = userInfoRepository.findByName(username);
		if (ui != null) {
			return "已经存在该用户名";
		} else {
			ui = new UserInfo();
		}
		ui.setUsername(username);
		ui.setPassword(password);
		ui.setAuthfind(authfind);
		ui.setAuthregister(authregister);
		ui.setAuthaudit(authaudit);
		ui.setAuthrelase(authrelase);
		ui.setAuthadmin(authadmin);
		userInfoRepository.save(ui);
		return "创建用户成功";
	}*/

	@GetMapping(value = "save")
	@ApiOperation("添加用户")
	public String save(@RequestParam(value = "username") String userName,
					   @RequestParam(value = "password") String password,
					   @RequestParam(value = "sex") int sex,
					   @RequestParam(value = "telephone") String telephone,
					   @RequestParam(value = "email", required = false) String email,
					   @RequestParam(value = "age") int age,
					   @RequestParam(value = "isClientOrg") int isClientOrg,
					   @RequestParam(value = "privilege") int privilege,
					   @RequestParam(value = "organizationId") int organizationId) {

		if (userName == null) {
			return "创建失败,用户名为空";
		}
		UserInfo userInfo1 = userInfoRepository.findByName(userName);
		if (userInfo1 != null) {
			return "创建失败,用户名已存在";
		}

		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(userName);
		userInfo.setPassword(password);
		userInfo.setSex(sex);
		userInfo.setTelephone(telephone);
		userInfo.setAge(age);
		userInfo.setIsClientOrg(isClientOrg);
		userInfo.setPrivilege(privilege);
		userInfo.setOrganization(organizationDAO.getOne(organizationId));
		userInfo.setRegisterTime(new Date());
		userInfo.setModifyTime(new Date());

		userInfoRepository.save(userInfo);
		return "创建用户成功";
	}

	/*@GetMapping(value = "save")
	@ApiOperation("添加用户")
	public String save(@RequestParam(value = "userInfo") UserInfo userInfo) {

		if (userInfo.getUsername() == null) {
			return "创建失败,用户名为空";
		}

		userInfo.setRegisterTime(new Date());
		userInfo.setModifyTime(new Date());

		userInfoRepository.save(userInfo);
		return "创建用户成功";
	}*/

	/*@GetMapping(value = "update")
	@ApiOperation("修改权限")
	public String update(@RequestParam(value = "username", required = true) String username,
						 @RequestParam(value = "userid", required = true) Integer userid,
						 @RequestParam(value = "password", required = true) String password,
						 @RequestParam(value = "authfind", required = true) Integer authfind,
						 @RequestParam(value = "authregister", required = true) Integer authregister,
						 @RequestParam(value = "authaudit", required = true) Integer authaudit,
						 @RequestParam(value = "authrelase", required = true) Integer authrelase,
						 @RequestParam(value = "authadmin", required = true) Integer authadmin) {
		Optional<UserInfo> userInfoOptional = userInfoRepository.findById(userid);
		if (userInfoOptional.get() == null) {
			return "要修改的用户不存在";
		}
		UserInfo adminui = userInfoRepository.findByName(username);
		if (adminui.getAuthadmin() != 1 || userInfoOptional.get().getAuthadmin() == 1) {
			return "权限不足";
		}
		UserInfo ui = userInfoOptional.get();
		ui.setPassword(password);
		ui.setAuthfind(authfind);
		ui.setAuthregister(authregister);
		ui.setAuthaudit(authaudit);
		ui.setAuthrelase(authrelase);
		ui.setAuthadmin(authadmin);
		userInfoRepository.save(ui);
		return "修改用户成功";
	}*/

	@GetMapping(value = "update")
	@ApiOperation("修改权限")
	public String update(@RequestParam(value = "userInfo") UserInfo userInfo) {
		Optional<UserInfo> userInfoOptional = userInfoRepository.findById(userInfo.getUserId());
		if (userInfoOptional.get() == null) {
			return "要修改的用户不存在";
		}
		userInfo.setModifyTime(new Date());
		userInfoRepository.save(userInfo);
		return "修改用户成功";
	}

}
