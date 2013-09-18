package backflow;


import java.util.ArrayList;
import java.util.List;

import org.extremecomponents.table.limit.Limit;

import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.flow.FlowInfo;
import apps.transmanager.weboffice.databaseobject.flow.FlowType;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.JQLServices;
/**
 * 流程名称的添加查询
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   web1.0
 */
public class FlowInfoWork extends AllSupport
{
	private JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	private FlowInfo flowinfo = new FlowInfo();
	private List<FlowInfo> flowlist=new ArrayList<FlowInfo>();
	private List<Company> companylist=new ArrayList<Company>();
	private List<FlowType> ftypelist=new ArrayList<FlowType>();
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
				if (flowinfo.getId()!=null)
				{
					//更新
					FlowInfo newflow=(FlowInfo)jqlService.getEntity(FlowInfo.class, flowinfo.getId());
					newflow=copyFlowInfo(newflow, flowinfo);
					jqlService.update(newflow);
				}
				else
				{
					//新建
					//检查是否已经存在
					String sql="select count(*) from FlowInfo where companyid=? and flowname=? ";
					Long hadnum=(Long)jqlService.getCount(sql, flowinfo.getCompanyid(),flowinfo.getFlowname());
					if (hadnum!=null && hadnum<1)
					{
						jqlService.save(flowinfo);
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
		getRequest().setAttribute("companylist", companylist);
		if (flowinfo.getCompanyid()!=null && flowinfo.getCompanyid().longValue()>0)
		{
			ftypelist = jqlService.findAllBySql("select a from FlowType as a "
					+" where a.companyid="+flowinfo.getCompanyid());
		}
		getRequest().setAttribute("ftypelist", ftypelist);
		List<Object[]> list=null;
		if (flowinfo.getCompanyid()!=null && flowinfo.getCompanyid().longValue()>0)
		{
			String sql="select a,b.name,c.typename from FlowInfo as a,Company as b,FlowType as c "
					+"where a.companyid=b.id and a.flowtypeid=c.id and a.companyid=? ";
			if (flowinfo.getFlowtypeid()!=null && flowinfo.getFlowtypeid().longValue()>0)
			{
				sql+=" and a.flowtypeid="+flowinfo.getFlowtypeid().longValue();
			}
			list=jqlService.findAllBySql(sql , flowinfo.getCompanyid());
		}
//		else
//		{
//			list=jqlService.findAllBySql("select a,b.name,c.typename from FlowInfo as a,Company as b "
//					+"where a.companyid=b.id and a.flowtypeid=c.id ");
//		}
		if (list!=null && list.size()>0)
		{
			for (int i=0;i<list.size();i++)
			{
				Object[] obj=list.get(i);
				FlowInfo temp=(FlowInfo)obj[0];
				temp.setCompanyname((String)obj[1]);
				temp.setFlowtypename((String)obj[2]);
				flowlist.add(temp);
			}
		}
		ExtremeTablePage extremeTablePage =new ExtremeTablePage();
		Limit limit = extremeTablePage.getLimit(getRequest());
		int viewnums=limit.getCurrentRowsDisplayed();
		if (viewnums==0)
		{
			viewnums=100;
		}
		limit.setRowAttributes(flowlist.size(),viewnums);
		getRequest().setAttribute("flowlist", flowlist);
		getRequest().setAttribute("totalRows", flowlist.size());
		return INPUT;
	}
	public String delete() throws Exception {
		
		jqlService.excute("delete from FlowInfo where id=? and flowname=? ", flowinfo.getId(),flowinfo.getFlowname());
		return search();
	}
	private FlowInfo copyFlowInfo(FlowInfo newflow,FlowInfo old)
	{
		if (old!=null)
		{
			Users userinfo = getUsers();
			
			newflow.setFlowname(old.getFlowname());
			newflow.setFlownum(old.getFlownum());
			newflow.setDescription(old.getDescription());
			newflow.setCompanyid(old.getCompanyid());
//			newflow.setFlowtype(old.getFlowtype());
			newflow.setFlowtypeid(old.getFlowtypeid());
		}
		return newflow;
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
	
	public JQLServices getJqlService()
	{
		return jqlService;
	}
	public void setJqlService(JQLServices jqlService)
	{
		this.jqlService = jqlService;
	}
	public FlowInfo getFlowinfo()
	{
		return flowinfo;
	}
	public void setFlowinfo(FlowInfo flowinfo)
	{
		this.flowinfo = flowinfo;
	}
	public List<FlowInfo> getFlowlist()
	{
		return flowlist;
	}
	public void setFlowlist(List<FlowInfo> flowlist)
	{
		this.flowlist = flowlist;
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
	public void setFtypelist(List<FlowType> ftypelist) {
		this.ftypelist = ftypelist;
	}
	public List<FlowType> getFtypelist() {
		return ftypelist;
	}
}
