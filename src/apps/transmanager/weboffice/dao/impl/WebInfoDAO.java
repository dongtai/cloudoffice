package apps.transmanager.weboffice.dao.impl;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IWebInfoDAO;
import apps.transmanager.weboffice.databaseobject.WebInfo;


public class WebInfoDAO extends BaseDAOImpl<WebInfo> implements IWebInfoDAO{

	@Override
	//更新订阅人数
	public void updateWebInfoNum(Long Id, Long num) {
		StringBuffer queryString = new StringBuffer(" update WebInfo as t set t.num=:num where t.gid=:gid");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("num", num);
		query.setParameter("gid", Id);
		query.executeUpdate();
	}

}
