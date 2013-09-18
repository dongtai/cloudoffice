package apps.transmanager.weboffice.util.server.convertforread.bean;

import java.io.File;

import apps.transmanager.weboffice.util.server.convertforread.FileUtil;
import dcs.config.Config;
import dcs.core.ConvertDecorate;

/**
 * 和servlet FileOnlindRead.交互
 * 
 * @author Administrator
 * 
 */
public class ConvertForRead {

	public static FileConvertStatus getStatusBean(String fid) {
		BeanControler beanCtrl = BeanControler.getInstance();
		FileConvertStatus fs = beanCtrl.getUploadStatus(fid);
		return fs;
	}

	public static void releaseStatusBean(FileConvertStatus fs) {
		if (fs != null)
			fs.setLocked(false);
	}

	public static void clear(String sessionid) {
		BeanControler beanCtrl = BeanControler.getInstance();
		FileConvertStatus[] fs = beanCtrl.removeUploadStatusbySid(sessionid);
		if (fs == null || fs.length == 0) {
			return;
		}
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].getFilepath() != null) {
				File filesrc = new File(fs[i].getFilepath());
				FileUtil.deleteFile(filesrc);
			}
			if (fs[i].getTargetFile() != null) {
				File filetar = new File(fs[i].getTargetFile());
				FileUtil.deleteFile(filetar);
				File filetar1 = new File(fs[i].getTargetFile() + ".files");
				FileUtil.deleteFile(filetar1);
				File filetar2 = new File(fs[i].getTargetFile() + ".html");
				FileUtil.deleteFile(filetar2);
				File filetar3 = new File(fs[i].getWebTargetFile() + ".files");
				FileUtil.deleteFile(filetar3);
				File filetar4 = new File(fs[i].getWebTargetFile() + ".html");
				FileUtil.deleteFile(filetar4);
			}
		}
	}

	public static FileConvertStatus createFileConvertStatus(String fid,
			String srcfilepath, String targetfile, String filetype) {
		BeanControler beanCtrl = BeanControler.getInstance();
		FileConvertStatus fs = null;
		if (fs == null) {
			fs = new FileConvertStatus();
			fs.setFid(fid);
			fs.setFilepath(srcfilepath);
			fs.setTargetFile(targetfile);
			fs.setFiletype(filetype);// 不加入进去
			beanCtrl.setUploadStatus(fs);
		}
		return fs;
	}

	public static int convertMStoHtml(String src, String target) {
		return ConvertDecorate.convertMStoHtml(src, target);
	}
	
	public static int convertPDFtoHtml(String src, String target) {
		return ConvertDecorate.convertPDFtoHtml(src, target);
	}

	public static int convertMStoPDF(String src, String target) {
		return ConvertDecorate.convertMStoPDF(src, target);
	}

	/**
	 * 转换所有页生成网页，并返回每页大小
	 * @param src
	 * @param tar
	 * @param fileType
	 * @param zoom
	 * @return
	 */
	public static float[][] convertMStoPic(String src, String tar,
			String fileType, float zoom) {
		return ConvertDecorate.convertMStoPic(src, tar, fileType, zoom);
	}

	/**
	 * 转换从start = 0，到end的页，并返回每页大小
	 * @param src
	 * @param tar
	 * @param end
	 * @param fileType
	 * @param zoom
	 * @return
	 */
	public static float[][] convertMStoPic(String src, String tar, int end,
			String fileType, float zoom) {
		return ConvertDecorate.convertMStoPic(src, tar, end, fileType, zoom);
	}

	
	/**
	 * 转换从start，到end的页
	 * @param src
	 * @param tar
	 * @param start
	 * @param end
	 * @param fileType
	 * @param zoom
	 */
	public static void convertMStoPic(String src, String tar, int start,
			int end, String fileType, float zoom) {
		ConvertDecorate.convertMStopic(src, tar, start, end, fileType, zoom);
	}

	public static float[][] getWhs(FileConvertStatus fs) {
		if (fs.getWhs() == null) {
			// convertMStopic(fs, 0);
			try {
				fs.lockTillChange();
				if (fs.getWhs() == null)// 首次转换
				{
					String src = fs.getFilepath();
					String targetfile = fs.getTargetFile();
					int picConvertperPages = Config.getinstance()
							.getpicConvertperPages();

					float zoom = fs.getZoom();
					if (zoom == 0) {
						zoom = Config.getinstance().getZoom();
						fs.setZoom(zoom);
					}
					String filetype = fs.getFiletype();
					if (filetype == null) {
						filetype = Config.getinstance().getPicStyle();
						fs.setFiletype(filetype);
					}
					float[][] whs = ConvertDecorate
							.convertMStopic_firstCall(src, targetfile,
									picConvertperPages, filetype, zoom);
					if (whs != null) {// 如果是float[0][0],则预览无内容
						fs.setWhs(whs);
					} else {
						// 代表失败
						fs.setWhs(null);
					}
				}
			} finally {
				fs.unlock();
			}

		}
		return fs.getWhs();
	}

	public static boolean convertMStopic(FileConvertStatus fs, int pageindex) {
		try {

			fs.lockTillChange();
			String src = fs.getFilepath();
			String targetfile = fs.getTargetFile();
			int picConvertperPages = Config.getinstance()
					.getpicConvertperPages();

			float zoom = fs.getZoom();
			if (zoom == 0) {
				zoom = Config.getinstance().getZoom();
				fs.setZoom(zoom);
			}
			String filetype = fs.getFiletype();
			if (filetype == null) {
				filetype = Config.getinstance().getPicStyle();
				fs.setFiletype(filetype);
			}

			int piccount = fs.getPagecount();
			if (piccount == 0) {
				return false;
			}
			int end = ((pageindex - 1) / picConvertperPages + 1)
					* picConvertperPages - 1;
			if (end > piccount) {
				end = piccount - 1;
			}
			int start = ((pageindex - 1) / picConvertperPages)
					* picConvertperPages;
			ConvertDecorate.convertMStopic(src, targetfile, start, end,
					filetype, zoom);
			return true;
		} finally {
			fs.unlock();
		}
	}

	public static String getSrcFold() {
		return Config.getinstance().getSrcFolder();
	}

	public static String getTargetFold() {
		return Config.getinstance().getTarFolder();
	}

}
