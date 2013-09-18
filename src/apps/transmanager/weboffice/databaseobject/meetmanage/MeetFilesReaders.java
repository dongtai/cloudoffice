package apps.transmanager.weboffice.databaseobject.meetmanage;

import java.util.Date;

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

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 用户查看文件的记录表。（时间关系暂没有对字段进行整理）
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 1.0
 * @see
 * @since web1.0
 */

@Entity
@Table(name="meetfilesreaders")
public class MeetFilesReaders implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_meetfilesreaders_gen")
	@GenericGenerator(name = "seq_meetfilesreaders_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MEETFILESREADERS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;//看文档用户
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private MeetFiles meetFiles;//会议文档
	private Date readdate;//查看时间
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public MeetFiles getMeetFiles() {
		return meetFiles;
	}
	public void setMeetFiles(MeetFiles meetFiles) {
		this.meetFiles = meetFiles;
	}
	public Date getReaddate() {
		return readdate;
	}
	public void setReaddate(Date readdate) {
		this.readdate = readdate;
	}

}
