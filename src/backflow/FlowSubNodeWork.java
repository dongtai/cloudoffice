package backflow;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.flow.FlowAllNode;
import apps.transmanager.weboffice.databaseobject.flow.FlowInfo;
import apps.transmanager.weboffice.databaseobject.flow.FlowState;
import apps.transmanager.weboffice.databaseobject.flow.FlowSubNode;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.JQLServices;
/**
 * 流程节点定义
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   web1.0
 */
public class FlowSubNodeWork extends AllSupport
{
	private JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	private List<FlowSubNode> flowsubnodelist=new ArrayList<FlowSubNode>();
	private Long flowinfoid;//流程编号
	private String flowinfoname;//当前流程名称

	private Long nodeid;//节点编号
	private String nodestatename;
	private Long orgid;//单位编号

	private Long[] ids;//主键
    private String[] description;//流程中节点描述
    private Integer[] topdot;//节点位置,暂不用
    private Integer[] leftdot;//节点位置,暂不用
    private String[] arrowsite;//箭头位置
    private Integer[] nodetype;//节点类别（串行还是并发）
    private String[] statename;//子节点状态名称
    private String[] actionname;//子节点按钮名称
    private String[] backactionname;//子节点返回按钮名称
	private Long delid;//删除编号
	
	public String save() throws Exception {
		String tempfile=""+System.currentTimeMillis();
		try
		{
			Users userinfo = getUsers();

			for (int i=0;i<ids.length;i++)
			{
				if (ids[i]!=null && ids[i].longValue()>0)
				{
					FlowSubNode newnode=(FlowSubNode)jqlService.getEntity(FlowSubNode.class,ids[i]);
					newnode=setValue(newnode,i);
					newnode.setOrgid(orgid);
					newnode.setNodeid(nodeid);
					jqlService.update(newnode);
				}
				else
				{
					String sql="select count(*) from FlowSubNode where  nodeid=? and statename=? ";
					Long hadnum=(Long)jqlService.getCount(sql, nodeid,statename[i]);
					if (hadnum!=null && hadnum<1)
					{
						FlowSubNode newnode=new FlowSubNode();
						newnode=setValue(newnode,i);
						newnode.setNodeid(nodeid);
						newnode.setOrgid(orgid);
						jqlService.save(newnode);
					}
					else
					{
						//防止用户刷屏用的，提示已经存在
					}
				}
			}
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
		
	    return search();
	}

	public String search() throws Exception {
		FlowAllNode tempnode=(FlowAllNode)jqlService.getEntity(FlowAllNode.class, nodeid);
		Long stateid=tempnode.getEndstateid();
		FlowState fstate=(FlowState)jqlService.getEntity(FlowState.class, stateid);
		nodestatename=tempnode.getActionname()+"——"+fstate.getStatename();
		
		FlowInfo flowinfo=(FlowInfo)jqlService.getEntity(FlowInfo.class, flowinfoid);
		flowinfoname=flowinfo.getFlowname();
		//查出子流程节点
		if (nodeid!=null && nodeid.longValue()>0)
		{
			String sql="select a from FlowSubNode as a where a.nodeid=? ";
			flowsubnodelist=jqlService.findAllBySql(sql,nodeid);
		}
		
		return INPUT;
	}
	public String delete() throws Exception {
		
		jqlService.excute("delete from FlowSubNode where id=? ", delid);
		return search();
	}
	public String selectman() throws Exception {
		
		String sql="select a from Organizations as a order by a.sortNum ";
//		rolelist=jqlService.findAllBySql(sql);//存放角色
//		sql="select a from Organizations as a where a.parent=null";
//		orglist=jqlService.findAllBySql(sql);
		//获取
		return "selectman";
	}
	public Long getParentOrgId(Long userid)
	{
		Long orgid=0L;
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
			if(index==-1){
				orgid=id.longValue();
			}else{
				orgid=Long.valueOf(groupcode.substring(0,index));
			}
		}
		return orgid;
	}
	public String viewpicture() throws Exception
	{
		return "viewpicture";
	}
	public String execute() throws Exception {
		System.out.println("come here!!!!");
		try
		{
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	    return SUCCESS;
	}
	private FlowSubNode setValue(FlowSubNode newnode,int index)
	{
		if (ids!=null && ids[index]>0)//主键
		{
//			newnode.setId(ids[index]);
		}
	    if (description!=null)//流程中节点描述
	    {
	    	newnode.setDescription(description[index]);
	    }
	    String str="";
	    
	    if (topdot!=null)//节点位置,暂不用
	    {
	    	newnode.setTopdot(topdot[index]);
	    }
	    if (leftdot!=null)//节点位置,暂不用
	    {
	    	newnode.setLeftdot(leftdot[index]);
	    }
	    if (nodetype!=null)
	    {
	    	newnode.setNodetype(nodetype[index]);
	    }
	    if (statename!=null)
	    {
	    	newnode.setStatename(statename[index]);
	    }
	    if (actionname!=null)
	    {
	    	newnode.setActionname(actionname[index]);
	    }
	    if (backactionname!=null)
	    {
	    	newnode.setBackactionname(backactionname[index]);
	    }
	    if (arrowsite!=null)
	    {
	    	newnode.setArrowsite(arrowsite[index]);
	    }
	    
		return newnode;
	}
	
	
	
	public JQLServices getJqlService()
	{
		return jqlService;
	}
	public void setJqlService(JQLServices jqlService)
	{
		this.jqlService = jqlService;
	}
	public List<FlowSubNode> getFlowsubnodelist()
	{
		return flowsubnodelist;
	}
	public void setFlowsubnodelist(List<FlowSubNode> flowsubnodelist)
	{
		this.flowsubnodelist = flowsubnodelist;
	}
	public Long getFlowinfoid()
	{
		return flowinfoid;
	}
	public void setFlowinfoid(Long flowinfoid)
	{
		this.flowinfoid = flowinfoid;
	}
	public Long getNodeid()
	{
		return nodeid;
	}
	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}
	public Long getOrgid()
	{
		return orgid;
	}
	public void setOrgid(Long orgid)
	{
		this.orgid = orgid;
	}
	public Long[] getIds()
	{
		return ids;
	}
	public void setIds(Long[] ids)
	{
		this.ids = ids;
	}
	public String[] getDescription()
	{
		return description;
	}
	public void setDescription(String[] description)
	{
		this.description = description;
	}
	public Integer[] getTopdot()
	{
		return topdot;
	}
	public void setTopdot(Integer[] topdot)
	{
		this.topdot = topdot;
	}
	public Integer[] getLeftdot()
	{
		return leftdot;
	}
	public void setLeftdot(Integer[] leftdot)
	{
		this.leftdot = leftdot;
	}
	public String[] getArrowsite()
	{
		return arrowsite;
	}
	public void setArrowsite(String[] arrowsite)
	{
		this.arrowsite = arrowsite;
	}
	public Integer[] getNodetype()
	{
		return nodetype;
	}
	public void setNodetype(Integer[] nodetype)
	{
		this.nodetype = nodetype;
	}
	public String[] getStatename()
	{
		return statename;
	}
	public void setStatename(String[] statename)
	{
		this.statename = statename;
	}
	public String[] getActionname()
	{
		return actionname;
	}
	public void setActionname(String[] actionname)
	{
		this.actionname = actionname;
	}
	public String[] getBackactionname()
	{
		return backactionname;
	}
	public void setBackactionname(String[] backactionname)
	{
		this.backactionname = backactionname;
	}
	public Long getDelid()
	{
		return delid;
	}
	public void setDelid(Long delid)
	{
		this.delid = delid;
	}
	public String getNodestatename()
	{
		return nodestatename;
	}

	public void setNodestatename(String nodestatename)
	{
		this.nodestatename = nodestatename;
	}
	public String getFlowinfoname()
	{
		return flowinfoname;
	}

	public void setFlowinfoname(String flowinfoname)
	{
		this.flowinfoname = flowinfoname;
	}

}
