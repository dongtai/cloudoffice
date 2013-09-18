package apps.transmanager.weboffice.databaseobject.flow;

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
 * 自定义的表单的元素表
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @date    2013-1-20     
 * @since   云办公1.0
 */
@Entity
@Table(name="formfields")
public class FormFields implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_formfields_gen")
	@GenericGenerator(name = "seq_formfields_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FORMFIELDS_ID") })
	private Long id;//主键
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private FormInfo formInfo;//对应的表单
	
	@Column(name = "fieldname")
	private String fieldname;//控件名称
	
	@Column(name = "fieldtype")
	private Integer fieldtype=0;//控件类型，0为text，1为list，2为button，3为date控件，。。。。自行添加
	
	@Column(name = "fieldvaluetype")
	private Integer fieldvaluetype=0;//控件值类型，0为字符串，1为数字，2为日期
	
	@Column(name = "fieldid")
	private String fieldid;//控件id
	
	@Column(name = "fieldscript", length = 60000)
    private String fieldscript;//控件说明、备注
	
	@Column(name = "ismust")
	private Integer ismust=0;//是否必填项，1为必填
	
	@Column(name = "fieldcond")
	private String fieldcond;//控件条件

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FormInfo getFormInfo() {
		return formInfo;
	}

	public void setFormInfo(FormInfo formInfo) {
		this.formInfo = formInfo;
	}

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public Integer getFieldtype() {
		return fieldtype;
	}

	public void setFieldtype(Integer fieldtype) {
		this.fieldtype = fieldtype;
	}

	public Integer getFieldvaluetype() {
		return fieldvaluetype;
	}

	public void setFieldvaluetype(Integer fieldvaluetype) {
		this.fieldvaluetype = fieldvaluetype;
	}

	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}

	public String getFieldscript() {
		return fieldscript;
	}

	public void setFieldscript(String fieldscript) {
		this.fieldscript = fieldscript;
	}

	public Integer getIsmust() {
		return ismust;
	}

	public void setIsmust(Integer ismust) {
		this.ismust = ismust;
	}

	public String getFieldcond() {
		return fieldcond;
	}

	public void setFieldcond(String fieldcond) {
		this.fieldcond = fieldcond;
	}
	
	
}
