package apps.moreoffice.report.commons.formula.token;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 报表公式中的token
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User381(俞志刚)
 * <p>
 * @日期:       2012-7-23
 * <p>
 * @负责人:      实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class RToken implements SerializableAdapter
{
    // RToken类型
    private short type;

    /**
     * 构造一个RToken
     * 
     * @param type RToken类型
     */
    public RToken(short type)
    {
        this.type = type;
    }

    /**
     * 得到RToken类型
     * 
     * @return short RToken类型
     */
    public short getType()
    {
        return type;
    }
    
    // 空值
    public static final RToken NULL_TOKEN = new RToken(FormulaCons.NULL);
    
    // +、-、*、/、^、&
    public static final RToken PLUS_TOKEN = new RToken(FormulaCons.PLUS);
    public static final RToken MINUS_TOKEN = new RToken(FormulaCons.MINUS);
    public static final RToken MULTIPLY_TOKEN = new RToken(FormulaCons.MULTIPLY);
    public static final RToken DIVIDE_TOKEN = new RToken(FormulaCons.DIVIDE);
    public static final RToken POWER_TOKEN = new RToken(FormulaCons.POWER);
    public static final RToken CONCAT_TOKEN = new RToken(FormulaCons.CONCAT);

    // 小于、小于或等于、等于、大于或等于、大于、不等于、形如、介于、属于、不属于
    public static final RToken[] COMPARE_TOKENS = {new RToken(FormulaCons.LESS),
        new RToken(FormulaCons.LESS_EQUAL), new RToken(FormulaCons.EQUAL),
        new RToken(FormulaCons.GREATER_EQUAL), new RToken(FormulaCons.GREATER),
        new RToken(FormulaCons.NOT_EQUAL), new RToken(FormulaCons.LIKE), new RToken(FormulaCons.BETWEEN),
        new RToken(FormulaCons.BELONG), new RToken(FormulaCons.NOT_BELONG), 
        new RToken(FormulaCons.HAS_VALUE), new RToken(FormulaCons.NULL_VALUE)};
    
    // 并且、或者、不满足
    public static final RToken AND_TOKEN = new RToken(FormulaCons.AND);
    public static final RToken OR_TOKEN = new RToken(FormulaCons.OR);
    public static final RToken NOT_TOKEN = new RToken(FormulaCons.NOT);
}