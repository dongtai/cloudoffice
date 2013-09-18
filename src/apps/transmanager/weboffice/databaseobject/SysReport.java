package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;
import apps.transmanager.weboffice.domain.SysReportBean;

/**
 * 文件注释
 * <p>
 * <p>
 * <p>
 * <p>
 */
@Entity
@Table(name="sysreport")
public class SysReport implements SerializableAdapter
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_sysreport_gen")
    @GenericGenerator(name = "seq_sysreport_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_SYSREPORT_ID") })
    private Long id;
    // 日期
    @Column(name = "reportdate")
    private Date reportdate;
    // 用户数
    @Column(name = "accountCount")
    private long accountCount;
    // 增加的用户数
    @Column(name = "addAccountCount")
    private long addAccountCount;
    // 文档库容量
    @Column(name = "contentSize")
    private long contentSize;
    // 增加文档容量
    @Column(name = "addContentSize")
    private long addContentSize;
    // 空间总数
    @Column(name = "spaceCount")
    private long spaceCount;
    // 增加空间数
    @Column(name = "addSpaceCount")
    private long addSpaceCount;
    // 文档总数
    @Column(name = "documentCount")
    private long documentCount;
    // 增加文档总数
    @Column(name = "addDocumentCount")
    private long addDocumentCount;

    public SysReport()
    {        
    }
    
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setReportdate(Date reportdate)
    {
        this.reportdate = reportdate;
    }

    public Date getReportdate()
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
    
    public SysReportBean databaseToBean(SysReportBean srb)
    {
        srb.setId(id);
        srb.setAccountCount(accountCount);
        srb.setAddAccountCount(addAccountCount);
        srb.setContentSize(contentSize);
        srb.setAddContentSize(addContentSize);
        srb.setDocumentCount(documentCount);
        srb.setAddDocumentCount(addDocumentCount);
        srb.setSpaceCount(spaceCount);
        srb.setAddSpaceCount(addSpaceCount);
        return srb;
    }
}
