package apps.moreoffice.report.commons.domain.constants;

/**
 * 参数常量接口
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-11
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface ParamCons
{
    // ----------json请求常量----------
    // json参数
    public static final String JSONPARAMS = "jsonParams";
    // 错误代码
    public final static String ERROR_CODE = "errorCode";
    // 错误信息
    public final static String ERROR_MESSAGE = "errorMessage";
    // 结果
    public final static String RESPONSE_RESULT = "result";
    // json请求的方法
    public final static String METHOD = "method";
    // json请求的具体参数
    public final static String PARAMS = "params";
    // json请求的token值
    public final static String TOKEN = "token";
    // 类型
    public final static String TYPE = "type";

    // 用户ID
    public final static String USERID = "userID";
    // 登录名
    public final static String LOGINNAME = "loginName";
    // 登录密码
    public final static String PASSWORD = "passWord";
    // 组织ID
    public final static String ORGID = "orgID";

    // 模板ID
    public final static String TEMPLATEID = "templateID";
    // 模板编号
    public final static String TEMPLATENUM = "templateNum";
    // 模板名称
    public final static String TEMPLATENAME = "templateName";
    // 权限
    public final static String PERMISSION = "permission";
    // 路径
    public final static String PATH = "path";
    // 使用状态
    public final static String USESTATE = "useState";
    // 路径类型
    public final static String PATHTYPE = "pathType";
    // 外部数据源
    public final static String EXTERNALDATASOURCE = "externalDataSource";

    // 表类型
    public final static String TABLETYPE = "tableType";
    // 表操作类型
    public final static String TABLEOPERATETYPE = "tableOperateType";
    // 数据操作类型
    public final static String DATAOPERATETYPE = "dataOperateType";
    // 表名
    public final static String TABLENAME = "tableName";
    // 字段名
    public final static String FIELDNAME = "fieldName";
    // 报表数据表ID
    public final static String RTABLEID = "rTableID";
    // 用户数据表ID
    public final static String DATATABLEID = "dataTableID";

    // 数据类型ID
    public final static String DATATYPEID = "dataTypeID";

    // 数据规范ID
    public final static String DATARULEID = "dataRuleID";
    // 数据规范类型
    public final static String DATARULETYPE = "dataRuleType";

    // 需要执行的表间规则ID数组
    public final static String TABLERULEIDS = "tableRuleIDs";
    // 执行表间规则时当前报表的值
    public final static String CURRENTDATA = "currentData";

    // 记录ID
    public final static String RECORDID = "recordID";
    // 记录开始值
    public final static String START = "start";

    // 函数类型
    public final static String FUNTYPE = "funType";

    // 保存文件类型
    public final static String UPLOADTYPE = "uploadType";
    // 文件名
    public final static String FILENAME = "fileName";

    // json格式请求
    public final static String ISJSON = "isJson";

    // ----------对象属性常量----------
    // 公共属性
    // 对象ID
    public final static String ID = "id";
    // 对象名称
    public final static String NAME = "name";
    // 基本属性集
    public final static String ATTRFLAG = "attrFlag";
    // 引用的物理表
    public final static String DTABLE = "dtable";
    // 引用的物理字段
    public final static String DFIELD = "dfield";
    // 引用的逻辑表
    public final static String RTABLE = "rtable";
    // 引用的逻辑字段
    public final static String RFIELD = "rfield";
    // 创建者ID
    public final static String CREATORID = "creatorId";
    // 创建时间
    public final static String CREATEDATE = "createDate";
    // 修改者ID
    public final static String MODIFIERID = "modifierId";
    // 修改时间
    public final static String MODIFYDATE = "modifyDate";

    // Template属性
    // 模板编号
    public final static String NUMBER = "number";
    // 用户表
    public final static String RTABLES = "rtables";
    // 表间规则
    public final static String TABLERULES = "tableRules";
    // 锁定条件
    public final static String LOCKCOND = "lockCond";
    // 权限是否有改变
    public final static String PERMISSIONCHANGED = "permissionChanged";

    // RTable属性
    // 对应的字段
    public final static String RFIELDS = "rfields";
    // 模式(0:单一; 1:行模式; 2:列模式; 3:交叉表)
    public final static String MODE = "mode";
    // 对应关系(新创建的、添加字段、映射)
    public final static String RELATION = "relation";
    // 重复表的记录ID数组
    public final static String DATAIDS = "dataIDs";

    // RField属性
    // 数据类型
    public final static String DATATYPE = "dataType";
    // 目标类型
    public final static String ADDRESSTYPE = "addressType";
    // 目标所在的sheetID
    public final static String SHEETID = "sheetID";
    // 目标地址
    public final static String ADDRESS = "address";
    // 别名
    public final static String ALIAS = "alias";
    // 必填提示
    public final static String NOTNULLTIP = "notNullTip";
    // 位置
    public final static String POSITION = "position";
    // 超链接
    public final static String HYPERLINK = "hyperLink";
    // 数据规范
    public final static String DATARULECOND = "dataRuleCond";
    // 值
    public final static String VALUES = "values";

    // DataTable属性
    // 物理上的表或视图名，即真正的表或视图名
    public final static String REALNAME = "realName";
    // 创建状态
    public final static String CREATESTATE = "createState";
    // 字段
    public final static String DFIELDS = "dfields";
    // 权限集
    public final static String PERMISSIONS = "permissions";
    // 数据源
    public final static String DATASOURCE = "dataSource";

    // TreeInfo属性
    // 父对象
    public final static String PARENT = "parent";
    // 孩子对象
    public final static String CHILDS = "childs";

    // DataType属性
    // 基本类型
    public final static String BASICTYPE = "basicType";
    // 限定长度
    public final static String LIMITLENG = "limitLength";
    // 小数位数
    public final static String DECIMALDIGIT = "decimalDigit";
    // 匹配值
    public final static String MATCHPATTERN = "matchPattern";
    // 说明
    public final static String DESCRIPTION = "description";

    // DataRule属性
    // 是否允许其他设计者修改
    public final static String ALLOWOTHERSMODIFY = "allowOthersModify";
    // 是否系统预定义
    public final static String SYSTEMDEFINED = "systemDefined";
    // AutoNumRule属性
    // 编号格式
    public final static String FORMAT = "format";
    // 顺序号
    public final static String NUMBERS = "numbers";
    // DownListRule属性
    // 数据源类型
    public final static String DATASOURCETYPE = "dataSourceType";
    // 固定取值
    public final static String FIXEDVALUE = "fixedValue";
    // 来自数据表时的排序字段名
    public final static String SORTFIELD = "sortField";
    // 筛选条件
    public final static String FILTERCOND = "filterCond";
    // TreeSelectRule属性
    // 构造方式
    public final static String FORMATTYPE = "formatType";
    // 分级字段
    public final static String LEVELFIELDS = "levelFields";
    // 编码或关键字段
    public final static String CODEORPRIMARYFIELD = "codeorPrimaryField";
    // 父关键字段
    public final static String PARENTPRIMARYFIELD = "parentPrimaryField";
    // 显示字段
    public final static String VIEWFIELD = "viewField";
    // 各级位数 
    public final static String LEVELNUM = "levelNum";
    // 排序选择
    public final static String SORTSELECT = "sortSelect";
    // 排序类型
    public final static String SORTTYPE = "sortType";
    // ListSelectRule属性
    // 数据项
    public final static String LISTSELECTITEMS = "listSelectItems";

    // Permission属性
    // 权限对应的作用对象类型
    public final static String OBJECTTYPE = "objectType";
    // 权限对应的作用对象ID
    public final static String OBJECTID = "objectID";
    // 查阅范围
    public final static String ACCESSRANGE = "accessRange";
    // 动态条件
    public final static String DYNAMICCOND = "dynamicCond";
    // 隐藏字段
    public final static String HIDEFIELDS = "hideFields";
    // 填报字段
    public final static String FILLFIELDS = "fillFields";

    // HyperLink属性
    // 目标模板文件名
    public final static String TARGETTEMPLATENAME = "targetTemplateName";
    // 外部URL
    public final static String URLPATH = "urlPath";
    // 超链接信息
    public final static String HYPERLINKINFOS = "hyperLinkInfos";

    // HyperLinkInfo属性
    // 本报表字段
    public final static String CURRENTFIELD = "currentField";
    // 目标模板上的字段
    public final static String TARGETFIELD = "targetField";
    // URL参数
    public final static String URLPARAM = "urlParam";

    // DataRuleCond属性
    // 数据规范
    public final static String DATARULE = "dataRule";
    // 父单元格
    public final static String PARENTFIELD = "parentField";

    // TableRule属性
    // 规则说明
    public final static String EXPLAIN = "explain";
    // 应用方式
    public final static String APPMODE = "appMode";
    // 公共属性集
    public final static String COMMONATTRFLAG = "commonAttrFlag";
    // ReadRule属性
    // 关联条件
    public final static String JOINCONDS = "joinConds";
    // 填充数据表
    public final static String FILLTABLE = "fillTable";
    // 填充方式
    public final static String FILLMODES = "fillModes";
    // 执行条件
    public final static String EXECCOND = "execCond";
    // 提取前N条记录
    public final static String DISPLAYNUMBER = "displayNumber";
    // ModifyRule属性
    // 回写项
    public final static String WRITEMODEITEMS = "writeModeItems";
    // AddDetailRule属性
    // 选择的模板
    public final static String TEMPLATE = "template";
    // 主表条件
    public final static String PRIMARYTABLECOND = "primaryTableCond";
    // NewFormRule属性
    // 回写方式
    public final static String WRITEMODES = "writeModes";

    // JoinCond属性
    // 左表
    public final static String LEFTTABLE = "leftTable";
    // 左字段
    public final static String LEFTFIELD = "leftField";
    // 右表
    public final static String RIGHTTABLE = "rightTable";
    // 右字段
    public final static String RIGHTFIELD = "rightField";

    // FillMode属性
    // 填充项
    public final static String FILLMODEITEMS = "fillModeItems";

    // FillModeItem属性
    // 提取表达式
    public final static String EXPRESSION = "expression";
    // 操作
    public final static String OPERATE = "operate";
    // 锁定
    public final static String LOCKSTATE = "lockState";

    // ListSelectItem属性
    // 显示名称
    public final static String SHOWNAME = "showName";
    // 关联树型规范
    public final static String TREESELECTRULE = "treeSelectRule";
    // 关联类型
    public final static String ASSOCIATIONTYPE = "associationType";

    // OrgUser属性
    // 组织对象
    public final static String ORGANIZATION = "organization";
    // 用户对象
    public final static String USER = "user";

    // User属性
    // 用户名
    public final static String USERNAME = "userName";

    // Record属性
    // 创建者所在部门ID
    public final static String CREATORORGID = "creatorOrgID";
    // 数据记录索引
    public final static String RECORDINDEXS = "recordIndexs";
    // 是否是导入记录模式
    public final static String ISIMPORTDATA = "isImportData";

    // RecordIndex属性
    // 数据记录id
    public final static String DATAID = "dataID";

    // NodeInfo属性
    // 是否是叶子节点
    public final static String ISLEAF = "isLeaf";

    // TemplateInfo属性
    // 状态
    public final static String STATUS = "status";
    // 创建者
    public final static String CREATORNAME = "creatorName";
    // 修改者
    public final static String MODIFIERNAME = "modifierName";
    // 最后修改时间
    public final static String LASTMODIFYDATE = "lastModifyDate";
}