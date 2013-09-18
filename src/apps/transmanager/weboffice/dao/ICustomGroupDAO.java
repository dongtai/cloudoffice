package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.domain.CustomGroupPo;

public interface ICustomGroupDAO extends IBaseDAO<CustomGroupPo>{

	/**
	 * 查找用户联系人默认分组
	 * @param ownerId
	 * @return
	 */
	CustomGroupPo findSystemDefaultCtmG(Long ownerId);

	/**
	 * 查找用户联系人中是否存在同名分组
	 * @param ownerId
	 * @param ctmGName
	 * @return
	 */
	CustomGroupPo findByOwnerAndGName(Long ownerId, String ctmGName);

	List<CustomGroupPo> findByOwner(Long ownerId);
	
	void updateGName(Long gId, String gName);
	
	void saveOrUpdateWithTransactional(CustomGroupPo customGroupPo);

}
