package apps.transmanager.weboffice.util.server.convertforread.bean;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BeanControler {
	private Lock lock = new ReentrantLock();

	private static BeanControler beanControler = new BeanControler();
	private Vector<FileConvertStatus> vector = new Vector<FileConvertStatus>();

	public Vector<FileConvertStatus> getVector() {
		return vector;
	}

//	private Timer timer = new Timer("BeanControler");

	private BeanControler() {
		//timer.schedule(new OnlineTimerTask(), 0, 600000);
	}
	
	/*private class OnlineTimerTask extends TimerTask
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			BeanControler.getInstance().clearTimeout();
		}
		
	}*/

	public static BeanControler getInstance() {
		return beanControler;
	}

	/**
	 * 取得相应FileUploadStatus类对象的存储位置
	 */
	private int indexOf(String fid) {
		if(fid == null)
		{
			return -1;
		}
		int nReturn = -1;
		for (int i = 0; i < vector.size(); i++) {
			FileConvertStatus status = vector.elementAt(i);
			if (status.getFid().equals(fid)) {
				nReturn = i;
				break;
			}
		}
		return nReturn;
	}

	/**
	 * 取得相应FileUploadStatus类对象
	 */
	public FileConvertStatus getUploadStatus(String fid) {
		if(fid == null)
		{
			return null;
		}
		lock.lock();
		try {
		int index = indexOf(fid);
		if (indexOf(fid) == -1) {
			return null;
		}
		FileConvertStatus fs = vector.elementAt(index);
//		fs.setCalltime(System.currentTimeMillis());
		fs.setLocked(true);
		return fs;
		} finally {
			lock.unlock();
		}
	}
	

	/**
	 * 存储FileUploadStatus类对象
	 */
	public void setUploadStatus(FileConvertStatus status) {
		if(status == null)
		{
			return;
		}
		lock.lock();
		try {
			int nIndex = indexOf(status.getFid());
			if (-1 == nIndex) {
				vector.add(status);
			} else {
				vector.insertElementAt(status, nIndex);
				vector.removeElementAt(nIndex + 1);
			}
		} finally {
			lock.unlock();
		}
	}

	public FileConvertStatus[] removeUploadStatusbySid(String sessionID) {
		ArrayList<FileConvertStatus> lists = new ArrayList<FileConvertStatus>();
		lock.lock();
		try {
			for (int i = 0; i < vector.size(); i++) {
				FileConvertStatus status = vector.elementAt(i);
				if (status.getSessionid().equals(sessionID)) {
					lists.add(status);
				}
			}
			for (FileConvertStatus st : lists) {
				vector.remove(st);
			}
			
		} finally {
			lock.unlock();
		}
		return lists.toArray(new FileConvertStatus[0]);
	}
	
	/**
	 * 删除FileUploadStatus类对象
	 */
	public FileConvertStatus removeUploadStatus(String strID) {
		lock.lock();
		FileConvertStatus fs = null;
		try {
			int nIndex = indexOf(strID);
			if (-1 != nIndex)
				fs = vector.remove(nIndex);
		} finally {
			lock.unlock();
		}
		return fs;
	}

	/**
	 * 删除FileUploadStatus类对象
	 */
	public void removeUploadStatus(FileConvertStatus status) {
		if(status == null)
		{
			return;
		}
		lock.lock();
		try {
			vector.remove(status);
		} finally {
			lock.unlock();
		}
	}
	
	/*private void clearTimeout()
	{
		long t = System.currentTimeMillis();
		lock.lock();
		try {
			ArrayList<FileConvertStatus> relists = new ArrayList<FileConvertStatus>();
			for (int i = 0; i < vector.size(); i++) {
				FileConvertStatus temp = vector.get(i);
				long ext = t - temp.getCalltime();
				if(!temp.isLocked() && ext > 600000)
				{
					relists.add(temp);
				}
			}
			for (int i = 0; i < relists.size(); i++) {
				vector.remove(relists.get(i));
			}
		} finally {
			lock.unlock();
		}
	}*/
}
