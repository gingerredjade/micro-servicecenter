package com.micro.serviceshow.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by MystFoo on 2019/10/24.
 */
@Getter
@Setter
@AllArgsConstructor
@Table(name = "organization")                    //组织机构表
@Entity
public class Organization {

	@Id    // 标识该属性为主键
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private int organizationId;                    //组织机构ID

	private String organizationName;            //组织机构名称
	private String organizationAddress;            //组织机构地点
	private String organizationWebsite;            //组织机构网站
	private String organizationContact;            //组织机构联系人
	private String organizationPhoneNumber;        //组织机构联系电话
	private String organizationEmail;            //组织机构联系邮箱
	private String specialIdentity;         //组织特殊标识

	public Organization() {
	}

	public Organization(String organizationName, String organizationAddress, String organizationWebsite, String organizationContact,
						String organizationPhoneNumber, String organizationEmail, String specialIdentity) {
		this.organizationName = organizationName;
		this.organizationAddress = organizationAddress;
		this.organizationWebsite = organizationWebsite;
		this.organizationContact = organizationContact;
		this.organizationPhoneNumber = organizationPhoneNumber;
		this.organizationEmail = organizationEmail;
		this.specialIdentity = specialIdentity;
	}


	public int getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationAddress() {
		return organizationAddress;
	}

	public void setOrganizationAddress(String organizationAddress) {
		this.organizationAddress = organizationAddress;
	}

	public String getOrganizationWebsite() {
		return organizationWebsite;
	}

	public void setOrganizationWebsite(String organizationWebsite) {
		this.organizationWebsite = organizationWebsite;
	}

	public String getOrganizationContact() {
		return organizationContact;
	}

	public void setOrganizationContact(String organizationContact) {
		this.organizationContact = organizationContact;
	}

	public String getOrganizationPhoneNumber() {
		return organizationPhoneNumber;
	}

	public void setOrganizationPhoneNumber(String organizationPhoneNumber) {
		this.organizationPhoneNumber = organizationPhoneNumber;
	}

	public String getOrganizationEmail() {
		return organizationEmail;
	}

	public void setOrganizationEmail(String organizationEmail) {
		this.organizationEmail = organizationEmail;
	}

	public String getSpecialIdentity() {
		return specialIdentity;
	}

	public void setSpecialIdentity(String specialIdentity) {
		this.specialIdentity = specialIdentity;
	}

}
