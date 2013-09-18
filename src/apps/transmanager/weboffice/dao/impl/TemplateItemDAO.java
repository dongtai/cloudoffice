package apps.transmanager.weboffice.dao.impl;



import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import apps.transmanager.weboffice.dao.ITemplateItemDAO;
import apps.transmanager.weboffice.domain.TemplateItemPo;

public class TemplateItemDAO extends BaseDAOImpl<TemplateItemPo> implements ITemplateItemDAO{

	@Override
	public List<TemplateItemPo> getTemplateItemPo(Long templateId) {
		return this.getSession().createCriteria(TemplateItemPo.class).add(Restrictions.eq("template.id", templateId)).addOrder(Order.desc("createDate")).list();
	}

}
