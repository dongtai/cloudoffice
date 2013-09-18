package apps.moreoffice.report.client.remote;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.DataRule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * 提供给GWT前端的入口类
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
public interface ReportRemote extends RemoteService
{
    // 名称
    public static final String SERVICE_URI = "ReportRemote";

    /**
     * 得到操作实例类
     * 
     * <pre>
     * 例如：ReportRemote.Util.getInstance()
     * </pre>
     */
    public static class Util
    {
        private static class Holder
        {
            private static ReportRemoteAsync instance = null;
            static
            {
                instance = (ReportRemoteAsync)GWT.create(ReportRemote.class);
                ServiceDefTarget target = (ServiceDefTarget)instance;
                target.setServiceEntryPoint(GWT.getModuleBaseURL() + SERVICE_URI);
            }
        }

        public static ReportRemoteAsync getInstance()
        {
            return Holder.instance;
        }
    }
    
    /**
     * 是否是报表设计者
     * 
     * @param userID 用户ID
     * @return Result 结果集
     */
    Result isReportDesigner(long userID);

    /**
     * 删除模板
     * 
     * @param userID 用户ID
     * @param templateIDs 模板ID数组
     * @param fileNames 模板名数组
     * @return Result 结果集
     */
    Result deleteTemplate(long userID, long[] templateIDs, String[] fileNames);

    /**
     * 设置模板使用状态
     * 
     * @param templateIDs 模板ID数组
     * @param useStates 使用状态数组
     * @return Result 结果集
     */
    Result setTemplateUseState(long[] templateIDs, boolean[] useStates);

    /**
     * 添加模板分类
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param nodeName 节点名称
     * @return Result 结果集
     */
    Result createTemplateSort(long userID, String nodePath, String nodeName);

    /**
     * 重命名模板分类
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param nodeName 节点名称
     * @return Result 结果集
     */
    Result renameTemplateSort(long userID, String nodePath, String nodeName);

    /**
     * 删除模板分类
     * 
     * @param userID 用户ID
     * @param nodeName 节点名称
     * @return Result 结果集
     */
    Result deleteTemplateSort(long userID, String nodePath);

    /**
     * 保存或更新实体对象
     * 
     * @param entity 实体对象
     * @return Result 结果集
     */
    Result saveOrUpdate(DataBaseObject entity);

    /**
     * 删除实体对象
     * 
     * @param entity 实体对象
     * @return Result 结果集
     */
    Result delete(DataBaseObject entity);

    /**
     * 得到节点分类
     * 
     * @param userID 用户ID
     * @param nodePath 路径
     * @return Result 结果集
     */
    Result getNodeSort(long userID, String nodePath);

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
    Result getNodeData(long userID, String nodePath, int nodeType, int start, int number);

    /**
     * 得到单一记录
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param start 开始位置
     * @param number 数量
     * @return Result 结果集
     */
    Result getSingleData(long userID, String nodePath, int start, int number);

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
    Result getRepeatData(long userID, String nodePath, long recordID, int start, int number);

    /**
     * 得到记录总数
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @return Result 结果集
     */
    Result getRecordNumber(long userID, String nodePath);

    /**
     * 删除记录
     * 
     * @param userID 用户ID
     * @param templateID 模板ID
     * @param recordIDs 记录ID数组
     * @return Result 结果集
     */
    Result deleteRecord(long userID, long templateID, long[] recordIDs);

    /**
     * 上锁/解锁记录
     * 
     * @param userID 用户ID
     * @param templateID 模板ID
     * @param recordIDs 记录ID数组
     * @param lockStatus 锁定状态数组
     * @return Result 结果集
     */
    Result lockRecord(long userID, long templateID, long[] recordIDs, boolean[] lockStatus);

    /**
     * 得到数据规范数据
     * 
     * @param userID 用户ID
     * @param dataRule 数据规范对象
     * @return Result 结果集
     */
    Result getDataRuleData(long userID, DataRule dataRule);
    
    /**
     * 得到具有权限的已创建的用户数据表
     * 
     * @param userID 用户ID
     * @param tableOperateType tableOperateType 表操作类型(添加、映射)
     * @return ArrayList<DataTable> DataTable集合
     */
    Result getDataTableList(long userID, int tableOperateType);
}