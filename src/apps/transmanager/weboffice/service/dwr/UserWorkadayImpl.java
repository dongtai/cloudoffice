package apps.transmanager.weboffice.service.dwr;

import java.util.Date;
import java.util.List;

import apps.transmanager.weboffice.databaseobject.UserWorkaday;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.dao.UserWorkadayDAO;

/**
 * 
 * @author user733（熊明威） 2010-5-28
 * 
 */
public class UserWorkadayImpl implements IUserWorkadayService
{

	private UserWorkadayDAO userWorkadayDao;
	

	public UserWorkadayDAO getUserWorkadayDao()
	{
		return userWorkadayDao;
	}

	public void setUserWorkadayDao(UserWorkadayDAO userWorkadayDao)
	{
		this.userWorkadayDao = userWorkadayDao;
	}

	public UserWorkaday findWorkadayByUserAndDate(long userId, Date date)
	{

		return userWorkadayDao.getByUserAndDate(userId, date);
	}

	public boolean saveWorkaday(Users userinfo, String title,
			String contentAm, String contentPm, Date date)
	{
		try
		{
			UserWorkaday userWorkaday = new UserWorkaday();
			userWorkaday.setUserinfo(userinfo);
			userWorkaday.setTitle(title);
			userWorkaday.setContentAm(contentAm);
			userWorkaday.setContentPm(contentPm);
			userWorkaday.setDate(date);
			userWorkadayDao.save(userWorkaday);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public boolean updateWorkaday(UserWorkaday userWorkaday, String title,
			String contentAm, String contentPm)
	{
		try
		{
			userWorkaday.setTitle(title);
			userWorkaday.setContentAm(contentAm);
			userWorkaday.setContentPm(contentPm);
			userWorkadayDao.save(userWorkaday);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public List<UserWorkaday> findWorkadaysByKeyWord(long userId, String keyWord)
	{
		return userWorkadayDao.getByKeyWord(keyWord, userId);
	}

	public List<UserWorkaday> findWorkadaysByKeyWordAndDate(long userId,
			String keyWord, Date fromDate, Date toDate)
	{
		return userWorkadayDao.getByKeyAndDate(keyWord, fromDate, toDate,
				userId);
	}

	public boolean deleteWorkaday(Long workadayId)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public UserWorkaday findWorkadayByid(long id)
	{
		return userWorkadayDao.findById(id);
	}

}
