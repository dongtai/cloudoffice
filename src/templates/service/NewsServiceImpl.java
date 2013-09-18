package templates.service;

import java.util.List;

import org.apache.derby.iapi.services.io.Limit;

import templates.dao.NewsDao;
import templates.dao.NewsTypeDao;
import templates.objectdb.News;
import templates.objectdb.NewsType;

public class NewsServiceImpl implements NewsService {

	
	NewsDao newsDao;
	NewsTypeDao newsTypeDao;
	@Override
	public void delete(Long id) {
		newsDao.delete(newsDao.find(id));
		
	}

	@Override
	public List<News> find(String hql) {
		return newsDao.find(hql);
	}
	
	public List<News> find(String hql,int limit) {
		return newsDao.find(hql, limit);
	}

	
	@Override
	public PageBean queryForPage(String hql, int pagenum, int firstpage, int totalrow) {
		 int totalPage = PageBean.countTotalPage(pagenum, totalrow);//总页数
	        final int beginIndex = PageBean.countOffset(pagenum, firstpage); //当前页开始记录
	        final int everyPage = pagenum;    //每页记录数
	        final int currentPage = PageBean.countCurrentPage(firstpage);
	        List<News> list = newsDao.getUserByPage(hql, beginIndex, everyPage);
	        //把分页信息保存到Bean中
	        PageBean pageBean = new PageBean();
	        pageBean.setPageSize(pagenum);    
	        pageBean.setCurrentPage(currentPage);
	        pageBean.setAllRow(totalrow);
	        pageBean.setTotalPage(totalPage);
	        pageBean.setList(list);
	        pageBean.init();
	        return pageBean;
	}

	@Override
	public News save(News newsInfo) {
		Long id = newsDao.save(newsInfo);
		newsInfo.setNewId(id);
		return newsInfo;
	}

	@Override
	public News update(News newsInfo) {
		newsDao.update(newsInfo);
		return newsInfo;
	}

	public void setNewsDao(NewsDao newsDao) {
		this.newsDao = newsDao;
	}

	@Override
	public News get(Long id) {
		return newsDao.find(id);
	}

	@Override
	public List<NewsType> findType(String hql) {
		return newsTypeDao.find(hql);
	}

	public void setNewsTypeDao(NewsTypeDao newsTypeDao) {
		this.newsTypeDao = newsTypeDao;
	}


}
