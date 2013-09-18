package apps.moreoffice.report.commons.domain.constants;

/**
 * 表和字段常量
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-5
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface TableCons
{
    // ----------路径类型常量----------
    // 分类
    public final static int SORT = 0;
    // 模板
    public final static int TEMPLATE = 1;
    
    // ----------表类型常量----------
    // 所有数据表
    public final static short ALLTABLE = 0;
    // 单一数据表
    public final static short SINGLETABLE = 1;
    // 重复数据表
    public final static short REPEATTABLE = 2;
    
    // ----------表模式常量----------
    // 单一数据项
    public final static short SINGLE = 0;
    // 行模式
    public final static short ROWMODE = 1;
    // 列模式
    public final static short COLUMNMODE = 2;
    // 交叉表
    public final static short CROSSMODE = 3;
    
    // ----------创建状态常量----------
    // 不创建
    public final static short NOCREATE = 0;
    // 需要创建
    public final static short NEEDCREATE = 1;
    // 已创建
    public final static short HASCREATED = 2;
    
    // ----------创建数据项时选择的表来源----------
    // 创建新表
    public final static short CREATETABLE = 0;
    // 添加到已有数据表
    public final static short ADDTOTABLE = 1;
    // 对应到已有数据表
    public final static short MAPTOTABLE = 2;
    
    // ----------排序常量----------
    // 不排序
    public final static byte DEFAULT = -1;
    // 升序
    public final static byte ASC = 0;
    // 降序
    public final static byte DESC = 1;
    
    // ----------地址类型----------
    public final static short SSCELL = 0;
}