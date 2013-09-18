package apps.transmanager.weboffice.dao.impl;

import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.INewsInfoDAO;
import apps.transmanager.weboffice.databaseobject.NewsInfo;

public class NewsInfoDAO extends BaseDAOImpl<NewsInfo> implements INewsInfoDAO{

	@Override
	public List<NewsInfo> getNewsByCategory(String category) {
		String queryString = "select tb1 from NewsInfo as tb1 wehre tb1.webinfo.gid in (select tb2.gid from WebInfo as tb2 where tb2.category =:category) order by tb1.date desc";
		Query query = getSession().createQuery(queryString);
		query.setParameter("category", category);
		return (List<NewsInfo> )query.list();
	}

}
