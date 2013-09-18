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
 * 所订阅的新闻内容列表
 * @author 孟密密  陈明顺
 *
 */
@Entity
@Table(name="newsinfo")
public class NewsInfo implements SerializableAdapter{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_newsinfo_gen")
	@GenericGenerator(name = "seq_newsinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_NEWSINFO_ID") })
	private Long newsId;
	
	private String newsUrl;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private WebInfo webinfo;
	
	@Column(length = 500)
	private String title;
	
	private Date date;
	
	private String source;
	
	private String content;
	
	@Column(length = 1000)
	private String abstractContent;
	
	private String picPath;

	public Long getNewsId() {
		return newsId;
	}

	public void setNewsId(long newsId) {
		this.newsId = newsId;
	}

	public String getNewsUrl() {
		return newsUrl;
	}

	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}

	public WebInfo getWebinfo() {
		return webinfo;
	}

	public void setWebinfo(WebInfo webinfo) {
		this.webinfo = webinfo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAbstractContent() {
		return abstractContent;
	}

	public void setAbstractContent(String abstractContent) {
		this.abstractContent = abstractContent;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	
}
