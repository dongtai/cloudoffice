package apps.transmanager.weboffice.databaseobject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 流程定义定义表。
 * 
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="flowprocessdefines")
public class FlowProcessDefines implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowprocessdefines_gen")
	@GenericGenerator(name = "seq_flowprocessdefines_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWPROCESSDEFINES_ID") })
	private Long id;
	@Column(length = 100)
	private String name;	           // 流程名
	@Column(length = 1000)
	private String description;        // 流程描述
	private Date createTime;           // 流程定义时间
	private boolean isValidate = true;        // 流程是否生效
	@OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "processId", nullable = true)
	private List<FlowTaskDefines> tasks;             // 流程中定义的节点。
	/**
	 * 流程中的节点间的流向，key值为流出节点的唯一编号，value值为流入节点的编号。
	 * 例如有流程：1——>2——>3，2——>4, 3——>5，4——>5。
	 * 则该值为：key为1，value值为有值为2的list对象；key为2，value值为有值为3、4的list对象；
	 * key为3，value值为有值为5的list对象；key为4，value值为有值为5的list对象
	 */
	@Lob
	private HashMap<Integer, List<Integer>> flowDir;  // 流程定义中节点的流向值，key值为前节点的number值，value值为后节点的number值。该number值参加FlowTaskDefines中定义
	@Transient 
	private HashMap<Integer, List<Integer>> flowRevDir;   // 流程定义中被流向值，key值为流向节点number值，value值为流来节点值。
	@Lob
	private HashMap<String, Object> datas;                // 流程定义中的全局数据。
	private Integer startNode;                           // 流程的开始节点编号。
	
	public FlowProcessDefines()
	{
	}
	
	/**
	 * 流程定义
	 * @param name 流程名
	 * @param des 流程的描述信息
	 * @param start 流程开始节点编号
	 * @param tasks 流程中的定义的任务节点
	 * @param dir 流程中各个节点的流动方向
	 * @param da 流程中的权限数据
	 */
	public FlowProcessDefines(String name, String des, Integer start, List<FlowTaskDefines> tasks, HashMap<Integer, List<Integer>> dir, HashMap<String, Object> da)
	{
		this.name = name;
		this.description = des;
		this.startNode = start;
		this.tasks = tasks;
		this.flowDir = dir;
		this.datas = da;
	}
	
	public Long getId()
	{
		return id;
	}
	
	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public boolean isValidate()
	{
		return isValidate;
	}

	public void setValidate(boolean isValidate)
	{
		this.isValidate = isValidate;
	}

	public HashMap<Integer, List<Integer>> getFlowDir()
	{
		return flowDir;
	}

	public void setFlowDir(HashMap<Integer,List<Integer>> flowDir)
	{
		this.flowDir = flowDir;
	}

	public HashMap<Integer, List<Integer>> getFlowRevDir()
	{
		return flowRevDir;
	}

	public void setFlowRevDir(HashMap<Integer, List<Integer>> flowRevDir)
	{
		this.flowRevDir = flowRevDir;
	}

	public HashMap<String, Object> getDatas()
	{
		return datas;
	}

	public void setDatas(HashMap<String, Object> values)
	{
		this.datas = values;
	}

	public List<FlowTaskDefines> getTasks()
	{
		return tasks;
	}

	public void setTasks(List<FlowTaskDefines> tasks)
	{
		this.tasks = tasks;
	}

	public Integer getStartNode()
	{
		return startNode;
	}

	public void setStartNode(Integer startNode)
	{
		this.startNode = startNode;
	}
	
	public FlowTaskDefines getStartFlowTaskDefines()
	{
		for (FlowTaskDefines temp : tasks)
		{
			if (startNode.equals(temp.getNumbers()))
			{
				return temp;
			}
		}
		return null;
	}
	
	
}
