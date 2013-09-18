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
 * 
 * @author  孙爱华
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@Entity
@Table(name="approvalcollect")
public class ApprovalCollect implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_approvalcollect_gen")
	@GenericGenerator(name = "seq_approvalcollect_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_APPROVALCOLLECT_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users collecter;//收藏者
	
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private ApprovalInfo appinfo;//签批信息
	
	private Date collecttime=new Date();//收藏时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Users getCollecter() {
		return collecter;
	}

	public void setCollecter(Users collecter) {
		this.collecter = collecter;
	}

	public ApprovalInfo getAppinfo() {
		return appinfo;
	}

	public void setAppinfo(ApprovalInfo appinfo) {
		this.appinfo = appinfo;
	}

	public Date getCollecttime() {
		return collecttime;
	}

	public void setCollecttime(Date collecttime) {
		this.collecttime = collecttime;
	}
	
}
