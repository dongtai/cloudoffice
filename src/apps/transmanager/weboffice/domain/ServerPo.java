package apps.transmanager.weboffice.domain;

import javax.persistence.Column;

public class ServerPo implements SerializableAdapter{

	private static final long serialVersionUID = 8566085659870060813L;
	private Long serverID;
	@Column(length = 100)
    private String serverName;
	@Column(length = 100)
    private String serverLogon;
	@Column(length = 100)
    private String serverPsw;
    private Long typeID;
    @Column(length = 100)
    private String email;
    @Column(length = 100)
    private String telephone;
    @Column(length = 100)
    private String effect;
    private Integer  cityID;
    private Long groupID;
    private Integer sortNum;
	public Long getServerID() {
		return serverID;
	}
	public void setServerID(Long serverID) {
		this.serverID = serverID;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerLogon() {
		return serverLogon;
	}
	public void setServerLogon(String serverLogon) {
		this.serverLogon = serverLogon;
	}
	public String getServerPsw() {
		return serverPsw;
	}
	public void setServerPsw(String serverPsw) {
		this.serverPsw = serverPsw;
	}
	public Long getTypeID() {
		return typeID;
	}
	public void setTypeID(Long typeID) {
		this.typeID = typeID;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getEffect() {
		return effect;
	}
	public void setEffect(String effect) {
		this.effect = effect;
	}
	public Integer getCityID() {
		return cityID;
	}
	public void setCityID(Integer cityID) {
		this.cityID = cityID;
	}
	public Long getGroupID() {
		return groupID;
	}
	public void setGroupID(Long groupID) {
		this.groupID = groupID;
	}
	public Integer getSortNum() {
		return sortNum;
	}
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	} 
    
    
	
}
