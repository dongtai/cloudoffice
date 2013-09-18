package apps.bugs;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.extremecomponents.table.limit.Limit;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.bug.BugActions;
import apps.transmanager.weboffice.databaseobject.bug.BugFiles;
import apps.transmanager.weboffice.databaseobject.bug.BugHistories;
import apps.transmanager.weboffice.databaseobject.bug.BugInfos;
import apps.transmanager.weboffice.databaseobject.bug.BugModifyInfos;
import apps.transmanager.weboffice.databaseobject.bug.BugStates;
import apps.transmanager.weboffice.service.cache.IMemCache;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.objects.LoginUserInfo;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.UserService;

/**
 * bug处理action
 * @author sah
 * 2013.6.15
 */
public class BugWork extends AllSupport {

//	private IUserService userService = (IUserService) ApplicationContext.getInstance().getBean("userService");
	private IBugsService bugsService = (IBugsService) ApplicationContext.getInstance().getBean("bugsService");
	private BugInfos bugInfos=new BugInfos();
	private BugModifyInfos bugModifyInfos=new BugModifyInfos();

	private List<BugInfos> bugslist=new ArrayList<BugInfos>();
	private List<BugFiles> bugFiles=new ArrayList<BugFiles>();
	private List<Users> reporterlist=new ArrayList<Users>();
	private List<BugActions> actionlist=new ArrayList<BugActions>();
	private BugStates defaultstate=new BugStates();//动作列表的第一个动作对应的状态
	private List<Users> ownerlist=new ArrayList<Users>();

	private Integer powertype;
	private String actiontype;
	private Boolean isedit;
	private boolean ismodify=false;


	/* 文件上传 */
	private List<File> upattach = new ArrayList<File> ();
    private List<String> upattachFileName = new ArrayList<String> ();
    private List<String> upattachContentType = new ArrayList<String> ();

	/* 附件编辑 */
    private String delfiles;//删除的附件
	private Long bugid;//问题编号,显示用的
	private String username;//登录的用户账号
    private String token;//用户登录的token
    private String domain;//系统用的DOMAIN
    private String bugadd;
    private String bugmanage;
    private Users user;//当前登录者
    private Date startdate;//查询用的，开始时间
    private Date enddate;//查询用的，结束时间
    private String reason;//模糊查询用的，概要和步骤
    private String viewtype="";//是否显示左侧的导航
	
	private boolean isInvalid(String value) {
        return (value == null || value.length() == 0);
    }
	private Users getUser()
	{
		if (user==null)
		{
			UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
			return userService.getUser(username);
		}
		else
		{
			return user;
		}
	}
	/**
	 * 检查当前用户是否合法登录
	 * @return
	 */
	private boolean checkToken()
	{
        IMemCache  memCache = (IMemCache)ApplicationContext.getInstance().getBean("memCacheBean");
        LoginUserInfo lui = memCache.getLoginUserInfo("com.yozo.do" + "+" + username.toLowerCase());
        if (token != null && lui != null && token.equals(lui.getToken()))
        {
        	return true;
        }
        return false;
	}
	//根据编号显示BUG信息
	public String view() throws Exception
	{
		if (bugInfos.getId()!=null || bugid!=null || getRequest().getSession().getAttribute("bugid")!=null)
		{
			if (bugInfos.getId()!=null)//刷新页面时需要的
			{
				
			}
			else if (bugid!=null)
			{
				bugInfos.setId(bugid);
			}
			else if (getRequest().getSession().getAttribute("bugid")!=null)
			{
				bugInfos.setId((Long)getRequest().getSession().getAttribute("bugid"));
			}
			
		}
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		
		bugInfos=bugsService.findById(bugInfos.getId());
		bugInfos.setUsername(userService.getUser(bugInfos.getUserid()).getRealName());
		isedit=bugInfos.getIsedit();
		
		bugModifyInfos=bugsService.findModifyById(bugInfos.getId());
		bugModifyInfos.setModifyname(userService.getUser(bugModifyInfos.getModifier()).getRealName());
		bugModifyInfos.setOwnername(userService.getUser(bugModifyInfos.getOwnerid()).getRealName());
		bugFiles=bugsService.findFilesByBugId(bugInfos.getId());//获取BUG附件
		bugInfos.setBugFiles(bugFiles);
		
		//判断当前用户有没有处理BUG的权限，目前只要判断是否在用户列表中和状态
		ismodify=bugsService.ismodifyUser(getUserinfo().getId());
		if (bugModifyInfos.getBugStates()!=null && !getUserinfo().getUserName().equals("admin_wx") && bugModifyInfos.getBugStates().getId().longValue()==6)
		{
			ismodify=false;//关闭的BUG不能再处理
		}
		
		getRequest().setAttribute("bugInfos", bugInfos);
		getRequest().getSession().setAttribute("savebug", null);//防止重复提交
		getRequest().setAttribute("username", username);
		getRequest().setAttribute("token", token);
		getRequest().setAttribute("domain", domain);
		getRequest().setAttribute("bugadd", bugadd);
		getRequest().setAttribute("bugmanage", bugmanage);
		getRequest().setAttribute("ismodify", ismodify);
		System.out.println("flash========================================");

		return VIEW;
	}
	
	//编辑BUG
	public String editbug() throws Exception
	{
		view();
		return "edit";
	}
    //保存bug信息
	public String save() throws Exception
    {
		Users users=getUserinfo();
		Long userid=users.getId();
		setBugPower();
		Long[] groupids =(Long[])getRequest().getSession().getAttribute("usergroups");
		//先处理时间,应该不为空,请做验证处理
		SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
		if(bugInfos.getSummer()!=null || bugInfos.getOpstep()!=null)//概述或步骤不能都为空，否则就认为是破坏
		{
			if (bugInfos.getId()==null && getRequest().getSession().getAttribute("savebug")==null)
			{
				bugInfos.setAdddate(new Date());
				bugInfos.setNumid(1l);
				bugInfos.setUsername(users.getRealName());
				bugInfos.setUserid(users.getId());
				if(groupids!=null)
				{
					bugInfos.setRootgroupid(groupids[0]);
				}
		    	bugsService.save(bugInfos);
		    	//增加处理信息表数据
		    	BugModifyInfos bugModifyInfos=new BugModifyInfos();
		    	bugModifyInfos.setBugInfos(bugInfos);
		    	bugModifyInfos.setModifier(users.getId());
		    	//获取单位管理员的编号
		    	Users adminuser=bugsService.getCompanyAdmin(users.getCompany().getId());
		    	if (adminuser!=null)
		    	{
		    		bugModifyInfos.setOwnerid(adminuser.getId());//默认先分配给单位管理员
		    	}
		    	else
		    	{
		    		bugModifyInfos.setOwnerid(users.getId());//一般不会没有单位管理员的，这里是为了容错
		    	}
		    	bugModifyInfos.setModifydate(new Date());
		    	
		    	bugModifyInfos.setBugActions((BugActions)bugsService.findObjById(BugActions.class,1l));//默认未处理
		    	bugModifyInfos.setBugStates((BugStates)bugsService.findObjById(BugStates.class,1l));//默认未处理状态
		    	bugsService.save(bugModifyInfos);
		    	//插入历史记录
		    	BugHistories history=copyTohistory(bugInfos,bugModifyInfos);
		    	history.setOphistype("新增BUG");//操作类型，新增BUG、修改BUG，处理BUG
				history.setModifier(userid);//处理人,不要从modifyinfo中取，自己的时间和人
				history.setModifydate(new Date());//处理时间
		    	bugsService.savehistory(history);//插入历史记录
		    	System.out.println(upattach.size());
		    	System.out.println(upattachFileName.size());
		    	System.out.println(upattachContentType.size());
		    	saveImg(bugInfos.getId());//插入附件
		    	bugInfos.setBugFiles(bugFiles);
			}
			else
			{
				bugInfos=copyBugInfos(bugsService.findById(bugInfos.getId()),bugInfos);//复制相关内容，防止把原来的冲掉
				editImg(bugInfos.getId());//编辑附件
				bugsService.update(bugInfos);

				//插入历史记录
				BugHistories history=copyTohistory(bugInfos,null);
				history.setOphistype("修改BUG");//操作类型，新增BUG、修改BUG，处理BUG
				history.setModifier(userid);//处理人,不要从modifyinfo中取，自己的时间和人
				history.setModifydate(new Date());//处理时间
		    	bugsService.savehistory(history);
			}
		}
    	getRequest().getSession().setAttribute("bugid", bugInfos.getId());
    	getRequest().getSession().setAttribute("savebug", "submit");//防止重复提交
    	view();
    	if ("view".equals(viewtype))
    	{
    		return VIEW;
    	}
    	return "confirm";
    }
	//处理BUG
    public String modifybug() throws Exception
    {
    	
    	view();
    	//判断当前用户有没有处理BUG的权限
    	
    	//获取动作列表
    	actionlist=bugsService.findActions(bugModifyInfos.getBugStates().getId());
    	if (actionlist!=null && actionlist.size()>0)
    	{
    		defaultstate=actionlist.get(0).getBugStates();
    	}
    	//获取人员列表
    	ownerlist=bugsService.findOwnerUsers();
    	return "modifybug";
    }
    //处理BUG提交
    public String modifyupdate() throws Exception
    {
    	//前端还要进行动作和状态联动处理
    	if (bugInfos.getId()!=null)
    	{
    		//更新数据
    		BugModifyInfos newmodify=bugsService.findModifyById(bugInfos.getId());
    		BugInfos newinfo=bugsService.findById(bugInfos.getId());
    		long actionid=bugModifyInfos.getBugActions().getId().longValue();
    		if (bugInfos.getIsedit()!=null)
    		{
	    		newinfo.setIsedit(bugInfos.getIsedit());
    		}
    		if (actionid==2 || actionid==3  || actionid==4  || actionid==5 || actionid==6 || actionid==7 )
    		{
    			newinfo.setModifyresult(2);
    		}
    		else if (actionid==11 )
    		{
    			newinfo.setModifyresult(3);
    		}
    		else if (actionid==8 )
    		{
    			newinfo.setModifyresult(4);
    		}
    		else 
    		{
    			newinfo.setModifyresult(5);
    			
    		}
    		newinfo.setBugaction(bugModifyInfos.getBugActions().getId());
    		newinfo.setBugstate(bugModifyInfos.getBugStates().getId());
    		bugsService.update(newinfo);
    		newmodify=copyBugModifys(newmodify,bugModifyInfos);
    		newmodify.setModifier(getUserinfo().getId());
    		Date mdate=new Date();
    		newmodify.setModifydate(mdate);
    		bugsService.update(newmodify);
    		//插入历史记录
    		BugHistories history=copyTohistory(bugInfos,newmodify);
			history.setOphistype("处理BUG");//操作类型，新增BUG、修改BUG，处理BUG
			history.setModifier(getUserinfo().getId());//处理人,不要从modifyinfo中取，自己的时间和人
			history.setModifydate(mdate);//处理时间
	    	bugsService.savehistory(history);
    	}
    	return view();
    }
	
	//根据条件查询BUG信息
    public String searchbugs() throws Exception
    {
    	if ("del".equals(actiontype) && getUserinfo().getUserName().equals("admin_wx"))//只有管理员和自己才能删除BUG
    	{
    		//删除记录
			bugsService.deleteByID(bugInfos.getId());//删除bug信息,如果有必要进行软删除
    	}

       	ExtremeTablePage extremeTablePage =new ExtremeTablePage();
		Limit limit = extremeTablePage.getLimit(getRequest());
		System.out.println("RowStart====="+limit.getRowStart());
		int viewnums=limit.getCurrentRowsDisplayed();
		if (viewnums==0)
		{
			viewnums=defaultnum;
		}
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		int totalRows=bugsService.findByBugsize(bugInfos,getUser());
		limit.setRowAttributes(totalRows,viewnums);

		String sort=extremeTablePage.getSort(limit);
		List<BugInfos> list=bugsService.findByBugs(bugInfos,getUser(),limit.getRowStart(), limit.getCurrentRowsDisplayed(),sort);
		reporterlist=bugsService.getReportList();//获取所有报BUG的人员,先这样做，如果人员和BUG数多了，不适合这样做
		getRequest().setAttribute("reporterlist", reporterlist);
		getRequest().setAttribute("bugslist", list);
		getRequest().setAttribute("totalRows", new Integer(totalRows));
		
//		bugInfos.setUserid(getUserinfo().getId());
		System.out.println(totalRows);
		return "searchlist";
    }
    
    //查询历史记录
    public String history() throws Exception
	{
		if (bugInfos.getId()!=null)
		{
			ExtremeTablePage extremeTablePage =new ExtremeTablePage();
			Limit limit = extremeTablePage.getLimit(getRequest());
			int viewnums=limit.getCurrentRowsDisplayed();
			if (viewnums==0)
			{
				viewnums=defaultnum;
			}
			int totalRows=bugsService.gethistorysize(bugInfos.getId()).intValue();
			limit.setRowAttributes(totalRows,viewnums);
			
			String sort=extremeTablePage.getSort(limit);
			List<BugHistories> list=bugsService.gethistory(bugInfos.getId(), limit.getRowStart(), limit.getCurrentRowsDisplayed(),sort);
			
			getRequest().setAttribute("bugslist", list);
			getRequest().setAttribute("totalRows", new Integer(totalRows));
			System.out.println(totalRows);
		}
		return "history";
	}

    public String add() throws Exception {
    	try
    	{
    		if (!checkToken())
    		{
    			return "logout";
    		}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return "add";
    }
	//默认的action方法
	public String execute() throws Exception {
    	try
    	{
    		if (!checkToken())
    		{
    			return "logout";
    		}
    		
    		System.out.println(getUsername()+"================");
//	    	List<AdminUserinfoView> list=userService.getAdminUsers();
//	    	if (list!=null)
//	    	{
//	    		System.out.println("size=========="+list.size());
//	    	}
//	        if (isInvalid(getUsername())) return INPUT;
	
//	        if (isInvalid(getPassword())) return INPUT;
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
        return "add";
    }
	/**
	 * 编辑附件信息
	 * @param bugid 
	 */
	private void editImg(Long bugid) {
		//先保存新的图片
		saveImg(bugid);
		//在删除图片
		if(delfiles!=null && !delfiles.equals(""))
		{
			String[] imgIdArr = delfiles.split(",");
//			bugsService.delfiles(ServletActionContext.getServletContext().getRealPath("/"),imgIdArr);
			bugsService.delfiles(imgIdArr);
		}
		
	}

	/**
	 * 保存附件信息
	 * @param bugid 
	 */
    private void saveImg(Long bugid) {
		if(upattach!=null && !upattach.isEmpty())
		{
			//目录根据年份月份命名
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			String relUrlDir = "data/bugfiles/"+sdf.format(new Date());
			String bugfilePath = ServletActionContext.getServletContext().getRealPath("/")+relUrlDir;
			File testDir = new File(bugfilePath);
			if(!testDir.exists())
			{
				testDir.mkdirs();
			}
			List<BugFiles> fileList = new ArrayList<BugFiles>();
			for(int i=0;i<upattach.size();i++)
			{
				if (upattach.get(i)!=null)
				{
					String fileName=bugid.longValue()+"_"+System.currentTimeMillis()+upattachFileName.get(i).substring(upattachFileName.get(i).lastIndexOf("."));
					String relUrl = relUrlDir+"/"+fileName;
					String name = fileName;
					try {
						File desFile = new File(bugfilePath+"/"+fileName);
						int num =1;
						while(desFile.exists())
						{
							name ="("+num+")"+fileName;
							desFile = new File(relUrlDir+"/"+ name);
							relUrl = relUrlDir+"/"+name;
							num++;
						}
						BugFiles bugFiles = new BugFiles();
						
						bugFiles.setBugInfos(bugInfos);
						bugFiles.setFileurl(relUrl);
						bugFiles.setFilename(upattachFileName.get(i));
						bugFiles.setUserid(getUserinfo().getId());
						fileList.add(bugFiles);
						System.out.println("desFile================================"+desFile.getPath());
						FileUtils.copyFile(upattach.get(i),desFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			bugsService.saveFiles(fileList);
		}
	}

	private BugModifyInfos copyBugModifys(BugModifyInfos newmodify,BugModifyInfos oldmodify)
	{
		newmodify.setBugActions(oldmodify.getBugActions());
		newmodify.setBugStates(oldmodify.getBugStates());
		newmodify.setOwnerid(oldmodify.getOwnerid());
		if (oldmodify.getTestuser()!=null && oldmodify.getTestuser().length()>0)
		{
			newmodify.setTestuser(oldmodify.getTestuser());
		}
		if (oldmodify.getRebugs()!=null && oldmodify.getRebugs().length()>0)
		{
			newmodify.setRebugs(oldmodify.getRebugs());
		}
		newmodify.setModifyscript(oldmodify.getModifyscript());
		return newmodify;
	}
	private BugInfos copyBugInfos(BugInfos newinfo,BugInfos oldinfo)
	{
		newinfo.setErrortype(oldinfo.getErrortype());
		newinfo.setBugtype(oldinfo.getBugtype());
		newinfo.setSeriousid(oldinfo.getSeriousid());
		newinfo.setEnvironmentid(oldinfo.getEnvironmentid());
		newinfo.setOtheren(oldinfo.getOtheren());
		newinfo.setOs(oldinfo.getOs());
		newinfo.setSummer(oldinfo.getSummer());
		newinfo.setOpstep(oldinfo.getOpstep());
		newinfo.setIsedit(false);
		newinfo.setEdittime(new Date());
		return newinfo;
	}
	private BugHistories copyTohistory(BugInfos bugInfos,BugModifyInfos bugModifyInfos)
	{
		BugHistories history=new BugHistories();
		history.setBugid(bugInfos.getId());//bug编号
		history.setUserid(bugInfos.getUserid());//报BUG人员
		history.setUsername(bugInfos.getUsername());//报BUG人员名称，冗余
		history.setAdddate(bugInfos.getAdddate()); //报BUG时间
		history.setNumid(bugInfos.getNumid());//报BUG人的自己BUG编号
		history.setSoftid(bugInfos.getSoftid());//软件号，默认1为信电局版本
		history.setErrortype(bugInfos.getErrortype()); //问题类别，1为云办公，2为OFFICE，3为移动，4为IOS
		history.setBugtype(bugInfos.getBugtype()); //BUG类型，1为BUG，2为建议
		history.setSeriousid(bugInfos.getSeriousid()); //严重性，1、一般问题，2、无法使用，3、死机
		history.setEnvironmentid(bugInfos.getEnvironmentid()); //环境
		history.setOtheren(bugInfos.getOtheren());//其他环境
		history.setOs(bugInfos.getOs());//操作系统
		history.setSummer(bugInfos.getSummer());//问题概述
		history.setOpstep(bugInfos.getOpstep());//操作步骤
		history.setIsedit(bugInfos.getIsedit());//是否可编辑，编辑完后自动变为false
		history.setEdittime(bugInfos.getEdittime());//编辑时间
		history.setModifyresult(bugInfos.getModifyresult());//处理结果 0、未确认，1、BUG，2、重复BUG，3、产品定义
		history.setRebugid(bugInfos.getRebugid());//重复的BUG号
		
		if (bugModifyInfos!=null)
		{
			history.setBuglevel(bugModifyInfos.getBuglevel());//BUG优先级，1，立解，2、必解，3、要解，4，缓解，5，不解
			history.setSolveuser(bugModifyInfos.getSolveuser());//解决人
			history.setSolvedate(bugModifyInfos.getSolvedate());//解决时间
			history.setBugActions(bugModifyInfos.getBugActions());
			history.setBugStates(bugModifyInfos.getBugStates());
			history.setOwnerid(bugModifyInfos.getOwnerid());
			history.setRebugs(bugModifyInfos.getRebugs());
			history.setTestuser(bugModifyInfos.getTestuser());//测试用户
			history.setTestdate(bugModifyInfos.getTestdate());//测试时间
			history.setTestresult(bugModifyInfos.getTestresult());//测试结果0未测试，1、测试通过，2、测试不通过
			history.setModifyscript(bugModifyInfos.getModifyscript());//处理备注
		}
		return history;
	}
    public static final String MESSAGE = "HelloWorld.message";
    public String welcome() throws Exception {

        return execute();
    }
    
	public static String getStr(String str)
	{
		if (str==null)
		{
			return str;
		}
		try
		{
			String temp=new String(str.getBytes("iso-8859-1"), "utf8");
			System.out.println(new String(str.getBytes("iso-8859-1"), "GBK"));
			System.out.println(new String(str.getBytes("GBK"), "utf8"));
			System.out.println(new String(str.getBytes("utf8"), "iso-8859-1"));
			System.out.println(new String(str.getBytes("utf8"), "GBK"));
			return temp;
		}
		catch (Exception e)
		{
			return str;
		}
	}
    public static String[] getActionStates(Long actionid)
    {
    	IBugsService tempService = (IBugsService) ApplicationContext.getInstance().getBean("bugsService");
    	BugActions tempaction=(BugActions)tempService.findObjById(BugActions.class,actionid);
    	BugStates tempstate=tempaction.getBugStates();
    	return new String[]{String.valueOf(tempstate.getId()),tempstate.getStatename()};
    }
    public String blank() throws Exception
    {
    	return "searchlist";
    }
        
    
    
    public IBugsService getBugsService() {
		return bugsService;
	}
	public void setBugsService(IBugsService bugsService) {
		this.bugsService = bugsService;
	}

	public String getActiontype() {
		return actiontype;
	}

	public void setActiontype(String actiontype) {
		this.actiontype = actiontype;
	}

	
	public void setBugPower(){
		HttpSession session = getRequest().getSession();
		Users userInfo=getUserinfo();
    	if (userInfo!=null && session.getAttribute("usergroups")==null)
    	{
    		try
    		{
        		Map<String,Object> groupIdsAndRootCode = bugsService.getRootGroupId(userInfo.getId());
        		Long[] groupids= (Long[]) groupIdsAndRootCode.get("groupIDs");
        		String depRootCode = (String) groupIdsAndRootCode.get("depRootCode");
        		session.setAttribute("usergroups", groupids);
        		session.setAttribute("depRootCode", depRootCode);
    		}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
	}
    public String getDelfiles() {
		return delfiles;
	}
	public void setDelfiles(String delfiles) {
		this.delfiles = delfiles;
	}
	public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
    
	public BugStates getDefaultstate() {
		return defaultstate;
	}
	public void setDefaultstate(BugStates defaultstate) {
		this.defaultstate = defaultstate;
	}
	public Boolean getIsedit() {
		return isedit;
	}
	public void setIsedit(Boolean isedit) {
		this.isedit = isedit;
	}
	public BugInfos getBugInfos() {
		return bugInfos;
	}
	public void setBugInfos(BugInfos bugInfos) {
		this.bugInfos = bugInfos;
	}
	public List<BugFiles> getBugFiles() {
		return bugFiles;
	}
	public void setBugFiles(List<BugFiles> bugFiles) {
		this.bugFiles = bugFiles;
	}
	public Integer getPowertype() {
		return powertype;
	}
	public void setPowertype(Integer powertype) {
		this.powertype = powertype;
	}

    public List<File> getUpattach() {
		return upattach;
	}
	public void setUpattach(List<File> upattach) {
		this.upattach = upattach;
	}
	public List<String> getUpattachFileName() {
		return upattachFileName;
	}
	public void setUpattachFileName(List<String> upattachFileName) {
		this.upattachFileName = upattachFileName;
	}
	public List<String> getUpattachContentType() {
		return upattachContentType;
	}
	public void setUpattachContentType(List<String> upattachContentType) {
		this.upattachContentType = upattachContentType;
	}
	public String getBugadd() {
		return bugadd;
	}
	public void setBugadd(String bugadd) {
		this.bugadd = bugadd;
	}
	public String getBugmanage() {
		return bugmanage;
	}
	public void setBugmanage(String bugmanage) {
		this.bugmanage = bugmanage;
	}
	public static String getMessage() {
		return MESSAGE;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public Date getEnddate() {
		return enddate;
	}
	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public List<BugInfos> getBugslist() {
		return bugslist;
	}
	public void setBugslist(List<BugInfos> bugslist) {
		this.bugslist = bugslist;
	}
	public List<Users> getReporterlist() {
		return reporterlist;
	}
	public void setReporterlist(List<Users> reporterlist) {
		this.reporterlist = reporterlist;
	}
	public BugModifyInfos getBugModifyInfos() {
		return bugModifyInfos;
	}
	public void setBugModifyInfos(BugModifyInfos bugModifyInfos) {
		this.bugModifyInfos = bugModifyInfos;
	}
    public Long getBugid() {
		return bugid;
	}
	public void setBugid(Long bugid) {
		this.bugid = bugid;
	}
	public String getViewtype() {
		return viewtype;
	}
	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}
	
	public List<BugActions> getActionlist() {
		return actionlist;
	}
	public void setActionlist(List<BugActions> actionlist) {
		this.actionlist = actionlist;
	}
	public List<Users> getOwnerlist() {
		return ownerlist;
	}
	public void setOwnerlist(List<Users> ownerlist) {
		this.ownerlist = ownerlist;
	}
	public boolean isIsmodify() {
		return ismodify;
	}
	public void setIsmodify(boolean ismodify) {
		this.ismodify = ismodify;
	}
}