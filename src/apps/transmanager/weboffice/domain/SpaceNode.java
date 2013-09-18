package apps.transmanager.weboffice.domain;

import apps.transmanager.weboffice.constants.both.SpaceConstants;
import apps.transmanager.weboffice.databaseobject.Spaces;

/**
 * 空间的节点类型，在该节点中记录空间节点中的各种内容。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class SpaceNode implements ISpacesNode
{
	private Spaces space;
    // 空间的类型， 目前分为组织空间，组空间，用户空间，用户自定义空间，具体建com.evermore.weboffice.constants.both.SpaceConstants中常量定义。
	private int spaceType;
    // 空间对象的拥有则的Id值，即是该空间所属对象的ID值，如orgId，groupId，userId，teamId。
	private int ownId;
	
	public SpaceNode()
	{
	}
	
	public SpaceNode(Spaces space)
	{
		this.space = space;
	}
	
	public SpaceNode(Spaces space, int type, int ownId)
	{
		this.space = space;
		this.spaceType = type;
		this.ownId = ownId;
	}
	
	public String getDisplayName()
	{
		return space != null ? space.getName() : "";
	}

	public int getNodeType()
	{
		return SpaceConstants.SPACE;
	}

	public String getPath()
	{
		return space != null ? space.getSpacePath() : "";
	}

	public Spaces getSpace()
	{
		return space;
	}

	public void setSpace(Spaces space)
	{
		this.space = space;
	}

	public int getSpaceType()
	{
		return spaceType;
	}

	public void setSpaceType(int spaceType)
	{
		this.spaceType = spaceType;
	}

	public int getOwnId()
	{
		return ownId;
	}

	public void setOwnId(int ownId)
	{
		this.ownId = ownId;
	}
	

}
