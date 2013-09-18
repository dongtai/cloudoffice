package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

@Entity
@Table(name="mailaccountsign")
//暂时只做最简单的文本,如有logo目前在解析邮件时还有问题.
public class MailAccountSign implements SerializableAdapter
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_mail_account_sign_gen")
	@GenericGenerator(name = "seq_mail_account_sign_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MAIL_ACCOUNT_SIGN_ID") })
	private Long id;
	
	@ManyToOne()
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Index(name = "IDX_ACC")
	private MailAccount account;// 某账户下的邮件
	
	@Column(length = 1024)
	private String sign;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MailAccount getAccount() {
		return account;
	}

	public void setAccount(MailAccount account) {
		this.account = account;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	
}
