package apps.transmanager.weboffice.databaseobject;

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
 * 文件实体元数据
 * 
 * @author wch
 * 
 * @date 2013-7-23
 *
 * @location com.evermore.weboffice.databaseobject
 *
 */
@Entity
@Table(name="metadata")
public class EntityMetadata implements SerializableAdapter
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_metadata_gen")
	@GenericGenerator(name = "seq_metadata_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_METADATA_ID") })
	private Long id;
	@Column(length=10000)
	private String filePath;                //文件路径
	@Column(length=100)
	private String code;					//元数据编号
	@Column(length=1000)
	private String metadataName;            //元数据名称，一个元数据编号对应一个元数据名称
	@Column(length=10000)
	private String metadataValue;           //元数据值
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getFilePath()
	{
		return filePath;
	}
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	public String getCode()
	{
		return code;
	}
	public void setCode(String code)
	{
		this.code = code;
	}
	public String getMetadataName()
	{
		return metadataName;
	}
	public void setMetadataName(String metadataName)
	{
		this.metadataName = metadataName;
	}
	public String getMetadataValue()
	{
		return metadataValue;
	}
	public void setMetadataValue(String metadataValue)
	{
		this.metadataValue = metadataValue;
	}

}
