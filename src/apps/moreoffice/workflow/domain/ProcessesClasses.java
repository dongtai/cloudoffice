package apps.moreoffice.workflow.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * 流程分类类别表
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="flow_processesclasses")
public class ProcessesClasses
{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_processclasses_gen")
    @GenericGenerator(name = "seq_processclasses_gen", strategy = "native", parameters = {@ Parameter(name = "sequence", value = "SEQ_PROCESS_CLASSES_ID")})
    private Long id;
	private String typeName;           // 流程类别名称
	@Column(name = "index_")
	private int index;                 // 流程类别编号
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getTypeName()
	{
		return typeName;
	}
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}
	public int getIndex()
	{
		return index;
	}
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	
	
}
