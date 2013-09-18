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
@Table(name="newstype")
public class NewsType implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_newstype_gen")
    @GenericGenerator(name = "seq_newstype_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_NEWSTYPE_ID") })
	private Long tid;
	
	/**行业*/
	@Column(length = 255)
	private String typeNames;

	public Long getTid() {
		return tid;
	}

	public void setTid(Long tid) {
		this.tid = tid;
	}

	public String getTypeNames() {
		return typeNames;
	}

	public void setTypeNames(String typeNames) {
		this.typeNames = typeNames;
	}
}
