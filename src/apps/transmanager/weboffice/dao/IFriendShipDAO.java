package apps.transmanager.weboffice.dao;

import apps.transmanager.weboffice.domain.FriendshipPo;
/**
 * 用户关系的数据库操作
 * @author 胡晓燕
 *
 */
public interface IFriendShipDAO extends IBaseDAO<FriendshipPo>{
    /**
     * 取消关注
     * @param follow_userid 被关注的用户id
     * @param userid 用户id
     * @return
     */
	boolean deletefollow(Long follow_userid, Long userid);

}
