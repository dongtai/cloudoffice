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

//采用class定义方式是禁止或实现继承类。
public final class ApproveConstants
{
	/**
	 * 禁止实例化类
	 */
	private ApproveConstants()
	{
	}
	 
	 public final static int APPROVAL_STATUS_START = 0; //仅阅读
	 public final static int APPROVAL_STATUS_PAENDING = 1; // 送审也就是领导待审批 
	 public final static int APPROVAL_STATUS_AGREE = 2;//审批通过
	 public final static int APPROVAL_STATUS_RETURNED = 3;//审批退回
	 public final static int APPROVAL_STATUS_ABANDONED = 4;//废弃
	 public final static int APPROVAL_STATUS_END = 5;//已成文
	 public final static int APPROVAL_STATUS_ENDTOOffICE = 6;//已办结并提交给办公室
	 public final static int APPROVAL_STATUS_PUBLISH = 7;//已发布
	 public final static int APPROVAL_STATUS_ARCHIVING = 8;//已归档
	 public final static int APPROVAL_STATUS_DESTROY = 9;//待销毁(发布跟归档删除的文档)
	 public static int APPROVAL_STATUS_OVER = 10;//用户文档归档后的一个最后状态，此状态的记录不在页面上显示
	 
	 
	 public final static int NEW_STATUS_START = 0; //协作或仅批阅
	 public final static int NEW_STATUS_READ = -1;//已签收
	 public final static int NEW_STATUS_WAIT = 1; // 签批，待办包括待协作、待批阅、待签
	 public final static int NEW_STATUS_HAD = 2;//已办
	 public final static int NEW_STATUS_HADREAD = 3;//已阅\已协作
	 
	 
	 public final static int NEW_STATUS_END = 8;//终止
	 public final static int NEW_STATUS_DEL = 9;//废弃
	 public final static int NEW_STATUS_SUCCESS = 10;//已成文
	 
	 public final static int NEW_STATUS_ENDTOOffICE = 15;//已办结并提交给办公室  再定
	 public final static int NEW_STATUS_PUBLISH = 16;//已发布  再定
	 public final static int NEW_STATUS_ARCHIVING = 17;//已归档  再定
	 public final static int NEW_STATUS_DESTROY = 18;//待销毁(发布跟归档删除的文档)  再定
	 
	 public final static long NEW_ACTION_SEND = 1l; //送审   这些应该放到数据库中
	 public final static long NEW_ACTION_COOPER = 2l; // 送协作
	 public final static long NEW_ACTION_SIGN = 3l; // 签批
	 public final static long NEW_ACTION_SIGNREAD = 4l; // 批阅
	 public final static long NEW_ACTION_HADCOOPER = 5l; // 协作
	 public final static long NEW_ACTION_HADSUCCESS = 7l; // 成文
	 public final static long NEW_ACTION_HADACTIVE = 8l; // 归档
	 
	 public final static long NEW_ACTION_END = 10l; // 终止
	 public final static long NEW_ACTION_DEL = 11l; // 终止
	 public final static String SENDTIME="sendtime";//按照更新时间排序
	 public final static String TITLE="title";//按照主题排序
	 public final static String STATE="state";//按照状态排序
	 public final static String ACCEPTER="accepter";//收文人
	 public final static String SENDER="sender";//送文人
	 public final static String MODIFYTYPE="modifytype";
	 public final static String MODIFYTIME="modifytime";
	 public final static String COMMENT="comment";
	 
	 
	 public final static String TRANSTITLE="title";//按照交办事务主题排序
	 public final static String TRANSSTATE="state";//按照交办事务状态排序
	 public final static String TRANSACCEPTER="accepter";//交办事务收文人
	 public final static String TRANSSENDER="sender";//交办事务送文人
	 public final static String TRANSMODIFYTIME="modifytime";//交办事务处理时间
	 public final static String TRANSCOMMENT="comment";//交办事务备注
	 
	 public final static String MEETNAME="meetname";//会议名称
	 public final static String MEETDETAILTIME="meetdetailtime";//会议时间
	 public final static String MEETADDDATE="adddate";//会议发布时间
	 public final static String MEETADDRESS="meetaddress";//会议地点
	 public final static String MEETMASTER="mastername";//会议召开人

	 
	 public final static long TRANS_STATUS_START = 0; //事务阅读状态
	 public final static long TRANS_STATUS_WAIT = 1; // 事务整体状态，待办（或者办理人的在办状态）
	 public final static long TRANS_STATUS_HAD = 2;//事务已处理完
	 public final static long TRANS_STATUS_END = 4;//终止
	 public final static long TRANS_STATUS_DEL = 5;//销毁
	 
	 public final static long TRANS_ACTION_SEND = 0; //交办
	 public final static long TRANS_ACTION_WAIT = 1; // 在办
	 public final static long TRANS_ACTION_HAD = 2;//办结
	 
	 
	 public final static long MEET_STATUS_START = 0; //会议阅读状态
	 public final static long MEET_STATUS_WAIT = 1; // 会议整体状态，待办（或者办理人的状态）
	 public final static long MEET_STATUS_HAD = 2;//会议已处理完
	 public final static long MEET_STATUS_END = 4;//终止
	 public final static long MEET_STATUS_DEL = 5;//销毁

	 public final static long MEET_ACTION_APPEND = 1;//参加
	 public final static long MEET_ACTION_REPLACE = 2;//替会，需要填写替会人员
	 public final static long MEET_ACTION_NOTAPPEND = 3;//不参加
	 
	 public final static int APPROVAL_FROMUNIT = 1;//字典表，来文单位
	 public final static int APPROVAL_MODIFYSCRIPT = 2;//字典表，处理备注
	 public final static int APPROVAL_FILETYPE = 3;//字典表，文档类别
	 
	 public final static int APPROVAL_DEFAULT_SEND = 1;//送文默认标记
	 public final static int APPROVAL_DEFAULT_MODIFY = 2;//处理默认标记
}
