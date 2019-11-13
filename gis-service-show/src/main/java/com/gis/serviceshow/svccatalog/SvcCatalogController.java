package com.gis.serviceshow.svccatalog;

import com.gis.serviceshow.organization.Organization;
import com.gis.serviceshow.organization.OrganizationDAO;
import com.gis.serviceshow.util.UploadFileUtil;
import com.nci.constants.ServiceTypes.ServiceTypeDef;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Created by gis on 2019/10/9.
 */
@RestController
@EnableAutoConfiguration
@CrossOrigin
@RequestMapping(value = "svccatalog")
public class SvcCatalogController {

	@Autowired
	private SvcCatalogDAO svcCatalogDAO;

	@Autowired
	private OrganizationDAO organizationDAO;

	@Value(value = "${preread.upload-path}")
	String uploadPath;
	@Value(value = "${preread.layer-upload-path}")
	String layerUploadPath;

//	@GetMapping(value = "findnotaudit")
//	@ApiOperation("查询未审核服务")
//	public Page<SvcCatalog> findNotAudit(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
//										 @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
//										 @RequestParam(value = "organizationidentity", required = false) String organizationidentity) {
//		return findAuditFromDao(pageNumber, pageSize, organizationidentity, "0");
//	}
//
//	@GetMapping(value = "findaudit")
//	@ApiOperation("查询已审核服务")
//	public Page<SvcCatalog> findAudit(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
//									  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
//									  @RequestParam(value = "organizationidentity", required = false) String organizationidentity) {
//		return findAuditFromDao(pageNumber, pageSize, organizationidentity, "1");
//	}
//
//	@GetMapping(value = "findnotrelase")
//	@ApiOperation("查询未发布服务")
//	public Page<SvcCatalog> findNotReleas(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
//										  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
//										  @RequestParam(value = "organizationidentity", required = false) String organizationidentity) {
//		return findReleaseFromDao(pageNumber, pageSize, organizationidentity, "0");
//	}
//
//	@GetMapping(value = "findrelease")
//	@ApiOperation("查询已发布服务")
//	public Page<SvcCatalog> findReleas(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
//									   @RequestParam(value = "size", required = false, defaultValue = "10") int size,
//									   @RequestParam(value = "organizationidentity", required = false) String organizationidentity) {
//		return findReleaseFromDao(page-1, size, organizationidentity, "1");
//	}

	@GetMapping(value = "findRegisterCount")
	@ApiOperation("查询已注册服务数量")
	public int findRegisterCount() {
		return svcCatalogDAO.findAll().size();
	}

	@GetMapping(value = "findAuditCount")
	@ApiOperation("查询已审核服务数量")
	public int findAuditCount() {
		return svcCatalogDAO.findAudit(1).size();
	}

	@GetMapping(value = "findNotAuditCount")
	@ApiOperation("查询未审核服务数量")
	public int findNotAuditCount() {
		return svcCatalogDAO.findAudit(0).size();
	}

	@GetMapping(value = "findReleaseCount")
	@ApiOperation("查询已发布服务数量")
	public int findReleaseCount() {
		return svcCatalogDAO.findRelease(1).size();
	}

	@GetMapping(value = "findNotReleaseCount")
	@ApiOperation("查询未发布服务数量")
	public int findNotReleaseCount() {
		return svcCatalogDAO.findRelease(0).size();
	}

	@GetMapping(value = "findAllByPage")
	@ApiOperation("分页查询")
	public Page<SvcCatalog> findAll(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page - 1, size);
		return svcCatalogDAO.findAll(pageRequest);
	}

	@GetMapping(value = "findWithConditions")
	@ApiOperation("条件分页查询待审核服务")
	public Page<SvcCatalog> findWithConditions(
		@ApiParam(name = "page", value = "页码")
		@RequestParam(value = "page", required = true) int page,
		@ApiParam(name = "size", value = "页面容量")
		@RequestParam(value = "size", required = true) int size,
		@ApiParam(name = "svctypename", value = "服务类型名称")
		@RequestParam(value = "svctypename", required = false) String svctypename,
		@ApiParam(name = "svctype", value = "服务类型")
		@RequestParam(value = "svctype", required = false) String svctype,
		@ApiParam(name = "info", value = "服务描述")
		@RequestParam(value = "info", required = false) String info,
		@ApiParam(name = "organizationId", value = "服务机构ID")
		@RequestParam(value = "organizationId", required = false) Integer organizationId
	) {
		PageRequest pageRequest = PageRequest.of(page - 1, size);
		if (svctypename == null) {
			svctypename = "";
		}
		if (svctype == null) {
			svctype = "";
		}
		if (info == null) {
			info = "";
		}
		if (organizationId == null) {
			return svcCatalogDAO.findWithConditionAllOrg(svctypename, svctype, info, pageRequest);
		}
		return svcCatalogDAO.findWithCondition(organizationId, svctypename, svctype, info, pageRequest);
	}

	@GetMapping(value = "findWithConditionsAudited")
	@ApiOperation("条件分页查询审核通过的服务")
	public Page<SvcCatalog> findWithConditionsAudited(
		@ApiParam(name = "page", value = "页码")
		@RequestParam(value = "page", required = true) int page,
		@ApiParam(name = "size", value = "页面容量")
		@RequestParam(value = "size", required = true) int size,
		@ApiParam(name = "svctypename", value = "服务类型名称")
		@RequestParam(value = "svctypename", required = false) String svctypename,
		@ApiParam(name = "svctype", value = "服务类型")
		@RequestParam(value = "svctype", required = false) String svctype,
		@ApiParam(name = "info", value = "服务描述")
		@RequestParam(value = "info", required = false) String info,
		@ApiParam(name = "organizationId", value = "服务机构ID")
		@RequestParam(value = "organizationId", required = false) Integer organizationId
	) {
		PageRequest pageRequest = PageRequest.of(page - 1, size);
		if (svctypename == null) {
			svctypename = "";
		}
		if (svctype == null) {
			svctype = "";
		}
		if (info == null) {
			info = "";
		}
		if (organizationId == null) {
			return svcCatalogDAO.findWithConditionAllOrgAudited(svctypename, svctype, info, pageRequest);
		}
		return svcCatalogDAO.findWithConditionAudited(organizationId, svctypename, svctype, info, pageRequest);
	}

	@PostMapping(value = "register")
	@ApiOperation("注册")
	public String save(
		@ApiParam(name = "svcname", value = "服务名称")
		@RequestParam(value = "svcname", required = false) String svcname,
		@ApiParam(name = "svctype", value = "服务类型*")
		@RequestParam(value = "svctype", required = true) String svctype,
		@ApiParam(name = "svctypenode", value = "服务子类型")
		@RequestParam(value = "svctypenode", required = false) String svctypenode,
		@ApiParam(name = "version", value = "服务版本")
		@RequestParam(value = "version", required = false) String version,
		@ApiParam(name = "info", value = "服务描述")
		@RequestParam(value = "info", required = false) String info,
		@ApiParam(name = "url", value = "服务地址*")
		@RequestParam(value = "url", required = true) String url,
		@ApiParam(name = "organizationId", value = "服务机构标识*")
		@RequestParam(value = "organizationId", required = true) int organizationId,
		@ApiParam(name = "testinfo", value = "服务测试信息")
		@RequestParam(value = "testinfo", required = false) String testinfo,
		@ApiParam(name = "paraminfo", value = "参数描述信息")
		@RequestParam(value = "paraminfo", required = false) String paraminfo,
		@ApiParam(name = "keyword", value = "关键字")
		@RequestParam(value = "keyword", required = false) String keyword,
		@ApiParam(name = "coverage", value = "覆盖区域")
		@RequestParam(value = "coverage", required = false) String coverage,
		@ApiParam(name = "serviceArea", value = "服务范围")
		@RequestParam(value = "serviceArea", required = false) String serviceArea,
		@ApiParam(name = "coordinateSystem", value = "坐标系")
		@RequestParam(value = "coordinateSystem", required = false) String coordinateSystem,
		@ApiParam(name = "projection", value = "投影类型")
		@RequestParam(value = "projection", required = false) String projection,
		@ApiParam(name = "updateCycle", value = "更新周期")
		@RequestParam(value = "updateCycle", required = false) String updateCycle,
		@ApiParam(name = "subjectClassification", value = "主题分类")
		@RequestParam(value = "subjectClassification", required = false) String subjectClassification,
		@ApiParam(name = "preview", value = "服务预览")
		@RequestParam(value = "preview", required = false) MultipartFile previewFile,
		//图层相关参数
		@ApiParam(name = "layerName", value = "图层名称")
		@RequestParam(value = "layerName", required = false) String layerName,
		@ApiParam(name = "layerDesc", value = "图层简介")
		@RequestParam(value = "layerDesc", required = false) String layerDesc,
		@ApiParam(name = "layerKeyword", value = "图层关键字")
		@RequestParam(value = "layerKeyword", required = false) String layerKeyword,
		@ApiParam(name = "layerCoverage", value = "图层覆盖区域")
		@RequestParam(value = "layerCoverage", required = false) String layerCoverage,
		@ApiParam(name = "layerServiceArea", value = "图层服务范围")
		@RequestParam(value = "layerServiceArea", required = false) String layerServiceArea,
		@ApiParam(name = "layerCoordinateSystem", value = "图层坐标系")
		@RequestParam(value = "layerCoordinateSystem", required = false) String layerCoordinateSystem,
		@ApiParam(name = "layerProjection", value = "图层投影类型")
		@RequestParam(value = "layerProjection", required = false) String layerProjection,
		@ApiParam(name = "layerUpdateCycle", value = "图层更新周期")
		@RequestParam(value = "layerUpdateCycle", required = false) String layerUpdateCycle,
		@ApiParam(name = "layerPreviewFile", value = "图层预览")
		@RequestParam(value = "layerPreviewFile", required = false) MultipartFile layerPreviewFile
	) {
		SvcCatalog svcCatalog = new SvcCatalog();
		String svctypename = ServiceTypeDef.getServiceNameCN(svctype);
		Organization organization = organizationDAO.findById(organizationId).get();

//		String organizationname = ProviderDef.getProviderNameByIdentify(organizationidentity);

		if (svcname == null) {
			svcname = "";
		}
		if (svctypenode == null) {
			svctypenode = "";
		}
		if (svctypename == null || organization.getOrganizationName() == null) {
			return "服务类型或者服务机构不存在";
		}
		SvcCatalog svcCatalogs = svcCatalogDAO.checkSvc(svctype, organization.getSpecialIdentity(), svcname, svctypenode);
		if (svcCatalogs != null) {
			return "已经存在该服务";
		}
		if (version == null) {
			version = "";
		}
		if (info == null) {
			info = "";
		}
		if (testinfo == null) {
			testinfo = "";
		}
		if (paraminfo == null) {
			paraminfo = "";
		}
		if (keyword == null) {
			keyword = "";
		}
		if (coverage == null) {
			coverage = "";
		}
		if (serviceArea == null) {
			serviceArea = "";
		}
		if (coordinateSystem == null) {
			coordinateSystem = "";
		}
		if (projection == null) {
			projection = "";
		}
		if (updateCycle == null) {
			updateCycle = "";
		}
		if (subjectClassification == null) {
			subjectClassification = "";
		}
		svcCatalog.setSvcname(svcname);
		svcCatalog.setSvctype(svctype);
		svcCatalog.setSvctypenode(svctypenode);
		svcCatalog.setSvctypename(svctypename);
		svcCatalog.setOrganization(organization);
		svcCatalog.setInfo(info);
		svcCatalog.setUrl(url);
		svcCatalog.setVersion(version);
		svcCatalog.setRegistertime(new Date());
		svcCatalog.setTestinfo(testinfo);
		svcCatalog.setReleasestate(0);
		svcCatalog.setAuditstate(0);
		svcCatalog.setParaminfo(paraminfo);
		svcCatalog.setKeyword(keyword);
		svcCatalog.setCoverage(coverage);
		svcCatalog.setServiceArea(serviceArea);
		svcCatalog.setCoordinateSystem(coordinateSystem);
		svcCatalog.setProjection(projection);
		svcCatalog.setUpdateCycle(updateCycle);
		svcCatalog.setSubjectClassification(subjectClassification);
		svcCatalog.setUpdateTime(new Date());
		//处理文件上传,服务预览上传
		String preview;
		if (layerPreviewFile == null) {
			preview = "";
		} else {
			String fileName = previewFile.getOriginalFilename();
			fileName = UploadFileUtil.createUUID(fileName);
			try {
				UploadFileUtil.uploadFile(previewFile.getBytes(), uploadPath, fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			preview = "/preview/" + fileName;
		}
		svcCatalog.setPreview(preview);


		// 新增,图层相关,可能没有图层信息
		if (layerName == null) {
			layerName = "";
		}
		if (layerDesc == null) {
			layerDesc = "";
		}
		if (layerKeyword == null) {
			layerKeyword = "";
		}
		if (layerCoverage == null) {
			layerCoverage = "";
		}
		if (layerServiceArea == null) {
			layerServiceArea = "";
		}
		if (layerCoordinateSystem == null) {
			layerCoordinateSystem = "";
		}
		if (layerProjection == null) {
			layerProjection = "";
		}
		if (layerUpdateCycle == null) {
			layerUpdateCycle = "";
		}
		svcCatalog.setLayerName(layerName);
		svcCatalog.setLayerDesc(layerDesc);
		svcCatalog.setLayerKeyword(layerKeyword);
		svcCatalog.setLayerCoverage(layerCoverage);
		svcCatalog.setLayerServiceArea(layerServiceArea);
		svcCatalog.setLayerCoordinateSystem(layerCoordinateSystem);
		svcCatalog.setLayerProjection(layerProjection);
		svcCatalog.setLayerUpdateCycle(layerUpdateCycle);
		svcCatalog.setLayerUpdateTime(new Date());
		//处理文件上传
		String layerPreview;
		if (layerPreviewFile == null) {
			layerPreview = "";
		} else {
			String fileName = layerPreviewFile.getOriginalFilename();
			fileName = UploadFileUtil.createUUID(fileName);
			try {
				UploadFileUtil.uploadFile(layerPreviewFile.getBytes(), layerUploadPath, fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			layerPreview = "/layer-preview/" + fileName;
		}
		svcCatalog.setLayerPreview(layerPreview);

		try {
			svcCatalogDAO.save(svcCatalog);
			return "注册成功";
		} catch (Exception e) {
			e.printStackTrace();
			return "注册失败";
		}
	}


	@GetMapping(value = "delete")
	@ApiOperation("删除")
	public String delect(Integer id) {
		try {
			svcCatalogDAO.deleteById(id);
			return "删除成功";
		} catch (Exception e) {
			e.printStackTrace();
			return "删除失败";
		}
	}

	@GetMapping(value = "release")
	@ApiOperation("发布")
	public String release(@ApiParam(name = "svcid", value = "服务id")
						  @RequestParam(value = "svcid") Integer svcid) {
		try {
			Optional<SvcCatalog> svcCatalog = svcCatalogDAO.findById(svcid);
			if (svcCatalog.isPresent()) {
				SvcCatalog svcCatalog1 = svcCatalog.get();
				svcCatalog1.setReleasestate(1);
				svcCatalog1.setReleaseTime(new Date());
				svcCatalogDAO.save(svcCatalog1);
			}
			return "发布成功";
		} catch (Exception e) {
			e.printStackTrace();
			return "发布失败";
		}
	}

	@GetMapping(value = "audit")
	@ApiOperation("审核")
	public String audit(@ApiParam(name = "svcid", value = "服务id")
						@RequestParam(value = "svcid") Integer svcid) {
		try {
			Optional<SvcCatalog> svcCatalog = svcCatalogDAO.findById(svcid);
			if (svcCatalog.isPresent()) {
				SvcCatalog svcCatalog1 = svcCatalog.get();
				svcCatalog1.setAuditstate(1);
				svcCatalog1.setAudittime(new Date());
				svcCatalogDAO.save(svcCatalog1);
			}
			return "审核成功";
		} catch (Exception e) {
			e.printStackTrace();
			return "审核失败";
		}
	}

	@GetMapping(value = "findbytype")
	@ApiOperation("通过类型查询")
	public List<SvcCatalog> findBytype(@ApiParam(name = "svctype", value = "服务类型")
									   @RequestParam(value = "svctype", required = true) String svctype) {
		return svcCatalogDAO.findByType(svctype);
	}

//	@GetMapping(value = "findbyogaid")
//	@ApiOperation("通过服务机构标识查询")
//	public List<SvcCatalog> findByorganizationidentity(@ApiParam(name = "organizationidentity", value = "服务机构标识")
//													   @RequestParam(value = "organizationidentity", required = true) String organizationidentity) {
//		return svcCatalogDAO.findByOGA(organizationidentity);
//	}
//
//	@GetMapping(value = "findparaminfo")
//	@ApiOperation("通过id查询参数信息")
//	public List<String> findParaminfo(@ApiParam(name = "svcid", value = "服务id")
//									  @RequestParam(value = "svcid") Integer svcid) {
//		Optional<SvcCatalog> svcCatalog = svcCatalogDAO.findById(svcid);
//		SvcCatalog svcCatalog1 = svcCatalog.get();
//		List<String> paraminfo = new ArrayList<>();
//		paraminfo.add(svcCatalog1.getUrl());
//		paraminfo.add(svcCatalog1.getParaminfo());
//		return paraminfo;
//	}
//
//	@GetMapping(value = "findbyid")
//	@ApiOperation("通过id查询")
//	public SvcCatalog findByID(@ApiParam(name = "svcid", value = "服务id")
//							   @RequestParam(value = "svcid") Integer svcid) {
//		return svcCatalogDAO.findById(svcid).get();
//	}

	@GetMapping(value = "checksvc")
	@ApiOperation("通过唯一性，查询服务状态")
	public SvcCatalog checkSvc(@ApiParam(name = "svctype", value = "服务类型")
							   @RequestParam(value = "svctype", required = true) String svctype,
							   @ApiParam(name = "organizationidentity", value = "服务机构名称")
							   @RequestParam(value = "organizationidentity", required = true) String organizationidentity,
							   @ApiParam(name = "svcname", value = "服务名称")
							   @RequestParam(value = "svcname", required = false) String svcname,
							   @ApiParam(name = "svctypenode", value = "服务子类型")
							   @RequestParam(value = "svctypenode", required = false) String svctypenode) {
		if (svcname == null) {
			svcname = "";
		}
		if (svctypenode == null) {
			svctypenode = "";
		}
		return svcCatalogDAO.checkSvc(svctype, organizationidentity, svcname, svctypenode);
	}

	private Page<SvcCatalog> findAuditFromDao(int page, int size, int organizationId, String state) {
//		if (organizationId == null || organizationId == "") {
//			Pageable pageable = PageRequest.of(page - 1, size);
//			return svcCatalogDAO.findAudit(new Integer("0"), pageable);
//		}
//		Pageable pageable = PageRequest.of(page - 1, size);
//		return svcCatalogDAO.findAuditByOrg(new Integer("0"), organizationId, pageable);
		return null;
	}

//	private Page<SvcCatalog> findReleaseFromDao(int pageNumber, int pageSize, String organizationidentity, String state) {
//		if (organizationidentity == null || organizationidentity == "") {
//			Pageable pageable = new PageRequest(pageNumber - 1, pageSize);
//			return svcCatalogDAO.findRelease(new Integer("0"), pageable);
//		}
//		Pageable pageable = new PageRequest(pageNumber - 1, pageSize);
//		return svcCatalogDAO.findReleaseByOrg(new Integer("0"), organizationidentity, pageable);
//	}
}
