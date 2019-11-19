package com.gis.serviceshow.svccatalog;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by gis on 2019/10/9.
 */
public interface SvcCatalogDAO extends JpaRepository<SvcCatalog, Integer> {

	@Query("select svc from SvcCatalog svc where svc.auditState = :auditstate")
	Page<SvcCatalog> findAudit(@Param("auditstate") Integer auditstate, Pageable pageable);

	@Query("select svc from SvcCatalog svc where svc.auditState = :auditstate")
	List<SvcCatalog> findAudit(@Param("auditstate") Integer auditstate);

	@Query("select svc from SvcCatalog svc where svc.auditState = :auditstate and svc.organization.organizationId = :organizationId")
	Page<SvcCatalog> findAuditByOrg(@Param("auditstate") Integer auditstate, @Param("organizationId") int organizationId, Pageable pageable);

	@Query("select svc from SvcCatalog svc where svc.releaseState = :releasestate and svc.auditState = 1")
	Page<SvcCatalog> findRelease(@Param("releasestate") Integer releasestate, Pageable pageable);

	@Query("select svc from SvcCatalog svc where svc.releaseState = :releasestate and svc.auditState = 1")
	List<SvcCatalog> findRelease(@Param("releasestate") Integer releasestate);

	@Query("select svc from SvcCatalog svc where svc.organization.id = :organizationId and svc.svctypename like concat('%',:svctypename,'%') and svc.svctype like concat('%',:svctype,'%') and svc.info like concat('%',:info,'%') ")
	Page<SvcCatalog> findWithCondition(@Param("organizationId") Integer organizationId, @Param("svctypename") String svctypename, @Param("svctype") String svctype, @Param("info") String info, Pageable pageable);

	@Query("select svc from SvcCatalog svc where svc.svctypename like concat('%',:svctypename,'%') and svc.svctype like concat('%',:svctype,'%') and svc.info like concat('%',:info,'%') ")
	Page<SvcCatalog> findWithConditionAllOrg(@Param("svctypename") String svctypename, @Param("svctype") String svctype, @Param("info") String info, Pageable pageable);

	@Query("select svc from SvcCatalog svc where svc.auditState=1 and svc.organization.id = :organizationId and svc.svctypename like concat('%',:svctypename,'%') and svc.svctype like concat('%',:svctype,'%') and svc.info like concat('%',:info,'%') ")
	Page<SvcCatalog> findWithConditionAudited(@Param("organizationId") Integer organizationId, @Param("svctypename") String svctypename, @Param("svctype") String svctype, @Param("info") String info, Pageable pageable);

	@Query("select svc from SvcCatalog svc where svc.auditState=1 and svc.svctypename like concat('%',:svctypename,'%') and svc.svctype like concat('%',:svctype,'%') and svc.info like concat('%',:info,'%') ")
	Page<SvcCatalog> findWithConditionAllOrgAudited(@Param("svctypename") String svctypename, @Param("svctype") String svctype, @Param("info") String info, Pageable pageable);

//	@Query("select svc from SvcCatalog svc where svc.releasestate = :releasestate and svc.auditstate = 1 and svc.organizationidentity = :organizationidentity")
//	Page<SvcCatalog> findReleaseByOrg(@Param("releasestate") Integer releasestate,@Param("organizationidentity") String organizationidentity, Pageable pageable);

	@Query("select svc from SvcCatalog svc where svc.svctype = :svctype and svc.releaseState = 1")
	List<SvcCatalog> findByType(@Param("svctype") String svctype);

//	@Query("select svc from SvcCatalog svc where svc.organizationidentity = :organizationidentity and svc.releasestate = 1")
//	List<SvcCatalog> findByOGA(@Param("organizationidentity") String organizationidentity);

	@Query("select svc from SvcCatalog svc where svc.svctype = :svctype and svc.organization.specialIdentity = :organizationidentity and svc.svcname = :svcname and svc.svctypenode = :svctypenode")
	SvcCatalog checkSvc(@Param("svctype") String svctype, @Param("organizationidentity") String organizationidentity, @Param("svcname") String svcname, @Param("svctypenode") String svctypenode);

	@Query("select organization.organizationName, count(*) from SvcCatalog group by organization.organizationName")
	List countByORG();

	@Query("select svctypename, count(*) from SvcCatalog group by svctypename")
	List countByType();
}
