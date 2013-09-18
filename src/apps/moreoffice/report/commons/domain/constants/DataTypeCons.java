package apps.moreoffice.report.commons.domain.constants;

/**
 * 数据类型常量
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-16
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface DataTypeCons
{
    // ----------数据类型常量----------
    // 文字
    public final static short TEXT_TYPE = 1;
    // 数字
    public final static short NUMBER_TYPE = 2;
    // 日期
    public final static short DATE_TYPE = 3;
    // 图形
    public final static short PICTURE_TYPE = 4;
    // 附件
    public final static short FILE_TYPE = 5;

    // 是否允许其它设计者修改
    public final static int ALLOWOTHERMODIFY = 0;
}