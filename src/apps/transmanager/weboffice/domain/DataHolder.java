package apps.transmanager.weboffice.domain;

import java.util.ArrayList;
import java.util.List;

import apps.transmanager.weboffice.databaseobject.Users;



/**
 * TODO: 此类用于封装基本类型数组，传给后台
 * <p>
 * <p>
 * 负责小组:        WEB
 * <p>
 * <p>
 */
public class DataHolder  implements SerializableAdapter
{
    private long[] longData;
    private String[] stringData;
    private String[] stringValue;

	private ArrayList<Object> folderData;
    private ArrayList<Object> filesData ;
    private int intData;
    private Users user;
    private int shareCount; // 共享的个数
    private int editCount; // 编辑的个数
    private ArrayList<UserinfoView> userinfoviewData;
    private List adminData;
    
    
    public String[] getStringValue() {
		return stringValue;
	}

	public void setStringValue(String[] stringValue) {
		this.stringValue = stringValue;
	}

    /**
     * 
     * @param user
     */
    public void setUserinfo(Users user)
    {
    	this.user = user;
    }
    
    /**
     * 
     * @return
     */
    public Users getUserinfo()
    {
    	return this.user;
    }
    /**
     * 
     * @param data
     */
    public void setLongData(long[] data)
    {
        this.longData = data;
    }
    
    /**
     * 
     */
    public long[] getLongData()
    {
        return this.longData;
    }
    
    public void setStringData(String[] data)
    {
        this.stringData = data;
    }
    
    public String[] getStringData()
    {
        return this.stringData;
    }
    
    public void setFolderData(ArrayList<Object> data)
    {
    	if (folderData == null)
    	{
    		folderData = new ArrayList<Object>();
    	}
        this.folderData = data;
    }
    
    public ArrayList<Object> getFolderData()
    {
        return folderData;
    }
    
    public void setFilesData(ArrayList<Object> data)
    {
    	if (filesData == null)
    	{
    		filesData = new ArrayList<Object>(); 
    	}
        this.filesData = data;
    }
    
    public ArrayList<Object> getFilesData()
    {
        return filesData;
    }
    
    public void setIntData(int data)
    {
        this.intData = data;
    }
    
    public int getIntData()
    {
        return intData;
    }
    public void setShareCount(int data)
    {
        this.shareCount = data;
    }
    
    public int getShareCount()
    {
        return shareCount;
    }
    public void setEditCount(int data)
    {
        this.editCount = data;
    }
    
    public int getEditCount()
    {
        return editCount;
    }

    public ArrayList<UserinfoView> getUserinfoviewData()
    {
        return userinfoviewData;
    }

    public void setUserinfoviewData(ArrayList<UserinfoView> userinfoviewData)
    {
        this.userinfoviewData = userinfoviewData;
    }
    public List getAdminData() {
		return adminData;
	}

	public void setAdminData(List adminData) {
		this.adminData = adminData;
	}
    
}
