package com.gis.serviceshow.statistical;

import com.gis.serviceshow.svccatalog.SvcCatalog;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by gis on 2019/10/9.
 */
public interface SubjectDAO extends JpaRepository<Subject, Integer> {
	@Query("select u from Subject u where u.svctypename = :svctypename and u.svcname = :svcname and u.organizationname= :organizationname " +
		"and u.year= :year and u.month= :month and u.week= :week  and u.countflag= :countflag")
	Subject findByName(@Param("svctypename") String svctypename, @Param("svcname") String svcname, @Param("organizationname") String organizationname,
					   @Param("year") Integer year, @Param("month") Integer month, @Param("week") Integer week, @Param("countflag") Integer countflag);

	@Query("select u from Subject u where u.year= :year and u.month= :month and u.week= :week  and u.countflag= :countflag")
	List<Subject> findByTime(@Param("year") Integer year, @Param("month") Integer month, @Param("week") Integer week, @Param("countflag") Integer countflag);

	@Query("select u.svctypename, sum(u.subjectcount) from Subject u where u.year= :year and u.month= :month and u.week= :week  and u.countflag= :countflag GROUP BY u.svctypename")
	List countByType(@Param("year") Integer year, @Param("month") Integer month, @Param("week") Integer week, @Param("countflag") Integer countflag);

	@Query("select u.organizationname, sum(u.subjectcount) from Subject u where u.year= :year and u.month= :month and u.week= :week  and u.countflag= :countflag GROUP BY u.organizationname")
	List countByORG(@Param("year") Integer year, @Param("month") Integer month, @Param("week") Integer week, @Param("countflag") Integer countflag);

	@Query("select u from Subject u where u.year= :year and u.month= :month and u.week= :week  and u.countflag= :countflag ORDER BY u.subjectcount DESC")
	List findHotSvc(@Param("year") Integer year, @Param("month") Integer month, @Param("week") Integer week, @Param("countflag") Integer countflag);

	@Query("select  sum(u.subjectcount) from Subject u where u.countflag= :countflag")
	Integer countAll(@Param("countflag") Integer countflag);


	@Query("select sum(u.subjectcount) from Subject u where u.year= :year and u.month= :month and u.week= :week  and u.countflag= :countflag")
	Integer countByTime(@Param("year") Integer year, @Param("month") Integer month, @Param("week") Integer week, @Param("countflag") Integer countflag);
}
