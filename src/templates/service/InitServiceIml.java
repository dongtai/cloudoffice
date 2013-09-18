package templates.service;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessResourceFailureException;

import templates.dao.NewsTypeDao;

public class InitServiceIml implements InitService{
	NewsTypeDao newsTypeDao;

	@Override
	public Boolean insert(String sql) {
		try {
			return 	newsTypeDao.insert(sql);
		} catch (DataAccessResourceFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}

	public void setNewsTypeDao(NewsTypeDao newsTypeDao) {
		this.newsTypeDao = newsTypeDao;
	}

}
