package apps.transmanager.weboffice.service;

import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.DiscuGroupPo;

public interface IDiscuGroupService {
	/**
	 * 添加讨论组的成员
	 * 
	 * @param discuG
	 *            讨论组
	 * @param userList
	 *            成员集合
	 */
	void addDiscuGMList(DiscuGroupPo discuG, List<Users> userList);

	/**
	 * 添加一个用户自定义的讨论组（需要重名验证，和添加成员）
	 * 
	 * @param discuG
	 *            讨论组
	 * @param userList
	 *            成员用户集合
	 * @return 添加结果
	 */
	Long addDiscuGroup(String gName, Users owner, List<Users> userList);
	
	/**
	 * 删除一个讨论组用户
	 * 
	 * @param groupId
	 *            组ID
	 * @param userId
	 *            用户ID
	 */
	void delDiscuGM(Long groupId, Long userId);
	
	/**
	 * 删除一个讨论组
	 * 
	 * @param id
	 *            讨论组ID
	 * @return 
	 */
	int delDiscuGroup(Long id);
	
	/**
	 * 编辑讨论组（需要重名验证，更新成员）
	 * 
	 * @param discuG
	 *            讨论组
	 * @param userList
	 *            成员用户集合
	 * @return 更新结果
	 */
	int editDiscuGroup(Long gId, String gName, Users owner, List<Users> userList);
	
	String getDiscuGMailStr(Long gId);
	
	/**
	 * 获得用户讨论组的成员集合，包含在线用户列表的集合
	 * 
	 * @param gId
	 *            讨论组的ID
	 * @param self
	 *            是否要求加入当前用户本身
	 * @return 讨论组成员集合
	 */
	Map<String, Object> getDiscuGMList(Long gId, Boolean self);
	
	List<Map<String, Object>> getDiscuGMNodeList(Long groupId);

	DiscuGroupPo getDiscuGroupById(Long id);

	List<DiscuGroupPo> getDiscuGroupList(Long id, boolean isStandaloneUser);
	
	Map<String, Object> getDiscuGroupNode(Long gId);

	List<Map<String, Object>> getDiscuGroupNodeList(Users user);


	/**
	 * 搜索讨论组
	 * 
	 * @param userId
	 * @param key
	 * @param b
	 * @return
	 */
	List<String[]> searchDiscuGList(Long userId, String key, boolean b);

	/**
	 * 查找讨论组，用于添加
	 * 
	 * @param key
	 * @param userId
	 * @return
	 */
	List<Map<String, String>> searchGroupsByKey(String key, Long userId);
}
