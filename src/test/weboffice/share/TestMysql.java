package test.weboffice.share;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.HttpSession;
import javax.servlet.http.*;


public class TestMysql {

	/**
	 * @param args
	 */
    public Connection conn=null;

    public Statement smt=null;
    public ResultSet rs=null;
	//public static String link="jdbc:mysql://210.51.25.140/chnmanufacture?user=chnmanufacture&password=gz7xyu&useUnicode=true&characterEncoding=gbk";
	public static String link="jdbc:mysql://61.129.81.5/linksw?user=linksw_f&password=sunah&useUnicode=true&characterEncoding=gbk";
	//linksw 210.51.25.215 linksw_r
	//密码： linksw_w
	//密码： linksw_f
	//密码： sunah

	
	
	public TestMysql() 
    {
        try {
        
        	//Class.forName("org.gjt.mm.mysql.Driver").newInstance(); 
        	Class.forName("com.mysql.jdbc.Driver").newInstance(); 
        }
        catch (Exception E) 
        {
            System.out.println("Unable to load driver.");
        }
    }
    public void getConn()
    {
    	try
    	{
    		//conn=jdbc.getConnection();
    		conn = DriverManager.getConnection(link);
    	}
    	catch (Exception e)
    	{
    		System.out.println("getConn====="+e.getMessage());
    	}
    }
    public Vector query(String SQL,int number) throws SQLException
    {
        Vector vector=new Vector();
        getConn();
        //conn=jdbc.getConnection();
        //conn = DriverManager.getConnection(link);
        // 原来的QueryDb.java --> QueryDbBack.java
        // 如果使用日文，需要使用JapanJdbcConnection.java
        // 王俊
        //conn = JdbcConnection.getConnection();
        //smt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        try
        {
        	System.out.println("11111111111111111111");
            smt=conn.createStatement();
            //smt=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs=smt.executeQuery(SQL);
            //System.out.println(SQL);
            while (rs.next())
            {
                String[] temp=new String[number];
                for (int i=0;i<number;i++)
                {
                    if (rs.getString(i+1)==null)
                    {
                        temp[i]="";
                    }
                    else
                    {
                        temp[i]=rs.getString(i+1);
                    }
                    System.out.println(temp[i]);
                }
                vector.addElement(temp);
            }
            //System.out.println("over!!!!!!!!!!11");
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println("query="+e.getMessage());
        }
        conn.close();
        return vector;
    }
	public static void main(String[] args) 
	{
		String SQL="select id,name from temp ";
		try
		{
			TestMysql test=new TestMysql();
			test.query(SQL,2);
		}
		catch (Exception e)
		{
			System.out.println("====="+e.getMessage());
		}
		System.exit(0);
	}

}
