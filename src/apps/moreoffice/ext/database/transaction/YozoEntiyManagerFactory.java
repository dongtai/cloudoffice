package apps.moreoffice.ext.database.transaction;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class YozoEntiyManagerFactory extends LocalContainerEntityManagerFactoryBean
{

	private TestDatabase td;

	public TestDatabase getTd()
	{
		return td;
	}

	public void setTd(TestDatabase td)
	{
		this.td = td;
		td.setEmb(this);
	}
	
	
	
}
