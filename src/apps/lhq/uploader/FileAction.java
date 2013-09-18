package apps.lhq.uploader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;


import com.opensymphony.xwork2.ActionContext;

// Referenced classes of package com.lhq.uploader:
//            BaseAction, MyUtils

public class FileAction extends BaseAction { 
	   private Map<String, Object> infos = new HashMap<String, Object>();  
	  
	    public static final String ROOT = "upload\\";  
	  
	    private File myUpload;  
	  
	    private String myUploadContentType;  
	  
	    private String myUploadFileName;  
	 
	    private String path;  
	    private HttpServletRequest request = ServletActionContext.getRequest();
	    private Map<String, Object> session = ActionContext.getContext().getSession();
	    private boolean success;  
	   /** 
	     * 上传文件 
	37.     *  
	38.     * @return 
	39.     
	 * @throws IOException */  
	
	    public String uploadFiles() throws IOException {  
	       String rootPath =request.getSession().getServletContext().getRealPath("");  
	       String sp = rootPath; 
	       try {  
	 	        MyUtils.mkDirectory(sp);  
	            MyUtils.upload(getMyUploadFileName(), sp, getMyUpload());  
	            this.success = true;  
	        } catch (RuntimeException e) {  
	           e.printStackTrace();  
	       }  
	       
	        return "ok";  
	    }  
	  
	    public File getMyUpload() {  
	        return myUpload;  
	    }  
	  
	    public void setMyUpload(File myUpload) {  
	        this.myUpload = myUpload;  
	    }  
	  
	    public String getMyUploadContentType() {  
	        return myUploadContentType;  
	    }  
	  
	    public void setMyUploadContentType(String myUploadContentType) {  
	        this.myUploadContentType = myUploadContentType;  
	    }  
	  
	    public String getMyUploadFileName() {  
	        return myUploadFileName;  
	    }  
	  
	    public void setMyUploadFileName(String myUploadFileName) {  
	        this.myUploadFileName = myUploadFileName;  
	   }  
	  
	    public boolean isSuccess() {  
	        return success;  
	    }  
	  
	    public void setSuccess(boolean success) {  
	       this.success = success;  
	    }  
	  
	    public String getPath() {  
	       return path;  
	    }  
	 
	    public void setPath(String path) throws UnsupportedEncodingException {  
	       this.path = URLDecoder.decode(path, "UTF-8");  
	   }  
	 
	   public Map<String, Object> getInfos() {  
	       return infos;  
	   }  
	 
	   public void setInfos(Map<String, Object> infos) {  
	       this.infos = infos;  
	    }


	}  
