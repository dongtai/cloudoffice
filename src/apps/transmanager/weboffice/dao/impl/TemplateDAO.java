package apps.transmanager.weboffice.dao.impl;



import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import apps.transmanager.weboffice.dao.ITemplateDAO;
import apps.transmanager.weboffice.domain.TemplatePo;

public class TemplateDAO extends BaseDAOImpl<TemplatePo> implements ITemplateDAO{

	@Override
	public List<TemplatePo> findAll(long UserId,Long rootOrgId) {
		Criteria criteria=getSession().createCriteria(TemplatePo.class);
		       if( null != rootOrgId){
		    	   criteria.add(Restrictions.or(Restrictions.or(Restrictions.eq("type", 1), Restrictions.eq("companyId", rootOrgId)), Restrictions.eq("userId", UserId)));
		       }else{
		    	   criteria.add(Restrictions.or(Restrictions.eq("type", 1), Restrictions.eq("userId", UserId)));
		       }
		       criteria.addOrder(Order.asc("createDate"));
			return  criteria.list();
	}
	
	@Override
	public TemplatePo findByUserId(long UserId) {
		Criteria criteria=getSession().createCriteria(TemplatePo.class);
		    	   criteria.add(Restrictions.eq("userId", UserId));
		       criteria.addOrder(Order.asc("createDate"));
			return  (TemplatePo) criteria.uniqueResult();
	}

	@Override
	public TemplatePo fintByCompanyId(Long rootOrgId) {
		Criteria criteria=getSession().createCriteria(TemplatePo.class);
	       criteria.add(Restrictions.eq("companyId", rootOrgId));
	       criteria.addOrder(Order.asc("createDate"));
		return  (TemplatePo) criteria.uniqueResult();
	}
	
	
}
