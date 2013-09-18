package apps.transmanager.weboffice.service.mail.receive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.service.mail.StringUtil;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;

public class PraseMimeMessage {
	private MimeMessage mimeMessage = null;
	private Folder folder;

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	private String saveAttachPath = ""; // 附件下载后的存放目录
	private StringBuffer bodytext = new StringBuffer(); // 存放邮件内容的StringBuffer对象
	private String dateformat = "yy-MM-dd HH:mm"; // 默认的日前显示格式

	/**
	 * 构造函数,初始化一个MimeMessage对象
	 */
	public PraseMimeMessage() {
	}

	public PraseMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
		// System.out.println("create a PraseMimeMessage object........");
	}

	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	/**
	 * 获得发件人的地址和姓名 InternetAddress对象中包括发件人的邮箱地址和发件人的名字
	 * InternetAddress对象有设定发件人名字的setPersonal方法 在发邮件的时候最好要把getPersonal对象构造完全
	 */
	public String getFrom() throws Exception {
		InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
		String from = address[0].getAddress();
		if (from == null)
			from = "";
		String personal = address[0].getPersonal();
		if (personal == null)
			personal = "";
		String fromaddr = personal + "<" + from + ">";
		return fromaddr;
	}

	/**
	 * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
	 */
	public String getMailAddress(String type) throws Exception {
		String mailaddr = "";
		String addtype = type.toUpperCase();
		InternetAddress[] address = null;
		if (addtype.equals("TO") || addtype.equals("CC")
				|| addtype.equals("BCC")) {
			if (addtype.equals("TO")) {
				address = (InternetAddress[]) mimeMessage
						.getRecipients(Message.RecipientType.TO);
			} else if (addtype.equals("CC")) {
				address = (InternetAddress[]) mimeMessage
						.getRecipients(Message.RecipientType.CC);
			} else {
				address = (InternetAddress[]) mimeMessage
						.getRecipients(Message.RecipientType.BCC);
			}
			if (address != null && address.length !=0) {
				for (int i = 0; i < address.length; i++) {
					String email = address[i].getAddress();
					if (email == null)
						email = "";
					else {
						email = MimeUtility.decodeText(email);
					}
					String personal = address[i].getPersonal();
					if (personal == null)
						personal = "";
					else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + email + ">";
					mailaddr += "," + compositeto;
				}
				mailaddr = mailaddr.substring(1);
			}
		} else {
			throw new Exception("Error emailaddr type!");
		}
//		System.out.println(mailaddr);
		return mailaddr;
	}

	/**
	 * 获得邮件主题
	 */
	public String getSubject() throws MessagingException {
		String subject = "";
		try {
			subject = MimeUtility.decodeText(mimeMessage.getSubject());
			if (subject == null)
				subject = "";
		} catch (Exception exce) {
		}
		return subject;
	}

	/**
	 * 获得邮件发送日期
	 */
	public String getSentDate() throws Exception {
		Date sentdate = mimeMessage.getSentDate();
		SimpleDateFormat format = new SimpleDateFormat(dateformat);
		return format.format(sentdate);
	}

	public Date getMsgSentDate() throws Exception {
		Date sentdate = mimeMessage.getSentDate();
		return sentdate;
	}

	public Date getMsgReceivedDate() throws Exception {
		Date sentdate = mimeMessage.getReceivedDate();
		return sentdate;
	}

	public int getSize() throws Exception {
		return mimeMessage.getSize();
	}

	/**
	 * 获得邮件正文内容
	 */
	public String getBodyText() throws Exception {
		getMailContent(this.mimeMessage);
		return bodytext.toString();
	}

	public String getBodyText(boolean contohtml) throws Exception {
		getMailContent(this.mimeMessage, contohtml);
		return bodytext.toString();
	}

	/*
	 * private int style; public int getBodyTextStyle() { return style; } public
	 * static final int plain = 1; public static final int html = 2;
	 */
	/**
	 * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
	 */
	public void getMailContent(Part part) throws Exception {
		getMailContent(part, false);
	}

	public void getMailContent(Part part, boolean contohtml) throws Exception {
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;
		if (nameindex != -1)
			conname = true;

//		System.out.println("CONTENTTYPE: " + contenttype);
		if (part.isMimeType("text/plain") && !conname) {
			if (contohtml) {
				bodytext.append(StringUtil.htmltextencoder((String) part
						.getContent()));
			} else {
				bodytext.append(((String) part.getContent()));
			}
			//
		} else if (part.isMimeType("text/html") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("multipart/alternative")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				if (i != counts - 1) {
					continue;
				}
				getMailContent(multipart.getBodyPart(i), contohtml);
			}
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				getMailContent(multipart.getBodyPart(i), contohtml);
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent(), contohtml);
		} else {
		}
	}

	/**
	 * 判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"
	 */
	public boolean getReplySign() throws MessagingException {
		boolean replysign = false;
		String needreply[] = mimeMessage
				.getHeader("Disposition-Notification-To");
		if (needreply != null) {
			replysign = true;
		}
		return replysign;
	}

	/**
	 * 获得此邮件的Message-ID
	 */
	public String getMessageId() throws MessagingException {
		return mimeMessage.getMessageID();
	}

	public String getMessageUId() throws MessagingException {

		if (folder == null) {
			return null;
		}
		return ((POP3Folder) folder).getUID(mimeMessage);
		// return mimeMessage.getMessageID();
	}

	/**
	 * 【判断此邮件是否已读，如果未读返回返回false,反之返回true】
	 */
	public boolean isNew() throws MessagingException {
		boolean isnew = false;
		Flags flags = ((Message) mimeMessage).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		// System.out.println("flags's length: " + flag.length);
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isnew = true;
//				System.out.println("seen Message.......");
				break;
			}
		}
		return isnew;
	}

	public boolean isContainAttach() throws Exception {
		return isContainAttach(this.mimeMessage);
	}

	public String getimportance() throws Exception {
		String[] hdrs = mimeMessage.getHeader("Importance");
		StringBuffer result = new StringBuffer();
		for (int i = 0; hdrs != null && i < hdrs.length; i++) {
			result.append(hdrs[i]);
		}
		return result.toString();
	}

	/**
	 * 判断此邮件是否包含附件
	 */
	public static boolean isContainAttach(Part part) throws Exception {
		boolean attachflag = false;
		// String contentType = part.getContentType();
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {// getcount方法慢
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition
								.equals(Part.INLINE)))) {
					attachflag = true;
					break;
				}

				else if (mpart.isMimeType("multipart/*")) {
					attachflag = isContainAttach((Part) mpart);
					if (attachflag) {
						break;
					}
				} else {
					String contype = mpart.getContentType();
					if (contype.toLowerCase().indexOf("application") != -1) {
						attachflag = true;
						break;
					}
					if (contype.toLowerCase().indexOf("name") != -1) {
						attachflag = true;
						break;
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			attachflag = isContainAttach((Part) part.getContent());
		}
		return attachflag;
	}

	public ArrayList<HashMap<Object, Object>> getContainAttach()
			throws Exception {
		ArrayList<HashMap<Object, Object>> list = getContainAttach(mimeMessage,
				null);
		if (list.size() != 0) {
			return list;
		}
		return null;
	}

	private ArrayList<HashMap<Object, Object>> getContainAttach(Part part,
			ArrayList<HashMap<Object, Object>> list) throws Exception {
		if (list == null)
			list = new ArrayList<HashMap<Object, Object>>();
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition
								.equals(Part.INLINE)))) {
					String fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = toChineseFileName(fileName);
//						System.out.println(fileName);
						// System.out.println(getContenttypeAndCharset("Content-Type",
						// mpart.getContentType()));
						Properties p = getContenttypeAndCharset("Content-Type",
								mpart.getContentType());
						String ct = p.getProperty("Content-Type");
						String cs = p.getProperty("charset");
						// mpart.getHeader("charset");
						HashMap<Object, Object> hm = new HashMap<Object, Object>();
						hm.put("filename", fileName);
						hm.put("partno", list.size());
						if (ct != null)
							hm.put("ctnttype", ct);
						if (cs != null)
							hm.put("charset", cs);
						InputStream is = ((Part) mpart).getInputStream();
						int size = is.available();;
						is.close();
						
						String str = null;
						java.text.DecimalFormat df = new java.text.DecimalFormat(
								"#.##");
						if (size / (1024.0f * 1024) > 1) {
							str = df.format(size / (1024.0f * 1024)) + "MB";
						} else {
							str = df.format(size / (1024.0f)) + "KB";
						}
						hm.put("size", str);
						/*
						 * fileName = "<a>" + fileName + "?partno=" +
						 * list.size() +"&ctnttype="+ct+"&charset="+cs +"</a>" +
						 * mpart.getSize();
						 */
						list.add(hm);
					}
				} else if (mpart.isMimeType("multipart/*")) {
					getContainAttach((Part) mpart, list);
				} else {
					String contype = mpart.getContentType();
					if (contype.toLowerCase().indexOf("application") != -1
							|| contype.toLowerCase().indexOf("name") != -1) {
						String fileName = mpart.getFileName();
						if (fileName != null) {
							fileName = toChineseFileName(fileName);
//							System.out.println(fileName);
							Properties p = getContenttypeAndCharset(
									"Content-Type", mpart.getContentType());
							String ct = p.getProperty("Content-Type");
							String cs = p.getProperty("charset");
							HashMap<Object, Object> hm = new HashMap<Object, Object>();
							hm.put("filename", fileName);
							hm.put("partno", list.size());
							if (ct != null)
								hm.put("ctnttype", ct);
							if (cs != null)
								hm.put("charset", cs);
							InputStream is = ((Part) mpart).getInputStream();
							int size = is.available();;
							is.close();
							String str = null;
							java.text.DecimalFormat df = new java.text.DecimalFormat(
									"#.##");
							if (size / (1024.0f * 1024) > 1) {
								str = df.format(size / (1024.0f * 1024)) + "MB";
							} else {
								str = df.format(size / (1024.0f)) + "KB";
							}
							hm.put("size", str);
							/*
							 * fileName = "<a>" + fileName + "?partno=" +
							 * list.size() +"&ctnttype="+ct+"&charset="+cs
							 * +"</a>" + mpart.getSize();
							 */
							list.add(hm);
						}
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			getContainAttach((Part) part.getContent(), list);
		}
		return list;
	}

	public Properties getContenttypeAndCharset(String hdr, String str) {
		boolean basicValueSet = false;
		Properties props = new Properties();
		int colonIdx = str.indexOf(':');
		for (StringTokenizer st = new StringTokenizer(
				str.substring(colonIdx + 1), ";"); st.hasMoreTokens();) {
			String avp = st.nextToken().trim();
			int equalIdx = avp.indexOf('=');
			if (equalIdx == -1) {
				if (!avp.equals("")) {
					if (!basicValueSet) {
						props.put(hdr, avp);
						basicValueSet = true;
					} else {
						props.put(avp.toLowerCase(), avp.toLowerCase());
					}
				}
			} else {
				String attr = avp.substring(0, equalIdx).trim().toLowerCase();
				String val = avp.substring(equalIdx + 1).trim();
				val = val.replaceFirst("^\"", "").replaceFirst("\"$", "");
				props.put(attr, val);
			}

		}
		return props;
	}

	public String saveAttachMent(Part part, final int index,
			ArrayList<String> list, String filePath) throws Exception {
		String fileName = "";
		if (list == null)
			list = new ArrayList<String>();
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition
								.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = toChineseFileName(fileName);
						if (list.size() == index) {

							if (filePath != null) {
								saveAttach(mpart, filePath
										+ File.separatorChar + fileName);
							}
							return filePath + File.separatorChar + fileName;
						}
						list.add(fileName);
					}
				} else if (mpart.isMimeType("multipart/*")) {
					String str = saveAttachMent(mpart, index, list, filePath);
					if(str != null)
					{
						return str;
					}
				} else {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = toChineseFileName(fileName);
						if (list.size() == index) {

							if (filePath != null) {
								saveAttach(mpart, filePath
										+ File.separatorChar + fileName);
							}
							return filePath + File.separatorChar + fileName;
						}
						list.add(fileName);
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			String str =  saveAttachMent((Part) part.getContent(), index, list, filePath);
			if(str != null)
			{
				return str;
			}
		}
		return null;
	}

	/**
	 * 【保存附件】
	 */
	public String saveAttachMent(HttpServletResponse response, Part part,
			PrintWriter out, final int index, ArrayList<String> list)
			throws Exception {
		String fileName = "";
		if (list == null)
			list = new ArrayList<String>();
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition
								.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();

					if (fileName != null) {
						fileName = toChineseFileName(fileName);
						if (list.size() == index) {
							response.setHeader(
									"Content-Disposition",
									"attachment; filename="
											+ new String(fileName
													.getBytes("gbk"),
													"iso-8859-1"));
							// response.setContentLength(mpart.getSize());
//							System.out.println(mpart.getSize());
							saveAttach(mpart, out);
							return fileName;
						}
						list.add(fileName);
					}
				} else if (mpart.isMimeType("multipart/*")) {
					String str = saveAttachMent(response, mpart, out, index, list);
					if(str != null)
					{
						return str;
					}
				} else {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = toChineseFileName(fileName);
						if (list.size() == index) {
							response.setHeader(
									"Content-Disposition",
									"attachment; filename="
											+ new String(fileName
													.getBytes("gbk"),
													"iso-8859-1"));
							// response.setContentLength(mpart.getSize());
							System.out.println(mpart.getSize());
							saveAttach(mpart, out);
							return fileName;
						}
						list.add(fileName);
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			String str =  saveAttachMent(response, (Part) part.getContent(), out, index, list);
			if(str != null)
			{
				return str;
			}
		}
		return null;
	}

	public Object[] saveAttachMentToWebDoc(Part part,
			final int index, ArrayList<String> list)
			throws Exception {
		String fileName = "";
		if (list == null)
			list = new ArrayList<String>();
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition
								.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();

					if (fileName != null) {
						fileName = toChineseFileName(fileName);
						if (list.size() == index) {
							return new Object[]{mpart.getInputStream(),fileName};
						}
						list.add(fileName);
					}
				} else if (mpart.isMimeType("multipart/*")) {
					Object[] objs =  saveAttachMentToWebDoc(mpart, index, list);
					if(objs  != null)
					{
						return objs;
					}
				} else {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = toChineseFileName(fileName);
						if (list.size() == index) {
							return new Object[]{mpart.getInputStream(),fileName};
						}
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			Object[] objs =  saveAttachMentToWebDoc((Part) part.getContent(), index, list);
			if(objs  != null)
			{
				return objs;
			}
		}
		return null;
	}
	
	private void saveAttach(Part messagePart, Writer writer)
			throws IOException, MessagingException {
		InputStream is = ((Part) messagePart).getInputStream();
		int c;
		
		while ((c = is.read()) != -1)
			writer.write(c);
		writer.flush();
		is.close();

		/*
		 * File fle = new File("c:/1.jpg"); FileOutputStream out = new
		 * FileOutputStream(fle); while ((c = is.read()) != -1) out.write(c);
		 * out.flush(); is.close(); out.close();
		 */

	}

	private void saveAttach(Part messagePart, String filename)
			throws IOException, MessagingException {
		InputStream is = ((Part) messagePart).getInputStream();
//		int c;
		if (filename != null) {
			File fle = new File(filename);
			FileOutputStream out = new FileOutputStream(fle);
			byte[] b = new byte[8 * 1024];
			int len = 0;
			while ((len = is.read(b)) > 0) {
				out.write(b, 0, len);
			}
			out.flush();
			is.close();
			out.close();
		}
	}

	/**
	 * 【保存附件】
	 */
	public void saveAttachMent(Part part) throws Exception {
		String fileName = "";
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition
								.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = toChineseFileName(fileName);
						saveFile(fileName, mpart.getInputStream());
					}
				} else if (mpart.isMimeType("multipart/*")) {
					saveAttachMent(mpart);
				} else {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = toChineseFileName(fileName);
						saveFile(fileName, mpart.getInputStream());
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachMent((Part) part.getContent());
		}
	}

	public String toChineseFileName(String fileName)
			throws UnsupportedEncodingException {
		String fileNameLowerCase = fileName.toLowerCase();
		if (fileNameLowerCase.startsWith("=?gbk?b?") && fileName.endsWith("?=")) {
			fileName = MimeUtility.decodeText(fileName);
		} else if (fileNameLowerCase.startsWith("=?gb2312?b?")
				&& fileName.endsWith("?=")) {
			fileName = MimeUtility.decodeText(fileName);
		} else if (fileNameLowerCase.startsWith("=?utf-8?b?")
				&& fileName.endsWith("?=")) {
			fileName = MimeUtility.decodeText(fileName);
		} else if (fileNameLowerCase.startsWith("=?")
				&& fileName.endsWith("?=")) {
			fileName = MimeUtility.decodeText(fileName);
		} else {
			fileName = toChinese(fileName);//
		}
		return fileName;
	}

	public String toChinese(String strvalue) {
		try {
			if (strvalue == null)
				return null;
			else {
				strvalue = new String(strvalue.getBytes("ISO8859_1"), "GBK");
				return strvalue;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 【设置附件存放路径】
	 */
	public void setAttachPath(String attachpath) {
		this.saveAttachPath = attachpath;
	}

	/**
	 * 【设置日期显示格式】
	 */
	public void setDateFormat(String format) throws Exception {
		this.dateformat = format;
	}

	/**
	 * 【获得附件存放路径】
	 */
	public String getAttachPath() {
		return saveAttachPath;
	}

	/**
	 * 【真正的保存附件到指定目录里】
	 */
	private void saveFile(String fileName, InputStream in) throws Exception {
		String osName = System.getProperty("os.name");
		String storedir = getAttachPath();
		String separator = "";
		if (osName == null)
			osName = "";
		if (osName.toLowerCase().indexOf("win") != -1) {
			separator = "\\";
			if (storedir == null || storedir.equals(""))
				storedir = "c:\\tmp";
		} else {
			separator = "/";
			storedir = "/tmp";
		}
		File storefile = new File(storedir + separator + fileName);
//		System.out.println("storefile's path: " + storefile.toString());
		// for(int i=0;storefile.exists();i++){
		// storefile = new File(storedir+separator+fileName+i);
		// }
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(storefile));
			bis = new BufferedInputStream(in);
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("文件保存失败!");
		} finally {
			bos.close();
			bis.close();
		}
	}

	public static String saveMessageAsFile(Message message,String path) {
        try {
        	File dpath = new File(path);
        	if(!dpath.exists())
        	dpath.mkdirs();
            String fileNameWidthExtension = System.currentTimeMillis() + ".eml";
            BufferedOutputStream bos = null;
            bos = new BufferedOutputStream(new FileOutputStream(path + File.separatorChar+fileNameWidthExtension));
            message.writeTo(bos);
            bos.flush();
            bos.close();
            return fileNameWidthExtension;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	
	/**
	 * PraseMimeMessage类测试
	 */
	public static void main1(String args[]) throws Exception {
		String host = "mail.yozosoft.com"; // 【pop.mail.yahoo.com.cn】
		String username = "zy"; // 【wwp_1124】
		String password = "123456"; // 【........】

		Properties props = new Properties();
		Session session = Session.getInstance(props, null);
		Store store = session.getStore("pop3");
		store.connect(host, username, password);

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message message[] = folder.getMessages();
//		System.out.println("Messages's length: " + message.length);
		PraseMimeMessage pmm = null;
		for (int i = message.length - 1; i >= 0; i--) {
			pmm = new PraseMimeMessage((MimeMessage) message[i]);
			/*
			 * System.out.println("Message "+i+" subject: "+pmm.getSubject());
			 * System.out.println("Message "+i+" sentdate: "+pmm.getSentDate());
			 * System
			 * .out.println("Message "+i+" replysign: "+pmm.getReplySign());
			 * System.out.println("Message "+i+" hasRead: "+pmm.isNew());
			 * System.
			 * out.println("Message "+i+" containAttachment: "+pmm.isContainAttach
			 * ((Part)message[i]));
			 * System.out.println("Message "+i+" form: "+pmm.getFrom());
			 * System.out
			 * .println("Message "+i+" to: "+pmm.getMailAddress("to"));
			 * System.out
			 * .println("Message "+i+" cc: "+pmm.getMailAddress("cc"));
			 * System.out
			 * .println("Message "+i+" bcc: "+pmm.getMailAddress("bcc"));
			 * pmm.setDateFormat("yy年MM月dd日 HH:mm");
			 * System.out.println("Message "+i+" sentdate: "+pmm.getSentDate());
			 * System
			 * .out.println("Message "+i+" Message-ID: "+pmm.getMessageId());
			 * pmm.getMailContent((Part)message[i]);
			 * System.out.println("Message "
			 * +i+" bodycontent: \r\n"+pmm.getBodyText());
			 * pmm.setAttachPath("c:\\tmp\\coffeecat1124");
			 */
			// pmm.saveAttachMent((Part)message[i]);
			Object list = pmm.getContainAttach((Part) message[i], null);

			if (list != null) {
				System.out.println("Message " + i + " sentdate: "
						+ pmm.getSentDate());
				System.out.println("Message " + i + " subject: "
						+ pmm.getSubject());
				System.out.println("Message " + i + " attr: "
						+ pmm.getContainAttach());
			}
		}
		try {
			if (folder != null)
				folder.close(false);
			if (store != null)
				store.close();

		} catch (Exception ex2) {
			ex2.printStackTrace();
		}
	}
	
	/**
	 * imap邮箱
	 * @param args
	 */
	public static void main(String[] args) {
        String imapserver = "imap.163.com"; // 邮件服务器
        String user = "你的用户名@163.com";//根据自已的用户名修改
        String pwd = "你的密码";     // 根据自已的密码修改
            // 获取默认会话
            Properties prop = System.getProperties();
            prop.put("mail.imap.host",imapserver);
           
            prop.put("mail.imap.auth.plain.disable","true");
            Session mailsession=Session.getInstance(prop,null);
            mailsession.setDebug(false); //是否启用debug模式
            IMAPFolder folder= null;
            IMAPStore store=null;
            int total= 0;
            try{
               store=(IMAPStore)mailsession.getStore("imap");  // 使用imap会话机制，连接服务器
               store.connect(imapserver,user,pwd);
               folder=(IMAPFolder)store.getFolder("INBOX"); //收件箱 
               // 使用只读方式打开收件箱 
               folder.open(Folder.READ_WRITE);
               //获取总邮件数
               total = folder.getMessageCount();
               System.out.println("-----------------您的邮箱共有邮件：" + total+" 封--------------");
               // 得到收件箱文件夹信息，获取邮件列表
         Message[] msgs =folder.getMessages();
               System.out.println("\t收件箱的总邮件数：" + msgs.length);  
               System.out.println("\t未读邮件数：" + folder.getUnreadMessageCount());  
               System.out.println("\t新邮件数：" + folder.getNewMessageCount());  
               System.out.println("----------------End------------------");
            }
            catch(MessagingException ex){
                 System.err.println("不能以读写方式打开邮箱!");
                 ex.printStackTrace();
            }finally {
       // 释放资源
              try{
                  if(folder!=null)
                      folder.close(true); //退出收件箱时,删除做了删除标识的邮件
                 if (store != null)
                    store.close();
                }catch(Exception bs){
                 bs.printStackTrace();
                }             
            }          
    }


}
