package apps.transmanager.weboffice.databaseobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.constants.both.FlowConstants;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 流程执行实例
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="flowprocessinstances")
public class FlowProcessInstances implements SerializableAdapter
{	
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowprocessinstances_gen")
	@GenericGenerator(name = "seq_flowprocessinstances_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWPROCESSINSTANCES_ID") })
	private Long id;
	@Column(name = "processId")
	private Long processId;             //  实例所属的流程定义ID，如果流程没有预先定义，则该值为null。
	@Column(length = 1000)
	private String description;
	private Date startTime;           // 流程启动时间
	private Date endTime;           // 流程结束时间
	@Lob
	private ArrayList<Integer> numbers;        // 当前活动节点，即是在流程中节点已经生成，还没有执行完成，该值同流程节点定义中的number是一致的。
	@Lob
	private ArrayList<Integer> endNumbers;        // 已经执行完成的节点，即是在流程中节点已经执行完成，该值同流程节点定义中的number是一致的。
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.NO_ACTION) 
	private Users user;              // 流程启动者。
	@Lob
	private HashMap<String, Object> datas;                // 流程执行中的全局数据。
	@Column(name = "status_")
	private int status;                       // 流程执行状态。参加com.evermore.weboffice.constants.both.FlowConstants中的定义
	
	// 没有流程预定义的流程实例会有下列信息
	@Column(name = "name_", length = 100)
	private String name;	                  // 流程名
	/**
	 * 流程中的节点间的流向，key值为流出节点的唯一编号，value值为流入节点的编号。
	 * 例如有流程：1——>2——>3，2——>4, 3——>5，4——>5。
	 * 则该值为：key为1，value值为有值为2的list对象；key为2，value值为有值为3、4的list对象；
	 * key为3，value值为有值为5的list对象；key为4，value值为有值为5的list对象
	 */
	@Lob
	private HashMap<Integer, List<Integer>> flowDir;  // 流程定义中节点的流向值，key值为前节点的number值，value值为后节点的number值。该number值参加FlowTaskDefines中定义
	private boolean definedFlag;              // 是否有流程预先定义，为true表示有预先流程定义，为false表示流程没有预先定义。
	private Integer defaultNumbers;           // 当前流程中的活动合并节点的唯一编号，为没有预先流动定义增加。如果该值不为null表示还有任务没有回到合并节点，为null表示由合并节点又有新流出
	private int currentNumber;                // 当前已经使用的最大编号，为没有流程预定义增加，在增加新节点时在此基础上编号，以保证唯一。
	private int defaultCount;                        // 当前流程中有默认节点发的节点数，一旦有一个节点回到合并节点，则该值就减1
	@Transient
	private List<FlowTaskInstances> instances;       // 该流程实例中所有的任务节点实例
	
	
	public FlowProcessInstances()
	{	
	}
	
	/**
	 * 开始一个流程实例。
	 * @param processId 流程所属的流程定义Id
	 * @param des 流程开始备注
	 * @param user 流程开始执行者
	 */
	public FlowProcessInstances(Long processId, String des, Users user)	
	{
		this.processId = processId;
		this.description = des;
		this.user = user;
		definedFlag = true;
		startTime = new Date();
	}
	
	/**
	 * 开始一个非预先定义的流程实例。
	 * @param name 流程名字
	 * @param des 流程开始备注
	 * @param user 流程开始执行者
	 */
	public FlowProcessInstances(String name, String des, Users user)	
	{
		this.name = name;
		this.description = des;
		this.user = user;
		definedFlag = false;
		currentNumber = 0;
		status = FlowConstants.INPROGRESS;
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
	public Long getProcessId()
	{
		return processId;
	}
	public void setProcessId(Long processId)
	{
		this.processId = processId;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
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
	public ArrayList<Integer> getNumbers()
	{
		return numbers;
	}
	public void setNumbers(ArrayList<Integer> numbers)
	{
		this.numbers = numbers;
	}
	public ArrayList<Integer> getEndNumbers()
	{
		return endNumbers;
	}
	public void setEndNumbers(ArrayList<Integer> endNumbers)
	{
		this.endNumbers = endNumbers;
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

	public boolean isDefinedFlag()
	{
		return definedFlag;
	}

	public void setDefinedFlag(boolean definedFlag)
	{
		this.definedFlag = definedFlag;
	}

	public Integer getDefaultNumbers()
	{
		return defaultNumbers;
	}

	public void setDefaultNumbers(Integer defaultNumbers)
	{
		this.defaultNumbers = defaultNumbers;
	}

	public int getCurrentNumber()
	{
		return currentNumber;
	}

	public void setCurrentNumber(int currentNumber)
	{
		this.currentNumber = currentNumber;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public HashMap<Integer, List<Integer>> getFlowDir()
	{
		return flowDir;
	}

	public void setFlowDir(HashMap<Integer, List<Integer>> flowDir)
	{
		this.flowDir = flowDir;
	}

	public int getDefaultCount()
	{
		return defaultCount;
	}

	public void setDefaultCount(int defaultCount)
	{
		this.defaultCount = defaultCount;
	}

	public List<FlowTaskInstances> getInstances()
	{
		return instances;
	}

	public void setInstances(List<FlowTaskInstances> instances)
	{
		this.instances = instances;
	}
	
	
	
	
}
