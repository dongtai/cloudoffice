package apps.transmanager.weboffice.rmi;

import java.util.Hashtable;

public class YOZOSession implements ISession
{
	public void setAttribute(String ID, String key, Object value)
	{	
		Hashtable<String, Object> attr = table.get(ID);
		if (attr == null)
		{
			attr = new Hashtable<String, Object>();
			table.put(ID, attr);
		}
		attr.put(key, value);
	}
	
	public Object getAttribute(String ID, String key)
	{
		Hashtable<String, Object> attr = table.get(ID);
		if (attr != null)
		{
			return attr.get(key);
		}
		return null;
	}
	
	private Hashtable<String, Hashtable<String, Object>> table = new Hashtable<String, Hashtable<String, Object>>();

}
