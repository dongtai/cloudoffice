package apps.moreoffice.ext.sms.utils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.TimerTask;

import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.Message.MessageEncodings;


/**CREATE TABLE `smsMessage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `receiver` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `content` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `isSend` varchar(10) COLLATE utf8_unicode_ci DEFAULT 'notSend',
  `reply` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sender` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sendtime` timestamp NULL DEFAULT NULL,
  `createTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
*/

public class SmsServiceSpirit extends Thread {
	private int id = 0;
	private String receiver = ""; //共享池变量
	private String content = "";
	private Service service = null;
	
	public SmsServiceSpirit() {
	}
	
	public SmsServiceSpirit(Service service) {
		this.service = service;
	}

	TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			Connection conn = null;
			PreparedStatement prStm = null;
			ResultSet sets = null;
			try {
				conn = ConnInit.getInstance().getConnection();
				String sql = "select id, receiver, content from smsMessage where isSend='notSend' order by createTime asc limit 1";
				prStm = conn.prepareStatement(sql);
				sets = prStm.executeQuery();
				
				while(sets.next()) {
					id = sets.getInt("id");
					receiver = sets.getString("receiver");
					content = sets.getString("content");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ConnInit.getInstance().free(sets, prStm, conn);
			}
		}
	};
	
	
	/** 发送一条短信
	 * @param id
	 * @param receiver
	 * @param content
	 * @return
	 */
	private boolean send(int id, String receiver, String content) {
		boolean result = false;
		if (sendMessage(receiver, content)) {
			result = changeMsgStatus(id, receiver, content);
		}
		return result;
	}

	/** 短信猫发送一条短信
	 * @param receiver
	 * @param content
	 * @return 
	 */
	private boolean sendMessage(String receiver, String content) {
		boolean result = false;
		try {
			OutboundMessage msg = new OutboundMessage(receiver, content);// Send a message synchronously.
			msg.setEncoding(MessageEncodings.ENCUCS2);
			result = service.sendMessage(msg);
			//service.queueMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/** 发送一条记录修改消息状态，来达到从发送队列中删除
	 * @param id
	 * @param receiver
	 * @param content
	 * @return
	 */
	public boolean changeMsgStatus(int id, String receiver, String content) {
		Connection conn = null;
		PreparedStatement prStm = null;
		int result = 0;
		try {
			conn = ConnInit.getInstance().getConnection();
			String sql = "update smsMessage set isSend='sended',sendTime = ? where id = ?";
			prStm = conn.prepareStatement(sql);
			prStm.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			prStm.setInt(2, id);
			result = prStm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnInit.getInstance().free(prStm, conn);
		}
		System.out.println("--> "+receiver+" : "+content);
		return result!=0 ? true:false;
	}

	/** 扫描数据库的精灵线程
	 */
	@Override public void run() {
		while (true) {
			timerTask.run();
			
			if (receiver!=null && !"".equals(receiver.trim())) {
				if (send(id, receiver, content)) { 
					id = 0;
					receiver = ""; 
					content ="";
				} else {
					System.out.println("*** 短信  : " + receiver +"-->"+ content + " 发送失败！ ***");
				}
			}
		}
	}
}
