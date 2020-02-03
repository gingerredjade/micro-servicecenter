package com.micro.serviceshow.userinfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 继承JpaRepository，UserInfo表示该Repository与实体UserInfo关联，主键类型为Integer
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

	@Query("select u from UserInfo u where u.username = :username and u.password = :password")
	UserInfo findByNameAndPassword(@Param("username") String username, @Param("password") String password);

	@Query("select u from UserInfo u where u.username = :username")
	UserInfo findByName(@Param("username") String username);

	@Query("select u from UserInfo u where u.username like concat('%',:username,'%') ")
	Page<UserInfo> findWithCondition(@Param("username") String username, Pageable pageable);

}
