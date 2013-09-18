package apps.moreoffice.ext.sms.service;

import apps.moreoffice.ext.sms.model.SmsMessage;
import apps.moreoffice.ext.sms.utils.SmsException;

public interface SmsService extends Runnable
{

	/**
	 * 发送短信，支持群发和单发
	 * 
	 * @param smsMessage
	 */

	public void sendSms(SmsMessage smsMessage) throws SmsException;

	/**
	 * 读取短信
	 * 
	 * @return 接收到的短信
	 * @throws SmsException
	 */
	public SmsMessage receiveSms() throws SmsException;

	/**
	 * 设置短信发送路径，即把短信写到哪个路径下
	 * 
	 * @param writeUrl
	 *            一个String，短信发送路径
	 */
	public void setWriteUrl(String writeUrl);

	/**
	 * 设置短信接收路径，即到哪个路径下读取短信
	 * 
	 * @param readUrl
	 *            一个String，短信接收路径
	 */
	public void setReadUrl(String readUrl);

}
