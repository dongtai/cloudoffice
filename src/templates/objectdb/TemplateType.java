package templates.objectdb;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;



@Entity 
@Table(name="yozo_templatetype")
public class TemplateType implements Serializable{

	
	
	@Id
	@GeneratedValue
	private Long ttid;
	
	/**类型名称*/
	@Column(length=50)
	private String ptName;
	
	/**行业类型预览图*/
	@Column(length=50)
	private String hyImages;
	
	/**该类型下的模板*/
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "tType")
	private Set<Template> templetes = new HashSet<Template>();


	public String getPtName() {
		return ptName;
	}

	public void setPtName(String ptName) {
		this.ptName = ptName;
	}

	public Set<Template> getTempletes() {
		return templetes;
	}

	public void setTempletes(Set<Template> templetes) {
		this.templetes = templetes;
	}

	public Long getTtid() {
		return ttid;
	}

	public void setTtid(Long ttid) {
		this.ttid = ttid;
	}

	public String getHyImages() {
		return hyImages;
	}

	public void setHyImages(String hyImages) {
		this.hyImages = hyImages;
	}
	
}
