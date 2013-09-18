package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.NewsInfo;

public interface INewsInfoDAO extends IBaseDAO<NewsInfo>{

	/**
	 * 根据分类查找所有属于该分类系列下的所有新闻，按最新时间排序
	 */
	List<NewsInfo> getNewsByCategory(String category);
}
