package apps.moreoffice.ext.database.dialect;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.dialect.MySQLInnoDBDialect;

/**
 * <p>
 * 数据库采用的引擎类型，根据数据库 本身的配置确定。
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class MysqlBaseDialect extends MySQLInnoDBDialect
{
	
	public MysqlBaseDialect()
	{
		super();
		// long型，采用64位记录
		registerColumnType(Types.BIGINT, "bigint(64)");
		//registerColumnType(Types.LONGVARCHAR, 10000000, "longtext");
		registerHibernateType(Types.LONGVARCHAR, Hibernate.STRING.getName());
		registerHibernateType(Types.LONGVARCHAR, Hibernate.TEXT.getName());
	}
	
	public String getTableTypeString() 
	{
		return "";
	}

	
}
