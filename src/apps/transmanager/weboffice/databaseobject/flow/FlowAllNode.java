package apps.transmanager.weboffice.databaseobject.flow;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 具体流程节点定义表,将很多状态串联在一起,也可以说将大状态串联在一起,暂不考虑子流程问题,以后再建一个子流程表，实现起来应该不难
 * <p>
 * <p>
 * EIO版本:        fgw
 * <p>
 * 作者:           孙爱华
 * <p>
 * 日期:           2012-4-6
 * <p>
 * 负责人:         孙爱华
 * <p>
 * 负责小组:        Office
 * <p>
 * <p>
 */
@Entity
@Table(name="flowallnode")
public class FlowAllNode  implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowallnode_gen")
    @GenericGenerator(name = "seq_flowallnode_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWALLNODE_ID") })
    private Long id;
    
    @Column(name = "flowinfoid")
    private Long flowinfoid;//流程编号
    
    @Column(name = "companyid")
    private Long companyid;//单位编号
    
    @Column(name = "companyname", length = 100)
    private String companyname;//单位名称


	@Column(name = "description", length = 1000)
    private String description;//流程中节点描述
    
    @Column(name = "createtime")
    private Date createtime;//流程创建时间
    
    @Column(name = "startstateid")
    private Long startstateid;//开始状态（节点）【绘图用的】
    
    @Column(name = "endstateid")
    private Long endstateid;//结束状态（节点）【绘图用的】
    
    @Column(name = "viewstateid")
    private Long viewstateid;//显示的状态，为了有子流程考虑的
    
    @Column(name = "starttype")
    private Integer starttype;//类别，1为自选人员，2固定人员，3送审者 
    
    @Column(name = "endtype")
    private Integer endtype;//
    
    
    
    @Column(name = "arrowsites", length = 255)
    private String arrowsites;//箭头位置格式为 x,y;x,y【起始;终止】
    

	@Column(name = "canread")
    private Integer canread;//是否允许单独送阅读，与审批分开进行

	@Column(name = "issub")
    private Integer issub;//是否有子流程，暂时不考虑
    
    @Column(name = "subauto")
    private Integer subauto=0;//子流程执行完毕后是否自动走向下一个节点,1为自动
    
    @Column(name = "nodenum")
    private Integer nodenum;//节点排序
    
    @Column(name = "modifytype")
    private Integer modifytype;//处理人类型
    
    @Column(name = "actionname",length = 100)
    private String actionname;//按钮名称
    
	private String dstartid;//设计时的起始状态
	private String dendid;//设计时的终止状态

	public String getDstartid() {
		return dstartid;
	}

	public void setDstartid(String dstartid) {
		this.dstartid = dstartid;
	}

	public String getDendid() {
		return dendid;
	}

	public void setDendid(String dendid) {
		this.dendid = dendid;
	}

	public String getActionname()
	{
		return actionname;
	}

	public void setActionname(String actionname)
	{
		this.actionname = actionname;
	}

	public String getArrowsites()
	{
		return arrowsites;
	}

	public void setArrowsites(String arrowsites)
	{
		this.arrowsites = arrowsites;
	}

	public Integer getModifytype()
	{
		return modifytype;
	}

	public void setModifytype(Integer modifytype)
	{
		this.modifytype = modifytype;
	}

	public Integer getNodenum()
	{
		return nodenum;
	}

	public void setNodenum(Integer nodenum)
	{
		this.nodenum = nodenum;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}
    
    public Integer getCanread()
	{
		return canread;
	}

	public void setCanread(Integer canread)
	{
		this.canread = canread;
	}

	public Long getFlowinfoid()
	{
		return flowinfoid;
	}

	public void setFlowinfoid(Long flowinfoid)
	{
		this.flowinfoid = flowinfoid;
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

	public Long getStartstateid()
	{
		return startstateid;
	}

	public void setStartstateid(Long startstateid)
	{
		this.startstateid = startstateid;
	}

	public Long getEndstateid()
	{
		return endstateid;
	}

	public void setEndstateid(Long endstateid)
	{
		this.endstateid = endstateid;
	}

	public Long getViewstateid()
	{
		return viewstateid;
	}

	public void setViewstateid(Long viewstateid)
	{
		this.viewstateid = viewstateid;
	}

	public Integer getStarttype()
	{
		return starttype;
	}

	public void setStarttype(Integer starttype)
	{
		this.starttype = starttype;
	}

	public Integer getEndtype()
	{
		return endtype;
	}

	public void setEndtype(Integer endtype)
	{
		this.endtype = endtype;
	}

	public Integer getIssub()
	{
		return issub;
	}

	public void setIssub(Integer issub)
	{
		this.issub = issub;
	}

	public Integer getSubauto()
	{
		return subauto;
	}

	public void setSubauto(Integer subauto)
	{
		this.subauto = subauto;
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
