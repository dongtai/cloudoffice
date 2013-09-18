package apps.transmanager.weboffice.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.databaseobject.Users;

@Entity
@Table(name="sortlist")
public class SortListPo implements SerializableAdapter{

	private static final long serialVersionUID = 3724980642326226035L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sortList_gen")
	@GenericGenerator(name = "seq_sortList_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SORT_LIST_ID") })
	private Long id;
	@ManyToOne
	@JoinColumn(name="userinfo_id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users userinfo;
	@Column(length = 100)
	private String name;
	@Column(length = 1000)
	private String description;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Users getUserinfo() {
		return userinfo;
	}
	public void setUserinfo(Users userinfo) {
		this.userinfo = userinfo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
