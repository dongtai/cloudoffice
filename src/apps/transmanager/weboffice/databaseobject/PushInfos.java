package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 文件注释
 * 存放所有消息
 * <p>
 * <p>
 * 
 * @author 孙爱华
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="pushinfos")
public class PushInfos implements SerializableAdapter
{
	private static final long serialVersionUID = -7644114512714619751L;

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_pushinfos_gen")
	@GenericGenerator(name = "seq_pushinfos_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_PUSHINFOS_ID") })
	private Long id;
	@Transient
	private String token = "";
	@Column(name = "userName", length = 100)
	private String userName;              // 用户登录名
	@Column(length = 100)
	private String realName;            // 用户显示名
	@Column(length = 1000)
	private String infocontext;//信息内容
	private Integer isreaded=0;//是否读取，0为未读取，1为已读取

	
	

}
