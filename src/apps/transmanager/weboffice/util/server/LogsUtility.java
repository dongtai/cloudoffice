package apps.transmanager.weboffice.util.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import apps.transmanager.weboffice.service.objects.ILogs;


/**
 * @author sch
 *
 */
public class LogsUtility extends ByteArrayOutputStream
{
    private static LogsUtility utility = new LogsUtility();
    private static PrintStream printStream = new PrintStream(utility);
    private static int start = -1;
    private static int saved;
    private static boolean found;
    protected static int stack;
    /** 当捕获的异常需要输出的随机流， */
    private RandomAccessFile catchExceptionStream;
    /** 当前行的标志：0 没有任何内容或其他日志行，1 首行"Exception ", 2 可能为异常行， 3 堆栈at行 */
    private static byte outputFlag;
    private static String path;
    private static String version;
    private static boolean del;            // 当log文件太大的时候是否删除以前内容
    private static boolean debug;          // 是否写debug信息
    private static boolean error = true;          // 是否写error信息
    private static boolean info;           // 是否写提示信息
    private static boolean consoleFlag;    // 信息是否输出到控制台。
    private static String userLogPath;
    

    /** Creates new ErrorUtility */
    private LogsUtility()
    {
    }

    /**
     * 注册异常捕获。
     */
    public static void init(String p, boolean flag, String logLevel, boolean console, String userLog)
    {
    	userLogPath = userLog;
    	path = p;
    	del = flag;
    	consoleFlag = console;
    	if (logLevel != null)
    	{
	    	debug = logLevel.indexOf("debug") != -1;
	    	error = logLevel.indexOf("error") != -1;
	    	info = logLevel.indexOf("info") != -1;
    	}
    	if (!consoleFlag)     // 输出到控制台
    	{
    		System.setErr(printStream);
    	}
    }
    
   /**
    * 
    * @param e
    */
    public static void error(Throwable e)
    {
    	if (error)
    	{
    		write(e, 0, 0, 0);
    	}
    }
    
    /**
     * 
     * @param e
     */
     public static void error(String me, Throwable e)
     {
     	if (error)
     	{
     		write(me);
     		if(e != null)
     		write(e, 0, 0, 0);
     	}
     }
     
     public static void error(String me)
     {
     	if (error)
     	{
     		write(me);
     	}
     }
    
    /**
     * 
     * @param s
     */
    public static void info(Object s)
    {
    	if (info)
    	{
    		write(s);
    	}
    }
    
    /**
     * 
     * @param s
     */
    public static void debug(Object s)
    {
    	if (debug)
    	{
    		write(s);
    	}
    }
    
    /**
     * write message
     */
    private static void write(Object message)
    {
        write(message, 0, message instanceof byte[] ? ((byte[])message).length : 0, 1);
    }

    private static void write(Object e, int off, int count, int type)
    {
    	if (consoleFlag)
    	{
    		if (e instanceof Throwable)
    		{
    			((Throwable) e).printStackTrace();
    			return;
    		}
    		System.out.println(e);
    		return;
    	}
        // 仅当太多的异常才返回。
        /*if (saved == 2)
        {
            return;
        }*/
        RandomAccessFile rf = null;
        PrintStream printStream = null;
        try
        {
            File parent = new File(path);
            boolean exists = parent.exists();
            if (!exists || !parent.isDirectory())
            {
                if (exists)
                {
                    parent.mkdir();
                }
                else
                {
                    parent.mkdirs();
                }
            }
            rf = new RandomAccessFile(path + File.separatorChar + "system_log.log", "rw");
            long size = rf.length();
            if (del && start != 0 && size > 0xC000)
            {
                int remain = Math.max(start > 0 ? (int)size - start : 0, 0x7FE0);
                int offs = (int)size - remain;
                if (start > 0)
                {
                    if (offs > start)
                    {
                        offs = start;
                    }
                    start -= offs;
                }
                remain = (int)size - offs;
                rf.seek(offs);
                byte[] buffer = new byte[remain];
                rf.read(buffer);
                rf.seek(0);
                if (start != 0)
                {
                    rf.writeBytes("Some old log messages are deleted.\015\012");
                    int bytes = (int)rf.getFilePointer();
                    if (start > 0)
                    {
                        start += bytes;
                    }
                    remain += bytes;
                }
                rf.write(buffer);
                buffer = null;
                rf.setLength(size = remain);
            }
            else
            {
                /*if (size >= 0x10000)
                {
                    saved = 2;
                }*/
                rf.seek(size);
            }
            Date date = type == 0 || saved == 0 ? new Date() : null;
            if (saved == 0)
            {
                start = (int)size;
                saved = 1;
                rf.writeShort(0xD0A);
                for (int i = 80; i-- > 0;)
                {
                    rf.writeByte('-');
                }
                rf.writeBytes(new SimpleDateFormat("'\015\012'yyyy.MM.dd HH:mm:ss'  " + version + "\015\012'").format(date));
            }
            if (type == 0)
            {
                rf.writeBytes(new SimpleDateFormat("'\015\012Exception occurs:' yyyy.MM.dd HH:mm:ss'\015\012'").format(date));
            }
            if (e instanceof Throwable)
            {
                // 临时的输出流
                LogsUtility tempStream = new LogsUtility();
                // 实际输出到随机流
                tempStream.catchExceptionStream = rf;
                printStream = new PrintStream(tempStream);
                // 用于写 Errorlog，不可以注释掉
                ((Throwable)e).printStackTrace(printStream);
            }
            else if (e instanceof byte[])
            {
                rf.write((byte[])e, off, count);
            }
            else if (e instanceof String)
            {
                rf.write(((String)e).getBytes());
                rf.writeShort(0xD0A);
            }
            // The log length has exceeded 64K.
            if (saved == 2)
            {
                rf.writeBytes("\015\012Too many exceptions.\015\012");
            }
        }
        catch(Throwable ex)
        {
            // Your system has error
            // Cannot process this exception, otherwise will be cycle call.
        }
        try
        {
            // 关闭文件流
            if (printStream != null)
            {
                printStream.close();
            }
            if (rf != null)
            {
                rf.close();
            }
        }
        catch(Throwable exp)
        {
            // Cannot process this exception
            // Otherwise will be cycle call.
        }
    }

    /**
     * write the buffer.
     * 
     * @param buf A byte array
     * @param off Offset from which to start taking bytes
     * @param len Number of bytes to write
     */
    public void write(byte[] buf, int off, int len)
    {
    	if (consoleFlag)
    	{
    		System.out.println(new String(buf, off, len));
    		return;
    	}
        // 输出捕获的异常
        if (catchExceptionStream != null)
        {
            try
            {
                catchExceptionStream.write(buf, off, len);
            }
            catch(Throwable e)
            {
                // 不做任何操作。
            }
            return;
        }
        // 回车换行不需要分析
        int type = len > 2 ? analyze(buf, off, off + len) : -1;
        // 需要屏蔽某些程序的输出
        if (outputFlag == 0)
        {
            return;
        }
        if (type == 0)
        {
            found = true;
            stack = 0;
        }
        else if (type == 1)
        {
            type = found ? -1 : 0;
            stack = 0;
            found = true;
        }
        else if (type == 2)
        {
            if (!found)
            {
                type = -1;
            }
            found = false;
            stack = 0;
        }
        else if (type == 3)
        {
            type = found ? -1 : 0;
            found = false;
            stack = 99;
        }
        else if (type == 4)
        {
            type = 2;
            found = false;
            stack = 0;
        }
        else if (stack > 0)
        {
            if (--stack == 0)
            {
                type = 2;
                stack = -1;
            }
        }
        else if (stack < 0)
        {
            return;
        }
        write(buf, off, len, type);
    }

    /*
     * return the type -1 = normal, 0 - first, 1 - exception, 2 - run
     */
    private int analyze(byte[] buf, int start, int end)
    {
        // 是否为堆栈行
        if (equals("\tat", buf, start, start + 3))
        {
            outputFlag = 3;
        }
        else
        {
            if (end - start > 20)
            {
                // Exception in 以前为 Exception occured
                if (equals("Exception ", buf, start, start + 10))
                {
                    outputFlag = 1;
                    return 0;
                }
                if (equals("Caused by:", buf, start, start + 10))
                {
                    outputFlag = 2;
                    return -1;
                }
            }
            // 其他行，在首行后面为异常行，否则为日志行
            if (outputFlag == 1)
            {
                outputFlag = 2;
            }
            else
            {
                // 检查是否为异常类行
                boolean hasDot = false;
                // 结束
                int stop = end;
                // 索引
                for (int i = start; i < end; i++)
                {
                    // 字符
                    int ch = buf[i];
                    if (ch == '.')
                    {
                        hasDot = true;
                    }
                    else if (ch == ' ' || ch == ':')
                    {
                        stop = i;
                        break;
                    }
                }
                // 为异常类
                if (hasDot
                    && (stop - 9 > start && equals("Exception", buf, stop - 9, stop) || stop - 5 > start
                        && equals("Error", buf, stop - 5, stop)))
                {
                    outputFlag = 1;
                    return 0;
                }
                outputFlag = 0;
            }
        }
        int next = next(buf, start, end);
        if (next < 0 || buf[next] != '.')
        {
            return -1;
        }
        int type = 1;
        int count = 1;
        while (true)
        {
            start = next + 1;
            next = next(buf, start, end);
            count++;
            int token;
            if (next < 0 || next >= end - 1 || (token = buf[next]) == ':')
            {
                if (next < 0)
                {
                    next = end;
                }
                if (count == 3 && type == 1)
                {
                    if (equals("StackOverflowError", buf, start, next))
                    {
                        return 3;
                    }
                    if (equals("OutOfMemoryError", buf, start, next))
                    {
                        return 4;
                    }
                }
                return count < 3 ? -1 : type;
            }
            if (token == '(')
            {
                return equals("run", buf, start, next) ? 2 : -1;
            }
            else if (token != '.')
            {
                type = -1;
            }
        }
    }

    private static int next(byte[] buf, int start, int end)
    {
        while (start < end)
        {
            int ch = buf[start];
            if (ch == '.' || ch == '$' || ch == '(' || ch == ':')
            {
                return start;
            }
            start++;
        }
        return -1;
    }

    private static boolean equals(String s, byte[] buf, int start, int end)
    {
        int length = s.length();
        if (length == end - start)
        {
            for (end = 0; end < length; end++)
            {
                if (s.charAt(end) != buf[start++])
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * 将一个字符串写入指定文件
     * @param filePath
     * @param fileName
     * @param content
     * @param isAppend
     * @return
     */
    public static boolean logToFile(String filePath, String fileName, boolean isAppend, ILogs log)
    {
    	RandomAccessFile out = null;
        try
        {
        	if (filePath != null && filePath.length() > 0)
        	{
        		filePath = userLogPath + File.separatorChar + filePath;
        	}
        	else
        	{
        		filePath = userLogPath;
        	}
            boolean bool = checkDirPath(filePath);
            if (bool)
            {
            	out = new RandomAccessFile(filePath + File.separatorChar + fileName, "rw");
            	if (isAppend)
            	{
            		out.seek(out.length());
            	}
                log.writeLogs(out);
                
                return true;
            }
            return false;
        }
        catch(Exception e)
        {
            return false;
        }
        finally
        {
        	if (out != null)
        	{
        		try
        		{
        			out.close();
        		}
        		catch(Exception ee)
        		{}
        	}
        }
    }
    
    /**
     * 检查指定目录是否存在,不存在则创建
     * @param directoryPath
     * @return
     */
    private static boolean checkDirPath(String directoryPath)
    {
        File f = new File(directoryPath);
        if (f.exists())
        {
            return true;
        }
        else
        {
            try
            {
                f.mkdirs();
                return true;
            }
            catch(Exception e)
            {
                return false;
            }
        }
    }

}

