package apps.transmanager.weboffice.dao.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IMobilemessageDAO;
import apps.transmanager.weboffice.databaseobject.MobileSendInfo;

public class MobilemessageDAO extends BaseDAOImpl<MobileSendInfo> implements IMobilemessageDAO{ 
	
	
	public List<MobileSendInfo> findMobilemessage(Date startdate,Date endate,String companyname,String content,String mobile,Integer type,HttpServletRequest req){
		
		
	//	int totalRecord = (int)findMobilemessageCount(mobilemessage);
	//	page.retSetTotalRecord(totalRecord);
		StringBuffer sql = new StringBuffer();				
		sql.append("from MobileSendInfo tb where 1=1");
		if((companyname!=null) && (!"".equals(companyname))){
		sql.append(" and companyname like '%"+companyname+"%'");
		}
		if((content!=null) && (!"".equals(content))){
		sql.append(" and content like '%"+content+"%'");
		}
		if((startdate!=null) && (!"".equals(startdate))){
		sql.append(" and DATE_FORMAT(senddate,'%y-%m-%d')>=DATE_FORMAT('"+new java.sql.Date(startdate.getTime())+"','%y-%m-%d')");
		}
		if((endate!=null) && (!"".equals(endate))){
		sql.append(" and DATE_FORMAT(senddate,'%y-%m-%d')<=DATE_FORMAT('"+new java.sql.Date(endate.getTime())+"','%y-%m-%d')");
		}
		if((mobile!=null) && (!"".equals(mobile))){
		sql.append(" and mobile like '%"+mobile+"%'");
		}
		if((type!=null) && (!"".equals(type))){
		sql.append(" and type="+type);
		}

		Query query = getSession().createQuery(sql.toString());
		
		//query.setFirstResult(page.getCurrentRecord());
		//query.setMaxResults(page.getPageSize());
		return query.list();
		
		
	}
		
	}
	

