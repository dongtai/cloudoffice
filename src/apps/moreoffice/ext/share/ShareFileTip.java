package apps.moreoffice.ext.share;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import apps.transmanager.weboffice.databaseobject.FileLog;
import apps.transmanager.weboffice.databaseobject.Files;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.Fileinfo;

import com.mysql.jdbc.PreparedStatement;

/*
 * @author zzy
 */
public class ShareFileTip 
{
	public ShareFileTip()
	{
		try
		{
		if(QueryDb.conn != null && !(QueryDb.conn.isClosed()))
		{
		
		}
		else
		{
			QueryDb querydb = new QueryDb();
			querydb.getConn();
			
		}
		}
		catch(Exception e)
		{
			System.out.println("ShareFileTip 初始化失败！！！！！");
		}
	}
	/*
	 * 
	 */
	public ArrayList queryNewPersonShareLikeDirA(String dir)
	{
		try
		{
			
			ArrayList list = new ArrayList();
			String sql ="select distinct a.sharefile,a.permit  from newpersonshareinfo a  where  a.sharefile like \'"+dir+"%\'";
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int i=0;
			while(rs.next())
			{
				list.add(i*2+0,rs.getString(1));
				list.add(i*2+1,""+rs.getInt(2));
				i++;
			}
			rs.close();
			stmt.close();
			return list;
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryPersonShareLikeDir()====="+e.getMessage());
		}

		return null;
	}
	/*
	 * 
	 */
	public   ArrayList queryPersonShareLikeDirA(String dir)
	{
		try
		{
			
			ArrayList list = new ArrayList();
			String sql ="select distinct  a.sharefile,a.permit  from personshareinfo a  where  a.sharefile like \'"+dir+"%\'";
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int i=0;
			while(rs.next())
			{
				list.add(i*2+0,rs.getString(1));
				list.add(i*2+1,""+rs.getInt(2));
				i++;
			}
			rs.close();
			stmt.close();
			return list;
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryPersonShareLikeDir()====="+e.getMessage());
		}

		return null;
	}
	/*
	 * 
	 */
	public ArrayList queryLogLikeDir(String dir)
	{
		try
		{
			
			ArrayList list = new ArrayList();
			String sql ="select distinct a.pathinfo  from files a ,filelog b where a.fileId = b.fileId  and a.pathinfo like \'"+dir+"%\'";
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int i=0;
			while(rs.next())
			{
				list.add(rs.getString(1));				
			}
			rs.close();
			stmt.close();
			return list;
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryLogLikeDir()====="+e.getMessage());
		}

		return null;
	}
	/*
	 * 
	 */
	public void deleteFormFile(String[] pathinfo)
	{
		try
		{
			String anySql="";
			if(pathinfo != null && pathinfo.length>0)
			{
				anySql +="(";
				for(int i =0;i<pathinfo.length;i++)
				{
					anySql+="\'"+pathinfo[i]+"\'";
					if(i< (pathinfo.length-1))
					{
						anySql+=",";
					}
				}
				anySql +=")";
			}
			
			ArrayList list = new ArrayList();
			String sql ="delete from files  where  pathinfo in "+ anySql;
			Statement stmt = QueryDb.conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();

		}
		catch(Exception e)
		{
			System.out.println("sharefiletip deleteFormFile()====="+e.getMessage());
		}

	}
	/*
	 * 
	 */
	public void deleteFormLog(String[] pathinfo)
	{
		try
		{
			String anySql="";
			if(pathinfo != null && pathinfo.length>0)
			{
				anySql +="(";
				for(int i =0;i<pathinfo.length;i++)
				{
					anySql+="\'"+pathinfo[i]+"\'";
					if(i< (pathinfo.length-1))
					{
						anySql+=",";
					}
				}
				anySql +=")";
			}
			ArrayList list = new ArrayList();
			String sql ="delete from filelog  where  filelog.fileId in (select b.fileId from files b where b.pathinfo in "+anySql+ ")";
			Statement stmt = QueryDb.conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();

		}
		catch(Exception e)
		{
			System.out.println("sharefiletip deleteFormLog()====="+e.getMessage());
		}

	}
	/*
	 * 
	 */
	public ArrayList queryLogbyUIDA(String  pathinfo,long uid)
	{
		try
		{
			
			ArrayList list = new ArrayList();
			String sql ="select a.pathinfo,b.optime,c.realname,c.username,c.realEmail,b.optype,b.opScript,b.opresult from files a ,filelog b,users c where a.fileId = b.fileId and b.uid = c.id and b.uid <> "+uid+" and a.pathinfo =\'"+pathinfo+"\'";
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int i=0;
			int len = 6;
			while(rs.next())
			{
				list.add(i*len+0,rs.getString(1));
//				Date logdate = (Date)rs.getDate(2);
				
//				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				String logdate1=sdf.format(logdate.getTime());
				
				list.add(i*len+1,rs.getString(2));
				list.add(i*len+2,rs.getString(3));
				list.add(i*len+3,rs.getString(4));
				list.add(i*len+4,rs.getString(5));
				list.add(i*len+5,""+rs.getInt(6));
//				list.add(i*len+6,rs.getString(7));
//				list.add(i*len+7,""+rs.getInt(8));
				i++;
			}
			rs.close();
			stmt.close();
			return list;
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryLog()====="+e.getMessage());
		}
		return null;
	}
	/*
	 * 
	 */
	public ArrayList queryLog(String  pathinfo)
	{
		try
		{
			
			ArrayList list = new ArrayList();
			String sql ="select a.pathinfo,b.optime,c.realname,c.username,c.realemail,b.optype from files a ,filelog b,users c where a.fileId = b.fileId and b.uid = c.id and a.pathinfo =\'"+pathinfo+"\'";
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int i=0;
			while(rs.next())
			{
				list.add(i*6+0,rs.getString(1));
//				Date logdate = (Date)rs.getDate(2);
				
//				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				String logdate1=sdf.format(logdate.getTime());
				
				list.add(i*6+1,rs.getString(2));
				list.add(i*6+2,rs.getString(3));
				list.add(i*6+3,rs.getString(4));
				list.add(i*6+4,rs.getString(5));
				list.add(i*6+5,""+rs.getInt(6));
				i++;
			}
			rs.close();
			stmt.close();
			return list;
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryLog()====="+e.getMessage());
		}
		return null;
	}
	/*
	 * 
	 */
	public ArrayList queryLogArrA(String[] pathinfo)
	{
		try
		{
			String anySql="";
			if(pathinfo != null && pathinfo.length>0)
			{
				anySql +="(";
				for(int i =0;i<pathinfo.length;i++)
				{
					anySql+="\'"+pathinfo[i]+"\'";
					if(i< (pathinfo.length-1))
					{
						anySql+=",";
					}
				}
				anySql +=")";
			}
			ArrayList list = new ArrayList();
			String sql ="select a.pathinfo,b.optime,c.realname,c.username,c.realemail from files a ,filelog b,users c where a.fileId = b.fileId and b.uid = c.id and a.pathinfo in "+anySql;
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				Object[] log1= new Object[5];
				log1[0]= rs.getString(1);
				log1[1] = rs.getDate(2);
				log1[2]=rs.getString(3);
				log1[3]= rs.getString(4);
				log1[4] = rs.getString(5);
				 list.add(log1);
			}
			rs.close();
			stmt.close();
//			QueryDb.conn.close();
			return list;
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryLog()====="+e.getMessage());
		}
//		try
//		{
//		if(QueryDb.conn != null)
//		{
//			QueryDb.conn.close();
//		}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		return null;
	}
	
	/*
	 * 
	 */
	public void insertFileListLog(ArrayList fileId,long uid,int optype)
	{
		if(fileId != null && fileId.size()>0)
		{
		try
		{

		QueryDb.conn.setAutoCommit(false); 
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
		TimeZone t = sdf.getTimeZone(); 
		t.setRawOffset(0); 
		sdf.setTimeZone(t); 
		Long startTime = System.currentTimeMillis(); 
		String sql = "insert into filelog(fileid,uid,optype,srcfileid,optime) values(?,?,?,?,now())";		
		PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
//		pst.setLong(1,log.getFlogId());
		for(int i=0;i<fileId.size();i++)
		{
		pst.setLong(1,(Long)fileId.get(i));
		pst.setLong(2,uid);
		 sdf=new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
//		String logdate1=sdf.format(new java.sql.Data(System.currentTimeMillis()));
		
//		pst.setString(3,"now()");
		pst.setInt(3,optype);
		
		pst.setLong(4,(Long)fileId.get(i));
		
		pst.addBatch();
		}
		pst.executeBatch();
		QueryDb.conn.commit(); 
//		QueryDb.conn.close();
		Long endTime = System.currentTimeMillis(); 
		System.out.println("用时：" + sdf.format(new Date(endTime - startTime)));  
		}
		catch(Exception e)
		{
			
			System.out.println("sharefiletip insertFilelog====="+e.getMessage());
		}
		}

	}
	
	public void insertFileListLog(ArrayList fileId,long uid,int optype,String opresult,String opScript)
    {
        if(fileId != null && fileId.size()>0)
        {
        try
        {

        QueryDb.conn.setAutoCommit(false); 
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
        TimeZone t = sdf.getTimeZone(); 
        t.setRawOffset(0); 
        sdf.setTimeZone(t); 
        Long startTime = System.currentTimeMillis(); 
        String sql = "insert into filelog(fileid,uid,optype,srcfileid,opresult,opScript,optime) values(?,?,?,?,?,?,now())";     
        PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
//      pst.setLong(1,log.getFlogId());
        for(int i=0;i<fileId.size();i++)
        {
        pst.setLong(1,(Long)fileId.get(i));
        pst.setLong(2,uid);
         sdf=new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
//      String logdate1=sdf.format(new java.sql.Data(System.currentTimeMillis()));
        
//      pst.setString(3,"now()");
        pst.setInt(3,optype);
        
        pst.setLong(4,(Long)fileId.get(i));
        pst.setString(5, opresult);
        pst.setString(6,opScript);
        pst.addBatch();
        }
        pst.executeBatch();
        QueryDb.conn.commit(); 
//      QueryDb.conn.close();
        Long endTime = System.currentTimeMillis(); 
        //System.out.println("用时：" + sdf.format(new Date(endTime - startTime)));  
        }
        catch(Exception e)
        {
            
            System.out.println("sharefiletip insertFilelog====="+e.getMessage());
        }
        }

    }
	
	public void insertFileLog(long fileId,long uid,int optype)
	{
		insertFileLog(fileId,uid,optype,-100l);
	}
	
	/*
	 * 
	 */
	public void insertFileLog(long fileId,long uid,int optype,long srcfileid)
	{
		try
		{

		QueryDb.conn.setAutoCommit(false); 
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
		TimeZone t = sdf.getTimeZone(); 
		t.setRawOffset(0); 
		sdf.setTimeZone(t); 
		Long startTime = System.currentTimeMillis(); 
		String sql = "insert into filelog(fileid,uid,optype,srcfileid,optime) values(?,?,?,?,now())";		
		PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
//		pst.setLong(1,log.getFlogId());
		pst.setLong(1,fileId);
		pst.setLong(2,uid);
		 sdf=new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
//		String logdate1=sdf.format(new java.sql.Data(System.currentTimeMillis()));
		
//		pst.setString(3,"now()");
		pst.setInt(3,optype);
//		if(srcfileid == -100)
//		{			
//		}
//		else
//		{
			pst.setLong(4,srcfileid);
//		}
		pst.execute();
		QueryDb.conn.commit(); 
//		QueryDb.conn.close();
		Long endTime = System.currentTimeMillis(); 
		System.out.println("用时：" + sdf.format(new Date(endTime - startTime)));  
		}
		catch(Exception e)
		{
			
			System.out.println("sharefiletip insertFilelog====="+e.getMessage());
		}
//		try
//		{
//			if(!QueryDb.conn.isClosed())
//			{
//				QueryDb.conn.close();
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}
	public Long queryUserinfoID(String realname)
	{
		try
		{
			String sql ="select id from users a  where a.realname =\'"+realname+"\'  or a.username=\'"+realname+"\'";
			System.out.println("queryUserinfoID========"+sql);
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			System.out.println("queryUserinfoID11111111111========"+sql);
			if(rs.next())
			{
				
				System.out.println("queryUserinfoID========"+rs.getLong(1));
				return rs.getLong(1);
			}
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryUserinfoID()====="+e.getMessage());
		}
		return null;
	}
	/*
	 * 
	 */
	public ArrayList queryFilearrID(String[] pathinfo)
	{
		String anySql="";
		if(pathinfo != null && pathinfo.length>0)
		{
			anySql +="(";
			for(int i =0;i<pathinfo.length;i++)
			{
				anySql+="\'"+pathinfo[i]+"\'";
				if(i< (pathinfo.length-1))
				{
					anySql+=",";
				}
			}
			anySql +=")";
		}
		
		ArrayList list = new ArrayList();	
		
		try
		{
			String sql ="select fileId,pathinfo from files  where  pathinfo in "+ anySql;
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				String[] temp = new String[2];
				temp[0]=""+rs.getLong(1);
				temp[1]=rs.getString(2);
				list.add(temp);
				
			}
			return list;
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryFilearrID()====="+e.getMessage());
		}
		return null;
	}
	/*
	 * 
	 */
	public ArrayList queryFileListID(String[] pathinfo)
	{
		String anySql="";
		if(pathinfo != null && pathinfo.length>0)
		{
			anySql +="(";
			for(int i =0;i<pathinfo.length;i++)
			{
				anySql+="\'"+pathinfo[i]+"\'";
				if(i< (pathinfo.length-1))
				{
					anySql+=",";
				}
			}
			anySql +=")";
		}
		
		ArrayList list = new ArrayList();	
		
		try
		{
			String sql ="select fileId  from files  where pathinfo in "+ anySql;
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
//				String[] temp = new String[2];
//				temp[0]=""+rs.getLong(1);
//				temp[1]=rs.getString(2);
//				list.add(temp);
				list.add(rs.getLong(1));
			}
			return list;
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryFilearrID()====="+e.getMessage());
		}
		return null;
	}
	/*
	 * 
	 */
	public Long queryFileID(String pathinfo)
	{
		try
		{
			String sql ="select a.fileId from files a  where a.pathinfo =\'"+pathinfo+"\' ";
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())
			{
				return rs.getLong(1);
			}
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryFileByFN()====="+e.getMessage());
		}
		return null;
	}
	/*
	 * 
	 */
	public ArrayList queryFileByFNarrA(String[] pathinfo)
	{
		ArrayList list=null;
		int len=0;
		if(pathinfo != null &&(len=pathinfo.length)>0)
		{
		try
		{
			
			QueryDb.conn.setAutoCommit(false); 

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
			TimeZone t = sdf.getTimeZone(); 
			t.setRawOffset(0); 
			sdf.setTimeZone(t); 
			Long startTime = System.currentTimeMillis(); 
			String sql ="select * from files a  where a.pathinfo =?";//\'"+pathinfo+"\' ";
			PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
			for(int i=0;i<len;i++)
			{
				Fileinfo fileinfo = (Fileinfo)list.get(i);
				pst.setString(1,fileinfo.getFileName());
			    pst.setString(2,fileinfo.getLinkAddress());
			    pst.setString(3,fileinfo.getPathInfo());
			    pst.setString(4,fileinfo.getImageUrl());
				pst.setString(5,fileinfo.getPrimalPath());
				pst.setString(6, fileinfo.getShowPath());
				pst.setLong(7,fileinfo.getFileId());
				
				// 把一个SQL命令加入命令列表 
				pst.addBatch(); 
			} 
			// 执行批量更新 
			pst.executeBatch(); 
			// 语句执行完毕，提交本事务 
			QueryDb.conn.commit(); 
	
			Long endTime = System.currentTimeMillis(); 
			System.out.println("用时：" + sdf.format(new Date(endTime - startTime))); 
	
			pst.close(); 
			
			
			
			
//			Statement stmt = QueryDb.conn.createStatement();
//			ResultSet rs = stmt.executeQuery(sql);
//			boolean flag= false;
//			if(rs.next())
//			{
//				flag= true;
//			}
//			rs.close();
//			stmt.close();
//			QueryDb.conn.close();
			return list;
			
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryFileByFN()====="+e.getMessage());
		}
		}
		return list;
	}
	/*
	 * 
	 */
	public boolean queryFileByFN(String pathinfo,long uid)
	{
		try
		{
			String sql ="select * from files a  where a.pathinfo =\'"+pathinfo+"\' ";
//			System.out.println("sharefiletip queryFileByFN()====="+sql);
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			boolean flag= false;
			if(rs.next())
			{
				flag= true;
			}
			rs.close();
			stmt.close();
//			QueryDb.conn.close();
			return flag;
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryFileByFN()====="+e.getMessage());
		}
		return false;
	}
	public static ArrayList queryFlogByFileIdA(long fid)
	{
		ArrayList list = null;
		try
		{
			list = new ArrayList();
			String sql ="select * from filelog a ,files b,users c where a.fileid = b.fileid and a.uid = c.id and a.fileId ="+fid;
			Statement stmt = QueryDb.conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				FileLog log = new FileLog();
				Long flogid = rs.getLong("flogId");
				long fileId = rs.getLong("fileId");
				Long uid = rs.getLong("uid");
				Date optime = rs.getDate("optime");
				Integer optype = rs.getInt("optype");
				log.setFlogId(flogid);
				log.setFileId(fileId);
				log.setUid(uid);
				log.setOpTime(optime);
				log.setOpType(optype);
				//设置文件值
				Files file = new Files();
				file.setFileId(fileId);
				file.setFileName(rs.getString("filename"));
				//String filename = rs.getString("filename");				
				file.setDescription( rs.getString("description"));
				file.setFiletype(rs.getString("filetype"));
				file.setKeywords(rs.getString("keywords"));
				file.setStatus(rs.getInt("status"));
				file.setFileSize(rs.getLong("filesize"));
				file.setCreateTime(rs.getDate("createTime"));
				file.setLastedTime( rs.getDate("lastedTime"));
				file.setDeletedTime(rs.getDate("deletedTime"));
				file.setLinkAddress( rs.getString("linkAddress"));
				file.setPathInfo(rs.getString("pathInfo"));
				file.setTitle(rs.getString("title"));
				Integer isFold = rs.getInt("isFold");
				if(isFold.intValue() ==0)
				{
					file.setIsFold(false);
				}
				else
				{
					file.setIsFold(true);
				}
				//file.setIsFold(rs.getInt("isFold"));
				file.setImageTitle(rs.getString("imageTitle"));
				file.setImageUrl( rs.getString("imageUrl"));  
				file.setPermit( rs.getInt("permit"));
				file.setUserLock(rs.getString("userLock")); 
				Integer isShared = rs.getInt("isShared");
				if(isShared.intValue() ==0)
				{
					file.setIsShared(false);
				}
				else
				{
					file.setIsShared(true);
				}
				
				file.setPrimalPath( rs.getString("primalPath")); 
				Integer isChild = rs.getInt("isChild");
				if(isChild.intValue() == 0)
				{
					file.setIsChild(false);
				}
				else
				{
					file.setIsChild(true);
				}
				file.setShowPath(rs.getString("showPath")); 
				file.setImportant(rs.getInt("important"));
				file.setIsNew( rs.getInt("isNew"));
				file.setSharecomment( rs.getString("sharecomment"));
				//设置文件值结束
				
				//设置用户信息
				Users user = new Users();
				user.setId(uid);
				
				user.setUserName(rs.getString("userName"));
				user.setPassW(rs.getString("passW"));
				//user.setEmail( rs.getString("email"));
				user.setRealName(rs.getString("realName"));
				user.setRealEmail( rs.getString("Realemail"));
				//user.setDepartment(rs.getString("department"));
				 user.setDuty( rs.getString("duty"));
				user.setImage( rs.getString("image")) ;
				//user.setClass_( rs.getString("class"));
				user.setMyoption(rs.getInt("myoption"));
				user.setStorageSize(rs.getFloat("storageSize"));
				user.setRole(rs.getShort("role"));
				user.setCompanyId(rs.getString("companyID"));
//				user.setOpentype(rs.getInt("opentype"));
				user.setValidate( rs.getShort("validate_"));
				user.setMobile(rs.getString("mobile"));
				user.setCompanyName( rs.getString("companyName"));
				user.setFax(rs.getString("fax"));
				user.setPhone(rs.getString("phone"));
				user.setPostcode(rs.getString("postcode"));
				user.setAddress( rs.getString("address"));
				user.setUruid(rs.getString("uruid"));
				user.setLoginType(rs.getInt("loginType"));	
				 System.out.println("realname::-------");
				 
				 //设置用户信息结束
				 log.setFileinfo(file);
				 log.setUserinfo(user);
				 list.add(log);
			}

			rs.close();
			stmt.close();
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryFileLogByUid()====="+e.getMessage());
		}
		return list;
	}
	/*
	 * 
	 */
	public void queryFlogByUidA(long uid)
	{
		try
		{
			//String sql ="select "
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip queryFileLogByUid()====="+e.getMessage());
		}
	}
	/*
	 *批量 插入文件日志；用户查看记录
	 */
	public static void batchFilelogA(ArrayList list)
	{
		int len =0;
		if(list != null && ((len=list.size())>0))
		{
		try
		{
		
		// 关闭事务自动提交 
		QueryDb.conn.setAutoCommit(false); 

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
		TimeZone t = sdf.getTimeZone(); 
		t.setRawOffset(0); 
		sdf.setTimeZone(t); 
		Long startTime = System.currentTimeMillis(); 
		String sql = "insert into filelog(fileid,uid,optime,optype) values(?,?,?,?)";
//		Statement stmt = (Statement)QueryDb.conn.createStatement();
		
		PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
	
		for (int i = 0; i < len; i++) 
		{ 
			FileLog log = (FileLog)list.get(i);
//			pst.setLong(1,log.getFlogId());
			pst.setLong(1,log.getFileId());
			pst.setLong(2,log.getUid());
			pst.setDate(3,new java.sql.Date(log.getOpTime().getTime()));
			pst.setInt(4,log.getOpType());	
			pst.addBatch(); 
		} 
//		 执行批量更新 
		pst.executeBatch(); 
		// 语句执行完毕，提交本事务 
		QueryDb.conn.commit(); 
		Long endTime = System.currentTimeMillis(); 
		System.out.println("用时：" + sdf.format(new Date(endTime - startTime))); 
		pst.close(); 
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip batchFilelog====="+e.getMessage());
		}
		}
	}
	/*
	 * 插入文件日志；用户查看记录
	 */
	public void insertFilelogA(FileLog log)
	{
		try
		{

		QueryDb.conn.setAutoCommit(false); 
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
		TimeZone t = sdf.getTimeZone(); 
		t.setRawOffset(0); 
		sdf.setTimeZone(t); 
		Long startTime = System.currentTimeMillis(); 
		String sql = "insert into filelog(fileid,uid,optime,optype) values(?,?,?,?)";		
		PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
//		pst.setLong(1,log.getFlogId());
		pst.setLong(1,log.getFileId());
		pst.setLong(2,log.getUid());
		pst.setDate(3,new java.sql.Date(log.getOpTime().getTime()));
		pst.setInt(4,log.getOpType());
		pst.execute();
		QueryDb.conn.commit(); 
		Long endTime = System.currentTimeMillis(); 
		System.out.println("用时：" + sdf.format(new Date(endTime - startTime)));  
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip insertFilelog====="+e.getMessage());
		}
	}

	/*
	  * 更新文件，当用户移动、重命名文件时需要更新
	  */
	public static void updateFileArrA(ArrayList list)
	{
		int len = list.size();
		if(list != null && len >0)
		{
			try{
			QueryDb.conn.setAutoCommit(false); 
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
			TimeZone t = sdf.getTimeZone(); 
			t.setRawOffset(0); 
			sdf.setTimeZone(t); 
			Long startTime = System.currentTimeMillis(); 
			String sql = "update files set filename =?,linkaddress=?,pathinfo=?,imageurl=?,primalpath=?,showpath=? where fileId=?";
			PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
			for(int i=0;i<len;i++)
			{
				Fileinfo fileinfo = (Fileinfo)list.get(i);
				pst.setString(1,fileinfo.getFileName());
			    pst.setString(2,fileinfo.getLinkAddress());
			    pst.setString(3,fileinfo.getPathInfo());
			    pst.setString(4,fileinfo.getImageUrl());
				pst.setString(5,fileinfo.getPrimalPath());
				pst.setString(6, fileinfo.getShowPath());
				pst.setLong(7,fileinfo.getFileId());
				
				// 把一个SQL命令加入命令列表 
				pst.addBatch(); 
			} 
			// 执行批量更新 
			pst.executeBatch(); 
			// 语句执行完毕，提交本事务 
			QueryDb.conn.commit(); 
	
			Long endTime = System.currentTimeMillis(); 
			System.out.println("用时：" + sdf.format(new Date(endTime - startTime))); 
	
			pst.close(); 
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip updateFileArr====="+e.getMessage());
		}
		}
		
	}
	/*
	  * 
	  */
	public void updateFileArrForRename(ArrayList list)
	{
		if(list != null && list.size()>0)
		{
			int len = list.size();
		String sql ="update files set pathinfo=? where pathinfo=?";
		try
		{
			QueryDb.conn.setAutoCommit(false);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
			TimeZone t = sdf.getTimeZone(); 
			t.setRawOffset(0); 
			sdf.setTimeZone(t); 
			Long startTime = System.currentTimeMillis(); 
			PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
			for (int i = 0; i < len; i++) 
			{ 
				String[] temp =(String[]) list.get(i);
				pst.setString(1,(String)temp[0]);
				pst.setString(2,(String)temp[1]);
				pst.addBatch(); 
			} 
			// 执行批量更新 
			pst.executeBatch(); 
			// 语句执行完毕，提交本事务 
			QueryDb.conn.commit(); 
	
			Long endTime = System.currentTimeMillis(); 
			System.out.println("用时：" + sdf.format(new Date(endTime - startTime))); 
	
			pst.close(); 		
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip updateFileArrForRename====="+e.getMessage());
		}
		}
	}
	/*
	  * 
	  */
	public void updateFileForRename(String oldName,String newName)
	{
		String sql ="update files set pathinfo=\'"+newName+"\' where pathinfo=\'"+oldName+"\'";
		try
		{
		
		Statement stmt = QueryDb.conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip updateFileForRename====="+e.getMessage());
		}
	}
	 /*
	  * 
	  */
	public void updateFileInfoA(long fileId,String newName)
	{
		String sql ="update files set pathinfo=\'"+newName+"\' where fileId="+fileId;
		try
		{
		
		Statement stmt = QueryDb.conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
		}
		catch(Exception e)
		{
			System.out.println("sharefiletip insertFileArray====="+e.getMessage());
		}
	}
	/*
	 * 插入文件信息到file表中，当用户上传，新建，拷贝文件时要用到
	 */
	public static void insertFileArrayA(ArrayList<Fileinfo> list,long uid)
	{
		if(list != null  && list.size()>0)
		{
			int len = list.size();
			try
			{
			
			// 关闭事务自动提交 
			QueryDb.conn.setAutoCommit(false); 
	
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
			TimeZone t = sdf.getTimeZone(); 
			t.setRawOffset(0); 
			sdf.setTimeZone(t); 
			Long startTime = System.currentTimeMillis(); 
			String sql = "insert into files(fileName,keywords,status,fileSize,createTime," +
			"lastedTime,deletedTime,linkAddress,pathInfo,title,imageTitle,imageUrl," +
			"permit,userLock,primalPath,showPath,important,isNew,uid,isFold,isShared,sharecomment,ischild,author) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
			for (int i = 0; i < len; i++) 
			{ 
				Fileinfo fileinfo = (Fileinfo)list.get(i);
//				pst.setLong(1,fileinfo.getFileId());
				String filename = fileinfo.getFileName();
			     pst.setString(1, filename); 
			     if(fileinfo.getKeyWords() ==null)
			     {
			    	 pst.setString(2,"");
			     }
			     else
			     {
			    	 pst.setString(2,fileinfo.getKeyWords());
			     }
			     if(fileinfo.getStatus() ==null)
			     {
			    	 pst.setInt(3,0);
			     }
			     else
			     {
			     pst.setInt(3,fileinfo.getStatus());
			     }
			     
			     pst.setLong(4,fileinfo.getFileSize());
			     if(fileinfo.getCreateTime() ==null)
			     {
			    	 pst.setDate(5, new java.sql.Date(System.currentTimeMillis()));
			     }
			     else
			     {
			     pst.setDate(5,new java.sql.Date(fileinfo.getCreateTime().getTime()));
			     }
			     if(fileinfo.getLastedTime() == null)
			     {
			    	 pst.setDate(6,new java.sql.Date(System.currentTimeMillis()));
			     }
			     else
			     {
			     pst.setDate(6,new java.sql.Date(fileinfo.getLastedTime().getTime()));
			     }
			     if(fileinfo.getDeletedTime() == null)
			     {
			    	 pst.setDate(7,new java.sql.Date(System.currentTimeMillis()));
			     }
			     else
			     {
			     pst.setDate(7,new java.sql.Date(fileinfo.getDeletedTime().getTime()));
			     }
			     if(fileinfo.getLinkAddress()==null)
			     {
			    	 pst.setString(8,"");
			     }
			     else
			     {
			     pst.setString(8,fileinfo.getLinkAddress());
			     }
			     
//			     
			     if(fileinfo.getPathInfo()==null)
			     {
			    	 pst.setString(9,"");
			     }
			     else
			     {
			     pst.setString(9,fileinfo.getPathInfo());
			     }
			     if(fileinfo.getTitle()==null)
			     {
			    	 pst.setString(10,"");
			     }
			     else
			     {
			   	 pst.setString(10,fileinfo.getTitle());
			     }
			     if(fileinfo.getImageTitle()== null)
			     {
			    	 pst.setString(11,"");
			     }
			     else
			     {
			     pst.setString(11,fileinfo.getImageTitle());
			     }
			     if(fileinfo.getImageUrl()==null)
			     {
			    	 pst.setString(12,"");
			     }
			     else
			     {
			     pst.setString(12,fileinfo.getImageUrl());
			     }	     
			     pst.setInt(13,fileinfo.getPermit());
			    if(fileinfo.getUserLock()==null)
			    {
			    	pst.setString(14,"");
			    }
			    else
			    {
			     pst.setString(14,fileinfo.getUserLock());		
			    }
			    if(fileinfo.getPrimalPath()== null)
			    {
			    	pst.setString(15,"");
			    }
			    else
			    {
			     pst.setString(15,fileinfo.getPrimalPath());
			    }
			    if(fileinfo.getShowPath()== null)
			    {
			    	pst.setString(16,"");
			    }
			    else
			    {
			     pst.setString(16,fileinfo.getShowPath());
			    }
			    
			     pst.setLong(17,fileinfo.getImportant());
			     pst.setInt(18,fileinfo.getIsNew());
			     pst.setLong(19,uid);
				if(filename.indexOf(".") == -1)
				{
					pst.setInt(20, 1);//1表示文件夹；0表示文件
				}
				else 
				{
					pst.setInt(20, 0);
				}
				if(fileinfo.isShared())
				{
					pst.setInt(21,1);
				}
				else
				{
					pst.setInt(21,0);
				}
				String shareComment="";
				if(fileinfo.getShareCommet()!=null)
				{
					shareComment = fileinfo.getShareCommet();
				}
				
					pst.setString(22,shareComment);
				
					
				boolean ischild = fileinfo.isChild();
				int childvalue =0;
				if(ischild)
				{
					childvalue=1;
				}
				pst.setInt(23,childvalue);
				if(fileinfo.getAuthor() != null)
				{
					pst.setString(24,fileinfo.getAuthor());
				}
				else
				{
					pst.setString(24,"");
				}
				// 把一个SQL命令加入命令列表 
				pst.addBatch(); 
			} 
			// 执行批量更新 
			pst.executeBatch(); 
			// 语句执行完毕，提交本事务 
			QueryDb.conn.commit(); 
	
			Long endTime = System.currentTimeMillis(); 
			System.out.println("用时：" + sdf.format(new Date(endTime - startTime))); 
	
			pst.close(); 			
			}
			catch(Exception e)
			{
				System.out.println("sharefiletip insertFileArray====="+e.getMessage());
			}
		}
		

	}
	public  ArrayList notExistFile(String[] pathinfo)
	{
		if(pathinfo != null && pathinfo.length>0)
		{
			try
			{
				String anySql="(";
				
					
					for(int i =0;i<pathinfo.length;i++)
					{
						anySql+="\'"+pathinfo[i]+"\'";
						if(i< (pathinfo.length-1))
						{
							anySql+=",";
						}
					}
					anySql +=")";
				
				
				ArrayList list = new ArrayList();
				String sql ="select pathinfo from files  where  pathinfo  in "+ anySql;
				Statement stmt = QueryDb.conn.createStatement();
//				stmt.executeUpdate(sql);
				ResultSet rs = stmt.executeQuery(sql);
//				boolean isexit=false;
				ArrayList exitList = new ArrayList();
				while(rs.next())
				{
//					isexit=true;
					String path = rs.getString(1);
//					boolean exit = false;
					for(int i=0;i<pathinfo.length;i++)
					{
						if(pathinfo[i].equals(path));
						{
//							exit= true;
							exitList.add(path);
							break;
						}
					}
//					if(!exit)
//					{
//						list.add(path);
//					}
					
				}
				/*
				 * 优化，根据将列表中减去存在的
				 */
				for(int i=0;i<pathinfo.length;i++)
				{
					boolean exit = false;
					if(exitList != null && exitList.size()>0)
					{
						for(int j=0;j<exitList.size();j++)
						{
							
							if(pathinfo[i].equals(exitList.get(j)))
							{
								exit= true;
//								exitList.add(path);
								break;
							}
							
						}
					}
					if(!exit)
					{
						list.add(pathinfo[i]);
					}
//					list.add(pathinfo[i]);
				}
//				/*
//				 * 不存在file表中，执行此步
//				 */
//				if(!isexit)
//				{
//					for(int i=0;i<pathinfo.length;i++)
//					{
//						list.add(pathinfo[i]);
//					}
//				}
				rs.close();
				stmt.close();
				return list;

			}
			catch(Exception e)
			{
				System.out.println("sharefiletip notExistFile(String[] pathinfo)====="+e.getMessage());
			}
		}
		return null;
	}
	
	/*
	 * 
	 */
	public   void insertFileListinfo(String[] pathinfo,long uid)
	{
		if(pathinfo != null && pathinfo.length>0)
		{
			try
			{
				ArrayList list = notExistFile(pathinfo);
				String[] tempinfo = null;
				if(list!= null && list.size()>0)
				{
					tempinfo = new String[list.size()];
					for(int i=0;i<list.size();i++)
					{
						tempinfo[i]=(String)list.get(i);
					}
				}
				
				if(tempinfo != null && tempinfo.length>0)
				{
					
					// 关闭事务自动提交 
					QueryDb.conn.setAutoCommit(false); 
			
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
					TimeZone t = sdf.getTimeZone(); 
					t.setRawOffset(0); 
					sdf.setTimeZone(t); 
					Long startTime = System.currentTimeMillis(); 
					String sql = "insert into files(pathinfo,uid) values(?,?)";// where file.pathinfo not in (select pathinfo from file)";
					PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
					for(int i = 0;i<tempinfo.length;i++)
					{
						pst.setString(1,tempinfo[i]);
						pst.setLong(2,uid);
						pst.addBatch(); 
					} 
					// 执行批量更新 
					pst.executeBatch(); 
					// 语句执行完毕，提交本事务 
					QueryDb.conn.commit(); 			
					Long endTime = System.currentTimeMillis(); 
					System.out.println("用时：" + sdf.format(new Date(endTime - startTime))); 
			
					pst.close(); 
				}
			}
			catch(Exception e)
			{
				System.out.println("sharefiletip insertFileinfo(String[] pathinfo)====="+e.getMessage());
			}
		}
	}
	/*
	 * 
	 */
	public void insertFileinfo(Fileinfo fileinfo,long uid)
	{
		
		if(fileinfo != null)
		{
			try
			{
			
			// 关闭事务自动提交 
			QueryDb.conn.setAutoCommit(false); 
	
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS"); 
			TimeZone t = sdf.getTimeZone(); 
			t.setRawOffset(0); 
			sdf.setTimeZone(t); 
			Long startTime = System.currentTimeMillis(); 
			String sql = "insert into files(fileName,keywords,status,fileSize,createTime," +
			"lastedTime,deletedTime,linkAddress,pathInfo,title,imageTitle,imageUrl," +
			"permit,userLock,primalPath,showPath,important,isNew,uid,isFold,isShared,sharecomment,ischild,author) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pst = (PreparedStatement) QueryDb.conn.prepareStatement(sql); 
			
				String filename = fileinfo.getFileName();
			     pst.setString(1, filename); 
			     if(fileinfo.getKeyWords() ==null)
			     {
			    	 pst.setString(2,"");
			     }
			     else
			     {
			    	 pst.setString(2,fileinfo.getKeyWords());
			     }
			     if(fileinfo.getStatus() ==null)
			     {
			    	 pst.setInt(3,0);
			     }
			     else
			     {
			     pst.setInt(3,fileinfo.getStatus());
			     }
			     if(fileinfo.getFileSize()== null)
			     {
			    	 pst.setLong(4,0l);
			     }
			     else
			     {
			     pst.setLong(4,fileinfo.getFileSize());
			     }
			     if(fileinfo.getCreateTime() ==null)
			     {
			    	 pst.setDate(5, new java.sql.Date(System.currentTimeMillis()));
			     }
			     else
			     {
			     pst.setDate(5,new java.sql.Date(fileinfo.getCreateTime().getTime()));
			     }
			     if(fileinfo.getLastedTime() == null)
			     {
			    	 pst.setDate(6,new java.sql.Date(System.currentTimeMillis()));
			     }
			     else
			     {
			     pst.setDate(6,new java.sql.Date(fileinfo.getLastedTime().getTime()));
			     }
			     if(fileinfo.getDeletedTime() == null)
			     {
			    	 pst.setDate(7,new java.sql.Date(System.currentTimeMillis()));
			     }
			     else
			     {
			     pst.setDate(7,new java.sql.Date(fileinfo.getDeletedTime().getTime()));
			     }
			     if(fileinfo.getLinkAddress()==null)
			     {
			    	 pst.setString(8,"");
			     }
			     else
			     {
			     pst.setString(8,fileinfo.getLinkAddress());
			     }
			     
//			     
			     if(fileinfo.getPathInfo()==null)
			     {
			    	 pst.setString(9,"");
			     }
			     else
			     {
			     pst.setString(9,fileinfo.getPathInfo());
			     }
			     if(fileinfo.getTitle()==null)
			     {
			    	 pst.setString(10,"");
			     }
			     else
			     {
			   	 pst.setString(10,fileinfo.getTitle());
			     }
			     if(fileinfo.getImageTitle()== null)
			     {
			    	 pst.setString(11,"");
			     }
			     else
			     {
			     pst.setString(11,fileinfo.getImageTitle());
			     }
			     if(fileinfo.getImageUrl()==null)
			     {
			    	 pst.setString(12,"");
			     }
			     else
			     {
			     pst.setString(12,fileinfo.getImageUrl());
			     }	     
			     pst.setInt(13,fileinfo.getPermit());
			    if(fileinfo.getUserLock()==null)
			    {
			    	pst.setString(14,"");
			    }
			    else
			    {
			     pst.setString(14,fileinfo.getUserLock());		
			    }
			    if(fileinfo.getPrimalPath()== null)
			    {
			    	pst.setString(15,"");
			    }
			    else
			    {
			     pst.setString(15,fileinfo.getPrimalPath());
			    }
			    if(fileinfo.getShowPath()== null)
			    {
			    	pst.setString(16,"");
			    }
			    else
			    {
			     pst.setString(16,fileinfo.getShowPath());
			    }
			    
			     pst.setLong(17,fileinfo.getImportant());
			     pst.setInt(18,fileinfo.getIsNew());
			     pst.setLong(19,uid);
				if(filename.indexOf(".") == -1)
				{
					pst.setInt(20, 1);//1表示文件夹；0表示文件
				}
				else 
				{
					pst.setInt(20, 0);
				}
				if(fileinfo.isShared())
				{
					pst.setInt(21,1);
				}
				else
				{
					pst.setInt(21,0);
				}
				String shareComment="";
				if(fileinfo.getShareCommet()!=null)
				{
					shareComment = fileinfo.getShareCommet();
				}
				
					pst.setString(22,shareComment);
				
					
				boolean ischild = fileinfo.isChild();
				int childvalue =0;
				if(ischild)
				{
					childvalue=1;
				}
				pst.setInt(23,childvalue);
				if(fileinfo.getAuthor() != null)
				{
					pst.setString(24,fileinfo.getAuthor());
				}
				else
				{
					pst.setString(24,"");
				}
			 pst.execute();
			
			QueryDb.conn.commit(); 
	
			Long endTime = System.currentTimeMillis(); 
			System.out.println("用时：" + sdf.format(new Date(endTime - startTime))); 
	
			pst.close(); 			
			}
			catch(Exception e)
			{
				System.out.println("sharefiletip insertFileArray====="+e.getMessage());
			}
		}
	}
   
	public static void main(String[] args)
	{
//		ShareFileTip queryTip=new ShareFileTip();
//		ArrayList pathinfo= new ArrayList();
//		pathinfo.add(16l);
//		pathinfo.add(17l);
//		queryTip.insertFileListLog(pathinfo,2l,16);
	}
	
}
