package apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao;

import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.DataType;

/**
 * 数据类型DAO
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
public class DataTypeDAO extends BaseHibernateDAO
{
    /**
     * 得到数据类型列表
     * 
     * @return Result 结果
     */
    @ SuppressWarnings("rawtypes")
    public List getDataTypeList()
    {
        try
        {
            String sql = "from " + entity_dataType;
            return find(sql);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 通过数据类型ID得到数据类型
     * 
     * @param dataTypeID 数据类型ID
     * @return DataType 数据类型
     */
    public DataType getDataTypeByID(long dataTypeID)
    {
        try
        {
            return (DataType)getEntityByID(entity_dataType, dataTypeID);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }
}