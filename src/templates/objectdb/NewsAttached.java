package templates.objectdb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@Entity 
@Table(name="newsattached")
public class NewsAttached implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_newsattached_gen")
    @GenericGenerator(name = "seq_newsattached_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_NEWSATTACHED_ID") })
	private Long tid;
	@Column(length = 60000)
	private String attached;
	
	
	public Long getTid()
	{
		return tid;
	}

	public void setTid(Long tid)
	{
		this.tid = tid;
	}
	public String getAttached() {
		return attached;
	}

	public void setAttached(String attached) {
		this.attached = attached;
	}

}
