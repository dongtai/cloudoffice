package apps.moreoffice.ext.share;

/**
 *
 * @author  sunah
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class QueryDb  implements Serializable
{
	//public static String link="jdbc:mysql://32.63.213.61:3306/newweboffice?user=root&password=123456&useUnicode=true&characterEncoding=utf-8";
    //public static String link="jdbc:mysql://localhost:3306/newweboffice_cloud?user=root&password=123456&useUnicode=true&characterEncoding=utf-8";
    public static String link="jdbc:mysql://localhost:3306/weboffice?user=root&password=123456&useUnicode=true&characterEncoding=utf-8";
    public static String urlip="32.63.213.62";
    public static String urlname="docs.yozosoft.com";
    public static transient Connection conn=null;
    
    // 临时这样改造
    public static final String SERVERINFO_VIEW = " (select `a`.`id` AS `server_id`, "
    		+ " `a`.`realName` AS `server_name`, `a`.`userName` AS `server_logon`, "
    		+ "`a`.`passW` AS `server_psw`, 1 AS `type_id`, "
    		+ "`a`.`realEmail` AS `e_mail`, `a`.`mobile` AS `telephone`, 'Y' AS `effect`, "
    		+ " `a`.`loginType` AS `cityid`, `b`.`organization_id` AS `groupId`, "
    		+ " `a`.`sortnum` AS `sortnum` " 
    		+ "from `users` `a`,`usersorganizations` `b` "
    		+ "where `a`.`id`=`b`.`user_id`) server ";

    private Statement smt=null;
    private ResultSet rs=null;
    public static ThreadPoolManager tm=new ThreadPoolManager(2);
    
    static{
    	
    	Properties p = new Properties();
			try {
				p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/funcConfig.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			link = p.getProperty("jdbc-0.proxool.driver-url").concat("?user="+p.getProperty("jdbc-0.user")+"&password="+p.getProperty("jdbc-0.password")+"&useUnicode=true&characterEncoding=utf-8"); 
    }
    
	public void sendmail(String emailto,String content,String subject)
	{
		try
		{
			tm.process(emailto,content,subject);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
    /** Creates a new instance of QueryDb */
    public QueryDb() 
    {
       try 
        {
        	Class.forName("com.mysql.jdbc.Driver").newInstance(); 
        }
        catch (Exception e) 
        {
           e.printStackTrace();
        }
    }
    public static String getIpName(String ip)
    {
    	if (ip!=null && ip.indexOf(urlip)>=0)
    	{
    		ip=ip.replace(urlip, urlname);
    	}
    	return ip;
    }
    public void getConn()
    {
    	try
    	{
    		if (conn==null)
    		{
    			conn = DriverManager.getConnection(link);
    		}
    		else if (conn.isClosed())
    		{
    			System.out.println("conn is close!");
    			conn.close();
    			conn = DriverManager.getConnection(link);
    		}
    	}
    	catch (Exception e)
    	{
    		System.out.println("getConn====="+e.getMessage());
    	}
    	
    }
    public void logon(String server_logon,String server_psw,HttpSession session) throws SQLException
    {
    	getConn();
    	try
    	{
    		String SQL="select server_id,type_id,e_mail,telephone,server_name,cityid "
    		+" from  " + SERVERINFO_VIEW
    		+" where server_logon='"+server_logon
    		+"' and server_psw='"+server_psw+"'";
    		System.out.println(SQL);
    		smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            String[] temp=new String[6];
            if (rs.next())
            {
                for (int i=0;i<6;i++)
                {
                    if (rs.getString(i+1)==null)
                    {
                        temp[i]="";
                    }
                    else
                    {
                        temp[i]=getStr(rs.getString(i+1));
                    }
                }
                session.setAttribute("server_id", temp[0]);
        		session.setAttribute("server_name", temp[4]);
        		session.setAttribute("servercityid", temp[5]);
            }
    		
    		SQL="update serverlogon set effect='N',logonout=now() where effect='Y' and server_id="+temp[0];
    		smt=conn.createStatement();
            int number=smt.executeUpdate(SQL);
            
    		SQL="insert into serverlogon (server_id,logontime,session_id) values("+temp[0]+",now(),'"+session.getId()+"')";
    		number=smt.executeUpdate(SQL);
    		//conn.commit();
    	}
    	catch (Exception e)
    	{
    		System.out.println("logon========"+e.getMessage());
    	}
//    	conn.close();
//        conn=null;
    }
    public String getUser(String userId,HttpSession session) throws SQLException
    {
    	String server_id="";
    	getConn();
    	try
    	{
    		String SQL="select server_id,type_id,e_mail,telephone,server_name,cityid "
    		+" from " + SERVERINFO_VIEW
    		+" where server_id="+userId;
    		System.out.println(SQL);
    		smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            String[] temp=new String[6];
            if (rs.next())
            {
                for (int i=0;i<6;i++)
                {
                    if (rs.getString(i+1)==null)
                    {
                        temp[i]="";
                    }
                    else
                    {
                        temp[i]=getStr(rs.getString(i+1));
                    }
                }
                session.setAttribute("server_id", temp[0]);
        		session.setAttribute("server_name", temp[4]);
        		session.setAttribute("servercityid", temp[5]);
            }
    		
    		SQL="update serverlogon set effect='N',logonout=now() where effect='Y' and server_id="+temp[0];
    		smt=conn.createStatement();
            int number=smt.executeUpdate(SQL);
            
    		SQL="insert into serverlogon (server_id,logontime,session_id) values("+temp[0]+",now(),'"+session.getId()+"')";
    		number=smt.executeUpdate(SQL);
    		//conn.commit();
    	}
    	catch (Exception e)
    	{
    		System.out.println("logon========"+e.getMessage());
    	}
    	return userId;
    }
    
    public Vector query(String SQL,int number) throws SQLException
    {
        Vector vector=new Vector();
        getConn();
        try
        {
        	if (rs!=null)
        	{
        		rs.close();
        	}
            smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            if (rs!=null)
            {
	            while (rs.next())
	            {
	                String[] temp=new String[number];
	                for (int i=0;i<number;i++)
	                {
	                	temp[i]=getStr(rs.getString(i+1));
	                    if (temp[i]==null)
	                    {
	                        temp[i]="";
	                    }
	                }
	                vector.addElement(temp);
	            }
	            
            }
            
        }
        catch (Exception e)
        {
            System.out.println(SQL+"query="+e.getMessage());
        }
        rs.close();
//        conn.close();
//        conn=null;
        return vector;
    }
    public List<Object[]> queryObj(String SQL,int number) throws SQLException
    {
    	List<Object[]> list=new ArrayList<Object[]>();
        getConn();
        try
        {
        	if (rs!=null)
        	{
        		rs.close();
        	}
            smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            if (rs!=null)
            {
	            while (rs.next())
	            {
	                Object[] temp=new Object[number];
	                for (int i=0;i<number;i++)
	                {
	                	temp[i]=rs.getObject(i+1);
	                }
	                list.add(temp);
	            }
	            
            }
            
        }
        catch (Exception e)
        {
            System.out.println(SQL+"query="+e.getMessage());
        }
        rs.close();
//        conn.close();
//        conn=null;
        return list;
    }
    public Vector querynbsp(String SQL,int number) throws SQLException
    {
        Vector vector=new Vector();
        getConn();
        try
        {
            smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            while (rs.next())
            {
                String[] temp=new String[number];
                for (int i=0;i<number;i++)
                {
                    if (rs.getString(i+1)==null)
                    {
                        temp[i]="&nbsp;";
                    }
                    else
                    {
                        temp[i]=getStr(rs.getString(i+1));
                    }
                }
                vector.addElement(temp);
            }
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println(SQL+"querynbsp="+e.getMessage());
        }
//        conn.close();
//        conn=null;
        return vector;
    }
    public String[] queryunit(String SQL,int number) throws SQLException
    {
        String[] temp=new String[number];
        getConn();
        try
        {
            smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            if (rs.next())
            {
                for (int i=0;i<number;i++)
                {
                    if (rs.getString(i+1)==null)
                    {
                        temp[i]="";
                    }
                    else
                    {
                        temp[i]=getStr(rs.getString(i+1));
                    }
                }
            }
            else
            {
                return null;
            }
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println(SQL+"queryunit="+e.getMessage());
            return null;
        }
//        conn.close();
//        conn=null;
        return temp;
    }
    public String[] queryint(String SQL,int number) throws SQLException
    {
        String[] temp=new String[number];
        getConn();
        try
        {
            smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            if (rs.next())
            {
                for (int i=0;i<number;i++)
                {
                    if (rs.getString(i+1)==null)
                    {
                        temp[i]="0";
                    }
                    else
                    {
                        temp[i]=getStr(rs.getString(i+1));
                    }
                }
            }
            else
            {
                for (int i=0;i<number;i++)
                {
                    temp[i]="0";
                }
            }
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println(SQL+"queryint="+e.getMessage());
            return null;
        }
//        conn.close();
//        conn=null;
        return temp;
    }
    public int getMax(String SQL) throws SQLException
    {
        int temp=-1;
        getConn();
        try
        {
            smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            
            if (rs.next())
            {
                temp=rs.getInt(1);
            }
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println(SQL+"getMax="+e.getMessage());
        }
//        conn.close();
//        conn=null;
        return temp;
    }
    public String getName(String SQL) throws SQLException
    {
        String temp="&nbsp;";
        getConn();
        try
        {
            smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            
            if (rs.next())
            {
                temp=getStr(rs.getString(1));
            }
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println(SQL+"getName="+e.getMessage());
        }
//        conn.close();
//        conn=null;
        return temp;
    }
    public String getValue(String SQL) throws SQLException
    {
        String temp="";
        getConn();
        try
        {
            smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            
            if (rs.next())
            {
                temp=getStr(rs.getString(1));
            }
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println(SQL+"getValue="+e.getMessage());
        }
//        conn.close();
//        conn=null;
        return temp;
    }
    public String[] queryunitnbsp(String SQL,int number) throws SQLException
    {
        String[] temp=new String[number];
        getConn();
        try
        {
            smt=conn.createStatement();
            rs=smt.executeQuery(SQL);
            if (rs.next())
            {
                for (int i=0;i<number;i++)
                {
                    if (rs.getString(i+1)==null)
                    {
                        temp[i]="&nbsp;";
                    }
                    else
                    {
                        temp[i]=getStr(rs.getString(i+1));
                    }
                }
            }
            else
            {
                return null;
            }
            rs.close();
        }
        catch (Exception e)
        {
            System.out.println(SQL+"queryunitnbsp="+e.getMessage());
            return null;
        }
//        conn.close();
//        conn=null;
        return temp;
    }
    public int modifyGroup() throws SQLException
    {
    	int number=0;
    	String SQL="select parent_id,id,name,parentKey,sortnum from organizations order by parent_id,id";
		Vector vector=query(SQL, 5);
		if (vector!=null)
		{
    		int len=vector.size();
    		/*for (int i=0;i<len;i++)
    		{
    			String[] temp=(String[])vector.elementAt(i);
    			String groupcode="";
    			if ("10000".equals(temp[4]))
    			{
    				 groupcode=getParentNode(temp[0],vector,getCode(temp[1]),temp[4]);
    			}
    			else
    			{
    				groupcode=getParentNode(temp[0],vector,getCode(temp[4]),temp[4]);
    			}
    			SQL="update groupinfo set groupcode='"+groupcode+"' where groupid="+temp[1];
    			modifydata(SQL);
    		}*/
		}
    	return number;
    }
    public String getParentNode(String pid,Vector vector,String back,String sortnum)
    {
    	if ("0".equals(pid))
    	{
    		return back;
    	}
    	for (int i=0;i<vector.size();i++)
    	{
    		String[] temp=(String[])vector.elementAt(i);
    		if (pid.equals(temp[1]))
    		{
    			back=temp[3]+"-"+back;
    			break;
    		}
    	}
    	return back;
    }
    private String getPid(String id,Vector vector,String back,String sortnum)
    {
    	if ("0".equals(id))
    	{
    		return back;
    	}
    	for (int i=0;i<vector.size();i++)
    	{
    		String[] temp=(String[])vector.elementAt(i);
    		if (id.equals(temp[1]))
    		{
    			id=temp[0];
    			back=getCode(temp[1])+"-"+back;
    			getPid(id,vector,back,sortnum);
    			break;
    		}
    	}
    	int index=back.lastIndexOf("-");
    	if (index>=0)
    	{
    		try
    		{
    			int num=Integer.parseInt(sortnum);
    			if (num<10)
    			{
    				sortnum="0000"+sortnum;
    			}
    			else if (num<100)
    			{
    				sortnum="000"+sortnum;
    			}
    			else if (num<1000)
    			{
    				sortnum="00"+sortnum;
    			}
    			else if (num<10000)
    			{
    				sortnum="0"+sortnum;
    			}
    		}
    		catch (Exception e)
    		{
    			sortnum="10000";
    		}
    		back=back.substring(0,index)+"-"+sortnum;
    		System.out.println(back);
    	}
    	return back;
    }
    private String getCode(String code)
    {
    	if (code.length()<2)
    	{
    		code="00"+code;
    	}
    	if (code.length()<3)
    	{
    		code="0"+code;
    	}
    	return code;
    }
    public int modifydata(String SQL) throws SQLException
    {
        int number=0;
        getConn();
        try
        {
            smt=conn.createStatement();
            number=smt.executeUpdate(SQL);
            smt.close();
            //conn.commit();
        }
        catch (Exception e)
        {
            System.out.println(SQL+"modifydata="+e.getMessage());
        }
//        conn.close();
//        conn=null;
        return number;
    }
    public static void main(String[] args) throws SQLException
    {
    	QueryDb querydb=new QueryDb();
    	System.out.println(querydb.modifydata("insert into rebatenumber(commodity_id,rebate,rebatenumber) values (1,8.5,100),(1,8,150),(1,7.5,200);"));
    }
    
    public ArrayList querytotal(String SQL,boolean isfirst) throws SQLException
    {
        ArrayList al=new ArrayList();
        try
        {
	        if (isfirst)
	        {
	            al.add(new OptionLabelValue("请选择","all"));
	        }
	        getConn();
	        smt=conn.createStatement();
	        rs=smt.executeQuery(SQL);
	        while (rs.next())
	        {
	            String[] temp=new String[2];
	            temp[0]=getStr(rs.getString(1));
	            temp[1]=getStr(rs.getString(2));
	            al.add(new OptionLabelValue(temp[1],temp[0]));
	        }
	        rs.close();
	        
        }
        catch (Exception e)
        {
        	System.out.println(SQL+e.getMessage());
        }
//        conn.close();
//        conn=null;
        return al;
    }
    
    
    public ArrayList queryother(String SQL) throws SQLException
    {
        ArrayList al=new ArrayList();
        try
        {
	        al.add(new OptionLabelValue("请选择","all"));
	        getConn();
	        smt=conn.createStatement();
	        rs=smt.executeQuery(SQL);
	        while (rs.next())
	        {
	            String[] temp=new String[2];
	            temp[0]=getStr(rs.getString(1));
	            temp[1]=getStr(rs.getString(2));
	            al.add(new OptionLabelValue(temp[1],temp[0]));
	
	        }
	        al.add(new OptionLabelValue("其他","other"));
	        rs.close();
        }
        catch (Exception e)
        {
        	System.out.println(SQL+e.getMessage());
        }
//        conn.close();
//        conn=null;
        return al;
    }
    public ArrayList querystr(String SQL,String str) throws SQLException
    {
        ArrayList al=new ArrayList();
        getConn();
        try
        {
	        if (str!=null)
	        {
	            al.add(new OptionLabelValue(str,"0"));
	        }
	        smt=conn.createStatement();
	        rs=smt.executeQuery(SQL);
	        while (rs.next())
	        {
	            String[] temp=new String[2];
	            temp[0]=getStr(rs.getString(1));
	            temp[1]=getStr(rs.getString(2));
	            al.add(new OptionLabelValue(temp[1],temp[0]));
	        }
	        rs.close();
        }
        catch (Exception e)
        {
        	System.out.println(SQL+e.getMessage());
        }
//        conn.close();
//        conn=null;
        return al;
    }
    public boolean isnumber(String number)
    {
        try
        {
            Integer.parseInt(number);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    public boolean isfloat(String number)
    {
        try
        {
            Float.parseFloat(number);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    public void sessionservlet(HttpSession session)
    {
        try
        {
            String SQL="";
            ArrayList al=querytotal(SQL,true);
            session.setAttribute("",al);
        }
        catch (Exception e)
        {
            System.out.println("sessionservlet!!!!!!!"+e.getMessage());
        }
    }
    public String getweek(String week)
    {
        if (week.equals("1"))
        {
            return "星期一";
        }
        else if (week.equals("2"))
        {
            return "星期二";
        }
        else if (week.equals("3"))
        {
            return "星期三";
        }
        else if (week.equals("4"))
        {
            return "星期四";
        }
        else if (week.equals("5"))
        {
            return "星期五";
        }
        else if (week.equals("6"))
        {
            return "星期六";
        }
        else if ((week.equals("0"))||(week.equals("7")))
        {
            return "星期日";
        }
        return "错错错错";
    }
    public String getweekday(String year,String month,String dayth)
    {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Integer.parseInt(year)-1,Integer.parseInt(month)-1,Integer.parseInt(dayth));
        String week=""+calendar.get(calendar.DAY_OF_WEEK);
        week=getweek(week);
        return week;
    }
    public String getdate()
    {
        long time=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(time);
        return date.toString();
    }
    public Vector toVector(String str)
    {
        Vector vector=new Vector();
        if ((str!=null)&&(!str.equals("")))
        {
            StringTokenizer st = new StringTokenizer(str);
            while (st.hasMoreTokens()) 
            {
                String temp=st.nextToken();
                if ((temp!=null)&&(!temp.trim().equals("")))
                {
                    vector.addElement(temp);
                }
            }
        }
        return vector;
     }
    public String getInstance(String instance_id,HttpSession session)
    {
        ArrayList al=(ArrayList)session.getAttribute("INSTANCE");
        for (int i=0;i<al.size();i++)
        {
            if (instance_id.equals(((OptionLabelValue)al.get(i)).getValue2()))
            {
                return ((OptionLabelValue)al.get(i)).getLabel();
            }
        }
        return instance_id;
    }
    public String getObjName(String SQL)throws SQLException
    {
        String temp="";
        getConn();
        smt=conn.createStatement();
        rs=smt.executeQuery(SQL);
        if (rs.next())
        {
            temp=getStr(rs.getString(1));
        }
        rs.close();
//        conn.close();
//        conn=null;
        return temp;
    }
    //以下是新增加的方法，用来删除合同的所有相关内容
    public StringBuffer ReadFile(String filename)
    {
        StringBuffer buffer=new StringBuffer();
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String context="";
            while ((context=in.readLine())!=null)
            {
            	buffer.append(context);
            }
            in.close(); 
        } 
        catch (IOException e) 
        { 
            System.err.println("文件错误"); 
        } 
        return buffer;
    }
    public BufferedWriter getOut(String filename)
    {
        BufferedWriter out=null;
        try
        {
            File outfile=new File(filename);
            if (!outfile.exists())
            {
                    outfile.createNewFile();
            }
            out = new BufferedWriter( new FileWriter(outfile)); 
        }
        catch (Exception e)
        {
            System.out.println("getOut===="+e.getMessage());
        }
        return out;
    }
    public void deletefile(String deletename,String filename)
    {
    	try
    	{
            if (deletename!=null)
            {
                StringBuffer buffer=ReadFile(filename);
                BufferedWriter out=getOut(deletename);
                out.write(buffer.toString());
                out.close();
            }
    		File filein=new File(filename);
    		if (filein.exists())
    		{
    			filein.delete();
    		}
    	}
    	catch (Exception e)
    	{
    		System.out.println("备份附件出错！！！！！！");
    	}
    }
    public String getToday()
    {
        java.util.Calendar calendar=java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }
    public String getAlName(ArrayList al,String id)
    {
        String value=id;
        if (al!=null)
        {
            for (int i=0;i<al.size();i++)
            {
                OptionLabelValue olv=(OptionLabelValue)al.get(i);
                if (olv.getValue2().equals(id))
                {
                    value=olv.getLabel();
                    break;
                }
            }
        }
        return value;
    }
    public String getStr(String str)
    {
    	String temp=str;
//    	if (temp!=null)
//    	{
//	    	try
//	    	{
//	    		temp=new String(temp.getBytes("iso-8859-1"), "GBK");
//	    	}
//	    	catch (Exception e)
//	    	{
//	    		System.out.println("getStr===="+e.getMessage());
//	    	}
//    	}
    	return temp;
    }
    public String getVectorName(Vector vector,String id)
    {
    	return getVectorName(vector,id,1);
    }
    public String getVectorName(Vector vector,String id,int number)
    {
        String value=id;
        if (vector!=null)
        {
            for (int i=0;i<vector.size();i++)
            {
                String[] temp=(String[])vector.elementAt(i);
                if (temp[0].equals(id))
                {
                    value=temp[number];
                    break;
                }
            }
        }
        return value;
    }
    public String getVecName(Vector vector,String id)
    {
        String value=id;
        if ("0".equals(value))
        {
        	return "&nbsp;";
        }
        if (vector!=null)
        {
            for (int i=0;i<vector.size();i++)
            {
                String[] temp=(String[])vector.elementAt(i);
                if (temp[0].equals(id))
                {
                    value=temp[1];
                    break;
                }
            }
        }
        return value;
    }
    
    public String replacestr(String temp)
    {
    	temp=temp.replaceAll("'", "‘");
    	temp=temp.replaceAll("\"", "“");
    	return temp;
    }
    public ArrayList readfile(String filename)
    {
    	ArrayList list=new ArrayList();
    	try
    	{
	    	java.io.File file=new java.io.File(filename);
	    	if (file.exists())
	    	{
	    		list.add(filename+" is live!");
	    	}
	    	else
	    	{
	    		list.add(filename+" is not live!");
	    	}
	    	java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(filename)));
	    	String data = "";
	    	while((data = br.readLine())!=null)
	    	{
	    		list.add(data);
	    	}
    	}
    	catch (Exception e)
    	{
    		System.out.println(e.getMessage());
    		list.add(e.getMessage());
    	}
    	return list;
    }
    public int getCity(HttpServletRequest request) throws Exception
    {
//    	System.out.println(request.getRemoteAddr());
//    	String ipname=LookIpTools.getCityname(request.getRemoteAddr());
//    	String SQL="select ltlId,LTLMEMO,LTLNAME,LCID from S_LOCATIONTYPELIST ";
//    	Vector vector=query(SQL,4);
//    	int cityid=0;
//    	if (vector!=null)
//		{
//			for (int i=0;i<vector.size();i++)
//			{
//				String[] temp=(String[])vector.elementAt(i);
//				if (ipname.indexOf(temp[2])>=0)
//				{
//					cityid=Integer.parseInt(temp[0]);
//					System.out.println("client from "+temp[2]);
//					break;
//				}
//			}
//		}
//    	return cityid;
    	return 0;
    }
    public void setUserlist(ServletContext application,String serverId)
    {
    	try
    	{
    		Map map=(Map)application.getAttribute("loginuserlist");
    		if (map==null)
    		{
    			
    		}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    public int getNums(String str,String split)
    {
    	if (str==null)
    	{
    		return 0;
    	}
    	String[] temp=str.split(split);
    	if (temp!=null)
    	{
    		return (temp.length-1);
    	}
    	return 0;
    }
    public String getSpace(String str,String split,int nums)
    {
    	String back="";
    	int len=getNums(str,split);
    	for (int i=0;i<(len*nums);i++)
    	{
    		back+="&nbsp;";
    	}
    	return back;
    }
}
