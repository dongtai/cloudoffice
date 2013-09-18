package apps.transmanager.weboffice.domain;

import apps.transmanager.weboffice.constants.both.ApproveConstants;

/**
 * TODO: 文件注释
 * <p>
 * <p>
 * <p>
 * <p>
 */
public class ApproveBean implements SerializableAdapter {
	private Long approveinfoId;

	private Long userId;// 送审用户ID

	private String userName;// 送审用户realName

	private String userDeptName;// 送审用户的部门

	private String filePath;// 送审文档的路径

	private String taskApprovalUserID;// 审批者ID

	private String taskApprovalUserName;// 审批者姓名
	
	private String taskApprovalUserDept;//审批者部门

	private int status;// 审批状态(详见ApproveConstants)

	private String date;

	private String comment;

	private String fileName;
	
	private String fileIcon;
	
	private String isRead;
	
	  //标题
    private String title;
    
    private Boolean predefined;             // 是否是预定义步骤，按需求定义，是则在审批的时候，后续人员及阅读者不能选择。
    
    private String stepName;     // 该步骤任务的名称
    
    private Integer nodetype=0;//当前节点类型,0是原来默认的方式，1为并行会签      孙爱华增加
    
    private String signtag;
    
	public Integer getNodetype()
	{
		return nodetype;
	}
	public void setNodetype(Integer nodetype)
	{
		this.nodetype = nodetype;
	}
	public String getStepName()
	{
		return stepName;
	}
	public void setStepName(String stepName)
	{
		this.stepName = stepName;
	}
    
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getApproveinfoId() {
		return approveinfoId;
	}

	public void setApproveinfoId(Long approveinfoId) {
		this.approveinfoId = approveinfoId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserDeptName() {
		return userDeptName;
	}

	public void setUserDeptName(String userDeptName) {
		this.userDeptName = userDeptName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTaskApprovalUserID() {
		return taskApprovalUserID;
	}

	public void setTaskApprovalUserID(String taskApprovalUserID) {
		this.taskApprovalUserID = taskApprovalUserID;
	}

	public String getTaskApprovalUserName() {
		return taskApprovalUserName;
	}

	public void setTaskApprovalUserName(String taskApprovalUserName) {
		this.taskApprovalUserName = taskApprovalUserName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getStatusName()
	{
		switch (status)
		{
			case ApproveConstants.APPROVAL_STATUS_PAENDING :
				return "待审批";
			
			case ApproveConstants.APPROVAL_STATUS_AGREE :
				return "审批通过";
				
			case ApproveConstants.APPROVAL_STATUS_RETURNED :
				return "审批退回";
			
			case ApproveConstants.APPROVAL_STATUS_ABANDONED :
				return "废弃";
			
			case ApproveConstants.APPROVAL_STATUS_END :
				return "已办结";
				
			case ApproveConstants.APPROVAL_STATUS_ENDTOOffICE :
				return "已提交";
				
			case ApproveConstants.APPROVAL_STATUS_PUBLISH :
				return "已发布";
				
			case ApproveConstants.APPROVAL_STATUS_ARCHIVING :
				return "已归档";
				
			case ApproveConstants.APPROVAL_STATUS_DESTROY :
				return "待销毁";
		}
		return "";
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTaskApprovalUserDept() {
		return taskApprovalUserDept;
	}

	public void setTaskApprovalUserDept(String taskApprovalUserDept) {
		this.taskApprovalUserDept = taskApprovalUserDept;
	}

	public String getFileIcon() {
		return fileIcon;
	}

	public void setFileIcon(String fileIcon) {
		this.fileIcon = fileIcon;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

	public Boolean getPredefined() {
		return predefined;
	}

	public void setPredefined(Boolean predefined) {
		this.predefined = predefined;
	}
	public void setSigntag(String signtag)
	{
		this.signtag = signtag;
	}
	public String getSigntag()
	{
		return signtag;
	}
	
}
