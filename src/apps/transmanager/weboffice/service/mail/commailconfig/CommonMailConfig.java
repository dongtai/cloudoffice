package apps.transmanager.weboffice.service.mail.commailconfig;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Properties;

public class CommonMailConfig {

	private Properties pro;

	public static CommonMailConfig createCommonMailConfig(String domain) {
		if (domain == null) {
			return null;
		}
		CommonMailConfig cmc = new CommonMailConfig();
		cmc.pro = new Properties();
		try {
			cmc.pro.load(CommonMailConfig.class.getResourceAsStream(domain
					+ ".properties"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(domain+"不存在");
			return null;
		}
		return cmc;
	}

	public static ArrayList<String> getAllDomain() {
		ArrayList<String> lists = null;
		File file = new File(CommonMailConfig.class.getResource("").getPath());
		File[] files = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if (name.endsWith(".properties")) {
					return true;
				}
				return false;
			}
		});
		if (files != null) {
			lists = new ArrayList<String>();
			for (int i = 0; i < files.length; i++) {
				String filename = files[i].getName();
				lists.add(filename.substring(0,filename.lastIndexOf('.')));
			}
		}
		return lists;
	}

	public String getIncomingServer() {
		return pro.getProperty("incoming_server");
	}

	public int getIncomingport() {
		return Integer.parseInt(pro.getProperty("incoming_port"));
	}

	public Boolean getInSSL() {
		return Boolean.parseBoolean(pro.getProperty("incoming_ssl"));
	}

	public String getIncomingServerType() {
		return pro.getProperty("server_type");
	}

	public String getOutgoingServer() {
		return pro.getProperty("outgoing_server");
	}

	public int getOutgoingport() {
		return Integer.parseInt(pro.getProperty("outgoing_port"));
	}

	public Boolean getOutSSL() {
		return Boolean.parseBoolean(pro.getProperty("outgoing_ssl"));
	}

	public Boolean getSmtpAuth() {
		return Boolean.parseBoolean(pro.getProperty("smtp_auth"));
	}

	public Boolean getSmtpAuthSameasin() {
		return Boolean.parseBoolean(pro.getProperty("smtpAuth_Sameasin"));
	}
	
	public Boolean inuserisFullEmailAddress() {
		String obj = pro.getProperty("inuser_isFullEmailAddress");
		if(obj == null)
		{
			return Boolean.FALSE;
		}
		return Boolean.parseBoolean(obj);
	}

}
