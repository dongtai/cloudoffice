package test;

import javax.net.ssl.HostnameVerifier;  
import javax.net.ssl.SSLSession;


public class TestHostname implements HostnameVerifier {  
		@Override  
		public boolean verify(String hostname, SSLSession session) {  
//			if("localhost".equals(hostname)){  
//				return true;  
//			} else {  
//				return false;  
//			}  
			return true;
		}  
	}
