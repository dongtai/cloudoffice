package templates.dao;



import templates.dao.GenericDaoHibernateImpl;
import templates.objectdb.Template;


public class TemplateDaoImpl extends GenericDaoHibernateImpl<Template, Long>
		implements TemplateDao {

	public TemplateDaoImpl(Class<Template> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}


}
