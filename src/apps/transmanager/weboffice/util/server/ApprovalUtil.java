package apps.transmanager.weboffice.util.server;

import java.util.ArrayList;
import java.util.List;


public class ApprovalUtil {

	public static Long strToLong(String id)
	{
		try
		{
			return Long.valueOf(id);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public static Integer strToInteger(String id)
	{
		try
		{
			return Integer.valueOf(id);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public static Boolean strToBoolean(String str)
	{
		try
		{
			return Boolean.parseBoolean(str);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public static List<Long> strsToLongs(List<String> strs)
	{
		try
		{
			List<Long> back=new ArrayList<Long>();
			for (int i=0;i<strs.size();i++)
			{
				back.add(Long.valueOf(strs.get(i)));
			}
			return back;
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
