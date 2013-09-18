package apps.transmanager.weboffice.action;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
/**
 * 上传图片（struts2）
 * @author 胡晓燕
 *
 */
public class uploadpic extends ActionSupport{
	private static final long serialVersionUID = 1L;
	private File file;
	
	public void upload(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_HHmmss");
		Random r=new Random();
		String path=ServletActionContext.getServletContext().getRealPath("/");
		String imgName=sdf.format(new Date())+r.nextInt(100000)+".jpg";
		try {
			FileUtils.copyFile(file,new File(path+"cloud\\weibo\\WeiboUpload\\"+imgName));
			String outPath=ServletActionContext.getRequest().getScheme()+"://"+ServletActionContext.getRequest().getServerName()+":"+ServletActionContext.getRequest().getServerPort()+ServletActionContext.getRequest().getContextPath()+"/";
			ServletActionContext.getResponse().getWriter().print(outPath+"cloud/weibo/WeiboUpload/"+imgName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void delete(){
		System.out.println("success forward");
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
}