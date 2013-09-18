package apps.moreoffice.report.server.servlet.config;

/**
 * 报表配置文件信息
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-11
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class ReportConfig
{
    // 系统运行时刻的根目录
    private String realPath;
    // 系统编码方式
    private String encoding;

    /**
     * @return 返回 realPath
     */
    public String getRealPath()
    {
        return realPath;
    }

    /**
     * @param realPath 设置 realPath
     */
    public void setRealPath(String realPath)
    {
        this.realPath = realPath;
    }

    /**
     * @return 返回 encoding
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * @param encoding 设置 encoding
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
}