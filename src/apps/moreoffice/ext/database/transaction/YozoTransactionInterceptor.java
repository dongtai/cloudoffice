package apps.moreoffice.ext.database.transaction;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class YozoTransactionInterceptor extends TransactionInterceptor
{
	public Object invoke(final MethodInvocation invocation) throws Throwable 
	{
		try
		{
			//String aaa = UploadServiceImpl.hashtable.put(Thread.currentThread(), "aaa");
			//System.out.println("-----------------------------  put --------------  "+aaa);
			return super.invoke(invocation);
		}
		finally
		{
			//UploadServiceImpl.hashtable.remove(Thread.currentThread());
		}
	}
}
