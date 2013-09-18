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
 * 流程表单临时表，此表的主要作用是流程过程中产生的表单数据 文件注释
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 1.0
 * @see
 * @since web1.0
 */
@Entity
@Table(name = "flowformtemp")
public class FlowFormTemp implements SerializableAdapter
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_flowformtemp_gen")
	@GenericGenerator(name = "seq_flowformtemp_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FLOWFORMTEMP_ID") })
	private Long id;// 主键

	@Column(name = "flowinfoid")
	private Long flowinfoid;// 签批执行的哪个流程，自定义流程
	private Long mainformid;// 主表单编号，flowform的主键

	private Long nodeid;// 流程执行的当前节点
	private Long modifier;// 处理者
	private Date modifytime;// 当前处理时间
	private Long stateid;// 当前状态
	@Column(length = 100)
	private String statename;

	private Long owner;// 当前拥有者或者叫办理人员
	@Column(length = 100)
	private String stepname;// 当前步骤名称
	@Column(length = 100)
	private String actionname;// 当前操作
	private Date cometime;// 接受时间
	@Column(length = 1000)
	private String modifyscript;// 办理意见（备注）
	private Long submiter;// 提交者
	private Date submitdate;// 提交时间

	// 以上是共有表单字段
	// 还有其他表单
	private Long num1;// 申请类型
	private Long num2;
	private Long num3;
	private Long num4;
	private Long num5;
	private Long num6;
	private Long num7;
	private Long num8;
	private Long num9;
	private Long num10;
	private Long num11;
	private Long num12;

	@Column(length = 100)
	private String numname1;// 办文单
	@Column(length = 100)
	private String numname2;// 申请人
	@Column(length = 100)
	private String numname3;// 申请人代码
	@Column(length = 100)
	private String numname4;// 办件名称
	@Column(length = 100)
	private String numname5;// 发文号
	@Column(length = 20)
	private String numname6;// 联系电话
	@Column(length = 50)
	private String numname7;// 联系人
	@Column(length = 100)
	private String numname8;
	@Column(length = 100)
	private String numname9;
	@Column(length = 1000)
	private String numname10;
	@Column(length = 1000)
	private String numname11;
	@Column(length = 1000)
	private String numname12;
	 @Transient
	private String modifyname="";//办理人
	 @Column(length = 100)
    private String submitname;//提交人
    
    
	public String getModifyname()
	{
		return modifyname;
	}

	public void setModifyname(String modifyname)
	{
		this.modifyname = modifyname;
	}

	public String getSubmitname()
	{
		return submitname;
	}

	public void setSubmitname(String submitname)
	{
		this.submitname = submitname;
	}

	public String getStatename()
	{
		return statename;
	}

	public void setStatename(String statename)
	{
		this.statename = statename;
	}

	public Long getSubmiter()
	{
		return submiter;
	}

	public void setSubmiter(Long submiter)
	{
		this.submiter = submiter;
	}

	public Date getSubmitdate()
	{
		return submitdate;
	}

	public void setSubmitdate(Date submitdate)
	{
		this.submitdate = submitdate;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getMainformid()
	{
		return mainformid;
	}

	public void setMainformid(Long mainformid)
	{
		this.mainformid = mainformid;
	}

	public Long getFlowinfoid()
	{
		return flowinfoid;
	}

	public void setFlowinfoid(Long flowinfoid)
	{
		this.flowinfoid = flowinfoid;
	}

	public Long getNodeid()
	{
		return nodeid;
	}

	public void setNodeid(Long nodeid)
	{
		this.nodeid = nodeid;
	}

	public Long getModifier()
	{
		return modifier;
	}

	public void setModifier(Long modifier)
	{
		this.modifier = modifier;
	}

	public Date getModifytime()
	{
		return modifytime;
	}

	public void setModifytime(Date modifytime)
	{
		this.modifytime = modifytime;
	}

	public Long getStateid()
	{
		return stateid;
	}

	public void setStateid(Long stateid)
	{
		this.stateid = stateid;
	}

	public Long getOwner()
	{
		return owner;
	}

	public void setOwner(Long owner)
	{
		this.owner = owner;
	}

	public String getStepname()
	{
		return stepname;
	}

	public void setStepname(String stepname)
	{
		this.stepname = stepname;
	}

	public String getActionname()
	{
		return actionname;
	}

	public void setActionname(String actionname)
	{
		this.actionname = actionname;
	}

	public Date getCometime()
	{
		return cometime;
	}

	public void setCometime(Date cometime)
	{
		this.cometime = cometime;
	}

	public String getModifyscript()
	{
		return modifyscript;
	}

	public void setModifyscript(String modifyscript)
	{
		this.modifyscript = modifyscript;
	}

	public Long getNum1()
	{
		return num1;
	}

	public void setNum1(Long num1)
	{
		this.num1 = num1;
	}

	public Long getNum2()
	{
		return num2;
	}

	public void setNum2(Long num2)
	{
		this.num2 = num2;
	}

	public Long getNum3()
	{
		return num3;
	}

	public void setNum3(Long num3)
	{
		this.num3 = num3;
	}

	public Long getNum4()
	{
		return num4;
	}

	public void setNum4(Long num4)
	{
		this.num4 = num4;
	}

	public Long getNum5()
	{
		return num5;
	}

	public void setNum5(Long num5)
	{
		this.num5 = num5;
	}

	public Long getNum6()
	{
		return num6;
	}

	public void setNum6(Long num6)
	{
		this.num6 = num6;
	}

	public Long getNum7()
	{
		return num7;
	}

	public void setNum7(Long num7)
	{
		this.num7 = num7;
	}

	public Long getNum8()
	{
		return num8;
	}

	public void setNum8(Long num8)
	{
		this.num8 = num8;
	}

	public Long getNum9()
	{
		return num9;
	}

	public void setNum9(Long num9)
	{
		this.num9 = num9;
	}

	public Long getNum10()
	{
		return num10;
	}

	public void setNum10(Long num10)
	{
		this.num10 = num10;
	}

	public Long getNum11()
	{
		return num11;
	}

	public void setNum11(Long num11)
	{
		this.num11 = num11;
	}

	public Long getNum12()
	{
		return num12;
	}

	public void setNum12(Long num12)
	{
		this.num12 = num12;
	}

	public String getNumname1()
	{
		return numname1;
	}

	public void setNumname1(String numname1)
	{
		this.numname1 = numname1;
	}

	public String getNumname2()
	{
		return numname2;
	}

	public void setNumname2(String numname2)
	{
		this.numname2 = numname2;
	}

	public String getNumname3()
	{
		return numname3;
	}

	public void setNumname3(String numname3)
	{
		this.numname3 = numname3;
	}

	public String getNumname4()
	{
		return numname4;
	}

	public void setNumname4(String numname4)
	{
		this.numname4 = numname4;
	}

	public String getNumname5()
	{
		return numname5;
	}

	public void setNumname5(String numname5)
	{
		this.numname5 = numname5;
	}

	public String getNumname6()
	{
		return numname6;
	}

	public void setNumname6(String numname6)
	{
		this.numname6 = numname6;
	}

	public String getNumname7()
	{
		return numname7;
	}

	public void setNumname7(String numname7)
	{
		this.numname7 = numname7;
	}

	public String getNumname8()
	{
		return numname8;
	}

	public void setNumname8(String numname8)
	{
		this.numname8 = numname8;
	}

	public String getNumname9()
	{
		return numname9;
	}

	public void setNumname9(String numname9)
	{
		this.numname9 = numname9;
	}

	public String getNumname10()
	{
		return numname10;
	}

	public void setNumname10(String numname10)
	{
		this.numname10 = numname10;
	}

	public String getNumname11()
	{
		return numname11;
	}

	public void setNumname11(String numname11)
	{
		this.numname11 = numname11;
	}

	public String getNumname12()
	{
		return numname12;
	}

	public void setNumname12(String numname12)
	{
		this.numname12 = numname12;
	}

}
