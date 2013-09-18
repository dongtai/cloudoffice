package dcs.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;

import dcs.config.RMIConfig;
import dcs.interfaces.IConvert;

public class ConvertRmiServer {

	public static void main(String[] argv) {
		try {
			int port;
			String serviceName;// 必须有
			String ip;
			int rmisocket;
			String hostname;
			if (argv != null && argv.length != 0) {
				serviceName = argv.length > 0 ? argv[0] : null;
				String registryPort = argv.length > 1 ? argv[1] : null;
				port = registryPort == null || registryPort.length() == 0 ? 1099
						: Integer.parseInt(registryPort);
				ip = argv.length > 2 ? argv[2] : null;
				ip = ip == null || ip.length() == 0 ? "127.0.0.1" : ip;
				String rmisocketstr = argv.length > 3 ? argv[3] : null;
				rmisocket = rmisocketstr == null || rmisocketstr.length() == 0 ? 0
						: Integer.parseInt(rmisocketstr);
				hostname = argv.length > 4 ? argv[4] : null;
			} else {
				port = RMIConfig.getRMIConfig().getPort();
				serviceName = RMIConfig.getRMIConfig().getServiceName();
				ip = RMIConfig.getRMIConfig().getIP();
				rmisocket = RMIConfig.getRMIConfig().getRmisocket();
				hostname = RMIConfig.getRMIConfig().getHostname();
			}
			if (hostname != null) {
				// 这个是典型的服务器有多个ip引起的rmi连接问题
				System.setProperty("java.rmi.server.hostname", hostname);
				System.out.println("java.rmi.server.hostname: " + hostname);
			}
			if (rmisocket != 0) {
				// 防火墙穿透问题
				RMISocketFactory.setSocketFactory(new SMRMISocket(rmisocket));
			}
			// 启动RMI注册服务，指定端口为1099　（1099为默认端口）
			LocateRegistry.createRegistry(port);
			// 创建远程对象的一个或多个实例，下面是hello对象 // 可以用不同名字注册不同的实例
			IConvert convert = new Convert();
			// 把hello注册到RMI注册服务器上，命名为Hello
			String str = "//" + ip + ":" + port + "/" + serviceName;
			Naming.rebind(str, convert);
			// 如果要把hello实例注册到另一台启动了RMI注册服务的机器上 //
			// Naming.rebind("//192.168.1.105:1099/Hello", convert);

			System.out.println("Convert RegistServer " + str + " is ready.");
			if (hostname != null) {
				System.out.println("Convert server " + "//" + hostname + ":"
						+ port + "/" + serviceName + " is ready.");
			}
			else {
				System.out.println("Convert server " + str + " is ready.");
			}
			// 通过spring的启动服务器
			/*
			 * new ClassPathXmlApplicationContext(
			 * "com/evermore/weboffice/util/server/convertforread/rmi/server.xml"
			 * );
			 */
			Object lock = new Object();
			synchronized (lock) {
				lock.wait();
			}

		} catch (Exception e) {
			System.out.println("Convert Server failed: " + e);
		}
	}

	static class SMRMISocket extends RMISocketFactory {

		private int rmisocket;

		SMRMISocket(int port) {
			rmisocket = port;
		}

		public Socket createSocket(String host, int port) throws IOException {
			return new Socket(host, port);
		}

		public ServerSocket createServerSocket(int port) throws IOException {
			if (port == 0)
				port = rmisocket;// rmi穿越防火墙

			System.out.println("RMI regist and transsocket =" + port);
			return new ServerSocket(port);
		}

	}
}
