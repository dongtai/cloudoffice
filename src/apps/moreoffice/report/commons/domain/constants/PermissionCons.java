package apps.moreoffice.report.commons.domain.constants;

/**
 * 权限常量
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-16
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface PermissionCons
{
    // ----------权限对象类型----------
    // 模板
    public static final int TEMPLATE = 0;
    // 表
    public static final int TABLE = 1;

    // ----------权限作用对象类型----------
    // 用户
    public static final short USER = 0;
    // 组织用户
    public static final short ORG_USER = 1;

    // ----------权限类型----------
    // 基本权限(填报、查阅、打印等)
    public static final short BASE = 1;
    // 设计权限
    public static final short DESIGN = 2;

    // ----------权限常量----------
    // 填报
    public final static int CANFILL = 1;
    // 查阅
    public final static int CANREAD = CANFILL + 1;
    // 打印
    public final static int CANPRINT = CANREAD + 1;
    // 新建
    public final static int CANNEW = CANPRINT + 1;
    // 修改
    public final static int CANMODIFY = CANNEW + 1;
    // 删除
    public final static int CANDELETE = CANMODIFY + 1;
    // 存本地
    public final static int CANSAVE = CANDELETE + 1;
    // 导出
    public final static int CANEXPORT = CANSAVE + 1;
    // 设计
    public final static int CANDESIGN = CANEXPORT + 1;
    // 提数
    public final static int CANEXTRACT = CANDESIGN + 1;
    // 回写
    public final static int CANWRITE = CANEXTRACT + 1;
    // 添加
    public final static int CANADDFIELD = CANWRITE + 1;
    // 映射
    public final static int CANMAP = CANADDFIELD + 1;
}