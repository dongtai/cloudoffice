package apps.moreoffice.report.server.service.manager.dataCenter;

import java.util.ArrayList;

import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.commons.domain.info.TemplateSort;

/**
 * 模板库接口
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-5
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface ITemplateLib
{
    /**
     * 初始化
     */
    void init();

    /**
     * 得到指定路径下的模板分类
     * 
     * @param path 路径
     * @return ArrayList<TemplateSort> 指定路径下的模板分类
     */
    ArrayList<TemplateSort> getTemplateSort(String path);

    /**
     * 得到外部数据源分类
     * 
     * @return TemplateSort 外部数据源分类
     */
    TemplateSort getExternalTemplateSort();

    /**
     * 创建模板分类
     * 
     * @param userID 用户ID
     * @param path 路径
     * @param name 名称
     */
    void createTemplateSort(long userID, String path, String name);

    /**
     * 重命名模板分类
     * 
     * @param userID 用户ID
     * @param path 路径
     * @param name 名称
     */
    void renameTemplateSort(long userID, String path, String name);

    /**
     * 得到当前路径下的模板
     * 
     * @param nodePath 路径
     * @param recursive 是否递归得到所有子目录中的模板
     * @return ArrayList<Template> 模板列表
     */
    ArrayList<Template> getTemplateList(String nodePath, boolean recursive);

    /**
     * 得到当前路径下的指定模板
     * 
     * @param nodePath 路径
     * @param recursive 是否递归得到所有子目录中的模板
     * @param startIndex 开始序号
     * @param number 总数
     * @return ArrayList<Template> 模板列表
     */
    ArrayList<Template> getTemplateList(String nodePath, boolean recursive, int startIndex,
        int number);

    /**
     * 删除指定路径
     * 
     * @param path 路径
     */
    void deletePath(String path);
}