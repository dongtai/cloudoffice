package apps.transmanager.weboffice.domain.workflow;

import apps.transmanager.weboffice.domain.SerializableAdapter;


/**
 * 流程定义信息
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */
public class ProcessDefinitionInfo implements SerializableAdapter
{
	/**
	 * 流程定义ID
	 */
	private String id;
	/**
	 * 流程定义名字
	 */
	private String name;
	/**
	 * 流程定义的版本号
	 */
	private String version;
	private String description;
	/**
	 * 流程定义的包名
	 */
	private String packageName;

	public ProcessDefinitionInfo()
	{
	}

	public ProcessDefinitionInfo(String id, String name, String version, String packageName)
	{
		this.id = id;
		this.name = name;
		this.version = version;
		this.packageName = packageName;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
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

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String toString()
	{
		return "ProcessDefinitionInfo{id=" + this.id + ", name=" + this.name
				+ ", version=" + this.version + "}";
	}


	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		ProcessDefinitionInfo that = (ProcessDefinitionInfo) o;

		if (version != that.version)
		{
			return false;
		}
		if (id != null ? !id.equals(that.id) : that.id != null)
		{
			return false;
		}
		if (name != null ? !name.equals(that.name) : that.name != null)
		{
			return false;
		}
		return true;
	}

	public int hashCode()
	{
		int result;
		result = (id != null ? id.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + ((version != null ? version.hashCode() : 0) >>> 32);
		return result;
	}

}
