package apps.transmanager.weboffice.dao.impl;

import java.util.List;

import org.hibernate.Query;

import apps.transmanager.weboffice.dao.IValidateSessionMegDAO;
import apps.transmanager.weboffice.domain.ValidateSessionMegPo;

@SuppressWarnings("unchecked")
public class ValidateSessiongMegDAO extends BaseDAOImpl<ValidateSessionMegPo>
		implements IValidateSessionMegDAO {
	/**
	 * 查找没有处理的验证信息
	 */
	@Override
	public List<ValidateSessionMegPo> findUnhandleValidateSessionMeg(
			Long acceptId) {
		StringBuffer queryString = new StringBuffer(" from ValidateSessionMegPo t");
		queryString.append(" where t.acceptId=:acceptId and t.handle=false)");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("acceptId", acceptId);
		return query.list();
	}
	
	public ValidateSessionMegPo  getUnValidateSessionMegPo(Long senderId,Long acceptId,int category){
		StringBuffer queryString = new StringBuffer("from ValidateSessionMegPo t");
		queryString.append(" where t.acceptId=:acceptId and t.handle=false and t.sendId=:senderId and t.category=:category)");
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter("acceptId", acceptId);
		query.setParameter("senderId", senderId);
		query.setParameter("category", category);
		return (ValidateSessionMegPo) query.uniqueResult();
	}

}
