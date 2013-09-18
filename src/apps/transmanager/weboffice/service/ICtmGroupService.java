package apps.transmanager.weboffice.service;

import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.dao.ICtmGroupMemberDAO;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.AddressBean;
import apps.transmanager.weboffice.domain.AddressListPo;
import apps.transmanager.weboffice.domain.CtmGroupMemberPo;
import apps.transmanager.weboffice.domain.CustomGroupPo;

public interface ICtmGroupService {

	/**
	 * 添加一个用户联系人组，需要检查是否有同名的用户联系人组，不能重复添加
	 * 
	 * @param ctmG
	 *            用户联系人组
	 * @param userList
	 *            组员集合
	 * @return 是否添加成功
	 */
	int addCtmG(CustomGroupPo ctmG, List<Users> userList);

	/**
	 * 添加一个用户联系人组，需要检查是否有同名的用户联系人组，不能重复添加
	 * 
	 * @param ownerId 用户ID
	 * @param ctmGName 分组名
	 * @param userList 要加入分组的联系人列表
	 * @return
	 */
	int addCtmG(Long ownerId, String ctmGName, List<Users> userList);

	/**
	 * 将用户添加至分组
	 * 
	 * @param ownerId
	 *            用户ID
	 * @param rosterId
	 *            联系人ID
	 * @param gId
	 *            分组ID
	 * @return 1 成功
	 */
	int addCtmGM(Long ownerId, Long rosterId, Long gId);

	/**
	 * 给用户联系人组添加成员(批量)
	 * 
	 * @param ctmG
	 *            用户联系人组
	 * @param userList
	 *            成员集合
	 */
	int addCtmGMList(CustomGroupPo ctmG, List<Users> userList);

	/**
	 * 获得在线用户
	 * 
	 * @param ownerId
	 *            当前用户ID
	 * @return
	 */
//	Map<String, Object> getCtmGMOnline(Long ownerId);

	/**
	 * 给用户自定义组添加成员(批量)
	 * 
	 * @param gId
	 *            用户自定义组id
	 * @param beanList
	 *            前台返回的bean列表
	 */
	int addCtmGMListWithBean(Long gId, List<AddressBean> beanList);

	/**
	 * 添加外部联系人，一般都是单个添加
	 * 
	 * @return 成功或失败代码
	 */
	int addCtmGMOuter(AddressBean addressBean);

	/**
	 * 添加联系人分组
	 * 
	 * @param ownerId
	 *            用户ID
	 * @param ctmGName
	 *            分组名
	 * @param userList
	 *            要加入分组的联系人列表
	 * @param isSystem
	 * @param isDefault
	 * @return
	 */
	void addSystemCtmG(Long ownerId, String ctmGName, boolean isSystem,
			boolean isDefault);

	void addSystemCtmGs(Long ownerId);
	/**
	 * 为用户联系人组添加一个成员，如果用户已经存在于联系人组内（范围为用户所有的联系人组），将不能添加
	 * 
	 * @param ctmGM
	 *            组成员
	 * @return 是否添加成功
	 */
//	int addCtmGM(CtmGroupMemberPo ctmGM);

	/**
	 * 删除一个用户联系人组,包括外部联系人,直接删除组下所有联系人 存在问题
	 * 
	 * @deprecated
	 * @param ctmGId
	 *            联系人组ID
	 */
	void delCtmG(Long gId);
	
	/**
	 * 删除分组,分组下的联系人移动到默认分组
	 * 
	 * @param ownerId
	 *            用户ID
	 * @param gId
	 *            分组ID
	 * @return
	 */
	int delCtmG(Long ownerId, Long gId);
	
	/**
	 * 删除一个联系人组成员
	 * 
	 * @param ownerId
	 * @param userId
	 * @param gId
	 */
	void delCtmGM(Long ownerId, Long userId, Long gId);
	void delCtmGM(Long ownerId, Long userId, Long gId, String idFix);
	/**
	 * 删除一个联系人组成员
	 * 
	 * @param ownerId
	 *            当前用户ID
	 * @param idString
	 *            树节点ID保存的信息值：ctmgm-1-2,outer-1-2,1标识在哪个组，2标识用户id，或外部联系人真实ID。
	 */
	void delCtmGM(Long ownerId, String idString);
	/**
	 * 删除自定义组外部联系人和内部联系人
	 * 
	 * @param ownerId
	 *            拥有者ID
	 * @param idList
	 *            内部联系人和外部联系人标示和id
	 */
	void delCtmGMInAndOut(Long ownerId, List<String> idList);
	
	/**
	 * 删除联系人组指定ID的成员
	 * 
	 * @param idList
	 *            联系人组成员ID列表
	 */
	void delCtmGMs(Long ownerId, List<String> uidList);

	/**
	 * 编辑用户联系人组的信息
	 * 
	 * @param ctmG
	 *            用户联系人组
	 * @param userList
	 *            用户联系人组成员集合
	 * @return 编辑信息
	 */
	int editCtmG(CustomGroupPo ctmG, List<Users> userList);
	/**
	 * 编辑联系人分组
	 * 
	 * @param ownerId
	 *            用户ID
	 * @param gId
	 *            分组ID
	 * @param ctmGName
	 *            分组名
	 * @param userList
	 *            要加入分组的联系人列表
	 * @return
	 */
	int editCtmG(Long ownerId, Long gId, String ctmGName, List<Users> userList);
	/**
	 * 获得用户的所有联系人
	 * 
	 * @param ownerId
	 *            用户ID
	 * @return 在线用户列表和离线用户列表
	 */
	Map<String, List<CtmGroupMemberPo>> getAllCtmGM(Long ownerId);
	/**
	 * 获得用户的所有联系人,包括外部联系人
	 * 
	 * @param ownerId
	 *            用户ID
	 */
	List<AddressBean> getAllCtmGMBean(Long ownerId);

	List<Map<String, Object>> getAllCtmGMList(Long ownerId);

	/**
	 * 获得用户的所有联系人,不包括外部联系人
	 * 
	 * @param ownerId
	 *            用户ID
	 */
	List<Users> getAllCtmGMUsersList(Long ownerId);

	/**
	 * 获得单个联系人分组
	 * 
	 * @param ownerId
	 *            用户ID
	 * @param ctmGName
	 *            分组名
	 * @return
	 */
	CustomGroupPo getCtmG(Long ownerId, String ctmGName);

	/**
	 * 获得用户的联系人组
	 * 
	 * @param ownerId
	 *            用户ID
	 * @return 用户所有的联系人组
	 */
	List<CustomGroupPo> getCtmGList(Long ownerId);

	/**
	 * 获得联系人
	 * 
	 * @param ownerId
	 * @param userId
	 * @return
	 */
	CtmGroupMemberPo getCtmGM(Long ownerId, Long userId);
	
	/**
	 * 获得联系人
	 * 
	 * @param ownerId
	 * @param userId
	 * @return
	 */
	CtmGroupMemberPo getCtmGMById(Long id);

	/**
	 * 通过用户ID和所有者ID获得用户在联系人组中的ID
	 * 
	 * @param userId
	 * @param ownerId
	 * @param gId
	 * @return
	 */
	CtmGroupMemberPo getCtmGM(Long ownerId, Long userId, Long gId);

	/**
	 * 获得用户联系人组成员（成员需要区分在线和非在线状态，同时获取在线用户ID集合）
	 * 
	 * @param gId
	 *            联系人组ID
	 * @return 联系人组内成员和在线用户ID集合
	 */
	Map<String, Object> getCtmGMByGId(Long gId);

	/**
	 * 得到联系人组下的用户
	 * 
	 * @param gId
	 *            用户组Id
	 * @return
	 */
	List<Long> getCtmGMIdByGId(Long gId);

	/**
	 * 获得指定分组内部联系人
	 * 
	 * @param gId
	 *            分组ID
	 * @return 内部联系人列表
	 */
	List<Users> getCtmGMInByGId(Long gId);

	/**
	 * 获得在线用户
	 * 
	 * @param ownerId
	 *            当前用户ID
	 * @return
	 */
	List<CtmGroupMemberPo> getCtmGMOnlineList(Long ownerId);

	/**
	 * 获得指定分组外部联系人
	 * 
	 * @param gId
	 *            分组ID
	 * @return 外部联系人列表
	 */
	List<AddressListPo> getCtmGMOutByGId(Long gId);

	List<Map<String, Object>> getCtmGNodeList(Long ownerId, String uId);

	List<Map<String, Object>> getCtmGOrMNode(Long userId, String pId, String idFix);

	List<CtmGroupMemberPo> getOnlineCtmGMPoList(Long userId);

	/**
	 * 获得默认联系人分组
	 * 
	 * @param ownerId
	 * @return
	 */
	CustomGroupPo getSystemDefaultCtmG(Long ownerId);

	/**
	 * 获得联系人所属分组ID ydm
	 * 
	 * @param userId
	 *            联系人ID
	 * @param ownerId
	 *            用户ID
	 * @return
	 */
	Long getUserCtmGId(Long userId, Long ownerId);

	/**
	 * 检查是否此名字已经被其他用户联系人组使用了，验证同名用（排除本用户组）
	 * 
	 * @param gId
	 *            用户联系人组ID
	 * @param name
	 *            组名
	 * @return 验证信息
	 */
	// boolean isCtmGExist(Long gId, String name);

	/**
	 * 根据组名和所属用户来判断是否存在同名联系人组
	 * 
	 * @param ctmG
	 *            联系人组
	 * @return 验证信息
	 */
	// boolean isCtmGExist(CustomGroupPo ctmG);

	/**
	 * 判断分组是否已存在
	 * 
	 * @param ctmG
	 * @param self
	 * @return
	 */
	boolean isCtmGExist(CustomGroupPo ctmG);

	/**
	 * 根据组名和所属用户判断是否存在相同组名的组
	 * 
	 * @param ctmG
	 * @return 是否存在组
	 */
	boolean isCtmGExistExceptSelf(CustomGroupPo ctmG);

	/**
	 * 用户是否存在于用户联系人中
	 * 
	 * @param ownerId
	 * @param memberId
	 * @return
	 */
	boolean isRosterExist(Long ownerId, Long memberId);

	List<String[]> searchCtmGMList(Long ownerId, String key);

	/**
	 * 通过关键字搜索联系人
	 * 
	 * @param key
	 * @param ownerId
	 * @return
	 */
	List<CtmGroupMemberPo> searchCtmGMList(String key, Long ownerId);

	/**
	 * 获取当前用户的所有联系人
	 * @param ownerId
	 * @return
	 */
	public List<CtmGroupMemberPo> getAllCtmGMByOwnerId(Long ownerId);
	
	public ICtmGroupMemberDAO getCtmGroupMemberDAO();
}
