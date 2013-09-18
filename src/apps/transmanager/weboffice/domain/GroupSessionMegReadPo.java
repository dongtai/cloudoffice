package apps.transmanager.weboffice.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="groupsessionmegread_tb")
public class GroupSessionMegReadPo implements SerializableAdapter
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_gen")
	@GenericGenerator(name = "seq_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ID") })
	private Long id;
	private Long groupId;
	private Boolean readed;
	
	/**
	 * 接收人是否删除
	 */
	private Boolean deleted;
	private Long acceptId;
	private Long groupSessionMegId;
	
	public Long getGroupSessionMegId() {
		return groupSessionMegId;
	}
	public void setGroupSessionMegId(Long groupSessionMegId) {
		this.groupSessionMegId = groupSessionMegId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Boolean getReaded() {
		return readed;
	}
	public void setReaded(Boolean readed) {
		this.readed = readed;
	}
	public Long getAcceptId() {
		return acceptId;
	}
	public void setAcceptId(Long acceptId) {
		this.acceptId = acceptId;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
}
