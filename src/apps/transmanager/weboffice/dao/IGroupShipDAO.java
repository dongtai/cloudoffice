package apps.transmanager.weboffice.dao;

import java.util.List;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.GroupShipPo;
/**
 * 用户与群组之间关系的数据库处理
 * @author 胡晓燕
 *
 */
public interface IGroupShipDAO extends IBaseDAO<GroupShipPo>{
    /**
     * 通过用户id和微群id获得该微群除本用户外的用户列表
     * @param userid 用户id
     * @param groupid 微群id
     * @return 用户列表
     */
	List<Users> findUserByGroupId(Long userid,Long groupid);
   /**
    * 根据用户id和微群id解除两者之间的关系
    * @param userid 用户id
    * @param groupid 微群id
    * @return
    */
	boolean delShip(Long userid, Long groupid);

}
