package apps.transmanager.weboffice.service;

import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.databaseobject.Groups;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.PMicroblogMegPo;
import apps.transmanager.weboffice.util.beans.Page;

public interface IPMicroblogService {

	/**
	 * 获得当前用户参与项目组，以及当前最新的一条留言
	 * @param id 当前用户ID
	 * @return 项目组和最新一条留言
	 */
	List<PMicroblogMegPo> getGroupsAndNewMeg(Long id);

	/**
	 * 获得当前用户最近参与的项目组
	 * @param userId 用户ID
	 * @return 项目组
	 */
	Groups getLastestGroup(Long userId);

	/**
	 * 根据项目组ID获取项目组
	 * @param groupId 项目组ID
	 * @return 项目组
	 */
	Groups getGroupById(Long groupId);

	/**
	 * 得到用户参与的项目组
	 * @param userId 用户ID
	 * @return 项目组集合
	 */
	List<Groups> getGroupList(Long userId);

	/**
	 * 获得当前用户最近参与的项目组的信息，包括成员信息
	 * @param userId 当前用户ID
	 * @return 项目组和成员集合
	 */
	Map<String, Object> getLastestGroupAndMember(Long userId);

	/**
	 * 获得项目组和成员信息
	 * @param groupId 项目组ID
	 * @return 项目组合成员集合
	 */
	Map<String, Object> getGroupAndMember(Long groupId);

	/**
	 * 获取项目微博
	 * @param groupId 项目组ID
	 * @param userId 用户ID
	 * @param page 分页信息
	 * @return 项目微博集合
	 */
	List<PMicroblogMegPo> getGroupBlog(Long groupId, Long userId, Page page);

	/**
	 * 添加一条微博
	 * @param pmblogMeg 微博信息
	 */
	void add(PMicroblogMegPo pmblogMeg);

	/**
	 * 获得一条微博的回复
	 * @param parentId 父节点ID
	 * @return 回复的集合
	 */
	List<PMicroblogMegPo> getBlogBack(Long parentId);
	
	public List<PMicroblogMegPo> getMicroBlog(Long blogID);

	/**
	 * 删除一条微博，同时删除相应的回复
	 * @param blogId 微博ID
	 * @param user 用户
	 */
	void del(Long blogId, Users user);

	/**
	 * 搜索项目微博
	 * @param groupId 项目组ID
	 * @param key 搜索关键字
	 * @param page 分页辅助类
	 * @return 项目微博集合
	 */
	List<PMicroblogMegPo> searchBlog(Long groupId, String key, Page page);

	/**
	 * 获得项目组成员
	 * @param groupId 项目组ID
	 * @return 项目组成员集合
	 */
	List<Users> getMemberList(Long groupId);

}
