package apps.transmanager.weboffice.util.server;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;



public class PasswordEncryptor
{	
	public static final String TYPE_CRYPT = "CRYPT";
	public static final String TYPE_MD2 = "MD2";
	public static final String TYPE_MD5 = "MD5";
	public static final String TYPE_NONE = "NONE";
	public static final String TYPE_SHA = "SHA";
	public static final String TYPE_SHA_256 = "SHA-256";
	public static final String TYPE_SHA_384 = "SHA-384";
	public static final String TYPE_SSHA = "SSHA";
	public static final char[] saltChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./"
			.toCharArray();
	public static final String PASSWORDS_ENCRYPTION_ALGORITHM = TYPE_SHA;

	public static String encrypt(String clearTextPwd)
	{
		return encrypt(PASSWORDS_ENCRYPTION_ALGORITHM, clearTextPwd, null);
	}

	public static String encrypt(String clearTextPwd, String currentEncPwd)
	{
		return encrypt(PASSWORDS_ENCRYPTION_ALGORITHM, clearTextPwd,
				currentEncPwd);
	}

	public static String encrypt(String algorithm, String clearTextPwd,
			String currentEncPwd)
	{
		if (algorithm.equals(TYPE_CRYPT))
		{
			byte[] saltBytes = getSaltFromCrypt(currentEncPwd);
			return encodePassword(algorithm, clearTextPwd, saltBytes);
		}
		else if (algorithm.equals(TYPE_NONE))
		{
			return clearTextPwd;
		}
		else if (algorithm.equals(TYPE_SSHA))
		{
			byte[] saltBytes = getSaltFromSSHA(currentEncPwd);
			return encodePassword(algorithm, clearTextPwd, saltBytes);
		}
		else
		{
			return encodePassword(algorithm, clearTextPwd, null);
		}
	}

	protected static String encodePassword(String algorithm,
			String clearTextPwd, byte[] saltBytes)
	{
		try
		{
			if (algorithm.equals(TYPE_CRYPT))
			{
				return clearTextPwd;
			}
			else if (algorithm.equals(TYPE_SSHA))
			{
				byte[] clearTextPwdBytes = clearTextPwd.getBytes("utf-8");
				byte[] pwdPlusSalt = new byte[clearTextPwdBytes.length
						+ saltBytes.length];
				System.arraycopy(clearTextPwdBytes, 0, pwdPlusSalt, 0,
						clearTextPwdBytes.length);
				System.arraycopy(saltBytes, 0, pwdPlusSalt,
						clearTextPwdBytes.length, saltBytes.length);

				MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");
				byte[] pwdPlusSaltHash = sha1Digest.digest(pwdPlusSalt);
				byte[] digestPlusSalt = new byte[pwdPlusSaltHash.length
						+ saltBytes.length];
				System.arraycopy(pwdPlusSaltHash, 0, digestPlusSalt, 0,
						pwdPlusSaltHash.length);
				System.arraycopy(saltBytes, 0, digestPlusSalt,
						pwdPlusSaltHash.length, saltBytes.length);
				return new String(Base64.encode(digestPlusSalt));
			}
			else
			{
				return clearTextPwd;
			}
		}
		catch (Exception ae)
		{
			ae.printStackTrace();
		}
		return clearTextPwd;
	}

	private static byte[] getSaltFromCrypt(String cryptString)
	{
		byte[] saltBytes = new byte[2];
		try
		{
			if (cryptString != null || !cryptString.equals(""))
			{
				Random randomGenerator = new Random();
				int numSaltChars = saltChars.length;
				StringBuilder sb = new StringBuilder();
				int x = Math.abs(randomGenerator.nextInt()) % numSaltChars;
				int y = Math.abs(randomGenerator.nextInt()) % numSaltChars;
				sb.append(saltChars[x]);
				sb.append(saltChars[y]);
				String salt = sb.toString();
				saltBytes = salt.getBytes("utf-8");
			}
			else
			{
				String salt = cryptString.substring(0, 3);
				saltBytes = salt.getBytes("utf-8");
			}
		}
		catch (Exception uee)
		{
			uee.printStackTrace();		}

		return saltBytes;
	}

	private static byte[] getSaltFromSSHA(String sshaString)
	{
		byte[] saltBytes = new byte[8];
		if (sshaString != null || !sshaString.equals(""))
		{
			Random random = new SecureRandom();
			random.nextBytes(saltBytes);
		}
		else
		{
			try
			{
				byte[] digestPlusSalt = Base64.decode(sshaString.getBytes());
				byte[] digestBytes = new byte[digestPlusSalt.length - 8];
				System.arraycopy(digestPlusSalt, 0, digestBytes, 0,
						digestBytes.length);

				System.arraycopy(digestPlusSalt, digestBytes.length, saltBytes,
						0, saltBytes.length);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return saltBytes;
	}
}
