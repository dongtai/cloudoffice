package dcs.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import dcs.config.ClusterConfig;
import dcs.config.RMIConfig;
import dcs.interfaces.IConvert;

public class ConvertRMIClusterRootServer {
	public static void main(String[] argv) {
		try {
			int port;
			String serviceName;//必须有
			String ip;
			if(argv != null && argv.length != 0)
			{
				serviceName = argv.length > 0 ? argv[0] : null;
				String registryPort = argv.length > 1 ? argv[1] : null;
				port = registryPort == null || registryPort.length() == 0 ? 1099 : Integer.parseInt(registryPort);
				ip = argv.length > 2 ? argv[2] : null;
				ip = ip == null || ip.length() == 0 ? "127.0.0.1" : ip;
			}
			else
			{
				port = RMIConfig.getRMIConfig().getPort();
				serviceName =  RMIConfig.getRMIConfig().getServiceName();
				ip = RMIConfig.getRMIConfig().getIP();
			}

			LocateRegistry.createRegistry(port);
			ClusterConfig.setRoot(true);
			IConvert convert = new ConvertCluster();
			Naming.rebind("//"+ip+":"+port+"/"+serviceName, convert);

			//Naming.rebind("//192.168.1.105:1099/Hello", convert);

			System.out.println("ConvertRMIClusterServer Server "+port+" is ready.");
			Object lock = new Object();
			synchronized (lock) {
				lock.wait();
			}

		} catch (Exception e) {
			System.out.println("Convert Server failed: " + e);
		}
	}
}
