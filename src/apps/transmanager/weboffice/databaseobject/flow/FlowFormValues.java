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
 * 流程表单的具体值表
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @date    2013-1-20     
 * @since   云办公1.0
 */
@Entity
@Table(name="flowformvalues")
public class FlowFormValues implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowformvalues_gen")
	@GenericGenerator(name = "seq_flowformvalues_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWFORMVALUES_ID") })
	private Long id;//主键
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private FlowTransForms flowTransForms;//对应的事务

	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private FormFields formFields;//对应的控件
	
	@Column(name = "fieldvalues")
	private String fieldvalues;//控件值

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public FlowTransForms getFlowTransForms() {
		return flowTransForms;
	}

	public void setFlowTransForms(FlowTransForms flowTransForms) {
		this.flowTransForms = flowTransForms;
	}

	public FormFields getFormFields() {
		return formFields;
	}

	public void setFormFields(FormFields formFields) {
		this.formFields = formFields;
	}

	public String getFieldvalues() {
		return fieldvalues;
	}

	public void setFieldvalues(String fieldvalues) {
		this.fieldvalues = fieldvalues;
	}
	
}
