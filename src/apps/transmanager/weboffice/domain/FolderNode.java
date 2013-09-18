package apps.transmanager.weboffice.domain;

import apps.transmanager.weboffice.constants.both.SpaceConstants;

/**
 * 文件夹类型节点，在该节点中记录文件夹的各种信息。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class FolderNode implements ISpacesNode
{

	public FolderNode(String displayName, String path)
	{
		this.name = displayName;
		this.path = path;
	}
	
	public String getDisplayName()
	{
		return name;
	}

	public int getNodeType()
	{
		return SpaceConstants.FOLDER;
	}

	public String getPath()
	{
		return path;
	}
	
	private String name;
	private String path;

}
