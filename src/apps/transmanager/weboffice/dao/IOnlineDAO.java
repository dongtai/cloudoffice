package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.domain.OnlinePo;

public interface IOnlineDAO extends IBaseDAO<OnlinePo>{

	/**
	 * 获取在线ID集合
	 * @return 在线的用户的ID的集合
	 */
	List<Long> findAllUserId();
	
	/**
	 * 获取当前用户的在线联系人的IDs
	 * 
	 * @param ownerId
	 * @return
	 */
	public List<Long> findAllOnlineUserIdsByOwnerId(Long ownerId);
	
	/**
	 * 获取当前用户的在线联系人
	 * 
	 * @param ownerId
	 * @return
	 */
	public List<OnlinePo> findAllOnlineUserByOwnerId(Long ownerId);
}
