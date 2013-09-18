package apps.transmanager.weboffice.databaseobject;


import java.util.Date;

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
 * 接待管理用户权限表
 * @author sunaihua
 */
@Entity
@Table(name="receptionusers")
public class ReceptionUsers implements SerializableAdapter {

	// Fields
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_receptionusers_gen")
	@GenericGenerator(name = "seq_receptionusers_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_RECEPTIONUSERS_ID") })
	private Long id;//编号
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Reception reception;//接待信息
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;//可以查看的人员
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users owner;//接待拥有者
	private Integer powerid=0;//1查看权限，2编辑，3删除
	private Date addtime=new Date();//添加时间
	private Integer seenums=0;//查看次数
	private Date lastseedate;//最后查看时间
	
	public Users getOwner()
	{
		return owner;
	}
	public void setOwner(Users owner)
	{
		this.owner = owner;
	}
	public Integer getSeenums()
	{
		return seenums;
	}
	public void setSeenums(Integer seenums)
	{
		this.seenums = seenums;
	}
	public Date getLastseedate()
	{
		return lastseedate;
	}
	public void setLastseedate(Date lastseedate)
	{
		this.lastseedate = lastseedate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Reception getReception() {
		return reception;
	}
	public void setReception(Reception reception) {
		this.reception = reception;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public Integer getPowerid() {
		return powerid;
	}
	public void setPowerid(Integer powerid) {
		this.powerid = powerid;
	}
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	
}