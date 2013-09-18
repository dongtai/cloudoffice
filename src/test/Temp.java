package test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class Temp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
			File file=new File("f:/greendb44.sql");
			FileInputStream in = new FileInputStream(file);
			byte[] b=new byte[512];
			while(in.read(b)!=-1)
			{
				baos.write(b);
			}
			baos.close();
			in.close();
			byte[] content = baos.toByteArray();
			System.out.println("=====content==="+content.length);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}

}
