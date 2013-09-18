package apps.transmanager.weboffice.service;

import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.TemplateItemPo;
import apps.transmanager.weboffice.domain.TemplatePo;


public interface IFileTemplateService {

	public Map<Long,String> listFileTemplatePo(long userId);
	
	public List<TemplateItemPo> getTemplateItemPo(Long templateId);
	
	
	/**
	 * 添加个人模板分组
	 * @param name
	 */
    public long addUserTemplate(long userId,String name);
    
    
    /**
	 * 添加系统模板分组
	 * @param name
	 */
    public long addSystemTemplate(String name);
    
    
    /**
	 * 添加公司模板分组
	 * @param name
	 */
    public long addCompanyTemplate(long companyId,String name);
    
    /**
	 * 获取templateItem 对象
	 * @param templateItemId
	 *           模板项ID
	 * @return
	 */
	
	public TemplatePo getTemplate(long templateId);
	
	
	/**
	 * 重新命名分组
	 * @param templateId
	 * @param name
	 * @return
	 */
	public int renameTemplate(long templateId,String name);
	
    /**
	 * 删除模板分组
	 * @param name
	 */
    public int delTemplate(long templateId);
    
    /**
     * 添加模板项
     * @param templateId
     *              模板ID
     * @param name
     *           模板项名称
     * @param imagePath
     *           模板项地址
     */
	public  List<String []> addUserTemplateItem(Users user,long templateId,String basePah,String name,String filename,String imgname,FileTransfer file,FileTransfer image)throws Exception;
	
	/**
     * 添加模板项
     * @param templateId
     *              模板ID
     * @param name
     *           模板项名称
     * @param imagePath
     *           模板项地址
     */
	public List<String []> addSystemTemplateItem(Users user,long templateId,String basePah,String name,String filename,String imgname,FileTransfer file,FileTransfer image)throws Exception;
	
	
	/**
     * 添加模板项
     * @param templateId
     *              模板ID
     * @param name
     *           模板项名称
     * @param imagePath
     *           模板项地址
     */
	public List<String []> addComapanyTemplateItem(Users user,long templateId,String basePah,String name,String filename,String imgname,FileTransfer file,FileTransfer image)throws Exception;
	/**
	 * 获取templateItem 对象
	 * @param templateItemId
	 *           模板项ID
	 * @return
	 */
	
	public TemplateItemPo getTemplateItem(long templateItemId);
	
	/**
	 * 删除模板项
	 * @param name
	 */
    public int delTemplateItem(long templateItemId);
}
