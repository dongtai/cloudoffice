package apps.transmanager.weboffice.util.server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import apps.transmanager.weboffice.util.both.MD5;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class FileMD5 extends MD5
{

	private FileMD5()
	{		
	}

	public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	// get file MD5 hashcode
	public static String getFileMD5Code(String fileName)
	{
		try
		{
			return getFileHash(fileName, "MD5");
		}
		catch (Exception ee)
		{
			ee.printStackTrace();
			return "";
		}
		
	}
	
	// get inputStream MD5 hashcode
	public static String getStreamMD5Code(InputStream input)
	{
		try
		{
			return getStreamHash(input, "MD5");
		}
		catch (Exception ee)
		{
			ee.printStackTrace();
			return "";
		}
		
	}

	// hashType can is one of "MD5,SHA1,SHA-256,SHA-384,SHA-512"
	public static String getFileHash(String fileName, String hashType)
			throws Exception
	{
		InputStream fis;
		fis = new FileInputStream(fileName);
		return getStreamHash(fis, hashType);
		
		/*byte[] buffer = new byte[1024];
		MessageDigest md5 = MessageDigest.getInstance(hashType);
		int numRead = 0;
		while ((numRead = fis.read(buffer)) > 0)
		{
			md5.update(buffer, 0, numRead);
		}
		fis.close();
		return toHexString(md5.digest());*/
	}
	
	// hashType can is one of "MD5,SHA1,SHA-256,SHA-384,SHA-512"
	public static String getStreamHash(InputStream input, String hashType)
			throws Exception
	{
		InputStream fis;
		fis = input;
		byte[] buffer = new byte[1024];
		MessageDigest md5 = MessageDigest.getInstance(hashType);
		int numRead = 0;
		while ((numRead = fis.read(buffer)) > 0)
		{
			md5.update(buffer, 0, numRead);
		}
		fis.close();
		return toHexString(md5.digest());
	}

	private static String toHexString(byte[] b)
	{
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++)
		{
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}
}
