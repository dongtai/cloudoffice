package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

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

/**
 * 系统统一信息对象。
 * 共享、签批
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="messages")
public class Messages  implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_messages_gen")
	@GenericGenerator(name = "seq_messages_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MESSAGES_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;     // 消息发送者。这里不做级联删除，因为用户发送的消息以不可抵赖的方式存在。
	private Integer type;       // 所有消息的类别，详细定义见com.evermore.weboffice.constants.both.MessageCons中的定义。
	private Integer worktype=0;//办理类别，0为送过来的 ,1为催办

	@Column(length = 255)
	private String title;
	@Column(length=65535)
	private String content;     // 
	private Long size;           //附件为文件时候，文件的大小， 先这样定义，后续合并到content中
	private Long permit;        //附件为文件时候，文件的权限， 先这样定义，后续合并到content中
	@Column(length=65535)
	private String attach;      // 消息的附件查看路径
	private Date date;//消息创建时间
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users msguser;//消息接收者
	private Integer state=0;//消息状态，默认是0未读，已读为1
	private Date modifydate;//消息处理日期
	private Integer deleted=0;//删除状态，默认是0，删除为1
	private Date deldate;//删除日期
	private Long outid;//根据type决定与哪个外键表关联,跟消息接收者结合起来，目前存放流程号
	private Long sameid;//会签编号，主要是考虑同一个签批，有人签批2次，最后提醒次数不好统计
	private Long readid;//传阅编号，根据type决定与哪个外键表关联,跟消息接收者结合起来
	private Long coopid;//协作编号，根据type决定与哪个外键表关联,跟消息接收者结合起来

	public Integer getWorktype()
	{
		return worktype;
	}

	public void setWorktype(Integer worktype)
	{
		this.worktype = worktype;
	}

	public Long getCoopid() {
		return coopid;
	}

	public void setCoopid(Long coopid) {
		this.coopid = coopid;
	}

	public Long getReadid() {
		return readid;
	}

	public void setReadid(Long readid) {
		this.readid = readid;
	}

	public Long getSameid() {
		return sameid;
	}

	public void setSameid(Long sameid) {
		this.sameid = sameid;
	}

	public Messages()
	{	
	}
	
	public Messages(int type, String title, String content, String attach)
	{
		this.type = type;
		this.title = title;
		this.content = content;
		this.attach = attach;
		date = new Date();		 
	}
	public Long getOutid() {
		return outid;
	}

	public void setOutid(Long outid) {
		this.outid = outid;
	}

	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Users getUser()
	{
		return user;
	}
	public void setUser(Users user)
	{
		this.user = user;
	}
	public Integer getType()
	{
		return type;
	}
	public void setType(Integer type)
	{
		this.type = type;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public String getAttach()
	{
		return attach;
	}
	public void setAttach(String attach)
	{
		this.attach = attach;
	}
	public Date getDate()
	{
		return date;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}

	public Long getSize()
	{
		return size;
	}

	public void setSize(Long size)
	{
		this.size = size;
	}

	public Long getPermit()
	{
		return permit;
	}

	public void setPermit(Long permit)
	{
		this.permit = permit;
	}
	
	public Users getMsguser() {
		return msguser;
	}

	public void setMsguser(Users msguser) {
		this.msguser = msguser;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getModifydate() {
		return modifydate;
	}

	public void setModifydate(Date modifydate) {
		this.modifydate = modifydate;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public Date getDeldate() {
		return deldate;
	}

	public void setDeldate(Date deldate) {
		this.deldate = deldate;
	}

}
