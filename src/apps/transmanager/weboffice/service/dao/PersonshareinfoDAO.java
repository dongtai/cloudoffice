package apps.transmanager.weboffice.service.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.Personshareinfo;
import apps.transmanager.weboffice.databaseobject.SignInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.handler.InsertFileLog;

/**
 */
public class PersonshareinfoDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(PersonshareinfoDAO.class);
	public static String SHAREFILE = "shareFile";

	/*
	 * 
	 */
	public void bulkUpdateDelByFileAndSharer(String[] values, Long uid)
	{
		log.debug("bulkUpdateDelByFileAndSharer ");
		try
		{
			if (values != null && values.length > 0)
			{
				String queryString = "delete Personshareinfo as model where shareFile"
						+ " in(";// ?) " + "and
									// model.userinfoBySharerUserId.userId=" +
									// uid;
				for (int i = 0; i < values.length; i++)
				{
					queryString += "\'" + (String) values[i] + "\'";
					if (i + 1 < values.length)
					{
						queryString += ",";
					}
				}
				queryString += ") and model.userinfoBySharerUserId.id="
						+ uid;
				excute(queryString);
			}
		}
		catch (RuntimeException re)
		{
			log.error("bulkUpdateDelByFileAndSharer failed", re);
			throw re;
		}
	}

	/*
	 * 批量保存文件
	 */
	public void saveAll(Collection entities)
	{
		try
		{
			super.saveAll(entities);
		}
		catch (RuntimeException re)
		{
			log.error("save failed", re);
			throw re;
		}
	}

	// public void saveAll(Collection entities)
	// {
	// log.debug("saveAllBySQL Personshareinfo collection instance");
	// try {
	// //Long personShareId;
	// // Userinfo userinfoBySharerUserId; // 被共享的个人
	// Long shareUserID;
	// Long shareowner;
	// Long shareOwnerGroupID;
	// // Userinfo userinfoByShareowner; // 共享的个人
	// // Groupinfo groupinfoOwner; // 共享的部门
	// String shareFile;
	// Integer permit;
	// String companyId;
	// Integer isNew;
	// //共享时间
	// Date date_;
	// //是否是文件夹：0是文件，1是文件夹
	// Integer isFolder;
	// //共享描述信息
	// String shareComment;
	//		
	// if(entities != null && entities.size()>0)
	// {
	// String queryString = "INSERT INTO Personshareinfo" +
	// "(sharerUserID,shareowner," +
	// "shareFile,permit,companyId,isNew,date_,isFolder,shareComment) " +
	// "VALUES";//(?,?,?,?,?,?,?,?,?,?,?)";
	// ArrayList arr = (ArrayList)entities;
	// for(int i=0;i<arr.size();i++)
	// {
	// Personshareinfo share =(Personshareinfo)arr.get(i);
	// //personShareId = share.getPersonShareId();
	// shareUserID = share.getUserinfoBySharerUserId().getUserId(); // 被共享的个人
	// shareowner = share.getUserinfoByShareowner().getUserId();
	// // 共享的个人
	// // if(share.getGroupinfoOwner().getGroupId() == null)
	// // {
	// // shareOwnerGroupID = -100;
	// // }
	// // else
	// // {
	// // shareOwnerGroupID =share.getGroupinfoOwner().getGroupId();
	// // }// 共享的部门
	// shareFile = share.getShareFile();
	// permit = share.getPermit();
	// companyId = share.getCompanyId();
	// isNew = share.getIsNew();
	// //共享时间
	// date_ = share.getDate();
	// String date1 = new SimpleDateFormat("yyyy-MM-dd").format(date_);
	// //是否是文件夹：0是文件，1是文件夹
	// isFolder = share.getIsFolder();
	// //共享描述信息
	// shareComment = share.getShareComment();
	// queryString+="("+shareUserID+","+shareowner
	// +",\'"+shareFile+"\',"+permit+",\'"+companyId+"\',"+isNew
	// +",\'"+date1+"\',"+isFolder+",\'"+shareComment+"\')";
	// if(i+1<arr.size())
	// {
	// queryString+=",";
	// }
	// }
	// System.out.println(queryString);
	// QueryDb query = new QueryDb();
	// try
	// {
	// query.saveOrUpdate(queryString);
	// }
	// catch(Exception e)
	// {
	// e.printStackTrace();
	// }
	// // query = null;
	// // queryString +=";";
	// }
	// // Query queryObject =
	// getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(queryString);
	// // queryObject.setParameter(0, personShareId);
	// // queryObject.setParameter(1, userinfoBySharerUserId);
	// // queryObject.setParameter(1, userinfoBySharerUserId);
	// // queryObject.executeUpdate();
	//		
	//		
	//		
	//		
	// // transientInstance.setIsNew(0);
	// // getHibernateTemplate().saveOrUpdateAll(entities);
	// // log.debug("save successful");
	// } catch (RuntimeException re) {
	// log.error("save failed", re);
	// throw re;
	// }
	// }

	public void save(Personshareinfo transientInstance)
	{
		try
		{
			if (transientInstance.getPersonShareId() == null)
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

	public void deleteByID(long id)
	{
		log.debug("delete by id");
		try
		{
			String queryString = "delete Personshareinfo where personShareId = "
					+ id;
			excute(queryString);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public void delByFile(String path)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Personshareinfo where shareFile"
					+ "=?";
			excute(queryString,  path);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public void delByOwnerAndFile(long userID, String path)
	{
		log.debug("delete ");
		try
		{
			String SQL="select model.firstshareid from Personshareinfo as model where model.shareFile =? and model.firstshareid is not null and model.firstshareid !=0 ";
			List<Long> list=findAllBySql(SQL, path);
			boolean ismy=false;
			if (list==null || list.size()==0)//原来老的共享
			{
				ismy=true;
			}
			else
			{
				
				for (int i=0;i<list.size();i++)
				{
					if (userID==list.get(i).longValue())
					{
						ismy=true;
						break;
					}
				}
			}
			if (ismy)
			{
				String queryString = "delete Personshareinfo where shareFile = ?";
				excute(queryString,  path);
			}
			else
			{
				String queryString = "delete Personshareinfo where userinfoByShareowner.id"
					+ "= ?  and shareFile = ?";
				excute(queryString,  userID, path);
			}
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public void delByGroupOwnerAndFile(long groupid, String path)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Personshareinfo where groupinfoOwner.groupId"
					+ "= ?  and shareFile = ?";
			excute(queryString, groupid, path);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public void delByFile(String path, String companyID)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Personshareinfo where shareFile"
					+ "=? and companyID=?";
			excute(queryString, path, companyID);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public void delByLikeFile(String path, String companyID)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Personshareinfo where shareFile like ? and companyID"
					+ "=?";
			excute(queryString, path + "%", companyID);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public Personshareinfo findById(java.lang.Long id)
	{
		log.debug("getting Personshareinfo instance with id: " + id);
		try
		{
			Personshareinfo instance = (Personshareinfo) find("com.evermore.weboffice.databaseobject.Personshareinfo", id);
			return instance;
		}
		catch (RuntimeException re)
		{
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value)
	{
		log.debug("finding Personshareinfo instance with property: "
				+ propertyName + ", value: " + value);
		try
		{
			return findByProperty("Personshareinfo", propertyName, value);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public boolean findByShareFile(String path)
	{
		try
		{
			String queryString = "select count(*) from Personshareinfo as model where model.shareFile = ?";			
			List l = findAllBySql(queryString, path);
			if (l != null && l.size() > 0)
			{
				return ((Long)l.get(0)).intValue() > 0;
			}
			else
			{
				return false;
			}

		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}

	}

	public List findAll()
	{
		log.debug("finding all Personshareinfo instances");
		try
		{
			return findAll("Personshareinfo");
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;
		}
	}

	public List<String> getSharePath(String path)
	{
		String query = " select ps.shareFile from Personshareinfo ps where LOCATE(ps.shareFile, ?) > 0 ";
		List ret = findAllBySql(query, path);
		return ret;
	}
	
	public Personshareinfo findByShareAndPath(long shareID, String path)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoByShareowner: "
						+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from Personshareinfo as model where userinfoByShareowner.id"
					+ "= ?  and model.shareFile = ?";
			
			List<Personshareinfo> l = findAllBySql(queryString, shareID, path);
			if (l.size() > 0)
			{
				return l.get(0);
			}
			return null;
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}

	}

	public Personshareinfo findByOtherShareAndPath(long shareID, String path)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id = ? and LOCATE(model.shareFile, ?) > 0";
//			String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id = ? and LOCATE(model.shareFile, ?) > 0";
			
			List<Personshareinfo> l = findAllBySql(queryString, shareID,path);
			if (l.size() > 0)
			{
				return l.get(0);
			}
			return null;
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
		// return null;

	}

	public List<String> findPathByProperty(String propertyName, Object value)
	{
		try
		{
			String queryString = "select distinct model.shareFile from Personshareinfo as model where model."
					+ propertyName + "= ?";
			return findAllBySql(queryString, value);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List likeSerch(String property, String value)
	{
		try
		{
			String queryString = "from Personshareinfo as model where model."
					+ property + " = ?";
			return findAllBySql(queryString, value);
		}
		catch (RuntimeException re)
		{
			log.error("likeSearch by property name failed", re);
			throw re;
		}
	}

	public List<Users> getShareOwner(long shareuserID)
	{
		try
		{
			String queryString = "select distinct model.userinfoByShareowner from Personshareinfo as model where model.userinfoBySharerUserId.id"
					+ "=?";
			return findAllBySql(queryString, shareuserID);
		}
		catch (RuntimeException re)
		{
			log.error("likeSearch by property name failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public List<Personshareinfo> getShareinfoBySharer(long shareuserID,int isall)
	{
		try
		{
			if (isall==1)
			{
				String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
					+ "=? ";
				return findAllBySql(queryString, shareuserID);
			}
			else
			{
				java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
				java.util.Calendar calendar=java.util.Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis()-7*24*60*60*1000);
				String date=df.format(calendar.getTime());
				String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
						+ "=? and model.date>'"+date+"' ";
				return findAllBySql(queryString, shareuserID);
			}
		}
		catch (RuntimeException re)
		{
			log.error("getShareinfoBySharer failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public List<Personshareinfo> getByOwnerAndShare(long ownerID, long shareID)
	{

		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoByShareowner.id = ? and model.userinfoBySharerUserId.id =?";
			return findAllBySql(queryString, ownerID, shareID);
		}
		catch (RuntimeException re)
		{
			log.error("likeSearch by property name failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public List<Personshareinfo> getByGroupOwnerAndShare(long ownerID,
			long shareID)
	{

		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id =? and model.groupinfoOwner.groupId = ? ";
			return findAllBySql(queryString, shareID, ownerID);
		}
		catch (RuntimeException re)
		{
			log.error("likeSearch by property name failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public Personshareinfo findByShareOwnerAndPath(long shareID, String path)
	{
		log	.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoByShareowner.id"
					+ "= ?  and model.shareFile = ?";
			 
			List<Personshareinfo> l = findAllBySql(queryString, shareID, path);
			if (l.size() > 0)
			{
				return l.get(0);
			}
			return null;
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}

	}

	public List<Personshareinfo> findByOwnerAndPath(long shareID, String path)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoByShareowner.id"
					+ "= ?  and model.shareFile = ?";
			return findAllBySql(queryString, shareID, path);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Personshareinfo> findByOwnerlikePath(long shareID, String path)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", shareFile: " + path);
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoByShareowner.id"
					+ "= ? and model.shareFile like ?";
			return findAllBySql(queryString, shareID, path);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Personshareinfo> findByOwner(long ownerID)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ ownerID);
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoByShareowner.id"
					+ "= ?";

			return findAllBySql(queryString,  ownerID);

		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Personshareinfo> findByShareOwner(long userID, long ownerID)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ ownerID);
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
					+ "= ?  and model.userinfoByShareowner.id = ?";
			return findAllBySql(queryString, userID, ownerID);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<Personshareinfo> findByShare(long userID,Long ownerid)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ userID);
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
					+ "= ?";
			if (ownerid!=null && ownerid>0)
			{
				queryString+=" and model.userinfoByShareowner.id="+ownerid.longValue();
			}
			return findAllBySql(queryString, userID);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	//他人共享中得到所有的或经过筛选的结果
	public List<Personshareinfo> findFilterByShare(long userID,List<Long> ownerids)
	{
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
					+ "= ?";
			if (ownerids!=null && ownerids.size()>0)
			{
				if(ownerids.get(0) > 0)
				{
					String ownerId = ownerids.get(0).toString();
					for (int i = 1;i<ownerids.size();i++) {
						ownerId += (","+ownerids.get(i).toString());
					}
					queryString+=" and userinfoByShareowner.id in ("+ownerId+")";
				}
				
			}
//			if(searchName != null)
//			{
//				queryString += " and (REVERSE(LEFT(REVERSE(shareFile),INSTR(REVERSE(shareFile),'/'))) like '%"+searchName+"%')";
//			}
//			if(time != null)
//			{
//				String[] times = time.split("/");
//				Date startTime = new Date(Long.valueOf(times[0]));
//				Date endTime = new Date(Long.valueOf(times[1]));
//				GregorianCalendar endD = new GregorianCalendar();
//				endD.setTime(endTime);				
//				endD.add(Calendar.DAY_OF_MONTH,1);
//				String sTime = String.format("%1$tF %1$tT", startTime);
//				String eTime = String.format("%1$tF %1$tT", endD);
//				queryString += " and model.date >='"+sTime+"' and model.date <='"+eTime+"'";				
//			}
			return findAllBySql(queryString,userID);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public List<Users> findByShareUsers(long userID,Long ownerid)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ userID);
		try
		{
			String queryString = "select distinct model.userinfoByShareowner from Personshareinfo as model where model.userinfoBySharerUserId.id"
					+ "= ?";
			if (ownerid!=null && ownerid>0)
			{
				queryString+=" and model.userinfoByShareowner.id="+ownerid.longValue();
			}
			return findAllBySql(queryString, userID);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public List<Personshareinfo> findByMemberAndNew(long shareID, int isNew)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", isNew: " + isNew);
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
					+ "= ?  and model.isNew = ?";
			return findAllBySql(queryString, shareID, isNew);
		}
		catch (RuntimeException re)
		{
			log.error("find by isNew failed", re);
			throw re;
		}
	}

	public List<Personshareinfo> findByMemberAndPath(String path, long shareID)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", path: " + path);
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
					+ "= ?  and model.shareFile = ?";
			return findAllBySql(queryString, shareID, path);
		}
		catch (RuntimeException re)
		{
			log.error("find by path failed", re);
			throw re;
		}
	}
	
	public List<Personshareinfo> findByUserIDAndPath(String path, long userID)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoByShareowner: "
						+ userID + ", path: " + path);
		try
		{
			String queryString = "from Personshareinfo as model where model.userinfoByShareowner.id"
					+ "= ?  and model.shareFile = ?";
			return findAllBySql(queryString, userID, path);
		}
		catch (RuntimeException re)
		{
			log.error("find by path failed", re);
			throw re;
		}
	}

	public List<Personshareinfo> findPathByMemberAndNew(Long shareID,
			int isNew, int type, Integer firstIndex, Integer pageCount)
	{
		log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
						+ shareID + ", isNew: " + isNew);
		try
		{
			StringBuffer queryString = new StringBuffer(
					"from Personshareinfo as model where 1=1");
			if (null != shareID)
			{
				queryString.append(
						" and model.userinfoBySharerUserId.id = ").append(
						shareID);
			}

			// if(-1!= isNew)
			// {
			// queryString.append(" and model.isNew = ").append(isNew);
			// }

			if (-1 != type)
			{
				queryString.append(" and model.isFolder = ").append(type);
			}
			queryString.append(" order by model.date desc");
			
			return findAllBySql(firstIndex != null ? firstIndex : -1, pageCount != null ? pageCount : -1, queryString.toString());
		}
		catch (RuntimeException re)
		{
			log.error("find by isNew failed", re);
			throw re;
		}

	}

	// 最近共享10条文件,只显示一周的文件
	public List findLatelyFile(Long userID)
	{
		// log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
		// + shareID + ", isNew: " + isNew);
		try
		{
			java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
			java.util.Calendar calendar=java.util.Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis()-7*24*60*60*1000);
			
			String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
					+ " =?  and model.date>?  order by model.id desc";
			return findAllBySql(queryString,  userID,calendar.getTime());
		}
		catch (RuntimeException re)
		{
			log.error("find by isNew failed", re);
			throw re;
		}
	}

	// 最近用户共享的文件
	public List findLatelyUserFile(Long userID, Long shareID)
	{
		// log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
		// + shareID + ", isNew: " + isNew);
		
		try
		{
			java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
			java.util.Calendar calendar=java.util.Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis()-7*24*60*60*1000);
			String date=df.format(calendar.getTime());
			String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id "
					+ "= ? and model.userinfoByShareowner.id "
					+ "= ?  and model.date>'"+date+"'  order by model.id desc";
			return findAllBySql(queryString, userID, shareID);
		}
		catch (RuntimeException re)
		{
			log.error("find by isNew failed", re);
			throw re;
		}
	}

	// 最近共享用户
	public List findLatelyUser(Long userID)
	{
		// log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
		// + shareID + ", isNew: " + isNew);
		try
		{
			String queryString = "select distinct model.userinfoByShareowner from Personshareinfo as model where model.userinfoBySharerUserId.id "
					+ "= ?  order by model.id desc";
			// String queryString =
			// "from Personshareinfo as model where model.userinfoBySharerUserId.userId "+"= ?"+" and model.isNew = 0 order by model.id desc limit 0,10"
			// ;
			return findAllBySql(0, 10, queryString, userID);
		}
		catch (RuntimeException re)
		{
			log.error("find by isNew failed", re);
			throw re;
		}
	}

	public void delByFileAndSharer(String path, Long sharerID)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Personshareinfo as model where shareFile"
					+ "=? and model.userinfoBySharerUserId.id =?";
			excute(queryString, path, sharerID);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}
	
	public boolean insertSign(Long userId,String fileName,String filePath,String content,String signType)
	{
		boolean result = false;
		try
        {
	    		SignInfo signinfo = new SignInfo();
	    		signinfo.setFileName(fileName);
	    		signinfo.setFilePath(filePath);
	    		Users signer= new Users();
	    		signer.setId(userId);
	    		signinfo.setSigner(signer);
	    		signinfo.setCreateDate(new Date());
	    		signinfo.setContent(content);
	    		signinfo.setSignType(signType);
	    		save(signinfo);
	    	result = true;
        }
        catch(RuntimeException re)
        {
            log.error("insert sign failed", re);
            throw re;
        }
		return result;
	}
	
	public List findAllSign(Long userId,String filePath,String signType)
	{
		 log.debug("find All signinfo ");
	        try
	        {
	            	String queryString = "from SignInfo as v where v.filePath=? and v.signType=? order by v.createDate desc";
	            	//List list = (List)excute(queryString,filePath);	            	            	
	            	return findAllBySql(queryString,filePath,signType);
	        }
	        catch(RuntimeException re)
	        {
	            log.error("find All SignInfo failed", re);
	            throw re;
	        }
	}
	
	public List getAllSignList(Long userId,String filePath)
	{
		log.debug("getAllSignList ");
        try
        {
            	String queryString = "from SignInfo as v where v.filePath=? order by v.createDate desc";
            	//List list = (List)excute(queryString,filePath);	            	            	
            	return findAllBySql(queryString,filePath);
        }
        catch(RuntimeException re)
        {
            log.error("find All SignInfo failed", re);
            throw re;
        }
	}
	
	
	public boolean hasSign(Long userId,String filePath)
	{
		log.debug("hasSign  ");
        try
        {
            	String queryString = "from SignInfo as v where v.filePath=? and signer.id =? order by v.createDate desc";
            	List list = (List)findAllBySql(queryString,filePath,userId);	            

            	if(null!=list&&!list.isEmpty())
            	{
            		return true;
            	}
        }
        catch(RuntimeException re)
        {
            log.error("hasSign failed", re);
            throw re;
        }
        return false;
	}
	public String getSignMessage(Long userId,String filePath)
	{
		log.debug("getSignMessage ");
		String result = null;
        try
        {
            	String queryString = "from SignInfo as v where v.filePath=? and signer.id =? order by v.createDate desc";
            	List list = (List)findAllBySql(queryString,filePath,userId);	
            	if(null!=list&&!list.isEmpty())
            	{
            		SignInfo sign = (SignInfo)list.get(list.size()-1);
            		result = sign.getContent();
            	}
        }
        catch(RuntimeException re)
        {
            log.error("getSignMessage failed", re);
            throw re;
        }
        return result;
	}
	
	public SignInfo findSignById(Long id) {
		log.debug("getting SignInfo instance with id: " + id);
		try {
			SignInfo instance = (SignInfo) find("com.evermore.weboffice.databaseobject.SignInfo", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public void delAllSign(String filePath)
	{
		 log.debug("find All signinfo ");
	        try
	        {
	            	String queryString = "delete from SignInfo as v where v.filePath=? ";
	            	excute(queryString,filePath);	
	        }
	        catch(RuntimeException re)
	        {
	            log.error("find All SignInfo failed", re);
	            throw re;
	        }
	}
	
    public List<Personshareinfo> getShareinfoBySharer(long shareuserID, String approveFlag)
    {
        try
        {
            String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
                + "=? and model.approve=?";
            //        Query queryObject = getSessionFactory().getCurrentSession().createQuery(queryString);
            //        queryObject.setParameter(0, shareuserID);
            //        queryObject.setParameter(1, approveFlag);
            //        return queryObject.list();
            return findAllBySql(queryString, shareuserID, approveFlag);
        }
        catch(RuntimeException re)
        {
            log.error("likeSearch by property name failed", re);
            re.printStackTrace();
            throw re;
        }
    }
	
    public List findLatelyUserFile(Long userID, Long shareID, String approveFlag)
    {
        //      log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
        //              + shareID + ", isNew: " + isNew);
        try
        {
            String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id "
                + "= ?"
                + " and model.userinfoByShareowner.id "
                + "= ?"
                + " and model.approve= ? and model.isFolder=0 order by model.id desc";
//            Query queryObject = getHibernateTemplate().getSessionFactory().getCurrentSession()
//                .createQuery(queryString);
//            queryObject.setParameter(0, userID);
//            queryObject.setParameter(1, shareID);
//            queryObject.setParameter(2, approveFlag);
//            return queryObject.list();
            return findAllBySql(queryString, userID, shareID, approveFlag);
        }
        catch(RuntimeException re)
        {
            log.error("find by isNew failed", re);
            throw re;
        }
    }
    
    public List findLatelyFile(Long userID, String approveFlag)
    {
        //      log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
        //              + shareID + ", isNew: " + isNew);
        try
        {
            String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
                + " =?" + " and model.approve=? and model.isFolder=0 order by model.id desc";
//            Query queryObject = getHibernateTemplate().getSessionFactory().getCurrentSession()
//                .createQuery(queryString);
//            queryObject.setParameter(0, userID);
//            queryObject.setParameter(1, approveFlag);
//            return queryObject.list();
            return findAllBySql(queryString, userID, approveFlag);
        }
        catch(RuntimeException re)
        {
            log.error("find by isNew failed", re);
            throw re;
        }
    }
    
    public List findMyLatelyFile(Long userID,String approveFlag)
    {
//      log.debug("finding PersonShareinfo instance with property: userinfoBySharerUserId: "
//              + shareID + ", isNew: " + isNew);
            try
            {
                String queryString = "from Personshareinfo as model where model.userinfoByShareowner.id"+" =?"+ " and model.approve=? and model.isFolder=0 order by model.id desc" ;                
//                Query queryObject = getHibernateTemplate().getSessionFactory().getCurrentSession()
//                    .createQuery(queryString);
//                queryObject.setParameter(0, userID);
//                queryObject.setParameter(1, approveFlag);
//                return queryObject.list();
                return findAllBySql(queryString, userID, approveFlag);
            }
            catch(RuntimeException re)
            {
                log.error("find by findMyLatelyFile failed", re);
                throw re;
            }       
    }
    
    public boolean reviewOrBack(Long userID,String[] path,String flag,String comment,String approveResult)
    {
        boolean result = false;
                     
         try
            {
                
                if(null!=path&&path.length>0)
                {
                    for(int i=0;i<path.length;i++)
                    {
                        String queryString = "update Personshareinfo as model set model.approve =?, model.approveComment=?,model.approveResult=? where model.shareFile=?" + " and model.userinfoBySharerUserId.id" + "=?";
//                        Query queryObject = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(queryString);
//                        queryObject.setParameter(0, flag);
//                        queryObject.setParameter(1, comment);
//                        queryObject.setParameter(2, approveResult);
//                        queryObject.setParameter(3, path[i]);
//                        queryObject.setParameter(4, userID);
//                        queryObject.executeUpdate(); 
                        excute(queryString, flag, comment, approveResult, path[i], userID);
                    }
//                    ShareFileTip queryTip=new ShareFileTip();
//                    queryTip.insertFileListinfo(path,userID);
//                    ArrayList fid = (ArrayList)queryTip.queryFileListID(path);  
                    if("1".equals(flag))
                    {
//                        queryTip.insertFileListLog(fid,userID,23,approveResult,comment);  
                        Thread receiveT = new Thread(new InsertFileLog(path, userID, 23,approveResult,comment,null,null));
                		receiveT.start();
                    }
                    else if("0".equals(flag))
                    {
//                        queryTip.insertFileListLog(fid,userID,24,approveResult,comment);
                        Thread receiveT = new Thread(new InsertFileLog(path, userID, 24,approveResult,comment,null,null));
                		receiveT.start();
                    }
                    
                }
                result = true;
            }
            catch(RuntimeException re)
            {
                log.error("delete failed", re);
                throw re;
            }
        
        return result;
    }
    
    public List findAmend(Long userID, String path)
    {
        try
        {
            String queryString = "from Personshareinfo as model where model.userinfoBySharerUserId.id"
                + " =?" + " and model.shareFile=? and model.isFolder=0 order by model.id desc";
            //                Query queryObject = getHibernateTemplate().getSessionFactory().getCurrentSession()
            //                    .createQuery(queryString);
            //                queryObject.setParameter(0, userID);
            //                queryObject.setParameter(1, approveFlag);
            //                return queryObject.list();
            return findAllBySql(queryString, userID, path);
        }
        catch(RuntimeException re)
        {
            log.error("find by findMyLatelyFile failed", re);
            throw re;
        }
    }
}