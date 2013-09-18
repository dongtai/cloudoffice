package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

@Entity
@Table(name="mailsources")
//邮件的源文件
public class MailSources  implements SerializableAdapter
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_mail_sources_gen")
	@GenericGenerator(name = "seq_mail_sources_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_MAIL_SOURCES_ID") })
	private Long id;
	
    @Lob
    @Basic(fetch=FetchType.LAZY)
	private byte[] content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
    
    
}
