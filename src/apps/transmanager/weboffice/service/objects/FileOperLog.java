package apps.transmanager.weboffice.service.objects;

import java.io.DataOutput;
import java.text.DateFormat;
import java.util.Date;

import apps.transmanager.weboffice.constants.server.LogConstant;
import apps.transmanager.weboffice.util.server.LogsUtility;

public class FileOperLog  implements ILogs
{

	private String fileName; // 文件名
	private String filePath; // 文件路径
	private String userName; // 用户名
	private String realName;// 真实用户名
	private String role; // 角色名
	private String ip; // 操作时间
	private String operType; // 操作类型

	/**
	 * 构造函数
	 * 
	 * @param fileName
	 *            文件名
	 * @param filePath
	 *            文件路径
	 * @param userName
	 *            用户名
	 * @param realName
	 *            真实姓名
	 * @param role
	 *            用户角色名
	 * @param operType
	 *            操作类型
	 * @param operTime
	 *            操作时间
	 */
	public FileOperLog(String fileName, String filePath, String userName,
			String realName, String role, String operType, String ip)
	{
		this.fileName = fileName;
		this.filePath = filePath;
		this.userName = userName;
		this.realName = realName;
		this.role = role;
		this.operType = operType;
		this.ip = ip;
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
		
		String operFileName = getFileName();
		
		if ((getFileName() == null || getFileName().equals(""))
				&& (filePath != null && !filePath.equals("")))
		{
			String filePathTemp = filePath.replaceAll(	"\\\\", "/");
			operFileName = filePathTemp.substring(filePathTemp.lastIndexOf("/") + 1);
		}
		
		sb.append(LogConstant.USER_NAME);
		sb.append(userName);
		sb.append("; ");
		sb.append(LogConstant.USER_REALNAME);
		sb.append(realName);
		sb.append(";  ");
		sb.append(LogConstant.OPERATE_TIME);
		sb.append(date);
		sb.append(";  "); 
		sb.append(LogConstant.IP_ADDRESS);
		sb.append(ip);
		sb.append(";  ");
		sb.append(LogConstant.OPERATE_TYPE);
		sb.append(operType);
		sb.append(";  ");
		sb.append(LogConstant.OPERATE_FILENAME);
		sb.append(operFileName);
		sb.append(";  ");
		sb.append(LogConstant.OPERATE_FILENAME);
		sb.append(filePath);
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

	
	public String getRealName()
	{
		return realName;
	}

	public void setRealName(String realName)
	{
		this.realName = realName;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole(String role)
	{
		this.role = role;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getOperType()
	{
		return operType;
	}

	public void setOperType(String operType)
	{
		this.operType = operType;
	}

}
