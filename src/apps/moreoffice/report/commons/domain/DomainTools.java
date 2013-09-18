package apps.moreoffice.report.commons.domain;

import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;

/**
 * 需要支持gwt的编译，仅仅针对domain包的工具类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-9-11
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class DomainTools
{
    /**
     * 按位进行权限判断
     */
    public static boolean isLongFlag(long value, int loc)
    {
        return (value >>> loc & 1L) == 1L;
    }

    /**
     * 产生标记
     * 
     * @param value 为标记目前的情况
     * @param loc 为产生标记的位
     * @param flag 为该位的值,true为1，false为0 。
     * @return int 返回新的特殊标记。
     */
    public static long setLongFlag(long value, int loc, boolean flag)
    {
        if (flag)
        {
            value |= (1L << loc);
        }
        else
        {
            value &= ~(1L << loc);
        }
        return value;
    }

    /**
     * 得到排序字符串
     * 
     * @param index 索引
     * @return String 排序字符串
     */
    public static String getSortString(int index)
    {
        switch (index)
        {
            case TableCons.ASC:
                return ReportCommonResource.ASC;
            case TableCons.DESC:
                return ReportCommonResource.DESC;
            default:
                return "";
        }
    }

    /**
     * 得到排序索引
     * 
     * @param sortString 排序字符串
     * @return int 排序索引
     */
    public static int getSortIndex(String sortString)
    {
        if (sortString == null || sortString.length() < 1)
        {
            return TableCons.DEFAULT;
        }

        if (sortString.equals(ReportCommonResource.ASC))
        {
            return TableCons.ASC;
        }
        else if (sortString.equals(ReportCommonResource.DESC))
        {
            return TableCons.DESC;
        }
        return TableCons.DEFAULT;
    }
}