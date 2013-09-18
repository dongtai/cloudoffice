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
@Table(name="calendarempower")
public class CalendarEmpower implements SerializableAdapter{
	
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_CalendarEmpower_gen")
	@GenericGenerator(name = "seq_CalendarEmpower_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CALENDAR_EMPOWER_ID") })
	private Long  id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo;
	
	private Long  empoweredUserId;
	
	public Long getEmpoweredUserId() {
		return empoweredUserId;
	}

	public void setEmpoweredUserId(Long empoweredUserId) {
		this.empoweredUserId = empoweredUserId;
	}

	public String getEmpoweredUserName() {
		return empoweredUserName;
	}

	public void setEmpoweredUserName(String empoweredUserName) {
		this.empoweredUserName = empoweredUserName;
	}

	public String getEmpoweredRealName() {
		return empoweredRealName;
	}

	public void setEmpoweredRealName(String empoweredRealName) {
		this.empoweredRealName = empoweredRealName;
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

	@Column(length = 100)
	String empoweredUserName;
	
	@Column(length = 100)
	String empoweredRealName;

	
}
