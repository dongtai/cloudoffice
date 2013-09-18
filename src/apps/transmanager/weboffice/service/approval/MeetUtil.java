package apps.transmanager.weboffice.service.approval;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.constants.both.MessageCons;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.Messages;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.meetmanage.MeetFiles;
import apps.transmanager.weboffice.databaseobject.meetmanage.MeetInfo;
import apps.transmanager.weboffice.databaseobject.meetmanage.MeetSameInfo;
import apps.transmanager.weboffice.databaseobject.meetmanage.MeetSave;
import apps.transmanager.weboffice.databaseobject.meetmanage.MeetTask;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.server.BackgroundSend;

public class MeetUtil {

private static MeetUtil instance=new MeetUtil();
    
    public MeetUtil()
    {    	
    	instance =  this;
    }
    public static MeetUtil instance()
    {
        return instance;
    }
    private void sendMobileinfo(List<String> mobilelist,String content,int type,List<Long> ids,boolean isback,Users user)
    {
    	if (mobilelist.size()>0)
	   	{
	    	 Thread receiveT = new Thread(new BackgroundSend(mobilelist.toArray(new String[mobilelist.size()]),content
	    			 ,user.getCompany().getId(),user.getCompany().getName()
	    			 ,Constant.MEETING,ids.toArray(new Long[ids.size()]),isback,user));
			 receiveT.start();
	   	}
    }
    private String fileNameReplace(String filename)
    {
    	if (filename!=null)
    	{
	    	int index=filename.indexOf("&amp;");
	    	if (index>=0)
	    	{
	    		filename=filename.replaceAll("&amp;", "&");
	    	}
    	}
    	return filename;
    }
    private String replaceFH(String str)
    {
    	if (str!=null && str.length()>0)
    	{
    		return str.replaceAll(";", "；");
    	}
    	return str;
    }
    public boolean setMeetWarnMessage(Long sameid,Users user,Integer worktype)
	{//会议催办，worktype为1为催办
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			MeetSameInfo meetSameInfo=(MeetSameInfo)jqlService.getEntity(MeetSameInfo.class, sameid);
			List<Long> userids=new ArrayList<Long>();
			userids.add(meetSameInfo.getMeeter().getId());
			Integer warnnum=meetSameInfo.getWarnnum();
			if (warnnum==null)
			{
				warnnum=0;
			}
			meetSameInfo.setWarnnum(warnnum+1);
			meetSameInfo.setWarndate(new Date());
			jqlService.update(meetSameInfo);
			//增加会议提醒
	        MessageUtil.instance().setOtherWarn(meetSameInfo.getMeetid(), MessageCons.SENDMEET, userids, "", "再次提醒("+meetSameInfo.getMeetname()+")", user,worktype);
	        
	        List<String> mobilelist=new ArrayList<String>();
			List<Long> tranids=new ArrayList<Long>();
			String content="";
			//消息通知此人
	        Users meeter=meetSameInfo.getMeeter();
	        if (meeter.getMobile()!=null && meeter.getMobile().length()==11)
	        {
	        	 mobilelist.add(meeter.getMobile());
				 tranids.add(meetSameInfo.getId());
				 MeetInfo meetInfo = (MeetInfo)jqlService.getEntity(MeetInfo.class, meetSameInfo.getMeetid());
				 content=user.getRealName()+"提醒您不要忘记参加“"+meetInfo.getMeetname()+"”会议,时间:"+meetInfo.getMeetdate()
						 +" "+meetInfo.getMeettime()+"地点:"+meetInfo.getMeetaddress();//暂不做短信回复功能
	        }
	        sendMobileinfo(mobilelist,content,Constant.MEETING,tranids,true,user);
	        
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
    private Date mergeDate(Date date,String time)
    {
    	try
    	{
	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    	String totime="";
//	    	if (time.toUpperCase().indexOf("AM")>=0)
//	    	{
//	    		totime=time.substring(0,time.length()-2).trim();
//	    	}
//	    	else
//	    	{
//	    		String hh=time.substring(0,time.indexOf(":")).trim();
//	    		time=(Integer.parseInt(hh)+12)+time.substring(time.indexOf(":"));
//	    		totime=time.substring(0,time.length()-2).trim();
//	    	}
	    	totime=time;
	    	return sdf.parse(df.format(date)+" "+totime);
    	}
    	catch (Exception e)
    	{
    		return date;
    	}
    }
    /**
     * 工具方法，将ID转换为人名
     * @param ids
     * @return
     */
	private String getUserName(String ids)
	{
		String usernames="";
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (ids!=null && ids.length()>0)
			{
				String[] uids=ids.split(",");
				if (uids!=null && uids.length>0)
				{
					for (int i=0;i<uids.length;i++)
					{
						//临时用ID去查询，以后再比较一下性能
						Users user=(Users)jqlService.getEntity(Users.class, Long.valueOf(uids[i]));
						if (i==0)
						{
							usernames+=user.getRealName();
						}
						else
						{
							usernames+=","+user.getRealName();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return usernames;
	}
	
	private MeetTask insertTask(Long meetid,String meetname,Users user
		,Long stateid,String stepName,long actionid,String comment
		,String meetmannames,String othermannames
		,JQLServices jqlService)
	{

		MeetTask task = new MeetTask();
		
		task.setMeetid(meetid);
		task.setMeetname(meetname);
		task.setComment(comment);
		task.setState(stateid);
		task.setActionid(actionid);
		task.setSubmiter(user.getId());
		task.setSubmitdate(new Date());
		jqlService.save(task);
		
		return task;
	}

	 
	private Fileinfo saveFile(String filePath,String fileName,Users user,FileSystemService fileSystemService)
	{
		Fileinfo info = null;
		String spaceid=user.getSpaceUID();
		if (spaceid==null){spaceid="";}
		if (filePath.startsWith("system_audit_root"))
		{
			info=new Fileinfo();
			info.setPathInfo(filePath);
			info.setFileName(fileName);
		}
		else if ((filePath.startsWith("user_") || filePath.startsWith("group_")
				 || filePath.startsWith("team_")
				 || filePath.startsWith("org_")
				 || filePath.startsWith("company_")
				 || filePath.startsWith(spaceid)
			)
				&& (filePath.indexOf("/") > 0))// 文档库文档
		{
			//文件库中的文件
			 info = fileSystemService.addAuditFile(user.getId(), filePath);
		}
		else// 本地已上传的文档放到文档库中
		{
			try
			{
				 String tempPath = WebConfig.tempFilePath + File.separatorChar;
//				 tempPath=tempPath.replace(WebConfig.TEMPFILE_FOLDER,"data"+ File.separatorChar+WebConfig.TEMPFILE_FOLDER);
				 String filename=fileNameReplace(filePath);
				 File file = new File(tempPath + filename);
				 InputStream fin = new FileInputStream(file);
				 InputStream ois = null;
 			     if (filename.toLowerCase().endsWith(".pdf"))
 			     {
 			    	ois = fin;
 			     }
 			     else
 			     {
 			    	ois=new FileInputStream(file);
 			     }
			     info = fileSystemService.addAuditFile(user.getId(), fileName, fin,  ois);
			     if (!filename.toLowerCase().endsWith(".pdf"))
			     {
			    	 ois.close();
			     }
			     fin.close();
			     file.delete();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		 return info;
	}
	private MeetInfo createMeet(String meetname,Date meetdate,String meettime,Date alldate
			,String meetaddress,String mastername,String[][] meetmannames,String[][] othermannames
			,String meetcontent,String[][] filePaths,String comment,Users user)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		 JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		 FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
//1、存储会议通知主信息
		 Long status=ApproveConstants.MEET_STATUS_WAIT;
		 MeetInfo meetInfo = new MeetInfo();
		 String allfilenames="";//所有文件名称，用,间隔
		 int nodetype=0;
		 meetInfo.setMeetname(meetname);
		 meetInfo.setMeetdate(sdf.format(meetdate));
		 meetInfo.setMeettime(meettime);
		 meetInfo.setMeetdetailtime(alldate);
		 meetInfo.setMeetaddress(meetaddress);
		 meetInfo.setMastername(mastername);
		 meetInfo.setMeetcontent(meetcontent);
		 meetInfo.setComment(comment);
		 meetInfo.setAdddate(new Date());
		 meetInfo.setSenduser(user.getId());
		 meetInfo.setSubmiter(user.getId());
		 meetInfo.setStatus(status);
		 meetInfo.setSubmitdate(new Date());
         jqlService.save(meetInfo);//保存了签批主数据
	         
         ArrayList<String> modifyfiles=new ArrayList<String>();
		 ArrayList<String> modifyname=new ArrayList<String>();
         if (filePaths!=null && filePaths.length>0)
         {
        	 for (int i=0;i<filePaths.length;i++)
        	 {
        		 //将文件存到文件库中
        		 Fileinfo info=saveFile(filePaths[i][0],filePaths[i][1],user,fileSystemService);//保存文件
        		 
        		 MeetFiles meetFiles=new MeetFiles();
        		 meetFiles.setMeetid(meetInfo.getId());
        		 meetFiles.setDocumentpath(info.getPathInfo());
        		 modifyfiles.add(info.getPathInfo());
        		 meetFiles.setFileName(info.getFileName());
        		 modifyname.add(info.getFileName());
        		 meetFiles.setUserID(user.getId());
        		 meetFiles.setAdddate(new Date());
        		 jqlService.save(meetFiles);//保存了交办附件
        	 }
         }
         String meetallnames="";
         String otherallnames="";
         
         if (meetmannames!=null && meetmannames.length>0)
         {
        	 for (int i=0;i<meetmannames.length;i++)
        	 {
        		
        		 if (i==0)
        		 {
        			 meetallnames+=meetmannames[i][0];
        		 }
        		 else
        		 {
        			 meetallnames+=";"+meetmannames[i][0];
        		 }
        	 }
         }
         if (othermannames!=null && othermannames.length>0)
         {
        	 for (int i=0;i<othermannames.length;i++)
        	 {
        		 if (i==0)
        		 {
        			 otherallnames+=othermannames[i][1]+"#"+othermannames[i][2]+"#"+othermannames[i][3];
        		 }
        		 else
        		 {
        			 otherallnames+=";"+othermannames[i][1]+"#"+othermannames[i][2]+"#"+othermannames[i][3];
        		 }
        	 }
         }
//3存储历史记录
         MeetTask task=insertTask(meetInfo.getId(),meetname,user,status,
        		 "发起",0,comment,meetallnames,otherallnames,jqlService);
					
//4、存储处理人
  	     jqlService.excute("update MeetSameInfo as a set a.isnew=a.isnew+1 where a.meetid=? ", meetInfo.getId());//isnew为0表示是最新的
         
  	     List<String> mobilelist=new ArrayList<String>();
		 List<Long> tranids=new ArrayList<Long>();
		 String content="";
    	 if (meetmannames!=null && meetmannames.length>0)//处理者
         {
    		 List<Long> userids=new ArrayList<Long>();
    		 for (int i=0;i<meetmannames.length;i++)
        	 {
        		 MeetSameInfo sameinfo=new MeetSameInfo();
        		 sameinfo.setMeetname(meetname);
        		 sameinfo.setTaskid(task.getId());
        		 sameinfo.setMeetid(meetInfo.getId());
        		 Long uid=Long.valueOf(meetmannames[i][0]);
        		 userids.add(uid);
        		 Users meeter=(Users)jqlService.getEntity(Users.class, uid);
        		 sameinfo.setMeeter(meeter);
        		 sameinfo.setActionid(null);
        		 sameinfo.setSenduser(user.getId());
        		 sameinfo.setSenddate(new Date());
		         sameinfo.setState(ApproveConstants.MEET_STATUS_START);
        		 sameinfo.setMantype(0);
		         sameinfo.setComment("");//不要将备注给处理者
		         sameinfo.setSubmitdate(new Date());
		         sameinfo.setSubmiter(user.getId());
		         jqlService.save(sameinfo);
		         if (meeter.getMobile()!=null && meeter.getMobile().length()==11)
		         {
		        	 mobilelist.add(meeter.getMobile());
					 tranids.add(sameinfo.getId());
					 
		         }
        	 }
    		//增加会议提醒
	         MessageUtil.instance().setOtherWarn(meetInfo.getId(), MessageCons.SENDMEET, userids, comment, "会议提醒("+meetname+")", user,0);
	         
	         
         }
    	 if (othermannames!=null && othermannames.length>0)//处理者
         {
    		 for (int i=0;i<othermannames.length;i++)
        	 {
        		 MeetSameInfo sameinfo=new MeetSameInfo();
        		 sameinfo.setTaskid(task.getId());
        		 sameinfo.setMeetid(meetInfo.getId());
        		 sameinfo.setMeetname(meetname);
        		 sameinfo.setMeetmanname(othermannames[i][1]);
        		 sameinfo.setMeetmanunit(othermannames[i][2]);
        		 sameinfo.setMobilenum(othermannames[i][3]);
        		 sameinfo.setActionid(null);
        		 sameinfo.setSenduser(user.getId());
        		 sameinfo.setSenddate(new Date());
		         sameinfo.setState(ApproveConstants.MEET_STATUS_START);
        		 sameinfo.setMantype(1);//其他人员
		         sameinfo.setComment("");//不要将备注给处理者
		         sameinfo.setSubmitdate(new Date());
		         sameinfo.setSubmiter(user.getId());
		         jqlService.save(sameinfo);
		         
		         if (othermannames[i][3]!=null && othermannames[i][3].length()==11)
		         {
		        	 mobilelist.add(othermannames[i][3]);
					 tranids.add(sameinfo.getId());
		         }
        	 }
         }
    	 content=user.getRealName()+"邀请您参加“"+meetInfo.getMeetname()+"”会议，时间:"+meetInfo.getMeetdate()
						 +" "+meetInfo.getMeettime()+"地点:"+meetInfo.getMeetaddress()+",议程:"+meetInfo.getMeetcontent()+",回复Y参加，N不参加，或输入替会人名";//暂不做短信回复功能
    	 sendMobileinfo(mobilelist,content,Constant.MEETING,tranids,true,user);
         return meetInfo;
	}
	/**
	 * 存储会议通知草稿
	 * @param id 草稿id
	 * @param meetname 会议名称
	 * @param meetdate 会议日期
	 * @param meettime 会议时间
	 * @param meetaddress 会议地点
	 * @param mastername 会议召开人
	 * @param meetmannames 与会人员
	 * @param othermannames 其他人员
	 * @param meetcontent  会议议程
	 * @param filePaths  会议资料
	 * @param comment  备注
	 * @param user
	 * @return
	 */
	public boolean meetSave(Long id,String meetname,Date meetdate,String meettime
			,String meetaddress,String mastername,String[][] meetmannames,String[][] othermannames
			,String meetcontent,String[][] filePaths
			,String comment
			,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			MeetSave meetSave=new MeetSave();
			if (id!=null && id>0)
			{
				meetSave=(MeetSave)jqlService.getEntity(MeetSave.class, id);
			}
			meetSave.setMeetname(meetname);//会议名称
			meetSave.setMeetdate(df.format(meetdate));//会议日期
			meetSave.setMeettime(meettime);//会议时间
			Date alldate=mergeDate(meetdate,meettime);
			meetSave.setMeetdetailtime(alldate);
			meetSave.setMeetaddress(meetaddress);//会议地点
			meetSave.setMastername(mastername);//会议召开人
			
			if (meetmannames!=null && meetmannames.length>0)
			{
				String meetpersonids="";
			    String meetpersonnames="";
				for (int i=0;i<meetmannames.length;i++)
				{
					if (i==0)
					{
						meetpersonids+=meetmannames[i][0];
						meetpersonnames+=meetmannames[i][1];
					}
					else
					{
						meetpersonids+=";"+meetmannames[i][0];
						meetpersonnames+=";"+meetmannames[i][1];
					}
				}
				meetSave.setMeetpersonids(meetpersonids);
				meetSave.setMeetpersonnames(meetpersonnames);
			}
			if (othermannames!=null && othermannames.length>0)
			{
				String meetmanname="";
			    String meetmanunit="";
			    String mobilenum="";
				for (int i=0;i<othermannames.length;i++)
				{
					if (i==0)
					{
						meetmanname+=replaceFH(othermannames[i][1]);
					    meetmanunit+=replaceFH(othermannames[i][2]);
					    mobilenum+=replaceFH(othermannames[i][3]);
					}
					else
					{
						meetmanname+=";"+replaceFH(othermannames[i][1]);
					    meetmanunit+=";"+replaceFH(othermannames[i][2]);
					    mobilenum+=";"+replaceFH(othermannames[i][3]);
					}
				}
				meetSave.setMeetmanname(meetmanname);//其他人名
				meetSave.setMeetmanunit(meetmanunit);//其他人单位
				meetSave.setMobilenum(mobilenum);//其他人手机号
			}
			meetSave.setMeetcontent(meetcontent);//议程
			meetSave.setComment(comment);//备注
			String filepaths="";//文件路径
			String filepathnames="";//文件名
			if (filePaths!=null && filePaths.length>0)
			{
				for (int i=0;i<filePaths.length;i++)
				{
					if (i==0)
					{
						filepaths+=filePaths[i][0];
						filepathnames+=filePaths[i][1];
					}
					else
					{
						filepaths+=";;;;"+filePaths[i][0];
						filepathnames+=";;;;"+filePaths[i][1];
					}
				}
			}
			meetSave.setFilepaths(filepaths);
			meetSave.setFilepathnames(filepathnames);
			
			meetSave.setSubmiter(user.getId());
			meetSave.setSubmitdate(new Date());
			if (id!=null && id>0)
			{
				jqlService.update(meetSave);
			}
			else
			{
				jqlService.save(meetSave);
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	public MeetSave getMeetSave(Long id,Users user)
	{//获取会议草稿 草稿id
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			//获取附件
			MeetSave meetSave=(MeetSave)jqlService.getEntity(MeetSave.class, id);
			String meetpersonids=meetSave.getMeetpersonids();
			String meetpersonnames=meetSave.getMeetpersonnames();
			if (meetpersonids!=null && meetpersonids.length()>0)
			{
				//处理签批者
				String[] userall=meetpersonids.split(";");
				String[] usernameall=meetpersonnames.split(";");
				String[][] meetmannames=new String[userall.length][2];
				for (int i=0;i<userall.length;i++)
				{
					String[] temp=new String[]{userall[i],usernameall[i]};
					meetmannames[i]=temp;
				}
				meetSave.setMeetmannames(meetmannames);//与会人员
			}
			String meetmanname=meetSave.getMeetmanname();//其他人名
			String meetmanunit=meetSave.getMeetmanunit();//其他人单位
			String mobilenum=meetSave.getMobilenum();//其他人手机号
			if (meetmanname!=null && meetmanname.length()>0)
			{
				String[] onames= meetmanname.split(";");
				String[] ounits= meetmanunit.split(";");
				String[] omobiles= mobilenum.split(";");
				String[][] othermannames=new String[onames.length][4];
				for (int i=0;i<onames.length;i++)
				{
					othermannames[i][0]="0";
					othermannames[i][1]=onames[i];
					othermannames[i][2]=ounits[i];
					othermannames[i][3]=omobiles[i];
				}
				meetSave.setOthermannames(othermannames);//其他人员
			}
			
			String filepaths=meetSave.getFilepaths();
			String filepathnames=meetSave.getFilepathnames();
			
			if (filepaths!=null && filepaths.length()>0)
			{
				String[] paths=filepaths.split(";;;;");
				String[] names=filepathnames.split(";;;;");
				String[][] filePaths=new String[paths.length][2];
				for (int i=0;i<paths.length;i++)
				{
					String[] temp=new String[]{paths[i],names[i]};
					filePaths[i]=temp;
				}
				meetSave.setFilePaths(filePaths);//附件
			}
			return meetSave;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 发出会议通知
	 * @param id 草稿id
	 * @param meetname 会议名称
	 * @param meetdate 会议日期
	 * @param meettime 会议时间
	 * @param meetaddress 会议地点
	 * @param mastername 会议召开人
	 * @param meetmannames 与会人员
	 * @param othermannames 其他人员
	 * @param meetcontent  会议议程
	 * @param filePaths  会议资料
	 * @param comment  备注
	 * @param user
	 * @return
	 */
	public boolean meetCommit(Long id,String meetname,Date meetdate,String meettime
			,String meetaddress,String mastername,String[][] meetmannames,String[][] othermannames
			,String meetcontent,String[][] filePaths
			,String comment
			,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			Date alldate=mergeDate(meetdate,meettime);
			MeetInfo meetInfo=createMeet(meetname,meetdate,meettime,alldate
					,meetaddress, mastername,meetmannames,othermannames
					,meetcontent,filePaths,comment,user);//存储送审信息
			if (id!=null && id.longValue()>0)
			{
				//删除保存的事务草稿信息
				jqlService.deleteEntityByID(MeetSave.class, "id", id);
				//还要增加删除文件的操作
			}
			//增加消息推送
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	public String getWebcontent(Long id,int type)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (type==0)
			{
				MeetInfo meetInfo=(MeetInfo)jqlService.getEntity(MeetInfo.class, id);
				if (meetInfo.getMeetcontent()!=null && meetInfo.getMeetcontent().length()>0)
				{
					return meetInfo.getMeetcontent();
				}
			}
			else
			{
				MeetSave meetSave=(MeetSave)jqlService.getEntity(MeetSave.class, id);
				if (meetSave.getMeetcontent()!=null && meetSave.getMeetcontent().length()>0)
				{
					return meetSave.getMeetcontent();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 获取会议详细数据
	 * @param id
	 * @param user
	 * @return
	 */
	public MeetInfo getMeetPermit(Long id,Users user)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			MeetInfo meetInfo=(MeetInfo)jqlService.getEntity(MeetInfo.class, id);
			meetInfo.setMeetingtime(sdf.format(meetInfo.getMeetdetailtime()));
			String sql="select a from MeetFiles as a where a.meetid=? and a.isnew=0 ";
			List<MeetFiles> attachlist = (List<MeetFiles>)jqlService.findAllBySql(sql, id);
			ArrayList<String[]> filelist=new ArrayList<String[]>();
			String filepath="";
			if (attachlist!=null && attachlist.size()>0)
			{
				for (int j=0;j<attachlist.size();j++)
				{
					MeetFiles meetFiles=attachlist.get(j);
					filelist.add(new String[]{meetFiles.getDocumentpath(),meetFiles.getFileName()});
				}
			}
			meetInfo.setFilelist(filelist);
			ArrayList meetmannames=new ArrayList();//用来存放与会人员
		    ArrayList othermannames=new ArrayList();//用来存放其他人员
		    String allpersons="";
		    sql="select a from MeetSameInfo as a where a.meetid=? and a.isnew=0 ";
		    List<MeetSameInfo> samelist = (List<MeetSameInfo>)jqlService.findAllBySql(sql, id);
		    if (samelist!=null && samelist.size()>0)
		    {
		    	for (int i=0;i<samelist.size();i++)
		    	{
		    		MeetSameInfo meetSameInfo=samelist.get(i);
		    		if (meetSameInfo.getMeetmanname()==null)
		    		{
		    			Users meeter=meetSameInfo.getMeeter();
		    			if (meeter!=null)
		    			{
		    				meetmannames.add(new String[]{String.valueOf(meeter.getId().longValue()),meeter.getRealName()});
		    				allpersons+=meeter.getRealName()+";";
		    			}
		    		}
		    		else
		    		{
		    			othermannames.add(new String[]{String.valueOf(meetSameInfo.getId().longValue()),
		    					meetSameInfo.getMeetmanname(),meetSameInfo.getMeetmanunit(),meetSameInfo.getMobilenum()
		    			});
		    			allpersons+=meetSameInfo.getMeetmanname()+";";
		    		}
		    	}
		    }
		    meetInfo.setAllpersons(allpersons);
		    meetInfo.setMeetmannames(meetmannames);
		    meetInfo.setOthermannames(othermannames);
			return meetInfo;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 会议回执处理
	 * @param id 会议编号id
	 * @param actionid 选择的类别
	 * @param comment 备注
	 * @param replaceuserid  替会人员ID，0就是系统外人员
	 * @param othername 替换人名
	 * @param otherunit 替换人单位
	 * @param otherphone 替换人电话
	 * @param user 当前处理人
	 * @return
	 */
	public boolean meetModify(Long id,Long actionid,String comment,Long replaceuserid
			,String othername,String otherunit,String otherphone,Users user,boolean ismobile)
	{
		try
		{
			FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			MeetInfo meetInfo=(MeetInfo)jqlService.getEntity(MeetInfo.class, id);

			meetInfo.setSubmitdate(new Date());
			meetInfo.setSubmiter(user.getId());
			String sql="select a from MeetSameInfo as a where a.meetid=? and a.isnew=0 and a.meeter.id=? order by a.id DESC";
			List<MeetSameInfo> samelist = (List<MeetSameInfo>)jqlService.findAllBySql(sql, id,user.getId());
			long state=ApproveConstants.MEET_STATUS_HAD;
			List<String> mobilelist=new ArrayList<String>();
			List<Long> tranids=new ArrayList<Long>();
			String content="";
			
			if (samelist!=null && samelist.size()>0)
			{
				
				 
				//更新用户处理信息
				MeetSameInfo meetSameInfo=samelist.get(0);
				meetSameInfo.setComment(comment);
				meetSameInfo.setActionid(actionid);
				//以下是替会
				if (replaceuserid!=null && replaceuserid.longValue()>0)
				{
					meetSameInfo.setReplaceid(replaceuserid);
					//消息通知此人
					List<Long> userids=new ArrayList<Long>();
					userids.add(replaceuserid);
			        MessageUtil.instance().setOtherWarn(meetInfo.getId(), MessageCons.SENDMEET, userids, comment
			        		, user.getRealName()+"请您替他参会("+meetInfo.getMeetname()+")", user,0);
			        Users replacer=(Users)jqlService.getEntity(Users.class, replaceuserid);
			        if (replacer.getMobile()!=null && replacer.getMobile().length()==11)
			        {
			        	 mobilelist.add(replacer.getMobile());
						 tranids.add(meetSameInfo.getId());
						 content=user.getRealName()+"请您帮他参加“"+meetInfo.getMeetname()+"”会议,时间:"+meetInfo.getMeetdate()
						 +" "+meetInfo.getMeettime()+"地点:"+meetInfo.getMeetaddress();//暂不做短信回复功能
			        }
			        sendMobileinfo(mobilelist,content,Constant.MEETING,tranids,true,user);
				}
				else
				{
					meetSameInfo.setReplaceman(othername);
					meetSameInfo.setReplaceunit(otherunit);
					meetSameInfo.setReplacemobile(otherphone);
					if (otherphone!=null && otherphone.length()==11)
			        {
			        	 mobilelist.add(otherphone);
						 tranids.add(meetSameInfo.getId());
						 content=user.getRealName()+"请您帮他参加“"+meetInfo.getMeetname()+"”会议,时间:"+meetInfo.getMeetdate()
						 +" "+meetInfo.getMeettime()+"地点:"+meetInfo.getMeetaddress();//
			        }
			        sendMobileinfo(mobilelist,content,Constant.MEETING,tranids,true,user);//短信回复可以处理
				}
				meetSameInfo.setSubmitdate(new Date());
				meetSameInfo.setSubmiter(user.getId());
				jqlService.update(meetSameInfo);
			}
			//记录处理历史
			MeetTask meetTask = insertTask(id,meetInfo.getMeetname(),user
					, state,"回执",actionid,comment,"","",jqlService);
			//处理交办事务总状态,暂不处理
			jqlService.update(meetInfo);
			sql="select a from MeetSameInfo as a where a.meetid=? and a.isnew=0 and a.meeter.id=? order by a.id DESC";
			

			jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.type="+MessageCons.SENDMEET
					+" and a.outid=? and a.msguser.id=? ",new Date(),id,user.getId());//去除催办

			//重新获取消息数量,进行推送
			//移动端不能进行推送，这里要报空指针异常
			if (ismobile)
			{
				List<Long> userIds=new ArrayList<Long>();
				userIds.add(user.getId());
				MessageUtil.instance().changeMsgNum(new Messages(),user,userIds);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	public boolean meetbackModify(Long id,Long actionid,String comment,Long replaceuserid
			,String othername,String otherunit,String otherphone,Users user,boolean ismobile)
	{
		try
		{
			FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			MeetSameInfo meetsameInfo=(MeetSameInfo)jqlService.getEntity(MeetSameInfo.class, id);
			System.out.println("meetsameid========="+id);
			if (meetsameInfo!=null)
			{
				MeetInfo meetInfo=(MeetInfo)jqlService.getEntity(MeetInfo.class, meetsameInfo.getMeetid());
				meetInfo.setSubmitdate(new Date());
				meetInfo.setSubmiter(user.getId());
				String sql="select a from MeetSameInfo as a where a.id=? ";
				
				long state=ApproveConstants.MEET_STATUS_HAD;
				List<String> mobilelist=new ArrayList<String>();
				List<Long> tranids=new ArrayList<Long>();
				String content="";
				//更新用户处理信息
				MeetSameInfo meetSameInfo=meetsameInfo;
				meetSameInfo.setComment(comment);
				meetSameInfo.setActionid(actionid);
				//以下是替会
				if (replaceuserid!=null && replaceuserid.longValue()>0)
				{
					meetSameInfo.setReplaceid(replaceuserid);
					//消息通知此人
					List<Long> userids=new ArrayList<Long>();
					userids.add(replaceuserid);
			        MessageUtil.instance().setOtherWarn(meetInfo.getId(), MessageCons.SENDMEET, userids, comment
			        		, user.getRealName()+"请您替他参会("+meetInfo.getMeetname()+")", user,0);
			        Users replacer=(Users)jqlService.getEntity(Users.class, replaceuserid);
			        if (replacer.getMobile()!=null && replacer.getMobile().length()==11)
			        {
			        	 mobilelist.add(replacer.getMobile());
						 tranids.add(meetSameInfo.getId());
						 content=user.getRealName()+"请您帮他参加“"+meetInfo.getMeetname()+"”会议,时间:"+meetInfo.getMeetdate()
						 +" "+meetInfo.getMeettime()+"地点:"+meetInfo.getMeetaddress();//暂不做短信回复功能
			        }
			        sendMobileinfo(mobilelist,content,Constant.MEETING,tranids,true,user);
				}
				else
				{
					meetSameInfo.setReplaceman(othername);
					meetSameInfo.setReplaceunit(otherunit);
					meetSameInfo.setReplacemobile(otherphone);
					if (otherphone!=null && otherphone.length()==11)
			        {
			        	 mobilelist.add(otherphone);
						 tranids.add(meetSameInfo.getId());
						 content=user.getRealName()+"请您帮他参加“"+meetInfo.getMeetname()+"”会议,时间:"+meetInfo.getMeetdate()
						 +" "+meetInfo.getMeettime()+"地点:"+meetInfo.getMeetaddress();//
			        }
			        sendMobileinfo(mobilelist,content,Constant.MEETING,tranids,true,user);//短信回复可以处理
				}
				meetSameInfo.setSubmitdate(new Date());
				meetSameInfo.setSubmiter(user.getId());
				jqlService.update(meetSameInfo);

				//记录处理历史
				MeetTask meetTask = insertTask(meetInfo.getId(),meetInfo.getMeetname(),user
						, state,"回执",actionid,comment,"","",jqlService);
				//处理交办事务总状态,暂不处理
				jqlService.update(meetInfo);
				
				jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.type="+MessageCons.SENDMEET
						+" and a.outid=? and a.msguser.id=? ",new Date(),meetInfo.getId(),user.getId());//去除催办
	
				//重新获取消息数量,进行推送
				//移动端不能进行推送，这里要报空指针异常
				if (ismobile)
				{
					List<Long> userIds=new ArrayList<Long>();
					userIds.add(user.getId());
					MessageUtil.instance().changeMsgNum(new Messages(),user,userIds);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	public boolean meetAdminChange(Long id,String[][] data,Users user)
	{//批量填写
		try
		{
			//Long sameid,Integer inout
			//,String othername,String otherstatus,String comment
			
			FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			MeetInfo meetInfo=(MeetInfo)jqlService.getEntity(MeetInfo.class, id);
			meetInfo.setSubmitdate(new Date());
			meetInfo.setSubmiter(user.getId());
			long state=ApproveConstants.MEET_STATUS_HAD;
			if (data!=null && data.length>0)
			{
				for (int i=0;i<data.length;i++)
				{
					String[] values=data[i];
					MeetSameInfo meetSameInfo=(MeetSameInfo)jqlService.getEntity(MeetSameInfo.class, Long.valueOf(values[0]));
					if (meetSameInfo.getMeeter()==null)
					{
						String actionname=values[3];
						String comment=values[4];
						long actionid=0;
						if ("参加".equals(actionname))
						{
							actionid=ApproveConstants.MEET_ACTION_APPEND;
						}
						else if ("不参加".equals(actionname))
						{
							actionid=ApproveConstants.MEET_ACTION_NOTAPPEND;
						}
						
						meetSameInfo.setActionid(actionid);
						meetSameInfo.setComment(comment);
						meetSameInfo.setSubmitdate(new Date());
						meetSameInfo.setSubmiter(user.getId());
						jqlService.update(meetSameInfo);
						
						//记录处理历史
						MeetTask meetTask = insertTask(id,meetInfo.getMeetname(),user
								, state,"电话确认",actionid,comment,"","",jqlService);
					}
				}
				jqlService.update(meetInfo);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	public boolean meetDelete(Long id,int type,Users user)
	{//删除记录
		try
		{
			FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			
			if (type==1)
			{
				MeetSave meetSave=(MeetSave)jqlService.getEntity(MeetSave.class, id);
				String filepaths=meetSave.getFilepaths();
				if (filepaths!=null)
				{
					String[] paths=filepaths.split(";;;;");
					for (int i=0;i<paths.length;i++)
					{
						if (paths[i].indexOf("/")<0)
						{
							 String tempPath = WebConfig.tempFilePath + File.separatorChar;
							 File file = new File(tempPath + fileNameReplace(paths[i]));
						     file.delete();
						}
					}
				}
				jqlService.deleteEntityByID(MeetSave.class, "id", id);
			}
			else if (type==2)//清除我的请求的记录
			{
				MeetInfo meetInfo=(MeetInfo)jqlService.getEntity(MeetInfo.class, id);
				meetInfo.setIsview(1);
				jqlService.update(meetInfo);
			}
			else//隐藏非发起人的记录
			{
				String sql="select a from MeetSameInfo as a where a.meetid=? and a.isnew=0 and a.meeter.id=? order by a.id DESC";
				List<MeetSameInfo> samelist = (List<MeetSameInfo>)jqlService.findAllBySql(sql, id,user.getId());
				if (samelist!=null && samelist.size()>0)
				{
					MeetSameInfo meetSameInfo=samelist.get(0);
					meetSameInfo.setIsview(1l);
					jqlService.update(meetSameInfo);
				}
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	public Map<String,Object> getMeetHistosy(Long userId, Long meetid)
	{//获取会议回执历史记录
		Map<String,Object> result = new HashMap<String,Object>();
		try
		{
			List<Map<String,Object>> historylist = new ArrayList<Map<String,Object>>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			MeetInfo info = (MeetInfo)jqlService.getEntity(MeetInfo.class, meetid);
			String fileName = "";
			String date = "";
			String ownerName = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String queryString = "select u.realName, t from MeetTask t, Users u "
				+ " where t.submiter = u.id and t.meetid = ? order by t.id ";
			List<Object[]> list = jqlService.findAllBySql(queryString, info.getId());
			result.put("meetname",info.getMeetname() );//会议主题
			
			result.put("history", historylist);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	public List<String[]> getMeetBackDetail(Long userId, Long meetid)
	{//获取会议回执详情--与会情况
		//[['徐弘成','未读',''],['王茂全','替会：人员A','我要替会'],['张蜀渝','参加','我参加'],['外部人员3','参加','']]
		Map<String,Object> result = new HashMap<String,Object>();
		try
		{
			List<String[]> historylist = new ArrayList<String[]>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			MeetInfo info = (MeetInfo)jqlService.getEntity(MeetInfo.class, meetid);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String queryString = "select t from MeetSameInfo t "
				+ " where t.meetid = ? order by t.id ";
			List<MeetSameInfo> list = jqlService.findAllBySql(queryString, info.getId());
			if (list!=null && list.size()>0)
			{
				for (int i=0;i<list.size();i++)
				{
					MeetSameInfo meetSameInfo=list.get(i);
					String[] temp=new String[3];
					//{String.valueOf(meetSameInfo.getId().intValue())}
					temp[0]=meetSameInfo.getMeetmanname();
					if (temp[0]==null)
					{
						temp[0]=meetSameInfo.getMeeter().getRealName();
					}
					else
					{
						temp[0]+="(外部)";
					}
					Long actionid=meetSameInfo.getActionid();
					if (actionid==null)
					{
						if (meetSameInfo.getMeetmanname()!=null)
						{
							temp[1]="待确认";
						}
						else
						{
							temp[1]="未读";
							if ("Y".equals(meetSameInfo.getSigntag()))
							{
								temp[1]="已读";
							}
						}
					}
					else if (actionid.longValue()==0)
					{
						temp[1]="待确认";
					}
					else if (actionid.longValue()==ApproveConstants.MEET_ACTION_APPEND)
					{
						temp[1]="参加";
					}
					else if (actionid.longValue()==ApproveConstants.MEET_ACTION_REPLACE)
					{
						if (meetSameInfo.getReplaceid()!=null)
						{
							temp[1]="替会："+userService.getUser(meetSameInfo.getReplaceid()).getRealName();
						}
						else
						{
							temp[1]="替会："+meetSameInfo.getReplaceman();
						}
					}
					else if (actionid.longValue()==ApproveConstants.MEET_ACTION_NOTAPPEND)
					{
						temp[1]="不参加";
					}
					
					temp[2]=meetSameInfo.getComment();
					if (temp[2]==null)
					{
						temp[2]="";
					}
					historylist.add(temp);
				}
			}
			return historylist;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public List<String[]> getMeetBackDetailCB(Long userId, Long meetid)
	{//获取会议回执详情--与会情况 有催办
		//[3,1,'王茂全','替会：人员A','我要替会',0]]
		Map<String,Object> result = new HashMap<String,Object>();
		try
		{
			List<String[]> historylist = new ArrayList<String[]>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			MeetInfo info = (MeetInfo)jqlService.getEntity(MeetInfo.class, meetid);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String queryString = "select t from MeetSameInfo t "
				+ " where t.meetid = ? order by t.id ";
			List<MeetSameInfo> list = jqlService.findAllBySql(queryString, info.getId());
			if (list!=null && list.size()>0)
			{
				for (int i=0;i<list.size();i++)
				{
					MeetSameInfo meetSameInfo=list.get(i);
					String[] temp=new String[7];
					temp[0]=String.valueOf(meetSameInfo.getId().intValue());
					Long actionid=meetSameInfo.getActionid();
					if (meetSameInfo.getMeeter()==null)
					{
						temp[6]="1";
					}
					else
					{
						temp[6]="0";
					}
					if (meetSameInfo.getMeeter()==null || actionid!=null && actionid.longValue()>0)
					{
						temp[1]="0";
					}
					else
					{
						temp[1]="1";
					}
					temp[2]=meetSameInfo.getMeetmanname();
					if (temp[2]==null)
					{
						temp[2]=meetSameInfo.getMeeter().getRealName();
					}
					else
					{
						temp[2]+="(外部)";
					}
					
					
					if (actionid==null)
					{
						if (meetSameInfo.getMeetmanname()!=null)
						{
							temp[3]="待确认";
						}
						else
						{
							temp[3]="未读";
							if ("Y".equals(meetSameInfo.getSigntag()))
							{
								temp[3]="已读";
							}
						}
					}
					else if (actionid.longValue()==0)
					{
						temp[3]="待确认";
					}
					else if (actionid.longValue()==ApproveConstants.MEET_ACTION_APPEND)
					{
						temp[3]="参加";
					}
					else if (actionid.longValue()==ApproveConstants.MEET_ACTION_REPLACE)
					{
						if (meetSameInfo.getReplaceid()!=null)
						{
							temp[3]="替会："+userService.getUser(meetSameInfo.getReplaceid()).getRealName();
						}
						else
						{
							temp[3]="替会："+meetSameInfo.getReplaceman();
						}
						//这里比较复杂，需要判断替会的人是系统内部的还是外部的
					}
					else if (actionid.longValue()==ApproveConstants.MEET_ACTION_NOTAPPEND)
					{
						temp[3]="不参加";
					}
					if (meetSameInfo.getComment()==null || meetSameInfo.getComment().length()==0)
					{
						temp[4]=meetSameInfo.getMobilenum();
					}
					else
					{
						
						temp[4]=meetSameInfo.getComment();
					}
					if (temp[4]==null)
					{
						temp[4]="";
					}
					if (meetSameInfo.getWarnnum()==null || meetSameInfo.getWarnnum().intValue()==0)
					{
						temp[5]="";
					}
					else
					{
						temp[5]="("+meetSameInfo.getWarnnum().intValue()+")";
					}
					
					historylist.add(temp);
				}
			}
			return historylist;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public HashMap<String, Object> getMeetDrafts(int start,int count,String sort,String order,Users user)
	{//获取会议草稿
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			sql="select distinct a.id,a.meetname,a.mastername,a.meetdetailtime,a.meetaddress,a.meetcontent,a.filepaths,a.filepathnames "
					+" from meetsave a "
					+" where a.submiter="+user.getId();
 			if (ApproveConstants.MEETNAME.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.meetname using gbk) "+order;
			}
 			else if (ApproveConstants.MEETDETAILTIME.equals(sort))//按照会议时间
			{
				sql+=" order by a.meetdetailtime "+order;
			}
			else if (ApproveConstants.MEETADDRESS.equals(sort))//会议地点
			{
				sql+=" order by convert(a.meetaddress using gbk) "+order;
			}
			else if (ApproveConstants.MEETMASTER.equals(sort))//会议召开人
			{
				sql+=" order by convert(a.mastername using gbk) "+order;
			}
			else
			{
				sql+=" order by a.submitdate "+order;
			}
			List<Object[]> draftlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			sqlcount="select count(distinct a) from MeetSave a where a.submiter="+user.getId();
			Long size=(Long)jqlService.getCount(sqlcount);
			resultmap.put("fileListSize", size);//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<draftlist.size();i++)
			{
				Object[] objs=draftlist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				values.put("id", id);//主键编号
				values.put("meetname", (String)objs[1]);//名称
				values.put("mastername", (String)objs[2]);//会议召开人
				values.put("meetdetailtime", sdf.format((Date)objs[3]));//会议时间
				values.put("meetaddress", (String)objs[4]);//会议地点
				String filepath=(String)objs[6];//文件路径
				String filepathname=(String)objs[7];//文件名
				List<String> files=new ArrayList<String>();
				List<String> filenames=new ArrayList<String>();
				if (filepath!=null)
				{
					String[] paths=filepath.split(";;;;");
					String[] pathnames=filepathname.split(";;;;");
					for (int j=0;j<paths.length;j++)
					{
						files.add(paths[j]);
						filenames.add(pathnames[j]);
					}
				}
				String webcontent=(String)objs[5];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/会议议程");
					filenames.add(id+"/会议议程");
				}
				else
				{
					files.add("");
					filenames.add("");
				}
				values.put("files", files);//附件列表
				values.put("filesnames", filenames);//附件列表

				list.add(values);
			}
			resultmap.put("fileList", list);
			return resultmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public HashMap<String, Object> getMeetTodo(int start,int count,String sort,String order,Users user,ArrayList<Long> userIds,String selectedTime
			,String searchName,ArrayList<String> userIds_1,String eSelectedTime,String searchSpace)
	{//获取待办列表
		try
		{
			String sql="";
			String sqlcount="";
			String countString ="";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			sql="select distinct a.id,a.meetname,a.mastername,a.meetdetailtime,a.meetaddress,a.meetcontent,a.adddate,c.realName "
					+" from MeetInfo a,MeetSameInfo b,Users c "
					+" where a.senduser=c.id and a.id=b.meetid and (a.isview is null or a.isview=0) and b.meeter_id="+user.getId()
					+" and (b.actionid is null or b.actionid=0) ";
			//筛选条件
			//通知者筛选
			if(userIds != null && userIds.size() > 0)
			{
				for (int i = 0;i < userIds.size();i++) {
					if(i == 0)
					{
						sql += " and ( a.senduser =" + userIds.get(0);
						countString += " and ( a.senduser =" + userIds.get(0);
					}else {
						sql += " or a.senduser =" + userIds.get(i);
						countString += " or a.senduser =" + userIds.get(i);
					}
				}
				sql += ")";
				countString += ")";
			}
			//通知时间段筛选
			if(selectedTime != null)
			{
				String[] Time = selectedTime.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.adddate >='"+sTime+"' and a.adddate <='"+eTime+"'";
				countString += " and a.adddate >='"+sTime+"' and a.adddate <='"+eTime+"'";
			}
			//会议名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and a.meetname like '%"+searchName+"%'";
				countString += " and a.meetname like '%"+searchName+"%'";
			}
			//召开人筛选
			if(userIds_1 != null && userIds_1.size() > 0)
			{
				for (int i = 0;i < userIds_1.size();i++) {
					if(i == 0)
					{
						sql += " and ( a.mastername = '" + userIds_1.get(0)+"'";
						countString += " and ( a.mastername = '" + userIds_1.get(0)+"'";
					}else {
						sql += " or a.mastername = '" + userIds_1.get(i)+"'";
						countString += " or a.mastername = '" + userIds_1.get(i)+"'";
					}
				}
				sql += ")";
				countString += ")";
			}
			//会议时间段筛选
			if(selectedTime != null)
			{
				String[] Time = selectedTime.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.meetdetailtime >='"+sTime+"' and a.meetdetailtime <='"+eTime+"'";
				countString += " and a.meetdetailtime >='"+sTime+"' and a.meetdetailtime <='"+eTime+"'";
			}
			//会议地点筛选
			if(searchSpace != null && searchSpace.length() > 0)
			{
				sql += " and a.meetaddress like '%"+searchSpace+"%'";
				countString += " and a.meetaddress like '%"+searchSpace+"%'";
			}
			if ("senduser".equals(sort))//通知人
			{
				sql+=" order by convert(c.realName using gbk) "+order;
			}
			else if (ApproveConstants.MEETNAME.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.meetname using gbk) "+order;
			}
 			else if (ApproveConstants.MEETDETAILTIME.equals(sort))//按照会议排序
			{
				sql+=" order by a.meetdetailtime "+order;
			}
 			else if (ApproveConstants.MEETADDDATE.equals(sort))//按照发布时间排序
			{
				sql+=" order by a.adddate "+order;
			}
			else if (ApproveConstants.MEETADDRESS.equals(sort))//会议地点
			{
				sql+=" order by convert(a.meetaddress using gbk) "+order;
			}
			else if (ApproveConstants.MEETMASTER.equals(sort))//会议召开人
			{
				sql+=" order by convert(a.mastername using gbk) "+order;
			}
			else
			{
				sql+=" order by a.submitdate "+order;
			}
			List<Object[]> draftlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			sqlcount="select count(distinct a) from MeetInfo a,MeetSameInfo b where a.id=b.meetid and (a.isview is null or a.isview=0) and b.isview=0 and b.meeter.id="+user.getId()
					+" and (b.actionid is null or b.actionid=0) "+countString;
			Long size=(Long)jqlService.getCount(sqlcount);
			resultmap.put("fileListSize", size);//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<draftlist.size();i++)
			{
				Object[] objs=draftlist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				values.put("id", id);//主键编号
				values.put("meetname", (String)objs[1]);//名称
				values.put("mastername", (String)objs[2]);//会议召开人
				values.put("senduser", (String)objs[7]);//会议发起人
				
				values.put("meetdetailtime", sdf.format((Date)objs[3]));//会议时间
				values.put("meetaddress", (String)objs[4]);//会议地点
				values.put("adddate", sdf.format((Date)objs[6]));//会议发布时间
				List<String> files=new ArrayList<String>();
				List<String> filenames=new ArrayList<String>();
				sql="select a from MeetFiles as a where a.meetid=? ";
				List<MeetFiles> fileslist = (List<MeetFiles>)jqlService.findAllBySql(sql, id);
				String filepath="";
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
					filenames.add(fileslist.get(j).getFileName());
				}
				String webcontent=(String)objs[5];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/会议议程");
					filenames.add(id+"/会议议程");
				}
				else
				{
					files.add("");
					filenames.add("");
				}
				List<MeetSameInfo> samelist=(List<MeetSameInfo>)jqlService.findAllBySql("select a from MeetSameInfo as a where a.meetid=? and a.meeter.id=? and a.isnew=0 ", id,user.getId());
				if (samelist!=null && samelist.size()>0)
				{
					values.put("signtag", samelist.get(0).getSigntag());//是否已签收标记
					values.put("warnnum", samelist.get(0).getWarnnum());//催办次数
					
				}
				else
				{
					values.put("signtag", "");
					values.put("warnnum", 0);
				}
				values.put("files", files);//附件列表
				values.put("filesnames", filenames);//附件列表

				list.add(values);
			}
			resultmap.put("fileList", list);
			return resultmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	public HashMap<String, Object> getMeetDone(int start,int count,String sort,String order,Users user,ArrayList<Long> userIds,String selectedTime
			,String searchName,ArrayList<String> userIds_1,String eSelectedTime,String searchSpace)
	{//获取办结列表
		try
		{
			String sql="";
			String sqlcount="";
			String countString="";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			sql="select distinct a.id,a.meetname,a.mastername,a.meetdetailtime,a.meetaddress,a.meetcontent,a.adddate,b.actionid,c.realName "
					+" from MeetInfo a,MeetSameInfo b,Users c "
					+" where a.senduser=c.id and a.id=b.meetid and (a.isview is null or a.isview=0) and b.isview=0 "
					+" and (b.meeter_id="+user.getId()+" or b.replaceid="+user.getId()+")"
					+" and b.actionid>0 ";
			//筛选条件
			//通知者筛选
			if(userIds != null && userIds.size() > 0)
			{
				for (int i = 0;i < userIds.size();i++) {
					if(i == 0)
					{
						sql += " and ( a.senduser =" + userIds.get(0);
						countString += " and ( a.senduser =" + userIds.get(0);
					}else {
						sql += " or a.senduser =" + userIds.get(i);
						countString += " or a.senduser =" + userIds.get(i);
					}
				}
				sql += ")";
				countString += ")";
			}
			//通知时间段筛选
			if(selectedTime != null)
			{
				String[] Time = selectedTime.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.adddate >='"+sTime+"' and a.adddate <='"+eTime+"'";
				countString += " and a.adddate >='"+sTime+"' and a.adddate <='"+eTime+"'";
			}
			//会议名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and a.meetname like '%"+searchName+"%'";
				countString += " and a.meetname like '%"+searchName+"%'";
			}
			//召开人筛选
			if(userIds_1 != null && userIds_1.size() > 0)
			{
				for (int i = 0;i < userIds_1.size();i++) {
					if(i == 0)
					{
						sql += " and ( a.mastername = '" + userIds_1.get(0)+"'";
						countString += " and ( a.mastername = '" + userIds_1.get(0)+"'";
					}else {
						sql += " or a.mastername = '" + userIds_1.get(i)+"'";
						countString += " or a.mastername = '" + userIds_1.get(i)+"'";
					}
				}
				sql += ")";
				countString += ")";
			}
			//会议时间段筛选
			if(selectedTime != null)
			{
				String[] Time = selectedTime.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.meetdetailtime >='"+sTime+"' and a.meetdetailtime <='"+eTime+"'";
				countString += " and a.meetdetailtime >='"+sTime+"' and a.meetdetailtime <='"+eTime+"'";
			}
			//会议地点筛选
			if(searchSpace != null && searchSpace.length() > 0)
			{
				sql += " and a.meetaddress like '%"+searchSpace+"%'";
				countString += " and a.meetaddress like '%"+searchSpace+"%'";
			}
			
			if ("senduser".equals(sort))//通知人
			{
				sql+=" order by convert(c.realName using gbk) "+order;
			}
			else if (ApproveConstants.MEETNAME.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.meetname using gbk) "+order;
			}
 			else if (ApproveConstants.MEETDETAILTIME.equals(sort))//按照会议排序
			{
				sql+=" order by a.meetdetailtime "+order;
			}
 			else if (ApproveConstants.MEETADDDATE.equals(sort))//按照发布时间排序
			{
				sql+=" order by a.adddate "+order;
			}
			else if (ApproveConstants.MEETADDRESS.equals(sort))//会议地点
			{
				sql+=" order by convert(a.meetaddress using gbk) "+order;
			}
			else if (ApproveConstants.MEETMASTER.equals(sort))//会议召开人
			{
				sql+=" order by convert(a.mastername using gbk) "+order;
			}
			else
			{
				sql+=" order by a.submitdate "+order;
			}
			List<Object[]> draftlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			sqlcount="select count(distinct a) from MeetInfo a,MeetSameInfo b where a.id=b.meetid and (a.isview is null or a.isview=0) and b.isview=0 and (b.meeter.id="+user.getId()+" or b.replaceid="+user.getId()+")"
					+" and b.actionid>0 "+countString;
			Long size=(Long)jqlService.getCount(sqlcount);
			resultmap.put("fileListSize", size);//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<draftlist.size();i++)
			{
				Object[] objs=draftlist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				long actionid=((BigInteger)objs[7]).longValue();
				values.put("id", id);//主键编号
				values.put("meetname", (String)objs[1]);//名称
				values.put("mastername", (String)objs[2]);//会议召开人
				values.put("senduser", (String)objs[8]);//会议通知人
				values.put("meetdetailtime", sdf.format((Date)objs[3]));//会议时间
				values.put("meetaddress", (String)objs[4]);//会议地点
				values.put("adddate", sdf.format((Date)objs[6]));//会议发布时间
				
				if (actionid==ApproveConstants.MEET_ACTION_APPEND)
				{
					values.put("actionname", "参加");
				}
				else if (actionid==ApproveConstants.MEET_ACTION_REPLACE)
				{
					sql="select a from MeetSameInfo as a where a.meetid=? and a.meeter.id=? order by a.id DESC ";
					List<MeetSameInfo> meetlist = (List<MeetSameInfo>)jqlService.findAllBySql(sql, id,user.getId());
					if (meetlist!=null && meetlist.size()>0)
					{
						MeetSameInfo meetSameInfo=meetlist.get(0);
						String replacename=meetSameInfo.getReplaceman();
						if (meetSameInfo.getReplaceid()!=null)
						{
							replacename=getUserName(""+meetSameInfo.getReplaceid().longValue());
						}
						values.put("actionname", "替会："+replacename);
					}
					else
					{
						sql="select a from MeetSameInfo as a where a.meetid=? and a.replaceid=? order by a.id DESC ";
						meetlist = (List<MeetSameInfo>)jqlService.findAllBySql(sql, id,user.getId());
						if (meetlist!=null && meetlist.size()>0)
						{
							MeetSameInfo meetSameInfo=meetlist.get(0);
							values.put("actionname", meetSameInfo.getMeeter().getRealName()+"请您替会");
						}
						else
						{
							values.put("actionname", "替会");
						}
					}
					
				}
				else if (actionid==ApproveConstants.MEET_ACTION_NOTAPPEND)
				{
					values.put("actionname", "不参加");
				}
				
				
				List<String> files=new ArrayList<String>();
				List<String> filenames=new ArrayList<String>();
				sql="select a from MeetFiles as a where a.meetid=? ";
				List<MeetFiles> fileslist = (List<MeetFiles>)jqlService.findAllBySql(sql, id);
				String filepath="";
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
					filenames.add(fileslist.get(j).getFileName());
				}
				String webcontent=(String)objs[5];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/会议议程");
					filenames.add(id+"/会议议程");
				}
				else
				{
					files.add("");
					filenames.add("");
				}
				values.put("files", files);//附件列表
				values.put("filesnames", filenames);//附件列表

				list.add(values);
			}
			resultmap.put("fileList", list);
			return resultmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public HashMap<String, Object> getMeetMyquestfiles(int start,int count,String sort,String order,Users user,ArrayList<String> userIds,String selectedTime
			,String searchName,String eSelectedTime,String searchSpace)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			String countString ="";
			sql="select distinct a.id,a.meetname,a.mastername,a.meetdetailtime,a.meetaddress,a.meetcontent,a.adddate "
					+" from MeetInfo a "
					+" where (a.isview is null or a.isview=0) and a.senduser="+user.getId();
			//筛选条件
			//通知时间段筛选
			if(selectedTime != null)
			{
				String[] Time = selectedTime.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.adddate >='"+sTime+"' and a.adddate <='"+eTime+"'";
				countString += " and a.adddate >='"+sTime+"' and a.adddate <='"+eTime+"'";
			}
			//会议名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and a.meetname like '%"+searchName+"%'";
				countString += " and a.meetname like '%"+searchName+"%'";
			}
			//召开人筛选
			if(userIds != null && userIds.size() > 0)
			{
				for (int i = 0;i < userIds.size();i++) {
					if(i == 0)
					{
						sql += " and ( a.mastername = '" + userIds.get(0)+"'";
						countString += " and ( a.mastername = '" + userIds.get(0)+"'";
					}else {
						sql += " or a.mastername = '" + userIds.get(i)+"'";
						countString += " or a.mastername = '" + userIds.get(i)+"'";
					}
				}
				sql += ")";
				countString += ")";
			}
			//会议时间段筛选
			if(selectedTime != null)
			{
				String[] Time = selectedTime.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.meetdetailtime >='"+sTime+"' and a.meetdetailtime <='"+eTime+"'";
				countString += " and a.meetdetailtime >='"+sTime+"' and a.meetdetailtime <='"+eTime+"'";
			}
			//会议地点筛选
			if(searchSpace != null && searchSpace.length() > 0)
			{
				sql += " and a.meetaddress like '%"+searchSpace+"%'";
				countString += " and a.meetaddress like '%"+searchSpace+"%'";
			}
 			if (ApproveConstants.MEETNAME.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.meetname using gbk) "+order;
			}
 			else if (ApproveConstants.MEETDETAILTIME.equals(sort))//按照会议排序
			{
				sql+=" order by a.meetdetailtime "+order;
			}
 			else if (ApproveConstants.MEETADDDATE.equals(sort))//按照发布时间排序
			{
				sql+=" order by a.adddate "+order;
			}
			else if (ApproveConstants.MEETADDRESS.equals(sort))//会议地点
			{
				sql+=" order by convert(a.meetaddress using gbk) "+order;
			}
			else if (ApproveConstants.MEETMASTER.equals(sort))//会议召开人
			{
				sql+=" order by convert(a.mastername using gbk) "+order;
			}
			else
			{
				sql+=" order by a.submitdate "+order;
			}
			List<Object[]> draftlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			sqlcount="select count(distinct a) from MeetInfo a where (a.isview is null or a.isview=0) and a.senduser="+user.getId()+countString;
			Long size=(Long)jqlService.getCount(sqlcount);
			resultmap.put("fileListSize", size);//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<draftlist.size();i++)
			{
				Object[] objs=draftlist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				values.put("id", id);//主键编号
				values.put("meetname", (String)objs[1]);//名称
				values.put("mastername", (String)objs[2]);//会议召开人
				values.put("meetdetailtime", sdf.format((Date)objs[3]));//会议时间
				values.put("meetaddress", (String)objs[4]);//会议地点
				values.put("adddate", sdf.format((Date)objs[6]));//会议发布时间
				List<String> files=new ArrayList<String>();
				List<String> filenames=new ArrayList<String>();
				sql="select a from MeetFiles as a where a.meetid=? ";
				List<MeetFiles> fileslist = (List<MeetFiles>)jqlService.findAllBySql(sql, id);
				String filepath="";
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
					filenames.add(fileslist.get(j).getFileName());
				}
				String webcontent=(String)objs[5];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/会议议程");
					filenames.add(id+"/会议议程");
				}
				else
				{
					files.add("");
					filenames.add("");
				}
				values.put("files", files);//附件列表
				values.put("filesnames", filenames);//附件列表

				list.add(values);
			}
			resultmap.put("fileList", list);
			return resultmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean meetsignreal(String id,Users user) 
	{//会议签收
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (id!=null && id.length()>0 && !id.toLowerCase().equals("null"))
			{
				List list=jqlService.findAllBySql("select a from MeetSameInfo as a where a.meetid=? and a.meeter.id=? and a.isnew=0 ", Long.valueOf(id),user.getId());
				if (list!=null && list.size()>0)
				{
					MeetSameInfo same=(MeetSameInfo)list.get(0);
					same.setSigntag("Y");
					same.setSigntagdate(new Date());
					jqlService.update(same);
					jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.outid=? and a.type=? and a.msguser.id=?",new Date(),Long.valueOf(id),MessageCons.SENDMEET,user.getId());
					List<Messages> msglist=jqlService.findAllBySql("select a from Messages as a where a.outid=? and a.type=? and a.msguser.id=?",Long.valueOf(id),MessageCons.SENDMEET,user.getId());
					if (msglist!=null && msglist.size()>0)
					{
						List<Long> userIds =new ArrayList<Long>();
						userIds.add(msglist.get(0).getMsguser().getId());
						MessageUtil.instance().changeMsgNum(msglist.get(0),user,userIds);
					}
				}
				return true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
		return false;
	}
	/**
	 * 获取是否签收
	 * @param id
	 * @param user
	 * @return
	 */
	public boolean getMeetSignReal(String id,Users user) 
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (id!=null && id.length()>0 && !id.toLowerCase().equals("null"))
			{
				List list=jqlService.findAllBySql("select a from MeetSameInfo as a where a.meetid=? and a.meeter.id=? and a.isnew=0 and a.signtag='Y' ", Long.valueOf(id),user.getId());
				if (list!=null && list.size()>0)
				{
					return true;
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
		return false;
	}
	
	public List getHistoryFiles(Long meetid)
	{//获取会议的附件
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			List<MeetFiles> filelist = (List<MeetFiles>)jqlService.findAllBySql("select a from MeetFiles as a where a.meetid=? ", meetid);
			List<String[]> backlist=new ArrayList<String[]>();
			if (filelist!=null && filelist.size()>0)
			{
				for (int i=0;i<filelist.size();i++)
				{
					MeetFiles taskfile=filelist.get(i);
					backlist.add(new String[]{taskfile.getDocumentpath(),taskfile.getFileName()});
				}
				return backlist;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
