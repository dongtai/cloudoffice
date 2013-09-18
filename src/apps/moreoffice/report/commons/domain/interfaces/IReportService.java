package apps.moreoffice.report.commons.domain.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.databaseObject.DataRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataType;
import apps.moreoffice.report.commons.domain.databaseObject.Record;
import apps.moreoffice.report.commons.domain.databaseObject.Template;

/**
 * 报表应用中与服务器交互的Action接口
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-10
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface IReportService
{
    /**
     * 注册服务器
     * 
     * @param serverName 服务器名称
     * @param server 服务器
     */
    void registServer(String serverName, IServer server);

    /**
     * 得到服务器
     * 
     * @param serverName 服务器名称
     * @return IServer 服务器
     */
    IServer getServer(String serverName);

    /**
     * 登录
     * 
     * @param loginName 登录名
     * @param passWord 密码
     * @return String 如果登录成功，返回null，如果登录不成功，返回错误信息
     */
    String login(String loginName, String passWord);

    /**
     * 初始化用户信息
     * 
     * @param loginName 登录名
     * @param token token
     * @return String 结果
     */
    String initUserInfo(String loginName, String token);

    /**
     * 模板对象存盘前的检查
     * 
     * @param templateNum 模板编号
     * @param templateName 模板名称
     * @return Result 结果
     */
    Result checkTemplate(String templateNum, String templateName);

    /**
     * 保存模板对象
     * 
     * @param template 模板对象
     * @return Result 如果保存成功，返回Template
     *                如果保存没有成功，返回错误提示
     */
    Result saveTemplate(Template template);

    /**
     * 得到模板对象
     * 会返回整个模板对象
     * 
     * @param templateName 模板名称
     * @return Result 如果成功，返回Template
     *                如果没有成功，返回错误提示
     */
    Result getTemplate(String templateName);

    /**
     * 删除模板对象
     * 
     * @param templateID 模板ID
     * @return Result 如果没有删除成功，返回错误提示
     */
    Result deleteTemplate(long templateID);

    /**
     * 得到指定路径下的模板分类
     * 
     * @param path 路径
     * @return Result 结果
     */
    Result getTemplateSort(String path);

    /**
     * 得到模板列表
     * 
     * @param path 路径
     * @param permissionType 权限类型
     * @return Result 结果
     */
    Result getTemplateList(String path, int permissionType);

    /**
     * 得到模板权限
     * 
     * @param templateID 模板ID
     * @return Result 结果
     */
    Result getTemplatePermission(long templateID);

    /**
     * 得到模板的使用状态(停用或启用)
     * 
     * @param templateID 模板ID
     * @return Result 结果集
     */
    Result getTemplateUseState(long templateID);

    /**
     * 得到模板是否正在被填报
     * 
     * @param templateID 模板ID
     * @return Result 模板是否正在被填报
     */
    Result isReporting(long templateID);

    /**
     * 得到模板是否正在被设计
     * 
     * @param templateID 模板ID
     * @return Result 模板是否正在被设计
     */
    Result isDesigning(long templateID);

    /**
     * 保存记录
     * 
     * @param record 记录对象
     * @return Result 结果(记录索引)
     */
    Result saveRecord(Record record);

    /**
     * 得到记录值
     * 
     * @param templateID 模板ID
     * @param recordID 记录ID
     * @return Result 结果(Record对象)
     */
    Result getRecord(long templateID, long recordID);

    /**
     * 根据记录ID得到对应的相关记录ID(第一、前、后、最后记录ID)
     * 
     * @param templateID 模板ID
     * @param recordID 当前记录ID
     * @return Result 结果(长度为4的long数组)
     */
    Result getRecordID(long templateID, long recordID);

    /**
     * 删除记录
     * 
     * @param templateID 模板ID
     * @param recordID 记录ID
     * @return Result 结果
     */
    Result deleteRecord(long templateID, long recordID);

    /**
     * 是否是报表设计者
     * 
     * @return Result 结果
     */
    Result isReportDesigner();

    /**
     * 得到组织结构
     * 
     * @return Result 结果
     */
    Result getOrganization();

    /**
     * 得到某个组织下的组织用户对象
     * 
     * @param orgID 组织ID
     * @return Result 结果
     */
    Result getOrgUserByOrg(long orgID);

    /**
     * 得到用户所在组织
     * 
     * @return Result 结果
     */
    Result getOrgByUser();

    /**
     * 得到设计者列表
     * 
     * @return Result 结果
     */
    Result getDesignerList();

    /**
     * 得到具有权限的报表数据表列表(仅基本数据)
     * 
     * @param pathType 路径类型(分类or模板)
     * @param path 名称(分类或模板名称)
     * @param tableType 表类型(单一、重复、所有)
     * @param tableOperateType 表操作类型(提数、回写)
     * @param dataOperateType 记录操作类型(新建、修改、删除)
     * @return Result 结果
     */
    Result getRTableList(int pathType, String path, int tableType, int tableOperateType,
        int dataOperateType);

    /**
     * 得到具有权限的已创建的用户数据表列表
     * 
     * @param tableOperateType 表操作类型(添加、映射)
     * @return Result 结果
     */
    Result getDataTableList(int tableOperateType);

    /**
     * 检查表名是否合法
     * 
     * @param tableName 表名
     * @return Result 结果
     */
    Result checkTableName(String tableName);

    /**
     * 保存数据类型
     * 
     * @param dataType 数据类型
     * @return Result 结果
     */
    Result saveDataType(DataType dataType);

    /**
     * 得到数据类型列表
     * 
     * @return Result 结果
     */
    Result getDataTypeList();

    /**
     * 通过数据类型ID得到数据类型
     * 
     * @param dataTypeID 数据类型ID
     * @return Result 结果
     */
    Result getDataTypeByID(long dataTypeID);

    /**
     * 保存数据规范
     * 
     * @param dataRule 数据规范
     * @return Result 结果
     */
    Result saveDataRule(DataRule dataRule);

    /**
     * 得到数据规范列表
     * 
     * @param type 类型
     * @return Result 结果
     */
    Result getDataRuleList(int type);

    /**
     * 通过数据规范ID得到数据规范
     * 
     * @param dataRuleID 数据规范ID
     * @return Result 结果
     */
    Result getDataRuleByID(long dataRuleID);

    /**
     * 得到数据规范的值
     * 
     * @param dataRuleID 数据规范ID
     * @return Result 结果
     */
    Result getDataRuleData(long dataRuleID);

    /**
     * 执行单个表间规则(可能有返回值)
     * 
     * @param tableRuleID 表间规则ID
     * @param currentData 当前数据
     * @return Result 结果
     */
    Result execTableRule(long tableRuleID, HashMap<String, Object> currentData);

    /**
     * 执行多个表间规则(无返回值)
     * 
     * @param tableRuleIDs 表间规则ID列表
     * @param currentData 当前数据
     * @return Result 是否有错误
     */
    Result execTableRules(ArrayList<Long> tableRuleIDs, HashMap<String, Object> currentData);

    /**
     * 得到数据库函数值
     * 
     * @param tableName 表名
     * @param fieldName 字段名
     * @param funType 函数类型
     * @return Result 函数计算结果
     */
    Result getFunctionData(String tableName, String fieldName, int funType);

    /**
     * 上传文件
     * 
     * @param action 方法名
     * @param path 路径
     * @param filePaths 文件路径数组
     * @return Result 是否成功
     */
    Result uploadFile(String action, String path, String[] filePaths);

    /**
     * 下载文件
     * 
     * @param downLoadType 下载类型
     * @param fileName 文件名
     * @return Result 文件
     */
    Result downLoadFile(int downLoadType, String fileName);
}