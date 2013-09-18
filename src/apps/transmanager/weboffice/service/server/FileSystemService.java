package apps.transmanager.weboffice.service.server;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.tools.ant.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.moreoffice.ext.share.ShareFileTip;
import apps.moreoffice.ext.sms.model.SmsMessage;
import apps.moreoffice.ext.sms.service.SmsService;
import apps.moreoffice.ext.sms.serviceimpl.SmsServiceForLinux;
import apps.transmanager.weboffice.client.constant.MainConstant;
import apps.transmanager.weboffice.constants.both.ApproveConstants;
import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.both.ManagementCons;
import apps.transmanager.weboffice.constants.both.MessageCons;
import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.both.SpaceConstants;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.Bulletins;
import apps.transmanager.weboffice.databaseobject.EntityMetadata;
import apps.transmanager.weboffice.databaseobject.FileLog;
import apps.transmanager.weboffice.databaseobject.Files;
import apps.transmanager.weboffice.databaseobject.Groupmembershareinfo;
import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.Groupshareinfo;
import apps.transmanager.weboffice.databaseobject.MessageInfo;
import apps.transmanager.weboffice.databaseobject.Messages;
import apps.transmanager.weboffice.databaseobject.NewPersonshareinfo;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Personshareinfo;
import apps.transmanager.weboffice.databaseobject.ReviewFilesInfo;
import apps.transmanager.weboffice.databaseobject.ReviewInfo;
import apps.transmanager.weboffice.databaseobject.Scheduletask;
import apps.transmanager.weboffice.databaseobject.SignInfo;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.SpacesActions;
import apps.transmanager.weboffice.databaseobject.Taginfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersOrganizations;
import apps.transmanager.weboffice.databaseobject.caibian.CollectEdit;
import apps.transmanager.weboffice.databaseobject.caibian.CollectEditSend;
import apps.transmanager.weboffice.domain.ApproveBean;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.domain.Groupmembershareview;
import apps.transmanager.weboffice.domain.UserinfoView;
import apps.transmanager.weboffice.domain.Versioninfo;
import apps.transmanager.weboffice.service.approval.ApprovalUtil;
import apps.transmanager.weboffice.service.approval.SignUtil;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dao.FiletaginfoDAO;
import apps.transmanager.weboffice.service.dao.GroupmembershareinfoDAO;
import apps.transmanager.weboffice.service.dao.GroupmembershareviewDAO;
import apps.transmanager.weboffice.service.dao.GroupshareinfoDAO;
import apps.transmanager.weboffice.service.dao.MessageInfoDAO;
import apps.transmanager.weboffice.service.dao.MessagesDAO;
import apps.transmanager.weboffice.service.dao.MetadataInfoDAO;
import apps.transmanager.weboffice.service.dao.NewPersonshareinfoDAO;
import apps.transmanager.weboffice.service.dao.PermissionDAO;
import apps.transmanager.weboffice.service.dao.PersonshareinfoDAO;
import apps.transmanager.weboffice.service.dao.ReviewinfoDAO;
import apps.transmanager.weboffice.service.dao.ScheduletaskDAO;
import apps.transmanager.weboffice.service.dao.SendCollectDAO;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.service.dao.TaginfoDAO;
import apps.transmanager.weboffice.service.dao.UserinfoViewDAO;
import apps.transmanager.weboffice.service.handler.FilesHandler;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.service.objects.FileArrayComparator;
import apps.transmanager.weboffice.service.objects.FileOperLog;
import apps.transmanager.weboffice.service.objects.PersonshareComparator;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.PasswordEncryptor;
import apps.transmanager.weboffice.util.server.RSACoder;


/**
 * 文件服务类，负责与文件及空间有关的处理。
 * <p>
 */

@Component(value=FileSystemService.NAME)
public class FileSystemService
{
	public static final String NAME = "fileSystemService";
    /**************************以下信息SPING依赖注入*****************************/
	@Autowired
	private StructureDAO structureDAO;
	@Autowired
    private FiletaginfoDAO filetaginfoDAO;	
	@Autowired
    private GroupshareinfoDAO groupshareinfoDAO;
	@Autowired
    private PersonshareinfoDAO personshareinfoDAO;
	@Autowired
    private NewPersonshareinfoDAO newpersonshareinfoDAO;
	@Autowired
    private TaginfoDAO taginfoDAO;
	@Autowired
    private GroupmembershareviewDAO groupmembershareviewDAO;
	@Autowired
    private UserinfoViewDAO userinfoViewDAO;
	@Autowired
    private GroupmembershareinfoDAO groupmembershareinfoDAO;
	@Autowired
    private ScheduletaskDAO scheduletaskDAO;
	@Autowired
	private PermissionDAO permissionDAO;
	@Autowired
	private MessagesDAO messagesDAO;
	@Autowired
	private ReviewinfoDAO reviewinfoDAO;
	@Autowired
	private MessageInfoDAO messageinfoDAO;
	@Autowired
	private MetadataInfoDAO metadatainfoDAO;
	@Autowired
	private SendCollectDAO sendCollectDAO;
	
	public MetadataInfoDAO getMetadatainfoDAO() {
		return metadatainfoDAO;
	}

	public void setMetadatainfoDAO(MetadataInfoDAO metadatainfoDAO) {
		this.metadatainfoDAO = metadatainfoDAO;
	}
    public SendCollectDAO getSendCollectDAO() {
		return sendCollectDAO;
	}

	public void setSendCollectDAO(SendCollectDAO sendCollectDAO) {
		this.sendCollectDAO = sendCollectDAO;
	}

	public ReviewinfoDAO getReviewinfoDAO() {
		return reviewinfoDAO;
	}

	public void setReviewinfoDAO(ReviewinfoDAO reviewinfoDAO) {
		this.reviewinfoDAO = reviewinfoDAO;
	}

	private ArrayList saveNewpersonshareinfoArr;
	private ArrayList savePersonshareinfoArr;
	private ArrayList delNewpersonshareinfoArr;
	private ArrayList delPersonshareinfoArr;
	private ArrayList shareLoginfo;
//	private Object[] delNewObject;
//	private Object[] delpersonObject;
//	private Object[] bulkDelList;
    public FileSystemService()
    {
        System.out.println("FileSystemSerVice被创建");
    }
    
    public MessageInfoDAO getMessageinfoDAO()
	{
		return messageinfoDAO;
	}

	public void setMessageinfoDAO(MessageInfoDAO messageinfoDAO)
	{
		this.messageinfoDAO = messageinfoDAO;
	}
    
    public StructureDAO getStructureDAO()
	{
		return structureDAO;
	}

	public void setStructureDAO(StructureDAO structureDAO)
	{
		this.structureDAO = structureDAO;
	}

	public ScheduletaskDAO getScheduletaskDAO() {
		return scheduletaskDAO;
	}

	public void setScheduletaskDAO(ScheduletaskDAO scheduletaskDAO) {
		this.scheduletaskDAO = scheduletaskDAO;
	}
	

    public FiletaginfoDAO getFiletaginfoDAO()
    {
        return filetaginfoDAO;
    }

    public void setFiletaginfoDAO(FiletaginfoDAO filetaginfoDAO)
    {
        this.filetaginfoDAO = filetaginfoDAO;
    }

    public GroupshareinfoDAO getGroupshareinfoDAO()
    {
        return groupshareinfoDAO;
    }

    public void setGroupshareinfoDAO(GroupshareinfoDAO groupshareinfoDAO)
    {
        this.groupshareinfoDAO = groupshareinfoDAO;
    }

    public PersonshareinfoDAO getPersonshareinfoDAO()
    {
        return personshareinfoDAO;
    }
//    public IPersonshareinfoDAO getPersonshareinfoDAO()
//    {
//        return personshareinfoDAO;
//    }
    public NewPersonshareinfoDAO getNewPersonshareinfoDAO()
    {
    	return newpersonshareinfoDAO;
    }
    
    public void setPersonshareinfoDAO(PersonshareinfoDAO personshareinfoDAO)
    {
        this.personshareinfoDAO = personshareinfoDAO;
    }

    public TaginfoDAO getTaginfoDAO()
    {
        return taginfoDAO;
    }

    public void setTaginfoDAO(TaginfoDAO taginfoDAO)
    {
        this.taginfoDAO = taginfoDAO;
    }


//    public SessionFactory getSessionFactory()
//    {
//        return sessionFactory;
//    }

    public GroupmembershareviewDAO getGroupmembershareviewDAO()
    {
        return groupmembershareviewDAO;
    }

    public void setGroupmembershareviewDAO(GroupmembershareviewDAO groupmembershareviewDAO)
    {
        this.groupmembershareviewDAO = groupmembershareviewDAO;
    }

//    private SessionFactory sessionFactory;
//
//    public void setSessionFactory(SessionFactory sessionFactory)
//    {
//        this.sessionFactory = (SessionFactory)sessionFactory;
//    }
    
	public GroupmembershareinfoDAO getGroupmembershareinfoDAO() {
		return groupmembershareinfoDAO;
	}

	public void setGroupmembershareinfoDAO(
			GroupmembershareinfoDAO groupmembershareinfoDAO) {
		this.groupmembershareinfoDAO = groupmembershareinfoDAO;
	}
	

    /**************************以上信息SPING依赖注入*****************************/

    //		public static void main(String[] args)
    //		{
    //			ApplicationContext.getInstance();
    //		     IFileSystemService se =  (IFileSystemService)ApplicationContext.getInstance().getBean(IFileSystemService.NAME);
    //		     String[] tagPath={"apple","ddd"};
    //		     String[] filePath = {"as"};
    //		     String[] dd = se.getSearchPath("55", tagPath, filePath);
    //		     System.out.println(dd);
    //		     String[] file = {"evermore_emo.com/Document/系统网络Office前端规格说明书_功能规格书.doc"};
    //		     DataHolder dh = new DataHolder();
    //		     dh.setStringData(file);
    //		     List l = se.getSharedUser(null);
    //		     Personshareinfo ps = (Personshareinfo)l.get(0);
    //		     ps.getUserinfoBySharerUserId().getUserName();
    //		     System.out.println(l);
    //		}

    /**
     * 得到文件的组共享信息
     * @param path
     * @return
     */
    private List<Groupshareinfo> getSharedGroup(String path)
    {
        List<Groupshareinfo> list = groupshareinfoDAO.findByProperty(GroupshareinfoDAO.SHAREFILE,
            path);
        return list;
    }

    /**
     * 得到文件的个人共享信息
     * @param path
     * @return
     */
    public List<Personshareinfo> getSharedUser(String path)
    {
        List<Personshareinfo> list = personshareinfoDAO.findByProperty(PersonshareinfoDAO.SHAREFILE, path);
        
        int len = list.size();
      
        for (int i=0;i<len;i++)
        {
        	Personshareinfo info = list.get(i);
        	Users userinfo = info.getUserinfoBySharerUserId();
        	//List<UsersOrganizations> lists = structureDAO.findOrganizationsByUserId(userinfo.getId());
        	List<Organizations> retList = structureDAO.findOrganizationsByUserId(userinfo.getId());
        	/*if (lists != null)
        	{
        		for (UsersOrganizations gi : lists)
        		{
        			retList.add(gi.getOrganization());
        		}
        	}*/
        	//userinfo.setDepartment(retList.get(0).getName());
        	if (retList.size() > 0)
        	{
	        	Organizations companyinfo = structureDAO.findOrganizationsById(retList.get(0).getParentID());
	        	if (companyinfo != null)//有时候抛异常导致对话盒内没有显示共享信息
	            {
	                userinfo.setCompanyName(companyinfo.getName());
	            }
        	}
        	info.setUserinfoBySharerUserId(userinfo);
        }
        return list;
    }
/**
 * 共享文件 ，以前方法稍微改动了下，先暂时这样
 * @param newShareFiles
 * @param delIDs
 * @param modifyArrayList
 * @param comment
 * @param isNeedSendSMS
 * @return
 */
    public ArrayList setFileShareinfo(ArrayList newShareFiles, ArrayList<String> delIDs,
            ArrayList<HashMap> modifyArrayList,String comment, boolean isNeedSendSMS)
        {
        	shareLoginfo=null;
        	long tempuid=0l;
            if (delIDs != null)
            {
                int size = delIDs.size();
                if(size>0)
                {
                	delNewpersonshareinfoArr = new ArrayList();
                	delPersonshareinfoArr = new ArrayList();
                	shareLoginfo= new ArrayList();
                }
                ArrayList<Long> userIds = new ArrayList<Long>();
                long userId = 0L;
                long permit = 0L;
                String sharePath = "";
                for (int i = 0; i < size; i++)
                {
                    String temp = delIDs.get(i);
                    long id = Long.parseLong(temp);
                    	Personshareinfo info = personshareinfoDAO.findById(id);
                    	if(i==0)
                    	{
                    		if (shareLoginfo==null)
                    		{
                    			shareLoginfo= new ArrayList();
                    		}
	                    	shareLoginfo.add(info.getShareFile());
	                    	permit = info.getPermit();
	                    	sharePath = info.getShareFile();
                    	}
                        int isFolder = info.getIsFolder().intValue();
                        Long uid = info.getUserinfoBySharerUserId().getId();
                        tempuid = info.getUserinfoByShareowner().getId();
                        userId = tempuid;
                        boolean hasID = false;
                        for(int s = 0; s < userIds.size(); s++)
                        {
                            if (userIds.get(s) == uid)
                            {
                                hasID = true;
                                break;
                            }
                        }
                        if (!hasID)
                        {
                            userIds.add(uid);
                        }
                        //文件夹
                        if (isFolder == 1)
                        {
                        	try
                        	{
                        		String filePath = info.getShareFile();
                        		
                        		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                        				JCRService.NAME);
                            	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
                            	int len = list.size();
                            	for (int j=0;j<len;j++)
                            	{
                            		String path = list.get(j).getPathInfo();
                            		boolean logflag = false;
                            		if(i==0)
                                	{
                            			logflag = true;
                            			if (shareLoginfo==null)
                                		{
                                			shareLoginfo= new ArrayList();
                                		}
                            			shareLoginfo.add(path);
                                	}
                            		int dx = path.lastIndexOf('/');
                            		String aa = path.substring(dx+1);            		
                            		int dd = aa.lastIndexOf('.');  
                            		
                            		//personshareinfoDAO.delByOwnerAndFile(info.getUserinfoBySharerUserId().getUserId(),path);
                            		delPersonshareinfoArr.add(path);
                            		delNewpersonshareinfoArr.add(path);
//                            		Personshareinfo childInfo = personshareinfoDAO.findByOtherShareAndPath(uid,path);
                            		if (dd == -1)
                                    {
                                    	//NewPersonshareinfo newPersonshareinfo = newpersonshareinfoDAO.findByOtherShareAndPath(info.getUserinfoBySharerUserId().getUserId(),path);
                                    	bulkdelNewPersonShareinfo(uid,path,logflag);
                                    	//delNewPersonShareinfo(newPersonshareinfo);
                                    	//added by zzy
                                    	
                                		//if(childInfo != null)
                                		//{
                                			bulkdelPersonShareinfo(uid,path,logflag);
                                			
                                		//}
                                    	//ended by zzy
                                    }
//                                    else
//                                    {
//                                    	newpersonshareinfoDAO.delByFileAndSharer(path, info.getUserinfoBySharerUserId().getUserId());
//                                    	//added by zzy
//                                    	
//                                		//if(childInfo != null)
//                                		//{
//                                			//delPersonShareinfo(childInfo);
//                                			personshareinfoDAO.delByFileAndSharer(path,uid);
//                                		//}
//                                		//ended by zzy
//                                    }
                            	}
                            	//personshareinfoDAO.delByOwnerAndFile(info.getUserinfoBySharerUserId().getUserId(),filePath);
                        	}
                        	catch(Exception e)
                        	{
                        	}
                        }
                        personshareinfoDAO.deleteByID(id);
                        if(delPersonshareinfoArr!=null && delPersonshareinfoArr.size()>0)
                        {
                        	String[] o = new String[delPersonshareinfoArr.size()];
                        	for(int kkk=0;kkk<delPersonshareinfoArr.size();kkk++)
                        	{
                        		o[kkk]=(String)delPersonshareinfoArr.get(kkk);
                        	}
                        	personshareinfoDAO.bulkUpdateDelByFileAndSharer(o, uid);
                        	delPersonshareinfoArr.clear();
                        	
                        }
                        if(delNewpersonshareinfoArr!=null && delNewpersonshareinfoArr.size()>0)
                        {
                        	String[] o = new String[delNewpersonshareinfoArr.size()];
                        	for(int kkk=0;kkk<delNewpersonshareinfoArr.size();kkk++)
                        	{
                        		o[kkk]=(String)delNewpersonshareinfoArr.get(kkk);
                        	}
                        	newpersonshareinfoDAO.bulkUpdateDelByFileAndSharer(o, uid);
                        	delNewpersonshareinfoArr.clear();
                        	
                        }
       
//                    if (i % 20 == 0)
//                    {
//                        sessionFactory.getCurrentSession().flush();
//                        sessionFactory.getCurrentSession().clear();
//                    }

                }
                if (size > 0)
                {
                    //sendShareMessage(userId, userIds, sharePath, permit, false); 
                }
//                if(shareLoginfo != null && shareLoginfo.size()>0)
//                {
//                	insertFileArrLog(shareLoginfo,tempuid,21);
//                	shareLoginfo=null;
//                }
            }
            if (newShareFiles != null)
            {
            	saveNewpersonshareinfoArr = new ArrayList();
            	savePersonshareinfoArr = new ArrayList();
            	 delNewpersonshareinfoArr = new ArrayList();
            	delPersonshareinfoArr = new ArrayList();
            	
//            	delNewObject = new Object[20];
//            	delpersonObject = new Object[];
//            	bulkDelList = new Object[20];
                int size = newShareFiles.size();
                shareLoginfo = new ArrayList();
                SmsMessage smsMessage = new SmsMessage() ;
                if(isNeedSendSMS && size > 0)
                {
                	
                	//现在没有组共享，暂时不考虑组共享
                	Object temp = newShareFiles.get(0);
                	Personshareinfo info = (Personshareinfo)temp;
                	String shareFile = info.getShareFile();
                	int index1 = shareFile.lastIndexOf("/") + 1;
                	int index2 = shareFile.lastIndexOf(".");
                	String fileName = shareFile.substring(index1, index2);
            		smsMessage.setContent(info.getUserinfoByShareowner().getRealName() + "给您共享了《" + fileName + "》，备注："+ comment + " 请登录政务移动协同办公系统查看。祝您工作愉快！");
                }

                ArrayList<Long> userIds = new ArrayList<Long>();
                String sharePath = "";
                long userId = 0L;
                long permit = 0L;
                List<String> tempList = new ArrayList<String>();
                for (int i = 0; i < size; i++)
                {
                    Object temp = newShareFiles.get(i);
                    if (temp instanceof Personshareinfo)
                    {          
                    	Personshareinfo info = (Personshareinfo)temp;
                    	if(!tempList.contains(info.getShareFile())){
                    		tempList.add(info.getShareFile());
                    	}
                    	// 加入对审阅的判断
                        if (((info.getPermit().intValue() & MainConstant.ISWRITE) != 0)
                            && ((info.getPermit().intValue() & MainConstant.ISAPPROVE) != 0)) {
                        info.setApprove("0");
                    }
                    	if(i ==0)
                    	{
                    	shareLoginfo.add(info.getShareFile());
                    	permit = info.getPermit();
                    	sharePath = info.getShareFile();
                    	}
                    	tempuid = info.getUserinfoByShareowner().getId();
                    	userId = tempuid;
                        Long uid = info.getUserinfoBySharerUserId().getId();
                    	boolean hasID = false;
                        for(int s = 0; s < userIds.size(); s++)
                        {
                            if (userIds.get(s).longValue() == uid.longValue())
                            {
                                hasID = true;
                                break;
                            }
                        }
                        if (!hasID)
                        {
                            userIds.add(uid);
                        }
                    	savePersonshareinfoArr.add(info);//为了批量存盘
                       // personshareinfoDAO.save(info);//为了批量存盘，先去掉
                        //新共享文件夹，需要将文件夹内的所有文件都设置到newpersonshareinfo内
                        int isFolder = info.getIsFolder().intValue();
                        //文件夹
                        if (isFolder == 1)
                        {
                        	try
                        	{
                        		String filePath = info.getShareFile();
                        		sharePath = filePath;
                        		 
                        		 JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                        				 JCRService.NAME);
                            	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
                            	int len = list.size();
                            	for (int j=0;j<len;j++)
                            	{
                            		//saveNewPersonShareinfo(info, list.get(j).getPathInfo());
                            		 String path = list.get(j).getPathInfo();
                            		 boolean logflag = false;
                            		 if(i ==0)
                                 	{
                            			 logflag= true;                                	
                            			 shareLoginfo.add(path);
                                 	}
                            		 /*NewPersonshareinfo newPersonshareinfo = newpersonshareinfoDAO.findByOtherShareAndPath(uid,path);
                                	if(newPersonshareinfo != null)
                                	{
                                		delNewPersonShareinfo(newPersonshareinfo);
                                	}*/
                            		NewPersonshareinfo share = new NewPersonshareinfo();
                                    share.setUserinfoBySharerUserId(info.getUserinfoBySharerUserId());
                                    share.setUserinfoByShareowner(info.getUserinfoByShareowner());
                                    share.setGroupinfoOwner(info.getGroupinfoOwner());
                                    share.setPermit(info.getPermit());
                                    share.setCompanyId(info.getCompanyId());
                                   
                                    share.setShareFile(path);
                                    share.setDate(new Date());
                                    share.setShareComment(info.getShareComment());
                                
                                    int dx = path.lastIndexOf('/');
                            		String aa = path.substring(dx+1);            		
                            		int dd = aa.lastIndexOf('.');    
                            		
                                    if (dd != -1)//无子文件了
                                    {
                                    	share.setIsFolder(0);
//                                    	delNewObject[0] = path;
//                                    	delpersonObject[0] = path;
//                                    	delNewObject[1] = uid;
//                                    	delpersonObject[1] = uid;
                                    	delNewpersonshareinfoArr.add(path);
                                    	delPersonshareinfoArr.add(path);
//                                    	personshareinfoDAO.delByFileAndSharer(path, uid);
//                                    	newpersonshareinfoDAO.delByFileAndSharer(path, uid);
                                     }
                                    else
                                    {
                                    	share.setIsFolder(1);
                                    	bulkdelNewPersonShareinfo(uid,path,logflag);
                                    	 bulkdelPersonShareinfo(uid,path,logflag);
                                      }
                                 
                                    share.setIsNew(1);
                                    saveNewpersonshareinfoArr.add(share);
                                    //saveNewpersonshareinfoDAO(share); 
                                    if(share.getIsFolder() == 1)
                                    {
                                    	
                                    	bulksaveNewPersonShareinfo(share,logflag);
                                    }
                                   
                                  }
                            	 if(delPersonshareinfoArr!=null && delPersonshareinfoArr.size()>0)
                                 {
                                 	String[] o = new String[delPersonshareinfoArr.size()];
                                 	for(int kkk=0;kkk<delPersonshareinfoArr.size();kkk++)
                                 	{
                                 		o[kkk]=(String)delPersonshareinfoArr.get(kkk);
                                 	}
                                 	personshareinfoDAO.bulkUpdateDelByFileAndSharer(o, uid);
                                 	delPersonshareinfoArr.clear();
                                 	
                                 }
                                 if(delNewpersonshareinfoArr!=null && delNewpersonshareinfoArr.size()>0)
                                 {
                                 	String[] o = new String[delNewpersonshareinfoArr.size()];
                                 	for(int kkk=0;kkk<delNewpersonshareinfoArr.size();kkk++)
                                 	{
                                 		o[kkk]=(String)delNewpersonshareinfoArr.get(kkk);
                                 	}
                                 	newpersonshareinfoDAO.bulkUpdateDelByFileAndSharer(o, uid);
                                 	delNewpersonshareinfoArr.clear();
                                 	
                                 }
                            	
                        	}
                        	catch(Exception e)
                        	{
                        	}
                        }
                        if(isNeedSendSMS && size > 0)
                        {
                        	smsMessage.getReceivers().add(info.getUserinfoBySharerUserId().getMobile()) ;
                        }
                    }           
                    else if (temp instanceof Groupshareinfo)
                    {
                    	setShareGroup((Groupshareinfo)temp);
                    }
                }
                if (size > 0)
                {
                	for (String tempPath : tempList) {
                		sendShareMessage(userId, userIds, tempPath, permit, true); 
					} 
                }
                
                if(savePersonshareinfoArr != null && savePersonshareinfoArr.size()>0)
                {
                	personshareinfoDAO.saveAll(savePersonshareinfoArr);
                	savePersonshareinfoArr.clear();
                }
                if(saveNewpersonshareinfoArr != null && saveNewpersonshareinfoArr.size()>0)
                {
                	newpersonshareinfoDAO.saveAll(saveNewpersonshareinfoArr);                    
                	saveNewpersonshareinfoArr.clear();
                }          
                if(isNeedSendSMS && size > 0)
                {
                	SmsService smsService = new SmsServiceForLinux(smsMessage);
                	new Thread(smsService).start();
                }
//                if(shareLoginfo != null && shareLoginfo.size()>0)
//                {
//                	insertFileArrLog(shareLoginfo,tempuid,6);
//                	shareLoginfo=null;
//                }
                
            }

            if (modifyArrayList != null)
            {
                int size = modifyArrayList.size();   
                shareLoginfo = new ArrayList();
    	            for (int i = 0; i < size; i++)
    	            {
    	            	 HashMap temp = modifyArrayList.get(i);
     	                long id = Long.parseLong(temp.get("id").toString());
     	                int permit = Integer.parseInt(temp.get("permit").toString());
    	               // System.out.println("modifyArryList permit:"+permit+",comment::"+shareComment);
    	                    Personshareinfo info = personshareinfoDAO.findById(new Long(id));
    	                    if (((permit & MainConstant.ISWRITE) != 0)
    	                        && ((permit & MainConstant.ISAPPROVE) != 0)) 
    	                    {
    	                    info.setApprove("0");
    	                    }
    	                    if (((permit & MainConstant.ISAPPROVE) == 0))
    	                    {
    	                        info.setApprove(null);
    	                    }
    	                    if(i==0)
    	                    {
    	                    	shareLoginfo.add(info.getShareFile());
    	                    }
    	                    info.setPermit(permit);
    	                    info.setShareComment(comment);
    	                    personshareinfoDAO.save(info);
    	                    int isFolder = info.getIsFolder().intValue();
    	                    Long uid = info.getUserinfoBySharerUserId().getId();
    	                    tempuid = info.getUserinfoByShareowner().getId();
    	                    //文件夹
    	                    if (isFolder == 1)
    	                    {
    	                    	try
    	                    	{
    	                    		String filePath = info.getShareFile();
    	                    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
    	                    				JCRService.NAME);
    	                        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
    	                        	int len = list.size();
    	                        	for (int j=0;j<len;j++)
    	                        	{
    	                        		String path = list.get(j).getPathInfo();
    	                        		boolean logflag = false;
    	                        		if(i ==0)
    	                        		{
    	                        			logflag = true;
    	                        			shareLoginfo.add(path);
    	                        		}
    	                        		int dx = path.lastIndexOf('/');
    	                        		String aa = path.substring(dx+1);            		
    	                        		int dd = aa.lastIndexOf('.');  
    	                        		//System.out.println("uid::"+uid+",path::"+path);
    	                           		NewPersonshareinfo newPersonshareinfo = newpersonshareinfoDAO.findByOtherShareAndPath(uid.longValue(),path);
    	                        		newPersonshareinfo.setShareComment(comment);
    	                        		newPersonshareinfo.setPermit(permit);
    	                        		//System.out.println("modifyNewPersonShareinfo permit::"+permit+",path:::"+path);
    	                        		if (dd == -1)
    	                                {
    	                        			
    	                                	ModifyNewPersonShareinfo(newPersonshareinfo, permit,comment,logflag);
    	                                	
    	                                }
    	                              
    	                        	}
    	                        	
    	                    	}
    	                    	catch(Exception e)
    	                    	{
    	                    	}
    	                    }
    	            }
//    	            if(shareLoginfo != null && shareLoginfo.size()>0)
//    	            {
//    	            	insertFileArrLog(shareLoginfo,tempuid,22);
//    	            	shareLoginfo = null;
//    	            }
              
            }
            return newShareFiles;
        }
    
    public ArrayList setShareinfo(ArrayList newShareFiles, ArrayList<String> delIDs,
        ArrayList<String> modifyArrayList, boolean isNeedSendSMS)
    {
    	shareLoginfo=null;
    	long tempuid=0l;
        if (delIDs != null)
        {
            int size = delIDs.size();
            if(size>0)
            {
            	delNewpersonshareinfoArr = new ArrayList();
            	delPersonshareinfoArr = new ArrayList();
            	shareLoginfo= new ArrayList();
            }
            ArrayList<Long> userIds = new ArrayList<Long>();
            long userId = 0L;
            long permit = 0L;
            String sharePath = "";
            for (int i = 0; i < size; i++)
            {
                String temp = delIDs.get(i);
                int type = Integer.parseInt(temp.substring(0, 1));
                long id = Long.parseLong(temp.substring(1, temp.length()));
                if (type == 0)
                {
                	Personshareinfo info = personshareinfoDAO.findById(id);
                	if(i==0)
                	{
                	shareLoginfo.add(info.getShareFile());
                	permit = info.getPermit();
                	sharePath = info.getShareFile();
                	}
                    int isFolder = info.getIsFolder().intValue();
                    Long uid = info.getUserinfoBySharerUserId().getId();
                    tempuid = info.getUserinfoByShareowner().getId();
                    userId = tempuid;
                    boolean hasID = false;
                    for(int s = 0; s < userIds.size(); s++)
                    {
                        if (userIds.get(s) == uid)
                        {
                            hasID = true;
                            break;
                        }
                    }
                    if (!hasID)
                    {
                        userIds.add(uid);
                    }
                    //文件夹
                    if (isFolder == 1)
                    {
                    	try
                    	{
                    		String filePath = info.getShareFile();
                    		
                    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                    				JCRService.NAME);
                        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
                        	int len = list.size();
                        	for (int j=0;j<len;j++)
                        	{
                        		String path = list.get(j).getPathInfo();
                        		boolean logflag = false;
                        		if(i==0)
                            	{
                        			logflag = true;
                        			shareLoginfo.add(path);
                            	}
                        		int dx = path.lastIndexOf('/');
                        		String aa = path.substring(dx+1);            		
                        		int dd = aa.lastIndexOf('.');  
                        		
                        		//personshareinfoDAO.delByOwnerAndFile(info.getUserinfoBySharerUserId().getUserId(),path);
                        		delPersonshareinfoArr.add(path);
                        		delNewpersonshareinfoArr.add(path);
//                        		Personshareinfo childInfo = personshareinfoDAO.findByOtherShareAndPath(uid,path);
                        		if (dd == -1)
                                {
                                	//NewPersonshareinfo newPersonshareinfo = newpersonshareinfoDAO.findByOtherShareAndPath(info.getUserinfoBySharerUserId().getUserId(),path);
                                	bulkdelNewPersonShareinfo(uid,path,logflag);
                                	//delNewPersonShareinfo(newPersonshareinfo);
                                	//added by zzy
                                	
                            		//if(childInfo != null)
                            		//{
                            			bulkdelPersonShareinfo(uid,path,logflag);
                            			
                            		//}
                                	//ended by zzy
                                }
//                                else
//                                {
//                                	newpersonshareinfoDAO.delByFileAndSharer(path, info.getUserinfoBySharerUserId().getUserId());
//                                	//added by zzy
//                                	
//                            		//if(childInfo != null)
//                            		//{
//                            			//delPersonShareinfo(childInfo);
//                            			personshareinfoDAO.delByFileAndSharer(path,uid);
//                            		//}
//                            		//ended by zzy
//                                }
                        	}
                        	//personshareinfoDAO.delByOwnerAndFile(info.getUserinfoBySharerUserId().getUserId(),filePath);
                    	}
                    	catch(Exception e)
                    	{
                    	}
                    }
                    personshareinfoDAO.deleteByID(id);
                    if(delPersonshareinfoArr!=null && delPersonshareinfoArr.size()>0)
                    {
                    	String[] o = new String[delPersonshareinfoArr.size()];
                    	for(int kkk=0;kkk<delPersonshareinfoArr.size();kkk++)
                    	{
                    		o[kkk]=(String)delPersonshareinfoArr.get(kkk);
                    	}
                    	personshareinfoDAO.bulkUpdateDelByFileAndSharer(o, uid);
                    	delPersonshareinfoArr.clear();
                    	
                    }
                    if(delNewpersonshareinfoArr!=null && delNewpersonshareinfoArr.size()>0)
                    {
                    	String[] o = new String[delNewpersonshareinfoArr.size()];
                    	for(int kkk=0;kkk<delNewpersonshareinfoArr.size();kkk++)
                    	{
                    		o[kkk]=(String)delNewpersonshareinfoArr.get(kkk);
                    	}
                    	newpersonshareinfoDAO.bulkUpdateDelByFileAndSharer(o, uid);
                    	delNewpersonshareinfoArr.clear();
                    	
                    }
                }
                else if (type == 1)
                {
                	delShareGroup(id);
					/*Groupshareinfo gs = groupshareinfoDAO.findById(id);
                    groupmembershareinfoDAO.delByOwnerAndGroupAndPath(gs.getGroupinfo().getGroupId(), 
                    		gs.getUserinfo().getUserId(), gs.getShareFile());
                    groupshareinfoDAO.deleteByID(id);*/
                    //groupmembershareinfoDAO.deleteByID(id);
                }
//                if (i % 20 == 0)
//                {
//                    sessionFactory.getCurrentSession().flush();
//                    sessionFactory.getCurrentSession().clear();
//                }

            }
            if (size > 0)
            {
                //sendShareMessage(userId, userIds, sharePath, permit, false); 
            }
//            if(shareLoginfo != null && shareLoginfo.size()>0)
//            {
//            	insertFileArrLog(shareLoginfo,tempuid,21);
//            	shareLoginfo=null;
//            }
        }
        if (newShareFiles != null)
        {
        	saveNewpersonshareinfoArr = new ArrayList();
        	savePersonshareinfoArr = new ArrayList();
        	 delNewpersonshareinfoArr = new ArrayList();
        	delPersonshareinfoArr = new ArrayList();
        	
//        	delNewObject = new Object[20];
//        	delpersonObject = new Object[];
//        	bulkDelList = new Object[20];
            int size = newShareFiles.size();
            shareLoginfo = new ArrayList();
            SmsMessage smsMessage = new SmsMessage() ;
            if(isNeedSendSMS && size > 0)
            {
            	
            	//现在没有组共享，暂时不考虑组共享
            	Object temp = newShareFiles.get(0);
            	Personshareinfo info = (Personshareinfo)temp;
            	String comment = info.getShareComment();
            	String shareFile = info.getShareFile();
            	int index1 = shareFile.lastIndexOf("/") + 1;
            	int index2 = shareFile.lastIndexOf(".");
            	String fileName = shareFile.substring(index1, index2);
        		smsMessage.setContent(info.getUserinfoByShareowner().getRealName() + "给您共享了《" + fileName + "》，备注："+ comment + " 请登录政务移动协同办公系统查看。祝您工作愉快！");
            }

            ArrayList<Long> userIds = new ArrayList<Long>();
            String sharePath = "";
            long userId = 0L;
            long permit = 0L;
            for (int i = 0; i < size; i++)
            {
                Object temp = newShareFiles.get(i);
                if (temp instanceof Personshareinfo)
                {          
                	Personshareinfo info = (Personshareinfo)temp;
                	// 加入对审阅的判断
                    if (((info.getPermit().intValue() & MainConstant.ISWRITE) != 0)
                        && ((info.getPermit().intValue() & MainConstant.ISAPPROVE) != 0)) {
                    info.setApprove("0");
                }
                	if(i ==0)
                	{
                	shareLoginfo.add(info.getShareFile());
                	permit = info.getPermit();
                	sharePath = info.getShareFile();
                	}
                	tempuid = info.getUserinfoByShareowner().getId();
                	userId = tempuid;
                    Long uid = info.getUserinfoBySharerUserId().getId();
                	boolean hasID = false;
                    for(int s = 0; s < userIds.size(); s++)
                    {
                        if (userIds.get(s) == uid)
                        {
                            hasID = true;
                            break;
                        }
                    }
                    if (!hasID)
                    {
                        userIds.add(uid);
                    }
                	savePersonshareinfoArr.add(info);//为了批量存盘
                   // personshareinfoDAO.save(info);//为了批量存盘，先去掉
                    //新共享文件夹，需要将文件夹内的所有文件都设置到newpersonshareinfo内
                    int isFolder = info.getIsFolder().intValue();
                    //文件夹
                    if (isFolder == 1)
                    {
                    	try
                    	{
                    		String filePath = info.getShareFile();
                    		sharePath = filePath;
                    		 
                    		 JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                    				 JCRService.NAME);
                        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
                        	int len = list.size();
                        	for (int j=0;j<len;j++)
                        	{
                        		//saveNewPersonShareinfo(info, list.get(j).getPathInfo());
                        		 String path = list.get(j).getPathInfo();
                        		 boolean logflag = false;
                        		 if(i ==0)
                             	{
                        			 logflag= true;                                	
                        			 shareLoginfo.add(path);
                             	}
                        		 /*NewPersonshareinfo newPersonshareinfo = newpersonshareinfoDAO.findByOtherShareAndPath(uid,path);
                            	if(newPersonshareinfo != null)
                            	{
                            		delNewPersonShareinfo(newPersonshareinfo);
                            	}*/
                        		NewPersonshareinfo share = new NewPersonshareinfo();
                                share.setUserinfoBySharerUserId(info.getUserinfoBySharerUserId());
                                share.setUserinfoByShareowner(info.getUserinfoByShareowner());
                                share.setGroupinfoOwner(info.getGroupinfoOwner());
                                share.setPermit(info.getPermit());
                                share.setCompanyId(info.getCompanyId());
                               
                                share.setShareFile(path);
                                share.setDate(new Date());
                                share.setShareComment(info.getShareComment());
                            
                                int dx = path.lastIndexOf('/');
                        		String aa = path.substring(dx+1);            		
                        		int dd = aa.lastIndexOf('.');    
                        		
                                if (dd != -1)//无子文件了
                                {
                                	share.setIsFolder(0);
//                                	delNewObject[0] = path;
//                                	delpersonObject[0] = path;
//                                	delNewObject[1] = uid;
//                                	delpersonObject[1] = uid;
                                	delNewpersonshareinfoArr.add(path);
                                	delPersonshareinfoArr.add(path);
//                                	personshareinfoDAO.delByFileAndSharer(path, uid);
//                                	newpersonshareinfoDAO.delByFileAndSharer(path, uid);
                                 }
                                else
                                {
                                	share.setIsFolder(1);
                                	bulkdelNewPersonShareinfo(uid,path,logflag);
                                	 bulkdelPersonShareinfo(uid,path,logflag);
                                  }
                             
                                share.setIsNew(1);
                                saveNewpersonshareinfoArr.add(share);
                                //saveNewpersonshareinfoDAO(share); 
                                if(share.getIsFolder() == 1)
                                {
                                	
                                	bulksaveNewPersonShareinfo(share,logflag);
                                }
                               
                              }
                        	 if(delPersonshareinfoArr!=null && delPersonshareinfoArr.size()>0)
                             {
                             	String[] o = new String[delPersonshareinfoArr.size()];
                             	for(int kkk=0;kkk<delPersonshareinfoArr.size();kkk++)
                             	{
                             		o[kkk]=(String)delPersonshareinfoArr.get(kkk);
                             	}
                             	personshareinfoDAO.bulkUpdateDelByFileAndSharer(o, uid);
                             	delPersonshareinfoArr.clear();
                             	
                             }
                             if(delNewpersonshareinfoArr!=null && delNewpersonshareinfoArr.size()>0)
                             {
                             	String[] o = new String[delNewpersonshareinfoArr.size()];
                             	for(int kkk=0;kkk<delNewpersonshareinfoArr.size();kkk++)
                             	{
                             		o[kkk]=(String)delNewpersonshareinfoArr.get(kkk);
                             	}
                             	newpersonshareinfoDAO.bulkUpdateDelByFileAndSharer(o, uid);
                             	delNewpersonshareinfoArr.clear();
                             	
                             }
                        	
                    	}
                    	catch(Exception e)
                    	{
                    	}
                    }
                   
                    //发送短信
                    //System.out.println("sms ready!*******************");
                    if(isNeedSendSMS && size > 0)
                    {
                    	//System.out.println("system root*******************"+System.getProperty("user.dir"));
                    	//smsMessage.setSender("13911111111") ;
                    	smsMessage.getReceivers().add(info.getUserinfoBySharerUserId().getMobile()) ;
                    	
                    	//System.out.println("Receivers *******************"+info.getUserinfoBySharerUserId().getMobile());
                    }
                }           
                else if (temp instanceof Groupshareinfo)
                {
                	setShareGroup((Groupshareinfo)temp);
                	/*Groupshareinfo gsinfo = (Groupshareinfo) temp;
                	Groupmembershareinfo gminfo = new Groupmembershareinfo();
                	List<UsersOrganizations> l = groupmemberinfoDAO.findByGroupId(gsinfo.getGroupinfo().getGroupId());
                	Iterator<UsersOrganizations> iter = l.iterator();
        			while(iter.hasNext())
        			{
        				UsersOrganizations gmi = iter.next();
        				Userinfo ui = gmi.getUserinfo();
        				gminfo.setUserinfo(ui);
        				gminfo.setShareFile(gsinfo.getShareFile());
        				gminfo.setShareowner(gsinfo.getUserinfo().getUserId());
        				gminfo.setGroupinfo(gsinfo.getGroupinfo());
        				gminfo.setGroupmemberinfo(gmi);
        				if(gminfo.getIsNew() == null)
        				{
        					gminfo.setIsNew(0);
        				}
						groupmembershareinfoDAO.save(gminfo);

        			}
                    groupshareinfoDAO.save((Groupshareinfo)temp);*/
                }
            }
            if (size > 0)
            {
                sendShareMessage(userId, userIds, sharePath, permit, true); 
            }
            
            if(savePersonshareinfoArr != null && savePersonshareinfoArr.size()>0)
            {
            	personshareinfoDAO.saveAll(savePersonshareinfoArr);
            	savePersonshareinfoArr.clear();
            }
            if(saveNewpersonshareinfoArr != null && saveNewpersonshareinfoArr.size()>0)
            {
            	newpersonshareinfoDAO.saveAll(saveNewpersonshareinfoArr);                    
            	saveNewpersonshareinfoArr.clear();
            }          
            if(isNeedSendSMS && size > 0)
            {
            	SmsService smsService = new SmsServiceForLinux(smsMessage);
            	new Thread(smsService).start();
            }
//            if(shareLoginfo != null && shareLoginfo.size()>0)
//            {
//            	insertFileArrLog(shareLoginfo,tempuid,6);
//            	shareLoginfo=null;
//            }
            
        }

        if (modifyArrayList != null)
        {
            int size = modifyArrayList.size();   
            shareLoginfo = new ArrayList();
	            for (int i = 0; i < size; i++)
	            {
	                String temp = modifyArrayList.get(i);
	                int pos = temp.indexOf(",");
	                int type = Integer.parseInt(temp.substring(0, 1));
	                long id = Long.parseLong(temp.substring(1, pos));
	                int commentPos = temp.indexOf(",",pos+1);
	                int permit = Integer.parseInt(temp.substring(pos + 1,commentPos));
	                String shareComment = temp.substring(commentPos+1);
	               // System.out.println("modifyArryList permit:"+permit+",comment::"+shareComment);
	                if (type == 0)
	                {
	                    Personshareinfo info = personshareinfoDAO.findById(new Long(id));
	                    if (((permit & MainConstant.ISWRITE) != 0)
	                        && ((permit & MainConstant.ISAPPROVE) != 0)) 
	                    {
	                    info.setApprove("0");
	                    }
	                    if (((permit & MainConstant.ISAPPROVE) == 0))
	                    {
	                        info.setApprove(null);
	                    }
	                    if(i==0)
	                    {
	                    	shareLoginfo.add(info.getShareFile());
	                    }
	                    info.setPermit(permit);
	                    info.setShareComment(shareComment);
	                    personshareinfoDAO.save(info);
	                    int isFolder = info.getIsFolder().intValue();
	                    Long uid = info.getUserinfoBySharerUserId().getId();
	                    tempuid = info.getUserinfoByShareowner().getId();
	                    //文件夹
	                    if (isFolder == 1)
	                    {
	                    	try
	                    	{
	                    		String filePath = info.getShareFile();
	                    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
	                    				JCRService.NAME);
	                        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
	                        	int len = list.size();
	                        	for (int j=0;j<len;j++)
	                        	{
	                        		String path = list.get(j).getPathInfo();
	                        		boolean logflag = false;
	                        		if(i ==0)
	                        		{
	                        			logflag = true;
	                        			shareLoginfo.add(path);
	                        		}
	                        		int dx = path.lastIndexOf('/');
	                        		String aa = path.substring(dx+1);            		
	                        		int dd = aa.lastIndexOf('.');  
	                        		//System.out.println("uid::"+uid+",path::"+path);
	                           		NewPersonshareinfo newPersonshareinfo = newpersonshareinfoDAO.findByOtherShareAndPath(uid.longValue(),path);
	                        		newPersonshareinfo.setShareComment(shareComment);
	                        		newPersonshareinfo.setPermit(permit);
	                        		//System.out.println("modifyNewPersonShareinfo permit::"+permit+",path:::"+path);
	                        		if (dd == -1)
	                                {
	                        			
	                                	ModifyNewPersonShareinfo(newPersonshareinfo, permit,shareComment,logflag);
	                                	
	                                }
	                              
	                        	}
	                        	
	                    	}
	                    	catch(Exception e)
	                    	{
	                    	}
	                    }
	                    
	                }
	                else if (type == 1)
	                {
	                	changeShareGroup(id, permit);
	                    /*Groupshareinfo gs = groupshareinfoDAO.findById(id);
	                    gs.setPermit(permit);
						List<Groupmembershareinfo> l = groupmembershareinfoDAO.findByOwnerAndGroupAndPath(gs.getGroupinfo().getGroupId(),
	                    		gs.getUserinfo().getUserId(),gs.getShareFile());
	                    Iterator<Groupmembershareinfo> it = l.iterator();
	                    while (it.hasNext())
	                    {
	                    	it.next().setIsNew(0);
	                    }*/
	                }
	            }
//	            if(shareLoginfo != null && shareLoginfo.size()>0)
//	            {
//	            	insertFileArrLog(shareLoginfo,tempuid,22);
//	            	shareLoginfo = null;
//	            }
          
        }
        return newShareFiles;
    }
    public void savePersonshareinfo(Personshareinfo pershareinfo)
    {
    	if(pershareinfo != null)
        {
    		if (pershareinfo.getPersonShareId()==null)
    		{
    			personshareinfoDAO.save(pershareinfo);
    		}
    		else
    		{
    			personshareinfoDAO.update(pershareinfo);
    		}
        }
    	
    }
    public void savePersonshareinfos(List<Personshareinfo> list)
    {
    	if(list != null && list.size()>0)
        {
        	personshareinfoDAO.saveAll(list);
        	list.clear();
        }
    }
    /*
     * 信电局发送审阅文件的提示消息
     */
    private void sendReviewMessage(long userId, List addList, String sharePath, String fileName)
    {
    	MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
        UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        /* 发送消息 */
        for (int i = 0;i < addList.size();i++) {
    		String[] info = ((String)addList.get(i)).split("-");
    		Long reviewer = Long.valueOf(info[0]);
    		long permit = Integer.valueOf(info[1]);
        	Messages message = new Messages();
            Users sendUser = userService.getUser(userId);
            message.setAttach(sharePath);
            message.setDate(new Date());// 日期
            message.setUser(sendUser);// 消息发送者
            message.setType(MessageCons.AUDIT);// 审阅类型
            boolean isFolder = false;
            if(fileName.length() <= 0)
            {
            	if (sharePath != null && sharePath.lastIndexOf("/") >= 0 && sharePath.lastIndexOf(".") >= 0)
                {
                    int index1 = sharePath.lastIndexOf("/") + 1;
                    int index2 = sharePath.length();
                    fileName = sharePath.substring(index1, index2);
                }
                else if (sharePath != null && sharePath.lastIndexOf("/") >= 0)
                {
                    int index1 = sharePath.lastIndexOf("/") + 1;
                    fileName = sharePath.substring(index1, sharePath.length());
                    isFolder = true;
                }
            }            
            SimpleDateFormat spm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String str = "";
    		str = "<font color='#0000FF'>"+sendUser.getRealName() + "送审了文件'"+(isFolder ? "夹-<b>" : "-<b>") +fileName+"'</b>给您," +spm.format(message.getDate())+"</font>";
            message.setContent(str);// 内容
            message.setSize(0L);
            message.setPermit(permit);
        	message.setTitle( "您有一个"+ (isFolder ? "夹" : "")+"待审任务");// 标题
            ArrayList<Long> userIdList = new ArrayList<Long>();
            userIdList.add(reviewer);
            messageService.sendMessage(message, userId, userIdList);
		}
    }
    private void sendShareMessage(long userId, ArrayList<Long>userIds, String sharePath, long permit, boolean isshare)
    {
        MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
        UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        /* 发送消息 */
        for (Long userLong : userIds) {
        	Messages message = new Messages();
            Users sendUser = userService.getUser(userId);
            message.setAttach(sharePath);
            message.setDate(new Date());// 日期
            message.setUser(sendUser);// 消息发送者
            message.setType(MessageCons.SHARE_TYPE);// 类型
            boolean isFolder = false;
            String fileName = "";
        	if (sharePath != null && sharePath.lastIndexOf("/") >= 0 && sharePath.lastIndexOf(".") >= 0)
            {
                int index1 = sharePath.lastIndexOf("/") + 1;
                int index2 = sharePath.length();
                fileName = sharePath.substring(index1, index2);
            }
            else if (sharePath != null && sharePath.lastIndexOf("/") >= 0)
            {
                int index1 = sharePath.lastIndexOf("/") + 1;
                fileName = sharePath.substring(index1, sharePath.length());
                isFolder = true;
            }                       
            SimpleDateFormat spm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String str = "";
            if (isshare)
            {
            	if((permit & MainConstant.ISAPPROVE) !=0)
                {
            		str = "<font color='#0000FF'>"+sendUser.getRealName() + "送审了文件'"+(isFolder ? "夹-<b>" : "-<b>") +fileName+"'</b>给您," +spm.format(message.getDate())+"</font>";
                }
            	else
            	{
            		str = "<font color='#0000FF'>"+sendUser.getRealName() + "共享了文件"+(isFolder ? "夹-<b>" : "-<b>") +fileName+"</b>给您," +spm.format(message.getDate())+"</font>";
            	}
            }
            message.setContent(str);// 内容
            message.setSize(0L);
            message.setPermit(permit);
            if((permit & MainConstant.ISAPPROVE) !=0)
            {
            	message.setTitle( "您有一个"+ (isFolder ? "夹" : "")+"待审任务");// 标题
            }
            else
            {
            	message.setTitle("您有一个文件"+ (isFolder ? "夹" : "")+"共享的信息");// 标题
            }
            ArrayList<Long> userIdList = new ArrayList<Long>();
            userIdList.add(userLong);
            messageService.sendMessage(message, userId, userIdList);
		}
        
    }
    /*private void saveNewPersonShareinfo(Personshareinfo info,String path)
    {
    	 NewPersonshareinfo share = new NewPersonshareinfo();
         share.setUserinfoBySharerUserId(info.getUserinfoBySharerUserId());
         share.setUserinfoByShareowner(info.getUserinfoByShareowner());
         share.setGroupinfoOwner(info.getGroupinfoOwner());
         share.setPermit(info.getPermit());
         share.setCompanyId(info.getCompanyId());
         share.setShareFile(path);
         share.setDate(new Date());
         share.setIsFolder(0);
         share.setIsNew(1);
         saveNewpersonshareinfoDAO(share);     	 	
    }*/
    
    /*
     * 新建NewPersonShareinfo，需要递归
     */
    private void saveNewPersonShareinfo(NewPersonshareinfo info)
    {
    	try
    	{
    		
			String filePath = info.getShareFile();
			String sharecomment1 = info.getShareComment();
			JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
					JCRService.NAME);
        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
        	int len = list.size();
        	for (int j=0;j<len;j++)
        	{
        		//saveNewPersonShareinfo(info, list.get(j).getPathInfo());
        		NewPersonshareinfo share = new NewPersonshareinfo();
                share.setUserinfoBySharerUserId(info.getUserinfoBySharerUserId());
                share.setUserinfoByShareowner(info.getUserinfoByShareowner());
                share.setGroupinfoOwner(info.getGroupinfoOwner());
                share.setPermit(info.getPermit());
                share.setCompanyId(info.getCompanyId());
                String path = list.get(j).getPathInfo();
                shareLoginfo.add(path);//为日志而添加
                share.setShareFile(list.get(j).getPathInfo());
                share.setDate(new Date());
                share.setIsNew(1);
                share.setShareComment(sharecomment1);
                int dx = path.lastIndexOf('/');
        		String aa = path.substring(dx+1);            		
        		int dd = aa.lastIndexOf('.');            		
                if (dd != -1)
                {
                	share.setIsFolder(0);
                }
                else
                {
                	share.setIsFolder(1);
                }
//                saveNewpersonshareinfoArr.add(share);
                saveNewpersonshareinfoDAO(share); 
                if(share.getIsFolder() == 1)
                {
                	saveNewPersonShareinfo(share);
                }
        	}
//        	/*为日志而添加*/
//        	if(loglist != null && loglist.size()>0)
//        	{
//        		String[] logfile = new String[loglist.size()];
//        		for(int i=0;i<loglist.size();i++)
//        		{
//        			logfile[i]=(String)loglist.get(i);
//        		}
//        		long uid = info.getUserinfoByShareowner().getUserId();
//        		insertFileListLog(logfile,uid,6);
//        	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	    	 	
    }
    /*
     * 新建NewPersonShareinfo，需要递归
     */
    private void bulksaveNewPersonShareinfo(NewPersonshareinfo info,boolean logflag)
    {
//    	ArrayList loglist = null;
    	try
    	{
//    		loglist = new ArrayList();
			String filePath = info.getShareFile();
			String sharecomment1 = info.getShareComment();
			JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
					JCRService.NAME);
        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
        	int len = list.size();
        	for (int j=0;j<len;j++)
        	{
        		//saveNewPersonShareinfo(info, list.get(j).getPathInfo());
        		NewPersonshareinfo share = new NewPersonshareinfo();
                share.setUserinfoBySharerUserId(info.getUserinfoBySharerUserId());
                share.setUserinfoByShareowner(info.getUserinfoByShareowner());
                share.setGroupinfoOwner(info.getGroupinfoOwner());
                share.setPermit(info.getPermit());
                share.setCompanyId(info.getCompanyId());
                String path = list.get(j).getPathInfo();
                if(logflag)
                {
                	shareLoginfo.add(path);
                }
                share.setShareFile(list.get(j).getPathInfo());
                share.setDate(new Date());
                share.setIsNew(1);
                share.setShareComment(sharecomment1);
                int dx = path.lastIndexOf('/');
        		String aa = path.substring(dx+1);            		
        		int dd = aa.lastIndexOf('.');            		
                if (dd != -1)
                {
                	share.setIsFolder(0);
                }
                else
                {
                	share.setIsFolder(1);
                }
                saveNewpersonshareinfoArr.add(share);
                //saveNewpersonshareinfoDAO(share); 
                if(share.getIsFolder() == 1)
                {
                	bulksaveNewPersonShareinfo(share,logflag);
                }
//                if(loglist != null && loglist.size()>0)
//            	{
//            		String[] logfile = new String[loglist.size()];
//            		for(int i=0;i<loglist.size();i++)
//            		{
//            			logfile[i]=(String)loglist.get(i);
//            		}
//            		long uid = info.getUserinfoByShareowner().getUserId();
//            		insertFileListLog(logfile,uid,6);
//            	}
        	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	    	 	
    }
    /*
     * 删除PersonShareinfo，需要递归
     */
    private void bulkdelPersonShareinfo(Long uid,String filePath,boolean logflag)
    {
    	
    	try
    	{
    		//String filePath = info.getShareFile();
    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
    				JCRService.NAME);
        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
        	int len = list.size();
        	for (int j=0;j<len;j++)
        	{
        		String path = list.get(j).getPathInfo();
        		if(logflag)
        		{
        		shareLoginfo.add(path);//为日志而增加
        		}
        		int dx = path.lastIndexOf('/');
        		String aa = path.substring(dx+1);            		
        		int dd = aa.lastIndexOf('.');   
        		if (dd == -1)
                {
                	Personshareinfo personshareinfo = personshareinfoDAO.findByOtherShareAndPath(uid,path);//findByShareOwnerAndPath(info.getUserinfoByShareowner().getUserId(), path);//
                	if(personshareinfo !=null)
                	{
                		bulkdelPersonShareinfo(uid,path,logflag);
                	}
                }
                else
                {

                	delPersonshareinfoArr.add(path);
//                	personshareinfoDAO.delByFileAndSharer(path, uid.longValue());//delByOwnerAndFile(info.getUserinfoByShareowner().getUserId(), path);//delByFileAndSharer(path, 
                }
        	}

        	delPersonshareinfoArr.add(filePath);
//        	personshareinfoDAO.delByFileAndSharer(filePath, uid.longValue());//delByFileAndSharer(path, info.getUserinfoBySharerUserId().getUserId());filePath, info.getUserinfoBySharerUserId().getUserId());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	
    	    	 	
    }
    /*
     * 删除PersonShareinfo，需要递归
     */
    private void delPersonShareinfo(Long uid,String filePath)
    {
    	
    	try
    	{
    		//String filePath = info.getShareFile();
    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
    				JCRService.NAME);
        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
        	int len = list.size();
        	for (int j=0;j<len;j++)
        	{
        		String path = list.get(j).getPathInfo();
        		shareLoginfo.add(path);//为日志而增加
        		int dx = path.lastIndexOf('/');
        		String aa = path.substring(dx+1);            		
        		int dd = aa.lastIndexOf('.');   
        		if (dd == -1)
                {
                	Personshareinfo personshareinfo = personshareinfoDAO.findByOtherShareAndPath(uid,path);//findByShareOwnerAndPath(info.getUserinfoByShareowner().getUserId(), path);//
                	if(personshareinfo !=null)
                	{
                		delPersonShareinfo(uid,path);
                	}
                }
                else
                {

                	personshareinfoDAO.delByFileAndSharer(path, uid.longValue());//delByOwnerAndFile(info.getUserinfoByShareowner().getUserId(), path);//delByFileAndSharer(path, 
                }
        	}

        	personshareinfoDAO.delByFileAndSharer(filePath, uid.longValue());//delByFileAndSharer(path, info.getUserinfoBySharerUserId().getUserId());filePath, info.getUserinfoBySharerUserId().getUserId());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	
    	    	 	
    }
    /*
     * 删除NewPersonShareinfo，需要递归
     */
    private void bulkdelNewPersonShareinfo(Long uid,String filePath,boolean logflag)//NewPersonshareinfo info)
    {
    	try
    	{
    		//String filePath = info.getShareFile();
    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
    				JCRService.NAME);
        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
        	int len = list.size();
        	for (int j=0;j<len;j++)
        	{
        		String path = list.get(j).getPathInfo();
        		if(logflag)
        		{
        		shareLoginfo.add(path);//为日志而增加
        		}
        		int dx = path.lastIndexOf('/');
        		String aa = path.substring(dx+1);            		
        		int dd = aa.lastIndexOf('.');  
        		if (dd == -1)
                {
        			bulkdelNewPersonShareinfo(uid,path,logflag);
                }
                else
                {

                	delNewpersonshareinfoArr.add(path);
//                
//                	newpersonshareinfoDAO.delByFileAndSharer(path,uid.longValue());
                }
        	}

        	delNewpersonshareinfoArr.add(filePath);

//        	newpersonshareinfoDAO.delByFileAndSharer(filePath,uid.longValue());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	    	 	
    }
    /*
     * 删除NewPersonShareinfo，需要递归
     */
    private void delNewPersonShareinfo(Long uid,String filePath)//NewPersonshareinfo info)
    {
    	try
    	{
    		//String filePath = info.getShareFile();
    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
    				JCRService.NAME);
        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
        	int len = list.size();
        	for (int j=0;j<len;j++)
        	{
        		String path = list.get(j).getPathInfo();
        		shareLoginfo.add(path);//为日志而增加
        		int dx = path.lastIndexOf('/');
        		String aa = path.substring(dx+1);            		
        		int dd = aa.lastIndexOf('.');  
        		if (dd == -1)
                {
                	delNewPersonShareinfo(uid,path);
                }
                else
                {
//                	
                	newpersonshareinfoDAO.delByFileAndSharer(path,uid.longValue());
                }
        	}

        	newpersonshareinfoDAO.delByFileAndSharer(filePath,uid.longValue());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	    	 	
    }
    /**
     * 删除NewPersonShareinfo，需要递归，该方法在删除文件夹时调用
     * @param path
     */
    public void delNewPersonShareinfoByPath(String filePath, String companyID)
    {
    	try
    	{
    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
    				JCRService.NAME);
        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
        	int len = list.size();
        	for (int j=0;j<len;j++)
        	{
        		String path = list.get(j).getPathInfo();
        		if (shareLoginfo==null)
        		{
        			shareLoginfo=new ArrayList();
        		}
        		shareLoginfo.add(path);//为日志而增加
        		int dx = path.lastIndexOf('/');
        		String aa = path.substring(dx+1);            		
        		int dd = aa.lastIndexOf('.');            		
        		newpersonshareinfoDAO.delByLikeFile(path, companyID);
                if (dd == -1)
                {
                	delNewPersonShareinfoByPath(path, companyID);
                }
        	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    }
    /**
     * 删除PersonShareinfo，需要递归，该方法在删除文件夹时调用
     * @param path
     */
    public void delPersonShareinfoByPath(String filePath, long userID)
    {
    	try
    	{
    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
    				JCRService.NAME);
    		if (jcrService.isFoldExist(filePath))
    		{
	        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
	        	int len = list.size();
	        	for (int j=0;j<len;j++)
	        	{
	        		String path = list.get(j).getPathInfo();
	        		if (shareLoginfo==null)
	        		{
	        			shareLoginfo=new ArrayList();
	        		}
	        		shareLoginfo.add(path);//为日志而增加
	        		int dx = path.lastIndexOf('/');
	        		String aa = path.substring(dx+1);            		
	        		int dd = aa.lastIndexOf('.');   
	//        		delPersonshareinfo.add(path);
	        		personshareinfoDAO.delByOwnerAndFile(userID, path);
	                if (dd == -1)
	                {
	                	delPersonShareinfoByPath(path, userID);
	                }
	        	}
    		}
    		else
    		{
    			personshareinfoDAO.delByOwnerAndFile(userID, filePath);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    }
    
    public void delAllPersonShareinfoByPath(String filePath)
    {
    	try
    	{
    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
    				JCRService.NAME);
    		if (jcrService.isFoldExist(filePath))
    		{
	        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
	        	int len = list.size();
	        	for (int j=0;j<len;j++)
	        	{
	        		String path = list.get(j).getPathInfo();
	        		shareLoginfo.add(path);//为日志而增加
	        		int dx = path.lastIndexOf('/');
	        		String aa = path.substring(dx+1);            		
	        		int dd = aa.lastIndexOf('.');   
	        		personshareinfoDAO.delByFile(path);
	        	}
    		}
    		else
    		{
    			personshareinfoDAO.delByFile(filePath);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    }
    /*
     * 修改NewPersonShareinfo，需要递归
     */
    private void ModifyNewPersonShareinfo(NewPersonshareinfo info, int permit,String shareComment,boolean logflag)
    {
//    	ArrayList loglist = null;
    	try
    	{
//    		loglist = new ArrayList();
    		String filePath = info.getShareFile();
    		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
    				JCRService.NAME);
        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
        	int len = list.size();
        	for (int j=0;j<len;j++)
        	{
        		String path = list.get(j).getPathInfo();
        		if(logflag)//为日志而增加
        		{
        		shareLoginfo.add(path);
        		}
        		int dx = path.lastIndexOf('/');
        		String aa = path.substring(dx+1);            		
        		int dd = aa.lastIndexOf('.');            		
        		NewPersonshareinfo newPersonshareinfo = newpersonshareinfoDAO.findByOtherShareAndPath((info.getUserinfoBySharerUserId().getId()).longValue(),path);
        		//System.out.println("newPersonshareinfo permit:::"+path+",permit:::"+permit);
        		newPersonshareinfo.setPermit(permit);
            	newPersonshareinfo.setShareComment(shareComment);
        		if (dd == -1)
                {
                	ModifyNewPersonShareinfo(newPersonshareinfo, permit,shareComment,logflag);
                }
               /* else
                {
                	newPersonshareinfo.setPermit(permit);
                	newPersonshareinfo.setShareComment(shareComment);
                }*/
        	}
//        	if(loglist != null && loglist.size()>0)
//        	{
//        		String[] logfile = new String[loglist.size()];
//        		for(int i=0;i<loglist.size();i++)
//        		{
//        			logfile[i]=(String)loglist.get(i);
//        		}
//        		long uid = info.getUserinfoByShareowner().getUserId();
//        		insertFileListLog(logfile,uid,22);
//        	}
        	 
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    	    	 	
    }
    
    public void setShareGroup(Groupshareinfo temp)
    {
    	Groupshareinfo gsinfo = temp;
    	Groupmembershareinfo gminfo = new Groupmembershareinfo();
    	long groupId = gsinfo.getGroupinfo().getId();
    	List<UsersOrganizations> l = structureDAO.findUsersOrganizationsByOrgId(groupId);
    	Iterator<UsersOrganizations> iter = l.iterator();
    	Users info = gsinfo.getUserinfo();
    	if (info != null)
    	{
			while(iter.hasNext())
			{
					UsersOrganizations gmi = iter.next();
					Users ui = gmi.getUser();
					gminfo.setUserinfo(ui);
					gminfo.setShareFile(gsinfo.getShareFile());
					gminfo.setShareowner(info.getId());
					gminfo.setGroupinfo(gsinfo.getGroupinfo());
					gminfo.setGroupmemberinfo(gmi);
					if(gminfo.getIsNew() == null)
					{
						gminfo.setIsNew(0);
					}
					groupmembershareinfoDAO.save(gminfo);
			}
    	}
		groupshareinfoDAO.save(temp);
		setShareChildGroup(temp, groupId);
    }
    
    private void setShareChildGroup(Groupshareinfo parentGroup, long parentId)
    {
		List<Organizations> child = structureDAO.getChildOrganizations(parentId <= 0 ? null : parentId);
		if (child != null)
		{
			Users info = parentGroup.getUserinfo();
			if (info != null)
			{
				for (Organizations temp : child)
				{
						Groupshareinfo share = new Groupshareinfo();
						share.setGroupinfo(temp);
		                share.setUserinfo(parentGroup.getUserinfo());
		                share.setPermit(parentGroup.getPermit());
		                share.setCompanyId(parentGroup.getCompanyId());
		                share.setShareFile(parentGroup.getShareFile());
		                setShareGroup(share);
				}
			}
		}
		
    }
    
    private void delShareGroup(long groupshareId)
    {
    	Groupshareinfo gs = groupshareinfoDAO.findById(groupshareId);
    	Organizations groupinfo = gs.getGroupinfo();
//        groupmembershareinfoDAO.delByOwnerAndGroupAndPath(groupinfo.getGroupId(), 
//        		gs.getUserinfo().getUserId(), gs.getShareFile());
        groupshareinfoDAO.deleteByID(groupshareId);
        delShareChildGroup(groupinfo.getParentID());
    }
    
    private void delShareChildGroup(long parentId)
    {
    	List<Groupshareinfo> gs = groupshareinfoDAO.findByGroupId(parentId);
    	if (gs != null)
    	{
    		for (Groupshareinfo temp : gs)
    		{
    			delShareGroup(temp.getFileShareId());
    		}
    	}
    }
    
    private void changeShareGroup(long groupshareId, int permit)
    {
    	Groupshareinfo gs = groupshareinfoDAO.findById(groupshareId);
        gs.setPermit(permit);
        Organizations groupinfo = gs.getGroupinfo();
        if (gs.getUserinfo() != null)
        {
			List<Groupmembershareinfo> l = groupmembershareinfoDAO.findByOwnerAndGroupAndPath(gs.getGroupinfo().getId(),
	        		gs.getUserinfo().getId(),gs.getShareFile());
	        Iterator<Groupmembershareinfo> it = l.iterator();
	        while (it.hasNext())
	        {
	        	it.next().setIsNew(0);
	        }
        }
        changeShareChildGroup(groupinfo.getParentID(), permit);
    }
    
    private void changeShareChildGroup(long parentId, int permit)
    {
    	List<Groupshareinfo> gs = groupshareinfoDAO.findByGroupId(parentId);
    	if (gs != null)
    	{
    		for (Groupshareinfo temp : gs)
    		{
    			changeShareGroup(temp.getFileShareId(), permit);
    		}
    	}
    }
    
    
    /**
     * 得到的单一文件的共享信息
     * @param path
     * @return
     */
    public List getshareInfo(String path)
    {
        List l = new ArrayList();
        l.addAll(getSharedGroup(path));
        l.addAll(getSharedUser(path));
        return l;
    }

    public List getMutishareInfo(DataHolder pathHolder)
    {
        String[] path = pathHolder.getStringData();
        ArrayList l = new ArrayList();
        for (int i = 0; i < path.length; i++)
        {
            l.addAll(getshareInfo(path[i]));
        }
        return l;
        //    	return getSharedUser(path[0]);
    }

    public int getShareInfoCount(String path)
    {
        List l = new ArrayList();
        l.addAll(getSharedGroup(path));
        l.addAll(getSharedUser(path));
        return l.size();
    }

    public List<Fileinfo> getMyShare(String loginMail, long creatorID, String companyID, int start,
        int limit, String sort, String dir)
    {
    	long time=System.currentTimeMillis();
        String[] mySharePath = getMyShareFPath(creatorID, companyID);
        if (limit < 0)
        {
            limit = mySharePath.length - start;
        }
        
        ArrayList filePath = new ArrayList();
        /*for (int i = start; i < limit + start; i++)
        {
            if (i >= mySharePath.length)
            {
                break;
            }
            filePath.add(mySharePath[i]);

        }*/
        if (mySharePath.length > 0)//filePath != null && filePath.size() > 0)
        { 
        	String path = mySharePath[0].substring(0,mySharePath[0].indexOf('/'));
        	//System.out.println("fileSystemService path==="+path);
        	ShareFileTip queryTip=new ShareFileTip();
        	ArrayList loglist = queryTip.queryLogLikeDir(path);
        	List<Personshareinfo> psharelist = getAllShareinfo(path);//personshareinfodao.likeSerch("shareFile",path);
        	List<NewPersonshareinfo> newpsharelist = getAllNewShareinfo(path);//newpersonshareinfodao.likeSerch("shareFile",path);
        	int loglen=0;
        	if(loglist!=null)
        	{
        		loglen=loglist.size();
        	}
            try
            {
            	 JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
            			JCRService.NAME);
                ArrayList listr = jcrService.getFileinfos(loginMail, mySharePath);
                
                //进行必要的容错处理，删除数据库中的冗余数据。user663
                int length = mySharePath.length;
                
                if(listr == null)
                {
                	return null;
                }
                
//             	for(int j=0;j<length;j++)
//            	{
//             		int size = listr.size();
//            	}
             	for(int j=0;j<length;j++)
            	{
            		String tempfilePath = mySharePath[j];
            		String tempfilePath0;
            		
            		for(int i=0;i<listr.size();i++)
            		{
            			Fileinfo  fileinfo = (Fileinfo) listr.get(i);
            			if (fileinfo.getPathInfo().equals(tempfilePath))
            			{
	            			tempfilePath0= fileinfo.getPathInfo();
	                        fileinfo.setShareCount(getShareInfoCount(tempfilePath0));
	            				 
    						for(int h=0;h<loglen;h++)
    		            	{
    		            		String lpi = (String)loglist.get(h);
    		            		if(tempfilePath0.equals(lpi))
    		            		{
    		            			fileinfo.setIslog(true);
    		            			break;
    		            		}
    		            	}               
        					if(psharelist != null && psharelist.size()>0)
        					{
        						int pslen = psharelist.size();
        						for(int h=0;h<pslen;h++)
        						{
        							Personshareinfo pshi = (Personshareinfo)psharelist.get(h);
        							String psname = pshi.getShareFile();
        							if(tempfilePath0.equals(psname))
        		            		{
	        							int spermit = (pshi.getPermit()).intValue();
	        							fileinfo.setPermit(spermit);
	        							break;
        		            		}
        						}
        					}
        					if(newpsharelist != null && newpsharelist.size()>0)
        					{
        						int pslen = newpsharelist.size();
        						for(int h=0;h<pslen;h++)
        						{
        							NewPersonshareinfo pshi = (NewPersonshareinfo)newpsharelist.get(h);
        							String psname = pshi.getShareFile();
        							if(tempfilePath0.equals(psname))
        		            		{
	        							int spermit = (pshi.getPermit()).intValue();
	        							fileinfo.setPermit(spermit);
	        							break;
        		            		}
        						}
        					}
	            		}
            		}
        		}
             	
                if(listr.size() < length)
                {
                	int size = listr.size();
                	for(int j=0;j<length;j++)
                	{
                		String tempfilePath = mySharePath[j];
                		String tempfilePath0;
                		int k=0;
                		for(int i=0;i<size;i++)
                		{
                			Fileinfo  fileinfo = (Fileinfo) listr.get(i);
                			tempfilePath0= fileinfo.getPathInfo();
                		
                			
                			
                			k++;
                			if(tempfilePath0.equals(tempfilePath))
                			{
                				break;
                			}
                			if(k>=size)
                    		{
                    			deleteredundancyShareFile(creatorID,tempfilePath);
                    		}
                		}
                	
                		
                	}
                }
                
                if (sort != null)
                {
                    Collections.sort(listr,
                        new FileArrayComparator(sort, dir.equals("ASC") ? 1 : -1));
                }
                else
                {
                	Collections.sort(listr, new FileArrayComparator("lastChanged", -1));
                }
                for (int i = start; i < limit + start; i++)
                {
                    if (i >= listr.size())
                    {
                        break;
                    }
                    filePath.add(listr.get(i));
                }
                filePath.add(0, listr.size());
                
                return filePath;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Fileinfo> getDeparmentShare(String loginMail, long creatorID, String companyID, int start,
            int limit, String sort, String dir)
        {
            String[] mySharePath = getDeparmentShareFPath(creatorID, companyID);
            if (limit < 0)
            {
                limit = mySharePath.length - start;
            }
            ArrayList filePath = new ArrayList();
            /*for (int i = start; i < limit + start; i++)
            {
                if (i >= mySharePath.length)
                {
                    break;
                }
                filePath.add(mySharePath[i]);

            }*/
            if (mySharePath.length > 0)//filePath != null && filePath.size() > 0)
            {
                try
                {
                	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                			JCRService.NAME);
                    ArrayList listr = jcrService.getFileinfos(loginMail, mySharePath);
                    //进行必要的容错处理，删除数据库中的冗余数据。user663
                    int length = mySharePath.length;
                    
                    if(listr == null)
                    {
                    	return null;
                    }
                    
                    if(listr.size() < length)
                    {
                    	int size = listr.size();
                    	for(int j=0;j<length;j++)
                    	{
                    		String tempfilePath = mySharePath[j];
                    		String tempfilePath0;
                    		int k=0;
                    		for(int i=0;i<size;i++)
                    		{
                    			Fileinfo  fileinfo = (Fileinfo) listr.get(i);
                    			tempfilePath0= fileinfo.getPathInfo();
                    			k++;
                    			if(tempfilePath0.equals(tempfilePath))
                    			{
                    				break;
                    			}
                    			if(k>=size)
                        		{
                        			deleteredundancyShareFile(creatorID,tempfilePath);
                        		}
                    		}
                    	
                    		
                    	}
                    }
                    if (sort != null)
                    {
                        Collections.sort(listr,
                            new FileArrayComparator(sort, dir.equals("ASC") ? 1 : -1));
                    }
                    for (int i = start; i < limit + start; i++)
                    {
                        if (i >= listr.size())
                        {
                            break;
                        }
                        filePath.add(listr.get(i));
                    }
                    filePath.add(0, listr.size());
                    return filePath;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }
    
    //删除数据库中的冗余数据。
    public void deleteredundancyShareFile(long creatorID,String shareFile)
    {
    	personshareinfoDAO.delByOwnerAndFile(creatorID,shareFile);
    	groupmembershareinfoDAO.delByOwnerAndFile(creatorID, shareFile);
    }

  
    public String[] getMyShareFPath(long creatorID, String companyID)
    {
        try
        {
//            Userinfo user = structureDAO.findUserById(creatorID);
            List<String> usrset = personshareinfoDAO.findPathByProperty(
                "userinfoByShareowner.id", creatorID);
            List<String> groupset = groupshareinfoDAO.findPathByProperty("userinfo.id",
                creatorID);
            ArrayList<String> newfs = new ArrayList<String>();
            String fileName = null;
            Iterator<String> ui = usrset.iterator();
            while (ui.hasNext())
            {
                fileName = ui.next();
                if (!newfs.contains(fileName))
                {
                    newfs.add(fileName);
                }
            }
            Iterator<String> gi = groupset.iterator();
            while (gi.hasNext())
            {
                fileName = gi.next();
                if (!newfs.contains(fileName))
                {
                    newfs.add(fileName);
                }
            }
            return newfs.toArray(new String[newfs.size()]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String[] getDeparmentShareFPath(long creatorID, String companyID)
    {
        try
        {
            List<String> usrset = personshareinfoDAO.findPathByProperty(
                "groupinfoOwner.id", creatorID);
            List<String> groupset = groupshareinfoDAO.findPathByProperty("groupinfoOwner.id",
                creatorID);
            ArrayList<String> newfs = new ArrayList<String>();
            String fileName = null;
            Iterator<String> ui = usrset.iterator();
            while (ui.hasNext())
            {
                fileName = ui.next();
                if (!newfs.contains(fileName))
                {
                    newfs.add(fileName);
                }
            }
            Iterator<String> gi = groupset.iterator();
            while (gi.hasNext())
            {
                fileName = gi.next();
                if (!newfs.contains(fileName))
                {
                    newfs.add(fileName);
                }
            }
            return newfs.toArray(new String[newfs.size()]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    
    public List<Personshareinfo> getShareOwner(long sharedID, String companyID)
    {
        //个人共享到组的数据表已更改，groupmembershareviewDAO实际已经没有意义，故此处代码注释
//        java.util.Hashtable<String, UserinfoView> table = new java.util.Hashtable<String, UserinfoView>();
//        List<Long> gv = groupmembershareviewDAO.getShareOwner(sharedID);
//        Iterator<Long> gvt = gv.iterator();
//        while (gvt.hasNext())
//        {
//            Long temp = gvt.next();
//            UserinfoView uTemp = userinfoViewDAO.findById(temp);
//            table.put(temp.toString(), uTemp);
//        }

//        Hashtable<String, UserinfoView> table = new Hashtable<String, UserinfoView>();
//        List<Userinfo> fs = personshareinfoDAO.getShareOwner(sharedID);
//        Iterator<Userinfo> it = fs.iterator();
//        while (it.hasNext())
//        {
//            Userinfo temp = it.next();
//            UserinfoView userview = new UserinfoView();
//            userview.setUserId(temp.getUserId());
//            userview.setEmail(temp.getEmail());
//            userview.setUserName(temp.getUserName());
//            table.put(temp.getUserId().toString(), userview);
//        }
//        ArrayList<UserinfoView> aa = new ArrayList<UserinfoView>(table.values());
        return personshareinfoDAO.findByProperty("userinfoBySharerUserId.id", sharedID);
    }

    private ArrayList<Shareinfo> getShareinfoforOthers(long sharedID, String companyID)
    {

        try
        {
            java.util.Hashtable<String, Shareinfo> table = new java.util.Hashtable<String, Shareinfo>();
            List<Groupmembershareview> gs = groupmembershareviewDAO.findByMemberId(sharedID);
            Iterator<Groupmembershareview> gi = gs.iterator();
            while (gi.hasNext())
            {
                Groupmembershareview temp = gi.next();
                String tempName = temp.getShareFile();
                Shareinfo info = new Shareinfo(tempName, temp.getPermit(),temp.getIsNew());
                table.put(tempName, info);
            }

            List<Personshareinfo> fs = personshareinfoDAO.findByProperty(
                "userinfoBySharerUserId.id", sharedID);
            Iterator<Personshareinfo> it = fs.iterator();
            while (it.hasNext())
            {
                Personshareinfo temp = it.next();
                String fileName = temp.getShareFile();
                Shareinfo info = new Shareinfo(fileName, temp.getPermit(),temp.getIsNew(),temp.getUserinfoByShareowner().getRealName(),temp.getShareComment());
                table.put(fileName, info);
            }
            return new ArrayList<Shareinfo>(table.values());
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public List getOthersShare(String loginMail, long sharedID, String companyID, int start,
        int limit, String sort, String dir)
    {
        ArrayList<Shareinfo> otherShareFile = getShareinfoforOthers(sharedID, companyID);
        int size = otherShareFile.size();
        if (limit < 0)
        {
            limit = size - start;
        }
        ArrayList filePath = new ArrayList();
        if (size > 0)
        {
            try
            {

            	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
            			JCRService.NAME);
                ArrayList list = jcrService.getFileinfos(loginMail, otherShareFile);
                if (sort != null)
                {
                    Collections.sort(list,
                        new FileArrayComparator(sort, dir.equals("ASC") ? 1 : -1));
                }
                for (int i = start; i < limit + start; i++)
                {
                    if (i >= list.size())
                    {
                        break;
                    }
                    filePath.add(list.get(i));
                }
                filePath.add(0, list.size());
                return filePath;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 取得他人对我的共享信息,用于WebOffice主页面加载时显示新共享信息 user308 2009-06-11
     * @param shareUserID 被共享者
     * @return
     */
    public List getOtherShareInfo(long shareUserID)
    {
        ArrayList<String> list = new ArrayList<String>();
        java.util.Hashtable<String, Shareinfo> table = new java.util.Hashtable<String, Shareinfo>();
        JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
        		JCRService.NAME);
        List<Groupmembershareview> gs = groupmembershareviewDAO.findByMemberAndNew(shareUserID, 0);
        Iterator<Groupmembershareview> gi = gs.iterator();
        while (gi.hasNext())
        {
            Groupmembershareview temp = gi.next();
            long ownerID = temp.getShareowner();
            UserinfoView ownerInfo = userinfoViewDAO.findById(ownerID);
            String ownerName = ownerInfo.getUserName();
            String shareFile = temp.getShareFile();
            Fileinfo file;
            try
            {
                file = jcrService.getFile(ownerInfo.getEmail(), shareFile);
                if (!list.contains(ownerName) && file != null)
                {
                    list.add(ownerName);
                }

            }
            catch(Exception e)
            {
//                e.printStackTrace();
            }
        } 
        List<Personshareinfo> fs = personshareinfoDAO.findByMemberAndNew(shareUserID, 0);
        Iterator<Personshareinfo> it = fs.iterator();
        while (it.hasNext())
        {
            Personshareinfo temp = it.next();
            Users ui = temp.getUserinfoByShareowner();
            String ownerName = ui.getUserName();
            String shareFile = temp.getShareFile();
            Fileinfo file;
            try
            {
                file = jcrService.getFile(ui.getSpaceUID(), shareFile);
                if (!list.contains(ownerName) && file != null)
                {
                    list.add(ownerName);
                }

            }
            catch(Exception e)
            {
//                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 
     * @param sharedID
     * @param companyID
     * @return
     */
    public String[] getOthersShareFPath(long sharedID, String companyID)
    {
        /*try
        {
            Userinfo user = structureDAO.findUserById(sharedID);
            Set<Personshareinfo> fs = user.getPersonshareinfosForSharerUserId();
            Iterator<Personshareinfo> it = fs.iterator();
            ArrayList<String> newfs = new ArrayList<String>();
            while (it.hasNext())
            {
                String temp = it.next().getShareFile();
                if (!newfs.contains(temp))
                {
                    newfs.add(temp);
                }
            }
            Set<UsersOrganizations> groupmemberSet = user.getGroupmemberinfos();
            Iterator<UsersOrganizations> gi = groupmemberSet.iterator();
            while (gi.hasNext())
            {
                Organizations temp = gi.next().getGroupinfo();
                Set<Groupshareinfo> sg = temp.getGroupshareinfos();
                Iterator<Groupshareinfo> si = sg.iterator();
                while (si.hasNext())
                {
                    String gtemp = si.next().getShareFile();
                    if (!newfs.contains(gtemp))
                    {
                        newfs.add(gtemp);
                    }
                }
            }
            return newfs.toArray(new String[newfs.size()]);
        }
        catch(Exception e)
        {
            return null;
        }*/
    	return null;
    }

    public void removeFileInDB(String companyID, String[] path)
    {
        int len = path.length;
        for (int i = 0; i < len; i++)
        {
//            filetaginfoDAO.delByFile(path[i], companyID);//delByLikeFile(path[i], companyID);
//            personshareinfoDAO.delByFile(path[i], companyID);//delByLikeFile(path[i], companyID);
//    		newpersonshareinfoDAO.delByFile(path[i], companyID);//delByLikeFile(path[i], companyID);
//    		int dx = path[i].lastIndexOf('/');
//    		String aa = path[i].substring(dx+1);            		
//    		int dd = aa.lastIndexOf('.');            		
//            if (dd == -1)
//            {
//            	delNewPersonShareinfoByPath(path[i], companyID);
//            }
//            groupshareinfoDAO.delByFile(path[i]);//delByLikeFile(path[i], companyID);
//            groupmembershareinfoDAO.delByFile(path[i]);//delByLikeFile(path[i], companyID);
        	
        	filetaginfoDAO.delByLikeFile(path[i], companyID);
            personshareinfoDAO.delByLikeFile(path[i], companyID);
    		newpersonshareinfoDAO.delByLikeFile(path[i], companyID);
    		int dx = path[i].lastIndexOf('/');
    		String aa = path[i].substring(dx+1);            		
    		int dd = aa.lastIndexOf('.');            		
            if (dd == -1)
            {
            	shareLoginfo = new ArrayList();
            	delNewPersonShareinfoByPath(path[i], companyID);
            }
            groupshareinfoDAO.delByLikeFile(path[i], companyID);
            groupmembershareinfoDAO.delByLikeFile(path[i], companyID);
           
        }
        //groupshareinfoDAO.delByLikeSignFile(path);
    }

    // 打开文件判断，里面逻辑太乱。
    public DataHolder checkFile1(String companyID, String email, long userID, int permit,
            DataHolder holder,String parentPath) throws PathNotFoundException, RepositoryException
        {

            DataHolder dataHolder = new DataHolder();
            String[] path = holder.getStringData();
            ArrayList filePaths = new ArrayList();
            for (int i = 0; i < path.length; i++)
            {
                filePaths.add(path[i]);
            }        
            int newPermit = 0;
            if ((permit & Constant.ISSHARE) != 0)
            {
            	//user663 修改。
            	if(parentPath== null || parentPath.equals(""))
            	{
            		 newPermit |= checkHasShareFile(path);
            	}
            	else
            	{
            		 newPermit |= checkHasShareFile(new String[]{parentPath});
            	}
            	String sharepath=path[0];//打开文档，怎么要传文档路径？
            	if (newPermit==0)//增加共享目录下的文件权限判断——孙爱华
            	{
            		while (!sharepath.endsWith("Document"))
            		{
            			int index=sharepath.lastIndexOf("/");
            			if (index>0)
            			{
            				sharepath=sharepath.substring(0,index);
            				newPermit |= checkHasShareFile(new String[]{sharepath});
            				if (newPermit>0)
            				{
            					break;
            				}
            			}
            			else
            			{
            				break;
            			}
            		}
            	}
            }            
            if ((permit & Constant.ISDOWN) != 0 || (permit & Constant.ISWRITE) != 0
            		|| (permit & Constant.ISREAD) != 0
            		|| (permit & Constant.CAN_NEW) != 0
            		|| (permit & Constant.CAN_COPY) != 0
            		|| (permit & Constant.CAN_DEL) != 0
            		|| (permit & Constant.CAN_MOVE) != 0
            		|| (permit & Constant.CAN_RENAME) != 0
            		|| (permit & Constant.CAN_SHARE) != 0
            		|| (permit & Constant.CAN_PASTE) != 0
            		|| (permit & Constant.CAN_UPLOAD) != 0
            		)
            {
            	if(parentPath== null || parentPath.equals(""))
            	{
            		  newPermit |= getSharePermit(path[0], userID);
            	}
              
            	else
            	{
            		  newPermit |= getSharePermit(parentPath, userID);
            	}
            }
            if ((permit & Constant.ISOPEN) != 0)
            {
                String[] ret = isFilesOpened(filePaths);
                dataHolder.setStringData(ret);
            }
            else
            {
            	dataHolder.setStringData(null);
            }
            dataHolder.setIntData(newPermit);
            return dataHolder;
        
        }
    
    
    public DataHolder checkFile(String companyID, String email, long userID, int permit,
        DataHolder holder,String parentPath) throws PathNotFoundException, RepositoryException
    {

        DataHolder dataHolder = new DataHolder();
        String[] path = holder.getStringData();
        ArrayList filePaths = new ArrayList();
        for (int i = 0; i < path.length; i++)
        {
            filePaths.add(path[i]);
        }        
        int newPermit = 0;
        if ((permit & Constant.ISSHARE) != 0)
        {
        	//user663 修改。
        	if(parentPath== null || parentPath.equals(""))
        	{
        		 newPermit |= checkHasShareFile(path);
        	}
        	else
        	{
        		 newPermit |= checkHasShareFile(new String[]{parentPath});
        	}
        }
        if ((permit & Constant.ISOPEN) != 0)
        {
            newPermit |= getFilePermit2(companyID, email, filePaths, 0, false, false);
        }
        if ((permit & Constant.ISDOWN) != 0 || (permit & Constant.ISWRITE) != 0
        		|| (permit & Constant.ISREAD) != 0
        		|| (permit & Constant.CAN_NEW) != 0
        		|| (permit & Constant.CAN_COPY) != 0
        		|| (permit & Constant.CAN_DEL) != 0
        		|| (permit & Constant.CAN_MOVE) != 0
        		|| (permit & Constant.CAN_RENAME) != 0
        		|| (permit & Constant.CAN_SHARE) != 0
        		|| (permit & Constant.CAN_PASTE) != 0
        		|| (permit & Constant.CAN_UPLOAD) != 0
        		)
        {
        	if(parentPath== null || parentPath.equals(""))
        	{
        		  newPermit |= getSharePermit(path[0], userID);
        	}
          
        	else
        	{
        		  newPermit |= getSharePermit(parentPath, userID);
        	}
        }
        if ((permit & Constant.ISLOCK) != 0)
        {
            newPermit |= getFilePermit2(companyID, email, filePaths, 1, (newPermit & Constant.ISOPEN) != 0, false);
        }

        dataHolder.setIntData(newPermit);
        return dataHolder;
    
    }

    public DataHolder checkMoveFile(String companyID, String email, long userID, int permit,
        DataHolder holder) throws PathNotFoundException, RepositoryException
    {
        DataHolder dataHolder = new DataHolder();
        ArrayList files = holder.getFilesData();
        int newPermit = 0;
        int size = files.size();
        int editCount = 0;
        if ((permit & Constant.ISSHARE) != 0)
        {
            newPermit |= checkMoveFileShare(files);
            dataHolder.setShareCount(size - files.size());
            size = files.size();
        }
        if ((permit & Constant.ISOPEN) != 0)
        {
            newPermit |= getFilePermit2(companyID, email, files, 0, false, true);
            editCount += size - files.size();
            size = files.size();
        }
        if ((permit & Constant.ISLOCK) != 0)
        {
            newPermit |= getFilePermit2(companyID, email, files, 1, (newPermit & Constant.ISOPEN) != 0, true);
            editCount += size - files.size();
        }
        if (editCount > 0)
        {
            dataHolder.setEditCount(editCount);
        }
        dataHolder.setIntData(newPermit);
        dataHolder.setFilesData(files);
        return dataHolder;
    }

    public String[] getSearchPath(String companyID, String[] tag, String[] filePath)
    {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < tag.length; i++)
        {
            for (int j = 0; j < filePath.length; j++)
            {
                List l = filetaginfoDAO.getSearchPath(companyID, tag[i], filePath[j]);
                list.addAll(l);
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * 获取带有标签为tagText的文件Path
     */
    public List<String> getTagPath(String companyID, String tagText)
    {
        /*List<Taginfo> tags = taginfoDAO.findByProperty("tag", tagText);
        if (tags != null)
        {
            List<String> filePath = new ArrayList<String>();
            int tagSize = tags.size();
            Filetaginfo tempfileTag;
            for (int i = 0; i < tagSize; i++)
            {
                Set<Filetaginfo> fileTags = tags.get(i).getFiletaginfos();
                Hibernate.initialize(fileTags);
                Iterator<Filetaginfo> it = fileTags.iterator();
                while (it.hasNext())
                {
                    tempfileTag = it.next();
                    if (tempfileTag.getCompanyId().equals(companyID))
                    {
                        filePath.add(tempfileTag.getFileName());
                    }
                }
            }
            return filePath;
        }*/
        return null;
    }

    public String[] getTagPath(long userID, String companyID, String tagText)
    {
    	 String paths[] = (String[])null;
    	 Users userinfo = structureDAO.findUserById(userID);
         List objs11 = getTaginfoDAO().findByTagAndID(tagText, userID);
         /*for(int i = 0; i < objs11.size(); i++)
         {
             Taginfo tag = (Taginfo)objs11.get(i);
             System.out.println((new StringBuilder(String.valueOf(tag.getTag()))).append("       ").append(tag.getTagId()).toString());
         }*/

         if(objs11 != null && objs11.size() > 0)
         {
             long tagid = ((Taginfo)objs11.get(0)).getTagId().longValue();
             List obj1s = getFiletaginfoDAO().getFilesByTagID(userinfo.getSpaceUID(), tagid, companyID);
             paths = new String[obj1s.size()];
             for(int i = 0; i < paths.length; i++)
             {
                 paths[i] = (String)obj1s.get(i);
//                 System.out.println(paths[i]);
             }

         }
         return paths;

    }
    private ArrayList<Shareinfo> getOthersShareFPath(long ownerID, long sharedID, String companyID)
    {
        java.util.Hashtable<String, Shareinfo> table = new java.util.Hashtable<String, Shareinfo>();
        List<Groupmembershareview> gs = groupmembershareviewDAO.getByOwnerAndShare(ownerID,
            sharedID);
        Iterator<Groupmembershareview> gi = gs.iterator();
        while (gi.hasNext())
        {
            Groupmembershareview temp = gi.next();
            String tempName = temp.getShareFile();
            Shareinfo info = new Shareinfo(tempName, temp.getPermit(),temp.getIsNew());
            table.put(tempName, info);
        }

        List<Personshareinfo> fs = personshareinfoDAO.getByOwnerAndShare(ownerID, sharedID);
        Iterator<Personshareinfo> it = fs.iterator();
        while (it.hasNext())
        {
            Personshareinfo temp = it.next();
            String fileName = temp.getShareFile();
            Shareinfo info = new Shareinfo(fileName, temp.getPermit(),temp.getIsNew(),temp.getUserinfoByShareowner().getRealName(),temp.getShareComment());
            table.put(fileName, info);
        }
        return new ArrayList<Shareinfo>(table.values());
    }
    
    private ArrayList<Shareinfo> getOthersShareFPath2(long ownerID, long sharedID, String companyID)
    {
        java.util.Hashtable<String, Shareinfo> table = new java.util.Hashtable<String, Shareinfo>();
        List<Groupmembershareview> gs = groupmembershareviewDAO.getByOwnerAndShare(ownerID,
            sharedID);
        Iterator<Groupmembershareview> gi = gs.iterator();
        while (gi.hasNext())
        {
            Groupmembershareview temp = gi.next();
            String tempName = temp.getShareFile();
            Shareinfo info = new Shareinfo(tempName, temp.getPermit(),temp.getIsNew());
            table.put(tempName, info);
        }

        List<Personshareinfo> fs = personshareinfoDAO.getByGroupOwnerAndShare(ownerID, sharedID);
        Iterator<Personshareinfo> it = fs.iterator();
        while (it.hasNext())
        {
            Personshareinfo temp = it.next();
            String fileName = temp.getShareFile();
            Shareinfo info = new Shareinfo(fileName, temp.getPermit(),temp.getIsNew(),temp.getUserinfoByShareowner().getRealName(),temp.getShareComment());
            table.put(fileName, info);
        }
        return new ArrayList<Shareinfo>(table.values());
    }
    
    private ArrayList<Shareinfo> getGroupShareFPath(long ownerGroupID, long sharedID, int start, int count)
    {	
        java.util.Hashtable<String, Shareinfo> table = new java.util.Hashtable<String, Shareinfo>();
        List<Groupshareinfo> list = groupshareinfoDAO.findShareFileByGroupId(sharedID, ownerGroupID, start, count);
        if(list != null && list.size() > 0)
        {
        	for (Groupshareinfo temp : list)
	        {
	            String tempName = temp.getShareFile();
	            Shareinfo info = new Shareinfo(tempName, temp.getPermit(), 0);
	            table.put(tempName, info);
	        }
        }
        
        return new ArrayList<Shareinfo>(table.values());
    }

    public ArrayList<Object> getFilesByGroupShare(long ownerGroupID, long shareID, int start, int count)
    {
    		if (count < 0)
    		{
    			count = (int)groupshareinfoDAO.findShareFileByGroupIdCount(shareID, ownerGroupID);
    			count -= start;
    		}
            ArrayList<Shareinfo> otherShareFile = getGroupShareFPath(ownerGroupID, shareID, start, count);
            int size = otherShareFile.size();
            ArrayList filePath = new ArrayList();
            if (size > 0)//filePath != null && filePath.size() > 0)
            {
                try
                {
                    //ArrayList list = new ArrayList();
                    //list.add(size);
                	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);                    
                    ArrayList list = jcrService.getFileinfos("", otherShareFile);
                    for (Object temp : list)
                    {
                        filePath.add(temp);
                    }
                    filePath.add(0, list.size());
                    return filePath;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }
    
    private ArrayList<Shareinfo> getGroupShareFPath2(long ownerUserID, long sharedID, int start, int count)
    {   
        java.util.Hashtable<String, Shareinfo> table = new java.util.Hashtable<String, Shareinfo>();
        List<Groupshareinfo> list = groupshareinfoDAO.findShareFileByUserId(sharedID, ownerUserID, start, count);
        if(list != null && list.size() > 0)
        {
            for (Groupshareinfo temp : list)
            {
                String tempName = temp.getShareFile();
                Shareinfo info = new Shareinfo(tempName, temp.getPermit(), 0);
                table.put(tempName, info);
            }
        }        
        return new ArrayList<Shareinfo>(table.values());
    }
    
    public ArrayList<Object> getFilesByGroupShare2(long ownerUserID, long shareID, int start, int count)
    {
            if (count < 0)
            {
                count = (int)groupshareinfoDAO.findShareFileByUserIdCount(shareID, ownerUserID);
                count -= start;
            }
            ArrayList<Shareinfo> otherShareFile = getGroupShareFPath2(ownerUserID, shareID, start, count);
            int size = otherShareFile.size();
            ArrayList filePath = new ArrayList();
            if (size > 0)//filePath != null && filePath.size() > 0)
            {
                try
                {
                    //ArrayList list = new ArrayList();
                    //list.add(size);
                	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);                    
                    ArrayList list = jcrService.getFileinfos("", otherShareFile);
                    for (Object temp : list)
                    {
                        filePath.add(temp);
                    }
                    filePath.add(0, list.size());
                    return filePath;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }
    
    
    public ArrayList<Object> getFilesByShare(String loginMail, long owerID, long shareID,
        String companyID, int start, int limit)
    {
        ArrayList<Shareinfo> otherShareFile = getOthersShareFPath(owerID, shareID, companyID);
        int size = otherShareFile.size();
        if (limit < 0)
        {
            limit = size - start;
        }
        ArrayList filePath = new ArrayList();
        /*for (int i = start; i < limit + start; i++)
        {
            if (i >= size)
            {
                break;
            }
            filePath.add(otherShareFile.get(i));
        }*/
        if (size > 0)//filePath != null && filePath.size() > 0)
        {
            try
            {
                //ArrayList list = new ArrayList();
                //list.add(size);
            	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
            			JCRService.NAME);
                
                ArrayList list = jcrService.getFileinfos(loginMail, otherShareFile);
                for (int i = start; i < limit + start; i++)
                {
                    if (i >= list.size())
                    {
                        break;
                    }
                    filePath.add(list.get(i));
                }
                filePath.add(0, list.size());
                return filePath;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public ArrayList<Object> getFilesByShare2(String loginMail, long owerID, long shareID,
            String companyID, int start, int limit)
        {
            ArrayList<Shareinfo> otherShareFile = getOthersShareFPath2(owerID, shareID, companyID);
            int size = otherShareFile.size();
            if (limit < 0)
            {
                limit = size - start;
            }
            ArrayList filePath = new ArrayList();
            /*for (int i = start; i < limit + start; i++)
            {
                if (i >= size)
                {
                    break;
                }
                filePath.add(otherShareFile.get(i));
            }*/
            if (size > 0)//filePath != null && filePath.size() > 0)
            {
                try
                {
                    //ArrayList list = new ArrayList();
                    //list.add(size);
                	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                        JCRService.NAME);
                    
                    ArrayList list = jcrService.getFileinfos(loginMail, otherShareFile);
                    for (int i = start; i < limit + start; i++)
                    {
                        if (i >= list.size())
                        {
                            break;
                        }
                        filePath.add(list.get(i));
                    }
                    filePath.add(0, list.size());
                    return filePath;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }
    
    

    public ArrayList<Object> getFilesByShare(String loginMail, long owerID, long shareID,
        String companyID, int start, int limit, String sort, String dir)
    {
        ArrayList<Shareinfo> otherShareFile = getOthersShareFPath(owerID, shareID, companyID);
        int size = otherShareFile.size();
        if (limit < 0)
        {
            limit = size - start;
        }
        ArrayList filePath = new ArrayList();
        /*for (int i = start; i < limit + start; i++)
        {
            if (i >= size)
            {
                break;
            }
            filePath.add(otherShareFile.get(i));
        }*/
        if (size > 0)//filePath != null && filePath.size() > 0)
        {
            try
            {
                //ArrayList list = new ArrayList();
                //list.add(size);
                JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                    JCRService.NAME);

                ArrayList list = jcrService.getFileinfos(loginMail, otherShareFile);
                if (sort != null)
                {
                    Collections.sort(list,
                        new FileArrayComparator(sort, dir.equals("ASC") ? 1 : -1));
                }
                for (int i = start; i < limit + start; i++)
                {
                    if (i >= list.size())
                    {
                        break;
                    }
                    filePath.add(list.get(i));
                }
                filePath.add(0, list.size());
                return filePath;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public ArrayList<Object> getFilesByShare2(String loginMail, long owerID, long shareID,
            String companyID, int start, int limit, String sort, String dir)
        {
            ArrayList<Shareinfo> otherShareFile = getOthersShareFPath2(owerID, shareID, companyID);
            int size = otherShareFile.size();
            if (limit < 0)
            {
                limit = size - start;
            }
            ArrayList filePath = new ArrayList();
            /*for (int i = start; i < limit + start; i++)
            {
                if (i >= size)
                {
                    break;
                }
                filePath.add(otherShareFile.get(i));
            }*/
            if (size > 0)//filePath != null && filePath.size() > 0)
            {
                try
                {
                    //ArrayList list = new ArrayList();
                    //list.add(size);
                    JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                        JCRService.NAME);

                    ArrayList list = jcrService.getFileinfos(loginMail, otherShareFile);
                    if (sort != null)
                    {
                        Collections.sort(list,
                            new FileArrayComparator(sort, dir.equals("ASC") ? 1 : -1));
                    }
                    for (int i = start; i < limit + start; i++)
                    {
                        if (i >= list.size())
                        {
                            break;
                        }
                        filePath.add(list.get(i));
                    }
                    filePath.add(0, list.size());
                    return filePath;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }
    
    private int getSharePermit(String filePath, long shareID)
    {
        try
        {
            Personshareinfo ps = personshareinfoDAO.findByOtherShareAndPath(shareID, filePath);
            if (ps != null)
            {
                return ps.getPermit();
            }
            NewPersonshareinfo nps = newpersonshareinfoDAO.findByOtherShareAndPath(shareID, filePath);
            if (nps != null)
            {
                return nps.getPermit();
            }
            ps = personshareinfoDAO.findByShareOwnerAndPath(shareID, filePath);
            if (ps != null)
            {
                return ps.getPermit();
            }
            nps = newpersonshareinfoDAO.findByShareOwnerAndPath(shareID, filePath);
            if (nps != null)
            {
                return nps.getPermit();
            }
            
            Groupshareinfo gs = groupshareinfoDAO.findByShareOwnerAndPath(shareID, filePath);
            if(gs != null)
            {
                return gs.getPermit();
            }
            Users user = structureDAO.findUserById(shareID);
            //Set<UsersOrganizations> gms = user.getGroupmemberinfos();
            //List<UsersOrganizations> gms = groupmemberinfoDAO.findByProperty("userinfo", user);
            //Iterator<UsersOrganizations> it = gms.iterator();
            List<Organizations> it = structureDAO.findOrganizationsByUserId(user.getId());
            //while (it.hasNext())
            for (Organizations group : it)
            {
                //Organizations group = it.next().getOrganization();
                gs = groupshareinfoDAO.findByShareAndPath(group.getId(),
                    filePath);
                if (gs != null)
                {
                    return gs.getPermit();
                }
            }
            return 0;
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    //仅限他人共享使用，外人最好不要调用。���˹���
    public Integer[] getAllSharePermit(String[] filePath, long shareID)
    {
    	Integer[] permit = new Integer[filePath.length];
    	for (int i = 0; i < filePath.length; i++)
    	{
			String path = filePath[i];
			Personshareinfo ps = personshareinfoDAO.findByOtherShareAndPath(
					shareID, path);
			if (ps != null)
			{
				permit[i] = ps.getPermit();
			}
			else
			{
				NewPersonshareinfo nps = newpersonshareinfoDAO.findByOtherShareAndPath(
						shareID, path);
				if(nps != null)
				{
					permit[i] = nps.getPermit();
				}
				else 
				{
					Users user = structureDAO.findUserById(shareID);
					//Set<UsersOrganizations> gms = user.getGroupmemberinfos();
					//List<UsersOrganizations> gms = groupmemberinfoDAO.findByProperty("userinfo", user);
					//Iterator<UsersOrganizations> it = gms.iterator();
					//while (it.hasNext())
					//{
						//Organizations group = it.next().getOrganization();
					List<Organizations> it = structureDAO.findOrganizationsByUserId(user.getId());
		            //while (it.hasNext())
		            for (Organizations group : it)
		            {
						Groupshareinfo gs = groupshareinfoDAO.findByShareAndPath(group.getId(),
								path);
						if (gs != null) 
						{
							permit[i] = gs.getPermit();
						}
						else 
						{
							permit[i] = null;
						}
					}
				}
			}
			
		}
    	return permit;
    }

    public String getShareRealName(String filePath, long shareID)
    {
		Personshareinfo ps = personshareinfoDAO.findByOtherShareAndPath(
				shareID, filePath);
		if (ps != null)
		{
			return ps.getUserinfoByShareowner().getRealName();
		}
		NewPersonshareinfo nps = newpersonshareinfoDAO.findByOtherShareAndPath(shareID, filePath);
		if(nps != null)
		{
			
			return nps.getUserinfoByShareowner().getRealName();
		}
		return null;
    }
    public String getSharecomment(String filePath, long shareID)
    {
		Personshareinfo ps = personshareinfoDAO.findByOtherShareAndPath(
				shareID, filePath);
		if (ps != null)
		{
			return ps.getShareComment();
			
		}
		NewPersonshareinfo nps = newpersonshareinfoDAO.findByOtherShareAndPath(shareID, filePath);
		if(nps != null)
		{
			
			return nps.getShareComment();
		}
		return null;
    }
    public Fileinfo getFileinfo(String filePath,long shareID)
    {
    	Personshareinfo ps = personshareinfoDAO.findByOtherShareAndPath(
				shareID, filePath);
		Fileinfo finfo = new Fileinfo();
    	if (ps != null)
		{
    		finfo.setPermit(ps.getPermit());
    		finfo.setShareRealName(ps.getUserinfoByShareowner().getRealName());
    		finfo.setShareCommet(ps.getShareComment());
    		finfo.setShareTime(ps.getDate());
			return finfo;
			
		}
		NewPersonshareinfo nps = newpersonshareinfoDAO.findByOtherShareAndPath(shareID, filePath);
		if(nps != null)
		{
			finfo.setPermit(nps.getPermit());
    		finfo.setShareRealName(nps.getUserinfoByShareowner().getRealName());
    		finfo.setShareCommet(nps.getShareComment());
    		finfo.setShareTime(nps.getDate());
			return finfo;
		}
    	return null;
    }
    /**
     * 
     * @param companyID
     * @param userID
     * @param filePaths
     * @param listFlag 0 文件打开列表 1 文件锁定列表
     * @return
     * @throws PathNotFoundException
     * @throws RepositoryException
     */

//    private int getFilePermit(String companyID, String userID, ArrayList filePaths, int flag, boolean isOpen, boolean needRemove)
//        throws PathNotFoundException, RepositoryException
//    {
//        JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
//            JCRService.NAME);
//
////        String nodePath = FileUtils.getPreName(path[0]);
//
//        ArrayList<String> array = jcrService.getFileList(userID, userID, flag);
//        
//        int permit = 0;
//        int size = filePaths.size();
//        for (int i = size - 1; i >= 0; i--)
////        for (int i = 0; i < path.length; i++)
//        {
//            String temp = (String)filePaths.get(i);
//            if (flag == 0)
//            {
//                ArrayList<String> closeList = jcrService.getFileList(userID, userID, 2);                
//                if (array.contains(temp))
//                {
//                    if (closeList.contains(temp))
//                    {
//                        //容错处理，如果打开列表里有该文件但关闭列表里同样有该文件，则认为该文件已被关闭，可以打开
//                        //清除打开列表中该文件
//                        jcrService.removeFileList(userID, temp, 0);
//                        //继续遍历锁定列表，如果锁定列表里有该文件但关闭列表里同样有该文件且锁定者就是本人，则认为该文件可以打开，并解锁
//                        ArrayList<String> lockList = jcrService.getFileList(userID, userID, 1);
//                        String tempF = temp + "," + userID;
//                        for (int j = 0; j < lockList.size(); j++)
//                        {
//                            if (lockList.get(j).equals(tempF))
//                            {
//                                //清除锁定列表中该文件
//                                jcrService.removeFileList(userID, tempF, 1);
//                                break;
//                            }
//                        }
//                        //清除关闭列表中该文件
//                        jcrService.removeFileList(userID, temp, 2);
//                    }
//                    else
//                    {
//                        permit |= Constant.ISOPEN;
//                        if (needRemove)
//                        {
//                            filePaths.remove(i);
//                        }
//                    }
//                }
//                else
//                {
//                                    	
//                    ArrayList<String> lockList = jcrService.getFileList(userID, userID, 1);
//                    String temp2 = temp + "," + userID;
//                    for (int j = 0; j < lockList.size(); j++)
//                    {
//                        if (lockList.get(j).equals(temp2))
//                        {
//                            //清除锁定列表中该文件
//                            jcrService.removeFileList(userID, temp2, 1);
//                            break;
//                        }
//                    }
//                    //有时候会莫名其妙出现打开列表没加进去的情况，这里先容下错吧，如果打开列表里没有，但关闭列表里有，则从关闭列表中清除
//                    if (closeList.contains(temp))
//                    {
//                        jcrService.removeFileList(userID, temp, 2);
//                    }
//                }
//            }
//            else if(flag == 1)
//            {
//                for (int j = 0; j < array.size(); j++)
//                {
//                    String str = array.get(j);
//                    int index = str.indexOf(",");
//                    str = str.substring(0, index);
//                    if (str.equals(temp))
//                    {
//                        if (!isOpen || userID.equals(array.get(j).substring(index + 1)))
//                        {
//                            permit |= Constant.ISLOCK;
//                            if (needRemove)
//                            {
//                                filePaths.remove(i);
//                            }
//                        }
//                        break;
//                    }
//                }
//            }
//        }
//        return permit;
//    }

    private String[] isFilesOpened(ArrayList filePaths)
    {
    	int permit = 0;        
        int size = filePaths.size();
        ArrayList<String> ret = new ArrayList<String>();
        String temp;
        String opened;
        for (int i = size - 1; i >= 0; i--)
        {
            temp = (String)filePaths.get(i);
            opened = FilesHandler.isFileOpened("", temp); 
            if (opened != null)
            {
            	ret.add(opened);
            }
        }
        if (ret.size() > 0)
        {
        	return ret.toArray(new String[ret.size()]);
        }
        return null;
    }
    
    private int getFilePermit2(String companyID, String userID, ArrayList filePaths, int flag, boolean isOpen, boolean needRemove)
        throws PathNotFoundException, RepositoryException
    {             
        int permit = 0;        
        int size = filePaths.size();        
        for (int i = size - 1; i >= 0; i--)
        {
    		List<String> ret = FilesHandler.getFileOpened("", (String)filePaths.get(i));
    		if (ret != null && ret.size() > 0)
    		{
    			permit |= Constant.ISOPEN;
    			if (needRemove)
                {
                    filePaths.remove(i);
                }
    		}
        }           
        if (true)
        {
        	return permit;
        }
        
        JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);   
        ArrayList<String> array = jcrService.getFileList(userID, userID, flag);
        if (flag == 0)
        {
            ArrayList<String> lockList = jcrService.getFileList(userID, userID, 1);
            ArrayList<String> closeList = jcrService.getFileList(userID, userID, 2);
            //lockList.add("ball_emo3.com/Document/kkk/222/Web_Bug.xls");
            //array.add("ball_emo3.com/Document/555/Web_Bug.xls");
            // 先做容错处理。
            doCloseList(jcrService, userID, array, closeList, lockList);
            for (int i = size - 1; i >= 0; i--)
            {
                String temp = (String)filePaths.get(i);
                int idx = temp.lastIndexOf('/');
                String endName = temp.substring(idx + 1);
                boolean isFold = endName.indexOf('.') < 0;
                boolean same = false;
                for (int j = 0; j < array.size(); j++)
                {
                    String open = array.get(j);
                    if (open.equals(temp))
                    {
                        same = true;
                        break;
                    }
                    else if (isFold)
                    {
                        int len = temp.length();
                        if (open.indexOf(temp) == 0)
                        {
                            if (open.charAt(len) == '/')
                            {
                                same = true;
                                break;
                            }
                        }
                    }
                }
                if (same)
                {
                    permit |= Constant.ISOPEN;
                    if (needRemove)
                    {
                        filePaths.remove(i);
                    }
                }
                else if (!isFold)
                {
                    //有时候会莫名其妙出现打开列表没加进去的情况，这里先容下错吧，如果打开列表里没有，但关闭列表里有，则从关闭列表中清除
                    if (closeList.contains(temp))
                    {
                        jcrService.removeFileList(userID, temp, 2);
                    }
                }
            }
        }
        else if(flag == 1)
        {
            for (int i = size - 1; i >= 0; i--)
            {
                String temp = (String)filePaths.get(i);
                int idx = temp.lastIndexOf('/');
                String endName = temp.substring(idx + 1);
                boolean isFold = endName.indexOf('.') < 0;
                boolean same = false;
                String lockUserID = null;
                for (int j = 0; j < array.size(); j++)
                {
                    String lock = array.get(j);
                    int index = lock.indexOf(",");
                    String lockPath = lock.substring(0, index);
                    if (lockPath.equals(temp))
                    {
                        lockUserID = lock.substring(index + 1);
                        same = true;
                        break;
                    }
                    else if (isFold)
                    {
                        int len = temp.length();
                        if (lockPath.indexOf(temp) == 0)
                        {
                            if (lockPath.charAt(len) == '/')
                            {
                                lockUserID = lock.substring(index + 1);
                                same = true;
                                break;
                            }
                        }
                    }
                }
                if (same && (!isOpen || userID.equals(lockUserID)))
                {
                    permit |= Constant.ISLOCK;
                    if (needRemove)
                    {
                        filePaths.remove(i);
                    }
                }
            }
        }
        return permit;
    }

    /**
     * 容错处理，(1)如果关闭列表里有某文件，锁定列表里也有该文件且锁定者就是本人，则清除锁定列表里该文件（解锁）
     * (2)如果锁定列表里有某文件且锁定者就是本人,但打开列表里没有该文件，则清除锁定列表里该文件（解锁）
     * (3)如果关闭列表里有某文件，同时打开列表里也有该文件，则清除打开列表和关闭列表中该文件
     * (2)的情况一般不会发生，但刚打开文件就当机时，可能导致锁定列表中添加该文件，而打开列表中还没来得及添加。
     * @param jcrService
     * @param userID
     * @param openList 打开列表
     * @param closeList 关闭列表
     * @param lockList 锁定列表
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
    private void doCloseList(JCRService jcrService, String userID,
            ArrayList<String> openList, ArrayList<String> closeList, ArrayList<String> lockList)
    throws PathNotFoundException, RepositoryException
    {
        int closeSize = closeList.size();
        int lockSize = lockList.size();
        for (int i = lockSize - 1; i >= 0; i--)
        {
            String lock = (String)lockList.get(i);
            int index = lock.indexOf(",");
            String lockUserID = lock.substring(index + 1);
            // 锁定列表里的文件，如果锁定者是本人，且在关闭列表中存在，或者不在打开列表中，则解锁。
            if (lockUserID.equals(userID))
            {
                String lockPath = lock.substring(0, index);
                if (closeList.contains(lockPath) || !openList.contains(lockPath))
                {
                    //清除锁定列表中该文件
                    jcrService.removeFileList(userID, lock, 1);
                    lockList.remove(i);
                    break;
                }
            }
            else
            {
                lockList.remove(i);
            }
        }
        for (int i = closeSize - 1; i >= 0; i--)
        {
            String close = (String)closeList.get(i);
            int openSize = openList.size();
            for (int k = openSize - 1; k >= 0; k--)
            {
                String open = (String)openList.get(k);
                if (open.equals(close))
                {
                    //清除打开列表中该文件
                    jcrService.removeFileList(userID, close, 0);
                    //清除关闭列表中该文件
                    jcrService.removeFileList(userID, close, 2);
                    openList.remove(k);
                    closeList.remove(i);
                    break;
                }
            }
        }
    }

    private boolean checkHasShareFile(String path)
    {
        List l = personshareinfoDAO.likeSerch("shareFile", path);
        List l1 = newpersonshareinfoDAO.likeSerch("shareFile", path);
        if (l1 != null)
        {
        	l.addAll(l1);
        }
        if (l != null && l.size() > 0)
        {
            return true;
        }
        else
        {
            List gl = groupshareinfoDAO.likeSerch("shareFile", path);
            if (gl != null && gl.size() > 0)
            {
                return true;
            }
        }
        return false;
    }

    public int checkHasShareFile(String[] folder)
    {
        int permit = 0;
        for (int i = 0; i < folder.length; i++)
        {
            if (checkHasShareFile(folder[i]))
            {
                return permit |= Constant.ISSHARE;
            }
        }
        return permit;
    }

    /**
     * 检查移动的文件是否共享
     * @param files
     * @return
     */
    private int checkMoveFileShare(ArrayList files)
    {
        int permit = 0;
        for (int i = files.size() - 1; i >= 0; i--)
        {
            String ff = (String)files.get(i);
            if (checkHasShareFile(ff))
            {
                permit |= Constant.ISSHARE;
                files.remove(i);
            }
        }
        return permit;
    }

    /**
     * 共享的文件信息
     * @author user615
     *
     */
    public class Shareinfo
    {
        private String sharePath;
        private int permit;
        private int isNew;
        private String shareRealName;
        private String shareComment;
        private String shareToUser;
        private String approvestate;
        private String approve;//记录最后一次审阅信息
        private String approveResult;//记录最后一次审阅信息
        private String approveComment;//记录最后一次审阅信息
        

		public String getApprove()
		{
			return approve;
		}

		public void setApprove(String approve)
		{
			this.approve = approve;
		}

		public String getApproveResult()
		{
			return approveResult;
		}

		public void setApproveResult(String approveResult)
		{
			this.approveResult = approveResult;
		}

		public String getApproveComment()
		{
			return approveComment;
		}

		public void setApproveComment(String approveComment)
		{
			this.approveComment = approveComment;
		}

		public void setIsNew(int isNew)
		{
			this.isNew = isNew;
		}

		private Shareinfo(String sharePath, int permit,int isNew)
        {
            this.sharePath = sharePath;
            this.permit = permit;
            this.isNew = isNew;
        }
        
        private Shareinfo(String sharePath, int permit,int isNew,String shareRealName,String shareComment)
        {
            this.sharePath = sharePath;
            this.permit = permit;
            this.isNew = isNew;
            this.shareRealName = shareRealName;
            this.shareComment = shareComment;
        }
        private Shareinfo(String sharePath, int permit,int isNew,String shareRealName,String shareComment
        		,String approve,String approveResult,String approveComment,String shareByname)
        {
            this.sharePath = sharePath;
            this.permit = permit;
            this.isNew = isNew;
            this.shareRealName = shareRealName;
            this.shareToUser=shareByname;
            this.shareComment = shareComment;
            this.approvestate=approveResult;
            this.approve=approve;
            this.approveResult=approveResult;
            this.approveComment=approveComment;
        }
        
        private Shareinfo(String sharePath, int permit)
        {
            this.sharePath = sharePath;
            this.permit = permit;
        }

        private Shareinfo(String sharePath, int permit,String shareRealName,String shareComment)
        {
            this.sharePath = sharePath;
            this.permit = permit;
            this.shareRealName = shareRealName;
            this.shareToUser=shareRealName;
            this.shareComment = shareComment;
        }
        private Shareinfo(String sharePath,String shareByName, int permit, int isNew,
            String shareRealName, String shareComment,String approvestate,String approveComment) {
        this.sharePath = sharePath;
        this.permit = permit;
        this.isNew = isNew;
        this.shareRealName = shareRealName;
        this.shareToUser=shareByName;
        this.shareComment = shareComment;
        this.approvestate=approvestate;
        this.approveResult=approvestate;
        this.approveComment=approveComment;
    }
        
        public String getShareToUser() {
            return shareToUser;
        }

        public void setShareToUser(String shareToUser) {
            this.shareToUser = shareToUser;
        }
        
        public String getApprovestate() {
            return approvestate;
        }

        public void setApprovestate(String approvestate) {
            this.approvestate = approvestate;
        }
        public String getSharePath()
        {
            return sharePath;
        }

        public void setSharePath(String sharePath)
        {
            this.sharePath = sharePath;
        }

        public int getPermit()
        {
            return permit;
        }

        public void setPermit(int permit)
        {
            this.permit = permit;
        }
        
        public int getIsNew()
        {
            return this.isNew;            
        }
        
        public void setNew(int isNew)
        {
            this.isNew = isNew;
        }

		public String getShareRealName() {
			return shareRealName;
		}

		public void setShareRealName(String shareRealName) {
			this.shareRealName = shareRealName;
		}

		public String getShareComment() {
			return shareComment;
		}

		public void setShareComment(String shareComment) {
			this.shareComment = shareComment;
		}
    }

    public UserinfoViewDAO getUserinfoViewDAO()
    {
        return userinfoViewDAO;
    }

    public void setUserinfoViewDAO(UserinfoViewDAO userinfoViewDAO)
    {
        this.userinfoViewDAO = userinfoViewDAO;
    }

    public void moveFileInDB(String srcPaths, String desPaths)
    {
        // TODO Auto-generated method stub

    }
    
	public void cancelShare(long userID,String[] paths) {
		if (paths != null) {
			int length = paths.length;
			for (int i = 0; i < length; i++) {
				String path = paths[i];
				List<Personshareinfo> userList = personshareinfoDAO.findByUserIDAndPath(path,userID);
				List<Messages> messagesList = messagesDAO.getMessagesByUserAndAttach(userID, path);
				ArrayList<Long> userIds = new ArrayList<Long>();
				ArrayList<Long> messagesIds = new ArrayList<Long>();
				for(Personshareinfo user:userList){
					userIds.add(user.getUserinfoBySharerUserId().getId());
				}
				for (Messages messages : messagesList) {
					messagesIds.add(messages.getId());
				}
				Long[] messagesIDs = new Long[messagesIds.size()];
				for (int j = 0;j < messagesIds.size();j++) {
					messagesIDs[j] = messagesIds.get(j);
				}
				SignUtil.instance().delMessage(messagesIDs, null);
				personshareinfoDAO.delByOwnerAndFile(userID, path);
				newpersonshareinfoDAO.delByLikeFile(path, "public");
	    		int dx = path.lastIndexOf('/');
	    		String aa = path.substring(dx+1);            		
	    		int dd = aa.lastIndexOf('.');  
	    		if (dd == -1)
	            {
	            	delPersonShareinfoByPath(path, userID);
	            	delNewPersonShareinfoByPath(path, "public");
	            }
				groupshareinfoDAO.delByOwnerAndFile(userID, path);
				groupmembershareinfoDAO.delByOwnerAndFile(userID, path);
			}
			
		}
	}

	public void cancelGroupShare(long groupId,String[] paths) {
		if (paths != null) {
			int length = paths.length;
			for (int i = 0; i < length; i++) {
				String path = paths[i];
				personshareinfoDAO.delByGroupOwnerAndFile(groupId, path);
				groupshareinfoDAO.delByGroupOwnerAndFile(groupId, path);
//				groupmembershareinfoDAO.delByOwnerAndFile(userID, path);
			}
		}
	}
	
	public boolean checkShare(String[] paths) {
		if(paths != null)
		{
			int length = paths.length;
			for(int i = 0; i < length; i++)
			{
				String path = paths[i];
				if(personshareinfoDAO.findByShareFile(path)||groupshareinfoDAO.findByShareFile(path))
				{
					return true;
				}
				if(newpersonshareinfoDAO.findByShareFile(path))
				{
					return true;
				}
			}
		}
		return false;
	}
	/*
	 * (non-Javadoc)
	 * @see com.evermore.weboffice.server.service.IFileSystemService#checkShare(java.lang.String[])
	 */
	public Integer isShare(String[] paths) {
		if(paths != null)
		{
			int length = paths.length;
			for(int i = 0; i < length; i++)
			{
				String path = paths[i];
				if(personshareinfoDAO.findByShareFile(path)||groupshareinfoDAO.findByShareFile(path))
				{
					return new Integer(0);
				}
				if(newpersonshareinfoDAO.findByShareFile(path))
				{
					return new Integer(1);
				}
			}
		}
		return new Integer(100);
	}
	public void setFileNewFlagByShareOwner(String path, long ownerID, int flag) 
	{
		List <Personshareinfo> ps = personshareinfoDAO.findByOwnerAndPath(ownerID, path);
		Iterator<Personshareinfo> psit = ps.iterator();
		while(psit.hasNext())
		{
			Personshareinfo psinfo = psit.next();
			psinfo.setIsNew(flag);
		}
		List<Groupmembershareinfo> gmsi = groupmembershareinfoDAO.findByOwner(path, ownerID);
		Iterator<Groupmembershareinfo> it = gmsi.iterator();
		while(it.hasNext())
		{
			it.next().setIsNew(flag);
		}
     }
	
	public void setFileNewFlagByShareUser(String path,long shareID,int flag)
	{
		Personshareinfo ps = personshareinfoDAO.findByOtherShareAndPath(shareID, path);
		if (ps != null)
		{
		    ps.setIsNew(flag);
		}
	}
	
	public void setFileNewFlagByShareUser2(String path,long shareID,int flag)
	{
		NewPersonshareinfo ps = newpersonshareinfoDAO.findByOtherShareAndPath(shareID, path);
		if (ps != null)
		{
		    ps.setIsNew(flag);
		}
	}
	
	public void setFileNewFlagByMember(String path,long memberID,int flag)
	{
		List<Groupmembershareinfo> gmsi = groupmembershareinfoDAO.findByMemberAndPath(path, memberID);
		Iterator<Groupmembershareinfo> it = gmsi.iterator();
		while(it.hasNext())
		{
			it.next().setIsNew(flag);
		}
	}
	
	public void setFileNewFlagByMember2(String path, long memberID, int flag)
    {
        //	    long ownerID = groupmembershareinfoDAO.
        List<Groupmembershareinfo> infoList1 = groupmembershareinfoDAO.findByMemberAndPath(path, memberID);
        List<Personshareinfo> infoList2 = personshareinfoDAO.findByMemberAndPath(path, memberID);
        if(infoList1.size()==0 && infoList2.size()==0)
        {
            return;
        }
        long ownerID = -1;
        if (infoList1.size() > 0)
        {
            Iterator<Groupmembershareinfo> ite = infoList1.iterator();
            Groupmembershareinfo info = ite.next();
            ownerID = info.getShareowner();
        }
        else if (infoList2.size() > 0)
        {
            Iterator<Personshareinfo> ite = infoList2.iterator();
            Personshareinfo info = ite.next();
            if (info.getUserinfoByShareowner() == null)
            {
            	return;
            }
            ownerID = info.getUserinfoByShareowner().getId();
        }
        if (ownerID < 0)
        {
            return;
        }
        //        setFileNewFlagByShareOwner(path, ownerID, flag)

        List<Personshareinfo> ps = personshareinfoDAO.findByOwnerAndPath(ownerID, path);
        Iterator<Personshareinfo> psit = ps.iterator();
        while (psit.hasNext())
        {
            Personshareinfo psinfo = psit.next();
            if (psinfo.getUserinfoBySharerUserId().getId().longValue() != memberID)
            {
                psinfo.setIsNew(flag);
            }
        }
        List<Groupmembershareinfo> gmsi = groupmembershareinfoDAO.findByOwner(path, ownerID);
        Iterator<Groupmembershareinfo> it = gmsi.iterator();
        while (it.hasNext())
        {
            Groupmembershareinfo inf = it.next();
            if (inf.getUserinfo().getId() != memberID)
            {
                inf.setIsNew(flag);
            }
        }
    } 
	public Integer saveScheduletask(Scheduletask task)
	{
		if (task.getBacktype()!=null)
		{
			Scheduletask oldtask=getScheduletask(task.getBacktype());
			if (oldtask!=null)
			{
				scheduletaskDAO.delete(oldtask);
			}
			String schedulecontent="0 ";
			task.setTaskName("定时备份数据库");
			
			String starttime=task.getStarttime().trim();
			int hour=0;
			int minute=0;
			int index=starttime.indexOf(":");
			if (index>0)
			{
				hour=Integer.parseInt(starttime.substring(0,index));
				minute=Integer.parseInt(starttime.substring(index+1));
			}
			schedulecontent+=minute+" ";
			
			if (hour>0)
			{
				schedulecontent+=hour+" ";
			}
			else
			{
				schedulecontent+="* ";
			}
			Integer userid;
			Date addtime;
			String state;//used,stop
			String daytype=task.getDaytype();
			if ("month".equals(daytype))
			{
				schedulecontent+=task.getMonthvalue()+" * ?";
			}
			else if ("week".equals(daytype))
			{
				schedulecontent+="? * "+task.getWeekvalue()+"";
			}
			else
			{
				schedulecontent+="* * ?";
			}
			task.setSchedulecontent(schedulecontent);
			return (Integer)scheduletaskDAO.save(task);
		}
		return null;
	}

	public Scheduletask getScheduletask(Integer typeid)
	{
		if (typeid!=null)
		{
			List list=scheduletaskDAO.findByBacktype(typeid);
			if (list!=null && list.size()>0)
			{
				return (Scheduletask)list.get(0);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	public Map<String,Object> findByMemberAndNew(Long userId, int i,int type,Integer firstIndex,Integer pageCount) 
	{
		//需要传回权限，因为路径和文档库路径一致，无法采用id对比
		//而文件路径相同，所以采用文件路径进行对比，将路径作为key,权限作为value
		//采用map传回
		Map resultMap = new HashMap<String, Object>();
		Map<String,Integer> permitMap = new HashMap<String, Integer>();
		Map<String,String> creatName = new HashMap<String, String>();
		Map<String,String> megMap = new HashMap<String, String>();
		List<Personshareinfo>  personshareinfos = personshareinfoDAO.findPathByMemberAndNew(userId,i,type,firstIndex,pageCount);
		List<NewPersonshareinfo> newPersonShare = newpersonshareinfoDAO.findNew(userId,i,firstIndex,pageCount);
		List<String> resultList = new ArrayList<String>();
		try
		{
		if(null!=personshareinfos && null==newPersonShare)
		{
			for(Personshareinfo personshareinfo : personshareinfos)
			{
				resultList.add(personshareinfo.getShareFile());
				permitMap.put(personshareinfo.getShareFile(), personshareinfo.getPermit());
				creatName.put(personshareinfo.getShareFile(), personshareinfo.getUserinfoByShareowner().getRealName());
				megMap.put(personshareinfo.getShareFile(), personshareinfo.getShareComment());
			}
		}else if(null==personshareinfos && null!=newPersonShare)
		{
			for(NewPersonshareinfo personshareinfo : newPersonShare)
			{
				resultList.add(personshareinfo.getShareFile());
				permitMap.put(personshareinfo.getShareFile(), personshareinfo.getPermit());
				creatName.put(personshareinfo.getShareFile(), personshareinfo.getUserinfoByShareowner().getRealName());
				megMap.put(personshareinfo.getShareFile(), personshareinfo.getShareComment());
			}
		}else if(null!=personshareinfos && null!=newPersonShare){
			for(int m=0;m<newPersonShare.size();m++)
			{
				boolean isAdd = false;
				for(int n=0;n<personshareinfos.size();n++)
				{
					 if(personshareinfos.get(n).getDate().before(newPersonShare.get(m).getDate()))
					 {
						 Personshareinfo insertShareInfo = new Personshareinfo();
						 BeanUtils.copyProperties(newPersonShare.get(m), insertShareInfo);
						 personshareinfos.add(n, insertShareInfo);
						 isAdd = true;
						 break;
					 }
				}
				if(!isAdd)
				{
					Personshareinfo insertShareInfo = new Personshareinfo();
					BeanUtils.copyProperties(newPersonShare.get(m), insertShareInfo);
					personshareinfos.add(insertShareInfo);
				}
			}
			for(Personshareinfo personshareinfo : personshareinfos)
			{
				resultList.add(personshareinfo.getShareFile());
				permitMap.put(personshareinfo.getShareFile(), personshareinfo.getPermit());
				creatName.put(personshareinfo.getShareFile(), personshareinfo.getUserinfoByShareowner().getRealName());
				megMap.put(personshareinfo.getShareFile(), personshareinfo.getShareComment());
			}
			
		}
		}catch (Exception e) {
//			Log.debug("get shareFile is Faile!");
			return null;
		}
		resultMap.put("paths", resultList);
		resultMap.put("permits",permitMap);
		resultMap.put("creatorName", creatName);
		resultMap.put("megs", megMap);
		return resultMap;
	}
//	catch (Exception e) 
//	{
////		Log.debug("get shareFile is Faile!");
//		return null;
//	}
		
//		//try
////		{
//			if(null!=personshareinfos && null==newPersonShare)
//			{
//				for(Personshareinfo personshareinfo : personshareinfos)
//				{
//					resultList.add(personshareinfo.getShareFile());
//				}
//				return resultList;
//			}
//			if(null==personshareinfos && null!=newPersonShare)
//			{
//				for(NewPersonshareinfo personshareinfo : newPersonShare)
//				{
//					resultList.add(personshareinfo.getShareFile());
//				}
//				return resultList;
//			}
//			if(null!=personshareinfos && null!=newPersonShare)
//			{
//				List list = new ArrayList();
//				for(Personshareinfo personshareinfo : personshareinfos)
//				{
//					list.add(personshareinfo);
//				}
//				for(NewPersonshareinfo personshareinfo : newPersonShare)
//				{
//					list.add(personshareinfo);
//				}
//				Collections.sort(list,
//                        new PersonshareComparator());
//				
//				for(int j = 0; j < list.size(); j++)
//				{
//					if(list.get(j) instanceof Personshareinfo)
//					{
//						Personshareinfo info = (Personshareinfo)list.get(j);
//						resultList.add(info.getShareFile());
//					}
//					else if (list.get(j) instanceof NewPersonshareinfo)
//					{
//						NewPersonshareinfo info = (NewPersonshareinfo)list.get(j);
//						resultList.add(info.getShareFile());
//					}
//				}
//				return resultList;
//			}
//			
//
//		
//		return null;
//	}
	public List<UsersOrganizations> findGroupByUserId(Long userId) 
	{
		//return groupmemberinfoDAO.findByUserId(userId);
		return structureDAO.findUsersOrganizationsByUserId(userId);
	}
	public List<String> findByGroupAndNew(Long groupId, int i) {
		return groupmembershareinfoDAO.findByGroupAndNew(groupId, i);
	}
	
	//得到全部人对人共享
	public List getAllOtherShare(Long userID,Long ownerid,int start, int limit, String sort, String dir)
    {
    	List<Personshareinfo> fs = personshareinfoDAO.findByShare(userID,ownerid);
    	int len = fs.size();
    	if (len < 1)
    	{
    		return null;
    	}
    	String[] filePath = new String[len];
    	ArrayList shareList = new ArrayList();
    	for(int i=0;i<len;i++)
    	{
//    		filePath[i] = fs.get(i).getShareFile();
    		String sc=fs.get(i).getShareComment();
    		
    		Shareinfo info = new Shareinfo(fs.get(i).getShareFile(),fs.get(i).getPermit(),fs.get(i).getIsNew()
    				,fs.get(i).getUserinfoByShareowner().getRealName(),sc);
    		shareList.add(info);
    	}
    	try
        {

            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List list = jcrService.getFileinfos(null, shareList);
            len = list.size();
            if(sort == null)
            {
            	Collections.sort(list, new FileArrayComparator("lastChanged", -1));
            	list = resetList(list, start, limit);
            }
            else
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
                Collections.sort(list, cp);
                list = list.subList(start, start + limit >= len ? len : start + limit);
            }
            list.add(0, len);
            return new ArrayList<Object>(list);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
	
	//得到筛选人对人共享
		public List getfilterOtherShare(Long userID,List<Long> ownerids,int start, int limit, String sort, String dir,String time,String lTime,String selectedSize)
	    {
	    	List<Personshareinfo> fs = personshareinfoDAO.findFilterByShare(userID,ownerids);
	    	int len = fs.size();
	    	if (len < 1)
	    	{
	    		return null;
	    	}
	    	String[] filePath = new String[len];
	    	ArrayList shareList = new ArrayList();
	    	for(int i=0;i<len;i++)
	    	{
//	    		filePath[i] = fs.get(i).getShareFile();
	    		String sc=fs.get(i).getShareComment();
	    		Shareinfo info = new Shareinfo(fs.get(i).getShareFile(),fs.get(i).getPermit(),fs.get(i).getIsNew()
	    				,fs.get(i).getUserinfoByShareowner().getRealName(),sc);
	    		shareList.add(info);
	    	}
	    	try
	        {

	            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
	                JCRService.NAME);
	            List list = jcrService.getFileinfos(null, shareList);
	            for (int i = 0;i < list.size();i++) {
	            	Fileinfo file = (Fileinfo)list.get(i);
		            for (Personshareinfo psi : fs) {
						if(file.getPathInfo().equals(psi.getShareFile()))
						{
							long sharerId = psi.getUserinfoByShareowner().getId();
							file.setShareTime(psi.getDate());
							file.setFileId(psi.getPersonShareId());
							file.setSharerId(sharerId);
						}
					}
	            }
	            //他人共享中筛选共享时间
	            if(time != null)
	            {
	            	String[] seTime = time.split("/");
			    	long startTime = Long.valueOf(seTime[0]);
			    	long endTime = Long.valueOf(seTime[1]) + 86400000l;
		            for (int i = 0;i < list.size();i++) {
		            	//将他人共享中的更新时间修改为共享时间
		            	Fileinfo file = (Fileinfo)list.get(i);
						for (Personshareinfo psi : fs) {
							if(file.getPathInfo().equals(psi.getShareFile()))
							{
								file.setShareTime(psi.getDate());
							}
						}
		            	long nowTime = file.getShareTime().getTime();
		            	if(nowTime < startTime || nowTime > endTime)
		            	{
		            		list.remove(i);
		            		i--;		            		
		            	}
					}
	            }
	            //他人共享中筛选更新时间
	            if(lTime != null)
	            {
	            	String[] seTime = lTime.split("/");
			    	long startTime = Long.valueOf(seTime[0]);
			    	long endTime = Long.valueOf(seTime[1]) + 86400000l;
		            for (int i = 0;i < list.size();i++) {
		            	//将他人共享中的更新时间修改为共享时间
//						for (Personshareinfo psi : fs) {
//							if(((Fileinfo)fileinfo).getPathInfo().equals(psi.getShareFile()))
//							{
//								((Fileinfo)fileinfo).setLastedTime(psi.getDate());
//							}
//						}
		            	Fileinfo file = (Fileinfo)list.get(i);
		            	long nowTime = (file.getLastedTime() == null ? file.getCreateTime() : file.getLastedTime()).getTime();
		            	if(nowTime < startTime || nowTime > endTime)
		            	{
		            		list.remove(i);
		            		i--;		            		
		            	}
					}
	            }
	            //他人共享中筛选文件大小
	            if(selectedSize != null)
	            {
	            	String[] size = selectedSize.split("/");
	            	double minSize = Double.valueOf(size[0]) * 1024 * 1024;
	            	double maxSize = Double.valueOf(size[1])* 1024 * 1024;
	            	for(int i = 0;i < list.size();i++)
	            	{
	            		Fileinfo file = (Fileinfo)list.get(i);
	            		double fileSize = file.isFold()?0:((double)file.getFileSize());
	            		if(fileSize == 0 || fileSize < minSize || fileSize > maxSize)
	            		{
	            			list.remove(i);
	            			i--;
	            		}
	            	}
	            }
	            len = list.size();
//	            System.out.println(start+"=====sort==================="+sort+"===dir==="+dir+"====="+limit);
//	            System.out.println("userID==================="+userID);
//	            
//	            if (ownerids!=null)
//	            {
//	            	for (int i = 0;i<ownerids.size();i++)
//	            	{
//	            		System.out.println("ownerids==================="+ownerids.get(i));
//	            	}
//	            }
//	            System.out.println("time==================="+time);
//	            System.out.println("lTime==================="+lTime);
//	            System.out.println("selectedSize==================="+selectedSize);
	            if(sort == null || sort.length()==0)
	            {
	            	Collections.sort(list, new FileArrayComparator("sharedTime", -1));//默认按照共享时间先后排序
	            	list = resetList(list, start, limit);
	            }
	            else
	            {
	                int sgn = dir.equals("ASC") ? 1 : -1;
	                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
	                Collections.sort(list, cp);
	                list = list.subList(start, start + limit >= len ? len : start + limit);
	            }
	            list.add(0, len);
	            return new ArrayList<Object>(list);
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	            return null;
	        }
	    }
	
	private List resetList(List list, int start, int limit)
	{
		 ArrayList al = new ArrayList();
         int fileSize = list.size();
         if(list!=null && !list.isEmpty())
         {
 	        int endIndex = start + limit > fileSize ? fileSize : start + limit;
 	        for(int i = start; i < endIndex; i++)
 	        {
 	            al.add(list.get(i));
 	        }
         }
         return al;
	}
	//得到全部人对人共享
	public List getPersonOtherShare(Long userId,Long shareID,int start, int limit, String sort, String dir)
    {
    	List<Personshareinfo> fs = personshareinfoDAO.findByShareOwner(userId, shareID);
    	int len = fs.size();
    	if (len < 1)
    	{
    		return null;
    	}
    	String[] filePath = new String[len];
    	ArrayList shareList = new ArrayList();
    	for(int i=0;i<len;i++)
    	{
//    		filePath[i] = fs.get(i).getShareFile();
    		Shareinfo info = new Shareinfo(fs.get(i).getShareFile(),fs.get(i).getPermit(),fs.get(i).getIsNew()
    				,fs.get(i).getUserinfoByShareowner().getRealName(),fs.get(i).getShareComment());
    		shareList.add(info);
    	}
    	try
        {

            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List list = jcrService.getFileinfos(null, shareList);
            len = list.size();
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
                Collections.sort(list, cp);
                list = list.subList(start, start + limit >= len ? len : start + limit);
            }
            else
            {
            	Collections.sort(list, new FileArrayComparator("lastChanged", -1));
            	 list = resetList(list, start, limit);
            }
            list.add(0, len);
            return new ArrayList<Object>(list);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }	
	
	//单位下的共享
	public List getCompanyOtherShare(Long userId,Long groupId,int start, int limit, String sort, String dir)
	{
		List<Personshareinfo> fs = personshareinfoDAO.findByShare(userId,0L);
		int len = fs.size();
    	if (len < 1)
    	{
    		return null;
    	}
    	List<Personshareinfo> endfs = new ArrayList();
    	for(int i=0;i<len;i++)
    	{
    		
    		Users userinfo = fs.get(i).getUserinfoByShareowner();
    		if (userinfo != null)
    		{
	    		/*List<UsersOrganizations> list = groupmemberinfoDAO.findByUserId(userinfo.getId());
	        	List<Organizations> retList = new ArrayList<Organizations>();
	        	if (list != null)
	        	{
	        		for (UsersOrganizations gi : list)
	        		{
	        			retList.add(gi.getOrganization());
	        		}
	        	}*/
	        	List<Organizations> retList = structureDAO.findOrganizationsByUserId(userinfo.getId());
	        	
	            if (retList != null && retList.size() > 0)
	    		{
	    			Organizations group = retList.get(0);
	    			//单位
	    			if (group != null && group.getParentID().intValue() == 0)
	    			{
	    				if (groupId.intValue() == group.getId().intValue())
		    			{
	    					endfs.add(fs.get(i));
		    			}
	    			}
	    			else
	    			{
		    			Organizations parentGroup = getGroup(group.getParentID());
		    			if (groupId.intValue() == parentGroup.getId().intValue())
		    			{
		    				endfs.add(fs.get(i));
		    			}
	    			}
	    		}
    		}
    	}
		
    	int size = endfs.size();
    	if (size < 1)
    	{
    		return null;
    	}
    	String[] filePath = new String[size];
    	ArrayList shareList = new ArrayList();
    	for(int i=0;i<size;i++)
    	{
//    		filePath[i] = endfs.get(i).getShareFile();
    		Shareinfo info = new Shareinfo(endfs.get(i).getShareFile(),endfs.get(i).getPermit(),endfs.get(i).getIsNew()
    				,endfs.get(i).getUserinfoByShareowner().getRealName(),endfs.get(i).getShareComment());
    		shareList.add(info);
    	}
    	try
        {
            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List list = jcrService.getFileinfos(null, shareList);
            len = list.size();
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
                Collections.sort(list, cp);
                list = list.subList(start, start + limit >= len ? len : start + limit);
            }
            else
            {
            	Collections.sort(list, new FileArrayComparator("lastChanged", -1));
            	list = resetList(list, start, limit);
            }
            list.add(0, len);
            return new ArrayList<Object>(list);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
	}
	
	//部门下的共享
	public List getDepOtherShare(Long userId,Long groupId,int start, int limit, String sort, String dir)
	{
		List<Personshareinfo> fs = personshareinfoDAO.findByShare(userId,0L);
		int len = fs.size();
    	if (len < 1)
    	{
    		return null;
    	}
    	List<Personshareinfo> endfs = new ArrayList();
    	for(int i=0;i<len;i++)
    	{
    		
    		Users userinfo = fs.get(i).getUserinfoByShareowner();
    		if (userinfo != null)
    		{
	    		/*List<UsersOrganizations> list = groupmemberinfoDAO.findByUserId(userinfo.getId());
	        	List<Organizations> retList = new ArrayList<Organizations>();
	        	if (list != null)
	        	{
	        		for (UsersOrganizations gi : list)
	        		{
	        			retList.add(gi.getOrganization());
	        		}
	        	}*/
	        	List<Organizations> retList = structureDAO.findOrganizationsByUserId(userinfo.getId());
	        	
	            if (retList != null && retList.size() > 0)
	    		{
	    			Organizations group = retList.get(0);
	    			//部门
	    			if (groupId.intValue() == group.getId().intValue())
	    			{
						endfs.add(fs.get(i));
	    			}
	    		}
    		}
    	}
		
    	int size = endfs.size();
    	if (size < 1)
    	{
    		return null;
    	}
    	String[] filePath = new String[size];
    	ArrayList shareList = new ArrayList();
    	for(int i=0;i<size;i++)
    	{
//    		filePath[i] = endfs.get(i).getShareFile();
    		Shareinfo info = new Shareinfo(endfs.get(i).getShareFile(),endfs.get(i).getPermit()
    				,endfs.get(i).getIsNew(),endfs.get(i).getUserinfoByShareowner().getRealName(),endfs.get(i).getShareComment());
    		shareList.add(info);
    	}
    	try
        {

            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List list = jcrService.getFileinfos(null, shareList);   
            len = list.size();
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
                Collections.sort(list, cp);
                list = list.subList(start, start + limit >= len ? len : start + limit);
            }
            else
            {
            	Collections.sort(list, new FileArrayComparator("lastChanged", -1));
            	list = resetList(list, start, limit);
            }
            list.add(0, len);
            return new ArrayList<Object>(list);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
	}	
	
	
	//最近共享10条文件
	public List findLatelyFile(Long userID,int start, int limit, String sort, String dir)
	{
		List<Personshareinfo> list = personshareinfoDAO.findLatelyFile(userID);
//		limit = 100;
		int size = list.size();
    	if (size < 1 )
    	{
    		return null;
    	}
    	String[] filePath = new String[size];
    	ArrayList shareList = new ArrayList();
    	for(int i=0;i<size;i++)
    	{
//    		filePath[i] = list.get(i).getShareFile();
    		Shareinfo info = new Shareinfo(list.get(i).getShareFile(),list.get(i).getPermit()
    				,list.get(i).getUserinfoByShareowner().getRealName(),list.get(i).getShareComment());
    		shareList.add(info);
    	}    	
		try
        {

            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List endList = jcrService.getFileinfos(null, shareList);
//            endList = resetList(endList, start, limit);
            int len = endList.size();
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
                Collections.sort(endList, cp);
                endList = endList.subList(start, start + limit >= len ? len : start + limit);
            }
            else
            {
            	Collections.sort(endList, new FileArrayComparator("lastChanged", -1));
            	endList = resetList(endList, start, limit);
            }
            endList.add(0, len);
            return new ArrayList<Object>(endList);
//            return jcrService.getFileinfos(null, shareList);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }        
	}
	
	public List findLatelyUserFile(Long userID,Long shareID,int start, int limit, String sort, String dir)
	{
		List<Personshareinfo> list = personshareinfoDAO.findLatelyUserFile(userID,shareID);
		int size = list.size();
    	if (size < 1)
    	{
    		return null;
    	}
    	String[] filePath = new String[size];
    	ArrayList shareList = new ArrayList();
    	for(int i=0;i<size;i++)
    	{
//    		filePath[i] = list.get(i).getSharePath();
    		Shareinfo info = new Shareinfo(list.get(i).getShareFile(),list.get(i).getPermit()
    				,list.get(i).getUserinfoByShareowner().getRealName(),list.get(i).getShareComment());
    		shareList.add(info);
    	}    	
		try
        {

            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List endList = jcrService.getFileinfos(null, shareList);
//            endList = resetList(endList, start, limit);
            int len = endList.size();
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
                Collections.sort(endList, cp);
                endList = endList.subList(start, start + limit >= len ? len : start + limit);
            }
            else
            {
            	Collections.sort(endList, new FileArrayComparator("lastChanged", -1));
            	endList = resetList(endList, start, limit);
            }
            endList.add(0, len);
            return new ArrayList<Object>(endList);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }        
	}
	
	//最近共享用户
	public List<Users> findLatelyUser(Long userID,int isall)
	{
		List list1 = personshareinfoDAO.getShareinfoBySharer(userID,isall);
		
		List list2 = newpersonshareinfoDAO.getShareinfoBySharer(userID);
		
		ArrayList list = new ArrayList();
		int len1 = list1.size();
		if (list1 != null)
		{
			
			for (int i=0;i<len1;i++)
			{				
				list.add(list1.get(i));
			}			
		}
		
		int len2 = list2.size();
		if (list2 != null)
		{
			
			for (int i=0;i<len2;i++)
			{				
				list.add(list2.get(i));
			}			
		}
		
		/*if (list2 != null)
		{
			int len = list2.size();
			boolean flag = false;
			for (int i=0;i<len;i++)
			{
				Userinfo info = (Userinfo)list2.get(i);
				for (int j=0;j<len1;j++)
				{
					if (info.getUserId().equals(((Userinfo)list1.get(j)).getUserId()))
					{
						flag = true;
						break;
					}
				}
				if(!flag)
				{
					list.add(info);
				}
				else
				{
					flag = false;
				}
			}			
		}*/
		
		Collections.sort(list,new PersonshareComparator());
		
		ArrayList userList = new ArrayList();
		int len = list.size();
		for (int i=0;i<len;i++)
		{				
			if(list.get(i) instanceof Personshareinfo)
			{
				Personshareinfo info = (Personshareinfo)list.get(i);
				userList.add(info.getUserinfoByShareowner());
			}
			else
			{
				NewPersonshareinfo info = (NewPersonshareinfo)list.get(i);
				userList.add(info.getUserinfoByShareowner());
			}
		}
		Set set = new HashSet();       
		List newList = new ArrayList();       
		for (Iterator iter = userList.iterator(); iter.hasNext(); )       
		{       
			Object element = iter.next();       
			if (set.add(element)) 
			{
				newList.add(element);       
			}
		}       
		userList.clear();       
		userList.addAll(newList);       
		return userList;
	}
	
	// 为移动临时拼凑的方法，没有任何意义。
	public Map<String, List> findShareFileByUser(String userName)
	{
		Users shareU = structureDAO.isExistUser(userName);
		Long userID = shareU.getId();
		List<Personshareinfo> list1 = personshareinfoDAO.getShareinfoBySharer(userID,1);		
		List<NewPersonshareinfo> list2 = newpersonshareinfoDAO.getShareinfoBySharer(userID);
		HashMap<String, List> path = new HashMap<String, List>();
		Users user;
		String key;
		List tempP;
		if (list1 != null)
		{
			for (Personshareinfo info : list1)
			{
				user = info.getUserinfoByShareowner();
				key = user.getUserName() + ";" + user.getRealName();				
				tempP = path.get(key);
				if (tempP == null)
				{
					tempP =  new ArrayList();
					tempP.add(0, 0);
					path.put(key, tempP);
				}
				if (info.getIsNew() == 0)
				{
					Integer count = (Integer)tempP.get(0);
					count++;
					tempP.set(0, count);
				}
				tempP.add(info.getShareFile());				
			}
		}
		if (list2 != null)
		{
			for (NewPersonshareinfo info : list2)
			{
				user = info.getUserinfoByShareowner();
				key = user.getUserName() + ";" + user.getRealName();				
				tempP = path.get(key);
				if (tempP == null)
				{
					tempP =  new ArrayList();
					tempP.add(0, 0);
					path.put(key, tempP);
				}
				if (info.getIsNew() == 0)
				{
					Integer count = (Integer)tempP.get(0);
					count++;
					tempP.set(0, count);
				}
				tempP.add(info.getShareFile());				
			}			
		}
		return path;
	}
	
	public Personshareinfo getFolderPersonshareinfo(long shareID, String path)
	{
		Personshareinfo info = personshareinfoDAO.findByShareAndPath(shareID, path);
		return info;
	}
	
	public List getFolderPersonshareinfoList(long shareID, String path)
	{
		List list =  personshareinfoDAO.findByOwnerAndPath(shareID, path);
		if (list != null && list.size() > 0)
		{
			return list;
		}
		return newpersonshareinfoDAO.findByOwnerAndPath(shareID, path);
		
	}
	
	/**
     * 根据Id得到组织,得到最顶层的group
     */
    private Organizations getGroup(long groupID)
    {
    	Organizations info = structureDAO.findOrganizationsById(groupID);
    	if (info == null)
    	{
    		return null;
    	}
    	if (info != null && info.getParentID().intValue() == 0)
    	{
    		return info;
    	}
    	else
    	{
    		return getGroup(info.getParentID());
    	}
    }
	
    
	public void saveNewpersonshareinfoDAO(NewPersonshareinfo share)
	{
		newpersonshareinfoDAO.save(share);
	}
	
	public void setNewShareFolder(long userID, String path,String name)
	{
		setNewShareFolder(userID, path,name,null,false);
	}
	public void setNewShareFolder(long userID, String path,String name,String beforePath,boolean isDel)
	{
		//System.out.println("setNewShareFolder path::"+path+",name:::"+name);
		//System.out.println("setNewShareFolder path::"+path+",name:::"+name);
		List list = getPermitList(userID,path);
    	if (list != null)
    	{
    		int len = list.size();
    		for (int i=0;i<len;i++)
    		{
    			Object info = list.get(i);
    			Users userinfo;
    			Users userinfo2;
    			Organizations groupinfo;
    			int permit;
    			String companyID;
    			String comment;
    			if (info instanceof Personshareinfo)
    			{
//    				info = (Personshareinfo)info;
    				userinfo = ((Personshareinfo)info).getUserinfoBySharerUserId();
    				userinfo2 = ((Personshareinfo)info).getUserinfoByShareowner();
    				groupinfo = ((Personshareinfo)info).getGroupinfoOwner();
    				permit = ((Personshareinfo)info).getPermit();
    				companyID = ((Personshareinfo)info).getCompanyId();
    				comment = ((Personshareinfo)info).getShareComment();
    			}
    			else
    			{
    				userinfo = ((NewPersonshareinfo)info).getUserinfoBySharerUserId();
    				userinfo2 = ((NewPersonshareinfo)info).getUserinfoByShareowner();
    				groupinfo = ((NewPersonshareinfo)info).getGroupinfoOwner();
    				permit = ((NewPersonshareinfo)info).getPermit();
    				companyID = ((NewPersonshareinfo)info).getCompanyId();
    				comment = ((NewPersonshareinfo)info).getShareComment();
//    				info = (NewPersonshareinfo)info;
    			}
    			
    			NewPersonshareinfo share = new NewPersonshareinfo();
    			 boolean isFolder = true;
    			if(name.indexOf('.') == -1)
    			{
        		share.setIsFolder(1);
    			}
    			else
    			{
    				share.setIsFolder(0);
    				isFolder = false;
    			}
                share.setUserinfoBySharerUserId(userinfo);
                share.setUserinfoByShareowner(userinfo2);
                share.setGroupinfoOwner(groupinfo);
                share.setPermit(permit);
                share.setCompanyId(companyID);
                String srcPath = null;                   
                String renamepath = path+"/"+name;
    			share.setShareFile(renamepath);      		
                share.setDate(new Date());
                share.setIsNew(1);
                share.setShareComment(comment); 
                long shareID = userinfo.getId();
                NewPersonshareinfo tempinfo = newpersonshareinfoDAO.findByOtherShareAndPath(shareID, renamepath);//findByOwnerAndPath(userID,renamepath);
                if(tempinfo != null )
                {
//                	newpersonshareinfoDAO.delByFileAndSharer(renamepath, shareID);
                }
                else
                {
                	saveNewpersonshareinfoDAO(share);
                }
               
                //如果是重命名，需要将所有的子文件设置共享属性               
                if(isFolder && isDel)
                {
                	resetAllChild(beforePath,renamepath,beforePath,share,shareID);
                }
//                }
    		}
    	}
	}
	
	private void saveChildPersonShareinfo(String sharePath,NewPersonshareinfo oldshare)
	{
		NewPersonshareinfo share = new NewPersonshareinfo();
		if(sharePath.indexOf('.') == -1)
		{
		share.setIsFolder(1);
		}
		else
		{
			share.setIsFolder(0);
		}
        share.setUserinfoBySharerUserId(oldshare.getUserinfoBySharerUserId());
        share.setUserinfoByShareowner(oldshare.getUserinfoByShareowner());
        share.setGroupinfoOwner(oldshare.getGroupinfoOwner());
        share.setPermit(oldshare.getPermit());
		share.setShareFile(sharePath);      		
        share.setDate(new Date());
        share.setIsNew(1);
        share.setShareComment(oldshare.getShareComment());      
        share.setCompanyId(oldshare.getCompanyId());
        saveNewpersonshareinfoDAO(share);
	}
	
	public void resetAllChild(String srcPath,String targetName,String childPath,NewPersonshareinfo share,long shareID)
    {
    	try
    	{
    		
        	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                    JCRService.NAME);
        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, childPath);
        	//System.out.println("resetAllChild srcPath:"+srcPath+",targetName:"+targetName+",childPath:"+childPath);
        	int len = list.size();
        	for (int j=0;j<len;j++)
        	{
        		String path = list.get(j).getPathInfo();
        		int dx = path.lastIndexOf('/');
        		String aa = path.substring(dx+1);            		
        		int dd = aa.lastIndexOf('.');   
        		String sharePath = path.replace(srcPath, targetName);
        		//System.out.println("setAllChild sharepath:"+sharePath);
        		NewPersonshareinfo tempinfo = newpersonshareinfoDAO.findByOtherShareAndPath(shareID, sharePath);//findByOwnerAndPath(userID,renamepath);
                if(tempinfo != null )
                {
//                	newpersonshareinfoDAO.delByFileAndSharer(renamepath, shareID);
                }
                else
                {
                	saveChildPersonShareinfo(sharePath,share);
                }
        		
                if (dd == -1)
                {
                	resetAllChild(srcPath, targetName, path, share,shareID);
                }
        	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	} 
    }
	
	public void createFolder(long userID, String path, String name)
    {
        try
        {
        	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                    JCRService.NAME);
        	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        	Users user = userService.getUser(userID);
        	Fileinfo fileinfo = jcrService.createFolder(user.getRealName(),path, name);
//        	Personshareinfo info = getPermit(userID,path);        	
//        	Fileinfo fileinfo = getFileinfo(path,userID);
        	 /*
             * 增加为写入文件表信息
             */
            ShareFileTip queryTip=new ShareFileTip();
            ArrayList arr = new ArrayList();
            arr.add(fileinfo);
            queryTip.insertFileinfo(fileinfo,userID);
            
        	setNewShareFolder(userID, path,name,null,false);
        	
//        	if(info != null)
//        	{
//        		NewPersonshareinfo share = new NewPersonshareinfo();
//        		share.setIsFolder(1);
//                share.setUserinfoBySharerUserId(info.getUserinfoBySharerUserId());
//                share.setUserinfoByShareowner(info.getUserinfoByShareowner());
//                share.setGroupinfoOwner(info.getGroupinfoOwner());
//                share.setPermit(info.getPermit());
//                share.setCompanyId(info.getCompanyId());
//                share.setShareFile(path + "/" + name);
//                share.setDate(new Date());
//                share.setIsNew(1);
//                share.setShareComment(info.getShareComment());                
//                saveNewpersonshareinfoDAO(share);
//        	}
        	
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
	
	public void moveFile(String companyID, Long userId, DataHolder srcPath, String targetName,int replace)
	{
		try
        {
			JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			PermissionService permissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
			if (Constant.DOC_PUBLIC.equals(companyID))
			{
				String[] srcName = srcPath.getStringData();
				jcrService.move(null, srcName, targetName,replace);
				messageService.updateSpaceNewMessages(srcName, targetName);
				permissionService.updateFileSystemActionForMove(targetName, srcName);
			}
			else
			{
	        	String[] srcName = srcPath.getStringData();
	        	
	        	int len = srcName.length;   
	        	ArrayList<String[]> nameList = new ArrayList();
	        	String[] tarName = new String[srcName.length];
	            if (userId != null)
	            {
	//            
	                	
	                	for (int i=0;i<len;i++)
	                	{
	                        int dx = srcName[i].lastIndexOf('/');
	                		String aa = srcName[i].substring(dx+1);   
	                		String[] temp = new String[2];//为了文件日志添加
	                		temp[0]=targetName+"/"+aa;
	                		temp[1]= srcName[i];
	                		nameList.add(temp);
	                		tarName[i]=temp[0];
	                		//System.out.println("fileSystemService  path ::"+targetName+",name"+aa+",beforeName::"+srcName[i]);
	                		setNewShareFolder(userId,  targetName,aa,srcName[i],true);
	                		
	                	}              	
	                }
	//            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
	//                    JCRService.NAME);
	        	jcrService.move(null, srcName, targetName,replace);
	        	messageService.updateSpaceNewMessages(srcName, targetName);
	        	permissionService.updateFileSystemActionForMove(targetName, srcName);
	        	ShareFileTip queryTip=new ShareFileTip();
	        	queryTip.updateFileArrForRename(nameList);
	        	 groupshareinfoDAO.delByLikeSignFile(srcName);
	//        	insertFileListLog(tarName,userId,4);
			}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
	
	public void copy(Long userId, DataHolder srcPathHolder, String targetName, int count, List<String> existNames)
	{
		copy( userId, srcPathHolder, targetName, count, existNames, false);
	}
	public void copyAndReplace(Long userId, DataHolder srcPathHolder, String targetName, int count, List<String> existNames)
	{
		copy( userId, srcPathHolder, targetName, count, existNames, true);
	}
	
	public void copy(Long userId, DataHolder srcPathHolder, String targetName, int count, List<String> existNames, boolean replace)
	{
		try
        {
        	String[] srcName = srcPathHolder.getStringData();
//        	insertFileListLog(srcName,userId,5);
        	if(userId != null)
        	{
        	for(int i = 0; i< srcName.length; i++)
        	{
        		boolean rep = replace;
        		String srcPath = srcName[i];
        		int j = 0;
    			boolean result = true;
    			for(int k=1;k<= count;k++)
    			{
    				
	    			String temp = srcPath.substring(srcPath.lastIndexOf("/") + 1, srcPath.length());
	    			String fileName = temp;
	    			if (!rep)
	    			{
	    				while(existNames.contains(fileName))
	    				{
	    					j+=1;
	    					fileName = "复件("+(j)+") "+ temp;
	    				}
    				}
    				JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                            JCRService.NAME);
    				result = jcrService.copy(targetName, srcPath, fileName,rep);
    				
    			    if(!result)break;
    			    rep = false;
    				existNames.add(fileName);   
//    				String beforepath = null;
////    				if(temp.indexOf('.')==-1)
////    				{
////    					beforePath =srcPath.substring(0,srcPath.la)
////    				}
    				setNewShareFolder(userId,  targetName,fileName,srcPath,true);    				
    			}	
        	}
        	}
            

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
	public void copyVersionFile(Users user, DataHolder srcPathHolder, String targetName, int count, List<String> existNames)
	{
		try
        {
        	String[] srcName = srcPathHolder.getStringData();
        	String[] filenames=srcPathHolder.getStringValue();
//        	insertFileListLog(srcName,userId,5);
        	if(user != null && user.getId()!=null)
        	{
        		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
	        	for(int i = 0; i< srcName.length; i++)
	        	{
	        		String srcPath = srcName[i];
	        		if (srcPath.startsWith("jcr:system/jcr:versionStorage"))
	        		{
	        			//复制历史版本
	        			String filename = filenames[i];
	        			if (filename!=null && filename.length()>0)
	        			{
		        			int j = 0;
			    			boolean result = true;
			    			
			    			InputStream in = jcrService.getVersionContent(srcPath,null);
		    				InputStream in2 = jcrService.getVersionContent(srcPath, null);
			    			for(int k=1;k<= count;k++)
			    			{
			    				String fileName = filename;
			    				while(existNames.contains(fileName))
			    				{
			    					j+=1;
			    					fileName = "复件("+(j)+") "+ filename;
			    				}
			    				
			    				Fileinfo fileinfo=createFile(user.getId(),user.getRealName(), targetName, fileName, in,
			    						in2, false, null);
			    				
			    			    if(fileinfo==null)break;
			    				existNames.add(fileName);   
			    				setNewShareFolder(user.getId(),  targetName,fileName,srcPath,true);    				
			    			}
	        			}
	        		}
	        		else
	        		{
		        		int j = 0;
		    			boolean result = true;
		    			for(int k=1;k<= count;k++)
		    			{
		    				String temp = srcPath.substring(srcPath.lastIndexOf("/") + 1, srcPath.length());
		    				String fileName = temp;
		    				while(existNames.contains(fileName))
		    				{
		    					j+=1;
		    					fileName = "复件("+(j)+") "+ temp;
		    				}
		    				result = jcrService.copy(targetName, srcPath, fileName ,false);
		    				
		    			    if(!result)break;
		    				existNames.add(fileName);   
		    				setNewShareFolder(user.getId(),  targetName,fileName,srcPath,true);    				
		    			}
	        		}
	        	}
        	}
            

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
	private List getPermitList(Long userId,String path)
	{
		return getFolderPersonshareinfoList(userId,path);		
	}
	
	private Personshareinfo getPermit(Long userId,String path)
	{
    	Personshareinfo obj = getFolderPersonshareinfo(userId,path);
		if (obj == null)
		{
			int index = path.lastIndexOf('/');
			if (index != -1)
			{
				String aa = path.substring(0, index);
				return getPermit(userId,aa);
			}
			else
			{
				return null;
			}
		}
		return obj;
	}
	
	
	
	private void saveNewPersonShareinfo(Personshareinfo info,String path)
    {
    	 NewPersonshareinfo share = new NewPersonshareinfo();
         share.setUserinfoBySharerUserId(info.getUserinfoBySharerUserId());
         share.setUserinfoByShareowner(info.getUserinfoByShareowner());
         share.setGroupinfoOwner(info.getGroupinfoOwner());
         share.setPermit(info.getPermit());
         share.setCompanyId(info.getCompanyId());
         share.setShareFile(path);
         share.setDate(new Date());
         int dx = path.lastIndexOf('/');
 		String aa = path.substring(dx+1);            		
 		int dd = aa.lastIndexOf('.');            		
         if (dd != -1)
         {
        	 share.setIsFolder(0);
         }     
         else
         {
        	 share.setIsFolder(1);
         }
         share.setIsNew(1);
         saveNewpersonshareinfoDAO(share);
         if(share.getIsFolder().intValue() == 1)
         {
        	 try
         	{
             	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                         JCRService.NAME);
             	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, path);
             	int len = list.size();
             	for (int j=0;j<len;j++)
             	{
             		String path2 = list.get(j).getPathInfo();
             		saveNewPersonShareinfo(info, path2);
             	}
         	}
         	catch(Exception e)
         	{
         	}
         }
    }
	
	public NewPersonshareinfoDAO getNewpersonshareinfoDAO() 
	{
		return newpersonshareinfoDAO;
	}
	
	public void setNewpersonshareinfoDAO(NewPersonshareinfoDAO newpersonshareinfoDAO) 
	{
		this.newpersonshareinfoDAO = newpersonshareinfoDAO;
	}
	
	public void setNewPersonShare(NewPersonshareinfo share) 
	{
		newpersonshareinfoDAO.save(share);
	}
	/*
	 * 以下三个方法是为了解决移动，拷贝，目标文件夹如果有共享属性，共享属性不传递给移动与拷贝文件及夹 added by zzy(non-Javadoc)
	 * @see com.evermore.weboffice.server.service.IFileSystemService#getFolderShareInfo(long, java.lang.String)
	 */
	  public List getFolderShareInfo(long userid,String foldername)
	    {
	    	List list;
	    	if(foldername != null)
	    	{
	    		list =personshareinfoDAO.findByOwnerAndPath(userid,foldername);
	    		return list;
	    	}
	    	if(foldername !=null)
	    	{
	    		list = newpersonshareinfoDAO.findByOwnerAndPath(userid, foldername);
	    	}
	    	return null;
	    }
	    public void setCopyOrMoveFileToShareFolder(long userid,String foldername,String filename,boolean isfolder,List list)
	    {
	    	if(list != null && list.size()>0)
	    	{
	    		Object obj ;
	    		String companyId ;
	    		Organizations groupinfo;
	    		Integer isFolder;
	    		Integer isNew;
	    		Integer permit;
	    		long personshareid;
	    		String sharecomment;
	    		String sharefile;
	    		Users shareownerinfo;
	    		Users shareuserid;
	    		for(int i =0 ;i<list.size();i++)
	    		{
	    			obj = list.get(i);
	    			
	    			if(obj instanceof Personshareinfo)
	    			{
	    				 companyId = ((Personshareinfo) obj).getCompanyId();
	    				 groupinfo =((Personshareinfo) obj).getGroupinfoOwner();
	    				 //isFolder= ((Personshareinfo) obj).getIsFolder();
	    				 
	    				 permit=((Personshareinfo) obj).getPermit();
	    				 personshareid= ((Personshareinfo) obj).getPersonShareId();
	    				 sharecomment= ((Personshareinfo) obj).getShareComment();
	    				 sharefile=((Personshareinfo) obj).getShareFile();
	    				 shareownerinfo=((Personshareinfo) obj).getUserinfoByShareowner();
	    				 shareuserid= ((Personshareinfo) obj).getUserinfoBySharerUserId();
	    				 
	    			}
	    			else
	    			{
	    				companyId = ((NewPersonshareinfo) obj).getCompanyId();
	    				 groupinfo =((NewPersonshareinfo) obj).getGroupinfoOwner();
	    				 //isFolder= ((Personshareinfo) obj).getIsFolder();
	    				 
	    				 permit=((NewPersonshareinfo) obj).getPermit();
	    				 personshareid= ((NewPersonshareinfo) obj).getPersonShareId();
	    				 sharecomment= ((NewPersonshareinfo) obj).getShareComment();
	    				 sharefile=((NewPersonshareinfo) obj).getShareFile();
	    				 shareownerinfo=((NewPersonshareinfo) obj).getUserinfoByShareowner();
	    				 shareuserid= ((NewPersonshareinfo) obj).getUserinfoBySharerUserId();
	    			}
	    			NewPersonshareinfo info  = new NewPersonshareinfo();
	    			info.setCompanyId(companyId);
	    			info.setDate(new Date());
	    			info.setGroupinfoOwner(groupinfo);
	    			info.setIsNew(1);
	    			info.setPermit(permit);
	    			info.setPersonShareId(personshareid);
	    			info.setShareComment(sharecomment);
	    			info.setShareFile(filename);
	    			info.setUserinfoByShareowner(shareownerinfo);
	    			info.setUserinfoBySharerUserId(shareuserid);
	    			if(isfolder)
	    			{
	    				info.setIsFolder(1);
	    			}
	    			 newpersonshareinfoDAO.save(info);
	                    //新共享文件夹，需要将文件夹内的所有文件都设置到newpersonshareinfo内
	                   // int isFolder = info.getIsFolder().intValue();
	    			 if(isfolder)
	    			 {
	    				 tempSave( filename,userid, shareuserid, shareownerinfo,
	    							 groupinfo, companyId, permit, sharecomment);
	    			 }
	    		}
	    	}
	    }
	private void tempSave(String filePath,Long uid,Users shareuserid,Users shareowner,
			Organizations groupinfo,String company,Integer permit,String sharecomment)
	{
		try
		{
    		
        	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                    JCRService.NAME);
        	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, filePath);
        	int len = list.size();
        	for (int j=0;j<len;j++)
        	{
        		 String path = list.get(j).getPathInfo();     		
        		NewPersonshareinfo share = new NewPersonshareinfo();
                share.setUserinfoBySharerUserId(shareuserid);//info.getUserinfoBySharerUserId());
                share.setUserinfoByShareowner(shareowner);//info.getUserinfoByShareowner());
                share.setGroupinfoOwner(groupinfo);//info.getGroupinfoOwner());
                share.setPermit(permit);//info.getPermit());
                share.setCompanyId(company);//info.getCompanyId());
               
                share.setShareFile(path);
                share.setDate(new Date());
                share.setShareComment(sharecomment);//info.getShareComment());
            
                int dx = path.lastIndexOf('/');
        		String aa = path.substring(dx+1);            		
        		int dd = aa.lastIndexOf('.');    
        		
                if (dd != -1)//无子文件了
                {
                	share.setIsFolder(0);
                	personshareinfoDAO.delByFileAndSharer(path, uid);
                	newpersonshareinfoDAO.delByFileAndSharer(path, uid);
                 }
                else
                {
                	share.setIsFolder(1);
                	delNewPersonShareinfo(uid,path);
                	 delPersonShareinfo(uid,path);
                  }
                share.setIsNew(1);
                
                saveNewpersonshareinfoDAO(share); 
                if(share.getIsFolder() == 1)
                {
                	saveNewPersonShareinfo(share);
                }
              }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    	
	}
	/*
	 * 
	 */
	private List getPermit1(Long userId,String path)
	{
    	List obj = getFolderPersonshareinfo1(userId,path);
		if (obj == null)
		{
			int index = path.lastIndexOf('/');
			if (index != -1)
			{
				String aa = path.substring(0, index);
				return getPermit1(userId,aa);
			}
			else
			{
				return null;
			}
		}
		return obj;
	}
	public List getFolderPersonshareinfo1(long shareID, String path)
	{
		List info =personshareinfoDAO.findByOwnerAndPath(shareID, path);
		if(info != null)
		{
			return info;
		}
		else
		{
			return newpersonshareinfoDAO.findByOwnerAndPath(shareID, path);
		}
		
	}

	private void saveNewPersonShareinfo1(Object info,String path)
    { 
		if(info != null)
		{
			Users shareuserid;
			Users shareowner;
			Organizations groupinfo;
			Integer permit;
			String companyid;
			
			if(info instanceof Personshareinfo)
			{
				shareuserid=((Personshareinfo)info).getUserinfoBySharerUserId();
				shareowner=((Personshareinfo)info).getUserinfoByShareowner();
				groupinfo=((Personshareinfo)info).getGroupinfoOwner();
				permit=((Personshareinfo)info).getPermit();
				companyid=((Personshareinfo)info).getCompanyId();
			}
			else
			{
				shareuserid=((NewPersonshareinfo)info).getUserinfoBySharerUserId();
				shareowner=((NewPersonshareinfo)info).getUserinfoByShareowner();
				groupinfo=((NewPersonshareinfo)info).getGroupinfoOwner();
				permit=((NewPersonshareinfo)info).getPermit();
				companyid=((NewPersonshareinfo)info).getCompanyId();
			}
	    	 NewPersonshareinfo share = new NewPersonshareinfo();
	         share.setUserinfoBySharerUserId(shareuserid);
	         share.setUserinfoByShareowner(shareowner);
	         share.setGroupinfoOwner(groupinfo);
	         share.setPermit(permit);
	         share.setCompanyId(companyid);
	         share.setShareFile(path);
	         share.setDate(new Date());
	         int dx = path.lastIndexOf('/');
	 		String aa = path.substring(dx+1);            		
	 		int dd = aa.lastIndexOf('.');            		
	         if (dd != -1)
	         {
	        	 share.setIsFolder(0);
	         }     
	         else
	         {
	        	 share.setIsFolder(1);
	         }
	         share.setIsNew(1);
	         saveNewpersonshareinfoDAO(share);
	         if(share.getIsFolder().intValue() == 1)
	         {
	        	 try
	         	{
	             	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
	                         JCRService.NAME);
	             	ArrayList<Fileinfo> list = jcrService.listFileinfos(null, path);
	             	int len = list.size();
	             	for (int j=0;j<len;j++)
	             	{
	             		String path2 = list.get(j).getPathInfo();
	             		saveNewPersonShareinfo1(info, path2);
	             	}
	         	}
	         	catch(Exception e)
	         	{
	         	}
	         }
		}
    }
	
	
	public List<Personshareinfo> getAllShareinfo(String path)
	{
		return personshareinfoDAO.likeSerch("shareFile",path);
	}
	
	public List<NewPersonshareinfo> getAllNewShareinfo(String path)
	{
		return newpersonshareinfoDAO.likeSerch("shareFile",path);
	}
	  /*
     * (non-Javadoc)
     * 为批量处理的数据而作，这个方法只为不涉及修改操作文件的路径的操作，例如下载，上传，
     * 删除、共享、取消共享，发送等。如果涉及到修改文件路径的批量操作不要用这个方法。
     * @see com.evermore.weboffice.client.remote.FileRemote#insertFileLog(Fileinfo fileinfo,long fileId,long uid)
     */
    public void insertFileListLog(String[] pathinfo,long uid,int optype,FileOperLog fileOperLog,Integer edittag)
    {
//    	ShareFileTip queryTip=new ShareFileTip();
//		queryTip.insertFileListinfo(pathinfo,uid);
//		ArrayList fid = (ArrayList)queryTip.queryFileListID(pathinfo);	
//			queryTip.insertFileListLog(fid,uid,optype);
    	//只有共享文档和公共空间中的文档才记录日志
    	if (edittag!=null)
    	{
    		//更新log状态为2
    		String sql="select model from FileLog as model,Files as f "
    			+" where model.fileId=f.fileId and model.uid=? and f.pathInfo=? order by model.flogId desc ";
    		List<FileLog> list=structureDAO.findAllBySql(0,1,sql, uid,pathinfo[0]);
    		if (list!=null && list.size()>0)
    		{
    			FileLog flog=list.get(0);
    			flog.setOpType(optype);
    			flog.setEndTime(new Date());
    			structureDAO.update(flog);
    		}
    	}
    	else
    	{
		    boolean issave=false;
		    if (pathinfo!=null)
		    {
		    	String cond=" where (1>1 ";
		    	for (int i=0;i<pathinfo.length;i++)
		    	{
		    		if (pathinfo[i].startsWith(FileConstants.GROUP_ROOT))
		    		{
		    			issave=true;
		    			break;
		    		}
		    		else if (pathinfo[i].startsWith(FileConstants.ORG_ROOT))
		    		{
		    			issave=true;
		    			break;
		    		}
		    		else if (pathinfo[i].startsWith(FileConstants.TEAM_ROOT))
		    		{
		    			issave=true;
		    			break;
		    		}
		    		else if (pathinfo[i].startsWith(FileConstants.AUDIT_ROOT))
		    		{
		    			issave=true;
		    			break;
		    		}
		    		else if (optype>2)
		    		{
		    			issave=true;
		    			break;
		    		}
		    		cond+=" or model.shareFile='"+pathinfo[i]+"' ";
		    	}
		    	cond+=")";
		    	if (!issave && cond.length()>12)
			    {
			    	String sql="select count(*) from Personshareinfo as model "+cond;
			    	Long nums=structureDAO.getCountBySql(sql);
			    	if (nums.longValue()>0)
			    	{
			    		issave=true;
			    	}
			    	else {
			    		sql="select count(*) from NewPersonshareinfo as model "+cond;
			    		nums=structureDAO.getCountBySql(sql);
			    		if (nums.longValue()>0)
				    	{
				    		issave=true;
				    	}
					}
			    }
		    	if (uid==1L)//外部用户下载就不需要再检查是否插入文件记录,全部记录是管理员下载的日志
		    	{
		    		String sql ="select distinct fileId from Files  where  pathInfo  = ? ";
					List<Long> backlist = (List<Long>)structureDAO.findAllBySql(sql,pathinfo[0]);
					if (backlist!=null && backlist.size()>0)
					{
						insertFileListLog(backlist,uid,optype,null,null);
					}
					return;
		    	}
			    if (issave)
			    {
		    		List<Long> list=insertFile(pathinfo,uid);
		    		insertFileListLog(list,uid,optype,null,null);
			    }
		    }
	    	if (fileOperLog!=null)
	    	{
	    		LogsUtility.logToFile(fileOperLog.getUserName(),
	                    DateUtils.format(new Date(), "yyyy-MM-dd") + ".log", true, fileOperLog);
	    	}
    	}
    }
    
    public void insertFileListLog(String[] pathinfo,long uid,int optype,String opresult,String opScript,FileOperLog fileOperLog,Integer edittag)
    {
//    	ShareFileTip queryTip=new ShareFileTip();
//		queryTip.insertFileListinfo(pathinfo,uid);
//		ArrayList fid = (ArrayList)queryTip.queryFileListID(pathinfo);	
//			queryTip.insertFileListLog(fid,uid,optype);
    	//只有共享文档和公共空间中的文档才记录日志
    	if (edittag!=null)
    	{
    		//更新log状态为2
    		String sql="select model from FileLog as model,Files as f "
    			+" where model.fileId=f.fileId and model.uid=? and f.pathInfo=? order by model.flogId desc ";
    		List<FileLog> list=structureDAO.findAllBySql(0,1,sql, uid,pathinfo[0]);
    		if (list!=null && list.size()>0)
    		{
    			FileLog flog=list.get(0);
    			flog.setOpType(optype);
    			flog.setEndTime(new Date());
    			structureDAO.update(flog);
    		}
    	}
    	else
    	{
		    boolean issave=false;
		    if (pathinfo!=null)
		    {
		    	String cond=" where (1>1 ";
		    	for (int i=0;i<pathinfo.length;i++)
		    	{
		    		if (pathinfo[i].startsWith(FileConstants.GROUP_ROOT))
		    		{
		    			issave=true;
		    			break;
		    		}
		    		else if (pathinfo[i].startsWith(FileConstants.ORG_ROOT))
		    		{
		    			issave=true;
		    			break;
		    		}
		    		else if (pathinfo[i].startsWith(FileConstants.TEAM_ROOT))
		    		{
		    			issave=true;
		    			break;
		    		}
		    		else if (pathinfo[i].startsWith(FileConstants.AUDIT_ROOT))
		    		{
		    			issave=true;
		    			break;
		    		}
		    		else if (optype>2)
		    		{
		    			issave=true;
		    			break;
		    		}
		    		else if (opresult!=null && opresult.length()>0)
		    		{
		    			issave=true;
		    			break;
		    		}
		    		cond+=" or model.shareFile='"+pathinfo[i]+"' ";
		    	}
		    	cond+=")";
		    	if (!issave && cond.length()>12)
			    {
			    	String sql="select count(*) from Personshareinfo as model "+cond;
			    	Long nums=structureDAO.getCountBySql(sql);
			    	if (nums.longValue()>0)
			    	{
			    		issave=true;
			    	}
			    }
		    	if (uid==1L)//外部用户下载就不需要再检查是否插入文件记录,全部记录是管理员下载的日志
		    	{
		    		String sql ="select distinct fileId from Files  where  pathInfo  = ? ";
					List<Long> backlist = (List<Long>)structureDAO.findAllBySql(sql,pathinfo[0]);
					if (backlist!=null && backlist.size()>0)
					{
						insertFileListLog(backlist,uid,optype,null,null);
					}
					return;
		    	}
			    if (issave)
			    {
		    		List<Long> list=insertFile(pathinfo,uid);
		    		insertFileListLog(list,uid,optype,null,null);
			    }
		    }
	    	if (fileOperLog!=null)
	    	{
	    		LogsUtility.logToFile(fileOperLog.getUserName(),
	                    DateUtils.format(new Date(), "yyyy-MM-dd") + ".log", true, fileOperLog);
	    	}
    	}
    }
    /**
     * 插入文件操作日志
     * @param fileId
     * @param uid
     * @param optype
     */
    public void insertFileListLog(List<Long> fileId,long uid,int optype,String opresult,String opScript)
	{
		if(fileId != null && fileId.size()>0)
		{
			try
			{
				for(int i=0;i<fileId.size();i++)
				{
					FileLog filelog=new FileLog();
					filelog.setFileId(fileId.get(i));
					filelog.setUid(uid);
					filelog.setOpType(optype);
					filelog.setSrcfileid(Integer.parseInt(""+fileId.get(i).longValue()));
					filelog.setOpTime(new Date());
					if (opresult!=null)
					{
						filelog.setOpresult(opresult);
						filelog.setOpscript(opScript);
					}
					structureDAO.save(filelog);
				}
			}
			catch(Exception e)
			{
				
				System.out.println("insertFileListLog====="+e.getMessage());
			}
		}

	}
    /**
     * 插入文件
     * @param pathinfo
     * @param uid
     * @return
     */
    public List<Long> insertFile(String[] pathinfo,long uid)
    {
		if(pathinfo != null && pathinfo.length>0)
		{
			List<Long> backlist=new ArrayList<Long>();
			try
			{
				List<String> list = notExistFile(pathinfo);
				
				if(list != null && list.size()>0)
				{
					for(int i = 0;i<list.size();i++)
					{
						Files file=new Files();
						
						file.setPathInfo(list.get(i));
						file.setUid(uid);
						file.setUserinfo((Users)structureDAO.find(Users.class, uid));
						structureDAO.save(file);
//						backlist.add(file.getFileId());
					} 
				}
				String anySql="(";
				for(int i =0;i<pathinfo.length;i++)
				{
					anySql+="\'"+pathinfo[i]+"\'";
					if(i< (pathinfo.length-1))
					{
						anySql+=",";
					}
				}
				anySql +=")";
				String sql ="select distinct fileId from Files  where  pathInfo  in "+ anySql;
				backlist = (List<Long>)structureDAO.findAllBySql(sql);
				return backlist;
			}
			catch(Exception e)
			{
				System.out.println("insertFile====="+e.getMessage());
			}
		}
		return null;
    }
    /**
     * 过滤不存在的文件
     * @param pathinfo
     * @return
     */
    public  List<String> notExistFile(String[] pathinfo)
	{
		if(pathinfo != null && pathinfo.length>0)
		{
			try
			{
				String anySql="(";
				for(int i =0;i<pathinfo.length;i++)
				{
					anySql+="\'"+pathinfo[i]+"\'";
					if(i< (pathinfo.length-1))
					{
						anySql+=",";
					}
				}
				anySql +=")";
				
				String sql ="select distinct pathInfo from Files  where  pathInfo  in "+ anySql;
				List<String> exitList = (List<String>)structureDAO.findAllBySql(sql);
				List<String> list=new ArrayList<String>();
				for(int i =0;i<pathinfo.length;i++)
				{
					boolean notshad=true;
					if (exitList!=null && exitList.size()>0)
					{
						for (int j=0;j<exitList.size();j++)
						{
							if (pathinfo[i].equals(exitList.get(j)))
							{
								notshad=false;
								break;
							}
						}
					}
					if (notshad)
					{
						list.add(pathinfo[i]);
					}
				}
				return list;

			}
			catch(Exception e)
			{
				System.out.println("notExistFile====="+e.getMessage());
			}
		}
		return null;
	}
    /**
     * 获取文档共享日志
     * @param pathinfo
     * @param uid
     * @return
     */
    public List<String> getFileLog(String  pathinfo,long uid)
	{
		try
		{
			List<String> list = new ArrayList<String>();
			String sql ="select a.pathInfo,b.opTime,c.realName,c.userName,c.realEmail,b.opType,b.opscript,b.opresult from Files as a ,FileLog as b,Users as c where a.fileId = b.fileId and b.uid = c.id and a.pathInfo =?";
			List<Object[]> templist=structureDAO.findAllBySql(sql, pathinfo);
			int len = 8;
			if (templist!=null && templist.size()>0)
			{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for (int i=0;i<templist.size();i++)
				{
					Object[] obj=templist.get(i);
					list.add(i*len+0,(String)obj[0]);
					String dateString = sdf.format((Date)obj[1]);
					if(list.contains(dateString))//一个文档同一时刻不可能有多个操作，一旦出现这种情况，则认为操作无效，不记录日志（为了容错，多线程下载导致多次记录日志）
					{
					    continue;
					}
					list.add(i*len+1,dateString);
					list.add(i*len+2,(String)obj[2]);
					list.add(i*len+3,(String)obj[3]);
					list.add(i*len+4,(String)obj[4]);
					list.add(i*len+5,""+(Integer)obj[5]);
					if ((String)obj[6]==null)
					{
						list.add(i*len+6,"");
					}
					else
					{
						list.add(i*len+6,(String)obj[6]);
					}
					
					if ((String)obj[7]==null)
					{
						list.add(i*len+7,"0");
					}
					else
					{
						list.add(i*len+7,(String)obj[7]);
					}
				}
			}
			return list;
		}
		catch(Exception e)
		{
			System.out.println("getFileLog====="+e.getMessage());
		}
		return null;
	}
    
    /**
     * 插入共享文件的操作日志
     * @param pathinfo 文件路径
     * @param uid
     * @param optype
     */
    public void insertFileLogs(String[] pathinfo,long uid,int optype)
    {
//    	ShareFileTip queryTip=new ShareFileTip();
//		queryTip.insertFileListinfo(pathinfo,uid);
//		ArrayList fid = (ArrayList)queryTip.queryFileListID(pathinfo);	
//			queryTip.insertFileListLog(fid,uid,optype);
//    	ShareFileTip queryTip=new ShareFileTip();
//		queryTip.insertFileListinfo(pathinfo,uid);
//		ArrayList fid = (ArrayList)queryTip.queryFileListID(pathinfo);	
//			queryTip.insertFileListLog(fid,uid,optype);
    	insertFileListLog(pathinfo,uid,optype,null,null);
    }
    /*
     * (non-Javadoc)
     * 为批量处理的数据而作，这个方法只为不涉及修改操作文件的路径的操作，例如下载，上传，
     * 删除、共享、取消共享，发送等。如果涉及到修改文件路径的批量操作不要用这个方法。
     * @see com.evermore.weboffice.client.remote.FileRemote#insertFileLog(Fileinfo fileinfo,long fileId,long uid)
     */
    public void insertFileArrLog(ArrayList pathinfo,long uid,int optype)
    {
    	if(pathinfo!= null && pathinfo.size()>0)
		{
			String[] tempPath = new String[pathinfo.size()];
			for(int i=0;i<pathinfo.size();i++)
			{
				tempPath[i] = (String)pathinfo.get(i);
			}
			insertFileListLog(tempPath,uid,optype,null,null);
		}		
    }
    public void deleteFilelog(String[] pathinfo,long uid)
    {
    	if(pathinfo != null && pathinfo.length > 0)
    	{
    		List<Long> fileIdlist = new ArrayList<Long>();
    		List<Long> flogIdlist = new ArrayList<Long>();
    		for (String path : pathinfo) 
    		{
    			String sql ="select a.fileId,b.flogId from Files as a ,FileLog as b,Users as c where a.fileId = b.fileId and b.uid = c.id and a.pathInfo =?";
    			List<Object[]> templist=structureDAO.findAllBySql(sql, path);
    			if(templist != null && templist.size() > 0)
    			{
    				for (int i = 0; i < templist.size(); i++) {
						Object[] obj = templist.get(i);
						fileIdlist.add((Long)obj[0]);
						flogIdlist.add((Long)obj[1]);
					}
    			}
			}
    		if (fileIdlist.size()>0) {
    			structureDAO.deleteEntityByID(Files.class, "fileId", fileIdlist);
			}
    		if (flogIdlist.size()>0) {
    			structureDAO.deleteEntityByID(FileLog.class, "flogId", flogIdlist);
			}

    	};
    	
    }
    
	public List showVersions(String path, String folder, String context)
	{
		JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
		List<Versioninfo> versionList = jcrService.getAllVersion(path);
		if (versionList != null && !versionList.isEmpty())
		{
			setDownPath(path, versionList, folder, context, jcrService);
		}
		return versionList;
	}

	private void setDownPath(String path, List<Versioninfo> versionList,
			String folder, String context, JCRService jcrService)
	{		
		for (Versioninfo version : versionList)
		{
			String downPath = getRealDownFilePath(path, version.getPath(),
					version.getName(), folder, context, jcrService);
			version.setDownPath(downPath);
		}
	}

	/**
	 * 获得版本文件的下载路径
	 * 
	 * @param path
	 *            版本节点路径
	 * @param fileName
	 *            版本文件名称(可为NUll)
	 * @param path
	 * @param request
	 *            请求信息
	 * @param jcrService
	 *            jcr实例
	 * @return 下载路径
	 */
	private String getRealDownFilePath(String srcPath, String path,
			String fileName, String folder, String context, JCRService jcrService)
	{
		String tempFolder = folder;
		try
		{
			InputStream in = jcrService.getVersionContent(path, null);
			String tempfoldernames = System.currentTimeMillis()+"" ;//+ fileName.substring(fileName.lastIndexOf('.'));
			File filedir = new File(tempFolder + File.separatorChar + tempfoldernames);
			if (!filedir.exists())
			{
				filedir.mkdir();
			}
			//file.mkdir();
			String fileRealName = srcPath.substring(srcPath.lastIndexOf("/")+1,
					srcPath.length());
			String type = fileRealName.substring(fileRealName.lastIndexOf("."));
			File realFile = new File(tempFolder + File.separatorChar + tempfoldernames+File.separatorChar+fileName+type);
			realFile.createNewFile();
			byte[] b = new byte[8 * 1024];
			int len = 0;
			FileOutputStream fo = new FileOutputStream(realFile);
			while ((len = in.read(b)) > 0)
			{
				fo.write(b, 0, len);
			}
			fo.close();
			String httpUrls = context + "/tempfile/" + tempfoldernames + "/" + realFile.getName();
			return httpUrls;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return path;
		}

	}
    
    /**
     * 得用户自己的私有的空间。
     * @param userId
     * @return
     */
    public Spaces getUserSpace(Long userId)
    {
    	return structureDAO.findUserSpaceByUserId(userId);
    }
    
    /**
     * 得的空间。
     * @param spaceUID
     * @return
     */
    public Spaces getSpace(String spaceUID)
    {
    	return structureDAO.findSpaceByUID(spaceUID);
    }
    
    /**
     * 获得用户所在用户自定义组的空间 
     * @param userId
     * @return
     */
    public List<Spaces> getTeamSpacesByUserId(Long userId)
    {
    	return structureDAO.findTeamSpaceByUserId(userId);
    }
    
    /**
     * 获得用户所在组织空间 
     * @param userId
     * @return
     */
    public List<Spaces> getOrgSpacesByUserId(Long userId)
    {
    	return structureDAO.findOrganizationSpaceByUserId(userId);
    }
    
    /**
     * 获得用户所在组的空间 
     * @param userId
     * @return
     */
    public List<Spaces> getGroupSpacesByUserId(Long userId)
    {
    	return structureDAO.findGroupSpaceByUserId(userId);
    }
    
    /**
	 * 得到用户所在公司的空间
	 */
	public Spaces findCompanySpaceByUserId(Long userId)
	{
		return structureDAO.findCompanySpaceByUserId(userId);
	}
	public String getCompanySpaceIdByUserId(Long userId)
	{
		return structureDAO.getCompanySpaceIdByUserId(userId);
	}
    public boolean canDelGroupSpacesByUserId(String spaceUID,Long userId)
    {
    	if (structureDAO.isGroupManager(userId, spaceUID))   // 空间管理员默认一些权限
		{
			return true;
		}
		// 系统级的角色拥有的权限。
    	long retAction = 0;
		List<SpacesActions> retA = permissionDAO.getSpacesActionsBySpaceUID(userId, spaceUID);
		if (retA != null && retA.size() > 0)
		{
			Long action;
			for (SpacesActions tempA : retA)
			{
				action = tempA.getAction();
				if (action != null)
				{
					retAction = FlagUtility.setValue(retAction, action, true);
				}
			}
		}
		if (FlagUtility.isValue(retAction, SpaceConstants.DELETE_SPACE_FLAG))
		{
			return true;
		}
		return false;
    }
    
    public boolean canDelTeamSpacesByUserId(String spaceUID,Long userId)
    {
    	if (structureDAO.isTeamOwner(userId, spaceUID))   // 空间管理员默认一些权限
		{
			return true;
		}
		// 系统级的角色拥有的权限。
    	long retAction = 0;
    	Long space_id = structureDAO.getTeamIdBySpaceUID(spaceUID);
		List<SpacesActions> retA = permissionDAO.getTeamSpacesActionsByUserId(userId,space_id);//getSpacesActionsBySpaceUID(userId, spaceUID);
		if (retA != null && retA.size() > 0)
		{
			Long action;
			for (SpacesActions tempA : retA)
			{
				action = tempA.getAction();
				if (action != null)
				{
					retAction = FlagUtility.setValue(retAction, action, true);
				}
			}
		}
		if (FlagUtility.isValue(retAction, SpaceConstants.DELETE_SPACE_FLAG))
		{
			return true;
		}
		return false;
    }
    /**
     * 获得用户有权删除的，所在组的空间。 
     * @param userId
     * @return
     */
    public List<Spaces> getCanDelGroupSpacesByUserId(Long userId)
    {
    	List<Spaces> temp = structureDAO.findGroupSpaceByUserId(userId);
    	List<Spaces> ret = new ArrayList<Spaces>();
    	
    	String spaceUID;
    	for(Spaces sp : temp)
    	{
    		spaceUID = sp.getSpaceUID();
			if (structureDAO.isGroupManager(userId, spaceUID))   // 空间管理员默认一些权限
			{
				ret.add(sp);
				continue;
			}
			// 系统级的角色拥有的权限。
	    	long retAction = 0;
			List<SpacesActions> retA = permissionDAO.getSpacesActionsBySpaceUID(userId, spaceUID);
			if (retA != null && retA.size() > 0)
			{
				Long action;
				for (SpacesActions tempA : retA)
				{
					action = tempA.getAction();
					if (action != null)
					{
						retAction = FlagUtility.setValue(retAction, action, true);
					}
				}
			}
			if (FlagUtility.isValue(retAction, SpaceConstants.DELETE_SPACE))
			{
				ret.add(sp);
			}
    	}
    	return ret;
    }
    
    /*
	 * 得到用户参与的所有group的空间
	 */
	public List<Spaces> findUserManageGroupSpaceByUserId(Long userId)
	{
		return structureDAO.findUserManageGroupSpaceByUserId(userId);
	}
	
	/*
	 * 得到用户参与的所有team的空间
	 */
	public List<Spaces> findUserManageOrganizationSpaceByUserId(Long userId)
	{
		return structureDAO.findUserManageOrganizationSpaceByUserId(userId);
	}
    /**
     * 获得用户所在组织和组的空间 
     * @param userId
     * @return
     */
    public List<Spaces> getAllSpacesByUserId(Long userId)
    {
    	List<Spaces> list = new ArrayList(); 
    	List<Spaces> orgL = structureDAO.findOrganizationSpaceByUserId(userId);
    	List<Spaces> groL = structureDAO.findGroupSpaceByUserId(userId);
    	if (orgL != null)
    	{
	    	int len = orgL.size();
	    	for (int i=0;i<len;i++)
	    	{
	    		list.add(orgL.get(i));
	    	}
    	}
    	if (groL != null)
    	{
	    	int len = groL.size();
	    	for (int i=0;i<len;i++)
	    	{
	    		list.add(groL.get(i));
	    	}
    	}
    	return list;
    }
	
    /**
     * 得到组织的所有下一级组织的空间，该方法只得下一级的组织，不递归得在下一级的组织
     */
    public List<Spaces> getChildOrganizationByOrgId(Long orgId)
    {
    	return structureDAO.findChildOrganizationSpaceByOrgId(orgId);
    }
    
    /**
     * 得到组的所有下一级组的空间，该方法只得下一级的组，不递归得在下一级的组
     */
    public List<Spaces> getChildGroupSpaceByGroupId(Long goupId)
    {
    	return structureDAO.findChildGroupSpaceByGroupId(goupId);
    }
    
    /**
     * 增加或新增空间公告。如果bulletins中id值为null，则表示新增加的公告，如果id值不为null，则表示
     * 修改已有公告内容。
     * @param bulls
     * @param delIds 需要删除公告，如果该值为null，则表示不需要删除公告。
     * @param spaceUID
     */
    public void addOrUpdateSpaceBulletins(List<Bulletins> bulls, List<Long>delIds, String spaceUID)
    {
    	for (Bulletins bull : bulls)
    	{
	    	if (bull.getId() == null)
	    	{
	    		Spaces space = structureDAO.findSpaceByUID(spaceUID);
	    		bull.setSpace(space);
	    		structureDAO.save(bull);
	    	}
	    	else
	    	{
	    		Bulletins bu = (Bulletins)structureDAO.find(Bulletins.class, bull.getId());
	    		bu.update(bull);
	    		structureDAO.update(bu);
	    	}
    	}
    	if (delIds != null && delIds.size() > 0)
    	{
    		structureDAO.deleteEntityByID(Bulletins.class, "id", delIds);
    	}
    }
    
    /**
     *  得到某个空间中的所有公告。
     * @param spaceUID
     * @param start  如果该值为-1，则表示不需要设置开始值。
     * @param count 如果该值为-1， 则表示获取所有的值。
     * @return
     */
    public List<Bulletins> getSpaceBulletins(String spaceUID, int start, int count)
    {
    	String queryString = " select model from Bulletins model where model.space.spaceUID = ? order by date desc";
    	return structureDAO.findAllBySql(start, count, queryString, spaceUID);
    }
    
    /**
     * 删除公告
     * @param id
     */    
    public void delSpaceBulletings(List<Long> id)
    {
    	structureDAO.deleteEntityByID(Bulletins.class, "id", id);
    }
    
    
    public int insertSign(Long userId,String fileName,String filePath,String content,String signType)
	{
		//boolean hasSign = personshareinfoDAO.hasSign(userId, filePath);
		//if(hasSign)
		//{
		//	return 0;
		//}
		//else
		//{
			personshareinfoDAO.insertSign(userId, fileName, filePath, content,signType);
			return 1;
		//}
		
	}
	public List findAllSign(Long userId,String filePath)
	{
		List<Map> result = new ArrayList<Map>();
		List signList = personshareinfoDAO.findAllSign(userId, filePath,"1");
		SimpleDateFormat spm = new SimpleDateFormat("yyyy年MM月dd HH时mm分ss秒");
		if(null!=signList&&!signList.isEmpty())
		{
			int length = signList.size();
			for(int i=0;i<length;i++)
			{
				SignInfo sign = (SignInfo)signList.get(i);
				Map map = new HashMap();
				map.put("signId", sign.getSignId());
				map.put("realName", sign.getSigner().getRealName());
				map.put("signDate", spm.format(sign.getCreateDate()));
				map.put("content", sign.getContent());
				result.add(map);
			}
		}
		return result;
	}
	
	public List findAllSystemSign(Long userId,String filePath,String fileMD5)
	{
		List<Map> result = new ArrayList<Map>();
		try {
			List signList = personshareinfoDAO.findAllSign(userId, filePath,"0");
			SimpleDateFormat spm = new SimpleDateFormat("yyyy年MM月dd HH时mm分ss秒");
			if(null!=signList&&!signList.isEmpty())
			{
				int length = signList.size();
				for(int i=0;i<length;i++)
				{
					SignInfo sign = (SignInfo)signList.get(i);
					Map map = new HashMap();
					map.put("signId", sign.getSignId());
					map.put("realName", sign.getSigner().getRealName());
					map.put("signDate", spm.format(sign.getCreateDate()));
					
					//验证签名是否通过
					Users user = sign.getSigner();
					String publicKeyStr = user.getPublicKey();
					String signData = sign.getContent();
					boolean ispass = RSACoder.verify(fileMD5.getBytes(), publicKeyStr, RSACoder.DeCodeStr(signData));
					map.put("isVerify", ispass);
					result.add(map);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public List getAllSign(Long userId,String filePath)
	{
		return personshareinfoDAO.getAllSignList(userId, filePath);
	}
	
	
	public String getSignMessage(Long userId,String filePath)
	{
		return personshareinfoDAO.getSignMessage(userId, filePath);
	}
	
	public String findSignMessageById(Long id)
	{
		String result = null;
		SignInfo sign = personshareinfoDAO.findSignById(id);
		if(null!=sign)
		{
			result = sign.getContent();
		}
		return result;
	}
	
	/**
	 * 得到用户所参与的所有项目空间中的最近操作的文件列表信息。
	 * @param userId 需要获得的用户id
	 * @param count 需要获得的数量
	 * @return
	 */
	public List<Fileinfo> getRecentFileinfoInGroupSpace(Long userId, int count)
	{
		List<Fileinfo> ret = new ArrayList<Fileinfo>();
		List<Groups> group = structureDAO.findGroupsByUserId(userId);
		JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
		for (Groups temp : group)
		{			
			List<Fileinfo> files = jcrs.getRecentRWtFile(temp.getSpaceUID(), FileConstants.SAVELIST);
			if (files != null && files.size() > 0)     // 先这样处理。
			{
				ret.addAll(files);
			}
			if (ret.size() >= count)
			{
				break;
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * @param creatorName
	 * @param path
	 * @param name
	 * @param in
	 * @param indata
	 * @param isNewFile
	 * @param oldPath
	 * @return
	 */
	public Fileinfo createFile(long creatorId, final String creatorName,final String path, final String name, final InputStream in,
	        final InputStream indata, final boolean isNewFile, final String oldPath)
	{
		return createFile(creatorId, creatorName, path, name, in,  indata, isNewFile, oldPath, true);
	}
	
	public Fileinfo createFile(long creatorId, final String creatorName,final String path, final String name, final InputStream in,
	        final InputStream indata, final boolean isNewFile, final String oldPath, boolean replace)
	{
		JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
		try
		{
			Fileinfo ret = jcrs.createFile(creatorName, path, name, in, indata, isNewFile, oldPath, replace);
			if (ret != null)
			{
				//handleGroupMessage(creatorId, creatorName, ret, path);
			}			
			return ret;
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
		return null;
	}
	
	public Fileinfo createFiles(long creatorId, final String creatorName,final String path, final String name, final InputStream in,
	        final InputStream indata, final boolean isNewFile, final String oldPath, boolean replace,String[] values)
	{
		JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
		try
		{
			Long time=null;
			if (values!=null)
			{
				try
				{
					time=Long.valueOf(values[1]);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			Fileinfo ret = jcrs.createFile(creatorName, path, name, in, indata, isNewFile, oldPath, replace,time);		
			return ret;
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
		return null;
	}
	/**
	 * 对加入到组空间中文件设置消息
	 * @param info
	 */
	private void handleGroupMessage(long creator, String creatorName, Fileinfo info, String path)
	{
		int index = path.indexOf("/");
		if (index > 0)
		{
			String filePath = info.getPathInfo();
			String spaceUID = path.substring(0, index);
			List<Users> users = structureDAO.findGroupUsersBySpaceUID(spaceUID);			
			if (users != null && users.size() > 0)
			{
				PermissionService pemissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);				
				List<Long> userIds = new ArrayList<Long>();
				for (Users u : users)
				{
					if (u.getId().longValue() != creator)
					{
						// 按需求，还需要权限判断.
						Long per = pemissionService.getFileSystemAction(u.getId(), path, true);
						if (per != null && per.longValue() != 0)
						{
							userIds.add(u.getId());
						}
					}
				}
				if (userIds.size() > 0)
				{
					Messages message = new Messages(MessageCons.ADD_DOC_TYPE, creatorName + "add <" + info.getFileName()
							+ "> file into " + filePath, info.getFileName(),        // + "/" + info.getFileSize() + "/" + info.getPermit(),
							filePath);
					message.setSize(info.getFileSize());
					message.setPermit(-1L);        //(long)info.getPermit());
					MessagesService ms = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
					ms.addMessage(message, creator, userIds);
				}
			}
			
		}		
	}
    
	// 该方法有问题。
    public void addIntoSharePath(String path, String name)
    {
    	List<String> sharePath = personshareinfoDAO.getSharePath(path);    	
        if (sharePath != null && sharePath.size() > 0)
        {
        	//如果向上查共享信息有的话，设置新共享信息
        	List<Personshareinfo> list = personshareinfoDAO.findByProperty("shareFile", sharePath.get(0));;
        	for(int i = 0; i < list.size(); i++)
        	{
        		Personshareinfo shareinfo = list.get(i);
        		NewPersonshareinfo share = new NewPersonshareinfo();
                share.setUserinfoBySharerUserId(shareinfo.getUserinfoBySharerUserId());
                share.setUserinfoByShareowner(shareinfo.getUserinfoByShareowner());
                share.setGroupinfoOwner(shareinfo.getGroupinfoOwner());
                share.setPermit(shareinfo.getPermit());
                share.setShareComment(shareinfo.getShareComment());//added by zzy for sharecomment
                share.setCompanyId(shareinfo.getCompanyId());
                share.setShareFile(path + "/" + name);
                share.setDate(new Date());
                share.setIsFolder(0);
                share.setIsNew(0);
                newpersonshareinfoDAO.save(share);
        	}
        }
    }    
    public void delSignInfo(String[] paths)
    {
    	 groupshareinfoDAO.delByLikeSignFile(paths);
    }
    
    /**
     * 用户提交需要审批的文档。
     * @param userId 用户id
     * @param fileName 文件名。
     * @param in 文件内容流
     * @param indata 文件内容流。
     * @return
     */
    public Fileinfo addAuditFile(long userId, final String fileName, final InputStream in,  final InputStream indata)
	{
    	Users user = structureDAO.findUserById(userId);
    	if (user == null)
    	{
    		return null;
    	}
		JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
		try
		{
			Fileinfo ret = jcrs.addAuditFile(user.getRealName(), user.getSpaceUID(), fileName, in, indata);
			/*if (ret != null)
			{
				handleGroupMessage(creatorId, creatorName, ret, path);
			}	*/		
			return ret;
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
		return null;
	}
    
    /**
     * 用户提交需要审批的文档。
     * @param userId 用户id
     * @param path 需要审核的文件全路径
     * @return
     */
    public Fileinfo addAuditFile(long userId, String path)
	{
    	Users user = structureDAO.findUserById(userId);
    	if (user == null)
    	{
    		return null;
    	}
		JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
		try
		{
			Fileinfo ret = jcrs.addAuditFile(user.getUserName(), path, user.getSpaceUID());
			/*if (ret != null)
			{
				handleGroupMessage(creatorId, creatorName, ret, path);
			}	*/		
			return ret;
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
		return null;
	}
    
    /**
     * 用户审核文档。
     * @param userId 用户id
     * @param path 审核的文件全路径 
     * @return
     */
    public String auditFile(long userId, String path)
	{
    	Users user = structureDAO.findUserById(userId);
    	if (user == null)
    	{
    		return null;
    	}
		JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
		try
		{
			String ret = jcrs.auditFile(user.getUserName(), path);
			/*if (ret != null)
			{
				handleGroupMessage(creatorId, creatorName, ret, path);
			}	*/		
			return ret;
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
		return null;
	}
    public String getLastVersion(String path)
    {
    	JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
    	Versioninfo versioninfo=jcrs.getLastVersion(path);
    	return versioninfo.getName();
    }
    /**
     * 用户提交需要发布的文档。
     * @param path 要发布文件的全路径
     * @return
     */
    public Fileinfo publishFile(String path)
	{
		JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
		try
		{
			Fileinfo ret = jcrs.publishFile(path);
			/*if (ret != null)
			{
				handleGroupMessage(creatorId, creatorName, ret, path);
			}	*/		
			return ret;
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
		return null;
	}
    
    /**
     * 用户提交需要归档的文档。
     * @param path 要归档的文件全路径
     * @return
     */
    public Fileinfo archiveFile(String path)
	{
		JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
		try
		{
			Fileinfo ret = jcrs.archiveFile(path);
			/*if (ret != null)
			{
				handleGroupMessage(creatorId, creatorName, ret, path);
			}	*/		
			return ret;
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
		return null;
	}
    
    /**
     * 删除paths中的所有文件，该删除是直接删除所有文件，不把文件放到回收站中。
     * @param paths
     */
    public void delete(String ... paths)
    {
    	JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
		try
		{
			jcrs.delete(paths);
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
    }
    
    /**
     * 得到用户最近操作的文档，目前需求是要打开及新建保持的文档,包括操作的所有空间中的文档。
     * @param userID 用户的ID值
     * @param type 获取类型。0表示个人空间的，1表示公共空间的
     */
    public ArrayList<Fileinfo> getUserRecentFile(final long userID, int type)
    {
    	Users user = structureDAO.findUserById(userID);
    	if (user == null)
    	{
    		return null;
    	}
    	try
    	{
	    	JCRService jcrs = (JCRService)ApplicationContext.getInstance().getBean("jcrService");
	    	PermissionService pemissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
	    	ArrayList<Fileinfo>  ret = jcrs.getUserRecentFile(user.getSpaceUID(), type);
	    	Long filePermit;
	    	for (Fileinfo temp : ret)
	    	{
	    		filePermit = pemissionService.getFileSystemAction(userID, temp.getPathInfo(), true);
            	if (filePermit == null)
            	{
            		temp.setPermit(0);
            	}
            	else
            	{
            		temp.setPermit(filePermit.intValue());
            	}
	    	}
	    	return ret;
    	}
    	catch(Exception e)
    	{
    		return null;
    	}
    }
    
    /**
     * 获取审批的记录数量
     * @param userId
     * @return
     */
    public long getAuditFileCount(Long userId)
    {
    	int count = 0;
    	PermissionService permission = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
    	long ret = permission.getSystemPermission(userId);
    	boolean toAduit = FlagUtility.isValue(ret, ManagementCons.AUDIT_SEND_FLAG);
		boolean aduit = FlagUtility.isValue(ret, ManagementCons.AUDIT_AUDIT_FLAG);
		boolean managerment = FlagUtility.isValue(ret, ManagementCons.AUDIT_MANGE_FLAG);
		
		if(managerment)
		{
			count += ApprovalUtil.instance().getAllEndDocumentCount(userId, ApproveConstants.APPROVAL_STATUS_END);
		}
		if(toAduit)
		{
			count +=  ApprovalUtil.instance().getMyPaendingPassOrReturnCount(userId);
		}
		if(aduit)
		{
			count +=  ApprovalUtil.instance().getLeaderPaendingCount(userId + "", ApproveConstants.APPROVAL_STATUS_PAENDING);
		}
		// 现在又不要了，注释掉
		//count += FilesHandler.getAllSpaceNewMessagesCountByUserId(userId);   // 呵呵根据需求增加该值
		return count;
    }
    public List getAuditFile(Long userId, int start, int count)
    {
    	return getAuditFile(userId, start, count,0);
    }
    /**
     * 获取审批的文件信息。
     * @param userId
     * @param start
     * @param count
     * @return
     */
    public List getAuditFile(Long userId, int start, int count,int stateid)
    {
    	List result = new ArrayList();
    	PermissionService permission = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
    	JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
    	long ret = permission.getSystemPermission(userId);
    	boolean toAduit = FlagUtility.isValue(ret, ManagementCons.AUDIT_SEND_FLAG);
		boolean aduit = FlagUtility.isValue(ret, ManagementCons.AUDIT_AUDIT_FLAG);
		boolean managerment = FlagUtility.isValue(ret, ManagementCons.AUDIT_MANGE_FLAG);
		
		long tempCount = 0;
		long temp;
		int index = 0;
		if (stateid==1)
		{
			if(aduit && tempCount < count)
			{
				temp = ApprovalUtil.instance().getLeaderPaendingCount(userId + "", ApproveConstants.APPROVAL_STATUS_PAENDING);
				if (start < temp)
				{
					DataHolder dh = ApprovalUtil.instance().getLeaderPaending(userId + "", ApproveConstants.APPROVAL_STATUS_PAENDING, start, (int)(count - tempCount), "", "");
					result.addAll(dh.getFilesData());
					tempCount += result.size();
				}
				start = start >= 0 ? (int)(start + tempCount - temp) : start;
			}
		}
		else
		{
			if(managerment)
			{
				temp = ApprovalUtil.instance().getAllEndDocumentCount(userId, ApproveConstants.APPROVAL_STATUS_END);
				if (start < temp)
				{
					DataHolder dh = ApprovalUtil.instance().getAllEndDocument(userId,ApproveConstants.APPROVAL_STATUS_END, start, count, "", "");
					result.addAll(dh.getFilesData());
					tempCount += result.size();
				}
				start = start >= 0 ? (int)(start + tempCount - temp) : start;
			}
			if(toAduit && tempCount < count)
			{
				temp = ApprovalUtil.instance().getMyPaendingPassOrReturnCount(userId);
				if (start < temp)
				{
					DataHolder dh = ApprovalUtil.instance().getMyPaendingPassOrReturn(userId, start, (int)(count - tempCount), "", "");
					result.addAll(dh.getFilesData());
					tempCount += result.size();
				}
				start = start >= 0 ? (int)(start + tempCount - temp) : start;
			}
			if(aduit && tempCount < count)
			{
				temp = ApprovalUtil.instance().getLeaderPaendingCount(userId + "", ApproveConstants.APPROVAL_STATUS_PAENDING);
				if (start < temp)
				{
					DataHolder dh = ApprovalUtil.instance().getLeaderPaending(userId + "", ApproveConstants.APPROVAL_STATUS_PAENDING, start, (int)(count - tempCount), "", "");
					result.addAll(dh.getFilesData());
					tempCount += result.size();
				}
				start = start >= 0 ? (int)(start + tempCount - temp) : start;
			}
		}
		List retFile = new ArrayList();
		ApproveBean ab;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Object o : result)
		{
			ab = (ApproveBean)o;
			try
			{
				Fileinfo info = jcrService.getFile("", ab.getFilePath());
				/*Fileinfo file = new Fileinfo();
				file.setShowPath(ab.getFilePath());
				file.setPathInfo(ab.getFilePath());
				file.setPrimalPath(ab.getFilePath());
				file.setFileName(ab.getFileName());
				file.setApprovalStatus(ab.getStatus());
				file.setFileStatus(ab.getStatusName());
				file.setAuthor(ab.getUserName());
				file.setPermit(0);			
				file.setFileSize(0L);*/
				//file.setCreateTime(ab.getDate());
				if (info != null && info.getFileName() != null && info.getFileName().length() > 0)
				{
					info.setPrimalPath(ab.getFilePath());
					info.setFileId(ab.getApproveinfoId());
					info.setPermit((int)FileSystemCons.WRITE_FLAG);
					info.setApprovalStatus(ab.getStatus());
					info.setFileStatus(ab.getStatusName());
					//info.setLastedTime(sdf.parse(ab.getDate()));
					info.setShareCommet(ab.getComment());
					info.setTitle(ab.getTitle());
					info.setKeyWords(ab.getStepName());
					info.setSpacePermisson(ab.getUserId());
					info.setAuthor(ab.getUserName());
					info.setFold(ab.getPredefined() != null ? ab.getPredefined() : false);
					info.setShareCount(ab.getNodetype());
					retFile.add(info);
				}
			}
			catch(Exception e)
			{
				LogsUtility.error(e);
			}
		}
		// 现在又不要了，注释掉
		/*int size = retFile.size();
		if (size < count)           // 呵呵根据领导的需求增加该值 
		{
			List<Messages> messages = FilesHandler.getAllSpaceNewMessagesByUserId(userId, start, count - size);
			for (Messages tm : messages)
			{
				try
				{
					Fileinfo info = jcrService.getFile("", tm.getAttach());
					if (info != null && info.getFileName() != null && info.getFileName().length() > 0)
					{
						info.setGroupFile(true);
						info.setPermit(tm.getPermit().intValue());
						retFile.add(info);
					}
				}
				catch(Exception e)
				{
					LogsUtility.error(e);
				}
			}
			
		}*/
    	return retFile;
    }
    
    //----------南京市公安局审批系统开始----------
    public List<Users> findLatelyApproveUser(Long userID, String approveFlag)
    {
        List list = personshareinfoDAO.getShareinfoBySharer(userID, approveFlag);
        Collections.sort(list, new PersonshareComparator());
        ArrayList userList = new ArrayList();
        int len = list.size();
        for (int i = 0; i < len; i++)
        {
            if (list.get(i) instanceof Personshareinfo)
            {
                Personshareinfo info = (Personshareinfo)list.get(i);
                Integer permit = info.getPermit();

                if (((permit.intValue() & MainConstant.ISWRITE) != 0)
                    && ((permit.intValue() & MainConstant.ISAPPROVE) != 0))
                {
                    userList.add(info.getUserinfoByShareowner());
                }

            }
        }
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = userList.iterator(); iter.hasNext();)
        {
            Object element = iter.next();
            if (set.add(element))
            {
                newList.add(element);
            }
        }
        userList.clear();
        userList.addAll(newList);
        return userList;
    }  
    
    public List findLatelyNoApproveFile(Long userID, int start, int limit, String sort, String dir)
    {
        List<Personshareinfo> list = personshareinfoDAO.findLatelyFile(userID, "0");
        // limit = 100;
        int size = list.size();
        if (size < 1)
        {
            return null;
        }
        String[] filePath = new String[size];
        ArrayList shareList = new ArrayList();
        for (int i = 0; i < size; i++)
        {
            // filePath[i] = list.get(i).getShareFile();
            Integer permit = list.get(i).getPermit();

            Shareinfo info = new Shareinfo(list.get(i).getShareFile(), list.get(i).getPermit(),
                list.get(i).getIsNew(), list.get(i).getUserinfoByShareowner().getRealName(), list
                    .get(i).getShareComment(),list.get(i).getUserinfoBySharerUserId().getRealName()
                    ,list.get(i).getApproveResult(),list.get(i).getApproveComment(),list.get(i).getUserinfoBySharerUserId().getRealName()
            );
            if (((permit.intValue() & MainConstant.ISWRITE) != 0)
                && ((permit.intValue() & MainConstant.ISAPPROVE) != 0))
            {
                shareList.add(info);
            }
        }
        try
        {

            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List endList = jcrService.getFileinfos(null, shareList);
            //      Iterator it = endList.iterator();
            // endList = resetList(endList, start, limit);
            int len = endList.size();
            for (int i = 0; i < len; i++)
            {
                if (endList.get(i) instanceof Fileinfo)
                {
                    Fileinfo info = (Fileinfo)endList.get(i);
                    List signList = findAllSign(null, info.getPathInfo());
                    List vList = jcrService.getAllVersion(info.getPathInfo());
//                  List vList = findAllVersion(info.getPathInfo(), 0, 1000);
                  if (signList != null && signList.size() > 0)
                  {
                      info.setIsSign("1");
//                      info.setHasSign("1");
                  }
                  if (vList != null && vList.size() > 0)
                  {
                      info.setHasVersion(true);
//                      info.setHasVersion("1");
                  }
                }
            }
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn, true);
                Collections.sort(endList, cp);
                endList = endList.subList(start, start + limit >= len ? len : start + limit);
            }
            else
            {
                Collections.sort(endList, new FileArrayComparator("lastChanged", -1));
                endList = resetList(endList, start, limit);
            }
            endList.add(0, len);
            List backlist = new ArrayList<Object>(endList);
            return backlist;
            //return result;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public List findLatelyNoApproveUserFile(Long userID, Long shareID, int start, int limit,
        String sort, String dir)
    {
        List<Personshareinfo> list = personshareinfoDAO.findLatelyUserFile(userID, shareID, "0");
        int size = list.size();
        if (size < 1)
        {
            return null;
        }
        String[] filePath = new String[size];
        ArrayList shareList = new ArrayList();
        for (int i = 0; i < size; i++)
        {
            // filePath[i] = list.get(i).getSharePath();
        	Personshareinfo psinfo=list.get(i);
            Shareinfo info = new Shareinfo(psinfo.getShareFile(), psinfo.getPermit(),
            		psinfo.getIsNew(), psinfo.getUserinfoByShareowner().getRealName(), 
            		psinfo.getShareComment(),psinfo.getApprove(),psinfo.getApproveResult(),psinfo.getApproveComment()
            ,psinfo.getUserinfoBySharerUserId().getRealName()		
            );
            Integer permit = list.get(i).getPermit();
            if (((permit.intValue() & MainConstant.ISWRITE) != 0)
                && ((permit.intValue() & MainConstant.ISAPPROVE) != 0))
            {
                shareList.add(info);
            }
        }
        try
        {

            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List endList = jcrService.getFileinfos(null, shareList);

            // endList = resetList(endList, start, limit);
            int len = endList.size();
            for (int i = 0; i < len; i++)
            {
                if (endList.get(i) instanceof Fileinfo)
                {
                    Fileinfo info = (Fileinfo)endList.get(i);
                    List signList = findAllSign(null, info.getPathInfo());
                    List vList = jcrService.getAllVersion(info.getPathInfo());
//                  List vList = findAllVersion(info.getPathInfo(), 0, 1000);
                  if (signList != null && signList.size() > 0)
                  {
                      info.setIsSign("1");
//                      info.setHasSign("1");
                  }
                  if (vList != null && vList.size() > 0)
                  {
                      info.setHasVersion(true);
//                      info.setHasVersion("1");
                  }
                }
            }
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn, true);
                Collections.sort(endList, cp);
                endList = endList.subList(start, start + limit >= len ? len : start + limit);
            }
            else
            {
                Collections.sort(endList, new FileArrayComparator("lastChanged", -1));
                endList = resetList(endList, start, limit);
            }
            //      endList.add(0, len);
            //      return new ArrayList<Object>(endList);
            endList.add(0, len);
            return new ArrayList<Object>(endList);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public List findLatelyApproveFile(Long userID, int start, int limit, String sort, String dir)
    {
        List<Personshareinfo> list = personshareinfoDAO.findLatelyFile(userID, "1");
        // limit = 100;
        int size = list.size();
        if (size < 1)
        {
            return null;
        }
        String[] filePath = new String[size];
        ArrayList shareList = new ArrayList();
        for (int i = 0; i < size; i++)
        {
            // filePath[i] = list.get(i).getShareFile();
            Personshareinfo personshareinfo = list.get(i);
            Shareinfo info = new Shareinfo(personshareinfo.getShareFile(),personshareinfo.getUserinfoBySharerUserId().getRealName(),
                personshareinfo.getPermit(), personshareinfo.getIsNew(), personshareinfo
                    .getUserinfoByShareowner().getRealName(), personshareinfo.getShareComment(),
                personshareinfo.getApproveResult(),personshareinfo.getApproveComment());
            if (personshareinfo.getApproveComment() != null
                && "1".equals(personshareinfo.getApproveResult()))
            {
                info.setShareComment(info.getShareComment() + ";\r\n"
                    + personshareinfo.getApproveComment());
            }
            Integer permit = list.get(i).getPermit();
            if (((permit.intValue() & MainConstant.ISWRITE) != 0)
                && ((permit.intValue() & MainConstant.ISAPPROVE) != 0))
            {
                shareList.add(info);
            }
        }
        try
        {

            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List endList = jcrService.getFileinfos(null, shareList);

            // endList = resetList(endList, start, limit);
            int len = endList.size();
            for (int i = 0; i < len; i++)
            {
                if (endList.get(i) instanceof Fileinfo)
                {
                    Fileinfo info = (Fileinfo)endList.get(i);
                    List signList = findAllSign(null, info.getPathInfo());
                    List vList = jcrService.getAllVersion(info.getPathInfo());
//                  List vList = findAllVersion(info.getPathInfo(), 0, 1000);
                  if (signList != null && signList.size() > 0)
                  {
                      info.setIsSign("1");
//                      info.setHasSign("1");
                  }
                  if (vList != null && vList.size() > 0)
                  {
                      info.setHasVersion(true);
//                      info.setHasVersion("1");
                  }
                }
            }
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn, true);
                Collections.sort(endList, cp);
                endList = endList.subList(start, start + limit >= len ? len : start + limit);
            }
            else
            {
                Collections.sort(endList, new FileArrayComparator("lastChanged", -1));
                endList = resetList(endList, start, limit);
            }
            //      endList.add(0, len);
            //      return new ArrayList<Object>(endList);
            endList.add(0, len);
            return new ArrayList<Object>(endList);
            // return jcrService.getFileinfos(null, shareList);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public List findLatelyApproveUserFile(Long userID, Long shareID, int start, int limit,
        String sort, String dir)
    {
        List<Personshareinfo> list = personshareinfoDAO.findLatelyUserFile(userID, shareID, "1");
        int size = list.size();
        if (size < 1)
        {
            return null;
        }
        String[] filePath = new String[size];
        ArrayList shareList = new ArrayList();
        for (int i = 0; i < size; i++)
        {
            // filePath[i] = list.get(i).getSharePath();
            Shareinfo info = new Shareinfo(list.get(i).getShareFile(),list.get(i).getUserinfoBySharerUserId().getRealName()
            		, list.get(i).getPermit(),
                list.get(i).getIsNew(), list.get(i).getUserinfoByShareowner().getRealName(), list
                    .get(i).getShareComment(),list.get(i).getApproveResult(),list.get(i).getApproveComment());
            Integer permit = list.get(i).getPermit();
            if (((permit.intValue() & MainConstant.ISWRITE) != 0)
                && ((permit.intValue() & MainConstant.ISAPPROVE) != 0))
            {
                shareList.add(info);
            }
        }
        try
        {
            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List endList = jcrService.getFileinfos(null, shareList);

            // endList = resetList(endList, start, limit);
            int len = endList.size();
            for (int i = 0; i < len; i++)
            {
                if (endList.get(i) instanceof Fileinfo)
                {
                    Fileinfo info = (Fileinfo)endList.get(i);
                    List signList = findAllSign(null, info.getPathInfo());
                    List vList = jcrService.getAllVersion(info.getPathInfo());
//                    List vList = findAllVersion(info.getPathInfo(), 0, 1000);
                    if (signList != null && signList.size() > 0)
                    {
                        info.setIsSign("1");
//                        info.setHasSign("1");
                    }
                    if (vList != null && vList.size() > 0)
                    {
                        info.setHasVersion(true);
//                        info.setHasVersion("1");
                    }
                }
            }
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn, true);
                Collections.sort(endList, cp);
                endList = endList.subList(start, start + limit >= len ? len : start + limit);
            }
            else
            {
                Collections.sort(endList, new FileArrayComparator("lastChanged", -1));
                endList = resetList(endList, start, limit);
            }
             endList.add(0, len);
             return new ArrayList<Object>(endList);
//            List result = new ArrayList();
//            result.add(len);
//            result.add(endList);
//            return result;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public List findMyLatelyFile(Long userID, String approveFlag, int start, int limit,
        String sort, String dir)
    {
        List<Personshareinfo> list = personshareinfoDAO.findMyLatelyFile(userID, approveFlag);
        // limit = 100;
        int size = list.size();
        if (size < 1)
        {
            return null;
        }
        String[] filePath = new String[size];
        ArrayList shareList = new ArrayList();
        for (int i = 0; i < size; i++)
        {
            // filePath[i] = list.get(i).getShareFile();
            Personshareinfo personshareinfo = list.get(i);
            Shareinfo info = new Shareinfo(personshareinfo.getShareFile(),personshareinfo.getUserinfoBySharerUserId().getRealName(),
                personshareinfo.getPermit(), personshareinfo.getIsNew(), personshareinfo
                    .getUserinfoByShareowner().getRealName(), personshareinfo.getShareComment(),
                personshareinfo.getApproveResult(),personshareinfo.getApproveComment());
            info.setShareToUser(list.get(i).getUserinfoBySharerUserId().getRealName());
            shareList.add(info);
        }
        try
        {

            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            List endList = jcrService.getFileinfos(null, shareList);
            // endList = resetList(endList, start, limit);
            int len = endList.size();
            for (int i = 0; i < len; i++)
            {
                if (endList.get(i) instanceof Fileinfo)
                {
                    Fileinfo info = (Fileinfo)endList.get(i);
                    List signList = findAllSign(null, info.getPathInfo());
                    List vList = jcrService.getAllVersion(info.getPathInfo());
//                  List vList = findAllVersion(info.getPathInfo(), 0, 1000);
                  if (signList != null && signList.size() > 0)
                  {
                      info.setIsSign("1");
//                      info.setHasSign("1");
                  }
                  if (vList != null && vList.size() > 0)
                  {
                      info.setHasVersion(true);
//                      info.setHasVersion("1");
                  }
                }
            }
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn, true);
                Collections.sort(endList, cp);
                endList = endList.subList(start, start + limit >= len ? len : start + limit);
            }
            else
            {
                Collections.sort(endList, new FileArrayComparator("lastChanged", -1));
                endList = resetList(endList, start, limit);
            }
            endList.add(0, len);
            return new ArrayList<Object>(endList);
            // return jcrService.getFileinfos(null, shareList);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean reviewOrBack(Long userID, String[] path, String flag, String approveResult,
        String approveComment)
    {
    	boolean bool = personshareinfoDAO.reviewOrBack(userID, path, flag, approveResult, approveComment);
    	
    	List<Long> userIds = new ArrayList<Long>();
    	MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	Users sendUser = userService.getUser(userID);
    	for (int i = 0; i < path.length; i++) 
    	{    		
    		List<Personshareinfo> info = personshareinfoDAO.findByMemberAndPath(path[i], userID);
    		userIds.clear();
    		userIds.add(info.get(0).getUserinfoByShareowner().getId());
    		String sharePath = path[i];
			  String fileName = "";
		      boolean isFolder = false;
		        if (sharePath != null && sharePath.lastIndexOf("/") >= 0 && sharePath.lastIndexOf(".") >= 0)
		        {
		            int index1 = sharePath.lastIndexOf("/") + 1;
		            int index2 = sharePath.length();
		            fileName = sharePath.substring(index1, index2);
		        }
		        else if (sharePath != null && sharePath.lastIndexOf("/") >= 0)
		        {
		            int index1 = sharePath.lastIndexOf("/") + 1;
		            fileName = sharePath.substring(index1, sharePath.length());
		            isFolder = true;
		        }
		    	Messages message = new Messages();
		        message.setDate(new Date());// 日期
		        message.setType(MessageCons.SHARE_TYPE);// 类型
		        SimpleDateFormat spm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		        
		    	String str = "<font color='#0000FF'>"+sendUser.getRealName() + (flag.equals("1")?"审阅":"反悔")+"了文件<b>'" +fileName+"'</b>," +spm.format(message.getDate())+"</font>";		    	
		    	message.setContent(str);
		    	messageService.sendMessage(message, userID, userIds);
		}
    
  
        return bool;
    }
    
    public boolean getWebActionControl(Long userID, String path)
    {
        List<Personshareinfo> list = personshareinfoDAO.findAmend(userID, path);
        if (list != null && list.size() > 0)
        {
            Integer permit;
            for (Personshareinfo info : list)
            {
                permit = info.getPermit();
                if ((permit.intValue() & MainConstant.ISAMEND) != 0)
                {
                    return true;
                }
            }
        }
        //审批
        int idx2 = path.indexOf("system_audit_root");
		if (idx2 > -1)
		{
	        int permit=SignUtil.instance().getPermit(path, (Users)structureDAO.find(Users.class, userID));
			if (permit==1)//可写权限，待处理的文件
			{
				return true;
			}
		}
        return false;
    }
    //----------南京市公安局审批系统结束----------
    /**
     * 获取可下载的文件地址
     */
    public List<String> insertPathUrl(String[] paths,Users users,String serverurl)
    {
    	try
    	{
    		List<String> taglist=new ArrayList<String>();
    		List<Long> list=insertFile(paths,users.getId());//获取文件编号
    		//根据编号更新下载变量
    		if (list!=null && list.size()>0)
    		{
    			for (int i=0;i<list.size();i++)
    			{
	    			String copyurl=PasswordEncryptor.encrypt(String.valueOf(System.currentTimeMillis()));
	    			Files files=(Files)structureDAO.find(Files.class, list.get(i));
	    			if (files.getCopyurl()!=null)
	    			{
	    				taglist.add(serverurl+"static/downloadService?"+ServletConst.COPYURL+"="+files.getCopyurl());//已有了就不要再更新
	    			}
	    			else
	    			{
		    			files.setCopyurl(copyurl);
		    			structureDAO.update(files);
		    			taglist.add(serverurl+"static/downloadService?"+ServletConst.COPYURL+"="+copyurl);
	    			}
    			}
    		}
    		return taglist;
		}
		catch(Exception e)
		{
			System.out.println("getPathUrl====="+e.getMessage());
		}
		return null;
    }
    public void updatePathUrl(String[] paths)
    {
    	if (paths!=null)
    	{
	    	for (int i=0;i<paths.length;i++)
	    	{
	    		structureDAO.excute("update Files set copyurl=null where pathInfo=?",paths[i]);
	    	}
    	}
    }
    /**
     * 获取URL对应的文件地址路径,新方法下载
     * @param copyurl
     * @return
     */
    public List<String> getCopyPathsNew(String copyurl)
    {
    	List<String> list=structureDAO.findAllBySql("select a.pathInfo from Files as a where a.copyurl=?", copyurl);
    	if (list!=null && list.size()>0)
    	{
//    		return list.get(0);
    		return list;
    	}
    	return null;
    }
    public String getCopyPaths(String copyurl)
    {
    	List<String> list=structureDAO.findAllBySql("select a.pathInfo from Files as a where a.copyurl=?", copyurl);
    	if (list!=null && list.size()>0)
    	{
    		return list.get(0);
    	}
    	return null;
    }
    public void shareNewFile(String[] paths,Users user,Fileinfo info)
    {
    	for (String path : paths) {
    		if("user".equals(path.substring(0, path.indexOf("_"))))
    		{
    			if(isShare(paths) != 100)
    			{
    				List<Personshareinfo> fPersonshareinfo = new ArrayList<Personshareinfo>();
    				fPersonshareinfo = getshareInfo(path);
    				String tempPath = info.getPathInfo();
    				Integer sharePermit = fPersonshareinfo.get(0).getPermit();
    				String shareComment = fPersonshareinfo.get(0).getShareComment();
    				Personshareinfo personshareinfo = new Personshareinfo();
    				personshareinfo.setCompanyId("public");
    				personshareinfo.setDate(new Date());
    				int dx = tempPath.lastIndexOf('/');
					String aa = tempPath.substring(dx + 1);
					int dd = aa.lastIndexOf('.');
					if (dd == -1)
					{
						personshareinfo.setIsFolder(1);
					}
					else
					{
						personshareinfo.setIsFolder(0);
					}
    				personshareinfo.setIsNew(0);
    				personshareinfo.setUserinfoByShareowner(user);
    				personshareinfo.setShareFile(info.getPathInfo());
    				personshareinfo
    						.setUserinfoBySharerUserId(fPersonshareinfo.get(0).getUserinfoBySharerUserId());
    				personshareinfo.setPermit(sharePermit);
    				personshareinfo.setShareComment(shareComment);
    				savePersonshareinfo(personshareinfo);
    			}
    		}
		}
    	
    }
    
    public void copyVersionFiles(Users user, DataHolder srcPathHolder, String targetName, int count,
            List<String> existNames)
    {
    	copyVersionFile(user, srcPathHolder, targetName, count, existNames);
        String[] paths = srcPathHolder.getStringData();

        for (String path : paths)
        {
            FileOperLog fileOperLog = new FileOperLog("", path, user.getUserName(),
            		user.getRealName(), "", "复制", "");
            LogsUtility.logToFile(fileOperLog.getUserName(),
                DateUtils.format(new Date(), "yyyy-MM-dd") + ".log", true, fileOperLog);
            //LogUtility.log(logDir, fileOperLog);
        }
    }
    
    public Fileinfo[] getFileList(String companyID, String userID, String path)
    {
        try
        {
        	JCRService fileService = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
            List list = fileService.listPageFileinfos(userID, path, 0, 1000);
            if (null != list && !list.isEmpty())
                list.remove(0);
            ArrayList<Fileinfo> fileinfoList = new ArrayList<Fileinfo>(list);
            if (list != null)
            {
                return fileinfoList.toArray(new Fileinfo[list.size()]);
            }
            return null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new Fileinfo[]{};
    }
    
    public void copyFiles(Long userId, DataHolder srcPathHolder, String targetName, int count,
            List<String> existNames)
    {
    	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
        copy(userId, srcPathHolder, targetName, count, existNames);
        String[] paths = srcPathHolder.getStringData();
        Users userInfo = userService.getUser(userId);
        for (String path : paths)
        {
            FileOperLog fileOperLog = new FileOperLog("", path, userInfo.getUserName(),
                userInfo.getRealName(), "", "复制", "");
            LogsUtility.logToFile(fileOperLog.getUserName(),
                DateUtils.format(new Date(), "yyyy-MM-dd") + ".log", true, fileOperLog);
            //LogUtility.log(logDir, fileOperLog);
        }
    }
    
    public void moveFiles(String companyID, Long userId, DataHolder srcPath, String targetName,int replace)
    {
        try
        {
        	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
            //System.out.println("moveFile srcPath::"+srcPath+", targetname：：："+targetName);
            if (Constant.DOC_PUBLIC.equals(companyID))
            {
                String[] srcName = srcPath.getStringData();
                //userService.deleteAllPower(srcName);
            }
            moveFile(companyID, userId, srcPath, targetName,replace);
            Users userInfo = userService.getUser(userId);
            String[] paths = srcPath.getStringData();
            for (String path : paths)
            {
                FileOperLog fileOperLog = new FileOperLog("", path, userInfo.getUserName(),
                    userInfo.getRealName(), "", "移动", "");
                LogsUtility.logToFile(fileOperLog.getUserName(),
                    DateUtils.format(new Date(), "yyyy-MM-dd") + ".log", true, fileOperLog);
                //LogUtility.log(logDir, fileOperLog);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void saveMessageInfo(MessageInfo messageinfo){
    	if(messageinfo != null){
    		messageinfoDAO.save(messageinfo);
    	}
    }
    
    public void deleteMessage(Long messageId){
    	messageinfoDAO.deleteMessage(messageId);
    }
    
    public HashMap<String, Object> getReceiverMessageInfo(String mobile, int start, int count, String sort, String dir){
    	HashMap<String, Object> result = new HashMap<String, Object>();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	List rmi = messageinfoDAO.getMessageInfoByReceiver(mobile);
    	UserService userservice = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	if(rmi != null){
    		result.put("fileListSize", rmi.size());
    		List<HashMap<String, Object>> fileList = new ArrayList<HashMap<String,Object>>();
    		for(Object object : rmi){
    			HashMap<String, Object> messagemap = new HashMap<String, Object>();
    			MessageInfo message = (MessageInfo)object;
    			messagemap.put("title", message.getContent());//content
    			ArrayList<String> titleList = new ArrayList<String>();
    			Users sender = message.getSendUsers();
    			titleList.add(sender.getRealName());
    			messagemap.put("files", titleList);
    			//messagemap.put("receiver",message.getReceiver());
    			messagemap.put("sendtime",sdf.format(message.getDate()));
    			messagemap.put("messageId", message.getId());
    			fileList.add(messagemap);
    		}
    		if(sort==null){
    			FileArrayComparator cp = new FileArrayComparator("sendtime", -1);
    			Collections.sort(fileList, cp);
    			fileList = resetList(fileList, start, count);
    		}else if(sort != null && dir != null){
    			int sgn = dir.equals("ASC") ? 1 : -1;
    			FileArrayComparator cp = new FileArrayComparator(sort,sgn);
    			Collections.sort(fileList, cp);
    			fileList = resetList(fileList, start, count);
    		}
    		result.put("fileList", fileList);
    		return result;
    	}
    	return null;
    }
    
    public HashMap<String, Object> getSenderMessageInfo(Long senderId, int start, int count, String sort, String dir){
    	HashMap<String, Object> result = new HashMap<String, Object>();
    	//List smi = messageinfoDAO.getMessageInfoBySender(senderId,start,count,sort,dir,true);
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	List smi = messageinfoDAO.getMessageInfoBySender(senderId);
    	UserService userservice = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
    	if(smi != null){
    		result.put("fileListSize", smi.size());
    		List<HashMap<String, Object>> fileList = new ArrayList<HashMap<String,Object>>();
    		for(Object object : smi){
    			HashMap<String, Object> messagemap = new HashMap<String, Object>();
    			MessageInfo message = (MessageInfo)object;
    			messagemap.put("title", message.getContent());//content
    			ArrayList<String> titleList = new ArrayList<String>();
    			String receiverMobile = message.getReceiver();
    			Users receiver = userservice.getUserByMobile(receiverMobile);
    			if(receiver!=null){
    				titleList.add(receiver.getRealName());
    			}else{
    				titleList.add(receiverMobile);
    			}
    			messagemap.put("files", titleList);
    			//messagemap.put("receiver",message.getReceiver());
    			messagemap.put("sendtime",sdf.format(message.getDate()));
    			messagemap.put("messageId", message.getId());
    			fileList.add(messagemap);
    		}
    		if(sort==null){
    			FileArrayComparator cp = new FileArrayComparator("sendtime", -1);
    			Collections.sort(fileList, cp);
    			fileList = resetList(fileList, start, count);
    		}else if(sort != null && dir != null){
    			int sgn = dir.equals("ASC") ? 1 : -1;
    			FileArrayComparator cp = new FileArrayComparator(sort,sgn);
    			Collections.sort(fileList, cp);
    			fileList = resetList(fileList, start, count);
    		}
    		result.put("fileList", fileList);
    		return result;
    	}
    	return null;
    }
    
    /*
     * 无锡信电局，设置审阅信息
     */
    public void setReviewInfo(ReviewInfo reviewInfo,List addList)
    {
    	if(reviewInfo != null)
    	{
        	reviewinfoDAO.save(reviewInfo);	
	    	ArrayList<ReviewFilesInfo> rfis = new ArrayList<ReviewFilesInfo>();
	    	for (int i = 0;i < addList.size();i++) {
	    		String[] info = ((String)addList.get(i)).split("-");
	    		ReviewFilesInfo rfi = new ReviewFilesInfo();
	    		Long reviewer = Long.valueOf(info[0]);
	    		int permit = Integer.valueOf(info[1]);
				Users user = structureDAO.findUserById(reviewer);
				rfi.setDocumentPath(reviewInfo.getOldPath());
				rfi.setReviewinfo(reviewInfo);
				rfi.setFileName(reviewInfo.getFileName());
				rfi.setReviewer(user);
				rfi.setPermit(permit);
				rfi.setState(0);
				rfi.setSendDate(reviewInfo.getSendDate());
				rfis.add(rfi);
			}
	    	if(rfis != null && rfis.size() > 0)
	    	{
	        	reviewinfoDAO.saveAll(rfis);
	        	for (int i = 0;i < addList.size();i++) {
	        		sendReviewMessage(reviewInfo.getSender().getId(), addList, reviewInfo.getOldPath(),reviewInfo.getFileName()); 
				}
	    	}
    	}
    }
    
    public Long getAllReviewFilesnums(long userid,int type)
    {
    	Long nums=0L;
    	if(type == 0)
    	{
    		nums = reviewinfoDAO.getReviewInfosOfSendNumsById(userid);
    	}else if(type == 1)
    	{
    		nums = reviewinfoDAO.getReviewInfosOfFiledNumsById(userid);
    	}else if(type == 2)
    	{
    		nums = reviewinfoDAO.getReviewInfosOfTodoNumsById(userid);
    	}else if(type == 3)
    	{
    		nums = reviewinfoDAO.getReviewInfosOfDoneNumsById(userid);
    	}
    	return nums;
    }
    /*
     * 无锡信电局，获取该用户所有的送阅文件
     */
    public HashMap<String, Object> getAllReviewFiles(long userid,int start,int limit,String order,String sort,int type)
    {
    	JCRService jcr = (JCRService)ApplicationContext.getInstance().getBean(JCRService.NAME);
    	List ris = null;
    	if(type == 0)
    	{
    		ris = reviewinfoDAO.getReviewInfosOfSendById(userid);
    	}else if(type == 1)
    	{
    		ris = reviewinfoDAO.getReviewInfosOfFiledById(userid);
    	}else if(type == 2)
    	{
    		ris = reviewinfoDAO.getReviewInfosOfTodoById(userid);
    	}else if(type == 3)
    	{
    		ris = reviewinfoDAO.getReviewInfosOfDoneById(userid);
    	}
		HashMap<String, Object> result = new HashMap<String, Object>();
		int len = ris.size();
    	result.put("fileListSize", len);
    	ArrayList<String> paths = new ArrayList<String>();   
    	List<HashMap<String, Object>> fileList = new ArrayList<HashMap<String,Object>>();
    	try 
    	{
    		if(ris != null && ris.size() > 0)
    		{
    			if(type == 0 || type ==1)
    			{
    				for (Object ri : ris) {
            			paths.add(((ReviewInfo)ri).getOldPath()+","+((ReviewInfo)ri).getId());
            		}
    			}else
    			{
    				for (Object ri : ris) {
            			paths.add(((ReviewFilesInfo)ri).getDocumentPath()+",");
            		}
    			}
    			
//    			String[] pathInfo = new String[ris.size()];
//    	    	List list = jcr.getReviewFileinfos(null,null, (String[])paths.toArray(pathInfo));     	    	
    	    	if(type == 0)
    	    	{
	    			for (Object object : ris)
	    			{    	 
		    			HashMap<String, Object> file = new HashMap<String, Object>();
	    				ReviewInfo reviewInfo = (ReviewInfo)object;
    					file.put("id", reviewInfo.getId());
    					String name=reviewInfo.getFileName();//实际文档的名称
    					if (reviewInfo.getOldPath().indexOf("/")>=0)
    					{
    						name=reviewInfo.getOldPath().substring(reviewInfo.getOldPath().lastIndexOf("/")+1);
    					}
						file.put("name", name);
						file.put("displayPath", reviewInfo.getFileName());//显示的文件名称
						file.put("path", reviewInfo.getOldPath());
						file.put("sendTime", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss",reviewInfo.getSendDate()));
//        						file.put("size", FilesOpeHandler.fileSizeOperation(((Fileinfo)list.get(i)).getFileSize()));
						file.put("comment", reviewInfo.getComment());
						fileList.add(file);
	    			}
    	    	}
    	    	else if(type == 1)
    	    	{
    	    		for (Object object : ris) {
        				HashMap<String, Object> file = new HashMap<String, Object>();
    	    			ReviewInfo reviewInfo = (ReviewInfo)object;
						file.put("id", reviewInfo.getId());
						String name=reviewInfo.getFileName();//实际文档的名称
    					if (reviewInfo.getOldPath().indexOf("/")>=0)
    					{
    						name=reviewInfo.getOldPath().substring(reviewInfo.getOldPath().lastIndexOf("/")+1);
    					}
						file.put("name", name);
						file.put("displayPath", reviewInfo.getFileName());
						file.put("path", reviewInfo.getOldPath());
						file.put("sendTime", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss",reviewInfo.getSendDate()));
						file.put("reviewTime", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss",reviewInfo.getReviewDate()));
						file.put("comment",reviewInfo.getComment());
						
						
//        						file.put("size", FilesOpeHandler.fileSizeOperation(((Fileinfo)list.get(i)).getFileSize()));
						fileList.add(file);
    				}
    	    	}
    	    	else if(type == 2)
    	    	{
    	    		for (Object object : ris) {
        				HashMap<String, Object> file = new HashMap<String, Object>();
    	    			ReviewFilesInfo reviewFilesInfo = (ReviewFilesInfo)object;
						Users sender = reviewFilesInfo.getReviewinfo().getSender();
						if(reviewFilesInfo.getPermit() != null && reviewFilesInfo.getPermit() > 0)
						{
							int p = reviewFilesInfo.getPermit();
							String permit = "读写";
							boolean isDown = (p & MainConstant.ISDOWN)!= 0;
							boolean isRevise = (p & MainConstant.ISAMENT)!=0;
							if(isRevise)
							{
								permit += ",修订";
							}
							if(isDown)
							{
								permit += ",下载";
							}
							file.put("permit", permit);
						}
						file.put("sender", sender.getRealName());
						file.put("id", reviewFilesInfo.getId());
						String name=reviewFilesInfo.getFileName();//实际文档的名称
    					if (reviewFilesInfo.getDocumentPath().indexOf("/")>=0)
    					{
    						name=reviewFilesInfo.getDocumentPath().substring(reviewFilesInfo.getDocumentPath().lastIndexOf("/")+1);
    					}
						file.put("name", name);
						file.put("displayPath", reviewFilesInfo.getFileName());
						file.put("path", reviewFilesInfo.getDocumentPath());
						file.put("sendTime", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss",reviewFilesInfo.getSendDate()));
						file.put("comment",reviewFilesInfo.getReviewinfo().getComment());
						file.put("senderusername",reviewFilesInfo.getReviewinfo().getSender().getRealName());
//        						file.put("size", FilesOpeHandler.fileSizeOperation(((Fileinfo)list.get(i)).getFileSize()));
						fileList.add(file);
    				}
    	    	}
    	    	else if(type == 3)
    	    	{
    	    		for (Object object : ris) {
        				HashMap<String, Object> file = new HashMap<String, Object>();
    	    			ReviewFilesInfo reviewFilesInfo = (ReviewFilesInfo)object;
						Users sender = reviewFilesInfo.getReviewinfo().getSender();
						file.put("sender", sender.getRealName());
						file.put("id", reviewFilesInfo.getId());
						String name=reviewFilesInfo.getFileName();//实际文档的名称
    					if (reviewFilesInfo.getDocumentPath().indexOf("/")>=0)
    					{
    						name=reviewFilesInfo.getDocumentPath().substring(reviewFilesInfo.getDocumentPath().lastIndexOf("/")+1);
    					}
						file.put("name", name);
						file.put("displayPath", reviewFilesInfo.getFileName());
						file.put("path", reviewFilesInfo.getDocumentPath());
						file.put("sendTime", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss",reviewFilesInfo.getSendDate()));
						file.put("reviewTime", apps.transmanager.weboffice.util.DateUtils
								.ftmDateToString("yyyy-MM-dd HH:mm:ss",reviewFilesInfo.getReviewDate()));
						file.put("result",reviewFilesInfo.getResult());
						file.put("comment", reviewFilesInfo.getReviewinfo().getComment());
						file.put("senderusername",reviewFilesInfo.getReviewinfo().getSender().getRealName());
//        						file.put("size", FilesOpeHandler.fileSizeOperation(((Fileinfo)list.get(i)).getFileSize()));
						fileList.add(file);
    				}
    	    	}
    		}
    		if(sort == null)
            {
            	Collections.sort(fileList, new FileArrayComparator("sendTime", -1));
            	fileList = resetList(fileList, start, limit);
            }
            else
            {
                int sgn = order.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
                Collections.sort(fileList, cp);//这种排序是事后排序影响性能，应该在查询那一层做排序，李孟生看到进行更改一下
                fileList = fileList.subList(start, start + limit >= len ? len : start + limit);
            }
	    	result.put("fileList", fileList);
	    	return result;
    	}    	
    	catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /*
     * 无锡信电局,单个审阅文件的审阅详情
     */
    public ArrayList getReviewDetailsById(Long id,boolean isOwn)
    {
    	int type = 0;
    	if(!isOwn)
    	{
    		type = 1;
    	}
    	List<ReviewFilesInfo> list = reviewinfoDAO.getReviewFilesInfosById(id,type);
    	ArrayList result = new ArrayList();
    	String fileName = "";
    	SimpleDateFormat spm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	for (ReviewFilesInfo rfi : list) {
			HashMap<String, Object> temp = new HashMap<String, Object>();
			temp.put("sender", rfi.getReviewinfo().getSender().getRealName());
			Users reviewer = rfi.getReviewer();
			temp.put("reviewer", reviewer.getRealName());
//			if(rfi.getPermit() != null && rfi.getPermit() > 0)
//			{
//				int p = rfi.getPermit();
//				String permit = "读写";
//				boolean isDown = (p & MainConstant.ISDOWN)!= 0;
//				boolean isRevise = (p & MainConstant.ISAMENT)!=0;
//				if(isRevise)
//				{
//					permit += ",修订";
//				}
//				if(isDown)
//				{
//					permit += ",下载";
//				}
//				temp.put("permit", permit);
//			}
			Date reviewDate = rfi.getReviewDate();
			if (reviewDate==null)
			{
				reviewDate=new Date();
			}
			temp.put("permit", spm.format(reviewDate));
			temp.put("state", (rfi.getState() == 0 ? "在审":"已审"));
			Integer resultsString = rfi.getResult();
			temp.put("result", resultsString != null ?(resultsString == 0?"同意":"不同意"):"");
			temp.put("reviewComment", rfi.getReviewCommet() != null ? rfi.getReviewCommet():"");
			temp.put("version", rfi.getVersion() != null ? rfi.getVersion():"");
			fileName = rfi.getVersion() != null ?rfi.getFileName():"";
			result.add(temp);			
		}
    	result.add(0, fileName);
    	return result;
    }
    
    /*
     * 无锡信电局，审阅文件
     */
    public void reviewFile(long id,int agree,String reviewComment)
    {
    	List<ReviewFilesInfo> rfiList = reviewinfoDAO.getReviewFilesInfosById(id,2);
    	List<ReviewInfo> riList = reviewinfoDAO.getReviewInfosById(id);
		JCRService jcrService = (JCRService) ApplicationContext
				.getInstance().getBean(JCRService.NAME);
    	Date reviewTime = new Date();
    	for (int i = 0;i < rfiList.size();i++) {
    		rfiList.get(i).setState(1);
    		rfiList.get(i).setReviewDate(reviewTime);
    		rfiList.get(i).setResult(agree);
    		rfiList.get(i).setReviewCommet(reviewComment);
			for(int j = 0;j < riList.size();j++)
			{
				if(riList.get(j).getId().equals(rfiList.get(i).getReviewinfo().getId()))
				{
					String version = jcrService.createReviewVersion(rfiList.get(i).getDocumentPath(), null, null, null);
					rfiList.get(i).setVersion(version);
					rfiList.get(i).setVersionsId(version.substring(version.lastIndexOf("/")+1, version.length()));
					if(riList.get(j).getCount() > 0)
					{
						riList.get(j).setCount(riList.get(j).getCount()-1);						
					}
					if(riList.get(j).getCount() == 0)
					{
						riList.get(j).setReviewDate(rfiList.get(i).getReviewDate());
					}
				}
			}
		}
    	reviewinfoDAO.updateAll(rfiList);
    	reviewinfoDAO.updateAll(riList);
    }
    
    /*
     * 无锡信电局，反悔已审阅的文档
     */
    public void goBackReviewFile(long id)
    {
    	List<ReviewFilesInfo> rfiList = reviewinfoDAO.getReviewFilesInfosById(id,2);
    	List<ReviewInfo> riList = reviewinfoDAO.getReviewInfosById(id);
//    	JCRService jcrService = (JCRService) ApplicationContext
//				.getInstance().getBean(JCRService.NAME);
    	for (int i = 0;i < rfiList.size();i++) {
    		rfiList.get(i).setState(0);
    		rfiList.get(i).setReviewDate(null);
    		rfiList.get(i).setResult(null);
    		List<String> versionName = new ArrayList<String>();
    		versionName.add(rfiList.get(i).getVersionsId());
//    		jcrService.delReviewVersions(rfiList.get(i).getDocumentPath(),versionName );
    		rfiList.get(i).setVersion(null);
    		rfiList.get(i).setVersionsId(null);
			for(int j = 0;j < riList.size();j++)
			{
				if(riList.get(j).getId().equals(rfiList.get(i).getReviewinfo().getId()))
				{
					riList.get(j).setCount(riList.get(j).getCount()+1);
					riList.get(j).setReviewDate(null);
				}
			}
		}
    	reviewinfoDAO.updateAll(rfiList);
    	reviewinfoDAO.updateAll(riList);
    }
    
    /*
     * 获取打开文档的审阅权限
     */
    public int getPermitOfReviewFile(String path,long userId)
    {
    	return reviewinfoDAO.getPermit(path,userId);
    }
    
    /*
     * 被共享人对共享文档添加备注
     */
    public void setCommentOfsharedfile(Users user,String comment,long personshareId)
    {
    	Personshareinfo psi = (Personshareinfo)personshareinfoDAO.find(Personshareinfo.class, personshareId);
    	Date date = new Date();
    	psi.setComment(comment);
    	psi.setUserinfoBySharerUserId(user);
    	psi.setAddDate(date);
    	personshareinfoDAO.update(psi);
    }
    
    /*
     * 被共享人对其备注进行修改
     */
    public boolean modifyCommentOfsharedfile(String comment,long commentId,Users user)
    {
    	Personshareinfo sc = (Personshareinfo)personshareinfoDAO.find(Personshareinfo.class, commentId);	
    	if(sc != null)
    	{
    		Users shareder = sc.getUserinfoBySharerUserId();
    		String userName = user.getUserName();
    		if(userName.equals(shareder.getUserName()))
    		{
    			sc.setAddDate(new Date());
        		sc.setComment(comment);
            	personshareinfoDAO.update(sc); 
            	return true;
    		}
    	} 
    	return false;
    }
    
    /*
     * 删除选中的备注
     */
    public boolean delCommentOfsharedfile(long commentId,Users user)
    {
    	Personshareinfo sc = (Personshareinfo)personshareinfoDAO.find(Personshareinfo.class, commentId);	
    	if(sc != null)
    	{
    		Users shareder = sc.getUserinfoBySharerUserId();
    		String userName = user.getUserName();
    		if(userName.equals(shareder.getUserName()))
    		{
    			sc.setAddDate(null);
    			sc.setComment(null);
            	personshareinfoDAO.update(sc); 
            	return true;
    		}
    	} 
    	return false;
    }
    
    /*
     * 获取选中的备注的具体备注信息
     */
    public String getOneComment(long commentId)
    {
    	Personshareinfo ps = (Personshareinfo)personshareinfoDAO.find(Personshareinfo.class, commentId);
    	String comment = null;
    	if(ps != null)
    	{
    		comment = ps.getComment();
    	}
    	return comment;
    }
    
    /*
     * 获取共享文档的备注
     */
    public ArrayList getCommentOfSharedfile(long ownerId,long userId,String path)
    {
    	List<Personshareinfo> pss = personshareinfoDAO.findByOwnerAndPath(ownerId, path);
    	ArrayList list = new ArrayList();
    	boolean isExist = false;    	
    	for (Personshareinfo ps : pss) {
    		boolean isHas = false;
			HashMap<String, Object> commentInfo = new HashMap<String, Object>();
			commentInfo.put("comment", ps.getComment()!=null?ps.getComment():"");
			if(ps.getComment() != null)
			{
				commentInfo.put("sharedUser", ps.getUserinfoBySharerUserId().getUserName());
				commentInfo.put("sharedRealname", ps.getUserinfoBySharerUserId().getRealName());
				commentInfo.put("commentId", ps.getPersonShareId());
				long sharederId = ps.getUserinfoBySharerUserId().getId();
				if(sharederId == userId)
				{
					isExist = true;
					commentInfo.put("isOwn", true);
				}else
				{
					commentInfo.put("isOwn", false);
				}
				try {
					commentInfo.put("date", apps.transmanager.weboffice.util.DateUtils
							.ftmDateToString("yyyy-MM-dd HH:mm:ss",ps.getAddDate()));
				}
				catch(Exception e)
		        {
		            e.printStackTrace();
		        }
				list.add(commentInfo);				
			}		
		}
    	list.add(0, isExist);
    	return list;
    }
    
    public Long getUserMessageInfoCount(Long userId,Date start,Date end){
    	return this.messageinfoDAO.getUserMessageInfoCount(userId, start, end);
    }
    
    // 以下为北京执法大队的需求临时增加
    
    /**
     * 报送
     * @param userN
     * @param path
     * @param fileName
     */
    public boolean doBS(final String[] paths, final String[] shows, final Long userID)
    {
    	Users user = structureDAO.findUserById(userID);
    	
    	if (user == null)
    	{
    		return false;
    	}
    	PermissionService permissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
    	long per = user != null ? permissionService.getSystemPermission(user.getId()) : 0;
		if (!FlagUtility.isValue(per, ManagementCons.COLLECT_BAOSONG_FLAG))//判断是否有报送权限
		{
			return false;
		}
    	Organizations org=structureDAO.getRootOrg(userID);//获取跟单位
    	if (org==null)
    	{
    		return false;
    	}
    	int size = paths.length;
    	ArrayList<CollectEditSend> list = new ArrayList<CollectEditSend>();
    	for (int i = 0; i < size; i++)
    	{
    		CollectEditSend cs = new CollectEditSend(user.getId(),user.getUserName(), user.getRealName(), paths[i], shows[i], "",org,user.getCompany());
    		list.add(cs);
    	}
		sendCollectDAO.saveAll(list);
		return true;
    }
    /**
     * 判断是否是报送文件 
     * @param filepath
     * @return
     */
    public boolean isBSFile(final String filepath)
    {
    	List<CollectEditSend> list = sendCollectDAO.getBSByFile(filepath);
		if (list!=null && list.size()>0)
		{
			return true;
		}
		else
		{
			return false;
		}
    }
    /**
     * 取消报送
     */
    public boolean doBSC(final String[] paths, final String[] shows, final Long userID)
    {
    	Users user = structureDAO.findUserById(userID);
    	if (user == null)
    	{
    		return false;
    	}
    	int size = paths.length;
    	for (int i = 0; i < size; i++)
    	{
    		sendCollectDAO.delSendCollectEdit(paths[i], user.getUserName());
    	}
    	return true;
    }
    
    /**
     * 采编
     */
    public boolean doCB(final String[] paths, final String[] shows, final Long userID)
    {
    	Users user = structureDAO.findUserById(userID);
    	if (user == null)
    	{
    		return false;
    	}
    	PermissionService permissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
    	long per = user != null ? permissionService.getSystemPermission(user.getId()) : 0;
		if (!FlagUtility.isValue(per, ManagementCons.COLLECT_EDIT_FLAG))//判断是否有采编权限
		{
			return false;
		}
    	Organizations org=structureDAO.getRootOrg(userID);//获取跟单位
    	if (org==null)
    	{
    		return false;
    	}
    	int size = paths.length;
    	ArrayList<CollectEdit> list = new ArrayList<CollectEdit>();
    	for (int i = 0; i < size; i++)
    	{
    		CollectEditSend ces = getSendCollectEdit(paths[i]);
    		if (ces != null)
    		{
	    		CollectEdit cs = new CollectEdit(ces.getId(),user.getId(), user.getUserName(), user.getRealName(), "", shows[i],org,user.getCompany());
	    		list.add(cs);
    		}
    	}
		sendCollectDAO.saveAll(list);
		return true;
    }
    
    /**
     * 得报送采编列表
     * @param userID
     * @param start
     * @param limit
     * @param sort
     * @param dir
     * @return
     */
    public List getBSCB(final Long userID, int start, int limit, String sort, String dir)
    {
    	try
        {
    		Users user = structureDAO.findUserById(userID);
        	if (user == null)
        	{
        		return null;
        	}
        	Organizations org=structureDAO.getRootOrg(user.getId());//获取跟单位
        	if (org==null)
        	{
        		return null;
        	}
        	PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);//FlagUtility.isValue(service.getSystemPermission(userId),  	ManagementCons.COLLECT_EDIT_FLAG)
         	long permission =  service.getSystemPermission(user.getId());
         	boolean flags =  FlagUtility.isValue(permission, ManagementCons.COLLECT_EDIT_FLAG);
         	List<CollectEditSend>  ret;
         	if (flags)    // 获取所有需要采编的文件
         	{
         		ret = sendCollectDAO.getSendCollectEdit(start, limit, sort, dir);
         	}
         	else     // 获取本单位报送的文件
         	{
         		ret = sendCollectDAO.getSendCollectEdit(org.getId(), start, limit, sort, dir);
         	}
         	int length = ret.size();
         	List list = new ArrayList();
         	CollectEditSend temp;
         	SimpleDateFormat spm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
         	for (int i = 0; i < length; i++)
         	{
         		temp = ret.get(i);
         		HashMap<String, Object> files = new HashMap<String, Object>();
         		files.put("name", temp.getFileName());// +
				files.put("folder", false);
				files.put("id", temp.getId());
				files.put("description", temp.getDescription());
				files.put("author", temp.getRealName() != null ? temp.getRealName() : "");
				files.put("orgname", temp.getOrg() != null ? temp.getOrg().getName() : "");
				files.put("path", temp.getFilePath());
				files.put("sendTime", spm.format(temp.getSendTime()));
				list.add(files);
         	}
         	
            if (sort != null)
            {
                int sgn = dir.equals("ASC") ? 1 : -1;
                FileArrayComparator cp = new FileArrayComparator(sort, sgn);
                Collections.sort(list, cp);
                list = list.subList(start, (start + limit) >= length ? length : (start + limit));
            }
            list.add(0, length);
            return new ArrayList<Object>(list);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    	return null;
    }
    
    /**
     * 采编
     * @param userN
     * @sendId 报送id
     * @param path
     * @param fileName
     */
    public void saveCollect(String userN, Long sendId, String path, String fileName, String des)
    {
    	Users user = structureDAO.getUserByName(userN);
    	if (user == null)
    	{
    		return;
    	}
    	Organizations org=structureDAO.getRootOrg(user.getId());//获取跟单位
    	if (org==null)
    	{
    		return ;
    	}
    	CollectEdit ce = new CollectEdit(sendId,user.getId(), user.getUserName(), user.getRealName(), des, fileName,org,user.getCompany());
		sendCollectDAO.save(ce);
    }
    
    /**
	 * 获得某个时间段的所有报送和采编的记录数。
	 * object[0]为报送者，object[1]为报送者真实名，object[2]为报送总数，object[3]为采编总数
	 * @param start
	 * @param end
	 * @return
	 */
	public List getSendCollectEdit(Date start, Date end)
	{
		return sendCollectDAO.getSendCollectEdit(start, end);
	}
    
    // 对比
    public String getPic4SC(Date start, Date end, List<Long> ids)
    {
    	List ret;
    	if (ids == null || ids.size() <= 0)
    	{
    		ret = sendCollectDAO.getSendCollectEdit(start, end);
    	}
    	else
    	{
    		ret = sendCollectDAO.getSendCollectEdit(start, end, ids);
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    	String date = sdf.format(start);
    	date += " - " + sdf.format(end);
    	return drawImage(ret, date, null);
    }
    
    // 环比
    public String getPic4SC(Date[] start, Date[] end, Long orgid)
    {
    	ArrayList list = new ArrayList();
    	ArrayList dates = new ArrayList();
    	int size = start.length;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    	for (int i = 0; i < size; i++)
    	{
    		List ret = sendCollectDAO.getSendCollectEdit(start[i], end[i], orgid);
    		if (ret.size() > 0)
    		{
    			list.addAll(ret);
    			String date = sdf.format(start[i]);
    	    	date += " - " + sdf.format(end[i]);
    	    	dates.add(date);
    		}
    	}
    	return drawImage(list, null, dates);
    }
    
    private String drawImage(List list, String date, List dates)
    {
    	try
    	{
    		int size = list.size();
    		int max = 10;
    		int gap = 40;
    		int yStep = 5;          // y方向的刻度数
    		Object[] temp;
    		for (int i = 0; i < size; i++)
    		{
    			temp = (Object[])list.get(i);
    			max = Math.max(max, ((Long)temp[2]).intValue());
    			max = Math.max(max, ((Long)temp[3]).intValue());
    		}
	    	int width = gap * 4 * (size + 2);     // 总宽度
	    	int step = (max + yStep - 1) / yStep;               // 刻度  
	    	int height = (yStep + 4) * gap;        // 总高度
	    	int ory = height - 2 * gap;
	    	int orx = gap + gap / 2;
	    	BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g = image.createGraphics();
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);        
	        g.setColor(Color.white);
	        g.fillRect(0, 0, width, height);
	        g.setColor(Color.black);
	        Color co = Color.red;      // 采编
	        Color se = Color.blue;     // 报送
	        	        
	        // 画坐标系
	        g.drawLine(orx, ory, width - 5 * gap, ory);    // x轴
	        g.drawLine(orx, ory, orx, gap);                  // y轴
	        for (int i = 0; i <= yStep; i++)
	        {
	        	g.drawString("" + step * i, gap / 2,  ory - gap * i);
	        	g.drawLine(orx - 5, ory - gap * i, width - 5 * gap, ory - gap * i);
	        }
	        g.setColor(co);
	        g.fillRect(width - gap * 3, height / 2, 10, 10);
	        g.drawString("采编数", width - gap * 3 + 15, height / 2 + 10);
	        g.setColor(se);
	        g.fillRect(width - gap * 3, height / 2 + gap, 10, 10);
	        g.drawString("报送", width - gap * 3 + 15, height / 2 + 10 + gap);
	        
	        // 画柱状
	        int tl;
	        for (int i = 0; i < size; i++)
	        {
	        	temp = (Object[])list.get(i);
	        	// 报送
	        	tl = ((Long)temp[2]).intValue();
	        	g.setColor(se);
	        	g.fillRect(orx + i * gap * 4 + gap, ory - tl * gap / step, gap, tl * gap / step);
	        	g.drawString("" + tl, orx + i * gap * 4 + gap + 5, ory - tl * gap / step - 5);
	        	
	        	// 采编
	        	tl = ((Long)temp[3]).intValue();
	        	g.setColor(co);	        	
	        	g.fillRect(orx + i * gap * 4 + 2 * gap, ory - tl * gap / step, gap, tl * gap / step);
	        	g.drawString("" + tl, orx + i * gap * 4 + 2 * gap + 5, ory - tl * gap / step - 5);
	        	
	        	// 时间及单位
	        	g.setColor(Color.black);
	        	g.drawLine(orx + i * gap * 4, ory, orx + i * gap * 4, ory + gap);
	        	if (date != null)
	        	{
	        		g.drawString(date, orx + i * gap * 4 + 5, ory + gap / 2);
	        	}
	        	else
	        	{
	        		g.drawString((String)dates.get(i), orx + i * gap * 4 + 5, ory + gap / 2);
	        	}
	        	g.drawString((String)temp[1], orx + i * gap * 4 + gap, ory + gap);
	        	//g.drawString((String)temp[1], orx + i * gap * 4 + gap, ory + gap / 2 + gap);
	        }
	        
	        g.dispose();
	        image.flush();            
	        String fileName = System.currentTimeMillis() + ".jpg";
	        String ret = "/" + WebConfig.SENDMAIL_FOLDER + "/" + fileName;
	        File file = new File(WebConfig.sendMailPath);
	        if (!file.exists())
	        {
	        	file.mkdirs();
	        }
	    	OutputStream os = new FileOutputStream(WebConfig.sendMailPath + File.separatorChar + fileName);
	    	try
	    	{
	    		ImageIO.write(image, "JPEG", os);
	    		os.flush();
	    	}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				os.close();
				return ret;
			}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return "";
    }
    
    /**
	 * 获取用户报送的记录
	 * @param userId
	 * @param start
	 * @param count
	 * @param sort
	 * @param dir
	 * @return
	 */
	public List<CollectEditSend> getSendCollectEdit(Long orgid, int start, int count, String sort, String dir)
	{
		return sendCollectDAO.getSendCollectEdit(orgid, start, count, sort, dir);
	}
	
	/**
	 * 获取用户报送的记录
	 * @param userId
	 * @param start
	 * @param count
	 * @param sort
	 * @param dir
	 * @return
	 */
	public List<CollectEditSend> getSendCollectEdit(Long orgid, int start, int count, String sort, String dir, Date startD, Date endD)
	{
		return sendCollectDAO.getSendCollectEdit(orgid, start, count, sort, dir, startD, endD);
	}
	
	/**
	 * 获取用户报送被采编的记录
	 * @param userId
	 * @param start
	 * @param count
	 * @param sort
	 * @param dir
	 * @return
	 */
	public List<CollectEdit> getCollectEdit(Long orgid, int start, int count, String sort, String dir)
	{
		return sendCollectDAO.getCollectEdit(orgid, start, count, sort, dir);
	}
	
	/**
	 * 获取用户报送被采编的记录
	 * @param userId
	 * @param start
	 * @param count
	 * @param sort
	 * @param dir
	 * @return
	 */
	public List<CollectEdit> getCollectEdit(Long orgid, int start, int count, String sort, String dir, Date startD, Date endD)
	{
		return sendCollectDAO.getCollectEdit(orgid, start, count, sort, dir, startD, endD);
	}
	
	/**
	 * 获取用户报送的记录
	 * @param path
	 * @return
	 */
	public CollectEditSend getSendCollectEdit(String path)
    {
		return sendCollectDAO.getSendCollectEdit(path);
    }
    /**
     * 保存元数据
     * @param entity 元数据对象列表
     */
    public void saveMetadata(List entity){
    	if(entity != null && entity.size() > 0)
    	{
        	metadatainfoDAO.saveAll(entity);
    	}

    }
    
    /**
     * 查询所有元数据对象
     * 
     * @return 所有元数据对象列表
     */
    public List<EntityMetadata> getAllMetadata(){
    	return metadatainfoDAO.findAll(EntityMetadata.class);
    }
}
