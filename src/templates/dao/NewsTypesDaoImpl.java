package templates.dao;

import templates.objectdb.NewsType;

public class NewsTypesDaoImpl extends GenericDaoHibernateImpl<NewsType, Long>
implements NewsTypeDao {

	public NewsTypesDaoImpl(Class<NewsType> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

}
