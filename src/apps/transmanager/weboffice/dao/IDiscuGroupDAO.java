package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.domain.DiscuGroupPo;

public interface IDiscuGroupDAO extends IBaseDAO<DiscuGroupPo> {

	/**
	 * 获得参与的讨论组的集合
	 * @param id 参与者ID
	 * @return 讨论组集合
	 */
	List<DiscuGroupPo> findByMbId(Long id);
	
	/**
	 * 根据管理员ID获取讨论组
	 * @param ownerId
	 * @return
	 */
	List<DiscuGroupPo> findByOwner(Long ownerId);
	
	List<DiscuGroupPo> searchDiscuGroupPoByKey(String key,String cols[],Long userId);

	List<DiscuGroupPo> searchByKey(String key);

	DiscuGroupPo findByNameAndOwner(String gname, Long ownerId);

}
