package apps.moreoffice.report.commons.formula.token;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;

/**
 * 词法分析Token
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       实习生76(魏强)
 * <p>
 * @日期:       2012-12-7
 * <p>
 * @负责人:     实习生76(魏强)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */

public class LexToken
{
    //调用者自己必须保证text非空，且type是有意义的值。
    //本模块没有对text与type进行合法性检查。
    private String text;
    private short type;
    
    public LexToken()
    {
        text = "";
        type = FormulaCons.INVALID;
    }
 
    public LexToken(String text, short type)
    {
        this.text = text;
        this.type = type;
    }
    
    public String getText()
    {
        return text;
    }
    
    public int textLength()
    {
        return text.length();
    }
    
    public boolean textEquals(String text)
    {
        return this.text.equals(text);
    }
    
    public boolean textStartsWith(String prefix)
    {
        return this.text.startsWith(prefix);
    }
    
    public boolean textContains(CharSequence s)
    {
        return this.text.contains(s);
    }
    
    public void setText(String text)
    {
        this.text = text;
    }
    
    public short getType()
    {
        return type;
    }
    
    public void setType(short type)
    {
        this.type = type;
    }
    
    public boolean typeEquals(short type)
    {
        return this.type == type;
    }
    

}