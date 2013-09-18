package apps.transmanager.weboffice.domain;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="templateItem")
public class TemplateItemPo implements SerializableAdapter{

	private static final long serialVersionUID = -3404728189850704603L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_templateItem_gen")
	@GenericGenerator(name = "seq_templateItem_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TEMPLATEITEM_ID") })
	private Long id;	//真实ID
	
	/**
	 * 模板名称
	 */
	private String name;
	
	/**
	 * 缩略图路径
	 */
	private String imagePath;
	
	/**
	 * 模板路径
	 */
	private String tempatePath;
	

	/**
	 * 是系统内部的模板组
	 *   0是普通的
	 *   1是系统内部的
	 *   2是公司的
	 */
	private int type;
	
	/**
	 * 创建时间
	 */
	private Date createDate;
	
	/**
	 * 模板组对象
	 */
	@ManyToOne
	private TemplatePo template;
	
	
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


	public String getImagePath() {
		return imagePath;
	}


	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public TemplatePo getTemplate() {
		return template;
	}


	public void setTemplate(TemplatePo template) {
		this.template = template;
	}
	
	public String getTempatePath() {
		return tempatePath;
	}


	public void setTempatePath(String tempatePath) {
		this.tempatePath = tempatePath;
	}
	
}
