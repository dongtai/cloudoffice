package apps.moreoffice.workflow.process;

import java.util.List;

import org.drools.event.KnowledgeRuntimeEventManager;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;

import apps.moreoffice.workflow.domain.ProcessInstanceUser;
import apps.transmanager.weboffice.domain.workflow.ProcessInstanceInfos;

/**
 * 文件注释
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */
public class InstanceLogHandler extends JPAWorkingMemoryDbLogger
{

	public InstanceLogHandler(KnowledgeRuntimeEventManager session)
	{
		super(session);
	}
		
	public void dispose()
	{
		super.dispose();
		env = null;
		clearFilters();
	}
	
    
	/**
	 * 保存工作流流程实例的执行者
	 * @param userId 工作流的实例启动者
	 * @param pid 工作流的具体实例id
	 * @param pn 工作流的名字
	 */
	public void addProcessInstanceUser(String userId, long pid, String pn, String disc)
	{
		ProcessInstanceUser piu = new ProcessInstanceUser(userId, pid, pn, disc);
		getEntityManager().persist(piu);
	}
	
	/**
	 * 取得由userId用户开始执行的工作流流程
	 * @param userId
	 * @return
	 */
	public List<ProcessInstanceUser> getProcessInstanceUser(String userId)
	{
		List<ProcessInstanceUser> result = getEntityManager()
			.createQuery("FROM ProcessInstanceUser v WHERE v.userId = :userId ORDER BY date")
			.setParameter("userId", userId)
			.getResultList();
		return result;
	}
	
	/**
	 * 通过流程实例Id获得列出的定义名字
	 */
	public String getProcessName(long processInstanceId)
	{
		List<ProcessInstanceUser> result = getEntityManager()
			.createQuery("FROM ProcessInstanceUser v WHERE v.processInstanceId = :processIntanceId ORDER BY date")
			.setParameter("processIntanceId", processInstanceId)
			.getResultList();
		if (result != null && result.size() > 0)
		{
			return result.get(0).getProcessName();
		}
		return "";
	}
	
	/**
	 * 取得流程实例Id为processInstanceid的用户开始执行的工作流流程
	 * @param processInstanceId 流程实例Id
	 * @return
	 */
	public ProcessInstanceUser getProcessInstanceByProcessInstanceId(long processInstanceId)
	{
		List<ProcessInstanceUser> result = getEntityManager()
			.createQuery("FROM ProcessInstanceUser v WHERE v.processInstanceId = :processIntanceId ORDER BY date")
			.setParameter("processIntanceId", processInstanceId).getResultList();
		if (result != null && result.size() > 0)
		{
			return result.get(0);
		}
		return null;
	}

	/**
	 * 获得所有的工作流实例值
	 * @return
	 */
	public List<ProcessInstanceLog> findProcessInstances()
	{	
		List<ProcessInstanceLog> result = getEntityManager()
			.createQuery("FROM ProcessInstanceLog ")
			.getResultList();
		return result;
	}

	/**
	 * 获得工作流定义id为processId的所有的工作流实例值
	 * @return
	 */
	public List<ProcessInstanceLog> findProcessInstances(String processId)
	{
		
		List<ProcessInstanceLog> result = getEntityManager()
			.createQuery("FROM ProcessInstanceLog p WHERE p.processId = :processId ")
			.setParameter("processId", processId)
			.getResultList();
		return result;
	}
	
	/**
	 * 获得工作流实例id为processInstanceId的工作流实例值
	 * @return
	 */
	public ProcessInstanceLog findProcessInstance(long processInstanceId)
	{		
		ProcessInstanceLog result = (ProcessInstanceLog)getEntityManager()
			.createQuery("FROM ProcessInstanceLog p WHERE p.processInstanceId = :processInstanceId ")
			.setParameter("processInstanceId", processInstanceId).getSingleResult();
		return result;
	}
    
	/**
	 * 获取工作流实例id为processInstanceId的节点实例	
	 * @param processInstanceId
	 * @return
	 */
	public List<NodeInstanceLog> findNodeInstances(long processInstanceId)
	{
		List<NodeInstanceLog> result = getEntityManager()
			.createQuery("FROM NodeInstanceLog v WHERE v.processInstanceId = :processInstanceId  ORDER BY date ")
			.setParameter("processInstanceId", processInstanceId)
			.getResultList();
		return result;
	}

	/**
	 * 获取工作流id为processInstanceId，节点实例id为nodeId的所有节点
	 * @param processInstanceId
	 * @param nodeId
	 * @return
	 */
	public List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId)
	{		
		List<NodeInstanceLog> result = getEntityManager()
			.createQuery("FROM NodeInstanceLog v WHERE v.processInstanceId = :processInstanceId and v.nodeId = :nodeId  ORDER BY date ")
			.setParameter("processInstanceId", processInstanceId)
			.setParameter("nodeId", nodeId)
			.getResultList();		
		return result;
	}
	 
	/**
	 * 获取工作流实例的信息
	 * @param processId
	 * @return
	 */
	public List<ProcessInstanceInfos> getProcessInstanceInfo(long processId)
	{
		List<ProcessInstanceInfos> result = getEntityManager()
			.createNamedQuery("ProcessInstanceInfos")
        	.setParameter("processId", processId)
        	.getResultList();

        return result;
	}
	
	/**
	 * 获取由uid用户启动的工作流流程
	 * @param uid
	 * @return
	 */
	public List<ProcessInstanceInfos> getProcessUserInstanceInfo(String uid)
	{
		List<ProcessInstanceInfos> result = getEntityManager()
			.createNamedQuery("ProcessInstanceInfosByUID")
			.setParameter("UId", uid)
			.getResultList();
		return result;
	}
	
	/**
	 * 通过流程实例Id取得该流程实例中已经执行过或已经分配的流程任务Id集合值。
	 * @param processInstanceId
	 * @return
	 */
	public List<Long> getProcessTaskId(long processInstanceId)
	{
		List<Long> result = getEntityManager()
			.createNamedQuery("ProcessTaskId")
			.setParameter("pid", processInstanceId)
			.getResultList();
		
		return result;
	}	
	
	/**
	 * 获取某个流程定义的仍然活动（流程实例没有结束）的流程实例。
	 * @param processId
	 * @return
	 */
	public List<ProcessInstanceLog> findActiveProcessInstances(String processId)
	{
		List<ProcessInstanceLog> result = getEntityManager()
			.createQuery("FROM ProcessInstanceLog p WHERE p.processId = :processId AND p.end is null")
			.setParameter("processId", processId).getResultList();
		return result;
	}
	
	/**
	 * 	获取流程中变量记录		
	 * @param processInstanceId
	 * @return
	 */
	public List<VariableInstanceLog> findVariableInstances(long processInstanceId)
	{
    	List<VariableInstanceLog> result = getEntityManager()
			.createQuery("FROM VariableInstanceLog v WHERE v.processInstanceId = :processInstanceId ORDER BY date")
			.setParameter("processInstanceId", processInstanceId)
			.getResultList();
		return result;
    }

    /**
     * 获取流程中变量记录
     * @param processInstanceId
     * @param variableId
     * @return
     */
	public List<VariableInstanceLog> findVariableInstances(long processInstanceId, String variableId) 
	{
    	List<VariableInstanceLog> result = getEntityManager()
			.createQuery("FROM VariableInstanceLog v WHERE v.processInstanceId = :processInstanceId AND v.variableId = :variableId ORDER BY date")
			.setParameter("processInstanceId", processInstanceId)
			.setParameter("variableId", variableId)
			.getResultList();
    	return result;
    }
}
