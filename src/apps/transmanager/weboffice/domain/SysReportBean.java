package apps.transmanager.weboffice.domain;


/**
 * 系统报告
 * <p>
 * <p>
 * EIO版本:        WebOffice v2.0
 * <p>
 * 作者:           user272(梁金晶)
 * <p>
 * 日期:           2011-9-5
 * <p>
 * 负责人:         user272(梁金晶)
 * <p>
 * 负责小组:        WebOffice
 * <p>
 * <p>
 */
public class SysReportBean implements SerializableAdapter
{
    private Long id;
    
    // 日期
    private String reportdate;
    // 用户数
    private long accountCount;
    // 增加的用户数
    private long addAccountCount;
    // 文档库容量
    private long contentSize;
    // 增加文档容量
    private long addContentSize;
    // 空间总数
    private long spaceCount;
    // 增加空间数
    private long addSpaceCount;
    // 文档总数
    private long documentCount;
    // 增加文档总数
    private long addDocumentCount;

    public SysReportBean()
    {        
    }
    
    public String toString()
    {
        return "ID：" + id
               + "\n日期: " + reportdate
               + "\n用户数：" + accountCount
               + "\n增加的用户数：" + addAccountCount
               + "\n文档库容量：" + contentSize
               + "\n增加的文档容量：" + addContentSize
               + "\n空间数：" + spaceCount
               + "\n增加的空间数：" + addSpaceCount
               + "\n文档数：" + documentCount
               + "\n增加的文档总数：" + addDocumentCount;
    }
    
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setReportdate(String reportdate)
    {
        this.reportdate = reportdate;
    }

    public String getReportdate()
    {
        return reportdate;
    }

    public void setAccountCount(long accountCount)
    {
        this.accountCount = accountCount;
    }

    public long getAccountCount()
    {
        return accountCount;
    }

    public void setAddAccountCount(long addAccountCount)
    {
        this.addAccountCount = addAccountCount;
    }

    public long getAddAccountCount()
    {
        return addAccountCount;
    }

    public void setContentSize(long contentSize)
    {
        this.contentSize = contentSize;
    }

    public long getContentSize()
    {
        return contentSize;
    }

    public void setAddContentSize(long addContentSize)
    {
        this.addContentSize = addContentSize;
    }

    public long getAddContentSize()
    {
        return addContentSize;
    }

    public void setSpaceCount(long spaceCount)
    {
        this.spaceCount = spaceCount;
    }

    public long getSpaceCount()
    {
        return spaceCount;
    }

    public void setAddSpaceCount(long addSpaceCount)
    {
        this.addSpaceCount = addSpaceCount;
    }

    public long getAddSpaceCount()
    {
        return addSpaceCount;
    }

    public void setDocumentCount(long documentCount)
    {
        this.documentCount = documentCount;
    }

    public long getDocumentCount()
    {
        return documentCount;
    }

    public void setAddDocumentCount(long addDocumentCount)
    {
        this.addDocumentCount = addDocumentCount;
    }

    public long getAddDocumentCount()
    {
        return addDocumentCount;
    }
}
