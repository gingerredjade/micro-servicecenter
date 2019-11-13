package com.gis.serviceshow.statistical;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.cmp.ProtectedPart;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * Created by gis on 2019/10/9.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "statistical")
@Entity
public class Subject implements Cloneable{

	@Id    // 标识该属性为主键
	@GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
	@GenericGenerator(name="native",strategy = "native")
	private Integer subjectid;				// 主键

	private String svctype;					// [*]服务类型，如gsmaptile,gsplacename
	private String svctypename;				// 服务类型名称，如'地图瓦片服务'，通过getServiceNameCN(gsmaptile)获取
	private String svcname;					// [*]服务名称（对外叫服务名称，对内是服务别名）如wordVec，可空
	private String organizationidentity;	// [*]服务机构标识，如cetc15,sm等
	private String organizationname;		// 服务机构名称，如15所,超图等,通过getProviderNameByIdentify(cetc15)接口获取
	private BigInteger subjectcount;		// 服务调用数量
	private Integer week;//周统计0
	private Integer month;//月统计1
	private Integer year;//年统计2
	private Integer countflag;//统计类型标识


	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(Integer subjectid) {
		this.subjectid = subjectid;
	}

	public String getSvctype() {
		return svctype;
	}

	public void setSvctype(String svctype) {
		this.svctype = svctype;
	}

	public String getSvctypename() {
		return svctypename;
	}

	public void setSvctypename(String svctypename) {
		this.svctypename = svctypename;
	}

	public String getSvcname() {
		return svcname;
	}

	public void setSvcname(String svcname) {
		this.svcname = svcname;
	}

	public String getOrganizationidentity() {
		return organizationidentity;
	}

	public void setOrganizationidentity(String organizationidentity) {
		this.organizationidentity = organizationidentity;
	}

	public String getOrganizationname() {
		return organizationname;
	}

	public void setOrganizationname(String organizationname) {
		this.organizationname = organizationname;
	}

	public BigInteger getSubjectcount() {
		return subjectcount;
	}

	public void setSubjectcount(BigInteger subjectcount) {
		this.subjectcount = subjectcount;
	}

	public Integer getCountflag() {
		return countflag;
	}

	public void setCountflag(Integer countflag) {
		this.countflag = countflag;
	}

	@Override
	protected  Object clone() throws CloneNotSupportedException{
		return  super.clone();
	}
}
