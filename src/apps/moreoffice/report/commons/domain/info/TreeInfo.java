package apps.moreoffice.report.commons.domain.info;

import java.util.ArrayList;
import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.ParamCons;

/**
 * 树结构信息
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-18
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class TreeInfo extends BaseInfo
{
    // 序列化ID
    private static final long serialVersionUID = -7940032444462812925L;
    // 父节点
    protected TreeInfo parent;
    // 孩子节点
    protected TreeInfo[] childs;
    // 路径
    protected String path;

    /**
     * 得到父节点
     * 
     * @return TreeInfo 父节点
     */
    public TreeInfo getParent()
    {
        return parent;
    }

    /**
     * 得到孩子节点
     * 
     * @return TreeInfo[] 孩子节点
     */
    public TreeInfo[] getChilds()
    {
        return childs;
    }

    /**
     * 设置孩子节点
     * 
     * @param childs
     */
    public void setChilds(TreeInfo[] childs)
    {
        this.childs = childs;
        if (childs != null)
        {
            for (TreeInfo child : childs)
            {
                child.parent = this;
            }
        }
    }

    /**
     * @return 返回 path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * @param path 设置 path
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * 得到json格式的HashMap对象
     * 
     * @return HashMap<String, Object> json格式的HashMap对象
     */
    public HashMap<String, Object> getJsonObj()
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        // 父信息
        params.putAll(super.getJsonObj());
        // 路径
        params.put(ParamCons.PATH, path);
        // 父节点
        if (parent != null)
        {
            params.put(ParamCons.PARENT, parent.getJsonObj());
        }
        // 孩子节点
        if (childs != null && childs.length > 0)
        {
            ArrayList<HashMap<String, Object>> childList = new ArrayList<HashMap<String, Object>>();
            for (TreeInfo child : childs)
            {
                childList.add(child.getJsonObj());
            }
            params.put(ParamCons.CHILDS, childList);
        }
        return params;
    }
}