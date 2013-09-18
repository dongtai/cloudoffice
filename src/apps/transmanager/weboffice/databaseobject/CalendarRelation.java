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

@Entity
@Table(name="calendarrelation")
public class CalendarRelation implements SerializableAdapter{
	
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_calendarRelation_gen")
	@GenericGenerator(name = "seq_calendarRelation_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CALENDAR_RELATION_ID") })
	private Long  id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo;
	
	private Long  relativedUserId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Users getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(Users userinfo) {
		this.userinfo = userinfo;
	}

	public Long getRelativedUserId() {
		return relativedUserId;
	}

	public void setRelativedUserId(Long relativedUserId) {
		this.relativedUserId = relativedUserId;
	}

	public String getRelativedUserName() {
		return relativedUserName;
	}

	public void setRelativedUserName(String relativedUserName) {
		this.relativedUserName = relativedUserName;
	}

	@Column(length = 100)
	String relativedUserName;
	
	@Column(length = 100)
	String relativedRealName;

	public String getRelativedRealName() {
		return relativedRealName;
	}

	public void setRelativedRealName(String relativedRealName) {
		this.relativedRealName = relativedRealName;
	}
	
}
