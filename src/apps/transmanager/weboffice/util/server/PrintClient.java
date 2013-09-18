package apps.transmanager.weboffice.util.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintClient 
{
	public static boolean printDocument(String printIP,String printPort,String filePath,String printName,String copyNum,String pageRange,String oldName,long tempFileSize)
	{
		boolean flag=false;
		Socket socket = null;
		DataInputStream inputStream = null;
		DataOutputStream out = null;
		try {
			socket = new Socket(printIP,Integer.parseInt(printPort));
			out = new DataOutputStream(socket.getOutputStream());
			File file = new File(filePath);								
			DataInputStream reader =  new DataInputStream(new BufferedInputStream(new FileInputStream(file)));					               
			out.writeUTF("1&"+printName+"&"+file.getName()+"&"+copyNum+"&"+pageRange+"&"+oldName+"&"+tempFileSize); //附带文件名  
			int bufferSize = 8 * 1024; //80K  
			byte[] buf = new byte[bufferSize];  
			int read = 0;  
			while((read=reader.read(buf)) != -1)
			{  
				out.write(buf, 0, read);  				
			}  
			out.flush();
			System.out.println("文件发送完毕，等待服务器返回信息！");
			inputStream = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));			
			String msg = inputStream.readUTF();
			System.out.println("服务器返回信息 ： "+msg);			
			
			if("success".equals(msg))
			{
				flag = true;
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				out.close();
				inputStream.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	public static List getAllPrinter(String printIP,String printPort)
	{
		List result = new ArrayList();
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream inputStream = null;
		try {
			socket = new Socket(printIP,Integer.parseInt(printPort));
			out = new DataOutputStream(socket.getOutputStream());				               
			out.writeUTF("2&"); //附带文件名  
			out.flush();
			System.out.println("文件发送完毕，等待服务器返回信息！");
			inputStream = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));			
			String msg = inputStream.readUTF();
			System.out.println("服务器返回信息 ： "+msg);
			if(!"".equals(msg))
			{
				String[] tempMsg = msg.split("&");
				for(int i=0;i<tempMsg.length;i++)
				{
					result.add(tempMsg[i]);
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				if(out!=null){
					out.close();
				}
				if (inputStream!=null) {
					inputStream.close();
				}
				if (socket!=null) {
					socket.close();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	public static List<Map> getPrinterStatus(String printIP,String printPort,Map printFileNames)
	{
		List<Map> result = new ArrayList<Map>();
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream inputStream = null;
		try {
			socket = new Socket(printIP,Integer.parseInt(printPort));
			out = new DataOutputStream(socket.getOutputStream());				               
			out.writeUTF("3&"); //附带文件名  
			out.flush();
			System.out.println("文件发送完毕，等待服务器返回信息！");
			inputStream = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));			
			String msg = inputStream.readUTF();
			System.out.println("服务器返回信息 ： "+msg);
			if(!"".equals(msg))
			{
				String[] msgArray = msg.split("&");
				for(int i=0;i<msgArray.length;i++)
				{
					String temp = msgArray[i];
					String[] status = temp.split("#");
					Map map = new HashMap();
					String filesize = (String)printFileNames.get(status[0]);
			        if(null==filesize)
			        {
			        	 continue;
			        }
			        map.put("fileName", status[0]);
			        map.put("status", status[1]);
			        map.put("currentPage", status[2]);
			        map.put("printtime", status[3]);
			        map.put("size", filesize);
					result.add(map);
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				out.close();
				inputStream.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
}
