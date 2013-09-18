package apps.transmanager.weboffice.service.dao.reception;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Reception;
import apps.transmanager.weboffice.databaseobject.Receptionpower;

public interface IReceptionDAO extends IBaseDAO<Reception>{

	public void delete(Reception persistentInstance);
	public void deleteByID(Integer id);
	public Reception findById(Integer id);
	public List findByExample(Reception instance);
	public List findByProperty(String propertyName, Object value);
	public List findByReception(Reception reception, int start,int count,String sort);
	public int findByReceptionsize(Reception reception);
	public int[] totalReception(Reception reception);
	
	public void deletePowerBytype(Integer powertype) throws Exception;
	public void savePower(Receptionpower receptionpower) throws Exception;
	public List<Receptionpower> getPowerByuserid(Integer userid) throws Exception;
}
