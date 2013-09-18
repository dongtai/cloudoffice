package apps.transmanager.weboffice.service.license;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.License;
import apps.transmanager.weboffice.databaseobject.Online;
import apps.transmanager.weboffice.service.dao.NormalDAO;

@Component(value=LicenseService.NAME)
public class LicenseService
{
	public final static String NAME = "licenseService";
	@Autowired
	private NormalDAO normalDao;
    private int importLicense = -1;
    

    public void setImportLicense(int s)
    {
        importLicense = s;
    }

    public int getImportLicense()
    {
        return importLicense;
    }
		
	
	public void checkInit()
	{
		List<License> list = normalDao.findAll("License");
		License license;
		if (list == null || list.size() <= 0)
		{
			license = new License();
			long ucEn = LicenseEn.encodeOUC(30);
			Calendar cal = Calendar.getInstance();
			long bud = cal.getTimeInMillis();
			cal.add(Calendar.DATE, 60);
			long date = cal.getTimeInMillis();			
			long udEn = LicenseEn.encodeOUD(date);
			long budEn = LicenseEn.encodeOUD(bud);
			String company = "evermore";
			String content = "evermore inner test";
			String com = "";
			license.setUc(ucEn);
			license.setBud(budEn);
			license.setUd(udEn);
			license.setCompany(company);
			license.setContent(content + "\n" + com);
			normalDao.save(license);
			//System.out.println("the ====  "+LicenseEn.decodeOUD(udEn)+"=="+date);
		}
		
		List<Online> onlineList = normalDao.findAll("Online");
		Online online;
		if (onlineList == null || onlineList.size() <= 0)
		{
			online = new Online();			
			online.setUc(LicenseEn.encodeOUC(1));
			normalDao.save(online);
		}
	}
	
	public int initLicense(File file)
	{
		List<License> list = normalDao.findAll("License");
		int ret = Constant.LEGAL_LICENSE;
		if (list == null || list.size() <= 0)
		{
			ret = importLicense(file, true, null);
			if (ret == Constant.LEGAL_LICENSE)
			{
				List<Online> onlineList =  normalDao.findAll("Online");
				Online online;
				if (onlineList == null || onlineList.size() <= 0)
				{
					online = new Online();			
					online.setUc(LicenseEn.encodeOUC(1));
					normalDao.save(online);
				}
			}
		}
		return ret;
	}
	
	public int importLicense(File file, String[] ret)
	{
		return importLicense(file, false, ret);
	}
	
	private int importLicense(File file, boolean newFlag, String[] retString)
	{
		RandomAccessFile read = null;
		try
		{
			read = new RandomAccessFile(file, "rw");
			long be = read.readLong();
			if (be != LicenseEn.BEGIN)
			{
				if (retString != null)
				{
					retString[0] = Constant.LICENSE_ERROR;
				}
				read.close();
				return Constant.ILLEGAL_LICENSE;
			}
			
			int bit = read.readInt();
			
			long temp = read.readLong();
			int uc = LicenseEn.decode(temp, bit);
			long ucEn = LicenseEn.encodeOUC(uc);
			
			long udEn = read.readLong();	
			
			//int ud = LicenseEn.decode(temp, bit);			
			//Calendar cal = Calendar.getInstance();
			//cal.add(Calendar.DATE, ud);
			//long date = cal.getTimeInMillis();			
			//long udEn = LicenseEn.encodeOUD(ud);
			
			String company = read.readUTF();
			String content = read.readUTF();
			String com = read.readUTF();
			read.close();
			int ret;
			if (newFlag)
			{
				ret = checkInitLicense(ucEn, udEn, company, content, com);
			}
			else
			{
				ret = checkImportLicense(ucEn, udEn, company, content, com);
			}
			if (retString != null)
			{
				if (ret == Constant.LEGAL_LICENSE)
				{
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(LicenseEn.decodeOUD(udEn));
					int tm = (cal.get(Calendar.MONTH) + 1);
					String ye = "" + cal.get(Calendar.YEAR) + "-" + tm + "-" + cal.get(Calendar.DAY_OF_MONTH);
					retString[0] = Constant.LICENSE_SUCCESS_0/*"success:License导入成功！\r\n允许最大同时在线用户数量："*/ + uc + Constant.LICENSE_SUCCESS_1/*"。\r\nLicense到期日期："*/ + ye + "。";
				}
				else
				{
					retString[0] = Constant.LICENSE_ERROR;
				}
				//System.out.println("the =============  "+retString[0]);
			}
			return ret;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if (retString != null)
			{
				retString[0] = Constant.LICENSE_ERROR;
			}
			if (read != null)
			{
				try
				{
					read.close();
				}
				catch(Exception ee)
				{
				}
			}
			return Constant.ILLEGAL_LICENSE;
		}
	}
	
	private int checkInitLicense(long ucEn, long udEn, String company, String content, String com)
	{
		License license = new License();
		Calendar cal = Calendar.getInstance();
		long bud = cal.getTimeInMillis();
		long budEn = LicenseEn.encodeOUD(bud);
		license.setUc(ucEn);
		license.setBud(budEn);
		license.setUd(udEn);
		license.setCompany(company);
		license.setContent(content + "\n" + com);
		normalDao.save(license);
		return Constant.LEGAL_LICENSE;
	}
	
	private int checkImportLicense(long ucEn, long udEn, String company, String content, String com)
	{
		try
		{
			List<License> list = normalDao.findAll("License");
			License license;
			if (list != null && list.size() > 0)
			{
				license = list.get(0);
				license.setUc(ucEn);
				license.setUd(udEn);
				license.setCompany(company);
				license.setContent(content + "\n" + com);
				normalDao.merge(license);
			}
			else
			{
				//以下是临时这样处理的，系统创建的时候没有建license记录
				license = new License();
				Calendar cal = Calendar.getInstance();
				long bud = cal.getTimeInMillis();
				long budEn = LicenseEn.encodeOUD(bud);
				license.setUc(ucEn);
				license.setBud(budEn);
				license.setUd(udEn);
				license.setCompany(company);
				license.setContent(content + "\n" + com);
				normalDao.save(license);
			}
			return Constant.LEGAL_LICENSE;
		}
		catch (Exception e)
		{
			return Constant.ILLEGAL_LICENSE;
		}
		
	}
	
	/**
	 * 
	 * @param count
	 * @return
	 */
	public int checkOnlinUser(int count)
	{
		List<License> list = normalDao.findAll("License");
		License license = null;
		if (list != null && list.size() > 0)
		{
			license = list.get(0);
		}
		if(license == null)
		{
			return Constant.ILLEGAL_LICENSE;
		}
		
		long onUser = count;
		long liceUser = LicenseEn.decodeOUC(license.getUc());
		if (onUser > liceUser)
		{
			return Constant.ONLINE_MAX_USER;
		}
		
		long bdate = LicenseEn.decodeOUD(license.getBud());
		Calendar cal = Calendar.getInstance();
		long ndate = cal.getTimeInMillis();
		if (ndate < bdate)
		{
			return Constant.LICENSE_ILLEGAL_TIME;
		}
		long date = LicenseEn.decodeOUD(license.getUd());
		if (ndate > date)
		{
			return Constant.LICENSE_END; 
		}		
		
		return Constant.ONLINE_USER_PER;
	}
	
	public int checkOnlinUser()
	{
		List<License> list = normalDao.findAll("License");
		License license = null;
		if (list != null && list.size() > 0)
		{
			license = list.get(0);
		}
		if(license == null)
		{
			return Constant.ILLEGAL_LICENSE;
		}
		List<Online> onlineList = normalDao.findAll("Online");;
		Online online = null;
		if (onlineList != null && onlineList.size() > 0)
		{
			online = onlineList.get(0);
		}
		if(online == null)
		{
			return Constant.ONLINE_USER_ILLEGAL;
		}
		long onUser = LicenseEn.decodeOUC(online.getUc());
		long liceUser = LicenseEn.decodeOUC(license.getUc());
		if (onUser > liceUser)
		{
			return Constant.ONLINE_MAX_USER;
		}
		
		long bdate = LicenseEn.decodeOUD(license.getBud());
		Calendar cal = Calendar.getInstance();
		long ndate = cal.getTimeInMillis();
		if (ndate < bdate)
		{
			return Constant.LICENSE_ILLEGAL_TIME;
		}
		long date = LicenseEn.decodeOUD(license.getUd());
		if (ndate > date)
		{
			return Constant.LICENSE_END; 
		}
		
		online.setUc(LicenseEn.encodeOUC(onUser + 1));
		normalDao.update(online);
		
		return Constant.ONLINE_USER_PER;
	}
	
	public void checkLogoutUser()
	{
		List<Online> onlineList = normalDao.findAll("Online");
		Online online = null;
		if (onlineList != null && onlineList.size() > 0)
		{
			online = onlineList.get(0);
		}
		if(online == null)
		{
			return;
		}
		long onUser = LicenseEn.decodeOUC(online.getUc()) - 1;
		if (onUser < 1)
		{
			onUser = 1;
		}
		//System.out.println("_____________________________te count is   +");
		online.setUc(LicenseEn.encodeOUC(onUser));
		normalDao.merge(online);
		
	}

	public void checkLogoutUser(int cout)
	{
		//System.out.println("=============================te count is   +"+cout);
		List<Online> onlineList = normalDao.findAll("Online");
		Online online = null;
		if (onlineList != null && onlineList.size() > 0)
		{
			online = onlineList.get(0);
		}
		if(online == null)
		{
			return;
		}
		long onUser = LicenseEn.decodeOUC(online.getUc()) - cout;
		if (onUser < 1)
		{
			onUser = 1;
		}
		online.setUc(LicenseEn.encodeOUC(onUser));
		normalDao.merge(online);
		//System.out.println("-------------------------------------e count is   +"+cout);
	}
	
}
