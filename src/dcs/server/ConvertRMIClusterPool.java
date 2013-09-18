package dcs.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

import dcs.config.ClusterConfig;
import dcs.interfaces.IConvert;

public class ConvertRMIClusterPool {
	private static ConvertRMIClusterPool instance = null;
	private ArrayList<ConvertorClusterObject> pool = new ArrayList<ConvertorClusterObject>();
	private ArrayList<IConvert> lists = new ArrayList<IConvert>();
	private ArrayList<String[]> configlists = new ArrayList<String[]>();
	private int availSize = 0;
	private int current = 0;
	// 池内维护了最大为5个实例，可以根据自己的服务器性能调整最大值
	private int maxSize = 2;

	public static ConvertRMIClusterPool getInstance() {
		if (instance == null) {
			instance = new ConvertRMIClusterPool();
		}
		return instance;
	}

	private ConvertRMIClusterPool() {
		int size = ClusterConfig.getConfigSize();
		maxSize = size;
		for (int i = 0; i < size; i++) {
			String[] configs = ClusterConfig.getConfig(i);
			IConvert convertor = getInitConvert(configs);
			if (convertor == null) {
				maxSize--;
				continue;
			}
			lists.add(convertor);
			configlists.add(configs);
		}
	}

	// 获取池内一个转换实例
	public synchronized ConvertorClusterObject getConvertor() {
		if (availSize > 0) {
			ConvertorClusterObject cco = getIdleConvertor();
			if (cco == null) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return getConvertor();
			}
			return cco;
		} else if (pool.size() < maxSize) {
			return createNewConvertor(lists.get(pool.size()),
					configlists.get(pool.size()));
		} else {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return getConvertor();
		}
	}

	private synchronized ConvertorClusterObject getIdleConvertor() {
		for (ConvertorClusterObject co : pool) {
			if (co.available) {
				if (co.convertor == null) {
					IConvert cv = connect(co.configs);
					if (cv == null) {
						continue;
					} else {
						co.convertor = cv;
					}
				}
				co.available = false;
				availSize--;
				return co;
			}
		}
		return null;
	}

	private synchronized ConvertorClusterObject createNewConvertor(
			IConvert convertor, String[] configs) {
		ConvertorClusterObject co = new ConvertorClusterObject(++current);
		co.convertor = convertor;//
		co.available = false;
		co.configs = configs;
		pool.add(co);
		return co;
	}

	public synchronized void returnConvertor(ConvertorClusterObject convertor) {
		for (ConvertorClusterObject co : pool) {
			if (co == convertor) {
				co.available = true;
				availSize++;
				notify();
				break;
			}
		}
	}

	public synchronized void returnBadConvertor(ConvertorClusterObject convertor) {
		for (ConvertorClusterObject co : pool) {
			if (co == convertor) {
				String[] configs = co.configs;
				IConvert con = co.convertor;
				co.convertor = null;
				try {
					// 退出
					con.exit();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
				// 重启---
				co.convertor = getInitConvert(configs);
				/*
				 * if (co.convertor == null) { //放弃了连接，永不在使用 co.available =
				 * false; pool.remove(co); maxSize--; break; }
				 */
				break;
			}
		}
	}

	// 包装convert类，可记录是否在使用中
	public class ConvertorClusterObject {
		private ConvertorClusterObject(int id) {
			this.id = id;
		}

		private int id;
		public IConvert convertor;
		private boolean available;
		private String[] configs;
	}

	// -----------------------
	private IConvert getInitConvert(String[] configs) {
		start(configs);
		IConvert convertor = connectIn60SEC(configs);
		return convertor;
	}

	private IConvert connect(String[] configs) {
		String serviceName = configs[0];
		String registryPort = configs[1];
		int port = registryPort == null || registryPort.length() == 0 ? 1099
				: Integer.parseInt(registryPort);
		String ip = configs[2];
		IConvert convertor = null;
		try {
			convertor = (IConvert) Naming.lookup("//" + ip + ":" + port + "/"
					+ serviceName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertor;
	}

	private IConvert connectIn60SEC(String[] configs) {
		String serviceName = configs[0];
		int port = Integer.parseInt(configs[1]);
		String ip = configs[2];
		IConvert convertor = null;
		// 连接60s如果失败，就不在连接
		for (int i = 0; i < 60; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				convertor = (IConvert) Naming.lookup("//" + ip + ":" + port
						+ "/" + serviceName);
				break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return convertor;
	}

	private boolean isstart(String serviceName, int port, String ip) {
		try {
			IConvert convertor = (IConvert) Naming.lookup("//" + ip + ":"
					+ port + "/" + serviceName);
			return convertor.testConnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return false;
	}

	private class PrintThread extends Thread {

		private PrintThread(InputStream is) {
			this.is = is;
		}

		private InputStream is = null;

		public void run() {
			InputStreamReader ir = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(ir);
			try {

				// 关键部分
				while (true) {
					int len = 1024;
					char[] cbuf = new char[len + 1];
					int status = br.read(cbuf);
					if (status == -1) {
						break;
					} else {
						System.out.println(new String(cbuf)
								.substring(0, status)); // 输出流的所有内容
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (ir != null)
						ir.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private void start(String[] configs) {
		if (ClusterConfig.isRoot()) {
			return;
		}
		String serviceName = configs[0];
		int port = Integer.parseInt(configs[1]);
		String ip = configs[2];
		if (isstart(serviceName, port, ip)) {
			System.out.println("Convert Server " + port + " has bean ready.");
			return;
		}
		ArrayList<String> list = new ArrayList<String>();
		list.add("java");
		String[] args = ClusterConfig.getArgs();
		for (int i = 0; i < args.length; i++) {
			list.add(args[i]);
		}
		list.add("dcs.server.ConvertRmiServer");
		list.add(serviceName);
		list.add(String.valueOf(port));
		list.add(ip);
		ProcessBuilder pb = new ProcessBuilder(list);
		// 设置环境变量
		/*
		 * Map env = pb.environment(); env.put("key1", "value1");
		 * env.remove("key2"); env.put("key2", env.get("key1") + "_test");
		 */
		pb.directory(new File(ClusterConfig.getDirectory())); // 设置工作目录
		try {
			Process p = pb.start();
			PrintThread pt = new PrintThread(p.getInputStream());
			pt.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 建立子进程
	}
	// -----------
}
