package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Column;
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
 * 用户自定义的分组。有用户自己维护的分组内容
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="customteams")
public class CustomTeams implements SerializableAdapter
{

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_custom_teams_gen")
	@GenericGenerator(name = "seq_custom_teams_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_CUSTOM_TEAMS_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users user;           // 该自定义组所属的用户，即是建立该组的用户。
	@Column(length = 100)
	private String name;
	@Column(length = 1000)
	private String description;
	@Column(length = 255)
	private String spaceUID;           // 用户自定义组空间的根目录名：建立用户自定义组时候的名字+系统时间，即是name_systemtime，以后用户自定义组修改name后，该名字始终不变。
	private Integer filenums=0;//文档总数，上传、复制、移动、删除都要进行更改

	public CustomTeams()
	{		
	}
	
	public CustomTeams(String name, String description)
	{
		this.name = name;
		this.description = description;
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
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getSpaceUID()
	{
		return spaceUID;
	}	
	public void setSpaceUID(String sp)
	{
		spaceUID = sp;
	}
	
	public void update(CustomTeams t)
	{
		name = t.name;
		description = t.description;
	}
	
	public Integer getFilenums() {
		return filenums;
	}

	public void setFilenums(Integer filenums) {
		this.filenums = filenums;
	}

}
