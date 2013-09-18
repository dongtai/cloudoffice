package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

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
@Table(name="calendarevent")
public class CalendarEvent implements SerializableAdapter{
	
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_calendarEvent_gen")
	@GenericGenerator(name = "seq_calendarEvent_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CALENDAR_EVENT_ID") })
	private Long  id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo;//日程用户
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users poweruserinfo;//为您安排日程的用户
    
    public Users getPoweruserinfo() {
		return poweruserinfo;
	}

	public void setPoweruserinfo(Users poweruserinfo) {
		this.poweruserinfo = poweruserinfo;
	}

	private Long calendarId;
    
    @Column(length = 255)
    private String title;
    
    private Date startDate;
    
	private Date endDate;
	@Column(length = 1000)
    private String rRule;
    @Column(length = 10000)
    private String location;
    @Column(length = 10000)
    private String notes;
    @Column(length = 1000)
    private String url;
    
    private Boolean isAllDay;
    @Column(length = 1000)
    private String reminder;
    @Column(length = 10000)
    private String testString;
    @Column(length = 10000)
    private String testString1;
    
    public String getTestString1() {
		return testString1;
	}

	public void setTestString1(String testString1) {
		this.testString1 = testString1;
	}

	private Boolean isInvite;
    
    private Boolean isPrivate;//true:不公开；false:公开
    
    public Boolean getIsInvite() {
		return isInvite;
	}

	public void setIsInvite(Boolean isInvite) {
		this.isInvite = isInvite;
	}

	private Boolean isRead = false;
    

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}



	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getTestString() {
		return testString;
	}

	public void setTestString(String testString) {
		this.testString = testString;
	}

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

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getrRule() {
		return rRule;
	}

	public void setrRule(String rRule) {
		this.rRule = rRule;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public Boolean getIsAllDay() {
		return isAllDay;
	}

	public void setIsAllDay(Boolean isAllDay) {
		this.isAllDay = isAllDay;
	}

	public String getReminder() {
		return reminder;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}

}
