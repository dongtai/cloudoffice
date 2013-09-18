package apps.transmanager.weboffice.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apps.transmanager.weboffice.dao.IOnlineDAO;
import apps.transmanager.weboffice.dao.IUserDAO;
import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.OnlinePo;
import apps.transmanager.weboffice.service.ITalkService;
import apps.transmanager.weboffice.service.IUserService;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.both.MD5;

@Service("user2Service")
public class UserService implements IUserService{
	
	@Autowired
	private IUserDAO userDAO;
	@Autowired
    private IOnlineDAO onlineDAO;
	@Autowired
    private StructureDAO structureDAO;
	
	public void setUserDAO(IUserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	public void setOnlineDAO(IOnlineDAO onlineDAO) {
		this.onlineDAO = onlineDAO;
	}
	public void setStructureDAO(StructureDAO structureDAO)
    {
        this.structureDAO = structureDAO;
    }
	
	public void addOnline(OnlinePo onlinePo) {
		//需要检查是否存在
		boolean isExist = getExistOnline(onlinePo.getUserId());
		if(isExist)
		{
			//存在的话更新登录时间
			//onlineDAO.update(OnlinePo.class.getName(), "loginTime", "userId", onlinePo);
			return;
		}
		onlineDAO.saveOrUpdate(onlinePo);
	}
	
	/**
	 * 检查是否存在在线用户
	 * @param userId 用户ID
	 * @return 是否存在
	 */
	private boolean getExistOnline(Long userId) {
		OnlinePo onlinePo = onlineDAO.findByPropertyUnique(OnlinePo.class.getName(), "userId", userId);
		if(onlinePo!=null)
		{
			return true;
		}
		return false;
	}



	public void updateUser(Users userInfo) {
		userDAO.saveOrUpdate(userInfo);
		userDAO.syncUpdate(userInfo.getId(), userInfo.getImage1(), userInfo.getRealName(), userInfo.getRealEmail());
	}
	
	public int updatePwd(Long id,String oldPwd,String newPwd){
		//验证旧密码
		MD5 md5 = new MD5();
		Users userinfo = userDAO.findById(Users.class.getName(), id);
		if(!md5.getMD5ofStr(oldPwd).equals(userinfo.getPassW())){
			return PageConstant.VALIDATOR_NAME_FAIL;
		}
		List<String> columNames = new ArrayList<String>();
		List<String> conditions = new ArrayList<String>();
		Map<String,Object> propertyMap = new HashMap<String,Object>(); 
		columNames.add("passW");
		conditions.add("id");
		propertyMap.put("id", id);
		propertyMap.put("passW", md5.getMD5ofStr(newPwd));
		userDAO.update(Users.class.getName(), columNames, conditions, propertyMap);
		return PageConstant.VALIDATOR_NAME_SUC;
	}


	public void delOnline(Long id) {
		this.onlineDAO.deleteByProperty(OnlinePo.class.getName(), "userId", id);
		ITalkService iTalkService= (ITalkService) ApplicationContext.getInstance().getBean(TalkService.NAME);
		iTalkService.sendOfflineNoticeMessage(id);
	}
	
	public Users getUserById(Long userId) {
		return userDAO.findById(Users.class.getName(),userId);
	}
    /**
     * 获取系统中的所有公司
     * @return
     */
    public List<Company> getCompanyList()
    {
    	return structureDAO.findAll(Company.class);
    }

	public List<Users> getUserByRole(short i, short j) {
		return structureDAO.findUserByRole(i,j);
	}
	
}
