package apps.transmanager.weboffice.service;

import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.dao.IAddressListDAO;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.AddressListPo;
import apps.transmanager.weboffice.util.beans.Page;

public interface IAddressListService {
	
	public IAddressListDAO getAddresslistDAO();

	/**
	 * 获得指定分组外部联系人
	 * 
	 * @param groupId
	 *            自定义分组ID
	 * @return 外部联系人列表
	 */
	public List<AddressListPo> findByGroupIdOuter(Long groupId);

	/**
	 * 获得指定分组内部联系人
	 * 
	 * @param groupId
	 *            自定义分组id
	 * @return 内部联系人列表
	 */
	public List<Users> findByGroupUser(Long groupId);

	/**
	 * 通过id获取外部联系人的信息
	 */
	public AddressListPo getAddressById(Long id);

	/**
	 * 获得联系人，需要分页，排序
	 * 
	 * @param id
	 *            组ID
	 * @param key
	 *            搜索关键字
	 * @param sort
	 *            排序字段
	 * @param order
	 *            排序顺序
	 * @param page
	 *            分页类
	 * @return 内部+外部联系人Map
	 */
	Map<String, Object> getAddrListPage(Long id, Long companyId, String type,
			String sort, String order, Page page);

	Map<String, Object> getAddrListPage(Long id, Long companyId, String type,
			String sort, String order, Page page, int usertype);
	/**
	 * 获取外部联系人
	 * @param groupid 外部联系人所在的组
	 * @param userid 拥有者
	 * @return
	 */
	public List<AddressListPo> getOutContacts(Long groupid, Long userid);
	/**
	 * 获得用户自定义组的成员（成员需要区分在线和非在线状态，同时获取在线用户ID集合）
	 * 
	 * @param groupId
	 *            自定义组的ID
	 * @return 自定义组内成员和在线用户ID集合
	 */
	Map<String, Object> getCtmGMByGroupId(Long groupId);

	List<Map<String, Object>> getCtmGOrMNode(Long userId, String pId,
			boolean isread, boolean isSign);

	ICtmGroupService getCtmGroupService();

	Map<String, Object> getCustomGroupMap(Long id);

	/**
	 * 根据当前用户的ID获取所对应的人员List
	 */
	Map<String, Object> getFilterMemberList(Long parentID, Long userId,
			String sortName, String order);

	/**
	 * 根据组的父ID获取子组List和成员List
	 * 
	 * @param parentID
	 *            父ID
	 * @parma companyId 公司ID
	 * @param sortName
	 *            排序字段
	 * @param order
	 *            排序方式
	 * @return 子组List和成员List
	 */
	Map<String, Object> getGroupAndMemberList(Long parentID, Long companyId,
			String sortName, String order);

	Map<String, Object> getGroupAndMemberList(Long parentID, Long companyId,
			String sortName, String order, int usertype);

	/**
	 * 根据当前用户获取组织
	 * 
	 * @param userId
	 * @return
	 */
	public Long getGroupByUserId(Long userId);

	public Long getRootDepByUserId(Long id);

	/**
	 * 获取当前用户的根组织
	 * 
	 * @param userId
	 * @return
	 */
	public Long getRootGroupByUserId(Long userId);

	Map<String, Object> getSearchAllUserMap(String keyword);

	/**
	 * 签批搜索联系人功能接口
	 * 
	 * @param keyword
	 *            关键字
	 * @return
	 */
	Map<String, Object> getSearchResultMap(Long companyId, String keyword);

	/**
	 * 签批搜索联系人功能接口
	 * 
	 * @param keyword
	 *            关键字
	 * @return
	 */
	Map<String, Object> getSearchSharedResultMap(Long userid, Long companyId,
			String keyword);

	/**
	 * 根据组的父ID获取子组List和成员List
	 * 
	 * @param parentID
	 *            父ID
	 * @param orgid
	 * @param sortName
	 *            排序字段
	 * @param order
	 *            排序方式
	 * @return 子组List和成员List
	 */
	Map<String, Object> getSharedGroupAndMemberList(Long userid, Long parentID,
			Long orgid, String sortName, String order);

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public Users getUser(Long userId);

	/**
	 * 从文件导入通讯录，然后删除文件
	 * 
	 * @param userInfo
	 *            当前用户
	 * @param filePath
	 *            文件路径
	 * @param fieldList
	 *            映射字段
	 * @throws Exception
	 * 
	 */
	void importAddr(List<String> fieldList, String filePath, Users user)
			throws Exception;

	/**
	 * 根据搜索关键字搜索用户信息
	 * 
	 * @param key
	 *            搜索关键字
	 * @return 用户以及部门信息
	 */
	// List<DepMemberPo> searchDepMemberList(Long companyId, String key);

	List<String[]> searchRel(Users user, String key);
	
	void update(AddressListPo po);
}
