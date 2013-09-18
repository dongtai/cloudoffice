package apps.transmanager.weboffice.service.objects;

import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import apps.transmanager.weboffice.client.constant.WebofficeUtility;
import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.domain.Fileinfo;

/**
 * TODO: 文件注释
 * <p>
 * <p>
 * EIO版本:        EIO Office V1.3
 * <p>
 * 作者:           yyh
 * <p>
 * 日期:           Oct 31, 2008
 * <p>
 * 负责人:         yyh
 * <p>
 * 负责小组:        MTT(请修改)
 * <p>
 * <p>
 */
public class FileArrayComparator implements Comparator
{
    private String sort;
    private int sgn;//1 ASC -1 DSC
    private boolean isTimeSort;

    public FileArrayComparator(String sort, int sgn)
    {
        this.sort = sort;
        this.sgn = sgn;
    }
    
    public FileArrayComparator(String sort, int sgn, boolean isTimeSort)
    {       
        this.sort = sort;
        this.sgn = sgn;
        this.isTimeSort = isTimeSort;
    }

    public int compare(Object o1, Object o2)
    {
        Object a1 = "";
        Object a2 = "";
        if (sort.equals("type"))
        {
            if (((Fileinfo)o1).isFold() && !((Fileinfo)o2).isFold())
            {
                return sgn * -1;
            }
            if (!((Fileinfo)o1).isFold() && ((Fileinfo)o2).isFold())
            {
                return sgn * 1;
            }
            a1 = ((Fileinfo)o1).getFileName();
            int index = ((String)a1).lastIndexOf(".");
            if (index != -1)
            {
                a1 = ((String)a1).substring(index + 1, ((String)a1).length());
            }
            a2 = ((Fileinfo)o2).getFileName();
            index = ((String)a2).lastIndexOf(".");
            if (index != -1)
            {
                a2 = ((String)a2).substring(index + 1, ((String)a2).length());
            }
        }
        else if (sort.equals("name"))
        {
            if(((Fileinfo)o1).isFold() && !((Fileinfo)o2).isFold())
            {
                return sgn * -1;
            }
            if(!((Fileinfo)o1).isFold() && ((Fileinfo)o2).isFold())
            {
                return sgn * 1;
            }
            a1 = ((Fileinfo)o1).getFileName();
            a2 = ((Fileinfo)o2).getFileName();
        }
        else if(sort.equals("owner"))
        {         
            a1 = ((Fileinfo)o1).getShareRealName();
            a2 = ((Fileinfo)o2).getShareRealName();
        }
        else if(sort.equals("locker"))
        {
        	
            a1 = ((Fileinfo)o1).getUserLock();
            a2 = ((Fileinfo)o2).getUserLock();
        }
        else if(sort.equals("state"))
        {
            int permit = ((Fileinfo)o1).getPermit();
            if((permit & Constant.ISWRITE) == Constant.ISWRITE)
            {
                a1 = Constant.GRID_TITLE_3;//"读写";
            }                 
            else
            {
                a1 = Constant.GRID_TITLE_2;//"只读"; 
            }                            
            if((permit & Constant.ISDOWN) == Constant.ISDOWN)
            {
                a1 = (String)a1 + Constant.FILELISTPANEL_GRIDDATA_4;//"，可下载"; 
            }
            permit = ((Fileinfo)o2).getPermit();
            if((permit & Constant.ISWRITE) == Constant.ISWRITE)
            {
                a2 = Constant.GRID_TITLE_3;//"读写";
            }                 
            else
            {
                a2 = Constant.GRID_TITLE_2;//"只读"; 
            }                            
            if((permit & Constant.ISDOWN) == Constant.ISDOWN)
            {
                a2 = (String)a2 + Constant.FILELISTPANEL_GRIDDATA_4;//"，可下载";  
            }
        }
        else if(sort.equals("path"))
        {
            if(((Fileinfo)o1).isFold() && !((Fileinfo)o2).isFold())
            {
                return sgn * -1;
            }
            if(!((Fileinfo)o1).isFold() && ((Fileinfo)o2).isFold())
            {
                return sgn * 1;
            }
            a1 = ((Fileinfo)o1).getShowPath();
            a2 = ((Fileinfo)o2).getShowPath();
        }
        else if (sort.equals("lastChanged") || sort.equals("deleteTime"))
        {
            if(((Fileinfo)o1).isFold() && !((Fileinfo)o2).isFold() && !isTimeSort)
            {
                return sgn * -1;
            }
            if(!((Fileinfo)o1).isFold() && ((Fileinfo)o2).isFold() && !isTimeSort)
            {
                return sgn * 1;
            }
            Date date = sort.equals("lastChanged") ? (((Fileinfo)o1).getLastedTime() != null
                ? ((Fileinfo)o1).getLastedTime() : ((Fileinfo)o1).getCreateTime()) : ((Fileinfo)o1)
                .getDeletedTime();
            if (date != null)
            {
                a1 = WebofficeUtility.getFormateDate(date, "");
            }
            date = sort.equals("lastChanged") ? (((Fileinfo)o2).getLastedTime() != null
                ? ((Fileinfo)o2).getLastedTime() : ((Fileinfo)o2).getCreateTime()) : ((Fileinfo)o2)
                .getDeletedTime();
            if (date != null)
            {
                a2 = WebofficeUtility.getFormateDate(date, "");
            }
        }
        else if (sort.equals("sharedTime"))
        {
            if(((Fileinfo)o1).isFold() && !((Fileinfo)o2).isFold() && !isTimeSort)
            {
                return sgn * -1;
            }
            if(!((Fileinfo)o1).isFold() && ((Fileinfo)o2).isFold() && !isTimeSort)
            {
                return sgn * 1;
            }
            Date date = ((Fileinfo)o1).getShareTime();
            if (date != null)
            {
                a1 = WebofficeUtility.getFormateDate(date, "");
            }
            date = ((Fileinfo)o2).getShareTime();
            if (date != null)
            {
                a2 = WebofficeUtility.getFormateDate(date, "");
            }
        }
        else if (sort.equals("sharer"))
        {
        	if (((Fileinfo)o1).isFold() && !((Fileinfo)o2).isFold())
            {
                return sgn * -1;
            }
            if (!((Fileinfo)o1).isFold() && ((Fileinfo)o2).isFold())
            {
                return sgn * 1;
            }
            a1 = ((Fileinfo)o1).getAuthor();
            a2 = ((Fileinfo)o2).getAuthor();
        }
        else if (sort.equals("size"))
        {
            if (((Fileinfo)o1).isFold() && !((Fileinfo)o2).isFold())
            {
                return sgn * -1;
            }
            if (!((Fileinfo)o1).isFold() && ((Fileinfo)o2).isFold())
            {
                return sgn * 1;
            }
            if (((Fileinfo)o1).isFold() && ((Fileinfo)o2).isFold())
            {
                return 0; 
            }
            a1 = ((Fileinfo)o1).getFileSize();
            a2 = ((Fileinfo)o2).getFileSize();
            if(a1 == null) a1= new Long(0);
            if(a2 == null) a2= new Long(0);
            return sgn * ((Long)a1).compareTo((Long)a2);
        }
        else if(sort.equals("tag2"))
        {
            if (((Fileinfo)o1).isFold() && !((Fileinfo)o2).isFold())
            {
                return sgn * -1;
            }
            if (!((Fileinfo)o1).isFold() && ((Fileinfo)o2).isFold())
            {
                return sgn * 1;
            }
            if (((Fileinfo)o1).isFold() && ((Fileinfo)o2).isFold())
            {
                return 0; 
            }
            a1 = ((Fileinfo)o1).getImportant();
            a2 = ((Fileinfo)o2).getImportant(); 
            if (a1 != null && a2 != null)
            {
                return sgn * ((Long)a1).compareTo((Long)a2);
            }
        }else if(sort.equals("sendtime")){
        	a1 = (String)((HashMap<String,Object>)o1).get("sendtime");
        	a2 = (String)((HashMap<String,Object>)o2).get("sendtime");
        }
        else if(sort.equals("sender")){
        	a1 = ((ArrayList<String>)((HashMap<String,Object>)o1).get("files")).get(0);
        	a2 = ((ArrayList<String>)((HashMap<String,Object>)o2).get("files")).get(0);
        }
        else if(sort.equals("tag"))
        {     
            a1 = ((Fileinfo)o1).getTag();
            a2 = ((Fileinfo)o2).getTag(); 
        }
        if(a1== null) a1 ="";
        if(a2== null) a2 ="";
        if (a1.equals("") && !a2.equals(""))
        {
            return sgn * -1;
        }
        if (a2.equals("") && !a1.equals(""))
        {
            return sgn * 1;
        }
        if(a1.equals("") && a2.equals(""))
        {
            return 0;
        }
        return sgn * ((RuleBasedCollator)Collator.getInstance(Locale.CHINA)).compare(a1, a2);
    }
}
