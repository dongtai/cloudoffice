package apps.transmanager.weboffice.databaseobject.flow;


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
@Table(name="flowsubnode")
public class FlowSubNode  implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowsubnode_gen")
    @GenericGenerator(name = "seq_flowsubnode_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWSUBNODE_ID") })
    private Long id;
    
    @Column(name = "nodeid")
    private Long nodeid;//节点编号
    
    @Column(name = "orgid")
    private Long orgid;//组织编号

	@Column(name = "description", length = 1000)
    private String description;//流程中节点描述
    
    @Column(name = "nodetype")
    private Integer nodetype;//节点类别（串行还是并发）
    
    @Column(name = "statename", length = 100)
    private String statename;//子节点状态名称
    
    @Column(name = "actionname", length = 100)
    private String actionname;//子节点按钮名称
    
    @Column(name = "backactionname", length = 100)    
    private String backactionname;//子节点返回按钮名称

    @Column(name = "topdot")
    private Integer topdot;//节点位置,暂不用
    
    @Column(name = "leftdot")
    private Integer leftdot;//节点位置,暂不用
    
    @Column(name = "arrowsite", length = 255)
    private String arrowsite;//箭头位置
    
    @Column(name = "isread")
    private Integer isread;//是否传阅
    
    
    public Integer getIsread()
	{
		return isread;
	}

	public void setIsread(Integer isread)
	{
		this.isread = isread;
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

	public String getArrowsite()
	{
		return arrowsite;
	}

	public void setArrowsite(String arrowsite)
	{
		this.arrowsite = arrowsite;
	}

	public Long getOrgid()
	{
		return orgid;
	}

	public void setOrgid(Long orgid)
	{
		this.orgid = orgid;
	}

	public String getStatename()
	{
		return statename;
	}

	public void setStatename(String statename)
	{
		this.statename = statename;
	}

	public String getActionname()
	{
		return actionname;
	}

	public void setActionname(String actionname)
	{
		this.actionname = actionname;
	}

	public String getBackactionname()
	{
		return backactionname;
	}

	public void setBackactionname(String backactionname)
	{
		this.backactionname = backactionname;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getNodeid()
	{
		return nodeid;
	}

	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Integer getNodetype()
	{
		return nodetype;
	}

	public void setNodetype(Integer nodetype)
	{
		this.nodetype = nodetype;
	}
    
    

}
