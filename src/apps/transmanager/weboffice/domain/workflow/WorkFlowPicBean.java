package apps.transmanager.weboffice.domain.workflow;

import apps.transmanager.weboffice.domain.SerializableAdapter;

public class WorkFlowPicBean  implements SerializableAdapter
{
	private Long approvalID;//流程编号,可以为空
	private String[] actions;//当前历史状态，为了显示流程节点名称用的

	private String[] nodeValues;//签批人
    private String[] times;//签批时间
    private String[] signtagdate;//签收时间，用户流程图时间戳显示

	private Integer[] states;//状态，1表示待签，2表示已签
	private Integer nodetype=0;//节点类型,0表示串行，1表示会签
    

	public String[] getSigntagdate() {
		return signtagdate;
	}
	public void setSigntagdate(String[] signtagdate) {
		this.signtagdate = signtagdate;
	}
    public String[] getActions()
	{
		return actions;
	}
	public void setActions(String[] actions)
	{
		this.actions = actions;
	}
    public Integer[] getStates()
	{
		return states;
	}
	public void setStates(Integer[] states)
	{
		this.states = states;
	}
	public Long getApprovalID()
	{
		return approvalID;
	}
	public void setApprovalID(Long approvalID)
	{
		this.approvalID = approvalID;
	}
	public String[] getNodeValues()
	{
		return nodeValues;
	}
	public void setNodeValues(String[] nodeValues)
	{
		this.nodeValues = nodeValues;
	}
	public String[] getTimes()
	{
		return times;
	}
	public void setTimes(String[] times)
	{
		this.times = times;
	}
	public Integer getNodetype()
	{
		return nodetype;
	}
	public void setNodetype(Integer nodetype)
	{
		this.nodetype = nodetype;
	}
}
