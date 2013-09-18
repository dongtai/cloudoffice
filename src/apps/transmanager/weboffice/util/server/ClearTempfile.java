package apps.transmanager.weboffice.util.server;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 
 * 文件注释
 * <p>
 * <p>
 * @author  孙爱华
 * @version 发改委
 * @see     
 * @since   2012-2-22
 */
public class ClearTempfile extends Thread
{
	private String path,startstr,endstr,str;//暂没用处
	
	/**
	 * 删除过滤条件的文件
	 * @param path 路径
	 * @param startstr 开始字符串
	 * @param endstr 结束字符串
	 * @param str 中间字符串
	 * 以上3个条件是或的关系
	 */
	public ClearTempfile(String path,String startstr,String endstr,String str)
	{
		this.path=path;
		this.startstr=startstr;
		this.endstr=endstr;
		this.str=str;

	}
	public void run()
	{
		try
		{
			File files = new File(path);
			if (files.isDirectory())
			{
				File[] filelist;
				DirFilter filter=new DirFilter(startstr,endstr,str);
				filelist=files.listFiles(filter);
				for(int i=0;i<filelist.length;i++){
					System.out.println("delete file's name========"+filelist[i].getName()+"======"+filelist[i].getPath());
					filelist[i].delete();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
/**
 * 过滤条件
 * 
 * <p>
 * <p>
 * @author  孙爱华
 * @version 发改委
 * @see     
 * @since   web1.0
 */
class DirFilter implements FilenameFilter
{
	private String startstr;
	private String endstr;
	private String str;
	 
	public DirFilter(String startstr,String endstr,String str){
		this.startstr=startstr;
		this.endstr=endstr;
		this.str=str;
	}
	
	public boolean accept(File fl,String path){
	 
		try
		{
			File file=new File(path);
			String filename=file.getName();
			boolean result=false;
			if (".*".equals(endstr))
			{
				return true;
			}
			else
			{
				if (startstr!=null)
				{
					result=filename.startsWith(startstr);
				}
				if (endstr!=null && !result)
				{
					result=filename.endsWith(endstr);
				}
				if (str!=null && !result)
				{
					result=filename.indexOf(str)!=-1;
				}
			}
			return result;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}