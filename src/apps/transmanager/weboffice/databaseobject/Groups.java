package apps.transmanager.weboffice.databaseobject;

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

import apps.transmanager.weboffice.domain.IParentKey;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 组定义表。
 * 该组同组织结构类似。每个组拥有独立的空间。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="groups")
public class Groups implements SerializableAdapter, IParentKey
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_groups_gen")
	@GenericGenerator(name = "seq_groups_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_GROUPS_ID") })
	private Long id;
	@ManyToOne
    @OnDelete(action=OnDeleteAction.CASCADE) 
	private Groups parent;
	//@Column(unique=true)
	@Column(length = 100)
	private String name;	
	/* 为了快速获得组的子组或父组，增加该字段。该字段的编码方式为从父组中得到同样的字段的值
	 * 加上父组id值加上“-”字符。
	 * 如果该组为没有父组，则该字段为null。
	 * 如果该组的父组的该字段为null,而父组的id为2，则该组的该字段值为“2-”。
	 * 如果该组的父组的该字段为“1-”,而父组的id为3，则该组的该字段值为“1-3-”。
	 * 如果如为该组下的子组，则该子组的该字段值按上述规则处理。
	 * 
	 */
	@Column(length = 255)
	private String parentKey;
	@Column(length = 1000)
	private String description;
	@Column(length = 255)
	private String spaceUID;           // 组空间的根目录名：建立组时候的名字+系统时间，即是name_systemtime，以后组修改name后，该名字始终不变。
	@ManyToOne
	@OnDelete(action=OnDeleteAction.NO_ACTION) 
	private Users manager;             // 组负责人。这里不做级联删除，因为负责人不在，但组可能需要任然存在。
	@Column(length = 255)
	private String image;              // 组头像的名字
	
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	
	public Groups getParent()
	{
		return parent;
	}
	
	public void setParent(Groups parent)
	{
		this.parent = parent;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getParentKey()
	{
		return parentKey;
	}
	public void setParentKey(String parentKey)
	{
		this.parentKey = parentKey;
	}
	public String getSpaceUID()
	{
		return spaceUID;
	}	
	public void setSpaceUID(String sp)
	{
		spaceUID = sp;
	}
	public Users getManager()
	{
		return manager;
	}
	public void setManager(Users manager)
	{
		this.manager = manager;
	}
	
	public String getImage1()
	{
		return image;
	}
	
	//后续删除该方法，由于用户的头像位置可以由用户系统配置决定，所有外界在使用的
	// 时候，需要获得系统的相应配置位置。
	@Deprecated 
	public String getImage()
	{
		return (image != null ? image : "image.jpg");
	}
	
	public void setImage(String image)
	{
		this.image = image;
	}
	public void update(Groups n)
	{
		name = n.name;
		description = n.description;
		image = n.image;
	}
}
