package dcs.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import dcs.core.ConvertUtil;
import dcs.interfaces.IConvert;
import dcs.interfaces.IPICConvertor;


//spring配置之后就不需要extends
public class Convert extends UnicastRemoteObject implements IConvert{

	public Convert() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int convertMStoHTML(String src, String tar){
		// TODO Auto-generated method stub
		return ConvertUtil.convertMStoHtml(src, tar);
	}
	
	public int convertPDFtoHTML(String src, String tar){
		// TODO Auto-generated method stub
		return ConvertUtil.convertPDFtoHtml(src, tar);
	}
	@Override
	public int convertMStoPDF(String src, String tar) throws RemoteException {
		// TODO Auto-generated method stub
		return ConvertUtil.convertMStoPDF(src, tar);
	}
	
	@Override
	public IPICConvertor convertMStoPic(String paramString)
	{
		PICConvertor pic = null;
		try {
			pic = new PICConvertor();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object[] obj = ConvertUtil.convertMStoPic(paramString);
		//System.out.println("ms==="+paramString+"==="+obj[0]+"===="+obj[1]);
		pic.setPICObj(obj[0],obj[1]);
		return pic;
	}
	
	public IPICConvertor convertPDFtoPic(String paramString)
	{
		PICConvertor pic = null;
		try {
			pic = new PICConvertor();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object[] obj = ConvertUtil.convertPDFtoPic(paramString);
		//System.out.println("==="+paramString+"==="+obj[0]+"===="+obj[1]);
		pic.setPICObj(obj[0],obj[1]);
		return pic;
	}

	@Override
	public boolean testConnect() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void exit() throws RemoteException {
		// TODO Auto-generated method stub
		System.exit(0);
	}
}
