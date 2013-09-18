package apps.transmanager.weboffice.service.jcr;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.jcr.AccessDeniedException;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.servlet.http.HttpServletRequest;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.util.ISO8601;
import org.apache.jackrabbit.util.ISO9075;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

import apps.transmanager.weboffice.constants.both.FileSystemCons;
import apps.transmanager.weboffice.domain.DataHolder;
import apps.transmanager.weboffice.domain.FileConstants;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.domain.Versioninfo;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.objects.FileArrayComparator;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.FileSystemService.Shareinfo;
import apps.transmanager.weboffice.service.server.PermissionService;
import apps.transmanager.weboffice.service.sysreport.SysMonitorTask;
import apps.transmanager.weboffice.util.both.FlagUtility;
import apps.transmanager.weboffice.util.both.MD5;
import apps.transmanager.weboffice.util.server.LogsUtility;

/**
 * 文件注释
 * <p>
 * <p>
 * <p>
 */

@Component(value=JCRService.NAME)
public class JCRService
{
	public static final String NAME = "jcrService";
	private static final String FILE_LIST = FileConstants.RECENT;   //"fileList";//usr753 add 用来做最近文档处理
    private final static String REGEX = "[@/:|*\\[\\]]";     // 过滤掉JCR中不支持的字符内容。(“/”, “:”, “[“, “]”, “|”, “*”)
    private final static String REPLACE = "_";
    private final static String[] NULLSTRING = new String[]{""};
    //public final static String COMPANY_ROOT = "group_company_1348799310312";     //  企业文库做法改变，临时这样处理吧。
    @Autowired
    JcrTemplate jcrTemplate;
    private ThumbnailProduct tpt; 
    
    public JCRService()
    {
    	 tpt = new ThumbnailProduct();
    	 //tpt.start();
    }

    public void setjcrTemplate(JcrTemplate jcrTemplate)
    {
        this.jcrTemplate = jcrTemplate;
        this.jcrTemplate.setAllowCreate(true);
    }

    /*public void changGrou()
    {
    	jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
            	NodeIterator iter = rootNode.getNodes();
            	while (iter.hasNext())
				{
					Node tempNode = iter.nextNode();
					String path = tempNode.getPath();
					String newpath = path.substring(1);
					//if (newpath.startsWith(FileConstants.GROUP_ROOT))
					if (newpath.startsWith("group_company_1348799310312"))
					{
						//newpath = newpath.replace(FileConstants.GROUP_ROOT, FileConstants.TEAM_ROOT);
						newpath = newpath.replace("group_company_1348799310312", "company_yozo_1348799310312");
						session.move(path,  "/" + newpath);
					}
				}
            	session.save();
            	return null;
            }
        });
    }*/
    /**
     * 创建所谓的企业文库。
     */
//    public void createCompanySpace()
//    {
//    	init(COMPANY_ROOT);
//    }
    
    /**
     * 登录空间，如果该空间不存在，则会创建一个空间。
     * @param spaceUID
     * @param password
     * @throws IOException
     * @throws LoginException
     * @throws RepositoryException
     * @throws InvalidNodeTypeDefException
     */
    public void login(String spaceUID, String password)
    {	
    	//init(COMPANY_ROOT);     //  企业文库做法改变，临时这样处理吧。
    	if (spaceUID == null)
    	{
    		return ;
    	}
    	init(spaceUID);
    	createAuditSpace(spaceUID);
    	reinitProperty(spaceUID);
    }
    private String reinitProperty(final String spaceUID)
	{
	    return (String)jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	        	try
	        	{
		            Node docNode = session.getRootNode().getNode(spaceUID.concat("/").concat(FileConstants.DOC));
		    /////////临时处理方法，在导入文件时要创建这些属性
		            if (!docNode.hasProperty(FileConstants.OPENLIST))
		            {
		            	docNode.setProperty(FileConstants.OPENLIST,NULLSTRING);
		            }
		            if (!docNode.hasProperty(FileConstants.CLOSELIST))
		            {
		            	docNode.setProperty(FileConstants.CLOSELIST, NULLSTRING);
		            }
		            if (!docNode.hasProperty(FileConstants.SAVELIST))
		    		{
		            	docNode.setProperty(FileConstants.SAVELIST, NULLSTRING);
		    		}
		            /////////临时处理方法，在导入文件时要创建这些属性
		            session.save();
	        	}
	        	catch (Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	            return null;
	        }
	    });
	}
    
    /**
     * 创建审核空间。
     * @param spaceUID
     */
    private void createAuditSpace(final String spaceUID)
    {
    	jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
		        Node node;
		        String path = FileConstants.AUDIT_ROOT;
		        if (!rootNode.hasNode(path))
		        {
		        	node = rootNode.addNode(path, FileConstants.NODE_FOLDER);
		        	createRootFolder(node, FileConstants.DOC, path);
		        	node.addNode(FileConstants.PUBLISHMENTS, FileConstants.NODE_FOLDER);
		        	node.addNode(FileConstants.ARCHIVES, FileConstants.NODE_FOLDER);
		        }
		        else
		        {
		        	node = rootNode.getNode(path);
		        	if (!node.hasNode(FileConstants.DOC))    // 没有必要的兼容处理
		        	{
		        		createRootFolder(node, FileConstants.DOC, path);
		        	}
		        }
		        if (!node.hasNode(spaceUID))
		        {
		        	node.addNode(spaceUID, FileConstants.NODE_FOLDER);		        	
		        }
		        session.save();
		        return null;
            }            
        });
    }
    
    /**
     * 删除空间。 
     * @param name
     */
	public void deleteSpace(final List<String> spaceUIDs)
	{
		jcrTemplate.execute(new JcrCallback()
		{
			public Object doInJcr(Session session) throws RepositoryException
			{
				try
				{
					Node rootNode = session.getRootNode();
					for(String temp : spaceUIDs)
					{
						Node nd = rootNode.getNode(temp);
						nd.remove();
					}
					session.save();
				}
				catch (Exception e)
				{
					LogsUtility.error(e);
				}
				return null;
			}
		});
	}
	
    /**
     * 创建空间,返回空间在文件库中的真实名字。 
     * @param name
     */
	public String createSpace(final String names)
	{
		Object o = jcrTemplate.execute(new JcrCallback()
		{
			public Object doInJcr(Session session) throws RepositoryException
			{
				try
				{
					String name = filterInvalidChar(names);//name.replaceAll(REGEX, REPLACE);
					String temp = name + "_" + System.currentTimeMillis();
					int i = 0;
					while (session.itemExists("/" + temp)) // 如果存在，则重新获取名字
					{
						temp = name + "_" + System.currentTimeMillis() + i;
						i++;
					}
					init(temp);
					return temp;

				}
				catch (Exception e)
				{
					LogsUtility.error(e);
				}
				return "";
			}
		});
		return (String) o;
	}
    	
	/**
	 * 创建各个空间的根目录
	 * @param root
	 * @param type
	 * @param spaceUID
	 */
	private void createRootFolder(Node root, String type, String spaceUID)
	{
		try
		{
			Node docHome = root.addNode(type, FileConstants.NODE_FOLDER);
	        docHome.setProperty(FileConstants.AUTHOR, spaceUID);
	        // 下面内容应该可以不需要。
	        docHome.setProperty(FileConstants.NOTIFICATION, new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});
	        docHome.setProperty(FileConstants.OPENLIST, NULLSTRING);
	        docHome.setProperty(FileConstants.CLOSELIST, NULLSTRING);
	        docHome.setProperty(FileConstants.SAVELIST, NULLSTRING);
		}
		catch(Exception e)
		{
			LogsUtility.error(e);
		}
	}
	
    public void init(final String spaceUID)
    {
    	jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
		        Node userHome = null;
		        if (!session.itemExists("/" + spaceUID))
		        {
		            try
		            {
		                Node rootNode = session.getRootNode();
		                // 后续删除
		                if (!rootNode.hasProperty(FileConstants.LOCKLIST))
		                {
		                    rootNode.setProperty(FileConstants.LOCKLIST, NULLSTRING);
		                }		
		                // 后续删除
		                if (FileConstants.WORKFLOW.equals(spaceUID))
		                {
		                    session.getRootNode().addNode(FileConstants.WORKFLOW, "nt:folder");
		                    session.save();
		                    return null;
		                }
		                // 创建空间的根
		                userHome = session.getRootNode().addNode(spaceUID, "nt:folder");
		                // 创建文档的根
		                createRootFolder(userHome, FileConstants.DOC, spaceUID);		                
		                // 创建回收站
		                createRootFolder(userHome, FileConstants.TRASH, spaceUID);
		                //  创建最近操作列表
		                createRootFolder(userHome, FileConstants.RECENT, spaceUID);
		                // 创建配置信息
		                createRootFolder(userHome, FileConstants.CONF, spaceUID);
		                
		               // 创建配置信息
		                createRootFolder(userHome, FileConstants.TEMPLATE, spaceUID);
		                
		                session.save();
		            }
		            catch(Exception e)
		            {
		            	LogsUtility.error(e);
		            }
		        }
		        else
		        {
		            //userHome = session.getRootNode().getNode(userID);
		        }
		        return null;
            }
        });
    }
    
    /**
     * 判断path所需要的空间是否存在，如果不存在，则根据创建。
     * @param session
     * @param path
     * @return
     */
    private boolean isSpaceExist(Session session, String path)
    {
    	try
    	{
	    	if (!session.getRootNode().hasNode(path))
			{
				int index = path.indexOf("/");
				String tempP;
				if (index >= 0)
				{
					tempP = path.substring(0, index);
				}
				else
				{
					tempP = path;
				}
				init(tempP);
			}
	    	return true;
    	}
    	catch(Exception e)
    	{
    		LogsUtility.error(e);
    	}
    	return false;
    }   
    
    /**
     * 设置用户当前以写方式打开的文档，该打开的文件包括个人空间的，他人共享的，其他空间的文件。
     * 该记录记录在用户根目录下的recent节点上的openlist属性中。
     * 每次在用户登录的时候，该节点中的该属性会被清空（非正常退出才会有值，如果是正常退出系统应该没有值）。
     * @param userSpaceUID 用户的spaceUID值
     * @param path 操作的文件全路径
     */
    public void addUserOpenFile(final String userSpaceUID, final String path)
    {
    	jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node root = session.getRootNode();
            	Node recentNode = root.getNode(userSpaceUID + "/" + FileConstants.RECENT);
		    	if(recentNode != null)
				{
					Value[] values = recentNode.getProperty(FileConstants.OPENLIST).getValues();
					List<String> array = new ArrayList<String>();
					int count = values.length;
			        for (int i = 0; i < count; i++)
			        {
			        	String temp = values[i].getString();
			        	if (temp != null && temp.length() > 0)
			        	{
			        		array.add(temp);
			        	}
			        }
			        if (array.contains(path))
			        {
			        	array.remove(path); 
			        }
			        array.add(0, path);
			        String[] listFile = array.toArray(new String[array.size()]);
			        recentNode.setProperty(FileConstants.OPENLIST, listFile);
			        session.save();
				}
		    	return null;
            }
        });    	
    }
    
    /**
     * 删除用户当前以写方式打开的所有文档，该打开的文件包括个人空间的，他人共享的，其他空间的文件。
     * 该记录记录在用户根目录下的recent节点上的openlist属性中。
     * 用户正常退出系统后，该节点中的该属性会被清空。
     * 每次在用户登录的时候，该节点中的该属性也会被清空（非正常退出才会有值，如果是正常退出系统应该没有值）。
     * @param userSpaceUID 用户的spaceUID值
     */
    public void removeUserOpenFile(final String userSpaceUID)
    {
    	jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node root = session.getRootNode();
            	Node recentNode = root.getNode(userSpaceUID + "/" + FileConstants.RECENT);
		    	if(recentNode != null)
				{					
			        recentNode.setProperty(FileConstants.OPENLIST, NULLSTRING);
			        session.save();
				}
		    	return null;
            }
        });    	
    }
    
    /**
     * 删除用户当前以写方式打开的文档，该打开的文件包括个人空间的，他人共享的，其他空间的文件。
     * 该记录记录在用户根目录下的recent节点上的openlist属性中。
     * 用户正常退出系统后，该节点中的该属性会被清空。
     * 每次在用户登录的时候，该节点中的该属性也会被清空（非正常退出才会有值，如果是正常退出系统应该没有值）。
     * @param userSpaceUID 用户的spaceUID值
     */
    public void removeUserOpenFile(final String userSpaceUID, final String path)
    {
    	jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node root = session.getRootNode();
            	Node recentNode = root.getNode(userSpaceUID + "/" + FileConstants.RECENT);
		    	if(recentNode != null)
				{
					Value[] values = recentNode.getProperty(FileConstants.OPENLIST).getValues();
					List<String> array = new ArrayList<String>();
					int count = values.length;
			        for (int i = 0; i < count; i++)
			        {
			        	String temp = values[i].getString();
			        	if (temp != null && temp.length() > 0)
			        	{
			        		array.add(temp);
			        	}
			        }
			        if (array.contains(path))
			        {
			        	array.remove(path); 
			        }
			        String[] listFile = array.toArray(new String[array.size()]);
			        recentNode.setProperty(FileConstants.OPENLIST, listFile);
			        session.save();
				}
		    	return null;
            }
        });    	
    }
    
    /**
     * 判断用户当前是否以写方式打开的文档，该打开的文件包括个人空间的，他人共享的，其他空间的文件。
     * 该记录记录在用户根目录下的recent节点上的openlist属性中。
     * @param userSpaceUID 用户的spaceUID值
     * @param path 操作的文件全路径
     */
    public boolean isUserOpenFile(final String userSpaceUID, final String path)
    {
    	return (Boolean)jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node root = session.getRootNode();
            	Node recentNode = root.getNode(userSpaceUID + "/" + FileConstants.RECENT);
		    	if(recentNode != null)
				{
					Value[] values = recentNode.getProperty(FileConstants.OPENLIST).getValues();
					int count = values.length;
					boolean flag;
			        for (int i = 0; i < count; i++)
			        {
			        	String temp = values[i].getString();
			        	if (temp != null && temp.length() > 0)
			        	{
			        		flag = temp.equals(path);
			        		if (flag)
			        		{
			        			return true;
			        		}
			        	}
			        }
			        return false;
				}
		    	return false;
            }
        });    	
    }
    
    /**
     *  在用户退出登录后，清除在空间节点中（个人、其他空间等）中文件由个人用户已经打开的文件的所有标记。
     * @param userName 打开文件的用户（登录名）
     * @param path
     * @throws ValueFormatException
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
    public void removeUserAllOpenedFile(final String userName, final String userSpaceUID)   throws ValueFormatException, PathNotFoundException, RepositoryException
	{
    	try
    	{
		    jcrTemplate.execute(new JcrCallback()
		    {
		        public Object doInJcr(Session session) throws RepositoryException
		        {
		        	Node root = session.getRootNode();
	            	Node recentNode = root.getNode(userSpaceUID + "/" + FileConstants.RECENT);
			    	if(recentNode != null)
					{
						Value[] values = recentNode.getProperty(FileConstants.OPENLIST).getValues();
						int count = values.length;
						String spaceUID;
						Node docNode;
						Value[] tempValue;
				        for (int i = 0; i < count; i++)     // 用户打开文件记录
				        {
				        	String temp = values[i].getString();
				        	if (temp != null && temp.length() > 0)
				        	{
					        	spaceUID = FileUtils.getPreName(temp);
					        	docNode = null;
					        	try
					        	{
					        		docNode = root.getNode(spaceUID.concat("/").concat(FileConstants.DOC));
					        	}
					        	catch(Exception ee)
					        	{
					        		LogsUtility.error(ee);
					        	}
					        	if (docNode == null)
					        	{
					        		continue;
					        	}
					            tempValue = docNode.getProperty(FileConstants.OPENLIST).getValues();
					            ArrayList<String> array = new ArrayList<String>();
					            String key;
					            String value;
					            String tempName;
					            int index;
					            boolean changFlag = false;
					            // 打开列表记录格式为“文件全路径|打开者”
					            for (int k = 0; k < tempValue.length; k++)
					            {
					            	value = tempValue[k].getString();
					                if ((index = value.indexOf("|")) > 0)
					                {
					                	key = value.substring(0, index);
					                	tempName = value.substring(index + 1);
					                }
					                else
					                {
					                	key = value;
					                	tempName = "";
					                }
					                if (temp.equals(key) && (userName.equals(tempName) || "".equals(tempName)))
					                {
					                	changFlag = true;
					                }
					                else    // 不属于清空的重新存回去。
					                {
					                	array.add(value);	                	
					                }
					            }
					            if (changFlag)    // 有改变才存
					            {
						            String[] listFile = array.toArray(new String[array.size()]);
						            docNode.setProperty(FileConstants.OPENLIST, listFile);
						            session.save();
					            }
				        	}
				        }
				        recentNode.setProperty(FileConstants.OPENLIST, NULLSTRING);
				        session.save();
					}
		            return null;
		        }
		    });
    	}
    	catch(Exception e)
    	{
    		LogsUtility.error(e);
    	}
	}    
    
    /**
     * 在空间节点中（个人、其他空间等）中设置文件已经打开标记。
     * 该记录在空间节点根目录下的Document节点上的openlist属性中。
     * @param userName 打开文件的用户（登录名）
     * @param path
     * @throws ValueFormatException
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
    public void addOpenedFile(final String userName, final String path)    throws ValueFormatException, PathNotFoundException, RepositoryException
	{
	    jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	        	final String spaceUID = FileUtils.getPreName(path);
	            Node docNode = session.getRootNode().getNode(spaceUID.concat("/").concat(FileConstants.DOC));
	            Value[] values = docNode.getProperty(FileConstants.OPENLIST).getValues();
	            HashMap<String, String> array = new HashMap<String, String>();
	            String key;
	            String value;
	            int index;
	            // 打开列表记录格式为“文件全路径|打开者”
	            for (int i = 0; i < values.length; i++)
	            {
	            	value = values[i].getString();
	                if ((index = value.indexOf("|")) > 0)
	                {
	                	key = value.substring(0, index);
	                }
	                else
	                {
	                	key = value;
	                }
	                array.put(key, value);
	            }
	            if (array.get(path) == null)    // 目前只支持一个文档只有一个人写打开
	            {
	                array.put(path, path + "|" + userName);
	                String[] listFile = array.values().toArray(new String[array.size()]);
	                docNode.setProperty(FileConstants.OPENLIST, listFile);
	                session.save();
	            }
	            return null;
	        }
	    });
	}
    
    /**
     *  清除在空间节点中（个人、其他空间等）中文件已经打开标记。
     *  该记录在空间节点根目录下的Document节点上的openlist属性中。
     * @param userName 打开文件的用户（登录名）
     * @param path
     * @throws ValueFormatException
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
    public void removeOpenedFile(final String userName,  final String path)   throws ValueFormatException, PathNotFoundException, RepositoryException
	{
	    jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	        	final String spaceUID = FileUtils.getPreName(path);
	            Node docNode = session.getRootNode().getNode(spaceUID.concat("/").concat(FileConstants.DOC));
	            Value[] values = docNode.getProperty(FileConstants.OPENLIST).getValues();
	            ArrayList<String> array = new ArrayList<String>();
	            String key;
	            String value;
	            String tempName;
	            int index;
	            boolean changFlag = false;
	            // 打开列表记录格式为“文件全路径|打开者”
	            for (int i = 0; i < values.length; i++)
	            {
	            	value = values[i].getString();
	                if ((index = value.indexOf("|")) > 0)
	                {
	                	key = value.substring(0, index);
	                	tempName = value.substring(index + 1);
	                }
	                else
	                {
	                	key = value;
	                	tempName = "";
	                }
	                if (path.equals(key) && (userName.equals(tempName) || "".equals(tempName)))
	                {
	                	changFlag = true;
	                }
	                else    // 不属于清空的重新存回去。
	                {
	                	array.add(value);	                	
	                }
	            }
	            if (changFlag)    // 有改变才存
	            {
		            String[] listFile = array.toArray(new String[array.size()]);
		            docNode.setProperty(FileConstants.OPENLIST, listFile);
		            session.save();
	            }
	            return null;
	        }
	    });
	}
    
    /**
     * 判断 在空间节点中（个人、其他空间等）中文件是否已经处于打开状态。
     * 该记录在空间节点根目录下的Document节点上的openlist属性中。
     * @param path
     * @return 返回被写打开的用户名（登录名）
     * @throws ValueFormatException
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
    public String isFileOpened(final String path)  throws ValueFormatException, PathNotFoundException, RepositoryException
	{
	    return (String)jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	        	final String spaceUID = FileUtils.getPreName(path);
	            Node docNode = session.getRootNode().getNode(spaceUID.concat("/").concat(FileConstants.DOC));

	            if (!docNode.hasProperty(FileConstants.OPENLIST))
	            {
	            	docNode.setProperty(FileConstants.OPENLIST,NULLSTRING);
	            }
	            
	            Value[] values = docNode.getProperty(FileConstants.OPENLIST).getValues();
	            String key;
	            String value;
	            int index;
	            // 打开列表记录格式为“文件全路径|打开者”
	            for (int i = 0; i < values.length; i++)
	            {
	            	value = values[i].getString();
	                if ((index = value.indexOf("|")) > 0)
	                {
	                	key = value.substring(0, index);
	                	if (path.equals(key))
		                {
		                	return value.substring(index + 1);
		                }
	                }                
	            }
	            return null;
	        }
	    });
	}
    
    /**
     * 判断 在空间节点中（个人、其他空间等）中在给定path中是否有文件已经处于打开状态。
     * 该记录在空间节点根目录下的Document节点上的openlist属性中。
     * @param path
     * @return 被写打开文件全路径|文件打开者
     * @throws ValueFormatException
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
    public List<String> getFileOpened(final String path)  throws ValueFormatException, PathNotFoundException, RepositoryException
	{
	    Object ret = jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	        	final String spaceUID = FileUtils.getPreName(path);
	            Node docNode = session.getRootNode().getNode(spaceUID.concat("/").concat(FileConstants.DOC));
	            Value[] values = docNode.getProperty(FileConstants.OPENLIST).getValues();	
	            String key;
	            String value;
	            int index;
	            ArrayList<String> ret = new ArrayList<String>();
	            // 打开列表记录格式为“文件全路径|打开者”
	            for (int i = 0; i < values.length; i++)
	            {
	            	value = values[i].getString();
	                if ((index = value.indexOf("|")) > 0)
	                {
	                	key = value.substring(0, index);
	                	if (key.indexOf(path) >= 0)
		                {
		                	ret.add(value);
		                }
	                }                
	            }
	            if (ret.size() > 0)
	            {
	            	return ret;
	            }
	            return null;
	        }
	    });
	    return (List<String>)ret;
	}
    
    /**
     * 设置用户最近操作的文档，目前需求是要打开及新建保持的文档,包括操作的所有空间中的文档。
     * 用户的最近操作文档放在用户根目录下的recent节点上的notification属性中。
     * @param userSpaceUID 用户的spaceUID值
     * @param path 操作的文件全路径
     */
    public void setUserRecentFile(final String userSpaceUID, final String path)
    {
    	jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node root = session.getRootNode();
            	Node recentNode = root.getNode(userSpaceUID + "/" + FileConstants.RECENT);
		    	if(recentNode != null)
				{
					Value[] values = recentNode.getProperty(FileConstants.NOTIFICATION).getValues();
					List<String> arrayS = new ArrayList<String>();
					List<String> arrayP = new ArrayList<String>();
					//int count = Math.min(20, values.length);    // 根据规格先记录20个， 前10个是个人空间的，后10个公共空间的
					int length = values.length;    // 为了兼容原来的
					int count = 10;
			        for (int i = 0; i < count; i++)
			        {
			        	if (i < length)
			        	{
				        	String temp = values[i].getString();
				        	if (temp != null)
				        	{
				        		arrayS.add(temp);
				        	}
				        	else
				        	{
				        		arrayS.add("");
				        	}
			        	}
			        	else
			        	{
			        		arrayS.add("");
			        	}
			        }
			        count = 20;
			        for (int k = 10; k < count; k++)
			        {
			        	if (k < length)
			        	{
				        	String temp = values[k].getString();
				        	if (temp != null)
				        	{
				        		arrayP.add(temp);
				        	}
				        	else
				        	{
				        		arrayP.add("");
				        	}
			        	}
			        	else
			        	{
			        		arrayP.add("");
			        	}
			        }
			        if (arrayS.contains(path))
			        {
			        	arrayS.remove(path); 
			        }
			        if (arrayP.contains(path))
			        {
			        	arrayP.remove(path); 
			        }
			        if (path.startsWith(FileConstants.USER_ROOT))     // 个人空间的
			        {
			        	arrayS.add(0, path);
			        }
			        else if(path.startsWith(FileConstants.COMPANY_ROOT))          // 其他空间的改为了仅公文库的
			        {
			        	arrayP.add(0, path);
			        }
			        String[] listFile = new String[20];
			        String[] tempS = arrayS.toArray(new String[10]);
			        String[] tempP = arrayP.toArray(new String[10]);
			        System.arraycopy(tempS, 0, listFile, 0, 10);
			        System.arraycopy(tempP, 0, listFile, 10, 10);
			        recentNode.setProperty(FileConstants.NOTIFICATION, listFile);
			        session.save();
				}
		    	return null;
            }
        }); 
    }
    
    /**
     * 得到用户最近操作的文档，目前需求是要打开及新建保持的文档,包括操作的所有空间中的文档。
     * @param userSpaceUID 用户的spaceUID值
     * @param type 获取类型。0表示个人空间的，1表示公共空间的
     */
    public ArrayList<Fileinfo> getUserRecentFile(final String userSpaceUID, final int type)
    {
    	Object ret= jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node root = session.getRootNode();
            	Node recentNode = root.getNode(userSpaceUID + "/" + FileConstants.RECENT);
		    	if(recentNode != null)
				{
					Value[] values = recentNode.getProperty(FileConstants.NOTIFICATION).getValues();
					List<String> array = new ArrayList<String>();
					//int count = 20;   //Math.min(20, values.length);    // 根据规格先记录10个
					int i = 10 * type;               // 10 开始的为公共空间的。
					int count = Math.min(10 + i, values.length);
			        for (; i < count; i++)
			        {
			        	String temp = values[i].getString();
			        	if (temp != null && temp.length() > 0)
			        	{
			        		array.add(temp);
			        	}
			        }
			        String[] listFile = array.toArray(new String[array.size()]);
			        ArrayList<Fileinfo> ret = new ArrayList<Fileinfo>();
			        for(int k = 0; k < listFile.length; k++)
	                {
			        	Fileinfo fileinfo = valueToFileInfo(root, listFile[k]);
			        	if (fileinfo != null)
			        	{
			        		ret.add(fileinfo);
			        	}
	                }
			        return ret;
				}
		    	return null;
            }
        });  
    	return (ArrayList<Fileinfo>)ret;
    }
    
    /**
     * 建立给定path上的所有文件夹。如果path中的文件夹某个文件夹不存在，则递归建立所有的子文件夹
     * @param path
     * @return
     * @throws RepositoryException
     */
    public String createFolders(final String path) throws RepositoryException
    {
        return (String)jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node root = session.getRootNode();
            	String tempPath = path;
            	ArrayList<String> list = new ArrayList<String>();
            	while (!root.hasNode(tempPath))
            	{
	            	String name = "";
	            	int index = tempPath.lastIndexOf("/");            	
	            	if (index >= 0)
	            	{
	            		name = tempPath.substring(index + 1);
	            		name = filterInvalidChar(name);
	            		if (name.length() <= 0)
	            		{
	            			break;
	            		}
	            		tempPath = tempPath.substring(0, index);
	            		list.add(0, name);
	            	}
	            	else
	            	{
	            		break;
	            	}
            	}
            	Node nd = root.getNode(tempPath);
            	for(int i = 0; i < list.size(); i++)
            	{
            		nd = createFolod(nd, list.get(i));            		
            	}
            	session.save();
            	tempPath = nd.getPath().substring(1);

                return tempPath;
            }
        });
    }   
    
    private Node createFolod(Node node, String nodeName)
    {
    	try
    	{
	    	Node folderNode = node.addNode(nodeName, FileConstants.NODE_FOLDER);
	        String path = folderNode.getPath().substring(1);
	        folderNode.setProperty(FileConstants.AUTHOR, "");
	        folderNode.setProperty(FileConstants.NAME, nodeName);
	        folderNode.setProperty(FileConstants.CREATED, Calendar.getInstance());
	        folderNode.setProperty(FileConstants.DELETED, Calendar.getInstance());
	        folderNode.setProperty(FileConstants.NODE_PATH, path + "/" + nodeName);
	        return folderNode;
    	}
    	catch(Exception e)
    	{
    		LogsUtility.error(e);
    	}
    	return null;
    }
        
    public DataHolder getFiles(final String spaceUID, final String path, final int startIndex,
            final int number)
    {
    	return getFiles(spaceUID, path, startIndex, number,null);
    }

    public DataHolder getFiles(final String spaceUID, final String path, final int startIndex,
            final int number,final Integer[] permit)
    {
    	return getFiles(spaceUID, path, startIndex, number,permit,null);
    }
    public DataHolder getFiles(final String spaceUID, final String path, final int startIndex,
            final int number,final Integer[] permit,final String realname)
    {
    	return getFiles(spaceUID, path, startIndex, number,permit,realname,null);
    }
    
    public DataHolder getFiles(final String spaceUID, final String path, final int startIndex,
            final int number,final Integer[] permit,final String realName,final String sharecomment)
    {
    	return getFiles(spaceUID, path, startIndex,  number, permit, realName, sharecomment, null);
    }
    
	public DataHolder getFiles(final String spaceUID, final String path, final int startIndex,
        final int number,final Integer[] permit,final String realName,final String sharecomment, final Long uid)
    {
        final DataHolder holder = new DataHolder();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	PermissionService pemissionService = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
            	try
            	{
            		isSpaceExist(session, path);
	                Node nd = session.getRootNode().getNode(path);
	                ArrayList<Object> files = new ArrayList<Object>();
	                ArrayList<Object> folders = new ArrayList<Object>();
	                int fileSize;
	                if (nd.isNodeType(FileConstants.NODE_FILE))
	                {
		                Fileinfo fileInfo = getFile(nd);
		                files.add(fileInfo);
		                fileSize = 1;
	                }
	                else
	                {
		                NodeIterator iter = nd.getNodes();		                
		                fileSize = (int)iter.getSize();
		                int nn = 0;
		                int endIndex = /*(startIndex <= 0 && number <= 0) ?*/ fileSize/* : startIndex + number*/;
		                boolean isTrash = path.endsWith(FileConstants.TRASH);
		                boolean fileEnd = false;
		                int index = -1;
		                Long filePermit;
		                while (iter.hasNext())
		                {
		                    Node tempNode = iter.nextNode();
		                    {
		                    	/*if (uid != null)
		                    	{
			                    	filePermit = pemissionService.getFileSystemAction(uid, tempNode.getPath().substring(1), true);
			                    	if (filePermit == null || filePermit == 0)
			                    	{
			                    		fileSize--;
			                    		continue;
			                    	}
		                    	}*/
		                        Fileinfo fileInfo = getFile(tempNode, !isTrash); 
		                        fileInfo.setIsNew(1);
		                        fileInfo.setShareRealName(realName);
		                        fileInfo.setShareCommet(sharecomment);
		                        if (permit != null && permit[0] != null)
		                        {
		                        	fileInfo.setPermit(permit[0]);
		                        }
		                        
		                        if(fileInfo.isFold())
		                        {
		                        	index++;
		                            files.add(index, fileInfo);
		                            folders.add(fileInfo);
		                        }
		                        else if (!fileEnd)
		                        {
		                            files.add(fileInfo);
		                        }
		                    }
		                    nn++;
		                    if (nn >= endIndex)
		                    {
		                        fileEnd = true;
		                    }
		                }
	                }                
	                Collections.sort(files, new FileArrayComparator("lastChanged", -1));
	                
	                holder.setIntData(fileSize);
	                holder.setFolderData(folders);
	                ArrayList al = new ArrayList();
	                if(!(startIndex <= 0 && number <= 0))
	                {
	                    int end = startIndex + number > fileSize ? fileSize : startIndex + number;
	                    for(int i = startIndex; i < end; i++)
	                    {	                    	
	                        al.add(files.get(i));
	                    }
	                    holder.setFilesData(al);
	                }
	                else
	                {
	                    holder.setFilesData(files); 
	                }
	                return null;
	            }
	            catch(Exception e)
	            {
	            	LogsUtility.error(e);
	            }
	            return null;
            }
			
        });
        
        return holder;
    }
    
	/**
	 * 获取node中的文件信息。
	 * @param node
	 * @return
	 */
	private Fileinfo getFile(Node node)
	{
		return getFile(node, false);
	}
	
	/**
	 * 获取node中的文件信息，如果是文件夹，是否需要判断文件夹中还有子文件夹。
	 * @param node
	 * @param flag 为true表示还需要判断是否有子文件夹
	 * @return
	 */
	private Fileinfo getFile(Node node, boolean flag)
	{		
		Fileinfo fileInfo = new Fileinfo();
		try
        {
			String showPath = getShowPath(node);
	        fileInfo.setPathInfo(node.getPath().substring(1));
	        String showName = node.getProperty(FileConstants.NAME).getString();
	
	        fileInfo.setFileName(showName);
	        fileInfo.setShowPath(showPath);
	        fileInfo.setAuthor(node.getProperty(FileConstants.AUTHOR).getString());
	        fileInfo.setPrimalPath(node.getProperty(FileConstants.NODE_PATH).getString());
	        fileInfo.setCreateTime(node.getProperty(FileConstants.CREATED).getDate().getTime());
	        fileInfo.setDeletedTime(node.getProperty(FileConstants.DELETED).getDate().getTime());
	        if (node.isNodeType(FileConstants.NODE_FOLDER))
            {
                fileInfo.setFold(true);
                if (flag)   // 判断是否有子文件夹
                {
                	NodeIterator it = node.getNodes();
                    while (it.hasNext())
                    {
                        Node childNode = it.nextNode();
                        if (childNode.isNodeType(FileConstants.NODE_FOLDER))
                        {
                            fileInfo.setChild(true);
                            break;
                        }
                    }
                }
            }
            else
            {
		        fileInfo.setUserLock(node.getProperty(FileConstants.LOCK).getString());
		        fileInfo.setKeyWords(node.getProperty(FileConstants.KEYWORDS).getString());
		        fileInfo.setImportant(node.getProperty(FileConstants.IMPORTANT).getLong());
		        fileInfo.setFileSize(node.getProperty(FileConstants.SIZE).getLong());
		        fileInfo.setTitle(node.getProperty(FileConstants.TITLE).getString());
		        fileInfo.setLastedTime(node.getProperty(FileConstants.LASTMODIFIED).getDate().getTime());
		        fileInfo.setFileStatus(node.getProperty(FileConstants.STATUS).getString());
		       	fileInfo.setIscheck(node.getProperty(FileConstants.ISCHECK).getString());
		       	fileInfo.setIsEntrypt(node.getProperty(FileConstants.ISENTRYPT).getString());
		       	fileInfo.setHasVersion(hasFileVersion(node));
		       	fileInfo.setVersionCount(getFileVersionCount(node));
		       	//双击表格表头后，审批状态错误,需要设置状态值
		       	//fileInfo.setApprovalStatus(ApprovalUtil.instance().getApprovalStatus(fileInfo.getPathInfo()));                    
		       	//fileInfo.setApprovalCount(ApprovalUtil.instance().getApprovalCount(fileInfo.getPathInfo()));
            }            
        }
        catch(Exception e)
        {
        	LogsUtility.error(e);
        }
        return fileInfo;
	}
	    
    /**
     * 系统监控数据
     * @return
     */
	public int getDocumnetCount()
	{
		Object ret = jcrTemplate.execute(new JcrCallback()
		{
			public Object doInJcr(Session session) throws RepositoryException
			{
				try
				{
					return getDocumnetCount(session.getRootNode());

				}
				catch (Exception e)
				{
					LogsUtility.error(e);
				}
				return 0;
			}
		});
		return ret != null ? (Integer) ret : 0;
	}
    
    /**
     * 
     */
    private int getDocumnetCount(Node node) throws RepositoryException
    {
        int count = 0;
        NodeIterator it = node.getNodes();
        Node tempNode;
        while (it.hasNext())
        {
            tempNode = it.nextNode();
            if (tempNode.isNodeType(FileConstants.NODE_FILE))
            {
                count++;
            }
            else
            {
                count += getDocumnetCount(tempNode);
            }
        }
        return count;
    }

    /**
     * 获得node的显示路径，该路径过滤了根路径。比如，
     * 获取文件的路径，则该路径过滤了/spaceUID/DOC/。
     * @param nd
     * @return
     */
    private String getShowPath(final Node nd)
    {
    	try
    	{
    		String path = nd.getPath();
	    	Item temp = nd.getAncestor(2);
	    	String path2 = temp != null ? temp.getPath() : "";
	    	int index = path.indexOf(path2);
	    	if (index != -1)
	    	{
	    		path = path.substring(index + path2.length());
	    	}
	    	return path;
    	}
    	catch(Exception e)
    	{
    		LogsUtility.error(e);
    	}
    	return "";
    }
    
    public List<String> checkUploadFile(String spaceUID, final String path,
            final List<String> listFile)
        {
            final ArrayList<String> array = new ArrayList<String>();
            jcrTemplate.execute(new JcrCallback()
            {
                public Object doInJcr(Session session) throws RepositoryException
                {
                    Node node = session.getRootNode().getNode(path);
                    for (int i = 0; i < listFile.size(); i++)
                    {
                    	String name = listFile.get(i);
                    	if (node.hasNode(name))
                    	{
                    		array.add(name);
                    	}
                    }
                    return null;
                }
            });
            return array;
        }

    /**
     * 增加审批文档
     * @param creatorName 增加者
     * @param userSpaceUID 用户在文件库的根
     * @param name 文件名
     * @param in 文件属性流
     * @param indata 文件内容流
     * @return
     * @throws IOException
     */
    public Fileinfo addAuditFile(final String creatorName, String userSpaceUID, final String name, final InputStream in,
            final InputStream indata) throws IOException,RepositoryException
    {
    	String path = FileConstants.AUDIT_ROOT + "/" + userSpaceUID;
    	Fileinfo ret = createFile(creatorName, path, name, in, indata, false, "", false);
    	String version = createVersion(ret.getPathInfo(), creatorName, "建立审核文档", "当前");
    	ret.setVersionName(version);
    	return ret;
    }
    
    /**
     * 无锡信电局
     * 从我的文库中选择文件送审
     * @param creatorName 增加者
     * @param path 系统中的文件路径
     * @param userSpaceUID 用户在文件库的根。
     * @return
     */
    public Fileinfo addSignFile(final String path, String userSpaceUID)
    {
    	createSignSpace(userSpaceUID);
    	Fileinfo ret = handlAuditFile(path, FileConstants.SIGN_ROOT + "/" + userSpaceUID, false);
    	return ret;
    }
    
    /**
     * 从系统目录中选择文件送审
     * @param creatorName 增加者
     * @param path 系统中的文件路径
     * @param userSpaceUID 用户在文件库的根。
     * @return
     */
    public Fileinfo addAuditFile(final String creatorName, final String path, String userSpaceUID)
    {
    	Fileinfo ret = handlAuditFile(path, FileConstants.AUDIT_ROOT + "/" + userSpaceUID, false);
    	String version = createVersion(ret.getPathInfo(), creatorName, "建立审核文档", "当前");
    	ret.setVersionName(version);
    	return ret;
    }
    
    /**
     * 文件审核
     * @param creatorName 增加者
     * @param path 需要审核的文件全路径
     * @return
     */
    public String auditFile(final String creatorName, final String path)
    {
    	String version = createVersion(path, creatorName, creatorName + "审核该文档！", "当前");
    	return version;
    }
    
    /**
     * 发布文档到发布目录中。
     * @param path 需要发布的文件全路径
     */
    public Fileinfo publishFile(final String path)
    {
    	Fileinfo ret = handlAuditFile(path, FileConstants.AUDIT_ROOT + "/" + FileConstants.PUBLISHMENTS, false);
    	return ret;
    }
    
    /**
     * 归档文档
     * @param path 需要归档的文件全路径
     * @return
     */
    public Fileinfo archiveFile(final String path)
    {
    	Fileinfo ret = handlAuditFile(path, FileConstants.AUDIT_ROOT + "/" + FileConstants.ARCHIVES, true);
    	return ret;
    }
    
    private Fileinfo handlAuditFile(final String path, final String targP, final boolean delSource)
    {
    	return (Fileinfo)jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Fileinfo fileinfo = new Fileinfo();
                try
                {
        			int index = path.lastIndexOf("/");
        			String nodeName;
        			nodeName = path.substring(index + 1);
        			
        			String publish = targP + "/";
        			Node rootNode = session.getRootNode();
        			
        			index = nodeName.lastIndexOf(".");
        			String suffix;
        			String tempName;
        			if (index >= 0)
        			{
        				suffix = nodeName.substring(index);
        				tempName = nodeName.substring(0, index);
        			}
        			else
        			{
        				suffix = "";
        				tempName = nodeName;
        			}
        			index = 0;
					while (rootNode.hasNode(publish + nodeName))
					{
        				index ++;
            			nodeName = tempName + "(" + index + ")" + suffix;
        			}
					session.getWorkspace().copy("/" + path,	"/" + publish + nodeName);
					Node copyNode = rootNode.getNode(publish + nodeName);
					copyNode.setProperty(FileConstants.NAME, nodeName);
					copyNode.setProperty(FileConstants.NODE_PATH, publish + nodeName);
					// 如果有状态，需要清除
					if (copyNode.hasProperty(FileConstants.STATUS))
					{
						copyNode.setProperty(FileConstants.STATUS, "");
					}
					if (copyNode.hasProperty(FileConstants.ISCHECK))
					{
						copyNode.setProperty(FileConstants.ISCHECK, "");
					}
					if (copyNode.hasProperty(FileConstants.ISENTRYPT))
					{
						copyNode.setProperty(FileConstants.ISENTRYPT, "");
					}
					if (delSource)
					{
						session.removeItem("/" + path);
					}
					session.save();    
					
					fileinfo.setPrimalPath(publish + nodeName);
	                fileinfo.setFold(false);
	                fileinfo.setFileName(nodeName);
	                fileinfo.setFileSize(copyNode.getProperty(FileConstants.SIZE).getLong());
	                fileinfo.setPathInfo(publish + nodeName);
	                fileinfo.setUserLock("");
	                fileinfo.setImportant(0);
                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }
				return fileinfo;
            }
        });
    }
     
    public Fileinfo createFile(final String creatorName,final String path, final String name, final InputStream in,
            final InputStream indata, final boolean isNewFile, final String oldPath, boolean replace) 
            throws IOException,RepositoryException
    {
    	return this.createFile(creatorName,path,name, in,indata,isNewFile,oldPath,replace,null);
    }
    public Fileinfo createFile(final String creatorName,final String path, final String name, final InputStream in,
            final InputStream indata, final boolean isNewFile, final String oldPath, boolean replace,final Long createdate) 
            throws IOException,RepositoryException
    {
    	String nodeName = filterInvalidChar(name);
    	boolean exist = this.isFileExist(path);
    	if (!exist)
    	{
//    		init(path.substring(0,path.indexOf("/Document")));
    		createFolders(path);
    	}
    	String ep = path + "/" + nodeName;
    	if (path.endsWith("/"))
    	{
    		ep = path + nodeName;
    	}
    	exist = this.isFileExist(ep);
    	if (exist)
    	{
    		if (replace)
    		{
    			update(creatorName, ep, indata, in.available(), null, false);
    			Fileinfo fileinfo = new Fileinfo();
    			
    			fileinfo.setPrimalPath(ep);
                fileinfo.setFold(false);
                fileinfo.setFileName(nodeName);
                //fileinfo.setFileSize((long)size);
                fileinfo.setPathInfo(ep);
                fileinfo.setUserLock("");
                fileinfo.setImportant(0);
    			return fileinfo;
    		}
    		else
    		{
    			int index = nodeName.lastIndexOf(".");
    			String suffix;
    			String tempName;
    			if (index >= 0)
    			{
    				suffix = nodeName.substring(index);
    				tempName = nodeName.substring(0, index);
    			}
    			else
    			{
    				suffix = "";
    				tempName = nodeName;
    			}
    			index = 1;
    			nodeName = tempName + "(" + index + ")" + suffix;
    			while(isFileExist(path + "/" + nodeName))
    			{
    				index ++;
        			nodeName = tempName + "(" + index + ")" + suffix;
    			}
    		}
    	}
    	return createFile(creatorName, path, nodeName, in, indata, isNewFile, oldPath,createdate);
    }      
    public Fileinfo createFile(final String creatorName,final String path, final String name, final InputStream in,
            final InputStream indata, final boolean isNewFile, final String oldPath) throws IOException
    {
    	return createFile(creatorName,path,name,in,indata,isNewFile,oldPath,null);
    }
    public Fileinfo createFile(final String creatorUser,final String path, final String name, final InputStream in,
        final InputStream indata, final boolean isNewFile, final String oldPath,final Long createdate) throws IOException        
    {
        final int size = in.available();
        in.mark(0);
        final String userID = FileUtils.getPreName(path);
        final Fileinfo fileinfo = new Fileinfo();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Object[] obj = new Object[5];
                String creatorName=creatorUser;
                boolean thumb = false;
                String nodeName = filterInvalidChar(name);                
                {
                    Node nd = session.getRootNode().getNode(path);
                    nodeName = removeNodeBySameName(nd, nodeName, true);
                    try
                    {
                        Node fileNode = nd.addNode(nodeName, FileConstants.NODE_FILE);

                        String nameExtension = FileUtils.getFileExtension(nodeName);
                        nameExtension = nameExtension.toLowerCase();
//                        if (nameExtension.equals("doc") || nameExtension.equals("xls")
//                            || nameExtension.equals("ppt") || nameExtension.equals("pptx")
//                            || nameExtension.equals("docx") || nameExtension.equals("xlsx"))
//                        {
//                        	try
//                        	{
//	                            ReadCustomPropertySets custom = new ReadCustomPropertySets(in);
//	                            obj = custom.getValue();
//                        	}
//                        	catch(Exception e)
//                        	{
//                        		LogsUtility.error(e);
//                        	}                            
//                        }
//                        else if (nameExtension.equals("eio") || nameExtension.equals("eit"))
//                        {
//                            //try
//                           // {
////                                Object[] eioObj = Macro.getMetaData(in);
////                                if (eioObj != null)
////                                {
////                                    obj = eioObj;
////                                }
//                           // }
//                           // catch(IOException e)
//                            //{
//                                // Auto-generated catch block
//                            //    e.printStackTrace();
//                           // }
//                        }
                
                        fileNode.setProperty(FileConstants.AUTHOR, creatorName);
                        fileNode.setProperty(FileConstants.KEYWORDS, obj[2] == null ? "" : (String)obj[2]);
                        fileNode.setProperty(FileConstants.NAME, nodeName);
                        fileNode.setProperty(FileConstants.NODE_PATH, path + "/" + nodeName);
                        
                        Calendar calendar = Calendar.getInstance();
                        if (createdate!=null)
                        {
                        	calendar.setTimeInMillis(createdate);
                        }
                        fileNode.setProperty(FileConstants.CREATED, calendar);
                        fileNode.setProperty(FileConstants.DELETED, calendar);

                        fileNode.setProperty(FileConstants.IMPORTANT, 0);
                        fileNode.setProperty(FileConstants.LOCK, "");
                        fileNode.setProperty(FileConstants.TITLE, obj[0] == null ? "" : (String)obj[0]);
                        fileNode.setProperty(FileConstants.LASTMODIFIED, calendar);
                        fileNode.setProperty(FileConstants.LASTMODIFIER, creatorName);
                        fileNode.setProperty(FileConstants.SIZE, size);
                        fileNode.setProperty(FileConstants.STATUS,"");
                        fileNode.setProperty(FileConstants.ISCHECK,"");	                   
	                    fileNode.setProperty(FileConstants.ISENTRYPT, "");

                        Node contentNode = fileNode.addNode(FileConstants.NODE_CONTENT, FileConstants.NODE_RESOURE);
                        contentNode.setProperty(FileConstants.AUTHOR, obj[1] == null ? creatorName : (String)obj[1]);
                        contentNode.setProperty(FileConstants.TITLE, obj[0] == null ? "" : (String)obj[0]);
                        contentNode.setProperty(FileConstants.P_JCR_DATA, indata);
                        contentNode.setProperty(FileConstants.LASTMODIFIED, calendar);
                        contentNode.setProperty(FileConstants.SIZE, size);
                        
                        String mimeType = "";
                        if (nameExtension.equals("eio"))
                        {
                            mimeType = FileConstants.EIOMIME;
                            thumb = true;
                        }
                        else if (nameExtension.equals("eit"))
                        {
                            mimeType = FileConstants.EITMIME;
                            thumb = true;
                        }
                        else if (nameExtension.equals("eiw"))
                        {
                            mimeType = "application/eiw";
                            thumb = true;
                        }
                        else if (nameExtension.equals("doc") || nameExtension.equals("docx"))
                        {
                            mimeType = FileConstants.DOCMIME;
                            thumb = true;
                        }
                        else if (nameExtension.equals("xls") || nameExtension.equals("xlsx"))
                        {
                            mimeType = FileConstants.XLSMIME;
                            thumb = true;
                        }
                        else if (nameExtension.equals("ppt") || nameExtension.equals("pptx"))
                        {
                            mimeType = FileConstants.PPTMIME;
                            thumb = true;
                        }
                        else if (nameExtension.equals("htm") || nameExtension.equals("html")
                            || nameExtension.equals("shtml") || nameExtension.equals("mht"))
                        {
                            mimeType = FileConstants.HTMLMIME;
                            thumb = true;
                        }
                        else if (nameExtension.equals("rtf"))
                        {
                            mimeType = FileConstants.RTFMIME;
                            thumb = true;
                        }
                        else if (nameExtension.equals("pdf"))
                        {
                            mimeType = FileConstants.PDFMIME;
                            thumb = true;
                        }
                        else if (nameExtension.equals("txt"))
                        {
                            mimeType = FileConstants.TXTMIME;
                            thumb = true;
                        }
                        else if (getType(nameExtension) == 0)
                        {
                        	mimeType = "image/" + nameExtension;
                            thumb = true;
                        }
                        else
                        {
                            mimeType = "application/octect-stream";
                        }

                        contentNode.setProperty(FileConstants.MIMETYPE, mimeType);

                        if (isNewFile)
                        {
                            Node docNode = session.getRootNode().getNode(userID.concat("/").concat(FileConstants.DOC));
                            String showPath = getShowPath(fileNode);
                            addLatelyList(docNode, path.concat("/").concat(nodeName), showPath);
                            if(oldPath != null && !oldPath.equals(""))
                            {
                                removeFileList(userID, oldPath, 0);
                                removeFileList(userID, oldPath + "," + userID, 1);
                            }
                        }
                        
                        //user753 add 创建文件需要设置成最近阅读
                        setRecentFile(session.getRootNode(),fileNode,userID,FileConstants.SAVELIST);

                        session.save();
                        if (thumb)
                        {
                        	tpt.addPath(path + "/" + nodeName);
                        }
                        // 系统监控数据
                        SysMonitorTask.instance().updateTodayAddDocumentCount(1);
                        
                    }
                    catch(Exception e)
                    {
                    	LogsUtility.error(e);
                    }
                }

                fileinfo.setPrimalPath(path + "/" + nodeName);
                fileinfo.setAuthor((String)obj[1]);
                fileinfo.setTitle((String)obj[0]);
                fileinfo.setKeyWords((String)obj[2]);
                fileinfo.setCreateTime((Date)obj[3]);
                fileinfo.setLastedTime((Date)obj[4]);
                fileinfo.setFold(false);
                fileinfo.setFileName(nodeName);
                fileinfo.setFileSize((long)size);
                fileinfo.setPathInfo(path + "/" + nodeName);
                fileinfo.setUserLock("");
                fileinfo.setImportant(0);

                return null;
            }

        });

        return fileinfo;
    }
    
    // 建立缩略图
    public void createThumbnail(final String path, final InputStream indata)
	{
	    Object obj = jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	        	try
	        	{
		            Node nd = session.getRootNode().getNode(path);
		            // 原来没有的先不管
		            /*Node contentNode = nd.addNode(FileConstants.NODE_CONTENT);
		            String mimeT = contentNode.getProperty(FileConstants.MIMETYPE).getString();
		            if (mimeT.equals(FileConstants.DOCMIME) || mimeT.equals(FileConstants.XLSMIME)
		            		|| mimeT.equals(FileConstants.PPTMIME) || mimeT.equals(FileConstants.RTFMIME))
		            {		            	
		            }*/
		            boolean updateF = nd.hasNode(FileConstants.NODE_THUMBNAIL);
		            Node thumbnailNode;
		            if (updateF)
		            {
		            	thumbnailNode = nd.getNode(FileConstants.NODE_THUMBNAIL);		            	
		            }
		            else
		            {
		            	thumbnailNode = nd.addNode(FileConstants.NODE_THUMBNAIL, FileConstants.NODE_RESOURE);
		            }
		            thumbnailNode.setProperty(FileConstants.AUTHOR, "");
		            thumbnailNode.setProperty(FileConstants.P_JCR_DATA, indata);	          
		            session.save();
		            return true;
	        	}
	        	catch(Exception e)
	        	{
	        		LogsUtility.error(e);
	        		return false;
	        	}
	        }
	    });
	}
    
    //得到缩略图
    public InputStream getThumbnail(final String path)
	{
	    Object obj = jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	        	try
	        	{
		            Node nd = session.getRootNode().getNode(path);
		            Node thumbnailNode = nd.hasNode(FileConstants.NODE_THUMBNAIL) ? nd.getNode(FileConstants.NODE_THUMBNAIL) : null;
		            if (thumbnailNode != null && thumbnailNode.hasProperty(FileConstants.P_JCR_DATA))
		            {
			            InputStream is = thumbnailNode.getProperty(FileConstants.P_JCR_DATA).getStream(); 
			            return is;
		            }
		            return null;
	        	}
	        	catch(Exception e)
	        	{
	        		LogsUtility.error(e);
	        		return null;
	        	}
	        }
	    });
	    return (InputStream)obj;
	}

    private String removeNodeBySameName(Node node, String name, boolean isFile)
        throws RepositoryException
    {
    	if (node.hasNode(name))
		{
			Node nd = node.getNode(name);
			if (nd.isNodeType(FileConstants.NODE_FILE) && isFile)
			{
				FileSystemService filesystemService = (FileSystemService) ApplicationContext.getInstance().getBean(FileSystemService.NAME);
				String path = nd.getPath().substring(1);
				filesystemService.removeFileInDB("Public",
						new String[] { path });
				nd.remove();
				return name;
			}
			else if (nd.isNodeType(FileConstants.NODE_FOLDER) && !isFile)
			{
				nd.remove();
				return name;
			}
			else
			{
				String temp = "^" + name;
				while (node.hasNode(temp))
				{
					temp = "^" + temp;
				}
				return temp;
			}
		}
    	return name;    	
    }

    private Node getNodeByShowName(Node node, String name, boolean isFile)
        throws RepositoryException
    {
    	if (node.hasNode(name))
		{
			Node nd = node.getNode(name);
			if (nd.isNodeType(FileConstants.NODE_FILE) && isFile)
            {
				return nd;
            }
            else if (nd.isNodeType(FileConstants.NODE_FOLDER) && !isFile)
            {
            	return nd;
            }
		}
    	return null;
    }

    public void addFileList(final String path, final int flag) throws RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                final String userID = FileUtils.getPreName(path);
                Node docNode = session.getRootNode().getNode(userID.concat("/").concat(FileConstants.DOC));
                addFileList(session, docNode, path, flag);
                return null;
            }
        });
    }

    public void addFileList(final String spaceUID, final String path, final int flag)
        throws RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node docNode = session.getRootNode().getNode(spaceUID.concat("/").concat(FileConstants.DOC));
                addFileList(session, docNode, path, flag);
                return null;
            }
        });
    }

    private void addFileList(Session session, Node docNode, String name, int flag)
        throws ValueFormatException, PathNotFoundException, RepositoryException
    {
        Value[] values = null;
        if (flag == 0)
        {
            values = docNode.getProperty(FileConstants.OPENLIST).getValues();
        }
        else if (flag == 1)
        {
            values = session.getRootNode().getProperty(FileConstants.LOCKLIST).getValues();
        }
        else if (flag == 2)
        {
            values = docNode.getProperty(FileConstants.CLOSELIST).getValues();
        }
        ArrayList<String> array = new ArrayList<String>();
        for (int i = 0; i < values.length; i++)
        {
            array.add(values[i].getString());
        }
        if (!array.contains(name))
        {
            array.add(name);
        }
        String[] listFile = array.toArray(new String[array.size()]);
        if (flag == 0)
        {
            docNode.setProperty(FileConstants.OPENLIST, listFile);
        }
        else if (flag == 1)
        {
            session.getRootNode().setProperty(FileConstants.LOCKLIST, listFile);
        }
        else if (flag == 2)
        {
            docNode.setProperty(FileConstants.CLOSELIST, listFile);
        }
        session.save();
    }

    private void addLatelyList(Node docNode, String pathName, String showPath)
        throws ValueFormatException, PathNotFoundException, RepositoryException
    {
        boolean flag = false;
        Value[] values = docNode.getProperty(FileConstants.NOTIFICATION).getValues();
        ArrayList<String> array = new ArrayList<String>();
        String tempP = pathName + "#" + showPath;
        for (int i = 0; i < values.length; i++)
        {
            String path = values[i].getString();
            if (!path.equals(""))
            {
            	if (path.equals(tempP))
            	{
            		flag = true;
            	}
                /*int index = path.indexOf("#");
                String oldShowPath = path.substring(index + 1);
                if (oldShowPath.equals(showPath))
                {
                    flag = true;
                }*/
            }
            array.add(path);
        }

        if (!flag)
        {
            if (array.size() >= 10)
            {
                array.remove(0);
                array.add(tempP);  //pathName + "#" + showPath);
            }
            String[] listFile = array.toArray(new String[array.size()]);
            docNode.setProperty(FileConstants.NOTIFICATION, listFile);
        }
    }

    private void upDateLatelyList(Node docNode, String realPath, String showPath)
        throws ValueFormatException, PathNotFoundException, RepositoryException
    {
        Value[] values = docNode.getProperty(FileConstants.NOTIFICATION).getValues();
        ArrayList<String> array = new ArrayList<String>();
        for (int i = 0; i < values.length; i++)
        {
            String path = values[i].getString();
            if (!path.equals(""))
            {
                if (path.indexOf(showPath) != -1)
                {
                    path = realPath + "#" + showPath;
                }
            }
            array.add(path);
        }

        String[] listFile = array.toArray(new String[array.size()]);
        docNode.setProperty(FileConstants.NOTIFICATION, listFile);
    }

    /**
     * 直接删除文件，不把文件放到垃圾站中。
     * @param path
     * @throws RepositoryException
     */
    public void delete(final String ... path) throws RepositoryException
    {
    	jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                try
                {
                	Node root = session.getRootNode();
                    for (int i = 0; i < path.length; i++)
                    {
                        Node nd = root.getNode(path[i]);
                        nd.remove();
                    }
                    session.save();
                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }
                return null;
            }
        });

    }
    
    public void delete(final String userID, final String[] path) throws RepositoryException
    {
    	jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                try
                {
                    for (int i = 0; i < path.length; i++)
                    {
                    	if (!canOper(path[i]))
                    	{
                    		continue;
                    	}
                        Node nd = session.getRootNode().getNode(path[i]);
                        String unPath = getShowPath(nd);
                        String rootPath = FileUtils.getPreName(path[i]);
                        nd.setProperty(FileConstants.NODE_PATH, unPath);
                        String name = nd.getProperty(FileConstants.NAME).getString();
                        Node userTrash = session.getRootNode().getNode(rootPath + "/" + FileConstants.TRASH);
                        String destPath = userTrash.getPath() + "/";

                        nd.setProperty(FileConstants.DELETED, Calendar.getInstance());

                        if (nd.isNodeType(FileConstants.NODE_FOLDER))
                        {
                            deleteFolder(session, "/".concat(path[i]), destPath, name);
                        }
                        else
                        {
                            deleteFile(session, "/".concat(path[i]), destPath, name);
                        }
                    }
                    session.save();
                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }
                return null;
            }
        });

    }

    private void deleteFolder(Session session, String srcPath, String destPath, String name)
        throws RepositoryException
    {
        if (name != null)
        {
            String testName = destPath + name;
            for (int i = 1; session.itemExists(testName); i++)
            {
                testName = destPath + name + "(" + i + ")";
            }
            session.move(srcPath, testName);
            Node node = session.getNode(testName);
            node.setProperty(FileConstants.NAME, name);
        }
        else
        {
            session.move(srcPath, destPath);
        }
    }

    private void deleteFile(Session session, String srcPath, String destPath, String name)
        throws RepositoryException
    {
        if (name != null)
        {
            String testName = name;

            String fileName = FileUtils.getFileName(name);
            String fileExtension = FileUtils.getFileExtension(name);

            testName = destPath + testName;
            for (int i = 1; session.itemExists(testName); i++)
            {
                testName = destPath + fileName + "(" + i + ")." + fileExtension;
            }
            session.move(srcPath, testName);
            Node node = session.getNode(testName);
            node.setProperty(FileConstants.NAME, name);
        }
        else
        {
            session.move(srcPath, destPath);
        }
    }

    public ArrayList<Object> listPageFileinfos(String userID, String path, int startIndex,
        int number)
    {
        return listPageFileinfos(userID, path, startIndex, number, null, null, null, null);
    }

    public ArrayList<Object> listPageFileinfos(String userID, String[] path, int startIndex,
            int number)
        {
            return listPageFileinfos(userID, path, startIndex, number, null, null);
        }
    
    public ArrayList<Object> listPageFileinfos(final String userID, final String path[],
            int startIndex0, final int number0, final String sort, final String dir)
        {
            final ArrayList<Object> array = new ArrayList<Object>();
            final int startIndex = startIndex0;
            final int number = number0;
            final ArrayList sizeList = new ArrayList();
            jcrTemplate.execute(new JcrCallback()
            {
                public Object doInJcr(Session session) throws RepositoryException
                {
                	for (int i = 0; i < path.length; i++)
                    {
	                	if(!session.getRootNode().hasNode(path[i]))
	                	{
	                		break;
	                	}	                	
	                    Node nd = session.getRootNode().getNode(path[i]);
	
	                    NodeIterator iter = nd.getNodes();
	                    int fileSize = (int)iter.getSize();
	                    sizeList.add(fileSize);
	                    int index = -1;
	                    while (iter.hasNext())
	                    {
	                        Node tempNode = iter.nextNode();
	                        Fileinfo fileInfo = getFile(tempNode);
	                        if(fileInfo.isFold())
	                        {
	                        	index++;
	                        	array.add(index, fileInfo);
	                        }
	                        else
	                        {
	                        	array.add(fileInfo);
	                        }
	                    }	                    
                    }
                	return null;
                }
            });
            int tempL = sizeList.size();
            int arraySize = 0;
            for (int i=0; i < tempL; i++)
            {
            	arraySize += (Integer)sizeList.get(i);
            }
            
            array.add(0, arraySize);
            if(sort != null)
            {
                return array;
            }
            ArrayList al = new ArrayList();
            if(array!=null && !array.isEmpty())
            {
    	        int fileSize = ((Integer)array.get(0)).intValue();  
    	        al.add(fileSize);
    	        int endIndex = startIndex + number > fileSize ? fileSize : startIndex + number;
    	        fileSize = endIndex - startIndex;
    	        for(int i = startIndex; i < endIndex; i++)
    	        {
    	            al.add(array.get(i + 1));
    	        }
            }
            return al;
        }
    
    public ArrayList<Object> listPageFileinfos(final String userID, final String path,
            int startIndex0, final int number0, final String sort, final String dir, final HashMap dd, final String filatg)
    {
    	return listPageFileinfos(userID, path, startIndex0, number0, sort, dir, null,null,null,dd,filatg);
    }
    
    //增加了过滤字符串及标签文件关系
    public ArrayList<Object> listPageFileinfos(final String userID, final String path,
        int startIndex0, final int number0, final String sort, final String dir, final Integer[] permit,
        final String realName,final String sharecomment ,final HashMap dd, final String filtertag)
    {
        final ArrayList<Object> array = new ArrayList<Object>();
        final int startIndex = startIndex0;
        final int number = number0;
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	if(!session.getRootNode().hasNode(path))
            	{
            		return null;
            	}
            	
                Node nd = session.getRootNode().getNode(path);

                NodeIterator iter = nd.getNodes();
                int fileSize = (int) iter.getSize();
                int index1=path.indexOf("/");
                int index2=path.lastIndexOf("/");
//                System.out.println(index1+"======"+index2);
                boolean isfiltertag=true;
                if (index1==index2)//只有一个/表示没有选择目录
                {
                	//不做处理
                }
                else if (filtertag != null && filtertag.length()>1)//选择带标签的目录
                {
                	isfiltertag=false;//点击带标签的文件夹，不管文件夹内容是否设置标签，都要全部显示出来
                }
                if(filtertag != null)
                {
                	fileSize = TraversalNodeSize(session, path);
                }
                array.add(fileSize);
                int index = 0;
				while (iter.hasNext())
				{
					Node tempNode = iter.nextNode();
					Fileinfo fileInfo = getFile(tempNode);
					String tt = null;
					if(dd != null)
					{
						tt = (String)dd.get(fileInfo.getShowPath());
					}
					if(filtertag != null && isfiltertag)
					{
						if(filtertag.equals(",,,"))//直接不筛选
						{
							break;
						}
						else
						{
							if (fileInfo.isFold())
							{
								TraversalNodeFile(array, session, fileInfo.getPathInfo(), dd, filtertag);
							}
							if(tt==null)
							{
								continue;
							}
							else if(!filtertag.equals(",,"))//全部筛选
							{
								String[] ttf = tt.split(",");
								boolean ti = true;
								for(String t1 : ttf)
								{
									t1=","+t1+",";
									if(filtertag.contains(t1))
									{
										ti = false;
									}
								}
								if(ti)
								{
									continue;
								}
							}
						}		
					}
					if(dd != null)
					{
						fileInfo.setTag(tt);
					}
					if (realName != null)
					{
						fileInfo.setShareRealName(realName);
					}
					if (sharecomment != null)
					{
						fileInfo.setShareCommet(sharecomment);
					}
					if (permit != null && permit[0] != null)
					{
						fileInfo.setPermit(permit[0]);
					}
					if (fileInfo.isFold())
					{
						index++;
						array.add(index, fileInfo);	
					}
					else
					{
						array.add(fileInfo);
					}
				}
                return null;
            }
        });
        if(sort == null)
        {
            return array;
        }
        ArrayList al = new ArrayList();
        if(array!=null && !array.isEmpty())
        {
        	 int fileSize = array.size() - 1;
	        
	        array.remove(0);
	        Collections.sort(array, new FileArrayComparator(sort, dir.equals("ASC")?1:-1));
	        array.add(0,(Integer)fileSize);
	        int endIndex = startIndex + number > fileSize ? fileSize : startIndex + number;
	      //  fileSize = endIndex - startIndex;
	        al.add(fileSize);
	        for(int i = startIndex; i < endIndex; i++)
	        {
	            al.add(array.get(i + 1));
	        }
        }
        
        return al;
    }
    //遍历整个文件系统
    public int TraversalNodeSize(Session session, String path) throws RepositoryException
    {
    	Node nd = session.getRootNode().getNode(path);
    	NodeIterator iter = nd.getNodes();
        int size = (int)iter.getSize();
        while (iter.hasNext())
		{
			Node tempNode = iter.nextNode();
            String patht = tempNode.getPath().substring(1);
            if(!patht.contains("."))
            {
            	size += TraversalNodeSize(session,patht);
            }     
		}
        return size;
    }
    //遍历整个文件系统
    public void TraversalNodeFile(ArrayList<Object> array, Session session, String path, HashMap dd, final String filtertag) throws RepositoryException
    {
    	Node nd = session.getRootNode().getNode(path);
    	NodeIterator iter = nd.getNodes();
        while (iter.hasNext())
		{
			Node tempNode = iter.nextNode();
			Fileinfo fileInfo = getFile(tempNode);
			String tt = null;
			if(dd != null)
			{
				tt = (String) dd.get(fileInfo.getShowPath());
			}
			if(filtertag != null)
			{
				if (fileInfo.isFold())
				{
					TraversalNodeFile(array, session, fileInfo.getPathInfo(), dd, filtertag);
				}
				if(tt==null)
				{
					continue;
				}
				else if(!filtertag.equals(",,"))
				{
					String[] ttf = tt.split(",");
					boolean ti = true;
					for(String t1 : ttf)
					{
						t1=","+t1+",";
						if(filtertag.contains(t1))
						{
							ti = false;
						}
					}
					if(ti)
					{
						continue;
					}
				}
			}
			if(dd != null)
			{
				fileInfo.setTag(tt);
			}
		    array.add(fileInfo);
		}
    }
    
    public ArrayList<String> checkRestoreFile(final String userID, final String[] path)
        throws PathNotFoundException, RepositoryException
    {
        final ArrayList<String> array = new ArrayList<String>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                for (int i = 0; i < path.length; i++)
                {
                    Node nd = session.getRootNode().getNode(path[i]);
                    String unPath = nd.getProperty(FileConstants.NODE_PATH).getString().substring(1);
                    String tokenPath = unPath;
                    
                    int aaa = path[i].indexOf('/');
           		    String userID = path[i].substring(0, aaa);
           		  
                    String realPath = "/" + userID + "/" + FileConstants.DOC;
                    int index;
                    while ((index = tokenPath.indexOf("/")) != -1)
                    {
                        String name = tokenPath.substring(0, index);
                        tokenPath = tokenPath.substring(index + 1, tokenPath.length());
                        if (!name.equals(""))
                        {
                            Node docNode = session.getNode(realPath);
                            NodeIterator iter = docNode.getNodes();
                            while (iter.hasNext())
                            {
                                Node ned = iter.nextNode();
                                if (ned.isNodeType(FileConstants.NODE_FOLDER))
                                {
                                    if (ned.getProperty(FileConstants.NAME).getString().equalsIgnoreCase(name))
                                    {
                                        realPath = ned.getPath();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (session.itemExists(realPath))
                    {
                        Node nodes = session.getNode(realPath);
                        if (nodes.hasNode(tokenPath))
                        {
                        	array.add(path[i]);
                        }
                    }
                }
                return null;
            }
        });

        return array;
    }

    /**
     * 还原时，判断回收站里是否有相同的文件或文件夹
     * @param userID
     * @param path
     * @return
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
    public ArrayList<String> containSameinRestoreFile(final String userID, final String[] path)
            throws PathNotFoundException, RepositoryException
        {
            final ArrayList<String> array = new ArrayList<String>();
            jcrTemplate.execute(new JcrCallback()
            {
                public Object doInJcr(Session session) throws RepositoryException
                {
                	ArrayList<String> list = new ArrayList<String>();
                    for (int i = 0; i < path.length; i++)
                    {
                        Node nd = session.getRootNode().getNode(path[i]);
                        String unPath = nd.getProperty(FileConstants.NODE_PATH).getString().substring(1);
                        if(list.contains(unPath))
                        {
                        	array.add(path[i]);
                        }
                        else
                        {
                        	list.add(unPath);
                        }
                    }
                    return null;
                }
            });

            return array;
        }
    public ArrayList<String> getRecyclerRealPath(final String[] path)throws RepositoryException
    {
    	final ArrayList<String> array = new ArrayList<String>();
    	jcrTemplate.execute(new JcrCallback()
		{
			public Object doInJcr(Session session) throws RepositoryException
			{
				try{
					for(int i = 0; i<path.length; i++)
					{
						Node nd = session.getRootNode().getNode(path[i]);
                        String unPath = nd.getProperty(FileConstants.NODE_PATH).getString().substring(1);
                        String tokenPath = unPath;
                        
                        int aaa = path[i].indexOf('/');
               		    String userID = path[i].substring(0,aaa);
               		    
                        String realPath = userID + "/" + FileConstants.DOC;

                        Node docNode = session.getRootNode().getNode(realPath);
                        Node tempN = docNode;
                        int index;
                        
                        while ((index = tokenPath.indexOf("/")) != -1)
                        {
                            String name = tokenPath.substring(0, index);
                            tokenPath = tokenPath.substring(index + 1, tokenPath.length());
                            if (!name.equals(""))
                            {
                                realPath = createUnFolder(tempN, name);
                                tempN = session.getRootNode().getNode(realPath);
                            }
                        }

                        String newPath = realPath + "/" + tokenPath;
                        array.add(newPath);
					}
				}
				catch(Exception e)
                {
                	LogsUtility.error(e);
                }
				return null;
			}
		});
    	return array;
    }
    
    public ArrayList<String> undeleted(final String userID, final String[] path)
        throws RepositoryException
    {
        final ArrayList<String> array = new ArrayList<String>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                try
                {
                    for (int i = 0; i < path.length; i++)
                    {
                        Node nd = session.getRootNode().getNode(path[i]);
                        String unPath = nd.getProperty(FileConstants.NODE_PATH).getString().substring(1);
                        String tokenPath = unPath;
                        
                        int aaa = path[i].indexOf('/');
               		    String userID = path[i].substring(0,aaa);
               		    
                        String realPath = userID + "/" + FileConstants.DOC;

                        Node docNode = session.getRootNode().getNode(realPath);
                        Node tempN = docNode;
                        int index;
                        
                        while ((index = tokenPath.indexOf("/")) != -1)
                        {
                            String name = tokenPath.substring(0, index);
                            tokenPath = tokenPath.substring(index + 1, tokenPath.length());
                            if (!name.equals(""))
                            {
                                realPath = createUnFolder(tempN, name);
                                tempN = session.getRootNode().getNode(realPath);
                            }
                        }

                        String newPath = realPath + "/" + tokenPath;// + System.currentTimeMillis();
                        String destPath = "/" + newPath;

                        if (session.itemExists(destPath))
                        {
                            Node node = session.getRootNode().getNode(destPath.substring(1));
                            node.remove();
                        }

                        if (session.itemExists("/" + realPath))
                        {
                            Node nodes = session.getRootNode().getNode(realPath);
                            if (nodes.hasNode(tokenPath))
                            {
                            	nodes = nodes.getNode(tokenPath);
                            	nodes.remove();
                            }
                        }
                        session.move("/" + path[i], destPath);
                        Node tar = session.getRootNode().getNode(newPath);
                        if (tar != null)
                        {
                        	tar.setProperty(FileConstants.NODE_PATH, newPath);
                        }
                        //upDateLatelyList(docNode, newPath, unPath);
                        array.add(destPath);
                    }
                    session.save();
                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }
                return null;
            }
        });
        return array;
    }

    private String createUnFolder(Node docNode, String name) throws RepositoryException
    {
    	if (docNode.hasNode(name))
    	{
    		return docNode.getNode(name).getPath().substring(1);
    	}

        String nodeName = filterInvalidChar(name);
        Node folderNode = docNode.addNode(nodeName, FileConstants.NODE_FOLDER);
        folderNode.setProperty(FileConstants.AUTHOR, name);
        folderNode.setProperty(FileConstants.NAME, nodeName);
        folderNode.setProperty(FileConstants.CREATED, Calendar.getInstance());
        folderNode.setProperty(FileConstants.DELETED, Calendar.getInstance());

        String path = docNode.getPath().substring(1) + "/" + nodeName;
        folderNode.setProperty(FileConstants.NODE_PATH, path);

        return path;
    }

    public Fileinfo createFolder(final String realName, final String path, final String name) throws RepositoryException
    {
//    	userInfo.getRealName();
        final Fileinfo fileinfo = new Fileinfo();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	System.out.println("path==="+path);
//            	Node rootNode = session.getRootNode();
//            	if (!rootNode.hasNode(path))
//		        {
//    		        Node node;
//    		        String spaceid=path.substring(0,path.indexOf("/"));
//            		node = rootNode.addNode(spaceid, FileConstants.NODE_FOLDER);
//		        	createRootFolder(node, FileConstants.DOC, spaceid);
//		        	node.addNode(FileConstants.PUBLISHMENTS, FileConstants.NODE_FOLDER);
//		        	node.addNode(FileConstants.ARCHIVES, FileConstants.NODE_FOLDER);
//			        
//			        if (!node.hasNode(path))
//			        {
//			        	node.addNode(path, FileConstants.NODE_FOLDER);
//			        }
//		        }
                Node nd = session.getRootNode().getNode(path);
                String nodeName = filterInvalidChar(name);
                try
                {
                    nodeName = removeNodeBySameName(nd, nodeName, false);                    
                    Node folderNode = nd.addNode(nodeName, FileConstants.NODE_FOLDER);
                    String path = folderNode.getPath().substring(1);
                    folderNode.setProperty(FileConstants.AUTHOR, realName);
                    folderNode.setProperty(FileConstants.NAME, nodeName);
                    folderNode.setProperty(FileConstants.CREATED, Calendar.getInstance());
                    folderNode.setProperty(FileConstants.DELETED, Calendar.getInstance());

                    folderNode.setProperty(FileConstants.NODE_PATH, path + "/" + nodeName);
                    session.save();

                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }

                fileinfo.setFold(true);
                fileinfo.setFileName(name);
                fileinfo.setPathInfo(path + "/" + nodeName);
                fileinfo.setCreateTime(Calendar.getInstance().getTime());
                return null;
            }
        });
        return fileinfo;
    }

    public ArrayList<String> getFileList(final String userID, final String nodePath, final int flag)
        throws PathNotFoundException, RepositoryException
    {
        final ArrayList<String> array = new ArrayList<String>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	isSpaceExist(session, userID);
                Node docNode = session.getRootNode().getNode(userID.concat("/").concat(FileConstants.DOC));
                Value[] values = null;
                if (flag == 0)
                {
                    values = docNode.getProperty(FileConstants.OPENLIST).getValues();
                    int index;
                    for (int i = 1; i < values.length; i++)
                    {
	                    String value = values[i].getString();
	                    if ((index = value.indexOf("|")) > 0)
		                {
		                	array.add(value.substring(0, index));
		                }
		                else
		                {
		                	array.add(value);
		                }
                    }
                    return null;
                }
                else if (flag == 1)
                {
                    values = session.getRootNode().getProperty(FileConstants.LOCKLIST).getValues();
                }
                else if (flag == 2)
                {
                    values = docNode.getProperty(FileConstants.CLOSELIST).getValues();
                }

                for (int i = 1; i < values.length; i++)
                {
                    array.add(values[i].getString());
                }
                return null;
                
            }
        });
        return array;
    }

    public void lock(final String userID, final String path) throws RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node nd = session.getRootNode().getNode(path);
                nd.setProperty(FileConstants.LOCK, userID);
                Node userNode = session.getRootNode().getNode(userID.concat("/").concat(FileConstants.DOC));
                addFileList(session, userNode, path + "," + userID, 1);
                return null;
            }
        });
    }

    public void lock(final String userID, final String realName, final String path) throws RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node nd = session.getRootNode().getNode(path);
                nd.setProperty(FileConstants.LOCK, realName);
                Node userNode = session.getRootNode().getNode(userID.concat("/").concat(FileConstants.DOC));
                addFileList(session, userNode, path + "," + userID, 1);
                return null;
            }
        });
    }
    
    public boolean isLock(String userID, final String path) throws RepositoryException
    {
        final ArrayList<Boolean> array = new ArrayList<Boolean>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node nd = session.getRootNode().getNode(path);
                if (!nd.hasProperty(FileConstants.LOCK) || nd.getProperty(FileConstants.LOCK).getString().equals(""))
                {
                    array.add(Boolean.FALSE);
                }
                else
                {
                    array.add(Boolean.TRUE);
                }
                return null;
            }
        });
        return array.get(0).booleanValue();
    }

    public void unLock(final String userID, final String path) throws RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node nd = session.getRootNode().getNode(path);
                nd.setProperty(FileConstants.LOCK, "");
                removeFileList(userID, session, path + "," + userID, 1);
                return null;
            }
        });
    }

    public void removeFileList(String userID, Session session, String path, int flag)
        throws ValueFormatException, PathNotFoundException, RepositoryException
    {
    	if (flag == 0)
    	{
    		removeOpenedFile(userID, path);
    		return;
    	}
    	String userPath = FileUtils.getPreName(path);
        Node docNode = session.getRootNode().getNode(userPath.concat("/").concat(FileConstants.DOC));
        Value[] values = null;
        if (flag == 0)
        {
            values = docNode.getProperty(FileConstants.OPENLIST).getValues();
        }
        else if (flag == 1)
        {
            values = session.getRootNode().getProperty(FileConstants.LOCKLIST).getValues();
        }
        else if (flag == 2)
        {
            values = docNode.getProperty(FileConstants.CLOSELIST).getValues();
        }
        ArrayList<String> array = new ArrayList<String>();
        for (int i = 0; i < values.length; i++)
        {
            array.add(values[i].getString());
        }

        if (array.contains(path))
        {
            array.remove(path);
        }
        String[] listFile = array.toArray(new String[array.size()]);
        if (flag == 0)
        {
            docNode.setProperty(FileConstants.OPENLIST, listFile);
        }
        else if (flag == 1)
        {
            session.getRootNode().setProperty(FileConstants.LOCKLIST, listFile);
        }
        else if (flag == 2)
        {
            docNode.setProperty(FileConstants.CLOSELIST, listFile);
        }
        session.save();

    }

    public synchronized void logOut(String userID) throws RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                if (session != null)
                {
                    try
                    {
                        session.logout();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        // transactionManager.remove(userID);
                        // sessionManager.remove(userID);
                    }
                }
                return null;
            }
        });

    }

    public DataHolder searchFile(final String filePath, final String[] contents,
        final Calendar from, final Calendar to, final int start, final int number,
        final String[] fileName) throws RepositoryException
    {
        final ArrayList<DataHolder> arrayList = new ArrayList<DataHolder>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                QueryParams params = new QueryParams();
                if (contents[0] != null)
                {
                    params.setName("%" + contents[0] + "%");
                }
                if (contents[2] != null)
                {
                    params.setAuthor(contents[2]);
                }
                if (contents[3] != null)
                {
                    params.setTitle("%" + contents[3] + "%");
                }
                if (contents[4] != null)
                {
                    params.setKeywords("%" + contents[4] + "%");
                }
                if (contents[5] != null)
                {
                    params.setContent(contents[5]);
                }
                if (contents[7] != null)
                {
                    params.setMimeType(contents[7]);
                }
                if (contents[11] != null)
                {
                    params.setImportant(contents[11]);
                }

                if (from != null)
                {
                    params.setLastModifiedFrom(from);
                    params.setLastModifiedTo(to);
                }

                DataHolder holder = null;
                try
                {
                    holder = find(session, "/" + filePath, params, false, start, number, fileName);
                    arrayList.add(holder);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        });
        return arrayList.get(0);

    }
    
    /**
     * 查询分为2种，一是查某个文件夹内，二是只查某个文件
     */
    public DataHolder searchFile(final Fileinfo[] fileinfo, final int index, final String userID, final String contents,
        final int start, final int number) throws RepositoryException
    {
    		DataHolder holder = new DataHolder();
    		final ArrayList<Object> array = new ArrayList<Object>();
    		final ArrayList<Integer> searchcount = new ArrayList<Integer>(1);
    		searchcount.add(new Integer(0));
            jcrTemplate.execute(new JcrCallback()
            {
                public Object doInJcr(Session session) throws RepositoryException
                {
                	int nn = 0;
                    int endIndex = start + number;
                    int scount = searchcount.get(0);
                	for (int i = 0; i < fileinfo.length; i++) {
					QueryParams params = new QueryParams();
                    if (index == 0 && contents != null)
                    {
                        params.setName("%" + contents + "%");
                        params.setAuthor(contents);
                        params.setTitle("%" + contents + "%");
                        params.setKeywords("%" + contents + "%");
                        params.setContent(contents);
                    }
                    else if (index == 1 && contents != null)
                    {
                        params.setName("%" + contents + "%");
                        params.setContent(contents);
                    }
                    else if (index == 2 && contents != null)
                    {
                        params.setName("%" + contents + "%");
                    }
					try {
						String spath = "/" + fileinfo[i].getPathInfo();
						String statement = prepareStatement(spath, params, false, fileinfo[i].isFold());
						if (statement != null && !statement.equals(""))
			            {
			                Workspace workspace = session.getWorkspace();
			                QueryManager queryManager = workspace.getQueryManager();
			                Query query = queryManager.createQuery(statement, Query.XPATH);
			                javax.jcr.query.QueryResult result = query.execute();
			                //
			                try
			                {
			                    NodeIterator iter = result.getNodes();
			                    scount += iter.getSize();
			                    while (iter.hasNext())
			                    {
			                        Node tempNode = iter.nextNode();
			                        if (nn >= start)
			                        {
			                            Fileinfo fileInfo = getFile(tempNode);
			                            array.add(fileInfo);
			                        }
			                        nn++;
			                        if (nn >= endIndex)
			                        {
			                            break;
			                        }
			                    }
			                }
			                catch(javax.jcr.RepositoryException e)
			                {
			                    throw new RepositoryException(e.getMessage(), e);
			                }
			                
			            }
					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
                searchcount.remove(0);
                searchcount.add(new Integer(scount));
				return null;
			}
            });
            holder.setFilesData(array);
            holder.setIntData(searchcount.get(0));
            return holder;
        }
    
    /**
     * 查询分为2种，一是查某个文件夹内，二是只查某个文件
     */
    public DataHolder searchFile(final Fileinfo[] fileinfo, final String[] contents,
            final Calendar from, final Calendar to, final int start, final int number) throws RepositoryException
        {
    		DataHolder holder = new DataHolder();
    		final ArrayList<Object> array = new ArrayList<Object>();
    		final ArrayList<Integer> searchcount = new ArrayList<Integer>(1);
    		searchcount.add(new Integer(0));
            jcrTemplate.execute(new JcrCallback()
            {
                public Object doInJcr(Session session) throws RepositoryException
                {
                	int nn = 0;
                    int endIndex = start + number;
                    int scount = searchcount.get(0);
                	for (int i = 0; i < fileinfo.length; i++) {
					QueryParams params = new QueryParams();
					if (contents[0] != null) {
						params.setName("%" + contents[0] + "%");
					}
					if (contents[2] != null) {
						params.setAuthor(contents[2]);
					}
					if (contents[3] != null) {
						params.setTitle("%" + contents[3] + "%");
					}
					if (contents[4] != null) {
						params.setKeywords("%" + contents[4] + "%");
					}
					if (contents[5] != null) {
						params.setContent(contents[5]);
					}
					if (contents[7] != null) {
						params.setMimeType(contents[7]);
					}
					if (contents[11] != null) {
						params.setImportant(contents[11]);
					}

					if (from != null) {
						params.setLastModifiedFrom(from);
						params.setLastModifiedTo(to);
					}

					try {
						String spath = "/" + fileinfo[i].getPathInfo();
						String statement = prepareStatement(spath, params, true, fileinfo[i].isFold());
						if (statement != null && !statement.equals(""))
			            {
			                Workspace workspace = session.getWorkspace();
			                QueryManager queryManager = workspace.getQueryManager();
			                Query query = queryManager.createQuery(statement, Query.XPATH);
			                javax.jcr.query.QueryResult result = query.execute();
			                //
			                try
			                {
			                    NodeIterator iter = result.getNodes();
			                    scount += iter.getSize();
			                    while (iter.hasNext())
			                    {
			                        Node tempNode = iter.nextNode();
			                        if (nn >= start)
			                        {
			                            Fileinfo fileInfo = getFile(tempNode);
			                            array.add(fileInfo);
			                        }
			                        nn++;
			                        if (nn >= endIndex)
			                        {
			                            break;
			                        }
			                    }
			                }
			                catch(javax.jcr.RepositoryException e)
			                {
			                    throw new RepositoryException(e.getMessage(), e);
			                }
			                
			            }
					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
                searchcount.remove(0);
                searchcount.add(new Integer(scount));
				return null;
			}
            });
            holder.setFilesData(array);
            holder.setIntData(searchcount.get(0));
            return holder;
        }
    

    public DataHolder find(Session session, String path, QueryParams params, boolean andFlag,
        int start, int number, String[] fileName) throws IOException, RepositoryException
    {
        return findPaginated(session, path, params, 0, FileConstants.MAX_SEARCH_RESULTS, andFlag, start, number,
            fileName);
    }

    public DataHolder findPaginated(Session session, String path, QueryParams params, int offset,
        int limit, boolean andFlag, int start, int number, String[] fileName) throws IOException,
        RepositoryException
    {
        String query = prepareStatement(path, params, andFlag, fileName);
        if (query.equals(""))
        {
            Node nd = session.getRootNode().getNode(path.substring(1));
            return getAllDocumentFiles(session, nd);
        }
        return findByStatementPaginated(session, query, "xpath", offset, limit, start, number,
            fileName);
    }

    private String prepareStatement(String path, QueryParams params, boolean andFlag,
        String[] fileName) throws IOException
    {
        StringBuffer sb = new StringBuffer();
        // boolean and = false;
        boolean firstFlag = false;

        // Clean params
        params.setTitle(params.getTitle() != null ? params.getTitle().trim() : "");
        params.setName(params.getName() != null ? params.getName().trim() : "");
        params.setContent(params.getContent() != null ? params.getContent().trim() : "");
        params.setKeywords(params.getKeywords() != null ? params.getKeywords().trim() : "");
        params.setMimeType(params.getMimeType() != null ? params.getMimeType().trim() : "");
        params.setAuthor(params.getAuthor() != null ? params.getAuthor().trim() : "");
        //params.setContext(params.getContext() != null ? params.getContext().trim() : "/" + path);
        
        // 解决数字结点搜索问题，
        String encodePath = "//";
        StringTokenizer st = new StringTokenizer(path, "/");
        if (st.hasMoreTokens())
        {
            encodePath += ISO9075.encode(st.nextToken());
        }
        while (st.hasMoreTokens())
        {
            encodePath +=  "/" + ISO9075.encode(st.nextToken());
        }
        params.setContext(params.getContext() != null ? params.getContext().trim() : encodePath);
        
        
        params.setProperties(params.getProperties() != null ? params.getProperties() : new HashMap());
        if (!params.getContent().equals("") || !params.getName().equals("")
            || !params.getKeywords().equals("") || !params.getMimeType().equals("")
            || !params.getAuthor().equals("") || !params.getProperties().isEmpty()
            || (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null)
            || !params.getTitle().equals("") || (params.getImportant() != null && !params.getImportant().equals("")))
        {
            if (fileName != null)
            {
                int index = params.getContext().lastIndexOf("/");
                sb.append(params.getContext().substring(0, index) + "//element(*,eiokm:document)[");
            }
            else
            {
                sb.append(params.getContext() + "//element(*,eiokm:document)[");
            }

            // Escape
            if (!params.getName().equals(""))
            {
                params.setName(escapeContains(params.getName()));
            }
            if (!params.getContent().equals(""))
            {
                params.setContent(escapeContains(params.getContent()));
            }
            if (!params.getKeywords().equals(""))
            {
                params.setKeywords(escapeContains(params.getKeywords()));
            }

            // Construct the query
            if (!params.getContent().equals(""))
            {
                if (firstFlag == true)
                {
                    if (andFlag)
                    {
                        sb.append(" and ");
                    }
                    else
                    {
                        sb.append(" or ");
                    }
                }
                sb.append("jcr:contains(eiokm:content,'" + "%" + params.getContent() + "%" + "')");
                firstFlag = true;
            }

            if (!params.getName().equals(""))
            {
                if (firstFlag == true)
                {
                    if (andFlag)
                    {
                        sb.append(" and ");
                    }
                    else
                    {
                        sb.append(" or ");
                    }
                }
                sb.append("jcr:like(@eiokm:name,'" + params.getName() + "')");
                firstFlag = true;
            }

            if (!params.getKeywords().equals(""))
            {
                if (firstFlag == true)
                {
                    if (andFlag)
                    {
                        sb.append(" and ");
                    }
                    else
                    {
                        sb.append(" or ");
                    }
                }
                sb.append("jcr:like(@eiokm:keywords,'" + params.getKeywords() + "')");
                firstFlag = true;
            }

            if (!params.getMimeType().equals(""))
            {
                if (firstFlag == true)
                {
                    if (andFlag)
                    {
                        sb.append(" and ");
                    }
                    else
                    {
                        sb.append(" or ");
                    }
                }
                sb.append("@eiokm:content/jcr:mimeType='" + params.getMimeType() + "'");
                firstFlag = true;
            }

            if (params.getImportant() != null && !params.getImportant().equals(""))
            {
                long important = Long.valueOf(params.getImportant());
                if (firstFlag == true)
                {
                    if (andFlag)
                    {
                        sb.append(" and ");
                    }
                    else
                    {
                        sb.append(" or ");
                    }
                }
                if (important == -1)
                {
                    sb.append("@eiokm:important>0");
                }
                else
                {
                    sb.append("@eiokm:important= " + params.getImportant());
                }

                firstFlag = true;
            }

            if (!params.getAuthor().equals(""))
            {
                if (firstFlag == true)
                {
                    if (andFlag)
                    {
                        sb.append(" and ");
                    }
                    else
                    {
                        sb.append(" or ");
                    }
                }
                sb.append("@eiokm:author='" + params.getAuthor() + "'");
                firstFlag = true;
            }

            if (!params.getTitle().equals(""))
            {
                if (firstFlag == true)
                {
                    if (andFlag)
                    {
                        sb.append(" and ");
                    }
                    else
                    {
                        sb.append(" or ");
                    }
                }
                // sb.append("@eiokm:content/eiokm:title='" + params.getTitle()
                // + "'");
                sb.append("jcr:like(@eiokm:content/eiokm:title,'" + params.getTitle() + "')");
                firstFlag = true;
            }

            if (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null)
            {
                if (firstFlag == true)
                {
                    if (andFlag)
                    {
                        sb.append(" and ");
                    }
                    else
                    {
                        sb.append(" or ");
                    }
                }
                sb.append("@eiokm:content/jcr:lastModified >= xs:dateTime('"
                    + ISO8601.format(params.getLastModifiedFrom()) + "')");
                sb.append(" and ");
                sb.append("@eiokm:content/jcr:lastModified <= xs:dateTime('"
                    + ISO8601.format(params.getLastModifiedTo()) + "')");
            }

            if (!params.getProperties().isEmpty())
            {
                HashMap metaMap = parseMetadata();

                for (Iterator it = params.getProperties().entrySet().iterator(); it.hasNext();)
                {
                    Entry ent = (Entry)it.next();
                    MetaData meta = (MetaData)metaMap.get(ent.getKey());

                    if (meta != null)
                    {
                        if (andFlag)
                            sb.append(" and ");

                        if (meta.getType() == MetaData.SELECT)
                        {
                            sb.append("@" + ent.getKey() + "='"
                                + escapeXPath(ent.getValue().toString()) + "'");
                        }
                        else
                        {
                            sb.append("jcr:contains(@" + ent.getKey() + ",'"
                                + escapeContains(ent.getValue().toString()) + "')");
                        }
                    }
                }
            }

            sb.append("] order by @jcr:score descending");
        }
        else
        {
            if (fileName != null)
            {
                int index = params.getContext().lastIndexOf("/");
                sb.append(params.getContext().substring(0, index) + "//element(*,eiokm:document)");
            }
        }

        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see es.git.openkm.module.SearchModule#findByStatementPaginated(java.lang.String,
     *      java.lang.String, java.lang.String, int, int)
     */
    public DataHolder findByStatementPaginated(Session session, String statement, String type,
        int offset, int limit, int start, int number, String[] fileName) throws RepositoryException
    {
        try
        {
            if (statement != null && !statement.equals(""))
            {
                Workspace workspace = session.getWorkspace();
                QueryManager queryManager = workspace.getQueryManager();
                Query query = queryManager.createQuery(statement, type);
                return executeQuery(session, query, offset, limit, start, number, fileName);
            }

        }
        catch(javax.jcr.RepositoryException e)
        {
            throw new RepositoryException(e.getMessage(), e);
        }
        return null;
    }

    private DataHolder getAllDocumentFiles(Session session, Node nd) throws PathNotFoundException,
        RepositoryException
    {
        ArrayList<Object> array = new ArrayList<Object>();
        getAllFiles(array, session, nd);
        DataHolder dataHolder = new DataHolder();
        dataHolder.setIntData(array.size());
        dataHolder.setFilesData(array);
        return dataHolder;
    }

    private void getAllFiles(ArrayList<Object> array, Session session, Node node)
        throws PathNotFoundException, RepositoryException
    {
        for (NodeIterator it = node.getNodes(); it.hasNext();)
        {
            Node child = it.nextNode();

            if (child.isNodeType(FileConstants.NODE_FILE))
            {
                Fileinfo fileInfo = getFile(child);
                array.add(fileInfo);
            }
            else if (child.isNodeType(FileConstants.NODE_FOLDER))
            {
                getAllFiles(array, session, child);
            }
        }

    }

    /**
     * Parse Metadata
     */
    public static HashMap parseMetadata() throws IOException
    {
        HashMap ret = new HashMap();
        Properties prop = new Properties();
        prop.load(new FileInputStream("PropertyGroupValues.properties"));

        for (Iterator it = prop.entrySet().iterator(); it.hasNext();)
        {
            Entry entry = (Entry)it.next();
            MetaData md = new MetaData();
            ArrayList al = new ArrayList();
            String values = (String)entry.getValue();

            if (values != null)
            {
                String[] value = values.split(",");
                for (int j = 0; j < value.length; j++)
                {
                    if (j == 0)
                    {
                        md.setType(Integer.parseInt(value[j]));
                    }
                    else
                    {
                        al.add(value[j]);
                    }
                }

                md.setValues(al);
                ret.put(entry.getKey(), md);
            }
        }

        return ret;
    }

    /**
     * Escape jcr:contains searchExp (view 6.6.5.2)
     * 
     * @param str
     *            The String to be escaped.
     * @return The escaped String.
     */
    private String escapeContains(String str)
    {
        String ret = str.replace("\\", "\\\\");
        ret = ret.replace("'", "\\'");
        ret = ret.replace("-", "\\-");
        ret = ret.replace("\"", "\\\"");
        ret = ret.replace("[", "\\[");
        ret = ret.replace("]", "\\]");
        ret = escapeXPath(ret);
        return ret;
    }

    /**
     * Escape XPath string
     * 
     * @param str
     *            The String to be escaped.
     * @return The escaped String.
     */
    private String escapeXPath(String str)
    {
        String ret = str.replace("'", "''");
        return ret;
    }

    /**
     * @param session
     * @param query
     */
    private DataHolder executeQuery(Session session, Query query, int offset, int limit, int start,
        int number, String[] fileName) throws RepositoryException
    {
        ArrayList<Object> array = new ArrayList<Object>();
        DataHolder holder = new DataHolder();
        try
        {
            javax.jcr.query.QueryResult result = query.execute();
            NodeIterator iter = result.getNodes();
            int nn = 0;
            int endIndex = start + number;
            
        	while (iter.hasNext())
            {
                Node tempNode = iter.nextNode();
                if (nn >= start)
                {
                    Fileinfo fileInfo = getFile(tempNode);
                    if (fileName != null)
                    {
                        for (int i = 0; i < fileName.length; i++)
                        {
                            //if (FileUtils.getName(path).equals(fileName[i]))
                        	if(fileInfo.getPathInfo().equalsIgnoreCase(fileName[i]))
                            {
                                array.add(fileInfo);
                            }
                        }
                    }
                    else
                    {
                        array.add(fileInfo);
                    }
                }
                nn++;
                if (nn >= endIndex)
                {
                    break;
                }
            }

            holder.setFilesData(array);
            holder.setIntData((int)iter.getSize());
            return holder;
        }
        catch(javax.jcr.RepositoryException e)
        {
            throw new RepositoryException(e.getMessage(), e);
        }
    }
    
    
    public DataHolder searchFile(final String path, final int index, final String userID, final String contents,
        final int start, final int number) throws RepositoryException
    {
        final ArrayList<DataHolder> arrayList = new ArrayList<DataHolder>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                QueryParams params = new QueryParams();
                DataHolder holder = null;
                if (contents.equals(""))
                {
                    Node nd = session.getRootNode().getNode(userID);
                    holder = getAllDocumentFiles(session, nd);
                    arrayList.add(holder);
                }
                else
                {
                    if (index == 0 && contents != null)
                    {
                        params.setName("%" + contents + "%");
                        params.setAuthor(contents);
                        params.setTitle("%" + contents + "%");
                        params.setKeywords("%" + contents + "%");
                        params.setContent(contents);
                    }
                    else if (index == 1 && contents != null)
                    {
                        params.setName("%" + contents + "%");
                        params.setContent(contents);
                    }
                    else if (index == 2 && contents != null)
                    {
                        params.setName("%" + contents + "%");
                    }
                    else
                    {
                        return null;
                    }
                    // Session session = sessionManager.get(userID);
                    try
                    {
                        holder = find(session, path, params, false, start,
                            number, null);
                        arrayList.add(holder);
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });
        return arrayList.get(0);
    }

    /**
     * 获取文件的属性及文件内容流。
     * @param path
     * @return
     * @throws RepositoryException
     */
    public Object[] getContentProperty(final String path)  throws RepositoryException
	{
	    Object obj = jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	            Node nd = session.getRootNode().getNode(path);
	            String date = nd.getProperty(FileConstants.LASTMODIFIED).getDate().toString();
	            String size = String.valueOf(nd.getProperty(FileConstants.SIZE).getLong());
	            Node contentNode = nd.getNode(FileConstants.NODE_CONTENT);
	            InputStream is = contentNode.getProperty(FileConstants.P_JCR_DATA).getStream();                
	            return new Object[] {date, size, is};
	        }
	    });
	    return (Object[])obj;
	}
    /**
     * 获取文档的相关属性数据
     * @param path
     * @return
     * @throws RepositoryException
     */
    public Object[] getFileProperty(final String path)  throws RepositoryException
	{
	    Object obj = jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	            Node nd = session.getRootNode().getNode(path);
	            String datetime = ""+nd.getProperty(FileConstants.LASTMODIFIED).getDate().getTimeInMillis();
	            String size = String.valueOf(nd.getProperty(FileConstants.SIZE).getLong());
	            String author=nd.getProperty(FileConstants.AUTHOR).toString();
	            String title=nd.getProperty(FileConstants.TITLE).toString();
	            String modifier="";
	            try
            	{
		            if (!nd.hasProperty(FileConstants.LASTMODIFIER))
		    		{
	            		nd.setProperty(FileConstants.LASTMODIFIER, NULLSTRING);
		    		}
		            modifier=nd.getProperty(FileConstants.LASTMODIFIER).toString();
	    		}
            	catch (Exception e)
            	{
            		e.printStackTrace();
            	}
	            return new Object[] {datetime, size, author,title,modifier};
	        }
	    });
	    return (Object[])obj;
	}
    public InputStream getContent(final String userName, final String path, final boolean isOpen)
        throws RepositoryException
    {
        // Session session = sessionManager.get(userID);
        //final ArrayList<InputStream> arrayList = new ArrayList<byte[]>();
        Object obj = jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	String userPath = FileUtils.getPreName(path);
                Node nd = session.getRootNode().getNode(path);
                Node contentNode = nd.getNode(FileConstants.NODE_CONTENT);
                InputStream is = contentNode.getProperty(FileConstants.P_JCR_DATA).getStream();                
                if (isOpen && !path.startsWith(FileConstants.WORKFLOW ))
                {
                    Node userNode = session.getRootNode().getNode(userPath + "/" + FileConstants.DOC);
                    String showPath = getShowPath(nd);
                    addLatelyList(userNode, path, showPath);
                    //addFileList(userPath, path, 0);
                    //addOpenedFile(userName, path);
                    session.save();
                }
                
                //user753 add 需要设置成最近阅读
                setRecentFile(session.getRootNode(), nd, userPath, FileConstants.OPENLIST);
                return is;
            }
        });
        return (InputStream)obj;
    }

    public DataHolder listFileinfos(final String userID, final String path, final int startIndex,
        final int number) throws RepositoryException
    {
        // Session session = sessionManager.get(userID);
        final DataHolder holder = new DataHolder();
        // synchronized(session)
        // {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	isSpaceExist(session, path);
                Node nd = session.getRootNode().getNode(path);
                NodeIterator iter = nd.getNodes();
                ArrayList<Object> files = new ArrayList<Object>();
                ArrayList<Object> folders = new ArrayList<Object>();

                int fileSize = (int)iter.getSize();
                boolean isTrash = path.endsWith(FileConstants.TRASH);
                int index = -1;
				while (iter.hasNext())
				{
					Node tempNode = iter.nextNode();
					Fileinfo fileInfo = getFile(tempNode, !isTrash);
					if (fileInfo.isFold())
					{
						index++;
						files.add(index, fileInfo);
						folders.add(fileInfo);
					}
					else
					{
						files.add(fileInfo);
					}
				}
                holder.setIntData(fileSize);
                holder.setFolderData(folders);
                ArrayList al = new ArrayList();
                if(!(startIndex <= 0 && number <= 0))
                {
                    int end = startIndex + number > fileSize ? fileSize : startIndex + number;
                    for(int i = startIndex; i < end; i++)
                    {
                        al.add(files.get(i));
                    }
                    holder.setFilesData(al);
                }
                else
                {
                    holder.setFilesData(files); 
                }
                return null;
            }
        });

        return holder;
    }

    public ArrayList<Fileinfo> listFileinfos(final String userID, final String path)
    throws RepositoryException
    {
    	return listFileinfos(userID, path, false);
    }
    
    public ArrayList<Fileinfo> listFileinfos(final String userID, final String path,final boolean isShare)
        throws RepositoryException
    {
        final ArrayList<Fileinfo> array = new ArrayList<Fileinfo>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	isSpaceExist(session, path);
                Node nd = session.getRootNode().getNode(path);

                NodeIterator iter = nd.getNodes();
                while (iter.hasNext())
                {
                    Node tempNode = iter.nextNode();
                    Fileinfo fileInfo = getFile(tempNode, false);
                    if (isShare)
                    {
                    	if (!fileInfo.isFold())
                    	{
                    		array.add(fileInfo);
                    	}
                    }
                    else
                    {
                    	array.add(fileInfo);
                    }
                }
                return null;
            }
        });

        return array;
    }

    public ArrayList<Object> getFileinfos(final String userID, final String[] filePath)
    throws RepositoryException
    {
    	return getFileinfos(null,userID, filePath);
    }
    public ArrayList<Object> getFileinfos(final HttpServletRequest req,final String userID, final String[] filePath)
        throws RepositoryException
    {
        // Session session = sessionManager.get(userID);
        final Fileinfo[] files = new Fileinfo[filePath.length];
        final ArrayList<Object> array = new ArrayList<Object>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	int index = -1;
                for (int i = 0; i < filePath.length; i++)
                {
                    if (!filePath[i].equals(""))
                    {
                        boolean isTrash = filePath[i].endsWith(FileConstants.TRASH);
                        Node nd = null;
                        try
                        {
                        	nd	= session.getRootNode().getNode(filePath[i]);
                        }
                        catch(Exception e)
                        {
                        	continue;
                        }

						if(nd == null)
                        {
                        	continue;
                        }
                        files[i] = getFile(nd, !isTrash);
                        
                        if(files[i].isFold())
                        {
                        	index++;
                        	array.add(index, files[i]);
                        }
                        else
                        {
                        	//孙爱华增加，解决docs下图片链接显示IP地址问题（目前没用）
                        	String imageUrl=files[i].getImageUrl();
                        	if (imageUrl!=null && req!=null)
                        	{
                        		if (imageUrl!=null && req!=null)
                             	{
                             		String fieldname=/*req.getScheme() + "://" + req.getServerName() + ":"
         	           		        + req.getServerPort() +*/ req.getContextPath()
         	           		        +"/"+imageUrl;
                             		files[i].setImageUrl(fieldname);
                             	}
                        	}
                        	array.add(files[i]);
                        }
                    }
                }
                return null;
            }
        });

        return array;
    }
    /**
     * 获取选中文件夹的具体文件数
     * @param foldPath，文件夹
     * @return
     * @throws RepositoryException
     */
    public Integer getFilenums(final String[] foldPath)
            throws RepositoryException
    {
		Object ret = jcrTemplate.execute(new JcrCallback()
		{
			public Object doInJcr(Session session) throws RepositoryException
			{
				try
				{
					int nums=0;
					for (int i=0;i<foldPath.length;i++)
					{
						int files=getDocumnetCount(session.getRootNode().getNode(foldPath[i]));
						nums+=files;
					}
					return nums;
				}
				catch (Exception e)
				{
					LogsUtility.error(e);
				}
				return 0;
			}
		});
		return ret != null ? (Integer) ret : 0;
    }
    
    /*
     * 信电局版本获取审阅文件
     */
    public ArrayList<Object> getReviewFileinfos(final HttpServletRequest req,final String userID, final String[] filePath)
        throws RepositoryException
    {
        // Session session = sessionManager.get(userID);
        final Fileinfo[] files = new Fileinfo[filePath.length];
        final ArrayList<Object> array = new ArrayList<Object>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	int index = -1;
                for (int i = 0; i < filePath.length; i++)
                {
                    if (!filePath[i].equals(""))
                    {
                    	String[] path = filePath[i].split(",");
                        boolean isTrash = path[0].endsWith(FileConstants.TRASH);
                        Node nd = null;
                        try
                        {
                        	nd	= session.getRootNode().getNode(path[0]);
                        }
                        catch(Exception e)
                        {
                        	continue;
                        }

						if(nd == null)
                        {
                        	continue;
                        }
                        files[i] = getFile(nd, !isTrash);
                        if(path.length > 1)
                        {
                            files[i].setFileId(Long.valueOf(path[1]));                        	
                        }
                        if(files[i].isFold())
                        {
                        	index++;
                        	array.add(index, files[i]);
                        }
                        else
                        {
                        	//孙爱华增加，解决docs下图片链接显示IP地址问题（目前没用）
                        	String imageUrl=files[i].getImageUrl();
                        	if (imageUrl!=null && req!=null)
                        	{
                        		if (imageUrl!=null && req!=null)
                             	{
                             		String fieldname=/*req.getScheme() + "://" + req.getServerName() + ":"
         	           		        + req.getServerPort() +*/ req.getContextPath()
         	           		        +"/"+imageUrl;
                             		files[i].setImageUrl(fieldname);
                             	}
                        	}
                        	array.add(files[i]);
                        }
                    }
                }
                return null;
            }
        });

        return array;
    }

    private ArrayList<Object> getFileinfosByLately(final String userID, final String[] filePath)
        throws RepositoryException
    {
        // Session session = sessionManager.get(userID);
        final Fileinfo[] files = new Fileinfo[filePath.length];
        final ArrayList<Object> array = new ArrayList<Object>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                for (int i = 0; i < filePath.length; i++)
                {
                	//Shareinfo info =(Shareinfo)files[i];
                    if (filePath[i]!= null && !filePath[i].equals(""))
                    {
                        files[i] = new Fileinfo();
                        int index = filePath[i].indexOf("#");
                        String realPath = filePath[i].substring(0, index);
                        String showPath = filePath[i].substring(index + 1);

                        if (!session.itemExists("/" + realPath))
                        {
                            files[i].setFileSize((long)-1);
                            files[i].setImportant(0);
                        }
                        else
                        {
                            Node tempNode = session.getRootNode().getNode(realPath);
                            files[i] = getFile(tempNode, false);
                            if (tempNode.hasProperty(FileConstants.IMPORTANT))
                            {
                                files[i].setImportant(tempNode.getProperty(FileConstants.IMPORTANT).getLong());
                            }
                            else
                            {
                                files[i].setImportant(0);
                            }
                        }
                        files[i].setPathInfo(realPath);
                        files[i].setShowPath(showPath);
                        files[i].setFileName(FileUtils.getName(showPath));
                        //files[i].setShareCommet(files[i].getShareCommet());
                        //files[i].setShareRealName(files[i].getShareRealName());

                        array.add(files[i]);
                    }
                }
                return null;
            }
        });
        return array;
    }

    /**
     * 涓轰粬浜哄叡浜彁渚? (non-Javadoc)
     * 
     * @see com.evermore.weboffice.server.service.fs.IFileSystem#getFileinfos(java.lang.String,
     *      java.util.ArrayList)
     * 
     */
    public ArrayList<Object> getFileinfos(final String userID, final ArrayList<Shareinfo> filePath)
        throws RepositoryException
    {
        // Session session = sessionManager.get(userID);
        final ArrayList<Object> list = new ArrayList<Object>();
        // synchronized(session)
        // {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Iterator<Shareinfo> it = filePath.iterator();
                int index = -1;
                while (it.hasNext())
                {
                    Shareinfo info = it.next();
                    if (!info.getSharePath().equals(""))
                    {
                        String path = info.getSharePath();
                        boolean isTrash = path.endsWith(FileConstants.TRASH);
                        Node nd = null;
                        try
                        {
                        	nd = session.getRootNode().getNode(path);
                        }
                        catch(Exception e)
                        {
                        	continue;
                        }
						if(nd == null)
                        {
                        	continue;
                        }

                        Fileinfo files = getFile(nd, !isTrash);
                        files.setPermit(info.getPermit());
                        files.setIsNew(info.getIsNew());
                        files.setShareToUser(info.getShareToUser());
                        if (info.getShareRealName()!=null && info.getShareRealName().length()>0 )
                        {
                        	files.setAuthor(info.getShareRealName());
                        }
                        if ("1".equals(info.getApprovestate()))
                        {
                            files.setApprovestate("同意");
                        }
                        else if ("0".equals(info.getApprovestate()))
                        {
                            files.setApprovestate("不同意");
                        }
                        files.setApproveComment(info.getApproveComment());
                        String sc=info.getShareComment();
                		if (sc!=null)
                		{
                			sc=sc.replaceAll("\n", "<br>");
                		}
                        files.setShareCommet(sc);
                        if(files.isFold())
                        {
                        	index++;
                        	list.add(index, files);
                        }
                        else
                        {
                        	list.add(files);
                        }
                    }
                }
                return null;
            }
        });

        return list;
    }

    public ArrayList<Object> getMyDocuments(String email, int startIndex, int endIndex)
        throws RepositoryException
    {
        return listPageFileinfos(email, email.concat("/").concat(FileConstants.DOC), startIndex,
            endIndex);
    }
    
    public ArrayList<Object> getMyTrash(String email, int startIndex, int endIndex)
        throws RepositoryException
    {
        return listPageFileinfos(email, email.concat("/").concat(FileConstants.TRASH), startIndex,
            endIndex);
    }

    public void copy(final String[] srcPath, final String targetName, final boolean move)
        throws ItemExistsException, PathNotFoundException, AccessDeniedException,
        RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                String name = null;
                Node srcNode = null;
                Node destNode = null;
                try
                {
                    for (int i = 0; i < srcPath.length; i++)
                    {
                        srcNode = session.getRootNode().getNode(srcPath[i]);
                        
                        destNode = session.getRootNode().getNode(targetName);
                        
                        name = srcNode.getProperty(FileConstants.NAME).getString();

                        if (srcNode.isNodeType(FileConstants.NODE_FILE))
                        {
                            try
                            {
                                copyFile(srcNode, destNode, name, move);
                            }
                            catch(IOException e)
                            {
                                // Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Node newFolder = null;
                            try
                            {
                                newFolder = copyFolder(session, srcNode, destNode, name, targetName);
                            }
                            catch(IOException e)
                            {
                                // Auto-generated catch block
                                e.printStackTrace();
                            }
                            try
                            {
                                copyHelper(session, srcNode, newFolder, move);
                            }
                            catch(IOException e)
                            {
                                // Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                    return null;
                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }
                return null;

            }
        });
    }

    private void copyFile(Node srcNode, Node destNode, String newName, boolean move)
        throws ItemExistsException, PathNotFoundException, AccessDeniedException,
        RepositoryException, IOException
    {
        //此处可以处理移动带来的路径改变问题

        // String name = srcNode.getProperty(FileConstants.NAME).getString();
        removeNodeBySameName(destNode, filterInvalidChar(newName), true);

        String author = srcNode.getProperty(FileConstants.AUTHOR).getString();
        String keyWord = srcNode.getProperty(FileConstants.KEYWORDS).getString();
        String primalPath = srcNode.getProperty(FileConstants.NODE_PATH).getString();
        //        Calendar calendar = srcNode.getProperty(FileConstants.CREATED).getDate();
        Calendar deleteCalendar = srcNode.getProperty(FileConstants.DELETED).getDate();
        //        long important = srcNode.getProperty(FileConstants.IMPORTANT).getLong();

        Node srcDocumentContentNode = srcNode.getNode(FileConstants.NODE_CONTENT);
        String mimeType = srcDocumentContentNode.getProperty(FileConstants.MIMETYPE).getString();
        long size = srcDocumentContentNode.getProperty(FileConstants.SIZE).getLong();
        String title = srcDocumentContentNode.getProperty(FileConstants.TITLE).getString();
        Calendar lastCalendar = srcDocumentContentNode.getProperty(FileConstants.LASTMODIFIED).getDate();
        InputStream is = srcDocumentContentNode.getProperty(FileConstants.P_JCR_DATA).getStream();
                
        InputStream tis;
        Node thumbnailNode = srcNode.hasNode(FileConstants.NODE_THUMBNAIL) ? srcNode.getNode(FileConstants.NODE_THUMBNAIL) : null; 
        if (thumbnailNode!= null && thumbnailNode.hasProperty(FileConstants.P_JCR_DATA))
        {
        	tis = thumbnailNode.getProperty(FileConstants.P_JCR_DATA).getStream();
        }
        else 
        {
        	tis = null;
        }

        Node documentNode = null;

        String innerName = newName;// + System.currentTimeMillis();
        documentNode = destNode.addNode(filterInvalidChar(innerName), FileConstants.NODE_FILE);
//        if (move)
//        {
//            IFileSystemService fs = (IFileSystemService)ApplicationContext.getInstance().getBean(
//                IFileSystemService.NAME);
//            fs.moveFileInDB(srcNode.getPath().substring(1), destNode.getPath().substring(1) + "/"
//                + innerName);
//        }
        documentNode.setProperty(FileConstants.NAME, newName);
        documentNode.setProperty(FileConstants.AUTHOR, author);
        documentNode.setProperty(FileConstants.KEYWORDS, keyWord);
        documentNode.setProperty(FileConstants.CREATED, srcNode.getProperty(FileConstants.CREATED).getDate());
        documentNode.setProperty(FileConstants.DELETED, deleteCalendar);
        documentNode.setProperty(FileConstants.NODE_PATH, documentNode.getPath().substring(1));   //primalPath);
        documentNode.setProperty(FileConstants.STATUS,"");
        documentNode.setProperty(FileConstants.ISCHECK,"");
        documentNode.setProperty(FileConstants.ISENTRYPT,"");
        documentNode.setProperty(FileConstants.IMPORTANT, srcNode.getProperty(FileConstants.IMPORTANT).getLong());
        documentNode.setProperty(FileConstants.LOCK, "");
        // 为了得文件提速为了提高获得文件速度，把文件大小、标题、最后修改时间放到文件结点中
        documentNode.setProperty(FileConstants.TITLE, title);
        documentNode.setProperty(FileConstants.LASTMODIFIED, lastCalendar);
        documentNode.setProperty(FileConstants.SIZE, size);
        
        Node contentNode = documentNode.addNode(FileConstants.NODE_CONTENT, FileConstants.NODE_RESOURE);
        contentNode.setProperty(FileConstants.AUTHOR, newName);

        contentNode.setProperty(FileConstants.TITLE, title);
        contentNode.setProperty(FileConstants.P_JCR_DATA, is);
        contentNode.setProperty(FileConstants.LASTMODIFIED, lastCalendar);
        contentNode.setProperty(FileConstants.SIZE, size);
        contentNode.setProperty(FileConstants.MIMETYPE, mimeType);

        if (tis != null)
        {
        	Node thumbNode = documentNode.addNode(FileConstants.NODE_THUMBNAIL, FileConstants.NODE_RESOURE);
        	thumbNode.setProperty(FileConstants.AUTHOR, "");
        	thumbNode.setProperty(FileConstants.P_JCR_DATA, tis);
        }
        
        destNode.save();
        is.close();
    }

    private Node copyFolder(Session session, Node srcNode, Node destNode, String name,
        String targetName) throws ItemExistsException, PathNotFoundException,
        AccessDeniedException, RepositoryException, IOException
    {
        Node sameNode = getNodeByShowName(destNode, name, false);
        if (sameNode != null)
        {
            return sameNode;
        }
        String newName = filterInvalidChar(name);

        Node folderNode = destNode.addNode(newName, FileConstants.NODE_FOLDER);
        String path = destNode.getPath().substring(1);
        folderNode.setProperty(FileConstants.AUTHOR, FileUtils.getPreName(path));
        folderNode.setProperty(FileConstants.NAME, newName);
        folderNode.setProperty(FileConstants.NODE_PATH, srcNode.getProperty(FileConstants.NODE_PATH).getString());

        Calendar deleteCalendar = srcNode.getProperty(FileConstants.DELETED).getDate();
        //        Calendar createCalendar = srcNode.getProperty(FileConstants.CREATED).getDate();
        /*if (!srcNode.hasProperty(FileConstants.CREATED))
        {
            srcNode.setProperty(FileConstants.CREATED, Calendar.getInstance());
        }*/
        folderNode.setProperty(FileConstants.CREATED, srcNode.getProperty(FileConstants.CREATED).getDate());
        folderNode.setProperty(FileConstants.DELETED, deleteCalendar);

        //folderNode.setProperty(FileConstants.USERS_READ, NULLSTRING);
        //folderNode.setProperty(FileConstants.USERS_WRITE, NULLSTRING);
        //folderNode.setProperty(FileConstants.ROLES_READ, NULLSTRING);
        //folderNode.setProperty(FileConstants.ROLES_WRITE, NULLSTRING);
        destNode.save();
        return folderNode;
    }

    private void copyHelper(Session session, Node srcNode, Node destNode, boolean move)
        throws ItemExistsException, PathNotFoundException, AccessDeniedException,
        RepositoryException, IOException
    {
        for (NodeIterator it = srcNode.getNodes(); it.hasNext();)
        {
            Node child = it.nextNode();
            String name = child.getProperty(FileConstants.NAME).getString();

            if (child.isNodeType(FileConstants.NODE_FILE))
            {
                copyFile(child, destNode, name, move);
            }
            else if (child.isNodeType(FileConstants.NODE_FOLDER))
            {
                Node newFolder = copyFolder(session, srcNode, destNode, name, null);
                copyHelper(session, child, newFolder, move);
            }
        }
    }

    public Fileinfo getFile(final String userID, final String path) throws RepositoryException
    {
        Object o = jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	try
            	{
            		Node tempNode = session.getRootNode().getNode(path);
            		return getFile(tempNode, false);
            	}
            	catch (Exception e) 
            	{
					return null;
				}
            }
        });
        return o != null ? (Fileinfo)o : new Fileinfo();
    }

    public String move(final String userID, final String[] srcPath, final String targetName,final int iscover)
        throws RepositoryException
    {
        
        ArrayList<String> arrayList = (ArrayList<String>)jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	ArrayList<String> myList =  new ArrayList<String>();
                String tagPath = null;
                try
                {
                	if (iscover==1)
                	{
	                	for (int i = 0; i < srcPath.length; i++)
	                    {
	                        Node srcNode = session.getRootNode().getNode(srcPath[i]);
	                        copy(new String[]{srcPath[i]}, targetName, true);
	                        srcNode.remove();
	                    }
	                    session.save();
	                    myList.add(tagPath);
                	}
                	else
                	{
	                    for (int i = 0; i < srcPath.length; i++)
	                    {
	                        Node srcNode = session.getRootNode().getNode(srcPath[i]);
//                        copy(new String[]{srcPath[i]}, targetName, true);
//                        srcNode.remove();
	                        int index=srcPath[i].lastIndexOf("/");
	                        String name = srcPath[i].substring(index+1);
	                        srcNode.setProperty(FileConstants.NAME, name);
	    	                srcNode.setProperty(FileConstants.NODE_PATH, targetName+"/"+name);
	    	                if (srcNode.getParent() != null)
	    	                {
	    	                    jcrTemplate.move("/"+srcPath[i], "/"+targetName+"/"+name);
	    	                }
	                    }
	                    session.save();
	                    myList.add(tagPath);
                	}
                    return myList;
                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }
                return null;
            }
        });
        if (arrayList!=null && arrayList.size()>0)
        {
        	return arrayList.get(0);
        }
        return null;
    }

    private boolean canOper(String path)
    {
    	int index = path.indexOf("/");
        if (index <= 0)   // 第一级path不允许修改，删除
        {
        	return false;
        }
        String tempPath = path.substring(index + 1);
        index = tempPath.indexOf("/");
        if (index <= 0)   // 第二级path不允许修改，删除
        {
        	return false;
        }
        return true;
    }
    
    public String rename(final String userID, final String path, final String targetName)
        throws RepositoryException
    {
        return rename(userID, path, targetName,false);
    }
    public String rename(final String userID, final String path, final String targetName,boolean changePdf)
    throws RepositoryException
	{
    	if (!changePdf)//转换pdf时进行重命名不需要下面的操作——孙爱华
    	{
		    if (!canOper(path))
		    {
		    	return "";
		    }
    	}
	    String ret = (String)jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	            try
	            {
	            	String name = filterInvalidChar(targetName);
	                Node nd = session.getRootNode().getNode(path);
	                if (nd.getParent() != null)
	                {
	                    jcrTemplate.rename(nd, name);
	                }
	                nd.setProperty(FileConstants.NAME, name);
	                String newPath = nd.getPath().substring(1);
	                nd.setProperty(FileConstants.NODE_PATH, newPath);
	                session.save();
	                return newPath;
	            }
	            catch(Exception e)
	            { 
	            	LogsUtility.error(e);
	            	e.printStackTrace();
	            }
	            return path;
	        }
	    });
	    return ret;
	
	}
    public ArrayList<Fileinfo> getFolderFileinfos(final String[] filePath)
        throws RepositoryException
    {
        final ArrayList<Fileinfo> array = new ArrayList<Fileinfo>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                for (int i = 0; i < filePath.length; i++)
                {
                    if (!filePath[i].equals(""))
                    {
                        Node nd = session.getRootNode().getNode(filePath[i]);                        
                        if (nd.isNodeType(FileConstants.NODE_FOLDER))
                        {
                        	Fileinfo fileinfo = getFile(nd, false);
                            array.add(fileinfo);
                        }
                    }
                }
                return null;
            }
        });

        return array;
    }

    public void clear(final String userID, final String[] path) throws RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                try
                {
                    for (int i = 0; i < path.length; i++)
                    {
                        Node nd = session.getRootNode().getNode(path[i]);
                        // 系统监控数据
                        if (nd.isNodeType(FileConstants.NODE_FILE))
                        {
                            SysMonitorTask.instance().updateTodayDeleteDocumentCount(1);
                        }
                        nd.remove();
                    }
                    session.save();
                    return null;
                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }
                return null;
            }
        });


    }
    
    // 清空回收站。
    public void clearRecyler(final String path) throws RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                try
                {
                	Node nd = session.getRootNode().getNode(path);
                	NodeIterator child = nd.getNodes();
                	Node temp;
                	while (child.hasNext())
                	{
                		temp = child.nextNode();
                		// 系统监控数据
                        if (nd.isNodeType(FileConstants.NODE_FILE))
                        {
                            SysMonitorTask.instance().updateTodayDeleteDocumentCount(1);
                        }
                        temp.remove();
                    }
                    session.save();
                    return null;
                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }
                return null;
            }
        });


    }

    public void clearAll(final String userID) throws PathNotFoundException, RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                try
                {
                    Node nd = session.getRootNode().getNode(userID + "/" + FileConstants.TRASH);

                    NodeIterator iter = nd.getNodes();
                    while (iter.hasNext())
                    {
                        Node tempNode = iter.nextNode();
                        if (tempNode.isNodeType(FileConstants.NODE_FILE))
                        {
                            SysMonitorTask.instance().updateTodayDeleteDocumentCount(1);
                        }
                        tempNode.remove();
                    }

                    session.save();                    
                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }
                return null;
            }
        });


    }

    public DataHolder getWorkSpace(String userID) throws PathNotFoundException, RepositoryException
    {
        return getSize(userID, null);
    }

    private DataHolder getSize(final String userID, final String path)
        throws PathNotFoundException, RepositoryException
    {
        final long[] size = new long[]{0L, 0L, 0L};
        final DataHolder data = new DataHolder();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node nd;
                if (path == null)
                {
                    nd = session.getRootNode().getNode(userID + "/" + FileConstants.DOC);
                }
                else
                {
                    nd = session.getRootNode().getNode(path);
                }
                getHelper(size, session, nd);

                data.setLongData(size);
                return null;
            }
        });

        return data;
    }

    private void getHelper(long[] size, Session session, Node node) throws RepositoryException
    {
        for (NodeIterator it = node.getNodes(); it.hasNext();)
        {
            Node child = it.nextNode();

            if (child.isNodeType(FileConstants.NODE_FILE))
            {
                getSize(size, child);
            }
            else if (child.isNodeType(FileConstants.NODE_FOLDER))
            {
                size[1]++;
                getHelper(size, session, child);
            }
        }
    }

    private void getSize(long[] size, Node node) throws ValueFormatException,
        PathNotFoundException, RepositoryException
    {
        size[0]++;
        {
            size[2] += node.getProperty(FileConstants.SIZE).getLong();
        }
    }

    public ArrayList<Object> getLatelyFile(final String userID) throws RepositoryException
    {
		final ArrayList<String> files = new ArrayList<String>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node nd = session.getRootNode().getNode(userID.concat("/").concat(FileConstants.DOC));
                Value[] vs = nd.getProperty(FileConstants.NOTIFICATION).getValues();

                for (int i = 0; i < vs.length; i++)
                {
                	if(!files.contains(vs[i].getString()))
                	{
                		files.add(vs[i].getString());
                	}
                }
                return null;
            }
        });

        return getFileinfosByLately(userID, files.toArray(new String[files.size()]));
    }

    public void update(final String userID, final String path, final InputStream stream,
        final int fileSize, final String oldPath, final boolean isApplet) throws IOException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node nd = session.getRootNode().getNode(path);
                try
                {                    
                    nd.setProperty(FileConstants.LASTMODIFIED, Calendar.getInstance());
                    nd.setProperty(FileConstants.SIZE, fileSize);
                    
                    Node contentNode = nd.getNode(FileConstants.NODE_CONTENT);
                    contentNode.setProperty(FileConstants.P_JCR_DATA, stream);
                    contentNode.setProperty(FileConstants.SIZE, fileSize);
                    contentNode.setProperty(FileConstants.LASTMODIFIED, Calendar.getInstance());
                    
                    setRecentFile(session.getRootNode(), nd, userID, FileConstants.SAVELIST);
                    
                    session.save();
                    ArrayList openList = getFileList(userID, null, 0);
                    if(oldPath != null && !oldPath.equals(""))
                    {
                        removeFileList(userID, oldPath, 0);
                        removeFileList(userID, oldPath + "," + userID, 1);
                    }
                    SysMonitorTask.instance().updateTodayChangeDocunetCount(1);
                    tpt.addPath(path);
                }
                catch(Exception e)
                {
                	LogsUtility.error(e);
                }
                return null;
            }
        });
    }
    /*
     * 更新文件
     */
    public void updateFile(final String path, final InputStream stream,
            final int fileSize) throws IOException
        {
            jcrTemplate.execute(new JcrCallback()
            {
                public Object doInJcr(Session session) throws RepositoryException
                {
                    Node nd = session.getRootNode().getNode(path);
                    try
                    {                    
                        nd.setProperty(FileConstants.LASTMODIFIED, Calendar.getInstance());
                        nd.setProperty(FileConstants.SIZE, fileSize);
                        Node contentNode = nd.getNode(FileConstants.NODE_CONTENT);
                        contentNode.setProperty(FileConstants.P_JCR_DATA, stream);
                        contentNode.setProperty(FileConstants.SIZE, fileSize);
                        contentNode.setProperty(FileConstants.LASTMODIFIED, Calendar.getInstance());
                        session.save();
                        tpt.addPath(path);
                    }
                    catch(Exception e)
                    {
                    	LogsUtility.error(e);
                    }
                    return null;
                }
            });

        }
    public DataHolder getFileSize(String userID, String path) throws PathNotFoundException,
        RepositoryException
    {
        return getSize(userID, path);
    }

    public void removeFileList(final String userID, final String path, final int flag)
        throws ValueFormatException, PathNotFoundException, RepositoryException
    {
    	if (flag == 0)
    	{
    		removeOpenedFile(userID, path);
    		return;
    	}
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	String userPath = FileUtils.getPreName(path);
                Node docNode = session.getRootNode().getNode(userPath.concat("/").concat(FileConstants.DOC));
                Value[] values = null;
                if (flag == 0)
                {
                    values = docNode.getProperty(FileConstants.OPENLIST).getValues();
                }
                else if (flag == 1)
                {
                    values = session.getRootNode().getProperty(FileConstants.LOCKLIST).getValues();
                }
                else if (flag == 2)
                {
                    values = docNode.getProperty(FileConstants.CLOSELIST).getValues();
                }
                ArrayList<String> array = new ArrayList<String>();
                for (int i = 0; i < values.length; i++)
                {
                    array.add(values[i].getString());
                }

                if (array.contains(path))
                {
                    array.remove(path);
                }
                String[] listFile = array.toArray(new String[array.size()]);
                if (flag == 0)
                {
                    docNode.setProperty(FileConstants.OPENLIST, listFile);
                }
                else if (flag == 1)
                {
                    session.getRootNode().setProperty(FileConstants.LOCKLIST, listFile);
                }
                else if (flag == 2)
                {
                    docNode.setProperty(FileConstants.CLOSELIST, listFile);
                }
                session.save();
                return null;
            }
        });
    }

    /**
     * 此方法只为上传文件检查前调用.
     * @param path
     * @return
     * @throws RepositoryException
     */
    public List<String> getFileForUpload(final String path) throws RepositoryException
    {
        final ArrayList<String> array = new ArrayList<String>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node nd = session.getRootNode().getNode(path);
                NodeIterator iter = nd.getNodes();
                while (iter.hasNext())
                {
                    Node tempNode = iter.nextNode();
                    if (tempNode.isNodeType(FileConstants.NODE_FILE))
                    {
                        String str = tempNode.getProperty(FileConstants.NAME).getString();
                        //str += "*" + tempNode.getPath().substring(1);
                        array.add(str);
                    }
                }
                return null;
            }
        });            
           
        return array;
    }

    public boolean fileExists(final String parentPath, final String fileName)
        throws RepositoryException
    {
        Object o = jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	return session.itemExists("/" + parentPath + "/" + fileName);
            }
        });
        return (Boolean)o;
    }

    public boolean fileExists(final String path) throws RepositoryException
    {
        return (Boolean)jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                return session.itemExists("/" + path);
            }
        });
    }

    public boolean setFileImportant(final String[] path, final long important)
        throws RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                for (int i = 0; i < path.length; i++)
                {
                    Node nd = session.getRootNode().getNode(path[i]);
                    nd.setProperty(FileConstants.IMPORTANT, important);
                }
                session.save();
                return null;
            }
        });
        return true;
    }

    public long getFileImportant(final String path) throws RepositoryException
    {
        return (Long) jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node nd = session.getRootNode().getNode(path);
                return nd.getProperty(FileConstants.IMPORTANT).getLong();
            }
        });
    }

    public DataHolder getFileByImportant(String userID, long important, int start, int limit)
        throws RepositoryException
    {
        String[] contents = new String[12];
        for (int i = 0; i < 11; i++)
        {
            contents[i] = null;
        }
        contents[11] = "" + important;

        return searchFile(userID + "/Document", contents, null, null, start, limit, null);
    }
    
    public DataHolder getFileByImportant(String userID, long important,
			int start, int limit, String sort, String dir)
			throws RepositoryException
	{
		String[] contents = new String[12];
		contents[11] = "" + important;
		DataHolder dh = searchFile(userID + "/Document", contents, null, null, 0,
				Integer.MAX_VALUE, null);
		ArrayList<Object> list = dh.getFilesData();
		if (sort != null)
        {
            Collections.sort(list,
                new FileArrayComparator(sort, dir.equals("ASC") ? 1 : -1));
        }
		ArrayList filePath = new ArrayList(limit);
		for (int i = start; i < limit + start && i < list.size(); i++)
        {
            filePath.add(list.get(i));
        }
		filePath.add(0, list.size());
		dh.setFilesData(filePath);
		return dh;
	}
    
    public void clearFileList(final String userID, final int flag) throws RepositoryException
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node docNode = session.getRootNode().getNode(userID.concat("/").concat(FileConstants.DOC));
                if (flag == 0)
                {
                    docNode.setProperty(FileConstants.OPENLIST, NULLSTRING);
                }
                //登录时，遍历锁定列表，如果有本人锁定的文件，则取消锁定
                else if (flag == 1)
                {
                    ArrayList<String> lockList = getFileList(userID, userID, 1);
                    for (int i = 0; i < lockList.size(); i++)
                    {
                        String temp = lockList.get(i);
                        if (temp.substring(temp.indexOf(",") + 1).equals(userID))
                        {
                            Node node = session.getRootNode().getNode(temp.substring(0, temp.indexOf(",")));
                            node.setProperty(FileConstants.LOCK, "");
                            removeFileList(userID, temp, 1);
                        }
                    }
                }
                else if (flag == 2)
                {
                    docNode.setProperty(FileConstants.CLOSELIST, NULLSTRING);
                }
                session.save();
                return null;
            }
        });
    }
    
    public void addSaveFile(final String file, String userid)
    {
        final String userID = FileUtils.getPreName(userid);
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node node = session.getRootNode().getNode(userID.concat("/").concat(FileConstants.DOC));
                Value[] values = node.getProperty(FileConstants.SAVELIST).getValues();

                ArrayList<String> array = new ArrayList<String>();
                for (int i = 0; i < values.length; i++)
                {
                    array.add(values[i].getString());
                }
                if (!array.contains(file))
                {
                    array.add(file);
                }
                String[] listFile = array.toArray(new String[array.size()]);
                node.setProperty(FileConstants.SAVELIST, listFile);
                session.save();
                return null;
            }
        });
    }
    
    public String[] getSaveFile(final String userID)
    {
        final String jcruserID = FileUtils.getPreName(userID);
        final ArrayList<String> savedFiles = new ArrayList<String>();
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node node = session.getRootNode().getNode(jcruserID.concat("/").concat(FileConstants.DOC));
                Value[] values = node.getProperty(FileConstants.SAVELIST).getValues();
                for (int i = 0; i < values.length; i++)
                {
                    savedFiles.add(values[i].getString());
                }
                return null;
            }
        });
        return savedFiles.toArray(new String[0]);
    }
    
    public void removeSaveFile(final String userID, final String file)
    {
        final String jcruserID = FileUtils.getPreName(userID);
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node node = session.getRootNode().getNode(jcruserID.concat("/").concat(FileConstants.DOC));
                if(!node.hasProperty(FileConstants.SAVELIST))
                {
                    node.setProperty(FileConstants.SAVELIST, NULLSTRING);
                    session.save();
                    return null;
                }
                if(file == null || file.equals(""))
                {
                    node.setProperty(FileConstants.SAVELIST, NULLSTRING);               
                }
                else
                {

                    Value[] values = node.getProperty(FileConstants.SAVELIST).getValues();

                    ArrayList<String> array = new ArrayList<String>();
                    for (int i = 0; i < values.length; i++)
                    {
                        array.add(values[i].getString());
                    }
                    if(array.contains(file))
                    {
                        array.remove(file);
                    }
                    String[] listFile = array.toArray(new String[array.size()]);
                    node.setProperty(FileConstants.SAVELIST, listFile);
                }
                session.save();
                return null;
            }
        });
    }
    
    public String[] getLastModiferMsg(final String file)
    {
        String[] msg = (String[])jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                Node node = session.getRootNode().getNode(file);
                if (!node.isNodeType(FileConstants.NODE_FILE))
                {
                    return null;
                }
                else
                {
                    Node contentNode = node.getNode(FileConstants.NODE_CONTENT);
                    String[] temp = new String[2];
                    String modefier = "";
                    String modifier="";
    	            try
                	{
    	            	//为了兼容老的文件属性
    		            if (!contentNode.hasProperty(FileConstants.LASTMODIFIER))
    		    		{
    		            	contentNode.setProperty(FileConstants.LASTMODIFIER, NULLSTRING);
    		    		}
    		            contentNode.getProperty(FileConstants.LASTMODIFIER).getString();
    	    		}
                	catch (Exception e)
                	{
                		e.printStackTrace();
                	}
                    
                    String modiferDate = getFormateDate(contentNode.getProperty(FileConstants.LASTMODIFIED).getDate().getTime());
                    temp[0] = modefier;
                    temp[1] = modiferDate;
                    return temp;
                }
            }
        });
        return msg;
    }
    
    private String getFormateDate(Date date)
    {
            int year = date.getYear() + 1900;
            int month = date.getMonth() + 1;
            int day = date.getDate();
            int hour = date.getHours();
            int minute = date.getMinutes();
            String value = year + "-" + (month >= 10 ? month : ("0" + month)) + "-"
                + (day >= 10 ? day : ("0" + day)) + " "
                + (hour >= 10 ? hour : ("0" + hour)) + ":"
                + (minute >= 10 ? minute : ("0" + minute));
            return value;
    }
        
    
    /**
     * 导出文件系统
     * @return
     */
    public boolean exportSystemView(final String dir,  final boolean isCheckProperty)
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                try
                {
                	Node root = session.getRootNode();
                	NodeIterator ni = root.getNodes();
                	MD5 md5;
                	while(ni.hasNext())
                	{
	                    /*if (isCheckProperty)
	                    {
	                        checkProperty(session.getRootNode());
	                    }*/
                		Node node = ni.nextNode();
                		String fileName = node.getPath();
                		md5 = new MD5();
                		fileName = md5.getMD5ofStr(fileName);
                		//fileName = URLEncoder.encode(fileName, "utf-8");
                		File file = new File(dir + "/" + fileName);
	                    FileOutputStream out = new FileOutputStream(file);
	                    session.exportSystemView(node.getPath(), out, false, false);
	                    out.close();
                	}
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return false;
                }
     
                return true;
            }
        });
        return true;
    }    
    
    
    /**
     * 导出文件系统
     * @return
     */
    public boolean exportSystemView(final File file, final String path, final boolean isCheckProperty)
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                try
                {
                    if (!session.itemExists(path))
                    {
                        return false;
                    }
                    /*if (isCheckProperty)
                    {
                        checkProperty(session.getRootNode());
                    }*/
                    FileOutputStream out = new FileOutputStream(file);
                    session.exportSystemView(path, out, false, false);
                    out.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return false;
                }
     
                return true;
            }
        });
        return true;
    }    
    
    
    /**
     * 导入文件系统
     * @return
     */
    public boolean importSystemView(final File file, final boolean unProtected)
    {
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                try
                {
                    /*if (unProtected)
                    {
                        unProtected(session.getRootNode());
                    }*/
                    //System.out.println("file name = " + file.getName());
                    FileInputStream in = new FileInputStream(file);
                    if (in.available() <= 0)
                    {
                        in.close();
                        file.delete();
                        return false;
                    }
                    session.importXML("/", in, ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
                    session.save();
                    in.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });
        return true;
    }
    
    /*private void unProtected(Node node)  throws RepositoryException
    {
        NodeIterator it = node.getNodes();
        Node tempNode;
        while (it.hasNext())
        {
            tempNode = it.nextNode();
            //NodeDefImpl ndf = (NodeDefImpl)((NodeDefinitionImpl)tempNode.getDefinition()).unwrap();
            NodeDefinitionTemplate ndf = (NodeDefinitionTemplate)((NodeDefinitionImpl)tempNode.getDefinition()).unwrap();
            ndf.setProtected(false);
            unProtected(tempNode);
        }
    }*/
    
    /**
     * 得到jackrabbit的内容管理配置信息
     */
    public Properties getFileSystemConfig()
    {
        Properties config = (Properties)
        jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                try
                {
                   return ((RepositoryImpl)session.getRepository()).getConfig().getSecurityConfig().getLoginModuleConfig().getParameters();
                }
                catch (Exception e)
                {
                    
                }
                return null;
            }
        });
        return config;
    }
    
    /**
     * 过滤掉jackrabbit不认的字符
     */
    private String filterInvalidChar(String s)
    {
        //return s.replace('[', '^').replace(']', '^');
    	if(s != null)
    	{
        	return s.replaceAll(REGEX, REPLACE);    		
    	}
    	else 
    	{
    		return "";
    	}
    }
    
    public List<Fileinfo> getRecentRWtFile(final String userID,final String type)
    {
    	return getRecentRWtFile(null,userID,type);
    }
    
	public List<Fileinfo> getRecentRWtFile(final HttpServletRequest req,final String userID,final String type)
	{
    	List<Fileinfo> fileinfoList = (List<Fileinfo>) jcrTemplate.execute(new JcrCallback()
         {
             public Object doInJcr(final Session session) throws RepositoryException
             {
            	 try
            	 {
            	 List<Fileinfo> fileList = new ArrayList<Fileinfo>();
            	 List<String> deleteFilePath = new ArrayList<String>();
            	 Node rootNode = session.getRootNode();
            	 Node userNode = rootNode.getNode(userID);
            	 Node recentReadNode = userNode.getNode(FILE_LIST);
            	 Value[] values = recentReadNode.getProperty(type).getValues();
                 
                 for(int i=0;i<values.length;i++)
                 {
                	 if(null!=values[i].getString() && !"".equals(values[i].getString()))
                	 {
                		 Fileinfo fileinfo = valueToFileInfo(rootNode,values[i].getString());
                		 if(fileinfo!=null)
                		 {
                			 String imageUrl=fileinfo.getImageUrl();
                			//孙爱华增加，解决docs下图片链接显示IP地址问题（目前没用）
                         	if (imageUrl!=null && req!=null)
                         	{
                         		String fieldname=/*req.getScheme() + "://" + req.getServerName() + ":"
     	           		        + req.getServerPort() +*/ req.getContextPath()
     	           		        +"/"+imageUrl;
                         		fileinfo.setImageUrl(fieldname);
                         	}

                			 fileList.add(fileinfo);
                		 }
                		 else
                		 {
                			 deleteFilePath.add(values[i].getString());
                		 }
                	 }
                 }
                 if(!deleteFilePath.isEmpty())
                 {
                	 deleteFilePath(recentReadNode,type,deleteFilePath);
                 }
                 if(!fileList.isEmpty())
                 {
				    return fileList;
                 }
                 }
            	 catch(Exception e)
                 {
                	 LogsUtility.error(e);
                 }
                 return null;
            	 
                 
             }
            
	         /**
	          * 删除列表中文件
	          * @param recentReadNode
	          * @param deleteFilePath
	         * @throws RepositoryException 
	         * @throws PathNotFoundException 
	         * @throws ValueFormatException 
	          */
			private void deleteFilePath(Node recentReadNode,String type,
					List<String> deleteFilePath) throws ValueFormatException, PathNotFoundException, RepositoryException 
			{
				List<String> fileList = new ArrayList<String>();
				Value[] values = recentReadNode.getProperty(type).getValues();
                for(int i=0;i<values.length;i++)
                {
	               	 if(null!=values[i].getString() && !"".equals(values[i].getString()))
	               	 {
	               		 fileList.add(values[i].getString());
	               	 }
                }
                
                for(String path : deleteFilePath)
                {
                	if(fileList.contains(path))
                	{
                		fileList.remove(path);
                	}
                }
                
                String[] list = fileList.toArray(new String[fileList.size()]);
                recentReadNode.setProperty(type, list);
                recentReadNode.save();
				
			}
             
         }
    	 );
		return fileinfoList;
	}
    
    
    
    
    /**
     * 根据value值转换成FileInfo对象
     * @param string
     * @return
     * @throws RepositoryException 
     * @throws PathNotFoundException 
     */
    protected Fileinfo valueToFileInfo(Node rootNode,String path) throws PathNotFoundException, RepositoryException {
    	if(rootNode.hasNode(path))
    	{
    		Node node = rootNode.getNode(path);
    		Fileinfo fileinfo = resolverFileInfo(node);
    		return fileinfo;
    	}
		return null;
	}

    /**
     * 将文件节点装换成文件信息对象
     * @param node 文件节点
     * @return 文件信息对象
     * @throws ValueFormatException
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
	private Fileinfo resolverFileInfo(Node node) throws ValueFormatException, PathNotFoundException, RepositoryException 
	{
		Fileinfo fileInfo = getFile(node, false);
        fileInfo.setIsNew(1);
		return fileInfo;
	}


	/**
     * 为用户创建最新阅读和最新编辑的文件夹
     * @param rootNode 用户根节点
     * @param userID   用户ID
     * @throws RepositoryException 
     * @throws ConstraintViolationException 
     * @throws VersionException 
     * @throws LockException 
     * @throws NoSuchNodeTypeException 
     * @throws PathNotFoundException 
     * @throws ItemExistsException 
     */
    /*private void creatFileList(Node userNode, String userID) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException {
		Node fileNode = userNode.addNode(FILE_LIST,FileConstants.NODE_FOLDER);
		fileNode.setProperty(FileConstants.AUTHOR, userID);
		fileNode.setProperty(FileConstants.USERS_READ, new String[]{userID});
		fileNode.setProperty(FileConstants.USERS_WRITE, new String[]{userID});
		fileNode.setProperty(FileConstants.ROLES_READ, new String[]{});
		fileNode.setProperty(FileConstants.ROLES_WRITE, new String[]{});
		fileNode.setProperty(FileConstants.NOTIFICATION, new String[]{"", "", "", "", "", "", "", "", "",
            ""});
		fileNode.setProperty(FileConstants.OPENLIST, NULLSTRING);
		fileNode.setProperty(FileConstants.CLOSELIST, NULLSTRING);
		fileNode.setProperty(FileConstants.SAVELIST, NULLSTRING);
		
	}*/
    
    /**
     * 设置此文件为最近阅读的文件
     * @param rootNode 根节点
     * @param fileNode 文件节点
     * @param userID 用户ID
     * @throws RepositoryException 
     * @throws PathNotFoundException 
     */
    private void setRecentFile(Node rootNode, Node fileNode, String userID,String propertyName) throws PathNotFoundException, RepositoryException 
    {
		if(null!=userID && !"".equals(userID) &&rootNode.hasNode(userID))
		{
	    	Node userNode = rootNode.getNode(userID);
	    	if (!userNode.hasNode(FILE_LIST))
	    	{
	    		return;
	    	}
			Node fileListNode = userNode.getNode(FILE_LIST);
			Value[] values = fileListNode.getProperty(propertyName).getValues();
			String path = fileNode.getPath().substring(1);
			List<String> array = new ArrayList<String>();
	        for (int i = 0; i < values.length; i++)
	        {
	            array.add(values[i].getString());
	        }
	         if (array.contains(path))
	         {
	        	 array.remove(path); 
	         }
	         array.add(path);
	        String[] listFile = array.toArray(new String[array.size()]);
			fileListNode.setProperty(propertyName, listFile);
			fileListNode.save();
		}
	}

    private String prepareStatement(String path, QueryParams params, boolean andFlag,
            boolean isFold) throws IOException
        {
            StringBuffer sb = new StringBuffer();
            // boolean and = false;
            boolean firstFlag = false;

            // Clean params
            params.setTitle(params.getTitle() != null ? params.getTitle().trim() : "");
            params.setName(params.getName() != null ? params.getName().trim() : "");
            params.setContent(params.getContent() != null ? params.getContent().trim() : "");
            params.setKeywords(params.getKeywords() != null ? params.getKeywords().trim() : "");
            params.setMimeType(params.getMimeType() != null ? params.getMimeType().trim() : "");
            params.setAuthor(params.getAuthor() != null ? params.getAuthor().trim() : "");
            //params.setContext(params.getContext() != null ? params.getContext().trim() : "/" + path);
            
            // 解决数字结点搜索问题，

            String encodePath = "//";
            StringTokenizer st = new StringTokenizer(path, "/");
            if (st.hasMoreTokens())
            {
                encodePath += ISO9075.encode(st.nextToken());
            }
            while (st.hasMoreTokens())
            {
                encodePath +=  "/" + ISO9075.encode(st.nextToken());
            }
            params.setContext(params.getContext() != null ? params.getContext().trim() : encodePath);
            
            
            params.setProperties(params.getProperties() != null ? params.getProperties() : new HashMap());
            if (!params.getContent().equals("") || !params.getName().equals("")
                || !params.getKeywords().equals("") || !params.getMimeType().equals("")
                || !params.getAuthor().equals("") || !params.getProperties().isEmpty()
                || (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null)
                || !params.getTitle().equals("") || (params.getImportant() != null && !params.getImportant().equals("")))
            {
                if (!isFold)
                {
                    sb.append(params.getContext() + "[");
                }
                else
                {
                    sb.append(params.getContext() + "//element(*,eiokm:document)[");
                }

                // Escape
                if (!params.getName().equals(""))
                {
                    params.setName(escapeContains(params.getName()));
                }
                if (!params.getContent().equals(""))
                {
                    params.setContent(escapeContains(params.getContent()));
                }
                if (!params.getKeywords().equals(""))
                {
                    params.setKeywords(escapeContains(params.getKeywords()));
                }

                // Construct the query
                if (!params.getContent().equals(""))
                {
                    if (firstFlag == true)
                    {
                        if (andFlag)
                        {
                            sb.append(" and ");
                        }
                        else
                        {
                            sb.append(" or ");
                        }
                    }
                    sb.append("jcr:contains(eiokm:content,'" + "%" + params.getContent() + "%" + "')");
                    firstFlag = true;
                }

                if (!params.getName().equals(""))
                {
                    if (firstFlag == true)
                    {
                        if (andFlag)
                        {
                            sb.append(" and ");
                        }
                        else
                        {
                            sb.append(" or ");
                        }
                    }
                    sb.append("jcr:like(@eiokm:name,'" + params.getName() + "')");
                    firstFlag = true;
                }

                if (!params.getKeywords().equals(""))
                {
                    if (firstFlag == true)
                    {
                        if (andFlag)
                        {
                            sb.append(" and ");
                        }
                        else
                        {
                            sb.append(" or ");
                        }
                    }
                    sb.append("jcr:like(@eiokm:keywords,'" + params.getKeywords() + "')");
                    firstFlag = true;
                }

                if (!params.getMimeType().equals(""))
                {
                    if (firstFlag == true)
                    {
                        if (andFlag)
                        {
                            sb.append(" and ");
                        }
                        else
                        {
                            sb.append(" or ");
                        }
                    }
                    sb.append("@eiokm:content/jcr:mimeType='" + params.getMimeType() + "'");
                    firstFlag = true;
                }

                if (params.getImportant() != null && !params.getImportant().equals(""))
                {
                    long important = Long.valueOf(params.getImportant());
                    if (firstFlag == true)
                    {
                        if (andFlag)
                        {
                            sb.append(" and ");
                        }
                        else
                        {
                            sb.append(" or ");
                        }
                    }
                    if (important == -1)
                    {
                        sb.append("@eiokm:important>0");
                    }
                    else
                    {
                        sb.append("@eiokm:important= " + params.getImportant());
                    }

                    firstFlag = true;
                }

                if (!params.getAuthor().equals(""))
                {
                    if (firstFlag == true)
                    {
                        if (andFlag)
                        {
                            sb.append(" and ");
                        }
                        else
                        {
                            sb.append(" or ");
                        }
                    }
                    sb.append("@eiokm:author='" + params.getAuthor() + "'");
                    firstFlag = true;
                }

                if (!params.getTitle().equals(""))
                {
                    if (firstFlag == true)
                    {
                        if (andFlag)
                        {
                            sb.append(" and ");
                        }
                        else
                        {
                            sb.append(" or ");
                        }
                    }
                    // sb.append("@eiokm:content/eiokm:title='" + params.getTitle()
                    // + "'");
                    sb.append("jcr:like(@eiokm:content/eiokm:title,'" + params.getTitle() + "')");
                    firstFlag = true;
                }

                if (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null)
                {
                    if (firstFlag == true)
                    {
                        if (andFlag)
                        {
                            sb.append(" and ");
                        }
                        else
                        {
                            sb.append(" or ");
                        }
                    }
                    sb.append("@eiokm:content/jcr:lastModified >= xs:dateTime('"
                        + ISO8601.format(params.getLastModifiedFrom()) + "')");
                    sb.append(" and ");
                    sb.append("@eiokm:content/jcr:lastModified <= xs:dateTime('"
                        + ISO8601.format(params.getLastModifiedTo()) + "')");
                }

                if (!params.getProperties().isEmpty())
                {
                    HashMap metaMap = parseMetadata();

                    for (Iterator it = params.getProperties().entrySet().iterator(); it.hasNext();)
                    {
                        Entry ent = (Entry)it.next();
                        MetaData meta = (MetaData)metaMap.get(ent.getKey());

                        if (meta != null)
                        {
                            if (andFlag)
                                sb.append(" and ");

                            if (meta.getType() == MetaData.SELECT)
                            {
                                sb.append("@" + ent.getKey() + "='"
                                    + escapeXPath(ent.getValue().toString()) + "'");
                            }
                            else
                            {
                                sb.append("jcr:contains(@" + ent.getKey() + ",'"
                                    + escapeContains(ent.getValue().toString()) + "')");
                            }
                        }
                    }
                }

                sb.append("] order by @jcr:score descending");
            }
            else
            {
            	if (!isFold)
                {
                    sb.append(params.getContext());
                }
            	else
            	{
            		sb.append(params.getContext() + "//element(*,eiokm:document)");
            	}
            }

            return sb.toString();
        }


	public boolean copy(final String folderPath, final String srcPath,final String targetName ,boolean replace) 
	{
		if(replace)
		{
			try
			{
				copy(new String[]{srcPath}, folderPath, false);
			}
			catch(Exception e)
			{
				LogsUtility.error(e);			
				return false;
			}
			return true;
		}
		else{
			boolean result = (Boolean) jcrTemplate.execute(new JcrCallback()
	        {
	            public Object doInJcr(Session session) throws RepositoryException
	            {

	                Node srcNode = null;
	                try
	                {
	                  
	                        srcNode = session.getRootNode().getNode(srcPath);
                            
	                        session.getWorkspace().copy("/"+srcPath, "/"+folderPath+"/"+targetName);
	                        
	                        Node copyNode = session.getRootNode().getNode(folderPath+"/"+targetName);
	                        
	                        copyNode.setProperty(FileConstants.NAME, targetName);
	                        copyNode.setProperty(FileConstants.NODE_PATH, folderPath+"/"+targetName);
	                        //如果有状态，需要清除
                            if(srcNode.hasProperty(FileConstants.STATUS))
                            {
                          	  copyNode.setProperty(FileConstants.STATUS,"");
                            }
                            if(srcNode.hasProperty(FileConstants.ISCHECK))
                            {
                          	  copyNode.setProperty(FileConstants.ISCHECK,"");
                            }
                            if(srcNode.hasProperty(FileConstants.ISENTRYPT))
                            {
                            	copyNode.setProperty(FileConstants.ISENTRYPT,"");
                            }
	                        session.save();
	                    
	                }
	                catch(Exception e)
	                {
	                	LogsUtility.error(e);
	                	return false;
	                }
					return true;
	            }
	        });
			return result;
		}
		
	}

	
	public List<Fileinfo> getFolders(final String parentPath) 
	{
		 List<Fileinfo> resultList =
		(List<Fileinfo>) jcrTemplate.execute(new JcrCallback(){
			public Object doInJcr(Session session) throws RepositoryException
            {
                List<Fileinfo> fileList = new ArrayList<Fileinfo>();
                Node rootNode = session.getRootNode();
                try
                {
                  if(rootNode.hasNode(parentPath))
                  {
                	 Node pNode = rootNode.getNode(parentPath);
                     Iterator<Node> nodeIter = pNode.getNodes();
                     while(nodeIter.hasNext())
                     {
                    	 Node node = nodeIter.next();
                    	 
                    	 if(node.isNodeType(FileConstants.NODE_FOLDER))
                    	 {
	                    	 Fileinfo fileinfo = getFile(node, true);
	                    	 fileList.add(fileinfo);
                    	 }
                     }
                  }
                }
                catch(Exception e)
                {
                	e.printStackTrace();
                }
				return fileList;
		}
		}
		);
		return resultList;
	}    
    
	public List<String> getExistFiles(final String parentPath) 
	{
		final List<String> fileList = new ArrayList<String>();
		 jcrTemplate.execute(new JcrCallback()
	        {
	            public Object doInJcr(Session session) throws RepositoryException
	            {
	                try
	                {
	                	Node rootNode = session.getRootNode();
	                	if(rootNode.hasNode(parentPath))
	                	{
	                		Node folderNode = rootNode.getNode(parentPath);
	                		NodeIterator nodeit = folderNode.getNodes();
	                		while(nodeit.hasNext())
	                		{
	                			Node childNode=(Node) nodeit.next();
	                			if(childNode.isNodeType(FileConstants.NODE_FILE))
	                			{
	                				fileList.add(childNode.getName());
	                			}
	                		}
	                	}
	                }
	                catch (Exception e)
	                {
	                    e.printStackTrace();
	                }
					return null;
	     
	            }
	        });
	        return fileList;
	}
	/**
	 * 获取path下的文件或子目录。
	 * 如果该path既不是文件，也不是文件夹，则返回null；
	 * 如果path是文件，则返回值为path的字符串；
	 * 如果path是文件夹，则返回值为该文件夹下的所有文件及一级文件夹的list。 
	 * @param parentPath
	 * @return
	 */
	public Object getFiles(final String path) 
	{		
		Object ret = jcrTemplate.execute(new JcrCallback()
		{
			public Object doInJcr(Session session) throws RepositoryException
			{
				try
				{
					Node rootNode = session.getRootNode();
					if (rootNode.hasNode(path))
					{
						Node folderNode = rootNode.getNode(path);
						if (folderNode.isNodeType(FileConstants.NODE_FILE))
						{
							return path;
						}
						List<String> fileList = new ArrayList<String>();						
						NodeIterator nodeit = folderNode.getNodes();
						while (nodeit.hasNext())
						{
							Node childNode = (Node) nodeit.next();
							fileList.add(childNode.getPath().substring(1));
						}
						return fileList;
					}
					else
					{
						return null;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return null;
				}
			}
		});
		return ret;
	}
	/**
	 * 判断path是否已经存在，该path存在可能是文件夹存在，也可能是文件存在
	 * @param path
	 * @return
	 */
	public boolean isPathExist(final String path)
	{
		Boolean ret = (Boolean)jcrTemplate.execute(new JcrCallback()
		{
			public Object doInJcr(Session session) throws RepositoryException
			{
				try
				{
					Node rootNode = session.getRootNode();
					return rootNode.hasNode(path);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return false;

			}
		});
		return ret;
	}
	
	/**
	 * 判断文件是否存在
	 * @param path
	 * @return
	 */
	public boolean isFileExist(final String path)
	{
		Boolean ret = (Boolean)jcrTemplate.execute(new JcrCallback()
		{
			public Object doInJcr(Session session) throws RepositoryException
			{
				try
				{
					String path1 = path.replaceAll("[@:|*\\[\\]]", REPLACE);
					Node rootNode = session.getRootNode();					
					if (rootNode.hasNode(path1))
					{
						Node nd = rootNode.getNode(path1);
						return nd.isNodeType(FileConstants.NODE_FILE);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return false;

			}
		});
		return ret;
	}
	
	/**
	 * 判断文件夹是否存在	
	 * @param path
	 * @return
	 */
	public boolean isFoldExist(final String path)
	{
		Boolean ret = (Boolean)jcrTemplate.execute(new JcrCallback()
		{
			public Object doInJcr(Session session) throws RepositoryException
			{
				try
				{
					Node rootNode = session.getRootNode();					
					if (rootNode.hasNode(path))
					{
						Node nd = rootNode.getNode(path);
						return nd.isNodeType(FileConstants.NODE_FOLDER);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return false;

			}
		});
		return ret;
	}
	
	/*
	 * 无锡信电局创建审阅文档版本
	 */
	public String createReviewVersion(final String path,final String userName,final String remark,final String status) 
	{
		return (String)jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
                if(!srcNode.hasProperty("jcr:baseVersion"))
                {
                	srcNode.addMixin("mix:versionable");
                	session.save();
                }
                VersionHistory versionH = srcNode.getVersionHistory();
            	Version version = srcNode.checkin();
            	srcNode.checkout();
            	Version tempVersion = srcNode.checkin();//新建的一个版本的副本，只是为了方便删除版本
                if(versionH.hasVersionLabel("^<>^"))
                {
                	Version temp = versionH.getVersionByLabel("^<>^");
                	versionH.removeVersion(temp.getName());
                }
            	session.save();
            	String versionName = tempVersion.getName();
            	try
            	{
            		versionH.addVersionLabel(versionName, "^<>^",false);
            	}
            	catch(Exception e)
            	{
            		LogsUtility.error(e);
            	}
            	finally
            	{
	            	//checkVersion(session,srcNode);//此行代码用来检查版本的一些信息，调试时可以使用来查看
	            	srcNode.checkout();
            	}
				return version.getPath().substring(1);
            }
        });
		
	}
	
	public String createVersion(final String path,final String userName,final String remark,final String status) 
	{
		return (String)jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
                if(!srcNode.hasProperty("jcr:baseVersion"))
                {
                	srcNode.addMixin("mix:versionable");
                	session.save();
                }
                VersionHistory versionH = srcNode.getVersionHistory();
            	Version version = srcNode.checkin();
            	session.save();
            	String remark2 = filterInvalidChar(remark); 
            	String versionName = version.getName();
//            	System.out.println(versionName);
            	String labels = String.valueOf(new Date().getTime())+"&"+userName+"&"+status+"&"+remark2;
            	try
            	{
            		versionH.addVersionLabel(versionName, labels,false);
            	}
            	catch(Exception e)
            	{
            		LogsUtility.error(e);
            	}
            	finally
            	{
	            	//checkVersion(session,srcNode);//此行代码用来检查版本的一些信息，调试时可以使用来查看
	            	srcNode.checkout();
            	}
				return version.getPath().substring(1);
            }
        });
		
	}
	public List<Versioninfo> getAllVersion(final String path) 
	{
		List<Versioninfo> versionMap = (List<Versioninfo>) jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	List<Versioninfo> versionList = new ArrayList<Versioninfo>();
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
            	//checkVersion(session,srcNode);//此行代码用来检查版本的一些信息，调试时可以使用来查看
            	if(!srcNode.hasProperty("jcr:baseVersion"))
                {
                	srcNode.addMixin("mix:versionable");
                	session.save();
                }
            	VersionHistory vh = srcNode.getVersionHistory();
            	VersionIterator vt = vh.getAllVersions();
            	int i=1;
            	SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	while(vt.hasNext())
            	{
            		Versioninfo versioninfo = new Versioninfo();
            		Version version = vt.nextVersion();
            		if(!"jcr:rootVersion".equals(version.getName()))
            		{
            		String[] versionLabels = vh.getVersionLabels(version);
            		try
            		{
	            		versioninfo.setCreateName(versionLabels==null?"":versionLabels[0].split("&")[1]);
	            		versioninfo.setStatus(versionLabels==null?"":versionLabels[0].split("&")[2]);
	            		versioninfo.setRemark(versionLabels==null?"":versionLabels[0].split("&")[3]);	            		
            		}
            		catch (Exception e)
            		{
						e.printStackTrace();
					}
            		
            		versioninfo.setCreateTime(spf.format(new Date(version.getCreated().getTimeInMillis())));//此处请改进，需要自己设置时间比较稳妥
            		versioninfo.setName(version.getName());
            		//showNodes(version);
            		//System.out.println("*****"+version);
            		versioninfo.setPath(version.getPath().substring(1));
            		if(srcNode.getBaseVersion().getUUID().equals(version.getUUID()))
            		{
            			versioninfo.setRefVersion(true);
            		}
            		versionList.add(versioninfo);
            		i++;
            		}
            	}
				return versionList;
            }
        });
		return versionMap;
	}
	public Versioninfo getLastVersion(final String path) 
	{
		Versioninfo versionMap = (Versioninfo) jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	List<Versioninfo> versionList = new ArrayList<Versioninfo>();
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
            	//checkVersion(session,srcNode);//此行代码用来检查版本的一些信息，调试时可以使用来查看
            	if(!srcNode.hasProperty("jcr:baseVersion"))
                {
                	srcNode.addMixin("mix:versionable");
                	session.save();
                }
            	VersionHistory vh = srcNode.getVersionHistory();
            	VersionIterator vt = vh.getAllVersions();
            	int i=1;
            	SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	while(vt.hasNext())
            	{
            		Versioninfo versioninfo = new Versioninfo();
            		Version version = vt.nextVersion();
            		if(!"jcr:rootVersion".equals(version.getName()))
            		{
            		String[] versionLabels = vh.getVersionLabels(version);
            		try
            		{
	            		versioninfo.setCreateName(versionLabels==null?"":versionLabels[0].split("&")[1]);
	            		versioninfo.setStatus(versionLabels==null?"":versionLabels[0].split("&")[2]);
	            		versioninfo.setRemark(versionLabels==null?"":versionLabels[0].split("&")[3]);	            		
            		}
            		catch (Exception e)
            		{
						e.printStackTrace();
					}
            		
            		versioninfo.setCreateTime(spf.format(new Date(version.getCreated().getTimeInMillis())));//此处请改进，需要自己设置时间比较稳妥
            		versioninfo.setName(version.getName());
            		//showNodes(version);
            		//System.out.println("*****"+version);
            		versioninfo.setPath(version.getPath().substring(1));
            		if(srcNode.getBaseVersion().getUUID().equals(version.getUUID()))
            		{
            			versioninfo.setRefVersion(true);
            		}
            		versionList.add(versioninfo);
            		i++;
            		}
            	}
            	int size=versionList.size();
            	if (size>0)
            	{
            		return versionList.get(size-1);
            	}
				return null;
            }
        });
		return versionMap;
	}
	private boolean hasFileVersion(Node srcNode) throws RepositoryException
    {
        long count = getFileVersionCount(srcNode);
        if(count>1l)
        {
            return true;
        }
        return false;
    }

    private long getFileVersionCount(Node srcNode) throws RepositoryException
    {
        if(!srcNode.hasProperty("jcr:baseVersion"))
        {
            return 0;
        }
        VersionHistory vh = srcNode.getVersionHistory();
        VersionIterator vt = vh.getAllVersions();
        if (vt != null&&vt.getSize()>1)
        {
            return vt.getSize();
        }
        return 0;
    } 
    
    /**
	 * 信电局删除版本，审阅文档的
	 */
	public void delReviewVersions(final String path, final List<String> vNames) {
		jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
            	VersionHistory versionH = srcNode.getVersionHistory();
            	Version baseVersion = srcNode.getBaseVersion();
            	for(String vName : vNames)
                {
            		versionH.removeVersion(vName);
            		vName = String.valueOf(Double.valueOf(vName) + 0.1);
                	if(!vName.equals(baseVersion.getName()))
                	{
                		versionH.removeVersion(vName);
                	}
                	session.save();
                }
				return null;
            }
        });
		
	}

	
	/**
	 * 删除版本，版本的
	 */
	public void delVersions(final String path, final List<String> vNames) {
		jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
            	VersionHistory versionH = srcNode.getVersionHistory();
            	Version baseVersion = srcNode.getBaseVersion();
                for(String vName : vNames)
                {
                	if(!vName.equals(baseVersion.getName()))
                	{
                		versionH.removeVersion(vName);
                		//srcNode.removeMixin("mix:versionable");//如果需要全部移除，需要移除版本标记
                		session.save();
                	}
                
                }
                
                
				return null;
            }
        });
		
	}
	
	/**
	 * 删除版本，版本的
	 */
	public void delAllVersions(final String path) {
		jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
            	VersionHistory versionH = srcNode.getVersionHistory();
            	VersionIterator vt = versionH.getAllVersions();
            	Version baseVersion = srcNode.getBaseVersion();
            	while(vt.hasNext())
            	{
            		Version version = vt.nextVersion();
            		if(!"jcr:rootVersion".equals(version.getName()))
            		{
            			if(!version.getName().equals(baseVersion.getName()))
            			{
            				versionH.removeVersion(version.getName());
                    		session.save();
            			}
            		}
            	}                
				return null;
            }
        });
		
	}
	
	
	/**
	 * 恢复版本，同时要求创建新的版本信
	 */
	public void restoryVersions(final String path, final String vName,final String userName,final String remark) {
		jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
                srcNode.restore(vName,true);//恢复版本根据名称，后一个参数代表是否移除具有同样UUID的节点（请详细参看文档）
                session.save();
                
                //再创建一个新的版本
                srcNode.checkout();
               // VersionHistory versionH = srcNode.getVersionHistory();
               // Version version = srcNode.checkin();
            	//session.save();
            	//String labels = String.valueOf(new Date().getTime())+"&"+userName+"&复原&"+remark;
            	//versionH.addVersionLabel(version.getName(), labels,false);
            	//checkVersion(session,srcNode);//此行代码用来检查版本的一些信息，调试时可以使用来查看
            	//srcNode.checkout();
              createVersion(path,userName,remark,"复原");
				return null;
            }
        });
		
	}
	
	
	/**
	 * 回滚版本，同时要求创建新的版本信
	 */
	public void rollbackVersions(final String path, final String vName,final String userName,final String remark) {
		jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
                srcNode.restore(vName,true);//恢复版本根据名称，后一个参数代表是否移除具有同样UUID的节点（请详细参看文档）
                session.save();
                
                //获取该版本的创建时间
                Version baseVersion = srcNode.getBaseVersion();
                long rollbacktime=0l;
                VersionHistory vh = srcNode.getVersionHistory();
            	VersionIterator vt = vh.getAllVersions();
            	while(vt.hasNext())
            	{
            		Version version = vt.nextVersion();
            		if(!"jcr:rootVersion".equals(version.getName()))
            		{
            			if(vName.equals(version.getName()))
            			{
            				rollbacktime = version.getCreated().getTimeInMillis();
            				break;
            			}
            		}
            	}
                //删除rollbacktime之后的版本
            	while(vt.hasNext())
            	{
            		Version version = vt.nextVersion();
            		if(!"jcr:rootVersion".equals(version.getName()))
            		{
            			if(version.getCreated().getTimeInMillis()>rollbacktime)
            			{
            				vh.removeVersion(version.getName());
                    		session.save();
            			}
            		}
            	}
            	
                //再创建一个新的版本
                srcNode.checkout();
              
                createVersion(path,userName,remark,"回滚");
				return null;
            }
        });
		
	}
	
	
	/**
	 * 修改
	 */
	public void updateVersionMemo(final String path,final String vname,final String memo)
	{
		jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
            	VersionHistory versionH = srcNode.getVersionHistory();
            	VersionIterator vt = versionH.getAllVersions();
            	//Version baseVersion = srcNode.getBaseVersion();
            	while(vt.hasNext())
            	{
            		Version version = vt.nextVersion();
            		if(!"jcr:rootVersion".equals(version.getName()))
            		{
            			if(vname.equals(version.getName()))
            			{
            				String[] versionLabels = versionH.getVersionLabels(version);
            				if(null==versionLabels||versionLabels.length==0)
            				{
            					continue;
            				}
            				String memo2 = filterInvalidChar(memo);       
            				String newLabel = versionLabels[0].substring(0,versionLabels[0].lastIndexOf("&")+1)+memo2;
            				versionH.removeVersionLabel(versionLabels[0]);  
            				versionH.addVersionLabel(vname, newLabel,false);
                    		session.save();
                    		break;
            			}
            		}
            	}                
				return null;
            }
        });
		
	}
	
	
	public String getVersionMemo(final String path,final String vname)
	{
		final StringBuilder builder = new StringBuilder();
		jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
            	Node srcNode = rootNode.getNode(path);
            	VersionHistory versionH = srcNode.getVersionHistory();
            	VersionIterator vt = versionH.getAllVersions();
            	//Version baseVersion = srcNode.getBaseVersion();
            	while(vt.hasNext())
            	{
            		Version version = vt.nextVersion();
            		if(!"jcr:rootVersion".equals(version.getName()))
            		{
            			if(vname.equals(version.getName()))
            			{
            				String[] versionLabels = versionH.getVersionLabels(version);
            				if(null==versionLabels||versionLabels.length==0)
            				{
            					continue;
            				}
            				builder.append(versionLabels[0].substring(versionLabels[0].lastIndexOf("&")+1, versionLabels[0].length()));
                    		break;
            			}
            		}
            	}
            	//checkVersion(session,srcNode);
				return null;
            }
        });
		return builder.toString();
	}
	
	
	 public InputStream getVersionContent(final String path,final String versionName)
     throws RepositoryException
     {
	     // Session session = sessionManager.get(userID);
	     //final ArrayList<InputStream> arrayList = new ArrayList<byte[]>();
	     Object obj = jcrTemplate.execute(new JcrCallback()
	     {
	         public Object doInJcr(Session session) throws RepositoryException
	         {
	                Node nd = session.getRootNode().getNode(path);
	                Version version = (Version) nd;
	                Node node = version.getNode("jcr:frozenNode");
	                Node contentNode = node.getNode(FileConstants.NODE_CONTENT);
	                InputStream is = contentNode.getProperty(FileConstants.P_JCR_DATA).getStream();
	                return is;
	         }
	     });
	     return (InputStream)obj;
	     //return arrayList.get(0);
     }	 
	 
	public InputStream getVersionContent1(final String path, final String versionName)
	{
		return (InputStream)jcrTemplate.execute(new JcrCallback()
		{
			public Object doInJcr(Session session)	throws RepositoryException
			{
				Node rootNode = session.getRootNode();
				Node srcNode = rootNode.getNode(path);
				VersionHistory vh = srcNode.getVersionHistory();
				VersionIterator vt = vh.getAllVersions();
				String name;
				while (vt.hasNext())
				{
					Version version = vt.nextVersion();
					name = version.getName();
					if (!"jcr:rootVersion".equals(name))
					{
						if (name.equalsIgnoreCase(versionName))
						{
							Node node = version.getNode("jcr:frozenNode");
			                Node contentNode = node.getNode(FileConstants.NODE_CONTENT);
			                InputStream is = contentNode.getProperty(FileConstants.P_JCR_DATA).getStream();
			                return is;
						}
					}
				}
				return null;
			}
		});
	}
	
	public void setNodeStatus(final String path, final String status,final String property) 
	{
		// TODO Auto-generated method stub
		
		  jcrTemplate.execute(new JcrCallback()
	        {
	            public Object doInJcr(Session session) throws RepositoryException
	            {
	            	Node fileNode = session.getRootNode().getNode(path);
	            	if(fileNode.hasProperty(property))
	            	{
	            		fileNode.setProperty(property, status);
	            	}
	            	fileNode.setProperty(FileConstants.LASTMODIFIED, Calendar.getInstance());
	            	session.save();
					return null;
	            }
	        });
	}
	
	public String getNodeStatus(final String path,final String property)
	{
		 final StringBuffer status=new StringBuffer();
		 jcrTemplate.execute(new JcrCallback()
	        {
	            public Object doInJcr(Session session) throws RepositoryException
	            {
	            	Node fileNode = session.getRootNode().getNode(path);
	            	if(fileNode.hasProperty(property))
                    {
	            		status.append( fileNode.getProperty(property).getString());
                    }
					return null;
	            }
	        });
		 return status.toString();
	}
	
	/**
	 * 可以调试时用来查看版本的一些信息，可以用来了解版本对象的一些成员变量等等
	 * @param session
	 * @param node
	 */
	private void checkVersion(Session session,Node node)
	{
		try {

			VersionHistory his = node.getVersionHistory();
			VersionIterator it = his.getAllVersions();
			while(it.hasNext())
			{
				Version ver = it.nextVersion();
				System.out.println(ver.getCreated().getTime().toLocaleString());
				showNodes(ver);
			}
		} catch (UnsupportedRepositoryOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void showNodes(Node node) throws RepositoryException
	{
		//PropertyIterator pIt = node.getProperties();
		//while(pIt.hasNext())
		//{
		//	Property p = pIt.nextProperty();
		//	String namep = p.getName();
		//	String pPath = p.getPath();
		//	try
		//	{
		//	Value value = p.getValue();
		//	System.out.println(namep+"-----"+pPath+"------"+value.toString());
		//	}catch(Exception e)
		//	{
		//		e.printStackTrace();
		//	}
		//}
		//Node no = node.getNode("jcr:system/jcr:versionStorage/cb/d9/0d/cbd90dae-1a9c-408a-9409-fa1585deb2f3/1.0");
		//System.out.println("----------------------------"+no);
		
		NodeIterator nIt = node.getNodes();
		
		while(nIt.hasNext())
		{
			Node cNode = nIt.nextNode();
			
			String path = cNode.getPath();
			String name = cNode.getName();
			PropertyIterator cpIt = cNode.getProperties();
			NodeIterator cNIt = cNode.getNodes();
//			while(cpIt.hasNext())
//			{
//				Property cp = cpIt.nextProperty();
//				String namep = cp.getName();
//				String pPath = cp.getPath();
//				System.out.println();
//				
//			}
			//cNode.getProperty("jcr:frozenNode");
			while(cNIt.hasNext())
			{
				Node cn =cNIt.nextNode();
				String namep = cn.getName();
				String pPath = cn.getPath();
				PropertyIterator properties = cn.getProperties();
				while(properties.hasNext())
				{
					Property p = properties.nextProperty();
					String namep2 = p.getName();
					String pPath2 = p.getPath();
					try
					{
						InputStream is =p.getStream();
						StringBuffer ss = new StringBuffer();
					if("jcr:data".equals(namep2))
					{						
						BufferedReader br = new BufferedReader(new InputStreamReader(is));						
						String temp="";
						while((temp=br.readLine())!=null)
						{
							ss.append(temp);
						}
					}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				System.out.println();
				
			}
		}
		
	}  
	
	public InputStream getFileContent(final String path)
    {
	    
	    Object obj = jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	            byte[] data = null;
	            Node nd = session.getRootNode().getNode(path);
	            Node contentNode = nd.getNode(FileConstants.NODE_CONTENT);
	            InputStream is = contentNode.getProperty(FileConstants.P_JCR_DATA).getStream();
	            
	            return is;
	        }
	    });
	    return (InputStream)obj;
    }
	
	
	public String getFileAuthor(final String path)
    throws RepositoryException
    {
		Object obj = jcrTemplate.execute(new JcrCallback()
	    {
	        public Object doInJcr(Session session) throws RepositoryException
	        {
	           
	            Node nd = session.getRootNode().getNode(path);
	            String author = nd.getProperty(FileConstants.AUTHOR).getString();	            
	            return author;
	        }
	    });
	    return (String)obj;
    }
	
	
	 public InputStream getContent(final String path)
     throws RepositoryException
     {
     // Session session = sessionManager.get(userID);
     //final ArrayList<InputStream> arrayList = new ArrayList<byte[]>();
     Object obj = jcrTemplate.execute(new JcrCallback()
     {
         public Object doInJcr(Session session) throws RepositoryException
         {
         	String userPath = FileUtils.getPreName(path);
             Node nd = session.getRootNode().getNode(path);
             Node contentNode = nd.getNode(FileConstants.NODE_CONTENT);
             InputStream is = contentNode.getProperty(FileConstants.P_JCR_DATA).getStream();                
            
             return is;
         }
     });
     return (InputStream)obj;
 }
	 /**
	  * 获取某个文件属性信息
	  * @param path
	  * @return
	  */
	 public Fileinfo getFileInfo(final String path){
		 String path2 = path;
		Fileinfo fileinfo = (Fileinfo)jcrTemplate.execute(new JcrCallback()
		    {
		        public Object doInJcr(Session session) throws RepositoryException
		        {
		                Node nd = session.getRootNode().getNode(path);
		                return  getFile(nd, false);
		           
		        }
		    });
		 
		 return fileinfo;
	 }
	 
	 ///////////////////////////////////// 企业文库的针对某一个空间的搜索，还没有排序////////////////////////////////////////
	 public DataHolder searchFile(final String path, final int index, final String spaceUID, final String contents,
		        final int start, final int number,final Long userid,final String sort,final String order) throws RepositoryException
		    {
		        final ArrayList<DataHolder> arrayList = new ArrayList<DataHolder>();
		        jcrTemplate.execute(new JcrCallback()
		        {
		            public Object doInJcr(Session session) throws RepositoryException
		            {
		                QueryParams params = new QueryParams();
		                DataHolder holder = null;
		                if (contents.equals(""))
		                {
		                    /*Node nd = session.getRootNode().getNode(spaceUID);
		                    //TODO .... 
		                    holder = getAllDocumentFiles(session, nd);
		                    arrayList.add(holder);*/
		                	//当关键字为空时，不应搜出企业文库的全部文档
		                	arrayList.add(new DataHolder());
		                }
		                else
		                {
		                    if (index == 0 && contents != null)
		                    {
		                        params.setName("%" + contents + "%");
		                        params.setAuthor(contents);
		                        params.setTitle("%" + contents + "%");
		                        params.setKeywords("%" + contents + "%");
		                        params.setContent(contents);
		                    }
		                    else if (index == 1 && contents != null)
		                    {
		                        params.setName("%" + contents + "%");
		                        params.setContent(contents);
		                    }
		                    else if (index == 2 && contents != null)
		                    {
		                        params.setName("%" + contents + "%");
		                    }
		                    else
		                    {
		                        return null;
		                    }
		                    // Session session = sessionManager.get(userID);
		                    try
		                    {
		                        holder = find(session, path, params, false, start,
		                            number, null,userid,sort,order);
		                        arrayList.add(holder);
		                    }
		                    catch(IOException e)
		                    {
		                        e.printStackTrace();
		                    }
		                }
		                return null;
		            }
		        });
		        return arrayList.get(0);
		    }
	 
	 private DataHolder find(Session session, String path, QueryParams params, boolean andFlag,
	            int start, int number, String[] fileName,Long userId,String sort,String order) throws IOException, RepositoryException
	        {
	            return findPaginated(session, path, params, 0, FileConstants.MAX_SEARCH_RESULTS, andFlag, start, number,
	                fileName,userId,sort,order);
	        }
	    
	 private DataHolder findPaginated(Session session, String path, QueryParams params, int offset,
	            int limit, boolean andFlag, int start, int number, String[] fileName,Long userId,String sort,String order) throws IOException,
	            RepositoryException
	        {
	            String query = prepareStatement(path, params, andFlag, fileName);
	            if (query.equals(""))
	            {
	                Node nd = session.getRootNode().getNode(path.substring(1));
	                //TODO...
	                return getAllDocumentFiles(session, nd);
	            }
	            return findByStatementPaginated(session, query, "xpath", offset, limit, start, number,
	                fileName,userId,sort,order);
	        }
	    private DataHolder findByStatementPaginated(Session session, String statement, String type,
	            int offset, int limit, int start, int number, String[] fileName,Long userId,String sort,String order) throws RepositoryException
	        {
	            try
	            {
	                if (statement != null && !statement.equals(""))
	                {
	                    Workspace workspace = session.getWorkspace();
	                    QueryManager queryManager = workspace.getQueryManager();
	                    Query query = queryManager.createQuery(statement, type);
	                    return executeQuery(session, query, offset, limit, start, number, fileName,userId,sort,order);
	                }

	            }
	            catch(javax.jcr.RepositoryException e)
	            {
	                throw new RepositoryException(e.getMessage(), e);
	            }
	            return null;
	        }
	    private DataHolder executeQuery(Session session, Query query, int offset, int limit, int start,
	            int number, String[] fileName,Long userId,String sort,String order) throws RepositoryException
	        {
	            ArrayList<Object> array = new ArrayList<Object>();
	            DataHolder holder = new DataHolder();
	            try
	            {
	                javax.jcr.query.QueryResult result = query.execute();
	                NodeIterator iter = result.getNodes();
	                int nn = 0;
	                int endIndex = start + number;
	                PermissionService service = (PermissionService)ApplicationContext.getInstance().getBean(PermissionService.NAME);
	                int count = 0;
	                while (iter.hasNext())
	                {
	                    Node tempNode = iter.nextNode();
	                    long permission = service.getFileSystemAction(userId, tempNode.getPath().substring(1), true);
	        			boolean flag = FlagUtility.isValue(permission, FileSystemCons.BROWSE_FLAG);
	        			if(!flag)
	        			{
	        				continue;
	        			}
	        			count++;
	                    if (sort != null || (nn >= start && nn < endIndex))
	                    {
	                        Fileinfo fileInfo = getFile(tempNode);
	                        if (fileName != null)
	                        {
	                            for (int i = 0; i < fileName.length; i++)
	                            {
	                                //if (FileUtils.getName(path).equals(fileName[i]))
	                            	if(fileInfo.getPathInfo().equalsIgnoreCase(fileName[i]))
	                                {
	                                    array.add(fileInfo);
	                                }
	                            }
	                        }
	                        else
	                        {
	                			array.add(fileInfo);
	                        }
	                    }
	                    nn++;
	                }
	                if(sort != null)
	                {
	                ArrayList al = new ArrayList();
	                if(array!=null && !array.isEmpty())
	                {
	                	Collections.sort(array, new FileArrayComparator(sort, order.equals("ASC")?1:-1));
	        	        for(int i = start; i < endIndex && i < array.size(); i++)
	        	        {
	        	            al.add(array.get(i));
	        	        }
	        	        holder.setFilesData(al);
	                }
	                }
	                else
	                {
	                
	                holder.setFilesData(array);
	                }
	                holder.setIntData(count);
	                return holder;
	            }
	            catch(javax.jcr.RepositoryException e)
	            {
	                throw new RepositoryException(e.getMessage(), e);
	            }
	        }
///////////////////////////////////// 企业文库的针对某一个空间的搜索，还没有排序 --- end ////////////////////////////////////////
	    
	
	// 缩略图转换    
	class ThumbnailProduct extends Thread
	{
		private Vector<String> paths;
		private boolean stop;
		private Object lock = new Integer(0);
		private boolean wait = true;
		private boolean start = false;

		public ThumbnailProduct()
		{
			paths = new Vector<String>();
		}

		public void addPath(String path)
		{
			if (!start)
			{
				start = true;
				start();
			}
			paths.add(path);
			synchronized(lock)
			{
				if (wait)
				{
					wait = false;
					lock.notifyAll();
				}				
			}
		}
		
		public void setStop()
		{
			stop = true;
		}

		public void run()
		 {
			 String temp;
			 while (!stop)
			 {				
				try
				{
					while (!paths.isEmpty())
					{
						temp = paths.remove(0);
						if (temp != null)
						{
							createThumnail(temp);
						}
					}
					synchronized(lock)
					{
						try
						{
							wait = true;
							lock.wait();
							wait = false;
						}
						catch(Exception e)
						{
							e.printStackTrace();								
						}
					}
				}
				catch(Throwable e)
				{
					LogsUtility.error(e);
				}
				
			 }
		 }
	}
	
	private void createThumnail(String path)
	{
		File file = null;
		File tarFile = null;
		try
		{
			//System.out.println("======="+path);
			int size;
			int index = path.lastIndexOf(".");
			int type = -1; 
			String suffix = "";
			if (index >= 0 && index + 1 < path.length())
			{
				suffix = path.substring(index + 1);
				suffix = suffix.toLowerCase();
				type = getType(suffix);
			}
			byte[] cont = new byte[1024 * 100];
			String dir = WebConfig.tempFilePath + File.separatorChar + "convert" + File.separatorChar;
			String name = "c" + System.currentTimeMillis();
			if (suffix.equalsIgnoreCase("pdf"))
			{
				name += ".pdf";
			}
			File des = new File(dir + "tar");
			if (!des.exists())
			{
				des.mkdirs();
			}
			file = new File(dir + name);
			RandomAccessFile out = new RandomAccessFile(file , "rw");
			InputStream is = getContent(path);
			while ((size = is.read(cont)) >= 0)
			{
				out.write(cont, 0, size);
			}
			is.close();
			out.close();			
			String srcPic = dir + "tar" + File.separatorChar + "1.jpg"; 
			String targPic = dir + "tar" + File.separatorChar + "a1.jpg"; 
			
//			File f=new File(srcPic);
//			if(!f.exists()){
//				return;
//			}
//			
//			tarFile = new File(targPic);
//			if(!tarFile.exists()){
//				return;
//			}
			if (type > 0)
			{
				float zoom = 1f;//type == 2 || type == 5 ? 0.5f : 0.31f;
				//dcs.core.ConvertDecorate.convertMStopic(dir + name, dir + "tar", 0, 0, "jpg", zoom);
				dcs.core.ConvertDecorate.convert(dir + name, dir + "tar", 0, 0, "jpg", zoom, suffix);
				scale(srcPic, targPic, 170, 250, type);
			}
			else if (type == 0)
			{
				scale(dir + name, targPic, 170, 250, 0);
			}
			else
			{
				return;
			}
			
			tarFile = new File(targPic);
			if(tarFile.exists()){
				FileInputStream indata = new FileInputStream(tarFile);
				createThumbnail(path, indata);
			}
						
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (file != null)
			{
				file.delete();
			}
			if (tarFile != null)
			{
				tarFile.delete();
			}
		}
	}
	
	// 缩放图片
	private  void scale(String srcImageFile, String result, int width, int height, int type) 
	{
		File f = null;
        try
        {            
            f = new File(srcImageFile);
            if(!f.exists()){
            	return;
            }
            BufferedImage bi = ImageIO.read(f);
            
            /*if (type == 1 || )    //  doc
            {
            	bi = bi.getSubimage(0, 0, bi.getWidth(), bi.getHeight() * 2 / 3);
            }*/
            /*if (type == 2 || type == 5)  // xls, txt
            {
            	//bi = bi.getSubimage(0, 0, Math.min(bi.getWidth(), width - 10), Math.min(bi.getHeight(), height - 10));
            }
            else */if (type == 3)   // ppt
            {
            	//itemp = bi;//.getSubimage(0, 0, width, height);
            	double wratio = bi.getWidth() > width  ? (new Integer(width)).doubleValue() / bi.getWidth() : 1;
            	height = (int)(bi.getHeight() * wratio); 
            }
            else
            {
            	width = bi.getWidth() > width ? width : bi.getWidth();
            	height = bi.getHeight() > height ? height : bi.getHeight();
	            //bi = bi.getSubimage(0, 0, bi.getWidth(), bi.getHeight() * 2 / 3);
	            //int oldHeight = bi.getHeight() > 500 ? 500 : bi.getHeight();
	             //bi = bi.getSubimage(0, 0, Math.min(bi.getWidth(), width), Math.min(bi.getHeight(), height));
            }
//            Image itemp = bi;
//            // 计算缩放比例 
//            double wratio = bi.getWidth() > width ? (new Integer(width)).doubleValue() / bi.getWidth() : 1;
//            double hratio = bi.getHeight() > height ? new Integer(height).doubleValue() / bi.getHeight() : 1;
//            double ratio = Math.min(wratio, hratio);            
//            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), 3);
//            itemp = op.filter(bi, null);
            //ImageIO.write((BufferedImage)itemp, "jpg", new File(result));
            
            
            /*double wratio = bi.getWidth() > width ? (new Integer(width)).doubleValue() / bi.getWidth() : 1;
            double hratio = bi.getHeight() > height ? new Integer(height).doubleValue() / bi.getHeight() : 1;
            double ratio = Math.min(wratio, hratio);*/
            ColorModel dstCM = bi.getColorModel();
            BufferedImage  dst = new BufferedImage(dstCM, 
            		dstCM.createCompatibleWritableRaster(width, height), dstCM.isAlphaPremultiplied(), null);

    		Image scaleImage = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH );
    		Graphics2D g = dst.createGraphics();
    		g.drawImage(scaleImage, 0, 0, width, height, null );
    		g.dispose();            
            ImageIO.write(dst, "jpg", new File(result));
            
            //ImageIO.write(new ScaleFilter(100,100), "jpeg", dest);
            
            /*ImageFilter cropFilter = new CropImageFilter(0, 0, width, height);
            itemp = bi.getScaledInstance(bi.getWidth(), bi.getHeight(), bi.SCALE_SMOOTH);
            Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(itemp.getSource(),  cropFilter));
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(img, 0, 0, width, height, null); // 绘制切割后的图
            g.dispose();
            ImageIO.write((BufferedImage) tag, "jpg", new File(result));*/
            
            
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        finally
        {
        	if (f != null)
        	{
        		f.delete();
        	}
        }
    }
	
	// 简单处理，根据后缀判断类型
	// 0为图片，1为doc，2为xls，3为ppt, 4为eio, -1为其他类型
	private int getType(String suffix)
	{
		suffix = suffix.toLowerCase();
		if (suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("bmp")
					|| suffix.equals("png") || suffix.equals("gif"))
		{
			return 0;
		}
		if (suffix.equals("doc") || suffix.equals("docx") || suffix.equals("dot"))
		{
			return 1;
		}
		if (suffix.equals("xls") || suffix.equals("xlsx"))
		{
			return 2;
		}
		if (suffix.equals("ppt") || suffix.equals("pptx"))
		{
			return 3;
		}		
		if (suffix.equals("eio") || suffix.equals("eit") || suffix.equals("eiw"))
		{
			return 4;
		}
		if (suffix.equals("txt"))
		{
			return 5;
		}
		if (suffix.equals("rtf"))
		{
			return 6;
		}
		if (suffix.equals("htm") || suffix.equals("html")
                || suffix.equals("shtml") || suffix.equals("mht"))
		{
			return 7;
		}
		if (suffix.equals("pdf"))
		{
			return 8;
		}
		return -1;
	}
	
	/**
     * 创建审阅空间。
     * @param spaceUID
     */
    private void createSignSpace(final String spaceUID)
    {
    	jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
            	Node rootNode = session.getRootNode();
		        Node node;
		        String path = FileConstants.SIGN_ROOT;
		        if (!rootNode.hasNode(path))
		        {
		        	node = rootNode.addNode(path, FileConstants.NODE_FOLDER);
		        	createRootFolder(node, FileConstants.DOC, path);
//		        	node.addNode(FileConstants.PUBLISHMENTS, FileConstants.NODE_FOLDER);
//		        	node.addNode(FileConstants.ARCHIVES, FileConstants.NODE_FOLDER);
		        }
		        else
		        {
		        	node = rootNode.getNode(path);
		        	if (!node.hasNode(FileConstants.DOC))    // 没有必要的兼容处理
		        	{
		        		createRootFolder(node, FileConstants.DOC, path);
		        	}
		        }
		        if (!node.hasNode(spaceUID))
		        {
		        	node.addNode(spaceUID, FileConstants.NODE_FOLDER);		        	
		        }
		        session.save();
		        return null;
            }            
        });
    }
}
