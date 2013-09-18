package apps.transmanager.weboffice.dao;
import java.util.List;

import apps.transmanager.weboffice.domain.TemplatePo;

public interface ITemplateDAO extends IBaseDAO<TemplatePo>{
	
	public List<TemplatePo> findAll(long UserId,Long rootOrgId);
	
	public TemplatePo fintByCompanyId(Long rootOrgId);
	
	public TemplatePo findByUserId(long UserId);
}
