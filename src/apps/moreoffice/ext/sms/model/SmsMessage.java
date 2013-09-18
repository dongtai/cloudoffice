package apps.moreoffice.ext.sms.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsMessage
{
	private String content;
	private String sender;
	private List<String> receivers = new ArrayList<String>();
	/* 时戳 */
	private Date timestamp;
	/* 短信方向：上行0（发送的短信） ，下行1（接收的短信） */
	private Integer direction;
	/* 有效截止时间 */
	private Date validClosedTime;

	public SmsMessage(String content, String sender, List<String> receivers)
	{
		super();
		this.content = content;
		this.sender = sender;
		this.receivers = receivers;
	}

	public SmsMessage()
	{
		super();
	}

	public SmsMessage(String content, String sender, List<String> receivers,
			Date timestamp, Integer direction, Date validClosedTime)
	{
		super();
		this.content = content;
		this.sender = sender;
		this.receivers = receivers;
		this.timestamp = timestamp;
		this.direction = direction;
		this.validClosedTime = validClosedTime;
	}

	public Date getValidClosedTime()
	{
		return validClosedTime;
	}

	public void setValidClosedTime(Date validClosedTime)
	{
		this.validClosedTime = validClosedTime;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getSender()
	{
		return sender;
	}

	public void setSender(String sender)
	{
		this.sender = sender;
	}

	public List<String> getReceivers()
	{
		return receivers;
	}

	public void setReceivers(List<String> receivers)
	{
		this.receivers = receivers;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public Integer getDirection()
	{
		return direction;
	}

	public void setDirection(Integer direction)
	{
		this.direction = direction;
	}

}
