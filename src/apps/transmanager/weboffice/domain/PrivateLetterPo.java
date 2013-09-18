package apps.transmanager.weboffice.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.databaseobject.Users;

@Entity
@Table(name="mblog_privateletter")
public class PrivateLetterPo implements SerializableAdapter{

	private static final long serialVersionUID = 3958170934292465394L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_groups_gen")
	@GenericGenerator(name = "seq_groups_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_GROUPS_ID") })
	private Long id;
	
	@OneToOne
	@JoinColumn(name="send_userid")
	@OnDelete(action=OnDeleteAction.NO_ACTION) 
	private Users sendmeg_user;
	
	@OneToOne
	@JoinColumn(name="sendto_userid")
	@OnDelete(action=OnDeleteAction.NO_ACTION) 
	private Users sendmegto_user;
	
	private String messagebody;
	private Date sendtime;
	private boolean send_del=false;
	public boolean isSend_del() {
		return send_del;
	}
	public void setSend_del(boolean send_del) {
		this.send_del = send_del;
	}
	public boolean isSend_to_del() {
		return send_to_del;
	}
	public void setSend_to_del(boolean send_to_del) {
		this.send_to_del = send_to_del;
	}
	private boolean send_to_del=false;
	private boolean isread=false;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Users getSendmeg_user() {
		return sendmeg_user;
	}
	public void setSendmeg_user(Users sendmeg_user) {
		this.sendmeg_user = sendmeg_user;
	}
	public Users getSendmegto_user() {
		return sendmegto_user;
	}
	public void setSendmegto_user(Users sendmegto_user) {
		this.sendmegto_user = sendmegto_user;
	}
	public String getMessagebody() {
		return messagebody;
	}
	public void setMessagebody(String messagebody) {
		this.messagebody = messagebody;
	}
	public Date getSendtime() {
		return sendtime;
	}
	public void setSendtime(Date sendtime) {
		this.sendtime = sendtime;
	}
	public boolean isIsread() {
		return isread;
	}
	public void setIsread(boolean isread) {
		this.isread = isread;
	}
}
