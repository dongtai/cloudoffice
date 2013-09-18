package apps.transmanager.weboffice.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.databaseobject.Users;

@Entity
@Table(name="mblog_favor")
public class MyFavoritePo implements SerializableAdapter{
	private static final long serialVersionUID = 4958170934291465394L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_gen")
	@GenericGenerator(name = "seq_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ID") })
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Users getFavorUser() {
		return favorUser;
	}

	public void setFavorUser(Users favorUser) {
		this.favorUser = favorUser;
	}

	public BlogContentPo getFavorblog() {
		return favorblog;
	}

	public void setFavorblog(BlogContentPo favorblog) {
		this.favorblog = favorblog;
	}

	@OneToOne
	@JoinColumn(name="user_id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users favorUser;
	
	@OneToOne
	@JoinColumn(name="favor_blogid")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BlogContentPo favorblog;
	
	public Date getFavor_time() {
		return favor_time;
	}

	public void setFavor_time(Date favor_time) {
		this.favor_time = favor_time;
	}

	private Date favor_time;
}
