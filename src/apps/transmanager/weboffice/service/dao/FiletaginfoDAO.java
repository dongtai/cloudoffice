package apps.transmanager.weboffice.service.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import apps.transmanager.weboffice.databaseobject.Filetaginfo;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.jcr.JCRService;

/**
 */
public class FiletaginfoDAO extends BaseDAO
{
	private static final Log log = LogFactory.getLog(FiletaginfoDAO.class);

	// property constants
	public static final String FILE_NAME = "fileName";
	public static final String COMPANY_ID = "companyId";

	public Filetaginfo findById(java.lang.Long id)
	{
		log.debug("getting Filetaginfo instance with id: " + id);
		try
		{
			Filetaginfo instance = (Filetaginfo) find("com.evermore.weboffice.databaseobject.Filetaginfo", id);
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
		log.debug("finding Filetaginfo instance with property: " + propertyName
				+ ", value: " + value);
		try
		{
			return findByProperty("Filetaginfo", propertyName, value);
		}
		catch (RuntimeException re)
		{
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByFileName(Object fileName)
	{
		return findByProperty(FILE_NAME, fileName);
	}

	public List findByCompanyId(Object companyId)
	{
		return findByProperty(COMPANY_ID, companyId);
	}

	public List findAll()
	{
		log.debug("finding all Filetaginfo instances");
		try
		{
			return findAll("Filetaginfo");
		}
		catch (RuntimeException re)
		{
			log.error("find all failed", re);
			throw re;
		}
	}

	public void deleteFiletaginfoByTagIDAndFile(long tagID, String filePath,
			String companyID)
	{
		log.debug("delete Filetaginfo instance with property: tagID: " + tagID
				+ ", fileName: " + filePath + ",companyId: " + companyID);
		try
		{
			String queryString = "delete Filetaginfo where tagId = ? "
					+ "and fileName = ? and companyId = ?";
			excute(queryString, tagID, filePath, companyID);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	/**
	 * 判断文件是否含有该标签
	 * @param path
	 * @param name
	 * @param uerId
	 * @return
	 */
	public boolean isFileTags(String path, String name, Long userId)
	{
		String queryString = "from Filetaginfo where fileName = ? and taginfo.tagId in " +
				"(select tagId from Taginfo where tag = ? and userinfo.id = ?)";
		List<Filetaginfo> ift= findAllBySql(queryString, path, name, userId);
		return ift != null && ift.size() > 0 ? true : false;
	}
	
	/**
	 * 删除用户文件所有标签
	 * @param filePaths
	 * @param userId
	 */
	public void deleteTags(List<String> filePaths, Long userId)
	{
//		log.debug("delete Filetaginfo instance with property: tagID: " + tagID
//				+ ", fileName: " + filePath + ",companyId: " + companyID);
		try
		{
			String queryString = "delete from Filetaginfo where fileName = ? and taginfo.tagId in (select tagId from Taginfo where userinfo.id = ?)";
			for(int i = 0; i < filePaths.size(); i++)
			{
				excute(queryString, filePaths.get(i), userId);
			}
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除用户文件标签
	 * @param filePaths
	 * @param userId
	 */
	public void deleteLitTags(String filename, List<String> tags, String username)
	{
//		log.debug("delete Filetaginfo instance with property: tagID: " + tagID
//				+ ", fileName: " + filePath + ",companyId: " + companyID);
		try
		{
			String queryString = "delete from Filetaginfo as a where a.fileName = ? and a.companyId = ? and a.taginfo.tagId = (select tagId from Taginfo where tag = ?)";
			for(int i = 0; i < tags.size(); i++)
			{
				excute(queryString, filename, username, tags.get(i));
			}
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 删除用户标签
	 * @param tagName
	 * @param userId
	 */
	public void deleteTTags(List tagName, Long userId)
	{
		//log.debug("delete Filetaginfo instance with property: tagID: " + tagID
				//+ ", fileName: " + filePath + ",companyId: " + companyID);
		try
		{
			String queryString = "delete Taginfo where tag = ? and userinfo.id = ?";
			for(int i = 0; i < tagName.size();i++){
				excute(queryString,tagName.get(i), userId);
			}
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}
	
	/**
	 * 重命名用户标签
	 * @param tagName
	 * @param newName
	 * @param userId
	 */
	public void updateTags(String tagName, String newName, Long userId)
	{
		//log.debug("delete Filetaginfo instance with property: tagID: " + tagID
				//+ ", fileName: " + filePath + ",companyId: " + companyID);
		try
		{
			String queryString = "update Taginfo  set tag = ? where tag = ? and userinfo.id = ?";
			
			excute(queryString, newName, tagName, userId);
			
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}
	
	public List<Object> getFilesByTagID(String loginMail, long tagID,
			String companyID)
	{
		log.debug("finding Filetaginfo instance with property: tagID: "
						+ tagID);
		try
		{
			String queryString = "select fileName from Filetaginfo as model where model.taginfo.tagId=?";
			return findAllBySql(queryString, tagID);

		}
		catch (RuntimeException re)
		{
			log.error("find by property tagID failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public List<Object> getFilesByTagID(String loginMail, long tagID,
			String companyID, int start, int limit, String sort, String dir)
	{
		log.debug("finding Filetaginfo instance with property: tagID: "
						+ tagID);
		try
		{
			String queryString = "select new java.lang.String(fileName) from Filetaginfo as model where model.taginfo.tagId"
					+ "=?";
			
			List<String> filePathList;
			if (sort == null)
			{
				filePathList = findAllBySql(start, limit, queryString, tagID);
			}
			else 
			{
				filePathList = findAllBySql(queryString, tagID);
			}
			if (filePathList.size() == 0)
			{
				return null;
			}
			List<Object> fileinfoList = new ArrayList<Object>();
			try
			{
				JCRService jcrService = (JCRService) ApplicationContext
						.getInstance().getBean(JCRService.NAME);
				fileinfoList = jcrService.getFileinfos(loginMail, filePathList
						.toArray(new String[filePathList.size()]));
			}
			catch (RepositoryException e)
			{
				e.printStackTrace();
			}
			ArrayList list = new ArrayList();
			list.add(fileinfoList.size());
			list.addAll(fileinfoList);
			return list;

		}
		catch (RuntimeException re)
		{
			log.error("find by property tagID failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	/**
	 * 通过文件名及用户id得到数据
	 * @param filePath
	 * @param userid
	 * @return
	 */
	public List<Filetaginfo> getTagsByFile(String filePath, long userid)
	{
		log.debug("finding Filetaginfo instance with property: fileName: "
				+ filePath);
		try
		{
			String queryString = "from Filetaginfo as model where model.fileName"
					+ "=? and model.taginfo.userinfo.id=?";
			return findAllBySql(queryString, filePath, userid);
		}
		catch (RuntimeException re)
		{
			log.error("find by property fileName failed", re);
			re.printStackTrace();
			throw re;
		}
	}
	
	/**
	 * 得到用户所有标签
	 */
	public List<String> getAllTags(long userid)
	{
		log.debug("finding Filetaginfo instance with property: fileName: ");
		try
		{
			String queryString = "select model.tag from Taginfo as model where model.userinfo.id=?";
			return findAllBySql(queryString,userid);
		}
		catch (RuntimeException re)
		{
			log.error("find by property fileName failed", re);
			re.printStackTrace();
			throw re;
		}
	}
	
	/**
	 * 得到用户文件所有标签
	 */
	public List<String> getFileTags(String filename, String username)
	{
		log.debug("finding Filetaginfo instance with property: fileName, username");
		try
		{
			String queryString = "select model.taginfo.tag from Filetaginfo as model where model.companyId=? and model.fileName=?";
			return findAllBySql(queryString, username, filename);
		}
		catch (RuntimeException re)
		{
			log.error("find by property fileName failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public List<String> getFileByTag(String tag, String companyID)
	{
		log.debug("finding Filetaginfo instance with property: tag: " + tag);
		try
		{
			String queryString = "select new java.lang.String(fileName) from Filetaginfo as model where model.taginfo.tag"
					+ "=?";
			return findAllBySql(queryString, tag);

		}
		catch (RuntimeException re)
		{
			log.error("find by property tagID failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public List<String> getSearchPath(String companyID, String tag,
			String filePath)
	{
		log.debug("finding Filetaginfo instance with property: tag: " + tag);
		try
		{
			String queryString = "select new java.lang.String(fileName) from Filetaginfo as model where model.companyId = ? and model.taginfo.tag like ? AND model.fileName like ?";
			return findAllBySql(queryString, companyID, "%" + tag + "%", filePath + "%");

		}
		catch (RuntimeException re)
		{
			log.error("find by property tagID failed", re);
			re.printStackTrace();
			throw re;
		}
	}

	public List<Object> getTagFiles(String loginMail, long creatorID,
			String companyID, int start, int limit, String sort, String dir)
	{

		log.debug("finding Filetaginfo instance with property: tagID: "
				+ creatorID);
		try
		{
			String queryString = "select DISTINCT new java.lang.String(fileName) from Filetaginfo as model where model.taginfo.userinfo.id"
					+ "=?";
			List<String> filePathList;
			if (sort == null)
			{
				filePathList = findAllBySql(start, limit, queryString, creatorID);
			}
			else
			{
				filePathList = findAllBySql(queryString, creatorID);
			}
			if (filePathList.size() == 0)
			{
				return null;
			}
			List<Object> fileinfoList = new ArrayList<Object>();
			try
			{
				JCRService jcrService = (JCRService) ApplicationContext
						.getInstance().getBean(JCRService.NAME);
				fileinfoList = jcrService.getFileinfos(loginMail, filePathList
						.toArray(new String[filePathList.size()]));
			}
			catch (RepositoryException e)
			{
				e.printStackTrace();
			}
			ArrayList list = new ArrayList();
			list.add(fileinfoList.size());
			list.addAll(fileinfoList);
			return list;

		}
		catch (RuntimeException re)
		{
			log.error("find by property tagID failed", re);
			re.printStackTrace();
			throw re;
		}

	}

	public void delByFile(String path, String companyID)
	{
		log.debug("delete ");
		try
		{
			String queryString = "delete Filetaginfo where fileName=?"
					+ " and companyId =?";
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
			String queryString = "delete Filetaginfo where fileName like ? and companyId"
					+ "=?";
			excute(queryString, path + "%", companyID);
		}
		catch (RuntimeException re)
		{
			log.error("delete failed", re);
			throw re;
		}
	}

	public List likeSerch(String property, String value)
	{
		String queryString = "from Filetaginfo as model where model."
				+ property + " like ?";
		return findAllBySql(queryString, value + "%");
	}

	public List<Filetaginfo> findByTagAndFile(long tagid, String file)
	{
		try
		{
			String queryString = "from Filetaginfo as model where model.taginfo.tagId"
					+ "=? and model.fileName =?";
			return findAllBySql(queryString, tagid, file);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
	}

	public void changePath(String srcPath, String desPaths)
	{
		try
		{
			String queryString = "update Filetaginfo as model set model.fileName"
					+ "=? where model.fileName =?";
			excute(queryString, desPaths, srcPath);
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
	}
}