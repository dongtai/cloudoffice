package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.domain.ValidateSessionMegPo;

public interface IValidateSessionMegDAO extends IBaseDAO<ValidateSessionMegPo> {

	/**
	 * 查找验证信息
	 * @param acceptId 接收者Id
	 * @return 讨论组未读信息
	 */
	List<ValidateSessionMegPo> findUnhandleValidateSessionMeg(Long acceptId);
	
	ValidateSessionMegPo  getUnValidateSessionMegPo(Long senderId,Long acceptId,int category);
	
}
