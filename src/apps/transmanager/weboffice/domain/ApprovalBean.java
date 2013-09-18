package apps.transmanager.weboffice.domain;

import java.util.Date;

/**
 * TODO: 文件注释
 * <p>
 * <p>
 * 负责小组:        WebOffice
 * <p>
 * <p>
 */
public class ApprovalBean implements SerializableAdapter
{
    /* 审批信息 */
    // 审批ID
    private Long infoID;    
    // 送审用户ID
    private Long infoUserID;
    // 第几步审批。
    private int infoApprovalStep;
    // 文档权限
    private int infoPermit; 
    // 文档所在空间ID
    private long infoSpaceID;
    // 审查状态，0 = 送审中，1 = 审批中， 2 = 已完成， 3 = 已终止
    private int infoStatus;
    // 送审时间
    private Date infoDate;
    // 送审说明
    private String infoComment;
    // 送审文档路径
    private String infoDocumentPath;
    // 要审批用户ID组，ID之用"|"间隔，此数据是顺序的，排在第一个是第一步审批者。
    private String infoApprovalUsersID;
    // 审批者用户名
    private String infoApprovalUsersName;
    
    
    /* 审批任务信息 */
    private Long taskID;
    // 审批ID
    private long taskApprovalID;
    // 文档所在空间ID
    private long taskSpaceID;
    // 审批者ID
    private long taskApprovalUserID;
    // 审批者名称
    private String taskApprovalUserName;    
    // 审批动作  -2 = 未处理， - 1 = 提交审批 ， 0 = 通过， 1 = 拒绝
    private int taskAction;
    // 审批日期
    private Date taskDate;
    // 审批说明
    private String taskComment;
    
    private Integer nodetype=0;//当前节点类型,0是原来默认的方式，1为并行会签      孙爱华增加
    
    
    
    public Integer getNodetype()
	{
		return nodetype;
	}
	public void setNodetype(Integer nodetype)
	{
		this.nodetype = nodetype;
	}
	public void setInfoID(Long infoID)
    {
        this.infoID = infoID;
    }
    public Long getInfoID()
    {
        return infoID;
    }
    public void setInfoUserID(Long infoUserID)
    {
        this.infoUserID = infoUserID;
    }
    public Long getInfoUserID()
    {
        return infoUserID;
    }
    public void setInfoApprovalStep(int infoApprovalStep)
    {
        this.infoApprovalStep = infoApprovalStep;
    }
    public int getInfoApprovalStep()
    {
        return infoApprovalStep;
    }
    public void setInfoPermit(int infoPermit)
    {
        this.infoPermit = infoPermit;
    }
    public int getInfoPermit()
    {
        return infoPermit;
    }
    public void setInfoSpaceID(long infoSpaceID)
    {
        this.infoSpaceID = infoSpaceID;
    }
    public long getInfoSpaceID()
    {
        return infoSpaceID;
    }
    public void setInfoStatus(int infoStatus)
    {
        this.infoStatus = infoStatus;
    }
    public int getInfoStatus()
    {
        return infoStatus;
    }
    public void setInfoDate(Date infoDate)
    {
        this.infoDate = infoDate;
    }
    public Date getInfoDate()
    {
        return infoDate;
    }
    public void setInfoComment(String infoComment)
    {
        this.infoComment = infoComment;
    }
    public String getInfoComment()
    {
        return infoComment;
    }
    public void setInfoDocumentPath(String infoDocumentPath)
    {
        this.infoDocumentPath = infoDocumentPath;
    }
    public String getInfoDocumentPath()
    {
        return infoDocumentPath;
    }
    public void setInfoApprovalUsersID(String infoApprovalUsersID)
    {
        this.infoApprovalUsersID = infoApprovalUsersID;
    }
    public String getInfoApprovalUsersID()
    {
        return infoApprovalUsersID;
    }
    public void setTaskID(Long taskID)
    {
        this.taskID = taskID;
    }
    public Long getTaskID()
    {
        return taskID;
    }
    public void setTaskApprovalID(long taskApprovalID)
    {
        this.taskApprovalID = taskApprovalID;
    }
    public long getTaskApprovalID()
    {
        return taskApprovalID;
    }
    public void setTaskSpaceID(long taskSpaceID)
    {
        this.taskSpaceID = taskSpaceID;
    }
    public long getTaskSpaceID()
    {
        return taskSpaceID;
    }
    public void setTaskApprovalUserID(long taskApprovalUserID)
    {
        this.taskApprovalUserID = taskApprovalUserID;
    }
    public long getTaskApprovalUserID()
    {
        return taskApprovalUserID;
    }
    public void setTaskAction(int taskAction)
    {
        this.taskAction = taskAction;
    }
    public int getTaskAction()
    {
        return taskAction;
    }
    public void setTaskComment(String taskComment)
    {
        this.taskComment = taskComment;
    }
    public String getTaskComment()
    {
        return taskComment;
    }
    public void setTaskDate(Date taskDate)
    {
        this.taskDate = taskDate;
    }
    public Date getTaskDate()
    {
        return taskDate;
    }
    public void setTaskApprovalUserName(String taskApprovalUserName)
    {
        this.taskApprovalUserName = taskApprovalUserName;
    }
    public String getTaskApprovalUserName()
    {
        return taskApprovalUserName;
    }
    public void setInfoApprovalUsersName(String infoApprovalUsersName)
    {
        this.infoApprovalUsersName = infoApprovalUsersName;
    }
    public String getInfoApprovalUsersName()
    {
        return infoApprovalUsersName;
    }
}
