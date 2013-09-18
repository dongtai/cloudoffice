package apps.moreoffice.workflow.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * 工作流流程实例启动者记录类
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="flow_processinstanceuser")
public class ProcessInstanceUser implements Serializable
{    
	private static final long serialVersionUID = 510l;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_processinstanceuser_gen")
    @GenericGenerator(name = "seq_processinstanceuser_gen", strategy = "native", parameters = {@ Parameter(name = "sequence", value = "SEQ_PROCESS_INSTANCESUSER_ID")})
    private Long id;
    private String userId;    
    private long processInstanceId;
    private String processName;    
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_date")
    private Date date;
	private String discription;
	@Column(name = "number_")
	private String number;                    // 流程流水号
    
    public ProcessInstanceUser()
    {
    	this.date = new Date();
    }
    
    public ProcessInstanceUser(String uid, long pid, String pn, String disc)
    {
    	userId = uid;
    	processInstanceId = pid;
    	processName = pn;
    	discription = disc;
    	this.date = new Date();
    }
    
    public String getDiscription()
	{
		return discription;
	}

	public void setDiscription(String discrition)
	{
		this.discription = discrition;
	}
    
    public String getProcessName()
	{
		return processName;
	}

	public void setProcessName(String processName)
	{
		this.processName = processName;
	}
	
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public long getProcessInstanceId()
	{
		return processInstanceId;
	}
	public void setProcessInstanceId(long processIntanceid)
	{
		this.processInstanceId = processIntanceid;
	}
	public Date getDate()
	{
		return date;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public void setId(Long id)
	{
		this.id = id;
	}
    

}
