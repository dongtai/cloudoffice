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
 * 系统中的应用字段表，该表的值由系统第一次启动的时候通过配置文件生成，
 * 系统在运行后，则可以通过重新导入配置文件，来更新系统的使用功能变更。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="systemapps")
public class SystemApps implements SerializableAdapter
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_system_apps_gen")
	@GenericGenerator(name = "seq_system_apps_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SYSTEM_APPS_ID") })
	private Long id;	
	@Column(unique=true, length = 100)
	private String name;               // 应用名
	@Column(length = 100)
	private String displayName;        // 应用显示名
	@Column(name="type_")
	private Integer type;               // 应用位置类型0在左边，1在上边，2为在右边，3为在下边。
	private Integer sortCode;           // 排序号，系统默认排序号
	@Column(length = 1000)
	private String path;                //  功能入口路径
	@Column(length = 1000)
	private String picPath;             //  功能图标路径
	private Date endTime;               // 功能结束使用时间,如果为null，则表示该功能无使用期限限制
	@Column(name="desc_", length = 1000)
	private String desc;                // 描述
	private Boolean flag;              // 是否是公司的用户默认都有的功能，true表示为公司用户默认都有的功能，false表示非默认功能
		
	public SystemApps()
	{		
	}

	public SystemApps(String name, String displayName, int type, int sort,
			String path, String picPath, Date time, String desc, Boolean flag)
	{
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.sortCode = sort;
		this.path = path;
		this.picPath = picPath;
		this.endTime = time;
		this.desc = desc;
		this.flag = flag;
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

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public Integer getType()
	{
		return type;
	}

	public void setType(Integer type)
	{
		this.type = type;
	}

	public Integer getSortCode()
	{
		return sortCode;
	}

	public void setSortCode(Integer sortCode)
	{
		this.sortCode = sortCode;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getPicPath()
	{
		return picPath;
	}

	public void setPicPath(String picPath)
	{
		this.picPath = picPath;
	}

	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}
	
	
	public Boolean getFlag()
	{
		return flag;
	}

	public void setFlag(Boolean flag)
	{
		this.flag = flag;
	}

	public void update(String displayName, int type, int sort, String path,
			String picPath, Date time, String desc, Boolean flag)
	{
		this.displayName = displayName;
		this.type = type;
		this.sortCode = sort;
		this.path = path;
		this.picPath = picPath;
		this.endTime = time;
		this.desc = desc;
		this.flag = flag;
	}	
	
}
