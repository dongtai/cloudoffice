package apps.moreoffice.report.server.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import apps.moreoffice.report.client.constants.ReportTreeCons;
import apps.moreoffice.report.commons.ReportTools;
import apps.moreoffice.report.commons.domain.HashMapTools;
import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.constants.PermissionCons;
import apps.moreoffice.report.commons.domain.databaseObject.AutoNumRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataTable;
import apps.moreoffice.report.commons.domain.databaseObject.DataType;
import apps.moreoffice.report.commons.domain.databaseObject.DownListRule;
import apps.moreoffice.report.commons.domain.databaseObject.ListSelectRule;
import apps.moreoffice.report.commons.domain.databaseObject.OrgUser;
import apps.moreoffice.report.commons.domain.databaseObject.Permission;
import apps.moreoffice.report.commons.domain.databaseObject.RTable;
import apps.moreoffice.report.commons.domain.databaseObject.Record;
import apps.moreoffice.report.commons.domain.databaseObject.SysVarRule;
import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.commons.domain.databaseObject.TreeSelectRule;
import apps.moreoffice.report.commons.domain.databaseObject.User;
import apps.moreoffice.report.commons.domain.info.NodeInfo;
import apps.moreoffice.report.commons.domain.info.Organization;
import apps.moreoffice.report.commons.domain.info.TemplateInfo;
import apps.moreoffice.report.commons.domain.info.TemplateSort;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;
import apps.moreoffice.report.commons.formula.FormulaUtil;
import apps.moreoffice.report.server.resource.TemplateSortResource;
import apps.moreoffice.report.server.service.manager.DataRuleManager;
import apps.moreoffice.report.server.service.manager.DataTypeManager;
import apps.moreoffice.report.server.service.manager.IUserManager;
import apps.moreoffice.report.server.service.manager.PermissionManager;
import apps.moreoffice.report.server.service.manager.RecordManager;
import apps.moreoffice.report.server.service.manager.TableManager;
import apps.moreoffice.report.server.service.manager.TableRuleManager;
import apps.moreoffice.report.server.service.manager.TemplateManager;
import apps.moreoffice.report.server.util.ErrorManager;

/**
 * 报表基本管理器
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
public class ReportManager extends HashMapTools
{
    // 用户管理器
    private IUserManager userM;
    // 模板管理器
    private TemplateManager templateM;
    // 记录管理器
    private RecordManager recordM;
    // 权限管理器
    private PermissionManager permissionM;
    // 表和字段管理器
    private TableManager tableM;
    // 数据类型管理器
    private DataTypeManager dataTypeM;
    // 数据规范管理器
    private DataRuleManager dataRuleM;
    // 表间规则管理器
    private TableRuleManager tableRuleM;

    /**
     * 得到用户对象
     * 
     * @param paramsMap json格式参数
     * @return Result 结果
     * @throws IOException 异常
     */
    public Result getUser(HashMap<String, Object> paramsMap) throws IOException
    {
        User user = null;
        // 登录名
        String loginName = getString(paramsMap, ParamCons.LOGINNAME);
        if (loginName != null && loginName.length() > 0)
        {
            user = userM.getUser(loginName);
        }

        // 用户id
        long userID = getLong(paramsMap, ParamCons.USERID);
        if (userID > 0)
        {
            user = userM.getUser(userID);
        }

        if (user != null)
        {
            Result result = new Result();
            if (isJson(paramsMap))
            {
                result.setData(user.getJsonObj());
            }
            else
            {
                result.setData(user);
            }
            return result;
        }

        return ErrorManager.getErrorResult();
    }

    /**
     * 模板对象存盘前的检查
     * 
     * @param paramsMap
     * @return Result 结果
     * @throws IOException 异常
     */
    public Result checkTemplate(HashMap<String, Object> paramsMap) throws IOException
    {
        String templateNum = getString(paramsMap, ParamCons.TEMPLATENUM);
        String templateName = getString(paramsMap, ParamCons.TEMPLATENAME);
        return templateM.checkTemplate(templateNum, templateName);
    }

    /**
     * 得到模板对象(完整的)
     * 
     * @param paramsMap json格式参数
     * @return Result 结果
     * @throws IOException 异常
     */
    public Result getTemplate(HashMap<String, Object> paramsMap) throws IOException
    {
        Template template = getTemplateByIdOrName(paramsMap);
        if (template == null)
        {
            return ErrorManager.getErrorResult();
        }

        // 对权限进行处理(权限因为可以在服务器设置，因此需要动态拿)
        template.setPermissions(null);

        Result result = new Result();
        if (isJson(paramsMap))
        {
            result.setData(template.getJsonObj());
        }
        else
        {
            result.setData(template.clone(false));
        }
        return result;
    }

    /*
     * 通过模板ID或名称得到模板对象(懒加载)
     */
    private Template getTemplateByIdOrName(HashMap<String, Object> paramsMap) throws IOException
    {
        Template template = null;

        // 通过模板ID得到模板对象
        long templateID = getLong(paramsMap, ParamCons.TEMPLATEID);
        if (templateID != -1)
        {
            template = templateM.getTemplateByID(templateID);
        }

        // 通过模板名得到模板对象
        String templateName = getString(paramsMap, ParamCons.TEMPLATENAME);
        if (templateName != null)
        {
            template = templateM.getTemplateByName(templateName);
        }

        return template;
    }

    /**
     * 删除模板对象
     * 
     * @param paramsMap json格式参数
     * @return Result 结果
     * @throws IOException 异常
     */
    public void deleteTemplate(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        long templateID = getLong(paramsMap, ParamCons.TEMPLATEID);
        templateM.deleteTemplate(userID, templateID);
    }

    /**
     * 添加模板分类
     * 
     * @param paramsMap json格式参数
     * @return Result 结果
     * @throws IOException 异常
     */
    public Result createTemplateSort(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        String nodePath = getPath(paramsMap);
        String nodeName = getString(paramsMap, ParamCons.NAME);
        return templateM.createTemplateSort(userID, nodePath, nodeName);
    }

    /**
     * 重命名模板分类
     * 
     * @param paramsMap json格式参数
     * @return Result 结果
     * @throws IOException 异常
     */
    public Result renameTemplateSort(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        String nodePath = getPath(paramsMap);
        String nodeName = getString(paramsMap, ParamCons.NAME);
        return templateM.renameTemplateSort(userID, nodePath, nodeName);
    }

    /**
     * 删除模板分类
     * 
     * @param paramsMap json格式参数
     * @return Result 结果
     * @throws IOException 异常
     */
    public Result deleteTemplateSort(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        String nodePath = getPath(paramsMap);
        return templateM.deleteTemplateSort(userID, nodePath);
    }

    /**
     * 得到节点分类
     * 
     * @param paramsMap json格式参数
     * @return Result 结果
     * @throws IOException 异常
     */
    public Result getNodeSort(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        String nodePath = getPath(paramsMap);
        ArrayList<NodeInfo> nodeInfos = new ArrayList<NodeInfo>();
        boolean isReportDesigner = userM.isReportDesigner(userID);
        // 根目录下的节点
        if ("".equals(nodePath))
        {
            if (isReportDesigner)
            {
                // 模板
                nodeInfos.add(new NodeInfo(ReportTreeCons.TEMPLATE_NODE,
                    TemplateSortResource.TEMPLATE, TemplateSortResource.TEMPLATEPATH, false));
                // 数据类型
                nodeInfos.add(new NodeInfo(ReportTreeCons.DATATYPE_NODE,
                    ReportCommonResource.DATATYPE, TemplateSortResource.DATATYPEPATH, true));
                // 数据规范
                nodeInfos.add(new NodeInfo(ReportTreeCons.DATARULE_NODE,
                    ReportCommonResource.DATARULE, TemplateSortResource.DATARULEPATH, false));
            }
            else
            {
                paramsMap.put(ParamCons.PATH, TemplateSortResource.TEMPLATEPATH);
                return getNodeSort(paramsMap);
            }
        }
        else if (nodePath.startsWith(TemplateSortResource.TEMPLATEPATH))
        {
            ArrayList<TemplateSort> templateSorts = templateM.getTemplateSort(nodePath, false);
            if (templateSorts != null && !templateSorts.isEmpty())
            {
                boolean isLeaf = true;
                for (TemplateSort templateSort : templateSorts)
                {
                    isLeaf = isLeaf(userID, templateSort.getPath(), isReportDesigner);
                    if (!isReportDesigner && isLeaf)
                    {
                        // 如果不是报表设计者，并且当前节点下没有任何有权限的模板，则不返回。
                        continue;
                    }
                    nodeInfos.add(new NodeInfo(ReportTreeCons.TEMPLATE_NODE,
                        templateSort.getName(), templateSort.getPath(), isLeaf));
                }
            }
            // 权限
            long permission = getDefaultPermission(isReportDesigner);
            ArrayList<Template> templates = templateM.getTemplateList(userID, nodePath, permission,
                false, false);
            if (templates != null && !templates.isEmpty())
            {
                for (Template template : templates)
                {
                    nodeInfos.add(new NodeInfo(ReportTreeCons.RECORD_NODE, template.getName(),
                        template.getPath() + "/" + template.getName(), true, template.getId()));
                }
            }
        }
        else if (nodePath.equals(TemplateSortResource.DATARULEPATH))
        {
            // 系统变量
            nodeInfos.add(new NodeInfo(ReportTreeCons.SYSTEMVAR_NODE, ReportCommonResource.SYSVAR,
                TemplateSortResource.SYSVARPATH, true));
            // 自动编号
            nodeInfos.add(new NodeInfo(ReportTreeCons.AUTONUM_NODE, ReportCommonResource.AUTONUM,
                TemplateSortResource.AUTONUMPATH, true));
            // 下拉列表
            nodeInfos.add(new NodeInfo(ReportTreeCons.DOWNLIST_NODE, ReportCommonResource.DOWNLIST,
                TemplateSortResource.DOWNLISTPATH, true));
            // 树型选择
            nodeInfos.add(new NodeInfo(ReportTreeCons.TREESELECT_NODE,
                ReportCommonResource.TREESELECT, TemplateSortResource.TREESELECTPATH, true));
            // 列表选择
            nodeInfos.add(new NodeInfo(ReportTreeCons.LISTSELECT_NODE,
                ReportCommonResource.LISTSELECT, TemplateSortResource.LISTSELECTPATH, true));
        }

        Result result = new Result();
        if (isJson(paramsMap) && nodeInfos.size() > 0)
        {
            ArrayList<HashMap<String, Object>> nodeInfoJ = new ArrayList<HashMap<String, Object>>();
            for (NodeInfo nodeInfo : nodeInfos)
            {
                nodeInfoJ.add(nodeInfo.getJsonObj());
            }
            result.setData(nodeInfoJ);
        }
        else
        {
            result.setNodeInfos(nodeInfos);
        }
        return result;
    }

    /*
     * 判断是否是叶子节点
     */
    private boolean isLeaf(long userID, String nodePath, boolean isReportDesigner)
    {
        ArrayList<TemplateSort> templateSorts = templateM.getTemplateSort(nodePath, false);
        if (templateSorts != null && !templateSorts.isEmpty())
        {
            return false;
        }

        long permission = getDefaultPermission(isReportDesigner);
        ArrayList<Template> templates = templateM.getTemplateList(userID, nodePath, permission,
            false, false);
        if (templates != null && !templates.isEmpty())
        {
            return false;
        }
        return true;
    }

    /*
     * 根据是否是报表设计者得到默认权限
     */
    private long getDefaultPermission(boolean isReportDesigner)
    {
        long permission = 0;
        permission = ReportTools.setLongFlag(permission, PermissionCons.CANFILL, true);
        permission = ReportTools.setLongFlag(permission, PermissionCons.CANREAD, true);
        if (isReportDesigner)
        {
            permission = ReportTools.setLongFlag(permission, PermissionCons.CANDESIGN, true);
        }
        return permission;
    }

    /**
     * 得到指定路径下的模板分类
     * 
     * @param paramsMap json格式参数
     * @return Result 指定路径下的模板分类
     * @throws IOException 异常
     */
    public Result getTemplateSort(HashMap<String, Object> paramsMap) throws IOException
    {
        String path = getPath(paramsMap);
        Boolean externalDataSource = getBoolean(paramsMap, ParamCons.EXTERNALDATASOURCE);
        ArrayList<TemplateSort> templateSorts = templateM.getTemplateSort(path,
            externalDataSource == null ? false : externalDataSource);
        Result result = new Result();
        if (isJson(paramsMap))
        {
            ArrayList<HashMap<String, Object>> templateSortJ = new ArrayList<HashMap<String, Object>>();
            for (TemplateSort templateSort : templateSorts)
            {
                if (templateSort != null)
                {
                    templateSortJ.add(templateSort.getJsonObj());
                }
            }
            result.setData(templateSortJ);
        }
        else
        {
            result.setData(templateSorts);
        }
        return result;
    }

    /**
     * 得到模板列表
     * 
     * @param paramsMap json格式参数
     * @return Result 模板列表
     * @throws IOException 异常
     */
    public Result getTemplateList(HashMap<String, Object> paramsMap) throws IOException
    {
        // 用户ID
        long userID = getLong(paramsMap, ParamCons.USERID);
        // 路径
        String path = getPath(paramsMap);
        // 权限类型
        int permissionType = getInt(paramsMap, ParamCons.PERMISSION);

        ArrayList<Template> templates = templateM.getTemplateList(userID, path, permissionType,
            true, true);
        Result result = new Result();
        if (isJson(paramsMap))
        {
            ArrayList<HashMap<String, Object>> templateJ = new ArrayList<HashMap<String, Object>>();
            if (templates != null && !templates.isEmpty())
            {
                for (Template template : templates)
                {
                    if (template != null)
                    {
                        templateJ.add(template.getJsonObj());
                    }
                }
                result.setData(templateJ);
            }
        }
        else
        {
            result.setData(templates);
        }
        return result;
    }

    /**
     * 得到模板信息列表(仅为显示模板列表提供)
     * 
     * @param paramsMap json格式参数
     * @return Result 模板信息列表
     * @throws IOException 异常
     */
    public Result getTemplateInfoList(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        String nodePath = getPath(paramsMap);
        boolean isReportDesigner = userM.isReportDesigner(userID);
        ArrayList<Template> templates = templateM.getTemplateList(userID, nodePath,
            getDefaultPermission(isReportDesigner), true, false);
        Result result = new Result();
        if (templates != null && !templates.isEmpty())
        {
            User user;
            TemplateInfo templateInfo;
            ArrayList<TemplateInfo> templateInfos = new ArrayList<TemplateInfo>();
            for (Template template : templates)
            {
                templateInfo = new TemplateInfo();
                templateInfo.setId(template.getId());
                templateInfo.setName(template.getName());
                templateInfo.setPath(template.getPath());
                templateInfo.setStatus(template.getUseState(null));
                user = userM.getUser(template.getCreatorId());
                if (user != null)
                {
                    templateInfo.setCreatorName(user.getUserName());
                }
                if (template.getModifierId() != null)
                {
                    user = userM.getUser(template.getModifierId());
                    if (user != null)
                    {
                        templateInfo.setModifierName(user.getUserName());
                    }
                    templateInfo.setLastModifyDate(FormulaUtil.convertDateToDateString(
                        template.getModifyDate(), "yyyy-MM-dd HH:mm:ss"));
                }
                templateInfos.add(templateInfo);
            }

            result.setData(templateInfos);
        }
        return result;
    }

    /**
     * 得到模板权限(模板ID或者模板名称)
     * 
     * @param paramsMap json格式参数
     * @return Result 权限集
     * @throws IOException 异常
     */
    public Result getTemplatePermission(HashMap<String, Object> paramsMap) throws IOException
    {
        // 模板ID
        long templateID = getTemplateByIdOrName(paramsMap).getId();

        Set<Permission> permissions = permissionM
            .getPermission(PermissionCons.TEMPLATE, templateID);
        if (permissions != null && !permissions.isEmpty())
        {
            Result result = new Result();
            if (isJson(paramsMap))
            {
                ArrayList<HashMap<String, Object>> permissionJ = new ArrayList<HashMap<String, Object>>();
                for (Permission permission : permissions)
                {
                    permissionJ.add(permission.getJsonObj());
                }
                result.setData(permissionJ);
            }
            else
            {
                HashSet<Permission> set = new HashSet<Permission>();
                for (Permission permission : permissions)
                {
                    set.add(permission.clone(false));
                }
                result.setData(set);
            }

            return result;
        }

        return null;
    }

    /**
     * 得到模板的使用状态(停用或启用)
     * 
     * @param paramsMap json格式参数
     * @return boolean 模板的使用状态(停用或启用)
     * @throws IOException 异常
     */
    public boolean getTemplateUseState(HashMap<String, Object> paramsMap) throws IOException
    {
        // 模板ID
        long templateID = getTemplateByIdOrName(paramsMap).getId();

        return templateM.getTemplateUseState(templateID);
    }

    /**
     * 设置模板的使用状态(停用或启用)
     * 
     * @param templateIDs 模板ID数组
     * @param useStates 使用状态数组
     * @return Result 结果集
     */
    @ SuppressWarnings("unchecked")
    public Result setTemplateUseState(HashMap<String, Object> paramsMap) throws IOException
    {
        long[] templateIDs = null;
        boolean[] useStates = null;
        Object value = paramsMap.get(ParamCons.TEMPLATEID);
        if (value instanceof ArrayList)
        {
            ArrayList<Long> templateIDList = (ArrayList<Long>)value;
            if (templateIDList.size() > 0)
            {
                templateIDs = new long[templateIDList.size()];
                for (int i = 0, size = templateIDList.size(); i < size; i++)
                {
                    templateIDs[i] = templateIDList.get(i);
                }
            }
        }
        value = paramsMap.get(ParamCons.USESTATE);
        if (value instanceof ArrayList)
        {
            ArrayList<Boolean> useStateList = (ArrayList<Boolean>)value;
            if (useStateList.size() > 0)
            {
                useStates = new boolean[useStateList.size()];
                for (int i = 0, size = useStateList.size(); i < size; i++)
                {
                    useStates[i] = useStateList.get(i);
                }
            }
        }
        if (templateIDs == null || useStates == null || templateIDs.length != useStates.length)
        {
            return ErrorManager.getErrorResult();
        }

        return templateM.saveTemplateUseState(templateIDs, useStates);
    }

    /**
     * 得到模板是否正在被填报
     * 
     * @param paramsMap json格式参数
     * @return boolean 模板是否正在被填报
     * @throws IOException 异常
     */
    public boolean isReporting(HashMap<String, Object> paramsMap) throws IOException
    {
        // 模板ID
        long templateID = getTemplateByIdOrName(paramsMap).getId();

        return templateM.isReporting(templateID);
    }

    /**
     * 得到模板是否正在被设计
     * 
     * @param paramsMap json格式参数
     * @return boolean 模板是否正在被设计
     * @throws IOException 异常
     */
    public boolean isDesigning(HashMap<String, Object> paramsMap) throws IOException
    {
        // 模板ID
        long templateID = getTemplateByIdOrName(paramsMap).getId();

        return templateM.isDesigning(templateID);
    }

    /**
     * 保存记录
     * 
     * @param paramsMap json格式参数
     * @return Result 数据规范
     * @throws IOException 异常
     */
    public Result saveRecord(HashMap<String, Object> paramsMap) throws IOException
    {
        return recordM.save(paramsMap);
    }

    /**
     * 根据记录ID得到模板对应的记录
     * 
     * @param paramsMap json格式参数
     * @return Result 记录对象
     * @throws IOException 异常
     */
    public Result getRecord(HashMap<String, Object> paramsMap) throws IOException
    {
        long templateID = getLong(paramsMap, ParamCons.TEMPLATEID);
        long recordID = getLong(paramsMap, ParamCons.RECORDID);

        Record record = recordM.getRecord(templateID, recordID);
        if (record != null)
        {
            Result result = new Result();
            if (isJson(paramsMap))
            {
                result.setData(record.getJsonObj());
            }
            else
            {
                result.setData(record.clone(false));
            }
            return result;
        }

        return ErrorManager.getErrorResult();
    }

    /**
     * 根据记录ID得到对应的相关记录ID(第一、前、后、最后记录ID)
     * 
     * @param paramsMap json格式参数
     * @return long[] 长度为4的long数组
     * @throws IOException 异常
     */
    public long[] getRecordID(HashMap<String, Object> paramsMap) throws IOException
    {
        long templateID = getLong(paramsMap, ParamCons.TEMPLATEID);
        long recordID = getLong(paramsMap, ParamCons.RECORDID);
        return recordM.getRecordID(templateID, recordID);
    }

    /**
     * 删除记录
     * 
     * @param paramsMap json格式参数
     * @return boolean 是否删除成功
     * @throws IOException IOException 异常
     */
    public boolean deleteRecord(HashMap<String, Object> paramsMap) throws IOException
    {
        long templateID = getLong(paramsMap, ParamCons.TEMPLATEID);
        long recordID = getLong(paramsMap, ParamCons.RECORDID);
        return recordM.deleteRecord(templateID, recordID);
    }

    /**
     * 上锁/解锁记录
     * 
     * @param paramsMap json格式参数
     * @return boolean 是否删除成功
     * @throws IOException IOException 异常
     */
    @ SuppressWarnings("unchecked")
    public Result lockRecord(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        long templateID = getLong(paramsMap, ParamCons.TEMPLATEID);
        long[] recordIDs = null;
        boolean[] lockStatus = null;
        Object value = paramsMap.get(ParamCons.RECORDID);
        if (value instanceof ArrayList)
        {
            ArrayList<Long> recordIDList = (ArrayList<Long>)value;
            if (recordIDList.size() > 0)
            {
                recordIDs = new long[recordIDList.size()];
                for (int i = 0, size = recordIDList.size(); i < size; i++)
                {
                    recordIDs[i] = recordIDList.get(i);
                }
            }
        }
        value = paramsMap.get(ParamCons.LOCKSTATE);
        if (value instanceof ArrayList)
        {
            ArrayList<Boolean> lockStateList = (ArrayList<Boolean>)value;
            if (lockStateList.size() > 0)
            {
                lockStatus = new boolean[lockStateList.size()];
                for (int i = 0, size = lockStateList.size(); i < size; i++)
                {
                    lockStatus[i] = lockStateList.get(i);
                }
            }
        }
        if (recordIDs == null || lockStatus == null || recordIDs.length != lockStatus.length)
        {
            return ErrorManager.getErrorResult();
        }

        return recordM.lockRecord(userID, templateID, recordIDs, lockStatus);
    }

    /**
     * 得到单一记录
     * 
     * @param paramsMap json格式参数
     * @return Result 单一记录
     * @throws IOException IOException 异常
     */
    public Result getSingleData(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        String nodePath = getPath(paramsMap);
        int start = getInt(paramsMap, ParamCons.START);
        int number = getInt(paramsMap, ParamCons.NUMBER);
        return recordM.getSingleData(userID, nodePath, start, number);
    }

    /**
     * 得到重复记录
     * 
     * @param paramsMap json格式参数
     * @return Result 重复记录记录
     * @throws IOException IOException 异常
     */
    public Result getRepeatData(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        String nodePath = getPath(paramsMap);
        long recordID = getLong(paramsMap, ParamCons.RECORDID);
        int start = getInt(paramsMap, ParamCons.START);
        int number = getInt(paramsMap, ParamCons.NUMBER);
        return recordM.getRepeatData(userID, nodePath, recordID, start, number);
    }

    /**
     * 是否是报表设计者
     * 
     * @param paramsMap json格式参数
     * @return boolean 是否是报表设计者
     * @throws IOException 异常
     */
    public boolean isReportDesigner(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        return userM.isReportDesigner(userID);
    }

    /**
     * 得到组织架构
     * 
     * @param paramsMap json格式参数
     * @return Result 根节点
     * @throws IOException 异常
     */
    public Result getOrganization(HashMap<String, Object> paramsMap) throws IOException
    {
        // 用户ID
        long userID = getLong(paramsMap, ParamCons.USERID);
        Organization organization = userM.getOrganization(userID);
        if (organization != null)
        {
            Result result = new Result();
            if (isJson(paramsMap))
            {
                result.setData(organization.getJsonObj());
            }
            else
            {
                result.setData(organization);
            }
            return result;
        }
        return ErrorManager.getErrorResult();
    }

    /**
     * 得到某个组织下的组织用户对象
     * 
     * @param paramsMap json格式参数
     * @return Result 结果
     * @throws IOException 异常
     */
    public Result getOrgUserByOrg(HashMap<String, Object> paramsMap) throws IOException
    {
        // 组织ID
        long orgID = getLong(paramsMap, ParamCons.ORGID);
        ArrayList<OrgUser> orgUsers = userM.getOrgUserByOrg(orgID);
        if (orgUsers != null && !orgUsers.isEmpty())
        {
            Result result = new Result();
            if (isJson(paramsMap))
            {
                ArrayList<HashMap<String, Object>> orgUserJ = new ArrayList<HashMap<String, Object>>();
                for (OrgUser orgUser : orgUsers)
                {
                    orgUserJ.add(orgUser.getJsonObj());
                }
                result.setData(orgUserJ);
            }
            else
            {
                result.setData(orgUsers);
            }

            return result;
        }
        return null;
    }

    /**
     * 得到用户所在的组织
     * 
     * @param paramsMap json格式参数
     * @return Result 组织集合
     * @throws IOException 异常
     */
    public Result getOrgByUser(HashMap<String, Object> paramsMap) throws IOException
    {
        // 用户ID
        long userID = getLong(paramsMap, ParamCons.USERID);
        ArrayList<OrgUser> orgUsers = userM.getOrgUserByUser(userID);
        if (orgUsers != null && !orgUsers.isEmpty())
        {
            Result result = new Result();
            if (isJson(paramsMap))
            {
                ArrayList<HashMap<String, Object>> orgUserJ = new ArrayList<HashMap<String, Object>>();
                for (OrgUser orgUser : orgUsers)
                {
                    orgUserJ.add(orgUser.getJsonObj());
                }
                result.setData(orgUserJ);
            }
            else
            {
                result.setData(orgUsers);
            }

            return result;
        }
        return null;
    }

    /**
     * 得到设计者列表
     * 
     * @param paramsMap json格式参数
     * @return Result 设计者列表
     * @throws IOException 异常
     */
    public Result getDesignerList(HashMap<String, Object> paramsMap) throws IOException
    {
        // 用户ID
        long userID = getLong(paramsMap, ParamCons.USERID);
        ArrayList<User> users = userM.getDesignerList(userID);
        if (users != null && !users.isEmpty())
        {
            Result result = new Result();
            if (isJson(paramsMap))
            {
                ArrayList<HashMap<String, Object>> userJ = new ArrayList<HashMap<String, Object>>();
                for (User user : users)
                {
                    userJ.add(user.getJsonObj());
                }
                result.setData(userJ);
            }
            else
            {
                result.setData(users);
            }
            return result;
        }
        return null;
    }

    /**
     * 得到具有权限的报表数据表列表(仅基本数据)
     * 
     * @param paramsMap json格式参数
     * @return Result RTable集合
     * @throws IOException 异常
     */
    public Result getRTableList(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        // 路径类型(分类or模板)
        int pathType = getInt(paramsMap, ParamCons.PATHTYPE);
        // 名称(分类或模板名称)
        String path = getPath(paramsMap);
        // 表类型(单一、重复、所有)
        int tableType = getInt(paramsMap, ParamCons.TABLETYPE);
        // 表操作类型(提数、回写)
        int tableOperateType = getInt(paramsMap, ParamCons.TABLEOPERATETYPE);
        // 记录操作类型(新建、修改、删除)
        int dataOperateType = getInt(paramsMap, ParamCons.DATAOPERATETYPE);

        ArrayList<RTable> rtables = tableM.getRTableList(userID, pathType, path, tableType,
            tableOperateType, dataOperateType);
        if (rtables != null && !rtables.isEmpty())
        {
            Result result = new Result();
            if (isJson(paramsMap))
            {
                ArrayList<HashMap<String, Object>> rtableJ = new ArrayList<HashMap<String, Object>>();
                for (RTable rtable : rtables)
                {
                    rtableJ.add(rtable.getJsonObj());
                }
                result.setData(rtableJ);
            }
            else
            {
                for (int i = 0, size = rtables.size(); i < size; i++)
                {
                    rtables.set(i, rtables.get(i).clone(true));
                }
                result.setData(rtables);
            }
            return result;
        }
        return null;
    }

    /**
     * 得到具有权限的已创建的用户数据表
     * 
     * @param paramsMap json格式参数
     * @return Result DataTable集合
     * @throws IOException 异常
     */
    public Result getDataTableList(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        // 表操作类型(添加、映射)
        int tableOperateType = ((Number)paramsMap.get(ParamCons.TABLEOPERATETYPE)).intValue();

        ArrayList<DataTable> dtables = tableM.getDataTableList(userID, tableOperateType);
        if (dtables != null && !dtables.isEmpty())
        {
            Result result = new Result();
            if (isJson(paramsMap))
            {
                ArrayList<HashMap<String, Object>> dtableJ = new ArrayList<HashMap<String, Object>>();
                for (DataTable dtable : dtables)
                {
                    dtableJ.add(dtable.getJsonObj());
                }
                result.setData(dtableJ);
            }
            else
            {
                for (int i = 0, size = dtables.size(); i < size; i++)
                {
                    dtables.set(i, dtables.get(i).clone(true));
                }
                result.setData(dtables);
            }
            return result;
        }

        return null;
    }

    /**
     * 检查表名是否合法
     * 
     * @param paramsMap json格式参数
     * @return boolean 是否合法
     * @throws IOException 异常
     */
    public boolean checkTableName(HashMap<String, Object> paramsMap) throws IOException
    {
        String tableName = getString(paramsMap, ParamCons.TABLENAME);

        return tableM.checkTableName(tableName);
    }

    /**
     * 保存数据类型
     * 
     * @param paramsMap json格式参数
     * @return Result 数据规范
     * @throws IOException 异常
     */
    public Result saveDataType(HashMap<String, Object> paramsMap) throws IOException
    {
        long dataTypeID = getLong(paramsMap, ParamCons.ID);
        DataType dataType = new DataType();
        if (dataTypeID > 0)
        {
            dataType = dataTypeM.getDataTypeByID(dataTypeID);
        }
        dataType.convetJsonToObj(paramsMap);
        dataTypeM.save(dataType);
        Result result = new Result();
        result.setData(dataType.getId());
        return result;
    }

    /**
     * 通过数据类型ID删除数据类型
     * 
     * @param paramsMap json格式参数
     * @return Result 结果集
     * @throws IOException 异常
     */
    public Result deleteDataType(HashMap<String, Object> paramsMap) throws IOException
    {
        long dataTypeID = getLong(paramsMap, ParamCons.ID);
        dataTypeM.deleteDataTypeByID(dataTypeID);
        Result result = new Result();
        return result;
    }

    /**
     * 得到数据类型列表
     * 
     * @param paramsMap json格式参数
     * @return Result DataType集合
     * @throws IOException 异常
     */
    public Result getDataTypeList(HashMap<String, Object> paramsMap) throws IOException
    {
        ArrayList<DataType> dataTypes = dataTypeM.getDataTypeList();
        Result result = new Result();
        if (isJson(paramsMap))
        {
            ArrayList<HashMap<String, Object>> dataTypeJ = new ArrayList<HashMap<String, Object>>();
            if (dataTypes != null && !dataTypes.isEmpty())
            {
                for (DataType dataType : dataTypes)
                {
                    if (dataType != null)
                    {
                        dataTypeJ.add(dataType.getJsonObj());
                    }
                }
                result.setData(dataTypeJ);
            }
        }
        else
        {
            result.setData(dataTypes);
        }
        return result;
    }

    /**
     * 通过数据类型ID得到数据类型
     * 
     * @param paramsMap json格式参数
     * @return Result 数据类型
     * @throws IOException 异常
     */
    public Result getDataTypeByID(HashMap<String, Object> paramsMap) throws IOException
    {
        long dataTypeID = getLong(paramsMap, ParamCons.DATATYPEID);
        DataType dataType = dataTypeM.getDataTypeByID(dataTypeID);
        Result result = new Result();
        if (isJson(paramsMap) && dataType != null)
        {
            result.setData(dataType.getJsonObj());
        }
        else
        {
            result.setData(dataType);
        }
        return result;
    }

    /**
     * 保存数据规范
     * 
     * @param paramsMap json格式参数
     * @return Result 数据规范
     * @throws IOException 异常
     */
    public Result saveDataRule(HashMap<String, Object> paramsMap) throws IOException
    {
        long dataRuleID = getLong(paramsMap, ParamCons.ID);
        DataRule dataRule = null;
        if (dataRuleID > 0)
        {
            dataRule = dataRuleM.getDataRuleByID(dataRuleID);
        }
        else
        {
            int type = HashMapTools.getInt(paramsMap, ParamCons.TYPE);
            switch (type)
            {
                case DataRuleCons.SYSVAR:
                    dataRule = new SysVarRule();
                    break;
                case DataRuleCons.AUTONUM:
                    dataRule = new AutoNumRule();
                    break;
                case DataRuleCons.DOWNLIST:
                    dataRule = new DownListRule();
                    break;
                case DataRuleCons.TREESELECT:
                    dataRule = new TreeSelectRule();
                    break;
                case DataRuleCons.LISTSELECT:
                    dataRule = new ListSelectRule();
                    break;
                default:
                    break;
            }
        }

        if (dataRule != null)
        {
            dataRule.convetJsonToObj(paramsMap);
            dataRuleM.save(dataRule);
            Result result = new Result();
            result.setData(dataRule.getId());
            return result;
        }
        else
        {
            return ErrorManager.getErrorResult();
        }
    }

    /**
     * 通过数据规范ID删除数据规范
     * 
     * @param paramsMap json格式参数
     * @return Result 结果集
     * @throws IOException 异常
     */
    public Result deleteDataRule(HashMap<String, Object> paramsMap) throws IOException
    {
        long dataRuleID = getLong(paramsMap, ParamCons.ID);
        dataRuleM.deleteDataRuleByID(dataRuleID);
        Result result = new Result();
        return result;
    }

    /**
     * 得到数据规范列表
     * 
     * @param paramsMap json格式参数
     * @return Result DataRule集合
     * @throws IOException 异常
     */
    @ SuppressWarnings("unchecked")
    public Result getDataRuleList(HashMap<String, Object> paramsMap) throws IOException
    {
        short type = getShort(paramsMap, ParamCons.DATARULETYPE);
        List<DataRule> dataRuleList = (List<DataRule>)dataRuleM.getDataRuleList(type);
        if (dataRuleList != null && !dataRuleList.isEmpty())
        {
            Result result = new Result();
            if (isJson(paramsMap))
            {
                ArrayList<HashMap<String, Object>> dataRuleJ = new ArrayList<HashMap<String, Object>>();
                for (DataRule dataRule : dataRuleList)
                {
                    if (dataRule != null)
                    {
                        dataRuleJ.add(dataRule.getJsonObj());
                    }
                }
                result.setData(dataRuleJ);
            }
            else
            {
                ArrayList<DataRule> dataRules = new ArrayList<DataRule>();
                for (DataRule dataRule : dataRuleList)
                {
                    if (dataRule != null)
                    {
                        dataRules.add(dataRule.clone(true));
                    }
                }
                result.setData(dataRules);
            }

            return result;
        }

        return ErrorManager.getErrorResult();
    }

    /**
     * 通过数据规范ID得到数据规范
     * 
     * @param paramsMap json格式参数
     * @return Result 数据规范
     * @throws IOException 异常
     */
    public Result getDataRuleByID(HashMap<String, Object> paramsMap) throws IOException
    {
        long dataRuleID = getLong(paramsMap, ParamCons.DATARULEID);
        Result result = new Result();
        DataRule dataRule = dataRuleM.getDataRuleByID(dataRuleID).clone(false);
        if (isJson(paramsMap) && dataRule != null)
        {
            result.setData(dataRule.getJsonObj());
        }
        else
        {
            result.setData(dataRule);
        }
        return result;
    }

    /**
     * 得到数据规范的值
     * 
     * @param paramsMap json格式参数
     * @return Serializable 数据规范的值
     * @throws IOException 异常
     */
    public Serializable getDataRuleData(HashMap<String, Object> paramsMap) throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        long dataRuleID = getLong(paramsMap, ParamCons.DATARULEID);
        return dataRuleM.getDataRuleData(userID, dataRuleID);
    }

    /**
     * 执行表间规则
     * 
     * @param paramsMap json格式参数
     * @return Vector<Vector<Object>> 结果
     * @throws IOException 异常
     */
    @ SuppressWarnings({"rawtypes", "unchecked"})
    public Vector<Vector<Object>> execTableRules(HashMap<String, Object> paramsMap)
        throws IOException
    {
        long userID = getLong(paramsMap, ParamCons.USERID);
        ArrayList tableRuleIDs = (ArrayList)paramsMap.get(ParamCons.TABLERULEIDS);
        HashMap<String, Object> currentData = (HashMap<String, Object>)paramsMap
            .get(ParamCons.CURRENTDATA);
        return tableRuleM.execTableRules(userID, tableRuleIDs, currentData);
    }

    /*
     * 是否是json请求
     */
    private boolean isJson(HashMap<String, Object> paramsMap)
    {
        if (paramsMap != null && paramsMap.get(ParamCons.ISJSON) != null)
        {
            return true;
        }
        return false;
    }

    /*
     * 得到路径
     */
    private String getPath(HashMap<String, Object> paramsMap)
    {
        String path = getString(paramsMap, ParamCons.PATH);
        return path == null ? "" : path;
    }

    // ----------配置管理器----------
    /**
     * @param userM 设置 userM
     */
    public void setUserM(IUserManager userM)
    {
        this.userM = userM;
    }

    /**
     * @param templateM 设置 templateM
     */
    public void setTemplateM(TemplateManager templateM)
    {
        this.templateM = templateM;
    }

    /**
     * @param recordM 设置 recordM
     */
    public void setRecordM(RecordManager recordM)
    {
        this.recordM = recordM;
    }

    /**
     * @param permissionM 设置 permissionM
     */
    public void setPermissionM(PermissionManager permissionM)
    {
        this.permissionM = permissionM;
    }

    /**
     * @param tableM 设置 tableM
     */
    public void setTableM(TableManager tableM)
    {
        this.tableM = tableM;
    }

    /**
     * @param dataTypeM 设置 dataTypeM
     */
    public void setDataTypeM(DataTypeManager dataTypeM)
    {
        this.dataTypeM = dataTypeM;
    }

    /**
     * @param dataRuleM 设置 dataRuleM
     */
    public void setDataRuleM(DataRuleManager dataRuleM)
    {
        this.dataRuleM = dataRuleM;
    }

    /**
     * @param tableRuleM 设置 tableRuleM
     */
    public void setTableRuleM(TableRuleManager tableRuleM)
    {
        this.tableRuleM = tableRuleM;
    }
}