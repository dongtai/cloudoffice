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
@Table(name="receptionimg")
public class ReceptionImg implements SerializableAdapter{

	private static final long serialVersionUID = 4323567809025909073L;
    
	/**
	 * ID
	 */
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_receptionImg_gen")
	@GenericGenerator(name = "seq_receptionImg_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_RECEPTIONIMG_ID") })
	private Long id;
	/**
	 * 接待记录ID
	 */
	private Long receptionId;
	/**
	 * 图片地址
	 */
	@Column(length = 100)
	private String url;
	/**
	 * 图片名称
	 */
	@Column(length = 100)
	private String name;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getReceptionId() {
		return receptionId;
	}
	public void setReceptionId(Long receptionId) {
		this.receptionId = receptionId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}
