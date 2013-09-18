package dcs.client;

import java.io.File;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import apps.transmanager.weboffice.service.config.WebConfig;
import dcs.config.RMIConfig;
import dcs.interfaces.IConvert;
import dcs.interfaces.IPICConvertor;

public class ConvertRmiClient {

	private static ConvertRmiClient cc;

	private static ConvertRmiClient getInstance() {
		if (cc == null) {
			cc = new ConvertRmiClient();
		}
		return cc;
	}

	public static IConvert getConvertInstance() {
		IConvert convert = getInstance().getConvert();
		try {
			if (convert != null && convert.testConnect())// 假设转换服务器端重启后，重新连接
			{
				return convert;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			getInstance().convertor = null;
			e.printStackTrace();
		}
		return getInstance().getConvert();
	}

	private IConvert convertor;

	private IConvert getConvert() {
		if (convertor == null) {

			try {
				convertor = getC();
					//(IConvert) Naming.lookup("//"
					//	+ RMIConfig.getRMIConfig().getIP() + ":"
					//	+ RMIConfig.getRMIConfig().getPort() + "/"
					//	+ RMIConfig.getRMIConfig().getServiceName());
			} 
			catch(ConnectException ce)
			{
				try
				{
					startServer();
					Thread.currentThread().sleep(5000);
					convertor = getC();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			catch (Exception e) 
			{
				System.out.println("HelloClient exception: " + e);
			}

			/*
			 * ApplicationContext ctx = new ClassPathXmlApplicationContext(
			 * "com/evermore/weboffice/util/server/convertforread/rmi/client.xml"
			 * ); convertor = (IConvert) ctx.getBean("mobileAccountService");
			 */
		}

		/*
		 * try { //(HelloInterface)Naming.lookup("//127.0.0.1:1099/Hello");
		 * convertor = (IConvert) Naming.lookup("Convert"); } catch (Exception
		 * e) { System.out.println("HelloClient exception: " + e); }
		 */
		return convertor;

	}
	
	private IConvert getC() throws Exception
	{
		return (IConvert) Naming.lookup("//"
				+ RMIConfig.getRMIConfig().getIP() + ":"
				+ RMIConfig.getRMIConfig().getPort() + "/"
				+ RMIConfig.getRMIConfig().getServiceName());
	}
	
	private void startServer()
	{
		String os = System.getProperty("os.name");
        boolean isWin = os.indexOf("Win") >= 0 || os.indexOf("win") >= 0;
		String java = System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + (isWin ? "java.exe" : "java");
		String path = WebConfig.webContextPath  + File.separatorChar + "WEB-INF" + File.separatorChar + "classes";
		String[] params = RMIConfig.getRMIConfig().getParams();
		int size = params.length;
		String[] exc  = new String[size + 3];
		exc[0] = java;
		exc[1] = "-classpath";
		exc[2] = path;
		System.arraycopy(params, 0, exc, 3, size);
		try
		{
			Process process = Runtime.getRuntime().exec(exc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("=====java===\n\n\n====" + java + "===path==" + path + "===\n\n\n");
	}

	// -----------test---------------//
	public static void main(String[] argv) {
		IPICConvertor pic = null;
		try {
			IConvert hello = ConvertRmiClient.getConvertInstance();
			pic = hello.convertMStoPic("/yozosoft/test/in/111.doc");
			pic.convertToGIF(0, -1, 1.5f, "/yozosoft/test/out");

		} catch (Exception e) {
			System.out.println("HelloClient exception: " + e);
		} finally {
			if (pic != null)
				try {
					pic.close();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
