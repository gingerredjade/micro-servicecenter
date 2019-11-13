package com.gis.serviceshow.statistical;
import com.nci.constants.ProviderDef;
import com.nci.constants.ServiceTypes.ServiceTypeDef;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by gis on 2019/10/9.
 */
@RestController
@EnableAutoConfiguration
@CrossOrigin
@RequestMapping(value = "statistical")
public class SubjectController {

	@Autowired
	SubjectDAO subjectDAO;

	@GetMapping(value = "findeAll")
	@ApiOperation("查询")
	public List<Subject> findAll(){
		return subjectDAO.findAll();
	}

	@GetMapping(value = "save")
	@ApiOperation("增加")
	public Boolean save(@ApiParam(name = "svctype", value = "服务类型")
							@RequestParam(value = "svctype" ) String svctype,
						@ApiParam(name = "svcname", value = "服务名称")
							@RequestParam(value = "svcname" , required=false) String svcname,
						@ApiParam(name = "organizationIdentity", value = "服务机构")
							@RequestParam(value = "organizationIdentity") String organizationIdentity
//		@ApiParam(name = "subjectnode", value = "服务子名称")organizationIdentity
//		@RequestParam(value = "subjectnode") String subjectnode,
//		@ApiParam(name = "subjectaux", value = "服务附加参数")
//		@RequestParam(value = "subjectaux") String subjectaux
	){
		try{
			String svctypename = ServiceTypeDef.getServiceNameCN(svctype);
			String organizationname = ProviderDef.getProviderNameByIdentify(organizationIdentity);
			if (svcname==null){
				svcname="";
			}
			if (svctypename==null||organizationname==null){
				return false;
			}
			Calendar ca = Calendar.getInstance();
			int year = ca.get(Calendar.YEAR);
			int month = ca.get(Calendar.MONTH)+1;
			int week = ca.WEEK_OF_YEAR;
			Subject subjectW =subjectDAO.findByName(svctypename,svcname,organizationname,year,month,week,0);
			Subject subjectM =subjectDAO.findByName(svctypename,svcname,organizationname,year,month,-1,1);
			Subject subjectY =subjectDAO.findByName(svctypename,svcname,organizationname,year,-1,-1,2);
			if (subjectW==null){
				//不存在该服务统计
				subjectW = new Subject();
				subjectW.setSvctype(svctype);
				subjectW.setSvcname(svcname);
				subjectW.setSvctypename(svctypename);
				subjectW.setOrganizationidentity(organizationIdentity);
				subjectW.setOrganizationname(organizationname);
//				BigInteger count = subjectW.getSubjectcount();
				subjectW.setSubjectcount(new BigInteger("1"));
				subjectW.setYear(year);
				subjectW.setMonth(month);
				subjectW.setWeek(week);
				subjectW.setCountflag(0);
				subjectDAO.save(subjectW);
				if(subjectM !=null){
					BigInteger countM = subjectM.getSubjectcount();
					subjectM.setSubjectcount(countM.add(new BigInteger("1")));
					subjectDAO.save(subjectM);
				}else {
					subjectM = (Subject) subjectW.clone();
					subjectM.setSubjectid(null);
					subjectM.setWeek(-1);
					subjectM.setCountflag(1);
					subjectDAO.save(subjectM);
				}

				if (subjectY!=null){
					BigInteger countY = subjectY.getSubjectcount();
					subjectY.setSubjectcount(countY.add(new BigInteger("1")));
					subjectDAO.save(subjectY);
				}else {
					subjectY = (Subject) subjectW.clone();
					subjectY.setSubjectid(null);
					subjectY.setWeek(-1);
					subjectY.setMonth(-1);
					subjectY.setCountflag(2);
					subjectDAO.save(subjectY);
				}
			}else {
				//已经存在该服务统计
				BigInteger count = subjectW.getSubjectcount();
				subjectW.setSubjectcount(count.add(new BigInteger("1")));
				subjectDAO.save(subjectW);

				BigInteger countM = subjectM.getSubjectcount();
				subjectM.setSubjectcount(countM.add(new BigInteger("1")));
				subjectDAO.save(subjectM);

				BigInteger countY = subjectY.getSubjectcount();
				subjectY.setSubjectcount(countY.add(new BigInteger("1")));
				subjectDAO.save(subjectY);
			}
			return true;
		}catch (Exception e){
			return false;
		}
	}


	@GetMapping(value = "findbyweek")
	@ApiOperation("查看本周各服务调用次数")
	public List<Subject> findByWeek(){
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH)+1;
		int week = ca.WEEK_OF_YEAR;
		return subjectDAO.findByTime(year,month,week,0);
	}

	@GetMapping(value = "countbyweek")
	@ApiOperation("查看本周各服务调用次数")
	public List countByWeekC(){
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH)+1;
		int week = ca.WEEK_OF_YEAR;
		return subjectDAO.countByType(year,month,week,0);
	}

	@GetMapping(value = "countbyorg")
	@ApiOperation("查看本周各服务调用次数")
	public List countByORG(){
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH)+1;
		int week = ca.WEEK_OF_YEAR;
		return subjectDAO.countByORG(year,month,week,0);
	}

	@GetMapping(value = "findHotSvc")
	@ApiOperation("查看本周调用前10的服务")
	public List findHotSvc(){
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH)+1;
		int week = ca.WEEK_OF_YEAR;
		return subjectDAO.findHotSvc(year,month,week,0);
	}

	@GetMapping(value = "countall")
	@ApiOperation("获取所有访问量")
	public Integer countAll(){
		return subjectDAO.countAll(2);
	}

	@GetMapping(value = "counbytime")
	@ApiOperation("获取本周，本月，本年，全部访问量")
	public List<Integer> countTime(){
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH)+1;
		int week = ca.WEEK_OF_YEAR;
		Integer weekcount = subjectDAO.countByTime(year,month,week,0);
		Integer monthcount = subjectDAO.countByTime(year,month,-1,1);
		Integer yearcount = subjectDAO.countByTime(year,-1,-1,2);
		Integer all = subjectDAO.countAll(2);
		List<Integer> list = new ArrayList<>();
		list.add(weekcount);
		list.add(monthcount);
		list.add(yearcount);
		list.add(all);
		return list;
	}
//	@GetMapping(value = "findbymonth")
//	@ApiOperation("查看本月各服务调用次数")
//	public List<Subject> findByMonth(){
//		Calendar ca = Calendar.getInstance();
//		int year = ca.get(Calendar.YEAR);
//		int month = ca.get(Calendar.MONTH)+1;
//		int week = ca.WEEK_OF_YEAR;
//		return subjectDAO.findByTime(year,month,-1,1);
//	}
//
//
//	@GetMapping(value = "findbyyear")
//	@ApiOperation("查看本年各服务调用次数")
//	public List<Subject> findByYear(){
//		Calendar ca = Calendar.getInstance();
//		int year = ca.get(Calendar.YEAR);
//		int month = ca.get(Calendar.MONTH)+1;
//		int week = ca.WEEK_OF_YEAR;
//		return subjectDAO.findByTime(year,-1,-1,2);
//	}

}
