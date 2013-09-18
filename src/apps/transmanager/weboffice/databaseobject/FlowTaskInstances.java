package apps.transmanager.weboffice.databaseobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.constants.both.FlowConstants;
import apps.transmanager.weboffice.domain.SerializableAdapter;
import apps.transmanager.weboffice.domain.TaskExcutor;

/**
 * 流程节点执行实例
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="flowtaskinstances")
public class FlowTaskInstances implements SerializableAdapter
{	
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowtaskinstances_gen")
	@GenericGenerator(name = "seq_flowtaskinstances_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWTASKINSTANCES_ID") })
	private Long id;
	@Column(name = "processInstanceId")
	private Long processInstanceId;    //  节点所属的流程实例ID
	private Date startTime;           // 节点启动时间
	private Date endTime;             // 节点执行结束时间
	@Lob
	private ArrayList<TaskExcutor> taskExcutor;   // 该节点分配的执行者，默认同节点定义的一致。
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.NO_ACTION) 
	private Users user;   // 该节点实际运行执行者。
	@Lob
	private HashMap<String, Object> datas;        // 流程节点中执行的数据。
	@Column(name = "status_")
	private int status;                // 该节点当前状态。参加com.evermore.weboffice.constants.both.FlowConstants中的定义
	// 没有流程预定义的流程实例会有下列信息
	@Column(name = "name_", length = 1000)
	private String name;	            // 该节点任务名。
	private boolean hiddened;           // 该任务节点是否是隐藏。根据特殊需求，隐藏的节点在流程图中不显示。
	private Integer numbers;            // 节点在该流程中的唯一编号，在整个流程中，每个节点的值必须唯一。节点间的流动采用该值。如果有预先流程定义，则该值同预先流程定义一致
	
	public FlowTaskInstances()
	{	
	}
		
	/**
	 * 构建流程节点执行实例。
	 * @param processInstanceId
	 * @param taskExcutor
	 * @param user
	 */
	public FlowTaskInstances(ArrayList<TaskExcutor> taskExcutor, String name, HashMap<String, Object> data)
	{
		this.taskExcutor = taskExcutor;
		this.name = name;
		this.datas = data;
		this.status = FlowConstants.CREATED;
		startTime = new Date();
	}
	
	/**
	 * 构建流程节点执行实例。
	 * @param processInstanceId
	 * @param taskExcutor
	 * @param user
	 */
	public FlowTaskInstances(Long processInstanceId, ArrayList<TaskExcutor> taskExcutor)
	{
		this.processInstanceId = processInstanceId;
		this.taskExcutor = taskExcutor;
		this.status = FlowConstants.CREATED;
		startTime = new Date();
	}
	
	/**
	 * 构建流程节点执行实例，并开始执行。
	 * @param processInstanceId
	 * @param taskExcutor
	 * @param user
	 */
	public FlowTaskInstances(Long processInstanceId, ArrayList<TaskExcutor> taskExcutor, Users user)
	{
		this.processInstanceId = processInstanceId;
		this.taskExcutor = taskExcutor;
		this.user = user;
		this.status = FlowConstants.INPROGRESS;
		startTime = new Date();
	}
	
	/**
	 * 构建流程节点执行实例，并开始执行。
	 * @param processInstanceId
	 * @param taskExcutor
	 * @param user
	 */
	public FlowTaskInstances(Long processInstanceId, String name, Users user, Integer num)
	{
		this.processInstanceId = processInstanceId;
		this.name = name;
		this.user = user;
		this.status = FlowConstants.INPROGRESS;
		this.numbers = num;
		hiddened = false;
		startTime = new Date();
	}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Long getProcessInstanceId()
	{
		return processInstanceId;
	}
	public void setProcessInstanceId(Long processInstanceId)
	{
		this.processInstanceId = processInstanceId;
	}
	public Date getStartTime()
	{
		return startTime;
	}
	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}
	public Date getEndTime()
	{
		return endTime;
	}
	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}
	public ArrayList<TaskExcutor> getTaskExcutor()
	{
		return taskExcutor;
	}
	public void setTaskExcutor(ArrayList<TaskExcutor> taskExcutor)
	{
		this.taskExcutor = taskExcutor;
	}
	public Users getUser()
	{
		return user;
	}
	public void setUser(Users user)
	{
		this.user = user;
	}
	public HashMap<String, Object> getDatas()
	{
		return datas;
	}
	public void setDatas(HashMap<String, Object> datas)
	{
		this.datas = datas;
	}
	public int getStatus()
	{
		return status;
	}
	public void setStatus(int status)
	{
		this.status = status;
	}

	public Integer getNumbers()
	{
		return numbers;
	}

	public void setNumbers(Integer numbers)
	{
		this.numbers = numbers;
	}

	public boolean isHiddened()
	{
		return hiddened;
	}

	public void setHiddened(boolean hiddened)
	{
		this.hiddened = hiddened;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public boolean hasUser(Users user)
	{
		if (taskExcutor == null)
		{
			return false;
		}
		for (TaskExcutor temp : taskExcutor)
		{
			if (temp.getType() == temp.USER && temp.getId().longValue() == user.getId().longValue())
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	
}
