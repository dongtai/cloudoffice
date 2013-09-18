package apps.moreoffice.report.commons.domain.constants;

/**
 * 方法常量
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
public interface MethodCons
{
    // 得到用户对象
    public final static String GETUSER = "getUser";

    // ----------与模板相关的Action----------
    // 检查模板信息
    public final static String CHECKTEMPLATE = "checkTemplate";
    // 得到模板信息
    public final static String GETTEMPLATE = "getTemplate";
    // 删除模板对象
    public final static String DELETETEMPLATE = "deleteTemplate";
    // 创建模板分类
    public final static String CREATETEMPLATESORT = "createTemplateSort";
    // 重命名模板分类
    public final static String RENAMETEMPLATESORT = "renameTemplateSort";
    // 删除模板分类
    public final static String DELETETEMPLATESORT = "deleteTemplateSort";
    // 得到模板分类
    public final static String GETTEMPLATESORT = "getTemplateSort";
    // 得到模板列表
    public final static String GETTEMPLATELIST = "getTemplateList";
    // 得到模板权限
    public final static String GETTEMPLATEPERMISSION = "getTemplatePermission";
    // 设置模板的使用状态
    public final static String SETTEMPLATEUSESTATE = "setTemplateUseState";
    // 得到模板的使用状态
    public final static String GETTEMPLATEUSESTATE = "getTemplateUseState";
    // 得到模板是否正在被填报
    public final static String ISREPORTING = "isReporting";
    // 得到模板是否正在被设计
    public final static String ISDESIGNING = "isDesigning";
    // ----------客户端与模板相关的Action----------
    // 报表标识
    public final static String REPORT = "report_";
    // 新建模板
    public final static String NEWTEMPLATE = REPORT + "new";
    public final static String OPEN = REPORT + "open_";
    // 修改模板
    public final static String MODIFYTEMPLATE = OPEN + "design";
    // 填报模板
    public final static String FILLTEMPLATE = OPEN + "fill";
    // 修改记录
    public final static String MODIFYRECORD = FILLTEMPLATE + "_modifyRecord";
    // 查看记录
    public final static String VIEWRECORD = FILLTEMPLATE + "_VIEWRECORD";
    // 导入数据
    public final static String IMPORTDATA = REPORT + "importData";
    
    // 流程标识
    public final static String WORKFLOW = "workFlow_";
    // 新建流程
    public final static String NEWWORKFLOW = WORKFLOW + "newWorkFlow";

    // ----------与记录相关的Action----------
    // 保存记录
    public final static String SAVERECORD = "saveRecord";
    // 根据记录ID得到模板对应的记录
    public final static String GETRECORD = "getRecord";
    // 根据记录ID得到对应的记录ID(第一、前、后、最后记录ID)
    public final static String GETRECORDID = "getRecordID";
    // 删除记录
    public final static String DELETERECORD = "deleteRecord";
    // 锁记录
    public final static String LOCKRECORD = "lockRecord";
    // 得到单一记录列表值
    public final static String GETSINGLEDATA = "getSingleData";
    // 得到重复记录列表值
    public final static String GETREPEATDATA = "getRepeatData";

    // ----------与组织架构相关的Action----------
    // 判断是否是报表设计者
    public final static String ISREPORTDESIGNER = "isReportDesigner";
    // 得到组织架构树
    public final static String GETORGANIZATION = "getOrganization";
    // 得到某个组织下的组织用户对象
    public final static String GETORGUSERBYORG = "getOrgUserByOrg";
    // 得到用户所在组织
    public final static String GETORGBYUSER = "getOrgByUser";
    // 得到设计者列表
    public final static String GETDESIGNERLIST = "getDesignerList";

    // ----------与表相关的Action----------
    // 得到可操作的报表数据表列表
    public final static String GETRTABLELIST = "getRTableList";
    // 得到已创建的用户数据表列表
    public final static String GETDATATABLELIST = "getDataTableList";
    // 检查表名的有效性
    public final static String CHECKTABLENAME = "checkTableName";

    // ----------与数据类型相关的Action----------
    // 保存数据类型
    public final static String SAVEDATATYPE = "saveDataType";
    // 删除数据类型
    public final static String DELETEDATATYPE = "deleteDataType";
    // 得到数据类型列表
    public final static String GETDATATYPELIST = "getDataTypeList";
    // 通过数据类型ID得到数据类型
    public final static String GETDATATYPEBYID = "getDataTypeByID";

    // ----------与数据规范相关的Action----------
    // 保存数据规范
    public final static String SAVEDATARULE = "saveDataRule";
    // 删除数据规范
    public final static String DELETEDATARULE = "deleteDataRule";
    // 得到数据规范列表
    public final static String GETDATARULELIST = "getDataRuleList";
    // 通过数据规范ID得到数据规范
    public final static String GETDATARULEBYID = "getDataRuleByID";
    // 通过数据规范ID得到数据规范的值
    public final static String GETDATARULEDATA = "getDataRuleData";

    // ----------与表间规则相关的Action----------
    // 执行表间规则
    public final static String EXECTABLERULES = "execTableRules";
    // 得到数据库函数值
    public final static String GETFUNCTIONDATA = "getFunctionData";

    // ----------与文件相关的Action----------
    // 保存为网页
    public final static String UPLOADHTML = "uploadHtml";
    // 保存为流程
    public final static String UPLOADWORKFLOW = "uploadWorkFlow";
}