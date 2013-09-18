package apps.transmanager.weboffice.databaseobject.bug;

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
 * BUG状态表
 * @author 孙爱华
 * 2013.6.18
 *
 */
@Entity
@Table(name="bugstates")
public class BugStates implements SerializableAdapter{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_bugstates_gen")
	@GenericGenerator(name = "seq_bugstates_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_BUGSTATES_ID") })
	private Long id;//编号
	@Column(length = 255)
	private String statename;//状态名称
	private Integer isdelete=0;//是否删除标记，1为删除
	
	public BugStates()
	{
		
	}
	public BugStates(Long id)
	{
		this.id=id;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStatename() {
		return statename;
	}
	public void setStatename(String statename) {
		this.statename = statename;
	}
	public Integer getIsdelete() {
		return isdelete;
	}
	public void setIsdelete(Integer isdelete) {
		this.isdelete = isdelete;
	}
	
}
