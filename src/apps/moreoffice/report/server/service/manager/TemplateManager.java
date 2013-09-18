package apps.moreoffice.report.server.service.manager;

import java.util.ArrayList;
import java.util.Date;

import apps.moreoffice.report.commons.ReportTools;
import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.PermissionCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.Permission;
import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.commons.domain.info.TemplateSort;
import apps.moreoffice.report.server.resource.TemplateSortResource;
import apps.moreoffice.report.server.service.manager.dataCenter.ITemplateDB;
import apps.moreoffice.report.server.service.manager.dataCenter.ITemplateLib;
import apps.moreoffice.report.server.util.ErrorManager;

/**
 * 模板管理器
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-15
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class TemplateManager extends BaseManager
{
    // 模板数据库操作接口
    private ITemplateDB db;
    // 模板库操作接口
    private ITemplateLib lib;
    // 用户管理器
    private IUserManager userM;
    // 表和字段管理器
    private TableManager tableM;
    // 表间规则管理器
    private TableRuleManager tableRuleM;
    // 权限管理器
    private PermissionManager permissionM;

    /**
     * 模板对象存盘前的检查
     * 
     * @param templateNum 模板编号
     * @param templateName 模板名称
     * @return Result 结果
     */
    public Result checkTemplate(String templateNum, String templateName)
    {
        Result result = new Result();
        // TODO 增加资源
        if (db.getTemplateByNum(templateNum) != null)
        {
            result.setErrorMessage("当前模板编号：" + templateNum + " 已经定义，请定义别的编号");
            return result;
        }
        if (db.getTemplateByName(templateName) != null)
        {
            result.setErrorMessage("当前模板名称：" + templateName + " 已经定义，请定义别的名称");
        }
        return result;
    }

    /**
     * 存盘
     * 
     * @param entity 实体对象
     * @return DataBaseObject 保存后的对象
     */
    public DataBaseObject save(DataBaseObject entity)
    {
        Template template = (Template)entity;

        /*
         * 此处合并的原因是，当打开模板后，不弹出模板属性对话盒，此时保存时
         * 客户端传过来的template对象是不包含权限的，因此需要合并，否则会把所有权限全部删除
         */
        if (template.getId() != null)
        {
            template = mergeTemplate(template);
        }

        // 设置创建或修改时间
        if (template.getCreateDate() == null)
        {
            template.setCreateDate(new Date());
        }
        else
        {
            template.setModifyDate(new Date());
        }

        // 对Permission进行存盘前处理
        permissionM.beforeSave(template);
        // 对RTable进行存盘前处理
        tableM.beforeSave(template);
        // 对TableRule进行存盘前处理
        tableRuleM.beforeSave(template);

        return db.save(template);
    }

    /*
     * 合并模板属性
     */
    private Template mergeTemplate(Template template)
    {
        // 如果是权限改变，则不需要合并，直接返回
        if (template.isPermissionChanged())
        {
            return template;
        }

        Template oldTemplate = getTemplateByID(template.getId());
        template.setPermissions(oldTemplate.getPermissions());
        return template;
    }

    /**
     * 根据模板名称得到模板对象
     * 
     * @param templateName 模板名称
     * @return Template 模板对象
     */
    public Template getTemplateByName(String templateName)
    {
        return db.getTemplateByName(templateName);
    }

    /**
     * 根据模板ID得到模板对象
     * 
     * @param templateID 模板ID
     * @return Template 模板对象
     */
    public Template getTemplateByID(long templateID)
    {
        return db.getTemplateByID(templateID);
    }

    /**
     * 删除模板对象
     * 
     * @param templateID 模板id
     * @return Result 结果集
     */
    public Result deleteTemplate(long userID, long templateID)
    {
        return deleteTemplate(userID, new long[]{templateID});
    }

    /**
     * 删除模板对象
     * 
     * @param userID 用户id
     * @param templateIDs 模板id数组
     * @return Result 结果集
     */
    public Result deleteTemplate(long userID, long[] templateIDs)
    {
        if (templateIDs == null || templateIDs.length < 1)
        {
            return ErrorManager.getErrorResult("没有可删除的模板");
        }

        // 检查是否可删除
        Template template;
        boolean hasPermission = false;
        ArrayList<Permission> permissions;
        ArrayList<Template> templates = new ArrayList<Template>();
        for (long templateID : templateIDs)
        {
            template = getTemplateByID(templateID);
            templates.add(template);
            if (!template.getCreatorId().equals(userID))
            {
                permissions = permissionM
                    .getPermission(PermissionCons.TEMPLATE, templateID, userID);
                if (permissions == null || permissions.isEmpty())
                {
                    return ErrorManager.getErrorResult("没有删除权限");
                }
                // 遍历权限，只要有一个允许权限，则认为是有权限
                for (Permission permission : permissions)
                {
                    if (permission.canDesign())
                    {
                        hasPermission = true;
                        break;
                    }
                }
                if (hasPermission)
                {
                    return ErrorManager.getErrorResult("没有删除权限");
                }
            }

            if (template.getUseState(null))
            {
                return ErrorManager.getErrorResult("请先停用模板再删除");
            }

            if (isDesigning(templateID))
            {
                return ErrorManager.getErrorResult("当前模板正在被设计，不能删除");
            }

            if (isReporting(templateID))
            {
                return ErrorManager.getErrorResult("当前模板正在被填报，不能删除");
            }
        }

        // 只有全部检查通过了，再执行删除操作
        for (int i = 0, size = templates.size(); i < size; i++)
        {
            template = templates.get(i);
            // 从模板库中删除
            lib.deletePath(template.getPath() + "/" + template.getName());
            // 从数据库中删除
            db.delete(template);
        }
        return new Result();
    }

    /**
     * 根据权限类型得到当前路径下的模板
     * 
     * @param userID 用户ID
     * @param path 路径
     * @param permissionType 权限类型
     * @param recursive 是否递归得到所有子目录中的模板
     * @param isEditor 是否是编辑器内
     * @return ArrayList<Template> 模板列表
     */
    public ArrayList<Template> getTemplateList(long userID, String path, int permissionType,
        boolean recursive, boolean isEditor)
    {
        // 外部数据源
        if (TemplateSortResource.EXTERNALPATH.equals(path))
        {
            return null;
        }

        // 先得到所有列表
        ArrayList<Template> templates = lib.getTemplateList(path, recursive);
        if (templates == null)
        {
            return null;
        }

        // 遍历所有模板进行权限判断
        ArrayList<Permission> permissionList;
        ArrayList<Template> finallyTemplates = new ArrayList<Template>();
        for (Template template : templates)
        {
            if (!userM.isReportDesigner(userID) && !template.getUseState(null))
            {
                continue;
            }

            if (isEditor)
            {
                if (permissionType == PermissionCons.CANDESIGN && template.getUseState(null))
                {
                    // 如果是设计模板并且当前模板是启用状态，则不返回
                    continue;
                }
                else if (permissionType != PermissionCons.CANDESIGN && !template.getUseState(null))
                {
                    // 如果是非设计模板并且当前模板是停用状态，则不返回
                    continue;
                }
            }

            // 如果是作者本人，则不用进行权限判断
            if (template.getCreatorId() == userID)
            {
                finallyTemplates.add(template.clone(true));
                continue;
            }
            permissionList = permissionM.getPermission(PermissionCons.TEMPLATE, template.getId(),
                userID);
            if (permissionList != null && permissionList.size() > 0)
            {
                for (Permission permission : permissionList)
                {
                    // 如果有权限，则添加到返回列表
                    if (ReportTools.isLongFlag(permission.getPermission(), permissionType))
                    {
                        finallyTemplates.add(template.clone(true));
                        break;
                    }
                }
            }
        }

        return finallyTemplates;
    }

    /**
     * 根据权限集得到当前路径下的模板
     * 
     * @param userID 用户ID
     * @param path 路径
     * @param permission 权限集
     * @param recursive 是否递归得到所有子目录中的模板
     * @param isEditor 是否是编辑器内
     * @return ArrayList<Template> 模板列表
     */
    public ArrayList<Template> getTemplateList(long userID, String path, long permission,
        boolean recursive, boolean isEditor)
    {
        // 外部数据源
        if (TemplateSortResource.EXTERNALPATH.equals(path))
        {
            return null;
        }

        // 先得到所有列表
        ArrayList<Template> templates = lib.getTemplateList(path, recursive);
        if (templates == null)
        {
            return null;
        }

        // 遍历所有模板进行权限判断
        ArrayList<Permission> permissionList;
        boolean canDesign = ReportTools.isLongFlag(permission, PermissionCons.CANDESIGN);
        ArrayList<Template> finallyTemplates = new ArrayList<Template>();
        for (Template template : templates)
        {
            if (!userM.isReportDesigner(userID) && !template.getUseState(null))
            {
                continue;
            }

            if (isEditor)
            {
                if (canDesign && template.getUseState(null))
                {
                    // 如果是设计模板并且当前模板是启用状态，则不返回
                    continue;
                }
                else if (!canDesign && !template.getUseState(null))
                {
                    // 如果是非设计模板并且当前模板是停用状态，则不返回
                    continue;
                }
            }

            // 如果是作者本人，则不用进行权限判断
            if (template.getCreatorId() == userID)
            {
                finallyTemplates.add(template.clone(true));
                continue;
            }
            permissionList = permissionM.getPermission(PermissionCons.TEMPLATE, template.getId(),
                userID);
            if (permissionList != null && permissionList.size() > 0)
            {
                for (Permission tempPermission : permissionList)
                {
                    // 如果有权限，则添加到返回列表
                    if ((permission & tempPermission.getPermission()) != 0)
                    {
                        finallyTemplates.add(template.clone(true));
                        break;
                    }
                }
            }
        }

        return finallyTemplates;
    }

    /**
     * 添加模板分类
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param nodeName 节点名称
     * @return Result 结果集
     */
    public Result createTemplateSort(long userID, final String nodePath, final String nodeName)
    {
        lib.createTemplateSort(userID, nodePath, nodeName);
        return new Result();
    }

    /**
     * 重命名模板分类
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param nodeName 节点名称
     * @return Result 结果集
     */
    public Result renameTemplateSort(long userID, final String nodePath, final String nodeName)
    {
        lib.renameTemplateSort(userID, nodePath, nodeName);
        return new Result();
    }

    /**
     * 删除模板分类
     * 
     * @param userID 用户ID
     * @param nodeName 节点名称
     * @return Result 结果集
     */
    public Result deleteTemplateSort(long userID, final String nodePath)
    {
        lib.deletePath(nodePath);
        return new Result();
    }

    /**
     * 得到指定路径下的模板分类
     * 
     * @param path 路径
     * @param externalOtherDataSource 是否包含外部数据源
     * @return ArrayList<TemplateSort> 指定路径下的模板分类
     */
    public ArrayList<TemplateSort> getTemplateSort(String path, boolean externalOtherDataSource)
    {
        // 如果是外部数据源分类，则直接返回
        if (path.equals(TemplateSortResource.EXTERNALPATH))
        {
            return null;
        }

        ArrayList<TemplateSort> list = lib.getTemplateSort(path);
        if (externalOtherDataSource && path.equals(TemplateSortResource.TEMPLATEPATH))
        {
            list.add(lib.getExternalTemplateSort());
        }
        return list;
    }

    /**
     * 得到模板的使用状态(停用或启用)
     * 
     * @param templateID 模板ID
     * @return boolean 模板的使用状态(停用或启用)
     */
    public boolean getTemplateUseState(long templateID)
    {
        Template template = getTemplateByID(templateID);
        return template.getUseState(null);
    }

    /**
     * 设置模板的使用状态(停用或启用)
     * 
     * @param templateIDs 模板ID数组
     * @param useStates 使用状态数组
     * @return Result 结果集
     */
    public Result saveTemplateUseState(long[] templateIDs, boolean[] useStates)
    {
        ArrayList<Template> templates = new ArrayList<Template>();
        if (templateIDs != null && templateIDs.length > 0)
        {
            Result result;
            Template template;
            for (long templateID : templateIDs)
            {
                template = getTemplateByID(templateID);
                templates.add(template);
                result = canStart(template);
                if (result != null)
                {
                    return result;
                }
            }

            for (int i = 0, size = templates.size(); i < size; i++)
            {
                setTemplateUseState(templates.get(i), useStates[i], false);
            }
        }

        return new Result();
    }

    /**
     * 设置模板的使用状态(停用或启用)
     * 
     * @param templateID 模板ID
     * @param useState true：启用；false：停用
     * @return Result 设置是否成功及错误信息
     */
    public Result saveTemplateUseState(long templateID, boolean useState)
    {
        Template template = getTemplateByID(templateID);

        return setTemplateUseState(template, useState, true);
    }

    /*
     * 设置模板的使用状态
     */
    private Result setTemplateUseState(Template template, boolean useState, boolean check)
    {
        // 如果模板不是纯查询报表且模板内有未创建的表，则不允许启动
        if (check && useState)
        {
            Result result = canStart(template);
            if (result != null)
            {
                return result;
            }
        }

        template.setUseState(useState);
        db.setAttrFlag(template.getId(), template.getAttrFlag());

        return null;
    }

    /*
     * 判断模板是否可以启动
     */
    private Result canStart(Template template)
    {
        //        if (!template.isPureQuery())
        //        {
        //            Set<RTable> rtables = template.getRtables();
        //            for (RTable rtable : rtables)
        //            {
        //                if (rtable.getDtable().getCreateState() != TableCons.HASCREATED)
        //                {
        //                    return ErrorManager.getErrorResult("有未创建的表");
        //                }
        //            }
        //        }
        return null;
    }

    /**
     * 得到模板是否正在被填报
     * 
     * @param templateID 模板ID
     * @return boolean 模板是否正在被填报
     */
    public boolean isReporting(long templateID)
    {
        // TODO
        return false;
    }

    /**
     * 得到模板是否正在被设计
     * 
     * @param templateID 模板ID
     * @return boolean 模板是否正在被设计
     */
    public boolean isDesigning(long templateID)
    {
        // TODO
        return false;
    }

    /**
     * @param db 设置 db
     */
    public void setDb(ITemplateDB db)
    {
        this.db = db;
        setBasedb(db);
    }

    /**
     * @param lib 设置 lib
     */
    public void setLib(ITemplateLib lib)
    {
        this.lib = lib;
    }

    /**
     * @param userM 设置 userM
     */
    public void setUserM(IUserManager userM)
    {
        this.userM = userM;
    }

    /**
     * @param tableM 设置 tableM
     */
    public void setTableM(TableManager tableM)
    {
        this.tableM = tableM;
    }

    /**
     * @param tableRuleM 设置 tableRuleM
     */
    public void setTableRuleM(TableRuleManager tableRuleM)
    {
        this.tableRuleM = tableRuleM;
    }

    /**
     * @param permissionM 设置 permissionM
     */
    public void setPermissionM(PermissionManager permissionM)
    {
        this.permissionM = permissionM;
    }
}