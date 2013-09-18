package dcs.config;

import java.io.IOException;
import java.util.Properties;

public class RMIConfig {

	private RMIConfig() {
		init();
	}

	private static RMIConfig rmiConfig;

	public static RMIConfig getRMIConfig() {
		if (rmiConfig == null) {
			rmiConfig = new RMIConfig();
		}
		return rmiConfig;
	}

	private int port;
	private String serviceName;
	private String ip;
	private int rmisocket;
	private String hostname;
	private String[] params;
	public int getPort() {
		return port;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getIP() {
		return ip;
	}

	public int getRmisocket() {
		return rmisocket;
	}

	public String[] getParams()
	{
		return params;
	}

	public void setParams(String[] params)
	{
		this.params = params;
	}

	public String getHostname() {
		return hostname;
	}

	private void init() {

		Properties pro = new Properties();
		try {
			pro.load(RMIConfig.class.getResourceAsStream("rmi.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String registryPort = pro.getProperty("registryPort");
		port = registryPort == null || registryPort.length() == 0 ? 1099
				: Integer.parseInt(registryPort);
		String rmiSockets = pro.getProperty("rmiSocket");
		rmisocket = rmiSockets == null || rmiSockets.length() == 0 ? 0
				: Integer.parseInt(rmiSockets);
		serviceName = pro.getProperty("serviceName");
		
		hostname = pro.getProperty("hostname");
		hostname = hostname != null && hostname.length() > 0 ? hostname : null;

		ip = pro.getProperty("ip");
		ip = ip == null || ip.length() == 0 ? "127.0.0.1" : ip;
		String temp = pro.getProperty("params");
		params = temp.trim().split(" ");

	}
}
