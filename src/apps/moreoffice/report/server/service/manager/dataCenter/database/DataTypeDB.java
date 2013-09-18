package apps.moreoffice.report.server.service.manager.dataCenter.database;

import java.util.ArrayList;

import apps.moreoffice.report.commons.domain.databaseObject.DataType;
import apps.moreoffice.report.server.service.manager.dataCenter.IDataTypeDB;
import apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao.DataTypeDAO;

/**
 * 数据库操作实现类：数据类型
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-5
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class DataTypeDB extends DataBase implements IDataTypeDB
{
    // dao对象
    private DataTypeDAO dao;

    /**
     * 得到数据类型列表
     * 
     * @return ArrayList<DataType> DataType集合
     */
    @ SuppressWarnings("unchecked")
    public ArrayList<DataType> getDataTypeList()
    {
        return (ArrayList<DataType>)dao.getDataTypeList();
    }

    /**
     * 通过数据类型ID得到数据类型
     * 
     * @param dataTypeID 数据类型ID
     * @return DataType 数据类型
     */
    public DataType getDataTypeByID(long dataTypeID)
    {
        return dao.getDataTypeByID(dataTypeID);
    }

    /**
     * @param dao 设置 dao
     */
    public void setDao(DataTypeDAO dao)
    {
        this.dao = dao;
        setBaseDao(dao);
    }
}