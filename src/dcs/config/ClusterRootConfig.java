package dcs.config;

import java.io.IOException;
import java.util.Properties;

public class ClusterRootConfig {
	private ClusterRootConfig() {
		init();
	}

	private static ClusterRootConfig clusterRootConfig;

	private static ClusterRootConfig getClusterRootConfig() {
		if (clusterRootConfig == null) {
			clusterRootConfig = new ClusterRootConfig();
		}
		return clusterRootConfig;
	}
	
	private String[][] configs;


	public static String[] getConfig(int index) {
		ClusterRootConfig clusterRootConfig = getClusterRootConfig();
		if (clusterRootConfig.configs != null
				&& index < clusterRootConfig.configs.length) {
			return getClusterRootConfig().configs[index];
		}
		return null;
	}

	public static int getConfigSize() {
		ClusterRootConfig clusterRootConfig = getClusterRootConfig();
		if (clusterRootConfig.configs != null) {
			return clusterRootConfig.configs.length;
		}
		return 0;
	}
	
	private void init() {

		Properties pro = new Properties();
		try {
			pro.load(ClusterConfig.class
					.getResourceAsStream("rmiclusterRoot.properties"));
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
	}
}
