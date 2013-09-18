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
 * 用户查看文件的记录表。
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 3.0
 * @see
 * @since web3.0
 */

@Entity
@Table(name="approvalfilesreaders")
public class ApprovalFilesReaders implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvalfilesreaders_gen")
	@GenericGenerator(name = "seq_approvalfilesreaders_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALFILESREADERS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private ApprovalFiles approvalFiles;
	
	public ApprovalFilesReaders()
	{
		
	}
	
	public ApprovalFilesReaders(Users u, ApprovalFiles o)
	{
		user = u;
		approvalFiles = o;
	}
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Users getUser()
	{
		return user;
	}
	public void setUser(Users user)
	{
		this.user = user;
	}

	public ApprovalFiles getApprovalFiles() {
		return approvalFiles;
	}

	public void setApprovalFiles(ApprovalFiles approvalFiles) {
		this.approvalFiles = approvalFiles;
	}
	
}
