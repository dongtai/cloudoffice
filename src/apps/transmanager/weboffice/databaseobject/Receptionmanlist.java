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

import apps.transmanager.weboffice.domain.SerializableAdapter;


/**
 * Receptionmanlist entity. 
 * @author sunaihua
 */
@Entity
@Table(name="receptionmanlist")
public class Receptionmanlist implements SerializableAdapter {

	// Fields

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_Receptionmanlist_gen")
	@GenericGenerator(name = "seq_Receptionmanlist_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_RECEPTIONMANLIST_ID") })
	private Long manid;		//主键
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Reception reception;	//接待主键
	@Column(length = 100)
	private String manname;			//来访人名
	@Column(length = 255)
	private String unitname;		//来访人单位

	@Column(length = 100)
	private String manjob;			//来访人职务
	@Column(length = 50)
	private String manphone;		//来访人联系电话
	@Column(length = 50)
	private String manmobile;		//来访人手机号
	@Column(length = 50)
	private String manqq;			//来访人QQ
	@Column(length = 100)
	private String manemail;		//来访人邮箱
	@Column(length = 100)
	private String manmsn;			//来访人MSN
	private Byte isleader;		//来访人是否领导
	@Column(length = 1000)
	private String redundanceA;		//来访人冗余字段
	@Column(length = 255)
	private String redundanceB;		//来访人冗余字段

	// Constructors

	/** default constructor */
	public Receptionmanlist() {
	}

	/** minimal constructor */
	public Receptionmanlist(Byte isleader) {
		this.isleader = isleader;
	}

	/** full constructor */
//	public Receptionmanlist(Reception reception, String manname, String manjob,
//			String manphone, String manmobile, String manqq, String manemail,
//			String manmsn, Byte isleader, String redundanceA, String redundanceB) {
//		this.reception = reception;
//		this.manname = manname;
//		this.manjob = manjob;
//		this.manphone = manphone;
//		this.manmobile = manmobile;
//		this.manqq = manqq;
//		this.manemail = manemail;
//		this.manmsn = manmsn;
//		this.isleader = isleader;
//		this.redundanceA = redundanceA;
//		this.redundanceB = redundanceB;
//	}

	// Property accessors

	public Long getManid() {
		return this.manid;
	}

	public void setManid(Long manid) {
		this.manid = manid;
	}

	public Reception getReception() {
		return this.reception;
	}

	public void setReception(Reception reception) {
		this.reception = reception;
	}

	public String getManname() {
		return this.manname;
	}

	public void setManname(String manname) {
		this.manname = manname;
	}

	public String getManjob() {
		return this.manjob;
	}

	public void setManjob(String manjob) {
		this.manjob = manjob;
	}

	public String getManphone() {
		return this.manphone;
	}

	public void setManphone(String manphone) {
		this.manphone = manphone;
	}

	public String getManmobile() {
		return this.manmobile;
	}

	public void setManmobile(String manmobile) {
		this.manmobile = manmobile;
	}

	public String getManqq() {
		return this.manqq;
	}

	public void setManqq(String manqq) {
		this.manqq = manqq;
	}

	public String getManemail() {
		return this.manemail;
	}

	public void setManemail(String manemail) {
		this.manemail = manemail;
	}

	public String getManmsn() {
		return this.manmsn;
	}

	public void setManmsn(String manmsn) {
		this.manmsn = manmsn;
	}

	public Byte getIsleader() {
		return this.isleader;
	}

	public void setIsleader(Byte isleader) {
		this.isleader = isleader;
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
	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

}