package apps.transmanager.weboffice.service.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.constants.both.FlowConstants;
import apps.transmanager.weboffice.databaseobject.FlowProcessDefines;
import apps.transmanager.weboffice.databaseobject.FlowProcessInstances;
import apps.transmanager.weboffice.databaseobject.FlowTaskDefines;
import apps.transmanager.weboffice.databaseobject.FlowTaskInstances;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.TaskExcutor;
import apps.transmanager.weboffice.service.dao.FlowDAO;
import apps.transmanager.weboffice.service.dao.StructureDAO;

/**
 * 根据需要定义的一个简单的流程服务，在该服务中只处理与数据库事务有关的内容。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
@Component(value=FlowService.NAME)
public class FlowService
{
	public static final String NAME = "flowService";
	@Autowired
	private FlowDAO flowDAO;
	@Autowired
	private StructureDAO structureDAO;
	
	/**
	 * 创建一个流程定义
	 * @param fpd
	 */
	public void createFlowProcessDefined(FlowProcessDefines fpd)
	{
		if (fpd == null)
		{
			return;
		}
		fpd.setCreateTime(new Date());
		flowDAO.save(fpd);
	}
	
	/**
	 * 更新一个流程定义。如果该流程是新定义的（id值为null），则会新建立一个流程定义，
	 * 如果是更新流程，则该参数的id值一定不能为null。
	 * @param fpd
	 */
	public void updateFlowProcessDefined(FlowProcessDefines fpd)
	{
		if (fpd == null)
		{
			return;
		}
		Long id = fpd.getId();
		if (id == null)
		{
			flowDAO.save(fpd);
		}
		else
		{
			flowDAO.update(fpd);
		}
	}
	
	/**
	 * 删除一些流程定义
	 * @param ids
	 */
	public void deleteFlowProcessDefined(List<Long> ids)
	{
		if (ids != null)
		{
			flowDAO.deleteFlowProcessDefineByID(ids);
		}
	}
	
	/**
	 * 删除一流程定义
	 * @param id
	 */
	public void deleteFlowProcessDefined(Long id)
	{
		if (id != null)
		{
			flowDAO.deleteFlowProcessDefineByID(id);
		}
	}
	
	/**
	 * 得到所有定义的流程
	 * @return
	 */
	public List<FlowProcessDefines> getAllFlowProcessDefined()
	{
		return flowDAO.findAll(FlowProcessDefines.class);
	}
	
	/**
	 * 得到用户有权限启动的所有定义的流程
	 * @return
	 */
	public List<FlowProcessDefines> getFlowProcessDefinedForUser(Long userId)
	{
		return null;
	}
	
	/**
	 * 根据流程定义id得到定义的流程
	 * @return
	 */
	public FlowProcessDefines getFlowProcessDefinedById(Long id)
	{
		return (FlowProcessDefines)flowDAO.find(FlowProcessDefines.class, id);
	}
	
	
	/**
	 * 根据流程实例id得到流程实例
	 * @return
	 */
	public FlowProcessInstances getFlowProcessInstancesById(Long id)
	{
		return (FlowProcessInstances)flowDAO.find(FlowProcessInstances.class, id);
	}
	
	/**
	 * 根据流程任务实例id得到流程任务实例
	 * @return
	 */
	public FlowTaskInstances getFlowTaskInstancesById(Long id)
	{
		return (FlowTaskInstances)flowDAO.find(FlowTaskInstances.class, id);
	}
	
	/**
	 * 根据一个流程定义Id值，启动一个流程实例, 并开始执行流程定义中的第一个任务节点。
	 * 该操作后流程处于执行中状态，流程中的第一个任务节点处理执行中状态。
	 * @param processDefinedId 需要启动的流程定义id
	 * @param userId 启动该流程的用户。
	 * @param des 启动该流程的描述
	 * @return 如果流程启动不成功，返回null，否则返回Object数组对象，其值表示为object[0]为流程定义FlowProcessDefines对象，
	 * object[1]为流程定义的第一个节点任务FlowTaskDefines对象， object[2]为启动的流程实例FlowProcessInstances对象，
	 * object[3]为流程实例中第一个任务节点实例FlowTaskInstances对象。
	 */
	public Object[] startFlowProcessAndTaskForDefined(Long processDefinedId, Long userId, String des)
	{
		Users user = structureDAO.findUserById(userId);
		if (user == null)
		{
			return null;
		}
		FlowProcessDefines fpd = (FlowProcessDefines)flowDAO.find(FlowProcessDefines.class, processDefinedId);
		if (fpd == null)
		{
			return null;
		}			
		// 初始化流程实例
		FlowProcessInstances fpi = new FlowProcessInstances(processDefinedId, des, user);
		flowDAO.save(fpi);
		
		// 初始化流程第一个节点
		FlowTaskDefines start = fpd.getStartFlowTaskDefines();
		FlowTaskInstances fti = new FlowTaskInstances(fpi.getId(), start.getTaskExcutor(), user);		
		flowDAO.save(fti);
		
		// 流程中当前活动节点
		ArrayList<Integer> number = new ArrayList<Integer>();
		number.add(start.getNumbers());
		fpi.setNumbers(number);
		fpi.setStatus(FlowConstants.INPROGRESS);
		flowDAO.update(fpi);
		
		Object[] ret = new Object[]{fpd, start, fpi, fti};
		return ret;
	}
	
	/**
	 * 根据一个流程定义Id值，启动一个流程实例, 并分配第一个任务节点的执行者。
	 * 该操作后流程处于执行中状态，流程中的第一个节点任务处理初始化状态
	 * @param processDefinedId 需要启动的流程定义id
	 * @param userId 启动该流程的用户。
	 * @param des 启动该流程的描述
	 * @param taskExcutor 分配第一个节点任务的执行者，如果为null则第一个节点的执行者分配为节点定义时候的执行者
	 * @return 如果流程启动不成功，返回null，否则返回为启动的流程实例FlowProcessInstances对象，
	 */
	public FlowProcessInstances startFlowProcessForDefined(Long processDefinedId, Long userId, String des, ArrayList<TaskExcutor> taskExcutor)
	{
		Users user = structureDAO.findUserById(userId);
		if (user == null)
		{
			return null;
		}
		FlowProcessDefines fpd = (FlowProcessDefines)flowDAO.find(FlowProcessDefines.class, processDefinedId);
		if (fpd == null)
		{
			return null;
		}			
		// 初始化流程实例
		FlowProcessInstances fpi = new FlowProcessInstances(processDefinedId, des, user);
		flowDAO.save(fpi);
		
		// 初始化流程第一个节点
		FlowTaskDefines start = fpd.getStartFlowTaskDefines();
		FlowTaskInstances fti = new FlowTaskInstances(fpi.getId(), taskExcutor != null ? taskExcutor : start.getTaskExcutor());		
		flowDAO.save(fti);
		
		// 流程中当前活动节点
		ArrayList<Integer> number = new ArrayList<Integer>();
		number.add(start.getNumbers());
		fpi.setNumbers(number);
		fpi.setStatus(FlowConstants.INPROGRESS);
		flowDAO.update(fpi);
		
		return fpi;
	}
	
	/**
	 * 保存一个流程任务的数据，流程不继续执行。该操作后，流程中该节点任务的状态
	 * 处于执行状态中。
	 * @param task 具体的流程任务
	 * @param userId 具体执行该任务的人员
	 */
	public void saveFlowTaskForDefined(FlowTaskInstances task, Long userId)
	{
		/*Users user = structureDAO.findUserById(userId);
		task.setUser(user);
		task.setEndTime(new Date());
		task.setStatus(FlowConstants.COMPLETED);
		flowDAO.update(task);
		
		// 更新流程实例状态
		FlowProcessInstances fti = (FlowProcessInstances)flowDAO.find(FlowProcessInstances.class, task.getProcessInstanceId());
		*/
		
	}
	
	/**
	 * 执行完成一个流程任务，并转到下一个流程节点，该操作后，流程节点处于完成状态，而下一个节点任务
	 * 处于初始化状态.
	 * @param task 具体的流程任务
	 * @param userId 具体执行该任务的人员
	 */
	public void excuteFlowTaskForDefined(FlowTaskInstances task, Long userId)
	{
		/*Users user = structureDAO.findUserById(userId);
		task.setUser(user);
		task.setEndTime(new Date());
		task.setStatus(FlowConstants.COMPLETED);
		flowDAO.update(task);
		
		// 更新流程实例状态
		FlowProcessInstances fti = (FlowProcessInstances)flowDAO.find(FlowProcessInstances.class, task.getProcessInstanceId());
		*/
		
	}
	
	/**
	 * 启动一个非预先定义的流程实例, 并建立第一个任务节点。 
	 * 该操作后流程处于执行中状态，流程中的第一个任务节点处理执行中状态。
	 * @param fpi 新的流程实例
	 * @param userId 新的流程启动者
	 * @param currentTask 当前第一流程
	 * @param nextTask 后续流程 
	 * @return 如果流程启动不成功，返回null，否则返回Object数组对象，
	 * 其值表示为：
	 * object[0]为启动的流程实例FlowProcessInstances对象，
	 * object[1]为流程实例中第一个任务节点实例FlowTaskInstances对象。
	 */
	public Object[] startFlowProcessAndTask(FlowProcessInstances fpi, Long userId, FlowTaskInstances currentTask, List<FlowTaskInstances> nextTasks)
	{
		Users user = structureDAO.findUserById(userId);
		if (user == null)
		{
			return null;
		}
		fpi.setUser(user);
		flowDAO.save(fpi);
		
		// 初始化流程第一个节点
		int num = 0;		
		FlowTaskInstances fti = currentTask;
		fti.setUser(user);
		fti.setNumbers(num);
		fti.setProcessInstanceId(fpi.getId());
		flowDAO.save(fti);
		
		// 流程中当前活动节点
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		numbers.add(num);
		fpi.setNumbers(numbers);
		fpi.setStatus(FlowConstants.INPROGRESS);
		flowDAO.update(fpi);
		
		updateNextTasks(userId, fpi, nextTasks, num);
		
		
		Object[] ret = new Object[]{fpi, fti};
		return ret;
	}
	
	/**
	 * 启动一个非预先定义的流程实例, 并建立第一个任务节点。
	 * 该操作后流程处于执行中状态，流程中的第一个任务节点处理执行中状态。
	 * @param name 流程名字
	 * @param userId 启动该流程和执行该流程第一个任务节点的用户
	 * @param des 启动该流程的描述
	 * @param taskName 第一个任务节点的任务名
	 * @param flag 表示该流程建立后，是否正式启用，如果只是临时启动，则该值不保存在数据库中，后续
	 * 在执行该流程第一个任务的时候，则必须同时传返回的流程对象及任务对象。
	 * @return 如果流程启动不成功，返回null，否则返回Object数组对象，
	 * 其值表示为：
	 * object[0]为启动的流程实例FlowProcessInstances对象，
	 * object[1]为流程实例中第一个任务节点实例FlowTaskInstances对象。
	 */
	public Object[] startFlowProcessAndTask(Long userId, String name, String des, String taskName, boolean flag)
	{
		Users user = structureDAO.findUserById(userId);
		if (user == null)
		{
			return null;
		}
		
		// 初始化流程实例
		FlowProcessInstances fpi = new FlowProcessInstances(name, des, user);
		if (flag)
		{
			flowDAO.save(fpi);
		}
		
		// 初始化流程第一个节点
		int num = 0;
		FlowTaskInstances fti = new FlowTaskInstances(fpi.getId(), taskName, user, num);	
		if (flag)
		{
			flowDAO.save(fti);
		}
		
		// 流程中当前活动节点
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		numbers.add(num);
		fpi.setNumbers(numbers);
		fpi.setStatus(FlowConstants.INPROGRESS);
		
		
		if (flag)
		{
			flowDAO.update(fpi);
		}
		
		Object[] ret = new Object[]{fpi, fti};
		return ret;
	}
		
	/**
	 * 更新流程的中的全局数据。 
	 * @param processIn
	 */
	public void saveFlowProcess(FlowProcessInstances processIn)
	{
		flowDAO.update(processIn);
	}
	
	/**
	 * 保存一个流程任务的数据，流程不继续执行。该操作后，流程中该节点任务
	 * 处于执行状态中。
	 * @param task 具体的流程任务
	 * @param userId 具体执行该任务的人员
	 */
	public void saveFlowTask(FlowTaskInstances task, Long userId)
	{
		Users user = structureDAO.findUserById(userId);
		task.setUser(user);
		task.setStatus(FlowConstants.INPROGRESS);
		flowDAO.update(task);		
	}
	
	/**
	 * 执行完成一个流程任务，并转到下一个流程节点，该操作后，流程节点处于完成状态，而下一个节点任务
	 * 处于初始化状态.
	 * @param task 具体的执行完成的流程任务
	 * @param userId 具体执行完成该任务的人员
	 * @param nextTasks 该流程执行完成后，后续的任务。如果为null，根据需要暂时表示流程回到该流程的发起者。
	 * 该值如果不为null，则必须为新对象，且内部预先分配需要执行者。
	 */
	public void excuteFlowTask(FlowTaskInstances task, Long userId, List<FlowTaskInstances> nextTasks)
	{
		Users user = structureDAO.findUserById(userId);
		task.setUser(user);
		task.setEndTime(new Date());
		task.setStatus(FlowConstants.COMPLETED);
		flowDAO.update(task);
				
		// 更新流程实例状态
		FlowProcessInstances fpi = (FlowProcessInstances)flowDAO.find(FlowProcessInstances.class, task.getProcessInstanceId());
		int num = task.getNumbers();
		updateNextTasks(userId, fpi, nextTasks, num);
	}
	
	/**
	 * 建立非预定义流程的新的节点任务。
	 * @param userId 当前完成任务者
	 * @param fpi 当前的流程实例
	 * @param nextTasks 新的流程任务节点定义
	 * @param num 当前完成任务的节点编号
	 */
	private void updateNextTasks(Long userId, FlowProcessInstances fpi, List<FlowTaskInstances> nextTasks, int num) 
	{		
		// 活动节点
		ArrayList<Integer> numbers = fpi.getNumbers();
		// 结束节点
		ArrayList<Integer> endNumbers = fpi.getEndNumbers();
		if (endNumbers == null)
		{
			endNumbers = new ArrayList<Integer>();
			fpi.setEndNumbers(endNumbers);
		}
		// 节点导向
		HashMap<Integer, List<Integer>> flowDir = fpi.getFlowDir();
		if (flowDir == null)
		{
			flowDir = new HashMap<Integer, List<Integer>>();
			fpi.setFlowDir(flowDir);
		}
		endNumbers.add(num);		
		numbers.remove(num);	
		
		// 更新流程实例中的各种关系
		Users onwer = fpi.getUser();       // 流程发起者。
		Long processInstanceId = fpi.getId();
		Integer defaultN = fpi.getDefaultNumbers();
		int currentNumber = fpi.getCurrentNumber();
		ArrayList<Integer> tempDir = new ArrayList<Integer>();
		ArrayList<FlowTaskInstances> saveNext = new ArrayList<FlowTaskInstances>();
		for(FlowTaskInstances temp : nextTasks)
		{
			if (temp.isHiddened())    // 隐藏节点
			{
				currentNumber++;
				temp.setNumbers(currentNumber);
				temp.setProcessInstanceId(processInstanceId);
				endNumbers.add(currentNumber);						
				tempDir.add(currentNumber);
				temp.setStatus(FlowConstants.COMPLETED);
				saveNext.add(temp);
				continue;
			}
			if (temp.getUser() != null || temp.hasUser(onwer))    // 回到发起者
			{
				if (defaultN == null)          // 默认者节点没生成
				{
					currentNumber++;
					
					fpi.setDefaultNumbers(currentNumber);
					temp.setUser(onwer);
					
					temp.setNumbers(currentNumber);
					temp.setProcessInstanceId(processInstanceId);
					numbers.add(currentNumber);
					tempDir.add(currentNumber);					
					saveNext.add(temp);
				}
				else
				{
					tempDir.add(currentNumber);
				}
				fpi.setDefaultCount(fpi.getDefaultCount() - 1);
			}
			else
			{
				currentNumber++;
				temp.setNumbers(currentNumber);
				temp.setProcessInstanceId(processInstanceId);
				numbers.add(currentNumber);						
				tempDir.add(currentNumber);
				saveNext.add(temp);
			}
		}
		flowDir.put(num, tempDir);
		fpi.setCurrentNumber(currentNumber);
		
		flowDAO.saveAll(saveNext);
		flowDAO.update(fpi);
		
	}
	
	/**
	 * 更新流程中的状态。
	 * @param processIn 需要更新的流程实例id
	 * @param status 需要更新的状态
	 */
	public void setFlowStatus(Long processIn, int status)
	{
		FlowProcessInstances fpi = (FlowProcessInstances)flowDAO.find(FlowProcessInstances.class, processIn);
		fpi.setStatus(status);
		fpi.setEndTime(new Date());
		flowDAO.update(fpi);
	}
	
	/**
	 * 更新节点状态
	 * @param task
	 */
	public void updateFlowTaskInstance(FlowTaskInstances task)
	{
		flowDAO.update(task);
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
		return flowDAO.getTaskInstancesCount(userId, status);
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
		return flowDAO.getTaskInstances(userId, status, start, length);
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
		return flowDAO.getHiddenTaskInstancesCount(userId, status);
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
		return flowDAO.getHiddenTaskInstances(userId, status, start, length);
	}
	
	/**
	 * 获取用户流程任务已经结束，但流程还没有结束的任务的数量
	 * @param userId
	 * @return
	 */
	public long getEndTaskCountByUser(Long userId)
	{
		return flowDAO.getEndTaskCountByUser(userId);
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
		return flowDAO.getEndTaskByUser(userId, start, length);
	}
	
	/**
	 * 获取用户流程任务没结束，流程也没有结束的任务的数量
	 * @param userId
	 * @return
	 */
	public long getTaskCountByUser(Long userId)
	{
		return flowDAO.getTaskCountByUser(userId);
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
		return flowDAO.getTaskByUser(userId, start, length);
	}
	
	/**
	 * 获取用户为默认节点流程任务已经结束，但流程还没有结束的任务的数量
	 * @param userId
	 * @return
	 */
	public long getEndTaskCountForDefaultUser(Long userId)
	{
		return flowDAO.getEndTaskCountForDefaultUser(userId);
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
		return flowDAO.getEndTaskForDefaultUser(userId, start, length);
	}
	
	/**
	 * 获取用户为默认节点流程任务没结束，流程也没有结束的任务的数量
	 * @param userId
	 * @return
	 */
	public long getTaskCountForDefaultUser(Long userId)
	{
		return flowDAO.getTaskCountForDefaultUser(userId);
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
		return flowDAO.getTaskForDefaultUser(userId, start, length);
	}
	
	/**
	 * 获取用户为默认节点流程中任务及流程都已经结束的任务。
	 * @param userId
	 * @return
	 */
	public long getEndTaskAndEndProcessCountByUser(Long userId)
	{
		return flowDAO.getEndTaskAndEndProcessCountByUser(userId);
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
		return flowDAO.getEndTaskAndEndProcessByUser(userId, start, length);
	}
	
	/**
	 * 所以签批文档
	 * @param userId
	 * @return
	 */
	public long getFlowProcessInstancesCount(Long userId)
	{
		return flowDAO.getFlowProcessInstancesCount(userId);
	}
	
	/**
	 * 所有签批文档
	 * @param userId
	 * @param start
	 * @param length
	 * @return
	 */
	public List<FlowProcessInstances> getFlowProcessInstances(Long userId, int start, int length)
	{
		return flowDAO.getFlowProcessInstances(userId, start, length);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public long getFlowProcessInstancesCountForAb(Long userId)
	{
		return flowDAO.getFlowProcessInstancesCountForAb(userId);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public List<FlowProcessInstances> getFlowProcessInstancesAb(Long userId, int start, int length)
	{		
		return flowDAO.getFlowProcessInstancesAb(userId, start, length);
	}
	

	/**
	 * 获取用户流程任务已经结束，但流程还没有结束的任务的数量
	 * @param userId
	 * @return
	 */
	public long getProcessCountByUser(Long userId)
	{
		return flowDAO.getProcessCountByUser(userId);
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
		return flowDAO.getProcessByUser(userId, start, length);
	}
	
	/**
	 * 得到流程实例及流程中的所有任务
	 */
	public FlowProcessInstances getProcessInstanceAndTask(Long processInstanceId)
	{
		return flowDAO.getProcessInstanceAndTask(processInstanceId);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public long getTaskCountForHidden(Long userId)
	{
		return flowDAO.getTaskCountForHidden(userId);
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
		return flowDAO.getTaskForHidden(userId, start, length);	
	}
	
}
