package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * Scheduletask entity.
 * 
 * @author MyEclipse Persistence Tools
 */

@Entity
@Table(name="scheduletask")
public class Scheduletask implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_scheduleTask_gen")
	@GenericGenerator(name = "seq_scheduleTask_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SCHEDULE_TASK_ID") })
	private Integer taskId;
	@Column(length = 100)
	private String taskName;
	@Column(length = 100)
	private String daytype;
	private Integer weekvalue;
	private Integer monthvalue;
	@Column(length = 20)
	private String starttime;
	@Column(length = 50)
	private String startday;
	@Column(length = 1000)
	private String schedulecontent;
	private Integer userid;
	private Date addtime;
	@Column(length = 255)
	private String state;
	@Column(length = 1000)
	private String backpath;
	private Integer backtype;
	@Column(length = 1000)
	private String backcond;
	@Column(length = 1000)
	private String reserveA;
	@Column(length = 255)
	private String reserveB;

	// Constructors

	/** default constructor */
	public Scheduletask()
	{
	}

	/** full constructor */
	public Scheduletask(String taskName, String daytype, Integer weekvalue,
			Integer monthvalue, String starttime, String startday,
			String schedulecontent, Integer userid, Date addtime, String state,
			String backpath, Integer backtype, String backcond,
			String reserveA, String reserveB)
	{
		this.taskName = taskName;
		this.daytype = daytype;
		this.weekvalue = weekvalue;
		this.monthvalue = monthvalue;
		this.starttime = starttime;
		this.startday = startday;
		this.schedulecontent = schedulecontent;
		this.userid = userid;
		this.addtime = addtime;
		this.state = state;
		this.backpath = backpath;
		this.backtype = backtype;
		this.backcond = backcond;
		this.reserveA = reserveA;
		this.reserveB = reserveB;
	}

	// Property accessors

	public Integer getTaskId()
	{
		return this.taskId;
	}

	public void setTaskId(Integer taskId)
	{
		this.taskId = taskId;
	}

	public String getTaskName()
	{
		return this.taskName;
	}

	public void setTaskName(String taskName)
	{
		this.taskName = taskName;
	}

	public String getDaytype()
	{
		return this.daytype;
	}

	public void setDaytype(String daytype)
	{
		this.daytype = daytype;
	}

	public Integer getWeekvalue()
	{
		return this.weekvalue;
	}

	public void setWeekvalue(Integer weekvalue)
	{
		this.weekvalue = weekvalue;
	}

	public Integer getMonthvalue()
	{
		return this.monthvalue;
	}

	public void setMonthvalue(Integer monthvalue)
	{
		this.monthvalue = monthvalue;
	}

	public String getStarttime()
	{
		return this.starttime;
	}

	public void setStarttime(String starttime)
	{
		this.starttime = starttime;
	}

	public String getStartday()
	{
		return this.startday;
	}

	public void setStartday(String startday)
	{
		this.startday = startday;
	}

	public String getSchedulecontent()
	{
		return this.schedulecontent;
	}

	public void setSchedulecontent(String schedulecontent)
	{
		this.schedulecontent = schedulecontent;
	}

	public Integer getUserid()
	{
		return this.userid;
	}

	public void setUserid(Integer userid)
	{
		this.userid = userid;
	}

	public Date getAddtime()
	{
		return this.addtime;
	}

	public void setAddtime(Date addtime)
	{
		this.addtime = addtime;
	}

	public String getState()
	{
		return this.state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getBackpath()
	{
		return this.backpath;
	}

	public void setBackpath(String backpath)
	{
		this.backpath = backpath;
	}

	public Integer getBacktype()
	{
		return this.backtype;
	}

	public void setBacktype(Integer backtype)
	{
		this.backtype = backtype;
	}

	public String getBackcond()
	{
		return this.backcond;
	}

	public void setBackcond(String backcond)
	{
		this.backcond = backcond;
	}

	public String getReserveA()
	{
		return this.reserveA;
	}

	public void setReserveA(String reserveA)
	{
		this.reserveA = reserveA;
	}

	public String getReserveB()
	{
		return this.reserveB;
	}

	public void setReserveB(String reserveB)
	{
		this.reserveB = reserveB;
	}
}
