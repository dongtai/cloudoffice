package apps.transmanager.weboffice.util.both;

import apps.transmanager.weboffice.constants.both.FileSystemCons;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
// 禁止继承类。
public final class FileSystemUtility
{
	/**
	 * 禁止实例化类
	 */
	private FileSystemUtility()
	{
	}
	
	/**
     * 判断是否有读权限
     * @param value
     * @return
     */
    public static boolean isRedAction(long value)
    {
    	return FlagUtility.isValue(value, FileSystemCons.READ_FLAG);
    }
    
    /**
     * 判断是否有写权限
     * @param value
     * @return
     */
    public static boolean isWriteAction(long value)
    {
    	return FlagUtility.isValue(value, FileSystemCons.WRITE_FLAG);
    }
    
    /**
     * 判断是否有打印权限
     * @param value
     * @return
     */
    public static boolean isPrintAction(long value)
    {
    	return FlagUtility.isValue(value, FileSystemCons.PRINT_FLAG);
    }
    
    /**
     * 判断是否有阅读权限
     * @param value
     * @return
     */
    public static boolean isBrowseAction(long value)
    {
    	return FlagUtility.isValue(value, FileSystemCons.BROWSE_FLAG);
    }
    
    /**
     * 判断是否有更名权限
     * @param value
     * @return
     */
    public static boolean isRenameAction(long value)
    {
    	return FlagUtility.isValue(value, FileSystemCons.RENAME_FLAG);
    }
    
    /**
     * 判断是否有另存权限
     * @param value
     * @return
     */
    public static boolean isSaveAsAction(long value)
    {
    	return FlagUtility.isValue(value, FileSystemCons.SAVE_AS_FLAG);
    }
    
    /**
     * 判断是否有下载权限
     * @param value
     * @return
     */
    public static boolean isDownloadAction(long value)
    {
    	return FlagUtility.isValue(value, FileSystemCons.DOWNLOAD_FLAG);
    }
    
    /**
     * 判断是否有删除权限
     * @param value
     * @return
     */
    public static boolean isDeleteAsAction(long value)
    {
    	return FlagUtility.isValue(value, FileSystemCons.DELETE_FLAG);
    }
    
    /**
     * 判断是否有上传权限
     * @param value
     * @return
     */
    public static boolean isUploadAction(long value)
    {
    	return FlagUtility.isValue(value, FileSystemCons.UPLOAD_FLAG);
    }
	
}
