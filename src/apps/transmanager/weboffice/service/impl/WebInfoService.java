package apps.transmanager.weboffice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.dao.INewsInfoDAO;
import apps.transmanager.weboffice.dao.IUserCustomeNewsDAO;
import apps.transmanager.weboffice.dao.IUserDAO;
import apps.transmanager.weboffice.dao.IWebInfoDAO;
import apps.transmanager.weboffice.databaseobject.NewsInfo;
import apps.transmanager.weboffice.databaseobject.UserCustomeNews;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.WebInfo;
import apps.transmanager.weboffice.service.IWebInfoService;
import apps.transmanager.weboffice.util.beans.Page;

@Component(value=WebInfoService.NAME)
public class WebInfoService implements IWebInfoService{
	public static final String NAME = "WebInfoService";

	@Autowired
	private IWebInfoDAO webinfoDAO;
	@Autowired
	private INewsInfoDAO newsinfoDAO;
	@Autowired
	private IUserCustomeNewsDAO ucnDAO;
	@Autowired
	private IUserDAO userDAO;
	
	public IWebInfoDAO getNewslistDAO() {
		return webinfoDAO;
	}
	
	public void setNewslistDAO(IWebInfoDAO webinfoDAO) {
		this.webinfoDAO = webinfoDAO;
	}

	@Override
	public void addWebInfo(WebInfo webinfo) {
		webinfoDAO.saveOrUpdate(webinfo);
	}

	@Override
	public void addWebInfoList(List<WebInfo> webInfolist) {
		webinfoDAO.saveOrUpdateAll(webInfolist);	
	}

	@Override
	public void updateWebInfoNum(Long id, Long num) {
		webinfoDAO.updateWebInfoNum(id, num);
	}

	@Override
	public WebInfo getWebInfoByWebName(String webname) {
		WebInfo  webinfo = webinfoDAO.findByPropertyUnique(WebInfo.class.getName(),"webname",webname);
		return webinfo;
	}

	@Override
	public WebInfo getWebInfoById(Long id) {
		WebInfo  webinfo = webinfoDAO.findById(WebInfo.class.getName(),id);
		return webinfo;
	}

	@Override
	public List<WebInfo> getWebInfoListByCategory(String category) {
		List<WebInfo> webInfoList = webinfoDAO.findByProperty(WebInfo.class.getName(),"category",category);
		return webInfoList;
	}

	@Override
	// 显示所有的订阅列表
	public List<WebInfo> getAllWebInfoList() {
		Page page=null;
		List<WebInfo> webInfoList = webinfoDAO.findAll(WebInfo.class.getName(), page);
		return webInfoList;
	}

	@Override
	public void delWebInfo(Long id) {
		webinfoDAO.deleteById(WebInfo.class.getName(),id);
	}

	@Override
	public void delWebInfoList(List<Long> idList) {
		webinfoDAO.deleteByIdList(WebInfo.class.getName(), idList);
	}
	
	@Override
	public List<WebInfo> getUserWebInfoList(Long id){
		UserCustomeNews ucn = ucnDAO.findById(UserCustomeNews.class.getName(), id); 
		List<WebInfo> webinfoList = webinfoDAO.findByProperty(WebInfo.class.getName(), "gid",ucn.getWebinfo().getGid());
		return webinfoList;
	}
	
	@Override
	public List<NewsInfo> getNewsByCategory(String category){
		List<NewsInfo> newsinfoList = newsinfoDAO.getNewsByCategory(category);
		return newsinfoList;
	}
	
	@Override
	public NewsInfo getNewsInfoById(Long id){
		NewsInfo ni = newsinfoDAO.findById(NewsInfo.class.getName(), id);
		return ni;
	}

	@Override
	public void saveOrUpdateNewsInfo(NewsInfo newsinfo){
		newsinfoDAO.saveOrUpdate(newsinfo);
	}
	
	@Override
	public void saveOrUpdateNewsInfoList(List<NewsInfo> newsinfoList){
		newsinfoDAO.saveOrUpdateAll(newsinfoList);
	}
			
	@Override
	public void delNewsInfo(Long id){
		newsinfoDAO.deleteById(NewsInfo.class.getName(), id);
	}

	@Override
	public void delNewsInfoList(List<Long> idList){
		newsinfoDAO.deleteByIdList(NewsInfo.class.getName(), idList);
	}

	@Override
	public void addAttenWebInfo(Long userid,Long webinfoid){
		UserCustomeNews ucn = new UserCustomeNews();
		WebInfo webinfo = webinfoDAO.findById(WebInfo.class.getName(), webinfoid);
		Users user=userDAO.findById(Users.class.getName(), userid);
		ucn.setUsers(user);
		ucn.setWebinfo(webinfo);
		ucnDAO.saveOrUpdate(ucn);
	}
	
	@Override
	public void addAttenWebInfo(Long userid, String webname) {
		UserCustomeNews ucn = new UserCustomeNews();
		WebInfo webinfo = webinfoDAO.findByPropertyUnique(WebInfo.class.getName(),"webname", webname);
		Users user=userDAO.findById(Users.class.getName(), userid);
		ucn.setUsers(user);
		ucn.setWebinfo(webinfo);
		ucnDAO.saveOrUpdate(ucn);
	}

	
	@Override
	public void delAttenWebInfo(Long userid,Long webinfoid){
		//有问题？
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("users.id", userid);
		paramMap.put("webinfo.gid", webinfoid);
		UserCustomeNews ucn = ucnDAO.findByPropertyUnique(UserCustomeNews.class.getName(), paramMap);
		ucnDAO.delete(ucn);
	}
	
	@Override
	public void delAttenWebInfo(Long userid, String webname) {
		//有问题？
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("users.id", userid);
		paramMap.put("webinfo.webname", webname);

		UserCustomeNews ucn = ucnDAO.findByPropertyUnique(UserCustomeNews.class.getName(), paramMap);
		ucnDAO.delete(ucn);
	}

	@Override
	public List<NewsInfo> listNewsInWebInfo(Long webinfoid) {
		List<NewsInfo> newsinfoList = newsinfoDAO.findByProperty(NewsInfo.class.getName(), "webinfo.gid", webinfoid);
		return newsinfoList;
	}

	@Override
	public List<NewsInfo> listNewsInWebInfo(String webname) {
		//这样可行不？
//		List<NewsInfo> newsinfoList = newsinfoDAO.findByProperty(NewsInfo.class.getName(), "WebInfo.webname", webname);
//		return newsinfoList;
		
		WebInfo webinfo = webinfoDAO.findByPropertyUnique(WebInfo.class.getName(),"webname", webname);
		List<NewsInfo> newsinfoList = newsinfoDAO.findByProperty(NewsInfo.class.getName(), "webinfo.gid", webinfo.getGid());
		return newsinfoList;
	}

	
}

	
	
