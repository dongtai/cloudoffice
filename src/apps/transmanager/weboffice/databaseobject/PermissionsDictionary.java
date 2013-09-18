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
 * 权限字典表，在该表中，定义系统中所有的权限定义。
 * 外界定义的权限也需要进入该字典表中，以使整个系统的
 * 权限类型统一编号
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="permissionsdictionary")
public class PermissionsDictionary implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_permissions_dictionary_gen")
	@GenericGenerator(name = "seq_permissions_dictionary_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_PERMISSIONS_DICTIONARY_ID") })
	private Long id;
	@Column(name = "type_")
	private Integer type;             // 权限类型，该类型在系统中统一编号，具体值见权限类型定义表
	@Column(length = 255)
	private String name;              // 权限名字
	private int positon;              // 权限的位置
	private long setFlag;             // 权限的设置值，该值根据权限矩阵关联表，设置某个权限时候，包含权限自动设置
	private long getFlag;             // 权限判断短路值。根据权限矩阵定义得到各个权限的短路值， 这样，在外界判断是否有某个权限的时候，只需要根据权限的相应短路值进行判断即可，而不需要一步步的判断权限矩阵表。
		
	public PermissionsDictionary()
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

	public Integer getType()
	{
		return type;
	}

	public void setType(Integer type)
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

	public int getPositon()
	{
		return positon;
	}

	public void setPositon(int positon)
	{
		this.positon = positon;
	}

	public long getSetFlag()
	{
		return setFlag;
	}

	public void setSetFlag(long setFlag)
	{
		this.setFlag = setFlag;
	}

	public long getGetFlag()
	{
		return getFlag;
	}

	public void setGetFlag(long flag)
	{
		getFlag = flag;
	}
		
}
