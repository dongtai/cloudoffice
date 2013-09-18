package apps.transmanager.weboffice.dao;

import java.util.Date;
import java.util.List;

import apps.transmanager.weboffice.domain.GroupSessionMegReadPo;

public interface IGroupSessionMegReadDAO extends
		IBaseDAO<GroupSessionMegReadPo> {

	/**
	 * 更新下已读信息
	 * @param groupId 组ID
	 * @param acceptId 接收者ID
	 */
	void updateReade(Long groupId, Long acceptId);

	List<GroupSessionMegReadPo> findHisRecordByDate(Long groupId, Long ownerId,
			Date startDate, Date endDate);

}
