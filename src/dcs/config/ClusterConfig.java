package dcs.config;

import java.io.IOException;
import java.util.Properties;

public class ClusterConfig {

	private static boolean isRoot;

	private ClusterConfig() {
		init();
	}

	private static ClusterConfig clusterConfig;

	private static ClusterConfig getClusterConfig() {
		if (clusterConfig == null) {
			clusterConfig = new ClusterConfig();
		}
		return clusterConfig;
	}

	// 执行的命令程序运行
	private String[] args;
	private String directory;
	private String[][] configs;

	private long overtime;

	public static String[] getConfig(int index) {
		if (isRoot) {
			return ClusterRootConfig.getConfig(index);
		}
		ClusterConfig clusterConfig = getClusterConfig();
		if (clusterConfig.configs != null
				&& index < clusterConfig.configs.length) {
			return getClusterConfig().configs[index];
		}
		return null;
	}

	public static int getConfigSize() {
		if (isRoot) {
			return ClusterRootConfig.getConfigSize();
		}
		ClusterConfig clusterConfig = getClusterConfig();
		if (clusterConfig.configs != null) {
			return clusterConfig.configs.length;
		}
		return 0;
	}

	public static String[] getArgs() {
		return getClusterConfig().args;
	}

	public static String getDirectory() {
		return getClusterConfig().directory;
	}

	public static long getOvertime() {
		if (isRoot) {//root的话，overtime的时间长一些。实际上没用。
			return 2 * getClusterConfig().overtime;
		}
		return getClusterConfig().overtime;
	}

	public static boolean isRoot() {
		return ClusterConfig.isRoot;
	}

	public static void setRoot(boolean isRoot) {
		ClusterConfig.isRoot = isRoot;
	}

	private void init() {

		Properties pro = new Properties();
		try {
			pro.load(ClusterConfig.class
					.getResourceAsStream("rmicluster.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String serviceNames = pro.getProperty("serviceNames");
		String[] serviceName = null;
		if (serviceNames != null && serviceNames.length() != 0) {
			serviceName = serviceNames.split(",");
		}
		int size = serviceName != null ? serviceName.length : 0;

		String registryPorts = pro.getProperty("registryPorts");
		String[] registryPort = new String[size];
		if (registryPorts != null && registryPorts.length() != 0) {
			registryPort = registryPorts.split(",");
		}
		String ips = pro.getProperty("ips");
		String[] ip = new String[size];
		if (ips != null && ips.length() != 0) {
			ip = ips.split(",");
		}

		configs = new String[size][3];
		for (int i = 0; i < configs.length; i++) {
			String tmpip = ip[i];
			tmpip = tmpip == null || tmpip.length() == 0 ? "127.0.0.1" : tmpip;
			String tmpregistryPort = registryPort[i];
			String port = registryPort == null || tmpregistryPort.length() == 0 ? "1099"
					: tmpregistryPort;
			configs[i] = new String[] { serviceName[i], port, tmpip };
		}
		String overtimeSt = pro.getProperty("overtime");
		if (overtimeSt != null && overtimeSt.length() != 0) {
			overtime = Integer.parseInt(overtimeSt) * 1000;
		}
		args = pro.getProperty("args").split(" ");
		directory = pro.getProperty("directory");
	}

}
