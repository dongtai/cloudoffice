package apps.transmanager.weboffice.dao.impl;




import java.util.HashMap;
import java.util.Map;

import apps.transmanager.weboffice.dao.IDiscuGroupMemberPoRelationDao;
import apps.transmanager.weboffice.domain.DiscuGroupMemberPoRelationPo;


public class DiscuGroupMemberPoRelationDao extends BaseDAOImpl<DiscuGroupMemberPoRelationPo> implements IDiscuGroupMemberPoRelationDao{
	
	 public DiscuGroupMemberPoRelationPo getDiscuGroupMemberPoRelationPo(Long ownerId,long memberId){
		   Map<String,Object> propertyMap=new HashMap<String, Object>();
			propertyMap.put("ownerId", ownerId);
			propertyMap.put("memberId", memberId);
			return this.findByPropertyUnique(DiscuGroupMemberPoRelationPo.class.getCanonicalName(), propertyMap);
	   }
}
