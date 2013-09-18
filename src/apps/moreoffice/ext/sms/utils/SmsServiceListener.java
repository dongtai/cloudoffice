package apps.moreoffice.ext.sms.utils;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;


public class SmsServiceListener implements ServletContextListener {
		private static final SerialModemGateway gateway = new SerialModemGateway("*", "/dev/ttyS0", 9600, "Siemens", "T35"); //9600 8N1
		private static final Service service = Service.getInstance();
		public void contextDestroyed(ServletContextEvent arg0) {
			try {
				service.stopService();
				service.removeGateway(gateway);
			} catch (Throwable e) {
				e.printStackTrace();
			} 
			//System.out.println("SmsServiceListener destroyed");
		}

		public void contextInitialized(ServletContextEvent arg0) {
			startService(); 
			new SmsServiceSpirit(service).start(); //System.out.println("****  发送短信监控开启！********");
		}

		private void startService() {
			try {
				gateway.setOutbound(true);
				service.addGateway(gateway);
				service.startService();
				System.out.println("****  短信猫已启动！********");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
