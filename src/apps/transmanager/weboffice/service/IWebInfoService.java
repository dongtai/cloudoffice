package apps.transmanager.weboffice.service;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.NewsInfo;
import apps.transmanager.weboffice.databaseobject.WebInfo;

public interface IWebInfoService{
	
	/**
	 * 添加或更新一个WebInfo
	 */
	void addWebInfo(WebInfo webinfo);
	
	/**
	 * 添加或更新一系列WebInfo
	 */
	void addWebInfoList(List<WebInfo> webInfolist);
	
	/**
	 * 更新订阅人数
	 */
	void updateWebInfoNum (Long id, Long num);
	
	/**
	 *根据订阅的名字搜索WebInfo 
	 */
	WebInfo getWebInfoByWebName(String webname);
	
	/**
	 * 根据Id查找一个WebInfo
	 */
	WebInfo getWebInfoById(Long id);
	
	/**
	 * 根据分类名称，查找一系列WebInfo
	 */
	List<WebInfo> getWebInfoListByCategory(String category);
	
	/**
	 * 查找所有的订阅列表(WebInfo)
	 */
	List<WebInfo> getAllWebInfoList();
	
	/**
	 * 删除一个订阅网站，根据Id
	 */
	void delWebInfo(Long id);
	
	/**
	 * 根据id集合删除一系列订阅网站
	 */
	void delWebInfoList(List<Long> idList);

	/**
	 *  显示我的订阅列表,传入用户id
	 */
	List<WebInfo> getUserWebInfoList(Long id);

	/**
	 * 根据分类(category)，列出新闻
	 */
	List<NewsInfo> getNewsByCategory(String category);

	/**
	 * 点击新闻标题，打开一个新闻,参数是新闻id
	 */
	NewsInfo getNewsInfoById(Long id);

	/**
	 * 增加或者更新一个新闻表
	 */
	void saveOrUpdateNewsInfo(NewsInfo newsinfo);

	/**
	 * 增加或者更新一系列新闻表
	 */
	void saveOrUpdateNewsInfoList(List<NewsInfo> newsinfoList);

	/**
	 * 删除一个新闻列表
	 */
	void delNewsInfo(Long id);

	/**
	 * 删除一系列新闻
	 */
	void delNewsInfoList(List<Long> idList);

	/**
	 * 关注一个订阅栏目
	 */
	void addAttenWebInfo(Long userid, Long webinfoid);
	
	/**
	 * 关注一个订阅栏目
	 * @param userid：用户id
	 * @param webinfoname:栏目名
	 */
	void addAttenWebInfo(Long userid, String webname);

	/**
	 * 取消订阅一个栏目
	 */
	void delAttenWebInfo(Long userid, Long webinfoid);

	/**
	 * 取消订阅一个栏目
	 * @param userid:用户id
	 * @param webname:栏目名
	 */
	void delAttenWebInfo(Long userid, String webname);
	/**
	 * 列出一个订阅栏目下的所有新闻
	 */
	List<NewsInfo> listNewsInWebInfo(Long webinfoid);
	
	/**
	 * 列出一个订阅栏目下的所有新闻
	 * @param webname：订阅栏目的名称
	 * @return
	 */
	List<NewsInfo> listNewsInWebInfo(String webname);
	
}
