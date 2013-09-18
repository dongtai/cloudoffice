package apps.transmanager.weboffice.service.dao;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import apps.transmanager.weboffice.constants.server.LogConstant;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.SystemLogs;



/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class SystemLogsDAO extends BaseDAO
{
	/**
	 * 
	 * @param companyId  公司id
	 * @param type  日志类型，见LogConstant中常量定义
	 * @param startD 开始时间
	 * @param endD 结束时间
	 * @param ip ip值
	 * @param userIds 用户id
	 * @param start 小于0为从头开始
	 * @param count 小于0为所有符合条件的记录
	 * @param order 排序方式，值为type，operType，startDate，endDate，ip，content中之一，默认为startDate
	 * @param dir 升降序asc或desc
	 * @return
	 */
	public List<SystemLogs> getSearchLogs(Long companyId, Integer type, Date startD, Date endD, String ip, List<Long> userIds, 
			int start, int count, String order, String dir)
	{	
		//System.out.println("----------"+order+"****"+dir);
		HashMap<String, Object> params = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("from SystemLogs as model where ");
		if (companyId != null)
		{
			sb.append(" and model.company.id = :company ");
			//sb.append(" and model.company_id = :company ");
			params.put("company", companyId);
		}
		else
		{
			sb.append(" 1 = 1");    // 为了后续语言好写
			//sb.append(" model.company is null ");
		}
		if (type != null && type < 0)
		{
			sb.append(" and model.type = :type ");
			//sb.append(" and model.type_ = :type ");
			params.put("type", type);
		}
		if (ip != null && !ip.equals("")&& !ip.equals(" "))
		{
			sb.append(" and model.ip = :ip ");
			params.put("ip", ip);
		}
		if (userIds != null && userIds.size() > 0)
		{
			sb.append(" and model.user.id in (:userIds) ");
			//sb.append(" and model.user_id in (:userIds) ");
			params.put("userIds", userIds);
		}
		if (startD != null)
		{
			sb.append(" and model.startDate >= :start");
			params.put("start", startD);
		}
		if (endD != null)
		{
			sb.append(" and model.startDate <= :end ");
			params.put("end", endD);
		}
		
		if (order != null && dir != null)
		{
			if(order.equals("userName")||order.equals("realName"))sb.append(" order by model.user.");
			else sb.append(" order by model.");
			sb.append(order);
			sb.append("  ");
			sb.append(dir);
		}
		else
		{
			sb.append(" order by model.startDate desc");
		}
		return findByNamedParams(sb.toString(), params, start, count);
	}
	
	/**
	 * 
	 * @param companyId
	 * @param type
	 * @param startD
	 * @param endD
	 * @param ip
	 * @param userIds
	 * @return
	 */
	public Long getSearchLogsCount(Long companyId, Integer type, Date startD, Date endD, String ip, List<Long> userIds)
	{	
		HashMap<String, Object> params = new HashMap<String, Object>();
	    StringBuffer sb = new StringBuffer("select count(id) from SystemLogs as model where ");
	    
		if (companyId != null)
		{
			sb.append(" model.company.id = :company ");
			//sb.append(" model.company_id = :company ");
			params.put("company", companyId);
		}
		else
		{
			sb.append(" 1 = 1");    // 为了后续语言好写
			//sb.append(" model.company is null ");
		}
		if (type != null && type < 0)
		{
			sb.append(" and model.type = :type ");
			//sb.append(" and model.type_ = :type ");
			params.put("type", type);
		}
		if (ip != null&& !ip.equals("")&& !ip.equals(" "))
		{
			sb.append(" and model.ip = :ip ");
			params.put("ip", ip);
		}
		if (userIds != null && userIds.size() > 0)
		{
			sb.append(" and model.user.id in (:userIds) ");
			//sb.append(" and model.user_id in (:userIds) ");
			params.put("userIds", userIds);
		}
		if (startD != null)
		{
			sb.append(" and model.startDate >= :start ");
			params.put("start", startD);
		}
		if (endD != null)
		{
			sb.append(" and model.startDate <= :end ");
			params.put("end", endD);
		}
		return getCountByNamedParams(sb.toString(), params);
	}
	public long getAllDayCount(Long companyId, Integer type,String ip, List<Long> userIds){
		HashMap<String, Object> params1 = new HashMap<String, Object>();
		HashMap<String, Object> params2 = new HashMap<String, Object>();
		StringBuffer sb1 = new StringBuffer("select min(model.startDate) from SystemLogs as model where ");
		StringBuffer sb2 = new StringBuffer("select max(model.startDate) from SystemLogs as model where ");
		if (companyId != null)
		{
			sb1.append(" model.company.id = :company ");
			params1.put("company", companyId);
			sb2.append(" model.company.id = :company ");
			params2.put("company", companyId);
		}
		else
		{
			sb1.append(" 1 = 1");    // 为了后续语言好写
			sb2.append(" 1 = 1");    // 为了后续语言好写
		}
		if (type != null && type < 0)
		{
			sb1.append(" and model.type_ = :type ");
			params1.put("type", type);
			sb2.append(" and model.type_ = :type ");
			params2.put("type", type);
		}
		if (ip != null)
		{
			sb1.append(" and model.ip = :ip ");
			params1.put("ip", ip);
			sb2.append(" and model.ip = :ip ");
			params2.put("ip", ip);
		}
		if (userIds != null && userIds.size() > 0)
		{
			sb1.append(" and model.user.id in (:userIds) ");
			params1.put("userIds", userIds);
			sb2.append(" and model.user.id in (:userIds) ");
			params2.put("userIds", userIds);
		}
		List ret1 = excuteNativeSQLByName(sb1.toString(), params1, -1, -1);
		List ret2 = excuteNativeSQLByName(sb2.toString(), params2, -1, -1);
		if (ret1!=null && ret1.size()>0&& ret2!=null && ret2.size()>0)
			{
				 SimpleDateFormat dd=new SimpleDateFormat("yyyy-MM-dd"); 
				 Date date1;
				 Date date2;
				try {
					date1 = dd.parse(ret1.get(0).toString().substring(0,ret1.get(0).toString().indexOf(' ')));
					date2 = dd.parse(ret2.get(0).toString().substring(0,ret2.get(0).toString().indexOf(' ')));
					long diff =  date2.getTime()-date1.getTime();
				    long days = diff / (24 * 60 * 60 * 1000);
				    if (days<=0){days=1l;}
				    else days = days+1;
					return Integer.parseInt(""+days);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 1;
				}
			}
		return 1;
		
	}
	/**
	 * 删除date之前的日志
	 * @param companyId
	 * @param startD
	 * @param endD
	 */
	public void deleteLogs(Long companyId, Date date, Integer type)
	{	
		HashMap<String, Object> params = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("delete SystemLogs as model where ");
		if (companyId != null)
		{
			sb.append(" model.company.id = :company ");
			params.put("company", companyId);
		}
		else
		{
			sb.append(" 1 = 1");    // 为了后续语言好写
			//sb.append(" model.company is null ");
		}
		if (type != null && type < 0)
		{
			sb.append(" and model.type = :type ");
			params.put("type", type);
		}
		if (date != null)
		{
			sb.append(" and model.startDate <= :end");
			params.put("end", date);
		}	
		excuteByNamedParams(sb.toString(), params);
	}
	
	/**
	 * 设置用户退出时间
	 */
	public void setLogout(Long userId, String token)
	{
		String sql = "update SystemLogs set endDate = ? where user.id = ? and content = ?";
		this.excute(sql, new Date(), userId, token);
	}	
	
	/**
	 * 获取用户登录推出记录，在sysetmlogs中startDate为登录时间，endDate为退出时间
	 * @param companyId
	 * @param startD
	 * @param endD
	 * @param ip
	 * @param userIds
	 * @param start
	 * @param count
	 * @param order
	 * @param dir
	 * @return
	 */
	public List<SystemLogs> getUserLoginLogs(Long companyId, Date startD, Date endD, String ip, List<Long> userIds, 
			int start, int count, String order, String dir)
	{
		HashMap<String, Object> params = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select s from SystemLogs as s where s.type = :type ");
		// 由于JPA目前还不支持左连接时候的on条件，所有这里以原生语句写
		//StringBuffer sb = new StringBuffer("SELECT u.userName, u.realName, s.startDate, s.ip, aa.startDate, s.ip, s.company_id FROM users u, systemlogs s left join systemlogs aa ");
		//sb.append(" on s.content = aa.content and aa.opertype = :operType2 where u.id = s.user_id and s.opertype = :operType and s.type_ = :type ");
		//params.put("operType", LogConstant.OPER_TYPE_LOGIN);
		//params.put("operType2", LogConstant.OPER_TYPE_LOGOUT);
		params.put("type", LogConstant.TYPE_ONLINE);
		if (companyId != null)
		{
			sb.append(" and s.company_id = :company ");
			params.put("company", companyId);
		}
		if (ip != null)
		{
			sb.append(" and s.ip = :ip ");
			params.put("ip", ip);
		}
		if (userIds != null && userIds.size() > 0)
		{
			sb.append(" and s.user_id in (:userIds) ");
			params.put("userIds", userIds);
		}
		if (startD != null)
		{
			sb.append(" and s.startDate >= :start");
			params.put("start", startD);
		}
		if (endD != null)
		{
			sb.append(" and s.startDate <= :end ");
			params.put("end", endD);
		}
		
		if (order != null && dir != null)
		{
			if (order.equals("endDate"))
			{
				sb.append(" order by s.endDate");
			}
			else
			{
				sb.append(" order by s.");
				sb.append(order);
			}			
			sb.append("  ");
			sb.append(dir);
		}
		else
		{
			sb.append(" order by s.startDate desc");
		}
		List ret = findByNamedParams(sb.toString(), params, start, count);

		return ret;		
	}
	
	/**
	 * 获取用户登录推出记录，总数量
	 * @param companyId
	 * @param startD
	 * @param endD
	 * @param ip
	 * @param userIds
	 * @return
	 */
	public Long getUserLoginLogsCount(Long companyId, Date startD, Date endD, String ip, List<Long> userIds)
	{
		HashMap<String, Object> params = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select count(s) from SystemLogs as s where s.type = :type ");
		// 由于JPA目前还不支持左连接时候的on条件，所有这里以原生语句写
		//StringBuffer sb = new StringBuffer("SELECT count(s.id) FROM systemlogs s left join systemlogs a ");
		//sb.append(" on s.content = a.content and a.opertype = :operType2 where s.opertype = :operType and s.type_ = :type ");
		//params.put("operType", LogConstant.OPER_TYPE_LOGIN);
		//params.put("operType2", LogConstant.OPER_TYPE_LOGOUT);
		params.put("type", LogConstant.TYPE_ONLINE);
		if (companyId != null)
		{
			sb.append(" and s.company_id = :company ");
			params.put("company", companyId);
		}
		if (ip != null)
		{
			sb.append(" and s.ip = :ip ");
			params.put("ip", ip);
		}
		if (userIds != null && userIds.size() > 0)
		{
			sb.append(" and s.user_id in (:userIds) ");
			params.put("userIds", userIds);
		}
		if (endD != null)
		{
			sb.append(" and s.startDate <= :end ");
			params.put("end", endD);
		}
		if (startD != null)
		{
			sb.append(" and s.startDate >= :start");
			params.put("start", startD);
		}
		
		return getCountByNamedParams(sb.toString(), params);
	}

	public void deleteLogs(Long companyId, Integer type, Date startD,
			Date endD, String ip, List<Long> userIds) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("delete SystemLogs as model where ");
		if (companyId != null)
		{
			sb.append(" and model.company.id = :company ");
			//sb.append(" and model.company_id = :company ");
			params.put("company", companyId);
		}
		else
		{
			sb.append(" 1 = 1");    // 为了后续语言好写
			//sb.append(" model.company is null ");
		}
		if (type != null && type < 0)
		{
			sb.append(" and model.type = :type ");
			params.put("type", type);
		}
		if (ip != null && !ip.equals("")&& !ip.equals(" "))
		{
			sb.append(" and model.ip = :ip ");
			params.put("ip", ip);
		}
		if (userIds != null && userIds.size() > 0)
		{
			sb.append(" and model.user.id in (:userIds) ");
			params.put("userIds", userIds);
		}
		if (startD != null)
		{
			sb.append(" and model.startDate >= :start");
			params.put("start", startD);
		}
		if (endD != null)
		{
			sb.append(" and model.startDate <= :end ");
			params.put("end", endD);
		}
		excuteByNamedParams(sb.toString(), params);
		
	}
    
	public List<HashMap<String, String>> getDepLogs(String companyId,Date startD, Date endD,int start,int count, String sort, String dir) {
        long cmpid = Long.parseLong(companyId);
		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
    	HashMap<String, String> ret;
	    if(cmpid==0){
	        //得到对应时间内有访问的公司
	    	HashMap<String, Object> cmpparams = new HashMap<String, Object>();
		    StringBuffer cmpsb = new StringBuffer("select distinct c.id from SystemLogs as s,Company as c where s.type_ = -1");
		    cmpsb.append(" and s.company_id = c.id");
		    if (startD != null)
			{
		    	cmpsb.append(" and s.startDate >= :start");
		    	cmpparams.put("start", startD);
			}
			if (endD != null)
			{
				cmpsb.append(" and s.startDate <= :end ");
				cmpparams.put("end", endD);
			}
			if (sort != null && dir != null)
			{
				cmpsb.append(" order by c.name");
				cmpsb.append("  ");
				cmpsb.append(dir);
			}
			else cmpsb.append(" order by c.id asc ");
			List cmpidlist = excuteNativeSQLByName(cmpsb.toString(),cmpparams,start,count);
			if(cmpidlist.size()>0){
			List<Company> cmplist = new ArrayList<Company>();
			for(int i=0;i<cmpidlist.size();i++){
				String resultcmpid = ""+cmpidlist.get(i);
				long id = Long.parseLong(resultcmpid);
				Company co = (Company) find(Company.class.getName(),id);
				cmplist.add(co);
			}
            //得到日志信息			
	    	for(Company cm:cmplist){
	    		ret = new HashMap<String, String>();
	    		//得到起始日期
	    		HashMap<String, Object> params = new HashMap<String, Object>();
			    StringBuffer sb = new StringBuffer("select min(s.startDate) from SystemLogs as s where s.type_ = -1");
			    sb.append(" and s.company_id = :companyId");
			    params.put("companyId", cm.getId());
			  if (startD != null)
				{
					sb.append(" and s.startDate >= :start");
					params.put("start", startD);
				}
				if (endD != null)
				{
					sb.append(" and s.startDate <= :end ");
					params.put("end", endD);
				}
				List starttimeL = excuteNativeSQLByName(sb.toString(),params,-1,-1);
				//String starttime = (""+starttimeL.get(0)).substring(0,(""+starttimeL.get(0)).indexOf(" "));
				String starttime = ""+starttimeL.get(0);
				//得到截止日期
				HashMap<String, Object> params1 = new HashMap<String, Object>();
			    StringBuffer sb1 = new StringBuffer("select max(s.startDate) from SystemLogs as s where s.type_ = -1");
			    sb1.append(" and s.company_id = :companyId");
			    params1.put("companyId", cm.getId());
			    if (startD != null)
				{
					sb1.append(" and s.startDate >= :start");
					params1.put("start", startD);
				}
				if (endD != null)
				{
					sb1.append(" and s.startDate <= :end ");
					params1.put("end", endD);
				}
				List endtimeL = excuteNativeSQLByName(sb1.toString(),params1,-1,-1);
				//String endtime = (""+endtimeL.get(0)).substring(0,(""+endtimeL.get(0)).indexOf(" "));
				String endtime = ""+endtimeL.get(0);
				//得到总人数
				HashMap<String, Object> params2 = new HashMap<String, Object>();
			    StringBuffer sb2= new StringBuffer("select count(u.id) from Users as u where");
			    sb2.append(" u.company_id = :companyId");
			    params2.put("companyId", cm.getId());
			    List totalUserCountL = excuteNativeSQLByName(sb2.toString(),params2,-1,-1);
			    String totalUserCount = ""+totalUserCountL.get(0);
			    //访问次数
			    HashMap<String, Object> params3 = new HashMap<String, Object>();
			    StringBuffer sb3 = new StringBuffer("select count(s.id) from SystemLogs as s where s.type_ = -1");
			    sb3.append(" and s.company_id = :companyId");
			    params3.put("companyId", cm.getId());
			    if (startD != null)
				{
					sb3.append(" and s.startDate >= :start");
					params3.put("start", startD);
				}
				if (endD != null)
				{
					sb3.append(" and s.startDate <= :end ");
					params3.put("end", endD);
				}
				List VisitCountL = excuteNativeSQLByName(sb3.toString(),params3,-1,-1);
				String VisitCount = ""+ VisitCountL.get(0);
				//访问人数
				HashMap<String, Object> params4 = new HashMap<String, Object>();
			    StringBuffer sb4 = new StringBuffer("select count(distinct s.user_id) from SystemLogs as s where s.type_ = -1");
			    sb4.append(" and s.company_id = :companyId");
			    params4.put("companyId", cm.getId());
			    if (startD != null)
				{
					sb4.append(" and s.startDate >= :start");
					params4.put("start", startD);
				}
				if (endD != null)
				{
					sb4.append(" and s.startDate <= :end ");
					params4.put("end", endD);
				}
				List VisitUserCountL = excuteNativeSQLByName(sb4.toString(),params4,-1,-1);
				String VisitUserCount = ""+ VisitUserCountL.get(0);
				//访问比例
				DecimalFormat df = new DecimalFormat("#0.00");
				String VisitPercent = "";
				if(totalUserCount.equals("0")){
					VisitPercent = "";
				}
				else {
					double data=Integer.parseInt(VisitUserCount)*100.0/Integer.parseInt(totalUserCount);
	        		VisitPercent =df.format(data)+"%";
				}
				
		    	ret.put("depName", cm.getName());
		    	if(starttime.equals("null")||starttime.equals("")||starttime.equals(" ")){
		    		 ret.put("startDate", "");
		    	}else{
		    		starttime = starttime.substring(0,starttime.indexOf(' '));
		    		ret.put("startDate", starttime);
		    	}
		    	if(endtime.equals("null")||endtime.equals("")||endtime.equals(" ")){
		    		ret.put("endDate", "");
			   	}else{
			   		endtime = endtime.substring(0,endtime.indexOf(' '));	
		    		ret.put("endDate", endtime);
			  }
			    ret.put("totalUser", totalUserCount);
				ret.put("totalNumber", VisitCount);
				ret.put("visitUser", VisitUserCount);
				ret.put("visitPercent", VisitPercent);
				result.add(ret);
	    	}
	    }
			return result;
	    }
	    
	    else {
	    	HashMap<String, Object> depparams = new HashMap<String, Object>();
	    	StringBuffer depsb = new StringBuffer("select distinct o.id from Organizations as o,Organizations as o2,UsersOrganizations as uo,SystemLogs as s where s.user_id = uo.user_id and o.parent_id is null ");
		    depsb.append(" and o.company_id = :companyId and uo.organization_id = o2.id and s.type_ = -1 ");
		    depparams.put("companyId", cmpid);
		    depsb.append(" and (SUBSTRING_INDEX(o2.organizecode,'-',1) = o.organizecode or o2.organizecode = o.organizecode)");
		    if (startD != null)
			{
		    	depsb.append(" and s.startDate >= :start");
		    	depparams.put("start", startD);
			}
			if (endD != null)
			{
				depsb.append(" and s.startDate <= :end ");
				depparams.put("end", endD);
			}
			if (sort != null && dir != null)
			{
				depsb.append(" order by o.name");
				depsb.append("  ");
				depsb.append(dir);
			}
			else depsb.append(" order by o.id asc ");
			List depidlist = excuteNativeSQLByName(depsb.toString(),depparams,start,count);
			
			if(depidlist.size()>0){
			List<Organizations> deplist = new ArrayList<Organizations>();
			for(int i=0;i<depidlist.size();i++){
				String resultdepid = ""+depidlist.get(i);
				long id = Long.parseLong(resultdepid);
				Organizations dep = (Organizations) find(Organizations.class.getName(),id);
				deplist.add(dep);
				//System.out.println("----------------"+dep.getName());
			}
			for(Organizations og:deplist){
				ret = new HashMap<String, String>();
				//得到开始日期
				HashMap<String, Object> params = new HashMap<String, Object>();
			    StringBuffer sb = new StringBuffer("select min(s.startDate) from Organizations as o,UsersOrganizations as uo,SystemLogs as s where s.user_id = uo.user_id ");
			    sb.append(" and uo.organization_id = o.id and s.type_ = -1 ");
			    sb.append(" and ( o.organizecode like '").append(og.getOrganizecode()).append("-%' or o.organizecode = :organizecode)");
			    params.put("organizecode", og.getOrganizecode());
			    if (startD != null)
				{
					sb.append(" and s.startDate >= :start");
					params.put("start", startD);
				}
				if (endD != null)
				{
					sb.append(" and s.startDate <= :end ");
					params.put("end", endD);
				}
				List starttimeL = excuteNativeSQLByName(sb.toString(),params,-1,-1);
				String starttime = ""+starttimeL.get(0);
					
					//得到结束日期
					HashMap<String, Object> params1 = new HashMap<String, Object>();
				    StringBuffer sb1 = new StringBuffer("select max(s.startDate) from Organizations as o,UsersOrganizations as uo,SystemLogs as s where s.user_id = uo.user_id ");
				    sb1.append(" and uo.organization_id = o.id and s.type_ = -1 ");
				    sb1.append(" and ( o.organizecode like '").append(og.getOrganizecode()).append("-%' or o.organizecode = :organizecode)");
				    params1.put("organizecode", og.getOrganizecode());
				    if (startD != null)
					{
						sb1.append(" and s.startDate >= :start");
						params1.put("start", startD);
					}
					if (endD != null)
					{
						sb1.append(" and s.startDate <= :end ");
						params1.put("end", endD);
					}
					List endtimeL = excuteNativeSQLByName(sb1.toString(),params1,-1,-1);
					String endtime = ""+endtimeL.get(0);
					
					//得到总人数
					HashMap<String, Object> params2 = new HashMap<String, Object>();
				    StringBuffer sb2 = new StringBuffer("select count(distinct uo.id) from Organizations as o,UsersOrganizations as uo where ");
				    sb2.append(" uo.organization_id = o.id ");
				    sb2.append(" and ( o.organizecode like '").append(og.getOrganizecode()).append("-%' or o.organizecode = :organizecode)");
				    params2.put("organizecode", og.getOrganizecode());
					List totalUserCountL = excuteNativeSQLByName(sb2.toString(),params2,-1,-1);
					String totalUserCount = ""+totalUserCountL.get(0);
					
					//访问次数
					HashMap<String, Object> params3 = new HashMap<String, Object>();
				    StringBuffer sb3 = new StringBuffer("select count(s.id) from Organizations as o,UsersOrganizations as uo,SystemLogs as s where s.user_id = uo.user_id ");
				    sb3.append(" and uo.organization_id = o.id and s.type_ = -1 ");
				    sb3.append(" and ( o.organizecode like '").append(og.getOrganizecode()).append("-%' or o.organizecode = :organizecode)");
				    params3.put("organizecode", og.getOrganizecode());
				    if (startD != null)
					{
						sb3.append(" and s.startDate >= :start");
						params3.put("start", startD);
					}
					if (endD != null)
					{
						sb3.append(" and s.startDate <= :end ");
						params3.put("end", endD);
					}
					List VisitCountL = excuteNativeSQLByName(sb3.toString(),params3,-1,-1);
					String VisitCount = ""+VisitCountL.get(0);
					
					//访问人数
					HashMap<String, Object> params4 = new HashMap<String, Object>();
				    StringBuffer sb4 = new StringBuffer("select count(distinct s.user_id) from Organizations as o,UsersOrganizations as uo,SystemLogs as s where s.user_id = uo.user_id ");
				    sb4.append(" and uo.organization_id = o.id and s.type_ = -1 ");
				    sb4.append(" and ( o.organizecode like '").append(og.getOrganizecode()).append("-%' or o.organizecode = :organizecode)");
				    params4.put("organizecode", og.getOrganizecode());
				    if (startD != null)
					{
						sb4.append(" and s.startDate >= :start");
						params4.put("start", startD);
					}
					if (endD != null)
					{
						sb4.append(" and s.startDate <= :end ");
						params4.put("end", endD);
					}
					List VisitUserCountL = excuteNativeSQLByName(sb4.toString(),params4,-1,-1);
					String VisitUserCount = ""+VisitUserCountL.get(0);
					//访问比例
					DecimalFormat df = new DecimalFormat("#0.00");
					String VisitPercent = "";
					if(totalUserCount.equals("0")){
						VisitPercent = "";
					}
					else {
						double data=Integer.parseInt(VisitUserCount)*100.0/Integer.parseInt(totalUserCount);
		        		VisitPercent =df.format(data)+"%";
					}
					ret.put("depName", og.getName());
			    	if(starttime.equals("null")||starttime.equals("")||starttime.equals(" ")){
			    		 ret.put("startDate", "");
			    	}else{
			    		starttime = starttime.substring(0,starttime.indexOf(' '));
			    		ret.put("startDate", starttime);
			    	}
			    	if(endtime.equals("null")||endtime.equals("")||endtime.equals(" ")){
			    		ret.put("endDate", "");
				   	}else{
				   		endtime = endtime.substring(0,endtime.indexOf(' '));	
			    		ret.put("endDate", endtime);
				  }
				    ret.put("totalUser", totalUserCount);
					ret.put("totalNumber", VisitCount);
					ret.put("visitUser", VisitUserCount);
					ret.put("visitPercent", VisitPercent);
					result.add(ret);
				
			   }
			}
		    return result;
	    }
	}

	public int getDepLogsCount(String companyId, Date startD, Date endD) {
		long cmpid = Long.parseLong(companyId);
		if(cmpid==0){
	        //得到对应时间内有访问的公司
	    	HashMap<String, Object> cmpparams = new HashMap<String, Object>();
		    StringBuffer cmpsb = new StringBuffer("select distinct c.id from SystemLogs as s,Company as c where s.type_ = -1");
		    cmpsb.append(" and s.company_id = c.id");
		    if (startD != null)
			{
		    	cmpsb.append(" and s.startDate >= :start");
		    	cmpparams.put("start", startD);
			}
			if (endD != null)
			{
				cmpsb.append(" and s.startDate <= :end ");
				cmpparams.put("end", endD);
			}
			cmpsb.append(" order by c.id asc ");
			List cmpidlist = excuteNativeSQLByName(cmpsb.toString(),cmpparams,-1,-1);
			return cmpidlist.size();
	     }
		else {
			HashMap<String, Object> depparams = new HashMap<String, Object>();
	    	StringBuffer depsb = new StringBuffer("select distinct o.id from Organizations as o,Organizations as o2,UsersOrganizations as uo,SystemLogs as s where s.user_id = uo.user_id and o.parent_id is null ");
		    depsb.append(" and o.company_id = :companyId and uo.organization_id = o2.id and s.type_ = -1 ");
		    depparams.put("companyId", cmpid);
		    depsb.append(" and (SUBSTRING_INDEX(o2.organizecode,'-',1) = o.organizecode or o2.organizecode = o.organizecode)");
		    if (startD != null)
			{
		    	depsb.append(" and s.startDate >= :start");
		    	depparams.put("start", startD);
			}
			if (endD != null)
			{
				depsb.append(" and s.startDate <= :end ");
				depparams.put("end", endD);
			}
		    depsb.append(" order by o.id asc ");
			List depidlist = excuteNativeSQLByName(depsb.toString(),depparams,-1,-1);
			return depidlist.size();
		}
	}
}
