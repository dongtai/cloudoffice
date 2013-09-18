package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * @author zzy
 * @Table(name = "filelog")
 */
@Entity
@Table(name = "filelog")
public class FileLog implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_fileLog_gen")
	@GenericGenerator(name = "seq_fileLog_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FILE_LOG_ID") })
	private long flogId;
	private long fileId;
	private long uid;
	// private String username;
	// private String realname;
	// private String filename;
	private Date opTime;
	private Date endTime;

	/*
	 * 为demo而设置的日志状态：0没有阅读；1开档；2编辑；3删除； 4表示移动；5复制； 6共享；7取消共享；8备份；
	 * 9上传；10新建；11搜索；12zip；13unzip；14还原； 15重命名；16下载；17发送；18合并；19永久删除；20清空垃圾箱
	 */
	private int opType;// 0表示没有阅读，1表示阅读过，2表是正在阅读
	/*
	 * 移动拷贝重命名，需要记录源路径文件信息
	 */
	private int srcfileid;
	private Files fileinfo;
	private Users userinfo;
	private Files srcFileinfo;
	@Column(length = 1000)
	private String opscript;
	@Column(length = 1000)
	private String opresult;
	
	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}

	public String getOpresult()
	{
		return opresult;
	}

	public void setOpresult(String opresult)
	{
		this.opresult = opresult;
	}

	public String getOpscript()
	{
		return opscript;
	}

	public void setOpscript(String opscript)
	{
		this.opscript = opscript;
	}

	public FileLog()
	{

	}

	public FileLog(long flogId, Files fileinfo, Users userinfo, Date opTime,
			int opType)
	{
		this.flogId = flogId;
		this.fileinfo = fileinfo;
		this.userinfo = userinfo;
		this.opTime = opTime;
		this.opType = opType;
	}

	// @SequenceGenerator(name="Sequence_1",sequenceName =
	// "Sequence_1",allocationSize = 1)
	// @GeneratedValue(strategy = GenerationType.SEQUENCE,generator =
	// "Sequence_1")
	public void setFlogId(long flogId)
	{
		this.flogId = flogId;
	}

	public long getFlogId()
	{
		return flogId;
	}

	public long getFileId()
	{
		return fileId;
	}

	public void setFileId(long fileId)
	{
		this.fileId = fileId;
	}

	public long getUid()
	{
		return uid;
	}

	public void setUid(long uid)
	{
		this.uid = uid;
	}

	public void setOpTime(Date opTime)
	{
		this.opTime = opTime;
	}

	public Date getOpTime()
	{
		return opTime;
	}

	public void setOpType(int optype)
	{
		this.opType = optype;
	}

	public Files getFileinfo()
	{
		return fileinfo;
	}

	public void setFileinfo(Files fileinfo)
	{
		this.fileinfo = fileinfo;
	}

	public Users getUserinfo()
	{
		return userinfo;
	}

	public void setUserinfo(Users userinfo)
	{
		this.userinfo = userinfo;
	}

	public int getOpType()
	{
		return opType;
	}

	public int getSrcfileid()
	{
		return srcfileid;
	}

	public void setSrcfileid(int srcfileid)
	{
		this.srcfileid = srcfileid;
	}

	public Files getSrcFileinfo()
	{
		return srcFileinfo;
	}

	public void setSrcFileinfo(Files srcFileinfo)
	{
		this.srcFileinfo = srcFileinfo;
	}

}
