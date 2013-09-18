package apps.transmanager.weboffice.databaseobject;

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
 * 流程处理时的默认选择者(多个默认选择者就是多条记录)
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   政府版
 */
@Entity
@Table(name="approvaldefaulter")
public class ApprovalDefaulter implements SerializableAdapter
{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvaldefaulter_gen")
    @GenericGenerator(name = "seq_approvaldefaulter_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALDEFAULTER_ID") })
    private Long id;
	private Integer type;//类型 ApproveConstants.APPROVAL_DEFAULT_SEND = 1;//送文默认标记
						 //APPROVAL_DEFAULT_MODIFY = 2;//处理默认标记
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users modifier;//默认处理人
	private Integer selecttype=0;//选择模式，0为返回送文人 ，1选择下一个人
	private Integer isreader=0;//是否选中了送阅，1为选中
	private String comment;//默认备注，送文和处理可同时使用这个字段
	private Integer issame=0;//是否会签，1为会签
	private String sendreadid;//送阅人ID,用,号间隔
	private String sendreadname;//送阅人名称,用,号间隔
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private ApprovalDic dic;//文件类别

	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;//设置的人员
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Users getModifier() {
		return modifier;
	}

	public void setModifier(Users modifier) {
		this.modifier = modifier;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}
	public Integer getSelecttype() {
		if (selecttype==null)
		{
			selecttype=0;
		}
		return selecttype;
	}

	public void setSelecttype(Integer selecttype) {
		this.selecttype = selecttype;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getSendreadid() {
		return sendreadid;
	}

	public void setSendreadid(String sendreadid) {
		this.sendreadid = sendreadid;
	}

	public String getSendreadname() {
		return sendreadname;
	}

	public void setSendreadname(String sendreadname) {
		this.sendreadname = sendreadname;
	}
	public Integer getIsreader() {
		if (isreader==null)
		{
			isreader=0;
		}
		return isreader;
	}

	public void setIsreader(Integer isreader) {
		this.isreader = isreader;
	}
	public Integer getIssame() {
		if (issame==null)
		{
			issame=0;
		}
		return issame;
	}

	public void setIssame(Integer issame) {
		this.issame = issame;
	}
	public ApprovalDic getDic() {
		return dic;
	}

	public void setDic(ApprovalDic dic) {
		this.dic = dic;
	}

}
