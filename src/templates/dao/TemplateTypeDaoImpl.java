package templates.dao;

import templates.dao.GenericDaoHibernateImpl;
import templates.objectdb.TemplateType;

public class TemplateTypeDaoImpl extends GenericDaoHibernateImpl<TemplateType, Long>
implements TemplateTypeDao{
	
	public TemplateTypeDaoImpl(Class<TemplateType> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

}
