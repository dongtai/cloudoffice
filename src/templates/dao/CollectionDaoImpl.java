package templates.dao;

import templates.dao.GenericDaoHibernateImpl;
import templates.objectdb.Collection;

public class CollectionDaoImpl extends GenericDaoHibernateImpl<Collection, Long>
implements CollectionDao {

	public CollectionDaoImpl(Class<Collection> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

}
