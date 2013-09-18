package apps.transmanager.weboffice.service.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.ReviewFilesInfo;
import apps.transmanager.weboffice.databaseobject.ReviewInfo;

public class ReviewinfoDAO extends BaseDAO {
	//无锡信电局版本
	private static final Log log = LogFactory.getLog(ReviewInfo.class);
	
	public void save(ReviewInfo transientInstance)
	{
		try
		{
			if (transientInstance.getId() == null)
			{
				super.save(transientInstance);
			}
			else
			{
				update(transientInstance);
			}
		}
		catch (RuntimeException re)
		{
			log.error("save failed", re);
			throw re;
		}
	}
	
	/*
	 * 通过用户id获取送审的文件总数
	 */
	public Long getReviewInfosOfSendNumsById(long userId)
	{
		try 
		{
			String sql = "select count(ri) from ReviewInfo ri where ri.sender.id = ? and ri.count > 0";
			return getCountBySql(sql, userId);
		} 
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	/*
	 * 通过用户id获取送审的文件列表
	 */
	public List<ReviewInfo> getReviewInfosOfSendById(long userId)
	{
		try 
		{
			String sql = "select ri from ReviewInfo ri where ri.sender.id = ? and ri.count > 0";
			return findAllBySql(sql, userId);
		} 
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	
	/*
	 * 通过用户id获取审结的文件总数
	 */
	public Long getReviewInfosOfFiledNumsById(long userId)
	{
		try 
		{
			String sql = "select count(ri) from ReviewInfo ri where ri.sender.id = ? and ri.count = 0";
			return getCountBySql(sql, userId);
		} 
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	/*
	 * 通过用户id获取审结的文件列表
	 */
	public List<ReviewInfo> getReviewInfosOfFiledById(long userId)
	{
		try 
		{
			String sql = "select ri from ReviewInfo ri where ri.sender.id = ? and ri.count = 0";
			return findAllBySql(sql, userId);
		} 
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	
	/*
	 * 通过用户id获取待审的文件总数
	 */
	public Long getReviewInfosOfTodoNumsById(long userId)
	{
		try 
		{
			String sql = "select count(rfi) from ReviewFilesInfo rfi where rfi.reviewer.id = ? and rfi.state = 0";
			return getCountBySql(sql, userId);
		} 
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	/*
	 * 通过用户id获取待审的文件列表
	 */
	public List<ReviewFilesInfo> getReviewInfosOfTodoById(long userId)
	{
		try 
		{
			String sql = "select rfi from ReviewFilesInfo rfi where rfi.reviewer.id = ? and rfi.state = 0";
			return findAllBySql(sql, userId);
		} 
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	/*
	 * 通过用户id获取已审的文件总数
	 */
	public Long getReviewInfosOfDoneNumsById(long userId)
	{
		try 
		{
			String sql = "select count(rfi) from ReviewFilesInfo rfi where rfi.reviewer.id = ? and rfi.state = 1";
			return getCountBySql(sql, userId);
		} 
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	/*
	 * 通过用户id获取已审的文件列表
	 */
	public List<ReviewFilesInfo> getReviewInfosOfDoneById(long userId)
	{
		try 
		{
			String sql = "select rfi from ReviewFilesInfo rfi where rfi.reviewer.id = ? and rfi.state = 1";
			return findAllBySql(sql, userId);
		} 
		catch (RuntimeException re)
		{
			log.error("find failed", re);
			throw re;
		}
	}
	
	/*
	 * 通过reviewInfoId查找单个文件的审阅信息
	 * type 查找的类型
	 * 0：通过reviewinfoId查找审阅详情
	 * 1：通过一个文件的reviewfileinfoId查找其审阅详情
	 * 2：查找一个待审的文件的reviewfileinfo
	 */
	public List<ReviewFilesInfo> getReviewFilesInfosById(Long reviewInfoId,int type)
	{
		try 
		{
			String sql = "";
			if(type == 0)
			{
				sql = "select rfi from ReviewFilesInfo rfi where rfi.reviewinfo.id = ?";				
			}else if(type == 1)
			{
				sql = "select rfi from ReviewFilesInfo rfi where rfi.reviewinfo.id = (select rfi.reviewinfo.id from ReviewFilesInfo rfi where rfi.id = ?)";
			}else
			{
				sql = "select rfi from ReviewFilesInfo rfi where rfi.id = ?";
			}
			return findAllBySql(sql, reviewInfoId);
		} 
		catch (RuntimeException re) {
			log.error("find failed", re);
			throw re;
		}
	}
	
	/*
	 * 通过reviewInfoId查找母文件的审阅信息
	 */
	public List<ReviewInfo> getReviewInfosById(Long reviewFilesInfoId)
	{
		try 
		{
			String sql = "";
				sql = "select ri from ReviewInfo ri,ReviewFilesInfo rfi where rfi.reviewinfo.id = ri.id and rfi.id = ?";
			return findAllBySql(sql, reviewFilesInfoId);
		} 
		catch (RuntimeException re) {
			log.error("find failed", re);
			throw re;
		}
	}
	
	/*
	 * 获取reviewFilesInfo的权限
	 */
	public int getPermit(String path,long userId)
	{
		try 
		{
			String sql = "";
				sql = "select rfi from ReviewFilesInfo rfi where rfi.documentPath = ? and rfi.reviewer.id = ?";
			List<ReviewFilesInfo> list = (List<ReviewFilesInfo>)findAllBySql(sql, path,userId);
			if(list != null && list.size() > 0)
			{
				ReviewFilesInfo info=list.get(0);
				if (info.getState()==null || info.getState().intValue()<1)//只有待审的才判断权限
				{
					return info.getPermit();
				}
				else
				{					
					return (Constant.ISREAD | Constant.ISDOWN);
				}
			}else{
				return 0;
			}
		} 
		catch (RuntimeException re) {
			log.error("find failed", re);
			throw re;
		}
	}
}
