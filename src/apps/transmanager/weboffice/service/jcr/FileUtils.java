package apps.transmanager.weboffice.service.jcr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import apps.transmanager.weboffice.domain.DataConstant;
import apps.transmanager.weboffice.domain.FileConstants;

/**
 * TODO: 文件注释
 * <p>
 * <p>
 * EIO版本:        EIO Office V1.3
 * <p>
 * <p>
 */
public class FileUtils
{

    /**
     * Returns the name of the file whithout the extension.  
     * 
     * @param file
     * @return
     */
    public static String getFileName(String file)
    {
        int idx = file.lastIndexOf(".");
        String ret = idx >= 0 ? file.substring(0, idx) : file;
        return ret;
    }

    /**
     * Returns the filename extension
     * 
     * @param file
     * @return
     */
    public static String getFileExtension(String file)
    {
        int idx = file.lastIndexOf(".");
        String ret = idx >= 0 ? file.substring(idx + 1) : "";
        return ret;
    }

    /**
     * @param path
     * @return
     */
    public static String getParent(String path)
    {
        int lastSlash = path.lastIndexOf("/");
        String ret = (lastSlash > 0) ? path.substring(0, lastSlash) : "";
        return ret;
    }
    
    public static String getPreName(String path)
    {
        int index = path.indexOf("/");
        if (index == -1)
        {
            index = path.indexOf(File.separator);
            if (index == -1)
            {
                return path;
            }
        }
        return path.substring(0, index);
    }

    /**
     * @param path
     * @return
     */
    public static String getName(String path)
    {
        int index = path.lastIndexOf("/") ;
        if(index == -1)
        {
            index = path.lastIndexOf(File.separator);
        }
        String ret = path.substring(index + 1);
        return ret;
    }

    /**
     * Eliminate dangerous chars in name
     * 
     * @param name
     * @return
     */
    public static String escape(String name)
    {
        String ret = name.replace('/', ' ');
        ret = ret.replace(':', ' ');
        ret = ret.replace('[', ' ');
        ret = ret.replace(']', ' ');
        ret = ret.replace('*', ' ');
        ret = ret.replace('\'', ' ');
        ret = ret.replace('"', ' ');
        ret = ret.replace('|', ' ');
        ret = ret.trim();
        return ret;
    }

    /**
     * Creates a temporal and unique directory
     * 
     * @throws IOException If something fails.
     */
    public static File createTempDir() throws IOException
    {
        File tmpFile = File.createTempFile("okm", null);

        if (!tmpFile.delete())
            throw new IOException();
        if (!tmpFile.mkdir())
            throw new IOException();

        return tmpFile;
    }
    
    /**
     * 从文本中截取标签
     * @param text
     * @return
     */
    public static ArrayList<String> getTagsFromText(String text, char token)
    {
        ArrayList<String> tags = new ArrayList<String>();
        int length = text.length();
        int k = 0;

        for (int i = 0; i < length; i++)
        {
            if (text.charAt(i) == token)
            {
                tags.add(text.substring(k, i));
                k = i + 1;
            }
        }
        //没有逗号时，k一直为0，for循环就走不到了。
        if (k == 0)
        {
            tags.add(text);
        }
        else if(k <= length)
        {
            tags.add(text.substring(k, length));
        }
        return tags;
    }
    
    /**
     * 获取文件的类型
     * @param type 文件类型参数
     * @return 文件类型
     */
    public static String convertFileType(String type)
    {
    	 if (DataConstant.ALL_TYPE.equals(type))
         {
             return  null;
         }
         else if (DataConstant.EIO.equals(type))
         {
             return FileConstants.EIOMIME;
         }
         else if (DataConstant.DOC.equals(type))
         {
             return FileConstants.DOCMIME;
         }
         else if (DataConstant.XLS.equals(type))
         {
             return FileConstants.XLSMIME;
         }
         else if (DataConstant.PPT.equals(type))
         {
             return FileConstants.PPTMIME;
         }
         else if (DataConstant.WEB_FILE.equals(type))
         {
             return FileConstants.HTMLMIME;
         }
         else if (DataConstant.PDF.equals(type))
         {
             return FileConstants.PDFMIME;
         }
         else if (DataConstant.RTF.equals(type))
         {
             return FileConstants.RTFMIME;
         }
         else if (DataConstant.EIT.equals(type))
         {
             return FileConstants.EITMIME;
         }
         else if (DataConstant.TXT.equals(type))
         {
             return FileConstants.TXTMIME;
         }
             return null;
    }

    /**
     * 根据范围转换时间
     * @param range 时间范围
     * @param from 时间结束点
     * @return 时间
     */
	public static Date convertCalByRange(String range,Date from) {
		if (DataConstant.PREVIOUS_WEEK.equals(range))
        {
            long time = from.getTime() - 7 * 24 * 60 * 60 * 1000L;
            return new Date(time);
        }
        else if (DataConstant.PREVIOUS_MONTH.equals(range))
        {
            long time = from.getTime() - 30 * 24 * 60 * 60 * 1000L;
            return new Date(time);
        }
        else if (DataConstant.PREVIOUS_THREE.equals(range))
        {
            long time = from.getTime() - 90 * 24 * 60 * 60 * 1000L;
            return new Date(time);
        }
        else if (DataConstant.PREVIOUS_SIX.equals(range))
        {
            long time = from.getTime() - 180 * 24 * 60 * 60 * 1000L;
            return new Date(time);
        }
        else if (DataConstant.PREVIOUS_YEAR.equals(range))
        {
            long time = from.getTime() - 365 * 24 * 60 * 60 * 1000L;
            return new Date(time);
        }
		return null;
	}
}
