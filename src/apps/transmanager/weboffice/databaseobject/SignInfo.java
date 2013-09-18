package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

@Entity
@Table(name="signinfo")
public class SignInfo implements SerializableAdapter
{	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_signList_gen")
	@GenericGenerator(name = "seq_signList_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SIGN_LIST_ID") })
	private Long signId;
	
	@Column(length = 1000)
	private String fileName;
	@Column(length = 60000)
	private String filePath;
	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users signer;
	
	private Date createDate;
	
	@Column(name="content",length=1000) 
	private String content;
	
	@Transient
	private boolean isVerifyPass; 
	
	
	//0-本系统的签名1-意源的签名
	@Column(length = 10)
	private String signType;
	
	
	public SignInfo()
	{
		
	}
	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}


	public Long getSignId() {
		return signId;
	}



	public void setSignId(Long signId) {
		this.signId = signId;
	}



	public String getFileName() {
		return fileName;
	}



	public void setFileName(String fileName) {
		this.fileName = fileName;
	}



	public String getFilePath() {
		return filePath;
	}



	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}



	public Users getSigner() {
		return signer;
	}



	public void setSigner(Users signer) {
		this.signer = signer;
	}



	public Date getCreateDate() {
		return createDate;
	}



	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getSignType() {
		return signType;
	}
	public void setSignType(String signType) {
		this.signType = signType;
	}
	
	
	public boolean isVerifyPass() {
		return isVerifyPass;
	}
	public void setVerifyPass(boolean isVerifyPass) {
		this.isVerifyPass = isVerifyPass;
	}
}
