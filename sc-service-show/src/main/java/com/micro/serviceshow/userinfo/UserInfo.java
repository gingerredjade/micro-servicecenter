package com.micro.serviceshow.userinfo;

import com.micro.serviceshow.organization.Organization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Table(name = "userinfo")    // 关联数据库中的userinfo表
@Entity                        // 标识该为一个实体
public class UserInfo {

	@Id    // 标识该属性为主键
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Integer userId;

	private String username;
	private String password;
	private Integer sex;
	private String telephone;
	private String email;
	private Integer age;
	private Date registerTime;
	private Date modifyTime;
	private Date loginTime;
	private String loginIP;
//	private String terminalType;
//	private Integer authfind;
//	private Integer authregister;
//	private Integer authaudit;
//	private Integer authrelase;
//	private Integer authadmin;

	@ManyToOne
	@JoinColumn(name = "organizationId")
	private Organization organization; //用户所属机构

	private int isClientOrg; //用户是否属于客户机构(指注册服务的机构组织)
	//用户权限 int->binary 000000|111111,0表示没有,1表示有(默认最后一位为用户管理权限)
	private int privilege;

	public UserInfo() {
	}

	public UserInfo(String username, String password, Integer sex, String telephone, String email,
					Integer age, int isClientOrg, int privilege) {
		this.username = username;
		this.password = password;
		this.sex = sex;
		this.telephone = telephone;
		this.email = email;
		this.age = age;
//		this.organization = organization;
		this.isClientOrg = isClientOrg;
		this.privilege = privilege;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public int getIsClientOrg() {
		return isClientOrg;
	}

	public void setIsClientOrg(int isClientOrg) {
		this.isClientOrg = isClientOrg;
	}

	public int getPrivilege() {
		return privilege;
	}

	public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String getLoginIP() {
		return loginIP;
	}

	public void setLoginIP(String loginIP) {
		this.loginIP = loginIP;
	}

}
