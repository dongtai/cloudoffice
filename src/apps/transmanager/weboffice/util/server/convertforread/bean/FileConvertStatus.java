package apps.transmanager.weboffice.util.server.convertforread.bean;

public class FileConvertStatus {
	private final Object lock = new Object();

	private volatile boolean flag;

	public void lockTillChange() {
		synchronized (lock) {
			while (flag) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
				}
			}
			flag = true;
		}

	}

	public void unlock() {
		synchronized (lock) {
			flag = false;
			lock.notifyAll();
		}
	}

	private String sessionid;
	
	
	
	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	private String fid;

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	private String filepath;

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	private String webTargetFile;
	
	
	public String getWebTargetFile() {
		return webTargetFile;
	}

	public void setWebTargetFile(String webTargetFile) {
		this.webTargetFile = webTargetFile;
	}

	private String targetFile;
	
	
	public String getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}

	private float[][] whs;

	public float[][] getWhs() {
		return whs;
	}

	public void setWhs(float[][] whs) {
		this.whs = whs;
	}
	private int pagecount;
	
	public int getPagecount() {
		if (whs != null)
			return whs.length;
		return pagecount;
	}

	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}

	private String filetype;

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
	
	private float zoom;
	
	
	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	private boolean locked;//每次get的时候，锁住。

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
}
