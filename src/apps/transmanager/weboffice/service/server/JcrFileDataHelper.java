package apps.transmanager.weboffice.service.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.jcr.JCRService;

/**
 * JCR文件上传助手，主要用于JCR文件的上传。
 *     可以通过com.evermore.weboffice.service.server.JcrFileDataHelper#loadJcrFiles(File)方法来
 *  完成上传操作。
 *  
 * @author xl  
 * 
 * @date 2013-3-21 10:20:00
 * 
 * @version 1.0
 * 
 * @see com.evermore.weboffice.service.server.JcrFileDataHelper#loadJcrFiles(File)
 * 
 *
 */
public class JcrFileDataHelper {
	
	private  static JcrFileDataHelper helper;
	

	/**
	 * jcr文件服务,主要用户上传文件
	 */
	private JCRService jcrService;
	
	/**
	 * 文件接口，主要用户文件夹的创建
	 */
	private FileSystemService fileSystemService;
	
	/**
	 * 用户服务接口,主要用于获取文件与用户之间的关系
	 */
	private UserService userService;
	
	/**
	 * 构造函数
	 */
	private JcrFileDataHelper(){
		
	}
	
	/**
	 * 获取JcrFileDataHelper 实例
	 * @return
	 */
	public static JcrFileDataHelper getInstance(){
		if(helper ==null){
			helper=new JcrFileDataHelper();
		}
		return helper;
	}
	
	/**
	 * 处理配置文件所匹配的文件
	 * @param loadFile
	 *           配置文件
	 * @throws Exception
	 */
	public void loadJcrFiles(File loadFile) throws Exception{
	    
		if (loadFile != null &&loadFile.exists())
		{
			List<Users> userlist = this.getUserService().getAllUsers();
//			Users tuser=this.getUserService().getUserBySpaceUID("swngb_sunhaidong_wuxigov.cn");
//			for (int i=0;i<userlist.size();i++)
//			{
//				Users user=userlist.get(i);
//				if (user.getSpaceUID().startsWith("swngb"))
//				{
//					System.out.println("spaceuid======"+user.getSpaceUID()+"====");
//				}
//				
////				System.out.println("======"+user.getSpaceUID()+"====");
//				if ("swngb_sunhaidong_wuxigov.cn".equals(user.getSpaceUID()))
//				{
//					System.out.println("username==="+user.getUserName());
//				}
//				else if ("sxdj_zhengxiao_126.com".equals(user.getSpaceUID()))
//				{
//					System.out.println("username==="+user.getUserName());
//				}
//				else if ("swngb_dailiangyun_wuxigov.cn".equals(user.getSpaceUID()))
//				{
//					System.out.println("username==="+user.getUserName());
//				}
//				else if ("xzfw_xiashenjie_wuxigov.cn".equals(user.getSpaceUID()))
//				{
//					System.out.println("username==="+user.getUserName());
//				}
//			}
			List<String[]> propertylist = getProperty();
			for(File file :  loadFile.listFiles()){
				System.out.println("---------------------"+file.getPath()+"----------------");
				//获取当前用户
				Users uploadUser=getUploadFileUser(file,userlist);
				if(uploadUser == null){
					continue;
				}
				String uploadPath = file.getName();
				createAndUpload(uploadPath, file, uploadUser,propertylist);
				System.out.println("---------------------"+file.getName()+"----------------"+"的文件上传完毕！");
			}
			System.out.println("所有文件上传完毕！");
		}else{
			System.out.println("请检查文件路径是否正确!");
		}
	}

	public List<String[]> getProperty()
    {
		List<String[]> list=new ArrayList<String[]>();
    	try
    	{
    		String importpath=InitDataService.importfilepath;
    		String path="/root/greenoffice/fileslist.property";//目前只考虑root和yozosoft的根目录
    		if (importpath!=null)
    		{
    			if (importpath.startsWith("/root"))
    			{
    				path="/root/greenoffice/fileslist.property";//目前只考虑root和yozosoft的根目录
    			}
    			else
    			{
    				path=importpath.substring(0,importpath.indexOf("/greenoffice/"))+"/greenoffice/fileslist.property";
    			}
    		}
    		File file = new File(path);
    		BufferedReader input = new BufferedReader(new FileReader(file));
    		String s=null;
    		while((s = input.readLine())!=null){
    		    if (s!=null && s.length()>8)
    		    {
    		    	//System.out.println(s);
    		    	String[] temp=s.trim().split("####");
    		    	list.add(temp);
    		    }
		    }
    		input.close();
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	System.out.println("list size================="+list.size());
    	return list;
    }
	/**
	 * 根据文件匹配当前用户
	 * @param file
	 *        文件
	 * @return
	 */
	private Users getUploadFileUser(File file,List<Users> userlist){
		String email = "";
		try
		{
			String spaceUID = file.getPath().substring(file.getPath().lastIndexOf(File.separatorChar)+1);
			//将spaceUID转换成email
			System.out.println("spaceUID=========="+spaceUID);
			String spaceHead = spaceUID.substring(0, spaceUID.lastIndexOf("_"));
			String spaceTail = spaceUID.substring(spaceUID.lastIndexOf("_")+1);
			email = spaceHead + "@" + spaceTail;
			System.out.println("email=========="+email);
			this.getJCRService().init(spaceUID);//email.replace("@", "_")
			Users uploadUser = getUsers(spaceUID,userlist);//this.getUserService().getUserByEmail(email);
			return uploadUser;
		}
		catch (Exception e)
		{
			System.out.println("email====================="+email);
			e.printStackTrace();
		}
		return null;
	}
	private Users getUsers(String spaceuid,List<Users> userlist)
	{
		try
		{
			for (int i=0;i<userlist.size();i++)
			{
				Users user=userlist.get(i);
				if (spaceuid.equals(user.getSpaceUID()))
				{
					return user;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("============="+spaceuid);
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 处理文件上传，包括创建文件夹或者上传文件，主要采用递归的方式，遍历文件夹目录，获取文件，并上传到JCR文件服务器中。
	 *        <b>备注:该方法并没有判断文件是否存在。</b>
	 * @param uploadPath
	 *          文件上传的根路径
	 * @param file
	 *          上传文件
	 * @param user
	 *          用户对象
	 * @throws Exception
	 */
	public  void createAndUpload(String uploadPath,File file,Users user,List<String[]> propertylist) throws Exception{
		String path=this.getFilePath(uploadPath, file);
		
		if(file.isDirectory()){
			if(!uploadPath.equals(file.getName())){
				try
				{
					this.createForlder(path, file.getName(), user);
				}
				catch(Exception e)
				{
					//已存在就不处理
				}
			}
			for(File f:file.listFiles()){
				this.createAndUpload(uploadPath, f, user,propertylist);
			}
		}else if(file.isFile()){
			try
			{
				this.createFile(path,file,user,propertylist);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 处理文件上传，包括创建文件夹或者上传文件，主要采用递归的方式，遍历文件夹目录，获取文件，并上传到JCR文件服务器中。
	 *        <b>备注:该方法并没有判断文件是否存在。</b>
	 * @param uploadPath
	 *          文件上传的根路径
	 * @param file
	 *          上传文件
	 * @param user
	 *          用户对象
	 * @throws Exception
	 */
	public  void createAndUpload(String uploadPath,File file,Users user) throws Exception{
		String path=this.getFilePath(uploadPath, file);
		
		if(file.isDirectory()){
			if(!uploadPath.equals(file.getName())){
			   this.createForlder(path, file.getName(), user);
			}
			for(File f:file.listFiles()){
				this.createAndUpload(uploadPath, f, user,this.getProperty());
			}
		}else if(file.isFile()){
			this.createFile(path,file,user,this.getProperty());
		}
	}
	
	/**
	 * 获取文件上传路径，根据上传路径，解析文件上传的相对路径。
	 *         主要是根据文件的路径，截取字符串后，获取文件的
	 *      上传路径
	 * @param uploadPath
	 *             上传路径
	 * @param file
	 *          需要上传的文件
	 * @return
	 */
	private String getFilePath(String uploadPath,File file){
		String filePath=file.getAbsolutePath().replace("\\", "/");
		filePath=filePath.substring(filePath.indexOf(uploadPath)+uploadPath.length());
		String path=uploadPath+filePath;
		if(path.contains("/")){
		  path=path.substring(0,path.lastIndexOf("/"));
		}
		return path;
	}
	
	/**
	 * 创建文件
	 *     调用底层jcr服务接口，创建文件。
	 *     <b>备注：底层上传文件的方法是不合理的，上传文件的过程，跟对应的用户是没有关系的，希望以后可以重构</b>
	 * @param path
	 *       上传文件的相对路径
	 * @param file
	 *          需要上传的文件
	 * @param user
	 *        上传文件的用户
	 * @author xl
	 * @throws Exception
	 */
	private  void createFile(String path,File file,Users user,List<String[]> propertylist) throws Exception{
		System.out.println("创建文件:"+path+"/"+file.getName());
		InputStream fin = new FileInputStream(file);
		InputStream ois = new FileInputStream(file);
		String[] values=getDetailProperty(path+"/"+file.getName(),propertylist);
		
		this.getFileSystemService().createFiles(user.getId(),user.getRealName(), path, file.getName(), fin, ois,false, null, true,values);
		fin.close();
		ois.close();
	}
	private String[] getDetailProperty(String path,List<String[]> propertylist)
	{
		for (int i=0;i<propertylist.size();i++)
		{
			String[] temp = propertylist.get(i);
			if (path.indexOf(temp[0])>=0)
			{
				System.out.println(temp[0]+"============="+temp[1]);
				return temp;
			}
		}
		return null;
	}
	/**
	 * 创建文件夹
	 *      调用底层jcr服务接口，创建文件。
	 *     <b>备注：底层上传文件的方法是不合理的，上传文件的过程，跟对应的用户是没有关系的，希望以后可以重构</b>
	 * @param path
	 *      上传文件的相对路径
	 * @param forldName
	 *         文件夹的名称
	 * @param user
	 *         文件对应的用户
	 * @throws RepositoryException
	 */
	private  void createForlder(String path,String forldName,Users user) throws RepositoryException{
		System.out.println("创建文件夹:"+path+"/"+forldName);
		this.getJCRService().createFolder(user.getRealName(), path, forldName);
	}
	
	private UserService getUserService(){
		if(userService == null){
			 userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
		}
		return userService;
	}
	
	/**
	 * 
	 * @return
	 */
	private FileSystemService getFileSystemService(){
		if(null ==fileSystemService){
			fileSystemService = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
		}
		return fileSystemService;
	}
	
	private JCRService getJCRService(){
		if(null == jcrService){
			jcrService = (JCRService) ApplicationContext.getInstance().getBean(JCRService.NAME);
		}
		return jcrService;
	}
}
