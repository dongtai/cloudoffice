package dcs.core;

import java.rmi.RemoteException;

import dcs.client.ConvertRmiClient;
import dcs.config.Config;
import dcs.core.ConvertorPool.ConvertorObject;
import dcs.interfaces.IPICConvertor;

/**
 * 两种方式，rmi，直接嵌入，统一接口：与ConvertForRead交互
 * 
 * @author Administrator
 * 
 */

public class ConvertDecorate {

	public static int convertMStoPDF(String src, String target) {
		if (Config.getinstance().isRMI()) {
			try {
				return ConvertRmiClient.getConvertInstance().convertMStoPDF(
						src, target);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;
		}
		return ConvertUtil.convertMStoPDF(src, target);
	}

	public static int convertMStoHtml(String src, String target) {
		if (Config.getinstance().isRMI()) {
			try {
				return ConvertRmiClient.getConvertInstance().convertMStoHTML(
						src, target);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;
		}
		return ConvertUtil.convertMStoHtml(src, target);
	}
	
	public static int convertPDFtoHtml(String src, String target) {
		if (Config.getinstance().isRMI()) {
			try {
				return ConvertRmiClient.getConvertInstance().convertPDFtoHTML(
						src, target);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;
		}
		return ConvertUtil.convertMStoHtml(src, target);
	}

	/**
	 * 转换所有页生成网页，并返回每页大小
	 * 
	 * @param src//文档
	 * @param tar//目标文件夹
	 * @param fileType//gif,png,jpg
	 * @param zoom//1.0f,0.5f
	 * @return //这篇每页图片宽高。
	 */
	public static float[][] convertMStoPic(String src, String tar, int end,
			String fileType, float zoom) {
		return convertMStopic_firstCall(src, tar, end, fileType, zoom);
	}

	/**
	 * 转换所有页生成网页，并返回每页大小
	 * 
	 * @param src
	 * @param tar
	 * @param fileType
	 * @param zoom
	 * @return
	 */
	public static float[][] convertMStoPic(String src, String tar,
			String fileType, float zoom) {
		return convertMStopic_firstCall(src, tar, -1, fileType, zoom);
	}

	/**
	 * 转换从start，到end的页
	 * 
	 * @param src
	 * @param tar
	 * @param start
	 * @param end
	 * @param fileType
	 * @param zoom
	 */
	public static void convertMStopic(String src, String tar, int start,
			int end, String fileType, float zoom) {
		if (Config.getinstance().isRMI()) {
			try {
				convertMStopicForRMI(src, tar, start, end, fileType, zoom);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			convertMStopicNoRMI(src, tar, start, end, fileType, zoom);
		}
	}

	/**
	 * 获取每页的大小
	 * @param src
	 * @return
	 * @throws RemoteException 
	 */
	public static float[][] getAllpageWHeigths(String src) throws RemoteException {
		float[][] picwidthheigths = null;
		if (Config.getinstance().isRMI()) {
			IPICConvertor pic = null;
			try {
				pic = ConvertRmiClient.getConvertInstance().convertMStoPic(src);
				int result = pic.resultCode();
				if (result == 0) {
					picwidthheigths = pic.getAllpageWHeigths();
				}
			}  finally {
				pic.close();
			}
		} else {
			Object[] objs = ConvertUtil.convertMStoPic(src);
			Object pic = objs[0];
			Object con = objs[1];
			try {
				int result = ConvertUtil.resultCode(pic);
				if (result == 0) {
					picwidthheigths = ConvertUtil.getAllpageWHeigths(pic,
							(ConvertorObject) con);
				}
			} finally {
				ConvertUtil.close(pic, con);
			}
		}
		return picwidthheigths;
	}

	public static float[][] convertMStopic_firstCall(String src, String tar,
			int end, String fileType, float zoom) {
		if (Config.getinstance().isRMI()) {
			try {
				return convertMStopic_firstCallForRMI(src, tar, end, fileType,
						zoom);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		return convertMStopic_firstCallNoRMI(src, tar, end, fileType, zoom);
	}

	private static float[][] convertMStopic_firstCallForRMI(String src,
			String tar, int end, String fileType, float zoom)
			throws RemoteException {

		IPICConvertor pic = ConvertRmiClient.getConvertInstance()
				.convertMStoPic(src);
		float[][] picwidthheigths = null;
		try {
			int result = pic.resultCode();
			if (result == 0) {
				picwidthheigths = pic.getAllpageWHeigths();
				if (picwidthheigths == null) {
					return null;// 返回null则表示文档转换失败
				}
				if (picwidthheigths.length == 0) {
					return picwidthheigths;// 返回null则表示文档空白无内容预览
				}
				if (zoom != 1.0f) {
					for (int i = 0; i < picwidthheigths.length; i++) {
						for (int j = 0; j < picwidthheigths[i].length; j++) {
							picwidthheigths[i][j] = picwidthheigths[i][j]
									* zoom;
						}
					}
				}
				int start = 0;
				if (picwidthheigths.length - end > 0) {
					end = end - 1 > 0 ? end - 1 : -1;
				}
				if ("jpg".equalsIgnoreCase(fileType)) {
					pic.convertToJPG(start, end, zoom, tar);
				} else if ("png".equalsIgnoreCase(fileType)) {
					pic.convertToPNG(start, end, zoom, tar);
				} else if ("gif".equalsIgnoreCase(fileType)) {
					pic.convertToGIF(start, end, zoom, tar);
				}
			}
		} finally {
			pic.close();
		}
		return picwidthheigths;
	}

	private static float[][] convertMStopic_firstCallNoRMI(String src,
			String tar, int end, String fileType, float zoom) {
		Object[] objs = ConvertUtil.convertMStoPic(src);
		Object pic = objs[0];
		Object con = objs[1];
		float[][] picwidthheigths = null;
		try {
			int result = ConvertUtil.resultCode(pic);
			if (result == 0) {
				picwidthheigths = ConvertUtil.getAllpageWHeigths(pic,
						(ConvertorObject) con);
				if (picwidthheigths == null) {
					return null;// 返回null则表示文档转换失败
				}
				if (picwidthheigths.length == 0) {
					return picwidthheigths;// 返回null则表示文档空白无内容预览
				}
				if (zoom != 1.0f) {
					for (int i = 0; i < picwidthheigths.length; i++) {
						for (int j = 0; j < picwidthheigths[i].length; j++) {
							picwidthheigths[i][j] = picwidthheigths[i][j]
									* zoom;
						}
					}
				}
				int start = 0;
				if (picwidthheigths.length - end > 0) {
					end = end - 1 > 0 ? end - 1 : -1;
				}
				if ("jpg".equalsIgnoreCase(fileType)) {
					ConvertUtil.convertToJPG(pic, start, end, zoom, tar,
							(ConvertorObject) con);
				} else if ("png".equalsIgnoreCase(fileType)) {
					ConvertUtil.convertToPNG(pic, start, end, zoom, tar,
							(ConvertorObject) con);
				} else if ("gif".equalsIgnoreCase(fileType)) {
					ConvertUtil.convertToGIF(pic, start, end, zoom, tar,
							(ConvertorObject) con);
				}
			}
		} finally {
			ConvertUtil.close(pic, con);
		}
		return picwidthheigths;
	}

	private static void convertMStopicForRMI(String src, String tar, int start,
			int end, String fileType, float zoom) throws RemoteException {
		IPICConvertor pic = ConvertRmiClient.getConvertInstance()
				.convertMStoPic(src);
		try {
			int result = pic.resultCode();
			if (result == 0) {
				if ("jpg".equalsIgnoreCase(fileType)) {
					pic.convertToJPG(start, end, zoom, tar);
				} else if ("png".equalsIgnoreCase(fileType)) {
					pic.convertToPNG(start, end, zoom, tar);
				} else if ("gif".equalsIgnoreCase(fileType)) {
					pic.convertToGIF(start, end, zoom, tar);
				}
			}
		} finally {
			pic.close();
		}
	}

	private static void convertMStopicNoRMI(String src, String tar, int start,
			int end, String fileType, float zoom) {
		Object[] objs = ConvertUtil.convertMStoPic(src);
		Object pic = objs[0];
		Object con = objs[1];
		try {
			int result = ConvertUtil.resultCode(pic);
			if (result == 0) {
				if ("jpg".equalsIgnoreCase(fileType)) {
					ConvertUtil.convertToJPG(pic, start, end, zoom, tar,
							(ConvertorObject) con);
				} else if ("png".equalsIgnoreCase(fileType)) {
					ConvertUtil.convertToPNG(pic, start, end, zoom, tar,
							(ConvertorObject) con);
				} else if ("gif".equalsIgnoreCase(fileType)) {
					ConvertUtil.convertToGIF(pic, start, end, zoom, tar,
							(ConvertorObject) con);
				}
			}
		} finally {
			ConvertUtil.close(pic, con);
		}
	}
	
	/**
	 * 转换从start，到end的页
	 * 
	 * @param src
	 * @param tar
	 * @param start
	 * @param end
	 * @param fileType
	 * @param zoom
	 */
	public static void convert(String src, String tar, int start,
			int end, String fileType, float zoom, String suffix) {
		if (Config.getinstance().isRMI()) {
			try {
				convertForRMI(src, tar, start, end, fileType, zoom, suffix);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			converttopicNoRMI(src, tar, start, end, fileType, zoom, suffix);
		}
	}
	
	private static void convertForRMI(String src, String tar, int start,
			int end, String fileType, float zoom, String suffix) throws RemoteException {
				IPICConvertor pic = suffix.equalsIgnoreCase("pdf")
				? ConvertRmiClient.getConvertInstance().convertPDFtoPic(src) : ConvertRmiClient.getConvertInstance().convertMStoPic(src);
		try {
			int result = pic.resultCode();
			if (result == 0) {
				if ("jpg".equalsIgnoreCase(fileType)) {
					pic.convertToJPG(start, end, zoom, tar);
				} else if ("png".equalsIgnoreCase(fileType)) {
					pic.convertToPNG(start, end, zoom, tar);
				} else if ("gif".equalsIgnoreCase(fileType)) {
					pic.convertToGIF(start, end, zoom, tar);
				}
			}
		} finally {
			pic.close();
		}
	}
	
	private static void converttopicNoRMI(String src, String tar, int start,
			int end, String fileType, float zoom, String suffix) {
				Object[] objs = suffix.equalsIgnoreCase("pdf") ? ConvertUtil.convertPDFtoPic(src) : ConvertUtil.convertMStoPic(src);
		Object pic = objs[0];
		Object con = objs[1];
		try {
			int result = ConvertUtil.resultCode(pic);
			if (result == 0) {
				if ("jpg".equalsIgnoreCase(fileType)) {
					ConvertUtil.convertToJPG(pic, start, end, zoom, tar,
							(ConvertorObject) con);
				} else if ("png".equalsIgnoreCase(fileType)) {
					ConvertUtil.convertToPNG(pic, start, end, zoom, tar,
							(ConvertorObject) con);
				} else if ("gif".equalsIgnoreCase(fileType)) {
					ConvertUtil.convertToGIF(pic, start, end, zoom, tar,
							(ConvertorObject) con);
				}
			}
		} finally {
			ConvertUtil.close(pic, con);
		}
	}
	
}
