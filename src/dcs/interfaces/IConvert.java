package dcs.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IConvert extends Remote{

	int convertMStoHTML(String src,String tar)throws RemoteException;
	int convertPDFtoHTML(String src,String tar)throws RemoteException;
	int convertMStoPDF(String src,String tar)throws RemoteException;
	IPICConvertor convertMStoPic(String paramString)throws RemoteException;
	boolean testConnect()throws RemoteException;
	void exit()throws RemoteException;
	IPICConvertor convertPDFtoPic(String paramString)throws RemoteException;
}
