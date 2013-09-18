package test;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class Test {
	
	public static void main(String[] args)
	{
		String httpsUrl="https://58.216.242.130/static/fileservice?jsonParams={method:\"openFile\",params:{domain:\"com.yozo.do\",account:\"test\",path:\"user_test_1367140286821/Document/3.doc\"},token:\"C14C0BE9BC2F4C4FB2888632D12AE6291367471867791\"}";
		try
		{
			HostnameVerifier hv = new HostnameVerifier() {  
			public boolean verify(String urlHostName, SSLSession session) {
				System.out.println("Warning: URL Host: " + urlHostName + " vs. "  
						+ session.getPeerHost());  
					return true;  
				}  
			};  

			HttpURLConnection connection = null;  
			try {
				trustAllHttpsCertificates();
				HttpsURLConnection.setDefaultHostnameVerifier(hv);
				URL validationUrl = new URL(httpsUrl);
				connection = (HttpURLConnection) validationUrl.openConnection();  
				String line;  
				final StringBuffer stringBuffer = new StringBuffer(255);  
				BufferedInputStream in = new BufferedInputStream(connection.getInputStream());  

				FileOutputStream tem = new FileOutputStream("d:/testhttps.doc");
				 byte[] mByte = new byte[4096];
				 int size=0;
				 while ((size = in.read(mByte)) != -1)
				 {
					 tem.write(mByte, 0, size);
				 }
				 tem.flush();
				 tem.close();
				 in.close();
			} catch (Exception e) {  
				e.printStackTrace();
			}finally {  
				if (connection != null) {  
					connection.disconnect();  
				}  
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
	private static void trustAllHttpsCertificates() throws Exception {  
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];  
		javax.net.ssl.TrustManager tm = new miTM();  
		trustAllCerts[0] = tm;  
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext  
				.getInstance("SSL");  
			sc.init(null, trustAllCerts, null);  
			javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc  
					.getSocketFactory());  
	}

	static class miTM implements javax.net.ssl.TrustManager,  
	javax.net.ssl.X509TrustManager {  
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
			return null;  
		}  

		public boolean isServerTrusted(  
				java.security.cert.X509Certificate[] certs) {  
					return true;  
		}  

		public boolean isClientTrusted(  
				java.security.cert.X509Certificate[] certs) {  
				return true;  
		}  

		public void checkServerTrusted(  
				java.security.cert.X509Certificate[] certs, String authType)  
						throws java.security.cert.CertificateException {  
			return;  
		}  

		public void checkClientTrusted(  
				java.security.cert.X509Certificate[] certs, String authType)  
						throws java.security.cert.CertificateException {  
			return;  
		}  
	}
}
