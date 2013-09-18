package apps.moreoffice.report.server.servlet.gwt;

import java.util.ArrayList;
import java.util.HashMap;

import apps.moreoffice.report.client.constants.ReportTreeCons;
import apps.moreoffice.report.client.remote.ReportRemote;
import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.constants.ErrorCodeCons;
import apps.moreoffice.report.commons.domain.constants.ParamCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.DataRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataTable;
import apps.moreoffice.report.commons.domain.databaseObject.DataType;
import apps.moreoffice.report.server.service.ReportEntityManager;
import apps.moreoffice.report.server.service.ReportManager;
import apps.moreoffice.report.server.service.manager.DataRuleManager;
import apps.moreoffice.report.server.service.manager.DataTypeManager;
import apps.moreoffice.report.server.service.manager.IUserManager;
import apps.moreoffice.report.server.service.manager.PermissionManager;
import apps.moreoffice.report.server.service.manager.RecordManager;
import apps.moreoffice.report.server.service.manager.TableManager;
import apps.moreoffice.report.server.service.manager.TemplateManager;
import apps.moreoffice.report.server.service.manager.dataCenter.ITemplateLib;
import apps.moreoffice.report.server.servlet.context.ReportApplicationContext;
import apps.moreoffice.report.server.util.ErrorManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 提供给GWT前端的实现类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-13
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class ReportRemoteImpl extends RemoteServiceServlet implements ReportRemote
{
    // 模板管理器
    TemplateManager templateM = (TemplateManager)ReportApplicationContext.getInstance().getBean(
        "ReportTemplateManager");
    // 模板库管理器
    ITemplateLib lib = (ITemplateLib)ReportApplicationContext.getInstance().getBean(
        "ReportTemplateLib");
    // 实体管理器
    ReportEntityManager entityM = (ReportEntityManager)ReportApplicationContext.getInstance()
        .getBean("ReportEntityManager");
    // 报表管理器
    ReportManager reportM = (ReportManager)ReportApplicationContext.getInstance().getBean(
        "ReportManager");
    // 数据类型管理器
    DataTypeManager dataTypeM = (DataTypeManager)ReportApplicationContext.getInstance().getBean(
        "ReportDataTypeManager");
    // 数据规范管理器
    DataRuleManager dataRuleM = (DataRuleManager)ReportApplicationContext.getInstance().getBean(
        "ReportDataRuleManager");
    // 用户管理器
    IUserManager userM = (IUserManager)ReportApplicationContext.getInstance().getBean(
        "ReportUserManager");
    // 权限管理器
    PermissionManager permissionM = (PermissionManager)ReportApplicationContext.getInstance()
        .getBean("ReportPermissionManager");
    // 记录管理器
    RecordManager recordM = (RecordManager)ReportApplicationContext.getInstance().getBean(
        "ReportRecordManager");
    // 表管理器
    TableManager tableM = (TableManager)ReportApplicationContext.getInstance().getBean(
        "ReportTableManager");

    /**
     * 是否是报表设计者
     * 
     * @param userID 用户ID
     * @return Result 结果集
     */
    public Result isReportDesigner(long userID)
    {
        Result result = new Result();
        result.setData(userM.isReportDesigner(userID));
        return result;
    }

    /**
     * 删除模板
     * 
     * @param userID 用户ID
     * @param templateIDs 模板ID数组
     * @param fileNames 模板名数组
     * @return Result 结果集
     */
    public Result deleteTemplate(long userID, final long[] templateIDs, final String[] fileNames)
    {
        return templateM.deleteTemplate(userID, templateIDs);
    }

    /**
     * 设置模板使用状态
     * 
     * @param templateIDs 模板ID数组
     * @param useStates 使用状态数组
     * @return Result 结果集
     */
    public Result setTemplateUseState(long[] templateIDs, boolean[] useStates)
    {
        return templateM.saveTemplateUseState(templateIDs, useStates);
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
        return templateM.createTemplateSort(userID, nodePath, nodeName);
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
        return templateM.renameTemplateSort(userID, nodePath, nodeName);
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
        return templateM.deleteTemplateSort(userID, nodePath);
    }

    /**
     * 保存或更新实体对象
     * 
     * @param entity 实体对象
     * @return Result 结果集
     */
    public Result saveOrUpdate(DataBaseObject entity)
    {
        Result result = new Result();
        result.setData(entityM.save(entity));
        return result;
    }

    /**
     * 删除实体对象
     * 
     * @param entity 实体对象
     * @return Result 结果集
     */
    public Result delete(DataBaseObject entity)
    {
        try
        {
            if (entity instanceof DataType)
            {
                dataTypeM.delete(entity);
            }
            else if (entity instanceof DataRule)
            {
                dataRuleM.delete(entity);
            }
        }
        catch(Exception e)
        {
            Result result = new Result();
            result.setErrorMessage(ErrorManager.getExceptionMessage(e));
            return result;
        }
        return null;
    }

    /**
     * 得到节点分类
     * 
     * @param userID 用户ID
     * @param nodePath 路径
     * @return Result 结果集
     */
    public Result getNodeSort(long userID, String nodePath)
    {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put(ParamCons.USERID, userID);
        paramsMap.put(ParamCons.PATH, nodePath);
        try
        {
            return reportM.getNodeSort(paramsMap);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    /**
     * 得到节点数据
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param nodeType 节点类型
     * @param start 开始位置
     * @param number 数量
     * @return Result 结果集
     */
    public Result getNodeData(long userID, String nodePath, int nodeType, int start, int number)
    {
        Result result = new Result();
        if (nodeType == ReportTreeCons.TEMPLATE_NODE)
        {
            HashMap<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put(ParamCons.USERID, userID);
            paramsMap.put(ParamCons.PATH, nodePath);
            try
            {
                result = reportM.getTemplateInfoList(paramsMap);
            }
            catch(Exception e)
            {

            }
        }
        else if (nodeType == ReportTreeCons.DATATYPE_NODE)
        {
            result.setData(dataTypeM.getDataTypeList());
        }
        else if (nodeType == ReportTreeCons.DATARULE_NODE)
        {
            result.setData(getDataRuleList(DataRuleCons.ALLTYPE));
        }
        else if (nodeType == ReportTreeCons.SYSTEMVAR_NODE)
        {
            result.setData(getDataRuleList(DataRuleCons.SYSVAR));
        }
        else if (nodeType == ReportTreeCons.AUTONUM_NODE)
        {
            result.setData(getDataRuleList(DataRuleCons.AUTONUM));
        }
        else if (nodeType == ReportTreeCons.DOWNLIST_NODE)
        {
            result.setData(getDataRuleList(DataRuleCons.DOWNLIST));
        }
        else if (nodeType == ReportTreeCons.TREESELECT_NODE)
        {
            result.setData(getDataRuleList(DataRuleCons.TREESELECT));
        }
        else if (nodeType == ReportTreeCons.LISTSELECT_NODE)
        {
            result.setData(getDataRuleList(DataRuleCons.LISTSELECT));
        }

        return result;
    }

    /*
     * 得到数据规范列表
     */
    @ SuppressWarnings("unchecked")
    private ArrayList<DataRule> getDataRuleList(short type)
    {
        return (ArrayList<DataRule>)dataRuleM.getDataRuleList(type);
    }

    /**
     * 得到单一记录
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param start 开始位置
     * @param number 数量
     * @return Result 结果集
     */
    public Result getSingleData(long userID, String nodePath, int start, int number)
    {
        return recordM.getSingleData(userID, nodePath, start, number);
    }

    /**
     * 得到重复记录
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param recordID 记录ID
     * @param start 开始位置
     * @param number 数量
     * @return Result 结果集
     */
    public Result getRepeatData(long userID, String nodePath, long recordID, int start, int number)
    {
        return recordM.getRepeatData(userID, nodePath, recordID, start, number);
    }

    /**
     * 得到记录总数
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @return Result 结果集
     */
    public Result getRecordNumber(long userID, String nodePath)
    {
        return recordM.getRecordNumber(userID, nodePath);
    }

    /**
     * 删除记录
     * 
     * @param userID 用户ID
     * @param templateID 模板ID
     * @param recordIDs 记录ID数组
     * @return Result 结果集
     */
    public Result deleteRecord(long userID, long templateID, long[] recordIDs)
    {
        Result result = new Result();
        if (recordIDs != null && recordIDs.length > 0)
        {
            for (long recordID : recordIDs)
            {
                if (!recordM.deleteRecord(templateID, recordID))
                {
                    result.setErrorCode(ErrorCodeCons.NOTKNOWN_ERROR);
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 上锁/解锁记录
     * 
     * @param userID 用户ID
     * @param templateID 模板ID
     * @param recordIDs 记录ID数组
     * @param lockStatus 锁定状态数组
     * @return Result 结果集
     */
    public Result lockRecord(long userID, long templateID, long[] recordIDs, boolean[] lockStatus)
    {
        return recordM.lockRecord(userID, templateID, recordIDs, lockStatus);
    }

    /**
     * 得到数据规范数据
     * 
     * @param userID 用户ID
     * @param dataRule 数据规范对象
     * @return Result 结果集
     */
    public Result getDataRuleData(long userID, DataRule dataRule)
    {
        Result result = new Result();
        result.setData(dataRuleM.getDataRuleData(userID, dataRule, true));
        return result;
    }

    /**
     * 得到具有权限的已创建的用户数据表
     * 
     * @param userID 用户ID
     * @param tableOperateType tableOperateType 表操作类型(添加、映射)
     * @return ArrayList<DataTable> DataTable集合
     */
    public Result getDataTableList(long userID, int tableOperateType)
    {
        Result result = new Result();
        ArrayList<DataTable> dtables = tableM.getDataTableList(userID, tableOperateType);
        if (dtables != null && !dtables.isEmpty())
        {
            for (int i = 0, size = dtables.size(); i < size; i++)
            {
                dtables.set(i, dtables.get(i).clone(true));
            }
        }
        result.setData(dtables);
        return result;
    }
}