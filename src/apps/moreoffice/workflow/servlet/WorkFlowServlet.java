package apps.moreoffice.workflow.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import apps.moreoffice.workflow.services.WorkflowServices;
import apps.transmanager.weboffice.constants.both.WorkflowConst;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.FileSystemService;

public class WorkFlowServlet extends HttpServlet
{
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException,
    IOException
    {
		doPost(request, resp);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException
    {
		//System.out.println("=============  "+request.getRequestURI()+"\n"+request.getQueryString());
		request.setCharacterEncoding("utf-8");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		HashMap<String, String> field = new HashMap<String, String>();
		FileItem fileItem = null;
		if (isMultipart == true)
		{
			try
			{
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> fileItems = upload.parseRequest(request);
				Iterator<FileItem> iter = fileItems.iterator();
				// 依次处理每个表单域
				while (iter.hasNext())
				{
					FileItem item = (FileItem) iter.next();
					if (item.isFormField())
					{
						// 如果item是正常的表单域
						String name = item.getFieldName();
						String value = item.getString("UTF-8");
						//System.out.print("\n表单域名为:" + name + "表单域值为:" + value);
						field.put(name, value);
					}
					else
					{
						// 如果item是文件上传表单域
						// 获得文件名及路径\
					    fileItem = item;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			if (fileItem != null && fileItem.getName().length() > 0)
			{
			    uplaodFile(fileItem, field);
			}
			String param = request.getParameter("start");
			boolean startFlag = param != null && "true".equalsIgnoreCase(param);   // 启动工作流
			WorkflowServices ws = (WorkflowServices)ApplicationContext.getInstance().getBean("workflowServices");	
			int ret = ws.processTask(field, startFlag);
			resp.getWriter().println(ret);
		}
		else
		{
			System.out.println("the enctype must be multipart/form-data");
			resp.getWriter().println(WorkflowConst.ERROR);
		}
	}
	
	/**
	 * 
	 */
	private String uplaodFile(FileItem fileItem, HashMap<String, String> field)
	{
	    try
	    {
	        String actorId = field.get("actorId");
            String processInstanceId = field.get("processInstanceId");
            String itemName = fileItem.getFieldName();
            String fileName = fileItem.getName();
            
            String tempFolder = getServletContext().getRealPath("tempfile");
            File foler = new File(tempFolder);
            if (!foler.exists())
            {
                foler.mkdir();
            }
            File file = new File(tempFolder + File.separatorChar + actorId + processInstanceId + System.currentTimeMillis());
            fileItem.write(file);
    	    FileInputStream in = new FileInputStream(file);
            FileInputStream indata = new FileInputStream(file);
            FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME); 
            //fileSystemService.createFile(actorId, FileConstants.WORKFLOW, fileName + "_" + processInstanceId, in, indata, false, null);
            in.close();
            indata.close(); 
            file.delete();
            field.put("attachAddress", FileConstants.WORKFLOW + "/" + fileName + "_" + processInstanceId);
            System.out.println("文件" + file.getName() + "上传成功");
	    }
	    catch (Exception e)
	    {
	        
	    }
	    return null;
	}
}
