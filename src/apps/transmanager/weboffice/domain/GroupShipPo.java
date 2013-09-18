package apps.transmanager.weboffice.domain;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.databaseobject.Users;

@Entity
@Table(name="mblog_groupShip")
public class GroupShipPo implements SerializableAdapter{
	private static final long serialVersionUID = 5958170934291465394L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_groups_gen")
	@GenericGenerator(name = "seq_groups_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_GROUPS_ID") })
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MicroGroupPo getGroup() {
		return group;
	}

	public void setGroup(MicroGroupPo group) {
		this.group = group;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	@OneToOne
	@JoinColumn(name="group_id")
	@OnDelete(action=OnDeleteAction.CASCADE) 
	private MicroGroupPo group;
	
	@OneToOne
	@JoinColumn(name="user_id")
	@OnDelete(action=OnDeleteAction.CASCADE) 
	private Users user;
}
