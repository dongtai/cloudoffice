package apps.transmanager.weboffice.databaseobject.caibian;
import java.util.Date;

import javax.persistence.Column;
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
 * 报送表，记录报送的情况
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 2.0
 * @see
 * @since 政务版
 */

@Entity
@Table(name="collecteditsend")
public class CollectEditSend implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_collecteditsend_gen")
	@GenericGenerator(name = "seq_collecteditsend_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_COLLECT_EDITSEND_ID") })
	private Long id;
	private String userName;           // 报送用户名登录名
	private String realName;           // 报送用户显示名
	private Long userid;				//报送用户编号
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations org;//报送单位，冗余
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company;//实际的单位，公云

	private String filePath;           // 报送文件路径
	private String fileName;           // 报送文件名
	private String description;        // 报送描述
	private Date sendTime=new Date();             // 报送时间
	@Column(name="del_")
	private boolean del;               // 报送是否被取消。
	
	public CollectEditSend()
	{		
	}

	public CollectEditSend(Long userid,String userName, String realName, String filePath, String fileName, String des,Organizations org,Company company)
	{
		this.userid=userid;
		this.userName = userName;
		this.realName = realName;
		this.filePath = filePath;
		this.fileName = fileName;
		this.description = des;
		this.org=org;
		this.company=company;
		sendTime = new Date();
	}
	
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
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

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Date getSendTime()
	{
		return sendTime;
	}

	public void setSendTime(Date sendTime)
	{
		this.sendTime = sendTime;
	}

	public boolean isDel()
	{
		return del;
	}

	public void setDel(boolean del)
	{
		this.del = del;
	}
	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

		
}
