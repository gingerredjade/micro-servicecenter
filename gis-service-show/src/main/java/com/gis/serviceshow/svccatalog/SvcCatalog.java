package com.gis.serviceshow.svccatalog;

import com.gis.serviceshow.organization.Organization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by gis on 2019/10/9.
 */
@Getter
@Setter
@AllArgsConstructor
@Table(name = "svccatalog")
@Entity
public class SvcCatalog {

	@Id    // 标识该属性为主键
	@GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
	@GenericGenerator(name="native",strategy = "native")
	private Integer svcid;					// 主键

	private String svcname;					// [*]服务名称（对外叫服务名称，对内是服务别名）如wordVec，可空
	private String svctype;					// [*]服务类型，如gsmaptile,gsplacename
	private String svctypename;				// 服务类型名称，如'地图瓦片服务'，通过getServiceNameCN(gsmaptile)获取
	private String svctypenode;				// 服务子类型
	private String version;					// 服务版本
	private String info;					// 服务描述
	private String url;						// 服务地址
//	private String organizationidentity;	// [*]服务机构标识，如cetc15,sm等
//	private String organizationname;		// 服务机构名称，如15所,超图等,通过getProviderNameByIdentify(cetc15)接口获取
//	private String address;					// 服务提供机构地址
//	private String tel;						// 服务提供机构联系方式
	private String testinfo;				// 服务测试信息
	private String subjectClassification;	// 主题分类
	private String updateCycle;				// 更新周期
	private String usePermission;			// 使用权限 :公开,授权
	private String preview;					// 服务预览
	private String coordinateSystem;		// 坐标系
	private String projection;				// 投影类型
	private String keyword;					// 关键字
	private String coverage;				// 覆盖区域
	private String serviceArea;				// 服务范围

	@ManyToOne
	@JoinColumn(name = "organizationId")
	Organization organization;

	// 图层相关信息
	private String layerName;				// 图层名称
	private String layerDesc;				// 图层简介
	private String layerKeyword;			// 图层关键字
	private String layerCoverage;			// 图层覆盖区域
	private String layerServiceArea;		// 图层服务范围
	private String layerCoordinateSystem;	// 图层坐标系
	private String layerProjection;			// 图层投影类型
	private String layerUpdateCycle;		// 图层更新周期
	private String layerPreview;			// 图层预览
	private Date layerUpdateTime;			// 图层修改时间

	private Date updateTime;				// 修改时间
	private Date registertime;				// 注册时间

	private Date releaseTime;				// 发布时间
	private Integer releasestate;			// 发布状态 0:未发布,1:已发布

	private Date audittime;					// 审核时间
	private Integer auditstate;				// 审核状态 0:未审核,1:已审核
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String paraminfo;

	public SvcCatalog(){}

	public Integer getSvcid() {
		return svcid;
	}

	public void setSvcid(Integer svcid) {
		this.svcid = svcid;
	}

	public String getSvcname() {
		return svcname;
	}

	public void setSvcname(String svcname) {
		this.svcname = svcname;
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

	public String getSvctypenode() {
		return svctypenode;
	}

	public void setSvctypenode(String svctypenode) {
		this.svctypenode = svctypenode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSubjectClassification() {
		return subjectClassification;
	}

	public void setSubjectClassification(String subjectClassification) {
		this.subjectClassification = subjectClassification;
	}

	public String getUpdateCycle() {
		return updateCycle;
	}

	public void setUpdateCycle(String updateCycle) {
		this.updateCycle = updateCycle;
	}

	public String getUsePermission() {
		return usePermission;
	}

	public void setUsePermission(String usePermission) {
		this.usePermission = usePermission;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getCoordinateSystem() {
		return coordinateSystem;
	}

	public void setCoordinateSystem(String coordinateSystem) {
		this.coordinateSystem = coordinateSystem;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	public String getServiceArea() {
		return serviceArea;
	}

	public void setServiceArea(String serviceArea) {
		this.serviceArea = serviceArea;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getLayerDesc() {
		return layerDesc;
	}

	public void setLayerDesc(String layerDesc) {
		this.layerDesc = layerDesc;
	}

	public String getLayerKeyword() {
		return layerKeyword;
	}

	public void setLayerKeyword(String layerKeyword) {
		this.layerKeyword = layerKeyword;
	}

	public String getLayerCoverage() {
		return layerCoverage;
	}

	public void setLayerCoverage(String layerCoverage) {
		this.layerCoverage = layerCoverage;
	}

	public String getLayerServiceArea() {
		return layerServiceArea;
	}

	public void setLayerServiceArea(String layerServiceArea) {
		this.layerServiceArea = layerServiceArea;
	}

	public String getLayerCoordinateSystem() {
		return layerCoordinateSystem;
	}

	public void setLayerCoordinateSystem(String layerCoordinateSystem) {
		this.layerCoordinateSystem = layerCoordinateSystem;
	}

	public String getLayerProjection() {
		return layerProjection;
	}

	public void setLayerProjection(String layerProjection) {
		this.layerProjection = layerProjection;
	}

	public Date getLayerUpdateTime() {
		return layerUpdateTime;
	}

	public void setLayerUpdateTime(Date layerUpdateTime) {
		this.layerUpdateTime = layerUpdateTime;
	}

	public String getLayerUpdateCycle() {
		return layerUpdateCycle;
	}

	public void setLayerUpdateCycle(String layerUpdateCycle) {
		this.layerUpdateCycle = layerUpdateCycle;
	}

	public String getLayerPreview() {
		return layerPreview;
	}

	public void setLayerPreview(String layerPreview) {
		this.layerPreview = layerPreview;
	}

	public String getTestinfo() {
		return testinfo;
	}

	public void setTestinfo(String testinfo) {
		this.testinfo = testinfo;
	}

	public Date getRegistertime() {
		return registertime;
	}

	public void setRegistertime(Date registertime) {
		this.registertime = registertime;
	}

	public Date getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}

	public Integer getReleasestate() {
		return releasestate;
	}

	public void setReleasestate(Integer releasestate) {
		this.releasestate = releasestate;
	}

	public Date getAudittime() {
		return audittime;
	}

	public void setAudittime(Date audittime) {
		this.audittime = audittime;
	}

	public Integer getAuditstate() {
		return auditstate;
	}

	public void setAuditstate(Integer auditstate) {
		this.auditstate = auditstate;
	}

	public String getParaminfo() {
		return paraminfo;
	}

	public void setParaminfo(String paraminfo) {
		this.paraminfo = paraminfo;
	}
}
