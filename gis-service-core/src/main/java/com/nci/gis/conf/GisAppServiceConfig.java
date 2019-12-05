package com.nci.gis.conf;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * GisAppConf
 * 读取自定义GIS应用配置文件
 * 将配置文件中配置的每一个属性的值映射到GisAppConf
 *
 * @since 1.0.0 2019年09月25日
 * @author <a href="https://gisnci.com">Hongyu Jiang</a>
 */
@Component
@Getter
@Setter
//@Data
//@EnableAutoConfiguration
//@PropertySource(value =	{"application-gisapp.yml"}, ignoreResourceNotFound = true, encoding = "utf-8")
@ConfigurationProperties(prefix = "gisapp-services")
@ToString
public class GisAppServiceConfig {

	private List<Map<String, String>> wms;
	private List<Map<String, String>> wmts;
	private List<Map<String, String>> gsplacename;
	private List<Map<String, String>> gsnetworkanalyze;
	private List<Map<String, String>> gsgeometry;
	private List<Map<String, String>> gstopanalyze;
	private List<Map<String, String>> gsmap;
	private List<Map<String, String>> gsmaptile;
	private List<Map<String, String>> gscatalog;
	private List<Map<String, String>> gsmd;
	private List<Map<String, String>> gsquery;
	private List<Map<String, String>> gsqueryfts;

	// 通用适配服务配置项
	private Map<String, String> cus;


	public List<Map<String, String>> getWms() {
		return wms;
	}

	public void setWms(List<Map<String, String>> wms) {
		this.wms = wms;
	}

	public List<Map<String, String>> getWmts() {
		return wmts;
	}

	public void setWmts(List<Map<String, String>> wmts) {
		this.wmts = wmts;
	}

	public List<Map<String, String>> getGsplacename() {
		return gsplacename;
	}

	public void setGsplacename(List<Map<String, String>> gsplacename) {
		this.gsplacename = gsplacename;
	}

	public List<Map<String, String>> getGsnetworkanalyze() {
		return gsnetworkanalyze;
	}

	public void setGsnetworkanalyze(List<Map<String, String>> gsnetworkanalyze) {
		this.gsnetworkanalyze = gsnetworkanalyze;
	}

	public List<Map<String, String>> getGsgeometry() {
		return gsgeometry;
	}

	public void setGsgeometry(List<Map<String, String>> gsgeometry) {
		this.gsgeometry = gsgeometry;
	}

	public List<Map<String, String>> getGstopanalyze() {
		return gstopanalyze;
	}

	public void setGstopanalyze(List<Map<String, String>> gstopanalyze) {
		this.gstopanalyze = gstopanalyze;
	}

	public List<Map<String, String>> getGsmap() {
		return gsmap;
	}

	public void setGsmap(List<Map<String, String>> gsmap) {
		this.gsmap = gsmap;
	}

	public List<Map<String, String>> getGsmaptile() {
		return gsmaptile;
	}

	public void setGsmaptile(List<Map<String, String>> gsmaptile) {
		this.gsmaptile = gsmaptile;
	}

	public Map<String, String> getCus() {
		return cus;
	}

	public void setCus(Map<String, String> cus) {
		this.cus = cus;
	}

	public List<Map<String, String>> getGscatalog() {
		return gscatalog;
	}

	public void setGscatalog(List<Map<String, String>> gscatalog) {
		this.gscatalog = gscatalog;
	}

	public List<Map<String, String>> getGsmd() {
		return gsmd;
	}

	public void setGsmd(List<Map<String, String>> gsmd) {
		this.gsmd = gsmd;
	}

	public List<Map<String, String>> getGsquery() {
		return gsquery;
	}

	public void setGsquery(List<Map<String, String>> gsquery) {
		this.gsquery = gsquery;
	}

	public List<Map<String, String>> getGsqueryfts() {
		return gsqueryfts;
	}

	public void setGsqueryfts(List<Map<String, String>> gsqueryfts) {
		this.gsqueryfts = gsqueryfts;
	}
}
