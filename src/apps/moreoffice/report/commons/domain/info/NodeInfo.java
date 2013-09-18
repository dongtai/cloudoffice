package apps.moreoffice.report.commons.domain.info;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 节点信息
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-9-3
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class NodeInfo implements SerializableAdapter
{
    // 序列化ID
    private static final long serialVersionUID = 1769671274268893185L;
    // 节点类型
    private int type;
    // 节点名称
    private String name;
    // 节点路径
    private String path;
    // 是否是叶子节点
    private boolean isLeaf;
    // 模板ID
    private long templateID;

    /**
     * 默认构造器
     */
    public NodeInfo()
    {

    }

    /**
     * 构造器
     * 
     * @param type 节点类型
     * @param name 节点名称
     * @param path 节点路径
     * @param isLeaf 是否是叶子节点
     */
    public NodeInfo(int type, String name, String path, boolean isLeaf)
    {
        this(type, name, path, isLeaf, 0);
    }

    /**
     * 构造器
     * 
     * @param type 节点类型
     * @param name 节点名称
     * @param path 节点路径
     * @param isLeaf 是否是叶子节点
     * @param templateID 模板ID
     */
    public NodeInfo(int type, String name, String path, boolean isLeaf, long templateID)
    {
        this.path = path;
        this.name = name;
        this.type = type;
        this.isLeaf = isLeaf;
        this.templateID = templateID;
    }

    /**
     * @return 返回 type
     */
    public int getType()
    {
        return type;
    }

    /**
     * @return 返回 name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return 返回 path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * @return 返回 isLeaf
     */
    public boolean isLeaf()
    {
        return isLeaf;
    }

    /**
     * @return 返回 templateID
     */
    public long getTemplateID()
    {
        return templateID;
    }

    /**
     * 得到json格式的HashMap对象
     * 
     * @return HashMap<String, Object> json格式的HashMap对象
     */
    public HashMap<String, Object> getJsonObj()
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        // 节点类型
        params.put(ParamCons.TYPE, type);
        // 节点名称
        params.put(ParamCons.NAME, name);
        // 节点路径
        params.put(ParamCons.PATH, path);
        // 是否是叶子节点
        params.put(ParamCons.ISLEAF, isLeaf);
        // 模板ID
        params.put(ParamCons.TEMPLATEID, templateID);

        return params;
    }
}