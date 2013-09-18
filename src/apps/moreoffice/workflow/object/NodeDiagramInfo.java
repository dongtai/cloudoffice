package apps.moreoffice.workflow.object;

/**
 * 工作流流程节点图形类
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */
public class NodeDiagramInfo
{
	private String nodeId;
	private int x;
	private int y;
	private int width;
	private int height;

	public NodeDiagramInfo()
	{
	}

	public NodeDiagramInfo(String nodeId, final int x, final int y, final int width, final int height)
	{
		this.nodeId = nodeId;
		this.height = height;
		this.width = width;
		this.x = x;
		this.y = y;
	}

	public String getNodeId()
	{
		return nodeId;
	}

	public void setNodeId(String nodeId)
	{
		this.nodeId = nodeId;
	}
	
	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}
}
