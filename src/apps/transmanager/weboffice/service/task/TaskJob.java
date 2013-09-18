package apps.transmanager.weboffice.service.task;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.CronTriggerBean;

import apps.transmanager.weboffice.databaseobject.MobileBackInfo;
import apps.transmanager.weboffice.databaseobject.Scheduletask;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.handler.UserOnlineHandler;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.util.server.Client;

public class TaskJob {

	private Scheduler scheduler;
	private FileSystemService fileSystemService;
    private String backpath = "D:/Tomcat6.0/webapps/WebEIO";
    private String rootpath = "D:/Tomcat6.0/webapps/WebEIO";
	public void backUp()
	{
		try
		{
			try
			{
				if (WebConfig.receiveBack)//只有设置了接收短信才能接受
				{
					Client client=new Client();
					String backinfo=client.mo();//获取回复的短信
					
			    	System.out.println("backinfo===="+backinfo);
			    	if (backinfo!=null && backinfo.length()>0)
			    	{
			    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
						if (backinfo.startsWith("-")) {
							//接收失败的情况，输出失败信息
							System.out.print(backinfo+"  序列号或密码不对。-6为未加密，-2为加密不对");
						}else if ("1".equals(backinfo)) {
							System.out.print("无可接收信息");
						}else {
							//多条信息的情况，以回车换行分割
							String[] result = backinfo.split("\r\n");
							for(int i=0;i<result.length;i++)
							{
								//内容做了url编码，在此解码，编码方式gb2312
								String infos=result[i];
	//								String infos=URLDecoder.decode(result[i], "gb2312");
								//95153359,157589222222,15251664207,好的,2012-11-29 11:05:31------------------数据格式
								MobileBackInfo mobileBackInfo=new MobileBackInfo();
								mobileBackInfo.setTotalback(infos);//不管是否解析成功都必须将内容保存，确保信息不丢失
								mobileBackInfo.setAdddate(new Date());
								try
								{
									int index=infos.indexOf(",");//主要考虑短信中有,号的问题
									//第一个不保存
									infos=infos.substring(index+1);
									index=infos.indexOf(",");
									int ext=Integer.parseInt(infos.substring(6,index));
									mobileBackInfo.setExt(ext);//扩展码
									infos=infos.substring(index+1);
									
									index=infos.indexOf(",");
									mobileBackInfo.setMobile(infos.substring(0,index));//手机号
									infos=infos.substring(index+1);
									
									index=infos.lastIndexOf(",");
									mobileBackInfo.setBackcontent(infos.substring(0,index));
									
									mobileBackInfo.setBackdate(sdf.parse(infos.substring(index+1)));
								}
								catch (Exception ee)
								{
									ee.printStackTrace();
								}
								jqlService.save(mobileBackInfo);
								
								UserOnlineHandler.updateTransInfo(mobileBackInfo,jqlService);
							}
						}			
						
			    	}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			//以上是收取短信
			
			//System.out.println("start=========");
			if (scheduler==null)
			{
				//System.out.println("scheduler is null=========");
				scheduler = (Scheduler)ApplicationContext.getInstance().getBean("schedulerFactory");
			}
			CronTriggerBean trigger=(CronTriggerBean)scheduler.getTrigger("cronTrigger", Scheduler.DEFAULT_GROUP);
			String firstExp=trigger.getCronExpression();
			if (fileSystemService==null)
			{
				//System.out.println("fileSystemService====null");
				fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(
			            FileSystemService.NAME);
			}
			//System.out.println("start2222222=========");
			Scheduletask task=fileSystemService.getScheduletask(Integer.valueOf(0));
			if(task == null)
			{
				return;
			}
			String path=task.getBackpath();
			String content=task.getSchedulecontent();
			//"0 32 11 * * ?"
			//从数据库中取日志任务
			//System.out.println(firstExp);
			if (content==null || content.length()==0)
			{
				trigger.setCronExpression("0 0 0 L * ?");
				scheduler.rescheduleJob("cronTrigger", Scheduler.DEFAULT_GROUP, trigger);
			}
			else
			{
				if (!firstExp.equalsIgnoreCase(content))
				{
					trigger.setCronExpression(content);
					scheduler.rescheduleJob("cronTrigger", Scheduler.DEFAULT_GROUP, trigger);
				}
				else
				{
					String startday=task.getStartday();
					SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
					//boolean isstart=false;
					if (startday.length()>=8)
					{
						Date date=sd.parse(startday);
						if (System.currentTimeMillis()>date.getTime())
						{
							//isstart=true;
						}
					}
	
					if ("used".equals(task.getState()))
					{
//						UserFilter.backing=1;
						UserOnlineHandler.clearAllSession();
						backup(0,3,new Date(),path);
//						UserFilter.backing=0;
					}
					//执行备份
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//System.out.println("AutoBackup error"+e.getMessage());
		}
//		UserFilter.backing=0;
	}
	public int backup(int type, int taskType, Date startDate)
    {
        return backup(type, taskType, startDate, null);
    }

    /**--------------------------------- 文件读取相关结束 ---------------------------------------------**/

    /**--------------------------------- 以下为文件操作相关 ---------------------------------------------**/

    /**
     * 备份数据 
     * 
     * @param type 备份类型 = 0 整库备份
     *                    = 1 增量备份
     * @param taskType 定时增量备份时的任务类型
     *                  = 0 每天
     *                  = 1 每周
     *                  = 2 每月
     *                  = 3 一次性


     * @param startDate 定时备份的开始时间


     * 这里参数定的不合理


     */

    public int backup(int type, int taskType, Date startDate, String foldpath)
    {
        //List list=getBackFoldList();

        String backname = DateFormat.getDateInstance().format(new Date());
        String backFile = backname + ".bk.sql";
        String backDir = backpath + File.separatorChar + "backupDir";
        String comPath = rootpath + File.separatorChar + "WEB-INF";

        if (foldpath != null && foldpath.length() > 0)
        {
            backDir += File.separatorChar + foldpath;
        }
        String os = System.getProperty("os.name");
        boolean isWindow = false;
        if (os.indexOf("Windows") >= 0)
        {
            isWindow = true;
        }
        //System.out.println("os=============="+os);
        File file = new File(backDir);
        if (!file.exists())
        {
            file.mkdir();
        }
        long freespace = file.getFreeSpace();
        //System.out.println("=================="+backDir+"          "+os);
        //System.out.println("isDirectory"+file.isDirectory());
        //System.out.println(freespace);
        if (freespace < 100000000)
        {
            System.out.println("Not Free space!!!");
            return -1;//没有空间
        }
        if (!file.canWrite())
        {
            return -2;//没有可写权限
        }
        WebConfig webConfig = (WebConfig)ApplicationContext.getInstance().getBean("webConfigBean");

        // 为false的话,说明jackrabbit用的本地文件，则走本地文件的备份流程,

        comPath += File.separatorChar + "lib" + File.separatorChar;
        //System.out.println("comPath================"+comPath);
        String fileurl = "";
        //if ("false".equals(webConfig.getDatabase()))
        {
            backupForLocal(type, taskType, startDate, backDir, backname);
        }
        /*else
        {
            Properties config = jcrService.getFileSystemConfig();
            String user = config.getProperty("user");

            if (user == null && !new File(comPath).exists())
            {
                return -3;//备份失败
            }
            String ps = config.getProperty("password");
            String url = config.getProperty("url");
            fileurl = url;
            String[] cmd = baskstr(url, user, ps, comPath, backDir, backFile, isWindow);
            runBackup(cmd);
        }*/
        if (webConfig.getSysdatabase().indexOf(fileurl) > 0
            || fileurl.indexOf(webConfig.getSysdatabase()) > 0)
        {

        }
        else
        {
            backFile = backFile.replace(".zip", ".bk.sql");
            runBackup(baskstr(webConfig.getSysdatabase(), webConfig.getSysuser(),
                webConfig.getSyspsw(), comPath, backDir, backFile, isWindow));
        }
        return 0;
    }

    private String[] baskstr(String url, String user, String psw, String comPath, String backDir,
        String backFile, boolean isWindow)
    {
        int index = url.indexOf("//");
        url = url.substring(index + 2);
        index = url.indexOf(":");
        String ip = "";
        if (index > 0)
        {
            ip = url.substring(0, index);
        }
        else
        {
            index = url.indexOf("/");
            ip = url.substring(0, index);
        }
        url = url.substring(index + 1);
        index = url.indexOf("/");
        // 解析出database
        if (index > 0)
        {
            url = url.substring(index + 1);
        }
        index = url.indexOf("?");
        String database = url;
        if (index > 0)
        {
            database = url.substring(0, url.indexOf("?"));
        }
        //System.out.println("ip = " + ip + "   database = " + database);
        String[] cmd;
        //System.out.println("the is window ========================  "+isWindow);
        if (isWindow)
        {
            if (comPath.startsWith("/"))
            {
                comPath = comPath.substring(1);
            }
            comPath = comPath.replaceAll(" ", "\" \"").replaceAll("%20", "\" \"");
            comPath = comPath.replaceAll("\\\\", "/");
            if (backDir.startsWith("/"))
            {
                backDir = backDir.substring(1);
            }
            backDir = backDir.replaceAll(" ", "\" \"").replaceAll("%20", "\" \"");
            cmd = new String[]{
                "cmd",
                "/c",
                "" + comPath + "mysqldump.exe -h" + ip + " -u" + user + " -p" + psw
                    + " --default-character-set=utf8 --opt --extended-insert=false"
                    + " --triggers -R --hex-blob --single-transaction " + database + ">" + backDir
                    + File.separatorChar + backFile};
        }
        else
        {
            cmd = new String[]{
                "/bin/sh",
                "-c",
                " " + comPath + "mysqldump -h" + ip + " -u" + user + " -p" + psw
                    + " --default-character-set=utf8 --opt --extended-insert=false"
                    + " --triggers -R --hex-blob --single-transaction " + database + ">" + backDir
                    + File.separatorChar + backFile};
        }
        return cmd;
    }

    private int runBackup(String[] cmd)
    {
        Runtime rt = Runtime.getRuntime();
        try
        {
            Process p = rt.exec(cmd);
            p.waitFor();
            InputStream in = p.getErrorStream();
            byte[] b = new byte[in.available()];
            in.read(b);
            String str = new String(b);
            //System.out.println("p = " + p.exitValue() + "  error = " + str);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return -3;//备份失败
        }
        return 0;
    }
    /**
     * 备份数据(jackrabbit数据放在本地)
     * 
     * @param type 备份类型 = 0 整库备份
     *                    = 1 增量备份
     * @param taskType 定时增量备份时的任务类型
     *                  = 0 每天
     *                  = 1 每周
     *                  = 2 每月
     *                  = 3 一次性


     * @param startDate 定时备份的开始时间


     */
    public int backupForLocal(int type, int taskType, Date startDate, String backDir,
        String backname)
    {
        String fileName = backDir + File.separatorChar + backname + ".zip";
        File zipFile = new File(fileName);
        if (zipFile.exists())
        {
            zipFile.delete();
        }
        try
        {
            jackrabbitToFile(backDir);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipFile(zipOut, new File(backDir + File.separatorChar + "temp"), "");
            zipOut.setEncoding("UTF-8");
            zipOut.close();
            // 存到本地
            /*FileInputStream fis = new FileInputStream(zipFile);
            byte[] b = new byte[8*1024];
            HttpServletResponse response = getThreadLocalResponse();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            ServletOutputStream out = response.getOutputStream();
            int len = 0;
            while ((len = fis.read(b)) > 0)
            {
                out.write(b, 0, len);
            }
            fis.close();
            out.close();*/
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 先把jackrabbit中文件导出放在本地


     * @return
     */
    public void jackrabbitToFile(String backDir)
    {
        try
        {
            File file = new File(backDir + File.separatorChar + "temp");
            if (!file.exists())
            {
                file.mkdir();
            }
            JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
            jcrService.exportSystemView(file.getAbsolutePath(), false);
            /*String str = file.getAbsolutePath() + File.separatorChar;
            JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(
                JQLServices.NAME);
            List alluser = jqlService.findAll(Users.class);
            if (alluser != null && alluser.size() > 0)
            {
                int size = alluser.size();
                String email;
                Users user;
                for (int i = 0; i < size; i++)
                {
                    user = (Users)alluser.get(i);
                    email = user.getSpaceUID();
                    // 此处要把管理员过滤掉
                    if (user.getUserId() == 0)
                    {
                        continue;
                    }
                    //System.out.println(i +  "  " + email);
                    email = email.replace('@', '_');
                    jcrService.exportSystemView(new File(str + email + ".xml"), "/" + email, i == 0);
                }
                //System.out.println("export end");
            }*/
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param out
     * @param sourFile
     * @param base
     */
    public void zipFile(ZipOutputStream out, File sourFile, String base)
    {
        try
        {
            if (sourFile.isDirectory())
            {
                ZipEntry entry = new ZipEntry(base + "/");
                entry.setUnixMode(755);
                out.putNextEntry(entry);

                File[] subFile = sourFile.listFiles();
                base = base.length() > 0 ? base + "/" : "";
                for (File tempFile : subFile)
                {
                    zipFile(out, tempFile, base + tempFile.getName());
                }
            }
            else
            {
                ZipEntry entry = new ZipEntry(base);
                entry.setUnixMode(644);
                out.putNextEntry(entry);
                FileInputStream in = new FileInputStream(sourFile);
                byte[] buffer = new byte[1024 * 10];
                int size;
                while ((size = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, size);
                }
                in.close();
                sourFile.delete();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取服务器备份文件夹树


     * list中存放Map[文件夹名[String],文件夹中的文件夹[list]]，没有子文件夹就为null
     */
    public List getBackFoldList()
    {
        String filetend = ".bk.sql";
        WebConfig webConfig = (WebConfig)ApplicationContext.getInstance().getBean("webConfigBean");
        // 为false的话,说明jackrabbit用的本地文件，则走本地文件的备份流程
        if ("false".equals(webConfig.getDatabase()))
        {
            filetend = ".zip";
        }

        List list = new ArrayList();
        String backDir = backpath + "/backupDir";
        File file = new File(backDir);
        if (!file.exists())
        {
            file.mkdir();
        }
        File[] files = file.listFiles();
        if (files != null)
        {
            return getFold(files, "backupDir" + File.separatorChar, filetend, false);
        }
        else
        {
            return null;
        }
    }

    private List getFold(File[] files, String backDir, String filetend, boolean isfiles)
    {
        List list = new ArrayList();
        if (files != null)
        {
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isDirectory())
                {
                    if ("temp".equalsIgnoreCase(files[i].getName()))
                    {
                        continue;
                    }
                    Map map = new HashMap();
                    String temppath = files[i].getAbsolutePath();
                    int index = temppath.indexOf(backDir);
                    if (index >= 0)
                    {
                        temppath = temppath.substring(index + backDir.length());
                    }
                    map.put("foldname", temppath);
                    // System.out.println("foldname================"+temppath);
                    if (files[i].listFiles() != null)
                    {
                        map.put(temppath, getFold(files[i].listFiles(), backDir, filetend, isfiles));
                    }
                    else
                    {
                        map.put(temppath, null);
                    }
                    list.add(map);
                }
                else if (isfiles)
                {
                    String tempname = files[i].getAbsolutePath();
                    int index = tempname.indexOf(backDir);
                    if (index >= 0)
                    {
                        tempname = tempname.substring(index + backDir.length());
                    }
                    if (tempname.endsWith(filetend))
                    {
                        index = tempname.lastIndexOf(filetend);
                        //tempname=tempname.substring(0,index);
                        if (index > 0)
                        {
                            //System.out.println("filename====="+tempname);
                            list.add(tempname);
                        }
                    }
                }
            }
        }
        return list;
    }


	public Scheduler getScheduler() {
		return scheduler;
	}



	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}




	public FileSystemService getFileSystemService() {
		return fileSystemService;
	}




	public void setFileSystemService(FileSystemService fileSystemService) {
		this.fileSystemService = fileSystemService;
	}
}
