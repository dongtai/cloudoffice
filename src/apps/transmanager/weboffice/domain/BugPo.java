package apps.transmanager.weboffice.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.util.DateUtils;

@Entity
@Table(name="bug_tbl")
public class BugPo implements SerializableAdapter{

	private static final long serialVersionUID = -3404728189850704603L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_bug_gen")
	@GenericGenerator(name = "seq_bug_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_BUG_ID") })
	private Long id;	//真实ID
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 优先级别
	 */
	private String level;
	
	/**
	 * 所属模块
	 */
	private String moduler;
	
	/**
	 * 问题描述
	 */
	private String describ;
	
	/**
	 * 创建时间
	 */
	private Date createDate;
	
	/**
	 * 创建者ID
	 */
	private Long creatorId;
	
	/**
	 * 创建者的名称
	 */
	private String creator;
	

	/**
	 * bug 的当前状态
	 * 状态 
	 *    0:新建状态
	 *    1:正在处理
	 *    2：处理完毕，等待测试
	 *    2:测试完毕,等待部署
	 *    3:已经解决，并关闭
	 *    4:无法重现，已经关闭
	 *    5:关闭
	 */
	private String state;
	
	
	/**
	 * 最后的处理人
	 */
	private Long lastProcessorId;
	
	
	@Transient
	private String createDateText;

	public String getCreateDateText() {
		if(createDate == null ){
			return "";
		}
		try {
			return DateUtils.ftmDateToString("yyyy-MM-dd hh:mm:ss", createDate);
		} catch (Exception e) {
			return "";
		}
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}


	public String getModuler() {
		return moduler;
	}


	public void setModuler(String moduler) {
		this.moduler = moduler;
	}


	public String getDescrib() {
		return describ;
	}


	public void setDescrib(String describ) {
		this.describ = describ;
	}


	public Date getCreateDate() {
		if(createDate == null){
			return new Date();
		}
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public Long getCreatorId() {
		return creatorId;
	}


	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public Long getLastProcessorId() {
		return lastProcessorId;
	}


	public void setLastProcessorId(Long lastProcessorId) {
		this.lastProcessorId = lastProcessorId;
	}
	
	
	public String getCreator() {
		return creator;
	}


	public void setCreator(String creator) {
		this.creator = creator;
	}
}
