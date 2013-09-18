package templates.dao;

import templates.objectdb.News;

public class NewsDaoImpl extends GenericDaoHibernateImpl<News, Long>
implements NewsDao{

	public NewsDaoImpl(Class<News> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

}
