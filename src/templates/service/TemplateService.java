package templates.service;

import java.util.List;

import templates.objectdb.Template;
import templates.objectdb.TemplateType;

public interface TemplateService {	
	/**
	 * 保存模板
	 * @param 
	 * @return
	 */
	public Template saveTemplate(Template template);
	/**
	 * 删除指定ID的模板
	 * @param id
	 */
	public void deleteTemplate(Long id);
	/**
	 * 根据条件查找模板
	 * @param hql
	 * @return
	 */
	public List<Template> findTemplate(String hql);
	/**
	 * 根据条件修改模板
	 * @param hql
	 * @return
	 */
	
	public Template modifyTemplate(Template template);
	public Template updateTemplate(Template template);
	
	/**
	 * 保存模板类型
	 * @param 
	 * @return
	 */
	public TemplateType saveTemplateType(TemplateType templateType);
	/**
	 * 删除指定ID的模板
	 * @param id
	 */
	public void deleteTemplateType(Long id);
	/**
	 * 根据条件查找模板
	 * @param hql
	 * @return
	 */
	public List<TemplateType> findTemplateType(String hql);
	/**
	 * 根据条件修改模板
	 * @param hql
	 * @return
	 */	
	public TemplateType modifyTemplateType(TemplateType templateType);	
	public PageBean queryForPage(String hql,int pageSize, int page,int allRow );
}