package apps.transmanager.weboffice.service.dwr;

import java.util.Date;
import java.util.List;

import apps.transmanager.weboffice.databaseobject.UserWorkaday;
import apps.transmanager.weboffice.databaseobject.Users;

/**
 * 
 * @author user733(熊明威) 2010-5-28
 * 
 */

public interface IUserWorkadayService
{

	/**
	 * 保存新的日志
	 * 
	 * @param userId
	 *            登录用户
	 * @param title
	 *            工作日志标题
	 * @param contentAm
	 *            上午日志内容
	 * @param contentPm
	 *            下午日志内容
	 * @param date
	 *            日期
	 * @return
	 */
	public boolean saveWorkaday(Users userinfo, String title,
			String contentAm, String contentPm, Date date);

	/**
	 * 更新已有日志
	 * 
	 * @param workadayId
	 *            日志ID
	 * @param title
	 *            工作日志标题
	 * @param contentAm上午日志内容
	 * @param contentPm
	 *            下午日志内容
	 * @return
	 */
	public boolean updateWorkaday(UserWorkaday userWorkaday, String title,
			String contentAm, String contentPm);

	/**
	 * 清楚日志
	 * 
	 * @param workadayId
	 *            日志ID
	 * @return
	 */
	public boolean deleteWorkaday(Long workadayId);

	/**
	 * 通过id查找
	 * 
	 * @param idworkadayId
	 *            日志ID
	 * @return
	 */
	public UserWorkaday findWorkadayByid(long id);

	/**
	 * 根据登录用户和日期获得当前日志
	 * 
	 * @param userId
	 *            登录用户ID
	 * @param date
	 *            日期
	 * @return
	 */
	public UserWorkaday findWorkadayByUserAndDate(long userId, Date date);

	/**
	 * 根据关键字和当前登录用户查询相关工作日志
	 * 
	 * @param userId
	 *            登录用户ID
	 * @param keyWord
	 *            关键字
	 * @return
	 */
	public List<UserWorkaday> findWorkadaysByKeyWord(long userId, String keyWord);

	/**
	 * 根据关键字和起止时间查询相关日志
	 * 
	 * @param userId
	 *            登录用户ID
	 * @param keyWord
	 *            关键字
	 * @param fromDate
	 *            开始时间
	 * @param toDate
	 *            结束时间
	 * @return
	 */
	public List<UserWorkaday> findWorkadaysByKeyWordAndDate(long userId,
			String keyWord, Date fromDate, Date toDate);

}
