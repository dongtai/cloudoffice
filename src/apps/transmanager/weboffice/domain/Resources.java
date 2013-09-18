package apps.transmanager.weboffice.domain;



/**
 * 文件注释
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

public interface Resources extends SerializableAdapter
{
	
	public Long getId();

	public void setId(Long id);

	public String getResourceName();

	public void setResurceName(String name);

	public Integer getResourceType();

}
