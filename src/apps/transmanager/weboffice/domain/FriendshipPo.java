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
@Table(name="mblog_friendship")
public class FriendshipPo implements SerializableAdapter{

	private static final long serialVersionUID = 3958170834291465394L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_groups_gen")
	@GenericGenerator(name = "seq_groups_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_GROUPS_ID") })
	private Long id;
	
	@OneToOne
	@JoinColumn(name="friend_follow_userid")
	@OnDelete(action=OnDeleteAction.CASCADE) 
	private Users follow_user;//关注
	
	@OneToOne
	@JoinColumn(name="friend_fan_userid")
	@OnDelete(action=OnDeleteAction.CASCADE) 
	private Users fan_user;//粉丝

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Users getFollow_user() {
		return follow_user;
	}

	public void setFollow_user(Users follow_user) {
		this.follow_user = follow_user;
	}

	public Users getFan_user() {
		return fan_user;
	}

	public void setFan_user(Users fan_user) {
		this.fan_user = fan_user;
	}

}
