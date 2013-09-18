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
@Table(name = "sessionmeg_tb")
public class SessionMegPo implements SerializableAdapter {
	// public static final String LG_DEFAULT_ICON =  "/static/images/personalset2/";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5027486691189569258L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sessionMeg_gen")
	@GenericGenerator(name = "seq_sessionMeg_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SESSION_MEG_ID") })
	private Long id;
	private Long sendId;
	private Long acceptId;
	@Lob
	private String sessionMeg;
	private Integer type;
	private Date addDate;
	private Boolean readed;
	@Column(length = 100)
	private String url;
	@Column(length = 100)
	private String sendName;
	@Column(length = 100)
	private String sendImg;
	
	/**
	 * 发送人是否删除
	 */
	private Boolean senderDelele;
	
	
	/**
	 * 接收人是否删除
	 */
	private Boolean accepterDelete;
	
	public Boolean getSenderDelele() {
		return senderDelele;
	}
	public void setSenderDelele(Boolean senderDelele) {
		this.senderDelele = senderDelele;
	}
	public Boolean getAccepterDelete() {
		return accepterDelete;
	}
	public void setAccepterDelete(Boolean accepterDelete) {
		this.accepterDelete = accepterDelete;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSendId() {
		return sendId;
	}

	public void setSendId(Long sendId) {
		this.sendId = sendId;
	}

	public Long getAcceptId() {
		return acceptId;
	}

	public void setAcceptId(Long acceptId) {
		this.acceptId = acceptId;
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

	public Boolean getReaded() {
		return readed;
	}

	public void setReaded(Boolean readed) {
		this.readed = readed;
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

	public String getSendImg() {
		if (sendImg == null || sendImg.trim().length() == 0) {
			return WebConfig.userPortrait + "image.jpg";
		}
		if (sendImg.indexOf("/") != -1) {
			return sendImg;
		} else {
			return WebConfig.userPortrait + sendImg;
		}
	}

	public void setSendImg(String sendImg) {
		this.sendImg = sendImg;
	}
}
