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
@Table(name="calendarfocused")
public class CalendarFocused implements SerializableAdapter{
	
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_calendarFocused_gen")
	@GenericGenerator(name = "seq_calendarFocused_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CALENDAR_FOCUSED_ID") })
	private Long  id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo;
	
	private Long  focusedUserId;
	
	public Long getFocusedUserId() {
		return focusedUserId;
	}

	public void setFocusedUserId(Long focusedUserId) {
		this.focusedUserId = focusedUserId;
	}

	public String getFocusedUserName() {
		return focusedUserName;
	}

	public void setFocusedUserName(String focusedUserName) {
		this.focusedUserName = focusedUserName;
	}

	public String getFocusedRealName() {
		return focusedRealName;
	}

	public void setFocusedRealName(String focusedRealName) {
		this.focusedRealName = focusedRealName;
	}

	@Column(length = 100)
	String focusedUserName;
	
	@Column(length = 100)
	String focusedRealName;
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


	
}
