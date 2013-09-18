package apps.transmanager.weboffice.dwr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.MobileSendInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.IMobilemessageService;

import com.ibm.icu.text.SimpleDateFormat;

public class MobilemessageDwr {

	private IMobilemessageService mobilemessageService;
	
	public void setMobilemessageService(IMobilemessageService mobilemessageService) {
		this.mobilemessageService = mobilemessageService;
	}
		
	
	/**
	 获取当前用户发送的短信息
	 */
	@SuppressWarnings("null")
	public Map<String,Object> getMessageList(Date startdate,Date endate,String companyname,String content,String mobile,Integer type,HttpServletRequest req){
		Users user = (Users) req.getSession().getAttribute(apps.transmanager.weboffice.util.beans.PageConstant.LG_SESSION_USER);
		//mobilemessage.setApplicant(user);
		List<MobileSendInfo> mobilemessageList = mobilemessageService.getMessageList(startdate,endate,companyname,content,mobile,type,req);
		Map<String,Object> map = new HashMap<String, Object>();
		List<Object> list=new ArrayList<Object>();
		for (MobileSendInfo mobileSendInfo : mobilemessageList) {
			Map<String,Object> temp=new HashMap<String, Object>();
			temp.put("id",mobileSendInfo.getId());
			temp.put("company", mobileSendInfo.getCompanyname());
			temp.put("type",getType(mobileSendInfo.getType()));
			temp.put("telnumber", mobileSendInfo.getMobile());
			temp.put("content",mobileSendInfo.getContent());
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			temp.put("time",f.format(mobileSendInfo.getSenddate()));
			list.add(temp);
		}
		map.put("data", list);
		return map;
	}
	
	private static String getType(Integer type){
		if (type.intValue()==Constant.REGEST.intValue()) {
			return "外部注册";
		}else if(type.intValue()==Constant.MEETING.intValue()){
			return "会议";
		}else if(type.intValue()==Constant.SHAREINFO.intValue()){
			return "共享";	
		}else if(type.intValue()==Constant.COMPANYFILE.intValue()){
			return "企业文库";	
		}else if(type.intValue()==Constant.GROUPTEAM.intValue()){
			return "群组协作";	
		}else if(type.intValue()==Constant.MOBILESIGN.intValue()){
			return "移动签批";	
		}else if(type.intValue()==Constant.TRANSSPLIT.intValue()){
			return "事务分发";	
		}else if(type.intValue()==Constant.SHAREDIALOG.intValue()){
			return "共享日程";	
		}
		return "";
	}
	
	
}
