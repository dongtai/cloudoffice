package apps.transmanager.weboffice.databaseobject;


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

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 个人定义的应用模块
 * 
 * @author 孙爱华 2013.3.31
 */

@Entity
@Table(name="personapps")
public class PersonApps implements SerializableAdapter
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_personapps_gen")
	@GenericGenerator(name = "seq_personapps_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_PERSONAPPS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user; // 拥有模块的用户
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private CompanyApps companyApps;//单位拥有的模块
	
	private Long modelid;//模块编号，冗余
	private String modelname;//模块名称，冗余
	private Long companyId;//所属公司，冗余
	private String sortcode;//个人模块排序，暂未用，改起来还麻烦
	private Date createdate=new Date();//添加时间
	
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
	public CompanyApps getCompanyApps() {
		return companyApps;
	}
	public void setCompanyApps(CompanyApps companyApps) {
		this.companyApps = companyApps;
	}
	public Long getModelid() {
		return modelid;
	}
	public void setModelid(Long modelid) {
		this.modelid = modelid;
	}
	public String getModelname() {
		return modelname;
	}
	public void setModelname(String modelname) {
		this.modelname = modelname;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public String getSortcode() {
		return sortcode;
	}
	public void setSortcode(String sortcode) {
		this.sortcode = sortcode;
	}
}