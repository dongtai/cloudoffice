package apps.transmanager.weboffice.service.approval;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.client.constant.WebofficeUtility;
import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.both.MessageCons;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.ApprovalCollect;
import apps.transmanager.weboffice.databaseobject.ApprovalCooper;
import apps.transmanager.weboffice.databaseobject.ApprovalCooperFiles;
import apps.transmanager.weboffice.databaseobject.ApprovalDefaulter;
import apps.transmanager.weboffice.databaseobject.ApprovalDic;
import apps.transmanager.weboffice.databaseobject.ApprovalFiles;
import apps.transmanager.weboffice.databaseobject.ApprovalInfo;
import apps.transmanager.weboffice.databaseobject.ApprovalReader;
import apps.transmanager.weboffice.databaseobject.ApprovalSave;
import apps.transmanager.weboffice.databaseobject.ApprovalSignRead;
import apps.transmanager.weboffice.databaseobject.ApprovalTask;
import apps.transmanager.weboffice.databaseobject.ApprovalTaskFiles;
import apps.transmanager.weboffice.databaseobject.Messages;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.SameSignInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersOrganizations;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.domain.workflow.WorkFlowPicBean;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.server.BackgroundSend;

public class SignUtil {

private static SignUtil instance=new SignUtil();
    
    public SignUtil()
    {    	
    	instance =  this;//new SignUtil();
    }
    public static SignUtil instance()
    {
        return instance;
    }
    private void sendMobileinfo(List<String> mobilelist,String content,int type,List<Long> ids,boolean isback,Users user)
    {
    	if (mobilelist.size()>0)
	   	{
	    	 Thread receiveT = new Thread(new BackgroundSend(mobilelist.toArray(new String[mobilelist.size()]),content
	    			 ,user.getCompany().getId(),user.getCompany().getName()
	    			 ,Constant.MOBILESIGN,ids.toArray(new Long[ids.size()]),isback,user));
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
				if (uids!=null && uids.length>0)
				{
					for (int i=0;i<uids.length;i++)
					{
						//临时用ID去查询，以后再比较一下性能
						Users user=(Users)jqlService.getEntity(Users.class, Long.valueOf(uids[i]));
						
						if (i==0)
						{
							if (user==null)
							{
								usernames+="已删除";
							}
							else
							{
								usernames+=user.getRealName();
							}
						}
						else
						{
							if (user==null)
							{
								usernames+=",已删除";
							}
							else
							{
								usernames+=","+user.getRealName();
							}
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
	 * 工具方法，将文件路径改成链接和文件名
	 * @param filepath
	 * @return
	 */
	private String getFileLink(String filepath)
	{
		String files="";
		if (filepath!=null && filepath.length()>0)
		{
			String[] detailpath=filepath.split(",");
			if (detailpath!=null)
			{
				for (int j=0;j<detailpath.length;j++)
				{
					int index=detailpath[j].lastIndexOf("/");
					String filename=detailpath[j];
					if (index>0)
					{
						filename=detailpath[j].substring(index+1);
					}
					if (j==0)
					{
						files+="<a href='"+detailpath[j]+"'>"+filename+"</a>";
					}
					else
					{
						files+="<a href='"+detailpath[j]+"'>"+filename+"</a><br>";
					}
				}
			}
		}
		return files;
	}
	/**
	 * 
	 * @param approvalid 流程编号
	 * @param title  标题
	 * @param user  签批用户
	 * @param actionid 动作
	 * @param stateid  状态
	 * @param stepName  步骤
	 * @param comment  备注
	 * @param issame  是否会签标记
	 * @param isnodetag  是否送签标记
	 * @param coopers 文档协作者，多人之间用,间隔
	 * @param signers 签批者的人ID,多人用,间隔
	 * @param sendreaders 传阅人，多人用,间隔
	 * @param signtype 签批类别，1为串签,2为并行会签
	 * @param signtagdate 签收时间，用于流程时间戳显示
	 * @param jqlService
	 */
	private ApprovalTask insertTask(Long approvalid,String title,String webcontent,Users user,Long actionid
		,Long stateid,String stepName,String comment,Boolean issame,Boolean isnodetag
		,String signers,String sendreaders,String coopers,int approvalStep
		,Integer signtype,Date signtagdate
		,JQLServices jqlService)
	{
		
		ApprovalTask task = new ApprovalTask();
		
		if (isnodetag!=null && isnodetag.booleanValue() && approvalStep>0)//是签批才加步骤
		{
			task.setApprovalStep(approvalStep);
		}
		task.setApprovalID(approvalid);
		task.setApprovalUserID(user.getId());
		task.setDate(new Date());
		task.setComment(comment);
		task.setAction(actionid);
		task.setTitle(title);
		task.setWebcontent(webcontent);
		task.setStepName(stepName);	
		task.setStateid(stateid);
		task.setSigntagdate(signtagdate);
		if (issame!=null && issame.booleanValue())
		{
			task.setNodetype(1);
		}
		else
		{
			task.setNodetype(0);
		}
		task.setIsnodetag(isnodetag);
		task.setSigners(signers);
		task.setSendreaders(sendreaders);
		task.setCoopers(coopers);
		task.setModifier(user.getId());
		task.setModifytime(new Date());
		jqlService.save(task);
		saveTaskFiles(approvalid,task.getId(),user);
		return task;
	}
	/**
	 * 将处理人的过程和状态放到一起
	 * @param approvalInfo
	 * @param sameSignInfo
	 * @param approvalReader
	 * @param taskid
	 */
	private void createSignRead(ApprovalInfo approvalInfo,SameSignInfo sameSignInfo,ApprovalReader approvalReader)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			ApprovalSignRead signread=new ApprovalSignRead();
			if (sameSignInfo!=null)
			{
				signread.setSender(jqlService.getUsers(sameSignInfo.getUserID()));
				signread.setSendtime(new Date());
				if (sameSignInfo.getComment()!=null)
				{
					signread.setSendcomment(sameSignInfo.getComment());
					signread.setSigncomment(sameSignInfo.getComment());//送文时的备注
					signread.setSigntime(new Date());
				}
				else
				{
//					signread.setSendcomment(approvalInfo.getComment());
				}
				signread.setSigner(sameSignInfo.getSigner());
				signread.setModifytype(1);

				signread.setState(sameSignInfo.getState());//ApproveConstants.NEW_STATUS_WAIT;//状态,记录当前人的状态，未变化，NEW_STATUS_READ
				signread.setActionid(sameSignInfo.getActionid());
				signread.setApprovalid(approvalInfo.getId());
	//			private Date signtime;//签批（处理）时间
	//		    private String signcomment;//签批（处理）备注
	//			signread.setFilename(filename);//用####间隔的文件名
	//			signread.setFileversion(fileversion);//用####间隔的版本
	//			private String signtag;//签收标记,已签为Y
	//			private Date signtagdate;//签收时间
				
				signread.setSameid(sameSignInfo.getSid());
				jqlService.save(signread);
			}
			else if (approvalReader!=null)
			{
				signread.setSender(jqlService.getUsers(approvalReader.getUserId()));
				signread.setSendtime(new Date());
				if (approvalReader.getComment()!=null)
				{
					signread.setSendcomment(approvalReader.getComment());
				}
				else
				{
//					signread.setSendcomment(approvalInfo.getComment());
				}
				signread.setSigner(jqlService.getUsers(approvalReader.getReadUser()));
				signread.setModifytype(2);
				signread.setState(approvalReader.getState());//ApproveConstants.NEW_STATUS_WAIT;//状态,记录当前人的状态，未变化，NEW_STATUS_READ
				signread.setActionid(1l);
				signread.setApprovalid(approvalInfo.getId());
				
	//			private Date signtime;//签批（处理）时间
	//		    private String signcomment;//签批（处理）备注
	//			signread.setFilename(filename);//用####间隔的文件名
	//			signread.setFileversion(fileversion);//用####间隔的版本
	//			private String signtag;//签收标记,已签为Y
	//			private Date signtagdate;//签收时间
				
				signread.setReadid(approvalReader.getId());
				jqlService.save(signread);
			}
//			jqlService.excute("update ApprovalTaskFiles as a set a.signreadid=? where a.taskid=?", signread.getId(),taskid);//将处理过程中的附件与签阅关联
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 签批信息更改时，同时要更改这些信息
	 * @param approvalInfo
	 * @param sameSignInfo
	 * @param approvalReader
	 * @param taskid
	 */
	private void updateSignRead(ApprovalInfo approvalInfo,SameSignInfo sameSignInfo,ApprovalReader approvalReader,Long taskid)
	{
		try
		{
			//更改的信息有
//			private Date signtime;//签批（处理）时间
//		    private String signcomment;//签批（处理）备注
//			private String signtag;//签收标记,已签为Y
//			private Date signtagdate;//签收时间
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (sameSignInfo!=null)
			{
				if (sameSignInfo.getCurrentsign()!=null && sameSignInfo.getCurrentsign().intValue()==1)
				{
					jqlService.excute("update ApprovalSignRead as a set a.signcomment=?,a.signtime=?,a.signtag=?,a.signtagdate=?,a.state=?,a.actionid=? where a.sameid=?"
							,sameSignInfo.getComment(),sameSignInfo.getApprovalDate(),sameSignInfo.getSigntag(),sameSignInfo.getSigntagdate(),ApproveConstants.NEW_STATUS_READ,sameSignInfo.getActionid(),sameSignInfo.getSid());
				}
				else
				{
					jqlService.excute("update ApprovalSignRead as a set a.signcomment=?,a.signtime=?,a.signtag=?,a.signtagdate=?,a.state=?,a.actionid=? where a.sameid=?"
							,sameSignInfo.getComment(),sameSignInfo.getApprovalDate(),sameSignInfo.getSigntag(),sameSignInfo.getSigntagdate(),sameSignInfo.getState(),sameSignInfo.getActionid(),sameSignInfo.getSid());
				}
				
				if (taskid!=null)
				{
					List<ApprovalSignRead> list=(List<ApprovalSignRead>)jqlService.findAllBySql("select a from ApprovalSignRead as a where a.sameid=?", sameSignInfo.getSid());
					if (list!=null && list.size()>0)
					{
						jqlService.excute("update ApprovalTaskFiles as a set a.signreadid=? where a.taskid=?", list.get(0).getId(),taskid);//将处理过程中的附件与签阅关联
					}
				}
			}
			if (approvalReader!=null)
			{
				if ("Y".equals(approvalReader.getSigntag()) && approvalReader.getState()<ApproveConstants.NEW_STATUS_HADREAD)
				{
					approvalReader.setState(ApproveConstants.NEW_STATUS_READ);
				}
				jqlService.excute("update ApprovalSignRead as a set a.signcomment=?,a.signtime=?,a.signtag=?,a.signtagdate=?,a.state=? where a.readid=?"
				,approvalReader.getComment(),approvalReader.getDate(),approvalReader.getSigntag(),approvalReader.getSigntagdate(),approvalReader.getState(),approvalReader.getId());
				if (taskid!=null)
				{
					List<ApprovalSignRead> list=(List<ApprovalSignRead>)jqlService.findAllBySql("select a from ApprovalSignRead as a where a.readid=?", approvalReader.getId());
					if (list!=null && list.size()>0)
					{
						jqlService.excute("update ApprovalTaskFiles as a set a.signreadid=? where a.taskid=?", list.get(0).getId(),taskid);//将处理过程中的附件与签阅关联
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 进行送审处理
	 * @param sendchecked
	 * @param checkread
	 * @param title
	 * @param sendfiles
	 * @param webcontent
	 * @param accepters
	 * @param samesign
	 * @param backsender
	 * @param readers
	 * @param filetype 送审时选择的文件类别
	 * @param backsigners 会签后处理人，多人之间用,间隔，如果有多个节点用；间隔
	 * @param comment
	 * @param user
	 * @return
	 */
	private ApprovalInfo createApproval(Boolean sendchecked,Boolean checkread
			,String title,List<String> sendfiles,List<String> sendfilenames,String webcontent
			,List<Long> accepters,Boolean samesign,Boolean backsender
			,String readers,String filetype,String backsigners,String comment
			,Long fileflowid,Date filesuccdate,String fromunit,String filecode,String filescript
			,Users user
			)
	{
//		Long userId, String filePath, String fileName, String acceptId, 
//		String comment, String title, ArrayList<Long> preUserIds, String stepName,int issame
		 JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		 FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
//1、存储主信息
		 int status=ApproveConstants.NEW_STATUS_START;
		 ApprovalInfo approvalInfo = new ApprovalInfo();
		 String allfilenames="";//所有文件名称，用,间隔
		 int nodetype=0;
		 ArrayList<Long> preUserIds=new ArrayList<Long>();
		 String coopers="";//文档协作者，多人之间用,间隔
		 String signers="";//签批者的人ID,多人用,间隔
		 String sendreaders=readers;//传阅人，多人用,间隔
		 int signtype=0;//签批类型
		 if (fileflowid==null || fileflowid.longValue()==0)
		 {
			 fileflowid=getFileflowid();//流水号自动加1
		 }
		 //冲突的问题后面再处理，还要分单位处理
		 approvalInfo.setFileflowid(fileflowid);
		 approvalInfo.setFilesuccdate(filesuccdate);
		 approvalInfo.setFromunit(fromunit);
		 approvalInfo.setFilecode(filecode);
		 approvalInfo.setFilescript(filescript);
		 
		 approvalInfo.setApprovalStep(1);//第一步送审
		 approvalInfo.setModifytype(1);//表示签阅
         approvalInfo.setUserID(user.getId());// 原始送审用户
         approvalInfo.setNewuserID(user.getId());//最近送审的人
         approvalInfo.setSubmiter(user.getId());//最新提交人
         approvalInfo.setSubmitdate(new Date());
         approvalInfo.setOperateid(user.getId());//当前（最新）操作者
         approvalInfo.setFiletype(filetype);
         // 审批状态
         if (sendchecked!=null && sendchecked.booleanValue())//会签，如果是单个人也当成会签
         {
        	 status=ApproveConstants.NEW_STATUS_WAIT;
        	 approvalInfo.setStatus(ApproveConstants.NEW_STATUS_WAIT);//不管有没有批阅
         }
         else
         {
        	 approvalInfo.setStatus(ApproveConstants.NEW_STATUS_START);//仅协作或批阅,不走流程
         }
         
         // 送审日期
         approvalInfo.setDate(new Date());
         approvalInfo.setWarndate(new Date());
         approvalInfo.setModifytime(new Date());
         // 送审说明
//         approvalInfo.setComment(comment);
         approvalInfo.setSendcomment(comment);
         // 送审文档路径
         if (samesign==null)
         {
        	 samesign=false;
         }
         if (samesign)
         {
        	 approvalInfo.setNodetype(1);//会签
        	 signtype=2;//task会签
        	 nodetype=1;
        	 for (int i=0;i<accepters.size();i++)
        	 {
        		 if (i==0)
        		 {
        			 signers+=accepters.get(i);
        		 }
        		 else
        		 {
        			 signers+=","+accepters.get(i);
        		 }
        	 }
        	 if (backsigners!=null && backsigners.length()>0)
        	 {
        		 String[] backers=backsigners.split(";");
        		 ArrayList<String> signerslist=new ArrayList<String>();
        		 if (backers!=null && backers.length>0)
        		 {
        			 for (int i=0;i<backers.length;i++)
        			 {
        				 signerslist.add(backers[i]);
        			 }
        			 approvalInfo.setSignerslist(signerslist);//会签后处理人
        		 }
        	 }
         }
         else if (accepters!=null && accepters.size()==1)
         {
        	 signtype=1;//签批
        	 approvalInfo.setNodetype(0);//单个签，下一步送审人无法确定
        	 nodetype=0;
        	 signers+=accepters.get(0);
         }
         else if (accepters!=null && accepters.size()>1)
         {
        	 approvalInfo.setNodetype(2);//串签
        	 nodetype=2;
        	 signtype=1;//签批
        	 for (int i=1;i<accepters.size();i++)
        	 {
        		 preUserIds.add(accepters.get(i));
        	 }
        	 signers+=accepters.get(0);
        	 approvalInfo.setPreUserIds(preUserIds);//预定义的人员
        	 approvalInfo.setUserlist(preUserIds);//将预定义的流程保留
         }
         approvalInfo.setTitle(title);
         if (webcontent!=null && webcontent.length()>0)
         {
        	 approvalInfo.setWebcontent(webcontent);
         }
         if (backsender==null)
         {
        	 backsender=false;
         }
         approvalInfo.setIsreturn(backsender);//返回送审人标记
         ArrayList<Long> sendhistory=new ArrayList<Long>();
         sendhistory.add(user.getId());
         approvalInfo.setSendhistory(sendhistory);//孙爱华增加送审历史记录
         jqlService.save(approvalInfo);//保存了签批主数据
	         
	     long starttime=System.currentTimeMillis();
	         
	//2、存储附件
	//再次送审，更新文件的isnew
	//         String sql="select max(a.isnew) from ApprovalFiles as a where a.approvalid=? ";
	//         Long isnew=(Long)jqlService.getCount(sql, approvalInfo.getId());
	//         if (isnew!=null)
	//         {
	//        	 isnew+=1l;
	//        	 jqlService.excute("update ApprovalFiles as a set a.isnew="+isnew+" where a.approvalid=? ", approvalInfo.getId());
	//         }
	         
         if (sendfiles!=null && sendfiles.size()>0)
         {
        	 for (int i=0;i<sendfiles.size();i++)
        	 {
        		 //将文件存到文件库中
        		 Fileinfo info = null;
        		 String spaceid=user.getSpaceUID();
        		 if (spaceid==null){spaceid="";}
        		 if ((sendfiles.get(i).startsWith("user_") || sendfiles.get(i).startsWith("group_")|| sendfiles.get(i).startsWith("team_")||sendfiles.get(i).startsWith("system_audit_root")
        				 || sendfiles.get(i).startsWith("company_")
        				 || sendfiles.get(i).startsWith(spaceid)
        				 ) && (sendfiles.get(i).indexOf("/") > 0))// 文档库文档
     			{
     				//文件库中的文件
        			 info = fileSystemService.addAuditFile(user.getId(), sendfiles.get(i));
     			}
     			else
     			// 本地已上传的文档放到文档库中
     			{
     				try
     				{
	     				 String tempPath = WebConfig.tempFilePath + File.separatorChar;
//	     				 tempPath=tempPath.replace(WebConfig.TEMPFILE_FOLDER,"data"+ File.separatorChar+WebConfig.TEMPFILE_FOLDER);
	     				 String filename=fileNameReplace(sendfiles.get(i));
	     				 File file = new File(tempPath + filename);
	     				 InputStream fin = new FileInputStream(file);
	     			     InputStream ois = null;
	     			     if (filename.toLowerCase().endsWith(".pdf"))
	     			     {
	     			    	ois=fin;
	     			     }
	     			     else
	     			     {
	     			    	ois=new FileInputStream(file);
	     			     }
	     			     
	     			     info = fileSystemService.addAuditFile(user.getId(), sendfilenames.get(i), fin,  ois);
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
        		 ApprovalFiles approvalFiles=new ApprovalFiles();
        		 approvalFiles.setApprovalid(approvalInfo.getId());//签批主键
        		 approvalFiles.setDocumentpath(info.getPathInfo());//存放文件库中的位置
        		 approvalFiles.setFileName(info.getFileName());//存放文件名称
        		 approvalFiles.setUserID(user.getId());
        		 approvalFiles.setAdddate(new Date());
        		 jqlService.save(approvalFiles);//保存了签批附件
        		 
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
         System.out.println(System.currentTimeMillis()-starttime);
//3存储历史记录
         ApprovalTask task=insertTask(approvalInfo.getId(),title,webcontent,user,ApproveConstants.NEW_ACTION_SEND
					,(long)status,"送签",comment,samesign,true,signers,readers,null,1,signtype,null,jqlService);
         System.out.println(System.currentTimeMillis()-starttime);
		 
//4、存储会签人
         //需要判断是否再次送审
         String sql="select max(a.isnew) from SameSignInfo as a where a.approvalID=? ";
         Long isnew=(Long)jqlService.getCount(sql, approvalInfo.getId());
         if (isnew!=null)
         {
      	     isnew+=1l;
      	     jqlService.excute("update SameSignInfo as a set a.isnew="+isnew+" where a.approvalID=? ", approvalInfo.getId());
         }
         
    	 if (accepters!=null && accepters.size()>0)//签批
         {
    		 //增加送文人已办信息
    		 //这是一种变态需求做法
    		 SameSignInfo sendsameinfo=new SameSignInfo();
    		 sendsameinfo.setUserID(user.getId());
    		 sendsameinfo.setTaskid(0l);
    		 sendsameinfo.setNodetype(nodetype);
    		 sendsameinfo.setSignnum(accepters.size());
    		 sendsameinfo.setApprovalID(approvalInfo.getId());
    		 sendsameinfo.setSigner(user);
    		 sendsameinfo.setActionid(ApproveConstants.NEW_ACTION_SEND);
    		 sendsameinfo.setState(ApproveConstants.NEW_STATUS_HAD);
    		 sendsameinfo.setCreateDate(new Date());
    		 sendsameinfo.setWarndate(new Date());
    		 sendsameinfo.setIsnew(1l);
    		 sendsameinfo.setWarnnum(0);
    		 sendsameinfo.setComment(comment);
    		 sendsameinfo.setIsreturn(backsender);
	         jqlService.save(sendsameinfo);//补充自己送审的信息
//	         createSignRead(approvalInfo,sendsameinfo,null);//送文就不要在历史记录中显示了
	        //这是一种变态需求做法------end
	         
	         List<String> mobilelist=new ArrayList<String>();
			 List<Long> tranids=new ArrayList<Long>();
    		 if (nodetype==1)
             {
    			 
            	 for (int i=0;i<accepters.size();i++)
            	 {
            		 SameSignInfo sameinfo=new SameSignInfo();
             		//全新的不需要这样处理
//            		 sql="select max(a.islast) from SameSignInfo as a where a.approvalID=? and a.signer.id=? ";
//                	 Long islast=(Long)jqlService.getCount(sql, approvalInfo.getId(),accepters[i]);
//                	 sameinfo.setIslast(islast);
            		 sameinfo.setUserID(user.getId());
            		 sameinfo.setTaskid(task.getId());
            		 sameinfo.setNodetype(nodetype);
            		 sameinfo.setSignnum(accepters.size());
    		         sameinfo.setApprovalID(approvalInfo.getId());
    		         Users signer=(Users)jqlService.getEntity(Users.class, accepters.get(i));
    		         
    	        	 sameinfo.setSigner(signer);
    		         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
    		         sameinfo.setCreateDate(new Date());
    		         sameinfo.setWarndate(new Date());
    		         sameinfo.setWarnnum(0);
//    		         sameinfo.setComment(comment);
    		         sameinfo.setIsreturn(backsender);
    		         jqlService.save(sameinfo);
    		         createSignRead(approvalInfo,sameinfo,null);
    		         
    		         String mobile=signer.getMobile();
    		         if (mobile!=null && mobile.length()==11)
    		         {
    		        	 mobilelist.add(mobile);
    		        	 tranids.add(sameinfo.getSid());
    		         }
    		         //增加送签提醒
    		         setWarnMessage("f"+sameinfo.getSid(),comment,user,0);//这里需要改进一下，多条信息一起插入，然后就可以跟短信合并到一个方法
            	 }
            	 
             }
        	 else
             {
        		 SameSignInfo sameinfo=new SameSignInfo();
        		 sameinfo.setUserID(user.getId());
        		 sameinfo.setTaskid(task.getId());
        		 sameinfo.setNodetype(nodetype);
        		 sameinfo.setSignnum(1);
		         sameinfo.setApprovalID(approvalInfo.getId());
		         Users signer=(Users)jqlService.getEntity(Users.class, accepters.get(0));
		         
	        	 sameinfo.setSigner(signer);
		         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
		         sameinfo.setCreateDate(new Date());
		         sameinfo.setWarndate(new Date());
		         sameinfo.setWarnnum(0);
//		         sameinfo.setComment(comment);
		         sameinfo.setIsreturn(backsender);
		         jqlService.save(sameinfo);
		         createSignRead(approvalInfo,sameinfo,null);
		         String mobile=signer.getMobile();
		         if (mobile!=null && mobile.length()==11)
		         {
		        	 mobilelist.add(mobile);
		        	 tranids.add(sameinfo.getSid());
		         }
		         
		         //增加送签提醒
		         setWarnMessage("f"+sameinfo.getSid(),comment,user,0);
             }
    		 String content=user.getRealName()+"请您对“"+approvalInfo.getTitle()+"”进行签批";//暂不做短信回复功能
        	 sendMobileinfo(mobilelist,content,Constant.MOBILESIGN,tranids,false,user);
         }
    	 System.out.println(System.currentTimeMillis()-starttime);
        
//5、存储批阅人
    	 //全新的应该不需要处理isnew和islast
//         sql="select max(a.isnew) from ApprovalReader as a where a.approvalID=? ";
//         isnew=(Long)jqlService.getCount(sql, approvalInfo.getId());
//         if (isnew!=null)
//         {
//        	 isnew+=1l;
//        	 jqlService.excute("update ApprovalReader as a set a.isnew="+isnew+" where a.approvalInfoId=? ", approvalInfo.getId());
//         }
         if (checkread!=null && checkread.booleanValue())
         {
        	 String[] readerids=readers.split(",");
        	 for (int i=0;i<readerids.length;i++)
        	 {
        		 boolean issave=true;
        		 for (int n=0;n<accepters.size();n++)
        		 {
        			 Long tid=accepters.get(n);
        			 if (readerids[i].equals(String.valueOf(tid)))
        			 {
        				 issave=false;
        				 break;
        			 }
        		 }
        		 if (issave)//人员已在签批人列表中不插入
        		 {
	        		 ApprovalReader approvalReader=new ApprovalReader();
	        		 Long userId=Long.valueOf(readerids[i]);
        		//全新的不需要这样处理
//        		 sql="select max(a.islast) from ApprovalReader as a where a.approvalID=? and a.userId=? ";
//            	 Long islast=(Long)jqlService.getCount(sql, approvalInfo.getId(),userId);
//            	 approvalReader.setIslast(islast);
	        		 
	        		 approvalReader.setFileName(allfilenames);//存放所有附件的地址
	        		 approvalReader.setWebcontent(webcontent);//存放网页内容
	        		 approvalReader.setTaskid(task.getId());
	        		 approvalReader.setApprovalInfoId(approvalInfo.getId());
	        		 approvalReader.setUserId(user.getId());
	        		 approvalReader.setSenddate(new Date());
	        		 approvalReader.setReadUser(userId);
	        		 jqlService.save(approvalReader);
	        		 createSignRead(approvalInfo,null,approvalReader);
	        		//增加批阅提醒
			         setWarnMessage("y"+approvalReader.getId(),comment,user,0);
        		 }
        	 }

         }
         System.out.println(System.currentTimeMillis()-starttime);
         return approvalInfo;
	}
	private ApprovalInfo createApprovalCooper(Long id,String title,List<String> sendfiles,List<String> sendfilenames,String webcontent,
			String cooperId,String comment,Long fileflowid,Date filesuccdate,String fromunit,String filecode,String filescript,Users user
			)
	{
		 JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		 FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
//1、存储主信息
		 
		 ApprovalInfo approvalInfo = new ApprovalInfo();
		 String allfilenames="";//所有文件名称，用,间隔
		 int nodetype=0;
		 ArrayList<Long> preUserIds=new ArrayList<Long>();
		 String coopers="";//文档协作者，多人之间用,间隔
//		 if (id==null || id==0l)
		 {
			 approvalInfo.setFileflowid(fileflowid);
			 approvalInfo.setFilesuccdate(filesuccdate);
			 approvalInfo.setFromunit(fromunit);
			 approvalInfo.setFilecode(filecode);
			 approvalInfo.setFilescript(filescript);
			 
			 approvalInfo.setModifytype(0);//表示协作
			 approvalInfo.setUserID(user.getId());// 原始传阅用户
	         approvalInfo.setNewuserID(user.getId());//最近传阅的人
	         approvalInfo.setOperateid(user.getId());//当前（最新）操作者
	         // 协作
        	 approvalInfo.setStatus(ApproveConstants.NEW_STATUS_START);//仅协作或批阅
	         
	         // 送审日期
	         approvalInfo.setDate(new Date());
	         approvalInfo.setWarndate(new Date());
	         approvalInfo.setModifytime(new Date());
	         // 送审说明
	         approvalInfo.setComment(comment);
	         // 送审文档路径
	         approvalInfo.setTitle(title);
	         if (webcontent!=null && webcontent.length()>0)
	         {
	        	 approvalInfo.setWebcontent(webcontent);
	         }
	         
	         jqlService.save(approvalInfo);//保存了签批主数据
	         
	         
	         
	//2、存储附件
	//再次送审，更新文件的isnew
	//         String sql="select max(a.isnew) from ApprovalFiles as a where a.approvalid=? ";
	//         Long isnew=(Long)jqlService.getCount(sql, approvalInfo.getId());
	//         if (isnew!=null)
	//         {
	//        	 isnew+=1l;
	//        	 jqlService.excute("update ApprovalFiles as a set a.isnew="+isnew+" where a.approvalid=? ", approvalInfo.getId());
	//         }
	         
	         if (sendfiles!=null && sendfiles.size()>0)
	         {
	        	 for (int i=0;i<sendfiles.size();i++)
	        	 {
	        		 //将文件存到文件库中
	        		 Fileinfo info = null;
	        		 String spaceid=user.getSpaceUID();
	        		 if (spaceid==null){spaceid="";}
	        		 if ((sendfiles.get(i).startsWith("user_") || sendfiles.get(i).startsWith("group_")
	        			|| sendfiles.get(i).startsWith("team_")
	        			|| sendfiles.get(i).startsWith("org_")
	        			|| sendfiles.get(i).startsWith("system_audit_root")
	        			|| sendfiles.get(i).startsWith("company_")
	        			|| sendfiles.get(i).startsWith(spaceid)
	        			)
	     					&& (sendfiles.get(i).indexOf("/") > 0))// 文档库文档
	     			{
	     				//文件库中的文件
	        			 info = fileSystemService.addAuditFile(user.getId(), sendfiles.get(i));
	     			}
	     			else
	     			// 本地已上传的文档放到文档库中
	     			{
	     				try
	     				{
		     				 String tempPath = WebConfig.tempFilePath + File.separatorChar;
//		     				 tempPath=tempPath.replace(WebConfig.TEMPFILE_FOLDER,"data"+ File.separatorChar+WebConfig.TEMPFILE_FOLDER);
		     				 String filename=fileNameReplace(sendfiles.get(i));
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
		     			     info = fileSystemService.addAuditFile(user.getId(), sendfilenames.get(i), fin,  ois);
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
	        		 ApprovalFiles approvalFiles=new ApprovalFiles();
	        		 approvalFiles.setApprovalid(approvalInfo.getId());//签批主键
	        		 approvalFiles.setDocumentpath(info.getPathInfo());//存放文件库中的位置
	        		 approvalFiles.setFileName(info.getFileName());//存放文件名称
	        		 approvalFiles.setUserID(user.getId());
	        		 approvalFiles.setAdddate(new Date());
	        		 jqlService.save(approvalFiles);//保存了签批附件
	        		 
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
		 }
//3存储历史记录
		 ApprovalTask task=insertTask(approvalInfo.getId(),title,webcontent,user,ApproveConstants.NEW_ACTION_COOPER
					,0l,"协作",comment,false,false,null,null,cooperId,0,0,null,jqlService);
		 
        
//4、存储协作人
    	 //全新的应该不需要处理isnew和islast
//         sql="select max(a.isnew) from ApprovalReader as a where a.approvalID=? ";
//         isnew=(Long)jqlService.getCount(sql, approvalInfo.getId());
//         if (isnew!=null)
//         {
//        	 isnew+=1l;
//        	 jqlService.excute("update ApprovalReader as a set a.isnew="+isnew+" where a.approvalInfoId=? ", approvalInfo.getId());
//         }
         if (cooperId!=null && cooperId.length()>0)
         {
        	 String[] cooperIds=cooperId.split(",");
        	 for (int i=0;i<cooperIds.length;i++)
        	 {
        		 ApprovalCooper approvalCooper=new ApprovalCooper();
        		 Long userId=Long.valueOf(cooperIds[i]);
        		//全新的不需要这样处理
//        		 sql="select max(a.islast) from ApprovalReader as a where a.approvalID=? and a.userId=? ";
//            	 Long islast=(Long)jqlService.getCount(sql, approvalInfo.getId(),userId);
//            	 approvalReader.setIslast(islast);
        		 approvalCooper.setState(ApproveConstants.NEW_STATUS_START);
        		 approvalCooper.setCooper((Users)jqlService.getEntity(Users.class, userId));//协作者
        		 approvalCooper.setFileName(allfilenames);//存放所有附件的地址
        		 approvalCooper.setWebcontent(webcontent);//存放网页内容
        		 approvalCooper.setTaskid(task.getId());
        		 approvalCooper.setApprovalID(approvalInfo.getId());
        		 approvalCooper.setUserID(user.getId());
        		 approvalCooper.setCreateDate(new Date());
        		 jqlService.save(approvalCooper);
        		//增加协作提醒
		         setWarnMessage("x"+approvalCooper.getId(),comment,user,0);
        	 }

         }
         return approvalInfo;
	}
	
    /**
     * 保存签批信息等待送审
     * @param id 已保存的签约编号
     * @param sendchecked 是否选中送签
     * @param checkread 是否选中传阅
     * @param title 标题
     * @param sendfiles 送审文件
     * @param accepters 签批者列表
     * @param samesigntag  是否会签
     * @param backsendertag 是否返回送审人
     * @param readers  传阅者，用,间隔
     * @param filetype  文件类别
     * @param backsigners  会签后的处理人
     * @param comment  备注
     * @param user  当前登录用户
     * @return
     */
	public boolean saveSign(Long id,Boolean sendsigntag,Boolean sendreadtag
			,String title,List<String> sendfiles,List<String> sendfilenames,String webcontent
			,List<Long> accepters,Boolean issame,Boolean isreturn
			,String sendreaders,String filetype,String backsigners,String comment
			,Long fileflowid,Date filesuccdate,String fromunit,String filecode,String filescript
			,Users user)
	{
		try
		{
			Integer status=0;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			//将信息插入到数据库
			ApprovalSave approvalSave;
			if (id==null || id==0)
			{
				approvalSave=new ApprovalSave();
			}
			else
			{
				approvalSave=(ApprovalSave)jqlService.getEntity(ApprovalSave.class, id);
			}
			//设置数据
			approvalSave.setFileflowid(fileflowid);
			approvalSave.setFilesuccdate(filesuccdate);
			approvalSave.setFromunit(fromunit);
			approvalSave.setFilecode(filecode);
			approvalSave.setFilescript(filescript);
			
			approvalSave.setModifytype(1);//表示签阅
			approvalSave.setSendsigntag(sendsigntag);
			approvalSave.setSendreadtag(sendreadtag);
			approvalSave.setTitle(title);
			approvalSave.setWebcontent(webcontent);
			String paths="";
			String pathnames="";
			if (sendfiles!=null)
			{
				for (int i=0;i<sendfiles.size();i++)
				{
					if (i==0)
					{
						paths+=sendfiles.get(i);
						pathnames+=sendfilenames.get(i);
					}
					else
					{
						paths+=","+sendfiles.get(i);
						pathnames+=","+sendfilenames.get(i);
					}
				}
			}
			approvalSave.setFilepaths(fileNameReplace(paths));
			approvalSave.setFilepathnames(pathnames);
			String signers="";
			if (accepters!=null)
			{
				for (int i=0;i<accepters.size();i++)
				{
					if (i==0)
					{
						signers+=accepters.get(i);
					}
					else
					{
						signers+=","+accepters.get(i);
					}
				}
			}
			approvalSave.setSigners(signers);
			approvalSave.setIssame(issame);
			approvalSave.setIsreturn(isreturn);
			approvalSave.setSendreaders(sendreaders);
			approvalSave.setFiletype(filetype);
			approvalSave.setBacksigners(backsigners);
			approvalSave.setComment(comment);
			approvalSave.setUserID(user.getId());
			approvalSave.setDate(new Date());
			
			if (id==null || id==0)
			{
				jqlService.save(approvalSave);
			}
			else
			{
				jqlService.update(approvalSave);
			}
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 根据ID获取保存的签批相关信息
	 * @param id 保存的ID编号
	 * @param user 当前登录的用户
	 * @return
	 */
	public ApprovalSave getApprovalSave(Long id,String seltype,Users user)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String sql="";
		//获取附件
			ApprovalSave approvalSave=(ApprovalSave)jqlService.getEntity(ApprovalSave.class, id);
			String coopers=approvalSave.getCoopers();
			String signers=approvalSave.getSigners();
			String sendreaders=approvalSave.getSendreaders();
			String backsigners=approvalSave.getBacksigners();
			String sendreadnames="";//传阅人名称，用,间隔
			String signernames="";//签批人名称，用,间隔
			String coopernames="";//协作者名称，用,间隔
			String backsignnames="";
			if (coopers!=null && coopers.length()>0)
			{
				//处理协作者
				coopernames=getUserName(coopers);
			}
			if (signers!=null && signers.length()>0)
			{
				//处理签批者
				signernames=getUserName(signers);
			}
			if (sendreaders!=null && sendreaders.length()>0)
			{
				//处理批阅者
				sendreadnames=getUserName(sendreaders);
			}
			if (backsigners!=null && backsigners.length()>0)
			{
				backsignnames=getUserName(backsigners);
			}
			approvalSave.setBacksignnames(backsignnames);
			approvalSave.setSignernames(signernames);
			approvalSave.setSendreadnames(sendreadnames);
			approvalSave.setCoopernames(coopernames);
			if (approvalSave.getWebcontent()!=null && approvalSave.getWebcontent().length()>0)
			{
				approvalSave.setIswebcontent(true);
			}
			
			return approvalSave;
	}
	/**
	 * 得到当前用户的所有标签
	 * @param user
	 * @return
	 */
	public List<String> getTagsName(Users user)
	{
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		return userService.getAllTags(user.getId());
		
	}
	
	public List<String> getFileTagsName(String filename, Users user)
	{
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		return userService.getFileTags(filename, user.getUserName());
		
	}
	
	/**
	 * 保存或更新写作信息,此方法可以合并到saveSign
	 * @param id 已保存的编号
	 * @param title 主题
	 * @param fileList 文件列表
	 * @param cooperId 写作人，用,间隔
	 * @param comment 备注
	 * @param user 当前登录用户
	 * @return
	 */
	public boolean saveCooper(Long id,String title,List<String> sendfiles,List<String> sendfilenames,String webcontent
			,String cooperId,String comment,Long fileflowid,Date filesuccdate,String fromunit,String filecode,String filescript,Users user)
	{
		try
		{
			Integer status=0;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			//将信息插入到数据库
			ApprovalSave approvalSave;
			if (id==null || id==0)
			{
				approvalSave=new ApprovalSave();
			}
			else
			{
				approvalSave=(ApprovalSave)jqlService.getEntity(ApprovalSave.class, id);
			}
			//设置数据
			approvalSave.setModifytype(0);//表示协作
			approvalSave.setTitle(title);
			approvalSave.setWebcontent(webcontent);
			approvalSave.setFileflowid(fileflowid);
			approvalSave.setFilesuccdate(filesuccdate);
			approvalSave.setFromunit(fromunit);
			approvalSave.setFilecode(filecode);
			approvalSave.setFilescript(filescript);
			String paths="";
			String pathnames="";
			if (sendfiles!=null)
			{
				for (int i=0;i<sendfiles.size();i++)
				{
					if (i==0)
					{
						paths+=sendfiles.get(i);
						pathnames+=sendfilenames.get(i);
					}
					else
					{
						paths+=","+sendfiles.get(i);
						pathnames+=","+sendfilenames.get(i);
					}
				}
			}
			approvalSave.setFilepaths(paths);
			approvalSave.setFilepathnames(pathnames);
			approvalSave.setCoopers(cooperId);
			approvalSave.setComment(comment);
			approvalSave.setUserID(user.getId());
			approvalSave.setDate(new Date());
			
			if (id==null || id==0)
			{
				jqlService.save(approvalSave);
			}
			else
			{
				jqlService.update(approvalSave);
			}
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 提交签批（送审）
	 * @param id 已保存的签约编号
     * @param sendchecked 是否选中送签
     * @param checkread 是否选中传阅
     * @param title 标题
     * @param sendfiles 送审文件
     * @param accepters 签批者列表
     * @param samesigntag  是否会签
     * @param backsendertag 是否返回送审人
     * @param readers  传阅者，用,间隔
     * @param filetypeCombo 送审的文档类别
     * @param backsigners  会签后的处理人
     * @param comment  备注
     * @param user  当前登录用户
	 */
	public boolean sendSign(Long id,Boolean sendchecked,Boolean checkread
			,String title,List<String> sendfiles,List<String> sendfilenames,String webcontent
			,List<Long> accepters,Boolean samesign,Boolean backsender
			,String readers,String filetypeCombo,String backsigners,String comment
			,Long fileflowid,Date filesuccdate,String fromunit,String filecode,String filescript
			,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			ApprovalInfo approvalinfo=createApproval(sendchecked,checkread
					,title,sendfiles,sendfilenames,webcontent,accepters,samesign,backsender
					,readers,filetypeCombo,backsigners,comment,fileflowid,filesuccdate,fromunit,filecode,filescript,user);//存储送审信息
			if (id!=null && id.longValue()>0)
			{
				//删除保存的签批草稿信息
				jqlService.deleteEntityByID(ApprovalSave.class, "id", id);
				//还要增加删除文件的操作
			}
			//增加消息推送
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 送协作信息
	 * @param id  原来保存的ID，送完后要将保存数据删除
	 * @param title 主题
	 * @param fileList  文件列表
	 * @param cooperId  协作者，用,间隔
	 * @param comment  备注
	 * @param user  当前登录的用户
	 * @return
	 */
	public boolean sendCooper(Long id,String title,List<String> fileList,List<String> sendfilenames,String webcontent
			,String cooperId,String comment,Long fileflowid,Date filesuccdate,String fromunit,String filecode,String filescript,Users user)
	{
		try
		{
			Integer status=0;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			//将信息插入到数据库
			ApprovalInfo approvalInfo = createApprovalCooper(id,title,fileList,sendfilenames,webcontent,
					cooperId,comment,fileflowid,filesuccdate,fromunit,filecode,filescript,user);
			
			
			if (id!=null && id.longValue()>0)
			{
				//删除保存的协作草稿信息
//				ApprovalSave approvalSave=(ApprovalSave)jqlService.getEntity(ApprovalSave.class, id);
				jqlService.deleteEntityByID(ApprovalSave.class, "id", id);
				//还要增加删除文件的操作
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取草稿列表数据
	 * @param start 分页开始页
	 * @param count 分页显示数量
	 * @param sort 排序
	 * @param order升序还是降序
	 * @param user 当前登录用户
	 * @return
	 */
	public HashMap<String, Object> getDrafts(int start,int count,String sort,String order,Users user,ArrayList<Long> userIds,String searchName,Integer isSendSign )
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			String countString = "";
			sql="select distinct a.id,a.modifytype,a.title,a.filepaths,a.filepathnames,a.webcontent,u.realName,a.date,a.comment,a.signers,a.sendreadtag from approvalsave a ,users u "
					+" where a.userID=u.id and a.userID="+user.getId();
			//筛选条件
			//发送人筛选
//			if(userIds != null && userIds.size() > 0)
			if(false)
			{
				for (int i = 0;i < userIds.size();i++) {
					if(i == 0)
					{
						sql += " and ( a.signers =" + userIds.get(0);
						countString += " and ( a.signers =" + userIds.get(0);
					}else {
						sql += " or a.signers =" + userIds.get(i);
						countString += " or a.signers =" + userIds.get(i);
					}
				}
				sql += ")";
				countString += ")";
			}
			//是否是送签
			if(isSendSign != null && isSendSign.intValue() < 2)
			{
				if(isSendSign == 0)
				{
					sql += " and a.sendreadtag = 0";
					countString += " and a.sendreadtag = 0";
				}else {
					sql += " and a.sendreadtag = 1";
					countString += " and a.sendreadtag = 1";
				}
			}
			//名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and a.title like '%"+searchName+"%'";
				countString += " and a.title like '%"+searchName+"%'";
			}
 			if (ApproveConstants.TITLE.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.title using gbk) "+order;
			}
			else if (ApproveConstants.MODIFYTYPE.equals(sort))//按照状态排序
			{
				sql+=" order by a.sendreadtag "+order;
			}
			List<Object[]> draftlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
//			QueryDb querydb=new QueryDb();
//			List<Object[]> draftlist=querydb.queryObj(sql,8);
			sqlcount="select count(distinct a) from ApprovalSave as a where a.userID=? "+countString;
			Long size=(Long)jqlService.getCount(sqlcount, user.getId());
			resultmap.put("fileListSize", size);//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<draftlist.size();i++)
			{
				Object[] objs=draftlist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				values.put("id", id);//主键编号
				Integer modifytype=(Integer)objs[1];
				if (modifytype==1)
				{
					Boolean sendreadtag=(Boolean)objs[10];
					if (sendreadtag!=null && sendreadtag.booleanValue())
					{
						values.put("modifytype", "传阅");//类型
					}
					else
					{
						values.put("modifytype", "送签");//类型
					}
				}
				else
				{
					values.put("modifytype", "传阅");//类型
				}
				values.put("title", (String)objs[2]);//标题(名称)
				values.put("state", "草稿");//状态
				values.put("sender", user.getRealName());//送文人
				String filepath=(String)objs[3];
				String filepathname=(String)objs[4];
				List<String> files=new ArrayList<String>();
				List<String> filenames=new ArrayList<String>();
				if (filepath!=null)
				{
					String[] paths=filepath.split(",");
					String[] pathnames=filepathname.split(",");
					for (int j=0;j<paths.length;j++)
					{
						files.add(paths[j]);
						filenames.add(pathnames[j]);
					}
				}
				String webcontent=(String)objs[5];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/事务详情");
					filenames.add(id+"/事务详情");
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
				if (objs[9]!=null)
				{
					values.put("accepter",getUserName((String)objs[9]));//收文人
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
	/**
	 * 获取流转过程中已办的事务，包括送文人发出去的事务
	 * @param start
	 * @param count
	 * @param sort
	 * @param user
	 * @return
	 */
	public HashMap<String, Object> getDone(int start,int count,String sort,String order,Users user,
			ArrayList<Long> userIds,String selectedTime,String searchName
			,String fileflowid,String filetype,String fromunit,String filecode,String successdate,HttpServletRequest request
			)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			String countString ="";
			sql="select distinct a.id,a.title,a.status,a.submiter,a.submitdate,u.realName,a.comment,a.modifytype,a.date,a.userID "
				+" from approvalinfo a ,samesigninfo b "
				+" ,users u "
				+" where a.id=b.approvalID and a.status!="+ApproveConstants.NEW_STATUS_DEL+" and (b.isview is null or b.isview=0 ) and b.state="+ApproveConstants.NEW_STATUS_HAD
				+" and a.userID=u.id "
				+" and (a.modifytype=1 and b.islast=0 and b.signer_id="+user.getId()+")"
				;//去除当前送文人待处理的记录
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
				sql += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
				countString += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
			}
			if (successdate!=null && successdate.length()>0)
			{
				String[] Time = successdate.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.filesuccdate >='"+sTime+"' and a.filesuccdate <='"+eTime+"'";
				countString += " and a.filesuccdate >='"+sTime+"' and a.filesuccdate <='"+eTime+"'";
			}
			if (fileflowid!=null && fileflowid.length()>0)
			{
				sql += " and a.fileflowid ="+fileflowid;
				countString += " and a.fileflowid ="+fileflowid;
			}
			if (filetype!=null && filetype.length()>0)
			{
				sql += " and a.filetype like '%"+filetype+"%'";
				countString += " and a.filetype like '%"+filetype+"%'";
			}
			if (fromunit!=null && fromunit.length()>0)
			{
				sql += " and a.fromunit like '%"+fromunit+"%'";
				countString += " and a.fromunit like '%"+fromunit+"%'";
			}
			if (filecode!=null && filecode.length()>0)
			{
				sql += " and a.filecode like '%"+filecode+"%'";
				countString += " and a.filecode like '%"+filecode+"%'";
			}
			//名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and (a.title like '%"+searchName+"%' or a.filescript like '%"+searchName+"%')";
				countString += " and (a.title like '%"+searchName+"%' or a.filescript like '%"+searchName+"%')";
			}
			if (ApproveConstants.SENDTIME.equals(sort))//按照更新时间排序
			{
				sql+=" ORDER BY a.date "+order;
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
			sqlcount="select count(distinct a.id) from ApprovalInfo a , SameSignInfo b "
					+" where a.id=b.approvalID and a.status!="+ApproveConstants.NEW_STATUS_DEL+" and (b.isview is null or b.isview=0 ) and b.state="+ApproveConstants.NEW_STATUS_HAD
					+" and (a.modifytype=1 and islast=0 and b.signer_id="+user.getId()+")"+countString;
			request.getSession().setAttribute("donesql", sql);
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
				Integer modifytype=(Integer)objs[7];
				if (modifytype!=null && modifytype.intValue()==0)
				{
					values.put("modifytype", "传阅");//类型
				}
				else
				{
					values.put("modifytype", "送签");//类型
				}
				
				values.put("totalstate", "已办");//tab类型
				String state="";
				sql="select a from SameSignInfo as a where a.approvalID=? and a.isnew=0 ";
				List<SameSignInfo> samelist = (List<SameSignInfo>)jqlService.findAllBySql(sql, id);
				if (samelist!=null && samelist.size()>0)
				{
					for (int j=0;j<samelist.size();j++)
					{
						SameSignInfo sameSignInfo=samelist.get(j);
						String statename="待签";
						if (sameSignInfo.getState()==null || sameSignInfo.getState()==ApproveConstants.NEW_STATUS_WAIT)
						{
							if ("Y".equals(sameSignInfo.getSigntag()))
							{
								statename="签收";
							}
							else
							{
								statename="未读";
							}
						}
						else if (sameSignInfo.getState().intValue()==ApproveConstants.NEW_STATUS_START)
						{
							statename="待阅";
						}
						else if (sameSignInfo.getState().intValue()==ApproveConstants.NEW_STATUS_HAD)
						{
							statename="已签";
						}
						else if (sameSignInfo.getState().intValue()==ApproveConstants.NEW_STATUS_HADREAD)
						{
							statename="已阅";
						}
						if (j>0)
						{
							state+="<br>"+sameSignInfo.getSigner().getRealName()+"("+statename+")";
						}
						else
						{
							state+=sameSignInfo.getSigner().getRealName()+"("+statename+")";
						}
					}
				}
				values.put("state", state);//状态
				
				values.put("sender", (String)objs[5]);//送文人
				long sendid=((BigInteger)objs[9]).longValue();
				if (user.getId().longValue()==sendid)
				{
					values.put("issend", "Y");//是送文人,界面上就显示 催办
				}
				else
				{
					values.put("issend", "");//不是送文人
				}
				
//				
				sql="select a from ApprovalFiles as a where a.approvalid=? and a.isnew=0 ";
				List<ApprovalFiles> fileslist = (List<ApprovalFiles>)jqlService.findAllBySql(sql, id);
				String filepath="";
				List<String> files=new ArrayList<String>();
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
				}
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, ((BigInteger)objs[0]).longValue());
				String webcontent=approvalInfo.getWebcontent();
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(approvalInfo.getId()+"/事务详情");
				}
				else
				{
					files.add("");
				}
				if (approvalInfo.getModifier()==null && approvalInfo.getUserID().longValue()==user.getId().longValue())//刚送
				{
					values.put("isundo", true);
				}
				else if (approvalInfo.getModifier()!=null)
				{
					values.put("isundo", approvalInfo.getModifier().longValue()==user.getId().longValue());
				}
				
				values.put("fileflowid", approvalInfo.getFileflowid());
				values.put("filecode", approvalInfo.getFilecode());
				values.put("files", files);//附件列表
				values.put("title", (String)objs[1]);//标题
				if (objs[4]!=null)
				{
					values.put("modifytime", sdf.format((Date)objs[4]));//处理时间或签批时间
				}
				else
				{
					values.put("modifytime", "");
				}
				if (objs[8]!=null)
				{
					values.put("sendtime", sdf.format((Date)objs[8]));//送文时间
				}
				else
				{
					values.put("sendtime", "");//送文时间
				}
				
				values.put("comment", (String)objs[6]);//备注
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
	
	public HashMap<String, Object> getCollect(int start,int count,String sort,String order,Users user,
			ArrayList<Long> userIds,String selectedTime,String searchName
			,String fileflowid,String filetype,String fromunit,String filecode,String successdate,HttpServletRequest request
			
			)
	{//获取收藏的签批
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			String countString ="";
			sql="select distinct a.id,a.title,a.status,a.submiter,a.submitdate,u.realName,a.comment,a.modifytype,a.date,a.userID "
				+" from approvalinfo a ,users u,approvalcollect c "
				+" where a.userID=u.id and a.status!="+ApproveConstants.NEW_STATUS_DEL+" and a.id=c.appinfo_id and c.collecter_id="+user.getId()
				;
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
				sql += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
				countString += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
			}
			if (successdate!=null && successdate.length()>0)
			{
				String[] Time = successdate.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.filesuccdate >='"+sTime+"' and a.filesuccdate <='"+eTime+"'";
				countString += " and a.filesuccdate >='"+sTime+"' and a.filesuccdate <='"+eTime+"'";
			}
			if (fileflowid!=null && fileflowid.length()>0)
			{
				sql += " and a.fileflowid ="+fileflowid;
				countString += " and a.fileflowid ="+fileflowid;
			}
			if (filetype!=null && filetype.length()>0)
			{
				sql += " and a.filetype like '%"+filetype+"%'";
				countString += " and a.filetype like '%"+filetype+"%'";
			}
			if (fromunit!=null && fromunit.length()>0)
			{
				sql += " and a.fromunit like '%"+fromunit+"%'";
				countString += " and a.fromunit like '%"+fromunit+"%'";
			}
			if (filecode!=null && filecode.length()>0)
			{
				sql += " and a.filecode like '%"+filecode+"%'";
				countString += " and a.filecode like '%"+filecode+"%'";
			}
			//名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and (a.title like '%"+searchName+"%' or a.filescript like '%"+searchName+"%')";
				countString += " and (a.title like '%"+searchName+"%' or a.filescript like '%"+searchName+"%')";
			}
			if (ApproveConstants.SENDTIME.equals(sort))//按照更新时间排序
			{
				sql+=" ORDER BY a.date "+order;
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
			sqlcount="select count(distinct a.id) from approvalinfo a ,users u,approvalcollect c "
				+" where a.userID=u.id and a.id=c.appinfo_id  and a.status!="+ApproveConstants.NEW_STATUS_DEL+""
				+countString;
			request.getSession().setAttribute("collectsql", sql);
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
				Integer modifytype=(Integer)objs[7];
				if (modifytype!=null && modifytype.intValue()==0)
				{
					values.put("modifytype", "传阅");//类型
				}
				else
				{
					values.put("modifytype", "送签");//类型
				}
				
				values.put("totalstate", "已办");//tab类型
				
				values.put("sender", (String)objs[5]);//送文人
				long sendid=((BigInteger)objs[9]).longValue();
				if (user.getId().longValue()==sendid)
				{
					values.put("issend", "Y");//是送文人,界面上就显示 催办
				}
				else
				{
					values.put("issend", "");//不是送文人
				}
				
//				
				sql="select a from ApprovalFiles as a where a.approvalid=? and a.isnew=0 ";
				List<ApprovalFiles> fileslist = (List<ApprovalFiles>)jqlService.findAllBySql(sql, id);
				String filepath="";
				List<String> files=new ArrayList<String>();
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
				}
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, ((BigInteger)objs[0]).longValue());
				String webcontent=approvalInfo.getWebcontent();
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(approvalInfo.getId()+"/事务详情");
				}
				else
				{
					files.add("");
				}
				values.put("fileflowid", approvalInfo.getFileflowid());
				values.put("filecode", approvalInfo.getFilecode());
				values.put("files", files);//附件列表
				values.put("title", (String)objs[1]);//标题
				if (objs[4]!=null)
				{
					values.put("modifytime", sdf.format((Date)objs[4]));//处理时间或签批时间
				}
				else
				{
					values.put("modifytime", "");
				}
				if (objs[8]!=null)
				{
					values.put("sendtime", sdf.format((Date)objs[8]));//送文时间
				}
				else
				{
					values.put("sendtime", "");//送文时间
				}
				
				values.put("comment", (String)objs[6]);//领导意见
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
	/**
	 * 获取我的送文列表
	 * @param start
	 * @param count
	 * @param sort
	 * @param order
	 * @param user
	 * @return
	 */
	public HashMap<String, Object> getMyquestfiles(int start,int count,String sort,String order,Users user,
			ArrayList<Long> userIds,String selectedTime,String searchName,Integer isSendSign
			,String fileflowid,String filetype,String fromunit,String filecode,String successdate,HttpServletRequest request
			)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			sql="select distinct a.id,a.modifytype,a.status,a.title,a.comment,a.webcontent,a.date,a.filecode,a.fileflowid from ApprovalInfo a ,users u,SameSignInfo b "
					+" where a.userID=u.id and a.status<8 and a.userID="+user.getId();//终止就不算了
			//筛选条件
			//发送人筛选
			String countString = "";
			if(userIds != null && userIds.size() > 0)
			{
				sql += " and b.approvalID=a.id and b.isnew=0";
				countString += " and b.approvalID=a.id and b.isnew=0";
				for (int i = 0;i < userIds.size();i++) {
					if(i == 0)
					{
						sql += " and ( b.signer_id =" + userIds.get(0);
						countString += " and ( b.signer.id =" + userIds.get(0);
					}else {
						sql += " or b.signer_id =" + userIds.get(i);
						countString += " or b.signer.id =" + userIds.get(i);
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
				sql += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
				countString += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
			}
			//是否是送签
			if(isSendSign != null && isSendSign.intValue() < 2)
			{
				if(isSendSign == 1)
				{
					sql += " and (a.status = 0 or a.status = 3)";
					countString += " and (a.status = 0 or a.status = 3)";
				}else {
					sql += " and (a.status <> 0) and (a.status <> 3)";
					countString += " and (a.status <> 0) and (a.status <> 3)";
				}
			}
			//名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and a.title like '%"+searchName+"%'";
				countString += " and a.title like '%"+searchName+"%'";
			}
			if (ApproveConstants.SENDTIME.equals(sort))//按照更新时间排序
			{
				sql+=" ORDER BY a.date "+order;
			}
			else if (ApproveConstants.TITLE.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.title using gbk) "+order;
			}
			else if (ApproveConstants.MODIFYTYPE.equals(sort))//按照类型排序
			{
				sql+=" order by a.status "+order;
			}
			else if (ApproveConstants.STATE.equals(sort))//按照类型排序
			{
				sql+=" order by a.status "+order;
			}

			
			sqlcount="select count(distinct a) from ApprovalInfo as a,SameSignInfo as b where a.userID=? and a.status<8"+countString;
			request.getSession().setAttribute("myrequestsql", sql);
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
				Integer status=(Integer)objs[2];
				if (status.intValue()==ApproveConstants.NEW_STATUS_START || status.intValue()==ApproveConstants.NEW_STATUS_HADREAD)
				{
					values.put("modifytype", "传阅");//类型
				}
				else
				{
					values.put("modifytype", "送签");//类型
				}
//				if (modifytype==1)
//				{
//					values.put("modifytype", "送签");//类型
//				}
//				else
//				{
//					values.put("modifytype", "传阅");//类型
//				}
				
				String statename="已送";
				
				if (status!=null)
				{
					if (status.intValue()==ApproveConstants.NEW_STATUS_START)
					{
						if (modifytype==1)
						{
							statename="待批";
						}
						else
						{
							statename="待阅";
						}
					}
				
					else if (status.intValue()==ApproveConstants.NEW_STATUS_WAIT)
					{
						statename="在办";
					}	
					else if (status.intValue()==ApproveConstants.NEW_STATUS_HAD)
					{
						statename="已办";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_HADREAD)
					{
						if (modifytype==1)
						{
							statename="已批";
						}
						else
						{
							statename="已阅";
						}
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_END)
					{
						statename="终止";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_DEL)
					{
						statename="废弃";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_SUCCESS)
					{
						statename="成文";
					}
				}
				
				values.put("state", statename);//状态
				values.put("sender", user.getRealName());//送文人
				values.put("title", (String)objs[3]);//标题(名称)
				values.put("comment", (String)objs[4]);//备注
				sql="select a from ApprovalFiles as a where a.approvalid=? and a.isnew=0 ";
				List<ApprovalFiles> fileslist = (List<ApprovalFiles>)jqlService.findAllBySql(sql, id);
				String filepath="";
				List<String> files=new ArrayList<String>();
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
				}
				String webcontent=(String)objs[5];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/事务详情");
				}
				else
				{
					files.add("");
				}
				values.put("files", files);//附件列表
				
				String accepter="";
				sql="select a from SameSignInfo as a where a.approvalID=? and a.isnew=0 ";
				List<SameSignInfo> samelist = (List<SameSignInfo>)jqlService.findAllBySql(sql, id);
				if (samelist!=null && samelist.size()>0)
				{
					for (int j=0;j<samelist.size();j++)
					{
						SameSignInfo sameSignInfo=samelist.get(j);
						String sname="待签";
						if (sameSignInfo.getState()==null || sameSignInfo.getState()==ApproveConstants.NEW_STATUS_WAIT)
						{
							if ("Y".equals(sameSignInfo.getSigntag()))
							{
								sname="签收";
							}
							else
							{
								sname="未读";
							}
						}
						else if (sameSignInfo.getState().intValue()==ApproveConstants.NEW_STATUS_START)
						{
							sname="待阅";
							
						}
						else if (sameSignInfo.getState().intValue()==ApproveConstants.NEW_STATUS_HAD)
						{
							sname="已签";
						}
						else if (sameSignInfo.getState().intValue()==ApproveConstants.NEW_STATUS_HADREAD)
						{
							sname="已阅";
						}
						if (j>0)
						{
							accepter+=";"+sameSignInfo.getSigner().getRealName()+"("+sname+")";
						}
						else
						{
							accepter+=sameSignInfo.getSigner().getRealName()+"("+sname+")";
						}
					}
				}
				
				values.put("accepter", accepter);//收文人
				if (objs[8]!=null)
				{
					values.put("fileflowid", ((BigInteger)objs[8]).longValue());
				}
				else
				{
					values.put("fileflowid",0);
				}
				values.put("filecode", (String)objs[7]);
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
	/**
	 *  获取待办数据,待协作、待签（批阅）
	 * @param start
	 * @param count
	 * @param sort
	 * @param user
	 * @return
	 */
	public HashMap<String, Object> getTodo(int start,int count,String sort,String order,Users user,
			ArrayList<Long> userIds,String selectedTime,String searchName
			,String fileflowid,String filetype,String fromunit,String filecode,String successdate,HttpServletRequest request)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			String countString = "";
			//left join (users u,usersorganizations o,organizations p) on (a."+userStr+"=u.id and u.id=o.user_id and o.organization_id=p.id)
			
			//List<Object[]> mylist=(List<Object[]>)jqlService.getObjectByNativeSQL(SQL,-1,-1);
			//ids[i]=((BigInteger)obj[2]).longValue();
			sql="select distinct a.id,a.title,a.status,a.submiter,a.submitdate,u.realName,a.comment,a.modifytype,a.date,a.userID from ApprovalInfo a left join SameSignInfo b "
				+" on (a.id=b.approvalID and b.isnew=0 and b.islast=0 "
				+")"
				+" ,users u "
				+" where a.userID=u.id and a.status="+ApproveConstants.NEW_STATUS_WAIT+" and ("
				+" (a.modifytype=1 and b.isnew=0 and b.state="+ApproveConstants.NEW_STATUS_WAIT+" and b.signer_id="+user.getId()+")"
				+")";
			
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
				sql += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
				countString += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
			}
			if (successdate!=null && successdate.length()>0)
			{
				String[] Time = successdate.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.filesuccdate >='"+sTime+"' and a.filesuccdate <='"+eTime+"'";
				countString += " and a.filesuccdate >='"+sTime+"' and a.filesuccdate <='"+eTime+"'";
			}
			if (fileflowid!=null && fileflowid.length()>0)
			{
				sql += " and a.fileflowid ="+fileflowid;
				countString += " and a.fileflowid ="+fileflowid;
			}
			if (filetype!=null && filetype.length()>0)
			{
				sql += " and a.filetype like '%"+filetype+"%'";
				countString += " and a.filetype like '%"+filetype+"%'";
			}
			if (fromunit!=null && fromunit.length()>0)
			{
				sql += " and a.fromunit like '%"+fromunit+"%'";
				countString += " and a.fromunit like '%"+fromunit+"%'";
			}
			if (filecode!=null && filecode.length()>0)
			{
				sql += " and a.filecode like '%"+filecode+"%'";
				countString += " and a.filecode like '%"+filecode+"%'";
			}
			//名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and (a.title like '%"+searchName+"%' or a.filescript like '%"+searchName+"%')";
				countString += " and (a.title like '%"+searchName+"%' or a.filescript like '%"+searchName+"%')";
			}
			if (ApproveConstants.SENDTIME.equals(sort))//按照送文时间排序
			{
				sql+=" ORDER BY a.date "+order;
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
			sqlcount="select count(distinct a.id) from ApprovalInfo a left join SameSignInfo b "
				+" on (a.id=b.approvalID and b.isnew=0 and b.islast=0 "
				+") "
				+" where a.status="+ApproveConstants.NEW_STATUS_WAIT+" and (a.modifytype=1 and b.isnew=0 and b.state="+ApproveConstants.NEW_STATUS_WAIT+" and b.signer_id="+user.getId()+")"
				+countString;
			request.getSession().setAttribute("todosql", sql);
			List<Object[]> mylist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			List<BigInteger> sizelist=(List<BigInteger>)jqlService.getObjectByNativeSQL(sqlcount,-1,-1);
			
			resultmap.put("fileListSize", sizelist.get(0).longValue());//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<mylist.size();i++)
			{
				Object[] objs=mylist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				long sendid=((BigInteger)objs[9]).longValue();
				if (user.getId().longValue()==sendid)
				{
					values.put("issend", "Y");//是送文人,界面上就显示“完成”和“废弃”
				}
				else
				{
					values.put("issend", "");//不是送文人
				}
				values.put("id", id);//主键编号
				Integer modifytype=(Integer)objs[7];
				
				if (modifytype!=null && modifytype.intValue()==0)
				{
					values.put("modifytype", "传阅");//状态
				}
				else
				{
					values.put("modifytype", "送签");//状态
				}
				values.put("state", "在办");//状态
				values.put("sender", (String)objs[5]);//送文人
//				
				sql="select a from ApprovalFiles as a where a.approvalid=? and a.isnew=0 ";
				List<ApprovalFiles> fileslist = (List<ApprovalFiles>)jqlService.findAllBySql(sql, ((BigInteger)objs[0]).longValue());
				String filepath="";
				List<String> files=new ArrayList<String>();
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
				}
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, ((BigInteger)objs[0]).longValue());
				String webcontent=approvalInfo.getWebcontent();
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(approvalInfo.getId()+"/事务详情");
				}
				else
				{
					files.add("");
				}
				values.put("fileflowid", approvalInfo.getFileflowid());
				values.put("filecode", approvalInfo.getFilecode());
//				String files=getFileLink(filepath);
				values.put("files", files);//"<a href='#'>接待计划11.doc</a><br><a href='#'>接待预算22.doc</a><br><a href='#'>阅办单33.doc</a>"
				values.put("title", (String)objs[1]);//标题
				if (objs[4]!=null)
				{
					values.put("modifytime", sdf.format((Date)objs[4]));//最后处理时间
				}
				else
				{
					values.put("modifytime", "");//最后处理时间
				}
				if (objs[8]!=null)
				{
					values.put("sendtime", sdf.format((Date)objs[8]));//送文时间
				}
				else
				{
					values.put("sendtime", "");//送文时间
				}
				values.put("comment", (String)objs[6]);//备注
				List<SameSignInfo> samelist=(List<SameSignInfo>)jqlService.findAllBySql("select a from SameSignInfo as a where a.approvalID=? and a.signer.id=? and a.isnew=0 ", id,user.getId());
				if (samelist!=null && samelist.size()>0)
				{
					values.put("signtag", samelist.get(0).getSigntag());
					values.put("warnnum", samelist.get(0).getWarnnum());//催办标记，如果大于0表示催办了
				}
				else
				{
					values.put("signtag", "");
					values.put("warnnum", 0);
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
	public HashMap<String, Object> getHadread(int start,int count,String sort,String order,Users user,
			ArrayList<Long> userIds,String selectedTime,Integer isToread,String searchName
			,String fileflowid,String filetype,String fromunit,String filecode,String successdate,HttpServletRequest request
			)
	{
		return getRead(start, count, sort, order, user, userIds, selectedTime, isToread, searchName, fileflowid, filetype, fromunit, filecode, successdate, request,true);
	}
	public HashMap<String, Object> getToread(int start,int count,String sort,String order,Users user,
			ArrayList<Long> userIds,String selectedTime,Integer isToread,String searchName
			,String fileflowid,String filetype,String fromunit,String filecode,String successdate,HttpServletRequest request
			)
	{
		return getRead(start, count, sort, order, user, userIds, selectedTime, isToread, searchName, fileflowid, filetype, fromunit, filecode, successdate, request,false);
	}
	/**
	 * 获取送/收阅列表
	 * @param start
	 * @param count
	 * @param sort
	 * @param order
	 * @param user
	 * @return
	 */
	public HashMap<String, Object> getRead(int start,int count,String sort,String order,Users user,
			ArrayList<Long> userIds,String selectedTime,Integer isToread,String searchName
			,String fileflowid,String filetype,String fromunit,String filecode,String successdate,HttpServletRequest request
			,boolean ishadread
			)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			String countString = "";
			sql="select distinct a.id,a.title,a.status,a.submiter,a.submitdate,u.realName,a.comment,a.modifytype,a.date,a.userID,a.filecode "
				+" from approvalinfo a"
				+" , ApprovalReader d "
				+" ,users u "
				+" where a.id=d.approvalInfoId and a.status!="+ApproveConstants.NEW_STATUS_DEL+" and a.userID=u.id";
			//筛选条件
			if(isToread != null && isToread.intValue() != 2)
			{
				if(isToread.intValue() == 0)
				{
					sql += " and a.userID="+user.getId();
					countString += " and a.userID="+user.getId();
				}else{
					sql += " and ((d.isview is null or d.isview=0 ) and (a.modifytype=1 and d.islast=0 and d.readUser="+user.getId()+"))";
					countString += " and ((d.isview is null or d.isview=0 ) and (a.modifytype=1 and d.islast=0 and d.readUser="+user.getId()+"))";
				}
			}else{
				sql +=" and (((d.isview is null or d.isview=0 ) and (a.modifytype=1 and d.islast=0 and d.readUser="+user.getId()+"))"
						//+" or a.userID="+user.getId()
						+")";
				countString +=" and (((d.isview is null or d.isview=0 ) and (a.modifytype=1 and d.islast=0 and d.readUser="+user.getId()+"))"
						//+" or a.userID="+user.getId()
						+")";
			}
			if (ishadread)
			{
				sql += " and d.state!="+ApproveConstants.NEW_STATUS_START;
				countString += " and d.state!="+ApproveConstants.NEW_STATUS_START;
			}
			else
			{
				sql += " and d.state="+ApproveConstants.NEW_STATUS_START;
				countString += " and d.state="+ApproveConstants.NEW_STATUS_START;
			}
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
				sql += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
				countString += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
			}
			if (successdate!=null && successdate.length()>0)
			{
				String[] Time = successdate.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.filesuccdate >='"+sTime+"' and a.filesuccdate <='"+eTime+"'";
				countString += " and a.filesuccdate >='"+sTime+"' and a.filesuccdate <='"+eTime+"'";
			}
			if (fileflowid!=null && fileflowid.length()>0)
			{
				sql += " and a.fileflowid ="+fileflowid;
				countString += " and a.fileflowid ="+fileflowid;
			}
			if (filetype!=null && filetype.length()>0)
			{
				sql += " and a.filetype like '%"+filetype+"%'";
				countString += " and a.filetype like '%"+filetype+"%'";
			}
			if (fromunit!=null && fromunit.length()>0)
			{
				sql += " and a.fromunit like '%"+fromunit+"%'";
				countString += " and a.fromunit like '%"+fromunit+"%'";
			}
			if (filecode!=null && filecode.length()>0)
			{
				sql += " and a.filecode like '%"+filecode+"%'";
				countString += " and a.filecode like '%"+filecode+"%'";
			}
			//名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and (a.title like '%"+searchName+"%' or a.filescript like '%"+searchName+"%')";
				countString += " and (a.title like '%"+searchName+"%' or a.filescript like '%"+searchName+"%')";
			}
			if (ApproveConstants.SENDTIME.equals(sort))//按照更新时间排序
			{
				sql+=" ORDER BY a.date "+order;
			}
			else if (ApproveConstants.TITLE.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.title using gbk) "+order;
			}
			else if (ApproveConstants.STATE.equals(sort))//按照状态排序
			{
				sql+=" order by d.state "+order;
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
			sqlcount="select count(distinct a.id) from ApprovalInfo a "
				+" , ApprovalReader d "
				+" where a.id=d.approvalInfoId and a.status!="+ApproveConstants.NEW_STATUS_DEL+" and (((d.isview is null or d.isview=0 )  and (a.modifytype=1 and d.islast=0 and d.readUser="+user.getId()+"))"
				+" or d.userId="+user.getId()+")"
				+countString;
			request.getSession().setAttribute("toreadsql", sql);
			List<Object[]> mylist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			List<BigInteger> sizelist=(List<BigInteger>)jqlService.getObjectByNativeSQL(sqlcount,-1,-1);
			
			resultmap.put("fileListSize", sizelist.get(0).longValue());//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<mylist.size();i++)
			{
				Object[] objs=mylist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				long uid=((BigInteger)objs[9]).longValue();//送文人ID
				if (uid==user.getId().longValue())
				{
					values.put("issend", "Y");//是送文人,界面上历史
					values.put("modifytype", "传阅");
					
					List<ApprovalReader> templist=(List<ApprovalReader>)jqlService.findAllBySql("select a from ApprovalReader as a where a.approvalInfoId=? and a.readUser="+user.getId()+" order by a.id DESC ", id);
					if (templist!=null && templist.size()>0)
					{
						Integer state=templist.get(0).getState();
						values.put("signtag", templist.get(0).getSigntag());
						if (state==0)
						{
							state=ApproveConstants.NEW_STATUS_START;
							values.put("state", "待阅");//状态
						}
						else
						{
							values.put("state", "已阅");//状态
						}
					}
					else
					{
						values.put("state", "已阅");//状态
					}
				}
				else
				{
					values.put("issend", "N");//界面上显示历史和批阅(是否显示废弃或删除再定)
					values.put("modifytype", "收阅");
					List<ApprovalReader> templist=(List<ApprovalReader>)jqlService.findAllBySql("select a from ApprovalReader as a where a.approvalInfoId=? and a.readUser="+user.getId()+" order by a.id DESC ", id);
					if (templist!=null && templist.size()>0)
					{
						values.put("state", "待阅");//状态
						Integer state=templist.get(0).getState();
						values.put("signtag", templist.get(0).getSigntag());
						if (state==0)
						{
							state=ApproveConstants.NEW_STATUS_START;
						}
						if (state.intValue()==ApproveConstants.NEW_STATUS_START)
						{
							values.put("state", "待阅");//状态
						}
						else if (state.intValue()==ApproveConstants.NEW_STATUS_WAIT)
						{
							values.put("state", "在办");//状态
						}	
						else if (state.intValue()==ApproveConstants.NEW_STATUS_HAD)
						{
							values.put("state", "已阅");//状态
						}
						else if (state.intValue()==ApproveConstants.NEW_STATUS_HADREAD)
						{
							values.put("state", "已阅");//状态
						}
						else if (state.intValue()==ApproveConstants.NEW_STATUS_END)
						{
							values.put("state", "已阅");//状态
						}
						else if (state.intValue()==ApproveConstants.NEW_STATUS_DEL)
						{
							values.put("state", "废弃");//状态
						}
						else if (state.intValue()==ApproveConstants.NEW_STATUS_SUCCESS)
						{
							values.put("state", "成文");//状态
						}
					}
				}
				values.put("id", id);//主键编号
//				Integer modifytype=(Integer)objs[7];
//				if (modifytype!=null && modifytype.intValue()==0)
//				{
//					values.put("modifytype", "协作");//类型
//				}
//				else
//				{
//					values.put("modifytype", "签阅");//类型
//				}
				
				
				values.put("sender", (String)objs[5]);//送文人
//				
				sql="select a from ApprovalFiles as a where a.approvalid=? and a.isnew=0 ";
				List<ApprovalFiles> fileslist = (List<ApprovalFiles>)jqlService.findAllBySql(sql, ((BigInteger)objs[0]).longValue());
				String filepath="";
				List<String> files=new ArrayList<String>();
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
				}
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, ((BigInteger)objs[0]).longValue());
				String webcontent=approvalInfo.getWebcontent();
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(approvalInfo.getId()+"/事务详情");
				}
				else
				{
					files.add("");
				}
				values.put("fileflowid", approvalInfo.getFileflowid());
				values.put("filecode", approvalInfo.getFilecode());//文号
				values.put("files", files);//附件列表
				values.put("title", (String)objs[1]);//标题
				if (objs[4]!=null)
				{
					values.put("modifytime", sdf.format((Date)objs[4]));//处理时间或签批时间
				}
				else
				{
					values.put("modifytime", "");
				}
				if (objs[8]!=null)
				{
					values.put("sendtime", sdf.format((Date)objs[8]));//送文时间
				}
				else
				{
					values.put("sendtime", "");//送文时间
				}
				
				values.put("comment", (String)objs[6]);//备注
				
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
	/**
	 * 获取办结数据
	 * @param start
	 * @param count
	 * @param sort
	 * @param user
	 * @return
	 */
	public HashMap<String, Object> getHadWorks(int start,int count,String sort,String order,Users user)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			//left join (users u,usersorganizations o,organizations p) on (a."+userStr+"=u.id and u.id=o.user_id and o.organization_id=p.id)
			
			//List<Object[]> mylist=(List<Object[]>)jqlService.getObjectByNativeSQL(SQL,-1,-1);
			//ids[i]=((BigInteger)obj[2]).longValue();
			sql="select distinct a.id,a.title,a.status,a.submiter,a.submitdate,u.realName,a.comment,a.modifytype,a.date from approvalinfo a left join samesigninfo b "
				+" on (a.id=b.approvalID and (b.isview is null or b.isview=0 ) and b.state="+ApproveConstants.NEW_STATUS_HAD
				+") left join ApprovalCooper c on (a.id=c.approvalID and (c.isview is null or c.isview=0 )"
				+" and c.state="+ApproveConstants.NEW_STATUS_HADREAD+" )"
				+" left join ApprovalReader d on (a.id=d.approvalInfoId and (d.isview is null or d.isview=0 ) and d.state="+ApproveConstants.NEW_STATUS_HADREAD+")"
				+" ,users u "
				+" where a.userID=u.id  and a.status!="+ApproveConstants.NEW_STATUS_DEL+" and ((a.modifytype=0 and c.cooper_id="+user.getId()+") or "
				+" (a.modifytype=1 and b.signer_id="+user.getId()+")"
				+" or (a.modifytype=1 and d.readUser="+user.getId()+")"
				+")";
			if (ApproveConstants.SENDTIME.equals(sort))//按照更新时间排序
			{
				sql+=" ORDER BY a.date "+order;
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
				sql+=" order by a.status "+order;//办结的类型是显示的状态
			}
			else if (ApproveConstants.MODIFYTIME.equals(sort))//处理时间
			{
				sql+=" order by a.submitdate "+order;
			}
			else if (ApproveConstants.COMMENT.equals(sort))//备注
			{
				sql+=" order by convert(a.comment using gbk) "+order;
			}
			sqlcount="select count(distinct a.id) from ApprovalInfo a left join SameSignInfo b "
				+" on (a.id=b.approvalID and (b.isview is null or b.isview=0 ) and b.state="+ApproveConstants.NEW_STATUS_HAD
				+") left join ApprovalCooper c on (a.id=c.approvalID and (c.isview is null or c.isview=0 )"
				+" and c.state="+ApproveConstants.NEW_STATUS_HADREAD+" )"
				+" left join ApprovalReader d on (a.id=d.approvalInfoId and (d.isview is null or d.isview=0 ) and d.state="+ApproveConstants.NEW_STATUS_HADREAD+")"
				+" where  a.status!="+ApproveConstants.NEW_STATUS_DEL+" and ((a.modifytype=0 and c.cooper_id="+user.getId()+") or "
				+" (a.modifytype=1 and b.signer_id="+user.getId()+")"
				+" or (a.modifytype=1 and d.readUser="+user.getId()+")"
				+")";
			List<Object[]> mylist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			List<BigInteger> sizelist=(List<BigInteger>)jqlService.getObjectByNativeSQL(sqlcount,-1,-1);
			
			resultmap.put("fileListSize", sizelist.get(0).longValue());//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			for (int i=0;i<mylist.size();i++)
			{
				Object[] objs=mylist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				values.put("id", ((BigInteger)objs[0]).longValue());//主键编号
				Integer modifytype=(Integer)objs[7];
				if (modifytype!=null && modifytype.intValue()==0)
				{
					values.put("modifytype", "传阅");//类型
				}
				else
				{
					values.put("modifytype", "送签");//类型
				}
				values.put("state", "已办");//状态
				values.put("sender", (String)objs[5]);//送文人
//				
				sql="select a from ApprovalFiles as a where a.approvalid=? and a.isnew=0 ";
				List<ApprovalFiles> fileslist = (List<ApprovalFiles>)jqlService.findAllBySql(sql, ((BigInteger)objs[0]).longValue());
				String filepath="";
				List<String> files=new ArrayList<String>();
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
				}
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, ((BigInteger)objs[0]).longValue());
				String webcontent=approvalInfo.getWebcontent();
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(approvalInfo.getId()+"/事务详情");
				}
				else
				{
					files.add("");
				}
				values.put("files", files);//附件列表
				values.put("title", (String)objs[1]);//标题
				if (objs[4]!=null)
				{
					values.put("modifytime", sdf.format((Date)objs[4]));//处理时间或签批时间
				}
				else
				{
					values.put("modifytime", "");
				}
				if (objs[8]!=null)
				{
					values.put("sendtime", sdf.format((Date)objs[8]));//送文时间
				}
				else
				{
					values.put("sendtime", "");//送文时间
				}
				
				values.put("comment", (String)objs[6]);//备注
				list.add(values);
			}
			resultmap.put("fileList", list);
//			resultmap.put("fileListSize", list.size());
			return resultmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 获取办结列表
	 * @param start
	 * @param count
	 * @param sort
	 * @param user
	 * @return
	 */
	public HashMap<String, Object> getEndWorks(int start,int count,String sort,String order,Users user,
			String sSelectedTime,String eSelectedTime,Integer isFinish,String searchName
			,String fileflowid,String filetype,String fromunit,String filecode,String successdate,HttpServletRequest request
			)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HashMap<String, Object> resultmap = new HashMap<String, Object>();
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			String countString = "";
			sql="select distinct a.id,a.modifytype,a.status,a.lastsignid,a.title,a.comment,a.webcontent,a.date,u.realName,a.modifytime,a.filecode,a.fileflowid from ApprovalInfo a ,users u "
					+" where a.userID=u.id and (a.status="+ApproveConstants.NEW_STATUS_END
//					+" or a.status="+ApproveConstants.NEW_STATUS_DEL
					+" or a.status="+ApproveConstants.NEW_STATUS_SUCCESS
					+") and a.userID="+user.getId();//获取成文//暂时先考虑所有都能看到a.userID=? and
			//送文时间段筛选
			if(sSelectedTime != null)
			{
				String[] Time = sSelectedTime.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
				countString += " and a.date >='"+sTime+"' and a.date <='"+eTime+"'";
			}//办结时间段筛选
			if(eSelectedTime != null)
			{
				String[] Time = eSelectedTime.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.modifytime >='"+sTime+"' and a.modifytime <='"+eTime+"'";
				countString += " and a.modifytime >='"+sTime+"' and a.modifytime <='"+eTime+"'";
			}
			if (successdate!=null && successdate.length()>0)
			{
				String[] Time = successdate.split("/"); 
				Date startTime = new Date(Long.valueOf(Time[0]));
				Date endTime = new Date(Long.valueOf(Time[1]));
				GregorianCalendar endD = new GregorianCalendar();
				endD.setTime(endTime);				
				endD.add(Calendar.DAY_OF_MONTH,1);
				String sTime = String.format("%1$tF %1$tT", startTime);
				String eTime = String.format("%1$tF %1$tT", endD);
				sql += " and a.filesuccdate >='"+sTime+"' and a.filesuccdate <='"+eTime+"'";
				countString += " and a.filesuccdate >='"+sTime+"' and a.filesuccdate <='"+eTime+"'";
			}
			if (fileflowid!=null && fileflowid.length()>0)
			{
				sql += " and a.fileflowid ="+fileflowid;
				countString += " and a.fileflowid ="+fileflowid;
			}
			if (filetype!=null && filetype.length()>0)
			{
				sql += " and a.filetype like '%"+filetype+"%'";
				countString += " and a.filetype like '%"+filetype+"%'";
			}
			if (fromunit!=null && fromunit.length()>0)
			{
				sql += " and a.fromunit like '%"+fromunit+"%'";
				countString += " and a.fromunit like '%"+fromunit+"%'";
			}
			if (filecode!=null && filecode.length()>0)
			{
				sql += " and a.filecode like '%"+filecode+"%'";
				countString += " and a.filecode like '%"+filecode+"%'";
			}
			//名称筛选
			if(searchName != null && searchName.length() > 0)
			{
				sql += " and (a.title like '%"+searchName+"%' or a.filescript like '%"+searchName+"%')";
				countString += " and (a.title like '%"+searchName+"%' or a.filescript like '%"+searchName+"%')";
			}
			//是否完结
			if(isFinish != null && isFinish.intValue() != 2)
			{
				if(isFinish == 0)
				{
					sql += " and a.status =" + ApproveConstants.NEW_STATUS_SUCCESS;
					countString += " and a.status =" + ApproveConstants.NEW_STATUS_SUCCESS;
				}else {
					sql += " and a.status =" + ApproveConstants.NEW_STATUS_DEL;
					countString += " and a.status =" + ApproveConstants.NEW_STATUS_DEL;
				}
			}
			if (ApproveConstants.SENDTIME.equals(sort))//按照更新时间排序
			{
				sql+=" ORDER BY a.date "+order;
			}
			else if (ApproveConstants.MODIFYTIME.equals(sort))//按照办结时间排序
			{
				sql+=" ORDER BY a.modifytime "+order;
			}
			else if (ApproveConstants.TITLE.equals(sort))//按照主题排序
			{
				sql+=" order by convert(a.title using gbk) "+order;
			}
			else if (ApproveConstants.MODIFYTYPE.equals(sort))//按照类型排序
			{
				sql+=" order by a.status "+order;
			}
			
			sqlcount="select count(distinct a) from ApprovalInfo as a where (a.status="+ApproveConstants.NEW_STATUS_END
//					+" or a.status="+ApproveConstants.NEW_STATUS_DEL
					+" or a.status="+ApproveConstants.NEW_STATUS_SUCCESS
					+") and a.userID="+user.getId()+countString;
			request.getSession().setAttribute("endsql", sql);
			List<Object[]> successlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
			Long size=(Long)jqlService.getCount(sqlcount);//, user.getId()
			resultmap.put("fileListSize", size);//文件（记录）总数量
			List list=new ArrayList();//存放每行的数据
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			for (int i=0;i<successlist.size();i++)
			{
				Object[] objs=successlist.get(i);
				HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
				long id=((BigInteger)objs[0]).longValue();
				values.put("id", id);//主键编号
				Integer modifytype=(Integer)objs[1];
				values.put("id", id);//主键编号
				
				if (objs[3]!=null)
				{
					long lastsignid=((BigInteger)objs[3]).longValue();
					values.put("accepter", userService.getUser(lastsignid).getRealName());//最后签批签批人
				}
				
				
				String statename="已办";
				Integer status=(Integer)objs[2];
				if (status!=null)
				{
					if (status.intValue()==ApproveConstants.NEW_STATUS_START)
					{
						statename="待阅";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_WAIT)
					{
						statename="在办";
					}	
					else if (status.intValue()==ApproveConstants.NEW_STATUS_HAD)
					{
						statename="已办";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_HADREAD)
					{
						statename="已办";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_END)
					{
						statename="终止";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_DEL)
					{
						statename="废弃";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_SUCCESS)
					{
						statename="成文";
					}
				}
				values.put("modifytype", statename);//类型
				values.put("state", statename);//状态
				values.put("sender", (String)objs[8]);//送文人
				
				sql="select a from ApprovalFiles as a where a.approvalid=? and a.isnew=0 ";
				List<ApprovalFiles> fileslist = (List<ApprovalFiles>)jqlService.findAllBySql(sql, id);
				String filepath="";
				List<String> files=new ArrayList<String>();
				for (int j=0;j<fileslist.size();j++)
				{
					files.add(fileslist.get(j).getDocumentpath());
				}
				String webcontent=(String)objs[6];
				if (webcontent!=null && webcontent.length()>0)
				{
					files.add(id+"/事务详情");
				}
				else
				{
					files.add("");
				}
				values.put("files", files);//附件列表
				values.put("title", (String)objs[4]);//标题
				values.put("sendtime", sdf.format((Date)objs[7]));//送文时间
				values.put("modifytime", sdf.format((Date)objs[9]));//办结时间
				values.put("filecode", (String)objs[10]);
				if (objs[11]!=null)
				{
					values.put("fileflowid",  ((BigInteger)objs[11]).longValue());
				}
				values.put("comment", (String)objs[5]);
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
	public void exportEnd(HttpServletRequest request,HttpServletResponse resp)
	{//导出办结的数据到文件
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql=(String)request.getSession().getAttribute("endsql");
			List<Object[]> successlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,-1,-1);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			StringBuffer buffer=new StringBuffer();
			buffer.append("名称,");
			buffer.append("文件,");
			buffer.append("送文人,");
			buffer.append("送文时间,");
			buffer.append("办结时间,");
			buffer.append("文号,");
			buffer.append("领导意见,");
			buffer.append("类型");
			buffer.append("\r\n");
			for (int i=0;i<successlist.size();i++)
			{
				Object[] objs=successlist.get(i);
				long id=((BigInteger)objs[0]).longValue();
				Integer modifytype=(Integer)objs[1];
				
				if (objs[3]!=null)
				{
					long lastsignid=((BigInteger)objs[3]).longValue();
					//userService.getUser(lastsignid).getRealName()//最后签批签批人
				}
				buffer.append((String)objs[4]+",");//标题
				sql="select a from ApprovalFiles as a where a.approvalid=? and a.isnew=0 ";
				List<ApprovalFiles> fileslist = (List<ApprovalFiles>)jqlService.findAllBySql(sql, id);
				String filepath="";
				String files="";
				for (int j=0;j<fileslist.size();j++)
				{
					files+=(fileslist.get(j).getDocumentpath())+"  ";
				}
				String webcontent=(String)objs[6];
				if (webcontent!=null && webcontent.length()>0)
				{
					files+=(id+"/事务详情");
				}
				else
				{
					files+=("");
				}
				buffer.append(files+",");//附件列表
				
				String statename="已办";
				Integer status=(Integer)objs[2];
				if (status!=null)
				{
					if (status.intValue()==ApproveConstants.NEW_STATUS_START)
					{
						statename="待阅";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_WAIT)
					{
						statename="在办";
					}	
					else if (status.intValue()==ApproveConstants.NEW_STATUS_HAD)
					{
						statename="已办";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_HADREAD)
					{
						statename="已办";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_END)
					{
						statename="终止";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_DEL)
					{
						statename="废弃";
					}
					else if (status.intValue()==ApproveConstants.NEW_STATUS_SUCCESS)
					{
						statename="成文";
					}
				}
				buffer.append((String)objs[8]+",");//送文人
				buffer.append(sdf.format((Date)objs[7])+",");//送文时间
				buffer.append(sdf.format((Date)objs[9])+",");//办结时间
				buffer.append((String)objs[10]+",");//文号
				buffer.append((String)objs[5]+",");//领导意见
				buffer.append(statename);//类型
				buffer.append("\r\n");
			}
			byte[] b=buffer.toString().getBytes();
			resp.setCharacterEncoding("utf-8");
			resp.setContentType("application/octet-stream");
			resp.setHeader("Content-Disposition", "attachment;filename=\"success.csv\"");
			resp.setHeader("errorCode", "0");
			resp.setHeader("Content-Length", String.valueOf(b.length));
			
			OutputStream oos = resp.getOutputStream();
			oos.write(b);
			oos.flush();
			oos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public List getSignDetail(Long id,Users user)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			String sqlcount="";
			ApprovalInfo approvalInfo = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);
			sql="select a from SameSignInfo as a where a.approvalID=? and a.isnew=0 ";
			List<SameSignInfo> samelist = (List<SameSignInfo>)jqlService.findAllBySql(sql, id);
			sql="select a from ApprovalCooper as a where a.approvalID=? and a.isnew=0 ";
			List<ApprovalCooper> cooperlist = (List<ApprovalCooper>)jqlService.findAllBySql(sql, id);
			sql="select a from ApprovalReader as a where a.approvalInfoId=? and a.isnew=0 ";
			List<ApprovalReader> readlist = (List<ApprovalReader>)jqlService.findAllBySql(sql, id);
			List list=new ArrayList();//存放每行的数据
			if (samelist!=null && samelist.size()>0)
			{
				String statename="已办";
				for (int i=0;i<samelist.size();i++)
				{
					SameSignInfo sameSignInfo=samelist.get(i);
					HashMap<String, Object> resultmap = new HashMap<String, Object>();
					resultmap.put("type", "sign");//类型
					resultmap.put("id", sameSignInfo.getSid());//主键
					resultmap.put("modifier", sameSignInfo.getSigner().getRealName());//办理人
					if (sameSignInfo.getState()==ApproveConstants.NEW_STATUS_WAIT)
					{
						statename="在办";
					}
					resultmap.put("state", statename);//状态
					resultmap.put("filepaths", "");//附件,以，间隔
					resultmap.put("filenames", "");//附件,以，间隔
					resultmap.put("comment", sameSignInfo.getComment());//备注
					resultmap.put("cuitag", true);//是否催办
				}
			}
			if (cooperlist!=null && cooperlist.size()>0)
			{
				String statename="已办";
				for (int i=0;i<cooperlist.size();i++)
				{
					ApprovalCooper approvalCooper=cooperlist.get(i);
					HashMap<String, Object> resultmap = new HashMap<String, Object>();
					resultmap.put("type", "coop");//类型
					resultmap.put("id", approvalCooper.getId());//主键
					resultmap.put("modifier", approvalCooper.getCooper().getRealName());//办理人
					if (approvalCooper.getState()==ApproveConstants.NEW_STATUS_START)
					{
						statename="在办";
					}
					resultmap.put("state", statename);
					sql="select a from ApprovalCooperFiles as a where a.coopid=?";
					List<ApprovalCooperFiles> filelist=(List<ApprovalCooperFiles>)jqlService.findAllBySql(sql, approvalCooper.getId());
					String filepaths="";
					String filenames="";
					if (filelist!=null && filelist.size()>0)
					{
						for (int j=0;j<filelist.size();j++)
						{
							ApprovalCooperFiles approvalCooperFiles=filelist.get(j);
							if (i==0)
							{
								filepaths+=approvalCooperFiles.getDocumentpath();
								filenames+=approvalCooperFiles.getFileName();
							}
							else
							{
								filepaths+=","+approvalCooperFiles.getDocumentpath();
								filenames+=","+approvalCooperFiles.getFileName();
							}
						}
					}
					resultmap.put("filepaths", filepaths);//附件,以，间隔
					resultmap.put("filenames", filenames);//附件,以，间隔
					resultmap.put("comment", approvalCooper.getComment());
					resultmap.put("cuitag", true);
				}
			}
			if (readlist!=null && readlist.size()>0)
			{
				String statename="已阅";
				UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
				for (int i=0;i<readlist.size();i++)
				{
					ApprovalReader approvalReader=readlist.get(i);
					HashMap<String, Object> resultmap = new HashMap<String, Object>();
					resultmap.put("type", "read");//类型
					resultmap.put("id", approvalReader.getId());//主键
					resultmap.put("modifier", userService.getUser(approvalReader.getReadUser()).getRealName());//办理人
					if (approvalReader.getState()==ApproveConstants.NEW_STATUS_START)
					{
						statename="未阅";
					}
					resultmap.put("state", statename);
					resultmap.put("filepaths", "");//附件,以，间隔
					resultmap.put("filenames", "");//附件,以，间隔
					resultmap.put("comment", approvalReader.getComment());
					resultmap.put("cuitag", false);
				}
			}
			return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	public Map<String,Object> getCurrentPermit(Long id,Users user)
	{
		HashMap<String, Object> resultmap = new HashMap<String, Object>();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);//获取主流程信息
			String wintitle="文档协作";
			int type=1;//判断弹出什么对话框
			int actiontype=0;
			String sql="select count(a) from SameSignInfo as a where a.isnew=0 and a.islast=0 and a.state="+ApproveConstants.NEW_STATUS_WAIT
			+" and a.signer.id=? and a.approvalID=? ";
			Long count=(Long)jqlService.getCount(sql, user.getId(),id);
			sql="select a from SameSignInfo as a where a.isnew=0 and a.islast=0 and a.state="+ApproveConstants.NEW_STATUS_WAIT
					+" and a.signer.id=? and a.approvalID=? order by a.sid DESC ";
			Long taskid=0l;
			List<SameSignInfo> samelist=(List<SameSignInfo>)jqlService.findAllBySql(sql, user.getId(),id);
			if (samelist!=null && samelist.size()>0)
			{
				taskid=samelist.get(0).getTaskid();
			}
			sql="select count(a) from ApprovalReader as a where a.isnew=0 and a.islast=0 and a.state="+ApproveConstants.NEW_STATUS_START
			+" and a.readUser=? and a.approvalInfoId=? ";
			Long readcount=(Long)jqlService.getCount(sql, user.getId(),id);
			
			if (approvalInfo.getModifytype().intValue()==0)//文档协作
			{
				
			}
			else if (count>0l)
			{
				wintitle="签批";
				actiontype=1;
				if (user.getLastsignlevel()!=null && user.getLastsignlevel()>0)//最高领导，不要再送文了，直接返回给送文人
				{
					
				}
				else if (approvalInfo.getNodetype()!=null && (approvalInfo.getNodetype().intValue()==0 || approvalInfo.getNodetype().intValue()==2)
						&& (approvalInfo.getIsreturn()==null || !approvalInfo.getIsreturn().booleanValue())
					)//正常一个人签,但又没有选中返回送文人
				{
					type=2;
				}
			}
			else if (readcount>0l)
			{
				wintitle="批阅";
				type=1;
			}
			else
			{
				wintitle="批阅";
				type=0;
			}
			sql="select a from ApprovalFiles as a where a.isnew=0 and a.approvalid=? ";//获取附件
			List<ApprovalFiles> filelist = (List<ApprovalFiles>)jqlService.findAllBySql(sql, id);
			List<String[]> attachfiles=new ArrayList<String[]>();
			if (filelist!=null && filelist.size()>0)
			{
				for (int i=0;i<filelist.size();i++)
				{
					String[] temp=new String[]{filelist.get(i).getDocumentpath(),filelist.get(i).getFileName()};
					attachfiles.add(temp);
				}
			}
			Users senduser=(Users)jqlService.getEntity(Users.class, approvalInfo.getNewuserID());
			resultmap.put("title", approvalInfo.getTitle());//标题
			resultmap.put("wintitle", wintitle);//窗口名称
			resultmap.put("type", type);//type=1,代表批阅窗口，type=2，代表签批窗口，处理方法不一样，窗口内容一样
			resultmap.put("actiontype", actiontype);//actiontype=1为会签，0为批阅
			
			resultmap.put("attachfiles", attachfiles);//[['user_111/document/接待计划.xls','接待计划.xls'],['user_111/document/接待预算.xls','接待预算.xls'],['user_111/document/阅办单.xls','阅办单.xls']]
			resultmap.put("senderid", senduser.getId());//送文人ID
			resultmap.put("sendername", senduser.getRealName());//送文人姓名
			resultmap.put("oldcomment", approvalInfo.getComment());//备注信息
			resultmap.put("comment", approvalInfo.getComment());//备注信息
			resultmap.put("webcontent", approvalInfo.getWebcontent());//备注信息
			resultmap.put("status", approvalInfo.getStatus());//当前状态，只有已办才能再送审
			ArrayList<Long> userlist=approvalInfo.getUserlist();
			if (userlist!=null && userlist.size()>0)
			{
				resultmap.put("userlist", userlist);
				UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
				ArrayList<String> userlistname=new ArrayList<String>();
				for (int i=0;i<userlist.size();i++)
				{
					userlistname.add(userService.getUser(userlist.get(i)).getRealName());
				}
				resultmap.put("userlistname", userlistname);
			}
			
			int notsign=1;
			
			if (approvalInfo.getModifytype()==1)
			{
				if (approvalInfo.getNodetype()!=null && approvalInfo.getNodetype()==1)//会签
				{
					resultmap.put("isSign", false);//会签
					sql="select count(a) from SameSignInfo as a where a.taskid=? ";
					Long samecount=(Long)jqlService.getCount(sql, taskid);
					if (samecount!=null && samecount.longValue()>1l)
					{
						System.out.println(samecount.longValue());
					}
					else
					{
						notsign=0;
					}
				}
				else
				{
					if (count!=null && count>0l)
					{
//						if (user.getLastsignlevel()!=null && user.getLastsignlevel()>0)//最高领导，不要再送文了，直接返回给送文人
//						{
//							resultmap.put("isSign", false);
//						}
//						else
						{
							resultmap.put("isSign", true);//是否签批,可选择下一个人
						}
						
					}
					else
					{
						resultmap.put("isSign", false);//是否签批
					}
				}
			}
			else
			{
				resultmap.put("isSign", false);//是否签批
			}
			resultmap.put("notsign", notsign);
//			getSignDetail(id,user);//测试
			return resultmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取打开文件的权限,0为只读，1为可写,2为无权限
	 * @param path
	 * @param user
	 * @return
	 */
	public int getPermit(String path,Users user)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			List<ApprovalFiles> fileslist=(List<ApprovalFiles>)jqlService.findAllBySql("select a from ApprovalFiles as a where a.documentpath=?", path);
			if (fileslist!=null && fileslist.size()>0)
			{
				long id=fileslist.get(0).getApprovalid();
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);//获取主流程信息
				if (approvalInfo.getStatus()<=ApproveConstants.NEW_STATUS_WAIT)//不是待签状态都不可以编辑
				{
					List<SameSignInfo> samelist=(List<SameSignInfo>)jqlService.findAllBySql("select a from SameSignInfo as a where a.approvalID=? and a.signer.id=? and a.state="+ApproveConstants.NEW_STATUS_WAIT,id,user.getId());
					if (samelist!=null && samelist.size()>0)
					{
						return 1;//可写
					}
				}
			
			}
			else
			{
				//检查是否草稿中的文档
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 处理协作、批阅、会签、顺签 都是同一个对话框，只能输备注
	 * @param id  签批主键
	 * @param type  提交类型 submit/end  提交或终止
	 * @param comment  备注信息
	 * @param user  当前登录用户
	 * @param readerId 传阅者，会签时，有些领导还要进行传阅
	 * @return
	 */
	public boolean modifySignRead(Long id,String type,String comment,Users user,String readerId)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);//获取主流程信息
			Integer status=null;//默认是已办
			boolean ischangedate=true;
			if (approvalInfo.getModifytype().intValue()==0)//文档协作
			{
				//更新ApprovalCooper表
				String usql="update ApprovalCooper as a set a.state="+ApproveConstants.NEW_STATUS_HADREAD
				+",a.actionid="+ApproveConstants.NEW_ACTION_HADCOOPER+",a.approvalDate=? "
				+" where a.isnew=0 and a.islast=0 and a.approvalID=? and a.cooper.id=?";
				jqlService.excute(usql, new Date(),id,user.getId());
				
				usql="select count(a) from ApprovalCooper as a where a.isnew=0 and a.islast=0 and a.approvalID=? "
					+" and a.state="+ApproveConstants.NEW_STATUS_START;//判断有没有全部协作完毕
				Long count=(Long)jqlService.getCount(usql, id);
				if (count==0l)
				{
					//全部协作完毕，需要更新状态
					status=ApproveConstants.NEW_STATUS_HADREAD;//已阅,不走流程
				}
				//插入ApprovalTask表
				ApprovalTask task=insertTask(id,approvalInfo.getTitle(),approvalInfo.getWebcontent(),user
						,ApproveConstants.NEW_ACTION_HADCOOPER
						,(long)ApproveConstants.NEW_STATUS_HADREAD,"批阅",comment,false,false,null,null,null,0,0,null,jqlService);
				ischangedate=false;
				
				//以下是增加处理完毕后消除所有的消息提示和消息催办
				usql="select a from ApprovalCooper as a where a.state="+ApproveConstants.NEW_STATUS_HADREAD
						+" and a.actionid="+ApproveConstants.NEW_ACTION_HADCOOPER
						+" and a.isnew=0 and a.islast=0 and a.approvalID=? and a.cooper.id=? order by a.id DESC ";
				List<ApprovalCooper> list = (List<ApprovalCooper>)jqlService.findAllBySql(usql, id,user.getId());
				if(list!=null && list.size()>0)
				{
					jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.coopid=?",new Date(),list.get(0).getId());
				}
			}
			else //处理会签和顺签以及返回送审人
			{
				//查出当前是否在批阅表中
				String sql="select count(a) from ApprovalReader as a where a.approvalInfoId=? and a.isnew=0 and a.islast=0 and a.readUser=? ";
				Long readcount=(Long)jqlService.getCount(sql, id,user.getId());//如果大于0说明该人有可能是批阅者，只需更新批阅备注就可能了
				List<String> mobilelist=new ArrayList<String>();
				List<Long> tranids=new ArrayList<Long>();
				String content="";
				sql="select count(a) from SameSignInfo as a where a.approvalID=? and a.isnew=0 and a.islast=0 and a.signer.id=? ";
				Long signcount=(Long)jqlService.getCount(sql, id,user.getId());//如果大于0说明该人有可能是批阅者，只需更新批阅备注就可能了
				if ((signcount!=null && signcount.longValue()>0)&&((approvalInfo.getNodetype()!=null && approvalInfo.getNodetype().intValue()==1)
						|| (user.getLastsignlevel()!=null && user.getLastsignlevel().intValue()>0)))//会签或最终领导
				{
					//会签全部结束一定反馈给最后送审人newuserID     最后签批人lastsignid
					String usql="update SameSignInfo as a set a.state="+ApproveConstants.NEW_STATUS_HAD
					+",a.actionid="+ApproveConstants.NEW_ACTION_SIGN+",a.approvalDate=? "
					+",a.comment=? "
					+" where a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=?";
					jqlService.excute(usql, new Date(),comment,id,user.getId());
					
					//以下是增加处理完毕后消除所有的消息提示和消息催办
					usql="select a from SameSignInfo as a where a.state="+ApproveConstants.NEW_STATUS_HAD
							+" and a.actionid="+ApproveConstants.NEW_ACTION_SIGN
							+" and a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=? order by a.id DESC ";
					List<SameSignInfo> samelist = (List<SameSignInfo>)jqlService.findAllBySql(usql, id,user.getId());
					if(samelist!=null && samelist.size()>0)
					{
						jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.sameid=?",new Date(),samelist.get(0).getSid());
						updateSignRead(approvalInfo,samelist.get(0),null,null);
					}
					
					
//					usql="select a from SameSignInfo as a where a.state="+ApproveConstants.NEW_STATUS_HAD
//							+" and a.actionid="+ApproveConstants.NEW_ACTION_SIGN
//							+" and a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=? order by a.id DESC ";
//					List<SameSignInfo> samelist=jqlService.findAllBySql(usql ,id,user.getId());//这里好像重复查询了
					Date signtagdate=null;
					if (samelist!=null && samelist.size()>0)
					{
						signtagdate=samelist.get(0).getSigntagdate();
					}
					usql="select count(a) from SameSignInfo as a where a.isnew=0 and a.islast=0 and a.approvalID=? "
						+" and a.state="+ApproveConstants.NEW_STATUS_WAIT;//判断有没有全部签批完毕
					Long count=(Long)jqlService.getCount(usql, id);
					if (count==0l || (user.getLastsignlevel()!=null && user.getLastsignlevel().intValue()>0))
					{
						//全部签批完毕，需要更新状态
						status=ApproveConstants.NEW_STATUS_HAD;//已办
					}
//					usql="select count(a) from SameSignInfo as a where a.isnew=0 and a.islast=0 and a.approvalID=? "
//						+" and a.state="+ApproveConstants.NEW_STATUS_HAD;//判断有没有全部签批完毕
//					count=(Long)jqlService.getCount(usql, id);
					int step=approvalInfo.getApprovalStep();
					if (count>0l)//还没有结束，step不变化，是同一节点
					{
						
					}
					else
					{
						step++;
						approvalInfo.setApprovalStep(step);
					}
					String signers="";
					if (status!=null)
					{
						signers+=""+approvalInfo.getUserID().intValue();
					}
					ApprovalTask task=insertTask(id,approvalInfo.getTitle(),approvalInfo.getWebcontent(),user
							,ApproveConstants.NEW_ACTION_SIGN
							,(long)ApproveConstants.NEW_STATUS_HAD,"会签",comment,false,false,signers,null,null,step,0,signtagdate,jqlService);
					if (count==0l)
					{
						jqlService.excute("update ApprovalTask as a set a.approvalStep="+step
								+" where a.approvalID=? and a.stateid="+ApproveConstants.NEW_STATUS_HAD
								+" and a.approvalStep=0 and a.isnodetag=false ",id);//将会见的处于流程节点的位置标出(主要是由于要标出具体会签的状态)
					}
					if(samelist!=null && samelist.size()>0)
					{
						updateSignRead(approvalInfo,samelist.get(0),null,task.getId());
					}
					//以下也是无理需求的实现方法,会签后相当于自动返回送审人
					if (status!=null)
					{
						 SameSignInfo sameinfo=new SameSignInfo();
						 sameinfo.setUserID(user.getId());
			      		 sameinfo.setTaskid(task.getId());
			      		 sameinfo.setNodetype(0);
			      		 sameinfo.setSignnum(1);
				         sameinfo.setApprovalID(approvalInfo.getId());
				         
				         approvalInfo.setNodetype(0);//会签后要将节点属性改过来
						//获取会签后的处理人
						ArrayList<String> signerslist=approvalInfo.getSignerslist();
						Users signer=null;
						Users oldsender=(Users)jqlService.getEntity(Users.class, approvalInfo.getUserID());
						if (user.getLastsignlevel()!=null && user.getLastsignlevel().intValue()>0)//最高领导人直接返还给送文人，只要用户的最高领导人权限不设置，不影响原来的审批
						{
							signer=oldsender;
					        sameinfo.setSigner(signer);
					        sameinfo.setActionid(-1l);//显示的类别，在历史记录中显示返回
						}
						else if (signerslist!=null && signerslist.size()>0)
						{
							String backs=signerslist.get(0);
							signerslist.remove(0);
							approvalInfo.setSignerslist(signerslist);
							String[] backmans=backs.split(",");
							signer=(Users)jqlService.getEntity(Users.class, Long.valueOf(backmans[0]));
							sameinfo.setSigner(signer);
							if (backmans.length>1)//再次多人顺签
							{
								ArrayList<Long> preUserIds = new ArrayList<Long>();
								for (int i=1;i<backmans.length;i++)
								{
									preUserIds.add(Long.valueOf(backmans[i]));
								}
								approvalInfo.setPreUserIds(preUserIds);//预定义的人员
					        	approvalInfo.setUserlist(preUserIds);//将预定义的流程保留
							}
						}
						else
						{
							signer=oldsender;
					        sameinfo.setSigner(signer);
					        sameinfo.setActionid(-1l);//显示的类别，在历史记录中显示返回
						}
				         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
				         sameinfo.setCreateDate(new Date());
				         sameinfo.setWarndate(new Date());
				         sameinfo.setWarnnum(0);
//				         sameinfo.setComment(comment);
				         sameinfo.setIsreturn(false);
				       //目前规格只算最后一次的状态
				         jqlService.excute("update SameSignInfo as a set a.islast=a.islast+1 where a.approvalID=? and a.signer.id=? ", approvalInfo.getId(),signer.getId());
				         jqlService.save(sameinfo);
				         createSignRead(approvalInfo,sameinfo,null);
				         if (signer.getMobile()!=null && signer.getMobile().length()==11)
				         {
				        	 mobilelist.add(signer.getMobile());
							 tranids.add(sameinfo.getSid());
							 if (signerslist!=null && signerslist.size()>0)
							 {
								 content=oldsender.getRealName()+"发起的签批“"+approvalInfo.getTitle()+"”已到您签批";//暂不做短信回复功能
							 }
							 else
							 {
								 content="您送签的“"+approvalInfo.getTitle()+"”已签批完毕";//暂不做短信回复功能
							 }
				         }
					}
					if (readerId!=null && readerId.length()>0)
					{
						//会签时传阅
						String[] readers=readerId.split(",");
						if (readers!=null && readers.length>0)
						{
					         String allfilenames="";
					         String msql="select a from ApprovalFiles as a where a.approvalid=? ";
							 List<ApprovalFiles> list=(List<ApprovalFiles>)jqlService.findAllBySql(msql, approvalInfo.getId());
							 if (list!=null)
							 {
								 for (int i=0;i<list.size();i++)
								 {
									 if (i==0)
					        		 {
					        			 allfilenames+=list.get(i).getFileName();
					        		 }
					        		 else
					        		 {
					        			 allfilenames+=","+list.get(i).getFileName();
					        		 }
								 }
							 }
							for (int i=0;i<readers.length;i++)
							{
								//插入批阅的人,先要判断有没有记录
								Long userId=Long.valueOf(readers[i]);
								sql="select count(a) from ApprovalReader as a where a.state<="+ApproveConstants.NEW_STATUS_WAIT
										+" and a.approvalInfoId=? and a.isnew=0 and a.islast=0 and a.readUser=? ";
								Long hadread = (Long)jqlService.getCount(sql, id,userId);
								if (hadread==null || hadread.longValue()<1)
								{
					        		 ApprovalReader approvalReader=new ApprovalReader();
					        		 
					        		 approvalReader.setFileName(allfilenames);//存放所有附件的地址
					        		 approvalReader.setWebcontent(approvalInfo.getWebcontent());//存放网页内容
					        		 approvalReader.setTaskid(task.getId());
					        		 approvalReader.setApprovalInfoId(approvalInfo.getId());
					        		 approvalReader.setUserId(user.getId());
					        		 approvalReader.setSenddate(new Date());
					        		 approvalReader.setReadUser(userId);
					        		 jqlService.save(approvalReader);
					        		 createSignRead(approvalInfo,null,approvalReader);
								}
							}
						}
					}
			       //以上也是无理需求的实现方法-----end
				}
				else if (readcount!=null && readcount.longValue()>0l)//当前用户是批阅,批阅不更新状态
				{
					//只需更新ApprovalReader表
					sql="update ApprovalReader as a set a.comment=?,isRead=?,state="+ApproveConstants.NEW_STATUS_HADREAD+",date=?"
							+" where a.approvalInfoId=? and a.isnew=0 and a.islast=0 and a.readUser=? ";
					jqlService.excute(sql, comment,true,new Date(),id,user.getId());
					ischangedate=false;
					//批阅要加历史记录
					ApprovalTask task=insertTask(id,approvalInfo.getTitle(),approvalInfo.getWebcontent(),user
							,ApproveConstants.NEW_ACTION_SIGNREAD
							,(long)ApproveConstants.NEW_STATUS_HADREAD,"传阅",comment,false,false,null,null,null,0,0,null,jqlService);
					
					//以下是增加处理完毕后消除所有的消息提示和消息催办
					sql="select a from ApprovalReader as a where a.state="+ApproveConstants.NEW_STATUS_HADREAD
							+" and a.approvalInfoId=? and a.isnew=0 and a.islast=0 and a.readUser=? order by a.id DESC ";
					List<ApprovalReader> list = (List<ApprovalReader>)jqlService.findAllBySql(sql, id,user.getId());
					if(list!=null && list.size()>0)
					{
						jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.readid=? ",new Date(),list.get(0).getId());
						updateSignRead(approvalInfo,null,list.get(0),task.getId());
					}
				}
				else if (approvalInfo.getNodetype()!=null && approvalInfo.getNodetype().intValue()==2)//串签
				{
					String usql="update SameSignInfo as a set a.state="+ApproveConstants.NEW_STATUS_HAD
					+",a.actionid="+ApproveConstants.NEW_ACTION_SIGN+",a.approvalDate=? "
					+",a.comment=? "
					+" where a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=?";
					jqlService.excute(usql, new Date(),comment,id,user.getId());
					
					//以下是增加处理完毕后消除所有的消息提示和消息催办
					usql="select a from SameSignInfo as a where a.state="+ApproveConstants.NEW_STATUS_HAD
							+" and a.actionid="+ApproveConstants.NEW_ACTION_SIGN
							+" and a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=? order by a.id DESC ";
					List<SameSignInfo> list = (List<SameSignInfo>)jqlService.findAllBySql(usql, id,user.getId());
					if(list!=null && list.size()>0)
					{
						jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.sameid=?",new Date(),list.get(0).getSid());
					}
					
					
					usql="select a from SameSignInfo as a where a.state="+ApproveConstants.NEW_STATUS_HAD
							+" and a.actionid="+ApproveConstants.NEW_ACTION_SIGN
							+" and a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=? order by a.id DESC ";
					List<SameSignInfo> samelist=jqlService.findAllBySql(usql ,id,user.getId());
					Date signtagdate=null;
					if (samelist!=null && samelist.size()>0)
					{
						signtagdate=samelist.get(0).getSigntagdate();
					}
					
					int step=approvalInfo.getApprovalStep();//相当于节点号
					step++;
					approvalInfo.setApprovalStep(step);
					ArrayList<Long> ulist=approvalInfo.getPreUserIds();
					String signers="";
					if (ulist!=null && ulist.size()>0)
					{
						signers+=""+ulist.get(0).longValue();
					}
					ApprovalTask task=insertTask(id,approvalInfo.getTitle(),approvalInfo.getWebcontent(),user
							,ApproveConstants.NEW_ACTION_SIGN
							,(long)ApproveConstants.NEW_STATUS_HAD,"串签",comment,false,false,signers,null,null,step,1,signtagdate,jqlService);
					if (samelist!=null && samelist.size()>0)
					{
						updateSignRead(approvalInfo,samelist.get(0),null,task.getId());//更新签批记录
					}
					if (approvalInfo.getIsreturn()!=null && approvalInfo.getIsreturn().booleanValue())//返回送文人
					{
						status=ApproveConstants.NEW_STATUS_HAD;//已办
					}
					else //没选中返回送文人
					{
						//1、如果后面还有人需要在SameSignInfo表中再插入一条记录，状态保持不变
						//2、如果后面没有人，直接更新主流程状态
						
						if (ulist==null || ulist.size()==0)
						{
							//已结束
							status=ApproveConstants.NEW_STATUS_HAD;//已办
						}
						else
						{
							Long nextid=ulist.get(0);
							
				      	     jqlService.excute("update SameSignInfo as a set a.isnew=a.isnew+1 where a.approvalID=? ", approvalInfo.getId());

			        		 SameSignInfo sameinfo=new SameSignInfo();
			        		 sameinfo.setUserID(user.getId());
			        		 sameinfo.setTaskid(task.getId());
			        		 sameinfo.setNodetype(2);
			        		 sameinfo.setSignnum(1);
					         sameinfo.setApprovalID(approvalInfo.getId());
					         Users signer=(Users)jqlService.getEntity(Users.class, nextid);
				        	 sameinfo.setSigner(signer);
					         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
					         sameinfo.setCreateDate(new Date());
					         sameinfo.setWarndate(new Date());
					         sameinfo.setWarnnum(0);
//					         sameinfo.setComment(comment);
					         sameinfo.setIsreturn(false);
					       //目前规格只算最后一次的状态
					         jqlService.excute("update SameSignInfo as a set a.islast=a.islast+1 where a.approvalID=? and a.signer.id=? ", approvalInfo.getId(),nextid);
					         jqlService.save(sameinfo);
					         createSignRead(approvalInfo,sameinfo,null);
					         ulist.remove(0);
					         approvalInfo.setPreUserIds(ulist);
					         
					         if (signer.getMobile()!=null && signer.getMobile().length()==11)
					         {
					        	 mobilelist.add(signer.getMobile());
								 tranids.add(sameinfo.getSid());
								 content=user.getRealName()+"已对“"+approvalInfo.getTitle()+"”签批完毕，现流转到您这里";//暂不做短信回复功能
					         }
						}
					}
				}
	        	sendMobileinfo(mobilelist,content,Constant.MOBILESIGN,tranids,false,user);
			}
			
			if ("end".equals(type))
			{
//				status=ApproveConstants.NEW_STATUS_END;//终止
				approvalInfo.setStatus(status);
			}
			else
			{
				//如果全部结束要更新状态
				if (status!=null)
				{
//					approvalInfo.setStatus(status);//更新状态
					approvalInfo.setIssender(1);//当前签批者为送文人
				}
			}
			if (ischangedate)
			{
				approvalInfo.setModifier(user.getId());//最后一个处理者
				approvalInfo.setModifytime(new Date());//最后一个处理者的时间
			}
			if (user.getLastsignlevel()!=null && user.getLastsignlevel().intValue()>0)
			{
				approvalInfo.setComment(comment);
			}
			jqlService.update(approvalInfo);
			
			Long nums=(Long)jqlService.getCount("select count(a) from ApprovalDefaulter as a where type=? and user.id=?",ApproveConstants.APPROVAL_DEFAULT_MODIFY,user.getId());
			if (nums==null || nums.longValue()==0)
			{
				setModifyDefault(approvalInfo.getId(),null,null,comment,false,user);
			}
			
			
			//重新获取消息数量
			List<Long> userIds=new ArrayList<Long>();
			userIds.add(user.getId());
			MessageUtil.instance().changeMsgNum(new Messages(),user,userIds);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 处理返还送文人
	 * @param id  流程号
	 * @param submittype  submit  和终止
	 * @param comment  备注
	 * @param user
	 * @return
	 */
	public boolean backSendSign(Long id,String submittype,String comment,Users user)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);//获取主流程信息
						
			int step=0;
//			approvalInfo.setStatus(ApproveConstants.NEW_STATUS_HAD);//已办,送文人参与流转，状态已经没有意义了
			step=approvalInfo.getApprovalStep();
			step++;
			approvalInfo.setApprovalStep(step);
			jqlService.excute("update SameSignInfo as a set a.state="+ApproveConstants.NEW_STATUS_HAD
					+",a.actionid="+ApproveConstants.NEW_ACTION_SIGN
					+",a.approvalDate=?,a.comment=? "
					+" where a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=? ", new Date(),comment,approvalInfo.getId(),user.getId());
			
			String usql="select a from SameSignInfo as a where a.state="+ApproveConstants.NEW_STATUS_HAD
					+" and a.actionid="+ApproveConstants.NEW_ACTION_SIGN
					+" and a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=? order by a.id DESC ";
			List<SameSignInfo> samelist=jqlService.findAllBySql(usql ,id,user.getId());
			Date signtagdate=null;
			if (samelist!=null && samelist.size()>0)
			{
				signtagdate=samelist.get(0).getSigntagdate();
				jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.sameid=?",new Date(),samelist.get(0).getSid());
			}
			ApprovalTask task=insertTask(id,approvalInfo.getTitle(),approvalInfo.getWebcontent(),user
					,ApproveConstants.NEW_ACTION_SIGN,(long)ApproveConstants.NEW_STATUS_HAD
					,"签批",comment,false,true,""+approvalInfo.getUserID().longValue(),null,null,step,0,signtagdate,jqlService);
			if (samelist!=null && samelist.size()>0)
			{
				updateSignRead(approvalInfo,samelist.get(0),null,task.getId());
			}
			approvalInfo.setNewuserID(user.getId());
			approvalInfo.setDate(new Date());
			approvalInfo.setModifier(user.getId());
			approvalInfo.setModifytime(new Date());
			if (user.getLastsignlevel()!=null && user.getLastsignlevel().intValue()>0)
			{
				approvalInfo.setComment(comment);
			}
			approvalInfo.setIssender(1);
			
			jqlService.excute("update SameSignInfo as a set a.isnew=a.isnew+1 where a.approvalID=? ", approvalInfo.getId());
			jqlService.update(approvalInfo);
			
			//增加送文人处理的节点

			//以下也是无理需求的实现方法
			 SameSignInfo sameinfo=new SameSignInfo();
			 sameinfo.setUserID(user.getId());
      		 sameinfo.setTaskid(task.getId());
      		 sameinfo.setNodetype(0);
      		 sameinfo.setSignnum(1);
	         sameinfo.setApprovalID(approvalInfo.getId());
	         Users signer=(Users)jqlService.getEntity(Users.class, approvalInfo.getUserID());
	         sameinfo.setSigner(signer);
	         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
	         sameinfo.setCreateDate(new Date());
	         sameinfo.setWarndate(new Date());
	         sameinfo.setWarnnum(0);
//	         sameinfo.setComment(comment);
	         sameinfo.setIsreturn(false);
	         sameinfo.setActionid(-1l);
	       //目前规格只算最后一次的状态
	         jqlService.excute("update SameSignInfo as a set a.islast=a.islast+1 where a.approvalID=? and a.signer.id=? ", approvalInfo.getId(),approvalInfo.getUserID());
	         jqlService.save(sameinfo);
	         createSignRead(approvalInfo,sameinfo,null);
	       //以上也是无理需求的实现方法-----end
	         
	         List<String> mobilelist=new ArrayList<String>();
			 List<Long> tranids=new ArrayList<Long>();
			 String content="";
	         if (signer.getMobile()!=null && signer.getMobile().length()==11)
	         {
	        	 mobilelist.add(signer.getMobile());
				 tranids.add(sameinfo.getSid());
				 content=user.getRealName()+"已对“"+approvalInfo.getTitle()+"”签批完毕，返还给您";//暂不做短信回复功能
	         }
	         sendMobileinfo(mobilelist,content,Constant.MOBILESIGN,tranids,false,user);
	         
	       //重新获取消息数量
			List<Long> userIds=new ArrayList<Long>();
			userIds.add(user.getId());
			MessageUtil.instance().changeMsgNum(new Messages(),user,userIds);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 处理单个人签批的问题，可以送下一个人,再次送审附件不要再保存
	 * @param id
	 * @param sendchecked
	 * @param checkread
	 * @param signids
	 * @param issame
	 * @param isreturn
	 * @param readids
	 * @param comment
	 * @param user
	 * @return
	 */
	//Boolean sendchecked,Boolean checkread,
	public boolean modifySignSend(Long id,ArrayList<Long> signids,Boolean issame,Boolean isreturn,String readids
			,String comment,Users user)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);//获取主流程信息
			if (issame==null)
			{
				issame=false;
			}
			int signtype=0;
			String tasksigners="";
			String taskreaders=readids;
			if (issame!=null && issame.booleanValue())
			{
				signtype=2;//task会签
			}
			else
			{
				signtype=1;//task签批
			}
			int step=0;
			if (signids!=null && signids.size()>0)
			{
//				approvalInfo.setStatus(ApproveConstants.NEW_STATUS_WAIT);//待签
				step=approvalInfo.getApprovalStep();
				step++;
				approvalInfo.setApprovalStep(step);
				if (issame!=null && issame.booleanValue())//会签将所有人都显示出来
				{
					for (int i=0;i<signids.size();i++)
					{
						if (i==0)
						{
							tasksigners+=""+signids.get(i).longValue();
						}
						else
						{
							tasksigners+=","+signids.get(i).longValue();
						}
					}
				}
				else
				{
					tasksigners+=""+signids.get(0).longValue();
				}
			}
			else if (readids!=null && readids.length()>0)
			{
				approvalInfo.setStatus(ApproveConstants.NEW_STATUS_START);//仅批阅
			}
			
						
			approvalInfo.setNewuserID(user.getId());
//			approvalInfo.setDate(new Date());
			approvalInfo.setModifier(user.getId());
			approvalInfo.setModifytime(new Date());
//			approvalInfo.setComment(comment);
			approvalInfo.setIssender(0);
			for (int i=0;i<signids.size();i++)
			{
				if (signids.get(i).longValue()==approvalInfo.getUserID().longValue())//如果返还给送文人
		         {
		        	 approvalInfo.setIssender(1);
		         }
			}
			
//			approvalInfo.setIsreturn(isreturn);
			//NEW_ACTION_SEND
			long actionid=ApproveConstants.NEW_ACTION_SIGN;
			if (user.getId().longValue()==approvalInfo.getUserID().longValue())
			{
				actionid=ApproveConstants.NEW_ACTION_SEND;//当前处理人是原始送文人，就显示为送审
			}
			jqlService.excute("update SameSignInfo as a set a.state="+ApproveConstants.NEW_STATUS_HAD
					+",a.actionid="+actionid
					+",a.approvalDate=?,a.comment=? "
					+" where a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=? ",
					new Date(),comment,approvalInfo.getId(),user.getId());
			if (user.getLastsignlevel()!=null && user.getLastsignlevel().intValue()>0)
			{
				approvalInfo.setComment(comment);
			}
			String usql="select a from SameSignInfo as a where a.state="+ApproveConstants.NEW_STATUS_HAD
					+" and a.actionid="+actionid
					+" and a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=? order by a.id DESC ";
			List<SameSignInfo> samelist=jqlService.findAllBySql(usql ,id,user.getId());
			Date signtagdate=null;
			if (samelist!=null && samelist.size()>0)
			{
				signtagdate=samelist.get(0).getSigntagdate();
				jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.sameid=?",new Date(),samelist.get(0).getSid());
			}
			ApprovalTask task=insertTask(id,approvalInfo.getTitle(),approvalInfo.getWebcontent(),user
					,actionid
					,(long)ApproveConstants.NEW_STATUS_HAD,"签批",comment,issame,true,tasksigners,taskreaders,null,step,signtype,signtagdate,jqlService);
			if (samelist!=null && samelist.size()>0)
			{
				updateSignRead(approvalInfo,samelist.get(0),null,task.getId());
			}
			
			jqlService.excute("update SameSignInfo as a set a.isnew=a.isnew+1 where a.approvalID=? ", approvalInfo.getId());
			
			List<String> mobilelist=new ArrayList<String>();
			List<Long> tranids=new ArrayList<Long>();
			String content="";
			if (issame!=null && issame.booleanValue())
			{
				//会签
				approvalInfo.setNodetype(1);
				//插入会签人员
				for (int i=0;i<signids.size();i++)
				{
					 SameSignInfo sameinfo=new SameSignInfo();
					 sameinfo.setUserID(user.getId());
	        		 sameinfo.setTaskid(task.getId());//这里要加，流程图的节点信息就靠它了
	        		 sameinfo.setNodetype(1);
	        		 sameinfo.setSignnum(signids.size());
			         sameinfo.setApprovalID(approvalInfo.getId());
			         Users signer=(Users)jqlService.getEntity(Users.class, signids.get(i));
		        	 sameinfo.setSigner(signer);
			         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
			         sameinfo.setCreateDate(new Date());
			         sameinfo.setWarndate(new Date());
			         sameinfo.setWarnnum(0);
//			         sameinfo.setComment(comment);
			         sameinfo.setIsreturn(false);
			       //目前规格只算最后一次的状态
			         jqlService.excute("update SameSignInfo as a set a.islast=a.islast+1 where a.approvalID=? and a.signer.id=? ", approvalInfo.getId(),signids.get(i));
			         jqlService.save(sameinfo);
			         createSignRead(approvalInfo,sameinfo,null);
			         setWarnMessage("f"+sameinfo.getSid(),comment,user,0);
			         
			         if (signer.getMobile()!=null && signer.getMobile().length()==11)
			         {
			        	 mobilelist.add(signer.getMobile());
						 tranids.add(sameinfo.getSid());
						 content=user.getRealName()+"已对“"+approvalInfo.getTitle()+"”签批完毕，先流转到您名下";//暂不做短信回复功能
			         }
				}
				approvalInfo.setPreUserIds(null);
		        approvalInfo.setUserlist(null);
			}
			else if (signids!=null && signids.size()>1)
			{
				approvalInfo.setNodetype(2);//串签
				//更新串签人员
				Long nextid=signids.get(0);
        		 SameSignInfo sameinfo=new SameSignInfo();
        		 sameinfo.setUserID(user.getId());
        		 sameinfo.setTaskid(task.getId());
        		 sameinfo.setNodetype(2);
        		 sameinfo.setSignnum(1);
		         sameinfo.setApprovalID(approvalInfo.getId());
		         Users signer=(Users)jqlService.getEntity(Users.class, nextid);
	        	 sameinfo.setSigner(signer);
		         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
		         sameinfo.setCreateDate(new Date());
		         sameinfo.setWarndate(new Date());
		         sameinfo.setWarnnum(0);
//		         sameinfo.setComment(comment);
		         sameinfo.setIsreturn(false);
		       //目前规格只算最后一次的状态
		         jqlService.excute("update SameSignInfo as a set a.islast=a.islast+1 where a.approvalID=? and a.signer.id=? ", approvalInfo.getId(),nextid);
		         jqlService.save(sameinfo);
		         createSignRead(approvalInfo,sameinfo,null);
		         signids.remove(0);
		         approvalInfo.setPreUserIds(signids);
		         approvalInfo.setUserlist(signids);
		         
		         setWarnMessage("f"+sameinfo.getSid(),comment,user,0);
		         
		         if (signer.getMobile()!=null && signer.getMobile().length()==11)
		         {
		        	 mobilelist.add(signer.getMobile());
					 tranids.add(sameinfo.getSid());
					 content=user.getRealName()+"已对“"+approvalInfo.getTitle()+"”签批完毕，先流转到您名下";//暂不做短信回复功能
		         }
			}
			else
			{
				approvalInfo.setNodetype(0);//单独一人
				//只插入一个人
				 Long nextid=signids.get(0);
				
				 SameSignInfo sameinfo=new SameSignInfo();
				 sameinfo.setUserID(user.getId());
	       		 sameinfo.setTaskid(task.getId());
	       		 sameinfo.setNodetype(0);
	       		 sameinfo.setSignnum(1);
		         sameinfo.setApprovalID(approvalInfo.getId());
		         Users signer=(Users)jqlService.getEntity(Users.class, nextid);
	        	 sameinfo.setSigner(signer);
		         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
		         sameinfo.setCreateDate(new Date());
		         sameinfo.setWarndate(new Date());
		         sameinfo.setWarnnum(0);
//		         sameinfo.setComment(comment);
		         sameinfo.setIsreturn(false);
			       //目前规格只算最后一次的状态
		         jqlService.excute("update SameSignInfo as a set a.islast=a.islast+1 where a.approvalID=? and a.signer.id=? ", approvalInfo.getId(),nextid);
		         jqlService.save(sameinfo);
		         createSignRead(approvalInfo,sameinfo,null);
	        	 approvalInfo.setPreUserIds(null);
			     approvalInfo.setUserlist(null);
			     
		         setWarnMessage("f"+sameinfo.getSid(),comment,user,0);
		         
		         if (signer.getMobile()!=null && signer.getMobile().length()==11)
		         {
		        	 mobilelist.add(signer.getMobile());
					 tranids.add(sameinfo.getSid());
					 content=user.getRealName()+"已对“"+approvalInfo.getTitle()+"”签批完毕，先流转到您名下";//暂不做短信回复功能
		         }
			}
			jqlService.update(approvalInfo);

	        sendMobileinfo(mobilelist,content,Constant.MOBILESIGN,tranids,false,user);//发送手机短信
	        
			//重新获取消息数量
			List<Long> userIds=new ArrayList<Long>();
			userIds.add(user.getId());
			MessageUtil.instance().changeMsgNum(new Messages(),user,userIds);
			
			if (readids!=null && readids.length()>0)
			{
				//存储阅读者
				String[] readers=readids.split(",");
				if (readers!=null && readers.length>0)
				{
		        	 jqlService.excute("update ApprovalReader as a set a.isnew=a.isnew+1,a.islast=a.islast+1 where a.approvalInfoId=? ", approvalInfo.getId());
			         String allfilenames="";
			         String msql="select a from ApprovalFiles as a where a.approvalid=? ";
					 List<ApprovalFiles> list=(List<ApprovalFiles>)jqlService.findAllBySql(msql, approvalInfo.getId());
					 if (list!=null)
					 {
						 for (int i=0;i<list.size();i++)
						 {
							 if (i==0)
			        		 {
			        			 allfilenames+=list.get(i).getFileName();
			        		 }
			        		 else
			        		 {
			        			 allfilenames+=","+list.get(i).getFileName();
			        		 }
						 }
					 }
					for (int i=0;i<readers.length;i++)
					{
						//插入批阅的人
						boolean issave=true;
		        		 for (int n=0;n<signids.size();n++)
		        		 {
		        			 Long tid=signids.get(n);
		        			 if (readers[i].equals(String.valueOf(tid)))
		        			 {
		        				 issave=false;
		        				 break;
		        			 }
		        		 }
		        		 if (issave)//人员已在签批人列表中不插入
		        		 {
			        		 ApprovalReader approvalReader=new ApprovalReader();
			        		 Long userId=Long.valueOf(readers[i]);
			        		 approvalReader.setFileName(allfilenames);//存放所有附件的地址
			        		 approvalReader.setWebcontent(approvalInfo.getWebcontent());//存放网页内容
			        		 approvalReader.setTaskid(task.getId());
			        		 approvalReader.setApprovalInfoId(approvalInfo.getId());
			        		 approvalReader.setUserId(user.getId());
			        		 approvalReader.setSenddate(new Date());
			        		 approvalReader.setReadUser(userId);
			        		 jqlService.save(approvalReader);
			        		 createSignRead(approvalInfo,null,approvalReader);
		        		 }
					}
				}
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 再次送审,可以跟上面合在一起
	 * @param id
	 * @param signids
	 * @param issame
	 * @param isreturn
	 * @param readids
	 * @param comment
	 * @param user
	 * @return
	 */
	public boolean reSendSign(Long id,ArrayList<Long> signids,Boolean issame,Boolean isreturn,String readids,String backsigners
			,String comment,Users user)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);//获取主流程信息
			approvalInfo.setUserlist(new ArrayList<Long>());//先将预定义的人员去除
			approvalInfo.setPreUserIds(new ArrayList<Long>());//先将预定义的人员去除
			if (issame==null)
			{
				issame=false;
			}
			int signtype=0;
			String tasksigners="";
			String taskreaders=readids;
			if (issame!=null && issame.booleanValue())
			{
				signtype=2;//task会签
			}
			else
			{
				signtype=1;//task签批
			}
			int step=0;
			if (signids!=null && signids.size()>0)
			{
//				approvalInfo.setStatus(ApproveConstants.NEW_STATUS_WAIT);//待签
				step=approvalInfo.getApprovalStep();
				step++;
				approvalInfo.setApprovalStep(step);
				if (signtype==2)
				{
					for (int i=0;i<signids.size();i++)
					{
						if (i==0)
						{
							tasksigners+=""+signids.get(i).longValue();
						}
						else
						{
							tasksigners+=","+signids.get(i).longValue();
						}
					}
				}
				else
				{
					tasksigners+=""+signids.get(0).longValue();
				}
			}
			else if (readids!=null && readids.length()>0)
			{
//				approvalInfo.setStatus(ApproveConstants.NEW_STATUS_START);//仅批阅
			}
			jqlService.excute("update SameSignInfo as a set a.state="+ApproveConstants.NEW_STATUS_HAD
					+",a.actionid="+ApproveConstants.NEW_ACTION_SEND
					+",a.approvalDate=?,a.comment=? "
					+" where a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=? ", new Date(),comment,approvalInfo.getId(),user.getId());
			String usql="select a from SameSignInfo as a where a.state="+ApproveConstants.NEW_STATUS_HAD
					+" and a.actionid="+ApproveConstants.NEW_ACTION_SEND
					+" and a.isnew=0 and a.islast=0 and a.approvalID=? and a.signer.id=? order by a.id DESC ";
			List<SameSignInfo> samelist=jqlService.findAllBySql(usql ,id,user.getId());
			Date signtagdate=null;
			if (samelist!=null && samelist.size()>0)
			{
				signtagdate=samelist.get(0).getSigntagdate();
			}
			
			ApprovalTask task=insertTask(id,approvalInfo.getTitle(),approvalInfo.getWebcontent(),user
					,ApproveConstants.NEW_ACTION_SEND
					,(long)ApproveConstants.NEW_STATUS_WAIT,"送审",comment,issame,true,tasksigners,taskreaders,null,step,signtype,signtagdate,jqlService);
			if (samelist!=null && samelist.size()>0)
			{
				updateSignRead(approvalInfo,samelist.get(0),null,task.getId());
			}
			//这里签批人、传阅人、协作者暂时都传空
			approvalInfo.setNewuserID(user.getId());
//			approvalInfo.setDate(new Date());
			approvalInfo.setModifier(user.getId());
			approvalInfo.setModifytime(new Date());
			if (user.getLastsignlevel()!=null && user.getLastsignlevel().intValue()>0)
			{
				approvalInfo.setComment(comment);
			}
			approvalInfo.setIsreturn(isreturn);
			
			
			jqlService.excute("update SameSignInfo as a set a.isnew=a.isnew+1 where a.approvalID=? ", approvalInfo.getId());
			
			List<String> mobilelist=new ArrayList<String>();
			List<Long> tranids=new ArrayList<Long>();
			String content="";
			if (issame!=null && issame.booleanValue())
			{
				//会签
				approvalInfo.setNodetype(1);
				//插入会签人员
				for (int i=0;i<signids.size();i++)
				{
					 SameSignInfo sameinfo=new SameSignInfo();
					 sameinfo.setUserID(user.getId());
	        		 sameinfo.setTaskid(task.getId());//这里要加，流程图的节点信息就靠它了
	        		 sameinfo.setNodetype(1);
	        		 sameinfo.setSignnum(signids.size());
			         sameinfo.setApprovalID(approvalInfo.getId());
			         Users signer=(Users)jqlService.getEntity(Users.class, signids.get(i));
		        	 sameinfo.setSigner(signer);
			         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
			         sameinfo.setCreateDate(new Date());
			         sameinfo.setWarndate(new Date());
			         sameinfo.setWarnnum(0);
//			         sameinfo.setComment(comment);
			         sameinfo.setIsreturn(false);
			       //目前规格只算最后一次的状态
			         jqlService.excute("update SameSignInfo as a set a.islast=a.islast+1 where a.approvalID=? and a.signer.id=? ", approvalInfo.getId(),signids.get(i));
			         jqlService.save(sameinfo);
			         createSignRead(approvalInfo,sameinfo,null);
			         setWarnMessage("f"+sameinfo.getSid(),comment,user,0);
			         
			         if (signer.getMobile()!=null && signer.getMobile().length()==11)
			         {
			        	 mobilelist.add(signer.getMobile());
						 tranids.add(sameinfo.getSid());
						 content=user.getRealName()+"请您对“"+approvalInfo.getTitle()+"”进行签批";//暂不做短信回复功能
			         }
				}
				if (backsigners!=null && backsigners.length()>0)
	        	{
	        		 String[] backers=backsigners.split(";");
	        		 ArrayList<String> signerslist=new ArrayList<String>();
	        		 if (backers!=null && backers.length>0)
	        		 {
	        			 for (int i=0;i<backers.length;i++)
	        			 {
	        				 signerslist.add(backers[i]);
	        			 }
	        			 approvalInfo.setSignerslist(signerslist);//会签后处理人
	        		 }
	        	 }
			}
			else if (signids!=null && signids.size()>1)
			{
				approvalInfo.setNodetype(2);//串签
				//更新串签人员
				Long nextid=signids.get(0);
        		 SameSignInfo sameinfo=new SameSignInfo();
        		 sameinfo.setUserID(user.getId());
        		 sameinfo.setTaskid(task.getId());
        		 sameinfo.setNodetype(2);
        		 sameinfo.setSignnum(1);
		         sameinfo.setApprovalID(approvalInfo.getId());
		         Users signer=(Users)jqlService.getEntity(Users.class, nextid);
	        	 sameinfo.setSigner(signer);
		         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
		         sameinfo.setCreateDate(new Date());
		         sameinfo.setWarndate(new Date());
		         sameinfo.setWarnnum(0);
//		         sameinfo.setComment(comment);
		         sameinfo.setIsreturn(false);
		       //目前规格只算最后一次的状态
		         jqlService.excute("update SameSignInfo as a set a.islast=a.islast+1 where a.approvalID=? and a.signer.id=? ", approvalInfo.getId(),nextid);
		         jqlService.save(sameinfo);
		         createSignRead(approvalInfo,sameinfo,null);
		         signids.remove(0);
		         approvalInfo.setPreUserIds(signids);
		         
		         setWarnMessage("f"+sameinfo.getSid(),comment,user,0);
		         if (signer.getMobile()!=null && signer.getMobile().length()==11)
		         {
		        	 mobilelist.add(signer.getMobile());
					 tranids.add(sameinfo.getSid());
					 content=user.getRealName()+"请您对“"+approvalInfo.getTitle()+"”进行签批";//暂不做短信回复功能
		         }
			}
			else
			{
				approvalInfo.setNodetype(0);//单独一人
				//只插入一个人
				Long nextid=signids.get(0);
				
				 SameSignInfo sameinfo=new SameSignInfo();
				 sameinfo.setUserID(user.getId());
	       		 sameinfo.setTaskid(task.getId());
	       		 sameinfo.setNodetype(0);
	       		 sameinfo.setSignnum(1);
		         sameinfo.setApprovalID(approvalInfo.getId());
		         Users signer=(Users)jqlService.getEntity(Users.class, nextid);
	        	 sameinfo.setSigner(signer);
		         sameinfo.setState(ApproveConstants.NEW_STATUS_WAIT);
		         sameinfo.setCreateDate(new Date());
		         sameinfo.setWarndate(new Date());
		         sameinfo.setWarnnum(0);
//		         sameinfo.setComment(comment);
		         sameinfo.setIsreturn(false);
		       //目前规格只算最后一次的状态
		         jqlService.excute("update SameSignInfo as a set a.islast=a.islast+1 where a.approvalID=? and a.signer.id=? ", approvalInfo.getId(),nextid);
		         jqlService.save(sameinfo);
		         createSignRead(approvalInfo,sameinfo,null);
		         setWarnMessage("f"+sameinfo.getSid(),comment,user,0);
		         if (signer.getMobile()!=null && signer.getMobile().length()==11)
		         {
		        	 mobilelist.add(signer.getMobile());
					 tranids.add(sameinfo.getSid());
					 content=user.getRealName()+"请您对“"+approvalInfo.getTitle()+"”进行签批";//暂不做短信回复功能
		         }
			}
			jqlService.update(approvalInfo);
			
			sendMobileinfo(mobilelist,content,Constant.MOBILESIGN,tranids,false,user);//发送手机短信
			if (readids!=null && readids.length()>0)
			{
				//存储阅读者
				String[] readers=readids.split(",");
				if (readers!=null && readers.length>0)
				{
		        	 jqlService.excute("update ApprovalReader as a set a.isnew=a.isnew+1,a.islast=a.islast+1 where a.approvalInfoId=? ", approvalInfo.getId());
			         String allfilenames="";
			         String msql="select a from ApprovalFiles as a where a.approvalid=? ";
					 List<ApprovalFiles> list=(List<ApprovalFiles>)jqlService.findAllBySql(msql, approvalInfo.getId());
					 if (list!=null)
					 {
						 for (int i=0;i<list.size();i++)
						 {
							 if (i==0)
			        		 {
			        			 allfilenames+=list.get(i).getFileName();
			        		 }
			        		 else
			        		 {
			        			 allfilenames+=","+list.get(i).getFileName();
			        		 }
						 }
					 }
					for (int i=0;i<readers.length;i++)
					{
						//插入批阅的人
						boolean issave=true;
		        		 for (int n=0;n<signids.size();n++)
		        		 {
		        			 Long tid=signids.get(n);
		        			 if (readers[i].equals(String.valueOf(tid)))
		        			 {
		        				 issave=false;
		        				 break;
		        			 }
		        		 }
		        		 if (issave)//人员已在签批人列表中不插入
		        		 {
			        		 ApprovalReader approvalReader=new ApprovalReader();
			        		 Long userId=Long.valueOf(readers[i]);
			        		 approvalReader.setFileName(allfilenames);//存放所有附件的地址
			        		 approvalReader.setWebcontent(approvalInfo.getWebcontent());//存放网页内容
			        		 approvalReader.setTaskid(task.getId());
			        		 approvalReader.setApprovalInfoId(approvalInfo.getId());
			        		 approvalReader.setUserId(user.getId());
			        		 approvalReader.setSenddate(new Date());
			        		 approvalReader.setReadUser(userId);
			        		 jqlService.save(approvalReader);
			        		 createSignRead(approvalInfo,null,approvalReader);
		        		 }
					}
				}
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 存储历史附件
	 * @param approvalid
	 * @param taskid
	 * @param user
	 * @return
	 */
	private boolean saveTaskFiles(Long approvalid,Long taskid,Users user)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="select a from ApprovalFiles as a where a.isnew=0 and a.approvalid=? ";
			List<ApprovalFiles> filelist=(List<ApprovalFiles>)jqlService.findAllBySql(sql, approvalid);
			if (filelist!=null && filelist.size()>0)
			{
				JCRService jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
				FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
				for (int i=0;i<filelist.size();i++)
				{
					ApprovalFiles afile=filelist.get(i);
					ApprovalTaskFiles taskfile=new ApprovalTaskFiles();
					taskfile.setApprovalid(approvalid);
					taskfile.setTaskid(taskid);
//					String versionname = fileSystemService.getLastVersion(afile.getDocumentpath());
					String versionname=jcrService.createVersion(afile.getDocumentpath(), user.getUserName(), "", "1");
					taskfile.setDocumentpath(versionname);//获取最后一次版本号,用户保存时直接保存到原文件上,这种情况导致会签的文档不知道到底是谁修改的，甚至可能造成张冠李戴，后面再处理
					taskfile.setFileName(afile.getFileName());
					taskfile.setIsnew(0l);
					taskfile.setOldpath(afile.getDocumentpath());
					taskfile.setUserID(user.getId());
					taskfile.setAdddate(new Date());
					jqlService.save(taskfile);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 删除签批相关信息包括（草稿、签批、批阅、协作）
	 * @param ids
	 * @param deltype "draft":"草稿","done":"已送","todo":"待办","filed":"已办" myquest 收阅 toread 我的请求
	 * @param user
	 */
	public String delSignInfo(List<Long> ids,String deltype,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String cond="";
			for (int i=0;i<ids.size();i++)
			{
				if (i==0)
				{
					cond+=" where (a.approvalID="+ids.get(i);
				}
				else
				{
					cond+=" or a.approvalID="+ids.get(i);
				}
				if (i==(ids.size()-1))
				{
					cond+=")";
				}
			}
			if ("draft".equals(deltype))
			{
				for (int i=0;i<ids.size();i++)
				{
					String sql="delete from ApprovalSave as a where a.id=? ";
					jqlService.excute(sql,ids.get(i));
				}
			}
			else if ("done".equals(deltype))//只有用户没有处理的才能删除，后面改
			{
				
				String sqlcount="select count(a) from ApprovalTask as a "+cond;
				Long count=(Long)jqlService.getCount(sqlcount);
				if (count.longValue()>1)
				{
					return "已有人处理过了，不能删除，您可以终止。";
				}
				for (int i=0;i<ids.size();i++)
				{
					String sql="delete from ApprovalInfo as a where a.id=? ";
					jqlService.excute(sql,ids.get(i));
					
					sql="delete from ApprovalTask as a where a.approvalID=? ";
					jqlService.excute(sql,ids.get(i));
					
					sql="delete from ApprovalCooper as a where a.approvalID=? ";
					jqlService.excute(sql,ids.get(i));
					
					sql="delete from ApprovalFiles as a where a.approvalid=? ";
					jqlService.excute(sql,ids.get(i));
					
					sql="delete from ApprovalReader as a where a.approvalInfoId=? ";
					jqlService.excute(sql,ids.get(i));
					
					sql="delete from SameSignInfo as a where a.approvalID=? ";
					jqlService.excute(sql,ids.get(i));
					
					sql="delete from ApprovalTaskFiles as a where a.approvalid=? ";
					jqlService.excute(sql,ids.get(i));
				}
			}
			else if ("todo".equals(deltype))
			{
				String sqlcount="select count(a) from SameSignInfo as a "+cond
				+" and a.isnew=0 and a.islast=0 and a.signer.id=? and a.state="+ApproveConstants.NEW_STATUS_WAIT;
				Long count=(Long)jqlService.getCount(sqlcount,user.getId());
				if (count.longValue()>0)
				{
					return "对不起，您不能删除。";
				}
				for (int i=0;i<ids.size();i++)
				{
					String sql="delete from ApprovalCooper as a where a.approvalID=? and a.cooper.id=? ";
					jqlService.excute(sql,ids.get(i),user.getId());
					
					sql="delete from ApprovalReader as a where a.approvalInfoId=? and a.readUser=? ";
					jqlService.excute(sql,ids.get(i),user.getId());
					
					sql="update SameSignInfo as a set a.isview=1 where a.approvalID=? and a.signer.id=?";
					jqlService.excute(sql,ids.get(i),user.getId());
				}
			}
			else if ("filed".equals(deltype))
			{
//				String sqlcount="select count(a) from SameSignInfo as a "+cond
//				+" and a.isnew=0 and a.islast=0 and a.signer.id=? and a.state="+ApproveConstants.NEW_STATUS_HAD;
//				Long count=(Long)jqlService.getCount(sqlcount,user.getId());
//				if (count.longValue()>0)
//				{
//					return "对不起，您不能删除。";
//				}
				for (int i=0;i<ids.size();i++)
				{
					String sql="update ApprovalCooper as a set a.isview=1 where a.approvalID=? and a.cooper.id=? ";
					jqlService.excute(sql,ids.get(i),user.getId());
					
					sql="update ApprovalReader as a set a.isview=1 where a.approvalInfoId=? and a.readUser=? ";
					jqlService.excute(sql,ids.get(i),user.getId());
					
					sql="update SameSignInfo as a set a.isview=1 where a.approvalID=? and a.signer.id=?  and a.state="+ApproveConstants.NEW_STATUS_HAD;
					jqlService.excute(sql,ids.get(i),user.getId());
				}
			}
			else if ("myquest".equals(deltype))//成文,不能删除，只能归档
			{
				for (int i=0;i<ids.size();i++)
				{
					ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, ids.get(i));
					approvalInfo.setStatus(ApproveConstants.NEW_STATUS_DEL);
					jqlService.update(approvalInfo);
					
					ApprovalTask task=insertTask(ids.get(i),approvalInfo.getTitle(),approvalInfo.getWebcontent(),user
							,ApproveConstants.NEW_ACTION_DEL
							,(long)ApproveConstants.NEW_STATUS_DEL,"删除","",false,true,null,null,null,-1,0,null,jqlService);
				}
			}
			else if ("toread".equals(deltype))
			{
				for (int i=0;i<ids.size();i++)
				{
					String sql="update ApprovalReader as a set a.isview=1,a.isnew=1 where a.approvalInfoId=? and a.readUser=? ";
					jqlService.excute(sql,ids.get(i),user.getId());
				}
			}
			else
			{
				//暂不处理
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 成文，只是将状态改成成文
	 * @param ids
	 * @param user
	 * @return
	 */
	public boolean signSuccess(List<Long> ids,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			for (int i=0;i<ids.size();i++)
			{
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, ids.get(i));
				approvalInfo.setStatus(ApproveConstants.NEW_STATUS_SUCCESS);
				
				ApprovalTask task=insertTask(ids.get(i),approvalInfo.getTitle(),approvalInfo.getWebcontent(),user
						,ApproveConstants.NEW_ACTION_HADSUCCESS
						,(long)ApproveConstants.NEW_STATUS_SUCCESS,"签批","成文",false,false,null,null,null,0,0,null,jqlService);
				approvalInfo.setModifytime(new Date());
				approvalInfo.setModifier(user.getId());
				jqlService.update(approvalInfo);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 终止,只是将状态改成终止状态
	 * @param ids
	 * @param user
	 * @return
	 */
	public boolean endSignInfo(List<Long> ids,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			for (int i=0;i<ids.size();i++)
			{
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, ids.get(i));
				approvalInfo.setStatus(ApproveConstants.NEW_STATUS_END);
				int step=approvalInfo.getApprovalStep();//暂不处理
				
				ApprovalTask task=insertTask(ids.get(i),approvalInfo.getTitle(),approvalInfo.getWebcontent(),user
						,ApproveConstants.NEW_ACTION_END
						,(long)ApproveConstants.NEW_STATUS_END,"终止","终止",false,false,null,null,null,0,0,null,jqlService);
				approvalInfo.setModifytime(new Date());
				approvalInfo.setModifier(user.getId());
				jqlService.update(approvalInfo);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public Map<String,Object> getNewHistory(Long userId, Long approveId)
	{
		return getNewHistory(userId, approveId,false);
	}
	public Map<String,Object> getNewHistory(Long userId, Long approveId,boolean ismoble)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> datalist = new ArrayList<Map<String,Object>>();
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);
		String fileName = "";
		String date = "";
		String ownerName = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	
//		if(info.getApprovalStep()>0)//说明是从发布或者归档点过来的,要从step字段中取旧的approvalID,task中的 approvalId都是根据旧的approvalinfo来的
		{ 
			//审批第几步
			Long oldId = Long.valueOf(info.getApprovalStep()+"");
			
			result.put("fileName",info.getTitle() );
			ownerName = userService.getUser(info.getUserID()).getRealName();				
			date = sdf.format(info.getDate());
			result.put("owner", ownerName);	
			String sql="select count(a) from SameSignInfo as a where a.approvalID=? ";
			Long samesize=(Long)jqlService.getCount(sql, info.getId());
			if (samesize==null || samesize==0)
			{
				result.put("modifytype", 0);//0为协作，1为签批
			}
			else
			{
				result.put("modifytype", info.getModifytype());//0为协作，1为签批
			}
			result.put("createDate", date);
			String endName = "doc";
			result.put("fileType", WebofficeUtility.getFileIconPathSrc(endName,false,false,false,"48", ".png"));
		}		
		
		List<Object[]> ar;
		Long hisnums=(Long)jqlService.getCount("select count(a) from ApprovalSignRead as a where a.approvalid=? ", info.getId());
		if (!WebConfig.signhistoryview || hisnums==null || hisnums.longValue()==0)
		{
			result.put("signhistoryview", false);
			String queryString = "select u.realName, t from ApprovalTask t, Users u "
					+ " where t.modifier = u.id and t.approvalID = ? order by t.id ";
				List<Object[]> list = jqlService.findAllBySql(queryString, info.getId());
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalTask approvaltask = (ApprovalTask)objArray[1];
					Map temp = new HashMap();
					temp.put("taskid",approvaltask.getId());
					if (objArray[0]!=null)
					{
						temp.put("actor", objArray[0].toString());
					}
					
					temp.put("action",approvaltask.getAction());
					if (approvaltask.getSigners()!=null && approvaltask.getSigners().length()>0)
					{
						temp.put("nextActor", getUserName(approvaltask.getSigners()));//这里要处理一下
						temp.put("actorType", "0");//送签
	
					}
					else
					{
						temp.put("nextActor", getUserName(approvaltask.getSendreaders()));//没有签批人就是传阅人
						temp.put("actorType", "1");//传阅
	
					}
					temp.put("time", sdf.format(approvaltask.getModifytime()));
					temp.put("comment", approvaltask.getComment()==null?"":approvaltask.getComment());
					List<ApprovalTaskFiles> tfilelist=(List<ApprovalTaskFiles>)jqlService.findAllBySql("select a from ApprovalTaskFiles as a where a.taskid=? ",approvaltask.getId());
					List<Map> versionlist=new ArrayList<Map>();
					if (tfilelist!=null && tfilelist.size()>0)
					{
						for (int n=0;n<tfilelist.size();n++)
						{
							ApprovalTaskFiles tfile=tfilelist.get(n);
							Map tempmap = new HashMap();
							tempmap.put("filename",tfile.getFileName());
							tempmap.put("fileversion",tfile.getDocumentpath());
							versionlist.add(tempmap);
						}
					}
					temp.put("versionlist", versionlist);
					temp.put("step", approvaltask.getStepName());
					//阅读信息后面处理
	//				ar = (List<Object[]>)jqlService.findAllBySql(queryString, approvaltask.getId());
	//				StringBuffer tempS = new StringBuffer();
	//				boolean flag = false;
	//				for (Object[] tr : ar)
	//				{
	//					if (flag)
	//					{
	//						tempS.append("<p>");
	//					}
	//					tempS.append(tr[0]);
	//					tempS.append(":");
	//					tempS.append(((ApprovalReader)tr[1]).isRead() ? "已经阅读" : "未阅读" );
	//					tempS.append("&nbsp;备注信息为：");
	//					tempS.append(((ApprovalReader)tr[1]).getComment() != null ? ((ApprovalReader)tr[1]).getComment() : "");
	//					flag = true;					
	//				}
	//				if (flag)
	//				{
	//					temp.put("reader", tempS.toString());
	//				}
					datalist.add(temp);
				}
			}
		}
		else//显示table
		{
			result.put("signhistoryview", true);
			String queryString = "select t from ApprovalSignRead t "
					+ " where t.approvalid = ? order by t.id ";
			List<ApprovalSignRead> list = (List<ApprovalSignRead>)jqlService.findAllBySql(queryString, info.getId());
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				Long taskid=0L;
				for(int i=0;i<size;i++)
				{
					ApprovalSignRead signread = (ApprovalSignRead)list.get(i);
					Map temp = new HashMap();
					temp.put("id",signread.getId());
					if (signread.getSender()!=null)
					{
						temp.put("actor", signread.getSender().getRealName());
					}
					else
					{
						temp.put("actor", jqlService.getUsers(info.getUserID()).getRealName());
						
					}
					temp.put("action",signread.getActionid());
					temp.put("nextActor", signread.getSigner().getRealName());//这里要处理一下
					String statename="待办";
					if (signread.getSameid()!=null)
					{
						temp.put("actiontypename", "送签");
						temp.put("actorType", "0");
						if (signread.getActionid()!=null && signread.getActionid()<0)
						{
							temp.put("actiontypename", "返回");
						}
					}
					else
					{
						temp.put("actiontypename", "传阅");
						temp.put("actorType", "1");
						statename="待阅";
					}
					
					if (signread.getState()==ApproveConstants.NEW_STATUS_START) //协作或仅批阅
					{
//						statename="待办";
					}
					else if (signread.getState()==ApproveConstants.NEW_STATUS_READ) //协作或仅批阅
					{
						statename="已签收";
					}
					else if (signread.getState()==ApproveConstants.NEW_STATUS_WAIT) //协作或仅批阅
					{
//						statename="待办";
					}
					else if (signread.getState()==ApproveConstants.NEW_STATUS_HAD) //协作或仅批阅
					{
						statename="已办";
					}
					else if (signread.getState()==ApproveConstants.NEW_STATUS_HADREAD) //协作或仅批阅
					{
						statename="已阅";
					}
					else if (signread.getState()==ApproveConstants.NEW_STATUS_END) //协作或仅批阅
					{
						statename="终止";
					}
					else if (signread.getState()==ApproveConstants.NEW_STATUS_DEL) //协作或仅批阅
					{
						statename="废弃";
					}
					else if (signread.getState()==ApproveConstants.NEW_STATUS_SUCCESS) //协作或仅批阅
					{
						statename="已成文";
					}
					temp.put("statename", statename);
					temp.put("sendtime", signread.getSendtime()==null?"":sdf.format(signread.getSendtime()));
					
					temp.put("signtime", signread.getSigntime()==null?"":sdf.format(signread.getSigntime()));
					
					temp.put("sendcomment", signread.getSendcomment()==null?"":signread.getSendcomment());
					temp.put("signcomment", signread.getSigncomment()==null?"":signread.getSigncomment());
					List<ApprovalTaskFiles> tfilelist=(List<ApprovalTaskFiles>)jqlService.findAllBySql("select a from ApprovalTaskFiles as a where a.signreadid=? ",signread.getId());
					List<Map> versionlist=new ArrayList<Map>();
					if (tfilelist!=null && tfilelist.size()>0)
					{
						for (int n=0;n<tfilelist.size();n++)
						{
							ApprovalTaskFiles tfile=tfilelist.get(n);
							Map tempmap = new HashMap();
							taskid=tfile.getTaskid();
							tempmap.put("filename",tfile.getFileName());
							tempmap.put("fileversion",tfile.getDocumentpath());
							versionlist.add(tempmap);
						}
						
					}
					temp.put("versionlist", versionlist);
					temp.put("step", "送审");
					temp.put("taskid", taskid);
					datalist.add(temp);
				}
				
			}
		}
		result.put("history", datalist);
		
		
		HashMap hash=getFlowPicData(userId, info,jqlService);
		//以下是查出流程中未签批的人员
		List<WorkFlowPicBean> flowpiclist=(List<WorkFlowPicBean>)hash.get("flowpiclist");
		if (ismoble)//移动端需要的数据
		{
			result.put("flowpiclist",flowpiclist);
		}
		else
		{
			String flowpic=WorkFlowPic.getInstance().getnewApprovePic(userId, approveId,flowpiclist);
			result.put("flowpic",flowpic);
		}
		return result;
	}
	public HashMap getFlowPicData(Long userId, ApprovalInfo info,JQLServices jqlService)
	{
		HashMap hash=new HashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String sql="select u.realName,a from ApprovalTask as a,Users as u where a.modifier=u.id and a.approvalID=? and a.approvalStep>0 order by a.approvalStep ";
		List<Object[]> flowlist=(List<Object[]>)jqlService.findAllBySql(sql, info.getId());//存放所有历史记录信息
		ArrayList<Long> userlist = info.getUserlist();
		List<WorkFlowPicBean> flowpiclist=new ArrayList<WorkFlowPicBean>();
		//需要对flowlist进行改造,将会签的数据放在一个数组中
		List<String> auditlist=new ArrayList<String>();
		
		if (flowlist!=null && flowlist.size()>0)
		{
			int fsize=flowlist.size();
			boolean currentSign=false;
			for (int n=1;n<(info.getApprovalStep()+1);n++)
			{
				WorkFlowPicBean picbean=new WorkFlowPicBean();
				Long approvalID;//流程编号,可以为空
				List<String> actions=new ArrayList<String>();//当前历史状态，为了显示流程节点名称用的
				List<String> nodeValues=new ArrayList<String>();//签批人
				List<String> times=new ArrayList<String>();//签批时间
				List<String> signtimes=new ArrayList<String>();//签收时间
				List<Integer> states=new ArrayList<Integer>();//状态，1表示待签，2表示已签
				
				Integer nodetype=0;//节点类型,0表示串行，1表示会签
				
				for (int i=0;i<fsize;i++)
				{
					Object[] objs=flowlist.get(i);
					ApprovalTask task=(ApprovalTask)objs[1];
					if (task.getApprovalStep()==n 
					//&& (n==1 || task.getAction().longValue()!=ApproveConstants.NEW_ACTION_SEND)	
					)
					{
						actions.add(String.valueOf(task.getAction()));
						nodeValues.add(String.valueOf(objs[0]));
						times.add(sdf.format(task.getModifytime()));
						if (task.getSigntagdate()!=null)
						{
							signtimes.add(sdf.format(task.getSigntagdate()));
						}
						else
						{
							signtimes.add("");
						}
						states.add(task.getStateid().intValue());
						
					}
				}
				if (nodeValues.size()>0)
				{
					picbean.setActions(actions.toArray(new String[actions.size()]));
					picbean.setNodeValues(nodeValues.toArray(new String[nodeValues.size()]));
					picbean.setTimes(times.toArray(new String[times.size()]));
					picbean.setSigntagdate(signtimes.toArray(new String[signtimes.size()]));
					picbean.setStates(states.toArray(new Integer[states.size()]));
					flowpiclist.add(picbean);
				}
			}
			sql="select count(a) from SameSignInfo as a where a.approvalID=? and a.isnew=0 ";
			Long signnum=(Long)jqlService.getCount(sql, info.getId());
			if (signnum!=null && signnum.longValue()>0l)
			{
				Object[] lastobjs=flowlist.get(fsize-1);
				ApprovalTask lasttask=(ApprovalTask)lastobjs[1];//最后一条签批信息
				sql="select a from SameSignInfo as a where a.taskid=? ";
				List<SameSignInfo> samelist = (List<SameSignInfo>)jqlService.findAllBySql(sql, lasttask.getId());
				if (samelist!=null && samelist.size()>0)
				{
					boolean isStartsign=false;//是否开始签标记
					List<String> actions=new ArrayList<String>();//当前历史状态，为了显示流程节点名称用的
					List<String> nodeValues=new ArrayList<String>();//签批人
					List<String> times=new ArrayList<String>();//签批时间
					List<String> signtimes=new ArrayList<String>();//签收时间
					List<Integer> states=new ArrayList<Integer>();//状态，1表示待签，2表示已签
					for (int i=0;i<samelist.size();i++)
					{
						SameSignInfo sameSignInfo=samelist.get(i);
						actions.add(String.valueOf(sameSignInfo.getActionid()));
						nodeValues.add(sameSignInfo.getSigner().getRealName());
						if (sameSignInfo.getApprovalDate()!=null)
						{
							times.add(sdf.format(sameSignInfo.getApprovalDate()));
						}
						else
						{
							times.add("");
						}
						if (sameSignInfo.getSigntagdate()!=null)
						{
							signtimes.add(sdf.format(sameSignInfo.getSigntagdate()));
						}
						else
						{
							signtimes.add("");
						}
						if (sameSignInfo.getSigner().getId()==lasttask.getModifier())
						{
							isStartsign=true;
						}
						states.add(sameSignInfo.getState());
					}
					WorkFlowPicBean picbean=new WorkFlowPicBean();
					picbean.setActions(actions.toArray(new String[actions.size()]));
					picbean.setNodeValues(nodeValues.toArray(new String[nodeValues.size()]));
					picbean.setTimes(times.toArray(new String[times.size()]));
					picbean.setSigntagdate(signtimes.toArray(new String[signtimes.size()]));
					picbean.setStates(states.toArray(new Integer[states.size()]));
					if (isStartsign)
					{
						flowpiclist.remove(flowpiclist.size()-1);//如果已经有人签了，就是替换，否则为增加一个节点
						flowpiclist.add(picbean);
					}
					else
					{
						flowpiclist.add(picbean);
					}
				}
			}
			if (userlist!=null && userlist.size()>0)
			{
				UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
				for (int n=0;n<userlist.size();n++)
				{
					Long userid=userlist.get(n);
					WorkFlowPicBean picbean=new WorkFlowPicBean();
					picbean.setActions(new String[]{"-1"});
					picbean.setNodeValues(new String[]{userService.getUser(userid).getRealName()});
					picbean.setTimes(new String[]{""});
					picbean.setSigntagdate(new String[]{""});
					picbean.setStates(new Integer[]{-1});
					flowpiclist.add(picbean);
				}
			}
		}
		hash.put("flowpiclist", flowpiclist);//存放已签或会签的流程信息
//		hash.put("auditlist", auditlist);//存放未签的信息
		return hash;
	}
	/**
	 * 反悔处理
	 * @param ids
	 * @param seltype
	 * @param user
	 * @return
	 */
	public String undoSign(Long id,String seltype,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			
			if ("done".equals(seltype))//已送中的反悔
			{
				//判断有没有人处理过或查看过
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);
				if (approvalInfo.getStatus()==ApproveConstants.NEW_STATUS_START
				||approvalInfo.getStatus()==ApproveConstants.NEW_STATUS_WAIT
				)
				{
					String sql="select a from ApprovalTask as a where a.approvalID=? order by a.id desc ";
					List<ApprovalTask> tasklist=(List<ApprovalTask>)jqlService.findAllBySql(sql, id);
					if (tasklist!=null && tasklist.size()>0 && tasklist.get(0).getModifier().longValue()!=user.getId().longValue())//判断是否最后处理人，否则不能反悔
					{
						//这里已经将协作当掉了
						UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
						return "对不起，已经被"+userService.getUser(tasklist.get(0).getModifier()).getRealName()+"处理过了，您不能反悔。";
					}
					
					if (tasklist!=null && tasklist.size()>1)
					{
						ApprovalTask nexttask=tasklist.get(1);
						sql="select a from SameSignInfo as a where a.taskid=? ";
						List<SameSignInfo> slist=(List<SameSignInfo>)jqlService.findAllBySql(sql, tasklist.get(0).getId());
						if (slist!=null && slist.size()>0)//当前是送审
						{
							//如果是会签反悔就不要减1
							sql="select a from SameSignInfo as a where a.approvalID=? order by a.sid DESC ";
							List<SameSignInfo> samelist=(List<SameSignInfo>)jqlService.findAllBySql(sql, id);
							if (samelist!=null && samelist.size()>1)
							{
								SameSignInfo same=samelist.get(1);
								if (same.getNodetype().intValue()==1)//会签反悔
								{
									
								}
								else
								{
									approvalInfo.setApprovalStep(approvalInfo.getApprovalStep()-1);//签批步骤要减1
								}
							}
							else
							{
								approvalInfo.setApprovalStep(approvalInfo.getApprovalStep()-1);//签批步骤要减1
							}
							approvalInfo.setComment(nexttask.getComment());
							for (int i=0;i<slist.size();i++)//删除table中的信息
							{
								sql="delete from ApprovalSignRead where sameid=? ";
								jqlService.excute(sql, slist.get(i).getSid());//删除处理信息
							}
							sql="delete from SameSignInfo where taskid=? ";
							jqlService.excute(sql, tasklist.get(0).getId());//删除刚送的信息
							sql="delete from ApprovalSignRead where readid in (select a.id from ApprovalReader as a where a.taskid="+tasklist.get(0).getId()+")";
							jqlService.excuteNativeSQL(sql);
							jqlService.excute("delete from ApprovalReader as a where a.taskid=?",tasklist.get(0).getId());
							sql="update SameSignInfo set isnew=0 where approvalID=? and isnew=1 ";
							jqlService.excute(sql, id);//更新原来的信息
							sql="update SameSignInfo set state="+ApproveConstants.NEW_STATUS_WAIT+" where approvalID=? and isnew=0 and signer.id=?";
							jqlService.excute(sql, id,user.getId());//更新原来的信息
							sql="select a from SameSignInfo as a where a.state="+ApproveConstants.NEW_STATUS_WAIT+" and a.approvalID=? and a.isnew=0 and a.signer.id=? ";
							List<SameSignInfo> templist=(List<SameSignInfo>)jqlService.findAllBySql(sql, id,user.getId());
							if (templist!=null && templist.size()>0)
							{
								SameSignInfo tempsame=templist.get(0);
								tempsame.setCurrentsign(1);
								updateSignRead(approvalInfo,tempsame,null,null);//同样要更新状态
							}
						}
						else
						{
							//如果会签就要更改SameSignInfo的状态
							sql="select a from SameSignInfo as a where a.approvalID=? and a.state="+ApproveConstants.NEW_STATUS_HAD+" and a.signer.id=? order by a.sid DESC";
							List<SameSignInfo> samelist=(List<SameSignInfo>)jqlService.findAllBySql(sql, id,user.getId());
							if (samelist!=null && samelist.size()>0)
							{
								SameSignInfo same=samelist.get(0);
								same.setIsnew(0l);
								same.setState(ApproveConstants.NEW_STATUS_WAIT);
								jqlService.update(same);
								
								updateSignRead(approvalInfo,same,null,null);//同样要更新状态
							}
							approvalInfo.setComment(nexttask.getComment());
						}
						sql="delete from ApprovalSignRead where readid in (select b.id from ApprovalReader as b where b.taskid="+tasklist.get(0).getId()+") ";
						jqlService.excuteNativeSQL(sql);//删除table中的信息
						sql="delete from ApprovalSignRead where readid in (select a.id from ApprovalReader as a where a.taskid="+tasklist.get(0).getId()+")";
						jqlService.excuteNativeSQL(sql);
						sql="delete from ApprovalReader as a where a.taskid=? ";
						jqlService.excute(sql,tasklist.get(0).getId());
						
//						approvalInfo.setStatus(ApproveConstants.NEW_STATUS_HAD);
						approvalInfo.setModifytime(nexttask.getModifytime());
						approvalInfo.setModifier(nexttask.getModifier());
						jqlService.update(approvalInfo);
						sql="delete from ApprovalTaskFiles as a where a.taskid=? ";//删除签批的附件
						jqlService.excute(sql,tasklist.get(0).getId());
						jqlService.deleteEntityByID(ApprovalTask.class, "id", tasklist.get(0).getId());//去除送签历史
						
					}
					else
					{
						//直接删除送签和协作
						sql="delete from ApprovalInfo as a where a.id=? ";
						jqlService.excute(sql,id);
						
						sql="delete from ApprovalTask as a where a.approvalID=? ";
						jqlService.excute(sql,id);
						
						sql="delete from ApprovalCooper as a where a.approvalID=? ";
						jqlService.excute(sql,id);
						
						sql="delete from ApprovalFiles as a where a.approvalid=? ";
						jqlService.excute(sql,id);
						
						sql="delete from ApprovalReader as a where a.approvalInfoId=? ";
						jqlService.excute(sql,id);
						
						sql="delete from SameSignInfo as a where a.approvalID=? ";
						jqlService.excute(sql,id);
						
						sql="delete from ApprovalTaskFiles as a where a.approvalid=? ";
						jqlService.excute(sql,id);
						
						sql="delete from ApprovalSignRead as a where a.approvalid=? ";//删除table中的信息
						jqlService.excute(sql,id);
					}
				}
				else
				{
					return "对不起，该事务已经被处理过，您不能反悔。";
				}
			}
			else if ("filed".equals(seltype))//办结中的反悔
			{
				//已办状态，看看后续有没有处理过，否则就不能UNDO
				String sql="select a from SameSignInfo as a where a.state!="
					+ApproveConstants.NEW_STATUS_WAIT+" and a.isnew=0  and a.approvalID=?" 
					+" order by a.approvalDate desc ";//已经有人签批过了
				List<SameSignInfo> list=(List<SameSignInfo>)jqlService.findAllBySql(sql,id);
				if (list!=null && list.size()>0)
				{
					if (user.getId().longValue()!=list.get(0).getSigner().getId().longValue())
					{
						return "对不起，已经被"+list.get(0).getSigner().getRealName()+"签过了，您不能反悔。";
					}
				}
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);
				if (approvalInfo.getStatus()==ApproveConstants.NEW_STATUS_END || approvalInfo.getStatus()==ApproveConstants.NEW_STATUS_HAD)
				{
					//如果已办或已终止，就要复原
				}
				
				sql="select a from ApprovalTask as a where a.approvalID=? order by a.id desc ";
				List<ApprovalTask> tasklist=(List<ApprovalTask>)jqlService.findAllBySql(sql, id);
				if (tasklist!=null && tasklist.size()>0)
				{
					ApprovalTask lasttask=tasklist.get(0);
					if (user.getId().longValue()==lasttask.getModifier().longValue())//只有最后一条记录是自己的才能反悔，否则提示
					{
						if (lasttask.getAction().longValue()==ApproveConstants.NEW_ACTION_SIGN) //签批
						{
							approvalInfo.setStatus(ApproveConstants.NEW_STATUS_WAIT);
							//删除送的下一个(几个)人
							sql="select count(a) from SameSignInfo as a  where a.taskid=? ";
							Long count=(Long)jqlService.getCount(sql,tasklist.get(0).getId());
							if (count!=null && count.longValue()>0)//只有再次送审了才要将签批步骤减1
							{
								approvalInfo.setApprovalStep(approvalInfo.getApprovalStep()-1);
								
							}
							else //要考虑直接返还给送文人的情况
							{
								sql="select count(a) from SameSignInfo as a  where a.approvalID=? and a.state="+ApproveConstants.NEW_STATUS_WAIT;
								count=(Long)jqlService.getCount(sql,approvalInfo.getId());
								if (count==0)//没有待签的，说明是返还给了送文人，需要将操作步骤减1，这些都是为历史记录的流程图服务的
								{
									approvalInfo.setApprovalStep(approvalInfo.getApprovalStep()-1);
								}
							}
							if (tasklist.size()>1)//还原备注
							{
								approvalInfo.setModifier(tasklist.get(1).getModifier());
								approvalInfo.setModifytime(tasklist.get(1).getModifytime());
								approvalInfo.setComment(tasklist.get(1).getComment());
							}
							jqlService.update(approvalInfo);//更新流程表
							
							
							jqlService.excute("delete from SameSignInfo as a where a.taskid=?",tasklist.get(0).getId());
							jqlService.excute("delete from ApprovalReader as a where a.taskid=?",tasklist.get(0).getId());
							
							sql="select a from SameSignInfo as a where signer.id=? and approvalID=? order by a.id desc ";
							List<SameSignInfo> samelist=(List<SameSignInfo>)jqlService.findAllBySql(sql, user.getId(),id);
							if (samelist!=null && samelist.size()>0)
							{
								SameSignInfo sameSignInfo=samelist.get(0);
								sameSignInfo.setState(ApproveConstants.NEW_STATUS_WAIT);
								sameSignInfo.setApprovalDate(new Date());
								sameSignInfo.setIsnew(0l);
								jqlService.update(sameSignInfo);//还原签批状态
							}
						}
						else if (lasttask.getAction().longValue()==ApproveConstants.NEW_ACTION_SIGNREAD) //批阅
						{
							//直接更新批阅表就可以了
							sql="select a from ApprovalReader as a where a.approvalInfoId=? and a.readUser=? order by a.id desc ";
							List<ApprovalReader> readlist=(List<ApprovalReader>)jqlService.findAllBySql(sql,id, user.getId());
							if (readlist!=null && readlist.size()>0)
							{
								ApprovalReader approvalReader=readlist.get(0);
								approvalReader.setState(ApproveConstants.NEW_STATUS_START);
								jqlService.update(approvalReader);
							}
						}
						else if (lasttask.getAction().longValue()==ApproveConstants.NEW_ACTION_HADCOOPER) //协作
						{
							//直接更新协作表就可以了
							sql="select a from ApprovalCooper as a where a.approvalID=? and a.cooper.id=? order by a.id desc ";
							List<ApprovalCooper> cooplist=(List<ApprovalCooper>)jqlService.findAllBySql(sql,id, user.getId());
							if (cooplist!=null && cooplist.size()>0)
							{
								ApprovalCooper approvalCooper=cooplist.get(0);
								approvalCooper.setState(ApproveConstants.NEW_STATUS_START);
								approvalCooper.setActionid((int)ApproveConstants.NEW_ACTION_COOPER);
								jqlService.update(approvalCooper);
								sql="delete from ApprovalCooperFiles where coopid=? ";
								jqlService.excute(sql, approvalCooper.getId());//删除协作对应的上传文档
							}
						}
						else if (lasttask.getAction().longValue()==ApproveConstants.NEW_ACTION_HADSUCCESS) //成文
						{
							//直接改状态
//							approvalInfo.setStatus(ApproveConstants.NEW_STATUS_HAD);//改成已办,已办才能成文，要独立出去
							jqlService.update(approvalInfo);//更新流程表
						}
						else if (lasttask.getAction().longValue()==ApproveConstants.NEW_ACTION_HADACTIVE) //归档
						{
							//直接改状态,暂没有处理
						}
						jqlService.deleteEntityByID(ApprovalTask.class, "id", tasklist.get(0).getId());//去除签批历史
						
					}
					else
					{
						UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
						return "对不起，已经被"+userService.getUser(lasttask.getModifier()).getRealName()+"处理过了，您不能反悔。";
					}
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
	public boolean collectSign(Long id,String seltype,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			
//			if ("done".equals(seltype))//不需要判断是已处理还是已阅
			{
				//判断有没有人处理过或查看过
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);
				Long count=(Long)jqlService.getCount("select count(a) from ApprovalCollect as a where a.collecter.id=? and a.appinfo.id=?", user.getId(),approvalInfo.getId());
				if (count==null || count.longValue()==0)
				{
					ApprovalCollect collect=new ApprovalCollect();
					collect.setCollecter(user);
					collect.setAppinfo(approvalInfo);
					jqlService.save(collect);
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
	public String getWebcontent(Long id,int type)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (type==0)
			{
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);
				if (approvalInfo.getWebcontent()!=null && approvalInfo.getWebcontent().length()>0)
				{
					return approvalInfo.getWebcontent();
				}
			}
			else
			{
				ApprovalSave approvalSave=(ApprovalSave)jqlService.getEntity(ApprovalSave.class, id);
				if (approvalSave.getWebcontent()!=null && approvalSave.getWebcontent().length()>0)
				{
					return approvalSave.getWebcontent();
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
	 * 根据ID获取当前节点的情况（点击已送中的在办）
	 * @param id
	 * @param user
	 * @param type 节点类别，1表示签批，0表示协作
	 * @return
	 */
	public List getWaitDetail(Long id,Integer type,Users user)
	{
		List back=new ArrayList();//数组中嵌套数组
		
		//主键，处理者,类型,状态,备注,催办
		//[[1,'xxx1','送审','待签','ddddddd','催办'],[2,'xxx2','送审','已签','ddddddd','催办'],[3,'xxx3','送审','待签','ddddddd','催办'],[0,'xxx4','传阅','已签','ddddddd',''],[0,'xxx5','传阅','待签','ddddddd','']]
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (type!=null && type==1)
			{
				String sql = "select a from SameSignInfo as a where a.isnew=0 and a.approvalID=? ";//获取当前节点的签批信息
				List<SameSignInfo> samelist=(List<SameSignInfo>)jqlService.findAllBySql(sql, id);
				if (samelist!=null)
				{
					
					for (int i=0;i<samelist.size();i++)
					{
						SameSignInfo sameSignInfo=samelist.get(i);
						
						String statename="已办";
						if (sameSignInfo.getState()!=null && sameSignInfo.getState()==ApproveConstants.NEW_STATUS_WAIT)
						{
							statename="待办";
						}
						String[] values=new String[]{"s"+sameSignInfo.getSid().longValue()
							,sameSignInfo.getSigner().getRealName()
							,"送签"
							,statename
							,sameSignInfo.getComment()
							,"催办"
						};
						back.add(values);
					}
				}
				sql = "select a from ApprovalReader as a where a.isnew=0 and a.approvalInfoId=? ";//获取当前节点批阅信息
				List<ApprovalReader> readlist=(List<ApprovalReader>)jqlService.findAllBySql(sql, id);
				if (readlist!=null)
				{
					UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
					for (int i=0;i<readlist.size();i++)
					{
						ApprovalReader approvalReader=readlist.get(i);
						String statename="已阅";
						if (approvalReader.getState()==ApproveConstants.NEW_STATUS_START)
						{
							statename="待阅";
						}
						
						String[] values=new String[]{"p"+approvalReader.getId().longValue()
							,userService.getUser(approvalReader.getReadUser()).getRealName()
							,"批阅"
							,statename
							,approvalReader.getComment()
							,""
						};
						back.add(values);
					}
				}
			}
			else //协作处理
			{
				//处理者,状态,附件,备注,催办
				String sql = "select a from ApprovalCooper as a where a.isnew=0 and a.approvalID=? ";//获取当前节点的协作信息
				List<ApprovalCooper> cooplist=(List<ApprovalCooper>)jqlService.findAllBySql(sql, id);
				if (cooplist!=null)
				{
					
					for (int i=0;i<cooplist.size();i++)
					{
						ApprovalCooper approvalCooper=cooplist.get(i);
						
						String statename="已阅";
						String modify="";
						String files="";//附件地址,用,间隔
						String filesname="";//附件名称列表，用,间隔
						if (approvalCooper.getState()==ApproveConstants.NEW_STATUS_WAIT)
						{
							statename="待阅";
							modify="催办";
						}
						else
						{
							sql = "select a from ApprovalCooperFiles as a where a.coopid=? ";//获取当前节点的协作附件
							List<ApprovalCooperFiles> coopfilelist=(List<ApprovalCooperFiles>)jqlService.findAllBySql(sql, approvalCooper.getId());
							if (coopfilelist!=null)
							{
								for (int j=0;j<coopfilelist.size();j++)
								{
									ApprovalCooperFiles approvalCooperFiles =coopfilelist.get(j);
									if (j==0)
									{
										files=approvalCooperFiles.getDocumentpath();
										filesname=approvalCooperFiles.getFileName();
									}
									else
									{
										files+=","+approvalCooperFiles.getDocumentpath();
										filesname+=","+approvalCooperFiles.getFileName();
									}
								}
							}
						}
						String[] values=new String[]{"r"+approvalCooper.getId().longValue()
							,approvalCooper.getCooper().getRealName()
							,statename
							,files
							,filesname
							,approvalCooper.getComment()
							,modify
						};
						back.add(values);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return back;
	}
	/**
	 * 根据ID进行发送消息提醒
	 * @param id 编号,s开头的是签批，p开头的批阅，r开头的协作
	 * @param type
	 * @param worktype//催办标记
	 * @param comment
	 * @param user
	 * @return
	 */
	public boolean setWarnMessage(String tid,String comment,Users user,Integer worktypes)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (tid!=null && tid.length()>0)
			{
				String type=tid.substring(0,1);
				String id=tid.substring(1);
				String title="";
				if ("s".equals(type))//签批提醒
				{
					title=user.getRealName()+"催促您签批";
					MessageUtil.instance().setSignWarn(Long.valueOf(id), MessageCons.SIGN, null, comment, title, user,worktypes);
					SameSignInfo same=(SameSignInfo)jqlService.getEntity(SameSignInfo.class, Long.valueOf(id));
					
					List<String> mobilelist=new ArrayList<String>();
					List<Long> tranids=new ArrayList<Long>();
					String content="";
			        if (same.getSigner().getMobile()!=null && same.getSigner().getMobile().length()==11)
			        {
			        	mobilelist.add(same.getSigner().getMobile());
						tranids.add(same.getSid());
						ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, same.getApprovalID());
						content=user.getRealName()+"请您尽快对“"+approvalInfo.getTitle()+"”进行签批";//暂不做短信回复功能
			        }
			        sendMobileinfo(mobilelist,content,Constant.MOBILESIGN,tranids,false,user);//催办短信提醒
				}
				else if ("p".equals(type))//批阅提醒
				{
					title=user.getRealName()+"催促您批阅";
					MessageUtil.instance().setSignWarn(Long.valueOf(id), MessageCons.SIGNREAD, null, comment, title, user,worktypes);
				}
				else if ("r".equals(type))//协作提醒
				{
					title=user.getRealName()+"催促您协作";
					MessageUtil.instance().setSignWarn(Long.valueOf(id), MessageCons.COOPER, null, comment, title, user,worktypes);
				}
				else if ("f".equals(type))//送签提醒
				{
					title=user.getRealName()+"请您帮助签批";
					MessageUtil.instance().setSignWarn(Long.valueOf(id), MessageCons.SENDSIGN, null, comment, title, user,worktypes);
				}
				else if ("y".equals(type))//批阅提醒
				{
					title=user.getRealName()+"请您帮助批阅";
					MessageUtil.instance().setSignWarn(Long.valueOf(id), MessageCons.SENDSIGNREAD, null, comment, title, user,worktypes);
				}
				else if ("x".equals(type))//协作提醒
				{
					title=user.getRealName()+"请您帮助文档协作";
					MessageUtil.instance().setSignWarn(Long.valueOf(id), MessageCons.SENDCOOPER, null, comment, title, user,worktypes);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 去除催办
	 * @param id
	 * @param user
	 * @return
	 */
	public boolean delMessage(Long id,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			MessageUtil.instance().hiddenMessage(new Long[]{id},1,null);//清除消息
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	public boolean delMessage(Long[] id,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			MessageUtil.instance().hiddenMessage(id,1,null);//清除消息
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	public boolean delAllMessage(Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			MessageUtil.instance().hiddenAllMessage(user,1,null);//清除所有消息
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 获取提示信息的数量
	 * @param user
	 * @return
	 */
	public Long getNewMessageNums(Users user)
	{
		return MessageUtil.instance().getNewMessageNums(user);
	}
	/**
	 * 获取具体的提示信息
	 * @param user
	 * @return
	 */
	public List getNewListMessages(Users user)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List backlist = new ArrayList();
		//var hash=["shared","send","todo","todo","todo"];
		//{type:2,sender:"王倚新",face:"",commet:"xxx备注22xxxx",time:"2012-12-21 10:02",title:"XXXXXXXX"},
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			List<Messages> list=MessageUtil.instance().getNewListMessages(user);
			if (list !=null && list.size()>0)
			{
				for (int i=0;i<list.size();i++)
				{
					Messages messages=list.get(i);
					Map<String,Object> map=new HashMap<String,Object>();
					map.put("id",messages.getId());//主键
					map.put("worktype", messages.getWorktype());
					if (messages.getType().intValue()==MessageCons.SIGN
							|| messages.getType().intValue()==MessageCons.SIGNREAD
							||messages.getType().intValue()==MessageCons.COOPER)
					{
						if (messages.getWorktype()==null || messages.getWorktype().intValue()==1)
						{
							map.put("type", 2);//类型
							map.put("typename", "签批催办");//类型
						}
						else
						{
							if (messages.getType().intValue()==MessageCons.SIGNREAD)
							{
								map.put("type", 10);//类型
								map.put("typename", "批阅提醒");//类型
							}
							else
							{
								map.put("type", 7);//类型
								map.put("typename", "签批提醒");//类型
							}
							
						}
					}
					else if (messages.getType().intValue()==MessageCons.SENDSIGN
							|| messages.getType().intValue()==MessageCons.SENDSIGNREAD
							|| messages.getType().intValue()==MessageCons.SENDCOOPER
							)
					{
						map.put("type", 7);//类型
						map.put("typename", "签批提醒");//类型
					}
					else if (messages.getType().intValue()==MessageCons.SENDTRANS)
					{
						map.put("type", 5);//类型
						map.put("typename", "交办提醒");//类型
					}
					else if (messages.getType().intValue()==MessageCons.AUDIT)//审阅类别
					{
						map.put("type", 11);//类型
						map.put("typename", "审阅提醒");//类型
					}
					else if (messages.getType().intValue()==MessageCons.SENDMEET)
					{
						if (messages.getWorktype()==null || messages.getWorktype().intValue()==0)
						{
							map.put("type", 6);//类型
							map.put("typename", "会议提醒");//类型
						}
						else
						{
							map.put("type", 9);//类型
							map.put("typename", "会议催办");//类型
						}
					}
					else
					{
						map.put("type", 0);//类型
						map.put("typename", "共享文件");//类型
					}
					map.put("sender", messages.getUser().getRealName());//送文者
					map.put("face", messages.getUser().getImage());//头像
					map.put("facepath", WebConfig.userPortrait);//头像路径
					map.put("commet", messages.getContent());//备注
					map.put("time", sdf.format(messages.getDate()));//时间
					map.put("title", messages.getTitle());//主题
					backlist.add(map);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return backlist;
	}
	/**
	 * 流程签收
	 * @param id
	 * @param user
	 * @return
	 */
	public boolean signreal(String id,Users user) 
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (id!=null && id.length()>0 && !id.toLowerCase().equals("null"))
			{
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, Long.valueOf(id));
				List list=jqlService.findAllBySql("select a from SameSignInfo as a where a.approvalID=? and a.signer.id=? and a.isnew=0 ", Long.valueOf(id),user.getId());
				if (list!=null && list.size()>0)
				{
					SameSignInfo same=(SameSignInfo)list.get(0);
					same.setSigntag("Y");
					same.setSigntagdate(new Date());
					same.setCurrentsign(1);
					jqlService.update(same);
					jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.sameid=?",new Date(),same.getSid());
					updateSignRead(approvalInfo,same,null,null);
				}
				list=jqlService.findAllBySql("select a from ApprovalReader as a where a.approvalInfoId=? and a.readUser=? and a.isnew=0 ", Long.valueOf(id),user.getId());
				if (list !=null && list.size()>0)
				{
					ApprovalReader read=(ApprovalReader)list.get(0);
					read.setSigntag("Y");
					read.setSigntagdate(new Date());
					jqlService.update(read);
					
					jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.readid=?",new Date(),read.getId());
					updateSignRead(approvalInfo,null,read,null);
				}
				
				list=jqlService.findAllBySql("select a from ApprovalCooper as a where a.approvalID=? and a.cooper.id=? and a.isnew=0 ", Long.valueOf(id),user.getId());
				if (list !=null && list.size()>0)
				{
					ApprovalCooper coop=(ApprovalCooper)list.get(0);
					coop.setSigntag("Y");
					coop.setSigntagdate(new Date());
					jqlService.update(coop);
					
					jqlService.excute("update Messages as a set a.state=1,a.modifydate=? where a.coopid=?",new Date(),coop.getId());
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
	public boolean getSignReal(String id,Users user) 
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (id!=null && id.length()>0 && !id.toLowerCase().equals("null"))
			{
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, Long.valueOf(id));
				List list=jqlService.findAllBySql("select a from SameSignInfo as a where a.approvalID=? and a.signer.id=? and a.isnew=0 and a.signtag='Y' ", Long.valueOf(id),user.getId());
				if (list!=null && list.size()>0)
				{
					return true;
				}
				list=jqlService.findAllBySql("select a from ApprovalReader as a where a.approvalInfoId=? and a.readUser=? and a.isnew=0 and a.signtag='Y' ", Long.valueOf(id),user.getId());
				if (list !=null && list.size()>0)
				{
					return true;
				}
				
				list=jqlService.findAllBySql("select a from ApprovalCooper as a where a.approvalID=? and a.cooper.id=? and a.isnew=0 and a.signtag='Y' ", Long.valueOf(id),user.getId());
				if (list !=null && list.size()>0)
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
	/**
	 * 根据历史编号获取相应的文件
	 * @param historyid
	 * @return
	 */
	public List getHistoryFiles(Long historyid)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			List<ApprovalTaskFiles> filelist = (List<ApprovalTaskFiles>)jqlService.findAllBySql("select a from ApprovalTaskFiles as a where a.taskid=? ", historyid);
			List<String[]> backlist=new ArrayList<String[]>();
			if (filelist!=null && filelist.size()>0)
			{
				for (int i=0;i<filelist.size();i++)
				{
					ApprovalTaskFiles taskfile=filelist.get(i);
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
	
	public String copyHistoryFiles(String targetPath,String[][] filepaths,Users user)
	{
		//判断targetPath目录有没有权限
		if (targetPath != null && targetPath.length() > 0 && filepaths!=null && filepaths.length>0)
		{
			String[] srcpaths=new String[filepaths.length];
			String[] srcname=new String[filepaths.length];
			for (int i=0;i<filepaths.length;i++)
			{
				srcpaths[i]=filepaths[i][0];
				srcname[i]=filepaths[i][1];
			}
			PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
			//service.getFileSystemAction(userId, path, treeFlag);
			Long permit = service.getFileSystemAction(user.getId(), targetPath, true);
			//long pd = FileSystemCons.COPY_PASTE_FLAG;
			long pd = FileSystemCons.WRITE_SET;
			boolean flag = permit == null || permit == 0 ? false: FlagUtility.isValue(permit, pd);

			if (flag)
			{
				final DataHolder srcPathHolder = new DataHolder();
				
				srcPathHolder.setStringData(srcpaths);
				srcPathHolder.setStringValue(srcname);
				
				// 先判断目标文件夹有没有权限，再进行复制或移动
				// 目标文件夹中有没有重名的
				FileSystemService fileService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
				Fileinfo[] fileinfos = fileService.getFileList(null,String.valueOf(user.getId()),
						targetPath);
				final List<String> exitesNames = new ArrayList<String>();//存放已经存在的文件名
				int size = fileinfos.length;
				if (size > 0)
				{
					final String[] newNames = new String[size];
					for (int i = 0; i < size; i++)
					{
						newNames[i] = fileinfos[i].getFileName();//
						exitesNames.add(newNames[i]);
					}
				}
				
				fileService.copyVersionFiles(user,	srcPathHolder, targetPath, 1,exitesNames);

			}
			else
			{
				return "对不起，目标文件夹没有权限写文件！";// 没有权限
			}
		}
		return "true";
	}
	//获取当前用户待办的数量
	public long getWaitwork(Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sqlcount="select count(distinct a.id) from ApprovalInfo a left join SameSignInfo b "
					+" on (a.id=b.approvalID and b.isnew=0 and b.islast=0 "
					+") "
					+" where a.status="+ApproveConstants.NEW_STATUS_WAIT+" and (a.modifytype=1 and b.isnew=0 and b.state="+ApproveConstants.NEW_STATUS_WAIT+" and b.signer_id="+user.getId()+")"
					;
			List<BigInteger> sizelist=(List<BigInteger>)jqlService.getObjectByNativeSQL(sqlcount,-1,-1);
			return sizelist.get(0).longValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	//获取当前用户待阅的数量
	public long getWaitread(Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sqlcount="select count(distinct a.id) from ApprovalInfo a "
					+" , ApprovalReader d "
					+" where a.id=d.approvalInfoId and (((d.isview is null or d.isview=0 )  and (a.modifytype=1 and d.islast=0 and d.readUser="+user.getId()+"))"
					+" or d.userId="+user.getId()+")";
			List<BigInteger> sizelist=(List<BigInteger>)jqlService.getObjectByNativeSQL(sqlcount,-1,-1);
			return sizelist.get(0).longValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	//获取当前用户待签收的数量
	public long getWaitsign(Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sqlcount="select count(distinct a.id) from ApprovalInfo a left join SameSignInfo b "
					+" on (a.id=b.approvalID and b.isnew=0 and b.islast=0 "
					+") "
					+" where a.status="+ApproveConstants.NEW_STATUS_WAIT+" and (a.modifytype=1 and b.isnew=0 and b.state="+ApproveConstants.NEW_STATUS_WAIT+" and b.signer_id="+user.getId()+")"
					+" and b.signtag is null "
					;
			List<BigInteger> sizelist=(List<BigInteger>)jqlService.getObjectByNativeSQL(sqlcount,-1,-1);
			return sizelist.get(0).longValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	//获取当前用户被催办的数量
	public long getHadcb(Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sqlcount="select count(distinct a.id) from ApprovalInfo a left join SameSignInfo b "
					+" on (a.id=b.approvalID and b.isnew=0 and b.islast=0 "
					+") "
					+" where a.status="+ApproveConstants.NEW_STATUS_WAIT+" and (a.modifytype=1 and b.isnew=0 and b.state="+ApproveConstants.NEW_STATUS_WAIT+" and b.signer_id="+user.getId()+")"
					+" and b.sid in (select d.sameid from Messages d where d.deleted=0 and d.msguser_id="+user.getId()+" )"
					;
			List<BigInteger> sizelist=(List<BigInteger>)jqlService.getObjectByNativeSQL(sqlcount,-1,-1);
			return sizelist.get(0).longValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	public List<String[]> modifyFromunit(String fromunit,String type,Users user)
	{//来文单位 字典操作
		List<String[]> backlist=new ArrayList<String[]>();
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			List<UsersOrganizations> orglist=(List<UsersOrganizations>)jqlService.findAllBySql("select a from UsersOrganizations as a where a.user.id=?", user.getId());
			Long orgid=null;
			if (orglist!=null && orglist.size()>0)
			{
				UsersOrganizations userorg=orglist.get(0);
				String key=userorg.getOrganization().getParentKey();
				if (key!=null && key.length()>0)
				{
					int index=key.indexOf("-");
					try
					{
						Long id=Long.valueOf(key.substring(0,index));
						Organizations org=(Organizations)jqlService.getEntity(Organizations.class, id);
						orgid=org.getId();
						
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					orgid=userorg.getOrganization().getId();
				}
			}
			if ("del".equals(type))//删除来文单位
			{
				if (WebConfig.cloudPro)//公云
				{
					jqlService.excute("delete from ApprovalDic where name=? and type=?  and a.company.id=?", fromunit,ApproveConstants.APPROVAL_FROMUNIT,user.getCompany().getId());
				}
				else
				{
					jqlService.excute("delete from ApprovalDic where name=? and type=?  and a.orgid=?", fromunit,ApproveConstants.APPROVAL_FROMUNIT,orgid);
				}
			}
			else if ("add".equals(type))//增加来文单位
			{
				Long count=1L;
				if (WebConfig.cloudPro)//公云
				{
					count=(Long)jqlService.getCount("select count(a) from ApprovalDic as a where a.type=? and a.name=? and a.company.id=?", ApproveConstants.APPROVAL_FROMUNIT,fromunit,user.getCompany().getId());
				}
				else
				{
					count=(Long)jqlService.getCount("select count(a) from ApprovalDic as a where a.type=? and a.name=? and a.orgid=?", ApproveConstants.APPROVAL_FROMUNIT,fromunit,orgid);
				}
				if (count==null || count.longValue()==0)
				{
					ApprovalDic dic=new ApprovalDic();
					dic.setName(fromunit);
					dic.setType(ApproveConstants.APPROVAL_FROMUNIT);
					dic.setUser(user);
					dic.setOrgid(orgid);
					dic.setCompany(user.getCompany());
					jqlService.save(dic);
				}
			}
			//获取来文单位的json格式数据
			List<ApprovalDic> list=null;
			if (WebConfig.cloudPro)//公云
			{
				list=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.company.id=? order by a.name", ApproveConstants.APPROVAL_FROMUNIT,user.getCompany().getId());
			}
			else
			{
				list=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=?  and a.orgid=? order by a.name", ApproveConstants.APPROVAL_FROMUNIT,orgid);
			}
			if (list!=null)
			{
				for (ApprovalDic dic:list)
				{
					String[] temp=new String[]{dic.getName()};
					backlist.add(temp);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return backlist;
	}
	private Long getRootOrgid(JQLServices jqlService,Long userid)
	{
		List<UsersOrganizations> orglist=(List<UsersOrganizations>)jqlService.findAllBySql("select a from UsersOrganizations as a where a.user.id=?", userid);
		Long orgid=null;
		if (orglist!=null && orglist.size()>0)
		{
			UsersOrganizations userorg=orglist.get(0);
			String key=userorg.getOrganization().getParentKey();
			if (key!=null && key.length()>0)
			{
				int index=key.indexOf("-");
				try
				{
					Long id=Long.valueOf(key.substring(0,index));
					Organizations org=(Organizations)jqlService.getEntity(Organizations.class, id);
					orgid=org.getId();
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				orgid=userorg.getOrganization().getId();
			}
		}
		return orgid;
	}
	public List<String[]> getFiletypes(String modifytype,Users user)
	{
		//获取文件类型的数据
		List<String[]> backlist=new ArrayList<String[]>();
		try
		{
			List<ApprovalDic> list=null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			
//			//以下是以后为了指定图标用的，暂没有这样做
//			if (WebConfig.cloudPro)//公云
//			{
//				String sql="select a from ApprovalDic as a where a.type=? and a.company.id=?  order by a.name";
//				list=(List<ApprovalDic>)jqlService.findAllBySql(sql, ApproveConstants.APPROVAL_FILETYPE,user.getCompany().getId());
//			}
//			else
//			{
//				Long orgid=getRootOrgid(jqlService,user.getId());
//				list=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=?  and a.orgid=? order by a.name", ApproveConstants.APPROVAL_FILETYPE,orgid);
//			}
			
			//判断是否有数据，没有数据就不显示
			Long nullnum=(Long)jqlService.getCount("select count(a) from ApprovalInfo as a where a.filetype is null ");
			if (nullnum!=null && nullnum.longValue()>0)
			{
				jqlService.excute("update ApprovalInfo set filetype=? where filetype is null ","领导签批");//将原来老的数据增加文件类别
			}
			List<Object[]> mylist=null;
			if ("1".equals(modifytype)||"todo".equals(modifytype))//待办的文件类型
			{
				String sql="select a.filetype,count(a.id) from ApprovalInfo a left join SameSignInfo b "
					+" on (a.id=b.approvalID and b.isnew=0 and b.islast=0 "
					+")"
					+" ,users u "
					+" where a.userID=u.id and a.status="+ApproveConstants.NEW_STATUS_WAIT+" and ("
					+" (a.modifytype=1 and b.isnew=0 and b.state="+ApproveConstants.NEW_STATUS_WAIT+" and b.signer_id="+user.getId()+")"
					+")"
					+" group by a.filetype ";
				mylist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,-1,-1);
			}
			else if ("2".equals(modifytype)||"done".equals(modifytype))//已办的文件类型
			{
				String sql="select a.filetype,count(a.id) "
					+" from approvalinfo a ,samesigninfo b "
					+" ,users u "
					+" where a.id=b.approvalID and a.status!="+ApproveConstants.NEW_STATUS_DEL+" and (b.isview is null or b.isview=0 ) and b.state="+ApproveConstants.NEW_STATUS_HAD
					+" and a.userID=u.id "
					+" and (a.modifytype=1 and b.islast=0 and b.signer_id="+user.getId()+")"
					+" group by a.filetype ";
				mylist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,-1,-1);
			}
			else//所有文件类型
			{
				
			}
			if (mylist!=null)
			{
				for (int j=0;j<mylist.size();j++)
				{
					Object[] obj=mylist.get(j);
					String filetype=(String)obj[0];
					BigInteger num=(BigInteger)obj[1];
//					for (int i=0;i<list.size();i++)
//					{
//						ApprovalDic dic=list.get(i);
//						if (dic.getName().equals(filetype))
//						{
//							
//							break;
//						}
//					}
					backlist.add(new String[]{num.toString(),filetype,"filetype"+(j+1)});
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return backlist;
	}
	public List<String[]> modifyFiletype(String filetype,String type,Users user)
	{//文件类型 字典操作
		List<String[]> backlist=new ArrayList<String[]>();
		try
		{
			
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			Long orgid=getRootOrgid(jqlService,user.getId());
			if ("del".equals(type))//删除文件类型
			{
				if (WebConfig.cloudPro)//公云
				{
					jqlService.excute("delete from ApprovalDic where name=? and type=?  and a.company.id=? ", filetype,ApproveConstants.APPROVAL_FILETYPE,user.getCompany().getId());//单位公用
				}
				else
				{
					jqlService.excute("delete from ApprovalDic where name=? and type=?  and a.orgid=? ", filetype,ApproveConstants.APPROVAL_FILETYPE,orgid);//单位公用
				}
			}
			else if ("add".equals(type))//增加文件类型
			{
				Long count=1L;
				if (WebConfig.cloudPro)//公云
				{
					count=(Long)jqlService.getCount("select count(a) from ApprovalDic as a where a.type=? and a.name=? and a.company.id=?", ApproveConstants.APPROVAL_FILETYPE,filetype,user.getCompany().getId());
				}
				else
				{
					count=(Long)jqlService.getCount("select count(a) from ApprovalDic as a where a.type=?  and a.name=? and a.orgid=?", ApproveConstants.APPROVAL_FILETYPE,filetype,orgid);
				}
				if (count==null || count.longValue()==0)
				{
					ApprovalDic dic=new ApprovalDic();
					dic.setName(filetype);
					dic.setType(ApproveConstants.APPROVAL_FILETYPE);
					dic.setUser(user);
					dic.setCompany(user.getCompany());
					dic.setOrgid(orgid);
					jqlService.save(dic);
				}
			}
			//获取文件类型的数据
			List<ApprovalDic> list=null;
			if (WebConfig.cloudPro)//公云
			{
				list=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.company.id=?  order by a.name", ApproveConstants.APPROVAL_FILETYPE,user.getCompany().getId());
			}
			else
			{
				List<ApprovalDic> nulllist=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.orgid is null");
				if (nulllist!=null && nulllist.size()>0)//这里是处理历史数据，防止出现orgid为空的现象
				{
					for (int i=0;i<nulllist.size();i++)
					{
						ApprovalDic dic=nulllist.get(i);
						if (dic.getUser()!=null)
						{
							Long myorgid=getRootOrgid(jqlService,dic.getUser().getId());
							dic.setOrgid(myorgid);
							dic.setCompany(dic.getUser().getCompany());
							jqlService.update(dic);
						}
					}
				}
				list=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=?  and a.orgid=? order by a.name", ApproveConstants.APPROVAL_FILETYPE,orgid);
			}
			if (list!=null)
			{
				for (ApprovalDic dic:list)
				{
					String[] temp=new String[]{dic.getName()};
					backlist.add(temp);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return backlist;
	}
	public List<String[]> modifyScript(String script,String type,Users user)
	{//文件类型 用户处理备注
		List<String[]> backlist=new ArrayList<String[]>();
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			
			if ("del".equals(type))//删除用户备注
			{
				jqlService.excute("delete from ApprovalDic where name=? and type=? and user.id=?", script,ApproveConstants.APPROVAL_MODIFYSCRIPT,user.getId());
			}
			else if ("add".equals(type))//增加用户备注
			{
				Long count=(Long)jqlService.getCount("select count(a) from ApprovalDic as a where a.type=? and a.user.id=? and a.name=?", ApproveConstants.APPROVAL_MODIFYSCRIPT,user.getId(),script);
				if (count==null || count.longValue()==0)
				{
					ApprovalDic dic=new ApprovalDic();
					dic.setName(script);
					dic.setType(ApproveConstants.APPROVAL_MODIFYSCRIPT);
					dic.setUser(user);
					jqlService.save(dic);
				}
			}
			//获取用户备注的数据
			List<ApprovalDic> list=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.user.id=? order by a.name", ApproveConstants.APPROVAL_MODIFYSCRIPT,user.getId());
			
			if (list!=null)
			{
				for (ApprovalDic dic:list)
				{
					String[] temp=new String[]{dic.getName()};
					backlist.add(temp);
				}
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return backlist;
	}
	public Map<String, Object> getFiledetail(Long approvalid,Users user)
	{//文件详情
		Map<String, Object> map=new HashMap<String, Object>();
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approvalid);
			if (approvalInfo!=null)
			{
				if (approvalInfo.getFiletype()==null)
				{
					map.put("filetype", "");
				}
				else
				{
					map.put("filetype", approvalInfo.getFiletype());
				}
				if (approvalInfo.getFileflowid()==null)
				{
					map.put("fileflowid", "");
				}
				else
				{
					map.put("fileflowid", approvalInfo.getFileflowid());
				}
				if (approvalInfo.getFilesuccdate()==null)
				{
					map.put("filesuccdate", "");
				}
				else
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					map.put("filesuccdate", sdf.format(approvalInfo.getFilesuccdate()));
				}
				if (approvalInfo.getFromunit()==null)
				{
					map.put("fromunit", "");
				}
				else
				{
					map.put("fromunit", approvalInfo.getFromunit());
				}
				if (approvalInfo.getFilecode()==null)
				{
					map.put("filecode", "");
				}
				else
				{
					map.put("filecode", approvalInfo.getFilecode());
				}
				if (approvalInfo.getFilescript()==null)
				{
					map.put("filescript", "");
				}
				else
				{
					map.put("filescript", approvalInfo.getFilescript());
				}
				if (approvalInfo.getSendcomment()==null)
				{
					map.put("comment", "");
				}
				else
				{
					map.put("comment", approvalInfo.getSendcomment());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public long getFileflowid()
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			Long maxid=(Long)jqlService.getCount("select max(a.id) from ApprovalInfo as a ");
			if (maxid!=null && maxid.longValue()>0)
			{
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, maxid);
				if (approvalInfo.getFileflowid()!=null)
				{
					return approvalInfo.getFileflowid().longValue()+1;
				}
				else
				{
					maxid=(Long)jqlService.getCount("select max(a.fileflowid) from ApprovalInfo as a ");
					if (maxid!=null)
					{
						return maxid.longValue()+1;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 1l;
	}
	public boolean setSendDefault(ArrayList<Long> accepters,String comment,Users user)
	{//设置送文时的默认接收者
		try
		{
			//删除原来的，再增加新的
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			jqlService.excute("delete from ApprovalDefaulter where type=? and user.id=?",ApproveConstants.APPROVAL_DEFAULT_SEND,user.getId());
			if (accepters!=null && accepters.size()>0)
			{
				for (int i=0;i<accepters.size();i++)
				{
					ApprovalDefaulter defaulter=new ApprovalDefaulter();
					defaulter.setType(ApproveConstants.APPROVAL_DEFAULT_SEND);
					defaulter.setModifier(jqlService.getUsers(accepters.get(i)));
					defaulter.setComment(comment);
					defaulter.setUser(user);
					jqlService.save(defaulter);
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
	public boolean setSendDefault(ArrayList<Long> accepters,String comment,Integer issame,String filetype,Users user)
	{//设置送文时的默认接收者
		try
		{
			if (!WebConfig.signmodifydefault || filetype==null)
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				
				jqlService.excute("delete from ApprovalDefaulter where type=? and user.id=?",ApproveConstants.APPROVAL_DEFAULT_SEND,user.getId());
				if (accepters!=null && accepters.size()>0)
				{
					for (int i=0;i<accepters.size();i++)
					{
						ApprovalDefaulter defaulter=new ApprovalDefaulter();
						defaulter.setType(ApproveConstants.APPROVAL_DEFAULT_SEND);
						defaulter.setModifier(jqlService.getUsers(accepters.get(i)));
						defaulter.setComment(comment);
						defaulter.setUser(user);
						jqlService.save(defaulter);
					}
				}
			}
			else
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				List<ApprovalDic> diclist=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.name=? ", ApproveConstants.APPROVAL_FILETYPE,filetype);
				if (diclist==null || diclist.size()==0)//将该文件类别添加到字典
				{
					modifyFiletype(filetype,"add",user);
					diclist=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.name=? ", ApproveConstants.APPROVAL_FILETYPE,filetype);
				}
				ApprovalDic dic=diclist.get(0);
				jqlService.excute("delete from ApprovalDefaulter where type=? and user.id=? and dic.id=?",ApproveConstants.APPROVAL_DEFAULT_SEND,user.getId(),dic.getId());
				if (accepters!=null && accepters.size()>0)
				{
					int len=accepters.size();
					for (int i=0;i<len;i++)
					{
						ApprovalDefaulter defaulter=new ApprovalDefaulter();
						defaulter.setType(ApproveConstants.APPROVAL_DEFAULT_SEND);
						defaulter.setModifier(jqlService.getUsers(accepters.get(i)));
						defaulter.setComment(comment);
						if (len>1)
						{
							defaulter.setIssame(issame);
						}
						defaulter.setDic(dic);
						defaulter.setUser(user);
						jqlService.save(defaulter);
					}
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
	public List<String[]> getSendDefault(Users user)
	{
		return getSendDefault(null,user);
	}
	public List<String[]> getSendDefault(String filetype,Users user)
	{//获取送文时的默认接收者
		List<String[]> backlist=new ArrayList<String[]>();
		try
		{
			if (!WebConfig.signmodifydefault || filetype==null)//不按照文件类别分类
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				List<ApprovalDefaulter> list=(List<ApprovalDefaulter>)jqlService.findAllBySql("select a from ApprovalDefaulter as a where type=? and user.id=?",ApproveConstants.APPROVAL_DEFAULT_SEND,user.getId());
				if (list!=null && list.size()>0)
				{
					for (int i=0;i<list.size();i++)
					{
						ApprovalDefaulter defaulter=list.get(i);
						String[] temp=new String[]{String.valueOf(defaulter.getModifier().getId().longValue()),defaulter.getModifier().getRealName()};
						backlist.add(temp);
					}
				}
			}
			else
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				List<ApprovalDic> diclist=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.name=? ", ApproveConstants.APPROVAL_FILETYPE,filetype);
				if (diclist!=null && diclist.size()>0)
				{
					List<ApprovalDefaulter> list=(List<ApprovalDefaulter>)jqlService.findAllBySql("select a from ApprovalDefaulter as a where type=? and user.id=? and dic.id=? "
							,ApproveConstants.APPROVAL_DEFAULT_SEND,user.getId(),diclist.get(0).getId());
					if (list!=null && list.size()>0)
					{
						for (int i=0;i<list.size();i++)
						{
							ApprovalDefaulter defaulter=list.get(i);
							String[] temp=new String[]{String.valueOf(defaulter.getModifier().getId().longValue()),defaulter.getModifier().getRealName()};
							backlist.add(temp);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return backlist;
	}
	public ApprovalDefaulter getSendOther(Users user)
	{
		return getSendOther(null,user);
	}
	public ApprovalDefaulter getSendOther(String filetype,Users user)
	{//获取送文时的默认的其他信心，目前只有备注
		try
		{
			if (!WebConfig.signmodifydefault || filetype==null)//不按照文件类别分类
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				List<ApprovalDefaulter> list=(List<ApprovalDefaulter>)jqlService.findAllBySql("select a from ApprovalDefaulter as a where type=? and user.id=?",ApproveConstants.APPROVAL_DEFAULT_SEND,user.getId());
				if (list!=null && list.size()>0)
				{
					return list.get(0);
				}
			}
			else
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				List<ApprovalDic> diclist=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.name=? ", ApproveConstants.APPROVAL_FILETYPE,filetype);
				if (diclist!=null && diclist.size()>0)
				{
					List<ApprovalDefaulter> list=(List<ApprovalDefaulter>)jqlService.findAllBySql("select a from ApprovalDefaulter as a where type=? and user.id=? and dic.id=? "
							,ApproveConstants.APPROVAL_DEFAULT_SEND,user.getId(),diclist.get(0).getId());
					if (list!=null && list.size()>0)
					{
						return list.get(0);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean setModifyDefault(Long id,ArrayList<Long> accepters,Boolean nextchecked,String comment,Boolean huiqian,Users user)
	{//设置处理时的默认接收者
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		ApprovalInfo appinfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);
		int issame=0;
		
		if (huiqian!=null && huiqian.booleanValue())
		{
			issame=1;
		}
		return setModifyDefault(accepters,nextchecked,comment
				,issame,null,null,appinfo.getFiletype(),user);
	}
	/**
	 * 
	 * @param accepters 处理人
	 * @param nextchecked 下一处理方式
	 * @param comment 备注
	 * @param issame 是否会签
	 * @param sendreadid 传阅者ID
	 * @param sendreadname 传阅者名称
	 * @param filetype 文件名称
	 * @param user 当前用户
	 * @return
	 */
	public boolean setModifyDefault(ArrayList<Long> accepters,Boolean nextchecked,String comment
			,Integer issame,String sendreadid,String sendreadname,String filetype,Users user)
	{//设置处理时的默认接收者
		try
		{
			if (!WebConfig.signmodifydefault || filetype==null)
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				jqlService.excute("delete from ApprovalDefaulter where type=? and user.id=?",ApproveConstants.APPROVAL_DEFAULT_MODIFY,user.getId());
				if (accepters!=null && accepters.size()>0)
				{
					int len=accepters.size();
					for (int i=0;i<len;i++)
					{
						ApprovalDefaulter defaulter=new ApprovalDefaulter();
						defaulter.setType(ApproveConstants.APPROVAL_DEFAULT_MODIFY);
						defaulter.setModifier(jqlService.getUsers(accepters.get(i)));
						if (nextchecked!=null && nextchecked.booleanValue())
						{
							defaulter.setSelecttype(1);
						}
						defaulter.setComment(comment);
						defaulter.setUser(user);
						
						jqlService.save(defaulter);
					}
				}
				else if (comment!=null && comment.length()>0)
				{
					ApprovalDefaulter defaulter=new ApprovalDefaulter();
					defaulter.setType(ApproveConstants.APPROVAL_DEFAULT_MODIFY);
					defaulter.setComment(comment);
					defaulter.setUser(user);
					jqlService.save(defaulter);
				}
			}
			else//根据文件类别来分类
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				
				List<ApprovalDic> diclist=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.name=? ", ApproveConstants.APPROVAL_FILETYPE,filetype);
				if (diclist==null || diclist.size()==0)//将该文件类别添加到字典
				{
					modifyFiletype(filetype,"add",user);
					diclist=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.name=? ", ApproveConstants.APPROVAL_FILETYPE,filetype);
				}
				ApprovalDic dic=diclist.get(0);
				jqlService.excute("delete from ApprovalDefaulter where type=? and user.id=? and dic.id=? ",ApproveConstants.APPROVAL_DEFAULT_MODIFY,user.getId(),dic.getId());
				if (accepters!=null && accepters.size()>0)
				{
					int len=accepters.size();
					for (int i=0;i<accepters.size();i++)
					{
						ApprovalDefaulter defaulter=new ApprovalDefaulter();
						defaulter.setType(ApproveConstants.APPROVAL_DEFAULT_MODIFY);
						defaulter.setModifier(jqlService.getUsers(accepters.get(i)));
						if (nextchecked!=null && nextchecked.booleanValue())
						{
							defaulter.setSelecttype(1);
						}
						defaulter.setComment(comment);
						defaulter.setDic(dic);
						if (len>1 && issame!=null)//只有超过1个处理人才确定是会签还是串签，否则为串签
						{
							defaulter.setIssame(issame);
						}
						defaulter.setUser(user);
						jqlService.save(defaulter);
					}
				}
				else if (comment!=null && comment.length()>0)
				{
					ApprovalDefaulter defaulter=new ApprovalDefaulter();
					defaulter.setType(ApproveConstants.APPROVAL_DEFAULT_MODIFY);
					defaulter.setComment(comment);
					defaulter.setDic(dic);
					defaulter.setUser(user);
					jqlService.save(defaulter);
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
	public List<String[]> getModifyDefault(Long id,Users user)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		ApprovalInfo appinfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);
		return getModifyDefault(appinfo,appinfo.getFiletype(),user);
	}
	public List<String[]> getModifyDefault(ApprovalInfo appinfo,String filetype,Users user)
	{//获取送文时的默认接收者
		List<String[]> backlist=new ArrayList<String[]>();
		try
		{
			if (!WebConfig.signmodifydefault || filetype==null)
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				List<ApprovalDefaulter> list=(List<ApprovalDefaulter>)jqlService.findAllBySql("select a from ApprovalDefaulter as a where type=? and user.id=?",ApproveConstants.APPROVAL_DEFAULT_MODIFY,user.getId());
				if (list!=null && list.size()>0)
				{
					for (int i=0;i<list.size();i++)
					{
						ApprovalDefaulter defaulter=list.get(i);
						if (defaulter.getModifier()!=null)
						{
							String[] temp=new String[]{String.valueOf(defaulter.getModifier().getId().longValue()),defaulter.getModifier().getRealName()};
							backlist.add(temp);
						}
					}
				}
			}
			else
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				List<ApprovalDic> diclist=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.name=? ", ApproveConstants.APPROVAL_FILETYPE,filetype);
				if (diclist!=null && diclist.size()>0)
				{
					List<ApprovalDefaulter> list=(List<ApprovalDefaulter>)jqlService.findAllBySql("select a from ApprovalDefaulter as a where type=? and user.id=? and dic.id=? "
							,ApproveConstants.APPROVAL_DEFAULT_MODIFY,user.getId(),diclist.get(0).getId());
					if (list!=null && list.size()>0)
					{
						for (int i=0;i<list.size();i++)
						{
							ApprovalDefaulter defaulter=list.get(i);
							if (defaulter.getModifier()!=null)
							{
								String[] temp=new String[]{String.valueOf(defaulter.getModifier().getId().longValue()),defaulter.getModifier().getRealName()};
								backlist.add(temp);
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return backlist;
	}
	public ApprovalDefaulter getModifyOther(Long appid,Users user)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		ApprovalInfo appinfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, appid);
		return getModifyOther(appinfo,appinfo.getFiletype(),user);
	}
	public ApprovalDefaulter getModifyOther(ApprovalInfo appinfo,String filetype,Users user)
	{//获取送文时的默认备注
		List<String[]> backlist=new ArrayList<String[]>();
		try
		{
			if (!WebConfig.signmodifydefault || filetype==null)
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				List<ApprovalDefaulter> list=(List<ApprovalDefaulter>)jqlService.findAllBySql("select a from ApprovalDefaulter as a where type=? and user.id=?",ApproveConstants.APPROVAL_DEFAULT_MODIFY,user.getId());
				if (list!=null && list.size()>0)
				{
					return list.get(0);
				}
			}
			else
			{
				//删除原来的，再增加新的
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				List<ApprovalDic> diclist=(List<ApprovalDic>)jqlService.findAllBySql("select a from ApprovalDic as a where a.type=? and a.name=? ", ApproveConstants.APPROVAL_FILETYPE,filetype);
				if (diclist!=null && diclist.size()>0)
				{
					List<ApprovalDefaulter> list=(List<ApprovalDefaulter>)jqlService.findAllBySql("select a from ApprovalDefaulter as a where type=? and user.id=? and dic.id=? "
							,ApproveConstants.APPROVAL_DEFAULT_MODIFY,user.getId(),diclist.get(0).getId());
					if (list!=null && list.size()>0)
					{
						return list.get(0);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
