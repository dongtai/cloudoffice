package apps.moreoffice.report.commons.domain.info;

import java.util.ArrayList;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.databaseObject.RTable;
import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.commons.domain.interfaces.IReportService;

/**
 * 模板分类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-16
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class TemplateSort extends TreeInfo
{
    // 序列化ID
    private static final long serialVersionUID = -2458331628226255906L;

    /**
     * 得到孩子节点
     * 
     * @param service 报表服务
     * @return TemplateSort[] 孩子节点
     */
    @ SuppressWarnings({"rawtypes", "unchecked"})
    public TemplateSort[] getChilds(IReportService service)
    {
        if (service != null)
        {
            Result result = service.getTemplateSort(getPath());
            if (!result.hasError() && result.getData() instanceof ArrayList)
            {
                ArrayList list = (ArrayList)result.getData();
                if (list != null && list.size() > 0)
                {
                    TemplateSort[] childs = new TemplateSort[list.size()];
                    list.toArray(childs);
                    return childs;
                }
            }
        }
        return null;
    }

    /**
     * 得到RTable列表
     * 
     * @param service 报表服务
     * @param tableType 表类型(单一、重复、所有)
     * @param tableOperateType 表操作类型(提数、回写)
     * @param dataOperateType 记录操作类型(新建、修改、删除)
     * @return ArrayList<RTable> RTable列表
     */
    @ SuppressWarnings("unchecked")
    public ArrayList<RTable> getRTableList(IReportService service, int tableType,
        int tableOperateType, int dataOperateType)
    {
        if (service != null)
        {
            Result result = service.getRTableList(TableCons.SORT, getPath(), tableType,
                tableOperateType, dataOperateType);
            if (!result.hasError())
            {
                return (ArrayList<RTable>)result.getData();
            }
        }
        return null;
    }

    /**
     * 得到模板列表
     * 
     * @param service 报表服务
     * @param permissionType 权限类型
     * @return ArrayList<Template> 模板列表
     */
    @ SuppressWarnings("unchecked")
    public ArrayList<Template> getTemplateList(IReportService service, int permissionType)
    {
        if (service != null)
        {
            Result result = service.getTemplateList(path, permissionType);
            if (!result.hasError())
            {
                return (ArrayList<Template>)result.getData();
            }
        }
        return null;
    }
}