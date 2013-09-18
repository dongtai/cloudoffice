package apps.moreoffice.ext.database.transaction;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class TestDatabase
{

	org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean emb;

	public org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean getEmb()
	{
		return emb;
	}

	public void setEmb(
			org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean emb)
	{
		this.emb = emb;
	}
	
	public void createTable()
	{
		if (emb != null)
		{
			emb.getPersistenceProvider().createContainerEntityManagerFactory(emb.getPersistenceUnitInfo(), emb.getJpaPropertyMap());
		}
	}
	
}
