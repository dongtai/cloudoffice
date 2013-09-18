package apps.transmanager.weboffice.databaseobject.archive;

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

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 归档文件查看权限
 * 文件注释
 * <p>
 * <p>
 * @author  Administrator
 * @version 1.0
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="archivereadpower")
public class ArchiveReadPower implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_archivereadpower_gen")
	@GenericGenerator(name = "seq_archivereadpower_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ARCHIVEREADPOWER_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private ArchiveForm archiveForm;
	
	private Date startdate=new Date();//授权开始日期
	private Date enddate;//授权开始日期
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users reader;//阅读者
	private Integer readnum=0;//阅读次数
	private Integer isEffect=1;//是否有效,默认有效
	
	
	public Integer getReadnum()
	{
		return readnum;
	}
	public void setReadnum(Integer readnum)
	{
		this.readnum = readnum;
	}
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public ArchiveForm getArchiveForm()
	{
		return archiveForm;
	}
	public void setArchiveForm(ArchiveForm archiveForm)
	{
		this.archiveForm = archiveForm;
	}
	public Date getStartdate()
	{
		return startdate;
	}
	public void setStartdate(Date startdate)
	{
		this.startdate = startdate;
	}
	public Date getEnddate()
	{
		return enddate;
	}
	public void setEnddate(Date enddate)
	{
		this.enddate = enddate;
	}
	public Users getReader()
	{
		return reader;
	}
	public void setReader(Users reader)
	{
		this.reader = reader;
	}
	public Integer getIsEffect()
	{
		return isEffect;
	}
	public void setIsEffect(Integer isEffect)
	{
		this.isEffect = isEffect;
	}


}
