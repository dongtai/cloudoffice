package apps.transmanager.weboffice.dao;
import java.util.List;

import apps.transmanager.weboffice.domain.TemplateItemPo;

public interface ITemplateItemDAO extends IBaseDAO<TemplateItemPo>{
	
	public List<TemplateItemPo> getTemplateItemPo(Long templateId);
}
