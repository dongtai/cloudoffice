package apps.transmanager.weboffice.service.dao.reception;

import apps.transmanager.weboffice.databaseobject.ReceptionImg;


public interface IReceptionImgDAO extends IBaseDAO<ReceptionImg>{

	/**
	 * 根据接待ID删除这次的接待上传图片
	 * @param receptionid 接待ID
	 */
	void delByReceptionId(Integer receptionid);

}
