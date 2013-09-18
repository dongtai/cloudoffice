package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;


/** 接待信息权限表
 * Receptionpower entity. 
 * @author sunaihua
 */
@Entity
@Table(name="receptionpower")
public class Receptionpower  implements SerializableAdapter {

	// Fields
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_receptionpower_gen")
	@GenericGenerator(name = "seq_receptionpower_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_RECEPTIONPOWER_ID") })
	private Long id;		//权限主键
	private Long rpuserid;		//权限用户
	private Long groupid;		//权限组别
	private Integer powernum=1;	//权限号，1查询，2编辑3删除   临时用位来表示权限
	private Integer typeid=0;		//类别，默认为0，0为用户，1为组织
	private Long ownerid;//谁填写的接待信息
	private Reception reception;//对应的接待信息
	private int seenums=0;//查看次数
	private Date seedate;//最后查看时间
	


	/** default constructor */
	public Receptionpower() {
	}

	/** full constructor */
	public Receptionpower(Long rpuserid, Long groupid, Integer powernum,
			Integer typeid) {
		this.rpuserid = rpuserid;
		this.groupid = groupid;
		this.powernum = powernum;
		this.typeid = typeid;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRpuserid() {
		return this.rpuserid;
	}

	public void setRpuserid(Long rpuserid) {
		this.rpuserid = rpuserid;
	}

	public Long getGroupid() {
		return this.groupid;
	}

	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}

	public Integer getPowernum() {
		return this.powernum;
	}

	public void setPowernum(Integer powernum) {
		this.powernum = powernum;
	}

	public Integer getTypeid() {
		return this.typeid;
	}

	public void setTypeid(Integer typeid) {
		this.typeid = typeid;
	}
	public Long getOwnerid()
	{
		return ownerid;
	}

	public void setOwnerid(Long ownerid)
	{
		this.ownerid = ownerid;
	}

	public Reception getReception()
	{
		return reception;
	}
	public void setReception(Reception reception)
	{
		this.reception = reception;
	}
	public int getSeenums()
	{
		return seenums;
	}
	public void setSeenums(int seenums)
	{
		this.seenums = seenums;
	}
	public Date getSeedate()
	{
		return seedate;
	}
	public void setSeedate(Date seedate)
	{
		this.seedate = seedate;
	}
}