package apps.transmanager.weboffice.service.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import apps.transmanager.weboffice.constants.both.FlowConstants;
import apps.transmanager.weboffice.databaseobject.FlowProcessInstances;
import apps.transmanager.weboffice.databaseobject.FlowTaskInstances;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.TaskExcutor;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.FlowService;
import apps.transmanager.weboffice.service.server.UserService;

/**
 * 为公文审批增加的业务逻辑处理类。
 * 由于目前公文审批在流程上有一些特殊的内容，为了适应基本通用的流程，
 * 在该类中做适当的流程业务逻辑转换。
 * 
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class FlowsHandler
{
	
	/**
	 * 启动一个新的公文审批流程的流程实例。	   
	 * @param userId， 送审者
	 * @param path 送审的文档全路径（在文件库中的全路径）
	 * @param version 送审文件的版本号。
	 * @param title 送审标题
	 * @param stepName 送审步骤名
	 * @param audit 签批者Id
	 * @param reader 阅读者Id
	 * @param com 备注
	 * @return 如果流程启动不成功，返回null，否则返回Object数组对象，
	 * 其值表示为：
	 * object[0]为启动的流程实例FlowProcessInstances对象，
	 * object[1]为流程实例中第一个任务节点实例FlowTaskInstances对象。
	 */
	public static Object[] startAudit(Long userId, String path, String version, String title, String stepName, 
			List<Long> audit, List<Long> reader, String com)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		Users user = userService.getUser(userId);
		
		// 构建流程实例
		FlowProcessInstances fpi = new FlowProcessInstances(title, com, user);
		HashMap<String, Object> datas = new HashMap<String, Object>();
		datas.put("path", path);
		datas.put("version", version);
		fpi.setDatas(datas);
		fpi.setDefaultNumbers(null);
	
		// 构建第一个任务节点
		FlowTaskInstances currentTask = new FlowTaskInstances(fpi.getId(), stepName, user, 0);
		currentTask.setDatas(datas);		
		
		// 构建后续任务节点
		List<FlowTaskInstances> nextTasks = new ArrayList<FlowTaskInstances>();
		HashMap<String, Object> nextDatas = new HashMap<String, Object>();
		nextDatas.putAll(datas);
		nextDatas.put("preUserName", user.getUserName());
		nextDatas.put("preUserRealName", user.getRealName());
		int count = 0;
		for (Long temp : audit)
		{
			ArrayList<TaskExcutor> excutor = new ArrayList<TaskExcutor>();
			TaskExcutor te = new TaskExcutor(TaskExcutor.USER, temp);
			FlowTaskInstances tempTask = new FlowTaskInstances(excutor, stepName, nextDatas);
			count ++;
			nextTasks.add(tempTask);
		}
		fpi.setDefaultCount(count);
				
		for (Long temp : reader)
		{
			ArrayList<TaskExcutor> excutor = new ArrayList<TaskExcutor>();
			TaskExcutor te = new TaskExcutor(TaskExcutor.USER, temp);
			FlowTaskInstances tempTask = new FlowTaskInstances(excutor, stepName, nextDatas);
			tempTask.setHiddened(true);
			nextTasks.add(tempTask);
		}
		
		Object[] ret = flowService.startFlowProcessAndTask(fpi, userId, currentTask, nextTasks);
		return ret;
	}
	
	/**
	 * 再次公文审批流程的流程实例。	   
	 * @param userId， 送审者
	 * @param FlowTaskInstances 需要在发起的任务节点
	 * @param stepName 送审步骤名
	 * @param audit 签批者Id
	 * @param reader 阅读者Id
	 * @param com 备注
	 */
	public static void restartAudit(Long userId, FlowTaskInstances task, String stepName, 
			List<Long> audit, List<Long> reader)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		Users user = userService.getUser(userId);
		
		// 构建流程实例
		FlowProcessInstances fpi = flowService.getFlowProcessInstancesById(task.getProcessInstanceId());
		HashMap<String, Object> datas = task.getDatas();
		fpi.setDefaultNumbers(null);
	
		// 构建第一个任务节点
		FlowTaskInstances currentTask = task;		
		
		// 构建后续任务节点
		List<FlowTaskInstances> nextTasks = new ArrayList<FlowTaskInstances>();
		HashMap<String, Object> nextDatas = task.getDatas();
		nextDatas.putAll(datas);
		nextDatas.remove("comment");
		nextDatas.put("preUserName", user.getUserName());
		nextDatas.put("preUserRealName", user.getRealName());
		int count = 0;
		for (Long temp : audit)
		{
			ArrayList<TaskExcutor> excutor = new ArrayList<TaskExcutor>();
			TaskExcutor te = new TaskExcutor(TaskExcutor.USER, temp);
			FlowTaskInstances tempTask = new FlowTaskInstances(excutor, stepName, nextDatas);
			count ++;
			nextTasks.add(tempTask);
		}
		fpi.setDefaultCount(count);		
		for (Long temp : reader)
		{
			ArrayList<TaskExcutor> excutor = new ArrayList<TaskExcutor>();
			TaskExcutor te = new TaskExcutor(TaskExcutor.USER, temp);
			FlowTaskInstances tempTask = new FlowTaskInstances(excutor, stepName, nextDatas);
			tempTask.setHiddened(true);
			nextTasks.add(tempTask);
		}
		
		flowService.excuteFlowTask(currentTask, userId, nextTasks);
	}
	
	
	/**
	 * 审批公文流程实例。	   
	 * @param userId，审批者
	 * @param taskId 审批实例Id
	 * @param stepName 送审步骤名
	 * @param audit 签批者Id
	 * @param reader 阅读者Id
	 * @param com 备注
	 */
	public static void audit(Long userId, Long taskId, String stepName, 
			List<Long> audit, List<Long> reader, String com)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
		Users user = userService.getUser(userId);
		
		// 构建流程实例
		FlowTaskInstances task = flowService.getFlowTaskInstancesById(taskId);
		FlowProcessInstances fpi = flowService.getFlowProcessInstancesById(task.getProcessInstanceId());		
		HashMap<String, Object> datas = task.getDatas();
	
		// 构建第一个任务节点
		FlowTaskInstances currentTask = task;		
		
		// 构建后续任务节点
		List<FlowTaskInstances> nextTasks = new ArrayList<FlowTaskInstances>();
		HashMap<String, Object> nextDatas = task.getDatas();
		nextDatas.putAll(datas);
		nextDatas.remove("comment");
		nextDatas.put("preUserName", user.getUserName());
		nextDatas.put("preUserRealName", user.getRealName());
		int count = 0;
		for (Long temp : audit)
		{
			ArrayList<TaskExcutor> excutor = new ArrayList<TaskExcutor>();
			TaskExcutor te = new TaskExcutor(TaskExcutor.USER, temp);
			FlowTaskInstances tempTask = new FlowTaskInstances(excutor, stepName, nextDatas);
			count ++;
			nextTasks.add(tempTask);
		}	
		for (Long temp : reader)
		{
			ArrayList<TaskExcutor> excutor = new ArrayList<TaskExcutor>();
			TaskExcutor te = new TaskExcutor(TaskExcutor.USER, temp);
			FlowTaskInstances tempTask = new FlowTaskInstances(excutor, stepName, nextDatas);
			tempTask.setHiddened(true);
			nextTasks.add(tempTask);
		}
		
		flowService.excuteFlowTask(currentTask, userId, nextTasks);
	}
	
	
	/**
	 * 得到已签批的文档
	 * 
	 * 得到用户送审的，并且已经都审批回到用户，需要用户做后续处理的节点
	 * @param userId
	 */
	public static long getSendCountForEnd(Long userId)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getEndTaskCountForDefaultUser(userId);
	}
	
	/**
	 * 得到已签批的文档
	 * 
	 * 得到用户送审的，并且已经都审批回到用户，需要用户做后续处理的节点 
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public static List<FlowTaskInstances> getSendForEnd(Long userId, int start, int length)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getEndTaskForDefaultUser(userId, start, length);
	}
	
	/**
	 * 得到送审中的文档
	 * 
	 * 得到用户送审的，并且还没有完全审批回到用户
	 * @param userId
	 */
	public static long getSendCountForInit(Long userId)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getTaskCountForDefaultUser(userId);
	}
	
	/**
	 * 得到送审中的文档
	 * 
	 * 得到用户送审的，并且还没有完全审批回到用户
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public static List<FlowTaskInstances> getSendForInit(Long userId, int start, int length)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getTaskForDefaultUser(userId, start, length);
	}
	
	/**
	 * 得到所有送审的文档，包括审批中，审批完成，已废弃
	 * 
	 * 
	 * @param userId
	 */
	public static long getSendCountForAll(Long userId)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getFlowProcessInstancesCount(userId);
	}
	
	/**
	 * 得到所有送审的文档，包括审批中，审批完成，已废弃
	 * 
	 * 
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public static List<FlowProcessInstances> getSendForAll(Long userId, int start, int length)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getFlowProcessInstances(userId, start, length);
	}
	
	
	/**
	 * 得到所有送审的已废弃文档
	 * 
	 * 
	 * @param userId
	 */
	public static long getSendCountForAb(Long userId)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getFlowProcessInstancesCountForAb(userId);
	}
	
	/**
	 * 得到所有送审的废弃文档
	 * 
	 * 
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public static List<FlowProcessInstances> getSendForAb(Long userId, int start, int length)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getFlowProcessInstancesAb(userId, start, length);
	}
	
	/**
	 * 得到所有需要自己签批的文档
	 * 
	 * 
	 * @param userId
	 */
	public static long getAuditCountForInit(Long userId)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getTaskCountByUser(userId);
	}
	
	/**
	 * 得到所有需要自己签批的文档
	 * 
	 * 
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public static List<FlowTaskInstances> getAuditForInit(Long userId, int start, int length)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getTaskByUser(userId, start, length);
	}
	
	/**
	 * 得到所有自己已经签批的文档
	 * 
	 * 
	 * @param userId
	 */
	public static long getAuditCountForEnd(Long userId)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getEndTaskCountByUser(userId);
	}
	
	/**
	 * 得到所有自己已经签批的文档
	 * 
	 * 
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public static List<FlowTaskInstances> getAuditForEnd(Long userId, int start, int length)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getEndTaskByUser(userId, start, length);
	}
	
	/**
	 * 得到所有自己没有签批和已经签批的文档
	 * 
	 * 
	 * @param userId
	 */
	public static long getAuditCountForAll(Long userId)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getProcessCountByUser(userId);
	}
	
	/**
	 * 得到所有自己没有签批和已经签批的文档
	 * 
	 * 
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public static List<FlowProcessInstances> getAuditForAll(Long userId, int start, int length)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getProcessByUser(userId, start, length);
	}
	
	/**
	 * 设置审核状态为废弃或已经办结状态。
	 * @param status 需要设置的状态，0为办结状态,其他值为废弃状态
	 */
	public static void setAuditStatus(Long processInatnceId, int status)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		flowService.setFlowStatus(processInatnceId, status == 0 ? FlowConstants.COMPLETED : FlowConstants.ABANDON);		
	}
	
	/**
	 * 获得流程实例及流程中的所有任务。
	 * @param processInstanceId
	 * @return
	 */
	public static FlowProcessInstances getProcessInstancesFor(Long processInstanceId)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getProcessInstanceAndTask(processInstanceId);
	}
	
	/**
	 * 得到所有阅读的文档的数量
	 * @param userID
	 * @return
	 */
	public static long getReaderFileCount(Long userID)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getTaskCountForHidden(userID);
	}
	
	/**
	 * 得到所有阅读的文档
	 * @param userID
	 * @return
	 */
	public static List<FlowTaskInstances> getReaderFileCount(Long userID, int start, int length)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		return flowService.getTaskForHidden(userID, start, length);
	}
	
	
	/**
	 * 更新阅读状态
	 * @param task
	 */
	public static void setReaderStatus(Long taskId, String comm)
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		FlowTaskInstances task = flowService.getFlowTaskInstancesById(taskId);
		HashMap<String, Object> data = task.getDatas();
		data.put("comment", comm);
		task.setEndTime(new Date());
		flowService.updateFlowTaskInstance(task);
	}
	
	public static void test()
	{
		FlowService flowService = (FlowService)ApplicationContext.getInstance().getBean(FlowService.NAME);
		
		Object[] ret = flowService.startFlowProcessAndTask(2L, "first process ", "String des", "first task", true);
		FlowProcessInstances fpi = (FlowProcessInstances)ret[0];
		FlowTaskInstances fti = (FlowTaskInstances)ret[1];
		
		flowService.saveFlowProcess(fpi);
		flowService.saveFlowTask(fti, 2L);
		
		List<FlowTaskInstances> nextTasks = new ArrayList<FlowTaskInstances>();
		ArrayList<TaskExcutor> taskExcutor = new ArrayList<TaskExcutor>();
		TaskExcutor tempU = new TaskExcutor(TaskExcutor.USER, 3L);
		taskExcutor.add(tempU);
		//tempU = new TaskExcutor(TaskExcutor.USER, 4L);
		//taskExcutor.add(tempU);
		
		FlowTaskInstances tempT = new FlowTaskInstances(taskExcutor, "second", null);
		nextTasks.add(tempT);		
		flowService.excuteFlowTask(fti, 2L, nextTasks);
		
		List<FlowTaskInstances> ent = flowService.getEndTaskByUser(2L, -1, -1);
		List<FlowTaskInstances> ent2 = flowService.getEndTaskForDefaultUser(2L, -1, -1);
		
		
		System.out.println("000000000000");
		
		
	}
	
	
}
