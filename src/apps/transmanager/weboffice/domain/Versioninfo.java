package apps.transmanager.weboffice.domain;


/**
 * 版本信息实体
 * @author 彭俊杰
 *
 */
public class Versioninfo implements SerializableAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 920683175413069614L;

	/**
	 * 版本名称（实际为版本号，由系统自动创建）
	 */
	private String name;
	
	/**
	 * 版本创建时间
	 */
	private String createTime;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 状态
	 */
	private String status;
	
	/**
	 * 创建版本者姓名
	 */
	private String createName;

	/**
	 * 是否被NODE引用
	 */
	private boolean refVersion;
	
	
	private String path;
	
	/**
	 * 下载路径
	 */
	private String downPath;

	public String getDownPath() {
		return downPath;
	}

	public void setDownPath(String downPath) {
		this.downPath = downPath;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isRefVersion() {
		return refVersion;
	}

	public void setRefVersion(boolean refVersion) {
		this.refVersion = refVersion;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
