package apps.moreoffice.workflow.object;

/**
 * 工作流流程图片的信息
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class ProcessImgInfo
{

	public ProcessImgInfo(String name, int x, int y)
	{
		picName = name;
		this.x = x;
		this.y = y;
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
	public String getPicName()
	{
		return picName;
	}
	public void setPicName(String picName)
	{
		this.picName = picName;
	}
	
	
	public int x;   // 图形或节点的x坐标
	public int y;   // 图形或节点的y坐标
	public String picName;       // 图形或节点的图片名字
	
}
