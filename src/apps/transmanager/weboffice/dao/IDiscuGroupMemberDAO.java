package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.DiscuGroupMemberPo;

public interface IDiscuGroupMemberDAO extends IBaseDAO<DiscuGroupMemberPo> {

	/**
	 * 删除讨论组成员
	 * @param groupId 组ID
	 * @param userId 成员ID
	 */
	void delete(Long groupId, Long userId);

	/**
	 * 查找讨论组成员集合，不包含创建者
	 * @param groupId 组ID
	 * @return 讨论组成员集合
	 */
	List<DiscuGroupMemberPo> findNoOwner(Long groupId);
	
	List<Users> findGroupUser(long gId);
	
	/**
	 *  
	 * @param groupId
	 * @param memberId
	 * @return
	 */
	public List<DiscuGroupMemberPo> findGroupUser(Long groupId,long memberId);

}
