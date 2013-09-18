package apps.transmanager.weboffice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * 主要描述讨论组员与组员之间的关系
 * @author Administrator
 *
 */
@Entity
@Table(name="discugroupmember_relation_tb")
public class DiscuGroupMemberPoRelationPo implements SerializableAdapter{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7933395655178233923L;
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_discu_group_member_relation_gen")
	@GenericGenerator(name = "seq_discu_group_member_relation_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_DISCU_GROUP_MEMBER_RELATION_ID") })
	private Long id;
	
	private Long ownerId;
	
	private Long memberId;
	/**
	 * 组员的昵称
	 */
	@Column(length = 100)
	private String nickName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

}
