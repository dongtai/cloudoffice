package templates.service;

import java.util.List;

import templates.dao.QuestionnaireDao;
import templates.objectdb.Questionnaire;

public class QuestionnaireServiceImpl implements QuestionnaireService {

	QuestionnaireDao questionnaireDao;
	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Questionnaire> find(String hql) {
		// TODO Auto-generated method stub
		return questionnaireDao.find(hql);
	}

	@Override
	public Questionnaire get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageBean queryForPage(String hql, int pageSize, int page, int allRow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Questionnaire save(Questionnaire questionnaire) {
		Long id = questionnaireDao.save(questionnaire);
		questionnaire.setId(id);
		return questionnaire;
	}

	@Override
	public Questionnaire update(Questionnaire questionnaire) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setQuestionnaireDao(QuestionnaireDao questionnaireDao) {
		this.questionnaireDao = questionnaireDao;
	}

}
