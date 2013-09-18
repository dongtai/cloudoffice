package apps.transmanager.weboffice.dao;
import java.util.List;

import apps.transmanager.weboffice.domain.AddressListPo;
import apps.transmanager.weboffice.util.beans.Page;

public interface IAddressListDAO extends IBaseDAO<AddressListPo>{
	
	/**
	 * 得到分组里面的联系人
	 * @param groupId 组ID
	 * @param page 分页类
	 * @param sort 排序关键字
	 * @param order 排序顺序
	 * @return 联系人
	 */
	List<AddressListPo> findByGroup(Long groupId,Page page,String sort, String order);

	/**
	 * 得到全部外部联系人
	 * @param ownerId 用户ID
	 * @param page 分页类
	 * @param sort 排序关键字
	 * @param order 排序顺序
	 * @return 联系人
	 */
	List<AddressListPo> findByAll(Long ownerId, Page page,String sort,String order);

	List<AddressListPo> searchByKey(String key, Long ownerId);
	
}
