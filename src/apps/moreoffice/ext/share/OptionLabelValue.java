package apps.moreoffice.ext.share;

/**
 *
 * @author  sunah
 */
import java.io.Serializable;

public class OptionLabelValue implements Serializable
{
    
    /** Creates a new instance of OptionLabelValue */
    private String label=null;
    private int value=0;
    private String value2=null;
    public OptionLabelValue(String label,int value)
    {
        this.label=label;
        this.value=value;
    }
    public OptionLabelValue(String label,String value2)
    {
        this.label=label;
        this.value2=value2;
    }
    public String getLabel()
    {
        return label;
    }
    public int getValue()
    {
        return value;
    }
    public String getValue2()
    {
        return value2;
    }
    public String toString()
    {
        StringBuffer sb=new StringBuffer("OptionLabelValue[");
        sb.append(this.label);
        sb.append(",");
        sb.append(this.value);
        sb.append("]");
        return (sb.toString());
    }
    public OptionLabelValue() 
    {
    }
}
