package templates.dao;

import templates.objectdb.Questionnaire;

public class QuestionnaireDaoImpl extends GenericDaoHibernateImpl<Questionnaire, Long>
implements QuestionnaireDao{

	public QuestionnaireDaoImpl(Class<Questionnaire> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

}
