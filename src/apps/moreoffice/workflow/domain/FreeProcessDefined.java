package apps.moreoffice.workflow.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * 自由流程的定义保存表。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="flow_freeprocessdefined")
public class FreeProcessDefined
{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_freeprocessdefined_gen")
    @GenericGenerator(name = "seq_freeprocessdefined_gen", strategy = "native", parameters = {@ Parameter(name = "sequence", value = "SEQ_FREE_PROCESS_DEFINED_ID")})
    private Long id;
	private String processId;            // 流程定义值。
	private String packName;             // 流程定义包名	
	@Lob
	private byte[] processDefined;       // 流程定义的节点数据
	@Column(name = "index_")
	private long index;                  // 当前节点的最大编号，为新增节点的导向用
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getProcessId()
	{
		return processId;
	}
	public void setProcessId(String processId)
	{
		this.processId = processId;
	}
	public String getPackName()
	{
		return packName;
	}
	public void setPackName(String packName)
	{
		this.packName = packName;
	}
	public byte[] getProcessDefined()
	{
		return processDefined;
	}
	public void setProcessDefined(byte[] processDefined)
	{
		this.processDefined = processDefined;
	}
	public long getIndex()
	{
		return index;
	}
	public void setIndex(long index)
	{
		this.index = index;
	}
	
	
}
