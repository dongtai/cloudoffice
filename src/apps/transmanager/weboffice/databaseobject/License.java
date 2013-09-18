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

@Entity
@Table(name="license")
public class License implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_license_gen")
	@GenericGenerator(name = "seq_license_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_LICENSE_ID") })
	private Integer id;
	private Long uc;
	private Long ud;
	private Long bud;
	@Column(length = 100)
	private String company;
	@Column(length = 1000)
	private String content;
	@Column(length = 255)
	private String mailHost;      // 系统发送邮件的服务器。
	@Column(length = 100)
	private String mailAddress;      // 系统发送邮件的用户名。后续系统提供邮件时候，则该字段不在需要。
	@Column(length = 1000)
	private String mailPwd;       // 系统发送邮件的password。
	@Column(length = 255)
	private String res;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Long getUc()
	{
		return uc;
	}

	public void setUc(Long uc)
	{
		this.uc = uc;
	}

	public Long getUd()
	{
		return ud;
	}

	public void setUd(Long ud)
	{
		this.ud = ud;
	}

	public String getCompany()
	{
		return company;
	}

	public void setCompany(String company)
	{
		this.company = company;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getRes()
	{
		return res;
	}

	public void setRes(String res)
	{
		this.res = res;
	}

	public Long getBud()
	{

		return bud;
	}

	public void setBud(Long bud)
	{

		this.bud = bud;
	}

	public String getMailHost()
	{
		return mailHost;
	}

	public void setMailHost(String mailHost)
	{
		this.mailHost = mailHost;
	}

	public String getMailAddress()
	{
		return mailAddress;
	}

	public void setMailAddress(String mailAddress)
	{
		this.mailAddress = mailAddress;
	}

	public String getMailPwd()
	{
		return mailPwd;
	}

	public void setMailPwd(String mailPwd)
	{
		this.mailPwd = mailPwd;
	}

}
