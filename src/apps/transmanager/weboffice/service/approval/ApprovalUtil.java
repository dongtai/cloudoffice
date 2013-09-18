package apps.transmanager.weboffice.service.approval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.client.constant.WebofficeUtility;
import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.both.MainConstants;
import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.MessageCons;
import apps.transmanager.weboffice.databaseobject.ApprovalInfo;
import apps.transmanager.weboffice.databaseobject.ApprovalReader;
import apps.transmanager.weboffice.databaseobject.ApprovalReaderDelete;
import apps.transmanager.weboffice.databaseobject.ApprovalTask;
import apps.transmanager.weboffice.databaseobject.Messages;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.SameSignInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.archive.ArchiveSecurity;
import apps.transmanager.weboffice.databaseobject.archive.ArchiveType;
import apps.transmanager.weboffice.databaseobject.flow.FlowAllNode;
import apps.transmanager.weboffice.databaseobject.flow.FlowFiles;
import apps.transmanager.weboffice.databaseobject.flow.FlowOwners;
import apps.transmanager.weboffice.databaseobject.flow.FlowStateOwners;
import apps.transmanager.weboffice.databaseobject.flow.FlowSubOwners;
import apps.transmanager.weboffice.domain.ApprovalBean;
import apps.transmanager.weboffice.domain.ApproveBean;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.DepMemberPo;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.domain.UserinfoView;
import apps.transmanager.weboffice.domain.Versioninfo;
import apps.transmanager.weboffice.domain.workflow.WorkFlowPicBean;
import apps.transmanager.weboffice.service.IAddressListService;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.impl.AddressListService;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.server.WebTools;
import apps.transmanager.weboffice.util.server.convertforread.bean.ConvertForRead;

/**
 * 文件审批工具类
 * <p>
 * <p>
 * EIO版本:        WebOffice v3.0
 * <p>
 * 作者:           孙爱华
 * <p>
 * 日期:           2012-12-14
 * <p>
 * 负责人:          孙爱华
 * <p>
 * 负责小组:        WebOffice3
 * <p>
 * <p>
 */
public class ApprovalUtil
{
    private static ApprovalUtil instance;// =  new ApprovalUtil();
    
    public ApprovalUtil()
    {    	
    	instance =  this;//new ApprovalUtil(true);
    }
    
    
    /**
     * 
     */
    public static ApprovalUtil instance()
    {
        return instance;
    }

    public static Long strToLong(String id)
	{
		try
		{
			return Long.valueOf(id);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public static Integer strToInteger(String id)
	{
		try
		{
			return Integer.valueOf(id);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public static Boolean strToBoolean(String str)
	{
		try
		{
			return Boolean.parseBoolean(str);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public static List<Long> strsToLongs(List<String> strs)
	{
		try
		{
			List<Long> back=new ArrayList<Long>();
			for (int i=0;i<strs.size();i++)
			{
				back.add(Long.valueOf(strs.get(i)));
			}
			return back;
		}
		catch (Exception e)
		{
			return null;
		}
	}
    private List getApprovalList(JQLServices jqlService,String SQL)
    {
	    try
		{

			List<Object[]> mylist=(List<Object[]>)jqlService.getObjectByNativeSQL(SQL,-1,-1);
			List<Object[]> resultlist=new ArrayList<Object[]>();
			//获取所有的ID
			Long[] ids=new Long[mylist.size()];
			String cond="select distinct model from ApprovalInfo as model where model.id in (";
			for (int i=0;i<mylist.size();i++)
			{
				Object[] obj=(Object[])mylist.get(i);
				ids[i]=((BigInteger)obj[2]).longValue();
				if (i==0)
				{
					cond+="?";
				}
				else
				{
					cond+=",?";
				}
			}
			cond+=")";
			if (ids.length>0)
			{
				List<ApprovalInfo> alist=jqlService.findInstance(cond, ids);
				if (alist!=null && !alist.isEmpty())
				{
					for (int i=0;i<mylist.size();i++)
					{
						Object[] obj=(Object[])mylist.get(i);
						Object[] robj=new Object[obj.length];
						robj[0]=obj[0];
						if (robj[0]==null)
						{
							robj[0]="";
						}
						robj[1]=obj[1];
						if (robj[1]==null)
						{
							robj[1]="";
						}
						BigInteger id = (BigInteger)obj[2];
						for (int j=0;j<alist.size();j++)
						{
							if (id.longValue()==alist.get(j).getId().longValue())
							{
								robj[2]=alist.get(j);
								break;
							}
						}
						boolean isadd=true;
						for (int j=0;j<resultlist.size();j++)
						{
							Object[] tempobj=(Object[])resultlist.get(j);
							if (id.longValue()==((ApprovalInfo)tempobj[2]).getId().longValue())
							{
								isadd=false;
								break;
							}
						}
						if (isadd)
						{
							resultlist.add(robj);
						}
					}
				}
			}
			
			System.out.println("NULL");
			return resultlist;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
    }
    /**
     * 处理审批信息
     * 
     * @param ab 审批信息
     * @param userID 用户ID
     * @param isCreate 是否新建一条记录
     */
    public int processApproval(long userID, ApprovalBean infoBean, boolean isCreate)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
        UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        
        List<Long> approvalUsersID = splitUsersID(infoBean.getInfoApprovalUsersID());
        if (isCreate)
        {
            ApprovalInfo approvalInfo = null;
            String sql = "select a from ApprovalInfo a where a.documentPath = ? ";
            List list = jqlService.findAllBySql(sql, infoBean.getInfoDocumentPath());
            if (list != null &&  !list.isEmpty())
            {
                approvalInfo = (ApprovalInfo)list.get(0);
                sql = "update ApprovalInfo as a set a.status = ?, a.approvalStep = ?, a.approvalUsersID = ?," +
                        "a.comment = ? where a.documentPath = ?";
                
                jqlService.excute(sql, MainConstants.APPROVAL_STATUS_PAENDING, 1, 
                    infoBean.getInfoApprovalUsersID(), infoBean.getInfoComment(), infoBean.getInfoDocumentPath());
            }
            else
            {
                approvalInfo = new ApprovalInfo();
                // 送审用户
                approvalInfo.setUserID(userID);
                // 第几步申批
                approvalInfo.setApprovalStep(1);
                // 文档权限
                approvalInfo.setPermit(infoBean.getInfoPermit());
                // 文档所在空间
                approvalInfo.setSpaceID(infoBean.getInfoSpaceID());
                // 审批状态
                approvalInfo.setStatus(MainConstants.APPROVAL_STATUS_PAENDING);
                // 送审日期
                approvalInfo.setDate(infoBean.getInfoDate());
                // 送审说明
                approvalInfo.setComment(infoBean.getInfoComment());
                // 送审文档路径
                approvalInfo.setDocumentPath(infoBean.getInfoDocumentPath());
                // 要审批用户ID组
                approvalInfo.setApprovalUsersID(infoBean.getInfoApprovalUsersID());            
                
                jqlService.save(approvalInfo);
            }
            
            // 建立下一个审批者任务
            ApprovalTask approvalTask = new ApprovalTask();
            // 审查ID
            approvalTask.setApprovalID(approvalInfo.getId());
            // 文档所在空间
            approvalTask.setSpaceID(infoBean.getInfoSpaceID());
            // 审查者ID号
            approvalTask.setApprovalUserID(approvalUsersID.get(0));
            // 动作
            approvalTask.setAction((long)MainConstants.APPROVAL_ACTION_NO);
            // 保存审批任务
            jqlService.save(approvalTask);
            
            /* 发送消息 */
            Messages message = new Messages();
            Users sendUser = userService.getUser(userID);
            // 文件路径
            String path = infoBean.getInfoDocumentPath();
            message.setAttach(path);
            // 日期
            message.setDate(new Date());
            // 消息发送者
            message.setUser(sendUser);
            // 类型
            message.setType(MessageCons.ADUIT_DOC_TYPE);
            // 内容
            message.setContent(path.substring(path.lastIndexOf("/") + 1));// + "/" + 0 + "/" + infoBean.getInfoPermit());
            message.setSize(0L);
            message.setPermit((long)infoBean.getInfoPermit());
            // 标题
            message.setTitle(MainConstants.APPROVEL_NOTICE_MESSAGE);
            
            ArrayList<Long> userIds = new ArrayList<Long>();
            userIds.add(approvalUsersID.get(0));
            messageService.sendMessage(message, userID, userIds);  
            
            /* 发送邮件 */
            Users user = userService.getUser(approvalUsersID.get(0));
            //messageService.sendMail(sendUser.getRealEmail(), user.getRealEmail(), MainConstants.APPROVEL_NOTICE_EMAIL, MainConstants.APPROVEL_NOTICE_EMAIL);
            
        }        
        // 更新
        else 
        {            
            int len = approvalUsersID.size();
            int step = 1;
            for (int i = 0; i < len; i++)
            {
                if (userID == approvalUsersID.get(i))
                {
                    step = i + 1;
                    break;
                }
            }
            // 0 = 送审中，1 = 审批中， 2 = 已完成， 3 = 已终止
            int status = step - 1 == len - 1 ? MainConstants.APPROVAL_STATUS_COMPLETED : MainConstants.APPROVAL_STATUS_ACTIVE;
            // 如果 执行是终上操作，则状态要改成终止
            if (infoBean.getTaskAction() == MainConstants.APPROVAL_ACTION_REJECT)
            {
                status = MainConstants.APPROVAL_STATUS_ABORTED;
            }
            // 更新审批信息
            String sql = "update ApprovalInfo as a set a.status = ?, a.approvalStep = ? where a.id = ?";
            jqlService.excute(sql, status, step, infoBean.getInfoID());
            // 更新审批任务信息
            sql = "update ApprovalTask as a set a.action = ?, a.comment = ?, a.date = ? where a.id = ?";
            jqlService.excute(sql, infoBean.getTaskAction(), infoBean.getTaskComment(), new Date(), infoBean.getTaskID());
            // 建立下一下审批者任务，只有审批中文档才需要建立下条审查任务
            if (status == 1)
            {
            	String path = infoBean.getInfoDocumentPath();
                ApprovalTask approvalTask = new ApprovalTask();
                // 审查ID
                approvalTask.setApprovalID(infoBean.getInfoID());
                // 文档所在空间
                approvalTask.setSpaceID(infoBean.getInfoSpaceID());
                // 审查者ID号
                approvalTask.setApprovalUserID(approvalUsersID.get(step));
                // 动作
                approvalTask.setAction((long)MainConstants.APPROVAL_ACTION_NO);
                // 保存记录
                jqlService.save(approvalTask);
                
                /* 发送消息 */
                Messages message = new Messages();
                Users sendUser = userService.getUser(infoBean.getInfoUserID());
                // 文件路径
                message.setAttach(infoBean.getInfoDocumentPath());
                // 日期
                message.setDate(new Date());
                // 消息发送者
                message.setUser(sendUser);
                // 类型
                message.setType(MessageCons.ADUIT_DOC_TYPE);
                // 内容
                message.setContent(path.substring(path.lastIndexOf("/") + 1));// + "/" + 0 + "/" + infoBean.getInfoPermit());
                message.setSize(0L);
                message.setPermit((long)infoBean.getInfoPermit());
                // 标题
                message.setTitle(MainConstants.APPROVEL_NOTICE_MESSAGE);
                
                ArrayList<Long> userIds = new ArrayList<Long>();
                userIds.add(approvalUsersID.get(step));
                messageService.sendMessage(message, userID, userIds);  
                
                /* 发送邮件 */
                Users user = userService.getUser(approvalUsersID.get(step));
                //messageService.sendMail(sendUser.getRealEmail(), user.getRealEmail(), MainConstants.APPROVEL_NOTICE_EMAIL, MainConstants.APPROVEL_NOTICE_EMAIL);
            }
            
        }
        return 0;
    }
    
    /**
     * 得到审批信息
     * 
     * @param ab 审批信息
     * @param userID 用户ID
     * @param isCreate 是否新建一条记录
     */
    public ApprovalBean getApproval(long userID, String filePath)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        String sql = "select a from ApprovalInfo a where a.documentPath = ? ";
        List list = jqlService.findAllBySql(sql, filePath);
        if (list == null || list.isEmpty())
        {
            return null;
        }
        ApprovalInfo infoDB = (ApprovalInfo)list.get(0);
        ApprovalBean infoBean = new ApprovalBean();
        // 文档审批ID
        infoBean.setInfoID(infoDB.getId());
        // 送审者ID
        infoBean.setInfoUserID(infoDB.getUserID());
        // 文件所的空间或织空间
        infoBean.setInfoSpaceID(infoDB.getSpaceID());
        // 权限
        infoBean.setInfoPermit(infoDB.getPermit());
        // 第几步审批
        infoBean.setInfoApprovalStep(infoDB.getApprovalStep());
        // 状态
        infoBean.setInfoStatus(infoDB.getStatus());
        // 送审日期
        infoBean.setInfoDate(infoDB.getDate());
        // 送审说明
        infoBean.setInfoComment(infoDB.getComment());
        // 文档路径
        infoBean.setInfoDocumentPath(infoDB.getDocumentPath());
        // 需要审批的用户组
        infoBean.setInfoApprovalUsersID(infoDB.getApprovalUsersID());
        
        // 提交用户名、审批者用户名
        List<Long> usersID = null;
        if (infoDB.getApprovalUsersID()!=null)
        {
        	usersID = splitUsersID(infoDB.getApprovalUsersID()); 
        }
        String str = userService.getUser(infoDB.getUserID()).getRealName() + "|";
        for (Long i : usersID)
        {
            str += userService.getUser(i).getRealName() + "|";
        }
        infoBean.setInfoApprovalUsersName(str);
        
        
        /* 任务信息  */
        sql = "select a from ApprovalTask a where a.approvalID = ? and a.approvalUserID = ?";
        list = jqlService.findAllBySql(sql, infoDB.getId(), userID);
        if (list != null && !list.isEmpty())
        {
            ApprovalTask task = null;
            int size = list.size();
            for (int i = 0; i < size; i++)
            {
                task = (ApprovalTask)list.get(i);
                if (task.getAction() == MainConstants.APPROVAL_ACTION_NO)
                {
                    break;
                }
            }            
            // 任务ID
            infoBean.setTaskID(task.getId());
            // 审批信息ID
            infoBean.setTaskApprovalID(task.getApprovalID());
            // 审批者ID
            infoBean.setTaskApprovalUserID(task.getApprovalUserID());
            // 审批说明
            infoBean.setTaskComment(task.getComment());
            // 审批日期
            infoBean.setTaskDate(task.getDate());
            // 审批动作
            infoBean.setTaskAction(task.getAction().intValue());
        }
        return infoBean;
    }
    
    /**
     * 获取选中文件是审批状态
     * 
     * @param filePath 选中文件的路径
     */
    public int getApprovalStatus(String filePath)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        String sql = "select a from ApprovalInfo a where a.documentPath = ? ";
        List list = jqlService.findAllBySql(sql, filePath);
        if (list == null || list.isEmpty())
        {
            return MainConstants.APPROVAL_STATUS_NO;
        }
        return ((ApprovalInfo)list.get(0)).getStatus();
    }
    
    /**
     * 获取选中文件是审批状态
     * 
     * @param filePath 选中文件的路径
     */
    public int getApprovalCount(String filePath)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        String sql = "select a from ApprovalInfo a where a.documentPath = ? ";
        List list = jqlService.findAllBySql(sql, filePath);
        int count = 0; 
        if (list != null && !list.isEmpty())
        {
            ApprovalInfo infoDB = ((ApprovalInfo)list.get(0));
            count++;
            
            sql = "select a from ApprovalTask a where a.approvalID = ?";
            list = jqlService.findAllBySql(sql, infoDB.getId());
            List<Long> usersID = null;
            if (infoDB.getApprovalUsersID()!=null)
            {
            	usersID=splitUsersID(infoDB.getApprovalUsersID());
            }
            if (list != null && !list.isEmpty())
            {
                // 处理已创建任务
                for (int i = 0; i < list.size(); i++)
                {
                    ApprovalTask taskDB = (ApprovalTask)list.get(i);
                    usersID.remove(new Long(taskDB.getApprovalUserID()));
                    count++;
                }
                // 没有创建任务
                for (int i = 0; i < usersID.size(); i++)
                {
                    count++;
                }                
            }
        }
        return count;
    }
    
    /**
     * 获得审批文档下拉菜单状态
     * 
     * @param userID
     * @param filePath
     * 
     * @return boolean[] 长度=3，[0] = "查看",[1] = "审批",[2]="审批状态"
     */
    public List<Boolean> getApprovalMenuStatus(long userID, String filePath)
    {
        boolean[] b = new boolean[3];
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        String sql = "select a from ApprovalInfo a where a.documentPath = ? ";
        List list = jqlService.findAllBySql(sql, filePath);
        if (list != null && !list.isEmpty())
        {
            ApprovalInfo infoDB = ((ApprovalInfo)list.get(0));
            int status = infoDB.getStatus();
            if (status != MainConstants.APPROVAL_STATUS_NO)
            {
                if (userID == infoDB.getUserID())
                {
                    // 查看、审批状态可见
                    b[0] = b[2] = true;
                }
                else
                {
                    sql = "select a from ApprovalTask a where a.approvalID = ? and a.approvalUserID = ?";
                    list = jqlService.findAllBySql(sql, infoDB.getId(), userID);
                    if (list != null && !list.isEmpty())
                    {
                        // 查看、审批状态可见
                        b[0] = b[2] = true;
                        // 如果没有审批，则审查可见
                        int size = list.size();
                        for (int i = 0; i < size; i++)
                        {
                            ApprovalTask task = (ApprovalTask)list.get(i);
                            if (b[1] = task.getAction() == MainConstants.APPROVAL_ACTION_NO)
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }
        ArrayList<Boolean> reList = new ArrayList<Boolean>();
        reList.add(b[0]);
        reList.add(b[1]);
        reList.add(b[2]);
        return reList;
    }
    
    /**
     * 获得文档的审批任务
     * 
     * @param userID
     * @param spaceID
     * @param filePath
     * 
     */
    public List<ApprovalBean> getApprovalTask(long userID, long spaceID, String filePath)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        String sql = "select a from ApprovalInfo a where a.documentPath = ? ";
        List list = jqlService.findAllBySql(sql, filePath);
        if (list != null && !list.isEmpty())
        {
            ArrayList<ApprovalBean> reList = new ArrayList<ApprovalBean>();
            
            ApprovalInfo infoDB = ((ApprovalInfo)list.get(0));
            ApprovalBean infoBean = new ApprovalBean();
            // 文档审批ID
            infoBean.setInfoID(infoDB.getId());
            // 送审者ID
            infoBean.setInfoUserID(infoDB.getUserID());
            // 文件所的空间或织空间
            infoBean.setInfoSpaceID(infoDB.getSpaceID());
            // 权限
            infoBean.setInfoPermit(infoDB.getPermit());
            // 第几步审批
            infoBean.setInfoApprovalStep(infoDB.getApprovalStep());
            // 状态
            infoBean.setInfoStatus(infoDB.getStatus());
            // 送审日期
            infoBean.setInfoDate(infoDB.getDate());
            // 送审说明
            infoBean.setInfoComment(infoDB.getComment());
            // 文档路径
            infoBean.setInfoDocumentPath(infoDB.getDocumentPath());
            // 需要审批的用户组
            infoBean.setInfoApprovalUsersID(infoDB.getApprovalUsersID());
            
            /* 任务信息 */
            // 封装提交者任务信息，这个在任务表中没有的。
            // 任务ID
            infoBean.setTaskID(-1l);
            // 审批者ID
            infoBean.setTaskApprovalUserID(infoDB.getUserID());
            // 用户名称
            infoBean.setTaskApprovalUserName(userService.getUser(infoDB.getUserID()).getRealName());
            // 审批动作
            infoBean.setTaskAction(MainConstants.APPROVAL_ACTION_PAENDING);
            // 审批日期
            infoBean.setTaskDate(infoDB.getDate());
            // 审批说明
            infoBean.setTaskComment(infoDB.getComment());
            //
            reList.add(infoBean);
            
            sql = "select a from ApprovalTask a where a.approvalID = ?";
            list = jqlService.findAllBySql(sql, infoDB.getId());
            
            List<Long> usersID = null;
            if (infoDB.getApprovalUsersID()!=null)
            {
            	usersID=splitUsersID(infoDB.getApprovalUsersID());
            }
            if (list != null && !list.isEmpty())
            {
                // 处理已创建任务
                for (int i = 0; i < list.size(); i++)
                {
                    ApprovalTask taskDB = (ApprovalTask)list.get(i);
                    infoBean = new ApprovalBean();
                    // 任务ID
                    infoBean.setTaskID(taskDB.getId());
                    // 审批者ID
                    infoBean.setTaskApprovalUserID(taskDB.getApprovalUserID());
                    // 用户名称
                    infoBean.setTaskApprovalUserName(userService.getUser(taskDB.getApprovalUserID()).getRealName());
                    // 审批动作
                    infoBean.setTaskAction(taskDB.getAction().intValue());
                    // 审批日期
                    infoBean.setTaskDate(taskDB.getDate());
                    // 审批说明
                    infoBean.setTaskComment(taskDB.getComment());
                    
                    usersID.remove(new Long(taskDB.getApprovalUserID()));
                    reList.add(infoBean);
                    // 如果拒绝的话，后面的任务就不要列出来了。
                    /*if (taskDB.getAction() == MainConstants.APPROVAL_ACTION_REJECT)
                    {
                        return reList;
                    }*/

                }
                // 没有创建任务
                for (int i = 0; i < usersID.size(); i++)
                {
                    infoBean = new ApprovalBean();
                    // 任务ID
                    infoBean.setTaskID(-1l);
                    // 审批者ID
                    infoBean.setTaskApprovalUserID(usersID.get(i));
                    // 用户名称
                    infoBean.setTaskApprovalUserName(userService.getUser(usersID.get(i)).getRealName());
                    // 审批动作
                    infoBean.setTaskAction(MainConstants.APPROVAL_ACTION_NO);
                    //
                    reList.add(infoBean);
                }                
            }
            return reList;
        }
        return null;
    }
    /**
     * 获得是否存当前用户需要审批的文件
     * @param userID
     * @param spaceID
     * @return
     */
    public boolean hasApproval(long userID, long spaceID)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        String infoSql = "select a from ApprovalInfo a where a.spaceID = ? ";
        List aList = jqlService.findAllBySql(infoSql, spaceID);
        long count = 0;
        List<String> ret = new ArrayList<String>();
        String taskSql = "select a from ApprovalTask a where a.approvalID = ? and a.spaceID = ? and a.approvalUserID = ?";
        if (aList != null && !aList.isEmpty())
        {
            int size = aList.size();
            for (int i = 0; i < size; i++)
            {
                ApprovalInfo infoDB = ((ApprovalInfo)aList.get(i));
                //List<Long> t = (List<Long>)jqlService.findAllBySql(taskSqla, infoDB.getId(), spaceID, userID);
                //ret.addAll((List<String>)jqlService.findAllBySql(taskSql, infoDB.getId(), spaceID, userID));
                // 审批者
                List taskList = jqlService.findAllBySql(taskSql, infoDB.getId(), spaceID, userID);
                if (taskList != null && !taskList.isEmpty())
                {
                    int tsize = taskList.size();
                    for (int j = 0; j < tsize; j++)
                    {
                        ApprovalTask aTask = (ApprovalTask)taskList.get(j);
                        if (aTask.getAction() < 0)
                        {
                            count ++;
                        }
                    }
                }      
            }
        }

        return count > 0;
    }
    
    /**
     * 获得审批文档列表
     * 
     * @param spaceUID 空间资源ID
     * @param path 路径
     * @param start 文档列表开始
     * @param limit  文件列表结束
     * @param userID 用户ID
     * @param spaceID 项目或组织ID 
     * @return
     */
    public DataHolder getApprovalFileList(JCRService jcrService, String spaceUID, String path,
        int start, int limit, long userID, long spaceID)
    {   
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        String infoSql = "select a from ApprovalInfo a where a.spaceID = ? ";
        String taskSql = "select a from ApprovalTask a where a.approvalID = ? and a.spaceID = ? and a.approvalUserID = ?";
        List aList = jqlService.findAllBySql(infoSql, spaceID);
        DataHolder reHolder = new DataHolder();
        reHolder.setFilesData(new ArrayList<Object>());
        
            
        int count = 0;
        if (aList != null && !aList.isEmpty())
        {
            int size = aList.size();
            for (int i = 0; i < size; i++)
            {
                ApprovalInfo infoDB = ((ApprovalInfo)aList.get(i));
                if (infoDB.getUserID() == userID)
                {
                    DataHolder holder = jcrService.getFiles(spaceUID, infoDB.getDocumentPath(), start, limit);
                    ArrayList fileList = holder.getFilesData();
                    if (fileList != null && !fileList.isEmpty())
                    {
                        Fileinfo fileinfo = (Fileinfo)fileList.get(0);
                        fileinfo.setApprovalStatus(infoDB.getStatus());
                        fileinfo.setApprovalCount(getApprovalCount(infoDB.getDocumentPath()));
                        
                        reHolder.getFilesData().add(fileinfo);
                        count++;
                    }
                    continue;
                }
                // 审批者
                List taskList = jqlService.findAllBySql(taskSql, infoDB.getId(), spaceID, userID);
                if (taskList != null && !taskList.isEmpty())
                {
                    DataHolder holder = jcrService.getFiles(spaceUID, infoDB.getDocumentPath(), start, limit);
                    ArrayList fileList = holder.getFilesData();
                    if (fileList != null && !fileList.isEmpty())
                    {
                        Fileinfo fileinfo = (Fileinfo)fileList.get(0);
                        fileinfo.setApprovalStatus(infoDB.getStatus());
                        fileinfo.setApprovalCount(getApprovalCount(infoDB.getDocumentPath()));
                        reHolder.getFilesData().add(fileinfo);
                        count++;
                    }
                }                
            }
            reHolder.setIntData(count);         
        }
        return reHolder;
    }
    
    /**
     * 获得审批者的审批文档列表
     * 
     * @param spaceUID 空间资源ID
     * @param path 路径
     * @param start 文档列表开始
     * @param limit  文件列表结束
     * @param userID 用户ID
     * @param spaceID 项目或组织ID 
     * @return
     */
    public DataHolder getApprovalFileList_Task(JCRService jcrService, String spaceUID, String path,
        int start, int limit, long userID, long spaceID)
    {   
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        String infoSql = "select a from ApprovalInfo a where a.spaceID = ? ";
        String taskSql = "select a from ApprovalTask a where a.approvalID = ? and a.spaceID = ? and a.approvalUserID = ?";
        List aList = jqlService.findAllBySql(infoSql, spaceID);
        DataHolder reHolder = new DataHolder();
        reHolder.setFilesData(new ArrayList<Object>());
        
            
        int count = 0;
        if (aList != null && !aList.isEmpty())
        {
            int size = aList.size();
            for (int i = 0; i < size; i++)
            {
                ApprovalInfo infoDB = ((ApprovalInfo)aList.get(i));
                // 审批者
                List taskList = jqlService.findAllBySql(taskSql, infoDB.getId(), spaceID, userID);
                if (taskList != null && !taskList.isEmpty())
                {
                    int tsize = taskList.size();
                    for (int j = 0; j < tsize; j++)
                    {
                        ApprovalTask aTask = (ApprovalTask)taskList.get(j);
                        if (aTask.getAction() < 0)
                        {
                            DataHolder holder = jcrService.getFiles(spaceUID, infoDB.getDocumentPath(), start, limit);
                            ArrayList fileList = holder.getFilesData();
                            if (fileList != null && !fileList.isEmpty())
                            {
                                Fileinfo fileinfo = (Fileinfo)fileList.get(0);
                                fileinfo.setApprovalStatus(infoDB.getStatus());
                                fileinfo.setApprovalCount(getApprovalCount(infoDB.getDocumentPath()));
                                reHolder.getFilesData().add(fileinfo);
                                count++;
                            }
                        }
                    }
                }                
            }
            reHolder.setIntData(count);         
        }
        return reHolder;
    }
    
    /**
     * 判断指定路径文档是送批 
     */
    public boolean hasApproval(String filePath)
    {
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        String infoSql = "select a from ApprovalInfo a where a.documentPath = ? ";
        List list = jqlService.findAllBySql(infoSql, filePath);
        return list != null && !list.isEmpty();
    }
    
    
    /**
     * 
     */
    private List<Long> splitUsersID(String usersID)
    {
        ArrayList<Long> list = new ArrayList<Long>();
        int len = usersID.length();
        int index = usersID.indexOf("|");
        if (index < 0 && len > 0)
        {
            list.add(Long.parseLong(usersID));
        }
        while (index >= 0)
        {
            String str = usersID.substring(0, index);
            if (str.length() > 0)
            {
                list.add(Long.parseLong(str));
            }
            usersID = usersID.substring(index + 1);
            index = usersID.indexOf("|");
            if (index < 0 && usersID.length() > 0)
            {
                list.add(Long.parseLong(usersID));
            }
            
        }
        return list;
    }
    private List<Object[]> filterSame(List<Object[]> list)
    {
    	List<Long> templist = new ArrayList<Long>();
		for (int i=0;i<list.size();i++)
		{
			Object[] obj=(Object[])list.get(i);
			Long tempid=(Long)obj[2];
			if (tempid!=null)
			{
				boolean isadd=true;
				for (int j=0;j<templist.size();j++)
				{
					if (tempid.longValue()==templist.get(j))
					{
						list.remove(i);
						i--;
						isadd=false;
						break;
					}
				}
				if (isadd)
				{
					templist.add(tempid);
				}
			}
		}
		return list;
    }
    
    /**
	 * 我的审批中，通过，退回，废弃-----普通人员，办公室人员共用
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getMyPaending(Long ownerId,int status,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			String queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.approvalUsersID=u.id and u.id=o.user.id and a.userID=? and a.status=? ";
			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String userStr="approvalUsersID";
			if (status!=1)
			{
				userStr="lastsignid";
			}
			String queryStringCount = "select count(distinct a.id) from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a."+userStr+"=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
				+" where a.id=t.approvalid and ( a.status="+status+" and ((t.resendtag=1 and t.approvalUserID="+ownerId+")"
				+" or (t.action=1 and t.approvalUserID="+ownerId+") or a.userid="+ownerId+"))";
			List<Object[]> list = null;// jqlService.findAllBySql(start,length,queryString,ownerId,status);
			String SQL = "select distinct u.realname,p.name,a.id "
				+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a."+userStr+"=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
				+" where a.id=t.approvalid and ( a.status="+status+" and ((t.resendtag=1 and t.approvalUserID="+ownerId+")"
				+" or (t.action=1 and t.approvalUserID="+ownerId+") or a.userid="+ownerId+"))";
			if (status==ApproveConstants.APPROVAL_STATUS_PAENDING)//如果领导再向上送审，要将领导的待审文件显示出来
			{
				SQL = "select distinct u.realname,p.name,a.id "
					+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
					+" on (a."+userStr+"=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
					+" where a.id=t.approvalid and ( a.status="+status+" and (t.action=1 or t.resendtag=1) and t.approvalUserID="+ownerId+")"
					;
				queryStringCount = "select count(distinct a.id) from approvalinfo a left join (users u,usersorganizations o,organizations p) "
					+" on (a."+userStr+"=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
					+" where a.id=t.approvalid and ( a.status="+status+" and (t.action=1 or t.resendtag=1) and t.approvalUserID="+ownerId+")"
					;
			}
			SQL+=" group by a.id ";
			SQL=appendSort2(SQL,sortField,isAsc);
			if (start<0)
			{
				start=0;
			}
			if (length<0)
			{
				SQL+=" limit "+start+",10000" ;
			}
			else
			{
				SQL+=" limit "+start+","+length ;
			}
			list=getApprovalList(jqlService,SQL);
			if (list!=null && list.size()>0)
			{
				SQL="select u.realName,o.organization.name,a.approvalID,a.state,a.isnew,a.signnum from SameSignInfo a,Users u,UsersOrganizations o "
					+" where a.signer.id=u.id and u.id=o.user.id and a.approvalID in (0";
				Long[] ids=new Long[list.size()];
				for (int i=0;i<list.size();i++)
				{
					Object[] obj=(Object[])list.get(i);
					ApprovalInfo info=(ApprovalInfo)obj[2];
					ids[i]=info.getId();
					SQL+=",?";
				}
				SQL+=") order by a.isnew desc,a.sid ";
				List samelist=jqlService.findAllBySql(SQL,ids);
				
				if (samelist!=null && samelist.size()>0)
				{
					List<Object[]> backlist=new ArrayList<Object[]>();
					for (int j=0;j<list.size();j++)
					{
						Object[] obj=(Object[])list.get(j);
						Object[] myobj=new Object[obj.length];
						myobj[2]=obj[2];
						ApprovalInfo info=(ApprovalInfo)obj[2];
						
						String users="";
						String orgs="";
						boolean SAME=false;
						if (status==ApproveConstants.APPROVAL_STATUS_PAENDING && info.getLastsignid()!=null)
						{
							myobj[0]=obj[0];
							myobj[1]=obj[1];
						}
						else
						{
							if (info.getLastsignid()==null)
							{
								Long maxid=1L;
								Integer signnum=null;
								for (int i=0;i<samelist.size();i++)
								{
									Object[] sameobj=(Object[])samelist.get(i);
									Long id2 = (Long)sameobj[2];
									Integer state=(Integer)sameobj[3];
									Long isnew=(Long)sameobj[4];
									if (i==0){signnum=(Integer)sameobj[5];}
									if (isnew==null)
									{
										isnew=1L;
									}
									if (i==0){maxid=isnew;}
									if (info.getId().longValue()==id2.longValue() && isnew.longValue()==maxid.longValue())
									{
										SAME=true;
										if (state==2)
										{
											users+=","+(String)sameobj[0];
											orgs+=","+(String)sameobj[1];
										}
									}
									
								}
								if (SAME)//有会签
								{
									if (users.length()>0)
									{
										myobj[0]=users.substring(1);
										myobj[1]=orgs.substring(1);
									}
									
								}
								else
								{
									myobj[0]=obj[0];
									myobj[1]=obj[1];
								}
							}
							else
							{
								String temp=(String)obj[0];
								myobj[0]=obj[0];
								myobj[1]=obj[1];
							}
						}
					
						backlist.add(myobj);
					}
					list=backlist;
				}
			}
			List<BigInteger> tempcountObj = (List<BigInteger>)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			
			Object countObj = Long.valueOf(""+tempcountObj.get(0));
			
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					if (status==ApproveConstants.APPROVAL_STATUS_ABANDONED)
					{
						Long optid=approveinfo.getOperateid();
						if (optid==null)
						{
							optid=approveinfo.getUserID();
						}
						Users user = userService.getUser(optid);
						bean.setUserName(user.getRealName());
						bean.setTaskApprovalUserDept(userService.getUserDept(optid));
					}
					else
					{
						Users user = userService.getUser(approveinfo.getUserID());
						bean.setUserName(user.getRealName());
						if (objArray[1]==null)
						{
							bean.setTaskApprovalUserDept("");
						}
						else
						{
							bean.setTaskApprovalUserDept((objArray[1].toString()));
						}
					}
					if (objArray[0]==null)
					{
						bean.setTaskApprovalUserName("");
					}
					else
					{
						bean.setTaskApprovalUserName(objArray[0].toString());
						
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFileIcon(getFileTypeImagePath(fileName));
					
					bean.setFilePath(filePath);	
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					bean.setStatus(status);
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					bean.setIsRead(approveinfo.getIsRead());
					bean.setPredefined(approveinfo.isPredefined());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	
	/**
	 * 我提交审批通过，退回-普通人员，办公室人员共用
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getMyPassOrReturn(Long ownerId,int status,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.approvalUsersID=u.id and u.id=o.user.id and a.userID=? and a.status=? ";
		queryString = appendSort(queryString,sortField,isAsc);//加入排序
		String queryStringCount = "select count(*) from ApprovalInfo a where a.userID=? and a.status=? ";
		List<Object[]> list = jqlService.findAllBySql(start,length,queryString,ownerId,status);
		Object countObj = jqlService.getCount(queryStringCount,ownerId,status);
		DataHolder reHolder = new DataHolder();
		ArrayList<Object> approveList = new ArrayList<Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(null!=list&&!list.isEmpty())
		{
			int size = list.size();
			for(int i=0;i<size;i++)
			{
				Object[] objArray = (Object[])list.get(i);
				ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
				ApproveBean bean  = new ApproveBean();
				bean.setNodetype(approveinfo.getNodetype());
				bean.setStepName(approveinfo.getStepName());
				bean.setApproveinfoId(approveinfo.getId());
				bean.setUserId(approveinfo.getUserID());
				if (objArray[0]==null)
				{
					bean.setTaskApprovalUserName("");
				}
				else
				{
					bean.setTaskApprovalUserName(objArray[0].toString());
				}
				if (objArray[1]==null)
				{
					bean.setTaskApprovalUserDept("");
				}
				else
				{
					bean.setTaskApprovalUserDept((objArray[1].toString()));
				}
				
				String filePath = approveinfo.getDocumentPath();
				if(null==filePath||"".equals(filePath))
				{
					continue;
				}
				int idx = filePath.lastIndexOf("/");
				String fileName = approveinfo.getFileName();
				if (fileName==null || fileName.length()==0)
				{
					fileName=filePath.substring(idx+1);
				}
				bean.setFileName(fileName);
				bean.setFilePath(filePath);	
				bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
				bean.setFileIcon(getFileTypeImagePath(fileName));
				bean.setStatus(status);
				bean.setDate(sdf.format(approveinfo.getDate()));
				bean.setComment(approveinfo.getComment());
				bean.setTitle(approveinfo.getTitle());
				bean.setPredefined(approveinfo.isPredefined());
				approveList.add(bean);
			}
		}
		reHolder.setFilesData(approveList);
		reHolder.setIntData(Integer.valueOf(countObj.toString()));      
		return reHolder;
	
}
	
	
	/**
	 * 我提交审批通过，退回-普通人员，办公室人员共用
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getAllMyDocument(Long ownerId,int status,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			String queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.approvalUsersID=u.id and u.id=o.user.id and a.userID=? and (a.status=1 or a.status=2 or a.status=3 or a.status=4) ";
			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String queryStringCount = "select count(*) from ApprovalInfo a where a.userID=? and (a.status=1 or a.status=2 or a.status=3 or a.status=4)";
			List<Object[]> list = null;//jqlService.findAllBySql(start,length,queryString,ownerId);
			String SQL = "select distinct u.realname,p.name,a.id "
				+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on ((a.approvalUsersID=u.id or a.lastsignid=u.id) and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
				+" where a.id=t.approvalid and (a.status=0 or a.status=1 or a.status=2 or a.status=3 or a.status=4) "
				+" and (t.resendtag=1 or t.action=1) and (t.approvalUserID="+ownerId+" or a.userid="+ownerId+")";
			SQL+=" group by a.id ";
			
			SQL=appendSort2(SQL,sortField,isAsc);
			if (start<0)
			{
				start=0;
			}
			if (length<0)
			{
				SQL+=" limit "+start+",10000" ;
			}
			else
			{
				SQL+=" limit "+start+","+length ;
			}
			list=getApprovalList(jqlService,SQL);
			if (list!=null && list.size()>0)
			{
				SQL="select u.realName,o.organization.name,a.approvalID,a.state,a.isnew,a.signnum from SameSignInfo a,Users u,UsersOrganizations o "
					+" where a.signer.id=u.id and u.id=o.user.id and a.approvalID in (0";
				for (int i=0;i<list.size();i++)
				{
					Object[] obj=(Object[])list.get(i);
					ApprovalInfo info=(ApprovalInfo)obj[2];
					if (info!=null && info.getNodetype()!=null && info.getNodetype().intValue()==1)
					{
						SQL+=","+info.getId().longValue();
					}
				}
				SQL+=") order by a.isnew desc,a.sid ";
				List samelist=jqlService.findAllBySql(SQL);
				
				if (samelist!=null && samelist.size()>0)
				{
					List<Object[]> backlist=new ArrayList<Object[]>();
					for (int j=0;j<list.size();j++)
					{
						Object[] obj=(Object[])list.get(j);
						Object[] myobj=new Object[obj.length];
						myobj[2]=obj[2];
						ApprovalInfo info=(ApprovalInfo)obj[2];
						
						String users="";
						String orgs="";
						boolean SAME=false;
						if (status==ApproveConstants.APPROVAL_STATUS_PAENDING && info.getLastsignid()!=null)
						{
							myobj[0]=obj[0];
							myobj[1]=obj[1];
						}
						else
						{
							if (info.getLastsignid()==null)
							{
								Long maxid=1L;
								Integer signnum=null;
								for (int i=0;i<samelist.size();i++)
								{
									Object[] sameobj=(Object[])samelist.get(i);
									Long id2 = (Long)sameobj[2];
									Integer state=(Integer)sameobj[3];
									Long isnew=(Long)sameobj[4];
									if (i==0){signnum=(Integer)sameobj[5];}
									if (isnew==null)
									{
										isnew=1L;
									}
									if (i==0){maxid=isnew;}
									if (info.getId().longValue()==id2.longValue() && isnew.longValue()==maxid.longValue())
									{
										SAME=true;
										if (state==2)
										{
											users+=","+(String)sameobj[0];
											orgs+=","+(String)sameobj[1];
										}
									}
								}
								if (SAME)//有会签
								{
									if (users.length()>0)
									{
										myobj[0]=users.substring(1);
										myobj[1]=orgs.substring(1);
									}
									
								}
								else
								{
									myobj[0]=obj[0];
									myobj[1]=obj[1];
								}
							}
							else
							{
								String temp=(String)obj[0];
								myobj[0]=obj[0];
								myobj[1]=obj[1];
							}
						}
						backlist.add(myobj);
					}
					list=backlist;
				}
			}
			
			
			Object countObj = jqlService.getCount(queryStringCount,ownerId);
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					if (objArray[0]==null)
					{
						bean.setTaskApprovalUserName("");
					}
					else
					{
						bean.setTaskApprovalUserName(objArray[0].toString());
					}
					if (objArray[1]==null)
					{
						bean.setTaskApprovalUserDept("");
					}
					else
					{
						bean.setTaskApprovalUserDept((objArray[1].toString()));
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");					
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFileIcon(getFileTypeImagePath(fileName));
					bean.setFilePath(filePath);	
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					bean.setStatus(approveinfo.getStatus());
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					bean.setPredefined(approveinfo.isPredefined());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}

	/**
	 * 获得搜索文档
	 * @param ownerId 当前用户ID
	 * @param type 搜索类型
	 * @param key 搜索关键字
	 * @param status 状态
	 * @param start 开始索引
	 * @param length 每页长度
	 * @param j 
	 * @param sortField 排序字段
	 * @param isAsc 是否升序
	 * @return 搜索文档
	 */
	public DataHolder getAllSearchDocument(Long ownerId,int type,String key,int status,int start ,int length, String sortField,String isAsc)
	{	
		//  List<Object[]> list = null;
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String queryString = "";
		String queryStringCount = "";
		List<Object[]> list = null;
		DataHolder reHolder = new DataHolder();
		ArrayList<Object> approveList = new ArrayList<Object>();
		String searchCondition = " and (a.title like '%"+key+"%' or a.fileName like '%"+key+"%' or a.comment like '%"+key+"%')";
		if(type == 1)
		{
			searchCondition = "and a.title like '%"+key+"%'";
		}else if(type==2){
			searchCondition = "and a.fileName like '%"+key+"%'";
		}else if(type==3){
			searchCondition = "and a.comment like '%"+key+"%'";
		}
	
		queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.userID=u.id and u.id=o.user.id and a.userID=?"+searchCondition;
		queryStringCount = "select count(*) from ApprovalInfo a where (a.userID=?)"+searchCondition;
		String statusCondition = " and a.status in(";
		if(status==1)
		{
			statusCondition += ApproveConstants.APPROVAL_STATUS_PAENDING+","+ApproveConstants.APPROVAL_STATUS_AGREE+","+ApproveConstants.APPROVAL_STATUS_ABANDONED;
		}
		if(status==2)
		{
			queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.userID=u.id and u.id=o.user.id and a.approvalUsersID=?"+searchCondition;
			queryStringCount = "select count(*) from ApprovalInfo a where a.approvalUsersID=?"+searchCondition;
			statusCondition += ApproveConstants.APPROVAL_STATUS_PAENDING+","+ApproveConstants.APPROVAL_STATUS_AGREE;
		}
		if(status==3)
		{
			statusCondition += ApproveConstants.APPROVAL_STATUS_ENDTOOffICE+","+ApproveConstants.APPROVAL_STATUS_PUBLISH+","+ApproveConstants.APPROVAL_STATUS_ARCHIVING+","+ApproveConstants.APPROVAL_STATUS_DESTROY;
		}
		statusCondition += ")";
		queryString+=statusCondition;
		queryStringCount+=statusCondition;
		queryString = appendSort(queryString,sortField,isAsc);//加入排序
		Object countObj = null;
		if(status==2)
		{
			list = jqlService.findAllBySql(start,length,queryString,ownerId.toString());
			countObj = jqlService.getCount(queryStringCount,ownerId.toString());
		}else{
			list = jqlService.findAllBySql(start,length,queryString,ownerId);
			countObj = jqlService.getCount(queryStringCount,ownerId);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(null!=list&&!list.isEmpty())
		{
			int size = list.size();
			for(int i=0;i<size;i++)
			{
				Object[] objArray = (Object[])list.get(i);
				ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
				ApproveBean bean  = new ApproveBean();
				bean.setNodetype(approveinfo.getNodetype());
				bean.setApproveinfoId(approveinfo.getId());
				bean.setStepName(approveinfo.getStepName());
				bean.setUserId(approveinfo.getUserID());
				if (objArray[0]==null)
				{
					bean.setTaskApprovalUserName("");
				}
				else
				{
					bean.setTaskApprovalUserName(objArray[0].toString());
				}
				if (objArray[1]==null)
				{
					bean.setTaskApprovalUserDept("");
				}
				else
				{
					bean.setTaskApprovalUserDept((objArray[1].toString()));
				}
				
				String filePath = approveinfo.getDocumentPath();
				if(null==filePath||"".equals(filePath))
				{
					continue;
				}
				int idx = filePath.lastIndexOf("/");					
				String fileName = approveinfo.getFileName();
				if (fileName==null || fileName.length()==0)
				{
					fileName=filePath.substring(idx+1);
				}
				bean.setFileName(fileName);
				bean.setFileIcon(getFileTypeImagePath(fileName));
				bean.setFilePath(filePath);	
				bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
				bean.setStatus(approveinfo.getStatus());
				bean.setDate(sdf.format(approveinfo.getDate()));
				bean.setComment(approveinfo.getComment());
				bean.setTitle(approveinfo.getTitle());
				bean.setPredefined(approveinfo.isPredefined());
				approveList.add(bean);
			}
		}
		reHolder.setFilesData(approveList);
		reHolder.setIntData(Integer.valueOf(countObj.toString()));      
		return reHolder;
		
	}
	/**
	 * 搜索批阅文档
	 * @param ownerId 当前用户ID
	 * @param type 搜索类型
	 * @param key 搜索关键字
	 * @param status 状态
	 * @param start 开始索引
	 * @param length 每页长度
	 * @param j 
	 * @param sortField 排序字段
	 * @param isAsc 是否升序
	 * @return 搜索文档
	 */
	public DataHolder getAllSearchReadDocument(Long ownerId,int type,String key,int start ,int length, String sortField,String isAsc)
	{	
		//  List<Object[]> list = null;
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String queryString = "";
		String queryStringCount = "";
		ArrayList<Object> approveList = new ArrayList<Object>();
		String searchCondition = " and (a.title like '%"+key+"%' or a.fileName like '%"+key+"%' or a.comment like '%"+key+"%')";
		if(type == 1)
		{
			searchCondition = "and a.title like '%"+key+"%'";
		}else if(type==2){
			searchCondition = "and a.fileName like '%"+key+"%'";
		}else if(type==3){
			searchCondition = "and a.comment like '%"+key+"%'";
		}
		
		queryString = "select u.realName,o.organization.name,a from ApprovalReader a,Users u,UsersOrganizations o where a.userId=u.id and u.id=o.user.id and a.userId=?"+searchCondition;
		queryString = appendReaderSort(queryString,sortField,isAsc);//加入排序
		queryStringCount = "select count(*) from ApprovalReader a where a.userID=?"+searchCondition;
		List<Object[]> list = jqlService.findAllBySql(start,length,queryString,ownerId);
		Object countObj = jqlService.getCount(queryStringCount,ownerId);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DataHolder reHolder = convertViewData(list,Integer.valueOf(countObj.toString()));    
		return reHolder;
		
	}
	/**
	 * 获得我的待阅文档
	 * @param ownerId 待阅者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getReadingDocument(Long ownerId,int status,int start ,int length,String sortField,String isAsc)
	{	
		//  List<Object[]> list = null;
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String queryString = "select u.realName,o.organization.name,a from ApprovalReader a, Users u, UsersOrganizations o, ApprovalInfo ai" +
				" where a.sendUser = u.id and u.id = o.user.id and a.userId = ? and a.isRead = false  and ai.status != ? and ai.id = a.approvalInfoId ";
		queryString = appendReaderSort(queryString,sortField,isAsc);//加入排序
		String queryStringCount = "select count(a) from ApprovalReader a, ApprovalInfo ai where a.userId = ? and a.isRead = false" +
				" and ai.status != ? and ai.id = a.approvalInfoId ";
		List<Object[]> list = jqlService.findAllBySql(start,length,queryString, ownerId, ApproveConstants.APPROVAL_STATUS_ABANDONED);
		Object countObj = jqlService.getCount(queryStringCount, ownerId, ApproveConstants.APPROVAL_STATUS_ABANDONED);
		DataHolder reHolder = convertViewData(list, Integer.valueOf(countObj.toString())) ;  
		return reHolder;
		
	}
	/**
	 * 获得我的已阅文档
	 * @param ownerId 待阅者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getReadedDocument(Long ownerId,int status,int start ,int length,String sortField,String isAsc)
	{	
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String queryString = "select u.realName,o.organization.name,a from ApprovalReader a,Users u,UsersOrganizations o, ApprovalInfo ai " +
				"where a.sendUser=u.id and u.id=o.user.id and a.userId=? and a.isRead=true  and ai.status != ? and ai.id = a.approvalInfoId ";
		queryString = appendReaderSort(queryString,sortField,isAsc);//加入排序
		String queryStringCount = "select count(a) from ApprovalReader a, ApprovalInfo ai where a.userId=? and a.isRead=true"
			 + " and ai.status != ? and ai.id = a.approvalInfoId ";
		List<Object[]> list = jqlService.findAllBySql(start,length,queryString, ownerId, ApproveConstants.APPROVAL_STATUS_ABANDONED);
		Object countObj = jqlService.getCount(queryStringCount, ownerId, ApproveConstants.APPROVAL_STATUS_ABANDONED);
		DataHolder reHolder = convertViewData(list,Integer.valueOf(countObj.toString()));
		return reHolder;
		
	}
	
	/**
	 * 获得所有批阅文档
	 * @param id
	 * @param i
	 * @param start
	 * @param limit
	 * @param sort
	 * @param dir
	 * @return
	 */
	public DataHolder getReadAllDocument(Long ownerId, int i, int start, int limit,
			String sortField, String isAsc) {
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String queryString = "select u.realName,o.organization.name,a from ApprovalReader a,Users u,UsersOrganizations o, ApprovalInfo ai " +
				"where a.sendUser=u.id and u.id=o.user.id and a.userId=? and ai.status != ? and ai.id = a.approvalInfoId";
		queryString = appendReaderSort(queryString,sortField,isAsc);//加入排序
		String queryStringCount = "select count(a.id) from ApprovalReader a, ApprovalInfo ai where a.userId=?"
			+ " and ai.status != ? and ai.id = a.approvalInfoId "; 
		List<Object[]> list = jqlService.findAllBySql(start,limit,queryString,ownerId, ApproveConstants.APPROVAL_STATUS_ABANDONED);
		Object countObj = jqlService.getCount(queryStringCount,ownerId, ApproveConstants.APPROVAL_STATUS_ABANDONED);
		DataHolder reHolder = convertViewData(list,Integer.valueOf(countObj.toString()));
		return reHolder;
	}
	
	private DataHolder convertViewData(List<Object[]> list,int count){
		DataHolder reHolder = new DataHolder();
		ArrayList<Object> approveList = new ArrayList<Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(null!=list&&!list.isEmpty())
		{
			int size = list.size();
			for(int i=0;i<size;i++)
			{
				Object[] objArray = (Object[])list.get(i);
				ApprovalReader approveReader = (ApprovalReader)objArray[2];
				ApproveBean bean  = new ApproveBean();
				bean.setStepName(approveReader.getStepName());
				bean.setApproveinfoId(approveReader.getId());
				if (objArray[0]==null)
				{
					bean.setTaskApprovalUserName("");
					bean.setUserName("");
				}
				else
				{
					bean.setTaskApprovalUserName(objArray[0].toString());
					bean.setUserName(objArray[0].toString());
				}
				
				if (objArray[1]==null)
				{
					bean.setTaskApprovalUserDept("");
					bean.setUserDeptName("");
				}
				else
				{
					bean.setTaskApprovalUserDept((objArray[1].toString()));
					bean.setUserDeptName((objArray[1].toString()));
				}
				
				String filePath = approveReader.getPath();
				if(null==filePath||"".equals(filePath))
				{
					continue;
				}
				String fileName = approveReader.getFileName();
				bean.setFileName(fileName);
				bean.setFileIcon(getFileTypeImagePath(fileName));
				bean.setFilePath(filePath);	
				if (approveReader.getDate()==null)
				{
					bean.setDate("");
				}
				else
				{
					bean.setDate(sdf.format(approveReader.getDate()));
				}
				bean.setComment(approveReader.getComment());
				bean.setTitle(approveReader.getTitle());
				bean.setIsRead(approveReader.isRead()?"1":"0");
				approveList.add(bean);
			}
		}
		reHolder.setFilesData(approveList);
		reHolder.setIntData(count);      
		return reHolder;
	}
	
	
	 /**
	 * 查询发布的文档
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getPublishDocument(Long ownerId,int start ,int length,String sortField,String isAsc)
	{	
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
//			PermissionService permservice = (PermissionService)ApplicationContext.getInstance().getBean("permissionService");
//			long permission=permservice.getSystemPermission(ownerId);//获取当前用户的权限，如果有文件管理的权限就显示本单位所有人的文件，否则只能显示本人的成文内容
//			boolean ismanage = FlagUtility.isValue(permission, ManagementCons.AUDIT_MANGE_FLAG);//是否有文件管理权限
			String usercond=getOrgUsers(ownerId,jqlService);
			String SQL = "select distinct u.realname,p.name,a.id "
				+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a.lastsignid=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
				+" where ( a.id=t.approvalID and a.status=7 "
				+")and ((t.action=1 and t.approvalUserID in "+usercond+") or (a.userID in "+usercond+"))"
				;
			SQL+=" group by a.id ";
			SQL=appendSort2(SQL,sortField,isAsc);
			if (start<0)
			{
				start=0;
			}
			
			if (length<0)
			{
				SQL+=" limit "+start+",10000" ;
			}
			else
			{
				SQL+=" limit "+start+","+length ;
			}
			List<Object[]> list=getApprovalList(jqlService,SQL);
			
			String queryStringCount = "select count(distinct a.id) from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a.lastsignid=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
				+" where ( a.id=t.approvalID and a.status=7 "
				+")and ((t.action=1 and t.approvalUserID in "+usercond+") or (a.userid in "+usercond+"))";
			List<BigInteger> tempcount=(List<BigInteger>)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			Object countObj = Long.valueOf(""+tempcount.get(0).longValue());
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					Long optid=approveinfo.getOperateid();
					if (optid==null)
					{
						optid=approveinfo.getUserID();
					}
					Users user = userService.getUser(optid);
					bean.setUserName(user.getRealName());
//					if (objArray[0]==null)
//					{
//						bean.setUserName("");
//					}
//					else
//					{
//						bean.setUserName(objArray[0].toString());
//					}
					if (objArray[1]==null)
					{
						bean.setUserDeptName("");
					}
					else
					{
						bean.setUserDeptName((objArray[1].toString()));
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFileIcon(getFileTypeImagePath(fileName));
					bean.setFilePath(filePath);	
					bean.setStatus(approveinfo.getStatus());
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					
//					Long appId = 0L;
//					if (approveinfo.getLastsignid()!=null)
//					{
//						appId=Long.valueOf(approveinfo.getLastsignid());
//					}
//					if(userService.getUser(appId)!=null)
//					{
//						bean.setTaskApprovalUserName(userService.getUser(appId).getRealName());
//						bean.setTaskApprovalUserDept(userService.getUserDept(appId));
//					}else{
//						bean.setTaskApprovalUserName("");
//						bean.setTaskApprovalUserDept("");
//					}
					bean.setTaskApprovalUserName(user.getRealName());
					bean.setTaskApprovalUserDept(userService.getUserDept(optid));
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	
	public DataHolder getSignDocument(Long ownerId,int start ,int length,String sortField,String isAsc)
	{	
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
//			PermissionService permservice = (PermissionService)ApplicationContext.getInstance().getBean("permissionService");
//			long permission=permservice.getSystemPermission(ownerId);//获取当前用户的权限，如果有文件管理的权限就显示本单位所有人的文件，否则只能显示本人的成文内容
//			boolean ismanage = FlagUtility.isValue(permission, ManagementCons.AUDIT_MANGE_FLAG);//是否有文件管理权限
			String usercond=getOrgUsers(ownerId,jqlService);
			String SQL = "select distinct u.realname,p.name,a.id "
				+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a.lastsignid=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
				+" where ( a.id=t.approvalID "
				+")and ((t.approvalUserID in "+usercond+") or (a.userID in "+usercond+"))"
				;
			SQL+=" group by a.id ";
			SQL=appendSort2(SQL,sortField,isAsc);
			if (start<0)
			{
				start=0;
			}
			if (length<0)
			{
				SQL+=" limit "+start+",10000" ;
			}
			else
			{
				SQL+=" limit "+start+","+length ;
			}
			List<Object[]> list=getApprovalList(jqlService,SQL);
			
			String queryStringCount = "select count(distinct a.id) from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a.lastsignid=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
				+" where ( a.id=t.approvalID "
				+")and ((t.approvalUserID in "+usercond+") or (a.userid in "+usercond+"))";
			List<BigInteger> tempcount=(List<BigInteger>)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			Object countObj = Long.valueOf(""+tempcount.get(0).longValue());
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					Long optid=approveinfo.getOperateid();
					if (optid==null)
					{
						optid=approveinfo.getUserID();
					}
					Users user = userService.getUser(optid);
					bean.setUserName(user.getRealName());
//					if (objArray[0]==null)
//					{
//						bean.setUserName("");
//					}
//					else
//					{
//						bean.setUserName(objArray[0].toString());
//					}
					if (objArray[1]==null)
					{
						bean.setUserDeptName("");
					}
					else
					{
						bean.setUserDeptName((objArray[1].toString()));
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFileIcon(getFileTypeImagePath(fileName));
					bean.setFilePath(filePath);	
					bean.setStatus(approveinfo.getStatus());
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					
//					Long appId = 0L;
//					if (approveinfo.getLastsignid()!=null)
//					{
//						appId=Long.valueOf(approveinfo.getLastsignid());
//					}
//					if(userService.getUser(appId)!=null)
//					{
//						bean.setTaskApprovalUserName(userService.getUser(appId).getRealName());
//						bean.setTaskApprovalUserDept(userService.getUserDept(appId));
//					}else{
//						bean.setTaskApprovalUserName("");
//						bean.setTaskApprovalUserDept("");
//					}
					bean.setTaskApprovalUserName(user.getRealName());
					bean.setTaskApprovalUserDept(userService.getUserDept(optid));
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	
	
	/**
	 * 领导查询给人家审批退回的文档
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder  getLeaderPaendingReturn(Long apporveUserId,String filePath,int status ,int start ,int length,String sortField,boolean isAsc)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		return null;
	}
	
	
	
	private String appendSort(String queryString,String sortField,String isAsc)
	{
		if(sortField==null||"".equals(sortField))
		{
			queryString+=" order by a.date desc";
			return queryString;
		}
		sortField = replaceField(sortField);
		if("ASC".equalsIgnoreCase(isAsc))
		{
			queryString+=" order by "+sortField+" asc";
		}
		else if("DESC".equalsIgnoreCase(isAsc))
		{
			queryString+=" order by "+sortField+" desc";
		}
		else
		{
			queryString+=" order by a.date"+sortField+" desc";
		}
		return queryString;
	}
	private String replaceReaderField(String sortField)
	{
		String result = "";
		if("fileName".equals(sortField)||"showFileName".equals(sortField))
		{
			result="a.path";
		}
		else if("owner".equals(sortField))
		{
			result="a.userId";
		}
		else if("signer".equals(sortField))
		{
			result="a.sendUser";
		}
		else if("ownerDept".equals(sortField))
		{
			result="o.organization.name";
		}
		else if("signerDept".equals(sortField))
		{
			result="o.organization.name";
		}
		else if("approveDate".equals(sortField))
		{
			result="a.date";
		}
		else if("status".equals(sortField))
		{
			result="a.isRead";
		}
		else
		{
			result="a."+sortField;
		}
		return result;
	}
	private String appendSort2(String queryString,String sortField,String isAsc)
	{
		if(sortField==null||"".equals(sortField))
		{
			queryString+=" order by a.date desc";
			return queryString;
		}
		sortField = replaceField2(sortField);
		if("ASC".equalsIgnoreCase(isAsc))
		{
			queryString+=" order by "+sortField+" asc";
		}
		else if("DESC".equalsIgnoreCase(isAsc))
		{
			queryString+=" order by "+sortField+" desc";
		}
		else
		{
			queryString+=" order by a.date"+sortField+" desc";
		}
		return queryString;
	}
	private String replaceField2(String sortField)
	{
		String result = "";
		if("fileName".equals(sortField)||"showFileName".equals(sortField))
		{
			result="a.documentPath";
		}
		else if("owner".equals(sortField))
		{
			result="a.userID";
		}
		else if("signer".equals(sortField))
		{
			result="a.approvalUsersID";
		}
		else if("ownerDept".equals(sortField))
		{
			result="p.name";
		}
		else if("signerDept".equals(sortField))
		{
			result="p.name";
		}
		else if("approveDate".equals(sortField))
		{
			result="a.date";
		}
		else
		{
			result="a."+sortField;
		}
		return result;
	}
	private String appendReaderSort(String queryString,String sortField,String isAsc)
	{
		if(sortField==null||"".equals(sortField))
		{
			queryString+=" order by a.date desc";
			return queryString;
		}
		sortField = replaceReaderField(sortField);
		if("ASC".equalsIgnoreCase(isAsc))
		{
			queryString+=" order by "+sortField+" asc";
		}
		else if("DESC".equalsIgnoreCase(isAsc))
		{
			queryString+=" order by "+sortField+" desc";
		}
		else
		{
			queryString+=" order by a.date"+sortField+" desc";
		}
		return queryString;
	}
	private String replaceField(String sortField)
	{
		String result = "";
		if("fileName".equals(sortField)||"showFileName".equals(sortField))
		{
			result="a.documentPath";
		}
		else if("owner".equals(sortField))
		{
			result="a.userID";
		}
		else if("signer".equals(sortField))
		{
			result="a.approvalUsersID";
		}
		else if("ownerDept".equals(sortField))
		{
			result="o.organization.name";
		}
		else if("signerDept".equals(sortField))
		{
			result="o.organization.name";
		}
		else if("approveDate".equals(sortField))
		{
			result="a.date";
		}
		else
		{
			result="a."+sortField;
		}
		return result;
	}
	
	/**
	 * 根据approveinfoId 来办结或者废弃文档
	 * @param approveId
	 * @param status
	 * @return
	 */
	public String endOrAbandoned(List approveIds,String status,Long operationUserId)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		int size = approveIds.size();
		for(int i=0;i<size;i++)
		{
			Long approveId = Long.valueOf(approveIds.get(i).toString());
			ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);
			if (info.getStatus()==ApproveConstants.APPROVAL_STATUS_PAENDING)
			{
				//先判断有没有处理，再判断有没有查阅文件
				Long len=(Long)jqlService.getCount("select count(a.id) from ApprovalTask as a where a.approvalID=? and a.action=2 ", approveId);
				if (len>0 && info.getStatus()==1)//有人签批过
				{
					return "对不起，已有人签批过，请查看签批“历史”";
				}
				if (info.getSignreader()!=null && info.getSignreader().intValue()>0 && info.getStatus()==1 && "4".equals(status))
				{
					Users users = (Users)jqlService.getEntity(Users.class, info.getSignreader());
					return "对不起，"+users.getRealName()+"已查看过签批文档，不能被终止！";
				}
			}
			if (info.getUserID().longValue()!=operationUserId.longValue() && info.getStatus()==1)
			{
				Users users = (Users)jqlService.getEntity(Users.class, info.getUserID());
				return "对不起，你的文档已被"+users.getRealName()+"送审，只有"+users.getRealName()+"才能终止！";
			}
			String sql = "update ApprovalInfo as a set a.date=?,a.status =?,a.isRead=0,operateid=? where a.id = ?";	    
			jqlService.excute(sql,new Date(),Integer.valueOf(status),operationUserId ,approveId);
			
			ApprovalTask task = new ApprovalTask();
	         task.setApprovalID(approveId);//加入送审ID
	         task.setDate(new Date());
	         task.setApprovalUserID(operationUserId);
	         task.setAction((long)Integer.valueOf(status));
	         jqlService.save(task);

		}
		return "1";
	}
	
	/**
	 * 我提交审批通过，退回-普通人员，办公室人员共用
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getAllLeaderDocument(String approveUserId,int status,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			//String queryString = "select u.realName,o.organization.name,b.documentPath,a from ApprovalTask a,ApprovalInfo b,Users u,UsersOrganizations o where u.id=b.userID and u.id=o.user.id and a.approvalID=b.id and b.status!=10 and ((a.approvalUserID=? and a.action=2) or (a.nextAcceptorID=? and a.action=1 and b.status=1 and b.approvalUsersID=? and a.date=b.date))";
//			String queryString = "select u.realName,o.organization.name,a from ApprovalTask b,ApprovalInfo a,Users u,UsersOrganizations o where u.id=a.userID and u.id=o.user.id and b.approvalID=a.id and a.status!=10 and ((b.approvalUserID=? and b.action=2) or (a.approvalUsersID=? and a.status=1))";
//			queryString = appendSort(queryString,sortField,isAsc);//加入排序
//			String queryStringCount = "select count(b.id) from ApprovalTask b,ApprovalInfo a where b.approvalID=a.id and a.status!=10 and ((b.approvalUserID=? and b.action=2) or (b.nextAcceptorID=? and b.action=1 and a.status=1 and a.approvalUsersID=? and a.date=b.date))";
//			//List<Object[]> list = jqlService.findAllBySql(start,length,queryString,Long.valueOf(approveUserId),Long.valueOf(approveUserId),approveUserId);
//			List<Object[]> list = jqlService.findAllBySql(start,length,queryString,Long.valueOf(approveUserId),approveUserId);
//			Object countObj = jqlService.getCount(queryStringCount,Long.valueOf(approveUserId),Long.valueOf(approveUserId),approveUserId);
			String queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.userID=u.id and u.id=o.user.id and a.approvalUsersID=? and (a.status=1 or a.status=2 or a.status=3 or a.status=4) ";
			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String queryStringCount ="SELECT count(distinct a.id) FROM approvalinfo a left join approvaltask b on b.approvalid=a.id "
				+" left join samesigninfo s on a.id=s.approvalid "
				+" where (a.approvalUsersID='"+approveUserId+"' or (b.approvaluserid="+approveUserId+" and b.action=2) or (s.signer_id="+approveUserId+" and s.state=1))"
				+" and a.status!=10 "
				;
			List<Object[]> list =null;// jqlService.findAllBySql(start,length,queryString,approveUserId);
			
			try
			{
				String SQL = "SELECT distinct a.id  FROM approvalinfo a left join approvaltask b on b.approvalid=a.id "
					+" left join samesigninfo s on a.id=s.approvalid "
					+" where (a.approvalUsersID='"+approveUserId+"' or (b.approvaluserid="+approveUserId+" and b.action=2) or (s.signer_id="+approveUserId+" and s.state=1))"
					+" and a.status!=10 "
					;
				
//				SQL=appendSort2(SQL,sortField,isAsc);
				SQL+=" limit "+start+","+length ;
				List<BigInteger> mylist=(List<BigInteger>)jqlService.getObjectByNativeSQL(SQL,-1,-1);
				List<Object[]> resultlist=new ArrayList<Object[]>();
				//获取所有的ID
				Long[] ids=new Long[mylist.size()];
				String cond="select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o "
					+" where a.userID=u.id and u.id=o.user.id and a.id in (0";
				for (int i=0;i<mylist.size();i++)
				{
					BigInteger obj=(BigInteger)mylist.get(i);
					ids[i]=obj.longValue();
					cond+=",?";
				}
				cond+=")";
				list =jqlService.findAllBySql(cond, ids);
				
				//需要进行排序——孙爱华
				if (list!=null)
				{
					List<Object[]> backlist=new ArrayList<Object[]>();
					for (int i=0;i<ids.length;i++)
					{
						for (int j=0;j<list.size();j++)
						{
							Object[] obj=list.get(j);
							ApprovalInfo info = (ApprovalInfo)obj[2];
							if (ids[i].longValue()==info.getId().longValue())
							{
								backlist.add(obj);
								break;
							}
						}
					}
					list=backlist;
				}
				System.out.println("====================NULL");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			List<BigInteger> countlist=(List<BigInteger>)jqlService.getObjectByNativeSQL(queryStringCount,-1,-1);
			Object countObj = 0L;
			if (countlist!=null && countlist.size()>0)
			{
				countObj=Long.valueOf(countlist.get(0).longValue());
			}
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					//ApprovalTask approveinfo = (ApprovalTask)objArray[3];
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					//bean.setUserId(approveinfo.getUserID());
					if (objArray[0]==null)
					{
						bean.setUserName("");
					}
					else
					{
						bean.setUserName(objArray[0].toString());
					}
					if (objArray[1]==null)
					{
						bean.setUserDeptName("");
					}
					else
					{
						bean.setUserDeptName((objArray[1].toString()));
					}
					
					//String filePath = approveinfo.getDocumentPath();
					//String filePath = objArray[2].toString();
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFilePath(filePath);	
					bean.setFileIcon(getFileTypeImagePath(fileName));
					//bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					bean.setStatus(approveinfo.getStatus());
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					//bean.setPredefined(approveinfo.isPredefined());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	
	/**
	 * 领导的待签批的以读或未读的文档
	 * @param approveUserId 审批者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getLeaderPaending(String approveUserId, int status, boolean isRead, int start, int length, String sortField, String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			String readFlag = isRead ? "1" : "0";
			String queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u," +
					"UsersOrganizations o where a.userID=u.id and u.id=o.user.id and a.approvalUsersID=? and a.status=? and a.isRead = ? ";
			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String queryStringCount = "select count(*) from ApprovalInfo a where a.approvalUsersID=? and a.status=?  and a.isRead = ? ";
			List<Object[]> list = null;//jqlService.findAllBySql(start,length,queryString,approveUserId,status, readFlag);
			String SQL = "select u.realname,p.name,a.id "
				+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a.userID=u.id and u.id=o.user_id and o.organization_id=p.id) "
				+" left join samesigninfo s on a.id = s.approvalID "
				+" where a.status="+status
				+" and  (a.approvalUsersID='"+approveUserId+"' or (s.signer_id="+approveUserId+" and s.state="+status+"))";
			SQL+=" group by a.id ";
			SQL=appendSort2(SQL,sortField,isAsc);
			if (start<0)
			{
				start=0;
			}
			
			if (length<0)
			{
				SQL+=" limit "+start+",10000" ;
			}
			else
			{
				SQL+=" limit "+start+","+length ;
			}
			list=getApprovalList(jqlService,SQL);
			
			
			Object countObj = jqlService.getCount(queryStringCount,approveUserId,status, readFlag);
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					if (objArray[0]==null)
					{
						bean.setUserName("");
					}
					else
					{
						bean.setUserName(objArray[0].toString());
					}
					if (objArray[1]==null)
					{
						bean.setUserDeptName("");
					}
					else
					{
						bean.setUserDeptName((objArray[1].toString()));
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					
					bean.setFileName(fileName);
					bean.setFilePath(filePath);	
					bean.setFileIcon(getFileTypeImagePath(fileName));
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					bean.setStatus(status);
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					bean.setIsRead(approveinfo.getIsRead());
					bean.setPredefined(approveinfo.isPredefined());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	
	/**
	 * 领导的待签批
	 * @param approveUserId 审批者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public long getLeaderPaendingCount(String approveUserId, int status)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String queryStringCount = "select count(*) from ApprovalInfo a where a.approvalUsersID=? and a.status=? ";
		Long ret = (Long)jqlService.getCount(queryStringCount, approveUserId, status);
		return ret == null ? 0 : ret;		
	}
	/**
	 * 领导的待签批
	 * @param approveUserId 审批者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getLeaderPaending(String approveUserId,int status,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			String queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.userID=u.id and u.id=o.user.id and a.approvalUsersID=? and a.status=? ";
			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String queryStringCount = "select count(distinct a.id) from ApprovalInfo a left join samesigninfo s on a.id = s.approvalID where (a.approvalUsersID='"+approveUserId+"' or (s.signer_id="+approveUserId+" and s.state="+status+")) and a.status="+status+" ";
			List<Object[]> list = null;// jqlService.findAllBySql(start,length,queryString,approveUserId,status);
			String SQL = "select u.realname,p.name,a.id "
				+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a.userID=u.id and u.id=o.user_id and o.organization_id=p.id) "
				+" left join samesigninfo s on a.id = s.approvalID "
				+" where a.status="+status
				+" and  (a.approvalUsersID='"+approveUserId+"' or (s.signer_id="+approveUserId+" and s.state="+status+"))";
			SQL+=" group by a.id ";
			SQL=appendSort2(SQL,sortField,isAsc);
			System.out.println(SQL);
			if (start<0)
			{
				start=0;
			}
			if (length<0)
			{
				SQL+=" limit "+start+",10000" ;
			}
			else
			{
				SQL+=" limit "+start+","+length ;
			}
			
			list=getApprovalList(jqlService,SQL);
			
			List<BigInteger> tempcountObj = (List<BigInteger>)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			Object countObj = ""+tempcountObj.get(0).longValue();//jqlService.getCount(queryStringCount,approveUserId,status);
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					bean.setSigntag(approveinfo.getSigntag());
					if (objArray[0]==null)
					{
						bean.setUserName("");
					}
					else
					{
						bean.setUserName(objArray[0].toString());
					}
					if (objArray[1]==null)
					{
						bean.setUserDeptName("");
					}
					else
					{
						bean.setUserDeptName((objArray[1].toString()));
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFilePath(filePath);	
					bean.setFileIcon(getFileTypeImagePath(fileName));
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					bean.setStatus(status);
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					bean.setIsRead(approveinfo.getIsRead());
					bean.setPredefined(approveinfo.isPredefined());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	
	
	public boolean createApprovalTask(List approveInfoIds,Long approveUserId,int status)
	{
		boolean flag = false;
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		
		int size = approveInfoIds.size();
		for(int i=0;i<size;i++)
		{
			Long approveId = Long.valueOf(approveInfoIds.get(i).toString());
			ApprovalInfo approvalInfo =(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);
			if(null!=approvalInfo)
			{
				//更新approvalinfo的status状态			
				String sql = "update ApprovalInfo as a set a.status =? where a.id = ?";
				jqlService.excute(sql,Integer.valueOf(status) ,approvalInfo.getId());	
				String docPath = approvalInfo.getDocumentPath();
				String fileName = docPath.substring(docPath.lastIndexOf('/')+1,docPath.length());
				 String versionName = fileSystemService.auditFile(approveUserId,approvalInfo.getDocumentPath());
				 ApprovalTask task = new ApprovalTask();
				 task.setApprovalID(approveId);
				 task.setApprovalUserID(approveUserId);
				 task.setDate(new Date());
				 task.setVersionName(versionName);
				 task.setAction((long)status);
				 task.setFileName(fileName);
				 jqlService.save(task);
				
			}
			flag = true;
		}
		
		
		
		return flag;
	}
	
	
	/**
	 * 领导，已签批，已退回
	 * @param approveUserId 审批者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getLeaderPassOrReturn(Long approveUserId,int status,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String queryString = "select distinct u.realName,o.organization.name,a,t.stepName from ApprovalInfo a,Users u,UsersOrganizations o,ApprovalTask t where a.userID=u.id and u.id=o.user.id and a.id=t.approvalID and a.status!=10 and t.approvalUserID=? and t.action=? ";
		queryString = appendSort(queryString,sortField,isAsc);//加入排序
		String queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t where a.id=t.approvalID and a.status!=10 and t.approvalUserID=? and t.action=?  ";
		
		//String queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.approvalUsersID=u.id and u.id=o.user.id and a.approvalUsersID=? and a.status=?";
		//queryString = appendSort(queryString,sortField,isAsc);//加入排序
		//String queryStringCount = "select count(a.id) from ApprovalInfo a where a.approvalUsersID=? and a.status=?";
		
		List<Object[]> list = jqlService.findAllBySql(start,length,queryString,approveUserId, status);
		Object countObj = jqlService.getCount(queryStringCount,approveUserId,status);
		DataHolder reHolder = new DataHolder();
		ArrayList<Object> approveList = new ArrayList<Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(null!=list&&!list.isEmpty())
		{
			int size = list.size();
			for(int i=0;i<size;i++)
			{
				Object[] objArray = (Object[])list.get(i);
				ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
				ApproveBean bean  = new ApproveBean();
				bean.setNodetype(approveinfo.getNodetype());
				bean.setStepName(objArray[3] != null ? objArray[3].toString() : "");
				bean.setApproveinfoId(approveinfo.getId());
				bean.setUserId(approveinfo.getUserID());
				if (objArray[0]==null)
				{
					bean.setUserName("");
				}
				else
				{
					bean.setUserName(objArray[0].toString());
				}
				if (objArray[1]==null)
				{
					bean.setUserDeptName("");
				}
				else
				{
					bean.setUserDeptName((objArray[1].toString()));
				}
				
				String filePath = approveinfo.getDocumentPath();
				if(null==filePath||"".equals(filePath))
				{
					continue;
				}
				int idx = filePath.lastIndexOf("/");
				String fileName = approveinfo.getFileName();
				if (fileName==null || fileName.length()==0)
				{
					fileName=filePath.substring(idx+1);
				}
				bean.setFileName(fileName);
				bean.setFileIcon(getFileTypeImagePath(fileName));
				bean.setFilePath(filePath);	
				bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
				bean.setStatus(status);
				bean.setDate(sdf.format(approveinfo.getDate()));
				bean.setComment(approveinfo.getComment());
				bean.setTitle(approveinfo.getTitle());
				approveList.add(bean);
			}
		}
		reHolder.setFilesData(approveList);
		reHolder.setIntData(Integer.valueOf(countObj.toString()));      
		return reHolder;
	
	}
	
	
	/**
	 * 领导的已办结 --只要领导审批通过并且现在approvalinfo状态是办结状态=5的文档
	 * @param approveUserId 审批者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getLeaderPaendingEnd(Long approveUserId,int status,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			String queryString = "select distinct u.realName,o.organization.name,a,t.stepName from ApprovalInfo a,Users u,UsersOrganizations o,ApprovalTask t where a.userID=u.id and u.id=o.user.id and a.id=t.approvalID and t.approvalUserID=? and t.action=2 and a.status=5";
			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t where a.id=t.approvalID and t.approvalUserID=? and t.action=2 and a.status=5  ";
			List<Object[]> list = jqlService.findAllBySql(start,length,queryString,approveUserId);
			Object countObj = jqlService.getCount(queryStringCount,approveUserId);
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					bean.setStepName(objArray[3] != null ? objArray[3].toString() : "");
					if (objArray[0]==null)
					{
						bean.setUserName("");
					}
					else
					{
						bean.setUserName(objArray[0].toString());
					}
					if (objArray[1]==null)
					{
						bean.setUserDeptName("");
					}
					else
					{
						bean.setUserDeptName((objArray[1].toString()));
					}
					
					bean.setTaskApprovalUserDept(userService.getUserDept(approveUserId));
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFileIcon(getFileTypeImagePath(fileName));
					bean.setFilePath(filePath);	
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					bean.setStatus(status);
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	
	/**
	 * 办公室人员查询已办结的文档
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public long getAllEndDocumentCount(Long ownerId,int status)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String queryStringCount = "select count(*) from ApprovalInfo a where a.status=? ";
		Long ret = (Long)jqlService.getCount(queryStringCount, status);
		return ret == null ? 0 : ret;
	}
	
	public String getOrgUsers(Long userid,JQLServices jqlService)
	{
		String usercond="(0";
		String SQL="select a.id,a.parentKey from organizations a,usersorganizations b "
			+" where a.id=b.organization_id and b.user_id="+userid;
		List<Object[]> grouplist = (List<Object[]>)jqlService.getObjectByNativeSQL(SQL,-1,-1);
		if (grouplist!=null && grouplist.size()>0)
		{
			BigInteger id=(BigInteger)(grouplist.get(0))[0];
			String groupcode=(String)(grouplist.get(0))[1];
			int index=-1;
			if (groupcode!=null)
			{
				index=groupcode.indexOf("-");
			}
			String porg="";
			if(index==-1){
				porg=""+id.longValue();
			}else{
				porg=groupcode.substring(0,index);
			}
			SQL="select b.id,b.user_id from organizations a,usersorganizations b "
				+" where a.id=b.organization_id and (a.parentKey like '"+porg+"-%' or a.id="+porg+")";
			List<Object[]> userlist = (List<Object[]>)jqlService.getObjectByNativeSQL(SQL,-1,-1);
			if (userlist!=null && userlist.size()>0)
			{
				for (int i=0;i<userlist.size();i++)
				{
					Object[] uobj=(Object[])userlist.get(i);
					usercond+=","+((BigInteger)uobj[1]).longValue();
				}
			}
		}
		usercond+=")";
		return usercond;
	}
	/**
	 * 获取本单位所有人员
	 * @param orgid
	 * @param jqlService
	 * @return
	 */
	public List<UserinfoView> getOrgUsersList(Long orgid,JQLServices jqlService)
	{
		String SQL=" select a,b from Users a,Organizations b,UsersOrganizations c "
			+" where a.id=c.user.id and b.id=c.organization.id "
			+" and (b.parentKey like '"+orgid+"-%' or b.id="+orgid+")"
			+" order by b.sortNum,a.sortNum ";
		List<Object[]> userlist = (List<Object[]>)jqlService.findAllBySql(SQL);
		if (userlist!=null && userlist.size()>0)
		{
			List<UserinfoView> viewlist =new ArrayList<UserinfoView>();
			for (int i=0;i<userlist.size();i++)
			{
				Object[] uobj=(Object[])userlist.get(i);
				Users users=(Users)uobj[0];
				Organizations org=(Organizations)uobj[1];
				UserinfoView uview=new UserinfoView();
				uview.setUserId(users.getId());
				uview.setRealName(users.getRealName());
				uview.setDepartment(org.getName());
				if (users.getIslead()!=null)
				{
					uview.setSpaceUID(""+users.getIslead());//是否领导，暂用这个字段
				}
				uview.setCompanyName(users.getDuty());//临时充当职务
				viewlist.add(uview);
			}
			return viewlist;
		}
		return null;
	}
	/**
	 * 办公室人员查询已办结的文档
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getAllEndDocument(Long ownerId,int status,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			PermissionService permservice = (PermissionService)ApplicationContext.getInstance().getBean("permissionService");
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			long permission=permservice.getSystemPermission(ownerId);//获取当前用户的权限，如果有文件管理的权限就显示本单位所有人的文件，否则只能显示本人的成文内容
			boolean ismanage = FlagUtility.isValue(permission, ManagementCons.AUDIT_MANGE_FLAG);//是否有文件管理权限
			
//			String queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.approvalUsersID=u.id and u.id=o.user.id and a.status=? ";
//			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String queryStringCount = "select count(distinct a.id) from approvalinfo a left join (users u,usersorganizations o,organizations p) "
					+" on (a.lastsignid=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
					+" where ( a.id=t.approvalID and a.status="+status
					+") and ((t.action=1 and t.approvalUserID="+ownerId+") or a.userid="+ownerId+")";
			String usercond="(0)";
			if (ismanage)//只显示自己的成文
			{
				usercond=getOrgUsers(ownerId,jqlService);
				queryStringCount = "select count(distinct a.id) from approvalinfo a left join (users u,usersorganizations o,organizations p) "
					+" on (a.lastsignid=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
					+" where ( a.id=t.approvalID and a.status="+status
					+")and ((t.action=1 and t.approvalUserID in "+usercond+") or a.userid in "+usercond+")";
			}

//			System.out.println(queryString+"==="+start+"==="+length+"==="+status);
			
			
			
			List<Object[]> list = null;// jqlService.findAllBySql(start,length,queryString,status);
			String SQL = "select distinct u.realname,p.name,a.id "
				+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a.lastsignid=u.id and u.id=o.user_id and o.organization_id=p.id) "
				+" where ( a.status="+status+")";
			if (!ismanage)//只显示自己的成文
			{
				SQL = "select distinct u.realname,p.name,a.id "
					+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
					+" on (a.lastsignid=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
					+" where ( a.id=t.approvalID and a.status="+status
					+") and ((t.action=1 and t.approvalUserID="+ownerId+") or a.userid="+ownerId+")"
					;
				
			}
			else//显示本单位所有的成文
			{
				
				SQL = "select distinct u.realname,p.name,a.id "
					+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
					+" on (a.lastsignid=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
					+" where ( a.id=t.approvalID and a.status="+status
					+")and ((t.action=1 and t.approvalUserID in "+usercond+") or a.userid in "+usercond+")"
					;
			}
			SQL+=" group by a.id ";
			SQL=appendSort2(SQL,sortField,isAsc);
			if (start<0)
			{
				start=0;
			}
			if (length<0)
			{
				SQL+=" limit "+start+",10000" ;
			}
			else
			{
				SQL+=" limit "+start+","+length ;
			}
			list=getApprovalList(jqlService,SQL);
			
			List<BigInteger> tempcountObj = (List<BigInteger>)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			Object countObj = Long.valueOf(""+tempcountObj.get(0));
			
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					Long optid=approveinfo.getOperateid();
					if (optid==null)
					{
						optid=approveinfo.getUserID();
					}
					Users user = userService.getUser(optid);
					bean.setUserName(user.getRealName());
					if (objArray[0]==null)
					{
						bean.setTaskApprovalUserName("");
					}
					else
					{
						bean.setTaskApprovalUserName(objArray[0].toString());
					}
					if (objArray[1]==null)
					{
						bean.setTaskApprovalUserDept("");
					}
					else
					{
						bean.setTaskApprovalUserDept((objArray[1].toString()));
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFileIcon(getFileTypeImagePath(fileName));
					bean.setFilePath(filePath);	
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					bean.setStatus(status);
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					bean.setIsRead(approveinfo.getIsRead());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	
	/**
	 * 办公室人员发布文档
	 * @param approveId
	 * @param publisherId
	 * @return
	 */
	public boolean publishDocument(List approveIds,Long publisherId)
	{
		boolean flag = false;
		int size = approveIds.size();
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		for(int i=0;i<size;i++)
		{
			Long approveId = Long.valueOf(approveIds.get(i).toString());
			
			ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);
			//从用户目录中拷贝一份到发布目录
			Fileinfo fileinfo = fileSystemService.publishFile(info.getDocumentPath());
			String docPath = info.getDocumentPath();
			String fileName = docPath.substring(docPath.lastIndexOf('/')+1,docPath.length());
			//新建一个approvalinfo 
//			ApprovalInfo newinfo = new ApprovalInfo();
//			newinfo.setUserID(info.getUserID());
//			newinfo.setApprovalUsersID(publisherId+"");
//			newinfo.setStatus(ApproveConstants.APPROVAL_STATUS_PUBLISH);
//			newinfo.setApprovalStep(Integer.valueOf(info.getId().toString()));//step就当是原始的approval的ID
//			newinfo.setDocumentPath(fileinfo.getPathInfo());
//			newinfo.setTitle(info.getTitle());
//			newinfo.setComment(info.getComment());
//			newinfo.setDate(new Date());
//			 jqlService.save(newinfo);
//			 info.setStatus(ApproveConstants.APPROVAL_STATUS_OVER);
			info.setOperateid(publisherId);
			info.setDate(new Date());
			info.setWarndate(new Date());
			 info.setDocumentPath(fileinfo.getPathInfo());
			 info.setStatus(ApproveConstants.APPROVAL_STATUS_PUBLISH);//孙爱华改进的，不需要在将流程数据复制一遍
			 jqlService.update(info);
			//新建一个approvaltask,跟原来的approvalinfo关联
			 ApprovalTask task = new ApprovalTask();
			 task.setApprovalID(info.getId());
			 task.setApprovalUserID(publisherId);
			 task.setDate(new Date());
			 task.setAction((long)ApproveConstants.APPROVAL_STATUS_PUBLISH);
			 task.setFileName(fileName);
			 jqlService.save(task);
		}
		flag = true;
		return flag;
	}
	
	/**
	 * 办公室人员  从已办中归档文档
	 * @param approveId
	 * @param publisherId
	 * @return
	 */
	public boolean filingDocument(List approveIds,Long archiverId)
	{
		boolean flag = false;
		int size = approveIds.size();
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		for(int i=0;i<size;i++)
		{
			Long approveId = Long.valueOf(approveIds.get(i).toString());
			
			ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);
			//从用户目录中拷贝一份到发布目录
			Fileinfo fileinfo = fileSystemService.archiveFile(info.getDocumentPath());
			String fileName = fileinfo.getFileName();
			//新建一个approvalinfo 
//			ApprovalInfo newinfo = new ApprovalInfo();
//			newinfo.setUserID(info.getUserID());
//			newinfo.setApprovalUsersID(archiverId+"");
//			newinfo.setStatus(ApproveConstants.APPROVAL_STATUS_ARCHIVING);
//			newinfo.setApprovalStep(Integer.valueOf(info.getId().toString()));//step就当是原始的approval的ID
//			newinfo.setDocumentPath(fileinfo.getPathInfo());
//			newinfo.setTitle(info.getTitle());
//			newinfo.setDate(new Date());
//			 jqlService.save(newinfo);
			 //归档时会删除原始用户目录的文档，需要重新设置原始approvalinfo的filepath
			info.setOperateid(archiverId);
			 info.setDocumentPath(fileinfo.getPathInfo());
			 info.setDate(new Date());
			 info.setWarndate(new Date());
//			 info.setStatus(ApproveConstants.APPROVAL_STATUS_OVER);
			 info.setStatus(ApproveConstants.APPROVAL_STATUS_ARCHIVING);//孙爱华改进，不需要再复制一遍流程数据
			 jqlService.update(info);
			//新建一个approvaltask,跟原来的approvalinfo关联
			 ApprovalTask task = new ApprovalTask();
			 task.setApprovalID(info.getId());
			 task.setApprovalUserID(archiverId);
			 task.setDate(new Date());
			 task.setFileName(fileName);
			 task.setAction((long)ApproveConstants.APPROVAL_STATUS_ARCHIVING);
			 jqlService.save(task);
		}
		 flag = true;
		 return flag;
	}
	
	/**
	 * 办公室人员  从发布中归档文档
	 * @param approveId
	 * @param publisherId
	 * @return
	 */
	public boolean publishToFilingDocument(List approveIds,Long archiverId)
	{
		boolean flag = false;
		int size = approveIds.size();
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		for(int i=0;i<size;i++)
		{
			Long approveId = Long.valueOf(approveIds.get(i).toString());
			ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);	
			//从用户目录中拷贝一份到发布目录
			Fileinfo fileinfo = fileSystemService.archiveFile(info.getDocumentPath());
			
			//新建一个approvalinfo 
//			ApprovalInfo newinfo = new ApprovalInfo();
//			newinfo.setUserID(info.getUserID());
//			newinfo.setApprovalUsersID(archiverId+"");
//			newinfo.setStatus(ApproveConstants.APPROVAL_STATUS_ARCHIVING);
//			newinfo.setApprovalStep(info.getApprovalStep());//step就当是原始的approval的ID
//			newinfo.setDocumentPath(fileinfo.getPathInfo());
//			newinfo.setTitle(info.getTitle());
//			newinfo.setDate(new Date());
//			 jqlService.save(newinfo);
	
//			 Long oldApproveId = Long.valueOf(info.getApprovalStep()+"");
//			 
//			 ApprovalInfo oldinfo = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, oldApproveId);
//			 oldinfo.setStatus(ApproveConstants.APPROVAL_STATUS_OVER);
			info.setStatus(ApproveConstants.APPROVAL_STATUS_ARCHIVING);//孙爱华改进，不需要再复制一遍流程数据
			info.setDate(new Date());
			info.setWarndate(new Date());
			info.setDocumentPath(fileinfo.getPathInfo());
			info.setOperateid(archiverId);
			jqlService.update(info);
			 
			 
			//新建一个approvaltask,跟原来的approvalinfo关联
			 ApprovalTask task = new ApprovalTask();
			 task.setApprovalID(info.getApprovalStep());
			 task.setApprovalUserID(archiverId);
			 task.setDate(new Date());
			 task.setAction((long)ApproveConstants.APPROVAL_STATUS_ARCHIVING);
			 task.setFileName(fileinfo.getFileName());
			 jqlService.save(task);
//			 jqlService.delete(info);
		 }
		 flag = true;
		 return flag;
	}
	
	/**
	 * 领导签批过的文档，包括通过，没有通过，办结的。
	 */
	public DataHolder getLeaderPaending(Long approveUserId, int start, int length, String sortField, String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			String queryString = "select distinct u.realName,o.organization.name,a,t.stepName from ApprovalInfo a,Users u,UsersOrganizations o,ApprovalTask t" +
					" where a.userID=u.id and u.id=o.user.id and a.id=t.approvalID and t.approvalUserID=? " +
					" and t.action in (2, 3, 4, 5, 6) and a.status != 10  ";
			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t " +
					" where a.id=t.approvalID and t.approvalUserID=? and t.action in (2, 3, 4, 5, 6) and a.status != 10  ";
			List<Object[]> list = jqlService.findAllBySql(start,length,queryString,approveUserId);
			Object countObj = jqlService.getCount(queryStringCount,approveUserId);
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					bean.setStepName(objArray[3] != null ? objArray[3].toString() : "");
					if (objArray[0]==null)
					{
						bean.setUserName("");
					}
					else
					{
						bean.setUserName(objArray[0].toString());
					}
					if (objArray[1]==null)
					{
						bean.setUserDeptName("");
					}
					else
					{
						bean.setUserDeptName((objArray[1].toString()));
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFilePath(filePath);	
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					bean.setStatus(approveinfo.getStatus());
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	/**
	 * 查询归档的文档
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 * condition 前台传过来的条件
	 * stype: type,
			     slevel : level,
			     sdescrip : descrip,
			     sfileName : fileName,
			     scontent : content,
			     scomment : comment
	 */
	public DataHolder getArchiveDocument(Long ownerId,int start ,int length,Map condition,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			PermissionService permservice = (PermissionService)ApplicationContext.getInstance().getBean("permissionService");
			long permission=permservice.getSystemPermission(ownerId);//获取当前用户的权限，如果有文件管理的权限就显示本单位所有人的文件，否则只能显示本人的成文内容
			//boolean ismanage = FlagUtility.isValue(permission, ManagementCons.AUDIT_MANGE_FLAG);//是否有文件管理权限
			boolean ismanage = FlagUtility.isValue(permission, ManagementCons.AUDIT_FILING_FLAG);//是否有文件归档权限
			String usercond="(0)";
			if (ismanage)//只显示自己的成文
			{
				usercond=getOrgUsers(ownerId,jqlService);
			}
			String searchcond="";
			if (condition!=null)
			{
				String stype=(String)condition.get("stype");
				String slevel=(String)condition.get("slevel");
				String sdescrip=(String)condition.get("sdescrip");
				String sfileName=(String)condition.get("sfileName");
				String scontent=(String)condition.get("scontent");
				String scomment=(String)condition.get("scomment");
				
				if (stype!=null && stype.length()>0)
				{
					//根据类型查找
					List list=jqlService.findAllBySql("select a from ArchiveType a where a.name=?", stype);
		        	if (list!=null && list.size()>0)
		        	{
		        		ArchiveType archiveType=(ArchiveType)list.get(0);
		        		searchcond+=" and k.type.id="+archiveType.getId();
		        	}
				}
				if (slevel!=null && slevel.length()>0)
				{
					List list=jqlService.findAllBySql("select a from ArchiveSecurity a where a.name=?", slevel);
		        	if (list!=null && list.size()>0)
		        	{
		        		ArchiveSecurity archiveSecurity=(ArchiveSecurity)list.get(0);
		        		searchcond+=" and k.security.id="+archiveSecurity.getId();
		        	}
				}
				if (sdescrip!=null && sdescrip.length()>0)
				{
					searchcond+=" and k.archivescript like '%"+sdescrip+"%'";
				}
				if (sfileName!=null && sfileName.length()>0)
				{
					searchcond+=" and a.fileName like '%"+sfileName+"%'";
				}
				if (scontent!=null && scontent.length()>0)
				{
					//暂不做全文查找
				}
			}
			String queryString = "select distinct u.realName,o.organization.name,a from ApprovalInfo a,ArchiveForm k,Users u,UsersOrganizations o,ApprovalTask t ,ArchivePermit ap "//
				+" where a.id=k.approvalinfo.id and a.userID=u.id and u.id=o.user.id and a.status=8 and a.id=t.approvalID ";
			Date curDate = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String new_date = simpleDateFormat.format(curDate);
			if (ismanage)
			{
				queryString=queryString.replace(",ArchivePermit ap", "");
				queryString+="  and ((t.action=1 and t.approvalUserID in "+usercond+") or a.userID in "+usercond+")";
			}
			else
			{
				//queryString+=" and ((t.action=1 and t.approvalUserID="+ownerId+") or a.userID="+ownerId+")";
				//没权限自己的归档也没法看,有权限谁的归档都可以看。
				queryString+=" and (t.action=1) and (a.id=ap.approvalinfo.id and ap.endDate >= '"+new_date+"' and ap.user.id="+ownerId+")";
				
			}
			queryString+=searchcond;
			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ArchiveForm k,ApprovalTask t,ArchivePermit ap where a.id=k.approvalinfo.id and a.id=t.approvalID and a.status=8 ";
			if (ismanage)
			{
				queryStringCount+="  and ((t.action=1 and t.approvalUserID in "+usercond+") or a.userID in "+usercond+")";
			}
			else
			{
				//queryStringCount+=" and ((t.action=1 and t.approvalUserID="+ownerId+") or a.userID="+ownerId+")";
				//没权限自己的归档也没法看,有权限谁的归档都可以看。
				queryStringCount+=" and (t.action=1) and (a.id=ap.approvalinfo.id and ap.endDate >= '"+new_date+"' and ap.user.id="+ownerId+")";
			}
			queryStringCount+=searchcond;
			List<Object[]> list = jqlService.findAllBySql(start,length,queryString);
			Object countObj = jqlService.getCount(queryStringCount);
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					if (objArray[0]==null)
					{
						bean.setUserName("");
					}
					else
					{
						bean.setUserName(objArray[0].toString());
					}
					if (objArray[1]==null)
					{
						bean.setUserDeptName("");
					}
					else
					{
						bean.setUserDeptName((objArray[1].toString()));
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFileIcon(getFileTypeImagePath(fileName));
					bean.setFilePath(filePath);	
//					if (approveinfo.getLastsignid()!=null)
//					{
//						Long appId = Long.valueOf(approveinfo.getLastsignid());
//						bean.setTaskApprovalUserID(""+appId);
//						bean.setTaskApprovalUserName(userService.getUser(appId).getRealName());
//						bean.setTaskApprovalUserDept(userService.getUserDept(appId));
//					}
					if (approveinfo.getOperateid()!=null)
					{
						Long appId = Long.valueOf(approveinfo.getOperateid());
						bean.setTaskApprovalUserID(""+appId);
						bean.setTaskApprovalUserName(userService.getUser(appId).getRealName());
						bean.setTaskApprovalUserDept(userService.getUserDept(appId));
					}
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					bean.setStatus(approveinfo.getStatus());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;		
	}
	
	
	/**
	 * 办公室人员销毁文档
	 * @param approveId
	 * @param publisherId
	 * @return
	 */
	public boolean toDestoryDocument(List approveIds,Long publisherId)
	{
		boolean flag = false;
		int size = approveIds.size();
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		for(int i=0;i<size;i++)
		{
			Long approveId = Long.valueOf(approveIds.get(i).toString());
			
			ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);
			String docPath = info.getDocumentPath();
			String fileName = docPath.substring(docPath.lastIndexOf('/')+1,docPath.length());
			//原始的approval
//			Long tempId = Long.valueOf(info.getApprovalStep()+"");
//			ApprovalInfo laset = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, tempId);
			info.setUserID(publisherId);//销毁者
			if (info.getLastsignid()!=null)
			{
				info.setApprovalUsersID(""+info.getLastsignid());//最后的签批者
			}
			info.setPermit(info.getStatus());
			info.setStatus(ApproveConstants.APPROVAL_STATUS_DESTROY);
			info.setDate(new Date());
			info.setWarndate(new Date());
			info.setOperateid(publisherId);//删除者
			 jqlService.update(info);
			//新建一个approvaltask,跟原来的approvalinfo关联
			 ApprovalTask task = new ApprovalTask();
			 task.setApprovalID(info.getId());//无论是从发布，还是归档，都是取原始的approvalId
			 task.setApprovalUserID(publisherId);
			 task.setDate(new Date());
			 task.setAction((long)ApproveConstants.APPROVAL_STATUS_DESTROY);
			 task.setFileName(fileName);
			 jqlService.save(task);
		}
		flag = true;
		return flag;
	}
	
	
	/**
	 * 查询待销毁的文档
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getDestoryDocument(Long ownerId,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
		try{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			PermissionService permservice = (PermissionService)ApplicationContext.getInstance().getBean("permissionService");
			long permission=permservice.getSystemPermission(ownerId);//获取当前用户的权限，如果有文件管理的权限就显示本单位所有人的文件，否则只能显示本人的成文内容
			boolean ismanage = FlagUtility.isValue(permission, ManagementCons.AUDIT_MANGE_FLAG);//是否有文件管理权限
			String usercond="(0)";
			if (ismanage)//只显示自己的成文
			{
				usercond=getOrgUsers(ownerId,jqlService);
			}
			
			
			String queryString = "select distinct u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o,ApprovalTask t "
				+" where a.userID=u.id and u.id=o.user.id and a.status=9 and a.id=t.approvalID ";
			if (ismanage)//只显示自己的销毁
			{
				queryString+=" and ((t.action=1 and t.approvalUserID in "+usercond+") or a.userID in "+usercond+")";
			}
			else
			{
				queryString+=" and ((t.action=1 and t.approvalUserID="+ownerId+") or a.userID="+ownerId+")";
			}
			
			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t where a.status=9 and a.id=t.approvalID ";
			if (ismanage)//只显示自己的成文
			{
				queryStringCount+=" and ((t.action=1 and t.approvalUserID in "+usercond+") or a.userID in "+usercond+")";
			}
			else
			{
				queryStringCount+=" and ((t.action=1 and t.approvalUserID="+ownerId+") or a.userID="+ownerId+")";
			}
			List<Object[]> list = jqlService.findAllBySql(start,length,queryString);
			Object countObj = jqlService.getCount(queryStringCount);
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					Long optid=approveinfo.getOperateid();
					if (optid==null)
					{
						optid=approveinfo.getUserID();
					}
					Users user = userService.getUser(optid);
					bean.setUserName(user.getRealName());
					if (objArray[1]!=null)
					{
						bean.setUserDeptName("");
					}
					else
					{
						bean.setUserDeptName((objArray[1].toString()));
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFileIcon(getFileTypeImagePath(fileName));
					bean.setFilePath(filePath);	
					bean.setStatus(approveinfo.getStatus());
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					if (approveinfo.getLastsignid()!=null)
					{
						Long appId = Long.valueOf(approveinfo.getLastsignid());
						bean.setTaskApprovalUserName(userService.getUser(appId).getRealName());
						bean.setTaskApprovalUserDept(userService.getUserDept(appId));
					}
					else
					{
						bean.setTaskApprovalUserName("");
						bean.setTaskApprovalUserDept("");
					}
					
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;	}catch (Exception e) {
				e.printStackTrace();
			}	
			return null;
	}
	
	/**
	 * 根据用户ID取得用户权限
	 * @param userId
	 * @return 1---送审，2---签批，3---送审+签批
	 */
	public int getUserRole(Long userId)
	{
		PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(
        "permissionService");
		long role = service.getSystemPermission(userId);
		boolean toAduit = FlagUtility.isValue(role, ManagementCons.AUDIT_SEND_FLAG);
		boolean aduit = FlagUtility.isValue(role, ManagementCons.AUDIT_AUDIT_FLAG);
		boolean managerment = FlagUtility.isValue(role, ManagementCons.AUDIT_MANGE_FLAG);
		if(toAduit&&managerment)
		{
			return 3;
		}
		if(aduit&&toAduit)
		{
			return 4;
		}
		if(toAduit)
		{
			return 1;
		}
		if(aduit)
		{
			return 2;
		}		
		return 0;
	}
	
	/**
	 * 查询领导审批的文件信息
	 */
	public DataHolder searchLeaderPaending(Long approveUserId, int condition, String keyWord, int start, int length, String sortField, String isAsc)
	{
		//0为搜索名字，1为搜索时间，2搜索来源，3搜索状态 
		//  List<Object[]> list = null;
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String queryString;
		List<Object[]> list;
		if (condition == 0)
		{
			queryString = "select distinct u.realName,o.organization.name,a,t.stepName from ApprovalInfo a,Users u,UsersOrganizations o,ApprovalTask t"
				+ " where a.userID = u.id and u.id = o.user.id and a.id = t.approvalID and t.approvalUserID = ? "		
				+ " and a.documentPath like ? ";
			queryString = appendSort(queryString,sortField,isAsc);
			list = jqlService.findAllBySql(start, length, queryString, approveUserId, "system_audit_root/%/%" + keyWord + "%");
		}
		else if (condition == 1)
		{
			String[] da = keyWord.split(";");
			Date begin = new Date(Long.valueOf(da[0]));
			Date end = new Date(Long.valueOf(da[1]));
			queryString = "select distinct u.realName,o.organization.name,a,t.stepName from ApprovalInfo a,Users u,UsersOrganizations o,ApprovalTask t"
				+ " where a.userID = u.id and u.id = o.user.id and a.id = t.approvalID and t.approvalUserID = ? "		
				+ " and (t.date BETWEEN ? and ?) ";
			queryString = appendSort(queryString,sortField,isAsc);
			list = jqlService.findAllBySql(start, length, queryString, approveUserId, begin, end);
		}
		else if (condition == 2)
		{
			queryString = "select distinct u.realName,o.organization.name,a,t.stepName from ApprovalInfo a,Users u,UsersOrganizations o,ApprovalTask t"
				+ " where a.userID = u.id and u.id = o.user.id and a.id = t.approvalID and t.approvalUserID = ? "		
				+ " and u.realName like ? ";
			queryString = appendSort(queryString,sortField,isAsc);
			list = jqlService.findAllBySql(start, length, queryString, approveUserId, keyWord);
		}
		else if (condition == 3)
		{
			queryString = "select distinct u.realName,o.organization.name,a,t.stepName from ApprovalInfo a,Users u,UsersOrganizations o,ApprovalTask t"
				+ " where a.userID = u.id and u.id = o.user.id and a.id = t.approvalID and t.approvalUserID = ? "		
				+ " and t.action = ? and a.status = ? ";
			int status = 0;
			if (keyWord.equals("待审批"))
			{
				status = ApproveConstants.APPROVAL_STATUS_PAENDING;
			}			
			if (keyWord.equals("审批通过"))
			{
				status = ApproveConstants.APPROVAL_STATUS_AGREE;
			}
			if (keyWord.equals("审批退回"))
			{
				status = ApproveConstants.APPROVAL_STATUS_RETURNED;
			}
			if (keyWord.equals("废弃"))
			{
				status =  ApproveConstants.APPROVAL_STATUS_ABANDONED;
			}
			if (keyWord.equals("已办结"))
			{
				status =  ApproveConstants.APPROVAL_STATUS_END;
			}
			if (keyWord.equals("已提交"))
			{
				status =  ApproveConstants.APPROVAL_STATUS_ENDTOOffICE;
			}
			if (keyWord.equals("已发布"))
			{
				status =  ApproveConstants.APPROVAL_STATUS_PUBLISH;
			}
			if (keyWord.equals("已归档"))
			{
				status =  ApproveConstants.APPROVAL_STATUS_ARCHIVING;
			}
			if (keyWord.equals("待销毁"))
			{
				status =  ApproveConstants.APPROVAL_STATUS_DESTROY;
			}
			queryString = appendSort(queryString,sortField,isAsc);
			list = jqlService.findAllBySql(start, length, queryString, approveUserId, status, status);
		}
		else
		{
			queryString = "select distinct u.realName,o.organization.name,a,t.stepName from ApprovalInfo a,Users u,UsersOrganizations o,ApprovalTask t"
				+ " where a.userID=u.id and u.id=o.user.id and a.id=t.approvalID and t.approvalUserID=? ";
			queryString = appendSort(queryString,sortField,isAsc);
			list = jqlService.findAllBySql(start, length, queryString, approveUserId);
		}
		//queryString = appendSort(queryString,sortField,isAsc);//加入排序
		//String queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t " +
		//		" where a.id=t.approvalID and t.approvalUserID=? and t.action in (2, 3, 4, 5, 6) and a.status in (2, 3, 4, 5, 6) ";
		//List<Object[]> list = jqlService.findAllBySql(start, length, queryString, approveUserId);
		//Object countObj = jqlService.getCount(queryStringCount,approveUserId);
		DataHolder reHolder = new DataHolder();
		ArrayList<Object> approveList = new ArrayList<Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
		if(null!=list&&!list.isEmpty())
		{
			int size = list.size();
			for(int i=0;i<size;i++)
			{
				Object[] objArray = (Object[])list.get(i);
				ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
				ApproveBean bean  = new ApproveBean();
				bean.setNodetype(approveinfo.getNodetype());
				bean.setApproveinfoId(approveinfo.getId());
				bean.setUserId(approveinfo.getUserID());
				bean.setStepName(objArray[3] != null ? objArray[3].toString() : "");
				if (objArray[0]==null)
				{
					bean.setUserName("");
				}
				else
				{
					bean.setUserName(objArray[0].toString());
				}
				if (objArray[1]==null)
				{
					bean.setUserDeptName("");
				}
				else
				{
					bean.setUserDeptName((objArray[1].toString()));
				}
				
				String filePath = approveinfo.getDocumentPath();
				if(null==filePath||"".equals(filePath))
				{
					continue;
				}
				int idx = filePath.lastIndexOf("/");
				String fileName = approveinfo.getFileName();
				if (fileName==null || fileName.length()==0)
				{
					fileName=filePath.substring(idx+1);
				}
				bean.setFileName(fileName);
				bean.setFilePath(filePath);	
				bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
				bean.setStatus(approveinfo.getStatus());
				bean.setDate(sdf.format(approveinfo.getDate()));
				bean.setComment(approveinfo.getComment());
				bean.setTitle(approveinfo.getTitle());
				approveList.add(bean);
			}
		}
		reHolder.setFilesData(approveList);
		//reHolder.setIntData(Integer.valueOf(countObj.toString()));      
		return reHolder;
		
	}
	
	/**
	 * 文件信息在整个审批流程中的各个信息
	 */
	public DataHolder getAuditFileInfo(String path, int start, int length, String sortField, String isAsc)
	{ 
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		String queryString = "select distinct u.realName, o.organization.name, t from ApprovalInfo a, Users u, UsersOrganizations o, ApprovalTask t"
				+ " where t.approvalUserID = u.id and u.id = o.user.id and a.id = t.approvalID and a.documentPath = ? ";
		queryString = appendSort(queryString,sortField,isAsc);//加入排序
		//String queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t " +
		//		" where a.id=t.approvalID and t.approvalUserID=? and t.action in (2, 3, 4, 5, 6) and a.status in (2, 3, 4, 5, 6) ";
		List<Object[]> list = jqlService.findAllBySql(start, length, queryString, path);
		//Object countObj = jqlService.getCount(queryStringCount,approveUserId);
		DataHolder reHolder = new DataHolder();
		ArrayList<Object> approveList = new ArrayList<Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
		if(null!=list&&!list.isEmpty())
		{
			int size = list.size();
			for(int i=0;i<size;i++)
			{
				Object[] objArray = (Object[])list.get(i);
				ApprovalTask approvaltask = (ApprovalTask)objArray[2];
				ApproveBean bean  = new ApproveBean();
				bean.setNodetype(approvaltask.getNodetype());
				bean.setStepName(approvaltask.getStepName());
				bean.setApproveinfoId(approvaltask.getApprovalID());
				if (objArray[0]==null)
				{
					bean.setUserName("");
				}
				else
				{
					bean.setUserName(objArray[0].toString());
				}
				if (objArray[1]==null)
				{
					bean.setUserDeptName("");
				}
				else
				{
					bean.setUserDeptName((objArray[1].toString()));
				}
				
				String filePath = path;
				if(null==filePath||"".equals(filePath))
				{
					continue;
				}
				int idx = filePath.lastIndexOf("/");
				String fileName = filePath.substring(idx+1);
				
				bean.setFileName(fileName);
				bean.setFilePath(filePath);	
				bean.setStatus(approvaltask.getAction().intValue());
				bean.setDate(sdf.format(approvaltask.getDate()));
				bean.setComment(approvaltask.getComment());
				approveList.add(bean);
			}
		}
		reHolder.setFilesData(approveList);
		//reHolder.setIntData(Integer.valueOf(countObj.toString()));      
		return reHolder;
		
	}
	
	public int getAduitFileCount(int role,Long userId)
	{
		int result = 0;
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		Object countObj = null;
		if(role==1||role==3)
		{
			String queryStringCount = "select count(*) from ApprovalInfo a where a.userID=? and a.status=1 ";
			countObj = jqlService.getCount(queryStringCount,userId);
		}
		else if(role==2)
		{
			String queryStringCount = "select count(*) from ApprovalInfo a where a.approvalUsersID=? and a.status=1 ";
			countObj = jqlService.getCount(queryStringCount,userId.toString());
		}
		if(null!=countObj)
		{
			result = Integer.valueOf(countObj.toString());
		}
		return result;
	}
	
	
	/**
	 * 查询最近送审的5个联系人
	 * @param userId
	 * @return
	 */
	public List<Map<String,Object>> getRecentLinkMan(Long userId)
	{
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String queryString = "select distinct u.id,u.realName,u.image from ApprovalInfo a,Users u,ApprovalTask t where a.id=t.approvalID and t.approvalUserID=u.id and a.userID=? and t.action=2 order by t.date desc";
		List<Object[]> list = jqlService.findAllBySql(queryString, userId);
		if(null!=list&&!list.isEmpty())
		{
			int size = list.size();
			for(int i=0;i<size;i++)
			{
				if(i==5)
				{
					break;
				}
				Map<String,Object> temp = new HashMap<String,Object>();
				Object[] objArray = (Object[])list.get(i);
				if (objArray[0]==null)
				{
					temp.put("linkManId", "");
				}
				else
				{
					temp.put("linkManId", objArray[0].toString());
				}
				if (objArray[1]==null)
				{
					temp.put("linkManName", "");
				}
				else
				{
					temp.put("linkManName", objArray[1].toString());
				}
				
				if(null== objArray[2]||"".equals(objArray[2].toString()))
				{
					temp.put("linkManImage", WebConfig.userPortrait + "image.jpg");
				}
				else
				{
					temp.put("linkManImage", WebConfig.userPortrait + objArray[2].toString());
				}
				result.add(temp);
			}
		}
		return result;
	}
	
	/**
	 * 普通用户或者办公室人员在已废弃中删除文档
	 * 文档删除，approvoalinfo,approvaltask都删除
	 * @param userId
	 * @param approvalId
	 * @return
	 */
	public boolean deleteDocument(Long userId,List approvalIds)
	{
		boolean flag = false;
		int size = approvalIds.size();
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		for(int i=0;i<size;i++)
		{
			Long approveId = Long.valueOf(approvalIds.get(i).toString());
			ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);
			String filePath = new String(info.getDocumentPath());
			String deleteInfoStr = "delete from ApprovalInfo where id=?";
			String deleteTaskStr = "delete from ApprovalTask where approvalID=?";
			//删除表数据
			jqlService.excute(deleteInfoStr, approveId);
			jqlService.excute(deleteTaskStr, approveId);
			//删除文档
			fileSystemService.delete(filePath);
		}
		flag = true;
		return flag;
	}
	
	/**
	 * 办公室人员销毁文档
	 * @param userId
	 * @param approvalId
	 * @return boolean
	 */
	public boolean destoryDocument(Long userId,List approvalIds)
	{

		boolean flag = false;
		int size = approvalIds.size();
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		for(int i=0;i<size;i++)
		{
			Long approveId = Long.valueOf(approvalIds.get(i).toString());
			ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);
			String filePath = info.getDocumentPath();
			//jqlService.delete(info);
			String deleteInfoStr = "delete from ApprovalInfo where id=?";
			String deleteTaskStr = "delete from ApprovalTask where approvalID=?";
			//删除表数据
			jqlService.excute(deleteInfoStr, approveId);
			jqlService.excute(deleteTaskStr, approveId);
			//删除文档
			fileSystemService.delete(filePath);
		}
		flag = true;
		return flag;
	
	}
	public List<Map<String, String>> getApprovalProcess(Long userId, Long approveId)
	{
		return getApprovalProcess(userId, approveId,false);
	}
	/**
	 * 得到审批的流程走向，为流程图用
	 * @param userId
	 * @param approveId
	 * @return
	 */
	public List<Map<String, String>> getApprovalProcess(Long userId, Long approveId,boolean excepMy)
	{
		ArrayList<Map<String, String>> ret = new ArrayList<Map<String, String>>();
		HashMap<String, String> hmap;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		System.out.println("approveId===="+approveId);
		ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);
		String queryString = "select u.realName, u2.realName, t from ApprovalTask t, Users u, Users u2 where t.approvalUserID = u.id " 
				+ " and t.approvalID = ? and t.nextAcceptorID = u2.id and t.action>0 order by t.date asc ";
		List<Object[]> list = jqlService.findAllBySql(queryString, info.getId());
		if(null!=list && !list.isEmpty())
		{
			int size = list.size();
			String nodeValue="";
			for(int i = 0; i < size; i++)
			{
				hmap = new HashMap<String, String>();
				Object[] objArray = (Object[])list.get(i);
				if (objArray[0]!=null)
				{
					if (i>0 && excepMy && nodeValue.equals(objArray[0].toString()))
					{
						continue;
					}
					else if (i==0)
					{
						nodeValue=objArray[0].toString();
					}
				}
				ApprovalTask approvaltask = (ApprovalTask)objArray[2];
				hmap.put("id", String.valueOf(i));
				if (i + 1 == size)
				{
					hmap.put("next", String.valueOf(-1));
				}
				else
				{
					hmap.put("next", String.valueOf(i + 1));
				}
				hmap.put("approvalID", String.valueOf(approveId));
				hmap.put("action",String.valueOf(approvaltask.getAction()));
				if (objArray[0]==null)
				{
					hmap.put("nodeValue", "");
				}
				else
				{
					hmap.put("nodeValue", objArray[0].toString());
				}
				
				if (approvaltask.getNodetype()!=null)
				{
					hmap.put("nodetype", ""+approvaltask.getNodetype().intValue());
				}
				if (approvaltask.getSameid()!=null && approvaltask.getSameid().intValue()>0)
				{
					hmap.put("sameid", ""+approvaltask.getSameid().intValue());
				}
				int actionFlag = approvaltask.getAction().intValue();
				String tipMeg = "";
				String str1="";
				if (objArray[0]!=null)
				{
					str1=objArray[0].toString();
				}
				String str2="";
				if (objArray[1]!=null)
				{
					str2=objArray[1].toString();
				}
				switch (actionFlag)
				{
					case 0 : tipMeg = "转PDF";break;
					case 1 : tipMeg = str1+"送审 该文档给"+str2;break;
					case 2 : tipMeg = str1+"签批 该文档给"+str2;break;
					case 3 : tipMeg = str1+"退回 该文档给"+str2;break;
					case 4 : tipMeg = str1+"成文 该文档";break;
					case 5 : tipMeg = str1+"发布 该文档";break;
					case 6 : tipMeg = str1+"已归档 该文档";break;
					case 7 : tipMeg = str1+"待销毁 该文档";break;
					case 8 : tipMeg = str1+"已终止 该文档";break;
				}
				hmap.put("tipMeg", tipMeg);
				hmap.put("time", sdf.format(approvaltask.getDate()));
				ret.add(hmap);
			}
		}
		return ret;
	}
	
	public Map<String,Object> getApprovalHistoryForReader(Long userId, Long readerId)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		ApprovalReader ar = (ApprovalReader)jqlService.getEntity(ApprovalReader.class, readerId);
		return getApprovalHistory(userId, ar.getApprovalInfoId());
		
	}
	
	public Map<String,Object> getApprovalHistory(Long userId, Long approveId)
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
		String queryString = "select u.realName, u2.realName, t from ApprovalTask t, Users u, Users u2 "
			+ " where t.approvalUserID = u.id and t.approvalID = ? and nextAcceptorID = u2.id ";
		List<Object[]> list = jqlService.findAllBySql(queryString, info.getId());
	
		if(info.getApprovalStep()!=0)//说明是从发布或者归档点过来的,要从step字段中取旧的approvalID,task中的 approvalId都是根据旧的approvalinfo来的
		{ 
			//审批第几步
			Long oldId = Long.valueOf(info.getApprovalStep()+"");
			ApprovalInfo oldinfo = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, oldId);
			if(oldinfo.getDocumentPath()==null||"".equals(oldinfo.getDocumentPath()))
			{
				int idx = info.getDocumentPath().lastIndexOf("/");
				fileName = info.getDocumentPath().substring(idx+1);
				result.put("fileName",info.getFileName() );
				ownerName = userService.getUser(oldinfo.getUserID()).getRealName();			
				date = sdf.format(oldinfo.getDate());	
				result.put("owner", ownerName);	
				if(null!=list&&!list.isEmpty())
				{
					Object[] tempobj=(Object[])list.get(0);
					result.put("createDate", sdf.format(((ApprovalTask)tempobj[2]).getDate()));
				}
				else
				{
					result.put("createDate", date);
				}
				int tempidx = fileName.lastIndexOf(".");
				String endName = fileName.substring(tempidx+1);
				result.put("fileType", WebofficeUtility.getFileIconPathSrc(endName,false,false,false,"48", ".png"));
				info = oldinfo;
			}
			else
			{
				info = oldinfo;
				int idx = info.getDocumentPath().lastIndexOf("/");
				fileName = info.getDocumentPath().substring(idx+1);
				result.put("fileName",info.getFileName() );
				ownerName = userService.getUser(info.getUserID()).getRealName();				
				date = sdf.format(info.getDate());	
				result.put("owner", ownerName);		
				if(null!=list&&!list.isEmpty())
				{
					Object[] tempobj=(Object[])list.get(0);
					result.put("createDate", sdf.format(((ApprovalTask)tempobj[2]).getDate()));
				}
				else
				{
					result.put("createDate", date);
				}
				int tempidx = fileName.lastIndexOf(".");
				String endName = fileName.substring(tempidx+1);
				result.put("fileType", WebofficeUtility.getFileIconPathSrc(endName,false,false,false,"48", ".png"));
			}			
		}		
		else
		{
			int idx = info.getDocumentPath().lastIndexOf("/");
			fileName = info.getDocumentPath().substring(idx+1);
			result.put("fileName",info.getFileName());
			ownerName = userService.getUser(info.getUserID()).getRealName();				
			date = sdf.format(info.getDate());	
			result.put("owner", ownerName);		
			if(null!=list&&!list.isEmpty())
			{
				Object[] tempobj=(Object[])list.get(0);
				result.put("createDate", sdf.format(((ApprovalTask)tempobj[2]).getDate()));
			}
			else
			{
				result.put("createDate", date);
			}
			int tempidx = fileName.lastIndexOf(".");
			if (tempidx>0)
			{
				String endName = fileName.substring(tempidx+1);
				result.put("fileType", WebofficeUtility.getFileIconPathSrc(endName,false,false,false,"48", ".png"));
			}
			else
			{
				result.put("fileType", WebofficeUtility.getFileIconPathSrc("eio",false,false,false,"48", ".png"));
			}
		}
		
		
		
		
		queryString = " select u.realName, r from ApprovalReader r, ApprovalTask t, Users u where t.approvalUserID = r.sendUser" 
				+ " and r.approvalInfoId = t.approvalID  and r.userId = u.id and t.id = ? ";
		List<Object[]> ar;
		if(null!=list&&!list.isEmpty())
		{
			int size = list.size();
			for(int i=0;i<size;i++)
			{
				Object[] objArray = (Object[])list.get(i);
				ApprovalTask approvaltask = (ApprovalTask)objArray[2];
				Map temp = new HashMap();
				if (objArray[0]!=null)
				{
					temp.put("actor", objArray[0].toString());
				}
				
				temp.put("action",approvaltask.getAction());
//				if(approvaltask.getAction()==ApproveConstants.APPROVAL_STATUS_AGREE){
//					queryString = "select u.realName from Users u where u.id=?";
//					List resultTemp = jqlService.findAllBySql(queryString, approvaltask.getNextAcceptorID());
//					if(!resultTemp.isEmpty())
//					{
//					String nextName = (String) resultTemp.get(0);
//					temp.put("nextActor", nextName);}else{
//						temp.put("nextActor", ownerName);
//					}
//				}
				temp.put("nextActor", objArray[1].toString());
				temp.put("time", sdf.format(approvaltask.getDate()));
				temp.put("comment", approvaltask.getComment()==null?"":approvaltask.getComment());
				temp.put("fileName", approvaltask.getFileName());
				temp.put("versionName", approvaltask.getVersionName());
				temp.put("step", approvaltask.getStepName());
				ar = (List<Object[]>)jqlService.findAllBySql(queryString, approvaltask.getId());
				StringBuffer tempS = new StringBuffer();
				boolean flag = false;
				for (Object[] tr : ar)
				{
					if (flag)
					{
						tempS.append("<p>");
					}
					tempS.append(tr[0]);
					tempS.append(":");
					tempS.append(((ApprovalReader)tr[1]).isRead() ? "已经阅读" : "未阅读" );
					tempS.append("&nbsp;备注信息为：");
					tempS.append(((ApprovalReader)tr[1]).getComment() != null ? ((ApprovalReader)tr[1]).getComment() : "");
					flag = true;					
				}
				if (flag)
				{
					temp.put("reader", tempS.toString());
				}
				datalist.add(temp);
			}
		}
		result.put("history", datalist);
		
		HashMap hash=getFlowPicData(userId, approveId);
		List<Map<String, String>> flowlist=(List<Map<String, String>>)hash.get("flowlist");
		result.put("nodeList", flowlist);
		//以下是查出流程中未签批的人员
		
		List<WorkFlowPicBean> flowpiclist=(List<WorkFlowPicBean>)hash.get("flowpiclist");
		List<String> auditlist=(List<String>)hash.get("auditlist");
		String flowpic=WorkFlowPic.getInstance().getApprovePic(userId, approveId,flowpiclist,auditlist);
		result.put("flowpic",flowpic);
		
		return result;
	}
	public HashMap getFlowPicData(Long userId, Long approveId)
	{
		HashMap hash=new HashMap();
		
		List<Map<String, String>> flowlist = getApprovalProcess(userId, approveId,false);//true表示过滤发送者
		hash.put("flowlist", flowlist);//存放所有历史记录信息
		List<WorkFlowPicBean> flowpiclist=new ArrayList<WorkFlowPicBean>();
		List<String> auditlist=new ArrayList<String>();
		if (flowlist!=null && flowlist.size()>0)
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			
			ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, approveId);
			List<Long> lista=info.getPreUserIds();
			if (info.getApprovalUsersID()!=null && info.getUserID()!=null
					&& Long.parseLong(info.getApprovalUsersID().trim())!=info.getUserID().longValue() )
			{
				if (lista==null && info.getStatus()==ApproveConstants.APPROVAL_STATUS_PAENDING)//只有待签批状态approvaluserid才有效
				{
					lista=new ArrayList<Long>();
				}
				if (lista!=null)
				{
					lista.add(0,Long.valueOf(info.getApprovalUsersID().trim()));
				}
			}
			
			if (lista!=null && lista.size()>0)
			{
				//根据ID获取ID对应的用户
				Long[] values=new Long[lista.size()];
				String SQL="from Users where id in (0";
				for (int i=0;i<lista.size();i++)
				{
					SQL+=",?";
					values[i]=lista.get(i);
				}
				SQL+=")";
				List ulist=jqlService.findAllBySql(SQL, values);
				for (int i=0;i<lista.size();i++)
				{
					for (int j=0;j<ulist.size();j++)
					{
						Users users=(Users)ulist.get(j);
						if (lista.get(i)!=null && lista.get(i).longValue()==users.getId().longValue())
						{
							auditlist.add(users.getRealName());
							break;
						}
					}
				}
			}
	
			//需要对flowlist进行改造,将会签的数据放在一个数组中
			
			if (flowlist!=null && flowlist.size()>0)
			{
				int fsize=flowlist.size();
				boolean currentSign=false;
				List<SameSignInfo> samelist=(List<SameSignInfo>)jqlService.findAllBySql("select model from SameSignInfo as model,ApprovalInfo as a "
						+" where model.approvalID=a.id and a.nodetype=1 and model.approvalID=? order by model.isnew desc, model.sid ", info.getId());
				List<SameSignInfo> copesamelist= new ArrayList<SameSignInfo>();
				copesamelist.addAll(samelist);
				
				if (info.getNodetype()!=null && info.getNodetype().intValue()==1)
				{
					//当前是会签
					int samesize=samelist.size();
					Integer signnum=samelist.get(0).getSignnum();//最后一次签批的节点数
					
					for (int j=0;j<samelist.size();j++)
					{
						SameSignInfo sameinfo=samelist.get(j);
						for (int i=0;i<fsize;i++)
						{
							Map<String, String> map=flowlist.get(i);
							String samestr=map.get("sameid");
							if (samestr!=null)//开始会签
							{
								if (sameinfo.getSid().longValue()==Long.parseLong(samestr))
								{
									samelist.remove(j);
									j--;
									break;
								}
							}
						}
					}
					if (signnum>samelist.size())
					{
						currentSign=true;//当前送的会签，已经有人签批了，在task表中有了对应的记录
					}
					//剩下的samelist中是没有签批的会签预设
				}
				
				boolean startsame=false;
				List<String> timelist=new ArrayList<String>();
				List<String> namelist=new ArrayList<String>();
				List<String> actionlist=new ArrayList<String>();
				for (int i=0;i<flowlist.size();i++)
				{
					Map<String, String> map=flowlist.get(i);
					WorkFlowPicBean bean=new WorkFlowPicBean();
					String nodeValue=map.get("nodeValue");//签批人
					String time=map.get("time");//签批时间
					String action=map.get("action");
					String samestr=map.get("sameid");
					Integer sameid=null;
					if (i==0)//只有第一次显示送审人，后面就不要显示
					{
						timelist=new ArrayList<String>();
						namelist=new ArrayList<String>();
						actionlist=new ArrayList<String>();
						timelist.add(time);
						namelist.add(nodeValue);
						actionlist.add(action);
						startsame=false;
					}
					else
					{
						if (samestr!=null)//开始会签
						{
							sameid=Integer.valueOf(samestr);
							Long isnew=null;
							for (int s=0;s<copesamelist.size();s++)
							{
								if (copesamelist.get(s).getSid().longValue()==(long)sameid)
								{
									isnew=copesamelist.get(s).getIsnew();
									break;
								}
							}
							timelist.add(time);
							namelist.add(nodeValue);
							actionlist.add(action);
							if (i<(flowlist.size()-1))//当没有达到最后一条记录时，判断有没有会签记录
							{
								Map<String, String> nextmap=flowlist.get(i+1);
								String nextsamestr=nextmap.get("sameid");
								Long nextisnew=null;
								for (int s=0;s<copesamelist.size();s++)
								{
									if (String.valueOf(copesamelist.get(s).getSid()).equals(nextsamestr))
									{
										nextisnew=copesamelist.get(s).getIsnew();
										break;
									}
								}
								
								//判断下一条记录是不是会签，如果为会签就将人和事件进行综合
								//接下来再进行会签有问题
								if (nextsamestr!=null)
								{
									if (isnew!=null && nextisnew!=null && isnew.longValue()!=nextisnew.longValue())//当都不是空，且不相等时表示不是一个流程节点
									{
										startsame=false;
									}
									else
									{
										startsame=true;
									}
								}
								else
								{
									startsame=false;
								}
							}
							else
							{
								startsame=false;
							}
						}
						else
						{
							timelist=new ArrayList<String>();
							namelist=new ArrayList<String>();
							actionlist=new ArrayList<String>();
							actionlist.add(action);
							timelist.add(time);
							namelist.add(nodeValue);
							startsame=false;
						}
					}
					if (!startsame)
					{
						String nodetype = (String)map.get("nodetype");//节点类型
						bean.setNodetype(Integer.valueOf(0));//暂时先考虑串行已签的
						
						bean.setNodeValues(getStrs(namelist));
						bean.setTimes(getStrs(timelist));
						bean.setActions(getStrs(actionlist));
						Integer[] states=new Integer[namelist.size()];
						for (int n=0;n<namelist.size();n++)
						{
							states[n]=2;
						}
						bean.setStates(states);//串行并行一起考虑
						if (namelist.size()>1)
						{
							bean.setNodetype(Integer.valueOf(1));//nodetype是为了历史记录的列表显示缩进用的
						}
						else
						{
							bean.setNodetype(Integer.valueOf(0));
						}
						flowpiclist.add(bean);
						timelist=new ArrayList<String>();
						namelist=new ArrayList<String>();
						actionlist=new ArrayList<String>();
					}
				}
				if (samelist.size()>0)
				{
					if (currentSign==true)//表示会签已经有部分进行了签批，将没有会签完的记录放到流程节点中，用灰色的线表示
					{
						WorkFlowPicBean bean=flowpiclist.get(flowpiclist.size()-1);
						int slen=bean.getNodeValues().length;
						int templen=slen+samelist.size();
						String[] names=new String[templen];//临时这样写
						String[] times=new String[templen];
						Integer[] states=new Integer[templen];
						String[] actions=new String[templen];
						for (int i=0;i<slen;i++)
						{
							names[i]=bean.getNodeValues()[i];
							times[i]=bean.getTimes()[i];
							states[i]=bean.getStates()[i];
							actions[i]="2";
						}
						for (int i=0;i<samelist.size();i++)
						{
							SameSignInfo sameinfo=samelist.get(i);
							names[slen+i]=sameinfo.getSigner().getRealName();
							times[slen+i]="";
							states[slen+i]=1;
							actions[slen+i]="2";
						}
						bean.setNodetype(Integer.valueOf(1));//暂时先考虑串行已签的
						bean.setNodeValues(names);
						bean.setTimes(times);
						bean.setStates(states);
						bean.setActions(actions);
						flowpiclist.remove(flowpiclist.size()-1);
						flowpiclist.add(bean);
					}
					else
					{
						//直接在流程节点中加会签记录（还未开始会签）
						WorkFlowPicBean bean=new WorkFlowPicBean();
						int len=samelist.size();
						String[] names=new String[len];
						String[] times=new String[len];
						Integer[] states=new Integer[len];
						String[] actions=new String[len];
						for (int i=0;i<len;i++)
						{
							SameSignInfo same=samelist.get(i);
							names[i]=same.getSigner().getRealName();
							times[i]="";
							states[i]=1;
							actions[i]="2";
						}
						bean.setNodetype(Integer.valueOf(1));//暂时先考虑串行已签的
						bean.setNodeValues(names);
						bean.setTimes(times);
						bean.setStates(states);//签批状态，1为待签，2为已签
						bean.setActions(actions);
						flowpiclist.add(bean);
					}
				}
			}
		}
		hash.put("flowpiclist", flowpiclist);//存放已签或会签的流程信息
		hash.put("auditlist", auditlist);//存放未签的信息
		return hash;
	}
	private String[] getStrs(List<String> list)
	{
		if (list!=null)
		{
			String[] strs=new String[list.size()];
			for (int i=0;i<list.size();i++)
			{
				strs[i]=list.get(i);
			}
			return strs;
		}
		return null;
	}
	private String replaceStatus(int status)
	{
		String result="";
		if(status==ApproveConstants.APPROVAL_STATUS_AGREE)
		{
			result = "签批";
		}else if(status==ApproveConstants.APPROVAL_STATUS_RETURNED)
		{
			result = "退回";
		}else if(status==ApproveConstants.APPROVAL_STATUS_ABANDONED)
		{
			result = "已废弃";
		}
		else if(status==ApproveConstants.APPROVAL_STATUS_PAENDING)
		{
			result = "送审";
		}
		else if(status==ApproveConstants.APPROVAL_STATUS_ARCHIVING)
		{
			result = "已归档";
		}
		else if(status==ApproveConstants.APPROVAL_STATUS_PUBLISH)
		{
			result = "发布";
		}
		else if(status==ApproveConstants.APPROVAL_STATUS_END)
		{
			result = "办结";
		}
		return result;
	}
	
	/**
	 * 根据文件名获取文件对应的图标
	 * @param fileName 文件名
	 * @return
	 */
	private String getFileTypeImagePath(String fileName)
	{
		if (fileName==null)
		{
			return "<img src='images/green/version.png'></img>";
		}
		else
		{
			int idx = fileName.lastIndexOf(".");
			String endName = fileName.substring(idx+1);
			String img =  WebofficeUtility.getFileIconPathSrc(endName,false,false,false,null, ".gif");
			String html = "<img src='"+img+"'></img>";
			return html;
		}
	}
	
	public void readApproval(Long readId,int isread,int isreadFlod,Long userId)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		
		if (isreadFlod==1)
		{
			ApprovalReader info = (ApprovalReader)jqlService.getEntity(ApprovalReader.class, readId);
			if(info.getUserId().longValue()==userId.longValue())
			{
				String sql="update ApprovalReader a set a.isRead = true,a.date = ? where a.id = ? ";
				jqlService.excute(sql,new Date(),readId);
			}
		}
		else
		{
			ApprovalInfo info = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, readId);
			if(info.getUserID().longValue()!=userId.longValue())
			{
				if (isread==1)
				{
					String sql = "update ApprovalInfo as a set a.isRead= 1,a.signreader= "+userId.longValue()+" where a.id = ?";
					jqlService.excute(sql,readId);
					
				}
				else
				{
					String sql = "update ApprovalInfo as a set a.signreader= "+userId.longValue()+" where a.id = ?";
					jqlService.excute(sql,readId);
				}
			}
		}
	}
	
	/**
	 * 设置送阅的文件为已经阅读状态
	 * @param readId
	 * @param userId
	 * @param comment 
	 */
	public void readApproval(Long readId, Long userId, String comment)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String sql = "update ApprovalReader a set a.isRead = true, a.comment = ?, a.date = ? "
					+ " where a.id = ? ";
		jqlService.excute(sql, comment, new Date(), readId);	
		
			
	}
	
	/**
	 * 为首页使用，普通人员的已签批或者退回文档
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public long getMyPaendingPassOrReturnCount(Long ownerId)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String queryStringCount = "select count(*) from ApprovalInfo a where a.userID=? and (a.status=2 or a.status=3) ";
		Long ret = (Long)jqlService.getCount(queryStringCount,ownerId);
		return ret == null ? 0 : ret; 
	}
	  /**
	 * 为首页使用，普通人员的已签批或者退回文档
	 * @param ownerId 送审者
	 * @param filePath 文件路径
	 * @param status 文件状态
	 * @param start 
	 * @param length
	 * @return List<ApprovalInfo>
	 */
	public DataHolder getMyPaendingPassOrReturn(Long ownerId,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			String queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.approvalUsersID=u.id and u.id=o.user.id and a.userID=? and  (a.status=2 or a.status=3)";
			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String queryStringCount = "select count(*) from ApprovalInfo a where a.userID=? and (a.status=2 or a.status=3) ";
			List<Object[]> list = jqlService.findAllBySql(start,length,queryString,ownerId);
			Object countObj = jqlService.getCount(queryStringCount,ownerId);
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					Users user = userService.getUser(approveinfo.getUserID());
					bean.setUserName(user.getRealName());
					if (objArray[0]==null)
					{
						bean.setTaskApprovalUserName("");
					}
					else
					{
						bean.setTaskApprovalUserName(objArray[0].toString());
					}
					if (objArray[1]==null)
					{
						bean.setTaskApprovalUserDept("");
					}
					else
					{
						bean.setTaskApprovalUserDept((objArray[1].toString()));
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFileIcon(getFileTypeImagePath(fileName));
					
					bean.setFilePath(filePath);	
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					bean.setStatus(approveinfo.getStatus());
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					bean.setIsRead(approveinfo.getIsRead());
					bean.setPredefined(approveinfo.isPredefined());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	
	
	/**
	 * 根据用户ID取得用户权限
	 * @param userId
	 * @return 1---送审，2---签批，3---送审+签批
	 */
	public ArrayList filterNoRoleFile(Long userId,List list)
	{
		ArrayList result = new ArrayList();
		try 
		{
			PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean("permissionService");
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object file = (Object)list.get(i);
					Boolean folder = (Boolean)((HashMap)file).get("expandable");
					if(folder.booleanValue())
					{
						result.add(file);
						continue;
					}
					else
					{
						String path = ((HashMap)file).get("id").toString();
						long role = service.getFileSystemAction(userId,path,true);
						boolean hasRole = FlagUtility.isValue(role, FileSystemCons.WRITE_FLAG);
						if(hasRole)
						{
							result.add(file);
						}
					}
				}
			}
			/*if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object file = (Object)list.get(i);
					Boolean folder = (Boolean)((HashMap)file).get("folder");
					if(null==folder)
					{
						result.add(file);
						continue;
					}
					else if(null!=folder&&folder.booleanValue())
					{
						continue;
					}
					else
					{
						String path = ((HashMap)file).get("path").toString();
						
						if(path.startsWith("group_"))
						{
							long role = service.getFileSystemAction(userId,path,true);
							boolean hasRole = FlagUtility.isValue(role, FileSystemCons.WRITE_FLAG);
							if(hasRole)
							{
								result.add(file);
							}
						}
						else
						{
							result.add(file);
						}
					}
				}
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	
		
	}
	
	/**
	 * 获得各项文档的数目
	 * @param userId 用户ID
	 * @param userPerm 用户权限
	 * @return 文档数目
	 */
	public Map<String,Object> getFileCount(Long userId, Map<String, Boolean> userPerm)
	{
		Map<String, Object> result = new HashMap<String, Object>();
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String queryStringCount = "";
		//未阅
		queryStringCount = "select count(*) from ApprovalReader a, ApprovalInfo ai, Users u where a.sendUser = u.id and a.userId = ? and a.isRead = ? " +
				"and ai.status != ? and ai.id = a.approvalInfoId";
		Object approveReadCount = jqlService.getCount(queryStringCount,userId,false, ApproveConstants.APPROVAL_STATUS_ABANDONED);
		//已阅
		queryStringCount = "select count(*) from ApprovalReader a, Users u, ApprovalInfo ai  where a.sendUser = u.id and a.userId=? and a.isRead=?" +
				" and ai.status != ? and ai.id = a.approvalInfoId ";
		Object approveReadedCount = jqlService.getCount(queryStringCount,userId,true, ApproveConstants.APPROVAL_STATUS_ABANDONED);
		result.put("approveReadCount", Integer.valueOf(approveReadCount.toString()));
		result.put("approveReadedCount", Integer.valueOf(approveReadedCount.toString()));
		if(userPerm.get("sendManage"))//送审
		{
			//已审
//			queryStringCount = "select count(distinct a.id) from approvalinfo a,approvaltask t "
//				+" where a.id=t.approvalid and a.status="+ApproveConstants.APPROVAL_STATUS_AGREE
//				+" and ((t.resendtag=1 and t.approvalUserID="+userId.longValue()
//				+") or (t.action=1 and t.approvalUserID="+userId.longValue()+") or a.userid="+userId.longValue()+")";

			queryStringCount="select count(distinct a.id) from approvalinfo a,approvaltask t "
			+" where a.id=t.approvalid and ( a.status="+ApproveConstants.APPROVAL_STATUS_AGREE
			+" and (t.action=1 or t.resendtag=1) and t.approvalUserID="+userId.longValue()+")";
			ArrayList approvedCount = (ArrayList)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			
			//待审批
			queryStringCount = "select count(distinct a.id) from approvalinfo a ,approvaltask t "
					+" where a.id=t.approvalid and ( a.status="+ApproveConstants.APPROVAL_STATUS_PAENDING
					+" and (t.action=1 or t.resendtag=1) and t.approvalUserID="+userId.longValue()+")";
			ArrayList approvingCount = (ArrayList)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			
			//终止
			queryStringCount = "select count(distinct a.id) "+"from ApprovalInfo a,ApprovalTask t "
			+" where a.id=t.approvalID and a.status=? "
			+" and (t.resendtag=1 or t.action=1) and (t.approvalUserID="+userId.longValue()+" or a.userID="+userId.longValue()+")";
			Object endedCount = jqlService.getCount(queryStringCount,ApproveConstants.APPROVAL_STATUS_ABANDONED);
			//所有
			queryStringCount="select count(distinct a.id) "
			+" from approvalinfo a,approvaltask t "
			+" where a.id=t.approvalid and (a.status=0 or a.status=1 or a.status=2 or a.status=3 or a.status=4) "
			+" and (t.resendtag=1 or t.action=1) and (t.approvalUserID="+userId.longValue()+" or a.userid="+userId.longValue()+")";
			ArrayList totalMyCount = (ArrayList)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			try
			{
				result.put("totalMyCount", Integer.valueOf(totalMyCount.get(0).toString()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			
			result.put("toApprovedCount", Integer.valueOf(approvedCount.get(0).toString()));
			result.put("toApprovingCount", Integer.valueOf(approvingCount.get(0).toString()));
			result.put("endedCount", Integer.valueOf(endedCount.toString()));
		}
		if(userPerm.get("acceptManage"))//审批
		{	
			//待签批
//			queryStringCount = "select count(*) from ApprovalInfo a where a.approvalUsersID=? and a.status=? ";
//			Object approvingCount = jqlService.getCount(queryStringCount,userId.toString(),ApproveConstants.APPROVAL_STATUS_PAENDING);
			queryStringCount = "SELECT count(distinct a.id)  FROM approvalinfo a left join samesigninfo s on a.id=s.approvalid "
			+" where (a.approvalUsersID='"+userId.longValue()+"' or (s.signer_id="+userId.longValue()
			+" and s.state="+ApproveConstants.APPROVAL_STATUS_PAENDING+")) and a.status="+ApproveConstants.APPROVAL_STATUS_PAENDING+"";
			
			Object tlist=jqlService.getObjectByNativeSQL(queryStringCount,-1,-1);
			BigInteger tobj=(BigInteger)((List)tlist).get(0);
			Object approvingCount=tobj.longValue();
			//已签批
			queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t where a.id=t.approvalID and a.status!=10 "
				+" and t.approvalUserID=? and t.action=? ";
			Object approvedCount = jqlService.getCount(queryStringCount,userId,ApproveConstants.APPROVAL_STATUS_AGREE);
			
			
			queryStringCount="SELECT count(distinct a.id)  FROM approvalinfo a left join approvaltask b on b.approvalid=a.id "
			+" left join samesigninfo s on a.id=s.approvalid "
			+" where (a.approvalUsersID='"+userId+"' or (b.approvaluserid="+userId+" and b.action=2) or (s.signer_id="+userId+" and s.state=1))"
			+" and a.status!=10 ";
			List<BigInteger> totallist=(List<BigInteger>)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			try
			{
				if (totallist!=null && totallist.size()>0)
				{
					result.put("approvedCount",Integer.valueOf(""+totallist.get(0).longValue()));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			result.put("approvingCount", Integer.valueOf(approvingCount.toString()));
			result.put("approvedCount", Integer.valueOf(approvedCount.toString()));
		}      
//		if(userPerm.get("officeManage"))//管理
        {
        	//已成文
			PermissionService permservice = (PermissionService)ApplicationContext.getInstance().getBean("permissionService");
			long permission=permservice.getSystemPermission(userId);//获取当前用户的权限，如果有文件管理的权限就显示本单位所有人的文件，否则只能显示本人的成文内容
			boolean ismanage = FlagUtility.isValue(permission, ManagementCons.AUDIT_MANGE_FLAG);//是否有文件管理权限
			String usercond="(0)";
			if (ismanage)//只显示自己的成文
			{
				usercond=getOrgUsers(userId,jqlService);
			}
			queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t where  a.id=t.approvalID and a.status=? ";
			if (ismanage)
			{
				queryStringCount+=" and ((t.action=1 and t.approvalUserID in "+usercond+") or a.userID in "+usercond+")";
			}
			else
			{
				queryStringCount+=" and ((t.action=1 and t.approvalUserID="+userId+") or a.userID="+userId+")";
			}
			Object endCount = jqlService.getCount(queryStringCount,ApproveConstants.APPROVAL_STATUS_END);
			//已归档
			queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t where a.id=t.approvalID and a.id=t.approvalID and a.status=? ";
			if (ismanage)
			{
				queryStringCount+=" and ((t.action=1 and t.approvalUserID in "+usercond+") or a.userID in "+usercond+")";
			}
			else
			{
				queryStringCount+=" and ((t.action=1 and t.approvalUserID="+userId+") or a.userID="+userId+")";
			}
			Object archivingCount = jqlService.getCount(queryStringCount,ApproveConstants.APPROVAL_STATUS_ARCHIVING);
			//已发布,只要根据单位来就可以了
			if (!ismanage)//只显示自己的成文
			{
				String tempcond=getOrgUsers(userId,jqlService);
				queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t where  a.id=t.approvalID and a.status=? ";
				queryStringCount+=" and ((t.action=1 and t.approvalUserID in "+tempcond+") or a.userID in "+tempcond+")";
			}
			else
			{
				queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t where  a.id=t.approvalID and a.status=? ";
				queryStringCount+=" and ((t.action=1 and t.approvalUserID in "+usercond+") or a.userID in "+usercond+")";
			}
			Object publishedCount = jqlService.getCount(queryStringCount,ApproveConstants.APPROVAL_STATUS_PUBLISH);
			//待销毁
			queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t where  a.id=t.approvalID and a.status=? ";
			if (ismanage)
			{
				queryStringCount+=" and ((t.action=1 and t.approvalUserID in "+usercond+") or a.userID in "+usercond+")";
			}
			else
			{
				queryStringCount+=" and ((t.action=1 and t.approvalUserID="+userId+") or a.userID="+userId+")";
			}
			Object destroyCount = jqlService.getCount(queryStringCount,ApproveConstants.APPROVAL_STATUS_DESTROY);
			result.put("finishedCount", Integer.valueOf(endCount.toString()));
			result.put("archivedCount", Integer.valueOf(archivingCount.toString()));
			result.put("destroyingCount", Integer.valueOf(destroyCount.toString()));
			result.put("publishedCount", Integer.valueOf(publishedCount.toString()));
        }

		return result;
	}
	
/* ----------------- ---------------------------------------- */	

/**
 * 文档初次送审
 * @param userId 送审用户ID
 * @param filePath 文件路径
 * @param fileName 文件名称
 * @param acceptId 接收者ID
 * @param comment 备注
 * @param title
 * @param readerIds 阅读者列表
 * @param preUserIds 预先定义多步审批操作的用户id列表，审批顺序为list的顺序。如果没有有预定义的多步，则该值为null 
 */
public void addAduit(Long userId, String filePath, String fileName,	String acceptId, 
		String comment, String title, ArrayList<Long> readerIds, ArrayList<Long> preUserIds, String stepName,int issame)
{
		FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		Fileinfo info = fileSystemService.addAuditFile(userId, filePath);
		ApprovalInfo approval = createApproval(userId, info.getPathInfo(), fileName, acceptId, comment, title, preUserIds, stepName,issame);
		createApprovalTask(approval.getId(),userId,userId,acceptId, info.getPathInfo(), ApproveConstants.APPROVAL_STATUS_PAENDING,
				comment,info.getVersionName(), readerIds, title, stepName,issame,null,false);
		/*if(readerIds!=null && !readerIds.isEmpty())
		{
			 for(Long readerId : readerIds)
			 createApprovalReader(approval.getId(),readerId,title,info.getVersionName(),fileName,false,userId,comment);
		}*/
			
}

/**
 * 审批过的文档再次送审，此方法应该与首次送审的合并
 * @param userId 送审用户ID
 * @param filePath 文档路径
 * @param acceptId 接收者ID
 * @param comment 备注
 * @param title 
 * @param readerIds 阅读者列表
 * @param preUserIds 预先定义多步审批操作的用户id列表，审批顺序为list的顺序。如果没有有预定义的多步，则该值为null 
 * @return 在送审是否成功
 */
public boolean reAddAduit(Long userId, String filePath,
		String acceptId, String comment, String title, ArrayList<Long> readerIds, ArrayList<Long> preUserIds, String stepName,int issame)
{
	FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
	String versionName = fileSystemService.auditFile(userId,filePath);
	boolean flag = false;
	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	String queryString = "from ApprovalInfo a where a.documentPath=? and a.userID=?";		
	List list = jqlService.findAllBySql(queryString, filePath,userId);
	Date upDate = new Date();
	if(null!=list&&!list.isEmpty())
	{
		ApprovalInfo approval = (ApprovalInfo)list.get(0);
		approval.setStatus(ApproveConstants.APPROVAL_STATUS_PAENDING);
        approval.setDate(upDate);
        approval.setWarndate(new Date());
        approval.setComment(comment);
        approval.setNodetype(issame);
        approval.setOperateid(userId);
        if (issame==1)
        {
       	 //会签，不给人
        }
        else
        {
       	 approval.setApprovalUsersID(acceptId);
        }
        
        
        approval.setTitle(title);
        approval.setStepName(stepName);
        if (preUserIds != null && preUserIds.size() > 0  && issame==0)
        {
        	approval.setPredefined(true);
        	approval.setPreUserIds(preUserIds);
        	
        }
        else
        {
            approval.setPredefined(false);
            approval.setPreUserIds(null);
        }
        ArrayList<Long> sendhistory=approval.getSendhistory();
        if (sendhistory==null)
        {
        	sendhistory=new ArrayList<Long>();
        	sendhistory.add(userId);
        }
        else
        {
        	boolean isadd=true;
//        	for (int h=0;h<sendhistory.size();h++)
//        	{
//        		Long history=sendhistory.get(h);
//        		if (history!=null && history.longValue()!=userId.longValue())
//        		{
//        			isadd=false;
//        			break;
//        		}
//        	}
        	if (isadd)
        	{
        		sendhistory.add(userId);
        	}
        }
        
        approval.setSendhistory(sendhistory);//孙爱华增加送审历史记录
        if (preUserIds!=null && preUserIds.size()==0 && (acceptId==null || "".equals(acceptId)))
        {
        	approval.setStatus(0);//再次送审，只有阅读人
        }
        jqlService.update(approval);
        
        //这个方法应该与首次送审合并的，临时这样加
        if (preUserIds!=null && preUserIds.size()>0 && issame==1)
        {
        	Long maxid=(Long)jqlService.getCount("select max(a.isnew) from SameSignInfo as a where a.approvalID=?", approval.getId());
	       	if (maxid==null)
	       	{
	       		maxid=1L;
	       	}
	       	else
	       	{
	       		maxid++;
	       	}
        	for (int i=-1;i<preUserIds.size();i++)
	       	 {
		         SameSignInfo sameinfo=new SameSignInfo();
		         sameinfo.setSignnum(preUserIds.size()+1);
		         sameinfo.setApprovalID(approval.getId());
		         if (i<0)
		         {
		        	 sameinfo.setSigner((Users)jqlService.getEntity(Users.class, Long.valueOf(acceptId)));
		         }
		         else
		         {
		        	 sameinfo.setSigner((Users)jqlService.getEntity(Users.class, preUserIds.get(i)));
		         }
		         sameinfo.setState(ApproveConstants.APPROVAL_STATUS_PAENDING);
		         sameinfo.setCreateDate(new Date());
		         sameinfo.setWarndate(new Date());
		         sameinfo.setComment(comment);
		         sameinfo.setFileName(filePath.substring(filePath.lastIndexOf("/")+1, filePath.length()));
		         sameinfo.setFilePath(filePath);
		         sameinfo.setIsnew(maxid);
		         jqlService.save(sameinfo);
	       	 }
        }
        
        
        
        createApprovalTask(approval.getId(),userId,userId,acceptId,approval.getDocumentPath(), 
        		ApproveConstants.APPROVAL_STATUS_PAENDING, comment,versionName, readerIds, title,upDate, stepName,0,null);
        /*String fileName = approval.getDocumentPath().substring(approval.getDocumentPath().lastIndexOf('/')+1,approval.getDocumentPath().length());
        if(readerIds!=null && !readerIds.isEmpty())
   	 	{
   		 for(Long readerId : readerIds)
   		 createApprovalReader(approval.getId(),readerId,title,versionName,fileName,false,userId,comment);
   	 	}*/
        flag = true;
        
	}
	return flag;
	
}
/**
 * 移动端再次送审
 * @param userId
 * @param filePath
 * @param acceptId
 * @param comment
 * @param title
 * @param readerIds
 * @param preUserIds
 * @param stepName
 * @param issame
 * @return
 */
public boolean retempAddAduit(Long userId, Long id,
		String acceptId, String comment, String title, ArrayList<Long> readerIds, ArrayList<Long> preUserIds, String stepName,int issame)
{
	String filePath=null;
	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, id);
	filePath=approvalInfo.getDocumentPath();
	return reAddAduit(userId, filePath, acceptId, comment, title, readerIds, preUserIds, stepName, issame);
}


/**
 * 上传的文档进行送审
 * @param userId 送审者ID
 * @param upFilePath 上传文档路径
 * @param fileName 文件名
 * @param acceptId 接收者ID
 * @param comment 备注
 * @param title 
 * @param readerIds 阅读者列表
 * @param preUserIds 预先定义多步审批操作的用户id列表，审批顺序为list的顺序。如果没有有预定义的多步，则该值为null 
 * @throws Exception 异常信息
 */
public void upAddAduit(Long userId, String upFilePath, String fileName,
		String acceptId, String comment, String title, ArrayList<Long> readerIds, ArrayList<Long> preUserIds, String stepName,int issame) throws Exception 
		{
	 String tempPath = WebConfig.tempFilePath + File.separatorChar;
	 File file = new File(tempPath + upFilePath);
	 InputStream fin = new FileInputStream(file);
     InputStream ois = new FileInputStream(file);
     FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
     Fileinfo info = fileSystemService.addAuditFile(userId, fileName, fin,  ois);
     fin.close();
     file.delete();
     ApprovalInfo approval = createApproval(userId, info.getPathInfo(), fileName, acceptId, comment,title, preUserIds, stepName,issame);
	 createApprovalTask(approval.getId(), userId, userId, acceptId, approval.getDocumentPath(), 
			 ApproveConstants.APPROVAL_STATUS_PAENDING, comment, info.getVersionName(), readerIds, approval.getTitle(), stepName,issame,null,false);
	 /*if(readerIds!=null && !readerIds.isEmpty())
	 {
		 for(Long readerId : readerIds)
		 createApprovalReader(approval.getId(),readerId,title,info.getVersionName(),fileName,false,userId,comment);
	 }*/
}


/**
 * 签批文档，更新当前操作信息和操作记录信息	
 * @param approvalId 当前操作信息ID
 * @param userId 当前操作用户ID
 * @param status 操作类型，签批操作类型
 * @param comment 备注
 * @param acceptId 接收者ID
 * @param readerIds 阅读者列表
 * @param preUserIds 预先定义多步审批操作的用户id列表，审批顺序为list的顺序。如果没有有预定义的多步，则该值为null 
 * nextissame 会签标记,0为顺签，1为会签
 */
public boolean aduitOperation(ApprovalInfo approvalinfo, Long userId, String acceptId,int status,
		String comment, ArrayList<Long> readerIds, ArrayList<Long> preUserIds, String stepName,int nextissame,int resend) 
{
	//try
	{
		String oldAccepId = acceptId;
		Long lastsignid=userId;
		//updateApproval(approvalinfo.getId(), acceptId, comment,false,status);
		boolean b = acceptId == null;
		FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		String versionName = fileSystemService.auditFile(userId,approvalinfo.getDocumentPath());
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		//approvalinfo = (ApprovalInfo) jqlService.getEntity(ApprovalInfo.class, approvalinfo.getId());
		int issame=0;
		int taskissame=0;
		Long sendid=null;
		int taskstatus=status;
		Long sameid=null;
		List<Long> oldsign=new ArrayList<Long>();
		if(null==acceptId || "".equals(acceptId))
		{			
			approvalinfo = (ApprovalInfo) jqlService.getEntity(ApprovalInfo.class, approvalinfo.getId());
			sendid=approvalinfo.getUserID();
			Long tempId;
			if (approvalinfo.isPredefined()!=null && approvalinfo.isPredefined())    // 有预定义的人员
			{
				List<Long> preU = approvalinfo.getPreUserIds();
				oldsign = approvalinfo.getPreUserIds();
				if (preU==null || preU.size()==0)
				{
					tempId = approvalinfo.getUserID();
				}
				else
				{
					tempId = preU.get(0);
					preU.remove(0);             // 移出预定义人员
					if (preU.size() == 0)       // 预定义人员都处理完成
					{
						status=1;
						approvalinfo.setPredefined(false);
						approvalinfo.setPreUserIds(null);
						approvalinfo.setApprovalUsersID(String.valueOf(tempId));
					}
					else
					{
						status=1;//没有处理完还是待审状态
						approvalinfo.setStatus(status);
						approvalinfo.setApprovalUsersID(String.valueOf(tempId));
					}
					jqlService.update(approvalinfo);
					oldAccepId = String.valueOf(tempId);
				}
			}
			else
			{				
				tempId = approvalinfo.getUserID();
			}
			acceptId = String.valueOf(tempId);
			
			if (approvalinfo.getNodetype()!=null && approvalinfo.getNodetype()==1)
			{
				//更新会签状态
				
				//查询会签有没有全部结束，结束后将acceptId赋值为送审人，否则还是NULL——孙爱华
				List<SameSignInfo> samelist=(List<SameSignInfo>)jqlService.findAllBySql("select model from SameSignInfo as model where model.state="
						+ApproveConstants.APPROVAL_STATUS_PAENDING+" and model.approvalID=?", approvalinfo.getId());
				SameSignInfo mysame=null;
				if (samelist!=null)
				{
					for (int i=0;i<samelist.size();i++)
					{
						if (samelist.get(i).getSigner().getId().longValue()==userId.longValue())
						{
							mysame=samelist.get(i);
							mysame.setState(status);
							mysame.setApprovalDate(new Date());
							mysame.setDeleted(1);//表示已处理过,2表示在办
							jqlService.update(mysame);
							issame=1;
							taskissame=1;
							sameid=mysame.getSid();
							break;
						}
					}
				}
				if (samelist==null || samelist.size()<2)
				{
					acceptId=String.valueOf(approvalinfo.getUserID());
					oldAccepId=null;
					lastsignid=null;
					issame=1;//暂不要将流程节点复原
				}
				else
				{
					acceptId = sendid.toString();
					status=approvalinfo.getStatus();
					oldAccepId=null;
					lastsignid=null;
				}
			}
		}
		else
		{
			status=1;//没有处理完还是待审状态
		}
		updateApproval(approvalinfo.getId(), oldAccepId, comment, false, status, stepName,lastsignid,userId);
		boolean addhistory=false;//是否有送审标记
		if (resend==1)
		{
			addhistory=true;
		}
		ArrayList<Long> sendhistory=((ApprovalInfo) jqlService.getEntity(ApprovalInfo.class, approvalinfo.getId())).getSendhistory();
		if (addhistory)//串签不一定是送审
		{
			if (sendhistory==null)
	        {
	        	sendhistory=new ArrayList<Long>();
	        	sendhistory.add(userId);
	        }
	        else
	        {
	        	boolean isadd=true;
//	        	for (int h=0;h<sendhistory.size();h++)
//	        	{
//	        		Long history=sendhistory.get(h);
//	        		if (history!=null && history.longValue()!=userId.longValue())
//	        		{
//	        			isadd=false;
//	        			break;
//	        		}
//	        	}
	        	if (isadd)
	        	{
	        		sendhistory.add(userId);
	        	}
	        }
			approvalinfo.setSendhistory(sendhistory);
		}
		if (preUserIds != null && preUserIds.size() > 0 && oldAccepId!=null && oldAccepId.length()>0)    // 又有新预定义人员
        {
			//如果选择了下一个人
			sendhistory=approvalinfo.getSendhistory();
	        
			if (nextissame==1)
			{
				Long maxid=1L;
				Object tempobj=jqlService.getCount("select max(a.isnew) from SameSignInfo as a where a.approvalID=?", approvalinfo.getId());
		       	if (tempobj==null)
		       	{
		       		maxid=1L;
		       	}
		       	else
		       	{
		       		maxid=(Long)tempobj;
		       		maxid++;
		       	}
		       	approvalinfo = (ApprovalInfo) jqlService.getEntity(ApprovalInfo.class, approvalinfo.getId());
	        	for (int i=-1;i<preUserIds.size();i++)
		       	 {
			         SameSignInfo sameinfo=new SameSignInfo();
			         sameinfo.setSignnum(preUserIds.size()+1);
			         sameinfo.setApprovalID(approvalinfo.getId());
			         if (i<0)
			         {
			        	 sameinfo.setSigner((Users)jqlService.getEntity(Users.class, Long.valueOf(oldAccepId)));
			         }
			         else
			         {
			        	 sameinfo.setSigner((Users)jqlService.getEntity(Users.class, preUserIds.get(i)));
			         }
			         sameinfo.setState(ApproveConstants.APPROVAL_STATUS_PAENDING);
			         sameinfo.setCreateDate(new Date());
			         sameinfo.setWarndate(new Date());
			         sameinfo.setComment(comment);
			         sameinfo.setFileName(approvalinfo.getFileName());
			         sameinfo.setFilePath(approvalinfo.getDocumentPath());
			         sameinfo.setIsnew(maxid);
			         jqlService.save(sameinfo);
		       	 }
	        	
	        	approvalinfo.setNodetype(nextissame);
	        	approvalinfo.setApprovalUsersID(null);
	        	approvalinfo.setStatus(1);
	        	approvalinfo.setPredefined(false);
	        	approvalinfo.setPreUserIds(null);
	        	approvalinfo.setSendhistory(sendhistory);
	        	if (addhistory)
	        	{
	        		approvalinfo.setUserID(userId);//如果再次进行送审，就需要将送审者更改
	        	}
		       	jqlService.update(approvalinfo);
			}
			else
			{
				approvalinfo = (ApprovalInfo) jqlService.getEntity(ApprovalInfo.class, approvalinfo.getId());
				approvalinfo.setPredefined(true);
		       	approvalinfo.setPreUserIds(preUserIds);
		       	approvalinfo.setSendhistory(sendhistory);
		       	if (addhistory)
	        	{
	        		approvalinfo.setUserID(userId);//如果再次进行送审，就需要将送审者更改
	        	}
		       	jqlService.update(approvalinfo);
			}
			
        }
		else if (addhistory)
		{
			approvalinfo = (ApprovalInfo) jqlService.getEntity(ApprovalInfo.class, approvalinfo.getId());
			approvalinfo.setSendhistory(sendhistory);
        	approvalinfo.setUserID(userId);//如果再次进行送审，就需要将送审者更改
        	jqlService.update(approvalinfo);
		}
		createApprovalTask(approvalinfo.getId(), null, userId, acceptId, approvalinfo.getDocumentPath(),
				taskstatus, comment, versionName, readerIds, approvalinfo.getTitle(), stepName,taskissame,sameid,addhistory);
	}
	//catch(Exception e)
	{
		//e.printStackTrace();
		//return false;
	}
	return false;
}	

/**
 * 批阅
 * @param id 批阅记录ID
 * @param comment 备注
 */
public void piyue(Long id, String comment) {
	updateApprovalReader(id, comment, true);
}
	
/*----------------------------------------- 新处理的方法 -----------------------------------------------*/
private void createApprovalTask(Long approvalInfoId, Long ownerId, Long approvalUserId, String approvalAcpId,
		String docPath, int status, String comment, String versionName, ArrayList<Long> readerIds, String title, String stepName,int issame)
{
	createApprovalTask(approvalInfoId, ownerId, approvalUserId, approvalAcpId,
			docPath, status, comment, versionName, readerIds, title, stepName,issame,null,false);
}
	/**
	 * 创建审批记录
	 * @param approvalInfoId 审批信息ID
	 * @param ownerId 文档拥有者ID
	 * @param approvalUserId 发送者ID
	 * @param approvalAcpId 接收者ID
	 * @param docPath 文件路径
	 * @param status 状态
	 * @param comment 批注
	 * @param title 标题
	 * @param readerIds 阅读者列表
	 * @return
	 */
	private void createApprovalTask(Long approvalInfoId, Long ownerId, Long approvalUserId, String approvalAcpId,
			String docPath, int status, String comment, String versionName, ArrayList<Long> readerIds, String title, String stepName
			,int issame,Long sameid,boolean issend)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		 String fileName = docPath.substring(docPath.lastIndexOf('/')+1,docPath.length());
		 ApprovalTask task = new ApprovalTask();
		 task.setApprovalID(approvalInfoId);
		 task.setApprovalUserID(approvalUserId);
		 task.setNextAcceptorID(!"".equals(approvalAcpId) && !"null".equals(approvalAcpId)?Long.parseLong(approvalAcpId):null);
		 task.setDate(new Date());
		 task.setComment(comment);
		 task.setVersionName(versionName);
		 task.setAction((long)status);
		 task.setFileName(fileName);
		 task.setTitle(title);
		 task.setNodetype(issame);//并发流程标记
		 task.setStepName(stepName);		 
		 task.setReaders(readerIds);
		 task.setSameid(sameid);
		 if (issend)
		 {
			 task.setResendtag(1);
		 }
		 jqlService.save(task);
		 if (readerIds != null && readerIds.size() > 0)     // 阅读人员
		 {
			 jqlService.excute("update ApprovalReader as a set a.isnew=0 where a.approvalInfoId=?",approvalInfoId);
			 ArrayList<ApprovalReader> arList = new ArrayList<ApprovalReader>();
			 for(Long temp : readerIds)
			 {
				 ApprovalReader ar = new ApprovalReader();
				 ar.setApprovalInfoId(approvalInfoId);
				 ar.setUserId(temp);
				 ar.setReadUser(approvalUserId);
				 ar.setPath(docPath);
//				 ar.setPath(versionName);
				 ar.setVersion(versionName);
				 ar.setTitle(title);
				 ar.setFileName(fileName);
				 ar.setRead(false);
				 ar.setStepName(stepName);
				 ar.setComment(comment);
				 ar.setIsnew(1);
				 ar.setSenddate(new Date());
				 arList.add(ar);					
			 }
			 jqlService.saveAll(arList);
		 }		 
	}	
	

	private void createApprovalTask(Long approvalInfoId, Long ownerId, Long approvalUserId, String approvalAcpId,
			String docPath, int status, String comment, String versionName, ArrayList<Long> readerIds, String title,
			Date upDate, String stepName,int issame,Long sameid)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		 String fileName = docPath.substring(docPath.lastIndexOf('/')+1,docPath.length());
		 ApprovalTask task = new ApprovalTask();
		 task.setApprovalID(approvalInfoId);
		 task.setApprovalUserID(approvalUserId);
		 task.setStepName(stepName);
		 task.setNextAcceptorID(!"".equals(approvalAcpId) && !"null".equals(approvalAcpId)?Long.parseLong(approvalAcpId):null);
		 task.setDate(upDate);
		 task.setComment(comment);
		 task.setVersionName(versionName);
		 task.setAction((long)status);
		 task.setFileName(fileName);
		 task.setTitle(title);
		 task.setNodetype(issame);
		 task.setReaders(readerIds);
		 task.setSameid(sameid);
		 jqlService.save(task);
		 if (readerIds != null && readerIds.size() > 0)     // 阅读人员
		 {
			 jqlService.excute("update ApprovalReader as a set a.isnew=0 where a.approvalInfoId=?",approvalInfoId);
			 ArrayList<ApprovalReader> arList = new ArrayList<ApprovalReader>();
			 for(Long temp : readerIds)
			 {
				 ApprovalReader ar = new ApprovalReader();
				 ar.setApprovalInfoId(approvalInfoId);
				 ar.setUserId(temp);
				 ar.setReadUser(approvalUserId);
				 ar.setPath(docPath);
				 ar.setStepName(stepName);
//				 ar.setPath(versionName);
				 ar.setVersion(versionName);
				 ar.setTitle(title);
				 ar.setFileName(fileName);
				 ar.setRead(false);
				 ar.setComment(comment);
				 ar.setIsnew(1);
				 ar.setSenddate(new Date());
				 arList.add(ar);					
			 }
			 jqlService.saveAll(arList);
		 }	
		
	}
	
	/**
	 * 创建审批文件当前步骤
	 * @param userId 文件拥有者ID
	 * @param filePath 文件路径
	 * @param fileName 文件名称
	 * @param acceptId 接收者ID
	 * @param comment 备注
	 * @param title 标题
	 * @param preUserId 预先定义多步审批操作的用户id列表，审批顺序为list的顺序。如果没有有预定义的多步，则该值为null
	 */
	private ApprovalInfo createApproval(Long userId, String filePath, String fileName, String acceptId, 
			String comment, String title, ArrayList<Long> preUserIds, String stepName,int issame)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		ApprovalInfo approvalInfo = new ApprovalInfo();
         // 送审用户
         approvalInfo.setUserID(userId);
         approvalInfo.setOperateid(userId);
         // 审批状态
         if ((preUserIds != null && preUserIds.size() > 0)
        		 || (acceptId!=null && acceptId.length()>0))
         {
        	 approvalInfo.setStatus(ApproveConstants.APPROVAL_STATUS_PAENDING);
         }
         else
         {
        	 approvalInfo.setStatus(ApproveConstants.APPROVAL_STATUS_START);//仅阅读
         }
         
         // 送审日期
         approvalInfo.setDate(new Date());
         approvalInfo.setWarndate(new Date());
         approvalInfo.setModifytime(new Date());
         // 送审说明
         approvalInfo.setComment(comment);
         // 送审文档路径
         approvalInfo.setDocumentPath(filePath);
         approvalInfo.setNodetype(issame);
         if(filePath!=null && "".equals(filePath))
         {
        	 approvalInfo.setFileName(filePath.substring(filePath.lastIndexOf("/")+1, filePath.length()));
         }
         if (issame==1)
         {
        	 //会签，不给人
         }
         else
         {
        	 approvalInfo.setApprovalUsersID(acceptId);
         }
         approvalInfo.setTitle(title);
         approvalInfo.setStepName(stepName);
         if (fileName==null || fileName.length()==0)
         {
        	 fileName="null.doc";
         }
         approvalInfo.setFileName(fileName);
         if (preUserIds != null && preUserIds.size() > 0 && issame==0)
         {
        	 approvalInfo.setPredefined(true);
        	 approvalInfo.setPreUserIds(preUserIds);
         }
         ArrayList<Long> sendhistory=new ArrayList<Long>();
         sendhistory.add(userId);
         approvalInfo.setSendhistory(sendhistory);//孙爱华增加送审历史记录
         jqlService.save(approvalInfo);
         if (preUserIds!=null && preUserIds.size()>0 && issame==1)
         {
        	 
        	 for (int i=-1;i<preUserIds.size();i++)
        	 {
		         SameSignInfo sameinfo=new SameSignInfo();
		         sameinfo.setSignnum(preUserIds.size()+1);
		         sameinfo.setApprovalID(approvalInfo.getId());
		         if (i<0)
		         {
		        	 sameinfo.setSigner((Users)jqlService.getEntity(Users.class, Long.valueOf(acceptId)));
		         }
		         else
		         {
		        	 sameinfo.setSigner((Users)jqlService.getEntity(Users.class, preUserIds.get(i)));
		         }
		         sameinfo.setState(ApproveConstants.APPROVAL_STATUS_PAENDING);
		         sameinfo.setCreateDate(new Date());
		         sameinfo.setWarndate(new Date());
		         sameinfo.setComment(comment);
		         sameinfo.setFileName(fileName);
		         sameinfo.setFilePath(filePath);
		         jqlService.save(sameinfo);
        	 }
         }
         return approvalInfo;
	}

	/**
	 * 更新当前审批文档信息
	 * @param approvalId 当前审批文档信息ID
	 * @param acceptId 接收者ID
	 * @param comment 批注
	 * @param status 状态
	 */
	private void updateApproval(Long approvalId,String acceptId,String comment,boolean isDate,int status, String stepName,Long lastsignid,Long userId)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String dates = isDate?",a.date=now() ":" ";
		//更新approvalinfo的status状态
		if(null!=acceptId&&!"".equals(acceptId))
		{
			String sql = "update ApprovalInfo as a set a.lastsignid=?,a.status =?,a.comment = ?, a.isRead = 0, a.approvalUsersID = ?, a.stepName = ?,a.modifytime=? " + dates + ",a.operateid=? where a.id = ? ";
			jqlService.excute(sql,lastsignid,status, comment, acceptId, stepName,new Date(),userId, approvalId);	
				
		}
		else
		{
			String sql = "update ApprovalInfo as a set a.approvalUsersID = null,a.lastsignid=?,a.status =?,a.comment=?,a.isRead=0,a.stepName = ?,a.modifytime=? "+dates+" ,a.operateid=? where a.id=?";
			jqlService.excute(sql,lastsignid,status,comment, stepName ,new Date(),userId,approvalId);	
		}
		
	}
	
	/**
	 * 创建一个批阅记录
	 * @param approveId 关联的审批记录的ID
	 * @param readerId 阅读者ID
	 * @param title 标题
	 * @param versionName 路径
	 * @param fileName 文件名
	 * @param isRead 是否已读
	 * @param userId 发送者ID
	 * @param comment 备注
	 */
	private void createApprovalReader(Long approveId, Long readerId, String title,
			String versionName, String fileName, boolean isRead, Long userId,
			String comment) {
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
//		jqlService.excute("update ApprovalReader as a set a.isnew=0 where a.approvalInfoId=?",approveId);
		ApprovalReader approvalReader = new ApprovalReader();
		approvalReader.setApprovalInfoId(approveId);
		approvalReader.setUserId(readerId);
		approvalReader.setTitle(title);
		approvalReader.setPath(versionName);
		approvalReader.setFileName(fileName);
		approvalReader.setRead(isRead);
		approvalReader.setReadUser(userId);
		approvalReader.setDate(new Date());
		approvalReader.setComment(comment);
		approvalReader.setIsnew(1);
		jqlService.save(approvalReader);
	}
	
	/**
	 * 更新当前批阅文档信息
	 * @param id 当前批阅文档信息ID
	 * @param comment 批注
	 */
	private void updateApprovalReader(Long id,String comment,boolean isRead)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String sql = "update ApprovalReader as a set a.comment=?,a.isRead=?,a.date=now() where a.id=?";
		jqlService.excute(sql,comment,isRead,id);	
	}
	/**
	 * 批量转PDF 0表示成功
	 * @param ids 流程编号
	 * @return
	 */
	public String changePdf(int[] ids,Users user)
	{
		try
		{
			//先根据id获取流程数据（地址）,然后再判断对应的pdf文件是否存在
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
			int len=ids.length;
			ApprovalInfo[] approvalInfos=new ApprovalInfo[len];
			String[] filesizes=new String[len];//存放文件大小
			String[] filePaths=new String[len];//存放文件路径
			String[] sourceFileName=new String[len];//转换前的服务器上的文件绝对路径
			String[] targetFileName=new String[len];//转换前的服务器上的pdf绝对路径
			
			for (int i=0;i<len;i++)//由于后台没有提供根据多个编号查实体的方法，只能一个个的查了
			{
				approvalInfos[i] = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, (long)ids[i]);//获取流程实体对象
				//检查pdf的文件是否存在，如果存在就退出
				String docpath=approvalInfos[i].getDocumentPath();
				int index=docpath.lastIndexOf("/");
				int indexD=docpath.lastIndexOf(".");
				String targetpath=docpath.substring(0,index);
				String fileName = docpath.substring(index+1,indexD)+".pdf";
				String changePath=targetpath+"/"+fileName;
				if (jcrService.isFileExist(changePath))
				{
					return fileName+"-已经存在！";
				}
			}
			
			for (int i=0;i<len;i++)//开始进行转换
			{
				filePaths[i]=approvalInfos[i].getDocumentPath();//获取流程文件路径
				//根据路径获取文件，先放到tempfile目录下，然后再删除
				Object[] obj=getFile(filePaths[i]);
				if (obj!=null)
				{
					filesizes[i]=(String)obj[1];
					sourceFileName[i]=(String)obj[2];
					if (sourceFileName[i]!=null)
					{
						int index = sourceFileName[i].lastIndexOf(".");
						targetFileName[i]=sourceFileName[i].substring(0,index)+".pdf";
						int convertresult = ConvertForRead.convertMStoPDF(sourceFileName[i], targetFileName[i]);//转换PDF
						if (convertresult!=0)
						{
							return approvalInfos[i].getFileName()+"-转换失败!";
						}
					}
				}
				else
				{
					return approvalInfos[i].getFileName()+"-获取文件失败!";
				}
			}
			//集中更新
			//对原文件进行重命名，然后再增加一条approvaltask记录，最后更新流程表，然后再删除中间的文件
			for (int i=0;i<len;i++)
			{
				 String versionName=jcrService.createVersion(filePaths[i], user.getRealName(), 
	                	"转PDF", MainConstants.APPROVAL_VERSION_STATUS_CURRENT);
				 //版本创建完后再进行重命名
				 List<Versioninfo> oldlist=jcrService.getAllVersion(filePaths[i]);//获取所有的旧版本
				 if (oldlist!=null)
				 {
					 for (int j=0;j<oldlist.size();j++)
					 {
						 Versioninfo versioninfo=oldlist.get(j);
						 System.out.println(versioninfo.getPath());
					 }
				 }
				 int index=filePaths[i].lastIndexOf("/");
				 int indexD=filePaths[i].lastIndexOf(".");
				 String targetpath=filePaths[i].substring(0,index);
				 String fileName = filePaths[i].substring(index+1,indexD)+"(a"+approvalInfos[i].getId()+").pdf";//防止重名，增加一个流程编号
				 String changePath=targetpath+"/"+fileName;//更改后的名称
				 
				 jcrService.rename(null, filePaths[i], fileName);//对原文件进行重命名

				 File file=new File(targetFileName[i]);
				 FileInputStream stream = new FileInputStream(file);
				 jcrService.updateFile(changePath, stream, Integer.parseInt(filesizes[i]));//更新文件,将pdf文件流进行保存
				 stream.close();
				 
				 List<Versioninfo> versionlist=jcrService.getAllVersion(changePath);//获取所有的版本
				 if (versionlist!=null)
				 {
					 for (int j=0;j<versionlist.size();j++)
					 {
						 Versioninfo versioninfo=versionlist.get(j);
						 System.out.println(versioninfo.getPath());
					 }
				 }
				 
				 
				 ApprovalTask task = new ApprovalTask();
				 task.setApprovalID(ids[i]);
				 task.setApprovalUserID(user.getId());
				 task.setStepName("转PDF");
				 task.setNextAcceptorID(user.getId());
				 task.setDate(new Date());
				 task.setComment(user.getRealName()+"转PDF");
				 task.setVersionName(versionName);
				 task.setAction((long)0);
				 task.setFileName(approvalInfos[i].getFileName());//保存的是原来的DOC
				 task.setTitle(approvalInfos[i].getTitle());
				 task.setNodetype(0);
				 jqlService.save(task);//插入转换记录
				 
				 
				 approvalInfos[i].setDocumentPath(changePath);//更改路径
				 approvalInfos[i].setFileName(fileName);//更改名称
				 approvalInfos[i].setChangepdftag(1);
				 jqlService.update(approvalInfos[i]);//更新流程数据 //更新文件库中的当前文件流
				 jqlService.excute("update ApprovalReader set fileName=?,path=? where approvalInfoId=?",approvalInfos[i].getFileName(),approvalInfos[i].getDocumentPath(),approvalInfos[i].getId());
				 try
				 {
					 //删除中间产生的文件
					 file.delete();
					 File srcFile=new File(sourceFileName[i]);
					 srcFile.delete();
				 }
				 catch (Exception e)
				 {
					 e.printStackTrace();
				 }
			}
			
			return "0";
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "转换失败";
	}
	public Object[] getFile(String srcPath) throws Exception
	{
        JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
        Object[] obj=jcrService.getContentProperty(srcPath);//根据路径获取流
        if (obj!=null)
        {
	        Object[] backobj=new Object[obj.length];
	        backobj[0]=obj[0];
	        backobj[1]=obj[1];
	        InputStream in=(InputStream)obj[2];
	        String extend = srcPath.substring(srcPath.lastIndexOf("."));
	        String backpath=WebConfig.tempFilePath+File.separator+"change"+System.currentTimeMillis()+extend;
	        OutputStream os = new FileOutputStream(backpath);
	        byte[] buff = new byte[1024 * 8];
	        int readed;
	        while((readed = in.read(buff)) > 0)
	        {
	        	os.write(buff, 0, readed);
	        }
	        os.flush();
	        os.close();
	        in.close();
	        backobj[2]=backpath;
	        return backobj;
        }
        return null;
	}
	/**
	 * 替换签批中的文件,先将文件库中的文件增加一条历史记录，再替换，然后再将表中的对应字段替换
	 * @param approveId
	 * @param oldfilePath
	 * @param oldfilename
	 * @param tempfilename
	 * @param newfilename
	 * @return
	 */
	public String replaceFile(Long approveId,String oldfilePath,String oldfilename
			,String tempfilename,String newfilename,Users user)
	{
		if (user==null)
		{
			return "0";
		}
		try
		{
			String tempPath = WebConfig.tempFilePath + File.separatorChar+tempfilename;//新文件路径地址
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
			
			if (tempfilename.indexOf("/")>0)
			{
				int index=tempfilename.lastIndexOf("/");
				InputStream input=jcrService.getContent(tempfilename);
				String myfile=System.currentTimeMillis()+newfilename;
				tempPath = WebConfig.tempFilePath + File.separatorChar+myfile;
				File file = new File(tempPath);
				byte[] b = new byte[8 * 1024];
	            int len = 0;
	            FileOutputStream fo = new FileOutputStream(file);
	            while ((len = input.read(b)) > 0)
	            {
	                fo.write(b, 0, len);
	            }
	            fo.close();
	            input.close();
				//从文件库中获取文件
	            
	            tempfilename=myfile;
			}
			
			ApprovalInfo approvalInfo = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class,approveId);//获取流程实体对象
			String filePaths=approvalInfo.getDocumentPath();
			String versionName=jcrService.createVersion(approvalInfo.getDocumentPath(), user.getRealName(), 
	                	"替换文件", MainConstants.APPROVAL_VERSION_STATUS_CURRENT);//创建一个历史版本
			jcrService.rename(null, filePaths, tempfilename);//对原文件进行重命名,防止有重名的，加了一串数字
			
			int index=filePaths.lastIndexOf("/");
			String targetpath=filePaths.substring(0,index);
			String changePath=targetpath+"/"+tempfilename;//更改后的路径
			
			//更新原文件流
			File file=new File(tempPath);
			FileInputStream stream = new FileInputStream(file);
			jcrService.updateFile(changePath, stream, stream.available());//更新文件,将新文件流进行保存
			stream.close();
			 
			ApprovalTask task = new ApprovalTask();
			task.setApprovalID(approvalInfo.getId());
			task.setApprovalUserID(user.getId());
			task.setStepName("替换文件");
			task.setNextAcceptorID(user.getId());
			task.setDate(new Date());
			task.setComment(user.getRealName()+"替换文件");
			task.setVersionName(versionName);
			task.setAction(0l);
			task.setFileName(approvalInfo.getFileName());//保存的是原来的DOC
			task.setTitle(approvalInfo.getTitle());
			task.setNodetype(0);
			jqlService.save(task);//插入替换记录
			 
			 
			approvalInfo.setDocumentPath(changePath);//更改路径
			approvalInfo.setFileName(newfilename);//更改名称
			approvalInfo.setChangepdftag(0);
			jqlService.update(approvalInfo);//更新流程数据 //更新文件库中的当前文件流
			jqlService.excute("update ApprovalReader set fileName=?,path=? where approvalInfoId=?",approvalInfo.getFileName(),approvalInfo.getDocumentPath(),approvalInfo.getId());
			 try
			 {
				 //删除中间产生的文件
				 file.delete();
			 }
			 catch (Exception e)
			 {
				 e.printStackTrace();
			 }
			 return "1";
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "0";
	}
	
	public String deleteReadinfo(Long[] readIds,Users users)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (readIds!=null && readIds.length>0)
			{
				List<Long> ids=new ArrayList<Long>();
				for (int i=0;i<readIds.length;i++)
				{
//					ids.add(readIds[i]);
					ApprovalReader reader=(ApprovalReader)jqlService.getEntity(ApprovalReader.class, readIds[i]);
					ApprovalReaderDelete readdel=copyReaderData(reader);
					if (readdel!=null)
					{
						jqlService.save(readdel);
						jqlService.delete(reader);
					}
				}
				return "1";
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "0";
	}
	private ApprovalReaderDelete copyReaderData(ApprovalReader reader)
	{
		ApprovalReaderDelete readerdel=new ApprovalReaderDelete();
		if (reader!=null && reader.getId()!=null)
		{
			readerdel.setApprovalInfoId(reader.getApprovalInfoId());
			readerdel.setComment(reader.getComment());
			readerdel.setDate(reader.getDate());
			readerdel.setFileName(reader.getFileName());
			readerdel.setPath(reader.getPath());
			readerdel.setRead(true);
			readerdel.setSendUser(reader.getReadUser());
			readerdel.setStepName(reader.getStepName());
			readerdel.setTitle(reader.getTitle());
			readerdel.setUserId(reader.getUserId());
			readerdel.setUserName(reader.getUserName());
			readerdel.setVersion(reader.getVersion());
			return readerdel;
		}
		return null;
	}
	/**
	 * 签批反悔
	 * @param ids 流程主键
	 * @param user 当前反悔用户
	 * @return
	 */
	public String auditGoBackOper(Long[] ids,Users user)
	{
		
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		if (ids!=null)
		{
			for (int i=0;i<ids.length;i++)
			{
				//查出当前流程反悔的节点，要判断会签的情况
				String sql = "select a from ApprovalTask a where a.approvalID = ? order by a.id desc ";
		        List<ApprovalTask> list = (List<ApprovalTask>)jqlService.findAllBySql(sql, ids[i]);
		        if (list!=null && list.size()>0)
		        {
		        	ApprovalTask task=list.get(0);
		        	ApprovalTask task2=null;
		        	if (list.size()>1)
		        	{
		        		task2=list.get(1);
		        	}
		        	if (task.getAction()==2 || (task.getAction()==1 && task.getApprovalUserID()==user.getId()))
		        	{
			        	if (task.getNodetype()!=null && task.getNodetype().intValue()==1)
			        	{
			        		//如果是会签，比较繁一点
			        		if (task.getApprovalUserID()==user.getId().longValue())
				        	{
				        		//最后一个是当前的用户签批的，可以反悔，还需要判断有没有用户打开当前文件
			        			//如果是最后一个会签，就要改流程状态，否则不要改流程状态
			        			Long sameid=task.getSameid();
			        			if (sameid!=null)
			        			{
			        				SameSignInfo sameinfo=(SameSignInfo)jqlService.getEntity(SameSignInfo.class, sameid);
			        				Integer signnum=sameinfo.getSignnum();
			        				if (list.size()>signnum)
			        				{
			        					//更新状态
			        					ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, task.getApprovalID());
				        				approvalInfo.setNodetype(1);
				        				approvalInfo.setStatus(1);
				        				jqlService.update(approvalInfo);
				        				sameinfo.setState(1);
			        					jqlService.update(sameinfo);
				        				jqlService.delete(task);//临时先做删除
				        				return "1";
			        				}
			        				else
			        				{
			        					sameinfo.setState(1);
			        					jqlService.update(sameinfo);
			        					jqlService.delete(task);//临时先做删除
				        				return "1";
			        				}
			        			}
				        	}
			        		else
			        		{
			        			//这里其实也要处理的，先不做处理，以后再加
			        			return "对不起，已有人处理了，不能反悔！";
			        		}
			        	}
			        	else
			        	{
			        		//不是会签只要判断是否是签批用户
			        		if (task.getApprovalUserID()==user.getId().longValue())//送审也一样
				        	{
			        			//可以反悔
			        			//1、非串签，2、串签的情况
			        			ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, task.getApprovalID());
			        			if (task.getResendtag()!=null && task.getResendtag().intValue()==1)//又送审给下一位
			        			{
			        				if (task2==null)
			        				{
			        					jqlService.delete(task);//临时先做删除
			        					jqlService.delete(approvalInfo);//删除送审
			        					return "1";
			        				}
			        				else
			        				{
				        				ArrayList<Long> hislist=approvalInfo.getSendhistory();
				        				if (hislist!=null && hislist.size()>0)
				        				{
				        					if (hislist.get(hislist.size()-1).longValue()==user.getId().longValue())
				        					{
				        						hislist.remove(hislist.size()-1);
				        						if (hislist.size()>0)
				        						{
				        							approvalInfo.setUserID(hislist.get(hislist.size()-1));//将送审人复原
				        						}
				        						approvalInfo.setSendhistory(hislist);//更新送审列表
				        					}
				        				}
				        				approvalInfo.setStatus(task2.getAction().intValue());
//				        				if (task2.getAction()==2)
//				        				{
//				        					approvalInfo.setapprovalUsersID
//				        				}
				        				approvalInfo.setApprovalUsersID(""+user.getId().longValue());
				        				approvalInfo.setPredefined(false);
				        				approvalInfo.setPreUserIds(new ArrayList<Long>());
				        				jqlService.update(approvalInfo);
				        				jqlService.delete(task);//临时先做删除
				        				return "1";
			        				}
			        			}
			        			else
			        			{
				        			if (approvalInfo.getPreUserIds()==null)
				        			{
				        				//不是串签，或者是串签的最后一步
				        				if (task2==null)
				        				{
				        					jqlService.delete(task);//临时先做删除
				        					jqlService.delete(approvalInfo);//删除送审
				        					return "1";
				        				}
				        				else
				        				{
				        					
					        				if (approvalInfo.getApprovalUsersID()!=null && !approvalInfo.getApprovalUsersID().equals(""+user.getId().longValue()))
					        				{
					        					//串签
					        					if (user.getId().longValue()==approvalInfo.getUserID().longValue())//送审人反悔
					        					{
					        						approvalInfo.setPreUserIds(new ArrayList<Long>());
						        					approvalInfo.setPredefined(false);
					        					}
					        					else
					        					{
						        					ArrayList<Long> slist=new ArrayList<Long>();
						        					slist.add(Long.valueOf(approvalInfo.getApprovalUsersID()));
						        					approvalInfo.setPreUserIds(slist);
						        					approvalInfo.setPredefined(true);
					        					}
					        				}
					        				approvalInfo.setApprovalUsersID(""+user.getId().longValue());
					        				approvalInfo.setStatus(task2.getAction().intValue());//返回上一个状态
					        				jqlService.update(approvalInfo);
					        				jqlService.delete(task);//临时先做删除
					        				return "1";
				        				}
				        			}
				        			else if (approvalInfo.getPreUserIds().size()==0 && approvalInfo.getApprovalUsersID()!=null && approvalInfo.getStatus()==2)
				        			{
				        				if (task2==null)
				        				{
				        					jqlService.delete(task);//临时先做删除
				        					jqlService.delete(approvalInfo);//删除送审
				        					return "1";
				        				}
				        				else
				        				{
					        				ArrayList<Long> signlist=new ArrayList<Long>();
					        				signlist.add(0,Long.valueOf(approvalInfo.getApprovalUsersID()));
					        				approvalInfo.setApprovalUsersID(""+user.getId().longValue());
					        				approvalInfo.setPreUserIds(signlist);
					        				jqlService.update(approvalInfo);
					        				jqlService.delete(task);//临时先做删除
					        				return "1";
				        				}
				        			}
				        			else if (approvalInfo.getApprovalUsersID()==null && approvalInfo.getStatus()==2)
				        			{
				        				if (task2==null)
				        				{
				        					jqlService.delete(task);//临时先做删除
				        					jqlService.delete(approvalInfo);//删除送审
				        					return "1";
				        				}
				        				else
				        				{
					        				approvalInfo.setApprovalUsersID(""+user.getId().longValue());
					        				approvalInfo.setStatus(task2.getAction().intValue());//改成待签状态
					        				jqlService.update(approvalInfo);
					        				jqlService.delete(task);//临时先做删除
					        				return "1";
				        				}
				        			}
				        			else
				        			{
				        				if (task2==null)
				        				{
				        					jqlService.delete(task);//临时先做删除
				        					jqlService.delete(approvalInfo);//删除送审
				        					return "1";
				        				}
				        				else
				        				{
					        				if (approvalInfo.getApprovalUsersID()!=null)
					        				{
						        				ArrayList<Long> signlist=approvalInfo.getPreUserIds();
						        				signlist.add(0,Long.valueOf(approvalInfo.getApprovalUsersID()));
						        				approvalInfo.setPreUserIds(signlist);
						        				
					        				}
					        				else
					        				{
					        					approvalInfo.setStatus(task2.getAction().intValue());
					        				}
					        				approvalInfo.setApprovalUsersID(""+user.getId().longValue());
					        				jqlService.update(approvalInfo);
					        				
					        				jqlService.delete(task);//临时先做删除
					        				return "1";
				        				}
				        			}
			        			}
				        	}
			        		else
			        		{
			        			return "对不起，已有人处理了，不能反悔！";
			        		}
			        	}
		        	}
		        	else
		        	{
		        		return "对不起，已有人处理了，不能反悔！";
		        	}
		        		
		        }
			}
		}
		return "反悔失败！";
	}
	
	public String checkSender(Long id,Users user)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		ApprovalInfo info =(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class,id);
		if (info.getUserID()!=null && info.getUserID().longValue()==user.getId().longValue())
		{
			return "1";
		}
		try
		{
			Users senduser=(Users)jqlService.getEntity(Users.class, info.getUserID());
			return "对不起，已被"+senduser.getRealName()+"送审，您不能再送审了！";
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "对不起，您不能再送审！";
	}
	/**
	 * 获取人员
	 * @param flowinfoid
	 * @param nodeid
	 * @param orgid
	 * @return
	 */
	public List<UserinfoView> getMans(Long flowinfoid,Long nodeid,Long orgid)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		List<UserinfoView> list = getOrgUsersList(orgid,jqlService);//获取本单位的所有人
		//获取已选中的人员
		String SQL="select a from FlowOwners as a where a.flowinfoid=? and a.nodeid=?";
		List<FlowOwners> ownerlist=jqlService.findAllBySql(SQL, flowinfoid,nodeid);
		if (list!=null && ownerlist!=null && ownerlist.size()>0)
		{
			List<UserinfoView> result = new ArrayList<UserinfoView>();
			for (int i=0;i<list.size();i++)
			{
				UserinfoView view=list.get(i);
				for (int j=0;j<ownerlist.size();j++)
				{
					if (view.getUserId().longValue()==ownerlist.get(j).getUserid().longValue())
					{
						view.setMobile("Y");//临时充当选中状态
						break;
					}
				}
				result.add(view);
			}
			list=result;
		}
		return list;
	}
	public void updateMans(Long flowinfoid,Long nodeid,Long orgid,Long[] pams)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String sql = "delete from FlowOwners as a where a.nodeid=?";
		jqlService.excute(sql,nodeid);//先删除原先的拥有者
		if (pams!=null)
		{
			for (int i=0;i<pams.length;i++)
			{
				FlowOwners owner=new FlowOwners();
				owner.setUserid(pams[i]);
				owner.setNodeid(nodeid);
				owner.setFlowinfoid(flowinfoid);
				jqlService.save(owner);
			}
		}
	}
	
	public List<UserinfoView> getStateMans(Long flowinfoid,Long stateid,Long orgid)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		List<UserinfoView> list = getOrgUsersList(orgid,jqlService);//获取本单位的所有人
		//获取已选中的人员
		String SQL="select a from FlowStateOwners as a where a.flowinfoid=? and a.stateid=?";
		List<FlowStateOwners> ownerlist=jqlService.findAllBySql(SQL, flowinfoid,stateid);
		if (list!=null && ownerlist!=null && ownerlist.size()>0)
		{
			List<UserinfoView> result = new ArrayList<UserinfoView>();
			for (int i=0;i<list.size();i++)
			{
				UserinfoView view=list.get(i);
				for (int j=0;j<ownerlist.size();j++)
				{
					if (view.getUserId().longValue()==ownerlist.get(j).getUserid().longValue())
					{
						view.setMobile("Y");//临时充当选中状态
						break;
					}
				}
				result.add(view);
			}
			list=result;
		}
		return list;
	}

	public void updateStateMans(Long flowinfoid,Long stateid,Long orgid,Long[] pams)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String sql = "delete from FlowStateOwners as a where a.stateid=? ";
		jqlService.excute(sql,stateid);//先删除原先的拥有者
		if (pams!=null)
		{
			for (int i=0;i<pams.length;i++)
			{
				FlowStateOwners owner=new FlowStateOwners();
				owner.setUserid(pams[i]);
				owner.setStateid(stateid);
				owner.setFlowinfoid(flowinfoid);
				jqlService.save(owner);
			}
		}
	}

	/**
	 * 获取子节点操作人员
	 * @param flowinfoid
	 * @param nodeid
	 * @param orgid
	 * @param subnodeid
	 * @return
	 */
	public List<UserinfoView> getSubMans(Long flowinfoid,Long nodeid,Long orgid,Long subnodeid)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		List<UserinfoView> list = getOrgUsersList(orgid,jqlService);//获取本单位的所有人
		//获取已选中的人员
		String SQL="select a from FlowSubOwners as a where a.nodeid=? and a.subnodeid=?";
		List<FlowSubOwners> ownerlist=jqlService.findAllBySql(SQL, nodeid,subnodeid);
		
		if (list!=null && ownerlist!=null && ownerlist.size()>0)
		{
			List<UserinfoView> result = new ArrayList<UserinfoView>();
			for (int i=0;i<list.size();i++)
			{
				UserinfoView view=list.get(i);
				for (int j=0;j<ownerlist.size();j++)
				{
					if (view.getUserId().longValue()==ownerlist.get(j).getUserid().longValue())
					{
						view.setMobile("Y");//临时充当选中状态
						break;
					}
				}
				result.add(view);
			}
			list=result;
		}
		return list;
	}
	/**
	 * 更新子节点操作人员
	 * @param flowinfoid
	 * @param nodeid
	 * @param orgid
	 * @param pams
	 * @param subnodeid
	 */
	public void updateSubMans(Long flowinfoid,Long nodeid,Long orgid,Long subnodeid,Long[] pams)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		String sql = "delete from FlowSubOwners as a where a.subnodeid=?";
		jqlService.excute(sql,subnodeid);//先删除原先的拥有者
		if (pams!=null)
		{
			for (int i=0;i<pams.length;i++)
			{
				FlowSubOwners owner=new FlowSubOwners();
				owner.setUserid(pams[i]);
				owner.setNodeid(nodeid);
				owner.setFlowinfoid(flowinfoid);
				owner.setSubnodeid(subnodeid);
				jqlService.save(owner);
			}
		}
	}
	
	public FlowAllNode getNodetype(Long nodeid) throws Exception
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		FlowAllNode flowAllNode=(FlowAllNode)jqlService.getEntity(FlowAllNode.class, nodeid);
		return flowAllNode;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void flowupAddAduit(Long userId, String[] upFilePath, String[] fileName,Integer[] filetypes,
			String acceptId, String comment, String title, ArrayList<Long> readerIds, ArrayList<Long> preUserIds, String[] stepName,int issame) throws Exception 
			{
		 String tempPath = WebConfig.tempFilePath + File.separatorChar;
		 String[] resPaths=new String[upFilePath.length];
		 String[] versionnames=new String[upFilePath.length];
		 for (int i=0;i<upFilePath.length;i++)//保存多个文件
		 {
			 File file = new File(tempPath + upFilePath[i]);
			 InputStream fin = new FileInputStream(file);
		     InputStream ois = new FileInputStream(file);
		     FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		     Fileinfo info = fileSystemService.addAuditFile(userId, fileName[i], fin,  ois);
		     fin.close();
		     file.delete();
		 }
	     ApprovalInfo approval = flowcreateApproval(userId, resPaths, fileName,filetypes, acceptId, comment,title, preUserIds, stepName,issame);
		 createApprovalTask(approval.getId(), userId, userId, acceptId, approval.getDocumentPath(), 
				 ApproveConstants.APPROVAL_STATUS_PAENDING, comment, versionnames[0], readerIds, approval.getTitle(), stepName[0],issame,null,false);
		 //此方法等稳定下来再改gggggggggggggggggggggggggggggggg
	}
	/**
	 * 创建流程
	 * @param userId
	 * @param filePath
	 * @param fileName
	 * @param acceptId
	 * @param comment
	 * @param title
	 * @param preUserIds
	 * @param stepName
	 * @param issame
	 * @return
	 */
	private ApprovalInfo flowcreateApproval(Long userId, String[] filePath, String[] fileName,Integer[] filetypes, String acceptId, 
			String comment, String title, ArrayList<Long> preUserIds, String[] stepName,int issame)
	{
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		ApprovalInfo approvalInfo = new ApprovalInfo();
         // 送审用户
         approvalInfo.setUserID(userId);
         approvalInfo.setOperateid(userId);
         // 审批状态
         if ((preUserIds != null && preUserIds.size() > 0)
        		 || (acceptId!=null && acceptId.length()>0))
         {
        	 approvalInfo.setStatus(ApproveConstants.APPROVAL_STATUS_PAENDING);
         }
         else
         {
        	 approvalInfo.setStatus(ApproveConstants.APPROVAL_STATUS_START);//仅阅读
         }
         
         // 送审日期
         approvalInfo.setDate(new Date());
         approvalInfo.setWarndate(new Date());
         // 送审说明
         approvalInfo.setComment(comment);
         // 送审文档路径
//         approvalInfo.setDocumentPath(filePath);
         approvalInfo.setNodetype(issame);
//         if(filePath!=null && "".equals(filePath))
//         {
//        	 approvalInfo.setFileName(filePath.substring(filePath.lastIndexOf("/")+1, filePath.length()));
//         }
         if (issame==1)
         {
        	 //会签，不给人
         }
         else
         {
        	 approvalInfo.setApprovalUsersID(acceptId);
         }
         approvalInfo.setTitle(title);
//         approvalInfo.setStepName(stepName);
//         if (fileName==null || fileName.length()==0)
//         {
//        	 fileName="null.doc";
//         }
//         approvalInfo.setFileName(fileName);
         if (preUserIds != null && preUserIds.size() > 0 && issame==0)
         {
        	 approvalInfo.setPredefined(true);
        	 approvalInfo.setPreUserIds(preUserIds);
         }
         ArrayList<Long> sendhistory=new ArrayList<Long>();
         sendhistory.add(userId);
         approvalInfo.setSendhistory(sendhistory);//孙爱华增加送审历史记录
         jqlService.save(approvalInfo);
         
         flowuploadAttach(userId,filePath, fileName,filetypes,approvalInfo);//保存附件
         
         
         if (preUserIds!=null && preUserIds.size()>0 && issame==1)
         {
        	 
        	 for (int i=-1;i<preUserIds.size();i++)
        	 {
		         SameSignInfo sameinfo=new SameSignInfo();
		         sameinfo.setSignnum(preUserIds.size()+1);
		         sameinfo.setApprovalID(approvalInfo.getId());
		         if (i<0)
		         {
		        	 sameinfo.setSigner((Users)jqlService.getEntity(Users.class, Long.valueOf(acceptId)));
		         }
		         else
		         {
		        	 sameinfo.setSigner((Users)jqlService.getEntity(Users.class, preUserIds.get(i)));
		         }
		         sameinfo.setState(ApproveConstants.APPROVAL_STATUS_PAENDING);
		         sameinfo.setCreateDate(new Date());
		         sameinfo.setWarndate(new Date());
		         sameinfo.setComment(comment);
//		         sameinfo.setFileName(fileName);
//		         sameinfo.setFilePath(filePath);
		         jqlService.save(sameinfo);
        	 }
         }
         return approvalInfo;
	}
	public void flowuploadAttach(Long userid,String[] filePaths, String[] fileNames,Integer[] filetypes,ApprovalInfo approvalinfo)
	{
		//保存文件
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		InputStream in=null;
		OutputStream out=null;
		List<FlowFiles> uploadfilelist=new ArrayList<FlowFiles>();
		
		for (int i=0;i<filePaths.length;i++)
		{
			  //存储到数据库中
			  FlowFiles flowfiles=new FlowFiles();
			  flowfiles.setMainformid(approvalinfo.getId());//flowform的主键，相当于具体流程编号
			  flowfiles.setOutid(approvalinfo.getId());
			  flowfiles.setFiletype(filetypes[i]);
			  flowfiles.setFilename(fileNames[i]);//文件名称
			  flowfiles.setCreatetime(new Date());
			  flowfiles.setUploadid(userid);//上传者
			  flowfiles.setFilepath(filePaths[i]);//文件地址
			  jqlService.save(flowfiles);//保存表单附件
		}
	}
	/**
	 * 获取流程列表
	 * @param ownerId   当前用户
	 * @param flowtype  流程类别，0表示全部
	 * @param status    大状态，1待办事项、2已办事项
	 * @param start     分页的开始页
	 * @param length    页面显示数量
	 * @param sortField  排序字段
	 * @param isAsc     排序方式
	 * @return
	 */
	public DataHolder getFlowFormList(Long ownerId,int flowtype,int status,int start ,int length,String sortField,String isAsc)
	{	
		  //  List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
//			String queryString = "select u.realName,o.organization.name,a from ApprovalInfo a,Users u,UsersOrganizations o where a.approvalUsersID=u.id and u.id=o.user.id and a.userID=? and a.status=? ";
//			queryString = appendSort(queryString,sortField,isAsc);//加入排序
			String userStr="approvalUsersID";
			if (status!=1)
			{
				userStr="lastsignid";
			}
			String queryStringCount = "select count(distinct a.id) from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a."+userStr+"=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
				+" where a.id=t.approvalid and ( a.status="+status+" and ((t.resendtag=1 and t.approvalUserID="+ownerId+")"
				+" or (t.action=1 and t.approvalUserID="+ownerId+") or a.userid="+ownerId+"))";
			List<Object[]> list = null;// jqlService.findAllBySql(start,length,queryString,ownerId,status);
			String SQL = "select distinct u.realname,p.name,a.id "
				+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a."+userStr+"=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
				+" where a.id=t.approvalid and ( a.status="+status+" and ((t.resendtag=1 and t.approvalUserID="+ownerId+")"
				+" or (t.action=1 and t.approvalUserID="+ownerId+") or a.userid="+ownerId+"))";
			if (status==ApproveConstants.APPROVAL_STATUS_PAENDING)//如果领导再向上送审，要将领导的待审文件显示出来
			{
				SQL = "select distinct u.realname,p.name,a.id "
					+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
					+" on (a."+userStr+"=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
					+" where a.id=t.approvalid and ( a.status="+status+" and (t.action=1 or t.resendtag=1) and t.approvalUserID="+ownerId+")"
					;
				queryStringCount = "select count(distinct a.id) from approvalinfo a left join (users u,usersorganizations o,organizations p) "
					+" on (a."+userStr+"=u.id and u.id=o.user_id and o.organization_id=p.id),approvaltask t "
					+" where a.id=t.approvalid and ( a.status="+status+" and (t.action=1 or t.resendtag=1) and t.approvalUserID="+ownerId+")"
					;
			}
			SQL+=" group by a.id ";
			SQL=appendSort2(SQL,sortField,isAsc);
			if (start<0)
			{
				start=0;
			}
			if (length<0)
			{
				SQL+=" limit "+start+",10000" ;
			}
			else
			{
				SQL+=" limit "+start+","+length ;
			}
			list=getApprovalList(jqlService,SQL);
			if (list!=null && list.size()>0)
			{
				SQL="select u.realName,o.organization.name,a.approvalID,a.state,a.isnew,a.signnum from SameSignInfo a,Users u,UsersOrganizations o "
					+" where a.signer.id=u.id and u.id=o.user.id and a.approvalID in (0";
				Long[] ids=new Long[list.size()];
				for (int i=0;i<list.size();i++)
				{
					Object[] obj=(Object[])list.get(i);
					ApprovalInfo info=(ApprovalInfo)obj[2];
					ids[i]=info.getId();
					SQL+=",?";
				}
				SQL+=") order by a.isnew desc,a.sid ";
				List samelist=jqlService.findAllBySql(SQL,ids);
				
				if (samelist!=null && samelist.size()>0)
				{
					List<Object[]> backlist=new ArrayList<Object[]>();
					for (int j=0;j<list.size();j++)
					{
						Object[] obj=(Object[])list.get(j);
						Object[] myobj=new Object[obj.length];
						myobj[2]=obj[2];
						ApprovalInfo info=(ApprovalInfo)obj[2];
						
						String users="";
						String orgs="";
						boolean SAME=false;
						if (status==ApproveConstants.APPROVAL_STATUS_PAENDING && info.getLastsignid()!=null)
						{
							myobj[0]=obj[0];
							myobj[1]=obj[1];
						}
						else
						{
							if (info.getLastsignid()==null)
							{
								Long maxid=1L;
								Integer signnum=null;
								for (int i=0;i<samelist.size();i++)
								{
									Object[] sameobj=(Object[])samelist.get(i);
									Long id2 = (Long)sameobj[2];
									Integer state=(Integer)sameobj[3];
									Long isnew=(Long)sameobj[4];
									if (i==0){signnum=(Integer)sameobj[5];}
									if (isnew==null)
									{
										isnew=1L;
									}
									if (i==0){maxid=isnew;}
									if (info.getId().longValue()==id2.longValue() && isnew.longValue()==maxid.longValue())
									{
										SAME=true;
										if (state==2)
										{
											users+=","+(String)sameobj[0];
											orgs+=","+(String)sameobj[1];
										}
									}
									
								}
								if (SAME)//有会签
								{
									if (users.length()>0)
									{
										myobj[0]=users.substring(1);
										myobj[1]=orgs.substring(1);
									}
									
								}
								else
								{
									myobj[0]=obj[0];
									myobj[1]=obj[1];
								}
							}
							else
							{
								String temp=(String)obj[0];
								myobj[0]=obj[0];
								myobj[1]=obj[1];
							}
						}
					
						backlist.add(myobj);
					}
					list=backlist;
				}
			}
			List<BigInteger> tempcountObj = (List<BigInteger>)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			
			Object countObj = Long.valueOf(""+tempcountObj.get(0));
			
			DataHolder reHolder = new DataHolder();
			ArrayList<Object> approveList = new ArrayList<Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(null!=list&&!list.isEmpty())
			{
				int size = list.size();
				for(int i=0;i<size;i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ApproveBean bean  = new ApproveBean();
					bean.setNodetype(approveinfo.getNodetype());
					bean.setStepName(approveinfo.getStepName());
					bean.setApproveinfoId(approveinfo.getId());
					bean.setUserId(approveinfo.getUserID());
					if (status==ApproveConstants.APPROVAL_STATUS_ABANDONED)
					{
						Long optid=approveinfo.getOperateid();
						if (optid==null)
						{
							optid=approveinfo.getUserID();
						}
						Users user = userService.getUser(optid);
						bean.setUserName(user.getRealName());
						bean.setTaskApprovalUserDept(userService.getUserDept(optid));
					}
					else
					{
						Users user = userService.getUser(approveinfo.getUserID());
						bean.setUserName(user.getRealName());
						if (objArray[1]==null)
						{
							bean.setTaskApprovalUserDept("");
						}
						else
						{
							bean.setTaskApprovalUserDept((objArray[1].toString()));
						}
					}
					if (objArray[0]==null)
					{
						bean.setTaskApprovalUserName("");
					}
					else
					{
						bean.setTaskApprovalUserName(objArray[0].toString());
						
					}
					
					String filePath = approveinfo.getDocumentPath();
					if(null==filePath||"".equals(filePath))
					{
						continue;
					}
					int idx = filePath.lastIndexOf("/");
					String fileName = approveinfo.getFileName();
					if (fileName==null || fileName.length()==0)
					{
						fileName=filePath.substring(idx+1);
					}
					bean.setFileName(fileName);
					bean.setFileIcon(getFileTypeImagePath(fileName));
					
					bean.setFilePath(filePath);	
					bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
					bean.setStatus(status);
					bean.setDate(sdf.format(approveinfo.getDate()));
					bean.setComment(approveinfo.getComment());
					bean.setTitle(approveinfo.getTitle());
					bean.setIsRead(approveinfo.getIsRead());
					bean.setPredefined(approveinfo.isPredefined());
					approveList.add(bean);
				}
			}
			reHolder.setFilesData(approveList);
			reHolder.setIntData(Integer.valueOf(countObj.toString()));      
			return reHolder;
		
	}
	
	/**
	 * 签收文档，只有在待签状态才会有签收，再次送审或审批完毕都要清空
	 * @param path
	 * @param id
	 * @param user
	 * @return
	 */
	public boolean signreal(String path, String id,Users user) 
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			if (id!=null && id.length()>0 && !id.toLowerCase().equals("null"))
			{
				ApprovalInfo approvalInfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, Long.valueOf(id));
				if (approvalInfo.getNodetype()!=null && approvalInfo.getNodetype()==1)//会签
				{
					List list=jqlService.findAllBySql("select a from SameSignInfo as a where a.approvalID=? and a.signer.id=? and a.isnew=0 ", Long.valueOf(id),user.getId());
					if (list!=null && list.size()>0)
					{
						SameSignInfo same=(SameSignInfo)list.get(0);
						same.setSigntag("Y");
						same.setSigntagdate(new Date());
						jqlService.update(same);
						
						jqlService.excute("update Messages as a set a.state=1 where a.sameid=?",same.getSid());
					}
				}
				else 
				{
					approvalInfo.setSigntag("Y");
					approvalInfo.setSigntagdate(new Date());
					jqlService.update(approvalInfo);
				}
				return true;
			}
			else
			{
				List list=jqlService.findAllBySql("select a from ApprovalInfo as a where a.documentPath=?", path);
				if (list!=null && list.size()>0)
				{
					ApprovalInfo approvalInfo=(ApprovalInfo)list.get(0);
					approvalInfo.setSigntag("Y");
					approvalInfo.setSigntagdate(new Date());
					jqlService.update(approvalInfo);
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
	
	
	public List<Map> getSignProcessInfo(String[] ids, Users user) 
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String cond="";
			for (int i=0;i<ids.length;i++)
			{
				if (i==0)
				{
					cond+=" and (a.id="+ids[i];
				}
				else
				{
					cond+=" or a.id="+ids[i];
				}
				if (i==(ids.length-1))
				{
					cond+=") ";
				}
			}
			String sql="select a.id,count(b.sid) from approvalinfo as a left join (samesigninfo as b) on a.id=b.approvalID where (a.status="
			+ApproveConstants.APPROVAL_STATUS_PAENDING+" or a.status="+ApproveConstants.APPROVAL_STATUS_AGREE+")"
			+cond
			+" group by a.id ";
			List totallist=(List)jqlService.getObjectByNativeSQL(sql,-1,-1);;
			
			sql="select a.id,count(b.sid) from approvalinfo as a left join (samesigninfo as b) on a.id=b.approvalID where (a.status="+ApproveConstants.APPROVAL_STATUS_AGREE
					+" or b.state="+ApproveConstants.APPROVAL_STATUS_AGREE+")"
				+cond
				+" group by a.id ";
			List hadlist=(List)jqlService.getObjectByNativeSQL(sql,-1,-1);;
			
			List<Map> list=new ArrayList<Map>();
			for (int i=0;i<ids.length;i++)
			{
				HashMap temp = new HashMap<String, Object>();
				temp.put("id", ids[i]);
				boolean istotal=true;
				boolean ishad=true;
				if (totallist!=null)
				{
					for (int j=0;j<totallist.size();j++)
					{
						Object[] obj=(Object[])totallist.get(j);
						if (Long.valueOf(ids[i].trim())==((BigInteger)obj[0]).longValue())
						{
							istotal=false;
							temp.put("total", (BigInteger)obj[1]);
							break;
						}
					}
				}
				if (istotal)
				{
					temp.put("total", 0);
				}
				if (hadlist!=null)
				{
					for (int n=0;n<hadlist.size();n++)
					{
						Object[] obj=(Object[])hadlist.get(n);
						if (Long.valueOf(ids[i].trim())==((BigInteger)obj[0]).longValue())
						{
							ishad=false;
							temp.put("isDone", (BigInteger)obj[1]);
							break;
						}
					}
				}
				if (ishad)
				{
					temp.put("isDone", 0);
				}
				list.add(temp);
			}
			return list;
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
		return null;
	}
	public List getSignInfos(String ids,Users user)
	{
		List result = new ArrayList();
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			//获取当前流程节点是否会签
			ApprovalInfo approvalinfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, Long.valueOf(ids.trim()));
			if (approvalinfo.getNodetype()!=null && approvalinfo.getNodetype().intValue()==1)
			{
				//当前为会签,//获取当前流程的最后一个会签
				sql="select max(model.isnew) from SameSignInfo as model where model.approvalID="+ids;
				Long max=(Long)jqlService.getCount(sql);
				
				sql="select a.id,c.userName,c.realName,b.approvalDate,b.warndate,b.state from approvalinfo as a ,samesigninfo as b,Users as c "
				+" where a.id=b.approvalID and b.signer_id=c.id and b.isnew="+max+" and a.id="+ids; 
			}
			else
			{
				if (approvalinfo.getStatus()==ApproveConstants.APPROVAL_STATUS_PAENDING)//待签
				{
					sql="select a.id,users.userName,users.realName,'',a.warndate,a.status from approvalInfo as a,users as users where a.approvalUsersID=users.id and a.id="+ids;
				}
				else if (approvalinfo.getStatus()==ApproveConstants.APPROVAL_STATUS_AGREE)//已签
				{
					sql="select a.id,users.userName,users.realName,a.modifytime,a.warndate,a.status from approvalInfo as a,users as users where a.lastsignid=users.id and a.id="+ids;
				}
			}
			List list=(List)jqlService.getObjectByNativeSQL(sql,-1,-1);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if (list!=null && list.size()>0)
			{
				List tempR = new ArrayList();
				Map<String, Object> temp2 = new HashMap<String, Object>();
				for (int i=0;i<list.size();i++)
				{
					Object[] objs=(Object[])list.get(i);
					Integer state=(Integer)objs[5];
					String username=(String)objs[1];
					String realName=(String)objs[2];
					Date date=null;
					if (objs[3]!=null && objs[3].toString().length()>0)
					{
						date=(Date)objs[3];
					}
					if (state!=null && state.intValue()==ApproveConstants.APPROVAL_STATUS_AGREE)//已签取签批时间
					{
						Map<String, Object> temp=new HashMap<String, Object>();
						temp.put("name", username);       // 签批人(已签的)
						temp.put("realName", realName);     // 签批人真实名
						if (date!=null)
						{
							temp.put("time", sdf.format(date));    // 签批时间
						}
						else
						{
							temp.put("time", "");    // 签批时间
						}
						tempR.add(temp);
					}
				}
				temp2.put("isDone", tempR);
				result.add(temp2);
				
				tempR = new ArrayList();
				temp2 = new HashMap<String, Object>();
				for (int i=0;i<list.size();i++)
				{
					Object[] objs=(Object[])list.get(i);
					Integer state=(Integer)objs[5];
					String username=(String)objs[1];
					String realName=(String)objs[2];
					Date date=null;
					if (objs[3]!=null && objs[3].toString().length()>0)
					{
						date=(Date)objs[3];
					}
					if (state!=null && state.intValue()==ApproveConstants.APPROVAL_STATUS_PAENDING)//待签取提醒时间，已签取签批时间
					{
						Map<String, Object> temp=new HashMap<String, Object>();
						temp.put("name",username);       // 签批人(已签的)
						temp.put("realName",realName);     // 签批人真实名
						if (date!=null)
						{
							temp.put("time", sdf.format(date));    // 签批时间
						}
						else
						{
							temp.put("time", "");    // 签批时间
						}
						tempR.add(temp);
					}
				}
				temp2.put("notDo", tempR);
				result.add(temp2);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 根据签批ID获取相应的签批人（签批和阅读）
	 * @param id 签批ID
	 * @param user 当前用户
	 * @return
	 */
	public List getSignMan(String ids,Users user)
	{
		List backlist=new ArrayList();//标识（单人0还是多人1）、主键ID、人名id、人名、签批（阅读）日期、是否回执、提醒次数
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			Map<String, Object> temp2 = new HashMap<String, Object>();
			ApprovalInfo approvalinfo=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, Long.valueOf(ids.trim()));
			if (approvalinfo.getNodetype()!=null && approvalinfo.getNodetype().intValue()==1)
			{
				//当前为会签,//获取当前流程的最后一个会签
				sql="select max(model.isnew) from SameSignInfo as model where model.approvalID="+ids;
				Long max=(Long)jqlService.getCount(sql);
				
				sql="select a.id,b.sid,b.signer_id,c.realName,b.approvalDate,b.state,b.signtag from approvalinfo as a ,samesigninfo as b,Users as c "
				+" where a.id=b.approvalID and b.signer_id=c.id and b.isnew="+max+" and a.id="+ids; 
				List list=(List)jqlService.getObjectByNativeSQL(sql,-1,-1);
				if (list!=null && list.size()>0)
				{
					//根据会签ID取获取提醒次数
					List mapvalue = new ArrayList();
					for (int i=0;i<list.size();i++)
					{
						Map<String, Object> tempvalue = new HashMap<String, Object>();//标识（单人0还是多人1）、主键ID、人名id、人名、签批（阅读）日期、是否回执、提醒次数
						Object[] objs=(Object[])list.get(i);
						long sid=((BigInteger)objs[1]).longValue();//签批ID
						Long num=(Long)jqlService.getCount("select count(a.id) from Messages as a where a.type=? and a.sameid=?", MessageCons.ADUIT_DOC_TYPE,sid);
						tempvalue.put("nodetype", 1);//标识（单人0还是多人1）
						tempvalue.put("sameid", sid);//主键ID(标识为0就是签批编号，为1就是会签编号)
						tempvalue.put("uid", ((BigInteger)objs[2]).longValue());//人名id
						tempvalue.put("realName", (String)objs[3]);//人名
						Date date=(Date)objs[4];
						if (objs[4]!=null)
						{
							tempvalue.put("signdate", sdf.format(date));//签批（阅读）日期
						}
						else
						{
							tempvalue.put("signdate", "");
						}
						tempvalue.put("state", (Integer)objs[5]);//是否已签，1未签，2为已签
						tempvalue.put("signtag", (String)objs[6]);//是否回执,Y为已经回执
						tempvalue.put("msgnum", num);//提醒次数
						mapvalue.add(tempvalue);
					}
					temp2.put("signman", mapvalue);
					backlist.add(temp2);
				}
			}
			else
			{
				if (approvalinfo.getStatus()==ApproveConstants.APPROVAL_STATUS_PAENDING)//待签
				{
					sql="select a.id,users.id,users.realName,'',a.status,a.signtag from approvalInfo as a,users as users where a.approvalUsersID=users.id and a.id="+ids;
				}
				else if (approvalinfo.getStatus()==ApproveConstants.APPROVAL_STATUS_AGREE)//已签
				{
					sql="select a.id,users.id,users.realName,a.modifytime,a.status,a.signtag from approvalInfo as a,users as users where a.lastsignid=users.id and a.id="+ids;
				}
				List list=(List)jqlService.getObjectByNativeSQL(sql,-1,-1);
				
				if (list!=null && list.size()>0)
				{
					//根据会签ID取获取提醒次数
					List mapvalue = new ArrayList();
					for (int i=0;i<list.size();i++)
					{
						Map<String, Object> tempvalue = new HashMap<String, Object>();//标识（单人0还是多人1）、主键ID、人名id、人名、签批（阅读）日期、是否回执、提醒次数
						Object[] objs=(Object[])list.get(i);
						Long num=(Long)jqlService.getCount("select count(a.id) from Messages as a where a.type=? and a.outid=? and a.sameid is null ", MessageCons.ADUIT_DOC_TYPE,Long.valueOf(ids));
						tempvalue.put("nodetype", 0);//标识（单人0还是多人1）
						tempvalue.put("sameid", ids);//主键ID(标识为0就是签批编号，为1就是会签编号)
						tempvalue.put("uid", ((BigInteger)objs[1]).longValue());//人名id
						tempvalue.put("realName", (String)objs[2]);//人名
						if (objs[3]!=null && objs[3].toString().length()>0)
						{
							Date date=(Date)objs[3];
							tempvalue.put("signdate", sdf.format(date));//签批（阅读）日期
						}
						else
						{
							tempvalue.put("signdate", "");
						}
						tempvalue.put("state", (Integer)objs[4]);//是否已签，1未签，2为已签
						tempvalue.put("signtag", (String)objs[5]);//是否回执,Y为已经回执
						tempvalue.put("msgnum", num);//提醒次数
						mapvalue.add(tempvalue);
					}
					temp2.put("signman", mapvalue);
					backlist.add(temp2);
				}
			}
			
			//获取传阅的信息
			sql="select b.id,c.id,c.realName,b.date_,b.isRead,b.signtag from approvalinfo as a ,approvalreader as b,Users as c "
			+" where a.id=b.approvalInfoId and b.userId=c.id and (b.isnew=1 or b.isnew is null) and a.id="+ids; 
			List list=(List)jqlService.getObjectByNativeSQL(sql,-1,-1);
			if (list!=null && list.size()>0)
			{
				//根据会签ID取获取提醒次数
				List mapvalue = new ArrayList();
				for (int i=0;i<list.size();i++)
				{
					Map<String, Object> tempvalue = new HashMap<String, Object>();//标识（单人0还是多人1）、主键ID、人名id、人名、签批（阅读）日期、是否回执、提醒次数
					Object[] objs=(Object[])list.get(i);
					long id=((BigInteger)objs[0]).longValue();//阅读ID
					Long num=(Long)jqlService.getCount("select count(a.id) from Messages as a where a.type=? and a.readid=? and a.sameid is null ", MessageCons.ADUIT_DOC_TYPE,id);
					tempvalue.put("nodetype", 0);//标识（单人0还是多人1）,该项对传阅不起作用
					tempvalue.put("readid", id);//主键ID，传阅ID
					tempvalue.put("uid", ((BigInteger)objs[1]).longValue());//人名id
					tempvalue.put("realName", (String)objs[2]);//人名
					Date date=(Date)objs[3];
					if (date!=null)
					{
						tempvalue.put("readdate", sdf.format(date));//签批（阅读）日期
					}
					else
					{
						tempvalue.put("readdate", "");
					}
					tempvalue.put("isRead", (Boolean)objs[4]);//是否阅读，true已签，false未阅
					tempvalue.put("signtag", (String)objs[5]);//是否回执,Y为已经回执
					tempvalue.put("msgnum", num);//提醒次数
					mapvalue.add(tempvalue);
				}
				temp2.put("readman", mapvalue);
				backlist.add(temp2);
			}
			return backlist;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	// user290 2012-09-03 桌面签批功能
    /**
     * 用户是否有待签权限。
     * @param req
     * @param res
     */
	public void isDaiQianPermit(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String userID = WebTools.converStr(req.getParameter("userID"));
			String filePath = WebTools.converStr(req.getParameter("filePath"));
			filePath = URLDecoder.decode(filePath,"utf-8");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String queryStringCount = "select count(distinct a.id) from ApprovalInfo a left join samesigninfo s on a.id = s.approvalID where (a.approvalUsersID='"+userID+"' or (s.signer_id="+userID+" and s.state="+ApproveConstants.APPROVAL_STATUS_PAENDING+")) and a.status="+ApproveConstants.APPROVAL_STATUS_PAENDING+" and a.documentPath='"+filePath+"'"+" ";
			System.out.println(queryStringCount);
			List<BigInteger> tempcountObj = (List<BigInteger>)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			long count = tempcountObj.get(0).longValue();
			if (count >= 1)
			{
				res.getWriter().print("true");
			}
			else
			{
				res.getWriter().print("false");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
    /**
     * 获得指定的一条待签记录数据。
     * @param req
     * @param res
     */
	public void getLeaderNoAduitRecord(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String approveUserId = WebTools.converStr(req.getParameter("userID"));
			String docPath = WebTools.converStr(req.getParameter("filePath"));
			docPath = URLDecoder.decode(docPath,"utf-8");
			int status = ApproveConstants.APPROVAL_STATUS_PAENDING;
			List<Object[]> list = null;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String SQL = "select u.realname,p.name,a.id "
				+" from approvalinfo a left join (users u,usersorganizations o,organizations p) "
				+" on (a.userID=u.id and u.id=o.user_id and o.organization_id=p.id) "
				+" left join samesigninfo s on a.id = s.approvalID "
				+" where a.status="+status
				+" and  (a.approvalUsersID='"+approveUserId+"' or (s.signer_id="+approveUserId+" and s.state="+status+"))"
				+" and a.documentPath='"+docPath+"'";
			System.out.println(SQL);
			list=getApprovalList(jqlService,SQL);
			if (list != null && !list.isEmpty() && list.size() == 1)
			{
				Object[] objArray = (Object[])list.get(0);
				ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
				ApproveBean bean  = new ApproveBean();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				bean.setNodetype(approveinfo.getNodetype());
				bean.setStepName(approveinfo.getStepName());
				bean.setApproveinfoId(approveinfo.getId());
				bean.setUserId(approveinfo.getUserID());
				if (objArray[0]==null)
				{
					bean.setUserName("");
				}
				else
				{
					bean.setUserName(objArray[0].toString());
				}
				if (objArray[1]==null)
				{
					bean.setUserDeptName("");
				}
				else
				{
					bean.setUserDeptName((objArray[1].toString()));
				}
				String filePath = approveinfo.getDocumentPath();
				int idx = filePath.lastIndexOf("/");
				String fileName = approveinfo.getFileName();
				if (fileName==null || fileName.length()==0)
				{
					fileName=filePath.substring(idx+1);
				}
				bean.setFileName(fileName);
				bean.setFilePath(filePath);	
				bean.setFileIcon(getFileTypeImagePath(fileName));
				bean.setTaskApprovalUserID(approveinfo.getApprovalUsersID());
				bean.setStatus(status);
				bean.setDate(sdf.format(approveinfo.getDate()));
				bean.setComment(approveinfo.getComment());
				bean.setTitle(approveinfo.getTitle());
				bean.setIsRead(approveinfo.getIsRead());
				bean.setPredefined(approveinfo.isPredefined());
				//
				Hashtable<String, Object> map = new Hashtable<String, Object>();
				map.put("approveId", bean.getApproveinfoId());
				map.put("filePath", bean.getFilePath());
				map.put("fileName", bean.getFileName());
				map.put("title", bean.getTitle());
				map.put("step", bean.getStepName());
				map.put("nodetype",bean.getNodetype());
				map.put("fileIcon", bean.getFileIcon());
				map.put("owner", bean.getUserName());
				map.put("ownerId", bean.getUserId());
				map.put("signerId", bean.getTaskApprovalUserID());
				map.put("status", replaceStatus2(bean.getStatus()));
				map.put("ownerDept", bean.getUserDeptName());
				map.put("approveDate", bean.getDate());
				map.put("comment", bean.getComment());
				//map.put("signerDept", bean.getTaskApprovalUserDept());
				//map.put("predefined", bean.getPredefined());
				map.put("isRead", bean.getIsRead());
				if ("1".equals(bean.getIsRead()))
				{
					map.put("showFileName", bean.getFileName());
				}
				else
				{
					//map.put("showFileName", "<b>" + bean.getFileName() + "</b>");
					//map.put("owner", "<b>" + bean.getUserName() + "</b>");
					map.put("showFileName", bean.getFileName());
					map.put("owner", bean.getUserName());	
				}
				//
	            ObjectOutputStream oos = new ObjectOutputStream(res.getOutputStream());
	            oos.writeObject(map);
	            oos.flush();
	            oos.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String replaceStatus2(int status) 
	{
		String result = "";
		if (status == ApproveConstants.APPROVAL_STATUS_AGREE) {
			result = "已签批";//2
		} else if (status == ApproveConstants.APPROVAL_STATUS_RETURNED) {
			result = "已退回";//3
		} else if (status == ApproveConstants.APPROVAL_STATUS_ABANDONED) {
			result = "已终止";//4
		} else if (status == ApproveConstants.APPROVAL_STATUS_PAENDING) {
			result = "签批中";//1
		} else if (status == ApproveConstants.APPROVAL_STATUS_ARCHIVING) {
			result = "已归档";//8
		} else if (status == ApproveConstants.APPROVAL_STATUS_PUBLISH) {
			result = "已发布";//7
		} else if (status == ApproveConstants.APPROVAL_STATUS_END) {
			result = "已成文";//5
		}else if (status == ApproveConstants.APPROVAL_STATUS_DESTROY) {
			result = "待销毁";//9
		}
		//归档 10
		return result;
	}
	
    /**
     * 一条待签记录进行签批操作。
     * @param req
     * @param res
     */
	public void aduitOperationRecord(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String userID = WebTools.converStr(req.getParameter("userID"));
			String approvalID = WebTools.converStr(req.getParameter("approvalID"));
			String filePath = WebTools.converStr(req.getParameter("filePath"));
			filePath = URLDecoder.decode(filePath,"utf-8");
			String ownerId = WebTools.converStr(req.getParameter("ownerId"));
			String title = WebTools.converStr(req.getParameter("title"));
			title = URLDecoder.decode(title,"utf-8");
			String acceptId = WebTools.converStr(req.getParameter("acceptId"));
			String status = WebTools.converStr(req.getParameter("status"));
			String comment = WebTools.converStr(req.getParameter("comment"));
			String readerIds = WebTools.converStr(req.getParameter("readerIds"));
			String preUserIds = WebTools.converStr(req.getParameter("preUserIds"));
			String stepName = WebTools.converStr(req.getParameter("stepName"));
			String isSame = WebTools.converStr(req.getParameter("isSame"));
			String resend = WebTools.converStr(req.getParameter("resend"));
			ApprovalInfo approvalinfo = new ApprovalInfo();
			approvalinfo.setId(Long.valueOf(approvalID));
			approvalinfo.setDocumentPath(filePath);
			approvalinfo.setUserID(Long.valueOf(ownerId));
			approvalinfo.setTitle(title);
			aduitOperation(approvalinfo,Long.valueOf(userID),acceptId,Integer.parseInt(status),
					comment,convertLongArray(readerIds),convertLongArray(preUserIds),
					stepName,Integer.parseInt(isSame),Integer.parseInt(resend));
			res.getWriter().print("true");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private ArrayList<Long> convertLongArray(String value)
	{
		ArrayList<Long> list = new ArrayList<Long>(0);
		try
		{
			if (value != null && !"".equals(value))
			{
				String[] objs = value.split(";");
				if (objs != null)
				{
					for (int i=0; i < objs.length; i++)
					{
						list.add(Long.valueOf(objs[i]));
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
    /**
     * 根据父部门ID获得签批的第一层子部门和子成员列表。
     * @param req
     * @param res
     */
	public void getApproveGroupMembers(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String userID = WebTools.converStr(req.getParameter("userID"));
			String parentID = WebTools.converStr(req.getParameter("groupID"));
			Long orgid = Long.valueOf(parentID);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			Users user = userService.getUser(userID);
			IAddressListService addressListService = (IAddressListService)ApplicationContext.getInstance().getBean(AddressListService.NAME);
			Map<String,Object> allMembersMap = addressListService.getGroupAndMemberList(orgid,user.getCompany().getId(),"groupcode","sortNum");
			List<DepMemberPo> groupMemberList = (List<DepMemberPo>) allMembersMap.get("groupMemberList");
			List<Organizations> groupList=(List<Organizations>)allMembersMap.get("groupList");
			ArrayList<HashMap<String,Object>> memberArray = new ArrayList<HashMap<String,Object>>(0);
			ArrayList<HashMap<String,Object>> groupArray = new ArrayList<HashMap<String,Object>>(0);
			Hashtable<String,ArrayList<HashMap<String,Object>>> groupMemberMap = new Hashtable<String,ArrayList<HashMap<String,Object>>>(0);
			if(groupList != null && !groupList.isEmpty())
			{
				for(Organizations groupTemp : groupList)
				{
					HashMap<String,Object> map = new HashMap<String,Object>(0);
					map.put("groupID", groupTemp.getId().toString());
					map.put("groupName", groupTemp.getName());
					groupArray.add(map);
				}
				groupMemberMap.put("groupList", groupArray);
			}
			if(groupMemberList != null && !groupMemberList.isEmpty())
			{
				groupMemberList = filterLeader(groupMemberList);
				for(DepMemberPo memberTemp : groupMemberList)
				{
					HashMap<String,Object> map = new HashMap<String,Object>(0);
					map.put("userID", memberTemp.getUser().getId().toString());
					map.put("userName", memberTemp.getUser().getUserName());
					map.put("realName", memberTemp.getUser().getRealName());
					map.put("duty", memberTemp.getUser().getDuty());
					memberArray.add(map);
				}
				groupMemberMap.put("memberList", memberArray);
			}
			//
            ObjectOutputStream oos = new ObjectOutputStream(res.getOutputStream());
            oos.writeObject(groupMemberMap);
            oos.flush();
            oos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private List<DepMemberPo> filterLeader(List<DepMemberPo> memberList)
	{
		PermissionService permissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
		List<DepMemberPo> result = new ArrayList<DepMemberPo>();
		int size = memberList.size();
		for(int i=0;i<size;i++)
		{
			DepMemberPo po = memberList.get(i);
			if(null==po.getOrganization())
			{
				continue;
			}
//			int role = ApprovalUtil.instance().getUserRole(po.getUser().getId());
			//判断这个角色有没有权限
			long myrole = permissionService.getSystemPermission(po.getUser().getId());
//			boolean toAduit = FlagUtility.isValue(role, ManagementCons.AUDIT_SEND_FLAG);
			boolean aduit = FlagUtility.isValue(myrole, ManagementCons.AUDIT_AUDIT_FLAG);
//			boolean managerment = FlagUtility.isValue(role, ManagementCons.AUDIT_MANGE_FLAG);
			if (aduit)
			{
				result.add(po);
			}
		}
		return result;
	}
	
    /**
     * 判断当前用户对打开的文档是否有已签操作的权限。
     * @param req
     * @param res
     */
	public void isYiQianPermit(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String approvalUserID = WebTools.converStr(req.getParameter("userID"));
			String filePath = WebTools.converStr(req.getParameter("filePath"));
			filePath = URLDecoder.decode(filePath,"utf-8");
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String queryStringCount = "select count(distinct a.id) from ApprovalInfo a,ApprovalTask t where a.id=t.approvalID and a.status!=10"
				   + " and t.approvalUserID=" + approvalUserID
				   + " and t.action=" + ApproveConstants.APPROVAL_STATUS_AGREE
				   + " and a.documentPath='" + filePath
				   + "'  ";
			System.out.println(queryStringCount);
			List<BigInteger> tempcountObj = (List<BigInteger>)jqlService.getObjectByNativeSQL(queryStringCount, -1, -1);
			long count = tempcountObj.get(0).longValue();
			if (count >= 1)
			{
				res.getWriter().print("true");
			}
			else
			{
				res.getWriter().print("false");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
    /**
     * 一条已签记录进行签批反悔操作。
     * @param req
     * @param res
     */
	public void auditGoBackRecord(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String userID = WebTools.converStr(req.getParameter("userID"));
			String filePath = WebTools.converStr(req.getParameter("filePath"));
			filePath = URLDecoder.decode(filePath,"utf-8");
			Long approveUserId = Long.valueOf(userID);
			int status = ApproveConstants.APPROVAL_STATUS_AGREE;
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			String queryString = "select distinct u.realName,o.organization.name,a,t.stepName from ApprovalInfo a,Users u,UsersOrganizations o,ApprovalTask t where a.userID=u.id and u.id=o.user.id and a.id=t.approvalID and a.status!=10 and t.approvalUserID=? and t.action=? and a.documentPath=?";
			queryString = appendSort(queryString,null,null);
			System.out.println(queryString);
			List<Object[]> list = jqlService.findAllBySql(-1,-1,queryString,approveUserId, status, filePath);
			if (list != null && !list.isEmpty())
			{
				int size = list.size();
				Long[] ids = new Long[size];
				for(int i=0; i<size; i++)
				{
					Object[] objArray = (Object[])list.get(i);
					ApprovalInfo approveinfo = (ApprovalInfo)objArray[2];
					ids[i] = approveinfo.getId();
				}
				Users user = userService.getUser(approveUserId.longValue());
				String value = auditGoBackOper(ids, user);
				res.getWriter().print(value);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
    /**
     * 一条记录的待签阅读。
     * @param req
     * @param res
     */
	public void readApprovalRecord(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String userID = WebTools.converStr(req.getParameter("userID"));
			String approvalID = WebTools.converStr(req.getParameter("approvalID"));
			readApproval(Long.valueOf(approvalID),0,0,Long.valueOf(userID));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	// end
	
}
