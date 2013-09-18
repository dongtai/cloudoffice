package apps.moreoffice.ext.database.dialect;


/**
 * <p>
 * hibernate的myusql方言类有问题， 对于新版本5.2版本的mysql，
 * 其不支持"type=InnoDB"方式建立InnoDB表。
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class MysqlInnoDBDialect extends MysqlBaseDialect
{
	
	public MysqlInnoDBDialect()
	{
		super();
		// long型，采用64位记录
		//registerColumnType(Types.BIGINT, "bigint(64)");
		//registerColumnType(Types.LONGVARCHAR, 10000000, "longtext");
		//registerHibernateType(Types.LONGVARCHAR, Hibernate.STRING.getName());
		//registerHibernateType(Types.LONGVARCHAR, Hibernate.TEXT.getName());
	}
	
	public String getTableTypeString() 
	{
		return " ENGINE=InnoDB";
	}

	
}
