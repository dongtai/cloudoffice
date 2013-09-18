package apps.transmanager.weboffice.service.config;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.james.cli.probe.ServerProbe;
import org.apache.james.cli.probe.impl.JmxServerProbe;

import apps.moreoffice.workflow.process.WorkflowManagementFactory;
import apps.moreoffice.workflow.server.WorkFlowServer;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.MailService;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.WebTools;

public class WebConfig
{
	public final static String TEMPFILE_FOLDER = "filedisks"+ "/" +"tempfile";//全部改到data目录下
	public final static String SRCSMS_FOLDER = "filedisks"+ "/" +"smspath";//短消息文件
	public final static String TAGSMS_FOLDER = "filedisks"+ "/" +"tagsmspath";//短消息文件转换后的文件
	public final static String SENDMAIL_FOLDER = "filedisks"+ "/" +"sendmailfile";
	private boolean autoConfig;     // 集群时候，是否自动获取服务器的ip地址
	private String bindIp;          // 集群时候，手动配置的服务器ip地址
	public static boolean cluster;        // 是否是集群部署
	private String clusterSchema;   
	public static List<String> clusterIPs;
	private String clusterIPString;
	private String database;        // 文件库是否采用数据库存储方式
	private String sysdatabase;     // 数据库的URL
	private String sysuser;         // 数据库用户名
	private String syspsw;          // 数据库密码
	private String backpath;        // 备份目录
	private boolean workflowServiceEnable;   // 是否启动工作流服务
	private boolean workflowProcessEnable;   // 是否启动工作流
	private String encoding;                 // 系统编码方式
	private String logPath;					 // 系统log位置
	private String logLevel;				 // 系统log级别
	private boolean delLog;					 // 系统log数量多的时候，是否删除	
	private String databaseType;             // 系统使用数据库类别
	private boolean console;                 // 系统log输出位置是否是控制台
	private boolean entryptEnable;           // 
	private String printType;                // 
	private String printIP;                  // 
	private String printPort;                //  
	private String printEnable;
	public static String userLogPath;              // 用户日志位置
	private String portraitPath;             // 各种头像的根位置	
	public static String userPortrait;             // 用户头像相对位置，
	public static  String groupPortrait;            // 组头像相对位置
	public static  String orgPortrait;              // 组织头像相对位置
	public static String userPortraitPath;             // 用户头像绝对位置
	public static  String groupPortraitPath;            // 组头像绝对位置
	public static  String orgPortraitPath;              // 组织头像绝对位置
	public static String tempFilePath;             // 系统临时文件位置
	public static String srcSmsPath;             // 短消息原文件
	public static String tagSmsPath;             // 短消息目标文件
	public static String sendMailPath;             // 系统发送邮件，附件位置	
	public static String webContextPath;          // 系统运行时刻的根目录。
	private boolean initFlag = false;
	public static boolean mailEnable;
	public static String mailServerAddress;
	public static int mailPort;
	public static String mailDomain;
	public static String publicserver;//发送邮件的服务器
	public static String publicname;//发送邮件的账号
	public static String publicpass;//发送邮件的密码
	public static String publicdisplay;//邮件账号显示的名称
	public static String serverurl;//手机短息发送的服务器地址
	public static String clientSDK;//手机短息发送的账号
	public static String clientPWD;//手机短息发送的密码
	public static String shortNUM;//手机短息发送的短号
	public static boolean receiveBack;//是否接受回复的短信
	public static boolean mobileopened;//是否打开手机短信

	public static String outwebserviceurl;//外部WEBSERVICE地址
	public static String nginxiptag;//nginx代理IPtag
	public static String nginxforwardtag;//nginx代理forward tag
	public static Float defaultsize;//用户默认空间大小
	//public static String cloudtype="public";//云办公类别，public为公云，private为私云，私云在账号前面没有单位编号

	private String language = "zh";
	private String country = "CN";
	public static boolean cloudPro;           // 云办公类别，true为公云，false为私云，私云在账号前面没有单位编号
	
	public static Map mobileregistmap=new HashMap();//存放手机注册码，每日0时清空，如果邮箱注册也需要再加
	public static String importfilespath;//导入外部目录的文件
	public static  boolean importFiles;
	public static boolean importtemplate;//导入外部模板
	public static String outsameusertag="server1";//外部系统用户同步接口
	public static String serverurlip;//服务器的实际IP
	public static String serverurlname;//服务器的域名,如果不是80端口需加上端口号
	public static String smsname;//发送短信的系统名称
	public static boolean replacetag;//是否需要替换logo，要与cloudPro配合使用
	public static boolean identifycode;//是否需要验证码
	public static boolean register;//是否需要注册
	public static boolean signhistoryview=false;//签批历史记录是否以table显示,true为table显示
	public static String accesstype="http";//访问类型(https走的是网关，常州要用的)
	public static String projectname="";//项目名称
	public static boolean signmodifydefault=true;//签批默认值是否根据文件类型分类，true分类，false不分类



	public void init(ServletContext sc)
	{
		if (initFlag)
		{
			return;
		}
		initFlag = true;
		initPath(sc);
		WebTools.setEncoding(encoding);
		String webServerPath = sc.getRealPath("") + File.separatorChar + logPath;		
		LogsUtility.init(webServerPath, delLog, logLevel, console, userLogPath);
		try
		{
			if (isCluster())
			{
				if (clusterIPString != null && !clusterIPString.equals(""))
				{
					String[] ips = clusterIPString.split(";");
					clusterIPs = new ArrayList<String>();
					for (String tempIp : ips)
					{
						clusterIPs.add(clusterSchema + "://" + tempIp);
					}
				}
				
				if (isAutoConfig())
				{
			        Enumeration<NetworkInterface> netInt = NetworkInterface.getNetworkInterfaces();
			        NetworkInterface ip;
			        while(netInt.hasMoreElements())
			        {
			        	ip = netInt.nextElement();
			        	Enumeration<InetAddress> ips = ip.getInetAddresses();
			        	InetAddress ia;
			        	while(ips.hasMoreElements())
			        	{
			        		ia = ips.nextElement();
			        		//System.out.println("he ips i   ="+ia+"======="+ia.isLoopbackAddress()+"===="+ia.isAnyLocalAddress()+"====="+ia.isLinkLocalAddress()
			        		//		+"=====|||"+ia.isSiteLocalAddress());
			        		if (!ia.isLoopbackAddress() && ia instanceof Inet4Address)//ia.isSiteLocalAddress())
			        		{
			        			System.setProperty("cluster.udp.bind_addr", ia.getHostAddress());
			        		}
			        	}
			        }
				}
				else
				{
					System.setProperty("cluster.udp.bind_addr", getBindIp());
				}
			}
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
		if (workflowServiceEnable)
		{
			WorkFlowServer pts = (WorkFlowServer)ApplicationContext.getInstance().getBean("workFlowServer");
			pts.startService();
			/*List<String> l = new ArrayList<String>();
			l.add("abc");
			l.add("xuabc");
			l.add("xuabc1");
			l.add("xuabc3");
			l.add("userAdmin");
			l.add("auditAdmin");
			pts.addUsers(l);*/
		}
		if (workflowProcessEnable)
		{
			WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");
			wmf.init();
			/*Map<String, Object> params = new HashMap<String, Object>();
			params.put("actorId", "tempabc11");
			wmf.getProcessHandler().startProcess("com.yozo.dispatch_doc", "xuabcX", params, "iqwerqpowieupqowit");*/
		}
		syncAddDomain();
	}
	
	private void initPath(ServletContext sc)
	{
		webContextPath = sc.getRealPath("");
		userLogPath = sc.getRealPath(userLogPath);            // 用户日志路径
		tempFilePath =  sc.getRealPath(TEMPFILE_FOLDER);           // 系统临时文件路径
		srcSmsPath =  sc.getRealPath(SRCSMS_FOLDER);             // 短消息原文件
		tagSmsPath =  sc.getRealPath(TAGSMS_FOLDER);			//短消息附件转后后的文件
		sendMailPath = sc.getRealPath(SENDMAIL_FOLDER);        // 系统发送邮件附件位置
		
		File file = new File(userLogPath);
		if (!file.exists())
		{
			file.mkdirs();
		}
		
		if (portraitPath != null && portraitPath.length() > 0) // 用户各种头像图片数据位置
		{
			userPortrait = portraitPath + "/personalset2";
			groupPortrait = portraitPath + "/groups";
			orgPortrait = portraitPath + "/orgs";
			
			userPortraitPath = sc.getRealPath(userPortrait);
			file = new File(userPortraitPath);
			if (!file.exists())
			{
				file.mkdirs();
			}
			groupPortraitPath = sc.getRealPath(groupPortrait);
			file = new File(groupPortraitPath);
			if (!file.exists())
			{
				file.mkdirs();
			}
			orgPortraitPath = sc.getRealPath(orgPortrait);
			file = new File(orgPortraitPath);
			if (!file.exists())
			{
				file.mkdirs();
			}
			userPortrait = "/" + userPortrait + "/";
			groupPortrait = "/" +  groupPortrait + "/";
			orgPortrait = "/" +  orgPortrait + "/";
		}
	}
	
	public String getUserLogPath()
	{
		return userLogPath;
	}

	public void setUserLogPath(String userLogPath0)
	{
		userLogPath = userLogPath0;
	}

	public String getUserPortraitPath()
	{
		return userPortrait;
	}
	
	public String getGroupPortraitPath()
	{
		return groupPortrait;
	}
	
	public String getOrgPortraitPath()
	{
		return orgPortrait;
	}
	
	public String getPortraitPath()
	{
		return portraitPath;
	}

	public void setPortraitPath(String portraitPath)
	{
		this.portraitPath = portraitPath;
	}

	public boolean isConsole()
	{
		return console;
	}

	public void setConsole(boolean console)
	{
		this.console = console;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public boolean getWorkflowProcessEnable()
	{
		return workflowProcessEnable;
	}

	public void setWorkflowProcessEnable(boolean workflowProcessEnable)
	{
		this.workflowProcessEnable = workflowProcessEnable;
	}

	public boolean getWorkflowServiceEnable()
	{
		return workflowServiceEnable;
	}

	public void setWorkflowServiceEnable(boolean workflowEnable)
	{
		this.workflowServiceEnable = workflowEnable;
	}

	public boolean isAutoConfig()
	{
		return autoConfig;
	}

	public void setAutoConfig(boolean autoConfig)
	{
		this.autoConfig = autoConfig;
	}

	public String getBindIp()
	{
		return bindIp;
	}

	public void setBindIp(String bindIp)
	{
		this.bindIp = bindIp;
	}

	public boolean isCluster()
	{
		return cluster;
	}

	public void setCluster(boolean cluster)
	{
		WebConfig.cluster = cluster;
	}

	public void setDatabase(String db)
	{
		this.database = db;
	}

	public String getDatabase()
	{
		return this.database;
	}

	public String getSysdatabase()
	{
		return sysdatabase;
	}

	public void setSysdatabase(String sysdatabase)
	{
		this.sysdatabase = sysdatabase;
	}

	public String getSysuser()
	{
		return sysuser;
	}

	public void setSysuser(String sysuser)
	{
		this.sysuser = sysuser;
	}

	public String getSyspsw()
	{
		return syspsw;
	}

	public void setSyspsw(String syspsw)
	{
		this.syspsw = syspsw;
	}

	public String getBackpath()
	{
		return backpath;
	}

	public void setBackpath(String backpath)
	{
		this.backpath = backpath;
	}

	public String getLogPath()
	{
		return logPath;
	}

	public void setLogPath(String logPath)
	{
		this.logPath = logPath;
	}

	public String getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(String logLevel)
	{
		this.logLevel = logLevel;
	}

	public boolean isDelLog()
	{
		return delLog;
	}

	public void setDelLog(boolean delLog)
	{
		this.delLog = delLog;
	}

    public void setDatabaseType(String databaseType)
    {
        this.databaseType = databaseType;
    }

    public String getDatabaseType()
    {
        return databaseType;
    }
    public boolean isEntryptEnable() {
		return entryptEnable;
	}

	public void setEntryptEnable(boolean entryptEnable) {
		this.entryptEnable = entryptEnable;
	}

	public String getPrintType() {
		return printType;
	}

	public void setPrintType(String printType) {
		this.printType = printType;
	}

	public String getPrintIP() {
		return printIP;
	}

	public void setPrintIP(String printIP) {
		this.printIP = printIP;
	}

	public String getPrintPort() {
		return printPort;
	}

	public void setPrintPort(String printPort) {
		this.printPort = printPort;
	}
	public String getPrintEnable() {
		return printEnable;
	}

	public void setPrintEnable(String printEnable) {
		this.printEnable = printEnable;
	}

	
	public boolean getMailEnable()
	{
		return mailEnable;
	}

	public void setMailEnable(boolean mailEnable)
	{
		WebConfig.mailEnable = mailEnable;
	}

	public String getMailServerAddress()
	{
		return mailServerAddress;
	}

	
	public void setMailServerAddress(String mailServerAddress)
	{
		WebConfig.mailServerAddress = mailServerAddress;
	}

	public int getMailPort()
	{
		return mailPort;
	}

	public void setMailPort(int mailPort)
	{
		WebConfig.mailPort = mailPort;
	}
		
	public String getMailDomain()
	{
		return mailDomain;
	}

	public void setMailDomain(String mailDomain)
	{
		WebConfig.mailDomain = mailDomain;
	}
		

	public String getClusterSchema()
	{
		return clusterSchema;
	}

	public void setClusterSchema(String clusterSchema)
	{
		this.clusterSchema = clusterSchema;
	}

	public static List<String> getClusterIPs()
	{
		return clusterIPs;
	}

	public static void setClusterIPs(List<String> clusterIPs)
	{
		WebConfig.clusterIPs = clusterIPs;
	}

	public String getClusterIPString()
	{
		return clusterIPString;
	}

	public void setClusterIPString(String clusterIPString)
	{
		this.clusterIPString = clusterIPString;
	}

	public static void syncAddDomain()
	{
		if (!mailEnable)
		{
			return;
		}
		try
		{
			ServerProbe probe = new JmxServerProbe(mailServerAddress, mailPort);
			String[] ds = probe.listDomains();
			boolean flag = true;
			if (ds != null && ds.length > 0)
			{
				for (String t : ds)
				{
					if (t.equalsIgnoreCase(mailDomain))
					{
						flag = false;
						break;
					}
				}				
			}
			if (flag)
			{
				probe.addDomain(mailDomain);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void syncAddMailUser(Users user)
	{
		MailService mailService = (MailService)ApplicationContext.getInstance().getBean(MailService.NAME);		
		if (!mailEnable)
		{
			mailService.createDefaultMailAccountByUser(user, null, null);
			return;
		}
		mailService.createDefaultMailAccountByUser(user, user.getUserName(), user.getRealPass());
		try
		{
			ServerProbe probe = new JmxServerProbe(mailServerAddress, mailPort);
			if (probe.getVirtualHostingEnabled())
			{
				probe.addUser(user.getRealEmail(), user.getRealPass());
			}
			else
			{
				probe.addUser(user.getUserName(), user.getRealPass());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void syncChangePass(Users user)
	{
		MailService mailService = (MailService)ApplicationContext.getInstance().getBean(MailService.NAME);		
		if (!mailEnable)
		{
			mailService.createDefaultMailAccountByUser(user, null, null);
			return;
		}
		mailService.createDefaultMailAccountByUser(user, user.getUserName(), user.getRealPass());
		try
		{
			ServerProbe probe = new JmxServerProbe(mailServerAddress, mailPort);
			if (probe.getVirtualHostingEnabled())
			{
				probe.setPassword(user.getRealEmail(), user.getRealPass());
			}
			else
			{
				probe.setPassword(user.getUserName(), user.getRealPass());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void syncRemoveMailUser(Users user)
	{
		if (!mailEnable)
		{
			return;
		}	
		try
		{
			ServerProbe probe = new JmxServerProbe(mailServerAddress, mailPort);
			if (probe.getVirtualHostingEnabled())
			{
				probe.removeUser(user.getRealEmail());
			}
			else
			{
				probe.removeUser(user.getUserName());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getLanguage(){
		return this.language;
	}
	
	public void setLanguage(String language){
		this.language = language;
	}
	
	public String getCountry(){
		return this.country;
	}
	
	public void setCountry(String country){
		this.country = country;
	}
	
	public String getPublicserver() {
		return publicserver;
	}

	public void setPublicserver(String publicserver) {
		WebConfig.publicserver = publicserver;
	}

	public String getPublicname() {
		return publicname;
	}

	public void setPublicname(String publicname) {
		WebConfig.publicname = publicname;
	}

	public String getPublicpass() {
		return publicpass;
	}

	public void setPublicpass(String publicpass) {
		WebConfig.publicpass = publicpass;
	}

	public String getPublicdisplay() {
		return publicdisplay;
	}

	public void setPublicdisplay(String publicdisplay) {
		WebConfig.publicdisplay = publicdisplay;
	}
	

	public String getServerurl() {
		return serverurl;
	}

	public void setServerurl(String serverurl) {
		this.serverurl = serverurl;
	}

	public String getClientSDK() {
		return clientSDK;
	}

	public void setClientSDK(String clientSDK) {
		this.clientSDK = clientSDK;
	}

	public String getClientPWD() {
		return clientPWD;
	}

	public void setClientPWD(String clientPWD) {
		this.clientPWD = clientPWD;
	}

	public String getShortNUM() {
		return shortNUM;
	}

	public void setShortNUM(String shortNUM) {
		this.shortNUM = shortNUM;
	}

	public String getOutwebserviceurl() {
		return outwebserviceurl;
	}

	public void setOutwebserviceurl(String outwebserviceurl) {
		this.outwebserviceurl = outwebserviceurl;
	}
	public boolean isReceiveBack() {
		return receiveBack;
	}

	public void setReceiveBack(boolean receiveBack) {
		this.receiveBack = receiveBack;
	}
	public String getNginxiptag() {
		return nginxiptag;
	}

	public void setNginxiptag(String nginxiptag) {
		this.nginxiptag = nginxiptag;
	}

	public String getNginxforwardtag() {
		return nginxforwardtag;
	}

	public void setNginxforwardtag(String nginxforwardtag) {
		this.nginxforwardtag = nginxforwardtag;
	}
	public Float getDefaultsize() {
		return defaultsize;
	}

	public void setDefaultsize(Float defaultsize) {
		this.defaultsize = defaultsize;
	}


	public boolean isCloudPro()
	{
		return cloudPro;
	}

	public void setCloudPro(boolean cloudPro0)
	{
		cloudPro = cloudPro0;
	}
	public String getImportfilespath() {
		return importfilespath;
	}

	public void setImportfilespath(String importfilespath) {
		this.importfilespath = importfilespath;
	}
	/*public String getCloudtype() {
		return cloudtype;
	}

	public void setCloudtype(String cloudtype) {
		this.cloudtype = cloudtype;
	}*/

	public boolean isImportFiles()
	{
		return importFiles;
	}

	public void setImportFiles(boolean importFiles)
	{
		this.importFiles = importFiles;
	}

	public String getOutsameusertag() {
		return outsameusertag;
	}

	public void setOutsameusertag(String outsameusertag) {
		this.outsameusertag = outsameusertag;
	}
	public boolean getImporttemplate() {
		return importtemplate;
	}

	public void setImporttemplate(boolean importtemplate) {
		this.importtemplate = importtemplate;
	}
	
	public boolean isMobileopened() {
		return mobileopened;
	}

	public void setMobileopened(boolean mobileopened) {
		this.mobileopened = mobileopened;
	}


	public String getServerurlip() {
		return serverurlip;
	}

	public void setServerurlip(String serverurlip) {
		this.serverurlip = serverurlip;
	}

	public String getServerurlname() {
		return serverurlname;
	}

	public void setServerurlname(String serverurlname) {
		this.serverurlname = serverurlname;
	}

	public boolean isIdentifycode() {
		return identifycode;
	}

	public void setIdentifycode(boolean identifycode) {
		this.identifycode = identifycode;
	}

	public boolean isRegister() {
		return register;
	}

	public void setRegister(boolean register) {
		this.register = register;
	}
	public boolean isReplacetag() {
		return replacetag;
	}

	public void setReplacetag(boolean replacetag) {
		this.replacetag = replacetag;
	}
	public String getSmsname() {
		return smsname;
	}

	public void setSmsname(String smsname) {
		this.smsname = smsname;
	}

	public boolean isSignhistoryview() {
		return signhistoryview;
	}

	public void setSignhistoryview(boolean signhistoryview) {
		this.signhistoryview = signhistoryview;
	}

	public String getAccesstype() {
		return accesstype;
	}

	public void setAccesstype(String accesstype) {
		this.accesstype = accesstype;
	}

	public String getProjectname() {
		return projectname;
	}

	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}
	public boolean isSignmodifydefault() {
		return signmodifydefault;
	}

	public void setSignmodifydefault(boolean signmodifydefault) {
		this.signmodifydefault = signmodifydefault;
	}
}
