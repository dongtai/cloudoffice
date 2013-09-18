package apps.transmanager.weboffice.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.FlushMode;
import org.hibernate.Session;

import apps.transmanager.weboffice.dao.ICustomGroupDAO;
import apps.transmanager.weboffice.domain.CustomGroupPo;

public class CustomGroupDAO extends BaseDAOImpl<CustomGroupPo> implements ICustomGroupDAO {
	@Override
	public CustomGroupPo findSystemDefaultCtmG(Long ownerId) {
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("system", true);
		propertyMap.put("defaultOrg", true);
		propertyMap.put("userId", ownerId);
		return findByPropertyUnique(CustomGroupPo.class.getName(), propertyMap);
	}
	
	@Override
	public CustomGroupPo findByOwnerAndGName(Long ownerId, String ctmGName) {
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("userId", ownerId);
		propertyMap.put("name", ctmGName);
		return findByPropertyUnique(CustomGroupPo.class.getName(), propertyMap);
	}
	
	@Override
	public List<CustomGroupPo> findByOwner(Long ownerId) {
		return findByProperty(CustomGroupPo.class.getName(), "userId", ownerId);
	}

	@Override
	public void saveOrUpdateWithTransactional(CustomGroupPo customGroupPo) {
		Session session = this.getSession();
		session.setFlushMode(FlushMode.COMMIT);
		session.beginTransaction();
		session.saveOrUpdate(customGroupPo);
		session.getTransaction().commit();
	}

	@Override
	public void updateGName(Long gId, String gName) {
		List<String> conditions = new ArrayList<String>();
		conditions.add("id");
		List<String> columNames = new ArrayList<String>();
		columNames.add("name");
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("id", gId);
		propertyMap.put("name", gName);
		update(CustomGroupPo.class.getName(), columNames, conditions, propertyMap);
	}
}
