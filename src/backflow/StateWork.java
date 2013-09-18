package backflow;

import java.util.ArrayList;
import java.util.List;

import org.extremecomponents.table.limit.Limit;

import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.flow.FlowInfo;
import apps.transmanager.weboffice.databaseobject.flow.FlowState;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.JQLServices;
/**
 * 流程状态的添加查询
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   web1.0
 */
public class StateWork extends AllSupport
{
	private JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	private FlowState flowstate = new FlowState();
	private List<FlowState> statelist=new ArrayList<FlowState>();
	private List<FlowInfo> flowinfolist=new ArrayList<FlowInfo>();
	private List<Company> companylist=new ArrayList<Company>();

	private String optag;//新增或更新数据标记

	public String firstdata()  throws Exception
	{
		System.out.println("============");
		return SUCCESS;
	}
	public String add() throws Exception {
		
		
		return search();
	}
	public String save() throws Exception {
		String tempfile=""+System.currentTimeMillis();
		try
		{
			Users userinfo = getUsers();
			if ("1".equals(optag))
			{
				if (flowstate.getId()!=null)
				{
					//更新
					FlowState newfs=(FlowState)jqlService.getEntity(FlowState.class, flowstate.getId());
					newfs=copyFlowState(newfs, flowstate);
					if (flowstate.getEndnode()!=null && flowstate.getEndnode().intValue()==1)
					{
						jqlService.excute("update FlowState set endnode=0 where flowinfoid="+flowstate.getFlowinfoid());
					}
					if (flowstate.getStartnode()!=null && flowstate.getStartnode().intValue()==1)
					{
						jqlService.excute("update FlowState set startnode=0 where flowinfoid="+flowstate.getFlowinfoid());
					}
					jqlService.update(newfs);
				}
				else
				{
					//新建
					//检查是否已经存在
					String sql="select count(*) from FlowState where companyid=? and statename=? ";
					Long hadnum=(Long)jqlService.getCount(sql, flowstate.getCompanyid(),flowstate.getStatename());
					if (flowstate.getEndnode()!=null && flowstate.getEndnode().intValue()==1)
					{
						jqlService.excute("update FlowState set endnode=0 where flowinfoid="+flowstate.getFlowinfoid());
					}
					if (flowstate.getStartnode()!=null && flowstate.getStartnode().intValue()==1)
					{
						jqlService.excute("update FlowState set startnode=0 where flowinfoid="+flowstate.getFlowinfoid());
					}
					if (hadnum!=null && hadnum<1)
					{
						jqlService.save(flowstate);
					}
					else
					{
						//提示已经存在
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
		
		companylist=jqlService.findAllBySql("select a from Company as a where a.id>1");
		flowinfolist=jqlService.findAllBySql("select a from FlowInfo as a,Company as b "
				+"where a.companyid=b.id and a.companyid=?", flowstate.getCompanyid());
		List<Object[]> list=null;
		if (flowstate.getCompanyid()!=null && flowstate.getCompanyid().longValue()>0
				&& flowstate.getFlowinfoid()!=null && flowstate.getFlowinfoid().longValue()>0
			)
		{
			list=jqlService.findAllBySql("select a,b.name from FlowState as a,Company as b "
					+"where a.companyid=b.id and a.companyid=? and a.flowinfoid=? order by a.statenum ", flowstate.getCompanyid(),flowstate.getFlowinfoid());
			
//			statelist=jqlService.findAllBySql("select a from FlowState as a where a.orgid=?", flowstate.getOrgid());
		}
//		else
//		{
//			list=jqlService.findAllBySql("select a,b.name from FlowState as a,Organizations as b "
//					+"where a.orgid=b.id");
////			statelist=jqlService.findAllBySql("select a from FlowState as a ");
//		}
		if (list!=null && list.size()>0)
		{
			for (int i=0;i<list.size();i++)
			{
				Object[] obj=list.get(i);
				FlowState temp=(FlowState)obj[0];
				temp.setCompanyname((String)obj[1]);
				statelist.add(temp);
			}
		}
		ExtremeTablePage extremeTablePage =new ExtremeTablePage();
		Limit limit = extremeTablePage.getLimit(getRequest());
		int viewnums=limit.getCurrentRowsDisplayed();
		if (viewnums==0)
		{
			viewnums=100;
		}
		limit.setRowAttributes(statelist.size(),viewnums);
		getRequest().setAttribute("statelist", statelist);
		getRequest().setAttribute("totalRows", statelist.size());
//		Long orgid=flowstate.getOrgid();
//		flowstate=new FlowState();
//		flowstate.setOrgid(orgid);
		return INPUT;
	}
	public String selectman() throws Exception {
		//暂未做组织结构显示
		return "selectman";
	}
	public String delete() throws Exception {
		
		jqlService.excute("delete from FlowState where id=? and statename=? ", flowstate.getId(),flowstate.getStatename());
		return search();
	}
	private FlowState copyFlowState(FlowState newflowstate,FlowState old)
	{
		if (old!=null)
		{
			Users userinfo = getUsers();
			
			newflowstate.setStatename(old.getStatename());
			newflowstate.setDescription(old.getDescription());
			newflowstate.setCompanyid(old.getCompanyid());
			newflowstate.setFlowinfoid(old.getFlowinfoid());
			newflowstate.setStatenum(old.getStatenum());
			newflowstate.setStartnode(old.getStartnode());
			newflowstate.setEndnode(old.getEndnode());
			newflowstate.setLeftdot(old.getLeftdot());
			newflowstate.setTopdot(old.getTopdot());
			newflowstate.setWidth(old.getWidth());
			newflowstate.setHeight(old.getHeight());
			newflowstate.setCanread(old.getCanread());
		}
		return newflowstate;
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
	

	public FlowState getFlowstate()
	{
		return flowstate;
	}
	public void setFlowstate(FlowState flowstate)
	{
		this.flowstate = flowstate;
	}
	public List<FlowState> getStatelist()
	{
		return statelist;
	}
	public void setStatelist(List<FlowState> statelist)
	{
		this.statelist = statelist;
	}

	public JQLServices getJqlService()
	{
		return jqlService;
	}
	public void setJqlService(JQLServices jqlService)
	{
		this.jqlService = jqlService;
	}

	public List<Company> getCompanylist() {
		return companylist;
	}
	public void setCompanylist(List<Company> companylist) {
		this.companylist = companylist;
	}
	
	public String getOptag()
	{
		return optag;
	}
	public void setOptag(String optag)
	{
		this.optag = optag;
	}
	public List<FlowInfo> getFlowinfolist()
	{
		return flowinfolist;
	}
	public void setFlowinfolist(List<FlowInfo> flowinfolist)
	{
		this.flowinfolist = flowinfolist;
	}
}
