package apps.transmanager.weboffice.service;

import java.util.List;
import java.util.Map;

import org.directwebremoting.io.FileTransfer;

import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.GroupSessionMegPo;
import apps.transmanager.weboffice.domain.GroupSessionMegReadPo;
import apps.transmanager.weboffice.domain.SessionMegPo;
import apps.transmanager.weboffice.util.beans.Page;

public interface ITalkService {
	void deleteAllGroupSessionMessage(Long ownerId, Long groupId);

	void deleteAllPersonalSessionMessage(Long ownerId, Long userId);

	/**
	 * 删除组信息
	 * 
	 * @param msgId
	 *            信息的ID
	 * @param userId
	 *            当前用户
	 */
	void deleteGroupSessionMessage(Long msgId, Long userId);

	void deleteHisSessionMessage(int type, Long ownerId, List<Long> msgIds);

	void deleteHisSessionMessageByDate(int type, Long otherId, int delType,
			String startDate, String endDate, Long id);

	/**
	 * 删除用户个人信息
	 * 
	 * @param msgId
	 *            消息ID
	 * @param userId
	 *            当前用户的ID
	 */
	public void deletePersonalSessionMessage(Long msgId, Long userId);

	/**
	 * 处理验证信息,结束一个验证信息
	 * 
	 * @param vMsgId
	 */
	void endValidateMessage(Long vMsgId);

	/**
	 * 获取当前用户的离线信息
	 * 
	 * @param userId
	 *            当前用户ID
	 * @param isMobile
	 *            是否是手机端
	 * @return
	 */
	public Map<String, Object> getAllNewMegTip(Long userId, boolean isMobile);

	/**
	 * 获得组织结构树节点
	 * 
	 * @param user
	 * @param pId
	 * @param exceptSelf
	 * @param idFix
	 * @return
	 */
	List<Map<String, Object>> getCompanyTree(Users user, String pId, boolean exceptSelf, String idFix);

	/**
	 * 获得联系人相关Service接口
	 * 
	 * @return
	 */
	ICtmGroupService getCtmGroupService();

	/**
	 * 获得讨论组相关Service接口
	 * 
	 * @return
	 */
	IDiscuGroupService getDiscuGroupService();

	/**
	 * 获得讨论组聊天记录(未读取的)
	 * 
	 * @param groupId
	 *            组ID
	 * @param acceptId
	 *            接受者ID
	 * @return 讨论组信息
	 */
	List<GroupSessionMegPo> getGroupAcceptMeg(Long groupId, Long acceptId, Boolean isMobile);

	/**
	 * 获得讨论组的消息记录集合
	 * 
	 * @param page
	 *            分页类
	 * @param groupId
	 *            组ID
	 * @param selfId
	 *            当前用户ID
	 * @return 讨论组消息集合
	 * @throws Exception
	 */
	List<GroupSessionMegPo> getHisGroupSessionMeg(Page page, Long groupId, Long selfId);

	/**
	 * 得到讨论组的信息的总数
	 * 
	 * @param groupId
	 *            组ID
	 * @return 讨论组信息记录
	 */
	int getHisGroupSessionMegCount(Long userId, Long groupId);

	/**
	 * 获得聊天历史记录(两个人的聊天记录)
	 * 
	 * @param page
	 *            分页信息
	 * @param ownerId
	 *            当前用户ID
	 * @param otherId
	 *            另一个用户的ID
	 * @return 聊天的历史记录
	 * @throws Exception
	 */
	List<SessionMegPo> getHisSessionMeg(Page page, Long ownerId, Long otherId);

	/**
	 * 获得聊天历史记录的个数
	 * 
	 * @param ownerId
	 *            当前用户ID
	 * @param otherId
	 *            另外一个用户的ID
	 * @return 聊天历史记录数目
	 */
	int getHisSessionMegCount(Long ownerId, Long otherId);

	/**
	 * 打开個人聊天窗口时获得未读取的聊天信息并处理聊天信息
	 * 
	 * @param sendId
	 *            消息来源用户ID
	 * @param acceptId
	 *            接收用户ID信息
	 * @return 聊天信息列表
	 */
	List<SessionMegPo> getPersonNewMeg(Long sendId, Long acceptId, Boolean isMobile);

	/**
	 * 最近联系人
	 * 
	 * @param ownerId
	 * @param size
	 * @return
	 */
	List<Map<String, Object>> getRecentCtmGM(Long ownerId, int size);

	/**
	 * 获得联系人节点，包括组织结构人员，用于人员选择
	 * 
	 * @param user
	 * @param parentID
	 * @param exceptSelf
	 * @return
	 */
	List<Map<String, Object>> getRelations(Users user, String parentID, boolean exceptSelf);

	/**
	 * 获取用户的备注的资料
	 * 
	 * @param currentUserId
	 * 
	 *        当前用户的ID
	 * @param userId
	 *         所要查看用户udeID
	 * @return
	 */
	Map<String, Object> getUserInfo(Long currentUserId,Long userId);

	/**
	 * 根据用户ID获取用户结点
	 * 
	 * @param id
	 * @return
	 */
	Map<String, Object> getUserNodeByUId(Long id);

	/**
	 * 处理用户验证信息
	 * 
	 * @param vMsgId
	 *            用户接受的信息ID
	 * @param validateSessionMegPo
	 *            用户处理信息
	 */
	boolean proValidateSessionMessage(Long userId, Long vMsgId, int type,
			String validateSessionMeg, Long groupId);

	/**
	 * 保存讨论组聊天记录
	 * 
	 * @param sessionMegGroup
	 *            讨论组聊天信息
	 */
	GroupSessionMegPo saveGroupMeg(GroupSessionMegPo sessionMegGroup);

	/**
	 * 保存讨论组是否被读的记录
	 * 
	 * @param sessionMegGroupRead
	 *            是否被读
	 */
	void saveGroupMegRead(GroupSessionMegReadPo sessionMegGroupRead);


	/**
	 * 保存一条聊天信息
	 * 
	 * @param sessionMeg
	 *            聊天信息
	 */
	void savePersonMeg(SessionMegPo sessionMeg);

	/**
	 * 根据搜索关键字搜索用户及部门信息
	 * 
	 * @param key
	 *            搜索关键字
	 * @return 用户以及部门信息
	 */
	List<String[]> searchDepMemberList(Long userId, String key, Company company);

	List<String[]> searchRel(Users user, String key, String index);

	/**
	 * 查找联系人，用于添加
	 * 
	 * @param keyWord
	 * @param id
	 * @return
	 */
	List<Map<String, String>> searchRostersByKey(String keyWord, Long id);

	/**
	 * 发送讨论组消息
	 * 
	 * @param groupSessionMeg
	 * @param user
	 * @param groupId
	 * @param type
	 * @param date
	 */
	void sendGroupSessionMeg(String groupSessionMeg, Users user, Long groupId, int type, String date);

	/**
	 * 发送下线通知
	 * 
	 * @param ownerId
	 *            当前用户ID
	 */
	void sendOfflineNoticeMessage(Long ownerId);

	/**
	 * 更改数据库中的当前用户的在线状态，并通知当前用户的联系人，当前用户上线
	 * 
	 * @param ip
	 *            用户的IP地址
	 * 
	 * @param ownerId
	 *            当前用户的ID
	 */
	void sendOnlineNoticeMessage(String ip, Long ownerId);

	/**
	 * 发送个人消息
	 * 
	 * @param msg
	 * @param us
	 * @param acceptId
	 * @param type
	 * @param date
	 */
	void sendSessionMeg(String msg, Users us, Long acceptId, int type, String date);

	/**
	 * 发送验证信息
	 * 
	 * @param validateSessionMegPo
	 */
	int sendValidateSessionMessage(int category, Users user, Long acceptId,
			Long groupId, String validateSessionMeg);

	/**
	 * 用户组信息更新为已读状态
	 * 
	 * @param userId
	 *            当前用户
	 * @param mesasgeIds
	 *            消息ID列表
	 */
	void updateGroupSessionMessageRead(Long groupId, Long userId, List<Long> mesasgeIds);

	/**
	 * 将用户的个人消息重置为已读
	 * 
	 * @param mesasgeIds
	 *            用户ID列表
	 */
	void updateSessionMessageRead(List<Long> mesasgeIds);

	
	/**
	 * 文件传输
	 * @param acceptId
	 *           接收者的Id
	 * @param basePath
	 *           文件基本路径
	 * @param file
	 *           文件
	 * @param user
	 *          当前用户
	 * @return
	 *          返回结果
	 */
	public boolean sendPersonFile(long acceptId,String basePath,FileTransfer file,Users user)throws Exception;
	
	/**
	 * 文件传输
	 * @param acceptId
	 *           接收者的Id
	 * @param basePath
	 *           文件基本路径
	 * @param file
	 *           文件
	 * @param user
	 *          当前用户
	 * @return
	 *          返回结果
	 */
	public boolean sendGroupFile(long acceptId,String basePath,FileTransfer file,Users user)throws Exception;
	
	/**
	 * 拒绝接受文件
	 * @param acceptId
	 * @param fileName
	 * @param user
	 */
	public void rejectFileTrans(String type,long msgId,long acceptId,String fileName,Users user);
	
	public void personFileTransBefore(long msgId);
	
	public void groupFileTransBefore(long msgId,long acceptId);
	
	/**
	 * 联系人重新命名
	 * @param user
	 *          当前用户
	 * @param userId
	 *           被命名用户的ID
	 * @param newNickname
	 *           新的昵称
	 */
	public void renamePersonNickName(Users user,long userId,String newNickname);
	
	/**
	 * 
	 * @param userInfo
	 * @param userId
	 * @return
	 */
	public boolean isUserSameCompany(Users userInfo,Long userId);
}
