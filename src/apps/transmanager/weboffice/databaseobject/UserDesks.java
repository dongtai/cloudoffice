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
 * 用户首页桌面显示内容存储表，要注意用户操作时也要更新此表，如移动文件、重命名、删除
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="userdesks")
public class UserDesks implements SerializableAdapter
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_userdesks_gen")
	@GenericGenerator(name = "seq_userdesks_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_USERDESKS_ID") })
	private Long id;//主键
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;           //使用桌面的用户 
	private String displayname;//显示名称
	private Integer sourcetype;//类型，0是文件夹，1是文件，2 其他,-1为固有的
	private Boolean isshare=false;//是否共享
	@Column(length = 500)
	private String paths;        // 文件库路径
	private String hashtag="my";//跳转到哪里
	private Date adddate=new Date();//添加日期
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public Integer getSourcetype() {
		return sourcetype;
	}
	public void setSourcetype(Integer sourcetype) {
		this.sourcetype = sourcetype;
	}
	public Boolean getIsshare() {
		return isshare;
	}
	public void setIsshare(Boolean isshare) {
		this.isshare = isshare;
	}
	public String getPaths() {
		return paths;
	}
	public void setPaths(String paths) {
		this.paths = paths;
	}
	public Date getAdddate() {
		return adddate;
	}
	public void setAdddate(Date adddate) {
		this.adddate = adddate;
	}
	public String getHashtag() {
		return hashtag;
	}
	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}
}
