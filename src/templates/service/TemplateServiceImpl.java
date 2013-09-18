package templates.service;

import java.util.List;


import templates.dao.TemplateDao;
import templates.dao.TemplateTypeDao;
import templates.objectdb.Template;
import templates.objectdb.TemplateType;

public class TemplateServiceImpl implements TemplateService{

	
	private TemplateDao templateDao ;
	private TemplateTypeDao templateTypeDao;
	
	@Override
	public void deleteTemplate(Long id) {
		templateDao.delete(templateDao.find(id));
		
	}

	@Override
	public List<Template> findTemplate(String hql) {
		return templateDao.find(hql);
	}

	@Override
	public Template modifyTemplate(Template template) {
		templateDao.merge(template);
		return template;
	}
  
	@Override
	public Template updateTemplate(Template template) {
		templateDao.update(template);
		return template;
	}
	
	public Template saveTemplate(Template template) {
		Long id = templateDao.save(template);
		template.setTid(id);
		return template;
		
	}


	@Override
	public void deleteTemplateType(Long id) {
		templateTypeDao.delete(templateTypeDao.find(id));
		
	}

	@Override
	public List<TemplateType> findTemplateType(String hql) {
		
		return templateTypeDao.find(hql);
	}

	@Override
	public TemplateType modifyTemplateType(TemplateType templateType) {
		templateTypeDao.merge(templateType);
		return templateType;
	}

	@Override
	public TemplateType saveTemplateType(TemplateType templateType) {
		Long id = templateTypeDao.save(templateType);
		templateType.setTtid(id);
		return templateType;
	}
	
	
public PageBean queryForPage(String hql,int pageSize, int page,int allRow ) {
		
//		String hql="from Template";
        int totalPage = PageBean.countTotalPage(pageSize, allRow);//总页数
        final int beginIndex = PageBean.countOffset(pageSize, page); //当前页开始记录
        final int everyPage = pageSize;    //每页记录数
        final int currentPage = PageBean.countCurrentPage(page);
        List<Template> list = templateDao.getUserByPage(hql, beginIndex, everyPage);
        //把分页信息保存到Bean中
        PageBean pageBean = new PageBean();
        pageBean.setPageSize(pageSize);    
        pageBean.setCurrentPage(currentPage);
        pageBean.setAllRow(allRow);
        pageBean.setTotalPage(totalPage);
        pageBean.setList(list);
        pageBean.init();
        return pageBean;
	}

	
	
	public TemplateDao getTemplateDao() {
		return templateDao;
	}

	public void setTemplateDao(TemplateDao templateDao) {
		this.templateDao = templateDao;
	}

	public TemplateTypeDao getTemplateTypeDao() {
		return templateTypeDao;
	}

	public void setTemplateTypeDao(TemplateTypeDao templateTypeDao) {
		this.templateTypeDao = templateTypeDao;
	}

	
}
