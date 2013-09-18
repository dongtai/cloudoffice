package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

@Entity
@Table(name="mailfolders")
//邮件所处文件夹
public class MailFolders  implements SerializableAdapter
{
	
	public static final String INBOX ="inbox";//收件箱 1
	public static final String SENT ="sent";//已发邮件 2
	public static final String DRAFT ="draft";//草稿 3
	public static final String TRASH ="trash";//回收站 4
	public static final String OUTBOX ="outbox";//发件箱 5
	public static final String LOCAL ="local";//根目录 6
	
	public static long INBOX_ID =2;//收件箱 1
	public static long SENT_ID =3;//已发邮件 2
	public static long DRAFT_ID =4;//草稿 3
	public static long TRASH_ID =5;//回收站 4
	public static long OUTBOX_ID =6;//发件箱 5
	public static long LOCAL_ID =1;//根目录 6
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_mail_folders_gen")
	@GenericGenerator(name = "seq_mail_folders_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MAIL_FOLDERS_ID") })
	private Long id;
	
	@ManyToOne()
	@OnDelete(action = OnDeleteAction.CASCADE)
	private MailAccount account; // 该配置所属的用户
	
	@Column(length = 255)
	private String name;//defaout
	
	@ManyToOne()
	@OnDelete(action = OnDeleteAction.CASCADE)
	private MailFolders parent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MailAccount getAccount() {
		return account;
	}

	public void setAccount(MailAccount account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MailFolders getParent() {
		return parent;
	}

	public void setParent(MailFolders parent) {
		this.parent = parent;
	}
	
	
}
