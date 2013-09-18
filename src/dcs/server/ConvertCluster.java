package dcs.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

import dcs.config.ClusterConfig;
import dcs.interfaces.IConvert;
import dcs.interfaces.IPICConvertor;
import dcs.server.ConvertRMIClusterPool.ConvertorClusterObject;

public class ConvertCluster extends UnicastRemoteObject implements IConvert {

	public ConvertCluster() throws RemoteException {
		super();
		ConvertRMIClusterPool.getInstance();
		// TODO Auto-generated constructor stub
	}

	private class ConvertorClusterObjectTimerTask extends TimerTask {

		ConvertorClusterObjectTimerTask(ConvertorClusterObject cco) {
			this.cco = cco;
		}

		ConvertorClusterObject cco;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ConvertRMIClusterPool.getInstance().returnBadConvertor(cco);
		}

	}

	@Override
	public int convertMStoHTML(String src, String tar) throws RemoteException {
		// TODO Auto-generated method stub
		ConvertorClusterObject cco = ConvertRMIClusterPool.getInstance()
				.getConvertor();
		Timer timer = new Timer();
		timer.schedule(new ConvertorClusterObjectTimerTask(cco),
				ClusterConfig.getOvertime());

		try {
			int re = cco.convertor.convertMStoHTML(src, tar);
			timer.cancel();
			return re;
		} catch (Exception e) {
			ConvertRMIClusterPool.getInstance().returnBadConvertor(cco);
		} finally {
			ConvertRMIClusterPool.getInstance().returnConvertor(cco);
		}
		return -1;
	}
	
	public int convertPDFtoHTML(String src, String tar) throws RemoteException {
		// TODO Auto-generated method stub
		ConvertorClusterObject cco = ConvertRMIClusterPool.getInstance()
				.getConvertor();
		Timer timer = new Timer();
		timer.schedule(new ConvertorClusterObjectTimerTask(cco),
				ClusterConfig.getOvertime());

		try {
			int re = cco.convertor.convertPDFtoHTML(src, tar);
			timer.cancel();
			return re;
		} catch (Exception e) {
			ConvertRMIClusterPool.getInstance().returnBadConvertor(cco);
		} finally {
			ConvertRMIClusterPool.getInstance().returnConvertor(cco);
		}
		return -1;
	}

	@Override
	public int convertMStoPDF(String src, String tar) throws RemoteException {
		ConvertorClusterObject cco = ConvertRMIClusterPool.getInstance()
				.getConvertor();
		// TODO Auto-generated method stub
		Timer timer = new Timer();
		timer.schedule(new ConvertorClusterObjectTimerTask(cco),
				ClusterConfig.getOvertime());
		try {

			int re = cco.convertor.convertMStoPDF(src, tar);
			timer.cancel();
			return re;
		} catch (Exception e) {
			ConvertRMIClusterPool.getInstance().returnBadConvertor(cco);
		} finally {
			ConvertRMIClusterPool.getInstance().returnConvertor(cco);
		}
		return -1;
	}

	@Override
	public IPICConvertor convertMStoPic(String paramString)
			throws RemoteException {
		// TODO Auto-generated method stub
		ConvertorClusterObject cco = ConvertRMIClusterPool.getInstance()
				.getConvertor();
		Timer timer = new Timer();
		timer.schedule(new ConvertorClusterObjectTimerTask(cco),
				ClusterConfig.getOvertime());
		try {
			IPICConvertor re = cco.convertor.convertMStoPic(paramString);
			timer.cancel();
			return re;
		} catch (Exception e) {
			ConvertRMIClusterPool.getInstance().returnBadConvertor(cco);
		} finally {
			ConvertRMIClusterPool.getInstance().returnConvertor(cco);
		}
		return null;
	}
	
	public IPICConvertor convertPDFtoPic(String paramString)	throws RemoteException 
	{
		// TODO Auto-generated method stub
		ConvertorClusterObject cco = ConvertRMIClusterPool.getInstance()
				.getConvertor();
		Timer timer = new Timer();
		timer.schedule(new ConvertorClusterObjectTimerTask(cco),
				ClusterConfig.getOvertime());
		try {
			IPICConvertor re = cco.convertor.convertPDFtoPic(paramString);
			timer.cancel();
			return re;
		} catch (Exception e) {
			ConvertRMIClusterPool.getInstance().returnBadConvertor(cco);
		} finally {
			ConvertRMIClusterPool.getInstance().returnConvertor(cco);
		}
		return null;
	}

	@Override
	public boolean testConnect() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void exit() throws RemoteException {
		// TODO Auto-generated method stub
	}

}
