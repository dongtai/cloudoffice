package apps.transmanager.weboffice.service.dao.reception;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Receptionhistory;

public interface IReceptionhistoryDAO  extends IBaseDAO<Receptionhistory>{

	public List<Receptionhistory> gethistory(Integer receptionid, int start,int count,String sort) throws Exception;
	public void deletehistoryByReception(Integer receptionid) throws Exception;
}
