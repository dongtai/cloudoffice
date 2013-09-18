package apps.transmanager.weboffice.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="template")
public class TemplatePo implements SerializableAdapter{

	private static final long serialVersionUID = -3404728189850704603L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_template_gen")
	@GenericGenerator(name = "seq_template_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TEMPLATE_ID") })
	private Long id;	//真实ID
	
	/**
	 * 模板组
	 */
	private String name;
	
	/**
	 * 是系统内部的模板组
	 *   0是普通的
	 *   1是系统内部的
	 *   2是公司
	 */
	private int type;
	
	private long userId;
	
	/**
	 * 顶层部门ID
	 */
	private  long companyId;
	
	
	private Date createDate;

	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="template_id")
	private List<TemplateItemPo> templateItems;


	public long getCompanyId() {
		return companyId;
	}


	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	
	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public List<TemplateItemPo> getTemplateItems() {
		return templateItems;
	}


	public void setTemplateItems(List<TemplateItemPo> templateItems) {
		this.templateItems = templateItems;
	}
	
	public long getUserId() {
		return userId;
	}


	public void setUserId(long userId) {
		this.userId = userId;
	}

	
}
