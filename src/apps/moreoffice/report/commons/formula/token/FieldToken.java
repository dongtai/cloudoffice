package apps.moreoffice.report.commons.formula.token;

import apps.moreoffice.report.commons.formula.constants.FormulaCons;

/**
 * 非本报表字段Token
 * 
 * <p>
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
@ SuppressWarnings("serial")
public class FieldToken extends RToken
{
    /**
     * 构造器
     * 
     * @param databaseName 库名
     * @param tableName 表名
     * @param fieldName 字段名
     */
    public FieldToken(String databaseName, String tableName, String fieldName)
    {
        super(FormulaCons.FIELD_VAR);
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.fieldName = fieldName;
    }

    /**
     * 构造器
     *
     * @param tableName 表名
     * @param fieldName 字段名
     */
    public FieldToken(String tableName, String fieldName)
    {
        this(null, tableName, fieldName);
    }
    
    /**
     * @return 返回 databaseName
     */
    public String getDatabaseName()
    {
        return databaseName;
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

    // 库名
    private String databaseName;
    // 表名
    private String tableName;
    // 字段名
    private String fieldName;
}