package apps.moreoffice.report.commons.domain.databaseObject;

import java.util.HashMap;

import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 数据规范：系统变量
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-6
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class SysVarRule extends DataRule
{
    // 序列化ID
    private static final long serialVersionUID = -745699785818212821L;

    /**
     * 构造器
     */
    public SysVarRule()
    {
        setType(DataRuleCons.SYSVAR);
    }

    /**
     * 得到类型名称
     * 
     * @return String 类型名称
     */
    public String getTypeName()
    {
        return ReportCommonResource.SYSVAR;
    }

    /**
     * 对象克隆
     * 
     * @param isSimple 是否简化处理
     * @return 返回克隆的对象
     */
    public SysVarRule clone(boolean isSimple)
    {
        SysVarRule sysVarRule = new SysVarRule();
        clone(sysVarRule, isSimple);
        return sysVarRule;
    }

    /**
     * 得到json格式的HashMap对象
     * 
     * @return HashMap<String, Object> json格式的HashMap对象
     */
    public HashMap<String, Object> getJsonObj()
    {
        return super.getJsonObj();
    }

    /**
     * 根据json参数得到对象
     * 
     * @param paramsMap json参数
     * @return SysVarRule 系统变量对象
     */
    public SysVarRule convetJsonToObj(HashMap<String, Object> paramsMap)
    {
        return (SysVarRule)super.convetJsonToObj(paramsMap);
    }
}