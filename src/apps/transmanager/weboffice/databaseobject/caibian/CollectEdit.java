package apps.transmanager.weboffice.databaseobject.caibian;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 采编表，记录报送被采编的情况
 * <p>
 * <p>
 * 
 * @author 徐文平 孙爱华改进
 * @version 2.0
 * @see
 * @since 政务版
 */

@Entity
@Table(name="collectedit")
public class CollectEdit implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_collectedit_gen")
	@GenericGenerator(name = "seq_collectedit_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_COLLECT_EDIT_ID") })
	private Long id;
	private Long sendId;               // 报送id
	private String userName;           // 采编用户名登录名
	private String realName;           // 采编用户显示名
	private Long userid;				//采编用户编号
	private String description;        // 采编描述
	private Date collectTime;          // 采编时间
	private String fileName;           // 报送文件名
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations org;//采编单位，冗余
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company;//实际的单位，公云



	public CollectEdit()
	{		
	}

	public CollectEdit(Long sendId,Long userid, String userName, String realName, String des, String file,Organizations org,Company company)
	{		
		this.sendId = sendId;
		this.userid=userid;
		this.userName = userName;
		this.realName = realName;
		this.description = des;
		this.fileName = file;
		this.org=org;
		this.company=company;
		collectTime = new Date();
	}

	public Long getId()
	{
		return id;
	}


	public void setId(Long id)
	{
		this.id = id;
	}
	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Organizations getOrg() {
		return org;
	}

	public void setOrg(Organizations org) {
		this.org = org;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	public Long getSendId()
	{
		return sendId;
	}


	public void setSendId(Long sendId)
	{
		this.sendId = sendId;
	}


	public String getUserName()
	{
		return userName;
	}


	public void setUserName(String userName)
	{
		this.userName = userName;
	}


	public String getRealName()
	{
		return realName;
	}


	public void setRealName(String realName)
	{
		this.realName = realName;
	}


	public String getDescription()
	{
		return description;
	}


	public void setDescription(String description)
	{
		this.description = description;
	}


	public Date getCollectTime()
	{
		return collectTime;
	}


	public void setCollectTime(Date collectTime)
	{
		this.collectTime = collectTime;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	
	
}
