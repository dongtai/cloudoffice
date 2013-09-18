package apps.transmanager.weboffice.util.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


public class ZipUtils 
{

	/**
	 * 设置缓冲值
	 */
	static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
	//private static byte buffer[] = new byte[BUFF_SIZE];
//	private static String CODE_UTF8 = "UTF-8";
	
	private static final String ALGORITHM = "PBEWithMD5AndDES";

	public static void zip(String zipFileName, String inputFile,String pwd) throws Exception {
		zip(zipFileName, new File(inputFile), pwd);
	}
	/**
	 * 功能描述：压缩指定路径下的所有文件
	 * @param zipFileName 压缩文件名(带有路径)
	 * @param inputFile   指定压缩文件夹
	 * @return
	 * @throws Exception  
	 */
	public static void zip(String zipFileName, String inputFile) throws Exception {
		zip(zipFileName, new File(inputFile), null);
	}

	
	/**
	 * 功能描述：压缩文件对象
	 * @param zipFileName 压缩文件名(带有路径)
	 * @param inputFile   文件对象
	 * @return
	 * @throws Exception
	 */
	public static void zip(String zipFileName, File inputFile,String pwd) throws Exception {
		File f = new File(zipFileName);
		if (!f.exists())
		{
			f.createNewFile();
		}
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		zip(out, inputFile, "",pwd);
		out.close();
	}

	/**
	 * 
	 * @param out  压缩输出流对象
	 * @param file 
	 * @param base
	 * @throws Exception
	 */
	public static void zip(ZipOutputStream outputStream, File file, String base,String pwd) throws Exception {
		base = base + (base.trim().length() == 0 ? "" : File.separator)
                + file.getName();
//		base = URLEncoder.encode(base, CODE_UTF8);
		
		if (file.isDirectory()) {
			File[] fl = file.listFiles();
			outputStream.putNextEntry(new ZipEntry(base + "/"));
			//base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zip(outputStream, fl[i], base + fl[i].getName(), pwd);
			}
		} 
		else {
			outputStream.putNextEntry(new ZipEntry(base));
			FileInputStream inputStream = new FileInputStream(file);
			//普通压缩文件
			if(pwd == null || pwd.trim().equals("")){
//				int b;
//				while ((b = inputStream.read()) != -1){
//					outputStream.write(b);
//				}
				
	            byte buffer[] = new byte[BUFF_SIZE];
	            int realLength;
	            while ((realLength = inputStream.read(buffer)) > 0) {
	            	outputStream.write(buffer, 0, realLength);
	            }
				
				inputStream.close();
			}
			//给压缩文件加密
			else{
				PBEKeySpec keySpec = new PBEKeySpec(pwd.toCharArray());
			    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
			    SecretKey passwordKey = keyFactory.generateSecret(keySpec);
			    byte[] salt = new byte[8];
			    Random rnd = new Random();
			    rnd.nextBytes(salt);
			    int iterations = 100;
			    PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
			    Cipher cipher = Cipher.getInstance(ALGORITHM);
			    cipher.init(Cipher.ENCRYPT_MODE, passwordKey, parameterSpec);
			    outputStream.write(salt);
			    byte[] input = new byte[64];
			    int bytesRead;
			    while ((bytesRead = inputStream.read(input)) != -1) {
			    	byte[] output = cipher.update(input, 0, bytesRead);
			    	if (output != null){
			    		outputStream.write(output);
			    	}
			    }
			    byte[] output = cipher.doFinal();
			    if (output != null){
			    	outputStream.write(output);
			    }
			    inputStream.close();
			    outputStream.flush();
			    outputStream.close();
			}
		}
//		file.delete();
	}
	
	public static void zipSingleFile(File zipFile, String fileName, InputStream inputStream, String base,String pwd) throws Exception 
	{
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		zipSingleFile(out, fileName, inputStream, base, pwd);
		out.flush();
		out.close();
		out = null;
	}
	/**
	 * 
	 * @param out  压缩输出流对象
	 * @param file 
	 * @param base
	 * @throws Exception
	 */
	public static void zipSingleFile(ZipOutputStream outputStream, String fileName, InputStream inputStream, String base,String pwd) throws Exception {
		base = base + (base.trim().length() == 0 ? "" : File.separator)
                + fileName;
		
			outputStream.putNextEntry(new ZipEntry(base));
//			FileInputStream inputStream = new FileInputStream(file);
			//普通压缩文件
			if(pwd == null || pwd.trim().equals("")){
//				int b;
//				while ((b = inputStream.read()) != -1){
//					outputStream.write(b);
//				}
				
	            byte buffer[] = new byte[BUFF_SIZE];
	            int realLength;
	            while ((realLength = inputStream.read(buffer)) > 0) {
	            	outputStream.write(buffer, 0, realLength);
	            }
				
				inputStream.close();
			}
			//给压缩文件加密
			else{
				PBEKeySpec keySpec = new PBEKeySpec(pwd.toCharArray());
			    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
			    SecretKey passwordKey = keyFactory.generateSecret(keySpec);
			    byte[] salt = new byte[8];
			    Random rnd = new Random();
			    rnd.nextBytes(salt);
			    int iterations = 100;
			    PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
			    Cipher cipher = Cipher.getInstance(ALGORITHM);
			    cipher.init(Cipher.ENCRYPT_MODE, passwordKey, parameterSpec);
			    outputStream.write(salt);
			    byte[] input = new byte[64];
			    int bytesRead;
			    while ((bytesRead = inputStream.read(input)) != -1) {
			    	byte[] output = cipher.update(input, 0, bytesRead);
			    	if (output != null){
			    		outputStream.write(output);
			    	}
			    }
			    byte[] output = cipher.doFinal();
			    if (output != null){
			    	outputStream.write(output);
			    }
			    inputStream.close();
			    outputStream.flush();
			    outputStream.close();
			    
			}
	}

	public static void unZip(String zipFileName, String outputDirectory)throws Exception {
		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFileName));
		unZip(inputStream, outputDirectory, null);
	}
	
	
	/**
	 * 功能描述：将压缩文件解压到指定的文件目录下
	 * @param zipFileName      压缩文件名称(带路径)
	 * @param outputDirectory  指定解压目录
	 * @return
	 * @throws Exception
	 */
	public static void unZip(String zipFileName, String outputDirectory, String pwd)throws Exception {
		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFileName));
		unZip(inputStream, outputDirectory, pwd);
	}
	
	
	public static void unZip(File zipFile, String outputDirectory, String pwd)throws Exception {
		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFile));
		unZip(inputStream, outputDirectory,pwd);
	}
	
	
	public static void unZip(ZipInputStream inputStream, String outputDirectory, String pwd) throws Exception{
		ZipEntry zipEntry = null;
		FileOutputStream outputStream = null;
		try{
			while ((zipEntry = inputStream.getNextEntry()) != null) {
				if (zipEntry.isDirectory()) {
					String name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					File file = new File(outputDirectory + File.separator + name);
					file.mkdir();
				} 
				else {
					File file = new File(outputDirectory + File.separator + zipEntry.getName());
					file.createNewFile();
					outputStream = new FileOutputStream(file);
					//普通解压缩文件
					if(pwd == null || pwd.trim().equals("")){
//						int b;
//						while ((b = inputStream.read()) != -1){
//							outputStream.write(b);
//						}
						
			            byte buffer[] = new byte[BUFF_SIZE];
			            int realLength;
			            while ((realLength = inputStream.read(buffer)) > 0) {
			            	outputStream.write(buffer, 0, realLength);
			            }
						
						outputStream.close();
					}
					//解压缩加密文件
					else{
						PBEKeySpec keySpec = new PBEKeySpec(pwd.toCharArray());
					    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
					    SecretKey passwordKey = keyFactory.generateSecret(keySpec);
					    byte[] salt = new byte[8];
					    inputStream.read(salt);
					    int iterations = 100;
					    PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
					    Cipher cipher = Cipher.getInstance(ALGORITHM);
					    cipher.init(Cipher.DECRYPT_MODE, passwordKey, parameterSpec);
					    byte[] input = new byte[64];
					    int bytesRead;
					    while ((bytesRead = inputStream.read(input)) != -1) {
					    	byte[] output = cipher.update(input, 0, bytesRead);
					    	if (output != null){
					    		outputStream.write(output);
					    	}
					    }
					    byte[] output = cipher.doFinal();
					    if (output != null){
					    	outputStream.write(output);
					    }
					    outputStream.flush();
					    outputStream.close();
					}
				}
			}
			inputStream.close();
		}
		catch(IOException ex){
			throw new Exception("解压读取文件失败");
		}
		catch(Exception ex){
			throw new Exception("解压文件密码不正确");
		}
		finally{
			inputStream.close();
			outputStream.flush();
		    outputStream.close();
		}
	}
	
	
	public static void main(String[] args) {
		try {
			long t = System.currentTimeMillis();
			//zip("D://SHYJ//Email//OUT//webapps//test.zip", "D://SHYJ//Email//OUT//webapps//export");
			
			unZip("D:/apache-tomcat-6.0.29/webapps/ROOT/tempfile/3EC914E7D4F2769B994B1E58574B8FA4460.jpg.zip", "D:/apache-tomcat-6.0.29/webapps/ROOT/tempfile");
			
			System.out.println("success : " + (System.currentTimeMillis() - t));
			
			t = System.currentTimeMillis();
			//zip("D://SHYJ//Email//OUT//webapps//test.zip", "D://SHYJ//Email//OUT//webapps//export");
			
//			zip("d:/10/pc.办公云事业部日报-黄海平-20120502.zip", "d:/10/unzip/办公云事业部日报-黄海平-20120502.doc");
			
			System.out.println("success : " + (System.currentTimeMillis() - t));
		} 
		catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	
		
}
