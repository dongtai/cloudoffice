package apps.transmanager.weboffice.service.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.dao.IMobilemessageDAO;
import apps.transmanager.weboffice.databaseobject.MobileSendInfo;
import apps.transmanager.weboffice.service.IMobilemessageService;
@Component(value=MobilemessageService.NAME)
public class MobilemessageService implements IMobilemessageService{
	public static final String NAME = "mobilemessageService";
	@Autowired
	private IMobilemessageDAO mobilemessageDAO;
	
	public void setMobilemessageDAO(IMobilemessageDAO mobilemessageDAO) {
		this.mobilemessageDAO = mobilemessageDAO;
	}
	
	public IMobilemessageDAO getMobilemessageDAO() {
		return mobilemessageDAO;
	}


	/**
	 * 获取短信息列表
	 */
	public List<MobileSendInfo> getMessageList(Date startdate,Date endate,String companyname,String content,String mobile,Integer type,HttpServletRequest req) {
		List<MobileSendInfo> messageList = mobilemessageDAO.findMobilemessage(startdate,endate,companyname,content,mobile,type,req);
		return messageList;
	}
	
}
