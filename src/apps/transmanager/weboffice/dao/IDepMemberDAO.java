package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.domain.DepMemberPo;
import apps.transmanager.weboffice.domain.DepartmentPo;
import apps.transmanager.weboffice.util.beans.Page;

public interface IDepMemberDAO extends IBaseDAO<DepMemberPo> {

	/**
	 * 获取所有签批领导
	 * @param orgid
	 * @param order
	 * @return
	 */
	public List<DepMemberPo> findOrgUsers(Long orgid);
	/**
	 * 靠关键字进行搜索
	 * @param key 关键字
	 * @return 部门成员集合
	 */
	List<DepMemberPo> searchByKey(String key);
	
	
	List<DepMemberPo> searchByKeyAndCompany(String key, Company company);
	/**
	 * 得到部门里面的联系人
	 * @param departmentPo 部门
	 * @param page 分页类
	 * @param sort 排序关键字
	 * @param order 排序顺序
	 * @return 联系人id
	 */
	List<Long> findByDepartment(DepartmentPo departmentPo,Page page, String sort, String order);
	List<Long> findByDepartment(DepartmentPo departmentPo,Page page, String sort, String order,int usertype);
	/**
	 * 得到部门里面的联系人
	 * @param page 分页类
	 * @param sort 排序关键字
	 * @param order 排序顺序
	 * @return 联系人id
	 */
	List<Long> findByDepAll(Long companyId,Page page, String sort, String order);
	List<Long> findByDepAll(Long companyId,Page page, String sort, String order,int usertype);
	/**
	 * 根据关键字进行搜索用户信息(包括帐号、姓名、邮件名）
	 */
	List<DepMemberPo> searchUsersByKey(Long companyId,String key);
	public List<DepMemberPo> findOrgSharedUsers(Long userId, Long parentID);
	public List<DepMemberPo> searchSharedUsersByKey(Long userid, Long companyId,String keyword);

}
