package apps.transmanager.weboffice.util.both;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
//禁止继承类。
public final class FlagUtility
{
	/**
	 * 禁止实例化类
	 */
	private FlagUtility()
	{
	}

	/**
     * 产生标记。
     * 
     * @param value 为标记目前的情况。
     * @param loc 为产生标记的位。
     * @param flag 为该位的值,true为1，false为0 。
     * @return int 返回新的特殊标记。
     */
	public static byte setByteFlag(byte value, int loc, boolean flag)
	{
	    if(flag)
	    {
	       value |= (byte)(1 << loc);
	    }
	    else
	    {
	        value &= ~(byte)(1 << loc);
	    }
	    return value;
	}

	/**
	 * 判断给定的value值在loc位的值，为1返回true，为0返回false。
	 * @param value
	 * @param loc
	 * @return
	 */
	public static boolean isByteFlag(byte value, int loc)
	{
	    return ((byte)(value >>> loc) & 1) == 1;
	}

	/**
     * 产生标记。
     * 
     * @param value 为标记目前的情况。
     * @param loc 为产生标记的位。
     * @param flag 为该位的值,true为1，false为0 。
     * @return int 返回新的特殊标记。
     */
	public static short setShortFlag(short value, int loc, boolean flag)
	{
	    if(flag)
	    {
	        value |= (short)(1 << loc);
	    }
	    else
	    {
	        value &= ~(short)(1 << loc);
	    }
	    return value;
	}

	/**
	 * 判断给定的value值在loc位的值，为1返回true，为0返回false。
	 * @param value
	 * @param loc
	 * @return
	 */
	public static boolean isShortFlag(short value, int loc)
	{
	    return ((short)(value >>> loc) & 1) == 1;
	}

	/**
     * 产生标记。
     * 
     * @param value 为标记目前的情况。
     * @param loc 为产生标记的位。
     * @param flag 为该位的值,true为1，false为0 。
     * @return int 返回新的特殊标记。
     */
	public static int setIntFlag(int value, int loc, boolean flag)
	{
	    if(flag)
	    {
	        value |= (1 << loc);
	    }
	    else
	    {
	        value &= ~(1 << loc);
	    }
	    return value;
	}

	/**
	 * 判断给定的value值在loc位的值，为1返回true，为0返回false。
	 * @param value
	 * @param loc
	 * @return
	 */
	public static boolean isIntFlag(int value, int loc)
	{
	    return (value >>> loc & 1) == 1;
	}

	/**
     * 产生标记。
     * 
     * @param value 为标记目前的情况。
     * @param loc 为产生标记的位。
     * @param flag 为该位的值,true为1，false为0 。
     * @return int 返回新的特殊标记。
     */
	public static long setLongFlag(long value, int loc, boolean flag)
	{
	    if(flag)
	    {
	        value |= (1L << loc);
	    }
	    else
	    {
	        value &= ~(1L << loc);//去除这位权限
	    }
	    return value;
	}

	/**
	 * 判断给定的value值在loc位的值，为1返回true，为0返回false。
	 * @param value
	 * @param loc
	 * @return
	 */
	public static boolean isLongFlag(long value, int loc)
	{
	    return (value >>> loc & 1L) == 1L;
	}

	/*
	 * 设置值value值为newValue值，如果flag为true，则表示设置为新值
	 * 如果flag为false，表示取消设置为新值。
	 */
	public static long setValue(long value, long newValue, boolean flag)
	{
	    if(flag)
	    {
	        return value | newValue;
	    }
	    else
	    {
	        return value & ~newValue;
	    }
	}

	/**
	 * 判断value值中是否有targValue中的值，只要有一位有值，则该方法即返回true。
	 * @param value 需要判断的值。
	 * @param targValue 需要判断的目标值。
	 * @return
	 */
	public static boolean isValue(long value, long targValue)
	{
	    return (value & targValue) != 0;
	}
}
