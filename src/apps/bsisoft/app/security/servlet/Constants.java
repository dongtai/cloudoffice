package apps.bsisoft.app.security.servlet;

import java.io.IOException;
import java.util.Properties;

public class Constants {
	
	public static final String CONFIG_FILE_NAME = "csspClient.properties";
	
	public static final String SUCCESS_DISPATCH_URL = "success_loginReDispatcherURL";
	public static final String FAILURE_DISPATCH_URL = "failure_loginReDispatcherURL";
	public static final String DN_ATTRIBUTE_NAME = "dn_attribute_name";
	public static final String REF_FILE = "refFile";

	public static final String MAX_CONN = "maxconn"; 
	public static final String OUT_TIME = "outtime";
	
	public static boolean isDebug = true;
	public static Properties ps = null;
	private Constants(){
		
		
	}
	
	public static synchronized Properties getProperties(){
		
		if(ps == null){
			ps = new Properties();
			try {
				ps.load(Constants.class.getClassLoader().getResourceAsStream(Constants.CONFIG_FILE_NAME));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(ps != null){
			String debug = ps.getProperty("debug");
			if(debug != null && !debug.trim().equals("")){
				isDebug = new Boolean(debug.trim());
			}
		}
		
		return ps;
		
		
	}
	
	
	

}