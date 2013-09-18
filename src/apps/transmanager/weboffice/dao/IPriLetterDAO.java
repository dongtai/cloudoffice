package apps.transmanager.weboffice.dao;
import java.util.List;

import apps.transmanager.weboffice.domain.PrivateLetterPo;
import apps.transmanager.weboffice.util.beans.Page;
/**
 * 私信与数据库的交互处理
 * @author 胡晓燕
 *
 */
public interface IPriLetterDAO extends IBaseDAO<PrivateLetterPo>{
    /**
     * 通过用户id获得用户收到的私信
     * @param userid  用户id
     * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
     * @return 私信列表
     */
	public List<PrivateLetterPo> findLetterByReceivedUser(Long userid,Page page,String sort, String order);
	/**
     * 通过用户id获得用户发出的私信
     * @param userid  用户id
     * @param page 分页辅助类
	 * @param sort 排序字段
	 * @param order 排序方式
     * @return 私信列表
     */
	public List<PrivateLetterPo> findLetterBySendUser(Long userid,Page page,String sort, String order);
	/**
	 * 通过私信id获得私信
	 * @param id 私信id
	 * @return 私信信息
	 */
	public PrivateLetterPo findLetterById(Long id);
	/**
	 * 删除私信（软删除）
	 * @param column 删除的用户（接受用户或发送用户）
	 * @param priletter_id 私信id
	 * @return
	 */
	boolean delByUser(String column, Long priletter_id);

}
