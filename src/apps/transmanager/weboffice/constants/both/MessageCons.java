package apps.transmanager.weboffice.constants.both;

/**
 * 系统发送消息的常量定义。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
// 禁止继承
public final class MessageCons
{
	/**
	 * 禁止实例化类
	 */
	private MessageCons()
	{
	}
	
	/**
	 * 消息类别的定义。
	 */
	public final static int USER_TYPE = 0;                        // 0 用户个人消息。
	public final static int SYS_TYPE = USER_TYPE + 1;             // 1  系统消息。
	public final static int SPACE_TYPE = SYS_TYPE + 1;            // 2 空间邀请消息。
	public final static int DOC_TYPE = SPACE_TYPE + 1;            // 3 文档消息。
	public final static int CALENDER_TYPE = DOC_TYPE + 1;         // 4 日程消息。
	public final static int IM_TYPE = CALENDER_TYPE + 1;          // 5 即时消息。
	public final static int ADD_DOC_TYPE = IM_TYPE + 1;           // 6增加文档 消息。
	public final static int ADUIT_DOC_TYPE = ADD_DOC_TYPE + 1;    // 7审核文档 消息。
    public final static int SHARE_TYPE = ADUIT_DOC_TYPE + 1;      // 8共享文档 消息。===
    public final static int FORCE_QUIT = SHARE_TYPE + 1;          // 9 被迫退出登录 
    public final static int READDOC = FORCE_QUIT + 1;          	// 10 传阅文档消息
    public final static int AVMSG = READDOC + 1;          		// 11 公告消息===
    public final static int MEETING = AVMSG + 1;          		// 12会议邀请消息
    public final static int AUDIT = MEETING + 1;          		// 13会议邀请消息
    //中间间隔几个留着备用
    public final static int SIGN = AUDIT + 3;          		// 16事务签批消息(催办)===
    public final static int SIGNREAD = SIGN + 1;          		// 17事务签批中批阅
    public final static int COOPER = SIGNREAD + 1;          	// 18协作提醒
    public final static int SENDSIGN = COOPER + 1;          	// 19送签提醒====
    public final static int SENDSIGNREAD = SENDSIGN + 1;        // 20送阅提醒
    public final static int SENDCOOPER = SENDSIGNREAD + 1;      // 21送协作提醒
    
    public final static int SENDTRANS = SENDCOOPER+5;    // 26分发事务提醒====
    public final static int SENDMEET = SENDTRANS+8;    // 34会议提醒====
}
