package apps.transmanager.weboffice.databaseobject.archive;

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

import apps.transmanager.weboffice.domain.IParentKey;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 归档类别表，该表表示归档文件的类别。
 * 归档中可以嵌套存在。在归档表中有字段parentcod
 * 查询父类别及子类别而定义的，通过该字段查询，可以避免在类别父组织或
 * 子类别的时候进行递归的查询处理，该字段的具体定义见下面的字段说明。
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="archivetype")
public class ArchiveType implements SerializableAdapter, IParentKey
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_archivetype_gen")
	@GenericGenerator(name = "seq_archivetype_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ARCHIVETYPE_ID") })
	private Long id;
	@ManyToOne
    @OnDelete(action=OnDeleteAction.CASCADE) 
	private ArchiveType parent;
	@Column(length = 100)
	private String name;
	@Column(length = 1000)
	private String description;
	@Column(length = 255)
	private String parentKey;
	/* 
	 * 快速查询用的,格式为0000-0000-0000
	 */
	private Integer sortNum=10000;//类别排序
	private Long orgid;//类别所属组织
	
	
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public Integer getSortNum()
	{
		return sortNum;
	}
	public void setSortNum(Integer sortNum)
	{
		this.sortNum = sortNum;
	}
	public Long getOrgid()
	{
		return orgid;
	}
	public void setOrgid(Long orgid)
	{
		this.orgid = orgid;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public void setParent(ArchiveType parent)
	{
		this.parent = parent;
	}
	@Override
	public Long getId()
	{
		// TODO Auto-generated method stub
		return id;
	}
	@Override
	public IParentKey getParent()
	{
		// TODO Auto-generated method stub
		return parent;
	}
	@Override
	public String getParentKey()
	{
		// TODO Auto-generated method stub
		return this.parentKey;
	}
	@Override
	public void setParentKey(String parentKey)
	{
		this.parentKey=parentKey;
		
	}
	

}
