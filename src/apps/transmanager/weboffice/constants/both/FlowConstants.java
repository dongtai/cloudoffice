package apps.transmanager.weboffice.constants.both;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

//禁止继承类。
public final class FlowConstants
{
	/**
	 * 禁止实例化类
	 */
	private FlowConstants()
	{
		
	}
	// 流程实例或流程任务建立状态
	public final static int CREATED = 0;                            // 0
	// 流程或流程中节点任务执行状态，对流程节点可能用户输入数据，暂时保存数据，而没有结束任务。
	public final static int INPROGRESS = CREATED + 1;               // 1
	// 流程或流程中任务节点执行完成状态。
	public final static int COMPLETED = INPROGRESS + 1;             // 2
	// 流程实例废弃状态
	public final static int ABANDON = COMPLETED + 1;                // 3
	// 流程实例错误状态
	public final static int ERROR = ABANDON + 1;                    // 4
}
