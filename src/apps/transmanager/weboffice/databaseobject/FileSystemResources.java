package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.constants.both.PermissionConst;
import apps.transmanager.weboffice.domain.Resources;

/**
 * 文件资源
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="filesystemresources")
public class FileSystemResources  implements Resources
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_filesystem_resources_gen")
	@GenericGenerator(name = "seq_filesystem_resources_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FILE_SYSTEM_RESOURCES_ID") })
	private Long id;	
	@Column(length = 255)
	private String name;
	@Column(length = 255)
	private String displayName;    // 在空间中的显示名字
	@Column(length=65535)
	private String abstractPath;   // 绝对路径，文件夹包含整个路径、文件包含文件名字
	private boolean isFolder;
	@Column(length = 1000)
	private String description;
	private Date date;
	@Transient
	private Long action;
	
	public FileSystemResources()
	{		
	}
	
	public FileSystemResources(String resourceName, String displayName, String path, boolean isFolder, String des)
	{
		this.name = resourceName;
		this.displayName = displayName;
		this.abstractPath = path;
		this.isFolder = isFolder;
		this.description = des;
		date = new Date();		
	}
	
	public FileSystemResources(FileSystemResources re)
	{
		this.name = re.name;
		this.displayName = re.displayName;
		this.abstractPath = re.abstractPath;
		this.isFolder = re.isFolder;
		this.description = re.description;
		date = new Date();		
	}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getDisplayName()
	{
		return displayName;
	}
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
	public String getAbstractPath()
	{
		return abstractPath;
	}
	public void setAbstractPath(String abstractPath)
	{
		this.abstractPath = abstractPath;
	}
	public boolean isFolder()
	{
		return isFolder;
	}
	public void setFolder(boolean isFolder)
	{
		this.isFolder = isFolder;
	}
	public Long getAction()
	{
		return action;
	}
	public void setAction(Long action)
	{
		this.action = action;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public Date getDate()
	{
		return date;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public String getResourceName()
	{
		return name;
	}

	public void setResurceName(String name)
	{
		this.name = name;
	}

	public Integer getResourceType()
	{
		return PermissionConst.FILE_RESOURCE;
	}
	
	public void update(FileSystemResources n)
	{
		this.name = n.name;
		this.displayName = n.displayName;
		this.description = n.description;
	}
	
}
