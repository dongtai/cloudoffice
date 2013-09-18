package apps.transmanager.weboffice.domain;
import java.util.Date;

import javax.persistence.Column;
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
@Table(name="mblog_weibo")
public class BlogContentPo implements SerializableAdapter{
	private static final long serialVersionUID = 4958170934291465394L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_gen")
	@GenericGenerator(name = "seq_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ID") })
	private Long id;
	
	@OneToOne
	@JoinColumn(name="user_id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users sendUser;
	
	public Users getSendUser() {
		return sendUser;
	}
	public void setSendUser(Users sendUser) {
		this.sendUser = sendUser;
	}
	public MicroGroupPo getGroups() {
		return groups;
	}
	public void setGroups(MicroGroupPo groups) {
		this.groups = groups;
	}
	@OneToOne
	@JoinColumn(name="group_id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private MicroGroupPo groups;
	
	@Column(columnDefinition="Text") 
	private String blog_body;
	
	private Date post_time;
	private int filetype;//1:为普通微博，2：回复的微博，3：转发的微博
	
	@OneToOne
	@JoinColumn(name="zf_blogid")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BlogContentPo zfblog;
	
	@OneToOne
	@JoinColumn(name="reply_blogid")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BlogContentPo replyblog;
	
	public BlogContentPo getReplyblog() {
		return replyblog;
	}
	public void setReplyblog(BlogContentPo replyblog) {
		this.replyblog = replyblog;
	}
	private int zf_times;//'转发的次数'
	private int reply_times;// '回复的次数'
	private boolean isgroup=false;//'是否为微群消息（false 不是，true 是）
    
	private boolean delete_flag = false;//true为删除,false为未删除
	public boolean isDelete_flag() {
		return delete_flag;
	}
	public void setDelete_flag(boolean delete_flag) {
		this.delete_flag = delete_flag;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getBlog_body() {
		return blog_body;
	}
	public void setBlog_body(String blog_body) {
		this.blog_body = blog_body;
	}
	public Date getPost_time() {
		return post_time;
	}
	public void setPost_time(Date postTime) {
		post_time = postTime;
	}
	public int getFiletype() {
		return filetype;
	}
	public void setFiletype(int filetype) {
		this.filetype = filetype;
	}

	public BlogContentPo getZfblog() {
		return zfblog;
	}
	public void setZfblog(BlogContentPo zfblog) {
		this.zfblog = zfblog;
	}
	public int getZf_times() {
		return zf_times;
	}
	public void setZf_times(int zf_times) {
		this.zf_times = zf_times;
	}
	public int getReply_times() {
		return reply_times;
	}
	public void setReply_times(int reply_times) {
		this.reply_times = reply_times;
	}
	public boolean isIsgroup() {
		return isgroup;
	}
	public void setIsgroup(boolean isgroup) {
		this.isgroup = isgroup;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
}
