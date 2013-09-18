package apps.moreoffice.ext.sms.utils;

public class SmsException extends Exception{

	public final static long serialVersionUID = 12345678;
	
	public static final int SYSTEM_NORMALEXCEPTION = 100;
	public static final int DATA_NOTFOUND = -2;
	public static final int SYSTEM_ERROR	= -1;
	private boolean debug = true;
	private String trace;
	private int code = SYSTEM_ERROR;

	public SmsException() {
		super();
		init();
	}

	public SmsException(String s) {
		super(s);
		init();
	}
	public SmsException(int code, String s) {
		super(s);
		this.code = code;
		init();
	}
	
	public int getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return (debug?trace:"") + super.getMessage();	
	}
	
	private void init() {
		StackTraceElement traces[] = getStackTrace();
		String className = traces[0].getClassName();
		int n = className.lastIndexOf('.');
		if(n > 0) className = className.substring(n + 1);
		trace = className + "." + traces[0].getMethodName() + "[line: " + traces[0].getLineNumber() + "]: ";
	}
}
