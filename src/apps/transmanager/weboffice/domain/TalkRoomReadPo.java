package apps.transmanager.weboffice.domain;

import javax.persistence.Column;

public class TalkRoomReadPo implements SerializableAdapter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4643860801288294018L;
	private Integer id;
	private Integer roomId;
	private Boolean isRead;
	private SessionMegPo sessionMeg;
	private Integer userId;
	@Column(length = 100)
	private String roomName;
	
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public SessionMegPo getSessionMeg() {
		return sessionMeg;
	}
	public void setSessionMeg(SessionMegPo sessionMeg) {
		this.sessionMeg = sessionMeg;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRoomId() {
		return roomId;
	}
	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	
	
}
