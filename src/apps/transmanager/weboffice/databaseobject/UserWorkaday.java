package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

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
@Table(name="userworkaday")
public class UserWorkaday implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_userWorkaday_gen")
	@GenericGenerator(name = "seq_userWorkaday_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_USER_WORKADAY_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo;
	@Column(length = 100)
	private String title;
	@Column(length = 1000)
	private String contentAm;
	@Column(length = 1000)
	private String contentPm;
	private Date date;

	public UserWorkaday()
	{
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Users getUserinfo()
	{
		return userinfo;
	}

	public void setUserinfo(Users userinfo)
	{
		this.userinfo = userinfo;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContentAm()
	{
		return contentAm;
	}

	public void setContentAm(String content)
	{
		this.contentAm = content;
	}

	public String getContentPm()
	{
		return contentPm;
	}

	public void setContentPm(String content)
	{
		this.contentPm = content;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

}
