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
public interface WorkflowConst
{
	/**
	 * 流程任务执行错误
	 */
	public final static int ERROR = 0;
	/**
	 * 流程任务执行成功
	 */
	public final static int SUCCESS = ERROR + 1;
	/**
	 * 流程任务保存成功
	 */
	public final static int SAVE =  SUCCESS + 1;
	/**
	 * 流程任务取消执行
	 */
	public final static int CANCEL = SAVE + 1;
	/**
	 * 流程任务代理成功
	 */
	public final static int DELEGATE = CANCEL + 1;
	
	
	/**
	 * 流程类别定义 开始
	 */
	/**
	 * 预定义的固定流程
	 */
	public final static int FIX_PROCESS = 0;
	/**
	 * 非预定义的自由流程	
	 */
	public final static int FREE_PROCESS = FIX_PROCESS + 1; 
	/**
	 * 流程类别定义 结束
	 */
	
}
