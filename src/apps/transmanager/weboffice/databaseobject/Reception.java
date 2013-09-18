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

import apps.transmanager.weboffice.domain.SerializableAdapter;


/**
 * Reception entity. 
 * @author sunaihua
 */
@Entity
@Table(name="reception")
public class Reception implements SerializableAdapter {

	// Fields
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_reception_gen")
	@GenericGenerator(name = "seq_reception_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_RECEPTION_ID") })
	private Long receptionid;//接待编号
	@Column(length = 50)
	private String province;//省份
	@Column(length = 50)
	private String city;	//城市
	@Column(length = 255)
	private String units;	//单位
	@Column(length = 100)
	private String leader;	//带队领导
	@Column(length = 50)
	private String jobtype;	//职务
	@Column(length = 50)
	private String phone;	//联系电话
	@Column(length = 50)
	private String mobilenum;	//手机号码
	@Column(length = 50)
	private String qq;			//QQ
	@Column(length = 100)
	private String email;		//邮件地址
	@Column(length = 100)
	private String msn;			//MSN
	private Integer mans;		//人数
	@Column(length = 255)
	private String lunchaddress;	//用餐地址
	@Column(length = 255)
	private String stayhotel;		//住宿酒店
	private Date comedate;			//来锡日期
	private Date leavedate;			//离锡时间
	private Float staydays;		//在锡时间
	@Column(length = 100)
	private String receiver;		//接待者
	private Float planmoney=Float.valueOf("0.0");		//计划费用
	private Float realmoney=Float.valueOf("0.0");		//实际费用
	@Column(length = 10000)
	private String comereason;		//来锡事由
	@Column(length = 10000)
	private String daycontext;		//日程安排
	@Column(length = 10000)
	private String visitspot;		//游览景点
	private Long userid;			//添加记录人
	private Long groupid;		//接待所属部门
	private Long rootgroupid;	//接待所属单位
	private Date addtime;			//添加时间
	private Boolean deleted=false;			//是否删除
	private Boolean isdisplay=false;//是否过期
	@Column(length = 100)
	private String redundanceA;		//冗余字段
	@Column(length = 1000)
	private String redundanceB;		//冗余字段
	@Column(length = 50)
	private String comeTime;//到锡时间
	@Column(length = 50)
	private String leaveTime;//离锡时间
	
	@Transient
	private Boolean isedit;			//添加记录人账号
	@Transient
	private Boolean isdelete;			//删除记录的权限
	@Transient
	private Boolean ismanage;			//设置权限
	

	public Boolean getIsmanage()
	{
		return ismanage;
	}

	public void setIsmanage(Boolean ismanage)
	{
		this.ismanage = ismanage;
	}

	public Boolean getIsdelete()
	{
		return isdelete;
	}

	public void setIsdelete(Boolean isdelete)
	{
		this.isdelete = isdelete;
	}

	public Boolean getIsedit() {
		return isedit;
	}

	public void setIsedit(Boolean isedit) {
		this.isedit = isedit;
	}

	public String getComeTime() {
		return comeTime;
	}

	public void setComeTime(String comeTime) {
		this.comeTime = comeTime;
	}

	public String getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(String leaveTime) {
		this.leaveTime = leaveTime;
	}

	/** default constructor */
	public Reception() {
	}

	/** full constructor */
//	public Reception(String province, String city, String units, String leader,
//			String jobtype, String phone, String mobilenum, String qq,
//			String email, String msn, Integer mans, String lunchaddress,
//			String stayhotel, Date comedate, Date leavedate, Integer staydays,
//			String receiver, Float planmoney, Float realmoney,
//			String comereason, String daycontext, String visitspot,
//			Integer userid, Date addtime, String effect, String redundanceA,
//			String redundanceB, Set receptionmanlists, Set receptionhistories) {
//		this.province = province;
//		this.city = city;
//		this.units = units;
//		this.leader = leader;
//		this.jobtype = jobtype;
//		this.phone = phone;
//		this.mobilenum = mobilenum;
//		this.qq = qq;
//		this.email = email;
//		this.msn = msn;
//		this.mans = mans;
//		this.lunchaddress = lunchaddress;
//		this.stayhotel = stayhotel;
//		this.comedate = comedate;
//		this.leavedate = leavedate;
//		this.staydays = staydays;
//		this.receiver = receiver;
//		this.planmoney = planmoney;
//		this.realmoney = realmoney;
//		this.comereason = comereason;
//		this.daycontext = daycontext;
//		this.visitspot = visitspot;
//		this.userid = userid;
//		this.addtime = addtime;
//		this.effect = effect;
//		this.redundanceA = redundanceA;
//		this.redundanceB = redundanceB;
//		this.receptionmanlists = receptionmanlists;
//		this.receptionhistories = receptionhistories;
//	}

	// Property accessors

	public Long getReceptionid() {
		return this.receptionid;
	}

	public void setReceptionid(Long receptionid) {
		this.receptionid = receptionid;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getUnits() {
		return this.units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getLeader() {
		return this.leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public String getJobtype() {
		return this.jobtype;
	}

	public void setJobtype(String jobtype) {
		this.jobtype = jobtype;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobilenum() {
		return this.mobilenum;
	}

	public void setMobilenum(String mobilenum) {
		this.mobilenum = mobilenum;
	}

	public String getQq() {
		return this.qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMsn() {
		return this.msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public Integer getMans() {
		return this.mans;
	}

	public void setMans(Integer mans) {
		this.mans = mans;
	}

	public String getLunchaddress() {
		return this.lunchaddress;
	}

	public void setLunchaddress(String lunchaddress) {
		this.lunchaddress = lunchaddress;
	}

	public String getStayhotel() {
		return this.stayhotel;
	}

	public void setStayhotel(String stayhotel) {
		this.stayhotel = stayhotel;
	}

	public Date getComedate() {
		return this.comedate;
	}

	public void setComedate(Date comedate) {
		this.comedate = comedate;
	}

	public Date getLeavedate() {
		return this.leavedate;
	}

	public void setLeavedate(Date leavedate) {
		this.leavedate = leavedate;
	}

	public Float getStaydays() {
		return this.staydays;
	}

	public void setStaydays(Float staydays) {
		this.staydays = staydays;
	}

	public String getReceiver() {
		return this.receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Float getPlanmoney() {
		return this.planmoney;
	}

	public void setPlanmoney(Float planmoney) {
		this.planmoney = planmoney;
	}

	public Float getRealmoney() {
		return this.realmoney;
	}

	public void setRealmoney(Float realmoney) {
		this.realmoney = realmoney;
	}

	public String getComereason() {
		return this.comereason;
	}

	public void setComereason(String comereason) {
		this.comereason = comereason;
	}

	public String getDaycontext() {
		return this.daycontext;
	}

	public void setDaycontext(String daycontext) {
		this.daycontext = daycontext;
	}

	public String getVisitspot() {
		return this.visitspot;
	}

	public void setVisitspot(String visitspot) {
		this.visitspot = visitspot;
	}

	public Long getUserid() {
		return this.userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Date getAddtime() {
		return this.addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	public Boolean getDeleted()
	{
		return deleted;
	}

	public void setDeleted(Boolean deleted)
	{
		this.deleted = deleted;
	}

	public Boolean getIsdisplay()
	{
		return isdisplay;
	}

	public void setIsdisplay(Boolean isdisplay)
	{
		this.isdisplay = isdisplay;
	}

	public String getRedundanceA() {
		return this.redundanceA;
	}

	public void setRedundanceA(String redundanceA) {
		this.redundanceA = redundanceA;
	}

	public String getRedundanceB() {
		return this.redundanceB;
	}

	public void setRedundanceB(String redundanceB) {
		this.redundanceB = redundanceB;
	}
	public Long getGroupid() {
		return groupid;
	}

	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}

	public Long getRootgroupid() {
		return rootgroupid;
	}

	public void setRootgroupid(Long rootgroupid) {
		this.rootgroupid = rootgroupid;
	}

}