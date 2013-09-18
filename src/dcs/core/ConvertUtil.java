package dcs.core;

import java.lang.reflect.Method;

import dcs.core.ConvertorPool.ConvertorObject;


/**
 * 转换的类，与yozo的转换代码交互
 * @author Administrator
 *
 */
public class ConvertUtil {

	public static int convertMStoHtml(String src, String target) {
		ConvertorObject co = ConvertorPool.getInstance().getConvertor();
		Object convertor = co.convertor;
		Object result = null;
		try {
			try {
				Method method = convertor.getClass().getMethod(
						"convertMStoHTML",
						new Class[] { String.class, String.class });
				result = method.invoke(convertor, src, target);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (result instanceof Integer) {
				co.setResult(((Integer) result).intValue());
				return ((Integer) result).intValue();
			}
			co.setResult(-1);
			return -1;
		} finally {
			ConvertorPool.getInstance().returnConvertor(co);
		}
	}
	
	public static int convertPDFtoHtml(String src, String target) {
		ConvertorObject co = ConvertorPool.getInstance().getConvertor();
		Object convertor = co.convertor;
		Object result = null;
		try {
			try {
				Method method = convertor.getClass().getMethod(
						"convertPdfToHtml",
						new Class[] { String.class, String.class });
				result = method.invoke(convertor, src, target);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (result instanceof Integer) {
				co.setResult(((Integer) result).intValue());
				return ((Integer) result).intValue();
			}
			co.setResult(-1);
			return -1;
		} finally {
			ConvertorPool.getInstance().returnConvertor(co);
		}
	}

	public static int convertMStoPDF(String src, String target) {
		ConvertorObject co = ConvertorPool.getInstance().getConvertor();
		Object convertor = co.convertor;
		Object result = null;
		try {
			try {
				Method method = convertor.getClass().getMethod(
						"convertMStoPDF",
						new Class[] { String.class, String.class });
				result = method.invoke(convertor, src, target);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result instanceof Integer) {
				co.setResult(((Integer) result).intValue());
				return ((Integer) result).intValue();
			}
			co.setResult(-1);
			return -1;
		} finally {
			ConvertorPool.getInstance().returnConvertor(co);
		}

	}

	public static Object[] convertMStoPic(String paramString) {
		ConvertorObject co = ConvertorPool.getInstance().getConvertor();
		Object convertor = co.convertor;
		Object result = null;
		try {
			Method method = convertor.getClass().getMethod("convertMStoPic",
					new Class[] { String.class });
			result = method.invoke(convertor, paramString);
			if (result != null) {
				co.setResult(resultCode(result));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// ConvertorPool.getInstance().returnConvertor(co);
			co.setResult(-1);
			e.printStackTrace();
		}
		/*
		 * finally { ConvertorPool.getInstance().returnConvertor(co); }
		 */
		return new Object[] { result, co };
	}

	public static int resultCode(Object obj) {
		Object result = null;
		Method method;
		try {
			method = obj.getClass().getMethod("resultCode", new Class[] {});
			result = method.invoke(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result instanceof Integer) {
			return ((Integer) result).intValue();
		}
		return -1;
	}

	public static int getPageCount(Object obj, Object co) {
		Object result = null;
		Method method;
		try {
			method = obj.getClass().getMethod("getPageCount", new Class[] {});
			result = method.invoke(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result instanceof Integer) {
			return ((Integer) result).intValue();
		}
		((ConvertorObject) co).setResult(-1);
		return -1;
	}

	public static float[][] getAllpageWHeigths(Object obj, Object co) {
		Object result = null;
		Method method;
		try {
			method = obj.getClass().getMethod("getAllpageWHeigths",
					new Class[] {});
			result = method.invoke(obj);
			if(result == null)
			{
				result = new float[0][0];
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result = null;
			e.printStackTrace();
		}
		if (result instanceof float[][]) {
			return ((float[][]) result);
		}
		((ConvertorObject) co).setResult(-1);
		return null;
	}

	public static int convertToGIF(Object obj, int paramInt1, int paramInt2,
			float paramFloat, String paramString, Object co) {
		Object result = null;
		Method method;
		try {
			method = obj.getClass().getMethod(
					"convertToGIF",
					new Class[] { int.class, int.class, float.class,
							String.class });
			result = method.invoke(obj, paramInt1, paramInt2, paramFloat,
					paramString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result instanceof Integer) {
			((ConvertorObject) co).setResult(((Integer) result).intValue());
			return ((Integer) result).intValue();
		}
		((ConvertorObject) co).setResult(-1);
		return -1;
	}

	public static int convertToPNG(Object obj, int paramInt1, int paramInt2,
			float paramFloat, String paramString, Object co) {
		Object result = null;
		Method method;
		try {
			method = obj.getClass().getMethod(
					"convertToPNG",
					new Class[] { int.class, int.class, float.class,
							String.class });
			result = method.invoke(obj, paramInt1, paramInt2, paramFloat,
					paramString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result instanceof Integer) {
			((ConvertorObject) co).setResult(((Integer) result).intValue());
			return ((Integer) result).intValue();
		}
		((ConvertorObject) co).setResult(-1);
		return -1;
	}

	public static int convertToJPG(Object obj, int paramInt1, int paramInt2,
			float paramFloat, String paramString, Object co) {
		Object result = null;
		Method method;
		try {
			method = obj.getClass().getMethod(
					"convertToJPG",
					new Class[] { int.class, int.class, float.class,
							String.class });
			result = method.invoke(obj, paramInt1, paramInt2, paramFloat,
					paramString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result instanceof Integer) {
			((ConvertorObject) co).setResult(((Integer) result).intValue());
			return ((Integer) result).intValue();
		}
		((ConvertorObject) co).setResult(-1);
		return -1;
	}

	public static void close(Object picconvert, Object convertorObject) {
		Method method;
		try {
			method = picconvert.getClass().getMethod("close", new Class[] {});
			method.invoke(picconvert);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ConvertorPool.getInstance().returnConvertor(
					(ConvertorObject) convertorObject);
		}
	}

	public static void deleteTempFiles(Object convertor) {
		try {
			Method method = convertor.getClass().getMethod("deleteTempFiles",
					new Class[] {});
			method.invoke(convertor);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setTempPath(Object convertor, String paramString) {
		try {
			Method method = convertor.getClass().getMethod("setTempPath",
					new Class[] { String.class });
			method.invoke(convertor, paramString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Object[] convertPDFtoPic(String paramString)
	{
		ConvertorObject co = ConvertorPool.getInstance().getConvertor();
		Object convertor = co.convertor;
		Object result = null;
		try 
		{
			Method method = convertor.getClass().getMethod("convertPDFtoPic",	new Class[] { String.class});
			result = method.invoke(convertor, paramString);
			if (result != null) 
			{
				co.setResult(resultCode(result));
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// ConvertorPool.getInstance().returnConvertor(co);
			co.setResult(-1);
			e.printStackTrace();
		}
		/*
		 * finally { ConvertorPool.getInstance().returnConvertor(co); }
		 */
		return new Object[] { result, co };
	}
	
}
