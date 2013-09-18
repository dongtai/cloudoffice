package templates.service;

import java.util.List;

import templates.objectdb.Questionnaire;

public interface QuestionnaireService {
	/**
	 * 保存
	 * @param 
	 * @return
	 */
	public Questionnaire save(Questionnaire questionnaire);
	/**
	 * 删除
	 * @param id
	 */
	public void delete(Long id);
	/**
	 * 根据条件查找
	 * @param hql
	 * @return
	 */
	public List<Questionnaire> find(String hql);
	/**
	 * 根据条件修改
	 * @param hql
	 * @return
	 */
	
	public Questionnaire update(Questionnaire questionnaire);
	
	public Questionnaire get(Long id);
	
	
	public PageBean queryForPage(String hql,int pageSize, int page,int allRow );
	
}
