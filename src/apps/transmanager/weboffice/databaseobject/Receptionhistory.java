package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

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

/**
 * Receptionhistory entity. 
 * @author 孙爱华
 */
@Entity
@Table(name="receptionhistory")
public class Receptionhistory implements java.io.Serializable {

	// Fields

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_receptionhistory_gen")
	@GenericGenerator(name = "seq_receptionhistory_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_RECEPTIONHISTORY_ID") })
	private Long historyid;		//主键
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Reception reception;	//接待外键
	@Column(length = 50)
	private String province;
	@Column(length = 50)
	private String city;
	@Column(length = 255)
	private String units;
	@Column(length = 100)
	private String leader;
	@Column(length = 100)
	private String jobtype;
	@Column(length = 50)
	private String phone;
	@Column(length = 50)
	private String mobilenum;
	@Column(length = 50)
	private String qq;
	@Column(length = 100)
	private String email;
	@Column(length = 100)
	private String msn;	
	private Integer mans;
	@Column(length = 255)
	private String lunchaddress;
	@Column(length = 255)
	private String stayhotel;
	private Date comedate;
	private Date leavedate;
	private Float staydays;
	@Column(length = 1000)
	private String receiver;
	private Float planmoney;
	private Float realmoney;
	@Column(length = 1000)
	private String comereason;
	@Column(length = 1000)
	private String daycontext;
	@Column(length = 255)
	private String visitspot;
	private Long userid;
	private Long groupid;		//接待所属部门
	private Long rootgroupid;	//接待所属单位
	private Date addtime;
	private Boolean deleted=false;			//是否删除
	private Boolean isdisplay=false;//是否过期

	@Column(length = 1000)
	private String redundanceA;
	@Column(length = 100)
	private String redundanceB;
	private Integer editer;		//编辑者
	private Date editdate;		//编辑时间

	// Constructors

	/** default constructor */
	public Receptionhistory() {
	}

	/** full constructor */
//	public Receptionhistory(Reception reception, String province, String city,
//			String units, String leader, String jobtype, String phone,
//			String mobilenum, String qq, String email, String msn,
//			Integer mans, String lunchaddress, String stayhotel, Date comedate,
//			Date leavedate, Integer staydays, String receiver, Float planmoney,
//			Float realmoney, String comereason, String daycontext,
//			String visitspot, Integer userid, Date addtime, String effect,
//			String redundanceA, String redundanceB, Integer editer, Date editdate) {
//		this.reception = reception;
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
//		this.editer = editer;
//		this.editdate = editdate;
//	}

	// Property accessors

	public Long getHistoryid() {
		return this.historyid;
	}

	public void setHistoryid(Long historyid) {
		this.historyid = historyid;
	}

	public Reception getReception() {
		return this.reception;
	}

	public void setReception(Reception reception) {
		this.reception = reception;
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

	public Integer getEditer() {
		return this.editer;
	}

	public void setEditer(Integer editer) {
		this.editer = editer;
	}

	public Date getEditdate() {
		return this.editdate;
	}

	public void setEditdate(Date editdate) {
		this.editdate = editdate;
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