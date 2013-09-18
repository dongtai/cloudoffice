package apps.moreoffice.workflow.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;

import apps.moreoffice.workflow.object.NodeDiagramInfo;

/**
 * 工作流流程图形处理类。目前只处理静态的图形，即是工作流定义完成后，先预先生产一个png图片，
 * 然后后续的显示根据该图片来进行。
 * 根据bpmn文件动态生成图片的处理方式后续处理。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class GraphViewerHandler
{

	private ProcessHandler ph;
	
	public GraphViewerHandler(ProcessHandler ph)
	{
		this.ph = ph;
	}
	
	public void dispose()
	{
		ph = null;
	}

	/**
	 * 获得instanceId工作流流程实例的活动节点的图形信息
	 * @param instanceId
	 * @return
	 */
	public List<NodeDiagramInfo> getActiveDiagramInfo(long processInstanceId)
	{
		ProcessInstanceLog processInstance = ph.getProcessInstanceLog(processInstanceId);
		if (processInstance == null)    // 没有该id的工作流流程
		{
			return null;
		}
		Map<String, NodeInstanceLog> nodeInstances = new HashMap<String, NodeInstanceLog>();
		List<NodeInstanceLog> nis = ph.getNodeInstances(processInstanceId);
		for (NodeInstanceLog nodeInstance : nis)
		{
			if (nodeInstance.getType() == NodeInstanceLog.TYPE_ENTER)    // 节点开始执行
			{
				nodeInstances.put(nodeInstance.getNodeInstanceId(),	nodeInstance);
			}
			else  if (!"end".equalsIgnoreCase(nodeInstance.getNodeName())) // 已经执行完成，移除
			{
				nodeInstances.remove(nodeInstance.getNodeInstanceId());
			}
		}
		if (!nodeInstances.isEmpty())
		{
			List<NodeDiagramInfo> result = new ArrayList<NodeDiagramInfo>();
			Map<String, NodeDiagramInfo> rets = getDiagramInfo(processInstance.getProcessId());
			if (!rets.isEmpty())
			{
				for (NodeInstanceLog nodeInstance : nodeInstances.values())    // 实例节点信息
				{
					NodeDiagramInfo nd = rets.get(nodeInstance.getNodeId());
					if (nd != null)
					{
						result.add(nd);
					}
				}
			}
		
			return result;
		}
		return null;
	}

	/**
	 * 获取processId工作流定义中的所有节点的图形信息
	 * @param processId
	 * @return
	 */
	private Map<String, NodeDiagramInfo> getDiagramInfo(String processId)
	{
		if (ph == null)
		{
			return null;
		}
		Process process = ph.getProcess(processId);
		if (process == null)
		{
			return null;
		}

		Map<String, NodeDiagramInfo> nodeMap = new HashMap<String, NodeDiagramInfo>();
		if (process instanceof WorkflowProcess)
		{
			addNodesDiagInfo(nodeMap, ((WorkflowProcess) process).getNodes());
		}
		return nodeMap;
	}

	private void addNodesDiagInfo(Map<String, NodeDiagramInfo> nodeMap, Node[] nodes)
	{
		Map<String, Object> meta;
		for (Node node : nodes)
		{
			String nodeId = String.valueOf(node.getId());
			meta = node.getMetaData();
			nodeMap.put(nodeId, new NodeDiagramInfo(nodeId, (Integer)meta.get("x"), (Integer) meta.get("y"),
					(Integer) meta.get("width"), (Integer) meta.get("height")));
			if (node instanceof NodeContainer)
			{
				addNodesDiagInfo(nodeMap, ((NodeContainer) node).getNodes());
			}
		}
	}	

}
