package apps.transmanager.weboffice.servlet.server;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.constants.both.ServletConst;
import apps.transmanager.weboffice.constants.server.ErrorCons;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.handler.FilesHandler;
import apps.transmanager.weboffice.service.handler.FilesOpeHandler;
import apps.transmanager.weboffice.service.handler.UserOnlineHandler;
import apps.transmanager.weboffice.service.server.UserService;
import apps.transmanager.weboffice.util.server.JSONTools;

public class FileOpeServlet  extends AbstractServlet{
	
	@Override
	protected String handleService(HttpServletRequest request, HttpServletResponse response, HashMap<String, Object> jsonParams) throws Exception,  IOException
	{
		
		HashMap<String, Object> param = (HashMap<String, Object>) jsonParams.get(ServletConst.PARAMS_KEY);
		String method = (String)jsonParams.get(ServletConst.METHOD_KEY);             // 
//		System.out.println("method2222222=========="+method);
		String account = (String) param.get("account");
		UserService userService = (UserService) ApplicationContext.getInstance().getBean(UserService.NAME);
        //下面这句有问题，这个user的getFawen是null
        //为何不使用UserService userService = getUserService();
        //Users user = userService.getUser(userId);

		Users user = (Users) request.getSession().getAttribute("userKey");
		if(user ==null && account!=null)
		{
			Users userFromMobile = userService.getUser(account);		// 非web端发来的请求，错误做法，将删除
			user = userFromMobile;
		}
		if (user ==null)
		{
			//要加容錯信息
			return null;
		}
		if (user != null) // 还需要进行登录认证
		{
			if (method == null || method.length() <= 0)                                 // json请求方法参数不可以为null
			{
				return  JSONTools.convertToJson(ErrorCons.JSON_FORMAT_ERROR, null);
			}
			if (method.equals(ServletConst.AUDIT_PIC_INFO_ACTION))//移动那边不能调试，临时放到这里——孙爱华
	        {
				return FilesHandler.getAuditPic(request, response, jsonParams);
	        }
			// 除了登录外，目前其他操作都需要验证用户的登录token是否有效。
			if (!UserOnlineHandler.isValidate(jsonParams) && request.getSession().getAttribute("userKey")==null)//目前只移动端检查token
			{
				return  JSONTools.convertToJson(ErrorCons.SYSTEM_TOKEN_ERROR, null);
			}
			
			if (method.equals(ServletConst.GET_FILE_LIST_ACTION)) // 获取文件列表
			{
				return FilesOpeHandler.getFileList(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.GET_FILE_BAOSONG_ACTION)) // 获取文件列表
			{
				return FilesOpeHandler.getFileBaosong(request, response, jsonParams,user);
			}
			
			if (method.equals(ServletConst.UPLOAD_FILES_ACTION)) // 文件上传
			{
				return FilesOpeHandler.uploadFile(request, response, jsonParams,user);
			}	
			if (method.equals(ServletConst.DOWNLOAD_FILE_ACTION)) // 文件下载
			{
				return FilesOpeHandler.downloadFile(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.DOWNLOAD_FILE_ACTION)) // 文件下载
			{
				return FilesOpeHandler.downloadFileNew(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.CREATE_FOLDER_ACTION)) // 创建文件夹
			{
				return FilesOpeHandler.createFolder(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.RENAME_ACTION)) // 重命名
			{
				return FilesOpeHandler.rename(request, response, jsonParams,user);
			}if (method.equals(ServletConst.DELETE_ACTION)) // 删除
			{
				return FilesOpeHandler.delete(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.COPY_FILE_ACTION)) // 复制
			{
				return FilesOpeHandler.copyFile(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MOVE_FILE_ACTION)) // 移动
			{
				return FilesOpeHandler.moveFile(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.GET_FILE_PROPERTIES)) // 属性
			{
				return FilesOpeHandler.getFileProperties(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.GET_FILE_VERSIONS)) // 获取版本列表
			{
				return FilesOpeHandler.getFileVersions(request, response, jsonParams,user);
			}if (method.equals(ServletConst.CREATE_VERSION_ACTION)) // 创建版本
			{
				return FilesOpeHandler.creatFileVersion(request, response, jsonParams,user);
			}if (method.equals(ServletConst.RESTORY_FILE_VERSIONS)) // 恢复版本
			{
				return FilesOpeHandler.restoryVersions(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.ROLLBACK_FILE_VERSIONS)) // 回滚版本
			{
				return FilesOpeHandler.rollbackVersions(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.DELETE_FILE_VERSIONS)) // 删除版本
			{
				return FilesOpeHandler.delVersion(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.DELETE_FILE_ALL_VERSIONS)) // 删除所有版本
			{
				return FilesOpeHandler.delAllVersions(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.UPDATE_FILE_VERSIONMEMO)) // 修改摘要
			{
				return FilesOpeHandler.updateVersionMemo(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.DOWNLOAD_FILE_VERSION)) // 版本下载
			{
				return FilesOpeHandler.getVersion(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.FINALIZE_FILE_VERSION)) // 定稿
			{
				return FilesOpeHandler.finalizeVersion(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.ADD_TEAM_SPACE)) // 添加或修改群组
			{
				return FilesOpeHandler.addTeamSpace(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.GET_USERS_BY_TEAMID)) // 获取自定义组内成员
			{
				return FilesOpeHandler.getUsersByTeamId(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MODIFY_USERS_BY_TEAMID)) // 修改自定义组内成员
			{
				return FilesOpeHandler.modifyUsersByTeamId(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.DELETE_CUSTOMREAMS)) // 删除群组
			{
				return FilesOpeHandler.delCustomTeams(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.GET_SHARE_INFO_ACTION)) // 获取已经共享的信息
			{
				return FilesOpeHandler.getShareInfo(request, response, jsonParams,user);
			}
			
			if (method.equals(ServletConst.SHARE_FILES_ACTION)) // 设置共享的信息
			{
				return FilesOpeHandler.shareFiles(request, response, jsonParams,user);
			}
			
			if (method.equals(ServletConst.CANCEL_SHARE_ACTION)) // 取消共享
			{
				return FilesOpeHandler.cancelShare(request, response, jsonParams,user);
			}
			
			if(method.equals(ServletConst.GET_FILE_SHARELOG))
			{
				return FilesOpeHandler.getFileShareLog(request, response, jsonParams, user);
			}
			
			if (method.equals(ServletConst.UNDELETE_FILES_ACTION)) // 还原文件
			{
				return FilesOpeHandler.undelete(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.DELETE_FILES_FOREVER)) // 清空或永久删除
			{
				return FilesOpeHandler.clear(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.DELETE_ALLFILES_FOREVER)) // 清空回收站
			{
				return FilesOpeHandler.clearall(request, response, jsonParams,user,"all");
			}
			
			
			if (method.equals(ServletConst.GET_FILE_UPLOAD_ACTION)) // 获取上传文件路径的文件列表
			{
				return FilesOpeHandler.getFileForUpload(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.GET_DRAFTS_ACTION)) // 获取草稿列表
			{
				return FilesOpeHandler.getDrafts(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.GET_HADSENDS_ACTION) || method.equals(ServletConst.GET_DONE_ACTION)) // 获取已送列表
			{
				return FilesOpeHandler.getDone(request, response, jsonParams,user);//已办
			}
			if (method.equals(ServletConst.GET_COLLECT_ACTION)) // 获取收藏列表
			{
				return FilesOpeHandler.getCollect(request, response, jsonParams,user);//收藏
			}

			if (method.equals(ServletConst.GET_WAITWORKS_ACTION)||method.equals(ServletConst.GET_TODO_ACTION)) // 获取待办列表
			{
				return FilesOpeHandler.getTodo(request, response, jsonParams,user);//待办
			}
			if (method.equals(ServletConst.GET_TOREAD_ACTION))
			{
				return FilesOpeHandler.getToread(request, response, jsonParams,user);//收阅
			}
			if (method.equals(ServletConst.GET_HADREAD_ACTION))
			{
				return FilesOpeHandler.getHadread(request, response, jsonParams,user);//收阅
			}
			if (method.equals(ServletConst.GET_ENDWORKS_ACTION))
			{
				return FilesOpeHandler.getEndWorks(request, response, jsonParams,user);//办结
			}
			
			if (method.equals(ServletConst.GET_FILETYPES_ACTION))//签批的文档类别
			{
				return FilesOpeHandler.getAuditFileTypes(request, response, jsonParams,user);//签批的文档类别
			}
			if (method.equals(ServletConst.GET_MYQUEST_ACTION))
			{
				return FilesOpeHandler.getMyquestfiles(request, response, jsonParams,user);//我的请求
			}
			
//			if (method.equals(ServletConst.GET_HADWORKS_ACTION)) // 获取已办列表
//			{
//				FilesOpeHandler.getHadWorks(request, response, jsonParams,user);
//				return;
//			}
			if (method.equals(ServletConst.GET_SEARCHFILES_ACTION)) // 获取搜索结果
			{
				return FilesOpeHandler.getSearch(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.DOWNLOAD_PERMISION_ACTION)) // 获取下载文件的权限
			{
				return FilesOpeHandler.downloadPermision(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.CANREAD_PERMISSION_ACTION)) //获取文件可读权限
			{
				return FilesOpeHandler.getReadPermission(request, response, jsonParams, user);
			}
			if (method.equals(ServletConst.GET_APPROVALINFO_ACTION)) // 根据ID获取流程数据
			{
				return FilesOpeHandler.getApprovalinfo(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.GET_WEBCONTENT_ACTION)) // 根据ID获取流程业务详情
			{
				return FilesOpeHandler.getWebcontent(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_SAVEDRAFT_ACTION)) //保存草稿
			{
				return FilesOpeHandler.saveDraft(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_SENDSIGN_ACTION)) //送签
			{
				return FilesOpeHandler.sendSign(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_GETAPPROVALSAVE_ACTION)) //获取草稿
			{
				return FilesOpeHandler.getApprovalsave(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_DELSIGNINFO_ACTION))//删除草稿
			{
				return FilesOpeHandler.delSignInfo(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_SUCCESSSIGN_ACTION))//成文
			{
				return FilesOpeHandler.successSign(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_ENDSIGN_ACTION))//终止
			{
				return FilesOpeHandler.endSign(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_UNDOSIGN_ACTION))//反悔
			{
				return FilesOpeHandler.undoSign(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_GETHISTORY_ACTION))//历史
			{
				return FilesOpeHandler.getHistory(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_GETCURRENTPERMIT_ACTION))//获取签批窗口的内容
			{
				return FilesOpeHandler.getCurrentPermit(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_MODIFYSIGNREAD_ACTION))//处理签阅
			{
				return FilesOpeHandler.modifySignRead(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_BACKSENDSIGN_ACTION))//处理返还送文人
			{
				return FilesOpeHandler.backSendSign(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_MODIFYSIGNSEND_ACTION))//处理签批，下一个步
			{
				return FilesOpeHandler.modifySignSend(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_RESENDSIGN_ACTION))//再次送审
			{
				return FilesOpeHandler.reSendSign(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.APPROVAL_SENDCOOPER_ACTION))//再次送审
			{
				return FilesOpeHandler.sendCooper(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MESSAGE_NEWLIST_ACTION))//获取当前人员的提醒信息列表
			{
				return FilesOpeHandler.getNewListMessages(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.SIGNREAL))//签收设置
			{
				return FilesOpeHandler.signreal(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.GETSIGNREAL))//获取签收情况
			{
				return FilesOpeHandler.getSignReal(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_DRAFT_ACTION))//获取交办事务草稿
			{
				return FilesOpeHandler.getTransDrafts(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_MYQUEST_ACTION))//获取我的交办事务
			{
				return FilesOpeHandler.getTransMyquestfiles(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_DONE_ACTION))//获取已办事务
			{
				return FilesOpeHandler.getTransDone(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_TODO_ACTION))//获取待办事务
			{
				return FilesOpeHandler.getTransTodo(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_SAVE_ACTION))//保存草稿
			{
				return FilesOpeHandler.transSave(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_GETSAVE_ACTION))//获取草稿
			{
				return FilesOpeHandler.getTransSave(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_COMMIT_ACTION))//事务提交
			{
				return FilesOpeHandler.transCommit(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_CONTENT_ACTION))//获取事务内容
			{
				return FilesOpeHandler.getTransWebcontent(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_INFO_ACTION))//获取事务详情
			{
				return FilesOpeHandler.getTransPermit(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_MODIFY_ACTION))//事务处理
			{
				return FilesOpeHandler.transModify(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_DELETE_ACTION))//事务删除
			{
				return FilesOpeHandler.transDelete(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_HISTORY_ACTION))//事务历史
			{
				return FilesOpeHandler.getTransHistosy(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_SIGN_ACTION))//事务签收设置
			{
				return FilesOpeHandler.transsignreal(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.TRANS_GETSIGN_ACTION))//获取事务签收情况
			{
				return FilesOpeHandler.getTransSignReal(request, response, jsonParams,user);
			}

            //公文收发

            if (method.equals(ServletConst.FAWEN_SEND_ACTION))
            {
                return FilesOpeHandler.getFawenSend(request, response, jsonParams,user);
            }
            if (method.equals(ServletConst.FAWEN_RECEIVE_ACTION))
            {
                return FilesOpeHandler.getFawenReceive(request, response, jsonParams,user);
            }
            if (method.equals(ServletConst.FAWEN_GET_DEPARTMENT))
            {
                return FilesOpeHandler.getFawenDepartment(request, response, jsonParams,user);
            }
           /* if (method.equals(ServletConst.FAWEN_SET_ACTION))
            {
                return FilesOpeHandler.setFawenUser(request, response, jsonParams,user);
            }
            if (method.equals(ServletConst.FAWEN_UNSET_ACTION))
            {
                return FilesOpeHandler.unsetFawenUser(request, response, jsonParams,user);
            }
            if (method.equals(ServletConst.FAWEN_GETUSER_ACTION))
            {
                return FilesOpeHandler.getFawenUser(request, response, jsonParams,user);
            }*/
            if (method.equals(ServletConst.FAWEN_SEND))
            {
                return FilesOpeHandler.sendFawen(request, response, jsonParams,user);
            }
            if (method.equals(ServletConst.FAWEN_RECEIVE))
            {
                return FilesOpeHandler.receiveFawen(request, response, jsonParams,user);
            }





			if (method.equals(ServletConst.MEET_DRAFT_ACTION))//会议草稿
			{
				return FilesOpeHandler.getMeetDrafts(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_MYQUEST_ACTION))//我的会议请求
			{
				return FilesOpeHandler.getMeetMyquestfiles(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_DONE_ACTION))//办结会议
			{
				return FilesOpeHandler.getMeetDone(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_TODO_ACTION))//待办会议
			{
				return FilesOpeHandler.getMeetTodo(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_SAVE_ACTION))//保存草稿
			{
				return FilesOpeHandler.meetSave(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_COMMIT_ACTION))//会议提交
			{
				return FilesOpeHandler.meetCommit(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_GETSAVE_ACTION))//获取草稿
			{
				return FilesOpeHandler.getMeetSave(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_CONTENT_ACTION))//获取会议内容
			{
				return FilesOpeHandler.getMeetWebcontent(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_INFO_ACTION))//获取会议详情
			{
				return FilesOpeHandler.getMeetPermit(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_MODIFY_ACTION))//会议处理
			{
				return FilesOpeHandler.meetModify(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_DELETE_ACTION))//会议删除
			{
				return FilesOpeHandler.meetDelete(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_BACKDETAIL_ACTION))//会议详情
			{
				return FilesOpeHandler.getMeetBackDetail(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_BACKDETAILCB_ACTION))//会议详情，待催办
			{
				return FilesOpeHandler.getMeetBackDetailCB(request, response, jsonParams,user);
			}
			
		    if(method.equals(ServletConst.CHECK_PATH_PERMISSION))
		    {
		    	return FilesOpeHandler.getPathPermission(request, response, jsonParams, user);
		    }
		    if (method.equals(ServletConst.MEET_SIGN_ACTION))//会议签收设置
			{
		    	return FilesOpeHandler.meetsignreal(request, response, jsonParams,user);
			}
			if (method.equals(ServletConst.MEET_GETSIGN_ACTION))//获取会议签收情况
			{
				return FilesOpeHandler.getMeetSignReal(request, response, jsonParams,user);
			}
			
			if (method.equals(ServletConst.MSG_GET_ACTION))//获取会议提醒情况
			{
				return FilesOpeHandler.getMsgCount(request, response, jsonParams,user);
			}if(method.equals(ServletConst.GET_TAGS_ACTION)) //获取标签数据
			{
				return FilesOpeHandler.getTags(request, response, jsonParams,user);
			}
			if(method.equals(ServletConst.CREATE_TAGS_ACTION)) //创建标签
			{
				return FilesOpeHandler.getCreateTags(request, response, jsonParams,user);
			}
			if(method.equals(ServletConst.ADD_FILETAGS_ACTION)) //文件添加标签
			{
				return FilesOpeHandler.getAddTags(request, response, jsonParams,user);
			}
			if(method.equals(ServletConst.DEL_FILETAGS_ACTION)) //删除所有文件标签
			{
				return FilesOpeHandler.getDelTags(request, response, jsonParams,user);
			}
			if(method.equals(ServletConst.DEL_FILETAG_ACTION)) //删除部分文件标签
			{
				return FilesOpeHandler.getDelLitTags(request, response, jsonParams,user);
			}
			if(method.equals(ServletConst.DEL_TAGS_ACTION))    //删除标签
			{
				return FilesOpeHandler.getDelTTags(request, response, jsonParams,user);
			}
			if(method.equals(ServletConst.RENAME_TAGS_ACTION)) //重命名标签
			{
				return FilesOpeHandler.getremTags(request, response, jsonParams,user);
			}
			if(method.equals(ServletConst.GET_SHARER))//获取他人共享中的共享人
			{
				return FilesOpeHandler.getSharer(request, response, user);
			}
			if(method.equals(ServletConst.SEND_REVIEW_FILE))//审阅文档
			{
				return FilesOpeHandler.sendReviewFiles(request, response, jsonParams, user);
			}
			if(method.equals(ServletConst.GET_REVIEW_FILEOF_SEND))//获取送审的文档列表
			{
				return FilesOpeHandler.getReviewFilesOfSend(request, response, jsonParams, user);
			}
			if(method.equals(ServletConst.GET_REVIEW_DETAILS))//获取审阅文档的详情
			{
				return FilesOpeHandler.getReviewDetails(request, response, jsonParams, user);
			}
			if(method.equals(ServletConst.GET_REVIEW_FILESOF_FILED))//获取审结的文档列表
			{
				return FilesOpeHandler.getReviewFilesOfFiled(request, response, jsonParams, user);
			}
			if(method.equals(ServletConst.GET_REVIEW_FILESOF_TODO))//获取待审的文档列表
			{
				return FilesOpeHandler.getReviewFilesOfTodo(request, response, jsonParams, user);
			}
			if(method.equals(ServletConst.GET_REVIEW_FILESOF_DONE))//获取已审的文档列表
			{
				return FilesOpeHandler.getReviewFilesOfDone(request, response, jsonParams, user);
			}
//			if(method.equals(ServletConst.REVIEW_FILE))//审阅文档
//			{
//				return FilesOpeHandler.reviewFile(request, response, jsonParams, user);
//			}
//			if(method.equals(ServletConst.GOBACK_REVIEWFILE))//反悔已审阅的文档
//			{
//				return FilesOpeHandler.goBackReviewFile(request, response, jsonParams, user);
//			}
			if(method.equals(ServletConst.DOWN_MERGE_ACTION)){
				return FilesHandler.downloadMerge(request, response,jsonParams,user);
			}
			if(method.equals(ServletConst.UNLOCK_OPENED_FILE))//强制解锁已打开的文件
			{
				return FilesHandler.unlockOpendFile(request, response, jsonParams,user);
			}
			if(method.equals(ServletConst.GET_SHAREDFILE_COMMENT))//获取共享文件的备注
			{
				return FilesOpeHandler.getSharedfileComment(request, response, jsonParams,user);
			}
			if(method.equals(ServletConst.GET_DETAIL_COMMENT))//获取某个备注详情
			{
				return FilesOpeHandler.getDetailComment(request, response, jsonParams);
			}
			if(method.equals(ServletConst.SET_SHAREDFILE_COMMENT))//对共享文件添加备注
			{
				return FilesOpeHandler.setSharedfileComment(request, response, jsonParams, user);
			}
			if(method.equals(ServletConst.MODIFY_SHAREDFILE_COMMENT))//修改选中的共享备注
			{
				return FilesOpeHandler.modifySharedfileComment(request, response, jsonParams, user);
			}
			if(method.equals(ServletConst.DEL_SHAREDFILE_COMMENT))//删除共享文件的备注
			{
				return FilesOpeHandler.delSharedfileComment(request, response, jsonParams, user);
			}
			if(method.equals(ServletConst.GET_USERS_MESSAGE))//获取发送短信
			{
				return FilesOpeHandler.getMySendMessage(request, response, jsonParams, user);
			}
			if(method.equals(ServletConst.GET_RECEIVE_MESSAGE))//获取接收短信
			{
				return FilesOpeHandler.getMyReceiveMessage(request, response, jsonParams, user);
			}
			if(method.equals(ServletConst.IS_FILE_EXIST_ACTION))//获取接收短信
			{
				return FilesHandler.isFileExist(request, response, jsonParams);
			}
			if(method.equals(ServletConst.GET_TEAMSHARENUMS))//移动端获取最新协作共享下的数量
			{
				return FilesOpeHandler.getTeamShareNums(request, response, jsonParams);
			}
			if(method.equals(ServletConst.GET_ALL_MEATDATA))//获取所有文件的元数据
			{
				return FilesOpeHandler.getAllMetadata(request, response, jsonParams);
			}
			if(method.equals("getnewsNum"))
			{
			    return FilesOpeHandler.getNewListMessagesNum(request, response, jsonParams, user);
			}
			
			
		}//else{
			// 无效请求方法
		return  JSONTools.convertToJson(ErrorCons.SYSTEM_REQUEST_ERROR, method);
		
	}

}
