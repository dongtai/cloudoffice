package apps.transmanager.weboffice.dwr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.io.FileTransfer;
import org.springframework.beans.factory.annotation.Autowired;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.TemplateItemPo;
import apps.transmanager.weboffice.domain.TemplatePo;
import apps.transmanager.weboffice.service.IFileTemplateService;
import apps.transmanager.weboffice.util.beans.PageConstant;

/**
 * 聊天的控制类,对聊天的各类事件进行数据转化和控制转发(dwr)
 * 
 * @author 彭俊杰(753)
 * @author 杨丁苗
 * 
 */
public class FileTemplateDwr {

	@Autowired
	private IFileTemplateService fileTemplateService;
	
	/**
	 * 获取用户的模板
	 * @return
	 */
	public Map<Long,String> listTemplate(HttpServletRequest request){
		Users user = getCurrentUser(request);
    	if( user !=null){
    		return this.fileTemplateService.listFileTemplatePo(user.getId());
    	}
		return new  HashMap<Long,String>();
	}
	
	/**
	 * 获取用户模板的子选项
	 * 
	 * @param templateId
	 *            模板ID
	 * @return
	 */
	public List<String[]> getTemplateItemPo(Long templateId){
		 List<String[]> data=new ArrayList<String[]>();
		List<TemplateItemPo> pos= this.fileTemplateService.getTemplateItemPo(templateId);
		if(!pos.isEmpty()){
			for(TemplateItemPo po:pos){
				String[] d=new String[5];
				d[0]=po.getId().toString();
				d[1]=po.getName();
				d[2]=po.getImagePath();
				d[3]=po.getTempatePath();
				d[4]=po.getTemplate().getId()+"";
				data.add(d);
			}
		}
		return data;
	}
	
	/**
	 * 添加模板
	 * @param name
	 */
    public long addTemplate(String name,HttpServletRequest request){
    	Users user = getCurrentUser(request);
    	if( user !=null){
    		return this.fileTemplateService.addUserTemplate(user.getId(),name);
    	}
    	return -1;
	}
    
    /**
	 * 获取templateItem 对象
	 * @param templateItemId
	 *           模板项ID
	 * @return
	 */
	
	public TemplatePo getTemplate(long templateId){
		return this.fileTemplateService.getTemplate(templateId);
	}
	
	/**
	 * 重命名templateItem 对象
	 * @param templateItemId
	 *           模板项ID
	 * @return
	 */
	
	public int renameTemplate(long templateId,String name){
		return this.fileTemplateService.renameTemplate(templateId, name);
	}
	
    /**
	 * 删除模板
	 * @param name
	 */
    public int delTemplate(long templateId){
		return this.fileTemplateService.delTemplate(templateId);
	}
    
    /**
     * 添加模板项
     * @param templateId
     *              模板ID
     * @param name
     *           模板项名称
     * @param imagePath
     *           模板项地址
     * @throws Exception 
     */
	public List<String []> addTemplateItem(String templateId,String templatename,boolean type,String filename,String imgname,FileTransfer file,FileTransfer image,HttpServletRequest request) throws Exception{
		Users user = getCurrentUser(request);
    	if( user !=null){
    		if(!type){
    			return this.fileTemplateService.addUserTemplateItem(user,Long.valueOf(templateId),request.getSession().getServletContext().getRealPath("/"), templatename,filename,imgname, file,image);
    		}else{
    			return this.fileTemplateService.addComapanyTemplateItem(user,Long.valueOf(templateId),request.getSession().getServletContext().getRealPath("/"), templatename,filename,imgname, file,image);
    		}
    	}
    	return new ArrayList<String[]>();
	}
	
	/**
	 * 获取templateItem 对象
	 * @param templateItemId
	 *           模板项ID
	 * @return
	 */
	
	public TemplateItemPo getTemplateItem(long templateItemId){
		return this.fileTemplateService.getTemplateItem(templateItemId);
	}
	
	/**
	 * 删除模板项
	 * @param name
	 */
    public int delTemplateItem(long templateItemId){
		return this.fileTemplateService.delTemplateItem(templateItemId);
	}
    
	private Users getCurrentUser(HttpServletRequest req) {
		return (Users) req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
	}
}
