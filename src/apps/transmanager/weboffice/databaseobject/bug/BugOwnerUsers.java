package apps.transmanager.weboffice.databaseobject.bug;


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

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * BUG处理人列表
 * @author 孙爱华
 * 2013.6.18
 *
 */
@Entity
@Table(name="bugownerusers")
public class BugOwnerUsers implements SerializableAdapter{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_bugownerusers_gen")
	@GenericGenerator(name = "seq_bugownerusers_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_BUGOWNERUSERS_ID") })
	private Long id;//编号
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users owners;//对应的BUG处理人
	private Long rootid;//所属根组织，可为空
	private Long companyid;//所属单位，可为空
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Users getOwners() {
		return owners;
	}
	public void setOwners(Users owners) {
		this.owners = owners;
	}
	public Long getRootid() {
		return rootid;
	}
	public void setRootid(Long rootid) {
		this.rootid = rootid;
	}
	public Long getCompanyid() {
		return companyid;
	}
	public void setCompanyid(Long companyid) {
		this.companyid = companyid;
	}
}
