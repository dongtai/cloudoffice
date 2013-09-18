package templates.service;

import java.util.List;

import templates.dao.NewsAttachedDao;
import templates.objectdb.NewsAttached;

public class NewsAttachedServiceImpl implements NewsAttachedService{

	NewsAttachedDao newsAttachedDao;
	@Override
	public void delete(Long id) {
		newsAttachedDao.delete(newsAttachedDao.find(id));
		
	}

	@Override
	public List<NewsAttached> find(String hql) {
		return newsAttachedDao.find(hql);
	}
	
	public NewsAttached get(Long id) {
		return newsAttachedDao.find(id);
	}
	@Override
	public NewsAttached save(NewsAttached newsAttached) {
		Long id = newsAttachedDao.save(newsAttached);
		newsAttached.setTid(id);
		return newsAttached;
	}

	@Override
	public NewsAttached update(NewsAttached newsAttached) {
		newsAttachedDao.update(newsAttached);
		return newsAttached;
	}

	public void setNewsAttachedDao(NewsAttachedDao newsAttachedDao) {
		this.newsAttachedDao = newsAttachedDao;
	}

}
