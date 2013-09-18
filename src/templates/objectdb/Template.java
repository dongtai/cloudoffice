package templates.objectdb;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity 
@Table(name="yozo_template")
public class Template implements Serializable{

	@Id
	@GeneratedValue
	private Long tid;
	/**模板名称*/
	@Column(length=100)
	private String tName;
	
	/**模板类型*/
	@ManyToOne
	private TemplateType tType;
	
	
	
	/**地址*/
	@Column(length = 1000)
	private String tUrl;
	
	/**点击*/
	private int thitCount;
	
	/**下载*/
	private int tDownloadCount;
	
	/**是否付款*/
	private int isPay;
	
	/**日期*/
	private Date date;
	
	/**价格*/
	private float price;
	
	/**图片路径*/
	@Column(length = 100)
	private String imgUrl;
	
	/**格式*/
	@Column(length = 20)
	private String format;
	
	/**页数*/
	@Column(length = 255)
	private String pageNum;
	
	/**大小*/
	@Column(length = 100)
	private String size;
	
	/**关键*/
	@Column(length = 255)
	private String keyWord;
	
	/**描述*/
	@Column(length = 1000)
	private String description;
	
	/**详细图片类型*/
	@Column(length = 10)
	private String imgType;
	

	/**审批结果*/
	private int checkupResult;
	
	/**模板编号*/
	@Column(length = 255)
	private String t_number;
	
	
	public String getTName() {
		return tName;
	}
	public void setTName(String tName) {
		this.tName = tName;
	}
	
	public String getTUrl() {
		return tUrl;
	}
	public void setTUrl(String tUrl) {
		this.tUrl = tUrl;
	}
	public int getThitCount() {
		return thitCount;
	}
	public void setThitCount(int thitCount) {
		this.thitCount = thitCount;
	}
	public int getIsPay() {
		return isPay;
	}
	public void setIsPay(int isPay) {
		this.isPay = isPay;
	}
	public String getDate() {
		SimpleDateFormat sdFormat= new SimpleDateFormat("yyyy-MM-dd ");
		return sdFormat.format(date);
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getTDownloadCount() {
		return tDownloadCount;
	}
	public void setTDownloadCount(int tDownloadCount) {
		this.tDownloadCount = tDownloadCount;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getPageNum() {
		return pageNum;
	}
	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImgType() {
		return imgType;
	}
	public void setImgType(String imgType) {
		this.imgType = imgType;
	}
	public Long getTid() {
		return tid;
	}
	public void setTid(Long tid) {
		this.tid = tid;
	}
	public TemplateType getTType() {
		return tType;
	}
	public void setTType(TemplateType tType) {
		this.tType = tType;
	}
	public int getCheckupResult() {
		return checkupResult;
	}
	public void setCheckupResult(int checkupResult) {
		this.checkupResult = checkupResult;
	}
	public String getT_number() {
		return t_number;
	}
	public void setT_number(String tNumber) {
		t_number = tNumber;
	}
	
}
