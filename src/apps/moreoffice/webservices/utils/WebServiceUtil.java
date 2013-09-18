package apps.moreoffice.webservices.utils;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import apps.moreoffice.webservices.server.AuditOA;
import apps.transmanager.weboffice.service.config.WebConfig;

/**
 *  该类是无锡发改委接口的工具类，利用该类可以快速、简洁的调用相关的wenservice方法
 * @author 徐雷
 * @see  com.yozo.webservices.utils。WebServiceUtil{@link #checkUser(String, String)}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#getAduitMaterialList(String)}}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#getAllNewsWaitHandle(String)}}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#getApplyMaterialList(String)}}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#getMaterialDetail(String)}}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#getProjectDetail(String)}}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#getUserList(String)}}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#getWaitHandleCount(String)}}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#getWaitHanleAN(String)}}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#sendSMS(String, String, String)}}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#sendWaitHanleCL(String, String, String, String, String, String, String)}}
 * @see  com.yozo.webservices.utils.WebServiceUtil{@link WebServiceUtil#waitHandleCanDO(String)}}
 */
public class WebServiceUtil {

	   private static AuditOA port = null;//new AuditOA(AuditOA.WSDL_LOCATION, AuditOA.SERVICE).getAuditOASoap();
       
	   static{
		    JaxWsProxyFactoryBean factory=new JaxWsProxyFactoryBean();
			factory.setServiceClass(AuditOA.class);
			factory.setAddress(WebConfig.outwebserviceurl);
			port=(AuditOA) factory.create();
	   }
		/**
		 *     手机端操作后返回相关的操作信息
		 * @param messageItemGuid
		 *                待办GUID
		 * @param toActivityGuid
		 *                送一下步处理的GUID 
		 * @param operationGuid
		 *                Workflow_Activity_Operation表中对应字段，按钮GUID
		 * @param userGuid
		 *                下步待办处理人员Guid如果为多人用“;”间隔
		 * @param userName
		 *                下步待办处理人员如果为多人用“;”间隔
		 * @param userOpnion
		 *                
		 * @param currentUserGuid
		 *                处理意见
		 * @return
		 *                返回报文格式如下：
		 *                <?xml version="1.0" encoding="utf-8" ?>
		 *					<string xmlns="http://tempuri.org/">
		 *					  <EpointDataBody>
		 *					    <DATA>
		 *					      <UserArea>
		 *					        <ReturnInfo>
		 *					          <Status>Success</Status>
		 *					          <Description>如果为其他的返回错误信息</Description>
		 *					        </ReturnInfo>
		 *					      </UserArea>
		 *					    </DATA>
		 *					  </EpointDataBody>
		 *					</string>
         *
		 */
		public  static    String SendWaitHanle_CL(String messageItemGuid,String toActivityGuid, String operationGuid, String userGuid,String userName, String userOpnion, String currentUserGuid){
			return port.SendWaitHanle_CL(messageItemGuid, toActivityGuid, operationGuid, userGuid, userName, userOpnion, currentUserGuid);
		}

		/**
		 *    获取待办事宜
		 * @param userGuid
		 *           用户Guid
		 * @return
		 *        返回报文格式如下：
	     *          <?xml version="1.0" encoding="utf-8" ?>
		 *			<string xmlns="http://tempuri.org/">
		 *			  <EpointDataBody>
		 *			    <DATA>
		 *			      <UserArea>
		 *			        <ReturnInfo>
		 *			          <Status>Success</Status>
		 *			          <Description>如果为其他的返回错误信息</Description>
		 *			        </ReturnInfo>
		 *			                        <WaitHandle>
		 *			<MessageItemGuid>唯一Guid</MessageItemGuid>
		 *			<Title>待办标题</Title>     
		 *			 </WaitHandle>
		 *			      </UserArea>
		 *			    </DATA>
		 *			  </EpointDataBody>
		 *			</string>
		 * 
		 */
		public  static    String GetAllNewsWaitHandle(String userGuid){
			return port.GetAllNewsWaitHandle(userGuid);
		}

		/**
		 * 
		 * @param userGuid
		 * @return
		 */
		public  static  String GetWaitHandleCount(String userGuid){
			return port.GetWaitHandleCount(userGuid);
		}

		public  static  String SendSMS(String targetUserName, String targetMobile,String content){
			return port.SendSMS(targetUserName, targetMobile, content);
		}

		/**
		 * 根据待办获取到相关按钮信息
		 * @param messageItemGuid
		 *                   待办GUID
		 * @return
		 *      返回报文格式如下：
		 *         <?xml version="1.0" encoding="utf-8" ?>
		 *			<string xmlns="http://tempuri.org/">
		 *			  <EpointDataBody>
		 *			    <DATA>
		 *			      <UserArea>
		 *			        <ReturnInfo>
		 *			          <Status>Success</Status>
		 *			          <Description></Description>
		 *			        </ReturnInfo>
		 *			           <AnNiuList>
		 *			              <AnNiu>
		 *			                 <OperationGuid >按钮唯一Guid</OperationGuid >
		 *							 <OperationName >按钮名</ OperationName >
		 *							 <OperationType >按钮类型</ OperationType >
		 *							 <TransitionGuid >默认处理人员</ TransitionGuid >
		 *							 <TransitionUserName >处理人员</TransitionUserName >
		 *							 <DefaultOpinion>默认意见</DefaultOpinion>
		 *							 <TargetActivity>退回步骤可以选择的步骤</ TargetActivity>
		 *							 <TargetActivity>退回步骤可以选择的步骤</ TargetActivity>
		 *							 <ToActivityGuid>如果是通过类型的下一步对于的步骤GUID</ ToActivityGuid >
		 *			             </AnNiu>
		 *			             <AnNiu>
		 *				             <OperationGuid >按钮唯一Guid</OperationGuid >
		 *							 <OperationName >按钮名</ OperationName >
		 *							 <OperationType >按钮类型</ OperationType >
		 *							 <TransitionGuid >默认处理人员</ TransitionGuid >
		 *							 <TransitionUserName >处理人员</TransitionUserName >
		 *							 <DefaultOpinion>默认意见</DefaultOpinion>
		 *							 <TargetActivity>退回步骤可以选择的步骤</ TargetActivity>
		 *							 <ToActivityGuid >如果是通过类型的下一步对于的步骤GUID</ ToActivityGuid >
		 *			
		 *			           </AnNiu >
		 *			        </AnNiuList>
		 *			      </UserArea>
		 *			    </DATA>
		 *			  </EpointDataBody>
		 *			</string>
		 *			
		 *			备注：
		 *			
		 *			10:通过；
		 *			15:独立按钮
		 *			25：送阅读；
		 *			30：退回；
		 *			（下面OperationType 为20，50的在手机端不显示）
		 *			20：办理进度
		 *			50：收回
		 */
		public  static  String GetWaitHanle_AN(String messageItemGuid){
			return port.GetWaitHanle_AN(messageItemGuid);
		}

		/**
		 * 获取附件,作附件打开时候调用
		 * @param attachStorageInfoGroupGuid
		 *                            附件对应GUID
		 * @return
		 *        返回格式如下：
		 *        <?xml version="1.0" encoding="utf-8" ?>
		 *			<string xmlns="http://tempuri.org/">
		 *			  <EpointDataBody>
		 *			    <DATA>
		 *			      <UserArea>
		 *			        <ReturnInfo>
		 *			          <Status>Success</Status>
		 *			          <Description>如果为其他的返回错误信息</Description>
		 *			        </ReturnInfo>
		 *			        <AttachStorage>
		 *							<FILE_NAME>file name</FILE_NAME>
		 *							<FILE_CONTENT>CDATA[…]</FILE_CONTENT>     
		 *			       </AttachStorage>
		 *			      </UserArea>
		 *			    </DATA>
		 *			  </EpointDataBody>
		 *			</string>
         *
		 */
		public  static  String Get_Material_Detail(String attachStorageInfoGroupGuid){
			return port.Get_Material_Detail(attachStorageInfoGroupGuid);
		}

		/**
		 * 根据按钮操作选择下步人员
		 * @param ouGuid
		 *         对应部门的GUID，如果顶级传递为空
		 * @return
		 *       <?xml version="1.0" encoding="utf-8" ?>
		 *			<string xmlns="http://tempuri.org/">
		 *			  <EpointDataBody>
		 *			    <DATA>
		 *			      <UserArea>
		 *			        <ReturnInfo>
		 *			          <Status>Success</Status>
		 *			          <Description></Description>
		 *			        </ReturnInfo>
		 *			           <OUList>
		 *			              <OU>
		 *					           <OUGuid>部门Guid</OUGuid>
		 *					           <OUName>部门名</OUName>
		 *			                   <HasChildOu>判断是否有下级部门1有，0没有</HasChildOu>
		 *			              </OU>
		 *			              <OU>
		 *			                  <OUGuid>部门Guid</OUGuid>
		 *			                  <OUName>部门名</OUName>
		 *			                  <HasChildOu>判断是否有下级部门</HasChildOu>
		 *			              </OU>
		 *			          </OUList>
		 *			         <UserList>
		 *			              <User>
		 *					           <UserName>用户姓名</UserName>
		 *					           <UserGuid>用户Guidang</UserGuid> 
		 *			              </User>
		 *			              <User>
		 *			                   <UserName>用户姓名</UserName>
		 *			                   <UserGuid>用户Guidang</UserGuid> 
		 *			             </User>
		 *			        </UserList>
		 *			      </UserArea>
		 *			    </DATA>
		 *			  </EpointDataBody>
		 *			</string> 
         *   
		 */
		public  static  String GetUserList(String ouGuid){
			return port.GetUserList(ouGuid);
		}

		/**
		 *     获取申请材料列表
		 * @param messageItemGuid
		 *                待办Guid 
		 * @return
		 *       返回报文如下：
		 *       <?xml version="1.0" encoding="utf-8" ?>
		 *			<string xmlns="http://tempuri.org/">
		 *			  <EpointDataBody>
		 *			    <DATA>
		 *			      <UserArea>
		 *			        <ReturnInfo>
		 *			          <Status>Success</Status>
		 *			          <Description>如果为其他的返回错误信息</Description>
		 *			        </ReturnInfo>
		 *			        <ApplyMaterialList>
		 *			           <ApplyMaterial>
		 *							<AttachStorageInfoGroupGuid>附件GroupGuid</AttachStorageInfoGroupGuid>  
		 *							<MaterialInstanceGuid>附件Guid<MaterialInstanceGuid>
		 *							<MaterialInstanceName>附件名称<MaterialInstanceName>
		 *							 <AddDate>提交时间</AddDate>
		 *							<AddUserGuid>提交人Guid<AddUserGuid>
		 *							<AddUserName>提交人</AddUserName>
		 *					 </ApplyMaterial>
		 *					<ApplyMaterial>
		 *							<AttachStorageInfoGroupGuid>附件GroupGuid</AttachStorageInfoGroupGuid>  
		 *							<MaterialInstanceGuid>附件Guid<MaterialInstanceGuid>
		 *							<MaterialInstanceName>附件名称<MaterialInstanceName>
		 *							<AddDate>提交时间</AddDate>
		 *							<AddUserGuid>提交人Guid<AddUserGuid>
		 *							<AddUserName>提交人</AddUserName>
		 *					 </ApplyMaterial>
		 *					</ApplyMaterialList>
		 *			      </UserArea>
		 *			    </DATA>
		 *			  </EpointDataBody>
		 *			</string> 
         *
		 */
		public  static  String GetApplyMaterial_List(String messageItemGuid){
			return port.GetApplyMaterial_List(messageItemGuid);
		}

		/**
		 *  验证用户
		 * @param userGuid
		 *            用户Guid
		 * @param password
		 *             密码
		 * @return
		 *       返回报文格式如下：
		 *        <?xml version="1.0" encoding="utf-8" ?>
		 *			<string xmlns="http://tempuri.org/">
		 *			  <EpointDataBody>
		 *			    <DATA>
		 *			      <UserArea>
		 *			        <ReturnInfo>
		 *			          <Status>Success</Status>
		 *			          <Description>如果为其他的返回错误信息</Description>
		 *			        </ReturnInfo>
		 *			         <CheckResult>
		 *			                  0：不存在； 1：存在；
		 *			        </CheckResult>
		 *			      </UserArea>
		 *			    </DATA>
		 *			  </EpointDataBody>
		 *			</string> 
         *
		 */
		public  static  String CheckUser(String userGuid, String password){
			return port.CheckUser(userGuid, password);
		}

		/**
		 * 判断此待办是否可以操作
		 * 
		 * @param messageItemGuid
		 *             待办GUID
		 * @return
		 *      true/false
		 */
		public  static  boolean WaitHandle_CanDO(String messageItemGuid){
			return port.WaitHandle_CanDO(messageItemGuid);
		}

		/**
		 * 获取内部审核材料列表
		 * @param messageItemGuid
		 *                  待办Guid 
		 * @return
		 *     报文格式如下：
		 *     <?xml version="1.0" encoding="utf-8" ?>
		 *		<string xmlns="http://tempuri.org/">
		 *		  <EpointDataBody>
		 *		    <DATA>
		 *		      <UserArea>
		 *		        <ReturnInfo>
		 *		          <Status>Success</Status>
		 *		          <Description>如果为其他的返回错误信息</Description>
		 *		        </ReturnInfo>
		 *		       <AduitMaterialList>
		 *		          <AduitMaterial>
		 *						<AttachStorageInfoGroupGuid>附件Guid</AttachStorageInfoGroupGuid>  
		 *						<Materialguid>材料Guid<Materialguid>
		 *						<MaterialName>材料名称<MaterialName>
		 *						<AddDate>提交时间</AddDate>
		 *						<AddUserGuid>提交人Guid<AddUserGuid>
		 *						<AddUserName>提交人</AddUserName>
		 *				 </AduitMaterial>
		 *				<AduitMaterial>
		 *						<AttachStorageInfoGroupGuid>附件GroupGuid</AttachStorageInfoGroupGuid>  
		 *						<Materialguid>材料Guid<Materialguid>
		 *						<MaterialName>材料名称<MaterialName>
		 *						<AddDate>提交时间</AddDate>
		 *						<AddUserGuid>提交人Guid<AddUserGuid>
		 *						<AddUserName>提交人</AddUserName>
		 *						<Istj>0：未提交，1：提交</Istj>
		 *				 </AduitMaterial>
		 *				</AduitMaterialList>
		 *		      </UserArea>
		 *		    </DATA>
		 *		  </EpointDataBody>
		 *		</string>
		 *  
		 */
		public  static  String GetAduitMaterial_List(String messageItemGuid){
			return port.GetAduitMaterial_List(messageItemGuid);
		}

		/**
		 *  根据待办获取到办件的基本信息、办件流程列表
		 * @param messageItemGuid
		 *       待办GUID
		 * @return
		 *     报文格式如下：
		 *     <?xml version="1.0" encoding="utf-8" ?>
		 *		<string xmlns="http://tempuri.org/">
		 *		  <EpointDataBody>
		 *		    <DATA>
		 *		      <UserArea>
		 *		        <ReturnInfo>
		 *		          <Status>Success</Status>
		 *		          <Description></Description>
		 *		        </ReturnInfo>
		 *		        <ProjectDetail>
		 *		            <Applicant_type>申请人类型</Applicant_type>
		 *		            <BWDH>办文单号</BWDH>
		 *		            <ApplyerName>申请人</ApplyerName>
		 *		            <Applicant_code>申请人代码</Applicant_code>
		 *		            <ProjectName>办件名称</ProjectName>
		 *		            <TextNum>发文号</TextNum>
		 *		            <Applicant_Phone>联系电话</Applicant_Phone>
		 *		            <Proposer>联系人</Proposer>
		 *		            <Applicant_Address>申请人地址</Applicant_Address>
		 *		            <Applicant_Zipcode>邮编</Applicant_Zipcode>
		 *		            <Applicant_Mobile>手机</Applicant_Mobile>
		 *		            <Applicant_Email>电子邮件</Applicant_Email>
		 *		            <Applicant_Paper_Type>证件名称</Applicant_Paper_Type>
		 *		           <Applicant_Paper_Number>证件号码</Applicant_Paper_Number>
		 *		          </ProjectDetail >
         *                  <WorkItemList>
		 *			              <WorkItem>
		 *			                 <ActivityName>步骤</ActivityName>
		 *			                 <UserName>处理人</UserName >
		 *							 <CreateDate>收到时间</CreateDate>
		 *							 <OperationDate>处理时间</OperationDate>
		 *							 <Opinion>处理意见</Opinion>
		 *							 <SendUserName>提交人</SendUserName>
		 *			          </WorkItem>
		 *			          <WorkItem>
		 *			              <ActivityName>步骤</ActivityName>
		 *						  <UserName>处理人</UserName >
		 *						  <CreateDate>收到时间</CreateDate>
		 *						  <OperationDate>处理时间</OperationDate>
		 *						  <Opinion>处理意见</Opinion>
		 *						  <SendUserName>提交人</SendUserName >
		 *			          </WorkItem >
	     *			         </WorkItemList>
	     *			      </UserArea>
		 *		    </DATA>
		 *		  </EpointDataBody>
		 *		</string>
         *
		 */
		public  static  String GetProject_Detail(String messageItemGuid){
			return port.GetProject_Detail(messageItemGuid);
		}
		/**
	     * 根据待办获取到办件的基本信息、办件流程列表
	     *               （获取已办事项详细情况时调用）
	     * @param Pvi_Guid
	     *             流程实例唯一标识
	     * @return
	     *     <?xml version="1.0" encoding="utf-8" ?>
		 *			<string xmlns="http://tempuri.org/">
		 *			  <EpointDataBody>
		 *			    <DATA>
		 *			      <UserArea>
		 *			        <ReturnInfo>
		 *			          <Status>Success</Status>
		 *			          <Description></Description>
		 *			        </ReturnInfo>
		 *			          <ProjectDetail>
		 *			           < Applicant_type >申请人类型</ Applicant_type >
		 *			            < BWDH >办文单号</ BWDH >
		 *			            <ApplyerName >申请人</ ApplyerName >
		 *			            < Applicant_code >申请人代码</ Applicant_code >
		 *			            < ProjectName >办件名称</ ProjectName >
		 *			            < TextNum >发文号</ TextNum >
		 *			             <Applicant_Phone>联系电话</ Applicant_Phone>
		 *			            < Proposer >联系人</ Proposer>
		 *			            < Applicant_Address>申请人地址</ Applicant_Address>
		 *			            < Applicant_Zipcode >邮编</ Applicant_Zipcode>
		 *			           < Applicant_Mobile>手机</Applicant_Mobile>
		 *			            <Applicant_Email >电子邮件</Applicant_Email>
		 *			             <Applicant_Paper_Type>证件名称</ Applicant_Paper_Type>
		 *			           <Applicant_Paper_Number>证件号码</Applicant_Paper_Number>
		 *			          </ProjectDetail >
		 *			           <WorkItemList>
		 *			              <WorkItem>
		 *			             <ActivityName>步骤</ActivityName>
		 *			  <UserName>处理人</ UserName >
		 *			< CreateDate>收到时间</ CreateDate>
		 *			 < OperationDate>处理时间</ OperationDate>
		 *			< Opinion>处理意见</ Opinion>
		 *			< SendUserName>提交人</ SendUserName >
		 *			          </ WorkItem >
		 *			  <WorkItem>
		 *			             <ActivityName>步骤</ActivityName>
		 *			  <UserName>处理人</ UserName >
		 *			< CreateDate>收到时间</ CreateDate>
		 *			 < OperationDate>处理时间</ OperationDate>
		 *			< Opinion>处理意见</ Opinion>
		 *			< SendUserName>提交人</ SendUserName >
		 *			          </ WorkItem >
		 *			
		 *			</WorkItemList>
		 *			
		 *			      </UserArea>
		 *			    </DATA>
		 *			  </EpointDataBody>
		 *			</string>
	     */
	    public  static  String GetProject_DetailDone( String Pvi_Guid){
	    	return port.GetProject_DetailDone(Pvi_Guid);
	    }
	    
	    
	    /**
	     *  获取内部审核材料列表(已办 调用)
	     * @param WorkItemGuid
	     *               工作项实例唯一标识
	     * @return
	     *         <?xml version="1.0" encoding="utf-8" ?>
		 *			<string xmlns="http://tempuri.org/">
		 *			  <EpointDataBody>
		 *			    <DATA>
		 *			      <UserArea>
		 *			        <ReturnInfo>
		 *			          <Status>Success</Status>
		 *			          <Description>如果为其他的返回错误信息</Description>
		 *			        </ReturnInfo>
		 *							<AduitMaterialList>
		 *							        <AduitMaterial>
		 *										<AttachStorageInfoGroupGuid>附件Guid</AttachStorageInfoGroupGuid>  
		 *										<Materialguid>材料Guid<Materialguid>
		 *										<MaterialName>材料名称<MaterialName>
		 *										 <AddDate>提交时间</AddDate>
		 *										<AddUserGuid>提交人Guid<AddUserGuid>
		 *										<AddUserName>提交人</AddUserName>
		 *									</AduitMaterial>
		 *									<AduitMaterial>
		 *										<AttachStorageInfoGroupGuid>附件GroupGuid</AttachStorageInfoGroupGuid>  
		 *										<Materialguid>材料Guid<Materialguid>
		 *										<MaterialName>材料名称<MaterialName>
		 *										 <AddDate>提交时间</AddDate>
		 *										<AddUserGuid>提交人Guid<AddUserGuid>
		 *										<AddUserName>提交人</AddUserName>
		 *										<Istj>0：未提交，1：提交</Istj>
		 *							     </AduitMaterial>
		 *							</AduitMaterialList>
		 *			      </UserArea>
		 *			    </DATA>
		 *			  </EpointDataBody>
		 *			</string>
	     */
	    public  static  String GetAduitMaterial_ListDone( String WorkItemGuid){
	    	return port.GetAduitMaterial_ListDone(WorkItemGuid);
	    }
	    
	    /**
	     * 获取申请材料列表(已办 调用)
	     * @param Pvi_Guid
	     *            流程实例唯一标识
	     * @return
		 *	          <?xml version="1.0" encoding="utf-8" ?>
		 *				<string xmlns="http://tempuri.org/">
		 *				  <EpointDataBody>
		 *				    <DATA>
		 *				      <UserArea>
		 *				        <ReturnInfo>
		 *				          <Status>Success</Status>
		 *				          <Description>如果为其他的返回错误信息</Description>
		 *				        </ReturnInfo>
		 *								<ApplyMaterialList>
		 *								        <ApplyMaterial>
		 *											<AttachStorageInfoGroupGuid>附件GroupGuid</AttachStorageInfoGroupGuid>  
		 *											<MaterialInstanceGuid>附件Guid<MaterialInstanceGuid>
		 *											<MaterialInstanceName>附件名称<MaterialInstanceName>
		 *											 <AddDate>提交时间</AddDate>
		 *											<AddUserGuid>提交人Guid<AddUserGuid>
		 *											<AddUserName>提交人</AddUserName>
		 *										 </ApplyMaterial>
		 *										<ApplyMaterial>
		 *											<AttachStorageInfoGroupGuid>附件GroupGuid</AttachStorageInfoGroupGuid>  
		 *											<MaterialInstanceGuid>附件Guid<MaterialInstanceGuid>
		 *											<MaterialInstanceName>附件名称<MaterialInstanceName>
		 *											 <AddDate>提交时间</AddDate>
		 *											<AddUserGuid>提交人Guid<AddUserGuid>
		 *											<AddUserName>提交人</AddUserName>
		 *										 </ApplyMaterial>
		 *								</ApplyMaterialList>
		 *				      </UserArea>
		 *				    </DATA>
		 *				  </EpointDataBody>
		 *				</string>
	     */
	    public static  String GetApplyMaterial_ListDone(String Pvi_Guid){
	    	return port.GetApplyMaterial_ListDone(Pvi_Guid);
	    }
	    
	    /**
	     * 获取已办事宜
	     * @param UserGuid
	     *            用户Guid
	     * @param PageSize
	     *            每页显示办件数
	     * @param CurrentPageIndex
	     *            当前页索引号(第一页为1)
	     * @return
	     *      <?xml version="1.0" encoding="utf-8" ?>
		 *		<string xmlns="http://tempuri.org/">
		 *		  <EpointDataBody>
		 *		    <DATA>
		 *		      <UserArea>
		 *		        <ReturnInfo>
		 *		          <Status>Success</Status>
		 *		          <Description>如果为其他的返回错误信息</Description>
		 *		        </ReturnInfo>
		 *		                   <DoneHandle>
		 *								<ProcessVersionInstanceGuid>工作流唯一Guid
		 *								</ProcessVersionInstanceGuid>
		 *								<WorkItemGuid>工作项唯一Guid
		 *								</WorkItemGuid>
		 *								<WorkItemName>已办事项标题</WorkItemName>     
		 *							</DoneHandle>
		 *							
		 *							<DoneHandle>
		 *								<ProcessVersionInstanceGuid>工作流唯一Guid
		 *								</ProcessVersionInstanceGuid>
		 *								<WorkItemGuid>工作项唯一Guid
		 *								</WorkItemGuid>
		 *								<WorkItemName>已办事项标题</WorkItemName>     
		 *							 </DoneHandle>
		 *		      </UserArea>
		 *		    </DATA>
		 *		  </EpointDataBody>
		 *		</string>
	     */
	    public static String GetAllNewsDone(String UserGuid,int PageSize,int CurrentPageIndex){
	    	return port.GetAllNewsDone(UserGuid, PageSize, CurrentPageIndex);
	    }
	}
