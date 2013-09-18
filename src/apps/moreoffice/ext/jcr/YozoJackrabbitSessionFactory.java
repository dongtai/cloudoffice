package apps.moreoffice.ext.jcr;

import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeTypeManager;

import org.apache.jackrabbit.api.JackrabbitNodeTypeManager;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;
import org.springmodules.jcr.JcrSessionFactory;

import apps.transmanager.weboffice.util.server.LogsUtility;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class YozoJackrabbitSessionFactory extends JcrSessionFactory
{
	private String contentType = JackrabbitNodeTypeManager.TEXT_XML;
	private Resource[] nodeDefinitions;

	protected void registerNodeTypes() throws Exception
    {    	
		try
		{
			Session session = getSession();
			if (session != null)
			{
		    	Workspace ws = session.getWorkspace();
				NodeTypeManager nodeTypeManager = ws.getNodeTypeManager();
				if (!nodeTypeManager.hasNodeType("eiokm:resource"))    // 如果已经注册过节点类型，则不在需要注册。
				{
					if (!ObjectUtils.isEmpty(nodeDefinitions)) 
					{
						JackrabbitNodeTypeManager nodeTypeManager1 = (JackrabbitNodeTypeManager) nodeTypeManager;
						for (int i = 0; i < nodeDefinitions.length; i++)
						{
							Resource resource = nodeDefinitions[i];
							nodeTypeManager1.registerNodeTypes(resource.getInputStream(), contentType);
						}
					}
				}
				session.logout();
			}
		}
		catch(Exception e)
		{
			if (!"Stack Trace".equals(e.getMessage()))
			{
				LogsUtility.error(e);
			}
		}
    }
	
	/**
	 */
	public void setNodeDefinitions(Resource[] nodeDefinitions)
	{
		this.nodeDefinitions = nodeDefinitions;
	}

	/**
	 */
	public void setContentType(String contentType) 
	{
		this.contentType = contentType;
	}
	
		
}
