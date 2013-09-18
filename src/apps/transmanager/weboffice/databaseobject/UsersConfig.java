package apps.transmanager.weboffice.databaseobject;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 用户的配置信息。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name = "usersconfig")
public class UsersConfig implements SerializableAdapter {
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_users_config_gen")
	@GenericGenerator(name = "seq_users_config_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_USERS_CONFIG_ID") })
	private Long id;
	@ManyToOne()
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Users user; // 该配置所属的用户
	private int opentype; // 打开文件的方式。具体常量参见。com.evermore.weboffice.constants.both.FileSystemCons。
	private Integer signVersion;    // 移动端的签名版本
	@Lob
	private byte[] sign;        // 移动端常用的具体签名内容。

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public int getOpentype() {
		return opentype;
	}

	public void setOpentype(int opentype) {
		this.opentype = opentype;
	}

	/*@ManyToOne()
	private YzStyle yzstyle;

	public YzStyle getYzstyle() {
		return yzstyle;
	}

	public void setYzstyle(YzStyle yzstyle) {
		this.yzstyle = yzstyle;
	}

	@OneToMany(mappedBy = "usersConfig",fetch=FetchType.EAGER)
	@Cascade(value = { CascadeType.DELETE })
	@OrderBy("id asc")
	private Set<UsersYzAPP> yzAPPSet;

	public Set<UsersYzAPP> getYzAPPSet() {
		return yzAPPSet;
	}

	public void setYzAPPSet(Set<UsersYzAPP> yzAPPSet) {
		this.yzAPPSet = yzAPPSet;
	}

	@OneToMany(targetEntity =UsersYzModule.class,mappedBy = "usersConfig",fetch=FetchType.EAGER)
	@Cascade(value = { CascadeType.DELETE })
	@OrderBy("col asc,row asc")
	private Set<UsersYzModule> yzModuleSet;

	public Set<UsersYzModule> getYzModuleSet() {
		return yzModuleSet;
	}

	public void setYzModuleSet(Set<UsersYzModule> yzModuleSet) {
		this.yzModuleSet = yzModuleSet;
	}

	@Transient
	// json数据转换时用
	private ArrayList<ArrayList<YzModule>> modules;


	public ArrayList<ArrayList<YzModule>> getModules() {
		if(modules != null)
		{
			return modules;
		}
		Set<UsersYzModule> set = getYzModuleSet();
		if(set == null)
		{
			return null;
		}
		modules = new ArrayList<ArrayList<YzModule>>();
		for (UsersYzModule usersYzModule : set) {
			int col = usersYzModule.getCol();
			int row = usersYzModule.getRow();
			ArrayList<YzModule> lists = null;
			if (modules.size() > col) {
				lists = modules.get(col);
			}
			if (lists == null) {
				lists = new ArrayList<YzModule>();
				if (col >= modules.size()) {
					for (int i = modules.size(); i <= col; i++) {
						modules.add(new ArrayList<YzModule>());
					}
				}
				modules.set(col, lists);
			}
			if (row >= lists.size()) {
				for (int i = lists.size(); i <= row; i++) {
					lists.add(null);
				}
			}
			lists.set(row, usersYzModule.getYzmodule());
		}
		return modules;
	}

	public void setModules(ArrayList<ArrayList<YzModule>> modules) {
		this.modules = modules;
	}

	@Transient
	// json数据转换时用
	private ArrayList<YzAPP> apps;
	
	public ArrayList<YzAPP> getApps() {
		if(apps != null)
		{
			return apps;
		}
		apps = new ArrayList<YzAPP>();
		Set<UsersYzAPP> set = getYzAPPSet();
		if(set == null)
		{
			return null;
		}
		for (UsersYzAPP usersyzapp : set) {
			apps.add(usersyzapp.getYzapp());
		}
		return apps;
	}

	public void setApps(ArrayList<YzAPP> apps) {
		this.apps = apps;
	}*/

	private Integer layout=4;

	public Integer getLayout() {
		return layout;
	}

	public void setLayout(Integer layout) {
		this.layout = layout;
	}
	
	private String skin;

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	private String apps;
	
	public String getApps() {
		return apps;
	}

	public void setApps(String apps) {
		this.apps = apps;
	}

	public String getModules() {
		return modules;
	}

	public void setModules(String modules) {
		this.modules = modules;
	}

	
	public Integer getSignVersion()
	{
		return signVersion;
	}

	public void setSignVersion(Integer signVersion)
	{
		this.signVersion = signVersion;
	}

	public byte[] getSign()
	{
		return sign;
	}

	public void setSign(byte[] sign)
	{
		this.sign = sign;
	}

	private String modules;


	
}
