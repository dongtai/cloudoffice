package apps.moreoffice.report.client.remote;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.DataRule;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 提供给GWT前端的异步调用类
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
public interface ReportRemoteAsync
{
    /**
     * 是否是报表设计者
     * 
     * @param userID 用户ID
     * @return Result 结果集
     */
    void isReportDesigner(long userID, AsyncCallback<Result> callback);

    /**
     * 删除模板
     * 
     * @param userID 用户ID
     * @param templateIDs 模板ID数组
     * @param fileNames 模板名数组
     * @return Result 结果集
     */
    void deleteTemplate(long userID, long[] templateIDs, String[] fileNames,
        AsyncCallback<Result> callback);

    /**
     * 设置模板使用状态
     * 
     * @param templateIDs 模板ID数组
     * @param useStates 使用状态数组
     * @return Result 结果集
     */
    void setTemplateUseState(long[] templateIDs, boolean[] useStates, AsyncCallback<Result> callback);

    /**
     * 添加模板分类
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param nodeName 节点名称
     * @return Result 结果集
     */
    void createTemplateSort(long userID, String nodePath, String nodeName,
        AsyncCallback<Result> callback);

    /**
     * 重命名模板分类
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param nodeName 节点名称
     * @return Result 结果集
     */
    void renameTemplateSort(long userID, String nodePath, String nodeName,
        AsyncCallback<Result> callback);

    /**
     * 删除模板分类
     * 
     * @param userID 用户ID
     * @param nodeName 节点名称
     * @return Result 结果集
     */
    void deleteTemplateSort(long userID, String nodePath, AsyncCallback<Result> callback);

    /**
     * 保存或更新实体对象
     * 
     * @param entity 实体对象
     * @return Result 结果集
     */
    void saveOrUpdate(DataBaseObject entity, AsyncCallback<Result> callback);

    /**
     * 删除实体对象
     * 
     * @param entity 实体对象
     * @return Result 结果集
     */
    void delete(DataBaseObject entity, AsyncCallback<Result> callback);

    /**
     * 得到节点分类
     * 
     * @param userID 用户ID
     * @param nodePath 路径
     * @return Result 结果集
     */
    void getNodeSort(long userID, String nodePath, AsyncCallback<Result> callback);

    /**
     * 得到节点数据
     * 
     * @param userID 用户ID
     * @param nodeName 节点名称
     * @param nodeType 节点类型
     * @param start 开始位置
     * @param number 数量
     * @return Result 结果集
     */
    void getNodeData(long userID, String nodeName, int nodeType, int start, int number,
        AsyncCallback<Result> callback);

    /**
     * 得到单一记录
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @param start 开始位置
     * @param number 数量
     * @return Result 结果集
     */
    void getSingleData(long userID, String nodePath, int start, int number,
        AsyncCallback<Result> callback);

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
    void getRepeatData(long userID, String nodePath, long recordID, int start, int number,
        AsyncCallback<Result> callback);

    /**
     * 得到记录总数
     * 
     * @param userID 用户ID
     * @param nodePath 节点路径
     * @return Result 结果集
     */
    void getRecordNumber(long userID, String nodePath, AsyncCallback<Result> callback);

    /**
     * 删除记录
     * 
     * @param userID 用户ID
     * @param templateID 模板ID
     * @param recordIDs 记录ID数组
     * @return Result 结果集
     */
    void deleteRecord(long userID, long templateID, long[] recordIDs, AsyncCallback<Result> callback);

    /**
     * 上锁/解锁记录
     * 
     * @param userID 用户ID
     * @param templateID 模板ID
     * @param recordIDs 记录ID数组
     * @param lockStatus 锁定状态数组
     * @return Result 结果集
     */
    void lockRecord(long userID, long templateID, long[] recordIDs, boolean[] lockStatus,
        AsyncCallback<Result> callback);

    /**
     * 得到数据规范数据
     * 
     * @param userID 用户ID
     * @param dataRule 数据规范对象
     * @return Result 结果集
     */
    void getDataRuleData(long userID, DataRule dataRule, AsyncCallback<Result> callback);

    /**
     * 得到具有权限的已创建的用户数据表
     * 
     * @param userID 用户ID
     * @param tableOperateType tableOperateType 表操作类型(添加、映射)
     * @return ArrayList<DataTable> DataTable集合
     */
    void getDataTableList(long userID, int tableOperateType, AsyncCallback<Result> callback);
}