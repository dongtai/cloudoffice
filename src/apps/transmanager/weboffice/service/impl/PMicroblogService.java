package apps.transmanager.weboffice.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.dao.IPMicroblogMegDAO;
import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.PMicroblogMegPo;
import apps.transmanager.weboffice.service.IPMicroblogService;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.dao.StructureDAO;
import apps.transmanager.weboffice.util.beans.Page;

@Component(value=PMicroblogService.NAME)
public class PMicroblogService implements IPMicroblogService {

	public static final String NAME = "PMicroblogService";
	
	@Autowired
	private IPMicroblogMegDAO pMicroblogMegDAO;

	@Autowired
    private StructureDAO structureDAO;

	public List<PMicroblogMegPo> getGroupsAndNewMeg(Long id) {
		List<PMicroblogMegPo> pmblogMegList = new ArrayList<PMicroblogMegPo>();
		List<Groups> groupList = structureDAO.findGroupsByUserId(id);
		if(groupList==null || groupList.isEmpty())
			return null;
		for(Groups group : groupList)
		{
			PMicroblogMegPo pmblogMeg = pMicroblogMegDAO.findLastNew(group.getId());
			if(pmblogMeg==null){
				pmblogMeg = new PMicroblogMegPo();
				pmblogMeg.setGroups(group);
			}
			pmblogMegList.add(pmblogMeg);
		}
		return pmblogMegList;
	}
	
	public Map<String, Object> getLastestGroupAndMember(Long userId) {
		Groups group = getLastestGroup(userId);
		if(group==null)
		{
			return null;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Users> memberList = structureDAO.findUsersByGroupId(group.getId(), false);
		resultMap.put("group", group);
		resultMap.put("memberList", memberList);
		resultMap.put("baseGroupURL", WebConfig.groupPortrait);
		resultMap.put("baseURL", WebConfig.userPortrait);
		return resultMap;
	}

	public Map<String, Object> getGroupAndMember(Long groupId) {
		Groups group = getGroupById(groupId);
		if(group==null)
			return null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Users> memberList = structureDAO.findUsersByGroupId(group.getId(), false);
		resultMap.put("group", group);
		resultMap.put("memberList", memberList);
		resultMap.put("baseGroupURL", WebConfig.groupPortrait);
		resultMap.put("baseURL", WebConfig.userPortrait);
		return resultMap;
	}
	
	public void add(PMicroblogMegPo pmblogMeg) {
		pMicroblogMegDAO.saveOrUpdate(pmblogMeg);
		
	}

	public List<PMicroblogMegPo> getGroupBlog(Long groupId,Long userId, Page page) {
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		Groups groups = new Groups();
		groups.setId(groupId);
		propertyMap.put("parent", null);
		propertyMap.put("groups",groups);
		if(userId!=null)
		{
			Users sendUser = new Users();
			sendUser.setId(userId);
			propertyMap.put("sendUser", sendUser);
		}
		List<PMicroblogMegPo> pmBlogList = null;
		pmBlogList = pMicroblogMegDAO.findGroupBlog(groupId,userId,page,"addDate", "desc");
		return pmBlogList;
	}

	public void del(Long blogId, Users user) {
		PMicroblogMegPo pmblog = pMicroblogMegDAO.findById(PMicroblogMegPo.class.getName(), blogId);
		if(pmblog!=null && pmblog.getSendUser().getId().longValue()==user.getId().longValue())
		{
			pMicroblogMegDAO.deleteByProperty(PMicroblogMegPo.class.getName(), "parent.id", blogId);
			pMicroblogMegDAO.deleteById(PMicroblogMegPo.class.getName(), blogId);
		}
		
	}

	public List<PMicroblogMegPo> getBlogBack(Long parentId) {
		List<PMicroblogMegPo> pmBlogList = pMicroblogMegDAO.findByProperty(PMicroblogMegPo.class.getName(), "parent.id", parentId,"addDate", "desc");
		return pmBlogList;
	}
	
	public List<PMicroblogMegPo> getMicroBlog(Long blogID) {
        List<PMicroblogMegPo> pmBlogList = pMicroblogMegDAO.findByProperty(PMicroblogMegPo.class.getName(), "id", blogID,"addDate", "desc");
        return pmBlogList;
    }

	public Groups getLastestGroup(Long userId) {
		List<Groups> groupList = structureDAO.findGroupsByUserId(userId);
		if(groupList==null || groupList.isEmpty())
			return null;
		return groupList.get(0);
	}
	
	public List<PMicroblogMegPo> searchBlog(Long groupId, String key, Page page) {
		List<PMicroblogMegPo> pmblogList = pMicroblogMegDAO.findByKey(groupId,key,page);
		return pmblogList;
	}
	
	public List<Users> getMemberList(Long groupId) {
		List<Users> memberList = structureDAO.findUsersByGroupId(groupId, false);
		return memberList;
	}

	public Groups getGroupById(Long groupId) {
		Groups group = structureDAO.findGroupById(groupId);
		return group;
	}
	
	public List<Groups> getGroupList(Long userId) {
		List<Groups> groupList = structureDAO.findGroupsByUserId(userId);
		return groupList;
	}

	public void setpMicroblogMegDAO(IPMicroblogMegDAO pMicroblogMegDAO) {
		this.pMicroblogMegDAO = pMicroblogMegDAO;
	}

	public void setStructureDAO(StructureDAO structureDAO) {
		this.structureDAO = structureDAO;
	}



	
	
}
