package apps.moreoffice.report.commons.formula.token;

/**
 * 常量Token
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User381(俞志刚)
 * <p>
 * @日期:       2012-8-23
 * <p>
 * @负责人:     实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class ObjectToken extends RToken
{
    /**
     * 构造器
     * 
     * @param type token类型
     * @param value 常量
     */
    public ObjectToken(short type, Object value)
    {
        super(type);
        this.value = value;
    }

    /**
     * @return 返回 value
     */
    public Object getValue()
    {
        return value;
    }
    
    // 常量
    private Object value;
}