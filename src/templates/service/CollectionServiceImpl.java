package templates.service;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;


import templates.dao.CollectionDao;
import templates.objectdb.Collection;
import templates.objectdb.CollectionBean;

public class CollectionServiceImpl implements CollectionService{

	
	private CollectionDao collectionDao ;
	
	@Override
	public void delete(Long id) {
		collectionDao.delete(collectionDao.find(id));
		
	}

	@Override
	public List<Collection> find(String hql) {
		return collectionDao.find(hql);
	}

  
	@Override
	public Collection update(Collection collection) {
		collectionDao.update(collection);
		return collection;
	}
	
	public Collection save(Collection collection) {
		Long id = collectionDao.save(collection);
		collection.setId(id);
		return collection;
		
	}


	
public PageBean queryForPage(String hql,int pageSize, int page,int allRow ) {
		
//		String hql="from Template";
        int totalPage = PageBean.countTotalPage(pageSize, allRow);//总页数
        final int beginIndex = PageBean.countOffset(pageSize, page); //当前页开始记录
        final int everyPage = pageSize;    //每页记录数
        final int currentPage = PageBean.countCurrentPage(page);
        List<Collection> list = collectionDao.getUserByPage(hql, beginIndex, everyPage);
        //把分页信息保存到Bean中
        PageBean pageBean = new PageBean();
        pageBean.setPageSize(pageSize);    
        pageBean.setCurrentPage(currentPage);
        pageBean.setAllRow(allRow);
        pageBean.setTotalPage(totalPage);
        pageBean.setList(list);
        pageBean.init();
        return pageBean;
	}

public void setCollectionDao(CollectionDao collectionDao) {
	this.collectionDao = collectionDao;
}

@Override
public List<Collection> query(CollectionBean queryBean) throws Exception {
	try
	{	
	    Criteria criteria =collectionDao.getCriteria();
	    if(queryBean.getCUser()!=null){
	    	criteria.add(Restrictions.eq("cUser", queryBean.getCUser()));
	    }
	    
	    return criteria.list();
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return null;
}

@Override
public void clear() {

	collectionDao.clear();
}

@Override
public void flush() {
	collectionDao.flush();
	
}



	
	

	
}
