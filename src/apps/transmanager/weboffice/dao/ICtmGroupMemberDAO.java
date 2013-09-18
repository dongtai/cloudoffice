package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.CtmGroupMemberPo;
import apps.transmanager.weboffice.util.beans.Page;

public interface ICtmGroupMemberDAO extends IBaseDAO<CtmGroupMemberPo>{
	
	/**
	 * 得到分组里面的联系人
	 * @param groupId 组ID
	 * @param key 关键字
	 * @param page 分页类
	 * @param sort 排序关键字
	 * @param order 排序顺序
	 * @return 联系人
	 */
	List<Long> findByGroup(Long groupId,Page page,String sort, String order);

    /**
     * 查找所有分组的联系人
     * @param key 查询关键字
     * @param page 分页类
     * @param order 排序字段
     * @param sort 排序顺序
     * @return 联系人
     */
	
	List<Long> findByAll(Long ownerId, Page page, String sort, String order);
	
	
	/**
	 * 获得所有分组联系人用户ID
	 * @param groupId 组ID
	 * @return 联系人ID
	 */
	public List<Long> findByGroup(Long groupId);
	
	/**
	 */
	List<Users> findRostersByOwner(Long ownerId) ;
	
	List<Users> findByGroupUser(Long groupId);

	List<CtmGroupMemberPo> searchByKey(String key, Long ownerId);
	
	List<CtmGroupMemberPo> findRosterListByOwner(Long ownerId, Long rosterId);

	CtmGroupMemberPo findRosterByOwner(Long ownerId, Long rosterId);
	
	CtmGroupMemberPo findRosterByOwner(Long ownerId, Long rosterId, Long groupId);
	
}
