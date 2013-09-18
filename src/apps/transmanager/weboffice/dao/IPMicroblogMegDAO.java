package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.domain.PMicroblogMegPo;
import apps.transmanager.weboffice.util.beans.Page;

public interface IPMicroblogMegDAO extends IBaseDAO<PMicroblogMegPo> {
	
	/**
	 * 找到每个组的最新信息
	 * @param groupId 组ID
	 * @return
	 */
	public PMicroblogMegPo findLastNew(Long groupId);

	/**
	 * 根据关键字查找微博
	 * @param groupId 项目组
	 * @param key 关键字
	 * @param page 分页辅助类
	 * @return 
	 */
	public List<PMicroblogMegPo> findByKey(Long groupId, String key, Page page);

	/**
	 * 
	 * @param groupId 组ID
	 * @param userId 用户ID
	 * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @return 微博方式
	 */
	public List<PMicroblogMegPo> findGroupBlog(Long groupId, Long userId,
			Page page, String sort, String order);

}
