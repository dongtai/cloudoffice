package apps.transmanager.weboffice.dao;

import apps.transmanager.weboffice.databaseobject.WebInfo;


public interface IWebInfoDAO extends IBaseDAO<WebInfo>{
	
	/**
	 * 更新订阅人数
	 */
	void updateWebInfoNum (Long Id, Long num);
	
}
