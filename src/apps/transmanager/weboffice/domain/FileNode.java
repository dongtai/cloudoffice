package apps.transmanager.weboffice.domain;

import apps.transmanager.weboffice.constants.both.SpaceConstants;

/**
 * 文件的节点类，该类中记录一个文件节点中的各种文件信息内容。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class FileNode implements ISpacesNode
{

	public FileNode(String displayName, String path)
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
		return SpaceConstants.FILE;
	}

	public String getPath()
	{
		return path;
	}
	
	private String name;
	private String path;

}
