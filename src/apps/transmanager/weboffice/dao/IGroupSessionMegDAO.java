package apps.transmanager.weboffice.dao;

import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.domain.GroupSessionMegPo;
import apps.transmanager.weboffice.util.beans.Page;

public interface IGroupSessionMegDAO extends IBaseDAO<GroupSessionMegPo> {

	/**
	 * 查找讨论组的未读信息
	 * @param acceptId 接收者Id
	 * @param groupId 讨论组的ID
	 * @return 讨论组未读信息
	 */
	List<GroupSessionMegPo> findReadSessionMeg(Long acceptId, Long groupId);

	/**
	 * 得到组ID
	 * @param groupId 讨论组ID
	 * @return 讨论组数目
	 */
	long findCount(Long groupId);

	/**
	 * 得到讨论组的最新消息提醒
	 * @param acceptId 用户ID
	 * @return 讨论组消息提醒
	 */
	List<Map<Long, Long>> findAllGNewMegTip(Long acceptId);
	
	/**
	 * 获取最新的一条组信息
	 * @param groupId
	 *       组名称
	 * @return
	 */
	public GroupSessionMegPo getLastUnreadGroupMessage(Long groupId);
	
	
	/**
	 * 用户组信息更新为已读状态
	 * @param userId
	 *           当前用户
	 * @param mesasgeIds
	 *           消息ID列表
	 */
	public void updateGroupSessionMessageRead(Long groupId,Long userId,List<Long> mesasgeIds);
	
	
	List<GroupSessionMegPo> findUndeleteSessionMeg(Long acceptId,Long groupId,Page page,String orderColumn,String order);
	
	long findUndeleteSessionMegCount(Long acceptId,Long groupId);

}
