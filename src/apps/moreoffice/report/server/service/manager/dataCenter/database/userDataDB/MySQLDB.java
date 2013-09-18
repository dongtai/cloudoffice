package apps.moreoffice.report.server.service.manager.dataCenter.database.userDataDB;

import java.util.List;
import java.util.Vector;

import apps.moreoffice.report.commons.domain.constants.DataTypeCons;
import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataSource;
import apps.moreoffice.report.commons.domain.databaseObject.DataType;
import apps.moreoffice.report.server.service.manager.dataCenter.ISQLDB;
import apps.moreoffice.report.server.service.manager.dataCenter.database.connection.IConnection;
import apps.moreoffice.report.server.service.manager.dataCenter.database.connection.UserDBManager;

/**
 * mysql数据库操作类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-6
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class MySQLDB implements ISQLDB
{
    //用户数据库管理器
    private UserDBManager udbm;

    /**
     * 得到外部数据表的表字段
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @return Vector<Vector<Object>> 表名列表
     */
    public Vector<Vector<Object>> getOtherTableFields(DataSource dataSource, String tableName)
    {
        return executeQuery(dataSource, "show columns from " + tableName);
    }

    /**
     * 创建用户表
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param tableName 表名
     * @param field 字段列表，Object[]格式如下：
     *           Object[0]: 字段名(String)
     *           Object[1]: 字段类型(short)
     *           Object[2]: 是否是无符号数字(boolean),若是数值类型的字段，且要无符号的，则为true，否则为false
     *           Object[3]: 是否要非空(boolean), 若需要此字段非空，则为true，否则为false
     *           Object[4]: 默认值(String), 若有默认值，则为一个字符串，否则则设为null
     */
    public void createUserTable(DataSource dataSource, String tableName, List<Object[]> fields)
    {
        if (tableName == null || tableName.length() < 1 || fields == null || fields.size() < 1)
        {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE ");
        sb.append(tableName);
        sb.append(" (id bigint(20) unsigned NOT NULL auto_increment,");
        addField(sb, fields, 0);
        sb.append(",PRIMARY KEY (id)");
        sb.append(") ENGINE=InnoDB CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';");
        executeUpdate(dataSource, sb.toString());
    }

    /**
     * 判断用户表是否是空表
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @return boolean 是否是空表
     */
    public boolean isNullTable(DataSource dataSource, String tableName)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("select count(*) from ");
        sb.append(tableName);
        Vector<Vector<Object>> aa = executeQuery(dataSource, sb.toString());
        if (aa != null && aa.get(0) != null && ((Long)aa.get(0).get(0)).longValue() == 0)
        {
            return true;
        }
        return false;
    }

    /**
     * 删除用户表
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     */
    public void deleteUserTable(DataSource dataSource, String tableName)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("DROP TABLE ");
        sb.append(tableName);
        executeUpdate(dataSource, sb.toString());
    }

    /**
     * 向已有表中添加字段
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param fields 字段列表(见创建表字段格式)
     */
    public void insertField(DataSource dataSource, String tableName, List<Object[]> fields)
    {
        if (tableName == null || tableName.length() < 1 || fields == null || fields.size() < 1)
        {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("ALTER TABLE ");
        sb.append(tableName);
        addField(sb, fields, 1);
        sb.append(SEMICOLON);
        executeUpdate(dataSource, sb.toString());
    }

    /**
     * 修改字段
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param fields 字段列表(见创建表字段格式)
     */
    public void modifyField(DataSource dataSource, String tableName, List<Object[]> fields)
    {
        if (tableName == null || tableName.length() < 1 || fields == null || fields.size() < 1)
        {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("ALTER TABLE ");
        sb.append(tableName);
        addField(sb, fields, 2);
        sb.append(SEMICOLON);
        executeUpdate(dataSource, sb.toString());
    }

    /**
     * 删除字段
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param fieldName 字段名
     */
    public void deleteField(DataSource dataSource, String tableName, String fieldName)
    {
        if (tableName == null || tableName.length() < 1 || fieldName == null
            || fieldName.length() < 1 || fieldName.toLowerCase().equals(ID))
        {
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" DROP COLUMN ");
        sb.append(fieldName);
        sb.append(SEMICOLON);
        executeUpdate(dataSource, sb.toString());
    }

    /*
     * 组成sql语句
     */
    private void addField(StringBuffer sb, List<Object[]> field, int operateType)
    {
        if (field == null || field.size() == 0)
        {
            return;
        }
        int len = field.size();
        Object[] tem;
        for (int i = 0; i < len; i++)
        {
            if (operateType == 2)
            {
                sb.append(" MODIFY ");
            }
            else if (operateType == 1)
            {
                sb.append(" ADD ");
            }

            tem = field.get(i);
            sb.append(tem[0].toString() + " ");
            sb.append(getTypeStr((DataType)tem[1]));
            if (((Boolean)tem[2]).booleanValue())
            {
                sb.append("unsigned ");
            }
            if (((Boolean)tem[3]).booleanValue())
            {
                sb.append("NOT NULL ");
            }
            if (tem[4] != null)
            {
                sb.append("default '" + tem[4].toString() + "'");
            }
            if (i != len - 1)
            {
                sb.append(",");
            }
        }
    }

    /**
     * 插入记录
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param fields 字段列表
     * @param values 值列表
     * @return long 记录ID
     */
    public long insertRecord(DataSource dataSource, String tableName, String[] fields,
        Object[] values)
    {
        if (fields == null || values == null || fields.length < 1 || fields.length != values.length)
        {
            return -1;
        }

        StringBuffer sb = getSQL("insert into ", tableName, fields, values);
        if (sb.toString().endsWith("set "))
        {
            return -1;
        }
        sb.append(SEMICOLON);
        executeUpdate(dataSource, sb.toString());
        Vector<Vector<Object>> id = executeQuery(dataSource, "select LAST_INSERT_ID();");
        if (id != null && id.get(0) != null && id.get(0).get(0) instanceof Long)
        {
            return (Long)id.get(0).get(0);
        }

        return -1;
    }

    /**
     * 修改记录
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param id 记录ID
     * @param fields 字段列表
     * @param values 值列表
     */
    public void modifyRecord(DataSource dataSource, String tableName, long id, String[] fields,
        Object[] values)
    {
        if (id < 1 || fields == null || values == null || fields.length < 1
            || fields.length != values.length)
        {
            return;
        }

        StringBuffer sb = getSQL("update ", tableName, fields, values);
        sb.append(" where id = ");
        sb.append(id);
        sb.append(SEMICOLON);
        executeUpdate(dataSource, sb.toString());
    }

    /*
     * 得到基本的SQL
     */
    private StringBuffer getSQL(String operate, String tableName, String[] fields, Object[] values)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(operate);
        sb.append(tableName);
        sb.append(" set ");
        for (int i = 0, size = fields.length; i < size; i++)
        {
            if (values[i] == null)
            {
                continue;
            }
            sb.append(fields[i]);
            sb.append(" = ");
            sb.append(dealWithDataValue(values[i]));
            sb.append(",");
        }
        if (sb.toString().endsWith(","))
        {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb;
    }

    /*
     * 字段值如果是字符串或日期时间，要用单引号“'”包起来。
     * 换句话说，除了数值，都要用单引号“'”包起来
     */
    private String dealWithDataValue(Object value)
    {
        if (value == null)
        {
            return null;
        }
        if (value instanceof Number)
        {
            return value.toString();
        }
        return "'" + value.toString() + "'";
    }

    /**
     * 得到记录
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param ids 记录ID数组
     * @param fieldNames 字段名列表
     * @param sortFieldName 排序字段
     * @param sortType 排序类型
     * @return Vector<Vector<Object>> 记录值
     */
    public Vector<Vector<Object>> getRecord(DataSource dataSource, String tableName,
        String[] fieldNames, long[] ids, String sortFieldName, byte sortType)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        for (int i = 0, size = fieldNames.length; i < size; i++)
        {
            if (fieldNames[i] == null || fieldNames[i].length() < 1)
            {
                continue;
            }
            sb.append(fieldNames[i]);
            if (i != size - 1)
            {
                sb.append(",");
            }
            sb.append(" ");
        }
        sb.append("from ");
        sb.append(tableName);
        sb.append(" where");
        for (int i = ids.length - 1; i >= 0; i--)
        {
            sb.append(" id = ");
            sb.append(ids[i]);
            if (i != 0)
            {
                sb.append(" OR");
            }
        }
        if (!(sortFieldName == null || sortFieldName.equals("") || sortType == TableCons.DEFAULT))
        {
            sb.append(" ORDER BY ");
            sb.append(sortFieldName);
            if (sortType == TableCons.ASC)
            {
                sb.append(" ASC");
            }
            else if (sortType == TableCons.DESC)
            {
                sb.append(" DESC");
            }
        }
        sb.append(SEMICOLON);
        return executeQuery(dataSource, sb.toString());
    }

    /**
     * 删除记录
     * 
     * @param dataSource 数据源
     * @param tableName 表名
     * @param id 记录id
     */
    public void deleteRecord(DataSource dataSource, String tableName, long id)
    {
        String sql = "delete from " + tableName + " where id = " + id + SEMICOLON;
        executeUpdate(dataSource, sql);
    }

    /*
     * 根据数据类型得到数据库创建时的数据类型
     */
    private static String getTypeStr(DataType dataType)
    {
        String str = "varchar(64) ";
        if (dataType != null)
        {
            int type = dataType.getBasicType();
            switch (type)
            {
                case DataTypeCons.TEXT_TYPE:
                    // TODO 限长
                    break;
                case DataTypeCons.NUMBER_TYPE:
                    // TODO 限长 小数
                    break;
                case DataTypeCons.DATE_TYPE:
                    str = "date ";
                    break;
                case DataTypeCons.PICTURE_TYPE:
                    str = "blob ";
                    break;
                case DataTypeCons.FILE_TYPE:
                    str = "blob ";
                    break;
                default:
                    break;
            }
        }
        return str;
    }

    /**
     * 执行创建、更新、删除语句
     * 
     * @param dataSource 数据源
     * @param sql sql语句
     */
    public void executeUpdate(DataSource dataSource, String sql)
    {
        IConnection con = dataSource == null ? udbm.getDefaultConnection() : udbm
            .getConnection(dataSource.getName());
        con.executeUpdate(sql);
    }

    /**
     * 执行查询语句
     * 
     * @param dataSource 数据源
     * @param sql sql语句 
     * @return ResultSet 结果集
     */
    public Vector<Vector<Object>> executeQuery(DataSource dataSource, String sql)
    {
        IConnection con = dataSource == null ? udbm.getDefaultConnection() : udbm
            .getConnection(dataSource.getName());
        return con.executeQuery(sql);
    }

    /**
     * @param udbm 设置 udbm
     */
    public void setUdbm(UserDBManager udbm)
    {
        this.udbm = udbm;
    }

    private static String ID = "id";
    private static String SEMICOLON = ";";
}