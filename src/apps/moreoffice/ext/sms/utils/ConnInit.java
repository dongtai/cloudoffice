package apps.moreoffice.ext.sms.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public final class ConnInit
{
    private static ConnInit instance = null;

    private static String url = null;
    private static String user = null;
    private static String password = null;
    public static String portName = "COM1";
    public static int portRate = 9600;

    private ConnInit()
    {
    }

    static
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = loadFile();
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");
            portName = properties.getProperty("portName");
            portRate = Integer.parseInt(properties.getProperty("portRate"));
        }
        catch(Exception e)
        {
            throw new ExceptionInInitializerError();
        }
    }

    public static ConnInit getInstance()
    {
        if (null == instance)
        { //单例延迟加载
            synchronized(ConnInit.class)
            {
                if (null == instance)
                { //JDK 5以后才可以使用双重枷锁
                    instance = new ConnInit();
                }
            }
        }
        return instance;
    }

    public Connection getConnection()
    {
        Connection conn = null;
        try
        {
            //System.out.println("Sms service connecting sms datbase...");
            conn = DriverManager.getConnection(url, user, password);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return conn;
    }

    public void free(ResultSet rs, Statement st, Connection conn)
    {
        if (null != rs)
        {
            try
            {
                rs.close();
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (null != st)
                {
                    try
                    {
                        st.close();
                    }
                    catch(SQLException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (null != conn)
                        {
                            try
                            {
                                conn.close();
                            }
                            catch(SQLException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public void free(Statement st, Connection conn)
    {
        if (null != st)
        {
            try
            {
                st.close();
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (null != conn)
                {
                    try
                    {
                        conn.close();
                    }
                    catch(SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static Properties loadFile()
    {
        Properties properties = new Properties();
        try
        {
            java.io.InputStream is = ConnInit.class.getClassLoader().getResourceAsStream("com/evermore/ext/sms/properties/sms.properties");
//            java.io.FileInputStream is = new java.io.FileInputStream("/usr/lib/sms.properties");
            properties.load(is);
            is.close();
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
        return properties;
    }
}
