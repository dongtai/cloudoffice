package apps.transmanager.weboffice.databaseobject.flow;

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
 * 自定义的表单路径保存表
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 1.0
 * @date    2013-1-20     
 * @since   云办公1.0
 */
@Entity
@Table(name="forminfo")
public class FormInfo implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_forminfo_gen")
	@GenericGenerator(name = "seq_forminfo_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FORMINFO_ID") })
	private Long id;//主键

	@Column(name = "formname")
	private String formname;//表单名称
	@Column(name = "formtypeid")
    private Long formtypeid;//表单类型id
	@Column(name = "formtype")
    private String formtype;//表单类型
	@Column(name = "formpath", length = 60000)
    private String formpath;//表单路径
	
	@Column(name = "formscript", length = 60000)
    private String formscript;//表单说明、备注
	
	@Column(name = "createtime")
    private Date createtime;//创建时间
	@Column(name = "userid")
    private Long userid;//创建人
	@Column(name = "companyid")
    private Long companyid;//所属单位
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFormname() {
		return formname;
	}
	public void setFormname(String formname) {
		this.formname = formname;
	}
	public Long getFormtypeid() {
		return formtypeid;
	}
	public void setFormtypeid(Long formtypeid) {
		this.formtypeid = formtypeid;
	}
	public String getFormtype() {
		return formtype;
	}
	public void setFormtype(String formtype) {
		this.formtype = formtype;
	}
	public String getFormpath() {
		return formpath;
	}
	public void setFormpath(String formpath) {
		this.formpath = formpath;
	}
	public String getFormscript() {
		return formscript;
	}
	public void setFormscript(String formscript) {
		this.formscript = formscript;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public Long getCompanyid() {
		return companyid;
	}
	public void setCompanyid(Long companyid) {
		this.companyid = companyid;
	}
	
	
}
