package apps.moreoffice.workflow.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * 流程表单管理对象
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="flow_formsmanager")
public class FormsManager
{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_form_manager_gen")
    @GenericGenerator(name = "seq_form_manager_gen", strategy = "native", parameters = {@ Parameter(name = "sequence", value = "SEQ_FORM_MANAGER_ID")})
    private Long id;
	private String name;      // 表单名字
	private String path;      // 表单所在的path
	
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
	public String getPath()
	{
		return path;
	}
	public void setPath(String path)
	{
		this.path = path;
	}
	
	
	
	
	
}
