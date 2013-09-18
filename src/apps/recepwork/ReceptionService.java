package apps.recepwork;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Reception;
import apps.transmanager.weboffice.databaseobject.ReceptionImg;
import apps.transmanager.weboffice.databaseobject.Receptionhistory;
import apps.transmanager.weboffice.databaseobject.Receptionmanlist;
import apps.transmanager.weboffice.databaseobject.Receptionpower;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersOrganizations;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.service.dao.reception.ReceptionDAO;
import apps.transmanager.weboffice.service.dao.reception.ReceptionImgDAO;
import apps.transmanager.weboffice.service.dao.reception.ReceptionhistoryDAO;
import apps.transmanager.weboffice.service.dao.reception.ReceptionmanlistDAO;

@Component(value=ReceptionService.NAME)
public class ReceptionService implements IReceptionService {
	public final static String NAME = "receptionService";
	
	@Autowired
	private StructureDAO  structureDAO;
	@Autowired
	private ReceptionhistoryDAO receptionhistoryDAO;
	@Autowired
	private ReceptionDAO receptionDao;
	@Autowired
	private ReceptionImgDAO receptionImgDAO;
	@Autowired
	private ReceptionmanlistDAO receptionmanlistDAO;
	
	
	
	public ReceptionImgDAO getReceptionImgDAO() {
		return receptionImgDAO;
	}
	public void setReceptionImgDAO(ReceptionImgDAO receptionImgDAO) {
		this.receptionImgDAO = receptionImgDAO;
	}
	public ReceptionhistoryDAO getReceptionhistoryDAO() {
		return receptionhistoryDAO;
	}
	public void setReceptionhistoryDAO(ReceptionhistoryDAO receptionhistoryDAO) {
		this.receptionhistoryDAO = receptionhistoryDAO;
	}

	public ReceptionmanlistDAO getReceptionmanlistDAO() {
		return receptionmanlistDAO;
	}
	public void setReceptionmanlistDAO(ReceptionmanlistDAO receptionmanlistDAO) {
		this.receptionmanlistDAO = receptionmanlistDAO;
	}

	public void setReceptionDao(ReceptionDAO receptionDao) {
		this.receptionDao = receptionDao;
	}
	
	public Long save(Reception entity) throws Exception
	{
		return receptionDao.save(entity);
	}
	public void update(Reception entity) throws Exception
	{
		receptionDao.update(entity);
	}
	public void delete(Reception persistentInstance)
	{
		receptionDao.delete(persistentInstance);
	}
	public void deleteByID(Long receptionid)
	{
		receptionDao.deleteByID(receptionid);
	}
	public void deleteUsersByID(Long id)
	{
		receptionDao.deleteUsersByID(id);
	}
	public Reception findById(Long id)
	{
		return receptionDao.findById(id);
	}
	public List<Reception> findByExample(Reception instance)
	{
		return receptionDao.findByExample(instance);
	}
	public List<Reception> findByProperty(String propertyName, Object value)
	{
		return receptionDao.findByProperty(propertyName, value);
	}
	public List<Reception> findByReception(Reception reception,Users user,List<Long> canlist, int start,int count,String sort)
	{
		return receptionDao.findByReception(reception,user,canlist, start, count,sort);
	}
	public int findByReceptionsize(Reception reception,Users user,List<Long> canlist)
	{
		return receptionDao.findByReceptionsize(reception,user,canlist);
	}
	public int[] totalReception(Reception reception,Users user,List<Long> canlist)
	{
		return receptionDao.totalReception(reception,user,canlist);
	}
	public void deletePowerBytype(Integer powertype) throws Exception
	{
		receptionDao.deletePowerBytype(powertype);
	}
	public void savePower(Receptionpower receptionpower) throws Exception
	{
		receptionDao.savePower(receptionpower);
	}
	public List<Receptionpower> getPowerByuserid(Long userid) throws Exception
	{
		return receptionDao.getPowerByuserid(userid);
	}
	public Map<String,Object> getRootGroupId(Long userid) throws Exception
	{
		Map<String,Object> groupIDsAndRootCode = new HashMap<String, Object>(); 
		Long[] result=new Long[2];
		List<UsersOrganizations> list = structureDAO.findUsersOrganizationsByUserId(Long.valueOf(userid.intValue()));
		Organizations groupinfo=null;
		if (list!=null && list.size()>0)
		{
			groupinfo=list.get(0).getOrganization();
			result[0]=groupinfo.getId();
			String rootcode="000";
	   		rootcode=groupinfo.getOrganizecode();
	   		int index=rootcode.indexOf("-");
	   		if (index>0)
	   		{
	   			rootcode=rootcode.substring(0,index);
	   		}
	   		List<Organizations> grouplist=structureDAO.findOrganizationsByOrgProperty("organizecode", rootcode);
	   		if (grouplist!=null && grouplist.size()>0)
	   		{
	   			result[1]=grouplist.get(0).getId();
	   		}
	   		groupIDsAndRootCode.put("groupIDs", result);
	   		groupIDsAndRootCode.put("depRootCode", rootcode);
		}
		return groupIDsAndRootCode;
	}
	public Long savemanlist(Receptionmanlist entity) throws Exception
	{
		return receptionmanlistDAO.save(entity);
	}
	public void updatemanlist(Receptionmanlist entity) throws Exception
	{
		receptionmanlistDAO.update(entity);
	}
	public void deletemanlist(Receptionmanlist persistentInstance)
	{
		receptionmanlistDAO.delete(persistentInstance);
	}
	public void deleteByManID(Integer id)
	{
		receptionmanlistDAO.deleteByID(id);
	}
	public Receptionmanlist findByManId(Integer id)
	{
		return receptionmanlistDAO.findById(id);
	}
	public List<Receptionmanlist> findByManExample(Receptionmanlist instance)
	{
		return receptionmanlistDAO.findByProperty("reception",instance.getReception());
	}
	public List<Receptionmanlist> findByManProperty(String propertyName, Object value)
	{
		return receptionmanlistDAO.findByProperty(propertyName, value);
	}
	public List<Receptionmanlist> findByReceptionmanlist(Receptionmanlist receptionmanlist, int start,int count)
	{
		return receptionmanlistDAO.findByReceptionmanlist(receptionmanlist, start, count);
	}
	public Long savehistory(Receptionhistory history) throws Exception
	{
		return 	receptionhistoryDAO.save(history);
	}
	public void deletehistoryByReception(Long receptionid) throws Exception
	{
		receptionhistoryDAO.deletehistoryByReception(receptionid);
	}
	public List<Receptionhistory> gethistory(Long receptionid, int start,int count,String sort) throws Exception
	{
		return receptionhistoryDAO.gethistory(receptionid,start,count,sort);
	}
	public int gethistorysize(Long receptionid) throws Exception
	{
		return receptionhistoryDAO.gethistory(receptionid,0,500,null).size();
	}
	public Organizations getGroupByuserId(Long userid)
	{
		List<UsersOrganizations> list =structureDAO.findUsersOrganizationsByUserId(userid);
		if (list!=null)
		{
			return list.get(0).getOrganization();
		}
		return null;
	}
	
	public Organizations getRootGroupByCode(String groupcode)
	{
		List<Organizations> list=structureDAO.findOrganizationsByOrgProperty("organizecode", groupcode);
		if (list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}

	public void saveImgs(List<ReceptionImg> imgList) {
		receptionImgDAO.saveAll(imgList);
		
	}

	public List<ReceptionImg> findImgListByReceptionId(Long receptionid) {
		List<ReceptionImg> receptionImgs = null;
		try {
			 receptionImgs = receptionImgDAO.findBy("receptionId", receptionid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return receptionImgs;
	}

	public void delImgByReception(Long receptionid,String rootDir) {
		List<ReceptionImg> receptionImgs = null;
		try {
			receptionImgs = receptionImgDAO.findBy("receptionId", receptionid);
			if(receptionImgs!=null && !receptionImgs.isEmpty())
			{
				//先删除文件夹中的文件
				File imgFile = new File(rootDir+receptionImgs.get(0).getUrl());
				File imgDir = null;
				if(imgFile.exists())
				{
					imgDir = imgFile.getParentFile();
					FileUtils.deleteDirectory(imgDir);
				}
				//删除数据库中的记录
				receptionImgDAO.delByReceptionId(receptionid);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void editImgs(String rootDir, String[] imgIdArr) {
		for(int i=0;i<imgIdArr.length;i++)
		{
			Long id = Long.parseLong(imgIdArr[i]);
			try {
				ReceptionImg img = (ReceptionImg) receptionImgDAO.findBy("id", id).get(0);
				File imgFile = new File(rootDir+img.getUrl());
				if(imgFile.exists())
					imgFile.delete();
				receptionImgDAO.delete(img);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * @param structureDAO the structureDAO to set
	 */
	public void setStructureDAO(StructureDAO structureDAO) {
		this.structureDAO = structureDAO;
	}
	/**
	 * @return the structureDAO
	 */
	public StructureDAO getStructureDAO() {
		return structureDAO;
	}
	@Override
	public Organizations getOrganizationByuserId(Long userid) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Organizations getRootOrganizationByCode(String groupcode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
