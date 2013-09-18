package apps.transmanager.weboffice.databaseobject.bug;

import java.util.Date;
import java.util.List;

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

import apps.transmanager.weboffice.domain.SerializableAdapter;


/**
 * BugInfos BUG处理信息表
 * @author 孙爱华
 * 2013.6.14
 */
@Entity
@Table(name="bugmodifyinfos")
public class BugModifyInfos implements SerializableAdapter {
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_bugmodifyinfos_gen")
	@GenericGenerator(name = "seq_bugmodifyinfos_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_BUGMODIFYINFOS_ID") })
	private Long id;//编号
	private Long modifier;//处理人
	private Date modifydate;//处理时间
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BugInfos bugInfos;//BUG编号
	private Integer buglevel=1;//BUG优先级，1，立解，2、必解，3、要解，4，缓解，5，不解
	private Long solveuser;//解决人
	private Date solvedate;//解决时间
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BugActions bugActions;//处理动作，1，未处理，2、接受，3、分配（转人），4、解决，5、测试通过，6、测试不通过，7、附带问题，8、重复BUG，9、不是BUG，10产品定义
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BugStates bugStates;//解决情况（状态），1，未处理，2、在处理，3、延期处理，4、测试不通过，5、已解，6、关闭
	private Long ownerid;//当前负责人
	private String rebugs;//重复BUG号
	private String testuser;//测试用户
	private Date testdate;//测试时间
	private Integer testresult=0;//测试结果0未测试，1、测试通过，2、测试不通过
	@Column(length = 2000)
	private String modifyscript;	//处理备注

	@Transient
	private String modifyname;//处理人名
	@Transient
	private String ownername;//处理人名

	@Transient
	private List<BugFiles> bugFiles;//BUG附件内容，冗余

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getModifier() {
		return modifier;
	}

	public void setModifier(Long modifier) {
		this.modifier = modifier;
	}

	public Date getModifydate() {
		return modifydate;
	}

	public void setModifydate(Date modifydate) {
		this.modifydate = modifydate;
	}

	public BugInfos getBugInfos() {
		return bugInfos;
	}

	public void setBugInfos(BugInfos bugInfos) {
		this.bugInfos = bugInfos;
	}

	public Integer getBuglevel() {
		return buglevel;
	}

	public void setBuglevel(Integer buglevel) {
		this.buglevel = buglevel;
	}

	public Long getSolveuser() {
		return solveuser;
	}

	public void setSolveuser(Long solveuser) {
		this.solveuser = solveuser;
	}

	public Date getSolvedate() {
		return solvedate;
	}

	public void setSolvedate(Date solvedate) {
		this.solvedate = solvedate;
	}

	public BugActions getBugActions() {
		return bugActions;
	}

	public void setBugActions(BugActions bugActions) {
		this.bugActions = bugActions;
	}

	public BugStates getBugStates() {
		return bugStates;
	}

	public void setBugStates(BugStates bugStates) {
		this.bugStates = bugStates;
	}

	public String getTestuser() {
		return testuser;
	}

	public void setTestuser(String testuser) {
		this.testuser = testuser;
	}

	public Date getTestdate() {
		return testdate;
	}

	public void setTestdate(Date testdate) {
		this.testdate = testdate;
	}

	public Integer getTestresult() {
		return testresult;
	}

	public void setTestresult(Integer testresult) {
		this.testresult = testresult;
	}

	public String getModifyscript() {
		return modifyscript;
	}

	public void setModifyscript(String modifyscript) {
		this.modifyscript = modifyscript;
	}

	public List<BugFiles> getBugFiles() {
		return bugFiles;
	}

	public void setBugFiles(List<BugFiles> bugFiles) {
		this.bugFiles = bugFiles;
	}
	public String getModifyname() {
		return modifyname;
	}

	public void setModifyname(String modifyname) {
		this.modifyname = modifyname;
	}
	public String getRebugs() {
		return rebugs;
	}

	public void setRebugs(String rebugs) {
		this.rebugs = rebugs;
	}
	public Long getOwnerid() {
		return ownerid;
	}

	public void setOwnerid(Long ownerid) {
		this.ownerid = ownerid;
	}

	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}

}