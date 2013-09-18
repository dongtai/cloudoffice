package apps.transmanager.weboffice.databaseobject;

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

import apps.transmanager.weboffice.domain.IParentKey;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 组织表，该表表示组织结构中的组织。
 * 组织中可以嵌套存在。在组织表中有字段parentkey,该自动主要是为快的
 * 查询父组织及子组织而定义的，通过该字段查询，可以避免在查询父组织或
 * 子组织的时候进行递归的查询处理，该字段的具体定义见下面的字段说明。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="organizations")
public class Organizations implements SerializableAdapter, IParentKey
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_organizations_gen")
	@GenericGenerator(name = "seq_organizations_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ORGAINZATIONS_ID") })
	private Long id;
	@ManyToOne
    @OnDelete(action=OnDeleteAction.CASCADE) 
	private Organizations parent;
	//@Column(unique=true)
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
	private String spaceUID;           // 组织空间的根目录名：建立组织时候的名字+系统时间，即是name_systemtime，以后组织修改name后，该名字始终不变。
	@ManyToOne
	@OnDelete(action=OnDeleteAction.NO_ACTION) 
	private Users manager;             // 组织负责人。这里不做级联删除，因为负责人不在，但组织可能需要任然存在。
	@Transient
	private Spaces space;
	@Column(length = 100)
	private String image;              // 组织头像的名字
	@Column(length = 1000)
	private String organizecode;		//组织结构树形编码
	@Column(length = 1000)
	private String sortcode;//显示组织结构用的

	@Column(length = 1000)
	private String depid;//部门编号（同步用的）
	@Column(length = 1000)
	private String parentid;//父部门编号(同步用的)
	@Column(length = 1000)
	private String updatetime;//同步时间（）
	@Column(length = 1000)
	private String depname;//部门名称
	@Column(length = 5000)
	private String memberlist;//部门成员
	
	public String getDepname() {
		return depname;
	}

	public void setDepname(String depname) {
		this.depname = depname;
	}

	public String getMemberlist() {
		return memberlist;
	}

	public void setMemberlist(String memberlist) {
		this.memberlist = memberlist;
	}

	public void setSmsMaxNums(Integer smsMaxNums) {
		this.smsMaxNums = smsMaxNums;
	}

	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company;
	
	/**
	 * 0:不限制
	 * 1:每年
	 * 2:每月
	 * 3:每日
	 */
	private Integer smsLimit;
	
	/**
	 * 每年或者每月或者每天最大发送多少条短信
	 */
	private Integer smsMaxNums;
	
	public Integer getSmsLimit() {
		if(null == smsLimit){
			return this.getParent() !=null?this.getParent().getSmsLimit() : 0;
		}
		return smsLimit;
	}

	public void setSmsLimit(Integer smsLimit) {
		this.smsLimit = smsLimit;
	}

	public Integer getSmsMaxNums() {
		if(null == smsMaxNums){
			return this.getParent() !=null?this.getParent().getSmsMaxNums() : 0;
		}
		return smsMaxNums;
	}

	public void setSmsMaxNums(int smsMaxNums) {
		this.smsMaxNums = smsMaxNums;
	}

	public String getSortcode()
	{
		return sortcode;
	}

	public void setSortcode(String sortcode)
	{
		this.sortcode = sortcode;
	}

	public String getDepid()
	{
		return depid;
	}

	public void setDepid(String depid)
	{
		this.depid = depid;
	}

	public String getParentid()
	{
		return parentid;
	}

	public void setParentid(String parentid)
	{
		this.parentid = parentid;
	}

	public String getUpdatetime()
	{
		return updatetime;
	}

	public void setUpdatetime(String updatetime)
	{
		this.updatetime = updatetime;
	}

	public Organizations()
	{	
	}
	public Organizations(Long id, String name)
	{
		this.name = name;
		this.id=id;
	}
	public Organizations(String name, String desc)
	{
		this.name = name;
		this.description = desc;
	}
	
	public Organizations(Organizations org, Spaces sp)
	{
		id = org.id;
		name = org.name;
		description = org.description;
		parent = org.parent;
		sortNum = org.sortNum;
		webaddress=org.webaddress;
		issub=org.issub;
		spaceUID = org.spaceUID;
		manager = org.manager;
		space = sp;
		organizecode=org.organizecode;
	}
	
	public Organizations(Organizations org, Spaces sp, int memSize)
	{
		id = org.id;
		name = org.name;
		description = org.description;
		parent = org.parent;
		sortNum = org.sortNum;
		webaddress=org.webaddress;
		issub=org.issub;
		spaceUID = org.spaceUID;
		manager = org.manager;
		space = sp;
		memberSize = memSize;
		organizecode=org.organizecode;
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
	public String getOrganizecode() {
		return organizecode;
	}

	public void setOrganizecode(String organizecode) {
		this.organizecode = organizecode;
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
	public void update(Organizations n)
	{
		name = n.name;
		description = n.description;
		sortNum = n.sortNum;
		organizecode=n.organizecode;
		sortcode=n.sortcode;
		image = n.image;
		webaddress=n.webaddress;
		issub=n.issub;
	}
	
}
