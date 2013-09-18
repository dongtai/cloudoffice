package apps.transmanager.weboffice.databaseobject.bug;


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
 * BUG动作表
 * @author 孙爱华
 * 2013.6.18
 *
 */
@Entity
@Table(name="bugactions")
public class BugActions implements SerializableAdapter{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_bugactions_gen")
	@GenericGenerator(name = "seq_bugactions_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_BUGACTIONS_ID") })
	private Long id;//编号
	private String actionname;//动作名称

	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BugStates bugStates;//对应的BUG状态
	private Integer viewtype=1;//显示人员类别 ，1，指定人员， 2，单位筛选，3，部门
	private Integer isupload=0;//是否必须上传附件
	private String uploadscript;//上传备注
	private Integer isscript=0;//是否必须填写备注
	private Integer isdelete=0;//是否删除标记，1为删除
	
	public BugActions()
	{
		
	}
	public BugActions(Long id)
	{
		this.id=id;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getActionname() {
		return actionname;
	}
	public void setActionname(String actionname) {
		this.actionname = actionname;
	}
	public BugStates getBugStates() {
		return bugStates;
	}
	public void setBugStates(BugStates bugStates) {
		this.bugStates = bugStates;
	}
	public Integer getIsupload() {
		return isupload;
	}
	public void setIsupload(Integer isupload) {
		this.isupload = isupload;
	}
	public String getUploadscript() {
		return uploadscript;
	}
	public void setUploadscript(String uploadscript) {
		this.uploadscript = uploadscript;
	}
	public Integer getIsscript() {
		return isscript;
	}
	public void setIsscript(Integer isscript) {
		this.isscript = isscript;
	}
	public Integer getIsdelete() {
		return isdelete;
	}
	public void setIsdelete(Integer isdelete) {
		this.isdelete = isdelete;
	}
	public Integer getViewtype() {
		return viewtype;
	}
	public void setViewtype(Integer viewtype) {
		this.viewtype = viewtype;
	}
	
	
}
