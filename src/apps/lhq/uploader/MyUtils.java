package apps.lhq.uploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;


public class MyUtils {  
	public static  Map<String, Object> session = ActionContext.getContext().getSession();
	private static HttpServletRequest request = ServletActionContext.getRequest();
	    public static  String upload(String uploadFileName, String savePath, File uploadFile) {  
	    	String newFileName = getUUIDName(uploadFileName, savePath);  
	    	String uploadURL=savePath.substring(0, savePath.length()-"/templates".length());
	    	int a =0;
	        try {  
	        	String uploadName= uploadFileName.substring(uploadFileName.lastIndexOf("."), uploadFileName.length());
	        	if(uploadName.equalsIgnoreCase(".jpg")){
	    	    	savePath=savePath+"/images/";
	    	    	session.put("tmpImgUrl", newFileName);
	    	    } 
	        	if(uploadName.equalsIgnoreCase(".png")){
	    	    	savePath=savePath+"/images/";
	    	    	session.put("tmpImgUrl", newFileName);
	    	    }
	    	    if(uploadName.equalsIgnoreCase(".gif")){
	    	    	savePath=savePath+"/images/";
	    	    	session.put("tmpImgUrl", newFileName);
	    	    }
	    	    if(!uploadName.equalsIgnoreCase(".jpg") && !uploadName.equalsIgnoreCase(".png")&& !uploadName.equalsIgnoreCase(".gif")
	    	      && !uploadName.equalsIgnoreCase(".dot")&& !uploadName.equalsIgnoreCase(".pot")
	    	      && !uploadName.equalsIgnoreCase(".xlt")&& !uploadName.equalsIgnoreCase(".xls")){
	    	    	session.put("uploadresult", "error");
	    	    	return "error";
	    	    }
	    	    if(uploadName.equalsIgnoreCase(".dot")){
	    	    	savePath=uploadURL+"/tdownload/";
	    	    	session.put("tmptUrl", newFileName);
	    	    	session.put("imgType", "wp");
	    	    	a=1;
	    	    } if(uploadName.equalsIgnoreCase(".pot")){
	    	    	savePath=uploadURL+"/tdownload/";
	    	    	session.put("tmptUrl", newFileName);
	    	    	session.put("imgType", "pg");
	    	    	a=1;
	    	    	
	    	    } if(uploadName.equalsIgnoreCase(".xlt")){
	    	    	savePath=uploadURL+"/tdownload/";
	    	    	session.put("tmptUrl", newFileName);
	    	    	session.put("imgType", "ss");
	    	    	a=1;
	    	    } if(uploadName.equalsIgnoreCase(".xls")){
	    	    	savePath=uploadURL+"/tdownload/";
	    	    	session.put("tmptUrl", newFileName);
	    	    	session.put("imgType", "ss");
	    	    	a=1;
	    	    }
	    	    session.put("uploadresult", "success"); 
	            FileOutputStream fos = new FileOutputStream(savePath + newFileName);  
	            FileInputStream fis = new FileInputStream(uploadFile);  
	            uploadFileName=newFileName;
	            byte[] buffer = new byte[1024];  
	            int len = 0;  
	            while ((len = fis.read(buffer)) > 0) {  
	                fos.write(buffer, 0, len);  
	            }  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        
	        return newFileName;  
	    }  
	  
	    public static String getUUIDName(String fileName, String dir) {  
	        String[] split = fileName.split("\\.");  
	        String extendFile = "." + split[split.length - 1].toLowerCase();  
	       return java.util.UUID.randomUUID().toString() + extendFile;  
	   }  
	  
	    /** 
	5.     * ���·������һϵ�е�Ŀ¼ 
	46.     *  
	47.     * @param path 
	48.     */  
	    public static boolean mkDirectory(String path) {  
	        File file = null;  
	        try {  
	            file = new File(path);  
	           if (!file.exists()) {  
	               return file.mkdirs();  
	           }  
	       } catch (RuntimeException e) {  
	           e.printStackTrace();  
	       } finally {  
	            file = null;  
	       }  
	        return false;  
	    }  
	  
	}  
