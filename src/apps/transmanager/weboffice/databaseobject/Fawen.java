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

/**
 * 公司信息
 * <p>
 * <p>
 * 
 * @author 徐文平
 * @version 2.0
 * @see
 * @since web2.0
 */

@Entity
@Table(name="fawen")
public class Fawen implements SerializableAdapter
{

    //发文ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_fawen_gen")
	@GenericGenerator(name = "seq_fawen_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_FAWEN_ID") })
    private Long  fid;

    public String getFpath() {
        return fpath;
    }

    public Long getFid() {
        return fid;
    }

    public void setFpath(String fpath) {
        this.fpath = fpath;
    }

    public void setSenddate(Date senddate) {
        this.senddate = senddate;
    }

    public void setSenddepartid(Long senddepartid) {
        this.senddepartid = senddepartid;
    }

    public void setSenddepartname(String senddepartname) {
        this.senddepartname = senddepartname;
    }

    public void setSenderid(Long senderid) {
        this.senderid = senderid;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public void setReceivedate(Date receivedate) {
        this.receivedate = receivedate;
    }

    public void setReceivedepartid(long receivedepartid) {
        this.receivedepartid = receivedepartid;
    }

    public void setReceivedepartname(String receivedepartname) {
        this.receivedepartname = receivedepartname;
    }

    public void setReceiverid(long receiverid) {
        this.receiverid = receiverid;
    }

    public void setReceivername(String receivername) {
        this.receivername = receivername;
    }

    public Date getSenddate() {
        return senddate;
    }

    public Long getSenddepartid() {
        return senddepartid;
    }

    public String getSenddepartname() {
        return senddepartname;
    }

    public Long getSenderid() {
        return senderid;
    }

    public String getSendername() {
        return sendername;
    }

    public Date getReceivedate() {
        return receivedate;
    }

    public long getReceivedepartid() {
        return receivedepartid;
    }

    public String getReceivedepartname() {
        return receivedepartname;
    }

    public long getReceiverid() {
        return receiverid;
    }

    public String getReceivername() {
        return receivername;
    }

    //发文文件名名称
    @Column(length = 255)
    private String fname;
    //文件path
    @Column(length = 3000)
    private String fpath;

    //发文时间
    private Date senddate;
    //发文单位ID
    private Long  senddepartid;
    //发文单位名称
    @Column(length = 100)
    private String senddepartname;
    //发文人ID
    private Long  senderid;
    //发文人名称
    @Column(length = 100)
    private String sendername;

    //收文时间
    @Column(nullable = true, updatable = true)
    private Date receivedate;
    //收文单位ID
    private long receivedepartid;
    //收文单位名称
    @Column(length = 100)
    private String receivedepartname;
    //收文人ID
    @Column(nullable = true, updatable = true)
    private long receiverid;
    //收文人名称
    @Column(length = 100,nullable = true, updatable = true)
    private String receivername;

	
	public Fawen()
	{		
	}
    public void setFname(String fname){
        this.fname=fname;
    }
    public String getFname(){
        return this.fname;
    }

    public void receiveFawen(Long receiverid,String receivername){
        this.receiverid=receiverid;
        this.receivername=receivername;
        this.receivedate=new Date();
    }

     public Fawen(String fname,String fpath,Long receivedepartid,String receivedepartname,long senddepartid,String senddepartname,Long senderid,String sendername){
         this.fname=fname;
         this.fpath=fpath;
         this.receivedepartid=receivedepartid;
         this.receivedepartname=receivedepartname;
         this.senddepartid=senddepartid;
         this.senddepartname=senddepartname;
         this.senderid=senderid;
         this.sendername=sendername;
         this.senddate=new Date();
     }

    public void update(Fawen t)
    {
        fname=t.fname;
        fpath=t.fpath;
        receivedepartid=t.receivedepartid;
        receivedepartname=t.receivedepartname;
        senddepartid=t.senddepartid;
        senddepartname=t.senddepartname;
        senderid=t.senderid;
        sendername=t.sendername;
        senddate=t.senddate;
    }
}
