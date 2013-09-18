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

/**
 * 公告表。
 * 记录空间中的各种公告。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="bulletins")
public class Bulletins implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_bulletins_gen")
	@GenericGenerator(name = "seq_bulletins_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_BULLETINS_ID") })
	private Long id;
	@Column(length = 255)
	private String title;              // 公告标题
	@Column(length = 3000)
	private String content;            // 公告内容
	private Date date;                 // 公告时间
	@ManyToOne
    @OnDelete(action=OnDeleteAction.CASCADE) 
	private Spaces space;              // 拥有该公告的space
	
	public Bulletins()
	{		
	}
	
	public Bulletins(String title, String content)
	{
		this.title = title; 
		this.content = content;
		date = new Date();
	}	

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public Spaces getSpace()
	{
		return space;
	}

	public void setSpace(Spaces space)
	{
		this.space = space;
	}

	public void update(Bulletins n)
	{
		title = n.title;
		content = n.content;
	}
	

}
