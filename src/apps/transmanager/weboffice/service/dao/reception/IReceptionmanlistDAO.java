package apps.transmanager.weboffice.service.dao.reception;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Receptionmanlist;


public interface IReceptionmanlistDAO  extends IBaseDAO<Receptionmanlist>{

	public void delete(Receptionmanlist persistentInstance);
	public void deleteByID(Integer id);
	public Receptionmanlist findById(Integer id);
	public List findByExample(Receptionmanlist instance);
	public List findByProperty(String propertyName, Object value);
	public List findByReceptionmanlist(Receptionmanlist receptionmanlist, int start,int count);
}
