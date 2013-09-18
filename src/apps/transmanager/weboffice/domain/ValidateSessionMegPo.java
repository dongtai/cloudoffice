package apps.transmanager.weboffice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="validate_sessionmeg_tb")
public class ValidateSessionMegPo implements SerializableAdapter{

	//public static final String LG_DEFAULT_ICON = "/static/images/personalset2/";
		/**
		 * 
		 */
		private static final long serialVersionUID = 4643860801288294018L;
		
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_validate_sessionmeg_gen")
		@GenericGenerator(name = "seq_validate_sessionmeg_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_validate_sessionmeg_ID") })
		private Long id;
		//接收者ID
		private Long acceptId;
		
		/**
		 * 1表示找人
		 * 2:找组
		 */
		private int category;
		
		/**
		 * 是否处理
		 */
		private boolean handle;
		
		/**
		 * 处理时间
		 */
		private Date handleDate;
		
		/**
		 * 请求加入的组的ID
		 */
		private Long groupId;
		
		/**
		 * 请求者的ID
		 */
		private Long sendId;
		
		/**
		 * 消息
		 */
		@Lob
		private String sessionMeg;
		
		/**
		 * 0普通验证信息
		 * 1:同意验证信息
		 * 2：拒绝验证信息
		 * 3:通知信息
		 */
		private Integer type;
		
		/**
		 * 添加时间
		 */
		private Date addDate;
		
		/**
		 * 发送者的姓名
		 */
		@Column(length = 100)
		private String sendName;
		
		public Long getSendId() {
			return sendId;
		}
		public void setSendId(Long sendId) {
			this.sendId = sendId;
		}
		public String getSessionMeg() {
			return sessionMeg;
		}
		public void setSessionMeg(String sessionMeg) {
			this.sessionMeg = sessionMeg;
		}
		public Integer getType() {
			return type;
		}
		public void setType(Integer type) {
			this.type = type;
		}
		public Date getAddDate() {
			return addDate;
		}
		public void setAddDate(Date addDate) {
			this.addDate = addDate;
		}
		
		public String getSendName() {
			return sendName;
		}
		public void setSendName(String sendName) {
			this.sendName = sendName;
		}
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getGroupId() {
			return groupId;
		}
		public void setGroupId(Long groupId) {
			this.groupId = groupId;
		}
		public Long getAcceptId() {
			return acceptId;
		}
		public void setAcceptId(Long acceptId) {
			this.acceptId = acceptId;
		}
		public int getCategory() {
			return category;
		}
		public void setCategory(int category) {
			this.category = category;
		}
		public boolean isHandle() {
			return handle;
		}
		public void setHandle(boolean handle) {
			this.handle = handle;
		}
		public Date getHandleDate() {
			return handleDate;
		}
		public void setHandleDate(Date handleDate) {
			this.handleDate = handleDate;
		}
}
