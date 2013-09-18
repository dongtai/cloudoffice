package apps.transmanager.weboffice.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.domain.SessionMegPo;
import apps.transmanager.weboffice.util.beans.Page;

public interface ISessionMegDAO extends IBaseDAO<SessionMegPo> {

	
	/**
	 * 获得历史记录
	 * @param ownerId 当前用户ID
	 * @param otherId 另外一个人的ID
	 * @param page 分页信息
	 * @return 历史记录的集合
	 */
	List<SessionMegPo> findHisRecord(Long ownerId, Long otherId, Page page);

	/**
	 * 获得历史记录的总数
	 * @param ownerId 当前用户ID
	 * @param otherId 另外一个用户ID
	 * @return 历史记录总数
	 */
	long findHisRecordCount(Long ownerId, Long otherId);
	
	
	/**
	 * 获取未读消息总数
	 * @param acceptId
	 *       接收人的ID
	 * @return
	 */
	public long getUnreadMessageCount(Long acceptId);
	
	/**
	 * 获取未读信息
	 * @param accpet
	 * @return
	 */
	public List<SessionMegPo> getUnreadMessage(Long acceptId);

	
	/**
	 * 获取最新一条未读信息
	 *     
	 * @param acceptId
	 *        当前用户ID
	 * @return
	 *      null 表示没有未读信息
	 */
	public SessionMegPo getLastestUnreadMessage(Long acceptId);
	
	
	/**
	 * 获取最新一条未读信息
	 *     
	 * @param acceptId
	 *        当前用户ID
	 * @return
	 *      null 表示没有未读信息
	 */
	public SessionMegPo getLastestUnreadMessage(Long acceptId,Long sendId);
	
	/**
	 * 获得联系人信息的最新提醒
	 * @param acceptId 接收者ID
	 * @return 最新消息提醒
	 */
	List<Map<Long, Long>> findAllPNewMegTip(Long accepId);
	
	/**
	 * 将个人信息置为已读
	 * @param mesasgeIds
	 */
	public void updateSessionMessageRead(List<Long> mesasgeIds);

	List<Map<Long, Long>> findRecentTalkRoster(Long ownerId);

	List<SessionMegPo> findHisRecord(Long ownerId, Long otherId);

	List<SessionMegPo> findHisRecordByDate(Long ownerId, Long userId, Date startDate, Date endDate);

}
