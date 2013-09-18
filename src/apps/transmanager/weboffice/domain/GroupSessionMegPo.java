package apps.transmanager.weboffice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.service.config.WebConfig;

@Entity
@Table(name="groupsessionmeg_tb")
public class GroupSessionMegPo implements SerializableAdapter{
	//public static final String LG_DEFAULT_ICON = "/static/images/personalset2/";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4643860801288294018L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_gen")
	@GenericGenerator(name = "seq_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ID") })
	private Long id;
	private Long groupId;
	//private Boolean readed;
	private Long sendId;
	//private Long acceptId;
	@Lob
	private String sessionMeg;
	private Integer type;
	private Date addDate;
	@Column(length = 255)
	private String url;
	@Column(length = 100)
	private String sendName;
	private Long ownerId;
	@Column(length = 100)
	private String groupName;
	@Column(length = 100)
	private String sendImg;
	
	public String getSendImg() {
		if (sendImg == null || sendImg.trim().length() == 0)
		{
			return WebConfig.userPortrait + "image.jpg";
		}
		if(sendImg.indexOf("/")!=-1)
		{
			return sendImg;
		}else{
			return WebConfig.userPortrait + sendImg;
		}
	}
	public void setSendImg(String sendImg) {
		this.sendImg = sendImg;
	}
	public Long getSendId() {
		return sendId;
	}
	public void setSendId(Long sendId) {
		this.sendId = sendId;
	}
	public String getSessionMeg() {
		return sessionMeg;
	}
	public void setSessionMeg(String sessionMeg) {
		this.sessionMeg = sessionMeg;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSendName() {
		return sendName;
	}
	public void setSendName(String sendName) {
		this.sendName = sendName;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
	
	
}
