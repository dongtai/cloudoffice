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
 * 默认设置用户的权限表，当添加接待管理时默认将这些用户赋权限
 * @author sunaihua
 */
@Entity
@Table(name="receptiondefaultusers")
public class ReceptionDefaultUsers implements SerializableAdapter 
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_receptiondefaultusers_gen")
	@GenericGenerator(name = "seq_receptiondefaultusers_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_RECEPTIONDEFAULTUSERS_ID") })
	private Long id;//编号
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users owner;//自己的
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users defaultuser;//默认授权的人
	private Integer defaultpowerid=1;//1查看权限，2添加，3编辑，4删除，10为管理员
	private Boolean ismobileinfo;//是否发手机短信提醒
	private Date addtime=new Date();//添加时间
	
	public Boolean getIsmobileinfo()
	{
		return ismobileinfo;
	}
	public void setIsmobileinfo(Boolean ismobileinfo)
	{
		this.ismobileinfo = ismobileinfo;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Users getOwner() {
		return owner;
	}
	public void setOwner(Users owner) {
		this.owner = owner;
	}
	public Users getDefaultuser() {
		return defaultuser;
	}
	public void setDefaultuser(Users defaultuser) {
		this.defaultuser = defaultuser;
	}
	public Integer getDefaultpowerid() {
		return defaultpowerid;
	}
	public void setDefaultpowerid(Integer defaultpowerid) {
		this.defaultpowerid = defaultpowerid;
	}
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
}