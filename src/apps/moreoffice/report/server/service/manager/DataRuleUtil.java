package apps.moreoffice.report.server.service.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import apps.moreoffice.report.commons.JSONTools;
import apps.moreoffice.report.commons.domain.info.TreeInfo;


/**
 * 数据规范工具类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-8-29
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class DataRuleUtil
{
    /**
     * 得到树型选择的固定树
     * 
     * @param fixedValue 固定值树信息
     * @return TreeInfo 根节点
     */
    @ SuppressWarnings("unchecked")
    public static TreeInfo getTreeSelectFixedTree(String fixedValue)
    {
        HashMap<String, Object> rootMap = (HashMap<String, Object>)JSONTools.convertJsonToValue(
            fixedValue, true);
        TreeInfo root = new TreeInfo();
        root.setName("");
        createChilds(root, rootMap.get(""));
        return root;
    }

    /*
     * 遍历树节点
     */
    @ SuppressWarnings("rawtypes")
    private static void createChilds(TreeInfo root, Object childMap)
    {
        Set keySets;
        TreeInfo child;
        if (childMap instanceof ArrayList)
        {
            HashMap map;
            ArrayList array = (ArrayList)childMap;
            TreeInfo[] childs = new TreeInfo[array.size()];
            int index = 0;
            for (Object obj : array)
            {
                map = (HashMap)obj;
                keySets = map.keySet();
                for (Object keySet : keySets)
                {
                    child = new TreeInfo();
                    child.setName(keySet.toString());
                    createChilds(child, map.get(keySet.toString()));
                    childs[index++] = child;
                }
            }
            root.setChilds(childs);
        }
        else if (childMap instanceof HashMap)
        {
            child = new TreeInfo();
            keySets = ((HashMap)childMap).keySet();
            for (Object keySet : keySets)
            {
                child = new TreeInfo();
                child.setName(keySet.toString());
                createChilds(child, ((HashMap)childMap).get(keySet.toString()));
            }
        }
    }
}