package apps.transmanager.weboffice.domain;

import java.util.Date;

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
@Table(name="mblog_group")
public class MicroGroupPo implements SerializableAdapter{

	private static final long serialVersionUID = 5958170934291465394L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_groups_gen")
	@GenericGenerator(name = "seq_groups_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_GROUPS_ID") })
	private Long id;
	
	private String group_name;
	
	private String group_image;
	private String group_description;
	
	@ManyToOne
	@JoinColumn(name="group_manager_id")
	@OnDelete(action=OnDeleteAction.NO_ACTION) 
	private Users group_manager;//微群创建者
	
	private Date create_time;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public String getGroup_image() {
		return group_image;
	}

	public void setGroup_image(String group_image) {
		this.group_image = group_image;
	}

	public String getGroup_description() {
		return group_description;
	}

	public void setGroup_description(String group_description) {
		this.group_description = group_description;
	}

	public Users getGroup_manager() {
		return group_manager;
	}

	public void setGroup_manager(Users group_manager) {
		this.group_manager = group_manager;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getCreate_time() {
		return create_time;
	}
}
