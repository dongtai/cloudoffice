package apps.moreoffice.report.server.service.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.ReportTools;
import apps.moreoffice.report.commons.domain.constants.PermissionCons;
import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.DataField;
import apps.moreoffice.report.commons.domain.databaseObject.DataTable;
import apps.moreoffice.report.commons.domain.databaseObject.Permission;
import apps.moreoffice.report.commons.domain.databaseObject.RField;
import apps.moreoffice.report.commons.domain.databaseObject.RTable;
import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.server.resource.TemplateSortResource;
import apps.moreoffice.report.server.service.manager.dataCenter.ITableDB;

/**
 * 表和字段管理器
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-16
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class TableManager extends BaseManager
{
    // 模板管理器
    private TemplateManager templateM;
    // 权限管理器
    private PermissionManager permissionM;
    // 数据库操作接口类
    private ITableDB db;

    /**
     * 对RTable进行存盘前处理
     * 
     * @param template 模板对象
     */
    protected void beforeSave(Template template)
    {
        Set<RTable> rtables = template.getRtables();
        if (rtables != null && rtables.size() > 0)
        {
            Set<RField> rfields;
            for (RTable rtable : rtables)
            {
                // 为了避免客户端错误，在此再次设置一下Tamplate，否则可能造成数据库数据不正确
                rtable.setTemplate(template);

                /**
                 * 建立DataTable和DataField的关联，客户端传过来的对象只有RField和DataField的关系
                 * 没有DataTable和DataField的关联，因此在存盘前需要建立好
                 */
                rfields = rtable.getRfields();
                Set<DataField> dfields = new HashSet<DataField>();
                if (rfields != null && rfields.size() > 0)
                {
                    // 把所有DataField放到一个Set中
                    for (RField rfield : rfields)
                    {
                        rfield.setRtable(rtable);
                        dfields.add(rfield.getDfield());
                    }
                    // 设置DataField的Set集合到DataTable中
                    rtable.getDtable().setDfields(dfields);
                }
            }
        }
        else
        {
            template.setRtables(null);
        }
    }

    /**
     * 得到具有权限的报表数据表列表(仅基本数据)
     * 
     * @param userID 用户ID
     * @param pathType 路径类型(分类or模板)
     * @param path 名称(分类或模板名称)
     * @param tableType 表类型(单一、重复、所有)
     * @param tableOperateType 表操作类型(提数、回写)
     * @param dataOperateType 记录操作类型(新建、修改、删除)
     * @return ArrayList<RTable> RTable集合
     */
    public ArrayList<RTable> getRTableList(long userID, int pathType, String path, int tableType,
        int tableOperateType, int dataOperateType)
    {
        // 外部数据源
        if (TemplateSortResource.EXTERNALPATH.equals(path))
        {
            return db.getOtherRTables();
        }

        // 得到所有的模板名
        String[] templateNames = null;
        // 如果是分类，先得到该分类下所有的模板
        ArrayList<Template> templateInfos = new ArrayList<Template>();
        if (pathType == TableCons.SORT)
        {
            long permission = 0;
            permission = ReportTools.setLongFlag(permission, tableOperateType, true);
            permission = ReportTools.setLongFlag(permission, dataOperateType, true);
            templateInfos = templateM.getTemplateList(userID, path, permission, true, true);
            if (templateInfos != null && templateInfos.size() > 0)
            {
                int size = templateInfos.size();
                templateNames = new String[size];
                for (int i = 0; i < size; i++)
                {
                    templateNames[i] = templateInfos.get(i).getName();
                }
            }
        }
        else
        {
            templateNames = new String[]{path};
        }

        // 根据模板名得到所有可操作的表
        ArrayList<RTable> tableList = new ArrayList<RTable>();
        if (templateNames != null && templateNames.length > 0)
        {
            long pValue;
            Template template;
            ArrayList<Permission> permissionList;
            for (String templateName : templateNames)
            {
                // 得到模板对象
                template = templateM.getTemplateByName(templateName);
                if (template == null)
                {
                    continue;
                }

                // 得到用户在当前模板上的权限
                permissionList = permissionM.getPermission(PermissionCons.TEMPLATE,
                    template.getId(), userID);
                // 权限判断
                if (permissionList != null && permissionList.size() > 0)
                {
                    boolean hasPermission = false;
                    if (tableOperateType != 0 || dataOperateType != 0)
                    {
                        for (Permission permission : permissionList)
                        {
                            pValue = permission.getPermission();
                            if ((tableOperateType != 0 && ReportTools.isLongFlag(pValue,
                                tableOperateType))
                                || (dataOperateType != 0 && ReportTools.isLongFlag(pValue,
                                    dataOperateType)))
                            {
                                hasPermission = true;
                            }
                        }
                    }

                    // 如果有权限
                    if (hasPermission)
                    {
                        Set<RTable> rtables = template.getRtables();
                        if (rtables != null && rtables.size() > 0)
                        {
                            for (RTable rtable : rtables)
                            {
                                if ((tableType == TableCons.SINGLETABLE && !rtable.isSingleTable())
                                    || (tableType == TableCons.REPEATTABLE && !rtable
                                        .isRepeatTable())
                                    || rtable.getDtable().getCreateState() != TableCons.HASCREATED)
                                {
                                    continue;
                                }

                                tableList.add(rtable);
                            }
                        }
                    }
                }
            }
        }

        return tableList;
    }

    /**
     * 得到具有权限的已创建的用户数据表
     * 
     * @param userID 用户ID
     * @param tableOperateType tableOperateType 表操作类型(添加、映射)
     * @return ArrayList<DataTable> DataTable集合
     */
    public ArrayList<DataTable> getDataTableList(long userID, int tableOperateType)
    {
        // 得到所有的DataTable对象
        ArrayList<DataTable> dataTableList = db.getDataTables();
        // 如果没有，则返回空
        if (dataTableList == null)
        {
            return null;
        }

        // 选择具有权限的表
        boolean hasPermission = true;
        ArrayList<DataTable> dtables = new ArrayList<DataTable>();
        ArrayList<Permission> permissionList;
        for (DataTable dtable : dataTableList)
        {
            // -1表示是从页面部分发过来的请求，暂时不屏蔽系统表
            if (tableOperateType != -1 && dtable.getName().startsWith("sys_"))
            {
                continue;
            }

            // 如果当前表没有创建，则继续
            if (dtable.getCreateState() != TableCons.HASCREATED)
            {
                continue;
            }

            // 得到当前用户对当前表的操作权限
            hasPermission = true;
            permissionList = permissionM
                .getPermission(PermissionCons.TABLE, dtable.getId(), userID);
            if (permissionList != null && permissionList.size() > 0)
            {
                // 如果有权限，进行过滤
                Permission permission;
                for (int i = permissionList.size() - 1; i >= 0; i--)
                {
                    permission = (Permission)permissionList.get(i);
                    if (!ReportTools.isLongFlag(permission.getPermission(), tableOperateType))
                    {
                        hasPermission = false;
                        break;
                    }
                }
            }

            if (hasPermission)
            {
                dtables.add(dtable);
            }
        }

        return dtables;
    }

    /**
     * 检查表名是否合法
     * 
     * @param tableName 表名
     * @return boolean 是否合法
     */
    public boolean checkTableName(String tableName)
    {
        return db.checkTableName(tableName);
    }

    /**
     * 根据RTableID得到RTable对象
     * 
     * @param rtableID RTableID
     * @return RTable RTable对象
     */
    public RTable getRTable(long rtableID)
    {
        DataBaseObject dbo = db.getRTableByID(rtableID);
        if (dbo instanceof RTable)
        {
            return (RTable)dbo;
        }
        return null;
    }

    // ----------配置管理器----------
    /**
     * @param templateM 设置 templateM
     */
    public void setTemplateM(TemplateManager templateM)
    {
        this.templateM = templateM;
    }

    /**
     * @param permissionM 设置 permissionM
     */
    public void setPermissionM(PermissionManager permissionM)
    {
        this.permissionM = permissionM;
    }

    /**
     * @param db 设置 db
     */
    public void setDb(ITableDB db)
    {
        this.db = db;
        setBasedb(db);
    }
}