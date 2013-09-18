package backflow;



import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import apps.transmanager.weboffice.databaseobject.ApprovalInfo;
import apps.transmanager.weboffice.databaseobject.ApprovalTask;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Roles;
import apps.transmanager.weboffice.databaseobject.SameSignInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.flow.FlowAllNode;
import apps.transmanager.weboffice.databaseobject.flow.FlowInfo;
import apps.transmanager.weboffice.databaseobject.flow.FlowState;
import apps.transmanager.weboffice.service.config.WebConfig;
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
public class FlowNodeWork extends AllSupport
{
	private JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	private FlowAllNode[] flowallnodes;
	private Long companyid;

	private List<FlowAllNode> flownodelist=new ArrayList<FlowAllNode>();
	private List<Company> companylist=new ArrayList<Company>();
	private List<FlowInfo> flowinfolist=new ArrayList<FlowInfo>(); 
	private List<FlowState> statelist=new ArrayList<FlowState>();
	private List<ApprovalTask> hadstatelist=new ArrayList<ApprovalTask>();

	private List<Roles> rolelist=new ArrayList<Roles>();//存放角色
	
	private String optag;//新增或更新数据标记
	private Long flowinfoid;//流程编号
	private Long[] ids;//主键

	private Long[] orgid;//单位编号
    private String[] orgname;//单位名称
    private String[] description;//流程中节点描述
    private Long[] startstateid;//开始状态（节点）【绘图用的】
    private Long[] endstateid;//结束状态（节点）【绘图用的】
    private Long[] viewstateid;//显示的状态，为了有子流程考虑的
    private Integer[] starttype;//开始类别（串行还是并发）【绘图用的】
    private Integer[] endtype;//结束节点类别（串行还是会聚）【绘图用的】
    private String[] arrowsites;//箭头位置

	private String[] canread;//是否允许单独送阅读，与审批分开进行
    private String[] issub;//是否有子流程，暂时不考虑
    private String[] subauto;
    private Integer[] nodenum;
    private Integer[] modifytype;//处理类型
    private String[] actionname;//按钮名称
    private Long flowformid;//具体流程表单主键

	private Long nodeid;
	private String nodestatename;//当前节点状态名称
	private Long delid;//删除编号
	private String flowpicsrc;//流程图位置
	

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
			
			for (int i=0;i<ids.length;i++)
			{
				if (ids[i]!=null && ids[i].longValue()>0)
				{
					FlowAllNode newnode=(FlowAllNode)jqlService.getEntity(FlowAllNode.class,ids[i]);
					newnode=setValue(newnode,i);
					newnode.setFlowinfoid(flowinfoid);
					newnode.setCompanyid(companyid);
					jqlService.update(newnode);
				}
				else
				{
					String sql="select count(*) from FlowAllNode where flowinfoid=? and startstateid=? and endstateid=? ";
					Long hadnum=(Long)jqlService.getCount(sql, flowinfoid,startstateid[i],endstateid[i]);
					if (hadnum!=null && hadnum<1)
					{
						FlowAllNode newnode=new FlowAllNode();
						newnode=setValue(newnode,i);
						newnode.setFlowinfoid(flowinfoid);
						newnode.setCompanyid(companyid);
						jqlService.save(newnode);
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
	private FlowAllNode setValue(FlowAllNode newnode,int index)
	{
		if (ids!=null && ids[index]>0)//主键
		{
//			newnode.setId(ids[index]);
		}
		if (orgid!=null)//单位编号
		{
			newnode.setCompanyid(orgid[index]);
		}
		if (orgname!=null)//单位名称
		{
			newnode.setCompanyname(orgname[index]);
		}
	    if (description!=null)//流程中节点描述
	    {
	    	newnode.setDescription(description[index]);
	    }
	    String str="";
	    if (startstateid!=null)//开始状态（节点）【绘图用的】
	    {
	    	str+=startstateid[index];
	    	newnode.setStartstateid(startstateid[index]);
	    }
	    if (endstateid!=null)//结束状态（节点）【绘图用的】
	    {
	    	str+="_"+endstateid[index];
	    	newnode.setEndstateid(endstateid[index]);
	    }
	    if (viewstateid!=null)//显示的状态，为了有子流程考虑的
	    {
	    	newnode.setViewstateid(viewstateid[index]);
	    }
	    if (starttype!=null)//开始类别（串行还是并发）【绘图用的】
	    {
	    	newnode.setStarttype(starttype[index]);
	    }
	    if (endtype!=null)//结束节点类别（串行还是会聚）【绘图用的】
	    {
	    	newnode.setEndtype(endtype[index]);
	    }
	    
	    if (canread!=null )//是否允许单独送阅读，与审批分开进行
	    {
	    	for (int i=0;i<canread.length;i++)
	    	{
	    		if (str.equals(canread[i]))
	    		{
	    			newnode.setCanread(1);
	    			break;
	    		}
	    	}
	    }
	    if (issub!=null)//是否有子流程，暂时不考虑
	    {
	    	for (int i=0;i<issub.length;i++)
	    	{
	    		if (str.equals(issub[i]))
	    		{
	    			newnode.setIssub(1);
	    			break;
	    		}
	    	}
	    }
	    if (subauto!=null)
	    {
	    	for (int i=0;i<subauto.length;i++)
	    	{
	    		if (str.equals(subauto[i]))
	    		{
	    			newnode.setSubauto(1);
	    			break;
	    		}
	    	}
	    }
	    if (nodenum!=null)
	    {
	    	newnode.setNodenum(nodenum[index]);
	    }
	    if (modifytype!=null)
	    {
	    	newnode.setModifytype(modifytype[index]);
	    }
	    if (actionname!=null)
	    {
	    	newnode.setActionname(actionname[index]);
	    }
	    if (arrowsites!=null)
	    {
	    	newnode.setArrowsites(arrowsites[index]);
	    }
		return newnode;
	}
	public String search() throws Exception {
		//获取本单位的orgid
		companylist=jqlService.findAllBySql("select a from Company as a where a.id>1");
		Users userinfo = getUsers();
		List<Object[]> list=null;
		if (companyid!=null && companyid.longValue()>0)
		{
			flowinfolist=jqlService.findAllBySql("select a from FlowInfo as a,Company as b "
					+"where a.companyid=b.id and a.companyid=?", companyid);
			
			list=jqlService.findAllBySql("select a,b.name from FlowState as a,Company as b "
					+"where a.companyid=b.id and a.companyid=?", companyid);
			if (list!=null && list.size()>0)
			{
				for (int i=0;i<list.size();i++)
				{
					Object[] obj=list.get(i);
					FlowState temp=(FlowState)obj[0];
					temp.setCompanyname((String)obj[1]);
					String num="";
					if (temp.getStatenum()!=null)
					{
						num=temp.getStatenum()+"_";
					}
					temp.setStatename(num+temp.getStatename());
					statelist.add(temp);
				}
			}
			getRequest().setAttribute("statelist", statelist);
		}
		
		
		
		//查出所有的流程节点
		if (flowinfoid!=null && flowinfoid.longValue()>0)
		{
			List<Long> dellist=new ArrayList<Long>();
			List<Long> templist=(List<Long>)jqlService.findAllBySql("select a.id from FlowState as a "
					+"where a.flowinfoid=?", flowinfoid);
			flownodelist=jqlService.findAllBySql("select a from FlowAllNode as a where a.flowinfoid=? ",flowinfoid);
			if (flownodelist!=null && flownodelist.size()>0)
			{
				for (int i=0;i<flownodelist.size();i++)
				{
					FlowAllNode tempfn=flownodelist.get(i);
					if (tempfn.getStartstateid()!=null
						&& tempfn.getEndstateid()!=null && tempfn.getEndstateid().intValue()>0
						)
					{
						boolean isdelete=true;
						if (tempfn.getStartstateid().intValue()==0)
						{
							isdelete=false;
						}
						else
						{
							for (int j=0;j<templist.size();j++)
							{
								Long sid=templist.get(j);
								if (sid.longValue()==tempfn.getStartstateid().longValue())
								{
									isdelete=false;
									break;
								}
							}
						}
						if (!isdelete)
						{
							isdelete=true;
							for (int j=0;j<templist.size();j++)
							{
								Long sid=templist.get(j);
								if (sid.longValue()==tempfn.getEndstateid().longValue())
								{
									isdelete=false;
									break;
								}
							}
						}
						if (isdelete)
						{
							dellist.add(tempfn.getId());
						}
					}
					else
					{
						dellist.add(tempfn.getId());
					}
				}
			}
			if (dellist.size()>0)
			{
				jqlService.deleteEntityByID(FlowAllNode.class, "id", dellist);
			}
			
			
			String sql="select a from FlowAllNode as a where a.flowinfoid=? order by a.nodenum ";
			flownodelist=jqlService.findAllBySql(sql,flowinfoid);
		}
		
		return INPUT;
	}
	public String delete() throws Exception {
		
		jqlService.excute("delete from FlowAllNode where id=? ", delid);
		return search();
	}
	public String selectman() throws Exception {
		
//		String sql="select a from Organizations as a order by a.sortNum ";
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
		//将流程图进行复制，加箭头
		if (flowformid==null)
		{
			statelist=jqlService.findAllBySql("select a from FlowState as a "
					+"where a.flowinfoid=? ", flowinfoid);
		}
		else
		{
			ApprovalInfo flowform=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, flowformid);
			flowinfoid=flowform.getFlowinfoid();
			List<FlowState> tempstatelist=jqlService.findAllBySql("select a from FlowState as a "
					+"where a.flowinfoid=? ", flowinfoid);
			//再获取已经处理的节点
			hadstatelist=jqlService.findAllBySql("select b from ApprovalTask as b "
					+"where b.mainformid=? ", flowformid);
			List<SameSignInfo> ownerlist=jqlService.findAllBySql("select b from SameSignInfo as b "
					+"where b.mainformid=?  ", flowformid);//查出所有的拥有者
			if (tempstatelist!=null)
			{

				String oldpic="flow1.PNG";
				int index=oldpic.lastIndexOf('.');
				String filename=oldpic.substring(0,index)+"_"+flowformid+oldpic.substring(index);
				flowpicsrc="/"+WebConfig.TEMPFILE_FOLDER+"/"+filename;

				for (int i=0;i<tempstatelist.size();i++)
				{
					FlowState tempstate=tempstatelist.get(i);
					if (tempstate.getId().longValue()==flowform.getStateid().longValue())
					{
						tempstate.setIscurrent(1);//当前执行的节点
					}
					tempstate.setModifyname("未处理");
					for (int j=0;j<hadstatelist.size();j++)
					{
						ApprovalTask myform=hadstatelist.get(j);
						if (tempstate.getId().longValue()==myform.getStateid().longValue())
						{
							tempstate.setHadrun(1);//已经执行过的节点
							//当前处理人要处理
							Users users=new Users();
							for (int m=0;m<ownerlist.size();m++)
							{
								//获取所有的处理人,当前节点不能显示上一步的处理者SameSignInfo
								SameSignInfo flowowner=ownerlist.get(m);
								if (flowowner.getNodeid().longValue()==myform.getNodeid().longValue())
								{
									users=flowowner.getSigner();//只取第一个
									break;
								}
							}
							tempstate.setModifyname(users.getRealName());
							tempstate.setModifydate(myform.getModifytime());
							tempstate.setHistoryid(myform.getId());
							break;
						}
						
					}
					statelist.add(tempstate);//将已执行和当前执行节点属性放进来
				}
			}
		}
		return "viewpicture";
	}
	/**
	 * 设置条件
	 * @return
	 * @throws Exception
	 */
	public String selectcond() throws Exception
	{
		return "selectcond";
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
    
    
	public String[] getArrowsites()
	{
		return arrowsites;
	}
	public void setArrowsites(String[] arrowsites)
	{
		this.arrowsites = arrowsites;
	}
	public List<FlowInfo> getFlowinfolist()
	{
		return flowinfolist;
	}
	public void setFlowinfolist(List<FlowInfo> flowinfolist)
	{
		this.flowinfolist = flowinfolist;
	}
    public Long[] getIds()
	{
		return ids;
	}
	public void setIds(Long[] ids)
	{
		this.ids = ids;
	}
	public JQLServices getJqlService()
	{
		return jqlService;
	}
	public void setJqlService(JQLServices jqlService)
	{
		this.jqlService = jqlService;
	}
	public FlowAllNode[] getFlowallnodes()
	{
		return flowallnodes;
	}
	public void setFlowallnodes(FlowAllNode[] flowallnodes)
	{
		this.flowallnodes = flowallnodes;
	}
	public List<FlowAllNode> getFlownodelist()
	{
		return flownodelist;
	}
	public void setFlownodelist(List<FlowAllNode> flownodelist)
	{
		this.flownodelist = flownodelist;
	}
	
	public String getOptag()
	{
		return optag;
	}
	public void setOptag(String optag)
	{
		this.optag = optag;
	}
	public Long getFlowinfoid()
	{
		return flowinfoid;
	}
	public void setFlowinfoid(Long flowinfoid)
	{
		this.flowinfoid = flowinfoid;
	}

	public List<FlowState> getStatelist()
	{
		return statelist;
	}
	public void setStatelist(List<FlowState> statelist)
	{
		this.statelist = statelist;
	}
	
	public Long getDelid()
	{
		return delid;
	}
	public void setDelid(Long delid)
	{
		this.delid = delid;
	}
    public Long[] getOrgid()
	{
		return orgid;
	}
	public void setOrgid(Long[] orgid)
	{
		this.orgid = orgid;
	}
	public String[] getOrgname()
	{
		return orgname;
	}
	public void setOrgname(String[] orgname)
	{
		this.orgname = orgname;
	}
	public String[] getDescription()
	{
		return description;
	}
	public void setDescription(String[] description)
	{
		this.description = description;
	}
	public Long[] getStartstateid()
	{
		return startstateid;
	}
	public void setStartstateid(Long[] startstateid)
	{
		this.startstateid = startstateid;
	}
	public Long[] getEndstateid()
	{
		return endstateid;
	}
	public void setEndstateid(Long[] endstateid)
	{
		this.endstateid = endstateid;
	}
	public Long[] getViewstateid()
	{
		return viewstateid;
	}
	public void setViewstateid(Long[] viewstateid)
	{
		this.viewstateid = viewstateid;
	}
	public Integer[] getStarttype()
	{
		return starttype;
	}
	public void setStarttype(Integer[] starttype)
	{
		this.starttype = starttype;
	}
	public Integer[] getEndtype()
	{
		return endtype;
	}
	public void setEndtype(Integer[] endtype)
	{
		this.endtype = endtype;
	}

	public String[] getCanread()
	{
		return canread;
	}
	public void setCanread(String[] canread)
	{
		this.canread = canread;
	}
	public String[] getIssub()
	{
		return issub;
	}
	public void setIssub(String[] issub)
	{
		this.issub = issub;
	}
	public String[] getSubauto()
	{
		return subauto;
	}
	public void setSubauto(String[] subauto)
	{
		this.subauto = subauto;
	}

	public Long getCompanyid() {
		return companyid;
	}
	public void setCompanyid(Long companyid) {
		this.companyid = companyid;
	}
	public List<Company> getCompanylist() {
		return companylist;
	}
	public void setCompanylist(List<Company> companylist) {
		this.companylist = companylist;
	}
	public Integer[] getNodenum()
	{
		return nodenum;
	}
	public void setNodenum(Integer[] nodenum)
	{
		this.nodenum = nodenum;
	}
	
	public Integer[] getModifytype()
	{
		return modifytype;
	}
	public void setModifytype(Integer[] modifytype)
	{
		this.modifytype = modifytype;
	}
	
	public List<Roles> getRolelist()
	{
		return rolelist;
	}
	public void setRolelist(List<Roles> rolelist)
	{
		this.rolelist = rolelist;
	}
    
	public String[] getActionname()
	{
		return actionname;
	}
	public void setActionname(String[] actionname)
	{
		this.actionname = actionname;
	}
    
	public String getNodestatename()
	{
		return nodestatename;
	}
	public void setNodestatename(String nodestatename)
	{
		this.nodestatename = nodestatename;
	}
    public Long getNodeid()
	{
		return nodeid;
	}
	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}
    
    public Long getFlowformid()
	{
		return flowformid;
	}
	public void setFlowformid(Long flowformid)
	{
		this.flowformid = flowformid;
	}
	public List<ApprovalTask> getHadstatelist()
	{
		return hadstatelist;
	}
	public void setHadstatelist(List<ApprovalTask> hadstatelist)
	{
		this.hadstatelist = hadstatelist;
	}

	public String getFlowpicsrc()
	{
		return flowpicsrc;
	}
	public void setFlowpicsrc(String flowpicsrc)
	{
		this.flowpicsrc = flowpicsrc;
	}
}
