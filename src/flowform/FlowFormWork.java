package flowform;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.extremecomponents.table.limit.Limit;

import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.databaseobject.ApprovalInfo;
import apps.transmanager.weboffice.databaseobject.ApprovalTask;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.SameSignInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.flow.FlowAllNode;
import apps.transmanager.weboffice.databaseobject.flow.FlowFiles;
import apps.transmanager.weboffice.databaseobject.flow.FlowInfo;
import apps.transmanager.weboffice.databaseobject.flow.FlowState;
import apps.transmanager.weboffice.databaseobject.flow.FlowStateOwners;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.service.approval.WorkFlowPic;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.JQLServices;
/**
 * 流程数据处理（列表、处理、显示等）
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   web1.0
 */
public class FlowFormWork extends AllSupport
{
	private static int FILE_SIZE=2*1024;
	private JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	private List<Organizations> orglist=new ArrayList<Organizations>();

	private List<ApprovalInfo> approvallist=new ArrayList<ApprovalInfo>();
	private List<FlowInfo> flowinfolist=new ArrayList<FlowInfo>();
	private List<ApprovalInfo> flowformlist=new ArrayList<ApprovalInfo>();//流程表单列表
	private List<FlowAllNode> flowallnodelist=new ArrayList<FlowAllNode>();
	private List<FlowFiles> innerfilelist=new ArrayList<FlowFiles>();//内部审批材料
	private List<FlowFiles> outfilelist=new ArrayList<FlowFiles>();//申报材料
	private Long[] ownerids;//接受者列表
	private String comments;
	private Long userid;//当前操作者
	private ApprovalInfo flowform=new ApprovalInfo();//流程表单
	private Integer flowtype;//流程类型
	private Long flowinfoid;//流程编号
	private Integer filetype;//文件类型
	private Long nodeid;//流程节点编号
	private Long formid;//表单编号
	private Long startstateid;//点击按钮执行的动作及状态，可与nodeid连起来
	private Integer ispower=0;//是否有权限操作
	private Integer flashparent=0;//刷新父窗体
	private File[] filepaths;
	private String[] uploadContentType;
	private String[] filepathsFileName;
	private Integer innerTag1=0;//内部通知单
	private FlowFiles flowfiles;//内部通知单


	public String flowformfirstdata()  throws Exception
	{
		System.out.println("============");
		flowinfolist=jqlService.findAllBySql("select a from FlowInfo as a where a.orgid=? order by flownum ", getParentOrgId());
		
		return SUCCESS;
	}
	public String addflowform() throws Exception {
		//根据流程执行情况显示处理按钮
		Users users = getUsers();
		userid=users.getId();
		if (flowform.getId()==null)//新添加
		{
//			根据flowinfoid去获取开始状态
			
			List<Long> idlist=jqlService.findAllBySql("select a.id from FlowState as a where a.flowinfoid=? and a.startnode=? order by a.id ", flowinfoid,1);//查询开始状态
			if (idlist!=null && idlist.size()>0)
			{
				startstateid=idlist.get(0);
			}
			else
			{
				startstateid=1L;
			}
			
		}
		else if (flowform.getId()!=null)
		{
			//针对具体流程来确定下一步的走向
			flowform=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, flowform.getId());
			startstateid=flowform.getStateid();//获得当前流程状态
			flowinfoid=flowform.getFlowinfoid();
			nodeid=flowform.getNodeid();
			
			//获取附件,可以一起查询,临时这样
			List<Object[]> templist=jqlService.findAllBySql("select a,b.realName from FlowFiles as a,Users as b where a.uploadid = b.id and a.mainformid=? and a.filetype=? order by a.id desc ", flowform.getId(),1);//内部审批材料
			if (templist.size()>0)
			{
				innerTag1=1;
				Object[] obj=(Object[])templist.get(0);
				flowfiles=(FlowFiles)obj[0];//获取公文处理单
				flowfiles.setUploadname((String)obj[1]);
			}
			templist=jqlService.findAllBySql("select a from FlowFiles as a where a.mainformid=? and a.filetype=? order by a.id desc ", flowform.getId(),2);//内部审批材料
			if (templist!=null && templist.size()>0)
			{
				for (int i=0;i<templist.size();i++)
				{
					Object[] obj=templist.get(i);
					FlowFiles flowfiles=(FlowFiles)obj[0];
					flowfiles.setUploadname((String)obj[1]);
					innerfilelist.add(flowfiles);
				}
			}
			
			List<Object[]> objlist=jqlService.findAllBySql("select a,b.realName from FlowFiles as a,Users as b "
					+" where a.uploadid = b.id and a.mainformid=? and a.filetype=? ", flowform.getId(),3);//申报材料
			if (objlist!=null && objlist.size()>0)
			{
				for (int i=0;i<objlist.size();i++)
				{
					Object[] obj=objlist.get(i);
					FlowFiles flowfiles=(FlowFiles)obj[0];
					flowfiles.setUploadname((String)obj[1]);
					outfilelist.add(flowfiles);
				}
			}
		}
//		if (startstateid==1)
//		{
//			flowform.setStatename("窗口登记");
//		}
//		else
		{
			flowform.setStatename(((FlowState)jqlService.getEntity(FlowState.class, startstateid)).getStatename());
		}
		List<SameSignInfo> templist=jqlService.findAllBySql(
				"select a from SameSignInfo as a where a.deleted=0 and a.mainformid=? and a.signer.id=?",flowform.getId(),userid);
		
		if (flowform.getId()==null ||templist.size()>0)
		{
			String SQL="select a from FlowAllNode as a where a.flowinfoid=? and a.startstateid=? "
				+" "
				+" order by a.startstateid ";
			flowallnodelist=jqlService.findAllBySql(SQL, flowinfoid,startstateid);//处理节点
			//判断当前状态，再获取当前用户有哪些操作按钮
			if (flowallnodelist!=null && flowallnodelist.size()>0)
			{
				ispower=1;
			}
			//获取当前节点是否可传阅
			if (flowform.getStateid()!=null)
			{
				FlowState flowstate=(FlowState)jqlService.getEntity(FlowState.class, flowform.getStateid());
				if (flowstate!=null && flowstate.getCanread()!=null && flowstate.getCanread().intValue()>0)
				{
					flowform.setCanread("1");
				}
			}
		}
		
		return "addflowform";
	}
	/**
	 * 处理流程，是个关键的方法
	 * @return
	 * @throws Exception
	 */
	public String save() throws Exception {
		String tempfile=""+System.currentTimeMillis();
		try
		{
			flashparent=1;//需要刷新父窗体
			Users users = getUsers();
			if (flowform.getId()!=null)//基本都是这里进行处理
			{
				//更新
				
				ApprovalInfo newflowform=(ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, flowform.getId());
				Long oldmodifier=newflowform.getModifier();
				newflowform=copyApprovalInfo(newflowform, flowform);
				newflowform.setNodeid(nodeid);
				
				
				FlowAllNode tempnode=(FlowAllNode)jqlService.getEntity(FlowAllNode.class, nodeid);
				if (tempnode!=null)
				{
					newflowform.setFlowinfoid(tempnode.getFlowinfoid());
					newflowform.setModifier(getUsers().getId());
					newflowform.setModifytime(new Date());
					newflowform.setNodeid(nodeid);
					newflowform.setStateid(tempnode.getEndstateid());
					newflowform.setStepName(tempnode.getActionname());
					newflowform.setActionname(tempnode.getActionname());
					newflowform.setCometime(new Date());
					//更新流程中处理人状态
					List<SameSignInfo> templist=jqlService.findAllBySql(
					"select a from SameSignInfo as a where a.deleted=0 and a.mainformid=? ",flowform.getId());
					int isMult=0;
					if (templist!=null && templist.size()>0)
					{
						for (int i=0;i<templist.size();i++)
						{
							SameSignInfo flowformowners=templist.get(i);
							if (nodeid.longValue()==flowformowners.getNodeid().longValue())//此节点的人数进行判断
							{
								if (flowformowners.getSigner().getId().longValue()==users.getId().longValue())
								{
									flowformowners.setDeleted(1);
									flowformowners.setComment("");
									flowformowners.setNodeid(nodeid);
									
									jqlService.update(flowformowners);
									templist.remove(i);
									i--;
									isMult--;
								}
								isMult++;
							}
						}
					}
//					Long nums=(Long)jqlService.getCount("select count(*) SameSignInfo as a where a.deleted=0 and a.mainformid=? ",flowform.getId());
					boolean ismodifyover=false;
					if (isMult>0)//有多人进行处理，判断是否自动跳转
					{
						//没有结束，不做处理
						ismodifyover=false;
					}
					else
					{
						ismodifyover=true;//已处理完毕，需要跳转
						//判断下一节点接受者有几人，如果只有一个，就直接赋值，否则下一接受者就为上一节点的送审者
						if (ownerids==null || ownerids.length==0)//没有选择人员，直接到节点中取
						{
							List<FlowStateOwners> myownerlist=jqlService.findAllBySql(
									"select a from FlowStateOwners as a where a.stateid=? ",tempnode.getEndstateid());
							if (myownerlist!=null && myownerlist.size()>0)
							{
								ownerids=new Long[myownerlist.size()];
								for (int i=0;i<myownerlist.size();i++)
								{
									ownerids[i]=myownerlist.get(i).getUserid();
								}
							}
							else
							{
								ownerids=new Long[]{oldmodifier};
							}
						}
					}
					if (ismodifyover)
					{
						if (ownerids!=null && ownerids.length>0)//肯定会有拥有者，否则前台不让提交
						{
							jqlService.excute("update SameSignInfo set deleted=1 where mainformid=?",flowform.getId());
							for (int i=0;i<ownerids.length;i++)
							{
								if (ownerids[i]!=null)
								{
									//插入拥有者
									SameSignInfo owners=new SameSignInfo();
									owners.setMainformid(flowform.getId());
									owners.setNodeid(nodeid);
									owners.setSigner(jqlService.getUsers(ownerids[i]));
									owners.setFlowinforid(flowform.getFlowinfoid());
									owners.setStateid(flowform.getStateid());
									jqlService.save(owners);
								}
							}
						}
						jqlService.update(newflowform);
					}
				}
				
				
				ApprovalTask flowformtemp=new ApprovalTask();
				flowformtemp=copyApprovalInfoHistory(flowformtemp,newflowform);
				jqlService.save(flowformtemp);
			}
			else
			{
				//新建
				//检查是否已经存在
				String sql="select count(*) from ApprovalInfo where numname1=? and numname5=? ";
				Long hadnum=(Long)jqlService.getCount(sql, flowform.getNumname1(),flowform.getNumname5());
				if (hadnum!=null && hadnum<1)
				{
					FlowAllNode tempnode=(FlowAllNode)jqlService.getEntity(FlowAllNode.class, nodeid);
					flowform.setNodeid(nodeid);
					
					flowform.setFlowinfoid(tempnode.getFlowinfoid());
					flowform.setModifier(getUsers().getId());
					flowform.setModifytime(new Date());
					flowform.setSubmiter(getUsers().getId());
					flowform.setSubmitdate(new Date());
					flowform.setCometime(new Date());
					flowform.setStateid(tempnode.getEndstateid());
					flowform.setStepName(tempnode.getActionname());
					flowform.setActionname(tempnode.getActionname());

					
					flowform.setUserID(users.getId());
					flowform.setOperateid(users.getId());
					flowform.setStatus(ApproveConstants.APPROVAL_STATUS_PAENDING);
			         
			         // 送审日期
					flowform.setDate(new Date());
			         // 送审说明
					flowform.setComment(flowform.getModifyscript());//冗余的，为了兼容原来的流程

					flowform.setApprovalUsersID(""+users.getId());
					flowform.setTitle(flowform.getNumname4());
					flowform.setStepName(tempnode.getActionname());
			        ArrayList<Long> sendhistory=new ArrayList<Long>();
			        sendhistory.add(users.getId());
			        flowform.setSendhistory(sendhistory);//孙爱华增加送审历史记录
					
					
					jqlService.save(flowform);
					
					SameSignInfo owners=new SameSignInfo();
					owners.setMainformid(flowform.getId());
					owners.setNodeid(flowform.getNodeid());
					owners.setSigner(users);
					owners.setFlowinforid(flowform.getFlowinfoid());
					owners.setStateid(flowform.getStateid());
					jqlService.save(owners);//首次处理默认是处理给自己，以后再改gggggggggggggggggggggggggggggggggggggggg
					
					//
					List<FlowFiles> uploadfilelist=(List<FlowFiles>)getRequest().getSession().getAttribute("uploadfilelist");
					if (uploadfilelist!=null && uploadfilelist.size()>0)
					{
						Long orgid=getParentOrgId();
						for (int i=0;i<uploadfilelist.size();i++)
						{
							FlowFiles flowfiles=uploadfilelist.get(i);
							flowfiles.setMainformid(flowform.getId());
							String parentpath=FileConstants.AUDIT_ROOT+"/"+orgid+"/"+flowform.getId();
							String serverfile=flowfiles.getFilepath()+flowfiles.getFilename();
							Fileinfo info = saveuploadfile(users,parentpath,flowfiles.getFilepath(),flowfiles.getFilename());//存储文件
							String savepath=info.getPathInfo();
							flowfiles.setFilepath(savepath);//文件地址

							jqlService.save(flowfiles);
							
							File file = new File(serverfile);
							if (file.exists())
							{
								file.delete();
							}
						}
						getRequest().getSession().setAttribute("uploadfilelist",null);
					}
					
					//还有其他需要增加的暂缓
					
					ApprovalTask flowformtemp=new ApprovalTask();
					flowformtemp=copyApprovalInfoHistory(flowformtemp,flowform);
					jqlService.save(flowformtemp);
				}
				else
				{
					//提示已经存在
					getRequest().setAttribute("errormsg", "该基本信息已经存在！");
					return addflowform();
				}
			}
			
			//多线程产生图片
			List<FlowAllNode> nodelist=jqlService.findAllBySql("select b from ApprovalTask as a,FlowAllNode as b "
					+"where a.nodeid=b.id and a.mainformid=? ", flowform.getId());
			//流程图名称根据流程号来获取
			System.out.println(flowform.getFlowinfoid()+"===flowinfoid==="+flowinfoid);
			new WorkFlowPic().writeLine("flow"+flowinfoid+".PNG",nodelist,flowform.getId());//生成图片
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
		}
		
	    return "successclose";
	}
	public String flowformsearch() throws Exception {

		Users users = getUsers();
		userid=users.getId();
		String SQL="select a from ApprovalInfo as a where a.submiter=? ";
		flowformlist=jqlService.findAllBySql(SQL, getUsers().getId());
		ExtremeTablePage extremeTablePage =new ExtremeTablePage();
		Limit limit = extremeTablePage.getLimit(getRequest());
		int viewnums=limit.getCurrentRowsDisplayed();
		if (viewnums==0)
		{
			viewnums=100;
		}
		limit.setRowAttributes(flowformlist.size(),viewnums);
		getRequest().setAttribute("flowformlist", flowformlist);
		getRequest().setAttribute("totalRows", flowformlist.size());
		return "formlist";
	}
	public String waititem() throws Exception {
		
		Users users = getUsers();
		userid=users.getId();
		String SQL="select distinct a from ApprovalInfo as a,SameSignInfo as b "
			+" where a.id=b.mainformid and b.deleted=0 and b.signer.id=? ";
		flowformlist=jqlService.findAllBySql(SQL, getUsers().getId());
		ExtremeTablePage extremeTablePage =new ExtremeTablePage();
		Limit limit = extremeTablePage.getLimit(getRequest());
		int viewnums=limit.getCurrentRowsDisplayed();
		if (viewnums==0)
		{
			viewnums=100;
		}
		limit.setRowAttributes(flowformlist.size(),viewnums);
		getRequest().setAttribute("flowformlist", flowformlist);
		getRequest().setAttribute("totalRows", flowformlist.size());
		return "modifyflowform";
	}
	public String haditem() throws Exception {
		
		Users users = getUsers();
		userid=users.getId();
		String SQL="select distinct a from ApprovalInfo as a,SameSignInfo as b "
			+" where a.id=b.mainformid and b.deleted=1 and b.signer.id=? ";
		flowformlist=jqlService.findAllBySql(SQL, getUsers().getId());
		ExtremeTablePage extremeTablePage =new ExtremeTablePage();
		Limit limit = extremeTablePage.getLimit(getRequest());
		int viewnums=limit.getCurrentRowsDisplayed();
		if (viewnums==0)
		{
			viewnums=100;
		}
		limit.setRowAttributes(flowformlist.size(),viewnums);
		getRequest().setAttribute("flowformlist", flowformlist);
		getRequest().setAttribute("totalRows", flowformlist.size());
		return "modifyflowform";
	}
	public String doingitem() throws Exception {
		
		Users users = getUsers();
		userid=users.getId();
		String SQL="select distinct a from ApprovalInfo as a,SameSignInfo as b "
			+" where a.id=b.mainformid and b.deleted=2 and b.signer.id=? ";
		flowformlist=jqlService.findAllBySql(SQL, getUsers().getId());
		ExtremeTablePage extremeTablePage =new ExtremeTablePage();
		Limit limit = extremeTablePage.getLimit(getRequest());
		int viewnums=limit.getCurrentRowsDisplayed();
		if (viewnums==0)
		{
			viewnums=100;
		}
		limit.setRowAttributes(flowformlist.size(),viewnums);
		getRequest().setAttribute("flowformlist", flowformlist);
		getRequest().setAttribute("totalRows", flowformlist.size());
		return "modifyflowform";
	}
	public String searchflow() throws Exception {
		//查出所有的流程，按照类别分类
		Users users = getUsers();
		userid=users.getId();
		if (flowtype==null || flowtype.intValue()==-1)
		{
			flowinfolist=jqlService.findAllBySql("select a from FlowInfo as a where a.orgid=? order by flownum ", getParentOrgId());
		}
		else
		{
			flowinfolist=jqlService.findAllBySql("select a from FlowInfo as a where a.orgid=? and a.flowtype=? order by flownum ", getParentOrgId(),flowtype);
		}
		ExtremeTablePage extremeTablePage =new ExtremeTablePage();
		Limit limit = extremeTablePage.getLimit(getRequest());
		int viewnums=limit.getCurrentRowsDisplayed();
		if (viewnums==0)
		{
			viewnums=100;
		}
		limit.setRowAttributes(flowinfolist.size(),viewnums);
		getRequest().setAttribute("flowinfolist", flowinfolist);
		getRequest().setAttribute("totalRows", flowinfolist.size());
		return "searchflow";
	}
	public String selectman()throws Exception {
		//还需要拿组织结构
		
		Users users = getUsers();
		userid=users.getId();
		orglist=jqlService.findAllBySql("select a from Organizations as a where a.parent=null ");
		//获取常用语，暂写死
		return "selectman";
	}
	public String uploadfile()throws Exception {
		//显示上传文件对话框
		Users users = getUsers();
		userid=users.getId();
		if (flowform.getId()!=null)
		{
			formid=flowform.getId();
		}
		getRequest().getSession().setAttribute("uploadfilelist", null);
		return "uploadfile";
	}
	public String uploadsuccess()throws Exception {
		//上传文件，并返回
		Users users = getUsers();
		userid=users.getId();
		InputStream in=null;
		OutputStream out=null;
		
		//formid
		//flowinfoid
		//filetype
		//nodeid
		
		List<FlowFiles> uploadfilelist=new ArrayList<FlowFiles>();
		
		Long orgid=0L;
		if (formid!=null && formid.longValue()>0)
		{
			orgid=getParentOrgId();
		}
		for (int i=0;i<filepaths.length;i++)
		{
			//存储文件
			String filepath=ServletActionContext.getServletContext().getRealPath("data/uploadfile")+File.separator;
			String filename=filepathsFileName[i];
			String outfile=filepath+filename;
			File file=new File(outfile);
			  try
			  {
				   
				   in=new BufferedInputStream(new FileInputStream(filepaths[i]),FILE_SIZE);
				   out=new BufferedOutputStream(new FileOutputStream(file),FILE_SIZE);
				   byte[] image=new byte[FILE_SIZE];
				   while(in.read(image)>0){
				    out.write(image);
				   }
			  } catch(IOException ex){
				  		ex.printStackTrace();
				  }finally{
				   try{
				    in.close();
				    out.close();
				   }catch(IOException ex){
				    
				   }
			  }
			 
			  //存储到数据库中
			  FlowFiles flowfiles=new FlowFiles();
			  flowfiles.setMainformid(formid);//flowform的主键，相当于具体流程编号
			  flowfiles.setOutid(formid);
			  flowfiles.setFiletype(filetype);
			  flowfiles.setFilename(filename);//文件名称
			  flowfiles.setCreatetime(new Date());
			  flowfiles.setUploadid(users.getId());//上传者
			  if (formid!=null && formid.longValue()>0)
			  {
				  String parentpath=FileConstants.AUDIT_ROOT+"/"+orgid+"/"+formid;
				  Fileinfo info = saveuploadfile(users,parentpath,filepath,filename);//存储文件
				  String savepath=info.getPathInfo();
				  flowfiles.setFilepath(savepath);//文件地址
				  jqlService.update(flowfiles);//保存表单附件
				  
				  file.delete();
			  }
			  else
			  {
				  flowfiles.setFilepath(filepath);//文件地址
				  uploadfilelist.add(flowfiles);
			  }
		}
		getRequest().getSession().setAttribute("uploadfilelist", uploadfilelist);
		getRequest().setAttribute("uploadfilesuccess", "success");
		return "uploadsuccess";
	}
	
	private Fileinfo saveuploadfile(Users user,String parentpath,String filepath,String filename) throws Exception
	{
		FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
        File file = new File(filepath + filename);
        InputStream fin = new FileInputStream(file);
        InputStream ois = new FileInputStream(file);
		    		
        Fileinfo info = fileSystemService.createFile(user.getId(), user.getRealName(),parentpath, filename, fin,  ois, false, null, true);
        fin.close();
        file.delete(); 
        return info;
	}
	public String delete() throws Exception {
		
//		jqlService.excute("delete from FlowInfo where id=? and flowname=? ", flowinfo.getId(),flowinfo.getFlowname());
		return flowformsearch();
	}
	private ApprovalInfo copyApprovalInfo(ApprovalInfo flowform,ApprovalInfo old)
	{
		if (old!=null)
		{
			Users userinfo = getUsers();
			flowform.setFlowinfoid(old.getFlowinfoid());
			flowform.setNodeid(old.getNodeid());
//			flowform.setModifier(old.getModifier());
//			flowform.setModifytime(old.getModifytime());
//			flowform.setStateid(old.getStateid());
//			flowform.setOwner(old.getOwner());
//			flowform.setStepname(old.getStepname());
//			flowform.setActionname(old.getActionname());
//			flowform.setCometime(old.getCometime());
			flowform.setModifyscript(old.getModifyscript());
			flowform.setNum1(old.getNum1());
			flowform.setNum2(old.getNum2());
			flowform.setNum3(old.getNum3());
			flowform.setNum4(old.getNum4());
			flowform.setNum5(old.getNum5());
			flowform.setNum6(old.getNum6());
			flowform.setNum7(old.getNum7());
			flowform.setNum8(old.getNum8());
			flowform.setNum9(old.getNum9());
			flowform.setNum10(old.getNum10());
			flowform.setNum11(old.getNum11());
			flowform.setNum12(old.getNum12());
			
			flowform.setNumname1(old.getNumname1());
			flowform.setNumname2(old.getNumname2());
			flowform.setNumname3(old.getNumname3());
			flowform.setNumname4(old.getNumname4());
			flowform.setNumname5(old.getNumname5());
			flowform.setNumname6(old.getNumname6());
			flowform.setNumname7(old.getNumname7());
			flowform.setNumname8(old.getNumname8());
			flowform.setNumname9(old.getNumname9());
			flowform.setNumname10(old.getNumname10());
			flowform.setNumname11(old.getNumname11());
			flowform.setNumname12(old.getNumname12());
		}
		return flowform;
	}
	private ApprovalTask copyApprovalInfoHistory(ApprovalTask flowform,ApprovalInfo old)
	{
		if (old!=null)
		{
			Users userinfo = getUsers();
			flowform.setMainformid(old.getId());
			flowform.setFlowinfoid(old.getFlowinfoid());
			flowform.setNodeid(old.getNodeid());
			flowform.setModifier(old.getModifier());
			flowform.setModifytime(old.getModifytime());
			flowform.setStateid(old.getStateid());
			flowform.setOwner(old.getOwner());
			flowform.setStepName(old.getStepName());
			flowform.setActionname(old.getActionname());
			flowform.setCometime(old.getCometime());
			flowform.setModifyscript(old.getModifyscript());
			flowform.setSubmiter(old.getSubmiter());
			flowform.setSubmitdate(old.getSubmitdate());
			
			
			
			flowform.setNum1(old.getNum1());
			flowform.setNum2(old.getNum2());
			flowform.setNum3(old.getNum3());
			flowform.setNum4(old.getNum4());
			flowform.setNum5(old.getNum5());
			flowform.setNum6(old.getNum6());
			flowform.setNum7(old.getNum7());
			flowform.setNum8(old.getNum8());
			flowform.setNum9(old.getNum9());
			flowform.setNum10(old.getNum10());
			flowform.setNum11(old.getNum11());
			flowform.setNum12(old.getNum12());
			
			flowform.setNumname1(old.getNumname1());
			flowform.setNumname2(old.getNumname2());
			flowform.setNumname3(old.getNumname3());
			flowform.setNumname4(old.getNumname4());
			flowform.setNumname5(old.getNumname5());
			flowform.setNumname6(old.getNumname6());
			flowform.setNumname7(old.getNumname7());
			flowform.setNumname8(old.getNumname8());
			flowform.setNumname9(old.getNumname9());
			flowform.setNumname10(old.getNumname10());
			flowform.setNumname11(old.getNumname11());
			flowform.setNumname12(old.getNumname12());
		}
		return flowform;
	}
	public Long getParentOrgId()
	{
		Long orgid=0L;
		Users users = getUsers();
		if (users!=null)
		{
			Long userid=users.getId();
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
		}
		return orgid;
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

	
	
	
	
	
	
	
	public List<ApprovalInfo> getFlowformlist()
	{
		return flowformlist;
	}
	public void setFlowformlist(List<ApprovalInfo> flowformlist)
	{
		this.flowformlist = flowformlist;
	}

	public ApprovalInfo getFlowform()
	{
		return flowform;
	}
	public void setFlowform(ApprovalInfo flowform)
	{
		this.flowform = flowform;
	}

	public Long getFlowinfoid()
	{
		return flowinfoid;
	}
	public void setFlowinfoid(Long flowinfoid)
	{
		this.flowinfoid = flowinfoid;
	}
	public Integer getFlowtype()
	{
		return flowtype;
	}
	public void setFlowtype(Integer flowtype)
	{
		this.flowtype = flowtype;
	}
	public List<FlowInfo> getFlowinfolist()
	{
		return flowinfolist;
	}
	public void setFlowinfolist(List<FlowInfo> flowinfolist)
	{
		this.flowinfolist = flowinfolist;
	}
	public List<ApprovalInfo> getApprovallist()
	{
		return approvallist;
	}
	public void setApprovallist(List<ApprovalInfo> approvallist)
	{
		this.approvallist = approvallist;
	}
	public void setJqlService(JQLServices jqlService)
	{
		this.jqlService = jqlService;
	}

	public List<FlowAllNode> getFlowallnodelist()
	{
		return flowallnodelist;
	}
	public void setFlowallnodelist(List<FlowAllNode> flowallnodelist)
	{
		this.flowallnodelist = flowallnodelist;
	}
	public Long getNodeid()
	{
		return nodeid;
	}
	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}
	public Long getStartstateid()
	{
		return startstateid;
	}
	public void setStartstateid(Long startstateid)
	{
		this.startstateid = startstateid;
	}
	public Integer getIspower()
	{
		return ispower;
	}
	public void setIspower(Integer ispower)
	{
		this.ispower = ispower;
	}
	
	public Long[] getOwnerids()
	{
		return ownerids;
	}
	public void setOwnerids(Long[] ownerids)
	{
		this.ownerids = ownerids;
	}
	public String getComments()
	{
		return comments;
	}
	public void setComments(String comments)
	{
		this.comments = comments;
	}
	public Long getUserid()
	{
		return userid;
	}
	public void setUserid(Long userid)
	{
		this.userid = userid;
	}
	public Integer getFlashparent()
	{
		return flashparent;
	}
	public void setFlashparent(Integer flashparent)
	{
		this.flashparent = flashparent;
	}
	public Long getFormid()
	{
		return formid;
	}
	public void setFormid(Long formid)
	{
		this.formid = formid;
	}
	public File[] getFilepaths()
	{
		return filepaths;
	}
	public void setFilepaths(File[] filepaths)
	{
		this.filepaths = filepaths;
	}

	public String[] getUploadContentType()
	{
		return uploadContentType;
	}
	public void setUploadContentType(String[] uploadContentType)
	{
		this.uploadContentType = uploadContentType;
	}
	public String[] getFilepathsFileName()
	{
		return filepathsFileName;
	}
	public void setFilepathsFileName(String[] filepathsFileName)
	{
		this.filepathsFileName = filepathsFileName;
	}
	
	public Integer getFiletype()
	{
		return filetype;
	}
	public void setFiletype(Integer filetype)
	{
		this.filetype = filetype;
	}
	public List<FlowFiles> getInnerfilelist()
	{
		return innerfilelist;
	}
	public void setInnerfilelist(List<FlowFiles> innerfilelist)
	{
		this.innerfilelist = innerfilelist;
	}
	public List<FlowFiles> getOutfilelist()
	{
		return outfilelist;
	}
	public void setOutfilelist(List<FlowFiles> outfilelist)
	{
		this.outfilelist = outfilelist;
	}

	public Integer getInnerTag1()
	{
		return innerTag1;
	}
	public void setInnerTag1(Integer innerTag1)
	{
		this.innerTag1 = innerTag1;
	}
	public FlowFiles getFlowfiles()
	{
		return flowfiles;
	}
	public void setFlowfiles(FlowFiles flowfiles)
	{
		this.flowfiles = flowfiles;
	}
	public List<Organizations> getOrglist()
	{
		return orglist;
	}
	public void setOrglist(List<Organizations> orglist)
	{
		this.orglist = orglist;
	}
}
