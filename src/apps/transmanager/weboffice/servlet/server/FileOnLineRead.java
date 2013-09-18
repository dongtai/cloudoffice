package apps.transmanager.weboffice.servlet.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.handler.BFilesOpeHandler;
import apps.transmanager.weboffice.service.handler.FilesHandler;
import apps.transmanager.weboffice.service.jcr.JCRService;
import apps.transmanager.weboffice.util.server.JSONTools;
import apps.transmanager.weboffice.util.server.LogsUtility;
import apps.transmanager.weboffice.util.server.convertforread.FileUtil;
import apps.transmanager.weboffice.util.server.convertforread.bean.ConvertForRead;
import apps.transmanager.weboffice.util.server.convertforread.bean.FileConvertStatus;

public class FileOnLineRead extends HttpServlet {

	private static final long serialVersionUID = 3607132928112133678L;

	public void init() throws ServletException {
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("getPicwhs".equalsIgnoreCase(action)) {
			getPicwhs(request, response);
		} else if ("convert2pic".equalsIgnoreCase(action)) {
			convert2pic(request, response);
		} else if ("convert2html".equalsIgnoreCase(action)) {
			convert2html(request, response);
			//刷新有问题，暂时先不处理，最好放在session的sessionDestroyed
		}else if ("iconvert2html".equalsIgnoreCase(action)) {
			iconvert2html(request, response);
			//刷新有问题，暂时先不处理，最好放在session的sessionDestroyed
		} /*else if ("closed".equalsIgnoreCase(action)) {
			// 删除bean
			//closed(request, response);
		} */else if ("redirect".equalsIgnoreCase(action)) {
			redirect(request, response);
		}
	}

	private void redirect(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
//			String email = URLDecoder.decode(request.getParameter("email"),"UTF-8");
			String path = URLDecoder.decode(request.getParameter("path"),"UTF-8");
			//////////////权限判断////////////
			Users user = (Users) request.getSession().getAttribute("userKey");  // 错误做法，将删除，所有用户信息需要由参数传入。
			if(!BFilesOpeHandler.canGroupOperationFileSystemAction(user.getId(),path,FileSystemCons.READ_FLAG))
			{
				String error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, "no permission");
				response.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				response.getWriter().write(error);
				String url = "converterror.html";
				response.sendRedirect(url);
				
				return;
			}
			//////////////////////////
			
			String fileName = URLDecoder.decode(request
					.getParameter("fileName"),"UTF-8");
			String readtype = request.getParameter("readtype");
			fileName = URLEncoder.encode(fileName,"UTF-8").replace("+", "%20");
//			long str = (System.currentTimeMillis() + (long) (Math.random() * 1000));
//			String fid = String.valueOf(str);
			String s = UUID.randomUUID().toString(); 
			String fid = s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
			FileConvertStatus fs = null;
			if (fs == null) {
				String[] strs = createsrcfileAndtargerFold(fid, path,fileName);
				fs = ConvertForRead.createFileConvertStatus(fid, strs[0],
						strs[1], null);
				fs.setSessionid(request.getSession().getId());
				FilesHandler.modifyTeamFile(path,user,0);//如果是协作共享文档认为已查看过
			}
			if (String.valueOf((FileSystemCons.READ_PIC_TYPE)).equals(readtype)) {
				String url = "convert.html?fid=" + fid + "&filename="
						+ fileName;
				response.sendRedirect(url);
			}
			else if (String.valueOf((FileSystemCons.READ_HTML_TYPE)).equals(readtype)) {
				String url = "converttohtml.html?fid=" + fid + "&filename="
						+ fileName;
				response.sendRedirect(url);
			}
			else if ("3".equals(readtype)) {
				String url = "convertperview.html?fid=" + fid + "&filename="
						+ fileName;
				response.sendRedirect(url);
			} 
			else {
				String url = "converttohtml.html?fid=" + fid + "&filename="
						+ fileName;
				response.sendRedirect(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void convert2html(HttpServletRequest request,
			HttpServletResponse response) {
		FileConvertStatus fs = null;
		try {
			request.setCharacterEncoding("UTF-8");
			String fid = request.getParameter("fid");
			fs = ConvertForRead.getStatusBean(fid);
			if (fs == null) {
				//
				LogsUtility.error("convert2html error 1");
				response.getWriter().print("error");
				return;
			}
			String srcfile = fs.getFilepath();
			String tarfile = fs.getTargetFile() + ".html";

			// 转换成网页
//			ConvertUtil.convertMStoHtml(srcfile, tarfile);
			// rmi的方式
			// ConvertRmiClient.getConvertInstance().convertMStoHTML(srcfile,
			// tarfile);
			if (srcfile.endsWith("pdf"))
			{
				ConvertForRead.convertPDFtoHtml(srcfile, tarfile);
			}
			else
			{
				ConvertForRead.convertMStoHtml(srcfile, tarfile);
			}

			if (new File(tarfile).exists()) {
				//转换的文件不在tomcat下，copy 过去
				if(ConvertForRead.getTargetFold() != null)
				{
					String htmlFold = getServletContext().getRealPath("tarpreviewfiles");
					File tfoler = new File(htmlFold);
					if (!tfoler.exists()) {
						tfoler.mkdir();
					}
					
					String targetFile = htmlFold + File.separatorChar +fs.getFid();
					fs.setWebTargetFile(targetFile);
					
					String htmlfile = fs.getTargetFile() + ".html";
					FileUtil.copyFile(new File(htmlfile), new File(targetFile + ".html"));
					String htmlfilees = fs.getTargetFile() + ".files";
					FileUtil.copyDirectiory(htmlfilees, targetFile + ".files");
				}
				String redirectpath = "../tarpreviewfiles/" + fid + ".html";
				response.getWriter().println(redirectpath);
			} else {
				LogsUtility.error("convert2html error 2");
				response.getWriter().print("error");
			}
		} catch (Exception e) {
			try {
				LogsUtility.error("convert2html error 3");
				response.getWriter().print("error");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	private void iconvert2html(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
//			String email = URLDecoder.decode(request.getParameter("email"),"UTF-8");
			String path = URLDecoder.decode(request.getParameter("path"),"UTF-8");
			//////////////权限判断////////////
			Users user = (Users) request.getSession().getAttribute("userKey");  // 错误做法，将删除，所有用户信息需要由参数传入。
			if(!BFilesOpeHandler.canGroupOperationFileSystemAction(user.getId(),path,FileSystemCons.READ_FLAG))
			{
				String error = JSONTools.convertToJson(ErrorCons.PERMISSION_ERROR, "no permission");
				response.setHeader("Cache-Control",
						"no-store,no-cache,must-revalidate");
				response.getWriter().write(error);/*
				String url = "converterror.html";
				response.sendRedirect(url);
				*/
				return;
			}
			//////////////////////////
			
			//String fileName = URLDecoder.decode(request.getParameter("fileName"),"UTF-8");
			int dot=path.lastIndexOf("/")+1;
			String fileName=path.substring(dot);
			String readtype ="1";// request.getParameter("readtype");
			fileName = URLEncoder.encode(fileName,"UTF-8").replace("+", "%20");
//			long str = (System.currentTimeMillis() + (long) (Math.random() * 1000));
//			String fid = String.valueOf(str);
			String s = UUID.randomUUID().toString(); 
			String fid = s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
			FileConvertStatus fs = null;
			if (fs == null) {
				String[] strs = createsrcfileAndtargerFold(fid, path,fileName);
				fs = ConvertForRead.createFileConvertStatus(fid, strs[0],
						strs[1], null);
				fs.setSessionid(request.getSession().getId());
				FilesHandler.modifyTeamFile(path,user,0);//如果是协作共享文档认为已查看过
			}
/*			if (String.valueOf((FileSystemCons.READ_PIC_TYPE)).equals(readtype)) {
				String url = "convert.html?fid=" + fid + "&filename="
						+ fileName;
				response.sendRedirect(url);
			}
			else if (String.valueOf((FileSystemCons.READ_HTML_TYPE)).equals(readtype)) {
				String url = "converttohtml.html?fid=" + fid + "&filename="
						+ fileName;
				response.sendRedirect(url);
			}
			else if ("3".equals(readtype)) {
				String url = "convertperview.html?fid=" + fid + "&filename="
						+ fileName;
				response.sendRedirect(url);
			} 
			else {
				String url = "converttohtml.html?fid=" + fid + "&filename="
						+ fileName;
				response.sendRedirect(url);
			}*/

			String srcfile = fs.getFilepath();
			String tarfile = fs.getTargetFile() + ".html";
			

			if (srcfile.endsWith("pdf"))
			{
				ConvertForRead.convertPDFtoHtml(srcfile, tarfile);
			}
			else
			{
				ConvertForRead.convertMStoHtml(srcfile, tarfile);
			}
			if (new File(tarfile).exists()) {
				//转换的文件不在tomcat下，copy 过去
				if(ConvertForRead.getTargetFold() != null)
				{
					String htmlFold = getServletContext().getRealPath("tarpreviewfiles");
					File tfoler = new File(htmlFold);
					if (!tfoler.exists()) {
						tfoler.mkdir();
					}
					
					String targetFile = htmlFold + File.separatorChar +fs.getFid();
					fs.setWebTargetFile(targetFile);
					
					String htmlfile = fs.getTargetFile() + ".html";
					FileUtil.copyFile(new File(htmlfile), new File(targetFile + ".html"));
					String htmlfilees = fs.getTargetFile() + ".files";
					FileUtil.copyDirectiory(htmlfilees, targetFile + ".files");
				}
				String redirectpath = "../tarpreviewfiles/" + fid + ".html";
				response.getWriter().print(redirectpath);
			} else {
				LogsUtility.error("iconvert2html error 2");
				response.getWriter().print("error");
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void convert2pic(HttpServletRequest request,
			HttpServletResponse response) {
		FileConvertStatus fs = null;
		try {
			request.setCharacterEncoding("UTF-8");
			String fid = request.getParameter("fid");
			int pageindex = Integer.parseInt(request.getParameter("pageindex"));
//			LogsUtility.error(pageindex);
			fs = ConvertForRead.getStatusBean(fid);// 可以把FileConvertStatus留到以后处理
			if (fs == null) {
				LogsUtility.error("convert2pic error 1");
				response.getWriter().print("error");
				return;
			}
			String picfile = fs.getTargetFile() + File.separator + pageindex
					+ "." + fs.getFiletype();
			File file = new File(picfile);
			if (!file.exists()) {
				boolean re = ConvertForRead.convertMStopic(fs, pageindex);
				if (!re) {
					LogsUtility.error("convert2pic error 2");
					response.getWriter().print("error");
					return;
				}
				file = new File(picfile);
				if (!file.exists()) {
					LogsUtility.error("convert2pic error 3");
					response.getWriter().print("error");
					return;
				}
			}
			response.setContentType("image/"+fs.getFiletype());
			BufferedInputStream buffInput = new BufferedInputStream(
					new FileInputStream(picfile));
			BufferedOutputStream buffout = new BufferedOutputStream(
					response.getOutputStream());
			int length = -1;
			byte[] buff = new byte[1024];
			while ((length = buffInput.read(buff)) != -1)
				buffout.write(buff, 0, length);
			buffout.flush();
			buffInput.close();
			buffout.close();

		} catch (Exception e) {
			try {
				LogsUtility.error("convert2pic error 4");
				response.getWriter().print("error");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			ConvertForRead.releaseStatusBean(fs);
		}
	}

	private void getPicwhs(HttpServletRequest request,
			HttpServletResponse response) {
		FileConvertStatus fs = null;
		try {
			request.setCharacterEncoding("UTF-8");
			String fid = request.getParameter("fid");
			fs = ConvertForRead.getStatusBean(fid);
			if (fs == null) {
				LogsUtility.error("getPicwhs error 1"+"fid:"+fid);
				response.getWriter().print("error");
				return;
			}
			float[][] whs = ConvertForRead.getWhs(fs);
			if (whs == null)
			{
				response.getWriter().print("error");
				LogsUtility.error("getPicwhs error 2");
			} else if (whs.length == 0) {
				//LogsUtility.error("getPicwhs error 2");
				response.getWriter().print("nopic");
			} else {
				String whss = "";
				for (int i = 0; i < whs.length; i++) {
					whss += whs[i][0] + " ";
					whss += whs[i][1] + " ";
				}
				response.getWriter().write(whss);
			}
		} catch (Exception e) {
			try {
				LogsUtility.error("getPicwhs error 3");
				response.getWriter().print("error");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			ConvertForRead.releaseStatusBean(fs);
		}
	}

	private String[] createsrcfileAndtargerFold(String fid, String path,String fileName) throws Exception {
		//srcFold tarFold 如果两台机器上，可以在转换的机器上挂载一个共享的文件夹。一致的路劲
		String fileNamesuddix = fileName.substring(fileName.lastIndexOf('.'));
		String srcFolder = ConvertForRead.getSrcFold() == null ? getServletContext().getRealPath("srcpreviewfiles") : ConvertForRead.getSrcFold();
		File foler = new File(srcFolder);
		if (!foler.exists()) {
			foler.mkdir();
		}

		String srcfile = srcFolder + File.separatorChar + fid + fileNamesuddix;
		//将txt的文本文件转换成utf-8//要将csv的utf-8转为gbk
		boolean istxt = fileNamesuddix.regionMatches(true, 0, ".txt", 0, 4)/* || pathsuddix.regionMatches(true, 0, ".csv", 0, 4)*/;
		boolean iscsv = fileNamesuddix.regionMatches(true, 0, ".csv", 0, 4);
		if(istxt || iscsv)
		{
			srcfile = srcFolder + File.separatorChar + "temp"+ fid + fileNamesuddix;
		}
		// srcfile =
		// "C:\\Documents and Settings\\Administrator\\workspace\\officedemo2.0\\war\\srcpreviewfiles\\test1.docx";
		File file = new File(srcfile);
		if (!file.exists()) {
			JCRService jcrService = (JCRService) ApplicationContext
					.getInstance().getBean(JCRService.NAME);
			InputStream in;
	        if (fileName == null || fileName.length() < 1)
	        {
	        	fileName = System.currentTimeMillis() + ".tmp";
	        }
	        int idx = path.indexOf("jcr:system/jcr:versionStorage");
	        int idx2 = path.indexOf("system_audit_root");
            if(idx>-1)
            {
            	in = jcrService.getVersionContent(path, fileName);
            }
            else if(idx2>-1)
            {
            	in = jcrService.getContent(path);
            }
            else
            {
            	in = jcrService.getContent(null, path, false);     // 从文件库中获取文件流
            }
			/*InputStream in = jcrService.getContent(nullemail.replace('@', '_'),
					path, false);*/
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			byte[] b = new byte[8 * 1024];
			int len = 0;
			while ((len = in.read(b)) > 0) {
				out.write(b, 0, len);
			}
			out.close();
		}
		if(istxt || iscsv)
		{
			String encode = FileUtil.getFileEncode(srcfile);
			String defaultendcode = System.getProperty("file.encoding");//Charset.defaultCharset();
			LogsUtility.error("encode:   "+encode+" defaultendcode::"+defaultendcode);
			if(encode.equals("UTF-8+BOM"))
			{
				String outFilename = srcFolder + File.separatorChar + "temp1"+fid + fileNamesuddix;
				FileUtil.UTF8BOMTOUTF8(new File(srcfile), new File(outFilename));
				srcfile = outFilename;
				file.delete();
				file = new File(outFilename);
				encode = "UTF-8";
			}
			if(encode.equals(defaultendcode))
			{
				srcfile = srcFolder + File.separatorChar + fid + fileNamesuddix;
				boolean flag = file.renameTo(new File(srcfile));
				if(!flag)
				{
					srcfile = file.getPath();
				}
			}
			else
			{
				String outFilename = srcFolder + File.separatorChar + fid + fileNamesuddix;
				FileUtil.translateCharset(srcfile, outFilename, encode, defaultendcode);
				srcfile = outFilename;
				file.delete();
			}
		}
		String tarFolder = ConvertForRead.getTargetFold() == null ? getServletContext()
				.getRealPath("tarpreviewfiles") : ConvertForRead
				.getTargetFold();
		File tfoler = new File(tarFolder);
		if (!tfoler.exists()) {
			tfoler.mkdir();
		}
		String tarfile = tarFolder + File.separatorChar + fid;
		return new String[] { srcfile, tarfile };
	}
}
