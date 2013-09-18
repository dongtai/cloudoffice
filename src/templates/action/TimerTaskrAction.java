package templates.action;

import java.io.IOException;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;



public class TimerTaskrAction extends TimerTask  {
	  
	  // 定时器同时调用两次的问题需要查找原因
	public void run() {
		
		 RssAction rs =new RssAction();
		 try {
			rs.findRss();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}
