package dcs.config;

import java.util.Properties;

public class Config {
	private static final String configfile = "convertconfig.properties";

	private static Config config;

	private Properties pro = new Properties();

	private Config() {

	}

	public Properties getPro() {
		return pro;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public int getLimitConvertSize() {
		return limitConvertSize;
	}

	public int getpicConvertperPages() {
		return picConvertperPages;
	}

	public String getPicStyle() {
		return picStyle;
	}

	public float getZoom() {
		return defaultzoom;
	}

	public int[] isBadjudge() {
		return badjudge;
	}

	public boolean isRMI() {
		return this.isRMI;
	}

	public String getSrcFolder() {
		return srcFolder;
	}

	public String getTarFolder() {
		return tarFolder;
	}

	// 池内维护了最大为5个实例，可以根据自己的服务器性能调整最大值
	private int maxSize = 2;
	// 回收机制，简单的处理下可以避免内存使用过多。转500次文档之后就释放回收文档
	private int limitConvertSize = 500;
	private int[] badjudge;// 转换过程中出现异常回收
	// 每次转换图片的数量
	private int picConvertperPages = 5;
	// 转换图片生成的格式
	private String picStyle = "gif";
	// 图片缩放的大小,默认的zoom，今后可能会由浏览器传入。
	private float defaultzoom = 1.0f;

	// 是否是rmi的方式调用？
	private boolean isRMI;

	// 文档转换，源文件夹
	private String srcFolder;
	// 文档转换，目标文件夹
	private String tarFolder;

	public static Config getinstance() {
		if (config == null) {
			config = new Config();
			try {
				config.pro.load(config.getClass().getResourceAsStream(
						configfile));
				Object obj = config.pro.get("maxSize");
				config.maxSize = Integer.parseInt(obj.toString());
				obj = config.pro.get("limitConvertSize");
				config.limitConvertSize = Integer.parseInt(obj.toString());
				obj = config.pro.get("picConvertperPages");
				config.picConvertperPages = Integer.parseInt(obj.toString());
				obj = config.pro.get("picStyle");
				config.picStyle = obj.toString() == null ? config.picStyle
						: obj.toString();
				obj = config.pro.get("defaultzoom");
				config.defaultzoom = Float.parseFloat(obj.toString());
				obj = config.pro.get("badjudge");
				String[] values = obj.toString().split(",");
				config.badjudge = new int[values.length];
				for (int i = 0; i < values.length; i++) {
					config.badjudge[i] = Integer.parseInt(values[i]);
				}
				String rmiString = config.pro.getProperty("rmi");
				if ("0".equals(rmiString)) {
					config.isRMI = true;
				}
				String srcFolder = config.pro.getProperty("srcFolder");
				if (srcFolder != null && srcFolder.length() != 0) {
					config.srcFolder = srcFolder;
				}
				String tarFolder = config.pro.getProperty("tarFolder");
				if (tarFolder != null && tarFolder.length() != 0) {
					config.tarFolder = tarFolder;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				config.maxSize = 2;
				config.limitConvertSize = 500;
				config.picConvertperPages = 5;
				config.picStyle = "gif";
				config.defaultzoom = 1.0f;
				config.badjudge = null;
				e.printStackTrace();
			}
		}
		return config;
	}
}
