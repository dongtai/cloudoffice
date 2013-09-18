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
@Table(name="calendarinviteer")
public class CalendarInviteer implements SerializableAdapter{
	
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_calendarInviteer_gen")
	@GenericGenerator(name = "seq_calendarInviteer_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CALENDAR_INVITEER_ID") })
	private Long  id;	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private CalendarEvent calendarEvent;
    /**
     * 被邀请人的Id
     */
    private Long userId;
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public CalendarEvent getCalendarEvent() {
		return calendarEvent;
	}
	public void setCalendarEvent(CalendarEvent calendarEvent) {
		this.calendarEvent = calendarEvent;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
     * 被邀请人的用户名
     */
	@Column(length = 100)
    private String userName;
	@Column(length = 100)
    private String realName;
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
    
	private Boolean isRead = false;
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
}
