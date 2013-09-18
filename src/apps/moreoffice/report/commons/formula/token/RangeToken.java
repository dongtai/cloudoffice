package apps.moreoffice.report.commons.formula.token;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;

/**
 * 区域Token
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User381(俞志刚)
 * <p>
 * @日期:       2012-8-23
 * <p>
 * @负责人:      实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class RangeToken extends RToken
{
    /**
     * 构造器
     * 
     * @param start 开始
     * @param end 结束
     */
    public RangeToken(int start, int end)
    {
        super(FormulaCons.DATE_RANGE);
        this.start = start;
        this.end = end;
    }

    /**
     * 得到区域位置
     * 
     * @return int[] 区域位置
     */
    public int[] getRange()
    {
        return new int[]{start, end};
    }

    // 开始
    private int start;
    // 结束
    private int end;
}