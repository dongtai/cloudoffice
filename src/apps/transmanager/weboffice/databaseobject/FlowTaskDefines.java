package apps.transmanager.weboffice.databaseobject;

import java.util.ArrayList;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;
import apps.transmanager.weboffice.domain.TaskExcutor;

/**
 *流程中节点任务的定义。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="flowtaskdefines")
public class FlowTaskDefines implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowtaskdefines_gen")
	@GenericGenerator(name = "seq_flowtaskdefines_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWTASKDEFINES_ID") })
	private Long id;
	@Column(name = "processId")
	private Long processId;             //节点所属的流程定义ID
	@Column(length = 100)
	private String name;	            // 节点名字
	private Integer numbers;            // 节点在该流程定义中的唯一编号，在整个流程定义中，每个节点的值必须唯一。节点间的流动采用该值。
	@Lob
	private ArrayList<TaskExcutor> taskExcutor;   // 该节点运行执行者。
	@Lob
	private HashMap<String, Object> datas;        // 流程定义中的全局数据。	
	private boolean hiddened;                     // 该任务节点是否是隐藏。根据特殊需求，隐藏的节点在流程图中不显示。
		
	public FlowTaskDefines()
	{		
	}
	
	/**
	 * 定义流程任务节点
	 * @param name 节点名
	 * @param numbers 节点在流程中的唯一编号
	 * @param excutor 节点预定义的执行者
	 * @param da 节点预定义的数据
	 * @param hid 节点是否在流程图中隐藏
	 */
	public FlowTaskDefines(String name, Integer numbers, ArrayList<TaskExcutor> excutor, HashMap<String, Object> da, boolean hid)
	{
		this.name = name;
		this.numbers = numbers;
		this.taskExcutor = excutor;
		this.datas = da;
		this.hiddened = hid;
	}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Long getProcessId()
	{
		return processId;
	}
	public void setProcessId(Long processId)
	{
		this.processId = processId;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public Integer getNumbers()
	{
		return numbers;
	}
	public void setNumbers(Integer number)
	{
		this.numbers = number;
	}
	public ArrayList<TaskExcutor> getTaskExcutor()
	{
		return taskExcutor;
	}
	public void setTaskExcutor(ArrayList<TaskExcutor> taskExcutor)
	{
		this.taskExcutor = taskExcutor;
	}
	public HashMap<String, Object> getDatas()
	{
		return datas;
	}
	public void setDatas(HashMap<String, Object> datas)
	{
		this.datas = datas;
	}
	public boolean isHiddened()
	{
		return hiddened;
	}
	public void setHiddened(boolean hiddened)
	{
		this.hiddened = hiddened;
	}
	
	
	
}
