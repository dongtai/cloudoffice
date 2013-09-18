package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.domain.DepartmentPo;

public interface IDepartmentDAO extends IBaseDAO<DepartmentPo>{

	/**
	 * 根据关键字进行搜索组名
	 */
	List<DepartmentPo> searchOrgListByKey(Long companyId,String key);

	/**
	 * 根据组织的sortNum排序的子组织
	 * @param parentId
	 * @return
	 */
	public List<DepartmentPo> findOrgByParentId(Long parentId);

	List<Organizations> findOrgByUserId(Long userId);
}
