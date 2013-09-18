package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 订阅列表
 * @author 孟密密  陈明顺
 *
 */
@Entity
@Table(name="webinfo")
public class WebInfo implements SerializableAdapter
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_webinfo_gen")
	@GenericGenerator(name = "seq_webinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_WEBINFO_ID") })
	private Long gid;
	@Column(length = 500)
	private String webname;
	@Column(length = 500)
	private String url;
	@Column(length = 255)
	private String category;
	
	private boolean isrss;
	private String newslist;
	private String picpath;
	private String mainarea;
	private String timearea;
	private String titlearea;
	private String contentarea;
	private Long num;

	public WebInfo(){
		
	}
	
	public Long getGid() {
		return gid;
	}

	public void setGid(Long gid) {
		this.gid = gid;
	}

	public String getWebname() {
		return webname;
	}

	public void setWebname(String webname) {
		this.webname = webname;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isIsrss() {
		return isrss;
	}

	public void setIsrss(boolean isrss) {
		this.isrss = isrss;
	}

	public String getNewslist() {
		return newslist;
	}

	public void setNewslist(String newslist) {
		this.newslist = newslist;
	}

	public String getPicpath() {
		return picpath;
	}

	public void setPicpath(String picpath) {
		this.picpath = picpath;
	}

	public String getMainarea() {
		return mainarea;
	}

	public void setMainarea(String mainarea) {
		this.mainarea = mainarea;
	}

	public String getTimearea() {
		return timearea;
	}

	public void setTimearea(String timearea) {
		this.timearea = timearea;
	}

	public String getTitlearea() {
		return titlearea;
	}

	public void setTitlearea(String titlearea) {
		this.titlearea = titlearea;
	}

	public String getContentarea() {
		return contentarea;
	}

	public void setContentarea(String contentarea) {
		this.contentarea = contentarea;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}



}
