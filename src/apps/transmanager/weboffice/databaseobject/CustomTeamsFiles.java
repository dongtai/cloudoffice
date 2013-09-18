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
 * 用户自定义分组的文件具体用户查阅情况，这是被动记录的，用户不知道
 * 用户未阅读数（新增数）=整个自定义组所有文件-记录数
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 政府版
 * @see
 * @since 政府版
 */

@Entity
@Table(name="customteamsfiles")
public class CustomTeamsFiles implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_custom_teams_gen")
	@GenericGenerator(name = "seq_custom_teams_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CUSTOM_TEAMS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;           // 具体操作者。
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private CustomTeams customTeams;//快速共享信息
	private String paths;//文档具体路径
	private Integer detaildo=0;//具体操作,0为阅读，1为编辑保存
	private Date dotime=new Date();//具体操作时间
	@Column(name = "comment",length = 5000)
	private String comment;//备注
	private Date commentdate;//最后一次填写备注的时间

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
	public CustomTeams getCustomTeams() {
		return customTeams;
	}
	public void setCustomTeams(CustomTeams customTeams) {
		this.customTeams = customTeams;
	}
	public Integer getDetaildo() {
		return detaildo;
	}
	public void setDetaildo(Integer detaildo) {
		this.detaildo = detaildo;
	}
	
	public Date getDotime() {
		return dotime;
	}
	public void setDotime(Date dotime) {
		this.dotime = dotime;
	}
	public String getPaths() {
		return paths;
	}
	public void setPaths(String paths) {
		this.paths = paths;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getCommentdate() {
		return commentdate;
	}
	public void setCommentdate(Date commentdate) {
		this.commentdate = commentdate;
	}
}
