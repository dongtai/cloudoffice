package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.Date;
import java.util.HashMap;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 报表产品表对象基础类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-6
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class DataBaseObject implements SerializableAdapter, Cloneable
{
    // 序列化ID
    private static final long serialVersionUID = -966273305539249695L;
    /**
     * @return 返回 isClient
     */
    public boolean isClient()
    {
        return isClient;
    }

    /**
     * @param isClient 设置 isClient
     */
    public void setClient(boolean isClient)
    {
        this.isClient = isClient;
    }

    /**
     * 克隆一个实体对象
     * 
     * @param isSimple 是否简化处理
     * @return DataBaseObject 克隆后的对象
     */
    public DataBaseObject clone(boolean isSimple)
    {
        return null;
    }

    /**
     * 得到json格式的HashMap对象
     * 
     * @return HashMap<String, Object> json格式的HashMap对象
     */
    public HashMap<String, Object> getJsonObj()
    {
        return null;
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return DataBaseObject 对象
     */
    public DataBaseObject convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        return null;
    }

    // 是否是客户端
    private boolean isClient;

    // 临时值
    protected int intValue;
    protected short shortValue;
    protected long longValue;
    protected Boolean booleanValue;
    protected String stringValue;
    protected Date dateValue;
}