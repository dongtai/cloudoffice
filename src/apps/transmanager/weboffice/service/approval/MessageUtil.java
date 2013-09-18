package apps.transmanager.weboffice.service.approval;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apps.transmanager.weboffice.constants.both.MessageCons;
import apps.transmanager.weboffice.databaseobject.ApprovalCooper;
import apps.transmanager.weboffice.databaseobject.ApprovalReader;
import apps.transmanager.weboffice.databaseobject.Messages;
import apps.transmanager.weboffice.databaseobject.SameSignInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.databaseobject.UsersMessages;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dwr.CalendarEventImpl;
import apps.transmanager.weboffice.service.dwr.ICalendarService;
import apps.transmanager.weboffice.service.server.JQLServices;
import apps.transmanager.weboffice.service.server.MessagesService;
import apps.transmanager.weboffice.util.beans.PageConstant;

public class MessageUtil {

private static MessageUtil instance=new MessageUtil();
    
    public MessageUtil()
    {    	
    	instance =  this;//new MessageUtil();
    }
    
    
    /**
     * 
     */
    public static MessageUtil instance()
    {
        return instance;
    }
    /**
     * 仅更新消息数字
     * @param messages
     * @param user
     * @param userIds
     */
    public void changeMsgNum(Messages messages,Users user,List<Long> userIds)
    {
    	JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
		MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
		Long totalnum=0l;
		if (userIds!=null && userIds.size()>0)
		{
			totalnum=(Long)jqlService.getCount("select count(a) from Messages as a where a.state=0 and a.msguser.id=? ", userIds.get(0));
		}
		//推送消息到前台，应该只推送数字
		messageService.sendMessageTo("changeMessages", PageConstant.LG_USER_ID, messages,totalnum, user.getId(), userIds);
    }
    /**
	 * 设置提醒
	 * @param id 签批编号
	 * @param type 类别，16为签批，17为传阅,18为协作
	 * @param uids 用户列表
	 * @param content 提醒内容
	 * @param user 当前账户
	 * @param worktype 是否催办编辑
	 * @return
	 */
	public boolean setSignWarn(Long id,Integer type,String[] uids,String content,String title,Users user,Integer worktype)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			//将信息插入到数据库
			if (uids==null)
			{
				Messages messages=new Messages();
				messages.setTitle(title);
				if (type==null){return false;}
				
				if ( type.intValue()==MessageCons.SIGNREAD)//传阅提醒催办
				{
					ApprovalReader read=(ApprovalReader)jqlService.getEntity(ApprovalReader.class, id);
					read.setWarnnum(read.getWarnnum()+worktype);
					jqlService.update(read);
					messages.setOutid(read.getApprovalInfoId());//存放流程号
					messages.setType(MessageCons.SIGNREAD);
					messages.setReadid(read.getId());
					
					Users tempuser=(Users)jqlService.getEntity(Users.class, read.getReadUser());
					messages.setMsguser(tempuser);
				}
				else if (type.intValue()==MessageCons.COOPER)//协作提醒催办
				{
					
					ApprovalCooper coop=(ApprovalCooper)jqlService.getEntity(ApprovalCooper.class, id);
					coop.setWarnnum(coop.getWarnnum()+worktype);
					jqlService.update(coop);
					
					messages.setOutid(coop.getApprovalID());//存放流程号
					messages.setType(MessageCons.COOPER);
					messages.setCoopid(coop.getId());
					
					messages.setMsguser(coop.getCooper());
				}
				else if (type.intValue()==MessageCons.SENDCOOPER)//送协作提醒催办
				{
					
					ApprovalCooper coop=(ApprovalCooper)jqlService.getEntity(ApprovalCooper.class, id);
					coop.setWarnnum(coop.getWarnnum()+worktype);
					jqlService.update(coop);
					messages.setOutid(coop.getApprovalID());//存放流程号
					messages.setType(MessageCons.COOPER);
					messages.setCoopid(coop.getId());
					messages.setMsguser(coop.getCooper());
					
				}
				else if (type.intValue()==MessageCons.SENDSIGNREAD)//送批阅提醒
				{
					
					ApprovalReader read=(ApprovalReader)jqlService.getEntity(ApprovalReader.class, id);
					if (read!=null)
					{
						read.setWarnnum(read.getWarnnum()+worktype);
						jqlService.update(read);
						messages.setOutid(read.getApprovalInfoId());//存放流程号
						messages.setType(MessageCons.SIGNREAD);
						messages.setReadid(read.getId());
						messages.setMsguser((Users)jqlService.getEntity(Users.class,read.getReadUser()));
						
					}
				}
				else
				{
					SameSignInfo same=(SameSignInfo)jqlService.getEntity(SameSignInfo.class, id);
					same.setWarnnum(same.getWarnnum()+worktype);
					jqlService.update(same);
					messages.setOutid(same.getApprovalID());//存放流程号
					messages.setType(MessageCons.SIGN);
					messages.setSameid(same.getSid());
					messages.setMsguser(same.getSigner());
					
				}
				messages.setContent(content);
				messages.setWorktype(worktype);
				messages.setDate(new Date());
				messages.setUser(user);//消息发送人
				jqlService.save(messages);
				
				UsersMessages um = new UsersMessages(messages.getMsguser().getId(), messages.getId());//为了兼容老的消息处理方式
				jqlService.save(um);
				List<Long> userIds=new ArrayList<Long>();
				userIds.add(messages.getMsguser().getId());
				
				Long totalnum=(Long)jqlService.getCount("select count(a) from Messages as a where a.state=0 and a.msguser.id=? ", messages.getMsguser().getId());
				//推送消息到前台，应该只推送数字
				messageService.sendMessageTo("changeMessages", PageConstant.LG_USER_ID, messages,totalnum, user.getId(), userIds);

			}
			else
			{
				for (int i=0;i<uids.length;i++)
				{
					Messages messages=new Messages();
					messages.setTitle(title);
					if (type==null){return false;}
					if ( type.intValue()==MessageCons.SIGNREAD)//传阅提醒
					{
						ApprovalReader read=(ApprovalReader)jqlService.getEntity(ApprovalReader.class, id);
						messages.setOutid(read.getApprovalInfoId());//存放流程号
						messages.setType(MessageCons.SIGNREAD);
						messages.setReadid(read.getId());
					}
					else if (type.intValue()==MessageCons.COOPER)//协作提醒
					{
						
						ApprovalCooper coop=(ApprovalCooper)jqlService.getEntity(ApprovalCooper.class, id);
						messages.setOutid(coop.getApprovalID());//存放流程号
						messages.setType(MessageCons.COOPER);
						messages.setCoopid(coop.getId());
					}
					else
					{
						SameSignInfo same=(SameSignInfo)jqlService.getEntity(SameSignInfo.class, id);
						messages.setOutid(same.getApprovalID());//存放流程号
						messages.setType(MessageCons.SIGN);
						messages.setSameid(same.getSid());
					}
					messages.setWorktype(worktype);
					messages.setContent(content);
					messages.setDate(new Date());
					messages.setUser(user);//消息发送人
					Users tempuser=(Users)jqlService.getEntity(Users.class, Long.valueOf(uids[i].trim()));
					messages.setMsguser(tempuser);
					jqlService.save(messages);
					
					UsersMessages um = new UsersMessages(tempuser.getId(), messages.getId());//为了兼容老的消息处理方式
					jqlService.save(um);
					List<Long> userIds=new ArrayList<Long>();
					userIds.add(tempuser.getId());
					//推送消息到前台，应该只推送数字
					Long totalnum=(Long)jqlService.getCount("select count(a) from Messages as a where a.state=0 and a.msguser.id=? ", messages.getMsguser().getId());
					//推送消息到前台，应该只推送数字
					messageService.sendMessageTo("changeMessages", PageConstant.LG_USER_ID, messages,totalnum, user.getId(), userIds);
				}
				
			}
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean setOtherWarn(Long id,Integer type,List<Long> uids,String content,String title,Users user,Integer worktype)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			MessagesService messageService = (MessagesService)ApplicationContext.getInstance().getBean(MessagesService.NAME);
			//将信息插入到数据库
			if (uids!=null)
			{
				for (int i=0;i<uids.size();i++)
				{
					Messages messages=new Messages();
					messages.setUser(user);
					messages.setTitle(title);
					if (type==null){return false;}

					messages.setOutid(id);//存放外部主键
					messages.setType(type);
					messages.setWorktype(worktype);
					messages.setContent(content);
					messages.setDate(new Date());
					Users tempuser=(Users)jqlService.getEntity(Users.class, uids.get(i));
					messages.setMsguser(tempuser);
					jqlService.save(messages);
					
					UsersMessages um = new UsersMessages(tempuser.getId(), messages.getId());//为了兼容老的消息处理方式
					jqlService.save(um);
					List<Long> userIds=new ArrayList<Long>();
					userIds.add(tempuser.getId());
					Long totalnum=(Long)jqlService.getCount("select count(a) from Messages as a where a.state=0 and a.msguser.id=? ", messages.getMsguser().getId());
					//推送消息到前台，应该只推送数字
					messageService.sendMessageTo("changeMessages", PageConstant.LG_USER_ID, messages,totalnum, user.getId(), userIds);
				}
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 获取签批提醒，获取消息后再进行消息注销,调用hiddenMessage
	 * @param id 流程编号
	 * @param user 当前用户
	 * @return
	 */
	public Map<String, Object> getSignWarn(Long id,Users user)
	{
		Map<String, Object> back=new HashMap<String, Object>();
		try
		{
			//var hash=["shared","send","todo","todo","todo"];
			//{type:2,sender:"王倚新",face:"",commet:"xxx备注22xxxx",time:"2012-12-21 10:02",title:"XXXXXXXX"},
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//这里有问题，当一个签批在不同节点送给同一个人时，就会将原先的提醒也拿出来的问题,后面再改进??????????????????
			List list=jqlService.findAllBySql("select a from Messages as a where (a.type="+MessageCons.SIGN
					+" or a.type="+MessageCons.SIGNREAD+" or a.type="+MessageCons.COOPER
					+") and a.outid=? and a.msguser.id=? ",id,user.getId());
			if (list!=null && list.size()>0)
			{
				Messages messages=(Messages)list.get(0);
				back.put("id", messages.getId());
				back.put("title",messages.getTitle());
				back.put("content", messages.getContent());
				back.put("date", sdf.format(messages.getDate()));
				back.put("appid", messages.getOutid());
				return back;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return back;
	}
	
	/**
	 * 根据信息号来处理信息
	 * @param ids
	 * @param isread
	 * @param isdeleted
	 * @return
	 */
	public boolean hiddenMessage(Long[] ids,Integer isread,Integer isdeleted)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			for (int i=0;i<ids.length;i++)
			{
				if (i==0)
				{
					sql+=" where (a.id="+ids[i];
				}
				else
				{
					sql+=" or a.id="+ids[i];
				}
				if (i==(ids.length-1))
				{
					sql+=")";
				}
			}
			if (sql.length()>0)
			{
				if (isread!=null)//读信息
				{
					sql="update Messages as a set a.state=1 "+sql;
				}
				else if (isdeleted!=null)
				{
					sql="update Messages as a set a.deleted=1 "+sql;
				}
				jqlService.excute(sql);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	public boolean hiddenAllMessage(Users user,Integer isread,Integer isdeleted)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql=" where a.msguser.id="+user.getId().longValue();
			
			if (sql.length()>0)
			{
				if (isread!=null)//读信息
				{
					sql="update Messages as a set a.state=1 "+sql;
				}
				else if (isdeleted!=null)
				{
					sql="update Messages as a set a.deleted=1 "+sql;
				}
				jqlService.excute(sql);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 提醒消息的统一入口,暂没有考虑附件的问题
	 * @param type   MessageCons中已经定义
	 * @param uids
	 * @param content
	 * @param user
	 * @param outid
	 * @return
	 */
	public boolean setMessages(int type,String[] uids,String content,String title,Users user,Long outid)
	{
//		public final static int SIGN = MEETING + 4;          		// 16事务签批消息
//	    public final static int SIGNREAD = SIGN + 1;          		// 17事务签批中批阅
//	    public final static int COOPER = SIGNREAD + 1;
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="";
			//将信息插入到数据库
			for (int i=0;i<uids.length;i++)
			{
				Messages messages=new Messages();
				messages.setType(type);
				messages.setOutid(outid);
				messages.setTitle(title);
				messages.setContent(content);
				messages.setDate(new Date());
				Users tempuser=(Users)jqlService.getEntity(Users.class, Long.valueOf(uids[i].trim()));
				messages.setMsguser(tempuser);
				jqlService.save(messages);
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 获取当前用户的所有信息
	 * @param user
	 * @return
	 */
	public List getTotalMessages(Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="select a from Messages as a where a.msguser.id=? and a.state is null and a.deleted is null ";
			List list=jqlService.findAllBySql(sql, user.getId());
			return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取最新消息数量
	 * @param user
	 * @return
	 */
	public Long getNewMessageNums(Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="select count(a) from Messages as a where a.msguser.id=? and (a.state is null or a.state=0) ";
			return (Long)jqlService.getCount(sql, user.getId());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0l;
	}
	/**
	 * 获取消息列表
	 * @param user
	 * @return
	 */
	public List<Messages> getNewListMessages(Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="select a from Messages as a where a.msguser.id=? and (a.state is null or a.state=0)  order by a.date DESC ";
			return (List<Messages>)jqlService.findAllBySql(sql, user.getId());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 处理消息
	 * @param id
	 * @param type 0，表示查看，1表示删除
	 * @param user
	 * @return
	 */
	public boolean updateMessages(Long id,int type,Users user)
	{
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			Messages messages = (Messages)jqlService.getEntity(Messages.class, id);
			if (type==0)
			{
				messages.setState(1);
			}
			else
			{
				messages.setState(1);
				messages.setDeleted(1);
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	
	public List<Long[]> getMsgCount(Users user)
	{
		List<Long[]> backlist=new ArrayList<Long[]>();
		try
		{
			JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
			String sql="select count(a) from Messages as a where a.msguser.id=? and (a.state is null or a.state=0) "
					+" and a.type in ("+MessageCons.SIGN+","+MessageCons.SIGNREAD
					+","+MessageCons.COOPER+")"
					+" and a.worktype=0 ";//有几条未处理的签批提醒
			Long[] nums=new Long[2];
			nums[0]=(Long)jqlService.getCount(sql, user.getId());
			sql="select count(a) from Messages as a where a.msguser.id=? and (a.state is null or a.state=0) "
					+" and a.type in ("+MessageCons.SIGN+","+MessageCons.SIGNREAD+","+MessageCons.SENDSIGN+")"
					+" and a.worktype=1 ";//有几条未处理的签批催办提醒
			nums[1]=(Long)jqlService.getCount(sql, user.getId());
			backlist.add(nums);
			
			sql="select count(a) from Messages as a where a.msguser.id=? and (a.state is null or a.state=0) "
					+" and a.type in ("+MessageCons.SENDTRANS+")"
					+"";//有几条未处理的事务提醒
			nums=new Long[2];
			nums[0]=(Long)jqlService.getCount(sql, user.getId());
			nums[1]=0l;//事务没有催办
			backlist.add(nums);
			
			sql="select count(a) from Messages as a where a.msguser.id=? and (a.state is null or a.state=0) "
					+" and a.type in ("+MessageCons.SENDMEET+") and a.title like '会议提醒%'"
					+"";//有几条未处理的会议提醒
			nums=new Long[2];
			nums[0]=(Long)jqlService.getCount(sql, user.getId());
			
			sql="select count(a) from Messages as a where a.msguser.id=? and (a.state is null or a.state=0) "
					+" and a.type in ("+MessageCons.SENDMEET+") and a.title like '再次提醒%'"
					+"";//有几条未处理的会议催办提醒
			nums[1]=(Long)jqlService.getCount(sql, user.getId());
			backlist.add(nums);
			
			ICalendarService calendarService = (ICalendarService) ApplicationContext
					.getInstance().getBean(CalendarEventImpl.NAME);
			HashMap calendarMap = calendarService.getAlertCalendarsEvent(user.getId());
			if (calendarMap != null && calendarMap.get("evts") instanceof ArrayList)
			{
				ArrayList list = (ArrayList) calendarMap.get("evts");
				int alertEventSize = list.size();
				nums=new Long[2];
				nums[0]=(long)alertEventSize;
				nums[1]=(long)alertEventSize;
				backlist.add(nums);//日程提醒数量
				//以下代码暂时没有用到
				Object object;
				HashMap map;
				Date date;
				for (int i = 0; i < list.size(); i++)
				{
					object = list.get(i);
					if (object instanceof HashMap)
					{
						map = (HashMap) object;
						try
						{
							date = (Date) map.get("start");
							map.put("start", apps.transmanager.weboffice.util.DateUtils
									.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
							date = (Date) map.get("end");
							map.put("end", apps.transmanager.weboffice.util.DateUtils
									.ftmDateToString("yyyy-MM-dd HH:mm:ss", date));
						}
						catch (Exception e)
						{
						}
					}
				}
			}
			
			
			return backlist;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
