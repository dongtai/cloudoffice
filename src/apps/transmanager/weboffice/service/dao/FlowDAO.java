package apps.transmanager.weboffice.service.dao;

import java.util.ArrayList;
import java.util.List;

import apps.transmanager.weboffice.constants.both.FlowConstants;
import apps.transmanager.weboffice.databaseobject.FlowProcessDefines;
import apps.transmanager.weboffice.databaseobject.FlowProcessInstances;
import apps.transmanager.weboffice.databaseobject.FlowTaskInstances;
import apps.transmanager.weboffice.domain.TaskExcutor;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class FlowDAO  extends BaseDAO 
{
	/*
	 * 删除一个流程定义
	 */
	public void deleteFlowProcessDefineByID(Long id)
	{
		super.deleteEntityByID(FlowProcessDefines.class, "id", id);
	}
	
	/*
	 * 删除一些流程定义
	 */
	public void deleteFlowProcessDefineByID(List<Long> id)
	{
		super.deleteEntityByID(FlowProcessDefines.class, "id", id);
	}
	
	/**
	 * 获取由用户实际执行中或执行完成的任务列表。
	 * @param userId
	 * @param status
	 * @param start
	 * @param length
	 * @return
	 */
	public long getTaskInstancesCount(Long userId, int status)
	{
		String sql = "select count(*) from  FlowTaskInstances t where t.user.id = ? and t.status = ? and t.hiddened = false ";
		return getCountBySql(sql, userId, status);
	}
	
	/**
	 * 获取由用户实际执行中或执行完成的任务列表。
	 * @param userId
	 * @param status
	 * @param start
	 * @param length
	 * @return
	 */
	public List<FlowTaskInstances> getTaskInstances(Long userId, int status, int start, int length)
	{
		String sql = "select distinct t from  FlowTaskInstances t where t.user.id = ? and t.status = ? and t.hiddened = false ";
		return findAllBySql(start, length, sql, userId, status);
	}
	
	/**
	 * 获取由用户实际执行中或执行完成的隐藏任务列表。
	 * @param userId
	 * @param status
	 * @param start
	 * @param length
	 * @return
	 */
	public long getHiddenTaskInstancesCount(Long userId, int status)
	{
		String sql = "select count(*) from  FlowTaskInstances t where t.user.id = ? and t.status = ? and t.hiddened = true ";
		return getCountBySql(sql, userId, status);
	}
	
	/**
	 * 获取由用户实际执行中或执行完成的隐藏任务列表。
	 * @param userId
	 * @param status
	 * @param start
	 * @param length
	 * @return
	 */
	public List<FlowTaskInstances> getHiddenTaskInstances(Long userId, int status, int start, int length)
	{
		String sql = "select distinct t from  FlowTaskInstances t where t.user.id = ? and t.status = ? and t.hiddened = true ";
		return findAllBySql(start, length, sql, userId, status);
	}
	
	/**
	 * 获取用户流程任务已经结束，但流程还没有结束的任务的数量
	 * @param userId
	 * @return
	 */
	public long getEndTaskCountByUser(Long userId)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select  count(t) from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in( ?1) and t.status = ?2 and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor) and t.hiddened = false ";
		return getCountBySql(sql, st, FlowConstants.COMPLETED, userId, te);
	}
	
	/**
	 * 获取用户流程任务已经结束，但流程还没有结束的任务
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public List<FlowTaskInstances> getEndTaskByUser(Long userId, int start, int length)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select distinct t from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in( ?1) and t.status = ?2 and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor) and t.hiddened = false ";
		return findAllBySql(start, length, sql, st, FlowConstants.COMPLETED, userId, te);
	}
	
	/**
	 * 获取用户流程任务没结束，流程也没有结束的任务的数量
	 * @param userId
	 * @return
	 */
	public long getTaskCountByUser(Long userId)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select  count(t.status) from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in( ?1) and t.status in( ?2) and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor) and t.hiddened = false ";
		return getCountBySql(sql, st, st, userId, te);
	}
	
	/**
	 * 获取用户流程任务没有结束，流程也没有结束的任务
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public List<FlowTaskInstances> getTaskByUser(Long userId, int start, int length)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select distinct t from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in( ?1) and t.status in( ?2) and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor) and t.hiddened = false ";
		return findAllBySql(start, length, sql, st, st, userId, te);
	}
	
	/**
	 * 获取用户为默认节点流程任务已经结束，但流程还没有结束的任务的数量
	 * @param userId
	 * @return
	 */
	public long getEndTaskCountForDefaultUser(Long userId)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select count(t.status) from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in( ?1) and p.defaultCount = 0 and t.status in ( ?2) and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor)" 
				+ " and t.hiddened = false ";
		return getCountBySql(sql, st, st, userId, te);
	}
	
	/**
	 * 获取用户为默认节点流程任务已经结束，但流程还没有结束的任务
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public List<FlowTaskInstances> getEndTaskForDefaultUser(Long userId, int start, int length)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select distinct t from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in(?1) and p.defaultCount = 0 and t.status in( ?2) and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor)"
				+ " and t.hiddened = false ";
		return findAllBySql(start, length, sql, st, st, userId, te);
	}
	
	/**
	 * 获取用户为默认节点流程任务没结束，流程也没有结束的任务的数量
	 * @param userId
	 * @return
	 */
	public long getTaskCountForDefaultUser(Long userId)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		String sql = "select count(t.status) from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in( ?1) and p.defaultCount > 0 and t.status = ?2 and t.user.id = ?3 and t.numbers = 0 "
				+ " and t.hiddened = false"; 
		return getCountBySql(sql, st, FlowConstants.COMPLETED, userId);
	}
	
	/**
	 * 获取用户为默认节点流程任务没有结束，流程也没有结束的任务
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public List<FlowTaskInstances> getTaskForDefaultUser(Long userId, int start, int length)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		String sql = "select distinct t from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in( ?1) and p.defaultCount > 0 and t.status = ?2 and t.user.id = ?3 and t.numbers = 0 "
				+ " and t.hiddened = false ";
		return findAllBySql(start, length, sql, st, FlowConstants.COMPLETED, userId);
	}
	
	/**
	 * 获取用户为默认节点流程中任务及流程都已经结束的任务。
	 * @param userId
	 * @return
	 */
	public long getEndTaskAndEndProcessCountByUser(Long userId)
	{
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select count(t.status) from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status = ?1 and t.status = ?2 and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor) and t.hiddened = false ";
		return getCountBySql(sql, FlowConstants.COMPLETED, FlowConstants.COMPLETED, userId, te);
	}
	
	/**
	 * 获取用户流程中任务及流程都已经结束的任务。
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public List<FlowTaskInstances> getEndTaskAndEndProcessByUser(Long userId, int start, int length)
	{
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select distinct t from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status = ?1 and t.status = ?2 and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor) and t.hiddened = false ";
		return findAllBySql(start, length, sql, FlowConstants.COMPLETED, FlowConstants.COMPLETED, userId, te);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public long getTaskCountForHidden(Long userId)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select count(t.status) from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in ( ?1) and t.status = ?2 and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor) and t.hiddened = true ";
		return getCountBySql(sql, st, FlowConstants.COMPLETED, userId, te);
	}
	
	/**
	 * 
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public List<FlowTaskInstances> getTaskForHidden(Long userId, int start, int length)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select distinct t from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in( ?1) and t.status = ?2 and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor) and t.hiddened = true ";
		return findAllBySql(start, length, sql, st, FlowConstants.COMPLETED, userId, te);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public long getFlowProcessInstancesCount(Long userId)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		st.add(FlowConstants.ABANDON);		
		String sql = "select count(*) from  FlowProcessInstances p where p.status in (?1) and p.user.id = ?2 ";
		return getCountBySql(sql, st, userId);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public List<FlowProcessInstances> getFlowProcessInstances(Long userId, int start, int length)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		st.add(FlowConstants.ABANDON);		
		String sql = "select distinct p from  FlowProcessInstances p where p.status in (?1) and p.user.id = ?2 ";
		return findAllBySql(start, length, sql, st, userId);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public long getFlowProcessInstancesCountForAb(Long userId)
	{
		String sql = "select count(*) from  FlowProcessInstances p where p.status = ?1 and p.user.id = ?2 ";
		return getCountBySql(sql, FlowConstants.ABANDON, userId);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public List<FlowProcessInstances> getFlowProcessInstancesAb(Long userId, int start, int length)
	{		
		String sql = "select distinct p from  FlowProcessInstances p where p.status  = ?1 and p.user.id = ?2 ";
		return findAllBySql(start, length, sql, FlowConstants.ABANDON, userId);
	}
	
	/**
	 * 获取用户流程任务已经结束，但流程还没有结束的任务的数量
	 * @param userId
	 * @return
	 */
	public long getProcessCountByUser(Long userId)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select  count(p) from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in( ?1) and t.status = ?2 and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor) and t.hiddened = false ";
		return getCountBySql(sql, st, FlowConstants.COMPLETED, userId, te);
	}
	
	/**
	 * 获取用户流程任务已经结束，但流程还没有结束的任务
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public List<FlowProcessInstances> getProcessByUser(Long userId, int start, int length)
	{
		ArrayList<Integer> st = new ArrayList<Integer>();
		st.add(FlowConstants.INPROGRESS);
		st.add(FlowConstants.CREATED);
		TaskExcutor te = new TaskExcutor(TaskExcutor.USER, userId);
		String sql = "select distinct p from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId " 
				+ "and p.status in( ?1) and t.status = ?2 and (t.user.id = ?3 or ?4 MEMBER OF t.taskExcutor) and t.hiddened = false ";
		return findAllBySql(start, length, sql, st, FlowConstants.COMPLETED, userId, te);
	}
	
	/**
	 * 得到流程实例及流程中的所有任务
	 */
	public FlowProcessInstances getProcessInstanceAndTask(Long processInstanceId)
	{
		String sql = "select distinct t from  FlowTaskInstances t, FlowProcessInstances p where p.id = t.processInstanceId ";
		List<FlowTaskInstances> tasks = findAllBySql(sql);
		FlowProcessInstances process = (FlowProcessInstances)find(FlowProcessInstances.class, processInstanceId);
		process.setInstances(tasks);
		return process;
	}
	
}
