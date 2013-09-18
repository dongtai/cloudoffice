package apps.transmanager.weboffice.dao;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import apps.transmanager.weboffice.databaseobject.MobileSendInfo;


public interface IMobilemessageDAO extends IBaseDAO<MobileSendInfo> {
/**
 * 查询符合条件的短信息
 */
	
	List<MobileSendInfo> findMobilemessage(Date startdate,Date endate,String companyname,String content,String mobile,Integer type,HttpServletRequest req);


}
