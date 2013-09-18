package apps.transmanager.weboffice.service.archive;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.databaseobject.ApprovalInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.archive.ArchiveForm;
import apps.transmanager.weboffice.databaseobject.archive.ArchivePermit;
import apps.transmanager.weboffice.databaseobject.archive.ArchiveSecurity;
import apps.transmanager.weboffice.databaseobject.archive.ArchiveType;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.UserService;



/**
 * 处理归档文档
 * 文件注释
 * <p>
 * <p>
 * @author  Administrator
 * @version 1.0
 * @see     
 * @since   web1.0
 */
public class ArchiveUtil
{
	private static ArchiveUtil instance =  new ArchiveUtil();
	public ArchiveUtil()
    {    	
    	instance =  this;
    }
    public static ArchiveUtil instance()
    {
        return instance;
    }
    
    /**
     * 初始化归档数据
     * @param user
     * @return
     */
    public List getArchiveInitData(Users user)
    {
    	List backlist=new ArrayList();
        JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
        UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        String sql = "select a from ArchiveType a order by a.sortNum ";
        List list = jqlService.findAllBySql(sql);
        List<String[]> typelist=new ArrayList<String[]>();
        if (list!=null && list.size()>0)
        {
            for (int i=0;i<list.size();i++)
            {
            	ArchiveType archiveType=(ArchiveType)list.get(i);
            	typelist.add(new String[]{""+archiveType.getId(),archiveType.getName()});
            }
        }
        else
        {
        	typelist.add(new String[]{"0",""});
        }
        backlist.add(typelist);//归档类型
        
        
        sql = "select a from ArchiveSecurity a ";
        List templist = jqlService.findAllBySql(sql);
        List<String[]> securitylist=new ArrayList<String[]>();
        if (templist!=null && templist.size()>0)
        {
            for (int i=0;i<templist.size();i++)
            {
            	ArchiveSecurity archiveSecurity=(ArchiveSecurity)templist.get(i);
            	securitylist.add(new String[]{""+archiveSecurity.getId(),archiveSecurity.getName()});
            }
        }
        else
        {
        	securitylist.add(new String[]{"0",""});
        }
        backlist.add(securitylist);//归档类型
        
        return backlist;
    }
    /**
     * 处理归档文件
     * @param approveIds 当前选中的审批编号
     * @param type  归档类型，暂时全部用字符串
     * @param parentid 类型父编号
     * @param security  密级
     * @param script  说明
     * @param user
     * @return
     */
    public String modifyArchive(List approveIds,String type,Long parentid,String security,String script,Users user)
    {
    	try
    	{
	    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	        UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	        ArchiveType archiveType=null;
	        if (parentid!=null && parentid.longValue()>0L)
	        {
	        	
	        	List list=jqlService.findAllBySql("select a from ArchiveType a where a.parent.id=? and a.name=?", parentid,type);
	        	if (list!=null && list.size()>0)//已存在，直接去ID或对象
	        	{
	        		archiveType=(ArchiveType)list.get(0);
	        	}
	        	else //不存在就要添加类别
	        	{
	        		ArchiveType parenttype = (ArchiveType)jqlService.getEntity(ArchiveType.class, parentid);
	        		archiveType = new ArchiveType();
	        		archiveType.setParent(parenttype);
	        		archiveType.setName(type);
	        		//以后再处理parentkey
	        		jqlService.save(archiveType);
	        	}
	        }
	        else
	        {
	        	List list=jqlService.findAllBySql("select a from ArchiveType a where a.parent=null and a.name=?", type);
	        	if (list!=null && list.size()>0)//已存在，直接去ID或对象
	        	{
	        		archiveType=(ArchiveType)list.get(0);
	        	}
	        	else //不存在就要添加类别
	        	{
	        		archiveType = new ArchiveType();
	        		archiveType.setName(type);
	        		//以后再处理parentkey
	        		jqlService.save(archiveType);
	        		
	        	}
	        }
	        ArchiveSecurity archiveSecurity=null;
	        if (security!=null && security.length()>0)
	        {
	        	String sql = "select a from ArchiveSecurity a where a.name=?";
	            List templist = jqlService.findAllBySql(sql,security);
	            if (templist!=null && templist.size()>0)
	            {
	            	archiveSecurity=(ArchiveSecurity)templist.get(0);
	            }
	        }
	        if (approveIds!=null)
	        {
		        for (int i=0;i<approveIds.size();i++)
		        {
		        	ArchiveForm form=new ArchiveForm();
		        	Long aid=Long.valueOf(""+approveIds.get(i));
		        	List templist=jqlService.findAllBySql("select a from ArchiveForm a where a.approvalinfo.id=?", aid);
		        	boolean isSave=true;
		        	if (templist!=null && templist.size()>0)
		        	{
		        		form=(ArchiveForm)templist.get(0);
		        		isSave=false;
		        	}
		        	ApprovalInfo approvalInfo = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class, aid);
			        form.setType(archiveType);
			        form.setArchivescript(script);
			        form.setSecurity(archiveSecurity);
			        form.setArchiver(user);
			        form.setApprovalinfo(approvalInfo);
			        if (isSave)
			        {
			        	jqlService.save(form);
			        }
			        else
			        {
			        	jqlService.update(form);
			        }
		        }
	        }
	    	return "success";
    	}
    	catch (Exception e)
    	{
    		return "error";
    	}
    }
    
    public Map<String,Object> getArchivePermitData(String approvalId){
    	try {
    		Map<String,Object> result = new HashMap<String,Object>();
    		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	        Long id = Long.parseLong(approvalId);
	        List<ArchivePermit> powerList=jqlService.findAllBySql("select a from ArchivePermit a where a.approvalinfo.id=? ",id);
	        List<Object> list = new ArrayList<Object>();
	        Date endDate = new Date();
	        for (int i = 0; i < powerList.size(); i++) {
	        	Object[] map = new Object[7];
				ArchivePermit power = powerList.get(i);
				Users user = power.getUser();
				map[0] = user.getId();
				map[1] = power.getId();
				map[2] = user.getRealName();
				map[3] = power.getIsReadOnly()==1 ? true : false;
				map[4] = power.getIsDownload()==1 ? true : false;
				map[5] = power.getIsReplace()==1 ? true : false;
				map[6] = power.getIsDelete()==1 ? true : false;
				endDate = power.getEndDate();
				list.add(map);
			}
	        result.put("list",list);
	        result.put("endDate",endDate);
	        return result;
	        
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public boolean setArchivePermitData(String approvalId,String[][] userPermitList,Date enddate){
    	try {
    		JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
	        UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
	        List<ArchivePermit> arpSaveList = new ArrayList<ArchivePermit>();
	        List<Long> needDelList=(List<Long>)jqlService.findAllBySql("select a.id from ArchivePermit a where a.approvalinfo.id=? ",Long.parseLong(approvalId));
	        for (int i = 0; i < userPermitList.length; i++) {
	        	ArchivePermit arp = new ArchivePermit();
				String[] userPermit = userPermitList[i];
				arp.setUser(userService.getUser(Long.parseLong(userPermit[0])));
				arp.setIsReadOnly(userPermit[3].equals("true") ? 1 : 0);
				arp.setIsDownload(userPermit[4].equals("true") ? 1 : 0);
				arp.setIsReplace(userPermit[5].equals("true") ? 1 : 0);
				arp.setIsDelete(userPermit[6].equals("true") ? 1 : 0);
				arp.setStartDate(new Date());
		        arp.setEndDate(enddate);
		        ApprovalInfo app = (ApprovalInfo)jqlService.getEntity(ApprovalInfo.class,Long.parseLong(approvalId));
		        arp.setApprovalinfo(app);
		        if (!(Long.parseLong(userPermit[1])==0)) {
		        	Long permitid = Long.parseLong(userPermit[1]);
					arp.setId(permitid);
					jqlService.update(arp);
					if(needDelList.contains(permitid)){
						needDelList.remove(permitid);
					};
				}else{
					arpSaveList.add(arp);
				}
			}
	        if (needDelList.size()>0) {
	        	jqlService.deleteEntityByID(ArchivePermit.class,"id",needDelList);
			}
	        if(arpSaveList.size()>0){
	        	jqlService.saveAll(arpSaveList);
	        }
    	}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }
	public boolean hasPermit(String approvalId, int type, Long id) {
		try {
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String queryString = "select a from ArchivePermit a where a.approvalinfo.id=? and a.user.id=?";
			List<ArchivePermit> arpList = jqlService.findAllBySql(queryString,Long.parseLong(approvalId),id);
			if ((arpList ==null) || (arpList.size()==0)) {
				return false;
			}
			ArchivePermit arp = arpList.get(0);
			if(arp.getEndDate().before(new Date())){
				return false;				
			}else{
				if (type==1) {
					return arp.getIsReadOnly()==1 ? true : false;
				}else if(type==2){
					return arp.getIsDownload()==1 ? true : false;
				}else if(type==3){
					return arp.getIsReplace()==1 ? true : false;
				}else if(type==4){
					return arp.getIsDelete()==1 ? true : false;
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean isCopyExist(String[] filenames,String newpath)
	{
		try
		{
			JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
			if (filenames!=null && newpath!=null)
			{
				for (int i=0;i<filenames.length;i++)
				{
					if (jcrService.isFileExist(newpath+"/"+filenames[i]))
					{
						return true;
					}
				}
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 复制归档的文件到指定目录
	 * @param oldfiles 原文件绝对路径
	 * @param newpath  目标文件夹
	 * @param copysize 复制份数
	 * @param users 
	 * @return
	 */
	public boolean copyFiles(String[] oldfiles,String newpath,Integer copysize,Users users)
	{
		try
		{
//			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
			if (copysize!=null && copysize.intValue()>0)
			{
				for (int i=0;i<copysize.intValue();i++)
				{
					jcrService.copy(oldfiles, newpath, false);
				}
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
