package templates.objectdb;

import java.io.Serializable;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

@Entity 
@Table(name="news")
public class News implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_news_gen")
    @GenericGenerator(name = "seq_news_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_NEWS_ID") })
	private Long newId;
	
	/**新闻标题*/
	@Column(name = "new_title",length=500)
	private String new_title;
	
	/**新闻内容*/
	@Column(name = "new_content",length=1000)
	private String new_content;
	
	
	/**新闻日期*/
	@Column(name = "new_date")
	private Date new_date;
	
	
	/**新闻类型*/
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private NewsType newType;
	
	/**标记*/
	private Integer flag;
	
	/**新闻发布人*/
	@Column(length = 100)
	private String publisher;
	
	/**新闻图片*/
	@Column(length = 200)
	private String news_images;
	
	/**提交公告的人的用户名*/
	@Column(length = 100)
	private String username;
	
	/**提交公告的人id*/
	private Long userid;
	
	 
	@OneToOne
	private NewsAttached attached;
	
	/**是否公开：返回1标示公开，返回0表示不公开。默认为1，公开*/
	private int isPublic=0;
	
	/**审核状态：0表示正在审核，1表示审核通过，2表示审核不通过。默认为0*/
	private int status=0;
	
	/**点击量,默认为0*/
	private int count=0;
	
	/**新闻所在的顶级部门id*/
	private Long orgid=1L;
	
	private Integer readtype;//发布的范围，空或0为本部门（政府机关为本单位）,1为选择的处室，2为本单位（company），3为整个系统

	/**凡是被用户读过的公告都将被存在userReadId当中*/
	@Column(name = "userReadId",length=10000)
	private String userReadId = null;
	
	public Long getNewId() {
		return newId;
	}

	public void setNewId(Long newId) {
		this.newId = newId;
	}

	public String getNew_title() {
		return new_title;
	}

	public void setNew_title(String newTitle) {
		new_title = newTitle;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getNews_images() {
		return news_images;
	}

	public void setNews_images(String newsImages) {
		news_images = newsImages;
	}

	public String getNew_content() {
		return new_content;
	}

	public void setNew_content(String newContent) {
		new_content = newContent;
	}
	
	public String getUserReadId() {
		return userReadId;
	} 
	
	public void setUserReadId(String newUserReadId) {
		if(userReadId == null){
			userReadId =  ";" + newUserReadId + ";";
		}
		else {
			userReadId = userReadId + newUserReadId + ";";
		}
	}

	public NewsType getNewType() {
		return newType;
	}

	public void setNewType(NewsType newType) {
		this.newType = newType;
	}


	public NewsAttached getAttached() {
		return attached;
	}

	public void setAttached(NewsAttached attached) {
		this.attached = attached;
	}

	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
	}

	public int getIsPublic() {
		return isPublic;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}
	
	public String getDate()
	{
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(new_date);
	}

	public Date getNew_date() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date strtodate = null;
		if(new_date !=null){
			String dates = sf.format(new_date);
			try {
				strtodate = sf.parse(dates);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Date date = new Date();
			strtodate=date;
		}
		
		return strtodate;
	}

	public void setNew_date(Date new_date) {
		this.new_date = new_date;
	}
	public Integer getReadtype() {
		return readtype;
	}

	public void setReadtype(Integer readtype) {
		this.readtype = readtype;
	}

}
