package apps.transmanager.weboffice.databaseobject.transmanage;

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

import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 用户查看文件的记录表。（时间关系暂没有对字段进行整理）
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 1.0
 * @see
 * @since web1.0
 */

@Entity
@Table(name="transfilesreaders")
public class TransFilesReaders implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_transfilesreaders_gen")
	@GenericGenerator(name = "seq_transfilesreaders_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_TRANSFILESREADERS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private TransFiles transfiles;
	
	
	public TransFilesReaders(Users u, TransFiles o)
	{
		user = u;
		transfiles = o;
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
	
	public TransFiles getTransfiles() {
		return transfiles;
	}

	public void setTransfiles(TransFiles transfiles) {
		this.transfiles = transfiles;
	}

}
