package apps.transmanager.weboffice.databaseobject.flow;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;
/**
 * 流程附件列表,记录流程的所有文件,在approvaltask表中记录当前签批的文件版本号
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   web1.0
 */
@Entity
@Table(name="flowfiles")
public class FlowFiles implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowfiles_gen")
	@GenericGenerator(name = "seq_flowfiles_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWFILES_ID") })
	private Long id;//主键
	private Long mainformid;//主表单编号，flowform的主键，相当于具体流程编号

	@Column(name = "outid")
    private Long outid;//与filetype配对使用
	@Column(name = "filetype")
	private Integer filetype;//文件类别，1、内部审批，2、???，3、外部材料
	@Column(name = "filename", length = 1000)
    private String filename;//文件名称
	@Column(name = "filepath", length = 60000)
    private String filepath;//文件地址
	@Column(name = "opentag", length = 255)
    private String opentag;//打开标记
	
	
	@Column(name = "createtime")
    private Date createtime;//创建文件
	@Column(name = "uploadid")
    private Long uploadid;//上传者
	@Transient
	private String uploadname;//上传者
	
	public String getUploadname()
	{
		return uploadname;
	}
	public void setUploadname(String uploadname)
	{
		this.uploadname = uploadname;
	}
	public Long getMainformid()
	{
		return mainformid;
	}
	public void setMainformid(Long mainformid)
	{
		this.mainformid = mainformid;
	}
	public String getOpentag()
	{
		return opentag;
	}
	public void setOpentag(String opentag)
	{
		this.opentag = opentag;
	}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Long getOutid()
	{
		return outid;
	}
	public void setOutid(Long outid)
	{
		this.outid = outid;
	}
	public Integer getFiletype()
	{
		return filetype;
	}
	public void setFiletype(Integer filetype)
	{
		this.filetype = filetype;
	}
	public String getFilename()
	{
		return filename;
	}
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	public String getFilepath()
	{
		return filepath;
	}
	public void setFilepath(String filepath)
	{
		this.filepath = filepath;
	}
	public Date getCreatetime()
	{
		return createtime;
	}
	public void setCreatetime(Date createtime)
	{
		this.createtime = createtime;
	}
	public Long getUploadid()
	{
		return uploadid;
	}
	public void setUploadid(Long uploadid)
	{
		this.uploadid = uploadid;
	}
	
}
