package apps.transmanager.weboffice.service.fawen;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import apps.moreoffice.Cn2Spell;
import apps.transmanager.weboffice.databaseobject.Fawen;
import apps.transmanager.weboffice.databaseobject.Organizations;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.FileSystemService;
import apps.transmanager.weboffice.service.server.JQLServices;

public class FawenUtil {

private static FawenUtil instance=new FawenUtil();
    
    public FawenUtil()
    {    	
    	instance =  this;
    }
    public static FawenUtil instance()
    {
        return instance;
    }
   /* //给人员添加权限
    public HashMap<String, Object> setFawenUser(String ids,Users user)
    {
        try
        {
            String sql="";
            String companyId=user.getCompany().getId().toString();
            Pattern pattern = Pattern.compile("^(\\d+,)*\\d+$");
            Matcher matcher=pattern.matcher(ids);
            if(!matcher.matches()){
                return null;  //非法id
            }
            JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
            sql="update users set isFawen = 1 where company_id="+companyId+" and id in ("+ids+")";
            jqlService.excuteNativeSQL(sql);
            HashMap<String, Object> resultmap = new HashMap<String, Object>();
            return resultmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //unset the user power
    public HashMap<String, Object> unsetFawenUser(String ids,Users user)
    {
        try
        {
            String sql="";
            String companyId=user.getCompany().getId().toString();
            Pattern pattern = Pattern.compile("^(\\d+,)*\\d+$");
            Matcher matcher=pattern.matcher(ids);
            if(!matcher.matches()){
                return null;  //非法id
            }
            JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
            sql="update users set isFawen = NULL where company_id="+companyId+" and id in ("+ids+")";
            jqlService.excuteNativeSQL(sql);
            HashMap<String, Object> resultmap = new HashMap<String, Object>();
            return resultmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //获取本公司公文收发人员名单
    public HashMap<String, Object> getFawenUser(Users user)
    {
        try
        {
            String sql="";
            String companyId=user.getCompany().getId().toString();
            JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
            sql="select realName,userName from users where company_id="+companyId+" and isFawen=1";
            List<Object[]> cmans=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,0,1000);
            HashMap<String, Object> resultmap = new HashMap<String, Object>();
            resultmap.put("names", cmans);
            return resultmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }*/
    //getFawenDepartment获取可发文单位列表
   public HashMap<String, Object> getFawenDepartment(Users user,Long exceptID)
   {
       try
       {
           String sql="";
           HashMap<String, Object> resultmap = new HashMap<String, Object>();
           JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
           //check the value
           sql="SELECT b.id, b.name FROM organizations b"
                +" WHERE ISNULL(b.parent_id)"
                +" and b.company_id="+user.getCompany().getId()
                +" and b.id<>"+exceptID;
           //筛选条件
           List<Object[]> draftlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql, 0, 2000);
           List list=new ArrayList();//存放每行的数据
           for (int i=0;i<draftlist.size();i++)
           {
               Object[] objs=draftlist.get(i);
               HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
               //long id=((BigInteger)objs[0]).longValue();
               values.put("id", objs[0]);//主键编号
               values.put("name", objs[1]);
               values.put("py", Cn2Spell.converterToFirstSpell((String)objs[1]));//拼音索引
               list.add(values);
           }
           resultmap.put("fileList", list);
           return resultmap;
       }
       catch (Exception e)
       {
           e.printStackTrace();
           return null;
       }
   }
    //我的发文
    public HashMap<String, Object> getFawenSend(int start,int count, java.lang.String sort, java.lang.String order,Users user)
    {
        try
        {
            String sql="";
            String sqlcount="";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            HashMap<String, Object> resultmap = new HashMap<String, Object>();
            JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
            //check the value
            sql="select fid,fname,fpath,sendername,senderid,senddate,senddepartid,senddepartname,receivername,receiverid,receivedate,receivedepartid,receivedepartname "                    +" from fawen"
                    +" where senderid="+user.getId()
                    +" order by fid desc";;
            //筛选条件
            List<Object[]> draftlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
            sqlcount="select count(*) from Fawen  where senderid="+user.getId();
            Long size=(Long)jqlService.getCount(sqlcount);
            resultmap.put("fileListSize", size);//文件（记录）总数量
            List list=new ArrayList();//存放每行的数据
            for (int i=0;i<draftlist.size();i++)
            {
                Object[] objs=draftlist.get(i);
                HashMap<String, Object> values = new HashMap<String, Object>();//一行的具体数据
                long id=((BigInteger)objs[0]).longValue();
                values.put("id", id);//主键编号
                values.put("fname", (String)objs[1]);
                values.put("fpath", (String)objs[2]);

                values.put("sendername", (String)objs[3]);
                values.put("senderid", ((BigInteger)objs[4]).longValue());
                values.put("senddate", sdf.format((Date)objs[5]));
                values.put("senddepartid", ((BigInteger)objs[6]).longValue());
                values.put("senddepartname", (String)objs[7]);


                if(objs[8]!=null){
                    values.put("receivername", (String)objs[8]);
                    values.put("receiverid", ((BigInteger)objs[9]).longValue());
                    values.put("receivedate", sdf.format((Date) objs[10]));
                }
                values.put("receivedepartid", ((BigInteger)objs[11]).longValue());
                values.put("receivedepartname", (String)objs[12]);

                list.add(values);
            }
            resultmap.put("fileList", list);
            return resultmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //incomming 单位收文 &   我的收文
    public HashMap<String, Object> getFawenReceive(int start,int count, java.lang.String sort, java.lang.String order,Users user,String scope,Long receiveDepartId)
    {
        try
        {
            String sql="";
            String sqlcount="";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            HashMap<String, Object> resultmap = new HashMap<String, Object>();
            JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);

            sql="SELECT f.fid,f.fname,f.fpath,f.sendername,f.senderid,f.senddate,f.senddepartid,f.senddepartname,f.receivername,f.receiverid,f.receivedate,f.receivedepartid,f.receivedepartname"
                +" FROM fawen f"
                +" where f.receivedepartid="+receiveDepartId;
               // +" and a.company_id="+companyid;
            if (scope==null){
                scope="";
            }
            if(scope.equals("my")){
                sql+=" and f.receiverid="+user.getId();
                sqlcount="select count(id) from Fawen  where receiverid="+user.getId();
            }else{
                sqlcount="select count(id) from Fawen  where receivedepartid="+receiveDepartId;
            }
            sql +=" order by f.fid desc";
            //筛选条件
            List<Object[]> draftlist=(List<Object[]>)jqlService.getObjectByNativeSQL(sql,start,count);
            //sqlcount="select count(id) from Fawen  where receivedepartid="+companyid;
            Long size=(Long)jqlService.getCount(sqlcount);
            resultmap.put("fileListSize", size);//文件（记录）总数量

            List list=new ArrayList();//存放每行的数据
            for (int i=0;i<draftlist.size();i++)
            {
                Object[] objs=draftlist.get(i);
                HashMap<String,Object> values=new HashMap<String,Object>();
                values.put("id", objs[0]);//主键编号
                values.put("fname", objs[1]);
                values.put("fpath",  objs[2]);

                values.put("sendername", objs[3]);
                values.put("senderid", objs[4]);
                values.put("senddate", sdf.format((Date)objs[5]));
                values.put("senddepartid", objs[6]);
                values.put("senddepartname", objs[7]);

                if(objs[9]!=null){
                    if(!objs[9].toString().equals("0")){
                        values.put("receiverid", objs[9]);
                        values.put("receivername", objs[8]);
                        values.put("receivedate", sdf.format((Date)objs[10]));
                    }
                }
                values.put("receivedepartid", objs[11]);
                values.put("receivedepartname", objs[12]);

                list.add(values);
            }
            resultmap.put("fileList", list);
            return resultmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    //发送公文
    public HashMap<String, Object> sendFawen(String path,String name,Organizations receiveDepart,Organizations sendDepart,Users user)
    {
        try
        {
            JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
            //复制文件
            Fileinfo info = null;
            FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);;
            if ((path.startsWith("user_") || path.startsWith("group_")
                    || path.startsWith("team_")
                    || path.startsWith("org_")
                    || path.startsWith("company_")
            ) && (path.indexOf("/") > 0))// 文档库文档
            {
                //文件库中的文件
                info = fileSystemService.addAuditFile(user.getId(), path);
            }else{
                //本地上传的
                String tempPath = WebConfig.tempFilePath + File.separatorChar;
                String filename=fileNameReplace(path);
                File file = new File(tempPath + filename);
                InputStream fin = new FileInputStream(file);
                InputStream ois = null;
                if (filename.toLowerCase().endsWith(".pdf"))
                {
                    ois = fin;
                }
                else
                {
                    ois=new FileInputStream(file);
                }
                info = fileSystemService.addAuditFile(user.getId(), name, fin,  ois);
                if (!filename.toLowerCase().endsWith(".pdf"))
                {
                    ois.close();
                }
                fin.close();
                file.delete();
            }
            Fawen fawen=new Fawen(name,info.getPathInfo(),receiveDepart.getId(),receiveDepart.getName(),
                    sendDepart.getId(),sendDepart.getName(),user.getId(),user.getRealName());
            jqlService.save(fawen);
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private String fileNameReplace(String filename)
    {
        if (filename!=null)
        {
            int index=filename.indexOf("&amp;");
            if (index>=0)
            {
                filename=filename.replaceAll("&amp;", "&");
            }
        }
        return filename;
    }

    //收取公文
    public HashMap<String, Object> receiveFawen(Long fawenid,Users user,Long receiveDepartID)
    {
        try
        {
//            UserService userService = (UserService)ApplicationContext.getInstance().getBean(UserService.NAME);
//            Fawen fawen=(Fawen) userService.getFawen(fawenid);
//            fawen.receiveFawen(user.getId(),user.getRealName());
            //确保是自己部门的人收取公文
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "update fawen set receiverid="+user.getId()+" , receivername ='"+user.getRealName()+"' , receivedate='"+sdf.format(new Date())
                +"' where fid="+fawenid+" and (ISNULL(receiverid) or receiverid=0)"
                +" and receivedepartid="+receiveDepartID;
            JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
            jqlService.excuteNativeSQL(sql);
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


}