package templates.objectdb;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


/*
 * 孙爱华
 */
@Entity 
@Table(name="questionnaire")
public class Questionnaire implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_questionnaire_gen")
    @GenericGenerator(name = "seq_questionnaire_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_QUESTIONNAIRE_ID") })
	private Long  id;
	
	/**反馈人名*/
	@Column(length=100)
	private String userName;
	
	/**日期*/
	@Column(length=256)
	private Date qDate;
	
	
	/**省*/
	@Column(length=50)
	private String province;
	
	/**行业*/
	@Column(length=100)
	private String industry;
	
	
	private int question1;
	
	private int question2;
	
	private int question3;
	
	@Column(length = 100)
	private String question4;
	@Column(length = 100)
	private String question5;
	@Column(length = 100)
	private String question6;
	
	private int question7;
	
	private int question8;
	

	
	/**建议*/
	@Column(length=5000)
	private String advice;
	
	/**备用字段*/
	@Column(length=256)
	private String standby;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getStandby() {
		return standby;
	}
	public void setStandby(String standby) {
		this.standby = standby;
	}
	public String getAdvice() {
		return advice;
	}
	public void setAdvice(String advice) {
		this.advice = advice;
	}
	public String getqDate() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(qDate);
	}
	public void setqDate(Date qDate) {
		this.qDate = qDate;
	}
	public int getQuestion1() {
		return question1;
	}
	public void setQuestion1(int question1) {
		this.question1 = question1;
	}
	public int getQuestion2() {
		return question2;
	}
	public void setQuestion2(int question2) {
		this.question2 = question2;
	}
	public int getQuestion3() {
		return question3;
	}
	public void setQuestion3(int question3) {
		this.question3 = question3;
	}
	public String getQuestion4() {
		return question4;
	}
	public void setQuestion4(String question4) {
		this.question4 = question4;
	}
	public String getQuestion5() {
		return question5;
	}
	public void setQuestion5(String question5) {
		this.question5 = question5;
	}
	public String getQuestion6() {
		return question6;
	}
	public void setQuestion6(String question6) {
		this.question6 = question6;
	}
	public int getQuestion7() {
		return question7;
	}
	public void setQuestion7(int question7) {
		this.question7 = question7;
	}
	public int getQuestion8() {
		return question8;
	}
	public void setQuestion8(int question8) {
		this.question8 = question8;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
}
