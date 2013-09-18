package dcs.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 类转载
 * 
 * @author Administrator
 * 
 */
public class ConvertClassLoader {

	public static final String lib = "lib" + File.separatorChar
			+ "yozo_convert" + File.separatorChar;
	public static final String jar0 = "Yozo_Office.jar";
	public static final String jar1 = "EMedia.jar";
	public static final String jar2 = "jai_codec.jar";
	public static final String jar3 = "jai_core.jar";
	public static final String jar4 = "mail.jar";
	public static final String jar5 = "PDFConvert_out.jar";

	static Class<?> clazz1;

	public static Object getNewInstance() {
		Class<?> clazz1 = null;
		try {
			clazz1 = getConvertClass();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return clazz1.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getConvertClass() throws Exception {
		if (clazz1 != null) {
			return clazz1;
		}
		EngineClassLoader loader1 = new EngineClassLoader(null);
		loader1.addURL(new File(getLib() + jar0).toURI().toURL());
		loader1.addURL(new File(getLib() + jar1).toURI().toURL());
		loader1.addURL(new File(getLib() + jar2).toURI().toURL());
		loader1.addURL(new File(getLib() + jar3).toURI().toURL());
		loader1.addURL(new File(getLib() + jar4).toURI().toURL());
		loader1.addURL(new File(getLib() + jar5).toURI().toURL());
		clazz1 = loader1.loadClass("application.dcs.Convert");
		return clazz1;
	}

	static class EngineClassLoader extends URLClassLoader {

		public EngineClassLoader() {
			this(getSystemClassLoader());
		}

		public EngineClassLoader(ClassLoader parent) {
			super(new URL[] {}, parent);
		}

		public void addURL(URL... urls) {
			if (urls != null) {
				for (URL url : urls) {
					super.addURL(url);
				}
			}
		}

		public void addFile(File... files) throws IOException {
			if (files != null) {
				for (File file : files) {
					if (file != null) {
						super.addURL(file.toURI().toURL());
					}
				}
			}
		}
	}

	private static String libpath;

	public static String getLib() throws URISyntaxException {
		if (libpath == null) {
			String classesrootpath = new File(ConvertClassLoader.class
					.getResource("").toURI()).getAbsolutePath();
			libpath = classesrootpath + File.separatorChar + lib;
		}
		return libpath;
	}

}