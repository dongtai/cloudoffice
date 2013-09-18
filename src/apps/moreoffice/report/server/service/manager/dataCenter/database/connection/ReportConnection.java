package apps.moreoffice.report.server.service.manager.dataCenter.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.hibernate.cfg.Configuration;

/**
 * 数据库连接
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-6
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class ReportConnection implements IConnection
{
    // 连接
    private Connection con = null;
    // statement
    private Statement stmt = null;
    // 地址
    private String hostIP;
    // 端口
    private String port;
    // 数据库名
    private String name;
    // 用户名
    private String user;
    // 密码
    private String pwd;
    // 驱动
    private String driver;

    /**
     * 构造器
     * 
     * @param configuration 配置文件
     */
    public ReportConnection(Configuration configuration)
    {
        if (init(configuration))
        {
            try
            {
                Class.forName(driver);
            }
            catch(ClassNotFoundException e)
            {
            }
            create();
        }
    }

    /*
     * 初始化
     */
    private boolean init(Configuration configuration)
    {
        if (configuration == null)
        {
            return false;
        }
        hostIP = configuration.getProperty("hostIP");
        port = configuration.getProperty("port");
        name = configuration.getProperty("name");
        user = configuration.getProperty("user");
        pwd = configuration.getProperty("pwd");
        driver = configuration.getProperty("driver");
        return true;
    }

    /*
     * 创建数据库连接 
     */
    private boolean create()
    {
        if (con != null && stmt != null)
        {
            return true;
        }
        try
        {
            close();
            StringBuffer sb = new StringBuffer();
            sb.append("jdbc:mysql://");
            sb.append(hostIP);
            sb.append(":");
            sb.append(port);
            sb.append("/");
            sb.append(name);
            sb.append("?useUnicode=true&characterEncoding=utf8");
            con = DriverManager.getConnection(sb.toString(), user, pwd);
            stmt = con.createStatement();
        }
        catch(SQLException e)
        {
            return false;
        }
        return true;
    }

    /*
     * 关闭
     */
    private void close(ResultSet rs)
    {
        try
        {
            if (rs != null)
            {
                rs.close();
            }
        }
        catch(SQLException e)
        {
        }
    }

    /*
     * 关闭
     */
    private void close()
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
            if (con != null)
            {
                con.close();
            }
        }
        catch(SQLException e)
        {
        }
    }

    /*
     * 检查数据库连接是否正常
     */
    private boolean checkState()
    {
        if (con == null || stmt == null)
        {
            return create();
        }
        return true;
    }

    /**
     * 执行创建、更新、删除语句
     * 
     * @param sql sql语句
     */
    public void executeUpdate(String sql)
    {
        try
        {
            if (checkState())
            {
                stmt.executeUpdate(sql);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * 执行查询语句
     * 
     * @param sql sql语句 
     * @return ResultSet 结果集
     */
    public Vector<Vector<Object>> executeQuery(String sql)
    {
        if (!checkState())
        {
            return null;
        }

        ResultSet rs = null;
        try
        {
            rs = stmt.executeQuery(sql);
            if (rs != null)
            {
                Vector<Vector<Object>> records = new Vector<Vector<Object>>();
                Vector<Object> record;
                int len = rs == null ? 0 : rs.getMetaData().getColumnCount();
                while (rs.next())
                {
                    record = new Vector<Object>(len);
                    for (int i = 1; i <= len; i++)
                    {
                        record.add(rs.getObject(i));
                    }
                    records.add(record);
                }
                return records;
            }
        }
        catch(Exception e)
        {

        }
        finally
        {
            if (rs != null)
            {
                close(rs);
            }
        }
        return null;
    }
}