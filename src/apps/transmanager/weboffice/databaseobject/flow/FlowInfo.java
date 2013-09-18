package apps.transmanager.weboffice.databaseobject.flow;


import java.util.Date;

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
 * 流程定义表，相当于字典表，采用的JBPM的方式，原先的OSWORKFLOW方式作废
 * <p>
 * <p>
 * EIO版本:        fgw
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
@Table(name="flowinfo")
public class FlowInfo  implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowinfo_gen")
    @GenericGenerator(name = "seq_flowinfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWINFO_ID") })
    private Long id;
    
    @Column(name = "flowname", length = 100)
    private String flowname;//流程名称
    
    @Column(name = "companyid")
    private Long companyid;//单位编号
    
    @Column(name = "companyname",length = 100)
    private String companyname;//单位名称

	@Column(name = "effect", length = 10)
    private String effect="Y";//有效性，默认有效，无效为N
    
    @Column(name = "flownum")
    private Integer flownum;//流程序号，排序用的

	@Column(name = "description", length = 1000)
    private String description;//流程描述
    
    @Column(name = "createtime")
    private Date createtime;//流程创建时间
    
    @Column(name = "flowpicture", length = 255)
    private String flowpicture;//流程图
    @Column(name = "timelen")
    private Integer timelen;//规定时长，天
    @Column(name = "flowtypeid")
    private Long flowtypeid;//流程类别
    
    @Column(name = "formid")
    private Long formid;//流程对应的表单

	
	@Transient
    private FlowState[] flowState;
    @Transient
	private FlowAllNode[] flowAllNodes;//
    @Transient
	private String flowtypename;//类型名称

    public Long getFormid() {
		return formid;
	}

	public void setFormid(Long formid) {
		this.formid = formid;
	}

    public String getFlowtypename() {
		return flowtypename;
	}

	public void setFlowtypename(String flowtypename) {
		this.flowtypename = flowtypename;
	}


	public FlowState[] getFlowState() {
		return flowState;
	}

	public void setFlowState(FlowState[] flowState) {
		this.flowState = flowState;
	}

	public FlowAllNode[] getFlowAllNodes() {
		return flowAllNodes;
	}

	public void setFlowAllNodes(FlowAllNode[] flowAllNodes) {
		this.flowAllNodes = flowAllNodes;
	}

    public Long getFlowtypeid() {
		return flowtypeid;
	}

	public void setFlowtypeid(Long flowtypeid) {
		this.flowtypeid = flowtypeid;
	}


	public Integer getTimelen()
	{
		return timelen;
	}

	public void setTimelen(Integer timelen)
	{
		this.timelen = timelen;
	}

	public String getFlowpicture()
	{
		return flowpicture;
	}

	public void setFlowpicture(String flowpicture)
	{
		this.flowpicture = flowpicture;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Date getCreatetime()
	{
		return createtime;
	}

	public void setCreatetime(Date createtime)
	{
		this.createtime = createtime;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getFlowname()
	{
		return flowname;
	}

	public void setFlowname(String flowname)
	{
		this.flowname = flowname;
	}

	

	public String getEffect()
	{
		return effect;
	}

	public void setEffect(String effect)
	{
		this.effect = effect;
	}

	public Integer getFlownum()
	{
		return flownum;
	}

	public void setFlownum(Integer flownum)
	{
		this.flownum = flownum;
	}
    
    

	public Long getCompanyid() {
		return companyid;
	}

	public void setCompanyid(Long companyid) {
		this.companyid = companyid;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}


}
