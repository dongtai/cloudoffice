package apps.recepwork;

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

import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.databaseobject.Reception;
import apps.transmanager.weboffice.databaseobject.ReceptionDefaultUsers;
import apps.transmanager.weboffice.databaseobject.ReceptionImg;
import apps.transmanager.weboffice.databaseobject.ReceptionUsers;
import apps.transmanager.weboffice.databaseobject.Receptionhistory;
import apps.transmanager.weboffice.databaseobject.Receptionmanlist;
import apps.transmanager.weboffice.databaseobject.Receptionpower;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.cache.IMemCache;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.objects.LoginUserInfo;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.both.FlagUtility;

public class ReceptionWork extends AllSupport {

//	private IUserService userService = (IUserService) ApplicationContext.getInstance().getBean("userService");
	private IReceptionService receptionService = (IReceptionService) ApplicationContext.getInstance().getBean("receptionService");
	private Reception reception=new Reception();
	private List<Receptionmanlist> manlist=new ArrayList<Receptionmanlist>();
	private List<ReceptionImg> imgList = new ArrayList<ReceptionImg>();
	private String province;
	private String city;
	private String[] manname;
	private String[] manjob;
	private String[] manphone;
	private String[] manmobile;
	private String[] unitname;

	private Integer powertype;
	private String[] powers;
	private String actiontype;
	private String totalmans;
	private boolean isedit=false;
	

	/* 文件上传 */
	private List<File> upImgs = new ArrayList<File> ();
    private List<String> upImgsFileName = new ArrayList<String> ();
    private List<String> upImgsContentType = new ArrayList<String> ();
 
    /* 图片编辑 */
    private String editImgs;
    private String username;//登录的用户账号
    private String token;//用户登录的token
    private String domain;//系统用的DOMAIN
    private String receptionadd;
    private String receptionmanage;
    private Users user;//当前登录者

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
	 * 检查当前用户是否合法
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
	/**
	 * 获取当前用户的权限
	 */
	private void setPower()
	{
		try
		{
			if (receptionadd==null || receptionmanage==null)
			{
				PermissionService permissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
				Users user = getUser();
	    		long systemA = permissionService.getSystemPermission(user.getId());
	    		
	    		receptionadd=""+FlagUtility.isLongFlag(systemA, ManagementCons.RECEPTION_MANAGE);//获取是否有添加接待的权限
	    		
	    		getRequest().setAttribute("receptionadd",receptionadd);
	    		getRequest().setAttribute("receptionmanage",receptionmanage);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	//根据编号显示接待信息
	public String view() throws Exception
	{
		setPower();
		if (reception.getReceptionid()!=null || getRequest().getSession().getAttribute("receptionid")!=null)
		{
			if (reception.getReceptionid()==null)//刷新页面时需要的
			{
				reception.setReceptionid((Long)getRequest().getSession().getAttribute("receptionid"));
			}
			
		}
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		
		reception=receptionService.findById(reception.getReceptionid());
		isedit=userService.getUser(reception.getUserid()).getUserName().equals(username);
		reception.setIsedit(isedit);
		getRequest().setAttribute("reception", reception);
		Receptionmanlist temp=new Receptionmanlist();
		temp.setReception(reception);
		manlist=receptionService.findByReceptionmanlist(temp,0,500);
		imgList=receptionService.findImgListByReceptionId(reception.getReceptionid());
		getRequest().setAttribute("manlist", manlist);

		System.out.println("flash========================================");

		return VIEW;
	}
	
	//编辑接待
	public String editreception() throws Exception
	{
		setPower();
		view();
		return "edit";
	}
    //保存接待信息
	public String save() throws Exception
    {
		setPower();
//		setReceptionPower();
		Users userinfo=getUserinfo();
		Long userid=userinfo.getId();
		Long[] groupids =(Long[])getRequest().getSession().getAttribute("usergroups");
		//先处理时间,应该不为空,请做验证处理
		if(reception.getComedate()!=null && reception.getLeavedate()!=null)
		{
			SimpleDateFormat slf = new SimpleDateFormat("yyyy-MM-dd");
			String comeDate = slf.format(reception.getComedate())+" "+reception.getComeTime();
			String leaveDate = slf.format(reception.getLeavedate())+" "+reception.getLeaveTime();
			slf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			reception.setComedate(slf.parse(comeDate));
			reception.setLeavedate(slf.parse(leaveDate));
		}

		if (reception.getReceptionid()==null && getRequest().getSession().getAttribute("savereception")==null)
		{
			reception.setAddtime(new Date());
			reception.setUserid(userid);
			if(groupids!=null)
			{
				reception.setGroupid(groupids[0]);
				reception.setRootgroupid(groupids[1]);
			}
	    	receptionService.save(reception);
	    	
	    	//插入历史记录
	    	Receptionhistory history=copyTohistory();
	    	history.setEditdate(new Date());
	    	history.setEditer(userid.intValue());
	    	receptionService.savehistory(history);
	    	//插入图片
	    	saveImg(reception.getReceptionid());
	    	//设置权限
	    	setDefaultPower();
		}
		else
		{
			reception.setAddtime(new Date());
			reception.setUserid(userid);
//			reception.setGroupid(groupids[0]);
//			reception.setRootgroupid(groupids[1]);
			//编辑图片下，图片的处理
			editImg(reception.getReceptionid());
			receptionService.update(reception);
			
			//插入历史记录
			Receptionhistory history=copyTohistory();
			history.setEditdate(new Date());
	    	history.setEditer(userid.intValue());
	    	receptionService.savehistory(history);
	    	
	    	
			//先删除原来的来访人员，再添加
			Receptionmanlist instance=new Receptionmanlist();
			instance.setReception(reception);
			List<Receptionmanlist> templist=receptionService.findByManExample(instance);
			if (templist!=null)
			{
				for (int i=0;i<templist.size();i++)
				{
					receptionService.deletemanlist(templist.get(i));
				}
			}
			//添加来访人员
		}
		//if (getRequest().getSession().getAttribute("savereception")==null)
    	{
    		if (manname!=null)//保存来访人员信息,与接待信息是多对一的关系
	    	{
	    		for (int i=0;i<manname.length;i++)
	    		{
	    			Receptionmanlist tempmanlist=new Receptionmanlist();
	    			if (i==0)
	    			{
	    				tempmanlist.setIsleader(Byte.valueOf("1"));
	    				reception.setLeader(manname[i]);
	    				reception.setJobtype(manjob[i]);
	    				reception.setPhone(manphone[i]);
	    				reception.setMobilenum(manmobile[i]);
	    				receptionService.update(reception);
	    			}
	    			else
	    			{
	    				tempmanlist.setIsleader(Byte.valueOf("0"));
	    			}
	    			tempmanlist.setReception(reception);
	    			tempmanlist.setManname(manname[i]);
	    			tempmanlist.setManjob(manjob[i]);
	    			tempmanlist.setManphone(manphone[i]);
	    			tempmanlist.setManmobile(manmobile[i]);
	    			tempmanlist.setUnitname(unitname[i]);
	    			receptionService.savemanlist(tempmanlist);
	    		}
	    		
	    	}
	    	getRequest().getSession().setAttribute("receptionid", reception.getReceptionid());
	    	getRequest().getSession().setAttribute("savereception", "submit");
    	}
    	return view();
    }
	
	/**
	 * 编辑这次接待的图片信息
	 * @param receptionid 这次接待的ID
	 */
	private void editImg(Long receptionid) {
		//先保存新的图片
		saveImg(receptionid);
		//在删除图片
		if(editImgs!=null && !editImgs.equals(""))
		{
			String[] imgIdArr = editImgs.split(",");
			receptionService.editImgs(ServletActionContext.getServletContext().getRealPath("/"),imgIdArr);
		}
		
	}

	/**
	 * 保存这次接待的图片信息
	 * @param receptionid 接待ID
	 */
    private void saveImg(Long receptionid) {
		if(upImgs!=null && !upImgs.isEmpty())
		{
			String relUrlDir = "receptionImgs/"+reception.getReceptionid();
			String receptionImgPath = ServletActionContext.getServletContext().getRealPath("/")+relUrlDir;
			File testDir = new File(receptionImgPath);
			if(!testDir.exists())
			{
				testDir.mkdirs();
			}
			List<ReceptionImg> imgList = new ArrayList<ReceptionImg>();
			for(int i=0;i<upImgs.size();i++)
			{
				String fileName=new Date().getTime()+upImgsFileName.get(i).substring(upImgsFileName.get(i).lastIndexOf("."));
				String relUrl = relUrlDir+"/"+fileName;
				String name = fileName;
				try {
					File desFile = new File(testDir+"/"+fileName);
					int num =1;
					while(desFile.exists())
					{
						name ="("+num+")"+fileName;
						desFile = new File(testDir+"/"+ name);
						relUrl = relUrlDir+"/"+name;
						num++;
					}
					ReceptionImg recImg = new ReceptionImg();
					recImg.setReceptionId(receptionid);
					recImg.setName(upImgsFileName.get(i));
					recImg.setUrl(relUrl);
					imgList.add(recImg);
					FileUtils.copyFile(upImgs.get(i),desFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			receptionService.saveImgs(imgList);
		}
	}

	//根据条件查询接待信息
    public String searchreception() throws Exception
    {
    	setPower();
    	if ("del".equals(actiontype))
    	{
    		//删除记录
			List<Receptionmanlist> templist=receptionService.findByManProperty("reception.receptionid",reception.getReceptionid());
			if (templist!=null)
			{
				for (int i=0;i<templist.size();i++)
				{
					receptionService.deletemanlist(templist.get(i));//删除接待的人员
				}
			}
			receptionService.deletehistoryByReception(reception.getReceptionid());//删除历史信息
			receptionService.delImgByReception(reception.getReceptionid(),ServletActionContext.getServletContext().getRealPath("/"));//删除上传的图片
			receptionService.deleteUsersByID(reception.getReceptionid());//删除权限用户
			receptionService.deleteByID(reception.getReceptionid());//删除接待信息,如果有必要进行软删除
    	}

       	ExtremeTablePage extremeTablePage =new ExtremeTablePage();
		Limit limit = extremeTablePage.getLimit(getRequest());
//		System.out.println("RowStart====="+limit.getRowStart());
		int viewnums=limit.getCurrentRowsDisplayed();
		if (viewnums==0)
		{
			viewnums=defaultnum;
		}
		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		List<Long> canlist=jqlService.findAllBySql("select a.reception.id from ReceptionUsers as a where a.user.id=? and a.powerid>0", getUser().getId());

		int totalRows=receptionService.findByReceptionsize(reception,getUser(),canlist);
		int[] totals=receptionService.totalReception(reception,getUser(),canlist);
		getRequest().setAttribute("totalmans", String.valueOf(totals[0]));
		totalmans=String.valueOf(totals[0]);
		limit.setRowAttributes(totalRows,viewnums);

		String sort=extremeTablePage.getSort(limit);
		List<Reception> list=receptionService.findByReception(reception,getUser(),canlist, limit.getRowStart(), limit.getCurrentRowsDisplayed(),sort);
		if (list!=null && list.size()>0)
		{
			List<Reception> backlist=new ArrayList<Reception>();
			for (int i=0;i<list.size();i++)
			{
				Reception temp=list.get(i);
				
				temp.setIsdelete(false);
				temp.setIsmanage(false);
				if (temp.getUserid().longValue()==getUser().getId().longValue())
				{
					temp.setIsmanage(true);
					temp.setIsdelete(true);
				}
				List<ReceptionUsers> templist=(List<ReceptionUsers>)jqlService.findAllBySql("select a from ReceptionUsers as a where a.reception.id=? and a.user.id=?", temp.getReceptionid(),getUser().getId());
				if (templist!=null && templist.size()>0)
				{
					ReceptionUsers receptionUsers=templist.get(0);
					temp.setIsdelete(FlagUtility.isIntFlag(receptionUsers.getPowerid(), 3));//1为查看，2为编辑，3为删除
				}
				backlist.add(temp);
			}
			list=backlist;
		}
		getRequest().setAttribute("users", list);
		getRequest().setAttribute("totalRows", new Integer(totalRows));
//		System.out.println(totalRows);
		return "searchlist";
    }
    //查询历史记录
    public String history() throws Exception
	{
    	setPower();
		if (reception.getReceptionid()!=null)
		{
			ExtremeTablePage extremeTablePage =new ExtremeTablePage();
			Limit limit = extremeTablePage.getLimit(getRequest());
			int viewnums=limit.getCurrentRowsDisplayed();
			if (viewnums==0)
			{
				viewnums=defaultnum;
			}
			int totalRows=receptionService.gethistorysize(reception.getReceptionid());
			limit.setRowAttributes(totalRows,viewnums);
			
			String sort=extremeTablePage.getSort(limit);
			List<Receptionhistory> list=receptionService.gethistory(reception.getReceptionid(), limit.getRowStart(), limit.getCurrentRowsDisplayed(),sort);
			
			getRequest().setAttribute("users", list);
			getRequest().setAttribute("totalRows", new Integer(totalRows));
			System.out.println(totalRows);
		}
		return "history";
	}
    //保存权限
    public String power() throws Exception
    {
    	setPower();
    	 if (powertype!=null)
    	 {
    		 receptionService.deletePowerBytype(powertype);//删除所有的权限
    		 getRequest().setAttribute("powertype", String.valueOf(powertype.intValue()));
    	 }
    	 if (powers!=null)
    	 {
    		 //添加选中的权限用户
    		 for (int i=0;i<powers.length;i++)
    		 {
    			 if (powers[i]!=null)
    			 {
	    			 Receptionpower receptionpower = new Receptionpower();
	    			 receptionpower.setTypeid(0);
	    			 receptionpower.setPowernum(powertype);
	    			 receptionpower.setRpuserid(Long.valueOf(powers[i]));
	    			 receptionService.savePower(receptionpower);
    			 }
    		 }
    	 }
    	return "power";
    }
    public String add() throws Exception {
    	try
    	{
    		if (!checkToken())
    		{
    			return "logout";
    		}
    		setPower();
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
    		setPower();
    		System.out.println(getUsername()+"================");
	    	System.out.println(getProvince()+"==========================="+getReception().getCity());
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
        return "searchlist";
    }
	private Receptionhistory copyTohistory()
	{
		Receptionhistory history=new Receptionhistory();
		history.setReception(reception);
		history.setProvince(reception.getProvince());
		history.setCity(reception.getCity());
		history.setUnits(reception.getUnits());
		history.setLeader(reception.getLeader());
		history.setJobtype(reception.getJobtype());
		history.setPhone(reception.getPhone());
		history.setMobilenum(reception.getMobilenum());
		history.setQq(reception.getQq());
		history.setEmail(reception.getEmail());
		history.setMsn(reception.getMsn());
		history.setMans(reception.getMans());
		history.setLunchaddress(reception.getLunchaddress());
		history.setStayhotel(reception.getStayhotel());
		history.setComedate(reception.getComedate());
		history.setLeavedate(reception.getLeavedate());
		history.setStaydays(reception.getStaydays());
		history.setReceiver(reception.getReceiver());
		history.setPlanmoney(reception.getPlanmoney());
		history.setRealmoney(reception.getRealmoney());
		history.setComereason(reception.getComereason());
		history.setDaycontext(reception.getDaycontext());
		history.setVisitspot(reception.getVisitspot());
		history.setUserid(reception.getUserid());
		history.setGroupid(reception.getGroupid());
		history.setRootgroupid(reception.getRootgroupid());
		history.setAddtime(reception.getAddtime());
		history.setDeleted(reception.getDeleted());
		history.setIsdisplay(reception.getIsdisplay());
		history.setRedundanceA(reception.getRedundanceA());
		history.setRedundanceB(reception.getRedundanceB());
		
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
    
    public String blank() throws Exception
    {
    	return "searchlist";
    }
    /**
     * 新建接待信息时，设置默认用户的权限
     */
    private void setDefaultPower()
    {
    	try
    	{
    		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    		List<ReceptionDefaultUsers> defaultlist=(List<ReceptionDefaultUsers>)jqlService.findAllBySql("select a from ReceptionDefaultUsers as a where a.owner.id=?", getUser().getId());
    		if (defaultlist!=null && defaultlist.size()>0)
    		{
    			for (int i=0;i<defaultlist.size();i++)
    			{
    				ReceptionDefaultUsers defaultusers=defaultlist.get(i);
    				ReceptionUsers receptionUsers=new ReceptionUsers();
    				receptionUsers.setReception(reception);
    				receptionUsers.setUser(defaultusers.getDefaultuser());
    				receptionUsers.setPowerid(defaultusers.getDefaultpowerid());
    				receptionUsers.setOwner(getUser());
    				jqlService.save(receptionUsers);
    				
    				//发送手机短信
    			}
    		}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    
    
    
    
    public IReceptionService getReceptionService() {
		return receptionService;
	}
	public void setReceptionService(IReceptionService receptionService) {
		this.receptionService = receptionService;
	}
	public String[] getManname() {
		return manname;
	}
	public void setManname(String[] manname) {
		this.manname = manname;
	}
	public String[] getManjob() {
		return manjob;
	}
	public void setManjob(String[] manjob) {
		this.manjob = manjob;
	}
	public String[] getManphone() {
		return manphone;
	}
	public void setManphone(String[] manphone) {
		this.manphone = manphone;
	}
	public String[] getManmobile() {
		return manmobile;
	}
	public void setManmobile(String[] manmobile) {
		this.manmobile = manmobile;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	
	public Integer getPowertype() {
		return powertype;
	}

	public void setPowertype(Integer powertype) {
		this.powertype = powertype;
	}

	public String[] getPowers() {
		return powers;
	}

	public void setPowers(String[] powers) {
		this.powers = powers;
	}

	public String getActiontype() {
		return actiontype;
	}

	public void setActiontype(String actiontype) {
		this.actiontype = actiontype;
	}
	public String getTotalmans() {
		return totalmans;
	}

	public void setTotalmans(String totalmans) {
		this.totalmans = totalmans;
	}
	
	public String[] getUnitname() {
		return unitname;
	}

	public void setUnitname(String[] unitname) {
		this.unitname = unitname;
	}
	
	public void setReceptionPower(){
		HttpSession session = getRequest().getSession();
		Users userInfo=getUserinfo();
    	String powerstr=(String)session.getAttribute("receptionpower");
    	if (userInfo!=null)
    	{
    		try
    		{
    			powerstr="";
        		IReceptionService receptionService = (IReceptionService) ApplicationContext.getInstance().getBean("receptionService");
        		List<Receptionpower> list=receptionService.getPowerByuserid(userInfo.getId());
        		if (list!=null)
        		{
        			for (int i=0;i<list.size();i++)
        			{
        				powerstr+=","+list.get(i).getPowernum().intValue();
        			}
        			if (powerstr.length()>0)
        			{
        				powerstr=powerstr.substring(1);
        			}
        		}
        		Map<String,Object> groupIdsAndRootCode = receptionService.getRootGroupId(userInfo.getId());
        		Long[] groupids= (Long[]) groupIdsAndRootCode.get("groupIDs");
        		String depRootCode = (String) groupIdsAndRootCode.get("depRootCode");
        		session.setAttribute("usergroups", groupids);
        		session.setAttribute("receptionpower",powerstr);
        		session.setAttribute("depRootCode", depRootCode);
    		}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
	}
	public String getEditImgs() {
		return editImgs;
	}

	public void setEditImgs(String editImgs) {
		this.editImgs = editImgs;
	}

	public List<ReceptionImg> getImgList() {
		return imgList;
	}

	public void setImgList(List<ReceptionImg> imgList) {
		this.imgList = imgList;
	}

	public List<File> getUpImgs() {
		return upImgs;
	}

	public void setUpImgs(List<File> upImgs) {
		this.upImgs = upImgs;
	}



	public List<String> getUpImgsFileName() {
		return upImgsFileName;
	}

	public void setUpImgsFileName(List<String> upImgsFileName) {
		this.upImgsFileName = upImgsFileName;
	}

	public List<String> getUpImgsContentType() {
		return upImgsContentType;
	}

	public void setUpImgsContentType(List<String> upImgsContentType) {
		this.upImgsContentType = upImgsContentType;
	}
	public List<Receptionmanlist> getManlist() {
		return manlist;
	}
	public void setManlist(List<Receptionmanlist> manlist) {
		this.manlist = manlist;
	}
	
    public Reception getReception() {
		return reception;
	}
	public void setReception(Reception reception) {
		this.reception = reception;
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
    
	public String getReceptionadd() {
		return receptionadd;
	}
	public void setReceptionadd(String receptionadd) {
		this.receptionadd = receptionadd;
	}
	public String getReceptionmanage() {
		return receptionmanage;
	}
	public void setReceptionmanage(String receptionmanage) {
		this.receptionmanage = receptionmanage;
	}
	public boolean isIsedit() {
		return isedit;
	}
	public void setIsedit(boolean isedit) {
		this.isedit = isedit;
	}
}