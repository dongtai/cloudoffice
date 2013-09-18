package dcs.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPICConvertor extends Remote {
	
	public void setPICObj(Object picconvert,Object convertorObject)throws RemoteException;
	
	public int resultCode()throws RemoteException;

	public int getPageCount()throws RemoteException;

	public float[][] getAllpageWHeigths()throws RemoteException;

	public int convertToGIF(int paramInt1, int paramInt2,
			float paramFloat, String paramString)throws RemoteException;

	public int convertToPNG(int paramInt1, int paramInt2,
			float paramFloat, String paramString)throws RemoteException;

	public int convertToJPG(int paramInt1, int paramInt2,
			float paramFloat, String paramString)throws RemoteException;

	public int convertToTIFF(int paramInt1, int paramInt2,
			float paramFloat, String paramString)throws RemoteException;

	public int convertToBMP(int paramInt1, int paramInt2,
			float paramFloat, String paramString)throws RemoteException;

	public void close()throws RemoteException;
}
