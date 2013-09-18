package apps.transmanager.weboffice.databaseobject.flow;

import java.util.Date;

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
 * 流程表单的业务表
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @date    2013-1-20     
 * @since   云办公1.0
 */
@Entity
@Table(name="flowtransforms")
public class FlowTransForms implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowformvalues_gen")
	@GenericGenerator(name = "seq_flowformvalues_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWFORMVALUES_ID") })
	private Long id;//主键
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private FormInfo formInfo;//对应的表单，可以为空
	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private FlowInfo flowInfo;//对应的流程
	
	@Column(name = "createtime")
    private Date createtime;//创建时间
	@Column(name = "userid")
    private Long userid;//创建人
	@Column(name = "companyid")
    private Long companyid;//所属单位
	//以上应该独立形成表，以后如果fields多了再独立出来
	
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

	public FlowInfo getFlowInfo() {
		return flowInfo;
	}

	public void setFlowInfo(FlowInfo flowInfo) {
		this.flowInfo = flowInfo;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getCompanyid() {
		return companyid;
	}

	public void setCompanyid(Long companyid) {
		this.companyid = companyid;
	}

}
