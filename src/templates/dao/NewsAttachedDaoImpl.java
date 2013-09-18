package templates.dao;

import templates.objectdb.NewsAttached;

public class NewsAttachedDaoImpl extends GenericDaoHibernateImpl<NewsAttached, Long>
implements NewsAttachedDao{

	public NewsAttachedDaoImpl(Class<NewsAttached> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

}
