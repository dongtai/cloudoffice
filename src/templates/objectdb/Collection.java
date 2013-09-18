package templates.objectdb;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity 
@Table(name="yozo_collection")
public class Collection implements Serializable{

	@Id
	@GeneratedValue
	private Long id;

	/**模板*/
	@OneToOne
	private Template templateid;
	
	/**次数*/
	private int thitCount;
	
	/**收藏人*/
	@Column(length = 100)
	private String  cUser;
	
	/**日期*/
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
