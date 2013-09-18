package apps.moreoffice.workflow.services;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.drools.definition.process.Process;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;

import apps.moreoffice.workflow.domain.ProcessInstanceUser;
import apps.moreoffice.workflow.object.NodeDiagramInfo;
import apps.moreoffice.workflow.object.ProcessImgInfo;
import apps.moreoffice.workflow.process.ProcessHandler;
import apps.moreoffice.workflow.process.WorkflowManagementFactory;
import apps.transmanager.weboffice.constants.both.WorkflowConst;
import apps.transmanager.weboffice.domain.workflow.ProcessDefinitionInfo;
import apps.transmanager.weboffice.domain.workflow.ProcessInstanceInfos;
import apps.transmanager.weboffice.domain.workflow.TaskInfo;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 工作流流程任务处理类
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class WorkflowServices
{	
	public int processTask(HashMap<String, String> field, boolean startProcess)
	{
		try
		{
			String action = field.get("actionId");
			String actorId = field.get("actorId");
			String taskId = field.get("taskId");			
			String processInstanceId = field.get("processInstanceId");
			long taskIdlong = 0;			
			WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");
			ProcessHandler ph = wmf.getProcessHandler();
			WorkflowProcessInstanceImpl wpi;
			if (startProcess)
			{
				if (action.equals("cancelStep"))   // 不启动工作流，也不保存输入的数据
				{
					return WorkflowConst.CANCEL;
				}
				String dis = field.get("processDic");
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("actorId", actorId);
				wpi = (WorkflowProcessInstanceImpl)wmf.getProcessHandler().startProcess(field.get("processId"), actorId, params, dis);
				if (wpi != null)
				{
					long pid = wpi.getId();
					processInstanceId = String.valueOf(pid);
					field.put("processInstanceId", processInstanceId);
					field.put("processName", wpi.getProcessName());
					int a = 10;
					for (int i = 0; i < a; i++)   // 由于流程执行后，立即取数据有可能有问题，这里采用此方式临时处理。
					{
						List<TaskSummary>  ts = wmf.getTask(actorId);
				    	if (ts != null && ts.size() > 0)
				    	{
				    		for (TaskSummary temp : ts)
				    		{
				    			if (temp.getProcessInstanceId() == pid)
				    			{
				    				taskIdlong = temp.getId();
				    				field.put("taskId", String.valueOf(taskIdlong));
				    				i = 10;
				    				break;
				    			}
				    		}
				    	}
					}
				}
			}
			else
			{
				wpi = (WorkflowProcessInstanceImpl)ph.getProcessInstance(Long.parseLong(processInstanceId));
				if (taskId.length() > 0)
				{
				    taskIdlong = Long.parseLong(taskId);
				}
			}
			if (wpi == null)          //异常处理
			{
				return WorkflowConst.ERROR;
			}
			//System.out.println("preactorid is ======================   "+wpi.getVariable("preActorId")+"   "+wpi.getVariable("preActorIdMap"));
			wpi.setVariable("actorId", field.get("userId"));
			String actorCount = field.get("actorCount");
			if (actorCount != null)
			{
				int size = Integer.valueOf(actorCount);
				for (int i = 1; i <= size; i++)
				{
					wpi.setVariable("acotrId" + i, field.get("userId" + i));
				}
			}
			wpi.setVariable("groupId", field.get("groupId"));
			String groupCount = field.get("groupCount");
			if (groupCount != null)
			{
				int size = Integer.valueOf(groupCount);
				for (int i = 1; i <= size; i++)
				{
					String tempG = "groupCount" + i;
					wpi.setVariable(tempG, field.get(tempG));
				}
			}

			String attachAddress = field.get("attachAddress");
			if (attachAddress != null && attachAddress.length() > 0)
			{
				wpi.setVariable("attachAddress", attachAddress);
			}
			
			if(action.equals("cancelStep"))    // 任务取消
			{
				return WorkflowConst.CANCEL;
			}
			else
			{
				//TaskManagement tm = wmf.getTaskManagement();
				field.put("realActorId", actorId);      // 实际执行者
				field.put("realActionId", field.get(action));    //实际执行动作
				Date date = new Date();
				field.put("realDateId", getFormateDate(date, "-"));
				ph.addTaskFormContent(taskIdlong, field);    // 保存表单业务数据
				
				if (action.equals("nextStep"))    // 任务通过完成
				{
					wpi.setVariable("auditStatus", true);
					wmf.completeTask(taskIdlong, null, actorId);
					//System.out.println("preactorid is ------------------------------   "+wpi.getVariable("preActorId")+"------  "+wpi.getVariable("preActorIdMap"));
					return WorkflowConst.SUCCESS;
				}
				else if(action.equals("preStep"))       // 任务不通过完成
				{
					wpi.setVariable("auditStatus", false);
					String tId = field.get("userId"); 
					if (tId == null || "".equals(tId))
					{
						HashMap hmap = (HashMap)wpi.getVariable("preActorIdMap");
						if (hmap != null)
						{
							wpi.setVariable("actorId", hmap.get(wpi.getVariable("preActorId")));
						}
					}
					wmf.completeTask(taskIdlong, null, actorId);
					return WorkflowConst.SUCCESS;
				}
				else if (action.equals("delegateStep")) // 授权给他人
				{
				    ph.delegateTask(taskIdlong, actorId, field.get("userId"));
				    return WorkflowConst.DELEGATE;
				}
				else if(action.equals("saveStep"))    // 任务保存，保存当前用户的选择
				{
					return WorkflowConst.SAVE;
				}
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return WorkflowConst.ERROR;
	}
	
	public String processDocument()
	{
		return "";
	}
	
	
	private static String getFormateDate(Date date, String dot)
    {
        if (date != null)
        {
            int year = date.getYear() + 1900;
            int month = date.getMonth() + 1;
            int day = date.getDate();
            StringBuffer sb = new StringBuffer();
            sb.append(year)
              .append(dot)
              .append(month >= 10 ? month : ("0" + month))
              .append(dot)
              .append(day >= 10 ? day : ("0" + day))
              .append(" ")
              .append(date.getHours())
              .append(":")
              .append(date.getMinutes());
            return sb.toString();
        }
        return "";
    }
	
	/**
     * 得到用户userId发起的的所有流程实例
     * @param userId 发起用户的id
     * @return
     */
    public List<ProcessInstanceInfos> getProcessInstanceInfoByUser(String userId)
    {
    	WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");
    	return wmf.getProcessHandler().getProcessInstanceInfoByUserId(userId);
    }
    
    /**
     * 获取工作流的表单定义内容
     * @param 那么工作流的定义的名字
     * @param path 为具体工作流状态表单定义的路径，需要以“/path/”格式传入
     * @return
     */
    public String getProcessDefinedStatus(ServletContext context, String name, String path)
    {
    	Configuration cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(context, "");
		String pName = name;
		
		Map<String, Object> root = new HashMap<String, Object>();
		CharArrayWriter out = new CharArrayWriter();		
				
		root.put("instanceStatus", path + pName + ".ftl");
		root.put("processName", pName);	
		root.put("processStatus", false);
				
		root.put("processInfo", new ProcessImgInfo("/workflow/flowimage/" + pName + ".png", 0, 0));	
				
		out.reset();
		try
		{
			Template t = cfg.getTemplate("/workflow/commons/processStatus.ftl", "utf-8"); 
			t.process(root, out);
			return out.toString();
		}
		catch (Exception e) 
		{
			e.printStackTrace();	
		}
    	return "error";
    }
    
    
    /**
     * 获取工作流实例id为processinstanceId的工作流状态
     * @param processInstanceId 工作流实例id
     * @param path 为具体工作流状态表单定义的路径，需要以“/path/”格式传入
     * @return
     */
    public String getProcessStatus(ServletContext context, String processInstanceId, String path)
    {        
    	long pid = Long.valueOf(processInstanceId);
        WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");   
        ProcessHandler ph = wmf.getProcessHandler();
        //ProcessInstance pi = ph.getProcessInstance(pid);
        
    	Configuration cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(context, ""); 		
		String pName = ph.getProcessName(pid);	
		StringBuffer sb = new StringBuffer();
		
		List<Long> taskId = ph.getProcessTaskId(pid);
		Map<String, String> taskMap;
		Map<String, Object> root = new HashMap<String, Object>();
		Map<String, Object> rootTemp = new HashMap<String, Object>();
		CharArrayWriter out = new CharArrayWriter();
		String taskName;
		for(Long tid : taskId)
		{
			taskMap = ph.getTaskFormContentAndName(tid);
			rootTemp = new HashMap<String, Object>();
			if (taskMap != null && taskMap.size() > 0)
			{				
				rootTemp.putAll(taskMap);
				rootTemp.put("processStatus", true);
				taskName = taskMap.get("taskNameValue");			
				try
				{
					out.reset();
					Template t = cfg.getTemplate(path + taskName + ".ftl", "utf-8");    //"workflowheader.ftl", "utf-8");
					t.process(rootTemp, out);
					sb.append(out.toString());
					root.put(taskName, true);
				}
				catch (Exception e) 
				{
					e.printStackTrace();	
				}
			}
		}		
		
		root.put("processTaskExc", sb.toString());		
		root.put("instanceStatus", path + pName + ".ftl");
		root.put("processName", pName);	
		root.put("processStatus", false);
				
		root.put("processInfo", new ProcessImgInfo("/workflow/flowimage/" + pName + ".png", 0, 0));	
		
		List<NodeDiagramInfo> ndi = wmf.getGraphViewerHandler().getActiveDiagramInfo(pid);
		if (ndi != null && ndi.size() > 0)
		{
			ArrayList<ProcessImgInfo> pii = new ArrayList<ProcessImgInfo>();
			for (NodeDiagramInfo tempN : ndi)
			{
				pii.add(new ProcessImgInfo("/workflow/flowimage/arrow.png", tempN.getX(), tempN.getY()));
			}
			root.put("activeList", pii);
		}
		
		out.reset();
		try
		{
			Template t = cfg.getTemplate("/workflow/commons/processStatus.ftl", "utf-8"); 
			t.process(root, out);
			return out.toString();
			//sb.append(out.toString());
			//return sb.toString();
		}
		catch (Exception e) 
		{
			e.printStackTrace();	
		}
    	return "error";
    }
    
    
    /**
     * 启动一个新的流程定义id为processId的工作流实例 
     * @param processId 工作流流程定义ID
     * @param actorId 开始工作流的人员
     * @param path 任务相关联的表单所在的目录，需要以“/path/”格式传入
     * @param name 任务的名字
     * @return
     */
    public  String startProcess(ServletContext context, String processId, String actorId, String path, String name, String sendRedirect)
    {    	
    	Configuration cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(context, "");     //"workflow");
		Map<String, Object> root = new HashMap<String, Object>();
		//root.put("processName", "Evaluation1");		
		root.put("processId", processId);
		root.put("actorId", actorId);
		//root.put("sendRedirect", sendRedirect);
		
		CharArrayWriter out = new CharArrayWriter();
		try
		{
			Template t = cfg.getTemplate(path + name + ".ftl", "utf-8");    //"workflowheader.ftl", "utf-8");
			t.process(root, out);
			return out.toString();
		}
		catch (Exception e) 
		{
			e.printStackTrace();	
		}
    	return "error";
    }      
    
    /**
     * 启动一个新的流程定义id为processId的工作流实例 
     * @param processId 工作流流程定义ID
     * @param actorId 开始工作流的人员
     * @param description 开始工作流的描述
     * @param parameters 开始工作流的参数
     * @return
     */
    public  boolean startProcess(String processId, String actorId, String description, Map<String, Object> params)
    {
    	WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");
    	if (params == null)
    	{
	    	params = new HashMap<String, Object>();
			params.put("actorId", actorId);
    	}
    	try
    	{
	    	ProcessInstance pi = wmf.getProcessHandler().startProcess(processId, actorId, params, description);
	    	return pi != null;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    }
    
    /**
     * 授权流程定义id为processInstanceId的工作流实例 
     * 
     * @param processId 工作流流程定义ID
     * @param actorId 开始工作流的人员
     * @param path 任务相关联的表单所在的目录，需要以“/path/”格式传入
     */
    public String delegateProcess(ServletContext context, String taskId, String processInstanceId, String actorId, String path, String sendRedirect)
    {
        Configuration cfg = new Configuration();
        cfg.setServletContextForTemplateLoading(context, "");     //"workflow");
        Map<String, Object> root = new HashMap<String, Object>();
        //root.put("processName", "Evaluation1");       
        root.put("taskId", taskId);
        root.put("actorId", actorId);
        root.put("processInstanceId", processInstanceId);
        //root.put("sendRedirect", sendRedirect);
        
        CharArrayWriter out = new CharArrayWriter();
        try
        {
            Template t = cfg.getTemplate("/workflow/commons/delegate.ftl", "utf-8");    //"workflowheader.ftl", "utf-8");
            t.process(root, out);
            return out.toString();
        }
        catch (Exception e) 
        {
            e.printStackTrace();    
        }
        return "error";
    }
    
    
    /**
     * 流程任务定义的路径及任务的文件名
     * @param path 任务相关联的表单所在的目录，需要以“/path/”格式传入
     * @param name 任务的名字
     * @param actorId 任务执行者
     * @param taskId 任务Id
     * @param processInstanceId 流程实例Id
     * @return 返回表单的html代码
     */
    public String getTaskForm(ServletContext context, String path, String name, String actorId, String taskId, String processInstanceId,
        String sendRedirect)
    {	
    	//HttpSession session = getThreadLocalRequest().getSession();
        //actorId = ((Userinfo)session.getAttribute("userKey")).getUserName();
        
        WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");
        Map<String, String> saveF = wmf.getProcessHandler().getTaskFormContent(Long.valueOf(taskId));
        
    	Configuration cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(context, "");     //"workflow");
		Map<String, Object> root = new HashMap<String, Object>();
		if (saveF != null && saveF.size() > 0)
		{
			root.putAll(saveF);
		}
        ProcessHandler ph = wmf.getProcessHandler();
        Map<String, Object> map = ph.getProcessInstanceVariables(Long.parseLong(processInstanceId));
        //Map<String, String>  map = wmf.getTaskManagement().getTaskFormContent(Long.parseLong(taskID));
        if (map != null)
        {
            root.put("attachAddress", map.get("attachAddress"));
        }
		//root.put("processName", "Evaluation1");		
		//root.put("processId", "freemarker test");
		root.put("processInstanceId", processInstanceId);
		root.put("taskId", taskId);
		root.put("actorId", actorId);	
		//root.put("sendRedirect", sendRedirect);
				
		CharArrayWriter out = new CharArrayWriter();
		try
		{
			Template t = cfg.getTemplate(path + name + ".ftl", "utf-8");    //"workflowheader.ftl", "utf-8");
			t.process(root, out);
			return out.toString();
		}
		catch (Exception e) 
		{
			e.printStackTrace();	
		}
    	return "error";
    }
    
    /**
     * 得到所有的流程定义
     * @return
     */
    public List<ProcessDefinitionInfo> getProcessDefinitionInfo()
    {
    	WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");
    	List<Process> pr = wmf.getProcessHandler().getProcesses();
    	if (pr != null && pr.size() > 0)
    	{
    		ArrayList<ProcessDefinitionInfo> ret = new ArrayList<ProcessDefinitionInfo>(pr.size());
    		ProcessDefinitionInfo tempP;
    		for (Process temp : pr)
    		{
    			tempP = new ProcessDefinitionInfo(temp.getId(), temp.getName(), temp.getVersion(), temp.getPackageName());
    			ret.add(tempP);
    		}
    		return ret;
    	}
    	return null;
    }
    
    /**
     * 得到流程定义id的所有流程实例
     * @param processId流程定义id
     * @return
     */
    public List<ProcessInstanceInfos> getProcessInstanceInfo(String processId)
    {
    	WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");
    	return wmf.getProcessHandler().getProcessInstanceInfo(Long.getLong(processId));
    }
    
    /**
     * 得到用户待处理的任务
     * @param userId 用户id
     * @return
     */
    public List<TaskInfo> getUserTaskInfo(String userId)
    {
    	WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");
    	ProcessHandler ph = wmf.getProcessHandler();
    	List<TaskSummary>  ts = wmf.getProcessHandler().getAssignedTasks(userId);
    	if (ts != null && ts.size() > 0)
    	{
    		ArrayList<TaskInfo> ret = new ArrayList<TaskInfo>(ts.size());
    		TaskInfo tempP;
    		ProcessInstanceUser piu;
    		for (TaskSummary temp : ts)
    		{
    			piu = ph.getProcessInstanceByProcessInstanceId(temp.getProcessInstanceId());
    			List<ProcessInstanceInfos> list = ph.getProcessInstanceInfo(temp.getProcessInstanceId());
    			if (piu != null)
    			{
	    			tempP = new TaskInfo(temp.getId(), temp.getProcessInstanceId(), temp.getName(),
	    					temp.getStatus().ordinal(), temp.getExpirationTime(), temp.getCreatedOn(),
	    					temp.getPriority(), temp.getDescription(), piu.getProcessName(), piu.getDiscription(), piu.getUserId(),
	    					list.get(0).getState());
	    			ret.add(tempP);
    			}
    		}
    		return ret;
    	}
    	return null;
    }
    
    /**
	 * 获得用户拥有的已经执行完成的任务列表。
	 * @param userId
	 * @return
	 */
	public List<TaskInfo> getOwnedCompletedTasks(String userId)
	{
		WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");
		ProcessHandler ph = wmf.getProcessHandler();
    	List<TaskSummary>  ts = wmf.getProcessHandler().getOwnedCompletedTasks(userId);
    	if (ts != null && ts.size() > 0)
    	{
    		ArrayList<TaskInfo> ret = new ArrayList<TaskInfo>(ts.size());
    		TaskInfo tempP;
    		ProcessInstanceUser piu;
    		for (TaskSummary temp : ts)
    		{
    			piu = ph.getProcessInstanceByProcessInstanceId(temp.getProcessInstanceId());
                List<ProcessInstanceInfos> list = ph.getProcessInstanceInfo(temp.getProcessInstanceId());
    			if (piu != null)
    			{
	    			tempP = new TaskInfo(temp.getId(), temp.getProcessInstanceId(), temp.getName(),
	    					temp.getStatus().ordinal(), temp.getExpirationTime(), temp.getCreatedOn(),
	    					temp.getPriority(), temp.getDescription(), piu.getProcessName(), piu.getDiscription(), piu.getUserId(), 
	    					list.get(0).getState());
	    			ret.add(tempP);
    			}
    		}
    		return ret;
    	}
    	return null;
	}
	
	/**
	 * 得到流程实例的附件文档在文档库中的路径。
	 * 
	 * @param taksID  任务ID
	 * @param processInstanceID 流程实例ID
	 */
    public String getAttachPath(String taskID, String processInstanceID)
    {
        WorkflowManagementFactory wmf = (WorkflowManagementFactory)ApplicationContext.getInstance().getBean("workflowManagement");
        ProcessHandler ph = wmf.getProcessHandler();
        Map<String, Object> map = ph.getProcessInstanceVariables(Long.parseLong(processInstanceID));
        //Map<String, String>  map = wmf.getTaskManagement().getTaskFormContent(Long.parseLong(taskID));
        if (map != null)
        {
            return map.get("attachAddress").toString();
        }
        return "";
    }
	
	
	
	
	
}
