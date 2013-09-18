package apps.transmanager.weboffice.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.databaseobject.Company;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.AddressBean;
import apps.transmanager.weboffice.domain.CtmGroupMemberPo;
import apps.transmanager.weboffice.domain.CustomGroupPo;
import apps.transmanager.weboffice.domain.DepMemberPo;
import apps.transmanager.weboffice.domain.DepartmentPo;
import apps.transmanager.weboffice.domain.DiscuGroupMemberPo;
import apps.transmanager.weboffice.domain.DiscuGroupPo;
import apps.transmanager.weboffice.service.config.WebConfig;

public class TreeUtil {

	private static final String NODE_ID = "id"; // 树ID
	private static final String NODE_NAME = "text"; // 树节点标题
	private static final String NODE_LEAF = "leaf";// 是否为叶子节点
	private static final String NODE_EXPANDABLE = "expandable";// 是否能展开
	private static final String NODE_ICON = "icon";// 节点图标
	// private static final String NODE_PERSON = "person";
	private static final String NODE_ICONCLS = "iconCls";// 节点图标的样式
	private static final String NODE_ATTR_RELID = "relId";// 用户的真实ID
	// private static final String NODE_ATTR_PCHAIN = "pchain";//用户路径链
	private static final String NODE_ATTR_OWNERID = "ownerId";// 所属用户，讨论组用
	private static final String NODE_ATTR_ONLINE = "online";// 是否在线
	private static final String NODE_ATTR_REALEMAIL = "email";
	private static final String NODE_ATTR_MOBILE = "mobile";
	private static final String NODE_VIEW_MOBILE = "viewmobile";//显示手机号，不显示全前面4位+后面4位
	private static final String NODE_ATTR_REALNAME = "realName";// 姓名
	private static final String NODE_ATTR_USERNAME = "userName";// 用户名
	private static final String NO_ONLINE_CSS = "icon-unavailable";// 下线用户的图标，变灰

	// private static final String NODE_ATTR_BLINKLIST = "blinkList";// 闪烁列表

	/**
	 * 将讨论组成员集合转换成树节点集合（多个）
	 * 将在线用户放在顶部
	 * @param discuGMList
	 *            讨论组成员集合
	 * @param onlineIdList
	 *            在线用户集合
	 * @return 树节点集合
	 */
	public static List<Map<String, Object>> convertDiscuGMList(
			List<DiscuGroupMemberPo> discuGMList, List<Long> onlineIdList) {
		if (null == discuGMList || discuGMList.isEmpty()) {
			return null;
		}
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> onlineNodeList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> offlineNodeList = new ArrayList<Map<String, Object>>();
		for (DiscuGroupMemberPo discuGM : discuGMList) {
			Boolean isOnline = onlineIdList.contains(discuGM.getMemberId()) ? true : false;
			Map<String, Object> node = covertDiscuGM(discuGM, isOnline, "dgm");
			if (isOnline) {
			  onlineNodeList.add(node);
			} else {
			  offlineNodeList.add(node);
			}
		}
		nodeList.addAll(offlineNodeList);
		nodeList.addAll(onlineNodeList);
		return nodeList;
	}

	/**
	 * 将讨论组成员转换成树节点(单个)
	 *
	 * @param discuGM
	 *            讨论组成员
	 * @param isOnline
	 *            是否在线
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点
	 */
	public static Map<String, Object> covertDiscuGM(DiscuGroupMemberPo discuGM, Boolean isOnline, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, idFix + "-" + discuGM.getMemberId());
		nodeMap.put(NODE_NAME, discuGM.getMemberName());
		// nodeMap.put(NODE_LEAF, true);
		nodeMap.put(NODE_ICON, getAvatarSrc(discuGM.getImage()));
		nodeMap.put(NODE_ATTR_ONLINE, isOnline);
		if (!isOnline) {
		  nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
		}
		nodeMap.put(NODE_ATTR_RELID, discuGM.getId());
		return nodeMap;
	}

	/**
	 * 将讨论组集合转换成树节点集合（多个）
	 *
	 * @param discuGroupList
	 *            讨论组集合
	 * @return
	 */
	public static List<Map<String, Object>> convertDiscuGList(List<DiscuGroupPo> discuGList) {
		if (discuGList == null || discuGList.isEmpty()) {
			return null;
		}
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (DiscuGroupPo dg : discuGList) {
			Map<String, Object> node = convertDiscuG(dg);
			nodeList.add(node);
		}
		return nodeList;
	}

	/**
	 * 将讨论组转换成一个树节点（单个）
	 *
	 * @param dg
	 *            讨论组
	 * @return 树节点
	 */
	public static Map<String, Object> convertDiscuG(DiscuGroupPo dg) {
		Map<String, Object> node = new HashMap<String, Object>();
		node.put(NODE_ID, dg.getId());
		node.put(NODE_NAME, dg.getName());
		node.put("qtip", dg.getName());
		node.put(NODE_LEAF, true);
		// 当id大于0时为普通讨论组,id小于0时为部门讨论组
		if (dg.getId() >= 0)
			node.put(NODE_ICON, "/static/images/extIcons/comments.png");
		else
			node.put(NODE_ICON, "/static/js/im/style/images/icon_chat.png");
		node.put(NODE_ATTR_OWNERID, dg.getOwnerId());
		return node;
	}

	/**
	 * 将部门用户集合转换成树节点信息集合（多个）
	 *
	 * @param groupMemberList
	 *            部门用户集合
	 * @param onlineIdList
	 *            在线用户ID集合
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @param id
	 *            当前用户ID
	 * @param exceptSelf
	 *            是否需要排除当前用户本身
	 * @return 树节点列表
	 */
	public static List<Map<String, Object>> convertDepMemberList(
			List<DepMemberPo> groupMemberList, List<Long> onlineIdList,
			String idFix, boolean exceptSelf, Long id) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (DepMemberPo gm : groupMemberList) {
			if (exceptSelf && id.longValue() == gm.getUser().getId().longValue()) {
				continue;
			}
			Boolean isOnline = onlineIdList.contains(gm.getUser().getId());
			Map<String, Object> nodeMap = convertDepMember(gm, isOnline, idFix);
			nodeList.add(nodeMap);
		}
		return nodeList;
	}
	/**
	 * 即时通讯获取单个用户节点
	 * @param user
	 * @param isOnline
	 * @param idFix
	 * @return
	 */
	public static Map<String, Object> convertUser(Users user, Boolean isOnline, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, idFix + "-" + user.getId());
		nodeMap.put(NODE_NAME, user.getRealName());
		nodeMap.put(NODE_ATTR_REALEMAIL, user.getRealEmail());
		nodeMap.put(NODE_LEAF, true);
		nodeMap.put(NODE_ICON, getAvatarSrc(user.getImage1()));
		nodeMap.put(NODE_ATTR_ONLINE, isOnline);
		if (!isOnline) {
			nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
		}
		return nodeMap;
	}
	/**
	 * 将部门用户转换成树节点（单个）
	 *
	 * @param gm
	 *            部门用户
     * @param isOnline
     *            是否在线
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点
	 */
	public static Map<String, Object> convertDepMember(DepMemberPo gm, Boolean isOnline, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, idFix + "-" + gm.getUser().getId());
		nodeMap.put(NODE_NAME, gm.getUser().getRealName());
		nodeMap.put(NODE_ATTR_REALEMAIL, gm.getUser().getRealEmail());
		nodeMap.put(NODE_LEAF, true);
		nodeMap.put(NODE_ICON, getAvatarSrc(gm.getUser().getImage1()));
		nodeMap.put(NODE_ATTR_ONLINE, isOnline);
		if(!isOnline) {
			nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
		}
		nodeMap.put(NODE_ATTR_RELID, gm.getId());
		// nodeMap.put(NODE_ATTR_PCHAIN, gm.getOrganization().getParentKey());
		return nodeMap;
	}

	/**
	 * 将部门信息集合转换成树节点集合(多个)
	 *
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点集合
	 */
	public static List<Map<String, Object>> convertDepList(
			List<DepartmentPo> groupList, String idFix) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (DepartmentPo gi : groupList) {
			Map<String, Object> nodeMap = convertDep(gi, idFix);
			nodeList.add(nodeMap);
		}
		return nodeList;
	}

	/**
	 * 将部门信息转换成树节点
	 *
	 * @param gi
	 *            部门信息
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点
	 */
	public static Map<String, Object> convertDep(DepartmentPo gi, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		// 需要判断是否为叶子节点
		// boolean isLeaf = checkLeaf(gi);
		nodeMap.put(NODE_ID, idFix + "-" + gi.getId());
		nodeMap.put(NODE_NAME, gi.getName());
		nodeMap.put(NODE_LEAF, false);
		nodeMap.put(NODE_EXPANDABLE, true);
		// nodeMap.put("group", true);
		// nodeMap.put(NODE_ICON, "");//"/static/js/talklib/images/group.gif");
		nodeMap.put(NODE_ICONCLS, "no-icon");
		// nodeMap.put(NODE_ATTR_BLINKLIST, new ArrayList<String>());
		return nodeMap;
	}

	/**
	 * 将自定义组集合转换成树节点的集合
	 *
	 * @param ctmGList
	 *            自定义组集合
	 * @param idFix
	 *            id前缀(避免树节点ID冲突)
	 * @return 树节点集合
	 */
	public static List<Map<String, Object>> convertCtmGList(
			List<CustomGroupPo> ctmGList, String idFix) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		// nodeList.add(onlineCtmG(idFix)); // 添加在线联系人组
		for (CustomGroupPo ctmG : ctmGList) {
			Map<String, Object> node = convertCtmG(ctmG, idFix);
			nodeList.add(node);
		}
		return nodeList;
	}

	public static List<Map<String, Object>> convertCtmGListA(
			List<CustomGroupPo> ctmGList, String idFix) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (CustomGroupPo ctmG : ctmGList) {
			Map<String, Object> node = convertCtmG(ctmG, idFix);
			nodeList.add(node);
		}
		return nodeList;
	}

	/**
	 * 将自定义组集合转换成树节点的集合,用于右键菜单 ydm
	 *
	 * @param ctmGList
	 *            用户所有联系人分组
	 * @param gId
	 *            选中联系人所属分组ID
	 * @param idFix
	 * @return
	 */
	public static List<Map<String, Object>> convertCtmGList(
			List<CustomGroupPo> ctmGList, Long gId, String idFix) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (CustomGroupPo ctmG : ctmGList) {
			Map<String, Object> node = convertCtmG(ctmG, gId, idFix);
			nodeList.add(node);
		}
		return nodeList;
	}

	/**
	 * 将用户自定义组转换成树节点
	 *
	 * @param ctmG
	 *            用户自定义组
	 * @param idFix
	 *            id前缀(避免树节点ID冲突)
	 * @return 树节点
	 */
	public static Map<String, Object> convertCtmG(CustomGroupPo ctmG,
			String idFix) {
		Map<String, Object> node = new HashMap<String, Object>();
		// boolean isLeaf = checkLeaf(ctmG);
		node.put(NODE_ID, idFix + "-" + ctmG.getId());
		node.put(NODE_NAME, ctmG.getName());
		node.put(NODE_ICONCLS, "no-icon");
		node.put(NODE_LEAF, false);
		node.put(NODE_EXPANDABLE, true);
		node.put("system", ctmG.isSystem());
		return node;
	}

	/**
	 * 将用户联系人分组转换成树节点,用于右键菜单 ydm
	 *
	 * @param ctmG
	 *            用户联系人分组
	 * @param gId
	 *            选中用户所属的分组ID
	 * @param idFix
	 *            ID前缀(避免树节点ID冲突)
	 * @return 树节点
	 */
	public static Map<String, Object> convertCtmG(CustomGroupPo ctmG, Long gId,
			String idFix) {
		Map<String, Object> node = new HashMap<String, Object>();
		node.put(NODE_ID, idFix + "-" + ctmG.getId());
		node.put(NODE_NAME, ctmG.getName());
		node.put("checked", false);
		if (gId != null && gId.equals(ctmG.getId())) {
			node.put("checked", true);
		}
		return node;
	}

	public static Map<String, Object> onlineCtmG(String idFix) {
		Map<String, Object> node = new HashMap<String, Object>();
		node.put(NODE_ID, idFix + "-online");
		node.put(NODE_NAME, "在线联系人");
		node.put(NODE_ICONCLS, "no-icon");
		node.put(NODE_LEAF, false);
		node.put(NODE_EXPANDABLE, true);
		node.put("system", true);
		return node;
	}

	/**
	 * 将自定义组的成员集合装换成树节点集合（多个）
	 *
	 * @param ctmGMList
	 *            组成员集合
	 * @param idFix
	 *            id前缀(避免树节点ID冲突)
	 * @param onlineIdList
	 *            在线用户ID集合
	 * @return 树节点集合
	 */
	public static List<Map<String, Object>> convertCtmGMList(
			List<CtmGroupMemberPo> ctmGMList, List<Long> onlineIdList,
			String idFix) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (CtmGroupMemberPo ctmGM : ctmGMList) {
			Boolean isOnline = onlineIdList.contains(ctmGM.getUserId());
			Map<String, Object> node = convertCtmGM(ctmGM, isOnline, idFix);
			nodeList.add(node);
		}
		return nodeList;
	}

	/**
	 * 将自定义组成员转换成树节点(单个)
	 *
	 * @param ctmGM
	 *            组成员
     * @param isOnline
     *            是否在线
	 * @return 树节点
	 */
	public static Map<String, Object> convertCtmGM(CtmGroupMemberPo ctmGM,
			Boolean isOnline, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, idFix + "-" + ctmGM.getUserId());
		nodeMap.put("gId", ctmGM.getGroupId());
		nodeMap.put(NODE_NAME, ctmGM.getRealName());
		nodeMap.put(NODE_ATTR_REALEMAIL, ctmGM.getMail());
		nodeMap.put(NODE_LEAF, true);
		nodeMap.put(NODE_ICON,
				(ctmGM.getImage() == null || ctmGM.getImage().equals("")|| ctmGM.getImage().trim().toLowerCase().equals("null")) ? WebConfig.userPortrait + "image.jpg"
						:  (ctmGM.getImage().contains("/")) ? ctmGM.getImage() : WebConfig.userPortrait + ctmGM.getImage());
	    nodeMap.put(NODE_ATTR_ONLINE, isOnline);
	    if (!isOnline) {
	      nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
	    }
		nodeMap.put(NODE_ATTR_RELID, ctmGM.getId());
		return nodeMap;
	}

		/**
	 * 将自定义组的成员集合装换成树节点集合（多个）,外部联系人
	 *
	 * @param ctmGMList
	 *            组成员集合
	 * @param idFix
	 *            id前缀(避免树节点ID冲突)
	 * @param onlineIdList
	 *            在线用户ID集合
	 * @return 树节点集合
	 */
	public static List<Map<String, Object>> convertCtmGMOutList(
			List<AddressBean> ctmGMList, List<Long> onlineIdList, String idFix) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (AddressBean ctmGM : ctmGMList) {
			Boolean isOnline = true;
		    if (ctmGM.getId().split("-")[0].equals("ctmgm")) {
		    	isOnline = onlineIdList.contains(Long.parseLong(ctmGM.getId().split("-")[2]));
		    }
			Map<String, Object> node = convertCtmGM(ctmGM, isOnline, idFix);
			nodeList.add(node);
		}
		return nodeList;
	}
	/**
	 * 将自定义组成员转换成树节点(单个),外部联系人
	 *
	 * @param ctmGM
	 *            组成员
     * @param isOnline
     *            是否在线
	 * @return 树节点
	 */
	public static Map<String, Object> convertCtmGM(AddressBean ctmGM,
			Boolean isOnline, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, ctmGM.getId());
		nodeMap.put(NODE_NAME, ctmGM.getRealName());		
		nodeMap.put(NODE_LEAF, true);
		nodeMap.put(NODE_ICON, getAvatarSrc(ctmGM.getImage()));
	    nodeMap.put(NODE_ATTR_ONLINE, isOnline);
	    if (!isOnline) {
	      nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
	    }
		nodeMap.put(NODE_ATTR_REALNAME, ctmGM.getRealName());
		nodeMap.put(NODE_ATTR_USERNAME, ctmGM.getUserName());
		nodeMap.put(NODE_ATTR_RELID, Long.parseLong(ctmGM.getId().split("-")[2]));
		nodeMap.put(NODE_ATTR_REALEMAIL, ctmGM.getRealEmail());
		nodeMap.put(NODE_ATTR_MOBILE, ctmGM.getMobile());
		String mobile=ctmGM.getMobile();
		if (mobile!=null && mobile.length()>=10)
		{
			nodeMap.put(NODE_VIEW_MOBILE, mobile.substring(0,4)+"...."+mobile.substring(8));
		}
		else
		{
			nodeMap.put(NODE_VIEW_MOBILE, "无");
		}
		return nodeMap;
	}
	/**
	 * 将部门信息集合转换成树节点集合(多个)
	 *
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点集合
	 */
	public static List<Map<String, Object>> convertOrgList(
			List<Organizations> groupList, String idFix) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (Organizations gi : groupList) {
			Map<String, Object> nodeMap = convertOrg(gi, idFix);
			nodeList.add(nodeMap);
		}
		return nodeList;
	}

	/**
	 * 将部门信息转换成树节点
	 *
	 * @param gi
	 *            部门信息
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点
	 */
	public static Map<String, Object> convertOrg(Organizations gi, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, idFix + "-" + gi.getId());
		nodeMap.put(NODE_NAME, gi.getName());
		nodeMap.put(NODE_LEAF, false);
		nodeMap.put(NODE_EXPANDABLE, true);
		nodeMap.put(NODE_ICONCLS, "no-icon");
		// nodeMap.put(NODE_ATTR_BLINKLIST, new ArrayList<String>());
		return nodeMap;
	}

	/**
	 * 将部门用户集合转换成树节点信息集合（多个）
	 *
	 * @param groupMemberList
	 *            部门用户集合
	 * @param onlineIdList
	 *            在线用户ID集合
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @param id
	 *            当前用户ID
	 * @param exceptSelf
	 *            是否需要排除当前用户本身
	 * @return 树节点列表
	 */
	public static List<Map<String, Object>> convertOrgMemberList(
			List<DepMemberPo> groupMemberList, List<Long> onlineIdList,
			String idFix, boolean exceptSelf, Long id) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (DepMemberPo gm : groupMemberList) {
			if (exceptSelf && id.longValue() == gm.getUser().getId().longValue()) {
				continue;
			}
			if (gm.getUser().getRealEmail() == null
					|| gm.getUser().getRealEmail().trim().equals("")) {
				continue;
			}
			Boolean isOnline = onlineIdList.contains(gm.getUser().getId());
			Map<String, Object> nodeMap = convertOrgMember(gm, isOnline, idFix);
			nodeList.add(nodeMap);
		}
		return nodeList;
	}

	/**
	 * 将部门用户转换成树节点（单个）
	 *
	 * @param gm
	 *            部门用户
     * @param isOnline
     *            是否在线
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点
	 */
	public static Map<String, Object> convertOrgMember(DepMemberPo gm, Boolean isOnline, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, idFix + "-" + gm.getOrganization().getId() + '-'
				+ gm.getUser().getId()); // 节点Id值如：g-部门Id-用户Id
		nodeMap.put(NODE_NAME, gm.getUser().getRealName());
		nodeMap.put(NODE_LEAF, true);
		nodeMap.put(NODE_ICON, getAvatarSrc(gm.getUser().getImage1()));
	    nodeMap.put(NODE_ATTR_ONLINE, isOnline);
	    if (!isOnline) {
	      nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
	    }
		nodeMap.put(NODE_ATTR_RELID, gm.getId());
		nodeMap.put(NODE_ATTR_REALEMAIL, gm.getUser().getRealEmail());
		nodeMap.put(NODE_ATTR_REALNAME, gm.getUser().getRealName());
		return nodeMap;
	}

//	/**
//	 * 将项目组集合转换成树节点的集合
//	 *
//	 * @param teamGList
//	 *            自定义组集合
//	 * @param idFix
//	 *            id前缀(避免树节点ID冲突)
//	 * @return 树节点集合
//	 */
//	public static List<Map<String, Object>> convertTeamGList(
//			List<Groups> teamGList, String idFix) {
//		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
//		for (Groups teamG : teamGList) {
//			Map<String, Object> node = convertTeamG(teamG, idFix);
//			nodeList.add(node);
//		}
//		return nodeList;
//	}

//	/**
//	 * 将用户项目组转换成树节点
//	 *
//	 * @param ctmG
//	 *            用户项目组
//	 * @param idFix
//	 *            id前缀(避免树节点ID冲突)
//	 * @return 树节点
//	 */
//	public static Map<String, Object> convertTeamG(Groups teamG, String idFix) {
//		Map<String, Object> node = new HashMap<String, Object>();
//		// boolean isLeaf = checkLeaf(ctmG);
//		node.put(NODE_ID, idFix + "-" + teamG.getId());
//		node.put(NODE_NAME, teamG.getName());
//		node.put(NODE_ICONCLS, "no-icon");
//		node.put(NODE_LEAF, false);
//		node.put(NODE_EXPANDABLE, true);
//		return node;
//	}

//	/**
//	 * 将项目组的成员集合装换成树节点集合（多个）
//	 *
//	 * @param teamGMList
//	 *            项目组成员集合
//	 * @param idFix
//	 *            id前缀(避免树节点ID冲突)
//	 * @param onlineIdList
//	 *            在线用户ID集合
//	 * @param gId
//	 *            项目组的Id，防止不同项目组下用户Id重复
//	 * @return 树节点集合
//	 */
//	public static List<Map<String, Object>> convertTeamGMList(
//			List<Users> teamGMList, List<Long> onlineIdList, String idFix,
//			Long gId) {
//		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
//		for (Users teamGM : teamGMList) {
//			Map<String, Object> node = convertTeamGM(teamGM, onlineIdList,
//					idFix, gId);
//			nodeList.add(node);
//		}
//		return nodeList;
//	}

//	/**
//	 * 将项目组成员转换成树节点(单个)
//	 *
//	 * @param teamGM
//	 *            项目组成员
//	 * @param onlineIdList
//	 *            在线成员ID集合
//	 * @param gId
//	 *            项目组ID，防止不同项目组下的用户Id重复
//	 * @return 树节点
//	 */
//	public static Map<String, Object> convertTeamGM(Users teamGM,
//			List<Long> onlineIdList, String idFix, Long gId) {
//
//		Map<String, Object> nodeMap = new HashMap<String, Object>();
//		nodeMap.put(NODE_ID, idFix + "-" + gId + "-" + teamGM.getId()); // 节点Id值如：teamgm-组Id-用户Id
//		nodeMap.put(NODE_NAME, teamGM.getRealName());
//		nodeMap.put(NODE_LEAF, true);
//		nodeMap.put(NODE_ICON, WebConfig.userPortrait + teamGM.getImage());
//
//		if (onlineIdList.contains(teamGM.getId())) {
//			nodeMap.put(NODE_ATTR_ONLINE, true);
//		} else {
//			nodeMap.put(NODE_ATTR_ONLINE, false);
//			nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
//		}
//		nodeMap.put(NODE_ATTR_RELID, teamGM.getId());
//		nodeMap.put(NODE_ATTR_REALEMAIL, teamGM.getRealEmail());
//		nodeMap.put(NODE_ATTR_REALNAME, teamGM.getRealName());
//		return nodeMap;
//	}

	/*
	 * 将会被筛选的用户集合转换成树节点信息集合
	 */
	public static List<Map<String, Object>> convertFilterMemberList(
			List<Users> memberList, List<Long> onlineIdList, String idFix,
			Long id, boolean exceptSelf) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (Users user : memberList) {
			Map<String, Object> nodeMap = new HashMap<String, Object>();
			nodeMap.put(NODE_ID, user.getId());
			nodeMap.put(NODE_NAME, user.getRealName() + "(" + user.getDuty() + ")");
			nodeMap.put(NODE_LEAF, true);
			nodeMap.put(NODE_ICON, getAvatarSrc(user.getImage1()));
			nodeMap.put(NODE_ATTR_REALEMAIL, user.getRealEmail());
			nodeMap.put(NODE_ATTR_REALNAME, user.getRealName());
			nodeMap.put(NODE_ATTR_USERNAME, user.getUserName());
			if (onlineIdList.contains(user.getId())) {
				nodeMap.put(NODE_ATTR_ONLINE, true);
			} else {
				nodeMap.put(NODE_ATTR_ONLINE, false);
				nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
			}
			nodeMap.put(NODE_ATTR_RELID, user.getId());
			nodeList.add(nodeMap);
		}
		return nodeList;
	}

	/**
	 * 将部门用户集合转换成树节点信息集合（多个）
	 *
	 * @param groupMemberList
	 *            部门用户集合
	 * @param onlineIdList
	 *            在线用户ID集合
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @param id
	 *            当前用户ID
	 * @param exceptSelf
	 *            是否需要排除当前用户本身
	 * @return 树节点列表
	 */
	public static List<Map<String, Object>> convertOrgMemberListWithOrg(
			List<DepMemberPo> groupMemberList, List<Long> onlineIdList,
			String idFix, boolean exceptSelf, Long id) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (DepMemberPo gm : groupMemberList) {
			if (exceptSelf && id.longValue() == gm.getUser().getId().longValue()) {
				continue;
			}
			if (gm.getOrganization() == null || gm.getUser() == null) { // 容错处理，如果这个人的组织获取不到或者用户信息获取不到就不执行转换
				continue;
			}
			Boolean isOnline = onlineIdList.contains(gm.getUser().getId());
			Map<String, Object> nodeMap = convertOrgMemberWithOrg(gm, isOnline, idFix);
			nodeList.add(nodeMap);
		}
		return nodeList;
	}

	/**
	 * 将部门用户转换成树节点（单个）
	 *
	 * @param gm
	 *            部门用户
	 * @param onlineIdList
	 *            在线用户ID集合
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点
	 */
	public static Map<String, Object> convertOrgMemberWithOrg(DepMemberPo gm,
			Boolean isOnline, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, idFix + "-" + gm.getOrganization().getId() + '-'
				+ gm.getUser().getId()); // 节点Id值如：g-部门Id-用户Id
		nodeMap.put(NODE_NAME, gm.getUser().getRealName()
//				+ "("+ gm.getUser().getDuty() + ")"//不需要职务
				);
		nodeMap.put(NODE_LEAF, true);
		nodeMap.put(NODE_ICON, getAvatarSrc(gm.getUser().getImage1()));
		nodeMap.put(NODE_ATTR_REALEMAIL, gm.getUser().getRealEmail());
		nodeMap.put(NODE_ATTR_REALNAME, gm.getUser().getRealName());
		nodeMap.put(NODE_ATTR_USERNAME, gm.getUser().getUserName());
		nodeMap.put(NODE_ATTR_MOBILE, gm.getUser().getMobile());
		String mobile=gm.getUser().getMobile();
		if (mobile!=null && mobile.length()>=10)
		{
			nodeMap.put(NODE_VIEW_MOBILE, mobile.substring(0,4)+"...."+mobile.substring(8));
		}
		else
		{
			nodeMap.put(NODE_VIEW_MOBILE, "无");
		}
		nodeMap.put(NODE_ATTR_ONLINE, isOnline);
		if (!isOnline) {
		    nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
		}
		nodeMap.put(NODE_ATTR_RELID, gm.getId());
		return nodeMap;
	}

	// ///////////////////////////////修改（日志）///////////////////////////////////
	/**
	 * 将公司信息集合转换成树节点集合(多个)
	 *
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点集合
	 */
	public static List<Map<String, Object>> convertCompanyList(
			List<Company> companyList, String idFix) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (Company ci : companyList) {
			Map<String, Object> nodeMap = convertComp(ci, idFix);
			nodeList.add(nodeMap);
		}
		return nodeList;
	}

	/**
	 * 将公司信息转换成树节点
	 *
	 * @param ci
	 *            公司信息
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点
	 */
	public static Map<String, Object> convertComp(Company ci, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, idFix + "-" + ci.getId());
		nodeMap.put(NODE_NAME, ci.getName());
		nodeMap.put(NODE_LEAF, false);
		nodeMap.put(NODE_EXPANDABLE, true);
		nodeMap.put(NODE_ICONCLS, "no-icon");
		return nodeMap;
	}

	/**
	 * 将搜索得到的用户转换成树节点集合（多个）
	 *
	 * @param searchUsersList
	 *            搜索得到的用户集合
	 * @param idFix
	 *            id前缀(避免树节点ID冲突)
	 * @param onlineIdList
	 *            在线用户ID集合
	 * @param exceptSelf
	 *            是否过滤自己
	 * @param UId
	 *            用户的Id，用于过滤用户本身
	 * @return 树节点集合
	 */
	public static List<Map<String, Object>> convertSearchUsers(
			List<Users> searchUsersList, List<Long> onlineIdList, String idFix,
			boolean exceptSelf, Long UId) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (Users searchUsers : searchUsersList) {
			if (exceptSelf && UId.longValue() == searchUsers.getId().longValue()) {
				continue;
			}
			Boolean isOnline = onlineIdList.contains(searchUsers.getId());
			Map<String, Object> node = convertSearchU(searchUsers, isOnline, idFix);
			nodeList.add(node);
		}
		return nodeList;
	}

	/**
	 * 将搜索得到的用户转换成树节点(单个)
	 *
	 * @param searchUser
	 *            搜索得到的用户
	 * @param onlineIdList
	 *            在线成员ID集合
	 * @param idFix
	 *            id前缀(避免树节点ID冲突)
	 * @return 树节点
	 */
	public static Map<String, Object> convertSearchU(Users searchUser,
			Boolean isOnline, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, idFix + "-" + searchUser.getId()); // 节点Id值如：gm-用户Id
		if( searchUser.getCompany()!=null)
		nodeMap.put(NODE_NAME, searchUser.getRealName() + "("
				+ searchUser.getCompany().getName() + ")");
		else nodeMap.put(NODE_NAME, searchUser.getRealName() + "(超级管理员)");
		nodeMap.put(NODE_LEAF, true);
		nodeMap.put(NODE_ICON, getAvatarSrc(searchUser.getImage1()));
		nodeMap.put(NODE_ATTR_ONLINE, isOnline);
		if (!isOnline) {
			nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
		}
		nodeMap.put(NODE_ATTR_RELID, searchUser.getId());
		nodeMap.put(NODE_ATTR_REALEMAIL, searchUser.getRealEmail());
		nodeMap.put(NODE_ATTR_USERNAME, searchUser.getUserName());
		nodeMap.put(NODE_ATTR_REALNAME, searchUser.getRealName());
		return nodeMap;
	}

	/**
	 * 将部门用户集合转换成树节点信息集合（多个）（用于日志管理）
	 *
	 * @param groupMemberList
	 *            部门用户集合
	 * @param onlineIdList
	 *            在线用户ID集合
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @param id
	 *            当前用户ID
	 * @param exceptSelf
	 *            是否需要排除当前用户本身
	 * @return 树节点列表
	 */
	public static List<Map<String, Object>> convertAllOrgMemberListWithOrg(
			List<DepMemberPo> groupMemberList, List<Long> onlineIdList,
			String idFix, boolean exceptSelf, Long id) {
		List<Map<String, Object>> nodeList = new ArrayList<Map<String, Object>>();
		for (DepMemberPo gm : groupMemberList) {
			if (exceptSelf
					&& id.longValue() == gm.getUser().getId().longValue()) {
				continue;
			}
			if (gm.getOrganization() == null || gm.getUser() == null) { // 容错处理，如果这个人的组织获取不到或者用户信息获取不到就不执行转换
				continue;
			}
			Boolean isOnline = onlineIdList.contains(gm.getUser().getId());
			Map<String, Object> nodeMap = convertAllOrgMemberWithOrg(gm, isOnline, idFix);
			nodeList.add(nodeMap);
		}
		return nodeList;
	}

	/**
	 * 将部门用户转换成树节点（单个）
	 *
	 * @param gm
	 *            部门用户
	 * @param isOnline
	 *            是否在线
	 * @param idFix
	 *            树节点的ID前缀，如果不加，会导致树节点点击问题(前缀中注意不要包含符号"-")
	 * @return 树节点
	 */
	public static Map<String, Object> convertAllOrgMemberWithOrg(
			DepMemberPo gm, Boolean isOnline, String idFix) {
		Map<String, Object> nodeMap = new HashMap<String, Object>();
		nodeMap.put(NODE_ID, idFix + "-" + gm.getOrganization().getId() + '-'
				+ gm.getUser().getId()); // 节点Id值如：g-部门Id-用户Id
		nodeMap.put(NODE_NAME, gm.getUser().getRealName() + "("
				+ gm.getUser().getDuty() + ")");
		nodeMap.put(NODE_LEAF, true);
		nodeMap.put(NODE_ICON, getAvatarSrc(gm.getUser().getImage1()));
		nodeMap.put(NODE_ATTR_REALEMAIL, gm.getUser().getRealEmail());
		nodeMap.put(NODE_ATTR_REALNAME, gm.getUser().getRealName());
		nodeMap.put(NODE_ATTR_USERNAME, gm.getUser().getUserName());
	    nodeMap.put(NODE_ATTR_ONLINE, isOnline);
	    if (!isOnline) {
	      nodeMap.put(NODE_ICONCLS, NO_ONLINE_CSS);
	    }
		nodeMap.put(NODE_ATTR_RELID, gm.getUser().getId());
		return nodeMap;
	}
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * 获得用户头像
	 * 
	 * @param avatar
	 * @return
	 */
	private static String getAvatarSrc(String avatar) {
		if (null == avatar || avatar.trim().equals("") || avatar.trim().toLowerCase().equals("null")) {
			avatar = WebConfig.userPortrait + "image.jpg";
		} else if (!avatar.contains("/")) {
			avatar = WebConfig.userPortrait + avatar;
		}else if (avatar.endsWith("null")) {
			avatar = WebConfig.userPortrait + avatar.replace("null", "image.jpg");
		}
		return avatar;
	}
}
