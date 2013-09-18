package apps.transmanager.weboffice.dwr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.io.FileTransfer;

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.CustomGroupPo;
import apps.transmanager.weboffice.domain.GroupSessionMegPo;
import apps.transmanager.weboffice.domain.GroupSessionMegReadPo;
import apps.transmanager.weboffice.domain.SessionMegPo;
import apps.transmanager.weboffice.service.ITalkService;
import apps.transmanager.weboffice.util.beans.PageConstant;
import apps.transmanager.weboffice.util.beans.Page;

/**
 * 聊天的控制类,对聊天的各类事件进行数据转化和控制转发(dwr)
 * 
 * @author 彭俊杰(753)
 * @author 杨丁苗
 * 
 */
public class TalkDwr {

	private ITalkService talkService;

	/**
	 * 添加联系人分组(不包含联系人) ydm
	 * 
	 * @param ctmGName
	 *            分组名
	 * @param req
	 *            请求信息
	 * @return 添加信息
	 */
	public int addCtmGByGName(String ctmGName, HttpServletRequest req) {
		return talkService.getCtmGroupService().addCtmG(
				getCurrentUser(req).getId(), ctmGName, null);
	}

	/**
	 * 添加用户自定义组(包含联系人列表) ydm
	 * 
	 * @deprecated
	 * @param ctmGName
	 *            组名
	 * @param userList
	 *            联系人列表
	 * @param req
	 *            请求信息
	 * @return 添加信息
	 */
	public int addCtmGByGNameAndGM(String ctmGName, List<Users> userList,
			HttpServletRequest req) {
		Users user = getCurrentUser(req);
		int result = talkService.getCtmGroupService().addCtmG(user.getId(),
				ctmGName, userList);
		return result;
	}

	/**
	 * 将用户添加至分组 ydm
	 * 
	 * @param rosterId
	 *            联系人ID
	 * @param gId
	 *            分组ID
	 * @param req
	 * @return
	 */
	public int addCtmGM(Long rosterId, Long gId, HttpServletRequest req) {
		Users user = getCurrentUser(req);
		return talkService.getCtmGroupService().addCtmGM(user.getId(),
				rosterId, gId);
	}

	/**
	 * 添加一个用户自定义的讨论组（需要重名验证，和添加成员）
	 * 
	 * @param gName
	 *            讨论组名称
	 * @param userList
	 *            成员用户集合
	 * @return 添加结果
	 */
	public Long addDiscuGroup(String gName, List<Users> userList,
			HttpServletRequest req) {
		Users user = getCurrentUser(req);
		try {
			return talkService.getDiscuGroupService().addDiscuGroup(gName,
					user, userList);
		} catch (Exception e) {
			return (long) PageConstant.VALIDATOR_NAME_FAIL;
		}
	}

	/**
	 * 删除一个用户的联系人组
	 * 
	 * @param gId
	 *            联系人组ID
	 * @return 删除信息
	 */
	public boolean delCtmG(Long gId, HttpServletRequest req) {
		try {
			talkService.getCtmGroupService().delCtmG(
					getCurrentUser(req).getId(), gId);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除一个用户，从树节点删除，与列表删除时ID标识不同
	 * 
	 * @return 删除信息
	 */
	public boolean delCtmGM(Long userId, Long gId, HttpServletRequest req) {
		try {
			Users user = getCurrentUser(req);
			talkService.getCtmGroupService()
					.delCtmGM(user.getId(), userId, gId);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除一个讨论组
	 * 
	 * @param id
	 *            讨论组ID
	 */
	public int delDiscuGroup(Long id) {
		return talkService.getDiscuGroupService().delDiscuGroup(id);
	}

	/**
	 * 删除当前用户的ID
	 * 
	 * @param type
	 *            类型 1 个人 2 讨论组
	 * @param msgId
	 *            消息ID
	 */
	public void deleteHisSessionMessage(int type, List<Long> msgIds,
			HttpServletRequest req) {
		talkService.deleteHisSessionMessage(type, getCurrentUser(req).getId(),
				msgIds);
	}

	public void deleteHisSessionMessageByDate(int type, Long otherId, int delType, String startDate, String endDate, HttpServletRequest req) {
		talkService.deleteHisSessionMessageByDate(type, otherId, delType, startDate, endDate, getCurrentUser(req).getId());
	}
	/**
	 * 全部删除
	 * @param type
	 * @param otherId
	 * @param req
	 */
	public void deleteAllHisSessionMessage(int type, Long otherId, HttpServletRequest req) {
		if (type == 1) {
			talkService.deleteAllPersonalSessionMessage(getCurrentUser(req).getId(), otherId);
		} else if (type == 2) {
			talkService.deleteAllGroupSessionMessage(getCurrentUser(req).getId(), otherId);
		}
	}
	/**
	 * 编辑用户自定义组的信息
	 * 
	 * @param gId
	 *            用户分组的ID
	 * @param ctmGName
	 *            用户分组名
	 * @param userList
	 *            用户自定义组成员集合
	 * @return 编辑信息
	 */
	public int editCtmGM(Long gId, String ctmGName, List<Users> userList,
			HttpServletRequest req) {
		return talkService.getCtmGroupService().editCtmG(
				getCurrentUser(req).getId(), gId, ctmGName, userList);
	}

	/**
	 * 编辑联系人分组
	 * 
	 * @param gId
	 *            用户分组的ID
	 * @param ctmGName
	 *            用户分组名
	 * @param req
	 * @return
	 */
	public int editCtmGName(Long gId, String ctmGName, HttpServletRequest req) {
		Users user = getCurrentUser(req);
		return talkService.getCtmGroupService().editCtmG(user.getId(), gId,
				ctmGName, null);
	}

	/**
	 * 编辑讨论组（需要重名验证，更新成员）
	 * 
	 * @param gId
	 *            讨论组ID
	 * @param gName
	 *            讨论组名称
	 * @param userList
	 *            成员用户集合
	 * @return 更新结果
	 */
	public int editDiscuGroup(Long gId, String gName, List<Users> userList,
			HttpServletRequest req) {
		try {
			Users user = getCurrentUser(req);
			return talkService.getDiscuGroupService().editDiscuGroup(gId,
					gName, user, userList);
		} catch (Exception e) {
			return PageConstant.VALIDATOR_NAME_FAIL;
		}
	}

	/**
	 * 结束一个用户验证信息
	 * 
	 * @param vMsgId
	 */
	public void endValidateMessage(Long vMsgId) {
		this.talkService.endValidateMessage(vMsgId);
	}

	/**
	 * 当前用户退出一个讨论组
	 * 
	 * @param groupId
	 *            组ID
	 * @param req
	 *            请求信息
	 */
	public void exitDiscuGroup(Long groupId, HttpServletRequest req) {
		talkService.getDiscuGroupService().delDiscuGM(groupId,
				getCurrentUser(req).getId());
	}

	/**
	 * 打开对话框时获取未读取的消息
	 * 
	 * @param sendIdS
	 *            来自于哪里的消息
	 * @return 消息集合
	 */
	public List<SessionMegPo> getAcceptMeg(Long sendId, HttpServletRequest req) {
		return talkService.getPersonNewMeg(sendId, getCurrentUser(req).getId(),
				false);
	}

	public List<Map<String, Object>> getAllCtmMb(HttpServletRequest req) {
		return this.talkService.getCtmGroupService().getAllCtmGMList(
				getCurrentUser(req).getId());
	}

	/**
	 * ydm 获得消息提醒,包括个人消息,群消息,联系人添加验证消息
	 * 
	 * @param req
	 *            请求信息
	 * @return 消息提醒
	 */
	public Map<String, Object> getAllNewMegTip(HttpServletRequest req) {
		return talkService.getAllNewMegTip(getCurrentUser(req).getId(), false);
	}

	/**
	 * 获得联系人分组，并转换成树节点(公司部门)
	 * 
	 * @param parentID
	 *            父节点ID
	 * @param exceptSelf
	 *            是否排除当前用户本身（仅限于用户）
	 * @return 分组信息
	 */
	public List<Map<String, Object>> getCompanyTree(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return talkService.getCompanyTree(getCurrentUser(req), parentID,
				exceptSelf, "gm");
	}

	public List<Map<String, Object>> getCtmG(String uId, HttpServletRequest req) {
		return talkService.getCtmGroupService().getCtmGNodeList(
				getCurrentUser(req).getId(), uId);
	}

	/**
	 * 根据用户ID获得用户的联系人分组
	 * 
	 * @param uId
	 * @param req
	 * @return
	 */
	public List<CustomGroupPo> getCtmGList(HttpServletRequest req) {
		return talkService.getCtmGroupService().getCtmGList(
				getCurrentUser(req).getId());
	}

	/**
	 * 获取当前用户所有联系人分组,当点击分组节点时,获取该组下所有成员,转化为树的节点
	 * 
	 * @param pId
	 *            节点ID
	 * @param req
	 *            请求信息
	 * @return 自定义
	 */
	public List<Map<String, Object>> getCtmGOrMNode(String pId,
			HttpServletRequest req) {
		return talkService.getCtmGroupService().getCtmGOrMNode(
				getCurrentUser(req).getId(), pId, "ctmgm");
	}

	private Users getCurrentUser(HttpServletRequest req) {
		return (Users) req.getSession().getAttribute(PageConstant.LG_SESSION_USER);
	}

	public String getDiscuGMailStr(Long gId) {
		return talkService.getDiscuGroupService().getDiscuGMailStr(gId);
	}

	/**
	 * 获得讨论组成员，并将其转换成树节点的集合（如果要求获得当前用户，self参数为true,否则为false）
	 * 
	 * @param gId
	 *            组ID
	 * @param self
	 *            是否要求加入当前用户本人
	 * @param req
	 *            请求信息
	 * @return 树节点集合
	 */
	public List<Map<String, Object>> getDiscuGMNodeList(Long gId, Boolean self,
			HttpServletRequest req) {
		return talkService.getDiscuGroupService().getDiscuGMNodeList(gId);
	}

	/**
	 * 根据讨论组ID获得讨论组节点
	 */
	public Map<String, Object> getDiscuGroupNode(Long id) {
		return talkService.getDiscuGroupService().getDiscuGroupNode(id);
	}

	/**
	 * 获得当前用户的讨论组集合，并转换成树节点集合，将自己创建的放在最前面
	 * 
	 * @param pId
	 *            默认为'g-0'
	 * @param req
	 *            请求信息
	 * @return 树节点集合
	 */
	public List<Map<String, Object>> getDiscuGroupNodeList(String pId,
			HttpServletRequest req) {
		Users user = getCurrentUser(req);
		return talkService.getDiscuGroupService().getDiscuGroupNodeList(user);
	}

	/**
	 * 读取未读消息
	 * 
	 * @param groupId
	 *            组ID
	 * @param req
	 *            请求信息
	 * @return 组未读取信息
	 */
	public List<GroupSessionMegPo> getGroupAcceptMeg(Long groupId,
			HttpServletRequest req) {
		return talkService.getGroupAcceptMeg(groupId, getCurrentUser(req)
				.getId(), false);
	}

	/**
	 * 获得当前用户和对应用户的聊天历史记录的总数目
	 * 
	 * @param otherId
	 *            对应用户的ID
	 * @param req
	 *            请求信息
	 * @return 聊天记录总数
	 */
	public int getHisCount(Long otherId, HttpServletRequest req) {
		return talkService.getHisSessionMegCount(getCurrentUser(req).getId(),
				otherId);
	}

	/**
	 * 获得当前用户和对应用户的聊天历史记录的总数目
	 * 
	 * @param groupId
	 *            讨论组ID
	 * @return 聊天记录总数
	 */
	public int getHisGroupCount(Long groupId, HttpServletRequest req) {
		Users user = getCurrentUser(req);
		if (null == user) {
			return 0;
		}
		return talkService.getHisGroupSessionMegCount(user.getId(), groupId);
	}

	/**
	 * 获得讨论组聊天历史记录
	 * 
	 * @param start
	 *            开始记录
	 * @param limit
	 *            每页显示数目
	 * @param gId
	 *            对应的组ID
	 * @param req
	 *            请求信息
	 * @return 历史记录
	 */
	public Map<String, Object> getHisGroupSessionMeg(int start, int limit,
			int total, Long gId, HttpServletRequest req) {
		Users user = getCurrentUser(req);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Page page = new Page();
			page.setPageSize(limit);
			page.setTotalRecord(total);
			page.setCurrentRecord(start);
			List<GroupSessionMegPo> megList = talkService
					.getHisGroupSessionMeg(page, gId, user.getId());
			map.put("totalRecords", page.getTotalRecord());
			map.put("data", megList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获得当前用户和对应用户的聊天历史记录
	 * 
	 * @param start
	 *            开始记录
	 * @param limit
	 *            每页显示数目
	 * @param otherId
	 *            对应用户的ID
	 * @param req
	 *            请求信息
	 * @return 历史记录
	 */
	public Map<String, Object> getHisSessionMeg(int start, int limit,
			int total, Long otherId, HttpServletRequest req) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Page page = new Page();
			page.setPageSize(limit);
			page.setTotalRecord(total);
			page.setCurrentRecord(start);
			List<SessionMegPo> megList = talkService.getHisSessionMeg(page,
					getCurrentUser(req).getId(), otherId);
			map.put("totalRecords", page.getTotalRecord());
			map.put("data", megList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获得最近联系人
	 * 
	 * @param req
	 * @return
	 */
	public List<Map<String, Object>> getRecentCtmMb(HttpServletRequest req) {
		return this.talkService.getRecentCtmGM(getCurrentUser(req).getId(), 20);
	}

	public List<Map<String, Object>> getRelations(String parentID,
			boolean exceptSelf, HttpServletRequest req) {
		return this.talkService.getRelations(getCurrentUser(req), parentID,
				exceptSelf);
	}

	/**
	 * 获得截屏图片
	 * 
	 * @param req
	 *            请求信息
	 * @return 截屏图片地址
	 */
	public String getScreen(HttpServletRequest req) {
		String url = "";
		Users user = getCurrentUser(req);
		long startTime = new Date().getTime();
		boolean flag = true;
		while (req.getSession().getAttribute(user.getId() + "-screen") == null
				|| req.getSession().getAttribute(user.getId() + "-screen")
						.equals("")) {
			long endTime = new Date().getTime();
			if ((endTime - startTime) / (1000) > 60) {
				flag = false;
				break;
			}
		}
		if (flag) {
			url = (String) req.getSession().getAttribute(
					user.getId() + "-screen");
			req.getSession().removeAttribute(user.getId() + "-screen");
			return url;
		} else {
			return "false";
		}
	}

	public Map<String, Object> getUserInfo(Long userId, HttpServletRequest req) {
		Users user = getCurrentUser(req);
		if (null == user) {
			return null;
		}
		return talkService.getUserInfo(user.getId(),userId);
	}

	/**
	 * ydm 根据联系人ID获得联系人节点,用于获取初始化聊天对话框时获取需要的参数
	 * 
	 * @param userId
	 *            联系人ID
	 * @return 参数K-V
	 */
	public Map<String, Object> getUserNode(Long userId) {
		return talkService.getUserNodeByUId(userId);
	}

	/**
	 * 
	 * @param vMsgId
	 *            用户接受到的信息ID
	 * @param type
	 *            1:同意 2：拒绝
	 * @param validateSessionMeg
	 *            同意或者拒绝原因
	 * @param groupId
	 *            同意后的添加的小组ID
	 */
	public boolean proValidateSessionMessage(Long vMsgId, int type,
			String validateSessionMeg, Long groupId, HttpServletRequest req) {
		Users user = getCurrentUser(req);
		if (null == user) {
			return false;
		}
		return this.talkService.proValidateSessionMessage(user.getId(), vMsgId,
				type, validateSessionMeg, groupId);
	}

	/**
	 * 保存sessionMegGroup
	 * 
	 * @param sessionMegGroup
	 */
	public void saveGroupMegRead(GroupSessionMegReadPo groupSessionMegRead) {
		talkService.saveGroupMegRead(groupSessionMegRead);
	}

	/**
	 * 保存sessionMeg
	 * 
	 * @param sessionMeg
	 */
	public void saveMeg(SessionMegPo sessionMeg) {
		talkService.savePersonMeg(sessionMeg);
	}

	/**
	 * 通过讨论组名称查找讨论组
	 * 
	 * @param keyWord
	 *            前台关键字
	 * @return 相关的用户对象
	 */
	public List<Map<String, String>> SearchGroups(String keyWord,
			HttpServletRequest req) {
		return talkService.getDiscuGroupService().searchGroupsByKey(keyWord,
				getCurrentUser(req).getId());
	}

	/**
	 * 获得搜索结果,包括在联系人中搜索,在讨论组中搜索,在企业组中搜索
	 * 
	 * @author ydm
	 * 
	 * @param key
	 *            搜索关键字
	 * @param index
	 *            判断搜索内容,企业组为"company",讨论组为"discu",联系人为"friend"
	 * @param req
	 *            请求信息
	 * @return 搜索结果
	 */
	public List<String[]> searchRel(String key, String index,
			HttpServletRequest req) {
		return talkService.searchRel(getCurrentUser(req), key, index);
	}

	/**
	 * 通过手机、账号、email查找用户
	 * 
	 * @param keyWord
	 *            前台关键字
	 * @return 相关的用户对象
	 */
	public List<Map<String, String>> searchRosters(String keyWord,
			HttpServletRequest req) {
		return talkService.searchRostersByKey(keyWord, getCurrentUser(req).getId());
	}

	/**
	 * 发送群聊信息
	 * 
	 * @param groupId
	 *            组ID
	 * @param sessionMeg
	 *            会话信息
	 * @param req
	 *            请求信息
	 */
	public void sendGroupSessionMeg(Long gId, String msg, int type,
			String date, HttpServletRequest req) {
		talkService.sendGroupSessionMeg(msg, getCurrentUser(req), gId, type, date);
	}

	/**
	 * 个人发送信息
	 * 
	 * @param meg
	 *            信息内容
	 * @param acceptId
	 *            收信息人ID
	 */
	public void sendSessionMeg(String msg, Long acceptId, Integer type,
			String date, Boolean online, HttpServletRequest req) {
		Users user = getCurrentUser(req);// 当前登录的用户编号
		if (null == user) {
			return;
		}
		talkService.sendSessionMeg(msg, user, acceptId, 0, date);
	}

	/**
	 * 发送一条用户的验证信息
	 * 
	 * @param category
	 *            1 加人 2加组
	 * @param acceptId
	 * 
	 * 
	 * @param groupId
	 *            组ID
	 * 
	 * @param validateSessionMeg
	 *            验证信息
	 * @return -1: 当前session过期 0： 发送成功 1： 联系人已经存在
	 */
	public int sendValidateSessionMessage(int category, Long acceptId,
			Long groupId, String validateSessionMeg, HttpServletRequest req) {
		Users user = getCurrentUser(req);
		if (null == user) {
			return -1;
		}
		return this.talkService.sendValidateSessionMessage(category, user,
				acceptId, groupId, validateSessionMeg);
	}

	public void setTalkService(ITalkService talkService) {
		this.talkService = talkService;
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 *            DWR文件格式
	 * @param req
	 *            请求信息
	 * @return 上传文件路径
	 * @throws IOException
	 */
	public String uploadImg(FileTransfer file, HttpServletRequest req) {
		String uploadUrl = "data/uploadfile/talk/resource";
		String name = file.getFilename();
		String type = name.substring(name.lastIndexOf("."), name.length());
		BufferedInputStream brIn = null;
		BufferedOutputStream brOut = null;
		try {
			InputStream in = file.getInputStream();
			brIn = new BufferedInputStream(in);
			byte[] by = new byte[1024];
			File dir = new File(req.getSession().getServletContext()
					.getRealPath("/")
					+ uploadUrl);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File imgFile = new File(dir, new Date().getTime() + type);
			brOut = new BufferedOutputStream(new FileOutputStream(imgFile));
			int read = 0;
			while ((read = brIn.read(by)) != -1) {
				brOut.write(by, 0, read);
			}
			brOut.flush();
			brOut.close();
			brIn.close();
			uploadUrl += "/" + imgFile.getName();
			// uploadUrl = imgFile.getAbsolutePath();
		} catch (IOException e) {
			System.out.println("文件上传出错了");
			e.printStackTrace();
		} finally {
			try {
				if (brOut != null)
					brOut.close();
				if (brIn != null)
					brIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return uploadUrl;
	}
	/**
	 * 上传文件
	 * 
	 * @param file
	 *            DWR文件格式
	 * @param req
	 *            请求信息
	 * @return 上传文件路径
	 * @throws IOException
	 */
	public boolean sendPersonFile(long acceptId,FileTransfer file, HttpServletRequest req)throws Exception {
		Users user = getCurrentUser(req);// 当前登录的用户编号
		if (null == user) {
			return false;
		}
		if(file.getSize() >1024*1024*10){
			return false;
		}
			
		return this.talkService.sendPersonFile(acceptId, req.getSession().getServletContext().getRealPath("/"), file, user);
	}
	
	/**
	 * 上传文件
	 * 
	 * @param file
	 *            DWR文件格式
	 * @param req
	 *            请求信息
	 * @return 上传文件路径
	 * @throws IOException
	 */
	public boolean sendGroupFile(long group,FileTransfer file, HttpServletRequest req)throws Exception {
		Users user = getCurrentUser(req);// 当前登录的用户编号
		if (null == user) {
			return false;
		}
		if(file.getSize() >1024*1024*10){
			return false;
		}
		return this.talkService.sendGroupFile(group, req.getSession().getServletContext().getRealPath("/"), file, user);
	}
	
	/**
	 * 文件传输拒绝
	 * @param data
	 */
	public void rejectFileTrans(String type,long msgId,long acceptId,String fileName, HttpServletRequest req){
		Users user = getCurrentUser(req);// 当前登录的用户编号
		if (null != user) {
			this.talkService.rejectFileTrans(type,msgId,acceptId, fileName, user);
		}
	}
	
	public void renamePersonNickName(long userId,String newNickname, HttpServletRequest req){
		Users user = getCurrentUser(req);// 当前登录的用户编号
		if (null != user) {
			this.talkService.renamePersonNickName(user, userId, newNickname);
		}
	}
	

	public boolean isUserSameCompany(Long userId,HttpServletRequest req){
		Users userInfo = (Users)req.getSession().getAttribute(PageConstant.LG_SESSION_USER);//当前登录的用户编号
		if(null !=userInfo){
			return this.talkService.isUserSameCompany(userInfo, userId);
		}
		return false;
	}
}
