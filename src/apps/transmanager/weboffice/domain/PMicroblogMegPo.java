package apps.transmanager.weboffice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.Users;

/**
 * 微博信息PO类
 * @author 彭俊杰
 *
 */
@Entity
@Table(name="pmicroblogmeg_tb")
public class PMicroblogMegPo implements SerializableAdapter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3958170934291465394L;

	/**
	 * 主键ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_gen")
	@GenericGenerator(name = "seq_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ID") })
	private Long id;
	
	@Column(length = 1000)
	private String meg;
	
	private Date addDate;

	@OneToOne
	@JoinColumn(name="user_id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users sendUser;
	

	@OneToOne
	@JoinColumn(name="group_id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Groups groups;
	
	@ManyToOne
	@JoinColumn(name="parent_id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private PMicroblogMegPo parent;
	
	private Integer backCount;
	
	
	@Transient
	public Integer getBackCount() {
		return backCount;
	}

	public void setBackCount(Integer backCount) {
		this.backCount = backCount;
	}

	@ManyToOne
	@JoinColumn(name="parent_id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	public PMicroblogMegPo getParent() {
		return parent;
	}

	public void setParent(PMicroblogMegPo parent) {
		this.parent = parent;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Users getSendUser() {
		return sendUser;
	}

	public void setSendUser(Users sendUser) {
		this.sendUser = sendUser;
	}

	public Groups getGroups() {
		return groups;
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}
	
	public String getMeg() {
		return meg;
	}

	public void setMeg(String meg) {
		this.meg = meg;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	
}
