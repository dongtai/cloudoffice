package apps.transmanager.weboffice.databaseobject.flow;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 流程类型表，相当于字典表，采用的JBPM的方式，原先的OSWORKFLOW方式作废
 * <p>
 * <p>
 * EIO版本:        公云版
 * <p>
 * 作者:           孙爱华
 * <p>
 * 日期:           2012-3-20
 * <p>
 * 负责人:         孙爱华
 * <p>
 * 负责小组:        Office
 * <p>
 * <p>
 */
@Entity
@Table(name="flowtype")
public class FlowType  implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowtype_gen")
    @GenericGenerator(name = "seq_flowtype_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWTYPE_ID") })
    private Long id;
    
    @Column(name = "typename", length = 255)
    private String typename;//流程类别名称
    
    @Column(name = "companyid")
    private Long companyid;//单位编号

	@Column(name = "effect", length = 10)
    private String effect="Y";//有效性，默认有效，无效为N

	@Column(name = "description", length = 1000)
    private String description;//流程描述
	@Transient
    private String companyname;//用来临时存放公司名称
	
	
	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	public Long getCompanyid() {
		return companyid;
	}

	public void setCompanyid(Long companyid) {
		this.companyid = companyid;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
