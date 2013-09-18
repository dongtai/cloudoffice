package apps.transmanager.weboffice.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import apps.transmanager.weboffice.databaseobject.MobileSendInfo;

public interface IMobilemessageService {

	public List<MobileSendInfo> getMessageList(Date startdate,Date endate,String companyname,String content,String mobile,Integer type,HttpServletRequest req);
	
}
