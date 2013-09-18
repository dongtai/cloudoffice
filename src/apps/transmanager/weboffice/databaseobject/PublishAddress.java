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
 * 记录系统中，用户发布共享给系统外用户使用的地址
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="publishaddress")
public class PublishAddress implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_publish_address_gen")
	@GenericGenerator(name = "seq_publish_address_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_PUBLISH_ADDRESS_ID") })
	private Long id;
	@Column(name="key_", length = 100)
	private String key;               // 对外路径
	@Column(length = 60000)
	private String innerPath;          // 内部绝对路径    
	private Date date;                 // 有效时间
	private Long userId;               // 发布者Id
	
	public PublishAddress()
	{		
	}
	
	public PublishAddress(String key, String inner, Date date, Long userId)
	{
		this.key = key; 
		this.innerPath = inner;
		this.date = date;
		this.userId = userId;
	}	

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getInnerPath()
	{
		return innerPath;
	}

	public void setInnerPath(String innerPath)
	{
		this.innerPath = innerPath;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}
	

}
