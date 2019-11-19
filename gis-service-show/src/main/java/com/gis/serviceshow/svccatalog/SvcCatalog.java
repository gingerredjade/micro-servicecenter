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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Integer svcid;                    // 主键

	private String svcname;                    // [*]服务名称（对外叫服务名称，对内是服务别名）如wordVec，可空
	private String svctype;                    // [*]服务类型，如gsmaptile,gsplacename
	private String svctypename;                // 服务类型名称，如'地图瓦片服务'，通过getServiceNameCN(gsmaptile)获取
	private String svctypenode;                // 服务子类型
	private String version;                    // 服务版本
	private String info;                    // 服务描述
	private String url;                        // 服务地址
	private String subjectClassification;    // 主题分类
	private String updateCycle;                // 更新周期
	private String usePermission;            // 使用权限 :公开,授权
	private String preview;                    // 服务预览
	private String coordinateSystem;        // 坐标系
	private String projection;                // 投影类型
	private String keyword;                    // 关键字
	private String coverage;                // 覆盖区域
	private String serviceArea;                // 服务范围

	// 新增
	private String registrantTestReport;    // 注册者测试报告
	private String testerTestReport;        // 测试人员测试报告（检验注册者自己提供的测试报告）
	private String testInfo;                // 服务测试信息（由测试人员提供）
	private int testState;                // 是否通过测试 0:不通过,1:通过

	@ManyToOne
	@JoinColumn(name = "organizationId")
	Organization organization;

	// 图层相关信息
	private String layerName;                // 图层名称
	private String layerDesc;                // 图层简介
	private String layerKeyword;            // 图层关键字
	private String layerCoverage;            // 图层覆盖区域
	private String layerServiceArea;        // 图层服务范围
	private String layerCoordinateSystem;    // 图层坐标系
	private String layerProjection;            // 图层投影类型
	private String layerUpdateCycle;        // 图层更新周期
	private String layerPreview;            // 图层预览
	private Date layerUpdateTime;            // 图层修改时间

	private Date updateTime;                // 修改时间
	private Date registerTime;                // 注册时间

	private Date releaseTime;                // 发布时间
	private Integer releaseState;            // 发布状态 0:未发布,1:已发布

	private Date auditTime;                    // 审核时间
	private Integer auditState;                // 审核状态 0:未审核,1:已审核
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String paramInfo;

	public SvcCatalog() {
	}

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

	public String getTestInfo() {
		return testInfo;
	}

	public void setTestInfo(String testInfo) {
		this.testInfo = testInfo;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	public Date getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}

	public Integer getReleaseState() {
		return releaseState;
	}

	public void setReleaseState(Integer releaseState) {
		this.releaseState = releaseState;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public Integer getAuditState() {
		return auditState;
	}

	public void setAuditState(Integer auditState) {
		this.auditState = auditState;
	}

	public String getParamInfo() {
		return paramInfo;
	}

	public void setParamInfo(String paramInfo) {
		this.paramInfo = paramInfo;
	}

	public String getRegistrantTestReport() {
		return registrantTestReport;
	}

	public void setRegistrantTestReport(String registrantTestReport) {
		this.registrantTestReport = registrantTestReport;
	}

	public String getTesterTestReport() {
		return testerTestReport;
	}

	public void setTesterTestReport(String testerTestReport) {
		this.testerTestReport = testerTestReport;
	}

	public int getTestState() {
		return testState;
	}

	public void setTestState(int testState) {
		this.testState = testState;
	}

}
