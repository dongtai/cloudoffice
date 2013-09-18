package templates.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import com.opensymphony.xwork2.ActionSupport;
public class RssAction extends ActionSupport{
	public void findRss() throws IOException{
		String classPth = "/templates/jfeed/xml/";
		rssJS(classPth);
		rssGN(classPth);
		rssPho(classPth);
		rssGJ(classPth);
		rssJUNSHI(classPth);
		rssSH(classPth);
		rssTY(classPth);
		
		}
	
	public void rssJS(String path)throws IOException{
		//百度江苏新闻
	    URL url=new URL("http://news.baidu.com/n?cmd=7&loc=2493&name=%BD%AD%CB%D5&tn=rss"); 
		URLConnection conn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"gb2312"));
		String line = null;
		String rssHtml="";
        while ((line = reader.readLine()) != null) {
        	rssHtml+=line+"\n";
		}
        rssHtml= rssHtml.replace("gb2312", "utf-8");
		File f=new File(path+"jsnews.xml");
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		pw.write(rssHtml);
		pw.close();
	}
	
	public void rssGN(String path)throws IOException{
		//百度国内新闻
	    URL url=new URL("http://news.baidu.com/n?cmd=1&class=civilnews&tn=rss"); 
		URLConnection conn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"gb2312"));
		String line = null;
		String rssHtml="";
        while ((line = reader.readLine()) != null) {
        	rssHtml+=line+"\n";
		}
        rssHtml= rssHtml.replace("gb2312", "utf-8");
		File f=new File(path+"gnnews.xml");
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		pw.write(rssHtml);
		pw.close();
	}
	
	public void rssPho(String path) throws IOException{
		//图片
	    URL url=new URL("http://feeds2.feedburner.com/ixiqi?format=xml"); 
		URLConnection conn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
		String line = null;
		String rssHtml="";
        while ((line = reader.readLine()) != null) {
        	rssHtml+=line+"\n";
		}
		File f=new File(path+"phonews.xml");
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		pw.write(rssHtml);
		pw.close();
	}
	
	public void rssGJ(String path) throws IOException{
		//国际
	    URL url=new URL("http://news.baidu.com/n?cmd=1&class=internews&tn=rss"); 
		URLConnection conn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"gb2312"));
		String line = null;
		String rssHtml="";
        while ((line = reader.readLine()) != null) {
        	rssHtml+=line+"\n";
		}
        rssHtml= rssHtml.replace("gb2312", "utf-8");
		File f=new File(path+"gjnews.xml");
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		pw.write(rssHtml);
		pw.close();
	}
	
	public void rssSH(String path) throws IOException{
		//财经
	    URL url=new URL("http://news.baidu.com/n?cmd=4&class=finannews&tn=rss"); 
		URLConnection conn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"gb2312"));
		String line = null;
		String rssHtml="";
        while ((line = reader.readLine()) != null) {
        	rssHtml+=line+"\n";
		}
        rssHtml= rssHtml.replace("gb2312", "utf-8");
		File f=new File(path+"cjnews.xml");
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		pw.write(rssHtml);
		pw.close();
	}
	
	public void rssJUNSHI(String path) throws IOException{
		//科技
	    URL url=new URL("http://news.baidu.com/n?cmd=4&class=technnews&tn=rss"); 
		URLConnection conn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"gb2312"));
		String line = null;
		String rssHtml="";
        while ((line = reader.readLine()) != null) {
        	rssHtml+=line+"\n";
		}
        rssHtml= rssHtml.replace("gb2312", "utf-8");
		File f=new File(path+"kjnews.xml");
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		pw.write(rssHtml);
		pw.close();
	}
	
	public void rssTY(String path) throws IOException{
		//互联网
	    URL url=new URL("http://news.baidu.com/n?cmd=4&class=internet&tn=rss"); 
		URLConnection conn = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"gb2312"));
		String line = null;
		String rssHtml="";
        while ((line = reader.readLine()) != null) {
        	rssHtml+=line+"\n";
		}
        rssHtml= rssHtml.replace("gb2312", "utf-8");
		File f=new File(path+"hlwnews.xml");
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		pw.write(rssHtml);
		pw.close();
	}
	
	
  
	
}
