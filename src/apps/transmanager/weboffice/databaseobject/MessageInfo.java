package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;


/**
 * TODO: 该类文件注释说明
 * <p>
 * Copyright(C) 2009-2010 Yozosoft Co. All Rights Reserved.
 * <p>
 * <p>
 */
@Entity
@Table(name="messageInfo")
public class MessageInfo implements SerializableAdapter {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,generator = "seq_messageInfo_gen")
	@GenericGenerator(name = "seq_messageInfo_gen",strategy = "native",parameters = {@Parameter(name = "sequence", value = "SEQ_MESSAGEINFO_ID")})
	private Long id;
	@Column(length=1000)
	private String content;
	private Date date;
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Users sendUsers;
	@Column(length=20)
	private String receiver;
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public Date getDate()
	{
		return date;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}
	public Users getSendUsers()
	{
		return sendUsers;
	}
	public void setSendUsers(Users sendUsers)
	{
		this.sendUsers = sendUsers;
	}
	public String getReceiver()
	{
		return receiver;
	}
	public void setReceiver(String receiver)
	{
		this.receiver = receiver;
	}

}
