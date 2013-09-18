package dcs.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import dcs.core.ConvertUtil;
import dcs.interfaces.IPICConvertor;
//spring配置之后就不需要extends
public class PICConvertor extends UnicastRemoteObject implements IPICConvertor{

	protected PICConvertor() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public int resultCode() {
		// TODO Auto-generated method stub
		return ConvertUtil.resultCode(this.obj);
	}

	@Override
	public int getPageCount() {
		// TODO Auto-generated method stub
		return ConvertUtil.getPageCount(this.obj,convertorObject);
	}

	@Override
	public float[][] getAllpageWHeigths() {
		// TODO Auto-generated method stub
		return ConvertUtil.getAllpageWHeigths(obj,convertorObject);
	}

	@Override
	public int convertToGIF(int paramInt1, int paramInt2, float paramFloat,
			String paramString) {
		// TODO Auto-generated method stub
		return ConvertUtil.convertToGIF(this.obj, paramInt1, paramInt2, paramFloat, paramString,convertorObject);
	}

	@Override
	public int convertToPNG(int paramInt1, int paramInt2, float paramFloat,
			String paramString) {
		// TODO Auto-generated method stub
		return ConvertUtil.convertToPNG(this.obj, paramInt1, paramInt2, paramFloat, paramString,convertorObject);
	}

	@Override
	public int convertToJPG(int paramInt1, int paramInt2, float paramFloat,
			String paramString) {
		// TODO Auto-generated method stub
		return ConvertUtil.convertToJPG(this.obj, paramInt1, paramInt2, paramFloat, paramString,convertorObject);
	}

	@Override
	public int convertToTIFF(int paramInt1, int paramInt2, float paramFloat,
			String paramString) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int convertToBMP(int paramInt1, int paramInt2, float paramFloat,
			String paramString) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		ConvertUtil.close(obj,convertorObject);
	}


	@Override
	public void setPICObj(Object picconvert,Object convertorObject) {
		// TODO Auto-generated method stub
		this.obj = picconvert;
		this.convertorObject =convertorObject;
	}
	
	Object obj;
	Object convertorObject;

}
