package templates.objectdb;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;


public class CollectionBean implements Serializable{

	private Long id;

	private Template templateid;
	
	private int thitCount;
	
	@Column(length = 100)
	private String  cUser;
	
	private Date date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Template getTemplateid() {
		return templateid;
	}

	public void setTemplateid(Template templateid) {
		this.templateid = templateid;
	}

	public int getThitCount() {
		return thitCount;
	}

	public void setThitCount(int thitCount) {
		this.thitCount = thitCount;
	}

	public String getCUser() {
		return cUser;
	}

	public void setCUser(String cUser) {
		this.cUser = cUser;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
