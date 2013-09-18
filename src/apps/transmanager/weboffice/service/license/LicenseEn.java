package apps.transmanager.weboffice.service.license;


public class LicenseEn
{
	public final static long BEGIN = 0x00FF00FF00FF00FFL;
	public final static short BIT = 0;
	public final static short UC = 1;
	public final static short UD = 2;
	public final static short COMPANY = 3;
	public final static short CONTENT = 4;
	public final static short COMM = 5;
	
	public static long encode(int c, int bit)
	{
		if (bit <= 0 || bit >= 32)
		{
			bit = 5;
		}
		long ret = c;
		ret <<= bit;
		ret |= bit;
		return ret;
	}
	
	public static int decode(long c, int bit)
	{
		if (bit <= 0 || bit >= 32)
		{
			bit = 5;
		}
		long ret = c;
		ret >>= bit;
		return (int)ret;
	}	
	
	public static long encodeOUC(long c)
	{
		short[] en = new short[4];
		en[0] = (short)((c >>> 0) & 0xFF);
		en[1] = (short)((c >>> 8) & 0xFF);
		en[2] = (short)((c >>> 16) & 0xFF);
		en[3] = (short)((c >>> 24) & 0xFF);
		long ret = ((long)en[2] << 40) | ((long)en[0] << 32)
				| ((long)en[1] << 24) | ((long)en[3] << 16) | ((long)en[1] << 8) | (en[0] & 5);   
		return ret;
	}
	
	public static long decodeOUC(long c)
	{
		short[] de = new short[8];
		de[0] = (short)(c & 0xFF);
		de[1] = (short)((c >>> 8)& 0xFF);
		de[2] = (short)((c >>> 16) & 0xFF);
		de[3] = (short)((c >>> 24)& 0xFF);
		de[4] = (short)((c >>> 32)& 0xFF);
		de[5] = (short)((c >>> 40)& 0xFF);		
		long ret = de[4] | ((long)de[3] << 8) | ((long)de[5] << 16) | ((long)de[2] << 24);
		return ret;
	}
	
	public static long encodeOUD(long c)
	{
		short[] en = new short[8];
		en[0] = (short)((c >>> 0) & 0xFF);
		en[1] = (short)((c >>> 8) & 0xFF);
		en[2] = (short)((c >>> 16) & 0xFF);
		en[3] = (short)((c >>> 24) & 0xFF);
		en[4] = (short)((c >>> 32) & 0xFF);
		en[5] = (short)((c >>> 40) & 0xFF);
		en[6] = (short)((c >>> 48) & 0xFF);
		en[7] = (short)((c >>> 56) & 0xFF);
		long ret = ((long)en[4] << 56) | ((long)en[2] << 48) | ((long)en[7] << 40)	| ((long)en[5] << 32)
					| ((long)en[0] << 24) | ((long)en[1] << 16) | ((long)en[3] << 8) | en[6];   
		return ret;
	}
	
	public static long decodeOUD(long c)
	{
		short[] de = new short[8];
		de[0] = (short)(c & 0xFF);
		de[1] = (short)((c >>> 8)& 0xFF);
		de[2] = (short)((c >>> 16) & 0xFF);
		de[3] = (short)((c >>> 24)& 0xFF);
		de[4] = (short)((c >>> 32)& 0xFF);
		de[5] = (short)((c >>> 40)& 0xFF);
		de[6] = (short)((c >>> 48)& 0xFF);
		de[7] = (short)((c >>> 56)& 0xFF);	
		long ret = ((long)de[5] << 56) | ((long)de[0] << 48) | ((long)de[4] << 40)	| ((long)de[7] << 32)
				| ((long)de[1] << 24) | ((long)de[6] << 16) | ((long)de[2] << 8) | de[3];
		return ret;
	}
	
}
