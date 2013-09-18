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

public interface Actions extends SerializableAdapter
{
	public Long getId();

	public void setId(Long id);

	public String getActionName();

	public void setActionName(String name);

	public Integer getActionType();
	
	public void update(Actions n);

	public Actions getClone();

}
