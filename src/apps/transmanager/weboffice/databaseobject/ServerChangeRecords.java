package apps.transmanager.weboffice.databaseobject;

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
 * 服务器更新记录表
 * 作者:           hwj
 * 日期:           2012-11-22
 */
@Entity
@Table(name="serverchangerecords")
public class ServerChangeRecords implements SerializableAdapter{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_serverchangerecords_gen")
    @GenericGenerator(name = "seq_serverchangerecords_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SERVER_CHANGE_RECORDS_ID") })
    private Long id;//主键，记录的ID
	
	@Column(name = "path",length = 60000)
	private String path;//文件的绝对路径
	
	@Column(name = "oldFileName",length = 1000)
	private String oldFileName;//重命名之前的文件名	
	
	@Column(name = "isFolder")
	private Integer isFolder;//是否为文件夹	
	
	@Column(name = "addDate")
	private Long addDate = 0L;//新建文件的时间
	
	@Column(name = "renameDate")
	private Long renameDate = 0L;//重命名文件的时间
	
	@Column(name = "modifyDate")
	private Long modifyDate = 0L;//修改文件的时间
	
	@Column(name = "deleteDate")
	private Long deleteDate = 0L;//删除文件的时间
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getOldFileName() {
		return oldFileName;
	}

	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}

	public int getIsFolder() {
		return isFolder;
	}

	public void setIsFolder(Integer isFolder) {
		this.isFolder = isFolder;
	}

	public long getAddDate() {
		return addDate;
	}

	public void setAddDate(Long addDate) {
		this.addDate = addDate;
	}

	public long getRenameDate() {
		return renameDate;
	}

	public void setRenameDate(Long renameDate) {
		this.renameDate = renameDate;
	}

	public long getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Long modifyDate) {
		this.modifyDate = modifyDate;
	}

	public long getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(Long deleteDate) {
		this.deleteDate = deleteDate;
	}

}
