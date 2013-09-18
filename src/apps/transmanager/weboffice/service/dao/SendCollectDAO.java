package apps.transmanager.weboffice.service.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import apps.transmanager.weboffice.databaseobject.caibian.CollectEdit;
import apps.transmanager.weboffice.databaseobject.caibian.CollectEditSend;



/**
 * 为报送和采编增加的类
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class SendCollectDAO extends BaseDAO
{ 
	
	/**
	 * 获取用户报送的记录
	 * @param path
	 * @return
	 */
	public CollectEditSend getSendCollectEdit(String path)
    {
    	
    	String queryString = " select c from CollectEditSend c where c.filePath = ? and c.del = false ";
		CollectEditSend ret = (CollectEditSend)findOneObjectBySql(queryString, path);	
		return ret; 	
    }
	
	/**
	 * 获取用户报送的记录
	 * @param path
	 * @return
	 */
	public CollectEditSend getSendCollectEdit(String path, String userName)
    {
    	
    	String queryString = " select c from CollectEditSend c where c.filePath = ? and c.del = false and c.userName = ? ";
		CollectEditSend ret = (CollectEditSend)findOneObjectBySql(queryString, path, userName);	
		return ret; 	
    }
	
	public void delSendCollectEdit(String path, String userName)
	{
		String queryString = " delete CollectEditSend  where filePath = ? and userName = ? ";
		excute(queryString, path, userName);	
	}
	
	/**
	 * 获取所有用户报送的记录，为采编而用
	 * @param userId
	 * @param start
	 * @param count
	 * @param sort
	 * @param dir
	 * @return
	 */
	public List<CollectEditSend> getSendCollectEdit(int start, int count, String sort, String dir)
    {
    	
    	String queryString = " select c from CollectEditSend c ";
		if (sort != null && dir != null)
		{
			queryString += " order by c." + sort + " " + dir;
		}
		List<CollectEditSend> ret = findAllBySql(start, count, queryString);	
		return ret; 	
    }
	/**
	 * 根据文件路径获取报送信息
	 * @param filepath
	 * @return
	 */
	public List<CollectEditSend> getBSByFile(String filepath)
	{
		String queryString = " select c from CollectEditSend c where c.filePath=? ";
		List<CollectEditSend> ret = findAllBySql(queryString,filepath);	
		return ret;
	}
	/**
	 * 获取用户报送的记录
	 * @param userId
	 * @param start
	 * @param count
	 * @param sort
	 * @param dir
	 * @return
	 */
	public List<CollectEditSend> getSendCollectEdit(Long orgid, int start, int count, String sort, String dir)
    {
    	
    	String queryString = " select c from CollectEditSend c where c.org.id = ? ";
		if (sort != null && dir != null)
		{
			queryString += " order by c." + sort + " " + dir;
		}
		List<CollectEditSend> ret = findAllBySql(start, count, queryString, orgid);	
		return ret; 	
    }
	
	/**
	 * 获取用户报送的记录
	 * @param userId
	 * @param start
	 * @param count
	 * @param sort
	 * @param dir
	 * @return
	 */
	public List<CollectEditSend> getSendCollectEdit(Long orgid, int start, int count, String sort, String dir, Date startD, Date endD)
    {
    	
    	String queryString = " select c from CollectEditSend c where c.org.id = ?1 and c.sendTime between ?2 AND ?3 ";
		if (sort != null && dir != null)
		{
			queryString += " order by c." + sort + " " + dir;
		}
		List<CollectEditSend> ret = findAllBySql(start, count, queryString, orgid, startD, endD);	
		return ret; 	
    }
	
	/**
	 * 获取用户报送被采编的记录
	 * @param userId
	 * @param start
	 * @param count
	 * @param sort
	 * @param dir
	 * @return
	 */
	public List<CollectEdit> getCollectEdit(Long orgid, int start, int count, String sort, String dir)
    {
    	
    	String queryString = " select a from CollectEditSend c, CollectEdit a where c.org.id = ?1 and c.id = a.sendId ";
		if (sort != null && dir != null)
		{
			queryString += " order by a." + sort + " " + dir;
		}
		List<CollectEdit> ret = findAllBySql(start, count, queryString, orgid);	
		return ret; 	
    }
	
	/**
	 * 获取用户报送被采编的记录
	 * @param userId
	 * @param start
	 * @param count
	 * @param sort
	 * @param dir
	 * @return
	 */
	public List<CollectEdit> getCollectEdit(Long orgid, int start, int count, String sort, String dir, Date startD, Date endD)
    {
    	
    	String queryString = " select a from CollectEditSend c, CollectEdit a where c.org.id = ?1 and c.id = a.sendId and c.sendTime between ?2 AND ?3 ";
		if (sort != null && dir != null)
		{
			queryString += " order by a." + sort + " " + dir;
		}
		List<CollectEdit> ret = findAllBySql(start, count, queryString, orgid, startD, endD);	
		return ret; 	
    }
	
	/**
	 * 获得某个时间段的所有报送和采编的记录数。
	 * object[0]为报送者，object[1]为报送者真实名，object[2]为报送总数，object[3]为采编总数
	 * @param start
	 * @param end
	 * @return
	 */
	public List getSendCollectEdit(Date start, Date end)
	{
		// 由于jpa现在不支持表间的left join，只能如此处理。
		String queryString = " select c.org.id, c.org.name, count(c), count(a) from CollectEditSend c, CollectEdit a " 
				+ " where c.id = a.sendId and c.sendTime between ?1 AND ?2 "
				+ " group by c.org.name";
		List ret = findAllBySql(queryString, start, end);
		
		HashMap<String, Object[]> hm = new HashMap<String, Object[]>();
		for (int index = ret.size() - 1; index >= 0; index--)
		{
			Object[] o = (Object[])ret.get(index);
			hm.put((String)o[1], o);
		}
		
		queryString = " select c.org.id, c.org.name, count(c) from CollectEditSend c " 
			+ " where c.sendTime between ?1 AND ?2  group by c.org.name";
		List ret2 = findAllBySql(queryString, start, end);
		for (int index = ret2.size() - 1; index >= 0; index--)
		{
			Object[] o = (Object[])ret2.get(index);
			Object[] v = hm.get(o[1]); 
			if (v == null)
			{
				Object[] t = new Object[4];
				System.arraycopy(o, 0, t, 0, 3);
				t[3] = new Long(0);
				ret.add(t);
			}
			else
			{
				v[2] = o[2]; 
			}
		}
		
		return ret; 
	}
	
	/**
	 * 获得某个时间段的某些报送和采编的记录数。
	 * object[0]为报送者，object[1]为报送者真实名，object[2]为报送总数，object[3]为采编总数
	 * @param start
	 * @param end
	 * @param names 为需要查询的报送者 
	 * @return
	 */
	public List getSendCollectEdit(Date start, Date end, List<Long> ids)
	{
		// 由于jpa现在不支持表间的left join，只能如此处理。
		String queryString = " select c.org.id, c.org.name, count(c), count(a) from CollectEditSend c, CollectEdit a " 
				+ " where c.id = a.sendId and c.sendTime between ?1 AND ?2 and c.org.id in (?3)"
				+ " group by c.org.name";
		List ret = findAllBySql(queryString, start, end, ids);
		
		HashMap<String, Object[]> hm = new HashMap<String, Object[]>();
		for (int index = ret.size() - 1; index >= 0; index--)
		{
			Object[] o = (Object[])ret.get(index);
			hm.put((String)o[1], o);
		}
		
		queryString = " select c.org.id, c.org.name, count(c) from CollectEditSend c " 
			+ " where c.sendTime between ?1 AND ?2 and c.org.id in (?3) group by c.org.name";
		List ret2 = findAllBySql(queryString, start, end, ids);
		for (int index = ret2.size() - 1; index >= 0; index--)
		{
			Object[] o = (Object[])ret2.get(index);
			Object[] v = hm.get(o[1]); 
			if (v == null)
			{
				Object[] t = new Object[4];
				System.arraycopy(o, 0, t, 0, 3);
				t[3] = new Long(0);
				ret.add(t);
			}
			else
			{
				v[2] = o[2]; 
			}
		}
		
		return ret; 
	}
	
	/**
	 * 获得某个时间段的某个报送和采编的记录数。
	 * object[0]为报送者，object[1]为报送者真实名，object[2]为报送总数，object[3]为采编总数
	 * @param start
	 * @param end
	 * @param names 为需要查询的报送者 
	 * @return
	 */
	public List getSendCollectEdit(Date start, Date end, Long id)
	{
		// 由于jpa现在不支持表间的left join，只能如此处理。
		String queryString = " select c.org.id, c.org.name, count(c), count(a) from CollectEditSend c, CollectEdit a " 
				+ " where c.id = a.sendId and c.sendTime between ?1 AND ?2 and c.org.id  = ?3 group by c.org.name";
		List ret = findAllBySql(queryString, start, end, id);
		
		HashMap<String, Object[]> hm = new HashMap<String, Object[]>();
		for (int index = ret.size() - 1; index >= 0; index--)
		{
			Object[] o = (Object[])ret.get(index);
			hm.put((String)o[1], o);
		}
		
		queryString = " select c.org.id, c.org.name, count(c) from CollectEditSend c " 
			+ " where c.sendTime between ?1 AND ?2 and c.org.id = ?3 group by c.org.name";
		List ret2 = findAllBySql(queryString, start, end, id);
		for (int index = ret2.size() - 1; index >= 0; index--)
		{
			Object[] o = (Object[])ret2.get(index);
			Object[] v = hm.get(o[1]); 
			if (v == null)
			{
				Object[] t = new Object[4];
				System.arraycopy(o, 0, t, 0, 3);
				t[3] = new Long(0);
				ret.add(t);
			}
			else
			{
				v[2] = o[2]; 
			}
		}
		
		return ret; 
	}
	
}
