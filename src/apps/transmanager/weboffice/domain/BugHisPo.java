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
@Table(name="bug_his_tbl")
public class BugHisPo implements SerializableAdapter{

	private static final long serialVersionUID = -3404728189850704603L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_bug_his_gen")
	@GenericGenerator(name = "seq_bug_his_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_BUG_HIS_ID") })
	private Long id;	//真实ID
	
	
	/**
	 * 
	 */
	private Long bugId;
	
	/**
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
	 * 处理者的主键
	 */
	private Long processorId;
	
	/**
	 * 处理备注
	 */
	private String comment;
	
	/**
	 * 创建者的名称
	 */
	private String creator;
	
	/**
	 * 创建时间
	 */
	private Date createDate;
	
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

	public Long getBugId() {
		return bugId;
	}

	public void setBugId(Long bugId) {
		this.bugId = bugId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getProcessorId() {
		return processorId;
	}

	public void setProcessorId(Long processorId) {
		this.processorId = processorId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
}
