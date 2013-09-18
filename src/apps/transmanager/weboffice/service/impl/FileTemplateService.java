package apps.transmanager.weboffice.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.dao.IDepartmentDAO;
import apps.transmanager.weboffice.dao.ITemplateDAO;
import apps.transmanager.weboffice.dao.ITemplateItemDAO;
import apps.transmanager.weboffice.dao.IUserDAO;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.TemplateItemPo;
import apps.transmanager.weboffice.domain.TemplatePo;
import apps.transmanager.weboffice.service.IFileTemplateService;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.FileSystemService;

@Component
public class FileTemplateService implements IFileTemplateService {
	public static final String NAME = "fileTemplateService";
	
	@Autowired
	private ITemplateDAO templateDAO;
	
	@Autowired
	private IUserDAO userDAO;
	
	@Autowired
	private ITemplateItemDAO templateItemDAO;
	
	@Autowired
	private IDepartmentDAO departmentDAO;
	
	
	private FileSystemService fileSystemService = null;
	@Override
	public Map<Long, String> listFileTemplatePo(long userId) {
		 Map<Long, String> templates=new LinkedHashMap<Long, String>();
		 Organizations org=this.getUserRootOrg(userId);
		 Long orgId=org==null?null:org.getId();
		 List<TemplatePo> templatePos=this.templateDAO.findAll(userId,orgId);
		 if(templatePos.size() <3){
			 Users user=this.userDAO.findById(Users.class.getCanonicalName(), new Long(userId));
			 if(null != org){
				 //创建公司模板
				 if(!this.containTemplate(templatePos, org.getName()+"模板")){
					 this.createCompanyTemplate(user);
				 }
			 }
			 //创建个人模板
//			 if(!this.containTemplate(templatePos, "个人模板")){
//				 this.addUserTemplate(user.getId(), "个人模板");
//			 }
			 
			 templatePos=this.templateDAO.findAll(userId,orgId);
		 }
		 
		 if(!templatePos.isEmpty()){
			 for(TemplatePo po:templatePos){
				 templates.put(po.getId(), po.getName());
			 }
		 }
		 return templates;
		
	}
	
	private boolean containTemplate(List<TemplatePo> templatePos,String templateName){
		for(TemplatePo po:templatePos){
			if(po.getName().equals(templateName)){
				return true;
			}
		}
		return false;
	}
	
	private boolean userIsAdmin(Users user){
		return user.getRole().intValue() == 8;
	}
	
	/**
	 * 创建当前用户和公司的模板
	 * @param user
	 *         当前用户
	 */
	private void createCompanyTemplate(Users user){
		 
		 if(user !=null){
			 /*
			  * 判断用户是不是admin,
			  *         如果是不创建公司分组
			  *            如果不是，创建公司分组
			  */
			 if(!this.userIsAdmin(user)){
				 Organizations root=this.getUserRootOrg(user.getId());
				 if(null != root){
			        this.addCompanyTemplate(root.getId(), root.getName()+"模板");
				 }
			 }
		 }
	}

	
	/*
	 * 
	 * (non-Javadoc)
	 * @see com.yozo.weboffice.service.IFileTemplateService#getTemplateItemPo(java.lang.Long)
	 */
	@Override
	public List<TemplateItemPo> getTemplateItemPo(Long templateId) {
		return this.templateItemDAO.getTemplateItemPo(templateId);
	}

	/**
	 * 添加分组
	 * @param userId
	 *       用户的ID
	 * @param name
	 *         分组名称
	 * @param type
	 *         类型
	 *             0: 用户的
	 *             1： 系统的
	 *             2：公司的
	 * @param orgId
	 *            公司ID，因为信电局公司为部门所以这个是部门ID
	 * @return
	 */
	private long addTemplate(long userId,String name,int type,long orgId) {
		TemplatePo templatePo =new TemplatePo();
		templatePo.setName(name);
		templatePo.setType(type);
		templatePo.setUserId(userId);
		templatePo.setCompanyId(orgId);
		templatePo.setCreateDate(new Date());
		this.templateDAO.saveOrUpdate(templatePo);
		return templatePo.getId();
	}
	
	@Override
	/**
	 * 添加公司分组
	 */
	public long addCompanyTemplate(long orgId, String name) {
		return this.addTemplate(0,name,2,orgId);
	}
	
	/**
	 * 添加用户模板组
	 */
	@Override
	public long addUserTemplate(long userId,String name) {
		return this.addTemplate(userId,name,0,0);
	}

	/**
	 * 添加系统模板组
	 */
	@Override
	public long addSystemTemplate(String name) {
		return this.addTemplate(0,name,1,0);
		
	}

	@Override
	/**
	 * 获取模板组
	 */
	public TemplatePo getTemplate(long templateId) {
		return this.templateDAO.findById(TemplatePo.class.getCanonicalName(), templateId);
	}

	@Override
	public int delTemplate(long templateId) {
		TemplatePo po=this.getTemplate(templateId);
		if(po.getType() == 1){
			return -1;
		}
		this.templateItemDAO.deleteByProperty(TemplateItemPo.class.getCanonicalName(), "template.id", templateId);
		this.templateDAO.deleteById(TemplatePo.class.getCanonicalName(), templateId);
		return 1;
	}

	/**
	 * 
	 * @param user
	 * @param templateId
	 *          为用户当前选中的templateId。
	 * @param basePath
	 * @param templateName
	 * @param type
	 * @param file
	 * @param image
	 * @return
	 * @throws Exception
	 */
	private  List<String []> addTemplateItem(Users user,long templateId,String basePath, String templateName,String upfilename,String upimgname,int type,FileTransfer file,FileTransfer image) throws Exception {
		/*
		String basePath=user.getSpaceUID()+"/tempalte";
		try {
		this.getFileSystemService().createFile(user.getId(), user.getUserName(), basePath, file.getFilename(), file.getInputStream(), file.getInputStream(), true, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		List<String []> data=new ArrayList<String[]>(); 
		
		
		//判断是不是系统模板，系统模板下，不允许，添加用户模板
		TemplatePo template=this.templateDAO.findById(TemplatePo.class.getCanonicalName(), templateId);
		if(null  == template){
			return data;
		}
		
		String uploadUrl ="";
	
		if( type == 0){
			//用户
			uploadUrl = "data/templatefiles/"+user.getSpaceUID();
			template=this.templateDAO.findByUserId(user.getId());
			if(null  == template){
				return data;
			}
		}else if( type == 1){
			//系统
			uploadUrl = "data/templatefiles/system";
		}else if( type == 2){
			//公司
			if (this.getUserRootOrg(user.getId())!=null)
			{
				uploadUrl = "data/templatefiles/"+this.getUserRootOrg(user.getId()).getId();
			}
			else
			{
				uploadUrl = "data/templatefiles/"+user.getCompany().getId();//如果根目录为空就用单位编号代替
			}
			Organizations org=this.getUserRootOrg(user.getId());
			if( null != org){
				template=this.templateDAO.fintByCompanyId(org.getId());
				if(null  == template){
					return data;
				}
			}else{
				return data;
			}
		}
		if(uploadUrl.equals("")){
			return data;
		}
		//uploadUrl = "data/templatefiles/"+user.getSpaceUID();
		//String name = new String(file.getFilename().getBytes(),"utf-8");
		//name=name.replace(" ", "");
		String filetype="";
		String fileName="";
		if (upfilename!=null && upfilename.length()>0)
		{
//			System.out.println(upfilename+"====file.getFilename()===="+file.getFilename());
			filetype=getFileType(upfilename);
			fileName=new String(this.UploadFile(file, basePath, uploadUrl, filetype).getBytes(),"utf-8");
//			System.out.println(uploadUrl+"===="+fileName);
		}
		String imageFiletype="";
		String imagefileName="";
		if (upimgname!=null && upimgname.length()>0)
		{
//			System.out.println(upimgname+"====image.getFilename()===="+image.getFilename());
			imageFiletype=getFileType(upimgname);
			imagefileName=new String(this.UploadFile(image, basePath, uploadUrl, imageFiletype).getBytes(),"utf-8");
//			System.out.println(uploadUrl+"==2222=="+imagefileName);
		}
		TemplateItemPo po=new TemplateItemPo();
		po.setCreateDate(new Date());
		po.setName(templateName.replace(filetype, ""));
		po.setImagePath(uploadUrl+"/"+imagefileName);
		po.setTempatePath(uploadUrl+"/"+fileName);
		po.setType(type);
		po.setTemplate(template);
		this.templateItemDAO.saveOrUpdate(po);
		String d[]=new String [5];
		d[0]=po.getId().toString();
		d[1]=po.getName();
		d[2]=po.getImagePath();
		d[3]=po.getTempatePath();
		d[4]=template.getId()+"";
		data.add(d);
		return data;
	}

	@Override
	public TemplateItemPo getTemplateItem(long templateItemId) {
		return this.templateItemDAO.findById(TemplateItemPo.class.getCanonicalName(), templateItemId);
	}

	@Override
	public int delTemplateItem(long templateItemId) {
		TemplateItemPo po=this.templateItemDAO.findById(TemplateItemPo.class.getCanonicalName(), templateItemId);
		if( po.getType() == 1){
			return -1;
		}
		this.templateItemDAO.deleteById(TemplateItemPo.class.getCanonicalName(), templateItemId);
		return 0;
	}
	
	public FileSystemService getFileSystemService(){
		if(null == fileSystemService){
			fileSystemService=(FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		}
		return fileSystemService;
	}


	@Override
	/**
	 * 重命名模板分组
	 */
	public int renameTemplate(long templateId, String name) {
		TemplatePo templatePo =this.getTemplate(templateId);
		if(null ==templatePo){
			return -1;
		}
		
		templatePo.setName(name);
		this.templateDAO.saveOrUpdate(templatePo);
		return 1;
	}
	
	private Organizations getUserRootOrg(long userId){
		List<Organizations> orgs=this.departmentDAO.findOrgByUserId(userId);
		if(orgs.isEmpty()){
			return null;
		}
		Organizations org=orgs.get(0);
		
		if(org == null){
			return null;
		}
		if( org.getParent() == null){
			return org;
		}
		return org.getParent();
	}
	
	/**
	 * 上传文件
	 * @param file
	 * @param basePath
	 * @param uploadUrl
	 * @param type
	 * @return
	 */
	   private String  UploadFile(FileTransfer file,String basePath,String uploadUrl,String type){
			
			BufferedInputStream brIn = null;
			BufferedOutputStream brOut = null;
			File imgFile = null;
			try {
				InputStream in = file.getInputStream();
				brIn = new BufferedInputStream(in);
				byte[] by = new byte[1024];
				String path=basePath+ uploadUrl;
				if (!basePath.endsWith("/"))
				{
					path = basePath+"/"+uploadUrl;
				}
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				imgFile = new File(dir, new Date().getTime() + type);
				brOut = new BufferedOutputStream(new FileOutputStream(imgFile));
				int read = 0;
				System.out.println(brIn.available()+"========"+imgFile.getPath());
				while ((read = brIn.read(by)) != -1) {
					brOut.write(by, 0, read);
				}
				brOut.flush();
				brOut.close();
				brIn.close();
				uploadUrl += "/" + imgFile.getName();
				// uploadUrl = imgFile.getAbsolutePath();
			} catch (IOException e) {
				System.out.println("文件上传出错了");
				e.printStackTrace();
			} finally {
				try {
					if (brOut != null)
						brOut.close();
					if (brIn != null)
						brIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return imgFile.getName();
		}
		
		private String getFileType(String name){
			String type = "";
			if(name.contains(".")){
				type=name.substring(name.lastIndexOf("."), name.length());
			}
			return type;
		}

		@Override
		public List<String[]> addUserTemplateItem(Users user, long templateId,
				String basePah, String name,String filename,String imgname, FileTransfer file,
				FileTransfer image) throws Exception {
			Organizations org=this.getUserRootOrg(user.getId());
			if( null != org){
				TemplatePo template=this.templateDAO.fintByCompanyId(org.getId());
				if(template.getType() == 1){
					return new ArrayList<String[]>();
				}
			}
			return this.addTemplateItem(user, templateId, basePah, name,filename,imgname, 0 ,file,image);
		}

		@Override
		public List<String[]> addSystemTemplateItem(Users user,
				long templateId, String basePah, String name,String filename,String imgname,
				FileTransfer file, FileTransfer image) throws Exception {
			 return this.addTemplateItem(user, templateId, basePah, name,filename,imgname, 1 ,file,image);
		}

		@Override
		public List<String[]> addComapanyTemplateItem(Users user,
				long templateId, String basePah, String name,String filename,String imgname,
				FileTransfer file, FileTransfer image) throws Exception {
			 return this.addTemplateItem(user, templateId, basePah, name,filename,imgname, 2 ,file,image);
		}

}
