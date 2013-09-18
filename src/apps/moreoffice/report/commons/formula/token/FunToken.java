package apps.moreoffice.report.commons.formula.token;

/**
 * 函数Token
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
public class FunToken extends RToken
{
    /**
     * 构造器
     * 
     * @param funType 函数类型
     * @param paramsCount 参数个数
     */
    public FunToken(short funType, byte paramsCount)
    {
        super(funType);
        this.paramsCount = paramsCount;
    }

    /**
     * @return 返回 paramsCount
     */
    public byte getParamsCount()
    {
        return paramsCount;
    }
    
    // 参数个数
    private byte paramsCount;
}