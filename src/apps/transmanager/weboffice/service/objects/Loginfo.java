package apps.transmanager.weboffice.service.objects;

import java.io.DataOutput;
import java.text.DateFormat;
import java.util.Date;

import apps.transmanager.weboffice.constants.server.LogConstant;
import apps.transmanager.weboffice.util.server.LogsUtility;

public class Loginfo implements ILogs
{
	private String userID;
	private String IPAddr;
	private String operateType;

	public Loginfo(String userID, String ip, String operateType)
	{
		this.userID = userID;
		this.IPAddr = ip;
		this.operateType = operateType;
	}

	/**
	 * 把log内容保存到给定的inputstream流中。
	 * 
	 * @param ot
	 */
	public void writeLogs(DataOutput ot)
	{
		DateFormat formatter = DateFormat.getDateTimeInstance();
		String date = formatter.format(new Date());
		StringBuffer sb = new StringBuffer();
		sb.append(LogConstant.USER_NAME);
		sb.append(userID);
		sb.append("; ");
		sb.append(LogConstant.OPERATE_TIME);
		sb.append(date);
		sb.append(";  "); 
		sb.append(LogConstant.IP_ADDRESS);
		sb.append(IPAddr);
		sb.append(";  ");
		sb.append(LogConstant.OPERATE_TYPE);
		sb.append(operateType);
		sb.append("\n\r");
		try
		{
			ot.writeUTF(sb.toString());
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
	}

	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}

	public String getIPAddr()
	{
		return IPAddr;
	}

	public void setIPAddr(String addr)
	{
		IPAddr = addr;
	}

	public String getOperateType()
	{
		return operateType;
	}

	public void setOperateType(String operateType)
	{
		this.operateType = operateType;
	}

}
