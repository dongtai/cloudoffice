package apps.moreoffice.report.commons.domain.constants;

/**
 * 表间规则常量
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-23
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface TableRuleCons
{
    // ----------表间规则类别----------
    // 任意规则
    public final static short ALL_RULE = 0;
    // 提数规则
    public final static short READ_RULE = 1;
    // 回写规则
    public final static short WRITE_RULE = 2;
    // 修改规则
    public final static short MODIFY_RULE = 3;
    // 补充明细规则
    public final static short ADDDETAIL_RULE = 4;
    // 删除明细规则
    public final static short DELDETAIL_RULE = 5;
    // 新建表单规则
    public final static short NEWFORM_RULE = 6;
    // 删除表单规则
    public final static short DELFORM_RULE = 7;

    // ----------应用方式----------
    // 手动执行
    public final static int MANUAL = 0;
    // 数据改变
    public final static int DATACHANGE = 1;
    // 初始填报打开
    public final static int FIRSTFILL_OPEN = 2;
    // 查看打开
    public final static int VIEWOPEN = 3;
    // 修改打开
    public final static int MODIFYOPEN = 4;
    // 初始填报保存时
    public final static int FIRSTFILL_SAVE = 5;
    // 每次保存
    public final static int EVERYSAVE = 6;
    // 删除或撤销
    public final static int DELETEORUNDO = 7;

    // ----------关联条件----------
    // 相等
    public final static String EQUAL = "=";
    // 左关联
    public final static String LEFTJOIN = "*=";
    // 右关联
    public final static String RIGHTJOIN = "=*";

    // ----------填充方式----------
    // 输入值
    public final static short INPUTVALUE = 0;
    // 构造下拉选项
    public final static short COMBOBOX = 1;
    // 构造可编辑下拉
    public final static short EDITCOMBOBOX = 2;

    // ----------boolean属性标记位----------
    // -----TableRule-----
    // 自动用默认值替换数据中的空值
    public final static int DEFAULTVALUETONULL = 0;
    // -----ReadRule-----
    // 显示全部查询数据
    public final static int SHOWALLDATA = 0;
    // 重复数据只显示一次
    public final static int REPEATDATASHOWONE = 1;
    // 手工应用时隐藏公式内容
    public final static int HIDEFORMULACONTENT = 2;
    // -----NewFormRule-----
    // 新建完毕后立即打开修改
    public final static int OPENMODIFY = 0;
}