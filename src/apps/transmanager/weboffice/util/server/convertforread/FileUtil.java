package apps.transmanager.weboffice.util.server.convertforread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class FileUtil {
	public static String getFileEncode(String path) throws java.io.IOException {
		java.io.RandomAccessFile raf = null;
		String encode = "";
		try {
			raf = new java.io.RandomAccessFile(path, "r");
			raf.seek(0);
			int flag1 = 0;
			int flag2 = 0;
			int flag3 = 0;
			if (raf.length() >= 2) {
				flag1 = raf.readUnsignedByte();
				flag2 = raf.readUnsignedByte();
			}
			if (raf.length() >= 3) {
				flag3 = raf.readUnsignedByte();
			}
			encode = getEncode(flag1, flag2, flag3);
			if(encode == null)
			{
				encode = new FileCharsetDetector().guestFileEncoding(new File(path),
					       Integer.valueOf(3));
			}
			if(encode == null)
			{
				return "GBK";
			}
		} finally {
			if (raf != null)
				raf.close();
		}
		return encode;

	}

	private static String getEncode(int flag1, int flag2, int flag3) {
		String encode = null;
		// txt文件的开头会多出几个字节，分别是FF、FE（Unicode）,
		// FE、FF（Unicode big endian）,EF、BB、BF（UTF-8）
		if (flag1 == 255 && flag2 == 254) {
			encode = "Unicode";
		} else if (flag1 == 254 && flag2 == 255) {
			encode = "UTF-16";
		} else if (flag1 == 239 && flag2 == 187 && flag3 == 191)//UTF-8 +Bom 
			{
			encode = "UTF-8+BOM";
		}
		return encode;
	}

	public static void translateCharset(String inFilename, String outFilename,
			String inFileCharsetName, String outFileCharsetName)
			throws Exception {

		File infile = new File(inFilename);
		File outfile = new File(outFilename);

		RandomAccessFile inraf = new RandomAccessFile(infile, "r");
		RandomAccessFile outraf = new RandomAccessFile(outfile, "rw");

		FileChannel finc = inraf.getChannel();
		FileChannel foutc = outraf.getChannel();

		MappedByteBuffer inmbb = finc.map(FileChannel.MapMode.READ_ONLY, 0,
				(int) infile.length());

		Charset inCharset = Charset.forName(inFileCharsetName);
		Charset outCharset = Charset.forName(outFileCharsetName);

		CharsetDecoder inDecoder = inCharset.newDecoder();
		CharsetEncoder outEncoder = outCharset.newEncoder();

		CharBuffer cb = inDecoder.decode(inmbb);
		ByteBuffer outbb = outEncoder.encode(cb);

		foutc.write(outbb);

		clean(inmbb);

		foutc.close();
		outraf.close();
		finc.close();
		inraf.close();

	}

	/**
	 * 因为文件句柄没有被清除，还被MappedByteBuffer占据，该类是jdk的代码,jre无此代码，该bug属于sun的老bug
	 * 
	 * @param buffer
	 */
	private static void clean(final MappedByteBuffer buffer) {
		if (buffer == null) {
			return;
		}
		AccessController.doPrivileged(new PrivilegedAction<Object>() {

			public Object run() {
				try {
					Method getCleanerMethod = buffer.getClass().getMethod(
							"cleaner", new Class[0]);
					if (getCleanerMethod != null) {
						getCleanerMethod.setAccessible(true);
						Object cleaner = getCleanerMethod.invoke(buffer,
								new Object[0]);
						Method cleanMethod = cleaner.getClass().getMethod(
								"clean", new Class[0]);
						if (cleanMethod != null) {
							cleanMethod.invoke(cleaner, new Object[0]);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	/**
	 * 删除文件夹
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		} else {
			// System.out.println("所删除的文件不存在！");
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	public static void UTF8BOMTOUTF8(File sourceFile, File targetFile)
			throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);
		
		inBuff.read();
		inBuff.read();
		inBuff.read();
		
		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		// 关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}
	/**
	 * 复制文件夹
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @throws IOException
	 */
	public static void copyDirectiory(String sourceDir, String targetDir)
			throws IOException {
		File srcFold = new File(sourceDir);
		if(!(srcFold).exists())
		{
			return;
		}
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = srcFold.listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(
						new File(targetDir).getAbsolutePath() + File.separator
								+ file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}
}
