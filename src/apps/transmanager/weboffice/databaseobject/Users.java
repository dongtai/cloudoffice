package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.constants.both.MainConstants;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 文件注释
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="users")
public class Users implements SerializableAdapter
{
//	private String hostIP;
	//public static final String LG_DEFAULT_ICON = "/static/images/personalset2/";
	private static final long serialVersionUID = -7644114512714619751L;

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_users_gen")
	@GenericGenerator(name = "seq_users_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_USERS_ID") })
	private Long id;
	@Transient
	private String token = "";
	//@Transient
	//private String key;
	@Column(unique=true, length = 100)
	private String userName;              // 用户登录名
	@Column(length = 100)
	private String passW;
	@Column(length = 100)
	private String realEmail;
	@Column(length = 100)
	private String realName;            // 用户显示名
	@Lob
    @Basic(fetch=FetchType.LAZY)
	private byte[] portrait;           // 用户头像	
	@Column(length = 50)
	private String duty;              // 输入的职务
	@ManyToOne()
	private JobTitles positon;        // 字典表中定义的职务
	@Column(length = 100)
	private String image="image.jpg";              // 用户头像的保存缓存名
	private Integer myoption;
	private Float storageSize=2048f;
	private Short role=0;                  // 将删除
	private Integer lastsignlevel;//最后签批的权限，一般为董事长或市委书记

	@Column(length = 10)
	private String companyId = "public";           // 将删除
	private Short validate = 1;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company;

	// 以下需要移动到单独的表中
	@Column(length = 100)
	private String mobile; // 手机，多个手机用空格分开
	@Column(length = 100)
	private String companyName; // 公司
	@Column(length = 100)
	private String fax; // 传真，多个传真用空格分开
	@Column(length = 100)
	private String phone; // 座机，多个座机用空格分开
	@Column(length = 100)
	private String postcode; // 邮编
	@Column(length = 255)
	private String address; // 通讯地址
	@Column(length = 255)
	private String companyAddress;  // 公司地址
	private boolean man = true;     // 性别，true为男，false为女。
	private Date birthday;   // 生日.
	@Column(length = 1000)
	private String description;     // 介绍说明。

	@Column(length = 100)
	private String uruid; // 指纹验证
	private Integer sortNum = 1000000;//用户序号
	
	private Integer pageviews;//列表显示的条数

	@Column(length = 255)
	private String loginCA;// CA认证登录
	@Column(length = 100)
	private String caId;
	
	private Boolean calendarPublic = false;// 日程是否公开，true 公开，false 私人
	
	private Date pwdupdatetime;//更改密码时间，用来提醒用户定时更改密码
	private Date logintime;//最后一次登录时间
	private Integer searchDepRight;//查询本单位所有人文件名的权限
	@Column(length = 255)
	private String depid;//部门号，同步用的
	@Column(length = 100)
	private String rolename;//角色名称，同步用的
	private Integer partadmin;//部门管理员  空或1为超级管理员，2为部门管理员，与role配合使用
	private Integer islead=0;//是否领导
	
	//新增内容
	private Integer weibo_num = 0;//微博数
	private Integer fans_num = 0;//粉丝数
	private Integer follow_num = 0;//关注的数目

	private Integer priread = 0;//未读的私信
	private Integer newfan = 0;//新增的粉丝
	private Integer newmention = 0;//新被@的次数
	private Integer deletetag=0;//删除标记，1为删除，如果删除后在username后加上“_delete”,注册时username中不能有delete关键字

	private Integer isactive = 0;//是否激活，1为未激活，暂没用以后再说

	@Transient
	private Long[] roles;//用户所属的角色，传递参数用的
	@Transient
	private Long[] orgs;//用户所属的部门，传递参数用的
	
	//信息是否保密
	private Boolean infodef;
	
	private String outcode;//同步的用户主键
	private String outname;//用户登录名
	private String outrealname;//外部用户名称
	private String outpassW;//外部同步过来的密码
	private String outemail;//外部邮箱
	private String outrole;//是否部门管理员
	private String outorgcode;//所属组织编号;多个组织用;间隔
	private String updatetime;//上次同步更新的时间
	private String outca;//ca号
	private String outsize;//空间大小
	private String outsex;//性别
    //是否是单位发文人员
    private Boolean  isFawen;
    private String username1;
	private String username2;
	
    public Boolean getIsFawen() {
		return isFawen;
	}
	public void setIsFawen(Boolean isFawen) {
		this.isFawen = isFawen;
	}
	public String getUsername1() {
		return username1;
	}
	public void setUsername1(String username1) {
		this.username1 = username1;
	}
	public String getUsername2() {
		return username2;
	}
	public void setUsername2(String username2) {
		this.username2 = username2;
	}
	public void setFawen(Boolean isFawen) {
        this.isFawen = isFawen;
    }
    public Boolean getFawen() {
        if(isFawen==null){
            return false;
        }
        return isFawen;
    }

	public String getOutcode() {
		return outcode;
	}

	public void setOutcode(String outcode) {
		this.outcode = outcode;
	}

	public String getOutname() {
		return outname;
	}

	public void setOutname(String outname) {
		this.outname = outname;
	}

	public String getOutrealname() {
		return outrealname;
	}

	public void setOutrealname(String outrealname) {
		this.outrealname = outrealname;
	}

	public String getOutpassW() {
		return outpassW;
	}

	public void setOutpassW(String outpassW) {
		this.outpassW = outpassW;
	}

	public String getOutemail() {
		return outemail;
	}

	public void setOutemail(String outemail) {
		this.outemail = outemail;
	}

	public String getOutrole() {
		return outrole;
	}

	public void setOutrole(String outrole) {
		this.outrole = outrole;
	}

	public String getOutorgcode() {
		return outorgcode;
	}

	public void setOutorgcode(String outorgcode) {
		this.outorgcode = outorgcode;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getOutca() {
		return outca;
	}

	public void setOutca(String outca) {
		this.outca = outca;
	}

	public String getOutsize() {
		return outsize;
	}

	public void setOutsize(String outsize) {
		this.outsize = outsize;
	}

	public String getOutsex() {
		return outsex;
	}

	public void setOutsex(String outsex) {
		this.outsex = outsex;
	}

	public Boolean getInfodef() {
		if(infodef == null || infodef.toString().toLowerCase().equals("null")){
			return false;
		}
		return infodef;
	}

	public void setInfodef(Boolean infodef) {
		this.infodef = infodef;
	}

	public Integer getIsactive() {
		return isactive;
	}

	public void setIsactive(Integer isactive) {
		this.isactive = isactive;
	}
	public Integer getDeletetag() {
		return deletetag;
	}

	public void setDeletetag(Integer deletetag) {
		this.deletetag = deletetag;
	}

	public Long[] getRoles() {
		return roles;
	}

	public void setRoles(Long[] roles) {
		this.roles = roles;
	}

	public Long[] getOrgs() {
		return orgs;
	}

	public void setOrgs(Long[] orgs) {
		this.orgs = orgs;
	}

	public Integer getWeibo_num() {
		return weibo_num;
	}

	public void setWeibo_num(Integer weibo_num) {
		this.weibo_num = weibo_num;
	}

	public Integer getFans_num() {
		return fans_num;
	}

	public void setFans_num(Integer fans_num) {
		this.fans_num = fans_num;
	}

	public Integer getFollow_num() {
		return follow_num;
	}

	public void setFollow_num(Integer follow_num) {
		this.follow_num = follow_num;
	}

	public Integer getPriread() {
		return priread;
	}

	public void setPriread(Integer priread) {
		this.priread = priread;
	}

	public Integer getNewfan() {
		return newfan;
	}

	public void setNewfan(Integer newfan) {
		this.newfan = newfan;
	}

	public Integer getNewmention() {
		return newmention;
	}

	public void setNewmention(Integer newmention) {
		this.newmention = newmention;
	}
	@Transient
	private String realPass = "";
	
	// 该用户允许的登录类别，如果该值为0表示由系统直接登录，如果非0表示该用户不允许从系统中的表登录，其登录需要从
	// 整合系统的认证系统登录如LDAP，SSO等，本系统此时记录该用户信息仅仅是为缓存而已。
	// @Column(columnDefinition="int(10) default " + MainConstant.SYSTEM_LOGIN)
	private Integer loginType = MainConstants.SYSTEM_LOGIN;
	@Column(length = 255)
	private String spaceUID;           // 用户空间的根目录名：建立用户时候的名字+系统时间，即是name_systemtime，以后用户修改name后，该名字始终不变。
	@Transient
	private String resetPass;          // 新增用户或修改用户信息时候，如果重置用户密码，则设置该值。
	
	@Column(name="privateKey",length=1000) 
	private String privateKey;
	
	@Column(name="publicKey",length=1000) 
	private String publicKey;
	
	@OneToOne(mappedBy="users",cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.EAGER)
	private UsersSynch sysnch;
	
	public UsersSynch getSysnch() {
		return sysnch;
	}

	public void setSysnch(UsersSynch sysnch) {
		this.sysnch = sysnch;
	}

	public Users()
	{
	}
	
	public Users(String userName, String passW, 
			String realEmail, String companyID, String role, String realName)
	{
		this.userName = userName;
		this.passW = passW;
		//this.email = email;
		this.realEmail = realEmail;
		this.companyId = companyID;
		this.role = Short.valueOf(role);
		this.realName = realName;
	}
	
	public Users(String userName, String passW, 
			String realEmail, int role, String realName)
	{
		this.userName = userName;
		this.passW = passW;
		//this.email = email;
		this.realEmail = realEmail;
		this.companyId = "public";   // 将删除
		this.role = (short)role;
		this.realName = realName;
	}

    public Long getId()
    {
        return id;
    }


	public void setId(Long id)
	{
		this.id = id;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}


	public String getRealPass()
	{
		return realPass;
	}

	public void setRealPass(String realPass)
	{
		this.realPass = realPass;
	}

	public Integer getIslead()
	{
		return islead;
	}

	public void setIslead(Integer islead)
	{
		this.islead = islead;
	}

	public Integer getPartadmin()
	{
		return partadmin;
	}

	public void setPartadmin(Integer partadmin)
	{
		this.partadmin = partadmin;
	}

	public String getDepid()
	{
		return depid;
	}

	public void setDepid(String depid)
	{
		this.depid = depid;
	}

	public String getRolename()
	{
		return rolename;
	}

	public void setRolename(String rolename)
	{
		this.rolename = rolename;
	}

	public Boolean getCalendarPublic() {
		return calendarPublic;
	}

	public void setCalendarPublic(Boolean calendarPublic) {
		this.calendarPublic = calendarPublic;
	}
	/*public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}*/

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassW()
	{
		return passW;
	}

	public byte[] getPortrait()
	{
		return portrait;
	}

	public void setPortrait(byte[] portrait)
	{
		this.portrait = portrait;
	}

	public void setPassW(String passW)
	{
		this.passW = passW;
	}

	/*public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}*/

	public JobTitles getPositon()
	{
		return positon;
	}

	public void setPositon(JobTitles positon)
	{
		this.positon = positon;
	}

	public String getRealEmail()
	{
		return realEmail;
	}

	public void setRealEmail(String realEmail)
	{
		this.realEmail = realEmail;
	}

	public String getRealName()
	{
		return realName;
	}

	public void setRealName(String realName)
	{
		this.realName = realName;
	}

	/*public String getDepartment()
	{
		return department;
	}

	public void setDepartment(String department)
	{
		this.department = department;
	}*/

	public String getDuty()
	{
		return duty;
	}

	public void setDuty(String duty)
	{
		this.duty = duty;
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
		if (image == null || image.trim().length() == 0 || image.toLowerCase().equals("null"))
		{
			return "image.jpg";
		}
		return image;
	}

	public void setImage(String image)
	{
	    /*if(image.contains("/")|| image.contains("\\"))
        {
            System.out.println("头像"+image);
            Thread.dumpStack();
        }*/
		this.image = image;
	}

	public Integer getMyoption()
	{
		return myoption;
	}

	public void setMyoption(Integer myoption)
	{
		this.myoption = myoption;
	}

	public Float getStorageSize()
	{
		return storageSize;
	}

	public void setStorageSize(Float storageSize)
	{
		this.storageSize = storageSize;
	}

	public Short getRole()
	{
		return role;
	}

	public void setRole(Short role)
	{
		this.role = role;
	}

	public String getCompanyId()
	{
		return companyId;
	}

	public void setCompanyId(String companyId)
	{
		this.companyId = companyId;
	}

	public Short getValidate()
	{
		return validate;
	}

	public void setValidate(Short validate)
	{
		this.validate = validate;
	}

	public String getMobile()
	{
		return mobile;
	}

	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}

	public String getCompanyName()
	{
		return companyName;
	}

	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	public String getFax()
	{
		return fax;
	}

	public void setFax(String fax)
	{
		this.fax = fax;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getPostcode()
	{
		return postcode;
	}

	public void setPostcode(String postcode)
	{
		this.postcode = postcode;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getUruid()
	{
		return uruid;
	}

	public void setUruid(String uruid)
	{
		this.uruid = uruid;
	}

	public Integer getSortNum()
	{
		return sortNum;
	}

	public void setSortNum(Integer sortNum)
	{
		this.sortNum = sortNum;
	}

	public String getLoginCA()
	{
		return loginCA;
	}

	public void setLoginCA(String loginCA)
	{
		this.loginCA = loginCA;
	}

	public String getCaId()
	{
		return caId;
	}

	public void setCaId(String caId)
	{
		this.caId = caId;
	}

	public Integer getLoginType()
	{
		return loginType;
	}

	public void setLoginType(Integer loginType)
	{
		this.loginType = loginType;
	}
	@Deprecated
	public String getEmail()
	{
		return spaceUID;
	}
	public String getSpaceUID()
	{
		return spaceUID;
	}	
	public void setSpaceUID(String sp)
	{
		spaceUID = sp;
	}	
	
	public String getResetPass()
	{
		return resetPass;
	}

	public void setResetPass(String resetPass)
	{
		this.resetPass = resetPass;
	}

	public String getCompanyAddress()
	{
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress)
	{
		this.companyAddress = companyAddress;
	}

	public boolean getMan()
	{
		return man;
	}

	public void setMan(boolean man)
	{
		this.man = man;
	}

	public Date getBirthday()
	{
		return birthday;
	}

	public void setBirthday(Date birthday)
	{
		this.birthday = birthday;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String desc)
	{
		this.description = desc;
	}
	
	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public Date getPwdupdatetime()
	{
		return pwdupdatetime;
	}

	public void setPwdupdatetime(Date pwdupdatetime)
	{
		this.pwdupdatetime = pwdupdatetime;
	}

	public Date getLogintime()
	{
		return logintime;
	}

	public void setLogintime(Date logintime)
	{
		this.logintime = logintime;
	}

	public Integer getSearchDepRight()
	{
		return searchDepRight;
	}

	public void setSearchDepRight(Integer searchDepRight)
	{
		this.searchDepRight = searchDepRight;
	}
	
	public Company getCompany()
	{
		return company;
	}

	public void setCompany(Company company)
	{
		this.company = company;
	}
	
	public Integer getLastsignlevel() {
		if (lastsignlevel==null)
		{
			lastsignlevel=0;
		}
		return lastsignlevel;
	}

	public void setLastsignlevel(Integer lastsignlevel) {
		this.lastsignlevel = lastsignlevel;
	}
	public Integer getPageviews() {
		if (pageviews==null || pageviews.intValue()<16)
		{
			pageviews=16;
		}
		return pageviews;
	}

	public void setPageviews(Integer pageviews) {
		this.pageviews = pageviews;
	}
	/**
	 * 修改用户信息
	 * @param u
	 */
	public void update(Users u)
	{
		userName = u.userName;		
		realEmail = u.realEmail;
		realName = u.realName;
		duty = u.duty;
		image = u.image;
		myoption = u.myoption;
		storageSize = u.storageSize;
		validate = u.validate;
		mobile = u.mobile;
		companyName = u.companyName;
		fax = u.fax;
		phone = u.phone;
		postcode = u.postcode;
		address = u.address;
		uruid = u.uruid;
		sortNum = u.sortNum;
        loginCA = u.loginCA;
        isFawen = u.isFawen;
		caId = u.caId;		
		companyAddress = u.companyAddress;
		man = u.man;
		birthday = u.birthday;
		description = u.description;
		pwdupdatetime=u.pwdupdatetime;
		logintime=u.logintime;
		searchDepRight=u.searchDepRight;
		weibo_num =u.weibo_num;
		fans_num = u.fans_num;
		follow_num = u.follow_num;

		priread = u.priread;
		newfan = u.newfan;
		newmention = u.newmention;
		realPass = u.realPass;
		if (u.getRole()!=null)
		{
			role=u.getRole();
		}
		partadmin=u.getPartadmin();
		
	}

//	public void setHostIP(String hostIP) {
//		this.hostIP = hostIP;
//	}
//
//	public String getHostIP() {
//		return hostIP;
//	}
	
	
}
