package apps.transmanager.weboffice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * 
 * 联系人分组表
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name="customgroup_tb")
public class CustomGroupPo implements SerializableAdapter{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7722189585797972002L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_gen")
	@GenericGenerator(name = "seq_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ID") })
	private Long id;
	
	/**
	 * 联系人组的创建者ID
	 */
	private Long userId;
	
	/**
	 * 联系人组的名称
	 */
	@Column(length = 100)
	private String name;
	
	
	/**
	 * 是否是系统内置的组
	 */
	@Column(columnDefinition="INT default 0")
	private Boolean system;
	
	/**
	 * 排序
	 */
	@Column(columnDefinition="INT default 0")
	private int sortNo;
	
	
	/**
	 * 是否为默认分组
	 *    一个用户只能有一个默认分组。
	 */
	@Column(columnDefinition="INT default 0")
	private Boolean defaultOrg;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	public int getSortNo() {
		return sortNo;
	}
	
	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}
	
	public Boolean isDefaultOrg() {
		if (defaultOrg==null)
		{
			defaultOrg=false;
		}
		return defaultOrg;
	}
	
	public void setDefaultOrg(Boolean defaultOrg) {
		this.defaultOrg = defaultOrg;
	}
	
	public Boolean isSystem() {
		if (system==null)
		{
			system=false;
		}
		return system;
	}
	
	public void setSystem(Boolean system) {
		this.system = system;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
