package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.MicroGroupPo;
/**
 * 微群的数据库处理
 * @author 胡晓燕
 *
 */
public interface IMicroGroupDAO extends IBaseDAO<MicroGroupPo>{
    /**
     * 通过用户id获得该用户的所有微群
     * @param userid 用户id
     * @param sort 排序字段
	 * @param order 排序方式
     * @return 微群列表
     */
	List<MicroGroupPo> findListByUserid(Long userid,String sort,String order);
    /**
     * 更新微群的管理员
     * @param newManager 新的管理员
     * @param groupid 微群号
     * @return
     */
	boolean updateManager(Users newManager, Long groupid);
    /**
     * 得到全部的微群
     * @return 微群列表
     */
	List<MicroGroupPo> findAllGroup();
    /**
     * 根据微群名获得除本微群以外的所有微群
     * @param groupname 微群名
     * @return 微群列表
     */
	List<MicroGroupPo> findAllOtherGroup(Long groupname);
    /**
     * 更新微群
     * @param groupid 微群号
     * @param group 新的内容
     * @return
     */
	boolean UpdateGroup(Long groupid, MicroGroupPo group);

}
