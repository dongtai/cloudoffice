package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Users;

public interface IUserDAO extends IBaseDAO<Users> {

	/**
	 * 进行同步更新（用户真实姓名，用户头像）
	 * 
	 * @param id
	 *            用户ID
	 * @param image1
	 *            用户头像
	 * @param realName
	 *            用户真实姓名
	 */
	void syncUpdate(Long id, String image1, String realName, String realEmail);

	public void updateCalendarSetting(Users user, boolean calendarSetting);

	/**
	 * 根据关键字进行搜索用户
	 * 
	 * @param key
	 *            关键字
	 * @param cols
	 *            查询的字段
	 * @return
	 */
	List<Users> searchByKey(String key, Long userId, String cols[]);
}
