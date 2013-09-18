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
 * 流程操作者
 * <p>
 * <p>
 * EIO版本:        fgw
 * <p>
 * 作者:           孙爱华
 * <p>
 * 日期:           2012-4-16
 * <p>
 * 负责人:         孙爱华
 * <p>
 * 负责小组:        Office
 * <p>
 * <p>
 */
@Entity
@Table(name="flowowners")
public class FlowOwners  implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowowners_gen")
    @GenericGenerator(name = "seq_flowowners_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWOWNERS_ID") })
    private Long id;
    
    
    @Column(name = "userid")
    private Long userid;//工号
    
    @Column(name = "realname", length = 100)
    private String realname;//名称

    @Column(name = "flowinfoid")
    private Long flowinfoid;//流程序号
    
    @Column(name = "nodeid")
    private Long nodeid;//节点主键

	@Column(name = "description", length = 1000)
    private String description;//状态描述


    public Long getNodeid()
	{
		return nodeid;
	}

	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getUserid()
	{
		return userid;
	}

	public void setUserid(Long userid)
	{
		this.userid = userid;
	}

	public String getRealname()
	{
		return realname;
	}

	public void setRealname(String realname)
	{
		this.realname = realname;
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
