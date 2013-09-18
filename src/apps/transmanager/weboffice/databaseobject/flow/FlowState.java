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
 * 流程表(状态，节点)字典表
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
@Table(name="flowstate")
public class FlowState  implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowstate_gen")
    @GenericGenerator(name = "seq_flowstate_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWSTATE_ID") })
    private Long id;
    
    @Column(name = "statename", length = 100)
    private String statename;//流程状态名称
    
    @Column(name = "companyid")
    private Long companyid;//单位编号
    
    @Column(name = "companyname", length = 100)
    private String companyname;//单位编号,为了显示用的

	@Column(name = "effect", length = 10)
    private String effect="Y";//有效性，默认有效，"N"是不显示
    
    @Column(name = "flowinfoid")
    private Long flowinfoid;//流程序号

    @Column(name = "description", length = 1000)
    private String description;//状态描述

    @Column(name = "statenum")
    private Integer statenum;//状态序号
    
    @Column(name = "endnode")
    private Integer endnode=0;//结束节点标志
    
    @Column(name = "startnode")
    private Integer startnode=0;//开始节点
    
    @Column(name = "topdot")
    private Integer topdot;//节点位置
    
    @Column(name = "leftdot")
    private Integer leftdot;//节点位置
    
    @Column(name = "width")
    private Integer width;//宽度
    
    @Column(name = "height")
    private Integer height;//高度

	@Column(name = "canread")
    private Integer canread=0;//该节点能否传阅
    
    @Column(name = "hadrun")
    private Integer hadrun=0;//冗余，表示是否已执行过的节点
    
    @Column(name = "iscurrent")
    private Integer iscurrent=0;//是否为当前节点
    
    @Column(length = 100)
    private String modifyname;//用来传递处理者

	private Date modifydate;//用来传递处理时间
    private Long historyid;//用来传递历史记录主键
    
	private String disignid;//设计时的编号
    
    
    
    public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

    public String getDisignid() {
		return disignid;
	}

	public void setDisignid(String disignid) {
		this.disignid = disignid;
	}

	public String getModifyname()
	{
		return modifyname;
	}

	public void setModifyname(String modifyname)
	{
		this.modifyname = modifyname;
	}
	public Date getModifydate()
	{
		return modifydate;
	}

	public void setModifydate(Date modifydate)
	{
		this.modifydate = modifydate;
	}

	public Long getHistoryid()
	{
		return historyid;
	}

	public void setHistoryid(Long historyid)
	{
		this.historyid = historyid;
	}

	public Integer getHadrun()
	{
		return hadrun;
	}

	public void setHadrun(Integer hadrun)
	{
		this.hadrun = hadrun;
	}

	public Integer getIscurrent()
	{
		return iscurrent;
	}

	public void setIscurrent(Integer iscurrent)
	{
		this.iscurrent = iscurrent;
	}

	public Integer getCanread()
	{
		return canread;
	}

	public void setCanread(Integer canread)
	{
		this.canread = canread;
	}

	public Integer getTopdot()
	{
		return topdot;
	}

	public void setTopdot(Integer topdot)
	{
		this.topdot = topdot;
	}

	public Integer getLeftdot()
	{
		return leftdot;
	}

	public void setLeftdot(Integer leftdot)
	{
		this.leftdot = leftdot;
	}

	public Integer getStartnode()
	{
		return startnode;
	}

	public void setStartnode(Integer startnode)
	{
		this.startnode = startnode;
	}

	public Integer getEndnode()
	{
		return endnode;
	}

	public void setEndnode(Integer endnode)
	{
		this.endnode = endnode;
	}

	public Integer getStatenum()
	{
		return statenum;
	}

	public void setStatenum(Integer statenum)
	{
		this.statenum = statenum;
	}


	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getStatename()
	{
		return statename;
	}

	public void setStatename(String statename)
	{
		this.statename = statename;
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


	public String getEffect()
	{
		return effect;
	}

	public void setEffect(String effect)
	{
		this.effect = effect;
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
    
    
}
