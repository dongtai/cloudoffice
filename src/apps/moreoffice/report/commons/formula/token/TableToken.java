package apps.moreoffice.report.commons.formula.token;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;

/**
 * 本报表字段Token
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
public class TableToken extends RToken
{
    /**
     * 构造器
     * 
     * @param tableName 表名
     * @param fieldName 字段名
     */
    public TableToken(String tableName, String fieldName)
    {
        super(FormulaCons.TABLE_VAR);
        this.tableName = tableName;
        this.fieldName = fieldName;
    }

    /**
     * @return 返回 tableName
     */
    public String getTableName()
    {
        return tableName;
    }

    /**
     * @return 返回 fieldName
     */
    public String getFieldName()
    {
        return fieldName;
    }
    
    // 表名
    private String tableName;
    // 字段名
    private String fieldName;
}