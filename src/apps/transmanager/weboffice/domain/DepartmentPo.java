package apps.transmanager.weboffice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Spaces;
import apps.transmanager.weboffice.databaseobject.Users;

@Entity
@Table(name="organizations")
public class DepartmentPo implements SerializableAdapter, IParentKey{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_organizations_gen")
	@GenericGenerator(name = "seq_organizations_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ORGAINZATIONS_ID") })
	private Long id;
	@ManyToOne
    @OnDelete(action=OnDeleteAction.CASCADE) 
	private Organizations parent;
	@Column(length = 100)
	private String name;
	@Column(length = 1000)
	private String description;
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
	@Transient
	private int memberSize;
	private Integer sortNum=10000;//组织结构排序
	@Column(length = 255)
	private String webaddress;//组织的网站地址
	private Integer issub;//是否有子组织
	@Column(length = 255)
	private String organizecode;		//组织结构树形编码

	@Column(length = 255)
	private String spaceUID;           // 组织空间的根目录名：建立组织时候的名字+系统时间，即是name_systemtime，以后组织修改name后，该名字始终不变。
	@ManyToOne
	@OnDelete(action=OnDeleteAction.NO_ACTION) 
	private Users manager;             // 组织负责人。这里不做级联删除，因为负责人不在，但组织可能需要任然存在。
	@Transient
	private Spaces space;
	@Column(length = 100)
	private String image;              // 部门头像的名字
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company;
	
	public DepartmentPo()
	{	
	}
	
	public DepartmentPo(DepartmentPo org, Spaces sp)
	{
		id = org.id;
		name = org.name;
		description = org.description;
		parent = org.parent;
		sortNum = org.sortNum;
		spaceUID = org.spaceUID;
		manager = org.manager;
		space = sp;
	}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Long getParentID()
	{
		return parent != null ? parent.getId() : 0;
	}	
	public Organizations getParent()
	{
		return parent;
	}
	public void setParent(Organizations parent)
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
	public int getMemberSize()
	{
		return memberSize;
	}
	public void setMemberSize(int memberSize)
	{
		this.memberSize = memberSize;
	}
	public Integer getSortNum()
	{
		return sortNum;
	}
	public void setSortNum(Integer sortNum)
	{
		this.sortNum = sortNum;
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
	

	public Spaces getSpace()
	{
		return space;
	}

	public void setSpace(Spaces space)
	{
		this.space = space;
	}

	public String getImage1()
	{
		return image;
	}
	
	// 后续删除该方法，由于用户的头像位置可以由用户系统配置决定，所有外界在使用的
	// 时候，需要获得系统的相应配置位置。
	@Deprecated 
	public String getImage()
	{
		return image != null ? image : "image.jpg";
	}

	public void setImage(String image)
	{
		this.image = image;
	}
	
	public String getWebaddress()
	{
		return webaddress;
	}

	public void setWebaddress(String webaddress)
	{
		this.webaddress = webaddress;
	}

	public Integer getIssub()
	{
		return issub;
	}

	public void setIssub(Integer issub)
	{
		this.issub = issub;
	}

	public String getOrganizecode()
	{
		return organizecode;
	}

	public void setOrganizecode(String organizecode)
	{
		this.organizecode = organizecode;
	}

	public Company getCompany()
	{
		return company;
	}

	public void setCompany(Company company)
	{
		this.company = company;
	}

	/**
	 * 更新部门信息
	 * @param n
	 */
	public void update(DepartmentPo n)
	{
		name = n.name;
		description = n.description;
		sortNum = n.sortNum;
		image = n.image;		
	}
	
}
