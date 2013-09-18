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
import apps.transmanager.weboffice.databaseobject.transmanage.TransFiles;
import apps.transmanager.weboffice.databaseobject.transmanage.TransInfo;
import apps.transmanager.weboffice.databaseobject.transmanage.TransSameFiles;
import apps.transmanager.weboffice.databaseobject.transmanage.TransSameInfo;
import apps.transmanager.weboffice.databaseobject.transmanage.TransSave;
import apps.transmanager.weboffice.databaseobject.transmanage.TransTask;
import apps.transmanager.weboffice.databaseobject.transmanage.TransTaskFiles;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.server.BackgroundSend;

public class TransUtil {

private static TransUtil instance=new TransUtil();
    
    public TransUtil()
    {    	
    	instance =  this;
    }
    public static TransUtil instance()
    {
        return instance;
    }
    private void sendMobileinfo(List<String> mobilelist,String content,int type,List<Long> ids,boolean isback,Users user)
    {
    	if (mobilelist.size()>0)
	   	{
	    	 Thread receiveT = new Thread(new BackgroundSend(mobilelist.toArray(new String[mobilelist.size()]),content
	    			 ,user.getCompany().getId(),user.getCompany().getName()
	    			 ,Constant.TRANSSPLIT,ids.toArray(new Long[ids.size()]),isback,user));
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
				if (ids.indexOf(";")>=0)
				{
					uids=ids.split(";");
				}
				
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
							usernames+=";"+user.getRealName();
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
	/**
	 * 根据ID进行发送消息提醒
	 * @param id 编号,s开头的是签批，p开头的批阅，r开头的协作
	 * @param info 信息标题
	 * @param comment 信息备注
	 * @param user
	 * @return
	 */
	public boolean setWarnMessage(String tid,List<Long> userids,String info,String comment,Users user,Integer worktype)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (tid!=null && tid.length()>0)
			{
				String type=tid.substring(0,1);
				String id=tid.substring(1);
				jqlService.excute("update TransSameInfo as a set a.warnnum=a.warnnum+1 where a.isnew=0 and a.transid="+id+" and a.signer.id=? ",user.getId());
				MessageUtil.instance().setOtherWarn(Long.valueOf(id), MessageCons.SENDTRANS, userids, comment, info, user,worktype);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}

	private TransTask insertTask(Long transid,String title,Users user
		,Long stateid,String stepName,long actionid,String comment,String signers,int transStep
		,ArrayList<String> uploadfiles,ArrayList<String> uploadfilenames
		,JQLServices jqlService)
	{

		TransTask task = new TransTask();
		
		task.setTransstep(transStep);
		task.setTransid(transid);
		task.setStepName(stepName);
		task.setComment(comment);
		task.setStateid(stateid);
		task.setActionid(actionid);
		task.setSigners(signers);
		task.setSubmiter(user.getId());
		task.setSubmitdate(new Date());
		jqlService.save(task);
		if (uploadfiles!=null && uploadfiles.size()>0)
		{
			saveTaskFiles(transid,task.getId(),uploadfiles,uploadfilenames,user);
		}
		return task;
	}
	/**
	 * 存储历史附件
	 * @param transid
	 * @param taskid
	 * @param user
	 * @return
	 */
	private boolean saveTaskFiles(Long transid,Long taskid,
			ArrayList<String> uploadfiles,ArrayList<String> uploadfilenames,Users user)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);

			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
			FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			for (int i=0;i<uploadfiles.size();i++)
			{
				TransTaskFiles taskfile=new TransTaskFiles();
				taskfile.setTransid(transid);
				taskfile.setTaskid(taskid);

				Fileinfo info = saveFile(uploadfiles.get(i),uploadfilenames.get(i),user,fileSystemService);//保存文件
       		 
       		 	taskfile.setDocumentpath(info.getPathInfo());
       		 	taskfile.setFileName(info.getFileName());
				taskfile.setUserID(user.getId());
				taskfile.setAdddate(new Date());
				jqlService.save(taskfile);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
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
		else if ((filePath.startsWith("user_") || filePath.startsWith("group_") || filePath.startsWith("team_") || filePath.startsWith("org_") || filePath.startsWith(spaceid) || filePath.startsWith("company_")) && (filePath.indexOf("/") > 0))// 文档库文档
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
	private TransInfo createTrans(String title,String content,ArrayList<String> filePaths
			,ArrayList<String> fileNames,ArrayList<Long> personIds,String personNames,String comment,Users user)
	{
		 JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		 FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
//1、存储主信息
		 Long status=ApproveConstants.TRANS_STATUS_WAIT;
		 TransInfo transInfo = new TransInfo();
		 String allfilenames="";//所有文件名称，用,间隔
		 int nodetype=0;
		 ArrayList<Long> preUserIds=new ArrayList<Long>();
		 
		 transInfo.setTitle(title);
		 transInfo.setWebcontent(content);
		 
		 transInfo.setTransStep(1);//第一步送审
		 transInfo.setModifytype(1);//表示签阅
		 transInfo.setUserID(user.getId());// 原始交办用户
		 transInfo.setSenddate(new Date());//事务发出日期
		 transInfo.setLastsignid(user.getId());//最近处理的人
		 transInfo.setSubmiter(user.getId());//最新提交人
		 transInfo.setSubmitdate(new Date());
         
    	 transInfo.setStatus(status);//不管有没有批阅，// 交办状态
    	 transInfo.setComment(comment);// 交办说明
    	 transInfo.setNodetype(1);//会签，交办都是并列的

    	 String signers="";
    	 for (int i=0;i<personIds.size();i++)
    	 {
    		 if (i==0)
    		 {
    			 signers+=personIds.get(i);
    		 }
    		 else
    		 {
    			 signers+=","+personIds.get(i);
    		 }
    	 }
         
         jqlService.save(transInfo);//保存了签批主数据
	         
         ArrayList<String> modifyfiles=new ArrayList<String>();
		 ArrayList<String> modifyname=new ArrayList<String>();
         if (filePaths!=null && filePaths.size()>0)
         {
        	 for (int i=0;i<filePaths.size();i++)
        	 {
        		 //将文件存到文件库中
        		 
        		 
        		 Fileinfo info=saveFile(filePaths.get(i),fileNames.get(i),user,fileSystemService);//保存文件
        		 
        		 TransFiles transFiles=new TransFiles();
        		 transFiles.setTransid(transInfo.getId());

        		 transFiles.setDocumentpath(info.getPathInfo());
        		 modifyfiles.add(info.getPathInfo());
        		 transFiles.setFileName(info.getFileName());
        		 modifyname.add(info.getFileName());
        		 transFiles.setUserID(user.getId());
        		 transFiles.setAdddate(new Date());
        		 jqlService.save(transFiles);//保存了交办附件
        		 
        		 if (i==0)
        		 {
        			 allfilenames+=info.getFileName();
        		 }
        		 else
        		 {
        			 allfilenames+=","+info.getFileName();
        		 }
        	 }
         }

//3存储历史记录
         TransTask task=insertTask(transInfo.getId(),title,user,status,
        		 "交办",ApproveConstants.TRANS_ACTION_SEND,comment,signers,1,modifyfiles
     			,modifyname,jqlService);
					
//4、存储处理人
  	     jqlService.excute("update TransSameInfo as a set a.isnew=a.isnew+1 where a.transid=? ", transInfo.getId());//isnew为0表示是最新的
         
    	 if (personIds!=null && personIds.size()>0)//处理者
         {
    		 List<String> mobilelist=new ArrayList<String>();
			 List<Long> tranids=new ArrayList<Long>();
			 String sendcontent="";
        	 for (int i=0;i<personIds.size();i++)
        	 {
        		 TransSameInfo sameinfo=new TransSameInfo();
        		 sameinfo.setTaskid(task.getId());
        		 sameinfo.setTransid(transInfo.getId());
        		 Users signer=(Users)jqlService.getEntity(Users.class, personIds.get(i));
        		 sameinfo.setSigner(signer);
        		 sameinfo.setSenduser(user.getId());
        		 sameinfo.setSenddate(new Date());
        		 
        		 sameinfo.setWarndate(new Date());
		         sameinfo.setWarnnum(0);
        		 
		         sameinfo.setState(ApproveConstants.TRANS_STATUS_START);
		         
        		 sameinfo.setNodetype(nodetype);
        		 sameinfo.setSignnum(personIds.size());
		         
		         sameinfo.setComment("");//不要将备注给处理者

		         sameinfo.setSubmitdate(new Date());
		         sameinfo.setSubmiter(user.getId());
		         jqlService.save(sameinfo);
		         
		         String mobile=signer.getMobile();
		         if (mobile!=null && mobile.length()==11)
		         {
		        	 mobilelist.add(mobile);
		        	 tranids.add(sameinfo.getId());
		         }
        	 }
        	//增加事务分发提醒
	         MessageUtil.instance().setOtherWarn(transInfo.getId(), MessageCons.SENDTRANS, personIds, comment, user.getRealName()+"分发事务("+title+")给您", user,0);
	         sendcontent=user.getRealName()+"请您对“"+transInfo.getTitle()+"”进行处理";//暂不做短信回复功能
        	 sendMobileinfo(mobilelist,sendcontent,Constant.TRANSSPLIT,tranids,false,user);
	         
         }

         return transInfo;
	}
	/**
	 * 存储或编辑交办草稿
	 * @param id 草稿id
	 * @param title  主题
	 * @param content  内容
	 * @param filePaths  附件路径
	 * @param fileNames  附件名称
	 * @param personIds  处理人编号
	 * @param personNames  处理人名称，多人用;间隔
	 * @param comment  备注说明
	 * @param user
	 * @return
	 */
	public boolean transSave(Long id,String title,String webcontent,ArrayList<String> filePaths
			,ArrayList<String> fileNames,ArrayList<Long> personIds,String personNames,String comment,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			TransSave transSave=new TransSave();
			if (id!=null && id>0)
			{
				transSave=(TransSave)jqlService.getEntity(TransSave.class, id);
			}
			transSave.setTitle(title);//主题
			transSave.setWebcontent(webcontent);//内容
			String filepaths="";//文件路径
			String filepathnames="";//文件名
			if (filePaths!=null && filePaths.size()>0 && fileNames!=null && fileNames.size()>0)
			{
				for (int i=0;i<filePaths.size();i++)
				{
					if (i==0)
					{
						filepaths+=filePaths.get(i);
						filepathnames+=fileNames.get(i);
					}
					else
					{
						filepaths+=";;;;"+filePaths.get(i);
						filepathnames+=";;;;"+fileNames.get(i);
					}
				}
			}
			transSave.setFilepaths(filepaths);
			transSave.setFilepathnames(filepathnames);
			String signers="";
			if (personIds!=null && personIds.size()>0)
			{
				for (int i=0;i<personIds.size();i++)
				{
					if (i==0)
					{
						signers+=personIds.get(i);
					}
					else
					{
						signers+=";"+personIds.get(i);
					}
				}
			}
			transSave.setSigners(signers);//处理人ID
			transSave.setSignernames(personNames);//处理人名称
			transSave.setComment(comment);//注释
			transSave.setAdddate(new Date());//提交时间
			transSave.setUserID(user.getId());//提交人
			if (id!=null && id>0)
			{
				jqlService.update(transSave);
			}
			else
			{
				jqlService.save(transSave);
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	public TransSave getTransSave(Long id,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			//获取附件
			TransSave transSave=(TransSave)jqlService.getEntity(TransSave.class, id);
			String signers=transSave.getSigners();
			String signernames="";//签批人名称，用,间隔
			if (signers!=null && signers.length()>0)
			{
				//处理签批者
				signernames=getUserName(signers);
				String[] userall=signers.split(";");
				String[] usernameall=signernames.split(";");
				List modifierlist=new ArrayList();
				for (int i=0;i<userall.length;i++)
				{
					String[] temp;
					if (i<usernameall.length)
					{
						temp=new String[]{userall[i],usernameall[i]};
					}
					else
					{
						temp=new String[]{userall[i],userall[i]};
					}
					modifierlist.add(temp);
				}
				transSave.setModifierlist(modifierlist);
			}
			transSave.setSignernames(signernames);
			if (transSave.getWebcontent()!=null && transSave.getWebcontent().length()>0)
			{
				transSave.setIswebcontent(true);
			}
			String filepaths=transSave.getFilepaths();
			String filepathnames=transSave.getFilepathnames();
			List filedata=new ArrayList();
			if (filepaths!=null && filepaths.length()>0)
			{
				String[] paths=filepaths.split(";;;;");
				String[] names=filepathnames.split(";;;;");
				for (int i=0;i<paths.length;i++)
				{
					String[] temp=new String[]{paths[i],names[i]};
					filedata.add(temp);
				}
				transSave.setFiledata(filedata);
			}
			return transSave;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 发出事务
	 * @param id
	 * @param title
	 * @param content
	 * @param filePaths
	 * @param fileNames
	 * @param personIds
	 * @param personNames
	 * @param comment
	 * @param user
	 * @return
	 */
	public boolean transCommit(Long id,String title,String content,ArrayList<String> filePaths
			,ArrayList<String> fileNames,ArrayList<Long> personIds,String personNames,String comment,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			TransInfo transInfo=createTrans(title,content,filePaths
					,fileNames,personIds,personNames,comment,user);//存储送审信息
			if (id!=null && id.longValue()>0)
			{
				//删除保存的事务草稿信息
				jqlService.deleteEntityByID(TransSave.class, "id", id);
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
				TransInfo transInfo=(TransInfo)jqlService.getEntity(TransInfo.class, id);
				if (transInfo.getWebcontent()!=null && transInfo.getWebcontent().length()>0)
				{
					String cont=transInfo.getWebcontent();
					if (cont!=null)
					{
						cont=cont.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
					}
					return cont;
				}
			}
			else
			{
				TransSave transSave=(TransSave)jqlService.getEntity(TransSave.class, id);
				if (transSave.getWebcontent()!=null && transSave.getWebcontent().length()>0)
				{
					String cont=transSave.getWebcontent();
					if (cont!=null)
					{
						cont=cont.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
					}
					return cont;
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
	 * 获取交办详细数据
	 * @param id
	 * @param user
	 * @return
	 */
	public TransInfo getTransPermit(Long id,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			TransInfo transInfo=(TransInfo)jqlService.getEntity(TransInfo.class, id);
			
			String sql="select a from TransFiles as a where a.transid=? and a.isnew=0 ";
			List<TransFiles> attachlist = (List<TransFiles>)jqlService.findAllBySql(sql, id);
			ArrayList<String[]> filelist=new ArrayList<String[]>();
			String filepath="";
			if (attachlist!=null && attachlist.size()>0)
			{
				for (int j=0;j<attachlist.size();j++)
				{
					TransFiles transFiles=attachlist.get(j);
					filelist.add(new String[]{transFiles.getDocumentpath(),transFiles.getFileName()});
				}
			}
			transInfo.setFilelist(filelist);
			transInfo.setSendname(((Users)jqlService.getEntity(Users.class, transInfo.getUserID())).getRealName());
			
			sql="select a from TransSameFiles as a where a.transid=? and a.isnew=0 and a.userID=? ";
			List<TransSameFiles> samefilelist = (List<TransSameFiles>)jqlService.findAllBySql(sql, id,user.getId());
			ArrayList<String[]> taskfilelist=new ArrayList<String[]>();
			if (samefilelist!=null && samefilelist.size()>0)
			{
				for (int i=0;i<samefilelist.size();i++)
				{
					TransSameFiles transSameFiles=samefilelist.get(i);
					taskfilelist.add(new String[]{transSameFiles.getDocumentpath(),transSameFiles.getFileName()});
				}
			}
			transInfo.setTaskfiles(taskfilelist);
			sql="select a from TransSameInfo as a where a.transid=? and a.isnew=0 and a.signer.id=? order by a.id DESC";
			List<TransSameInfo> samelist = (List<TransSameInfo>)jqlService.findAllBySql(sql, id,user.getId());
			if (samelist!=null && samelist.size()>0)
			{
				TransSameInfo transSameInfo=samelist.get(0);
				transInfo.setDetail(transSameInfo.getComment());
			}
			return transInfo;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 事务处理
	 * @param id
	 * @param filePaths 处理过程中上传的文件
	 * @param fileNames 上传的文件名
	 * @param comment 处理说明
	 * @param handle  在办，还是办结
	 * @param user
	 * @return
	 */
	public boolean transModify(Long id,String comment,ArrayList<String> filePaths,ArrayList<String> fileNames
			,Boolean handle,Users user)
	{
		try
		{
			FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
			TransInfo transInfo=(TransInfo)jqlService.getEntity(TransInfo.class, id);

			transInfo.setSubmitdate(new Date());
			transInfo.setSubmiter(user.getId());
			String sql="select a from TransSameInfo as a where a.transid=? and a.isnew=0 and a.signer.id=? order by a.id DESC";
			List<TransSameInfo> samelist = (List<TransSameInfo>)jqlService.findAllBySql(sql, id,user.getId());
			long state=ApproveConstants.TRANS_STATUS_WAIT;
			long actionid=ApproveConstants.TRANS_ACTION_WAIT;
			if (samelist!=null && samelist.size()>0)
			{
				//更新用户处理信息
				List<String> mobilelist=new ArrayList<String>();
				List<Long> tranids=new ArrayList<Long>();
				String sendcontent="";
				 
				TransSameInfo transSameInfo=samelist.get(0);
				transSameInfo.setComment(comment);
				if (handle)
				{
					transSameInfo.setState(ApproveConstants.TRANS_STATUS_WAIT);
				}
				else
				{
					transSameInfo.setState(ApproveConstants.TRANS_STATUS_HAD);
					state=ApproveConstants.TRANS_STATUS_HAD;
					actionid=ApproveConstants.TRANS_ACTION_HAD;
				}
				transSameInfo.setActionid(actionid);
				transSameInfo.setSubmitdate(new Date());
				transSameInfo.setSubmiter(user.getId());
				jqlService.update(transSameInfo);
				if (!handle)//办结要短信通知交办人员
				{
					Users senduser=(Users)jqlService.getEntity(Users.class, transInfo.getUserID());
					String mobile=senduser.getMobile();
			        if (mobile!=null && mobile.length()==11)
			        {
			        	mobilelist.add(mobile);
			        	tranids.add(transSameInfo.getId());
			        }
			        sendcontent=user.getRealName()+"已对您交办的“"+transInfo.getTitle()+"”处理完毕";//暂不做短信回复功能
			        sendMobileinfo(mobilelist,sendcontent,Constant.TRANSSPLIT,tranids,false,user);
				}
			}
			//更新处理附件
			ArrayList<String> hadpaths=new ArrayList<String>();
			ArrayList<String> hadnames=new ArrayList<String>();
			if (filePaths!=null && filePaths.size()>0)
			{
				sql="select a from TransSameFiles as a where a.transid=? and a.isnew=0 and a.userID=? order by a.id DESC";
				List<TransSameFiles> samefilelist = (List<TransSameFiles>)jqlService.findAllBySql(sql, id,user.getId());
				if (samefilelist!=null && samefilelist.size()>0)
				{
					for (int i=0;i<samefilelist.size();i++)
					{
						TransSameFiles transSameFiles=samefilelist.get(i);
						boolean isLive=false;
						for (int j=0;j<filePaths.size();j++)
						{
							if (transSameFiles.getDocumentpath().equals(filePaths.get(j)))
							{
								//已存在的
								isLive=true;
								filePaths.remove(j);
								fileNames.remove(j);
								break;
							}
						}
						if (!isLive)
						{
							//jcrService.delete(transSameFiles.getDocumentpath());
							jqlService.deleteEntityByID(TransSameFiles.class, "id", transSameFiles.getId());//去除了
						}
					}
				}

				//直接添加附件
				for (int j=0;j<filePaths.size();j++)
				{
					Fileinfo info=saveFile(filePaths.get(j),fileNames.get(j),user,fileSystemService);//保存文件
					TransSameFiles transSameFiles=new TransSameFiles();
					transSameFiles.setDocumentpath(info.getPathInfo());
					transSameFiles.setFileName(info.getFileName());
					transSameFiles.setTransid(id);
					transSameFiles.setUserID(user.getId());
					transSameFiles.setAdddate(new Date());
					jqlService.save(transSameFiles);
				}
				sql="select a from TransSameFiles as a where a.transid=? and a.isnew=0 and a.userID=? order by a.id DESC";
				samefilelist = (List<TransSameFiles>)jqlService.findAllBySql(sql, id,user.getId());
				if (samefilelist!=null && samefilelist.size()>0)
				{
					for (int i=0;i<samefilelist.size();i++)
					{
						TransSameFiles transSameFiles=samefilelist.get(i);
						hadpaths.add(transSameFiles.getDocumentpath());
						hadnames.add(transSameFiles.getFileName());
					}
				}
			}
			//记录处理历史
			TransTask transtask = insertTask(id,transInfo.getTitle(),user
					, state,"办理",actionid,comment,null,1
					,hadpaths,hadnames
					,jqlService);
			//处理交办事务总状态,暂不处理
			
			sql="select a from TransSameInfo as a where a.transid=? and a.isnew=0 and a.signer.id=? order by a.id DESC";
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	public boolean transDelete(Long id,int type,Users user)
	{//删除事务或草稿
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (type==0)
			{
				TransInfo transInfo=(TransInfo)jqlService.getEntity(TransInfo.class, id);
				if (user.getId().longValue()==transInfo.getUserID().longValue() && type==2)
				{
					//彻底删除了
					//删除附件
					//删除人员
					//删除历史记录
					//等等，等其他做完了再考虑这个
//					jqlService.deleteEntityByID(TransInfo.class, "id", id);
					transInfo.setIsview(1);
					jqlService.update(transInfo);
				}
				else//将其他人员的删除进行隐藏
				{
					String sql="select a from TransSameInfo as a where a.transid=? and a.isnew=0 and a.isview=0 and a.signer.id=? order by a.id DESC";
					List<TransSameInfo> samelist = (List<TransSameInfo>)jqlService.findAllBySql(sql, id,user.getId());
					if (samelist!=null && samelist.size()>0)
					{
						TransSameInfo transSameInfo=samelist.get(0);
						transSameInfo.setIsview(1);
						jqlService.update(transSameInfo);
					}
				}
			}
			else
			{
				TransSave transSave=(TransSave)jqlService.getEntity(TransSave.class, id);
				String filepaths=transSave.getFilepaths();
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
				jqlService.deleteEntityByID(TransSave.class, "id", id);
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	
	public Map<String,Object> getTransHistosy(Long userId, Long transid)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		try
		{
			List<Map<String,Object>> historylist = new ArrayList<Map<String,Object>>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			TransInfo info = (TransInfo)jqlService.getEntity(TransInfo.class, transid);
			String fileName = "";
			String date = "";
			String ownerName = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String queryString = "select u.realName, t from TransTask t, Users u "
				+ " where t.submiter = u.id and t.transid = ? order by t.id ";
			List<Object[]> list = jqlService.findAllBySql(queryString, info.getId());
			result.put("affairName",info.getTitle() );//交办事务主题
			ownerName = userService.getUser(info.getUserID()).getRealName();	//交办人			
			date = sdf.format(info.getSenddate());//交办时间
			result.put("affairPerson", ownerName);	
			result.put("affairDate", date);	//交办时间
			String signers="";
			String sql="select a from TransSameInfo as a where a.transid=? order by a.id ";
			List<TransSameInfo> samelist=jqlService.findAllBySql(sql, transid);
			if (samelist!=null && samelist.size()>0)
			{
				for (int j=0;j<samelist.size();j++)
				{
					if (j==0)
					{
						signers+=""+samelist.get(j).getSigner().getRealName();
					}
					else
					{
						signers+=","+samelist.get(j).getSigner().getRealName();
					}
				}
			}
			result.put("signers",signers);//办理者
	
	
	//		String endName = "doc";
	//		result.put("fileType", WebofficeUtility.getFileIconPathSrc(endName,false,false,false,"48", ".png"));
	
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					TransTask transTask = (TransTask)objArray[1];
					Map temp = new HashMap();
					if (objArray[0]!=null)
					{
						temp.put("actor", objArray[0].toString());//处理者
					}
					int process=1;
					if (transTask.getStateid()!=null && transTask.getStateid().longValue()==ApproveConstants.TRANS_STATUS_HAD)
					{
						process=2;
					}
					
	//				temp.put("actionid",transTask.getActionid());//操作名称
					String actionname="在办";
					String detailname="办理详情";
					if (transTask.getActionid().longValue()==ApproveConstants.TRANS_ACTION_SEND)
					{
						actionname="交办";
						detailname="交办备注";
						process=0;
					}
					else if (transTask.getActionid().longValue()==ApproveConstants.TRANS_ACTION_HAD)
					{
						actionname="办结";
					}
					temp.put("process",process);//办理进度，1表示办结，0表示在办（前台用的）
					temp.put("detailname",detailname);
					temp.put("actionname",actionname);//操作名称
					temp.put("time", sdf.format(transTask.getSubmitdate()));//处理时间
					temp.put("detail", transTask.getComment()==null?"":transTask.getComment());
					temp.put("stepName", transTask.getStepName()==null?"":transTask.getStepName());//办理类型
					
					List<TransTaskFiles> tfilelist=(List<TransTaskFiles>)jqlService.findAllBySql("select a from TransTaskFiles as a where a.taskid=? ",transTask.getId());
					List<Map> attachlist=new ArrayList<Map>();
					if (tfilelist!=null && tfilelist.size()>0)
					{
						for (int n=0;n<tfilelist.size();n++)
						{
							TransTaskFiles tfile=tfilelist.get(n);
							Map tempmap = new HashMap();
							tempmap.put("filename",tfile.getFileName());
							tempmap.put("filepath",tfile.getDocumentpath());
							attachlist.add(tempmap);
						}
					}
					temp.put("attachFile", attachlist);
					//阅读信息后面处理
					historylist.add(temp);
				}
			}
			
			result.put("history", historylist);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	public HashMap<String, Object> getTransDrafts(int start,int count,String sort,String order,Users user)
	{//获取交办事务草稿
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			sql="select distinct a.id,a.title,a.filepaths,a.filepathnames,a.webcontent,a.signers,u.realName,a.adddate,a.comment "
					+" from transsave a ,users u "
					+" where a.userID=u.id and a.userID="+user.getId();
			
 			if (ApproveConstants.TRANSTITLE.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.title using gbk) "+order;
			}
			else if (ApproveConstants.TRANSACCEPTER.equals(sort))//交办事务收文人
			{
				sql+=" order by a.signers "+order;
			}
			else
			{
				sql+=" order by a.adddate desc ";
			}
			List<Object[]> draftlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			sqlcount="select count(a) from TransSave as a where a.userID=? ";
			Long size=(Long)jqlService.getCount(sqlcount, user.getId());
			resultmap.put("fileListSize", size);//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<draftlist.size();i++)
			{
				Object[] objs=draftlist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				values.put("id", id);//主键编号
				values.put("modifytype", "去除");//类型
				values.put("title", (String)objs[1]);//标题(名称)
				values.put("sender", user.getRealName());//交办事务发起人
				String filepath=(String)objs[2];//文件路径
				String filepathname=(String)objs[3];//文件名
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
				String webcontent=(String)objs[4];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/交办内容");
					filenames.add(id+"/交办内容");
				}
				else
				{
					files.add("");
					filenames.add("");
				}
				values.put("files", files);//附件列表
				values.put("filesnames", filenames);//附件列表
				values.put("modifytime", sdf.format((Date)objs[7]));//提交（最后处理）时间
				values.put("comment", (String)objs[8]);
				if (objs[5]!=null)
				{
					values.put("accepter",getUserName((String)objs[5]));//收文人
				}
				else
				{
					values.put("accepter","");//收文人
				}
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
	
	public HashMap<String, Object> getTransTodo(int start,int count,String sort,String order,Users user,ArrayList<Long> userIds,String selectedTime,String searchName)
	{//获取待办列表
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			String countString ="";
			sql="select distinct a.id,a.title,a.status,a.userID,u.realName,a.comment,a.senddate,a.webcontent "
				+" from transinfo a , transsameinfo b ,users u "
				+" where a.id=b.transid and (a.isview is null or a.isview=0) and b.isnew=0 and b.islast=0 and b.isview=0 and (b.state="+ApproveConstants.TRANS_STATUS_START
				+" or b.state="+ApproveConstants.TRANS_STATUS_WAIT+")"
				+" and a.userID=u.id "
				+" and b.signer_id="+user.getId()
				;//未办和在办都属于待办
			//筛选条件
			//发送人筛选
			if(userIds != null && userIds.size() > 0)
			{
				for (int i = 0;i < userIds.size();i++) {
					if(i == 0)
					{
						sql += " and ( a.userID =" + userIds.get(0);
						countString += " and ( a.userID =" + userIds.get(0);
					}else {
						sql += " or a.userID =" + userIds.get(i);
						countString += " or a.userID =" + userIds.get(i);
					}
				}
				sql += ")";
				countString += ")";
			}
			//时间段筛选
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
				sql += " and a.senddate >='"+sTime+"' and a.senddate <='"+eTime+"'";
				countString += " and a.senddate >='"+sTime+"' and a.senddate <='"+eTime+"'";
			}
			//名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and a.title like '%"+searchName+"%'";
				countString += " and a.title like '%"+searchName+"%'";
			}
			if (ApproveConstants.SENDTIME.equals(sort))//按照送文时间排序
			{
				sql+=" ORDER BY a.senddate "+order;
			}
			else if (ApproveConstants.TITLE.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.title using gbk) "+order;
			}
			else if (ApproveConstants.MODIFYTYPE.equals(sort))//按照类型排序
			{
				sql+=" order by a.modifytype "+order;
			}
			else if (ApproveConstants.STATE.equals(sort))//收文人
			{
				sql+=" order by a.status "+order;
			}
			else if (ApproveConstants.SENDER.equals(sort))//送文人
			{
				sql+=" order by convert(u.realName using gbk) "+order;
			}
			else if (ApproveConstants.MODIFYTIME.equals(sort))//处理时间
			{
				sql+=" order by a.submitdate "+order;
			}
			else if (ApproveConstants.COMMENT.equals(sort))//备注
			{
				sql+=" order by convert(a.comment using gbk) "+order;
			}
			else
			{
				sql+=" order by a.senddate desc ";
			}
			sqlcount="select count(distinct a.id) from transinfo a , transsameinfo b "
					+" where a.id=b.transid and (a.isview is null or a.isview=0) and b.isnew=0 and b.islast=0 and b.isview=0 and (b.state="+ApproveConstants.TRANS_STATUS_START
					+" or b.state="+ApproveConstants.TRANS_STATUS_WAIT+")"
					+" and b.signer_id="+user.getId()
					+countString;
			List<Object[]> mylist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			List<BigInteger> sizelist=(List<BigInteger>)jqlService.getObjectByNativeSQL(sqlcount,-1,-1);
			
			resultmap.put("fileListSize", sizelist.get(0).longValue());//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<mylist.size();i++)
			{
				Object[] objs=mylist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				long sendid=((BigInteger)objs[3]).longValue();
				if (user.getId().longValue()==sendid)
				{
					values.put("issend", "Y");//是交办人
				}
				else
				{
					values.put("issend", "");//不是交办人
				}
				values.put("id", id);//主键编号
				
				values.put("sender", (String)objs[4]);//交办人
//				
				sql="select a from TransFiles as a where a.transid=? and a.isnew=0 ";
				List<TransFiles> fileslist = (List<TransFiles>)jqlService.findAllBySql(sql, ((BigInteger)objs[0]).longValue());
				String filepath="";
				List<String> files=new ArrayList<String>();
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
				}
				String webcontent=(String)objs[7];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/交办内容");
				}
				else
				{
					files.add("");
				}
				values.put("files", files);
				values.put("title", (String)objs[1]);//标题
				
				if (objs[6]!=null)
				{
					values.put("sendtime", sdf.format((Date)objs[6]));//交办时间
				}
				else
				{
					values.put("sendtime", "");//交办时间
				}
				values.put("comment", (String)objs[5]);//备注
				List<TransSameInfo> samelist=(List<TransSameInfo>)jqlService.findAllBySql("select a from TransSameInfo as a where a.transid=? and a.signer.id=? and a.isnew=0 ", id,user.getId());
				if (samelist!=null && samelist.size()>0)
				{
					values.put("signtag", samelist.get(0).getSigntag());
					values.put("warnnum", samelist.get(0).getWarnnum());//催办次数
				}
				else
				{
					values.put("signtag", "");
					values.put("warnnum", 0);//催办次数
				}
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
	public HashMap<String, Object> getTransDone(int start,int count,String sort,String order,Users user,ArrayList<Long> userIds,String selectedTime,String searchName)
	{//获取办结列表
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			String countString="";
			sql="select distinct a.id,a.title,a.status,u.realName,a.comment,a.webcontent,a.senddate "
					+" from transinfo a ,transsameinfo b "
				+" ,users u "
				+" where a.id=b.transid and (a.isview is null or a.isview=0) and (b.isview is null or b.isview=0 ) and b.isnew=0 and b.state="+ApproveConstants.TRANS_STATUS_HAD
				+" and a.userID=u.id "
				+" and b.signer_id="+user.getId()
				;//去除当前送文人待处理的记录
			//筛选条件
			//交办人筛选
			if(userIds != null && userIds.size() > 0)
			{
				for (int i = 0;i < userIds.size();i++) {
					if(i == 0)
					{
						sql += " and ( a.userID =" + userIds.get(0);
						countString += " and ( a.userID =" + userIds.get(0);
					}else {
						sql += " or a.userID =" + userIds.get(i);
						countString += " or a.userID =" + userIds.get(i);
					}
				}
				sql += ")";
				countString += ")";
			}
			//时间段筛选
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
				sql += " and a.senddate >='"+sTime+"' and a.senddate <='"+eTime+"'";
				countString += " and a.senddate >='"+sTime+"' and a.senddate <='"+eTime+"'";
			}
			//名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and a.title like '%"+searchName+"%'";
				countString += " and a.title like '%"+searchName+"%'";
			}
			if ("modifytime".equals(sort))
			{
				sql+=" order by b.submitdate "+order;
			}
			else if (ApproveConstants.SENDTIME.equals(sort))//按照更新时间排序
			{
				sql+=" order by a.senddate "+order;
			}
			else if (ApproveConstants.TITLE.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.title using gbk) "+order;
			}
			else if (ApproveConstants.STATE.equals(sort))//按照状态排序
			{
				sql+=" order by a.status "+order;
			}
			else if (ApproveConstants.SENDER.equals(sort))//送文人
			{
				sql+=" order by convert(u.realName using gbk) "+order;
			}
			else if (ApproveConstants.MODIFYTYPE.equals(sort))//类型
			{
				sql+=" order by a.modifytype "+order;
			}
			else if (ApproveConstants.MODIFYTIME.equals(sort))//处理时间
			{
				sql+=" order by a.submitdate "+order;
			}
			else if (ApproveConstants.COMMENT.equals(sort))//备注
			{
				sql+=" order by convert(a.comment using gbk) "+order;
			}
			else
			{
				sql+=" order by b.submitdate desc ";
			}
			sqlcount="select count(distinct a.id) from transinfo a ,transsameinfo b "
					+" where a.id=b.transid and (a.isview is null or a.isview=0) and (b.isview is null or b.isview=0 ) and b.isnew=0 and b.state="+ApproveConstants.TRANS_STATUS_HAD
					+" and b.signer_id="+user.getId()+countString;
			
			List<Object[]> mylist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			List<BigInteger> sizelist=(List<BigInteger>)jqlService.getObjectByNativeSQL(sqlcount,-1,-1);
			
			resultmap.put("fileListSize", sizelist.get(0).longValue());//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<mylist.size();i++)
			{
				Object[] objs=mylist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				values.put("id", id);//主键编号
				values.put("sender", (String)objs[3]);//办文人
				sql="select a from TransFiles as a where a.transid=? and a.isnew=0 ";
				List<TransFiles> fileslist = (List<TransFiles>)jqlService.findAllBySql(sql, id);
				String filepath="";
				List<String> files=new ArrayList<String>();
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
				}
				
				String webcontent=(String)objs[5];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/交办内容");
				}
				else
				{
					files.add("");
				}
				values.put("files", files);//附件列表
				values.put("title", (String)objs[1]);//标题
				
				sql="select a from TransSameInfo as a where a.transid=? and a.signer.id=? order by a.id DESC ";
				List<TransSameInfo> sameslist = (List<TransSameInfo>)jqlService.findAllBySql(sql, id,user.getId());
				if (sameslist!=null && sameslist.size()>0)
				{
					values.put("modifytime", sdf.format(sameslist.get(0).getSubmitdate()));//处理时间或签批时间
				}
				else
				{
					values.put("modifytime", "");
				}
				if (objs[6]!=null)
				{
					values.put("sendtime", sdf.format((Date)objs[6]));//交办时间
				}
				else
				{
					values.put("sendtime", "");//交办时间
				}
				
				values.put("comment", (String)objs[4]);//备注
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
	
	public HashMap<String, Object> getTransMyquestfiles(int start,int count,String sort,String order,Users user,ArrayList<Long> userIds,String selectedTime,String searchName)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			String countString="";
			sql="select distinct a.id,a.modifytype,a.status,a.title,a.comment,a.webcontent,a.senddate from TransInfo a ,users u,TransSameInfo b "
					+" where a.userID=u.id and (a.isview is null or a.isview=0) and a.status<5 and a.userID="+user.getId();//终止就不算了
			//筛选条件
			//发送人筛选
			String cond="";
			if(userIds != null && userIds.size() > 0)
			{
				cond+=" and a.id = b.transid  and b.isnew=0";
				sql += " and a.id = b.transid  and b.isnew=0";
				countString += " and a.id = b.transid  and b.isnew=0";
				for (int i = 0;i < userIds.size();i++) {
					if(i == 0)
					{
						cond+=" and ( b.signer_id =" + userIds.get(0);
						sql += " and ( b.signer_id =" + userIds.get(0);
						countString += " and ( b.signer_id =" + userIds.get(0);
					}else {
						sql += " or b.signer_id =" + userIds.get(i);
						countString += " or b.signer_id =" + userIds.get(i);
					}
				}
				sql += ")";
				countString += ")";
			}
			//时间段筛选
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
				sql += " and a.senddate >='"+sTime+"' and a.senddate <='"+eTime+"'";
				countString += " and a.senddate >='"+sTime+"' and a.senddate <='"+eTime+"'";
			}
			//名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and a.title like '%"+searchName+"%'";
				countString += " and a.title like '%"+searchName+"%'";
			}
			if (ApproveConstants.SENDTIME.equals(sort))//按照更新时间排序
			{
				sql+=" ORDER BY a.senddate "+order;
			}
			else if (ApproveConstants.TITLE.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.title using gbk) "+order;
			}
			else if (ApproveConstants.MODIFYTYPE.equals(sort))//按照类型排序
			{
				sql+=" order by a.modifytype "+order;
			}
			else if (ApproveConstants.STATE.equals(sort))//按照类型排序
			{
				sql+=" order by a.status "+order;
			}
			else
			{
				sql+=" order by a.senddate desc ";
			}
			
			sqlcount="select count(distinct a) from TransInfo as a,TransSameInfo as b where a.userID=? and a.status<5 and (a.isview is null or a.isview=0)"+countString;
			List<Object[]> sendlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			Long size=(Long)jqlService.getCount(sqlcount, user.getId());
			resultmap.put("fileListSize", size);//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<sendlist.size();i++)
			{
				Object[] objs=sendlist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				values.put("id", id);//主键编号
				Integer modifytype=(Integer)objs[1];
				Integer status=((BigInteger)objs[2]).intValue();
				if (status.intValue()==ApproveConstants.NEW_STATUS_START || status.intValue()==ApproveConstants.NEW_STATUS_HADREAD)
				{
					values.put("modifytype", "交办事务");//类型
				}
				else
				{
					values.put("modifytype", "交办事务");//类型
				}
				
				values.put("sender", user.getRealName());//送文人
				values.put("title", (String)objs[3]);//标题(名称)
				values.put("comment", (String)objs[4]);//备注
				sql="select a from TransFiles as a where a.transid=? ";
				List<TransFiles> fileslist = (List<TransFiles>)jqlService.findAllBySql(sql, id);
				String filepath="";
				List<String> files=new ArrayList<String>();
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
				}
				String webcontent=(String)objs[5];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/交办内容");
				}
				else
				{
					files.add("");
				}
				values.put("files", files);//附件列表
				List modifierlist=new ArrayList();
				String accepter="";
				sql="select a from TransSameInfo as a where a.transid=? and a.isnew=0 ";
				List<TransSameInfo> samelist = (List<TransSameInfo>)jqlService.findAllBySql(sql, id);
				if (samelist!=null && samelist.size()>0)
				{
					for (int j=0;j<samelist.size();j++)
					{
						TransSameInfo transSameInfo=samelist.get(j);
						Long state=transSameInfo.getState();
						String statename="在办";
						 
						if (ApproveConstants.TRANS_STATUS_START==state.longValue())
						{
							statename="未读";
							if ("Y".equals(transSameInfo.getSigntag()))
							{
								statename="已读";
							}
						}
						else if (ApproveConstants.TRANS_STATUS_WAIT==state.longValue())
						{
							statename="在办";
						}
						else if (ApproveConstants.TRANS_STATUS_HAD==state.longValue())
						{
							statename="已办";
						}
						String[] temp=new String[]{transSameInfo.getSigner().getRealName(),statename};
						modifierlist.add(temp);
						if (j>0)
						{
							accepter+="; "+transSameInfo.getSigner().getRealName();
						}
						else
						{
							accepter+=transSameInfo.getSigner().getRealName();
						}
					}
				}
				
				values.put("accepter", modifierlist);//收文人
				
				values.put("sendtime", sdf.format((Date)objs[6]));//送文时间
				
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
	
	
	public boolean transsignreal(String id,Users user) 
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (id!=null && id.length()>0 && !id.toLowerCase().equals("null"))
			{
				List list=jqlService.findAllBySql("select a from TransSameInfo as a where a.transid=? and a.signer.id=? and a.isnew=0 ", Long.valueOf(id),user.getId());
				if (list!=null && list.size()>0)
				{
					TransSameInfo same=(TransSameInfo)list.get(0);
					same.setSigntag("Y");
					same.setSigntagdate(new Date());
					jqlService.update(same);
					jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.outid=? and a.type=? and a.msguser.id=?",new Date(),Long.valueOf(id),MessageCons.SENDTRANS,user.getId());
					List<Messages> msglist=jqlService.findAllBySql("select a from Messages as a where a.outid=? and a.type=? and a.msguser.id=?",Long.valueOf(id),MessageCons.SENDTRANS,user.getId());
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
	public boolean getTransSignReal(String id,Users user) 
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (id!=null && id.length()>0 && !id.toLowerCase().equals("null"))
			{
				List list=jqlService.findAllBySql("select a from TransSameInfo as a where a.transid=? and a.signer.id=? and a.isnew=0 and a.signtag='Y' ", Long.valueOf(id),user.getId());
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
	
	public List getHistoryFiles(Long historyid)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			List<TransTaskFiles> filelist = (List<TransTaskFiles>)jqlService.findAllBySql("select a from TransTaskFiles as a where a.taskid=? ", historyid);
			List<String[]> backlist=new ArrayList<String[]>();
			if (filelist!=null && filelist.size()>0)
			{
				for (int i=0;i<filelist.size();i++)
				{
					TransTaskFiles taskfile=filelist.get(i);
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
