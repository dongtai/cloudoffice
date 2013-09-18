package apps.transmanager.weboffice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
@Entity
@Table(name="online_tb")
public class OnlinePo implements SerializableAdapter{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5845874757244238861L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_gen")
	@GenericGenerator(name = "seq_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ID") })
	private Long id;
	@Column(length = 100)
	private String name;	
	private Long userId;
	@Column(length = 100)
	private String ip;
	private Date loginTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
 
	
}
