package apps.moreoffice.ext.spring;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import apps.transmanager.weboffice.util.server.JSONTools;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class PropertyPlaceHoders extends PropertyPlaceholderConfigurer
{

	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException
	{
		String json = System.getenv("VCAP_SERVICES");
		if (json != null && json.length() > 0)
		{
			Object o = JSONTools.convertParams(json);
			Object temp;
			if (o instanceof Map)
			{
				temp = ((Map)o).get("mysql-5.1");
				if (temp instanceof List)
				{
					temp = ((List)temp).get(0);				
					if (temp instanceof Map)
					{
						Map temp1;
						String temp2;
						temp1 = (Map)((Map)temp).get("credentials");
						temp2 = "jdbc:mysql://" + (String)temp1.get("host") + ":" + temp1.get("port") + "/" + temp1.get("name");
						props.put("jdbc-0.proxool.driver-url", temp2);
						temp2 = (String)temp1.get("user");
						props.put("jdbc-0.user", temp2);
						temp2 = (String)temp1.get("password");
						props.put("jdbc-0.password", temp2);
						
					}
				}
			}
		}
		String temp = System.getenv("OPENSHIFT_DB_HOST");
		if (temp != null && temp.length() > 0)
		{
			temp = "jdbc:mysql://" + temp + ":" + System.getenv("OPENSHIFT_DB_PORT") + "/" + System.getenv("OPENSHIFT_APP_NAME");
			props.put("jdbc-0.proxool.driver-url", temp);
			temp = System.getenv("OPENSHIFT_DB_USERNAME");
			props.put("jdbc-0.user", temp);
			temp = System.getenv("OPENSHIFT_DB_PASSWORD");
			props.put("jdbc-0.password", temp);
		}
		
		super.processProperties(beanFactoryToProcess, props);
	}
}
