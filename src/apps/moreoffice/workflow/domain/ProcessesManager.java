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
 * 流程管理表
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="flow_processesmanager")
public class ProcessesManager
{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_process_manager_gen")
    @GenericGenerator(name = "seq_process_manager_gen", strategy = "native", parameters = {@ Parameter(name = "sequence", value = "SEQ_PROCESS_MANAGER_ID")})
    private Long id;
	private String name;          // 流程名
	@Column(name = "index_")
	private int index;             // 流程序号
	private Long classId;          // 流程类别Id
	@Column(name = "type_")
	private int type;              // 流程类型具体值参加com.evermore.weboffice.constants.both.WorkflowConst定义(FIX_PROCESS, FREE_PROCESS)
	private Long formId;           // 流程相关的表单Id
	private String path;           // 流程定义文件位置
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public int getIndex()
	{
		return index;
	}
	public void setIndex(int index)
	{
		this.index = index;
	}
	public Long getClassId()
	{
		return classId;
	}
	public void setClassId(Long classId)
	{
		this.classId = classId;
	}
	public int getType()
	{
		return type;
	}
	public void setType(int type)
	{
		this.type = type;
	}
	public Long getFormId()
	{
		return formId;
	}
	public void setFormId(Long formId)
	{
		this.formId = formId;
	}
	public String getPath()
	{
		return path;
	}
	public void setPath(String path)
	{
		this.path = path;
	}
	

}
