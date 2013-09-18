package apps.moreoffice.report.server.service.manager.dataCenter.database.connection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataField;
import apps.moreoffice.report.commons.domain.databaseObject.DataSource;
import apps.moreoffice.report.commons.domain.databaseObject.DataTable;
import apps.moreoffice.report.commons.domain.databaseObject.RField;
import apps.moreoffice.report.commons.domain.databaseObject.RTable;
import apps.moreoffice.report.server.service.manager.dataCenter.IDataBase;
import apps.moreoffice.report.server.service.manager.dataCenter.ISQLDB;

/**
 * 管理用户数据库
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
public class UserDBManager
{
    // 用户数据库表配置文件路径
    private String dbFilePath;
    // 数据库操作接口
    private IDataBase db;
    // 用户表操作接口
    private ISQLDB sqlDB;
    // 所有连接
    private HashMap<String, IConnection> cons;
    // 数据源
    private ArrayList<DataSource> dataSources;
    // 默认数据库名称
    private final String defaultDB = "defaultDB";

    /**
     * 初始化用户数据库
     * 
     * @param sc ServletContext
     */
    public void init(ServletContext sc)
    {
        File[] dbcfgList = getDBCfgList(sc.getRealPath(dbFilePath));
        Configuration cfg = new Configuration();
        if (dbcfgList != null)
        {
            int size = dbcfgList.length;
            cons = new HashMap<String, IConnection>();
            File file;
            String name;
            for (int i = 0; i < size; i++)
            {
                file = dbcfgList[i];
                name = file.getName();
                try
                {
                    cons.put(name.substring(0, name.lastIndexOf(".cfg.xml")), new ReportConnection(
                        cfg.configure(file)));
                }
                catch(HibernateException e)
                {
                    throw e;
                }
            }
        }

        // 初始化数据源对象
        initDataSource();
    }

    /*
     * 初始化数据源对象
     */
    @ SuppressWarnings("unchecked")
    private void initDataSource()
    {
        if (cons == null)
        {
            return;
        }

        // 先得到已经导入的数据源对象
        List<DataSource> list = db.findByExample(new DataSource(), 0, Integer.MAX_VALUE);

        if (cons.size() > 1)
        {
            // 构造DataSource
            boolean isExist = false;
            Set<String> names = cons.keySet();
            dataSources = new ArrayList<DataSource>();
            for (String name : names)
            {
                if (name.equals(defaultDB))
                {
                    continue;
                }

                // 判断当前数据源是否已经导入
                if (list != null && !list.isEmpty())
                {
                    isExist = false;
                    for (DataSource dataSource : list)
                    {
                        if (dataSource.getName().equals(name))
                        {
                            isExist = true;
                            list.remove(dataSource);
                            break;
                        }
                    }
                }
                if (isExist)
                {
                    continue;
                }

                DataSource dataSource = new DataSource();
                dataSource.setName(name);
                dataSources.add(dataSource);
                // 创建RTable和DataTable
                createReportTable(cons.get(name), dataSource);
            }
        }

        // list中剩下的就是用户已经删除配置文件的数据源
        if (list != null && !list.isEmpty())
        {
            for (DataSource dataSource : list)
            {
                db.delete(dataSource);
            }
        }
    }

    /*
     * 创建外部数据源对应的RTable和DataTable
     */
    private void createReportTable(IConnection con, DataSource dataSource)
    {
        String sql = "show tables";
        Vector<Vector<Object>> records = con.executeQuery(sql);
        if (records == null || records.size() == 0 || records.get(0).size() == 0)
        {
            return;
        }

        String tableName;
        Vector<Vector<Object>> fields;
        for (int i = 0, size = records.size(); i < size; i++)
        {
            tableName = (String)records.get(i).get(0);

            RTable rtable = new RTable();
            DataTable dtable = new DataTable();
            rtable.setDtable(dtable);
            dtable.setName(tableName);
            dtable.setRealName(tableName);
            dtable.setDataSource(dataSource);
            dtable.setCreateState(TableCons.HASCREATED);
            fields = sqlDB.getOtherTableFields(dataSource, tableName);
            if (fields != null && !fields.isEmpty())
            {
                Set<RField> rfields = new HashSet<RField>();
                Set<DataField> dfields = new HashSet<DataField>();
                for (Vector<Object> field : fields)
                {
                    DataField dfield = new DataField();
                    dfield.setName(field.get(0).toString());
                    dfield.setRealName(field.get(0).toString());
                    dfields.add(dfield);

                    RField rfield = new RField();
                    rfield.setDfield(dfield);
                    rfields.add(rfield);
                }
                rtable.setRfields(rfields);
                dtable.setDfields(dfields);
            }

            db.save(rtable, false);
        }
    }

    /**
     * 得到默认连接
     * 
     * @return IConnection 用户数据连接
     */
    public IConnection getDefaultConnection()
    {
        return cons.get(defaultDB);
    }

    /**
     * 根据数据库名得到连接
     * 
     * @param dbName 数据库名
     * @return IConnection 用户数据连接
     */
    public IConnection getConnection(String dbName)
    {
        return cons.get(dbName);
    }

    /**
     * @return 返回 dataSources
     */
    public ArrayList<DataSource> getDataSources()
    {
        return dataSources;
    }

    /*
     * 得到路径下所有配置文件
     */
    private File[] getDBCfgList(String path)
    {
        File file = new File(path);
        if (file.isDirectory())
        {
            return file.listFiles();
        }
        return null;
    }

    /**
     * @param dbFilePath 设置 dbFilePath
     */
    public void setDbFilePath(String dbFilePath)
    {
        this.dbFilePath = dbFilePath;
    }

    /**
     * @param db 设置 db
     */
    public void setDb(IDataBase db)
    {
        this.db = db;
    }

    /**
     * @param sqlDB 设置 sqlDB
     */
    public void setSqlDB(ISQLDB sqlDB)
    {
        this.sqlDB = sqlDB;
    }
}