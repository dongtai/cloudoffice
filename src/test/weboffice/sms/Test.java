package test.weboffice.sms;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.apache.log4j.helpers.LogLog;

import apps.moreoffice.ext.sms.utils.ConnInit;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String httpAddr = "http://service.winic.org:8009/sys_port/gateway/?id=dongfang_li&pwd=dongfang_li2&to=&content=";
//		
//		System.out.println(httpAddr.substring(0, httpAddr.indexOf("?")));
//		
//		String str1 = httpAddr.substring(httpAddr.indexOf("?")+1);
//		System.out.println(str1);
//		
//		//System.out.println(str1.substring(str1.indexOf("&")));
//		
//		//去掉最后一个&及以后的内容
//		String str = httpAddr.substring(0, httpAddr.lastIndexOf("&"));
//		
//		System.out.println(str);
//		
//		System.out.println(str.substring(str.lastIndexOf("&")));
//		System.out.println(httpAddr.substring(httpAddr.lastIndexOf("&")));
//		
//		System.out.println("**********");
//		
//		String[] params = str1.split("&");
//		System.out.println(params[0]);
//		System.out.println(params[1]);
//		
//		System.out.println(params[params.length-2]);
//		System.out.println(params[params.length-1]);
		
//		for (String s : params) {
//			System.out.println(s);
//		}
		
		/*		try {
		Enumeration propertyNames = properties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			Object key = propertyNames.nextElement();
			System.out.println("key=" + key + "; value=" + properties.getProperty((String) key));
		}
	} catch (Throwable e) {
		e.printStackTrace();
	}*/
		
		//System.out.println(new java.util.Date(System.currentTimeMillis()));
		
		//doConfigure();
		//
		
		//String receiver = null;
		
		//System.out.println( !"".equals(receiver)&&receiver!=null);
		
		
//		Thread sss = new SmsServiceSpirit();
//		
//		sss.start();
		
		for (int i=1; i<=1999; i++) {
			insert("test"+i);
		}
		
	}
	
	
	public static void doConfigure() {
	    Properties props = new Properties();
	    try {
 	//      FileInputStream istream = new FileInputStream("log4j.properties");
	      InputStream istream = Test.class.getClassLoader().getResourceAsStream("log4j.properties");
	      props.load(istream);
	      istream.close();
	    }
	    catch (IOException e) {
	      LogLog.error("Could not read configuration file [log4j.properties].", e);
	      LogLog.error("Ignoring configuration file [log4j.properties].");
	      return;
	    }
	  }
	
	private static void insert(String loginName) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = ConnInit.getInstance().getConnection();
			
			String sql = " insert userinfo(userName, passW, email, realName, role, storageSize, companyID) " +
					" values(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement prStm = conn.prepareStatement(sql);
			
			prStm.setString(1, loginName);
			prStm.setString(2, "E10ADC3949BA59ABBE56E057F20F883E");
			prStm.setString(3, loginName+"@163.com");
			prStm.setString(4, loginName);
			prStm.setInt(5, 4);
			prStm.setString(6, "1024");
			prStm.setString(7, "public");
			
			prStm.executeUpdate();
			prStm.clearParameters();
			
			int id = 0;
			sql = "select userID from userinfo where userName='"+loginName+"'";
			prStm = conn.prepareStatement(sql);
			ResultSet sets = prStm.executeQuery();
			
			while(sets.next()) {
				id = sets.getInt("userID");
			}
			prStm.clearParameters();

			System.out.println(id);
			
			sql = " insert groupmemberinfo(groupID, memberID) values(?, ?)";
			prStm = conn.prepareStatement(sql);
			
			prStm.setInt(1, 2);
			prStm.setInt(2, id);
					
			prStm.executeUpdate();
			prStm.clearParameters();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnInit.getInstance().free(ps, conn);
		}
	}

}
