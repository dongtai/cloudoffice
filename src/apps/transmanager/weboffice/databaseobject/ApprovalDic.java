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
 * 流程字典表
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @see     
 * @since   政府版
 */
@Entity
@Table(name="approvaldic")
public class ApprovalDic implements SerializableAdapter
{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvaldic_gen")
    @GenericGenerator(name = "seq_approvaldic_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALDIC_ID") })
    private Long id;
	private Integer type;//类型 ApproveConstants.APPROVAL_FROMUNIT = 1;//字典表，来文单位
						 //APPROVAL_MODIFYSCRIPT = 2;//字典表，处理备注
						 //APPROVAL_FILETYPE = 3;//字典表，文档类别
	private String name;//名称
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;//添加的用户
	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Company company;//单位编号，主要是考虑单位公用的情况
	private String filetype;//属于哪个文件类别
	private Long orgid;//私有云按照这个来
	private String picurl;//对应的图标位置，文件类型用的


	public String getPicurl() {
		return picurl;
	}
	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}
	public Long getOrgid() {
		return orgid;
	}
	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
}
