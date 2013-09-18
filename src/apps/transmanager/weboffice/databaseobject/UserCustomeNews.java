package apps.transmanager.weboffice.databaseobject;

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

/**
 * 用户订阅表，用来关联用户和订阅列表
 * @author 孟密密  陈明顺
 *
 */
@Entity
@Table(name="usercustomenews")
public class UserCustomeNews implements SerializableAdapter{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_ucnews_gen")
	@GenericGenerator(name = "seq_ucnews_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_UCNEWS_ID") })
	private Long ucNewsId;
	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users users;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private WebInfo webinfo;
	
	public Long getUcNewsId() {
		return ucNewsId;
	}
	
	public void setUcNewsId(Long ucNewsId) {
		this.ucNewsId = ucNewsId;
	}
	
	public Users getUsers() {
		return users;
	}
	
	public void setUsers(Users users) {
		this.users = users;
	}
	
	public WebInfo getWebinfo() {
		return webinfo;
	}
	
	public void setWebinfo(WebInfo webinfo) {
		this.webinfo = webinfo;
	}
}
