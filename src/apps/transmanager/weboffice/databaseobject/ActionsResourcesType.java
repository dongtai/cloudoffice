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
 * 权限类型及操作动作类型统一定义，及资源统一定义表。
 * 该表为字段表。提供系统中统一定义的action类型和resource类型。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="actionsresourcestype")
public class ActionsResourcesType implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_actionsresourcestype_gen")
	@GenericGenerator(name = "seq_actionsresourcestype_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ACTIONSRESOURCESTYPE_ID") })
	private Long id;
	@Column(name = "type_")
	private int type;                 // 记录类型，0为action记录，1为resource记录
	@Column(length = 255)
	private String name;              // 类型名字
	@Column(name = "value_")
	private int value;                // 类型的值
	
	
	public ActionsResourcesType()
	{		
	}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}
		
}
