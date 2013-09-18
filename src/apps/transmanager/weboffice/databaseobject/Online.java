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
@Table(name="online")
public class Online implements SerializableAdapter
{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_online_gen")
	@GenericGenerator(name = "seq_online_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ONLINE_ID") })
	private Integer id;
	private Long uc;
	@Column(length = 255)
	private String res;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Long getUc()
	{
		return uc;
	}

	public void setUc(Long uc)
	{
		this.uc = uc;
	}

	public String getRes()
	{
		return res;
	}

	public void setRes(String res)
	{
		this.res = res;
	}

}
