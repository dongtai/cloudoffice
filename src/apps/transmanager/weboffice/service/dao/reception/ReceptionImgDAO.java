package apps.transmanager.weboffice.service.dao.reception;

import org.hibernate.Query;

import apps.transmanager.weboffice.databaseobject.ReceptionImg;


public class ReceptionImgDAO extends BaseDAO<ReceptionImg> {

	public void delByReceptionId(Long receptionid) {
		String queryS = "delete from ReceptionImg t where t.receptionId="+receptionid;
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(queryS);
		query.executeUpdate();
	}


}
