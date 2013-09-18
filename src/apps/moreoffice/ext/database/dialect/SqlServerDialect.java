package apps.moreoffice.ext.database.dialect;

import java.sql.Types;

import org.hibernate.dialect.SQLServerDialect;

/**
 * <p>
 * @see     
 * @since   web2.0
 */
public class SqlServerDialect extends SQLServerDialect
{
	private static final int MAX_LENGTH = 8000;

	public SqlServerDialect() {
		
		// As per http://www.sql-server-helper.com/faq/sql-server-2005-varchar-max-p01.aspx
		// use varchar(max) and varbinary(max) instead of TEXT and IMAGE types
		registerColumnType( Types.BLOB, "varbinary(MAX)" );
		registerColumnType( Types.VARBINARY, "varbinary(MAX)" );
		registerColumnType( Types.VARBINARY, MAX_LENGTH, "varbinary($l)" );
		registerColumnType( Types.LONGVARBINARY, "varbinary(MAX)" );
		
		registerColumnType( Types.CLOB, "varchar(MAX)" );
		registerColumnType( Types.LONGVARCHAR, "varchar(MAX)" );
		registerColumnType( Types.VARCHAR, "varchar(MAX)" );
		registerColumnType( Types.VARCHAR, MAX_LENGTH, "varchar($l)" );
		
		
		//registerFunction("row_number", new NoArgSQLFunction("row_number", StandardBasicTypes.INTEGER, true));
	}
	
	
	// 是否支持触发器
	public boolean supportTrigger()
	{
		return true;
	}
	
	// 把外界级联删除的采用,返回值[0]表示引用表，[1]表示如果原存在触发器则删除原有触发器的语句，
	// [2] 表示新建触发器的头，[3]表示触发器中间执行语句，[4]表示触发器后续执行语句。
	// 在后续使用的时候，[1]， [2] + [3] + [4]是两个完整的执行sql语句
	public String[] getDeleteTrigger(String table,	String constraintName,
			String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) 
	{
		String[] ret = new String[5];
		ret[0] = referencedTable;
		
		StringBuffer res = new StringBuffer(30);
		int size = foreignKey.length;
		for (int i = 0; i < size; i++)
		{
			res.append(" delete from ");
			res.append(table); 
			res.append(" where ");
			res.append(foreignKey[i]);
			res.append(" in (select ");
			res.append(primaryKey[i]);
			res.append(" from deleted) ");
		}
		ret[3] = res.toString();
		
		res = new StringBuffer(30);
		String triggerName = referencedTable + "_delete";
		//ret[1] = "if exists(select * from dbo.sysobjects where name='" + triggerName
		// + "' and xtype='tr' ) drop trigger " + triggerName;
		
		ret[1] = "if exists(select * from dbo.sysobjects where id = object_id(N'" + triggerName
				 + "') and OBJECTPROPERTY(id, N'IsTrigger') = 1) drop trigger " + triggerName;
		
		res.append(" create trigger ");
		res.append(triggerName);
		res.append(" on ");
		res.append(referencedTable);
		res.append(" INSTEAD OF DELETE  AS 	BEGIN SET NOCOUNT ON;");
		ret[2] = res.toString();
		
		res = new StringBuffer(30);		
		size = primaryKey.length;
		for (int i = 0; i < size; i++)
		{
			res.append(" DELETE FROM ");
			res.append(referencedTable);
			res.append(" where ");
			res.append(primaryKey[i]);
			res.append(" in (select ");
			res.append(primaryKey[i]);
			res.append(" from deleted) ");
		}
		res.append(" END");
		ret[4] = res.toString();

		return ret;
	}
	
	public boolean supportsCascadeDelete() 
	{
		return false;
	}
	
	public boolean supportsCircularCascadeDeleteConstraints() {
		// SQL Server (at least up through 2005) does not support defining
		// cascade delete constraints which can circle back to the mutating
		// table
		return false;
	}
	
}
