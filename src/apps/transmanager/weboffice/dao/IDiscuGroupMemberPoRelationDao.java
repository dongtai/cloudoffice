package apps.transmanager.weboffice.dao;


import apps.transmanager.weboffice.domain.DiscuGroupMemberPoRelationPo;

public interface IDiscuGroupMemberPoRelationDao extends IBaseDAO<DiscuGroupMemberPoRelationPo>{
	
	 public DiscuGroupMemberPoRelationPo getDiscuGroupMemberPoRelationPo(Long ownerId,long memberId);
	
}
