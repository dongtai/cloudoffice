package test.weboffice.sms;

import apps.moreoffice.ext.sms.model.SmsMessage;
import apps.moreoffice.ext.sms.service.SmsService;
import apps.moreoffice.ext.sms.serviceimpl.SmsServiceForLinux;
import apps.moreoffice.ext.sms.utils.SmsException;

public class SmsServiceForLinuxTest {

	/**
	 * @param args
	 * @throws SmsException 
	 */
	public static void main(String[] args) throws Exception {
		SmsMessage smsMessage = new SmsMessage() ;
		//smsMessage.setSender("138xxxxxxxx") ;
		smsMessage.getReceivers().add("13405769567") ;
		smsMessage.setContent("李东方给您共享了一份《艾美时尚，货币三件》文档，权限：读写、下载，备注：http://aimee-online.taobao.com") ;
		
		//System.out.println("********** 同步发送 **********");
		//SmsService smsService = new SmsServiceForLinux() ;
		//smsService.sendSms(smsMessage);
		
		System.out.println("********** 异步发送 **********");
		SmsService smsServiceSyc = new SmsServiceForLinux(smsMessage) ;
		Thread smsThread = new Thread(smsServiceSyc);
		smsThread.start();
		
		System.out.println("********** 短信发送完毕 **********");
	}

}
