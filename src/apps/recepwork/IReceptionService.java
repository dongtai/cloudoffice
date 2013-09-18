package apps.recepwork;

import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Reception;
import apps.transmanager.weboffice.databaseobject.ReceptionImg;
import apps.transmanager.weboffice.databaseobject.Receptionhistory;
import apps.transmanager.weboffice.databaseobject.Receptionmanlist;
import apps.transmanager.weboffice.databaseobject.Receptionpower;
import apps.transmanager.weboffice.databaseobject.Users;


public interface IReceptionService {

	public Long save(Reception entity) throws Exception;
	public void update(Reception entity) throws Exception;
	public void delete(Reception persistentInstance);
	public void deleteByID(Long id);
	public void deleteUsersByID(Long id);
	public Reception findById(Long id);
	public List<Reception> findByExample(Reception instance);
	public List<Reception> findByProperty(String propertyName, Object value);
	public List<Reception> findByReception(Reception reception,Users user,List<Long> canlist, int start,int count,String sort);
	public int findByReceptionsize(Reception reception,Users user,List<Long> canlist);
	public int[] totalReception(Reception reception,Users user,List<Long> canlist);
	
	
	
	public Long savemanlist(Receptionmanlist entity) throws Exception;
	public void updatemanlist(Receptionmanlist entity) throws Exception;
	public void deletemanlist(Receptionmanlist persistentInstance);
	public void deleteByManID(Integer id);
	public Receptionmanlist findByManId(java.lang.Integer id);
	public List<Receptionmanlist> findByManExample(Receptionmanlist instance);
	public List<Receptionmanlist> findByManProperty(String propertyName, Object value);
	public List<Receptionmanlist> findByReceptionmanlist(Receptionmanlist receptionmanlist, int start,int count);

	public void deletePowerBytype(Integer powertype) throws Exception;
	public void savePower(Receptionpower receptionpower) throws Exception;
	public List<Receptionpower> getPowerByuserid(Long userid) throws Exception;
	public Map<String, Object> getRootGroupId(Long userid) throws Exception;
	
	public Long savehistory(Receptionhistory history) throws Exception;
	public void deletehistoryByReception(Long receptionid) throws Exception;
	public List<Receptionhistory> gethistory(Long receptionid,int start,int count,String sort) throws Exception;
	public int gethistorysize(Long receptionid) throws Exception;
	
	public Organizations getOrganizationByuserId(Long userid);
	public Organizations getRootOrganizationByCode(String groupcode);
	/**
	 * 保存接待上传的图片
	 * @param imgList 图片列表
	 */
	public void saveImgs(List<ReceptionImg> imgList);
	/**
	 * 获得这次上传的图片
	 * @param receptionid 接待ID
	 * @return 图片列表
	 */
	public List<ReceptionImg> findImgListByReceptionId(Long receptionid);
	/**
	 * 删除上传的图片
	 * @param receptionid 接待ID
	 * @param rootDir 根目录
	 */
	public void delImgByReception(Long receptionid, String rootDir);
	/**
	 * 编辑接待时需要删除的图片
	 * @param rootDir 根目录
	 * @param imgIdArr 图片ID数组
	 */
	public void editImgs(String rootDir, String[] imgIdArr);
	
	
	
}
