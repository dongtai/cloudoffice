package apps.transmanager.weboffice.servlet.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.both.MD5;
import apps.transmanager.weboffice.util.server.WebTools;

/**
 * 孙爱华临时增加的从office直接上传文件到服务器
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   web1.0
 */
public class OfficeUploadService extends HttpServlet
{
	private static final long serialVersionUID = 1L;     

    public void destroy()
    {
    	
    }
    
    public void init() throws ServletException
    {
        super.init();
        try
        {
        	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
    		//更新组织结构
        	
			String SQL="select a from Organizations as a "
				+" where a.organizecode is null "
				;
			List<Organizations> orglist=(List<Organizations>)jqlService.findAllBySql(SQL);
			if (orglist!=null && orglist.size()>0)
			{
				for (int i=0;i<orglist.size();i++)
				{
					Organizations orgs=orglist.get(i);
					String pkey=orgs.getParentKey();
					if (pkey==null)
					{
						pkey=String.valueOf(orgs.getId());
					}
					else
					{
						pkey+=String.valueOf(orgs.getId());
					}
					orgs.setOrganizecode(pkey);
					jqlService.update(orgs);
				}
			}
			
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart)
		{
			try
			{
				org.apache.commons.fileupload.FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
			    String username=WebTools.converStr(request.getParameter("username"));
			    String password=WebTools.converStr(request.getParameter("password"));
			    String path = WebTools.converStr(request.getParameter("path"));
			    //根据用户名和密码校验
				List fileItems = upload.parseRequest(request);
				for (Iterator iter = fileItems.iterator(); iter.hasNext();)
				{
					FileItem item = (FileItem) iter.next();
					if (item.isFormField())
					{
						String name = URLDecoder.decode(item.getFieldName(),"utf-8");
						String value = item.getString();
					}
					else
					{
						String fileName = WebTools.converStr(item.getName());
						String tempPath = WebConfig.tempFilePath + File.separatorChar;        	
			        	String tempName = System.currentTimeMillis() + fileName;
			        	if (tempName.length() > 30)   // 避免长文件名在不同操作系统的可能有问题。
			        	{
					    	MD5 md5 = new MD5();
					    	tempName = md5.getMD5ofStr(tempName);
			        	}
						File fileOnServer = new File(tempPath+tempName);
						item.write(fileOnServer);
						
						try
				        {
				            if (fileName!=null && fileName.length()>0)
				            {
					            FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
					            File file = new File(tempPath + tempName);
					            InputStream fin = new FileInputStream(file);
					            InputStream ois = new FileInputStream(file);
					            Users userInfo = (Users)request.getSession().getAttribute("userKey");
					            if (userInfo==null)
					            {
					            	UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
					            	DataHolder data=userService.loginCheck(username, password);
					            	userInfo=data.getUserinfo();
					            	if (path==null || path.length()==0)
					            	{
					            		path="";
					            	}
					            	else if (!path.startsWith("/"))
					            	{
					            		path="/"+path;
					            	}
					            	path=userInfo.getSpaceUID()+"/Document"+path;
					            }
					            Fileinfo info = fileSystemService.createFile(userInfo.getId(), userInfo.getRealName(),path, fileName, fin,  ois, false, null);
					            fin.close();
					            file.delete();
					            response.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
					            response.getWriter().write("success");
					            return;
				            }
				        }
				        catch(Exception ex)
				        {
				            ex.printStackTrace();
				        }
					}
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("the enctype must be multipart/form-data");
		}
		response.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
        response.getWriter().write("error");
        return;
	}
}
