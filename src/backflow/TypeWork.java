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

public class TypeWork extends AllSupport {
	
	private JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	private FlowType flowtype = new FlowType();


	private List<FlowType> typelist=new ArrayList<FlowType>();
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
		
		try 
		{
			getRequest().setAttribute("doublename", null);
			if (flowtype.getCompanyid()!=null && flowtype.getCompanyid().longValue()>0)
			{
				if("1".equals(optag))
				{
					Long tmpid = flowtype.getId();
					flowtype.setId(tmpid);
					flowtype.setTypename(flowtype.getTypename());
					if(tmpid!=null)
					{
						// 更新
						FlowType newft = (FlowType)jqlService.getEntity(FlowType.class, tmpid);
						newft = copyFlowType(newft, flowtype);
						jqlService.update(newft);
					}
					
//				if(flowtype.getId()!=null)
//				{
//					// 更新
//					FlowType newft = (FlowType)jqlService.getEntity(FlowType.class, flowtype.getId());
//					newft = copyFlowType(newft, flowtype);
//					jqlService.update(newft);
//				}
				}
				else
				{
					// 新建
					// 检查是否已经存在
					String sql = "select count(*) from FlowType where companyid = ? and typename = ?";
					Long hadnum = (Long)jqlService.getCount(sql, flowtype.getCompanyid(), flowtype.getTypename());
					if(hadnum!=null && hadnum<1)
					{
						jqlService.save(flowtype);
					}
					else
					{
						// 提示已经存在
						getRequest().setAttribute("doublename", "重名了");
					}
				}
			}
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		
		return search();
	}
	

	public String search() throws Exception {
		companylist=jqlService.findAllBySql("select a from Company as a where a.id>1");
		List list = jqlService.findAllBySql("select a,b.name from FlowType as a,Company as b "
		+" where a.companyid=b.id and a.companyid="+flowtype.getCompanyid());
		if (list!=null && list.size()>0)
		{
			for (int i=0;i<list.size();i++)
			{
				Object[] obj = (Object[])list.get(i);
				FlowType temptype=(FlowType)obj[0];
				temptype.setCompanyname((String)obj[1]);
				typelist.add(temptype);
			}
		}
		ExtremeTablePage extremeTablePage =new ExtremeTablePage();
		Limit limit = extremeTablePage.getLimit(getRequest());
		int viewnums=limit.getCurrentRowsDisplayed();
		if (viewnums==0)
		{
			viewnums=100;
		}
		limit.setRowAttributes(typelist.size(),viewnums);
		getRequest().setAttribute("typelist", typelist);
		getRequest().setAttribute("flowtype", flowtype);
		getRequest().setAttribute("totalRows", typelist.size());

		return INPUT;
	}

	public String selectman() throws Exception {
		//暂未做组织结构显示
		return "selectman";
	}
	public String delete() throws Exception {

			jqlService.excute("delete from FlowType where id=?", flowtype.getId());
//			jqlService.excute("delete from FlowType where id=? and typename=?", Long.parseLong(this.getTypeid()), this.getTypename());		

		
//		jqlService.excute("delete from FlowType where id=?", flowtype.getId());
		return search();
	}

	private FlowType copyFlowType(FlowType newflowtype, FlowType old) {
		// TODO Auto-generated method stub
		if(old!=null) 
		{
			Users userinfo = getUsers();
			
			newflowtype.setCompanyid(old.getCompanyid());
			newflowtype.setCompanyname(old.getDescription());
			newflowtype.setEffect(old.getEffect());
			newflowtype.setTypename(old.getTypename());
		}
		return newflowtype;
	}
	
	public String execute() throws Exception {
		
		System.out.println("come here");
		try {
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return SUCCESS;
	}
	
	
	public FlowType getFlowType()
	{
		return flowtype;
	}
	
	public void setFlowType(FlowType flowtype)
	{
		this.flowtype = flowtype;
	}
	
	public List<FlowType> getTypeList()
	{
		return typelist;
	}
	
	public void setTypeList(List<FlowType> typelist)
	{
		this.typelist = typelist;
	}
	
	public JQLServices getJqlServices()
	{
		return jqlService;
	}
	
	public void setJqlServices(JQLServices jqlService)
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
	
	public List<FlowInfo> getFlowinfolist() {
		return flowinfolist;
	}
	
	public void setFlowinfolist(List<FlowInfo> flowinfolist)
	{
		this.flowinfolist = flowinfolist;
	}
	public FlowType getFlowtype() {
		return flowtype;
	}
	public void setFlowtype(FlowType flowtype) {
		this.flowtype = flowtype;
	}
}
