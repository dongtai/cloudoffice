package apps.moreoffice.workflow.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.common.AbstractRuleBase;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.process.core.datatype.DataType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.workflow.core.impl.ConnectionImpl;

import apps.moreoffice.workflow.domain.FreeProcessDefined;


/**
 * 处理自由流程
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class FreeProcessHandler
{
	private ProcessHandler pHandler;
	private final static long START = 1;     // 开始节点编号固定为1
	private final static long END = 2;       // 结束节点编号固定为2.
	private Map<String, FreeProcessDefined> processes = new HashMap<String, FreeProcessDefined>();
	
	
	public ProcessHandler getpHandler()
	{
		return pHandler;
	}

	public void setpHandler(ProcessHandler pHandler)
	{
		this.pHandler = pHandler;
	}
	
	public void dispose()
	{
		pHandler = null;
	}

	/**
	 * 创建一个有开始节点和结束节点的，新的非预定义自由流程，并加入到统一的流程管理中。
	 * @param processId 流程定义id
	 * @param processName 流程名
	 * @param version 流程的版本号
	 * @return
	 */
	public RuleFlowProcess createProcess(String processId, String processName, String version)
	{
		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess(processId);
		factory.name(processName).version(version).packageName("com.yozo")
				.startNode(START).name("Start").done()     
				.endNode(END).name("End").done()         
				.connection(START, END);
		RuleFlowProcess process = factory.validate().getProcess();
		StatefulKnowledgeSession ksession = pHandler.getSession();
		((AbstractRuleBase)((InternalKnowledgeBase)ksession.getKnowledgeBase()).getRuleBase()).addProcess(process);
		FreeProcessDefined fp = new FreeProcessDefined();
		fp.setIndex(END);
		processes.put(processId, fp);
		return process;
	}
	
	/**
	 * 保存自由流程。当新增加节点的时候，需要把自由流程的定义保存下来
	 * @param processId
	 */
	public void saveProcess(String processId)
	{
		FreeProcessDefined fp = processes.get(processId);
		if (fp != null)
		{
			RuleFlowProcess process = (RuleFlowProcess)pHandler.getSession().getKnowledgeBase().getProcess(processId);
			
		}
	}
	
	/**
	 * 设置流程的变量
	 * @param name 变量名
	 * @param type 变量类型
	 * @param value 变量默认值
	 * @param processId 流程定义id
	 */
	public void setVariable(String name, DataType type, Object value, String processId)
	{
		RuleFlowProcess process = (RuleFlowProcess)pHandler.getSession().getKnowledgeBase().getProcess(processId);
		setVariable(name, type, value, process);
	}
	
	/**
	 * 设置流程变量
	 * @param name 变量名
	 * @param type 变量类型
	 * @param value 变量默认值
	 * @param process 流程
	 */
	public void setVariable(String name, DataType type, Object value, RuleFlowProcess process) 
	{
		Variable variable = new Variable();
		variable.setName(name);
		variable.setType(type);
		variable.setValue(value);
		process.getVariableScope().getVariables().add(variable);
	}
		
	/**
	 * 为已经定义自由流程增加动态节点。
	 * 该方法是在自由流程的结束节点前加一个新的任务节点
	 * @param node 任务节点
	 * @param processId 需要加入的流程id
	 */
	public void addTask(Node node, String processId)
	{
		RuleFlowProcess process = (RuleFlowProcess)pHandler.getSession().getKnowledgeBase().getProcess(processId);		
		Node to = process.getNode(END);    // 
		List<Connection> con = to.getIncomingConnections(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
		Node from = con.get(0).getFrom();           //按照定义，一个结束节点只可能有一个入口节点。		
		
		FreeProcessDefined fp = processes.get(processId);
		long index = fp.getIndex() + 1;
		((org.jbpm.workflow.core.Node)node).setId(index);
		fp.setIndex(index);
		addTask(node, from, to, process);
		
	}
	
	/**
	 * 为已经定义自由流程增加动态节点。
	 * 该方法是在自由流程的from节点和to节点间增加一个新的任务节点
	 * @param node 任务节点
	 * @param processId 需要加入的流程id
	 */
	public void addTask(Node node, Node from, Node to, String processId)
	{
		RuleFlowProcess process = (RuleFlowProcess)pHandler.getSession().getKnowledgeBase().getProcess(processId);
		
		FreeProcessDefined fp = processes.get(processId);
		long index = fp.getIndex() + 1;
		((org.jbpm.workflow.core.Node)node).setId(index);
		fp.setIndex(index);
		addTask(node, from, to, process);
	}
	
	private void addTask(Node node, Node from, Node to, RuleFlowProcess process)
	{
		process.addNode(node);
				
		Map<String, List<Connection>> pr = from.getOutgoingConnections();
		for (List<Connection> t : pr.values())    // 重新设置前面节点的to出口
		{
			for (Connection tt : t)
			{
				if (tt.getTo() == to && tt.getFrom() == from)
				{					
					((ConnectionImpl)tt).setTo(node);    // 重新设置前面节点的to出口
					((org.jbpm.workflow.core.Node)to).removeIncomingConnection(tt.getFromType(), tt);        // 去掉后面节点的from出口
				}
			}
		 }
		
		 ConnectionImpl con = new ConnectionImpl(node, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
	            to, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
		 
	}
	
	
}
