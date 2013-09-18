package apps.moreoffice.ext.jpa;

import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.Environment;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * hibernate的jap类有问题。主要是原有的类对数据库的方言是
 * 定义死了，只支持那几种数据库。
 * 该类修改为数据库的方言由配置方言的class来确定，这样对于新增加
 * 的数据库，就能够很好的支持。
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */
public class WebHibernateJpaVendorAdapter extends HibernateJpaVendorAdapter
{
	private String dialect ;
	
	public String getDialect()
	{
		return dialect;
	}

	public void setDialect(String dialect)
	{
		this.dialect = dialect;
	}

	public Map getJpaPropertyMap()
	{
		Properties jpaProperties = new Properties();

		if (getDatabasePlatform() != null)
		{
			jpaProperties.setProperty(Environment.DIALECT,
					getDatabasePlatform());
		}
		else if (getDatabase() != null)
		{
			Class databaseDialectClass = determineDatabaseDialectClass(getDatabase());
			if (databaseDialectClass == null)
			{
				databaseDialectClass = determineDatabaseDialectClassByDialect();
			}
			if (databaseDialectClass != null)
			{
				jpaProperties.setProperty(Environment.DIALECT,	databaseDialectClass.getName());
			}
		}

		if (isGenerateDdl())
		{
			jpaProperties.setProperty(Environment.HBM2DDL_AUTO, "update");
		}
		if (isShowSql())
		{
			jpaProperties.setProperty(Environment.SHOW_SQL, "true");
		}

		return jpaProperties;
	}
	
	/**
	 */
	private Class determineDatabaseDialectClassByDialect()
	{
		if (dialect != null)
		{
			try
			{
				return Class.forName(dialect);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

}
