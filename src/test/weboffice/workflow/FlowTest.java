package test.weboffice.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.common.AbstractRuleBase;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.process.core.impl.WorkImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessContext;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClientHandler.AddCommentResponseHandler;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

import apps.moreoffice.workflow.domain.FreeProcessDefined;
import apps.moreoffice.workflow.domain.ProcessInstanceUser;
import apps.moreoffice.workflow.object.NodeDiagramInfo;
import apps.moreoffice.workflow.process.FreeProcessHandler;
import apps.moreoffice.workflow.process.GraphViewerHandler;
import apps.moreoffice.workflow.process.ProcessHandler;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class FlowTest
{

	private static ProcessHandler pm;
	private static GraphViewerHandler gvh;
	
	public static void main(String[] args)
	{
		//FlowTest.test();
		FlowTest aaa = new FlowTest();
		aaa.init();
		
		//aaa.testStartProcess();
		//aaa.testFirstTask();
		aaa.testDefineProcess();
		//aaa.testReadDefin();
	}
	
	public void testReadDefin()
	{
		StatefulKnowledgeSession ksession = pm.getSession();
    	FreeProcessDefined fpd = (FreeProcessDefined)pm.find(2L);
    	try
    	{
	    	byte[] processDefined;
	    	ByteArrayInputStream bos = new ByteArrayInputStream(fpd.getProcessDefined());
	    	ObjectInputStream oos = new ObjectInputStream(bos);
	    	RuleFlowProcess process = (RuleFlowProcess)oos.readObject();	
	    	
	    	((AbstractRuleBase)((InternalKnowledgeBase)ksession.getKnowledgeBase()).getRuleBase()).addProcess(process);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	Map<String, Object> params = new HashMap<String, Object>();			
		List<TaskSummary> t = getTask("john");
		completeTask(t.get(0).getId(), params, "john");
    	
		t = getTask("krisv");
		completeTask(t.get(0).getId(), params, "krisv");
	}
	
	public void testDefineProcess()
	{
		FreeProcessHandler fp = new FreeProcessHandler();
		fp.setpHandler(pm);
		RuleFlowProcess process = fp.createProcess("org.jbpm.HelloWorld", "HelloWorldProcess", "1.0");
		fp.setVariable("pm", new StringDataType(), "john", process);
		fp.setVariable("hr", new StringDataType(), null, process);
		
		
		/*RuleFlowProcess process2 = fp.createProcess("org.jbpm.HelloWorld", "HelloWorldProcess", "1.0");
		fp.setVariable("pm", new StringDataType(), "john", process2);
		fp.setVariable("hr", new StringDataType(), null, process2);*/
		
				
		ActionNode an = new ActionNode();
		an.setId(3);
		an.setName("Action");
		DroolsAction droolsAction = new DroolsAction();
		droolsAction.setMetaData("Action", new aaa());
		an.setAction(droolsAction);
		fp.addTask(an, "org.jbpm.HelloWorld");
				
		HumanTaskNode nf = new HumanTaskNode();
		nf.setId(4);
		nf.setName("human");
		Work work = nf.getWork();
    	if (work == null)
    	{
    		work = new WorkImpl();
    		nf.setWork(work);
    	}
    	work.setParameter("TaskName", "human task");
    	work.setParameter("ActorId", "#{pm}, #{hr}");
    	work.setParameter("Comment", "comment"); 
    	fp.addTask(nf, "org.jbpm.HelloWorld");
    	
    	Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "  krisv,  Bobba Fet,   Steve Rogers,  Jabba Hutt  ");
		//params.put("pm", "john");
		params.put("hr", "mary");
    	ProcessInstance  pi = newProcess("org.jbpm.HelloWorld", "krisv", params);
    	
    	
    	
    	HumanTaskNode nf2 = new HumanTaskNode();
		nf2.setId(5);
		nf2.setName("human2");
		work = nf2.getWork();
    	if (work == null)
    	{
    		work = new WorkImpl();
    		nf2.setWork(work);
    	}    	
    	work.setParameter("TaskName", "human task2");
    	work.setParameter("ActorId", "krisv");
    	work.setParameter("Comment", "comment2");     	
    	fp.addTask(nf2, nf, process.getNode(2), "org.jbpm.HelloWorld");
    	
    	FreeProcessDefined fpd = new FreeProcessDefined();
    	fpd.setProcessId(process.getId());
    	fpd.setPackName(process.getPackageName());
    	try
    	{
	    	byte[] processDefined;
	    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	ObjectOutputStream oos = new ObjectOutputStream(bos);
	    	oos.writeObject(process);
	    	fpd.setProcessDefined(bos.toByteArray());
	    	bos.close();
	    	oos.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	pm.save(fpd);
    	
    	params = new HashMap<String, Object>();			
		List<TaskSummary> t = getTask("john");
		completeTask(t.get(0).getId(), params, "john");
    	
		t = getTask("krisv");
		completeTask(t.get(0).getId(), params, "krisv");
    	
	}
	
	public void testDefineProcess1()
	{
		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.jbpm.HelloWorld");
		factory.name("HelloWorldProcess").version("1.0").packageName("org.jbpm").variable("pm", new StringDataType()).variable("hr", new StringDataType())
				.startNode(1).name("Start").done()
				.actionNode(2).name("Action").action(new aaa()).done()
				.humanTaskNode(3).name("human").taskName("human task").comment("comment").actorId("#{pm}, #{hr}").done()
				.endNode(4).name("End").done()
				.connection(1, 2)
				.connection(2, 3)
				.connection(3, 4);
		RuleFlowProcess process = factory.validate().getProcess();
		StatefulKnowledgeSession ksession = pm.getSession();
		((AbstractRuleBase)((InternalKnowledgeBase)ksession.getKnowledgeBase()).getRuleBase()).addProcess(process);
		
				
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "  krisv,  Bobba Fet,   Steve Rogers,  Jabba Hutt  ");
		params.put("pm", "john");
		params.put("hr", "mary");
		ProcessInstance  pi = newProcess("org.jbpm.HelloWorld", "krisv", params);	
				
		RuleFlowProcess p3 = (RuleFlowProcess)pm.getProcess("org.jbpm.HelloWorld");
		RuleFlowProcess p2 = (RuleFlowProcess)ksession.getKnowledgeBase().getProcess("org.jbpm.HelloWorld");
		RuleFlowProcess p5 = (RuleFlowProcess)ksession.getKnowledgeBase().getProcess("com.sample.evaluation2");
		RuleFlowProcess p6 = (RuleFlowProcess)ksession.getKnowledgeBase().getProcess("com.yozo.finanical_reimbursement");
		
		HumanTaskNode nf = new HumanTaskNode();
		nf.setId(5);
		nf.setName("human2");
		Work work = nf.getWork();
    	if (work == null)
    	{
    		work = new WorkImpl();
    		nf.setWork(work);
    	}
    	work.setParameter("TaskName", "human task2");
    	work.setParameter("ActorId", "krisv");
    	work.setParameter("Comment", "comment2");    	
		p2.addNode(nf);
		
		 Node from = p2.getNode(3);
		 Map<String, List<Connection>> pr = from.getOutgoingConnections();
		 for (List<Connection> t : pr.values())
		 {
			 for (Connection tt : t)
			 {
				 ((ConnectionImpl)tt).setTo(nf);
			 }
		 }
		 
		 Node to = p2.getNode(4);
		 pr = to.getIncomingConnections();
		 for (List<Connection> t : pr.values())
		 {
			 t.clear();
		 }
		 
		 
		 ConnectionImpl con = new ConnectionImpl(nf, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
	            to, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
		
		
		
		
		params = new HashMap<String, Object>();			
		List<TaskSummary> t = getTask("john");
		completeTask(t.get(0).getId(), params, "john");
		
		t = getTask("krisv");
		completeTask(t.get(0).getId(), params, "krisv");
		
	}
	
	public void testStartProcess()
	{
		StatefulKnowledgeSession ksession = pm.getSession();				
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "  krisv,  Bobba Fet,   Steve Rogers,  Jabba Hutt  ");
		params.put("pm", "john");
		params.put("hr", "mary");
		params.put("gi", "group1");
		params.put("gu", "group2");
		params.put("startUser2", "startuser");
		params.put("pm2", "Bobba Fet");
		params.put("pass", false);
		
		org.drools.definition.process.Process p = pm.getProcess("com.sample.evaluation2");
		
		p.getMetaData();
		
		//List<ProcessInstanceInfos> pii = pm.getProcessInstanceInfo("com.sample.evaluation2");
		
		ProcessInstance  pi = newProcess("com.sample.evaluation2", "krisv", params);	
	}
	
	public void testFirstTask()
	{
		StatefulKnowledgeSession ksession = pm.getSession();
		Map<String, Object> params = new HashMap<String, Object>();			
		List<TaskSummary> t = getTask("krisv");
		completeTask(t.get(0).getId(), params, "krisv");
		
		//t = getTask("john");
		//completeTask(t.get(0).getId(), params, "krisv");       
		
		//t = getTask("mary");
		//completeTask(t.get(0).getId(), params, "krisv");
		
		
	}
	
	
	private void test()
	{
		FlowTest aaa = new FlowTest();
		aaa.init();		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "krisv");
		params.put("john", "john");
		params.put("mary", "mary");
		aaa.newProcess("com.sample.evaluation", "krisv", params);
		//aaa.init();
		List<TaskSummary> t = aaa.getTask("krisv");
		Map<String, Object> data0 = new HashMap<String, Object>();
		
		aaa.completeTask(t.get(0).getId(), data0, "krisv");
		
		t = aaa.getTask("john");
		
		aaa.completeTask(t.get(0).getId(), data0, "john");
		
		t = aaa.getTask("mary");
		aaa.completeTask(t.get(0).getId(), data0, "mary");
	}
	
	private void testActiveDiagramInfo(long pid)
	{
		List<NodeDiagramInfo> ret = gvh.getActiveDiagramInfo(pid);
	}
	
	private ProcessInstance testStartPro()
	{	
		try
		{
			StatefulKnowledgeSession ksession = pm.getSession();				
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("employee", "  krisv,  Bobba Fet,   Steve Rogers,  Jabba Hutt  ");
			params.put("pm", "john");
			params.put("hr", "mary");
			params.put("gi", "group1");
			params.put("gu", "group2");
			params.put("startUser2", "startuser");
			params.put("pm2", "Bobba Fet");
			params.put("pass", false);
			
			org.drools.definition.process.Process p = pm.getProcess("com.sample.evaluation2");
			
			p.getMetaData();
			
			//List<ProcessInstanceInfos> pii = pm.getProcessInstanceInfo("com.sample.evaluation2");
			
			ProcessInstance  pi = newProcess("com.sample.evaluation2", "krisv", params);			
			long processId = pi.getId();
			
			//List<ProcessInstanceInfos> pii2 = pm.getProcessInstanceInfo("com.sample.evaluation2");
			
			testActiveDiagramInfo(processId);
			
			//getProcess(ksession, "com.sample.evaluation");			
			WorkflowProcessInstanceImpl wpi = (WorkflowProcessInstanceImpl)pi;
			wpi.setVariable("startUser3", "mary");
			wpi.setMetaData("startUser1", "mary1");
			wpi.setVariable("pm", "john");
			wpi.setVariable("hr", "mary");
			wpi.setVariable("gi", "group1");
			wpi.setVariable("gu", "group2");
			
			System.out.println("  ======     "+wpi.getVariable("startUser"));
			
			Map<String, Object> va = wpi.getVariables();
			Iterator<String> vas = va.keySet().iterator();
			String temps;
			while(vas.hasNext())
			{
				temps = vas.next();
				System.out.println("var-----------------  key:" + temps + "------ value:" + va.get(temps));
			}
			
			Map<String, Object> ma = wpi.getMetaData();
			Iterator<String> mas = ma.keySet().iterator();
			String tempm;
			while(mas.hasNext())
			{
				tempm = mas.next();
				System.out.println("meta-----------------  key:" + tempm + "------ value:" + ma.get(tempm));
			}
			
			
			ProcessInstance  pi2 = ksession.getProcessInstance(1);			
			WorkflowProcessInstanceImpl wpi2 = (WorkflowProcessInstanceImpl)pi2;
			System.out.println("  ======     "+wpi2.getVariable("startUser"));
			
			Map<String, Object> va2 = wpi2.getVariables();
			Iterator<String> vas2 = va2.keySet().iterator();
			String temps2;
			while(vas2.hasNext())
			{
				temps2 = vas2.next();
				System.out.println("var-----------------  key:" + temps2 + "------ value:" + va2.get(temps2));
			}
			
			Map<String, Object> ma2 = wpi2.getMetaData();
			Iterator<String> mas2 = ma2.keySet().iterator();
			String tempm2;
			while(mas2.hasNext())
			{
				tempm2 = mas2.next();
				System.out.println("meta-----------------  key:" + tempm2 + "------ value:" + ma2.get(tempm2));
			}
			
//			List<TaskSummary> t = getTask("sales-rep");
//			completeTask(t.get(0).getId(), params, "sales-rep");	
//			testActiveDiagramInfo(1);
			
			return pi;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
		
		private void testStart()
		{
			try
			{
				//ProcessInstance pi = testStartPro();
				
				StatefulKnowledgeSession ksession = pm.getSession();
			Map<String, Object> params = new HashMap<String, Object>();			
			List<TaskSummary> t = getTask("krisv");
			completeTask(t.get(0).getId(), params, "krisv");
			
			List<ProcessInstanceUser> ret = pm.getUserStartedProcess("krisv");
			
//			Map<String, Object> va2 = ((WorkflowProcessInstanceImpl)pi).getVariables();
//			Iterator<String> vas2 = va2.keySet().iterator();
//			String temps2;
//			while(vas2.hasNext())
//			{
//				temps2 = vas2.next();
//				System.out.println("var===================  key:" + temps2 + "================== value:" + va2.get(temps2));
//			}
//			
//			Map<String, Object> ma2 = ((WorkflowProcessInstanceImpl)pi).getMetaData();
//			Iterator<String> mas2 = ma2.keySet().iterator();
//			String tempm2;
//			while(mas2.hasNext())
//			{
//				tempm2 = mas2.next();
//				System.out.println("meta======================  key:" + tempm2 + "==================== value:" + ma2.get(tempm2));
//			}
//			
//			//pm.abortProcessInstance(2l);
//			
//			testActiveDiagramInfo(1);
//			
//			((WorkflowProcessInstanceImpl)pi).setVariable("pm2", "Bobba Fet");
//			
			t = getTask("john");
			
			Map<String, String> params2 = new HashMap<String, String>();	
	        params2.put("param2", "test aaaaa222222222");	        
	        pm.addTaskFormContent(t.get(0).getId(), params2);
			
			Task task2 = pm.getTaskById(t.get(0).getId());  // 输入映射
	        TaskData taskData = task2.getTaskData();
	        System.out.println("TaskData = "+taskData);
	        Content content = pm.getContent(taskData.getDocumentContentId());
	       
	        System.out.println("Content= "+content);
	        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());	       
	        ObjectInputStream ois = new ObjectInputStream(bais);
	        Map taskinfo =(Map) ois.readObject();
	        System.out.println("TASKINFO = "+taskinfo);
	        
			
	        params.put("Result", "test aaaaa");
	        params.put("abc", "abcvalue");   // 结果映射
	        
	        
	        
			completeTask(t.get(0).getId(), params, "john");
			
			Map<String, String> params3 = new HashMap<String, String>();	
	        params3.put("param3", "test aaaaa333333333");	        
	        pm.addTaskFormContent(t.get(0).getId(), params3);
			
			
//			va2 = ((WorkflowProcessInstanceImpl)pi).getVariables();
//			vas2 = va2.keySet().iterator();
//			while(vas2.hasNext())
//			{
//				temps2 = vas2.next();
//				System.out.println("var+++++++++++++++++++  key:" + temps2 + "================== value:" + va2.get(temps2));
//			}
//			
//			ma2 = ((WorkflowProcessInstanceImpl)pi).getMetaData();
//			mas2 = ma2.keySet().iterator();
//			while(mas2.hasNext())
//			{
//				tempm2 = mas2.next();
//				System.out.println("meta+++++++++++++++++  key:" + tempm2 + "==================== value:" + ma2.get(tempm2));
//			}
			
			
			testActiveDiagramInfo(1);
			t = getTask("mary");
			
			Map<String, String> params4 = new HashMap<String, String>();	
	        params4.put("param4", "test aaaaa4444444444");	        
	        pm.addTaskFormContent(t.get(0).getId(), params4);
			
			completeTask(t.get(0).getId(), params, "mary");		
			
			
			t = getTask("john");
			
			//((WorkflowProcessInstanceImpl)pi).setVariable("pass", true);
			completeTask(t.get(0).getId(), params, "john");			
			testActiveDiagramInfo(1);
			
			t = getTask("mary");
			completeTask(t.get(0).getId(), params, "mary");		
			testActiveDiagramInfo(1);
			//((WorkflowProcessInstanceImpl)pi).setVariable("pass", true);
			
			//((WorkflowProcessInstanceImpl)pi).setVariable("pass", false);
			t = getTask("Bobba Fet");
			completeTask(t.get(0).getId(), params, "Bobba Fet");	
			testActiveDiagramInfo(1);
			
			
			//dispose();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	public void init()
	{
		if (pm == null)
		{
			pm = new ProcessHandler();
			pm.setDirectory("bpmresources");
			gvh = new GraphViewerHandler(pm);
		}
	}
	
	public ProcessInstance newProcess(String defId, String uid, Map<String, Object> params)
	{
		if (pm == null)
		{
			pm = new ProcessHandler();
			pm.setDirectory("bpmresources");
		}
		return pm.startProcess(defId, uid, params, "oiweupoqwte"); 
	}
	
	public List<TaskSummary> getTask(String userId)
	{
		/*if (tm == null)
		{
			tm = new TaskManagement();
		}*/
		return pm.getAssignedTasks(userId);
	}
	
	public void completeTask(long taskId, Map data, String userId)
	{
		/*if (tm == null)
		{
			tm = new TaskManagement();
		}*/
		pm.completeTask(taskId, data, userId);
	}
	
	public void dispose()
	{
		if (pm != null)
		{
			pm.dispose();
			pm = null;
		}
	}
	
	static class DefaultAddCommentResponseHandler implements AddCommentResponseHandler
	{

		public void execute(long commentId)
		{
			// TODO Auto-generated method stub
			
		}

		public void setError(RuntimeException error)
		{
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class aaa implements Action, Serializable
	{
		public void execute(ProcessContext context) throws Exception
		{
			System.out.println("===============\nweqwet\nwe07097-070346234623============\n");
		}
	}
	
}
