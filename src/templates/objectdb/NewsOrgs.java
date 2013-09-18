package templates.objectdb;


import java.io.Serializable;

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

import apps.transmanager.weboffice.databaseobject.Organizations;

/**
 * 新闻发布的范围,如果readtype为0或null表示选择的部门，否则表示全体
 * @author sah
 *
 */
@Entity 
@Table(name="newsorgs")
public class NewsOrgs implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_newsorgs_gen")
    @GenericGenerator(name = "seq_newsorgs_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_NEWSORGS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private News news;//对应的新闻编号
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Organizations orgs;//发布到哪些部门（或单位【政府私云】）
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public News getNews() {
		return news;
	}
	public void setNews(News news) {
		this.news = news;
	}
	public Organizations getOrgs() {
		return orgs;
	}
	public void setOrgs(Organizations orgs) {
		this.orgs = orgs;
	}
	
	
	
}
