package apps.transmanager.weboffice.service;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.OnlinePo;

public interface IUserService {

	/**
	 * 更新用户信息
	 * @param userInfo 用户对象
	 */
	void updateUser(Users userInfo);
	/**
	 * 更新用户密码
	 * @param id 用户ID
	 * @param oldPwd 旧密码
	 * @param newPwd 新密码
	 */
	int updatePwd(Long id, String oldPwd, String newPwd);
	/**
	 * 根据ID获得用户信息
	 * @param userId 用户ID
	 * @return 用户信息
	 */
	Users getUserById(Long userId);
	
	/**
	 * 将用户添加进入在线列表
	 * @param onlinePo 在线用户
	 */
	void addOnline(OnlinePo onlinePo);
	
	/**
	 * 删除在线用户
	 * @param id 用户ID
	 */
	void delOnline(Long id);
    /**
     * 获取系统中的所有公司
     * @return
     */
    public List<Company> getCompanyList();
	List<Users> getUserByRole(short i, short j);
}
