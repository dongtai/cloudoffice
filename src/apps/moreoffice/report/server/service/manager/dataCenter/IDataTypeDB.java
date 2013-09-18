package apps.moreoffice.report.server.service.manager.dataCenter;

import java.util.ArrayList;

import apps.moreoffice.report.commons.domain.databaseObject.DataType;

/**
 * 数据库操作接口：数据类型
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
public interface IDataTypeDB extends IDataBase
{
    /**
     * 得到数据类型列表
     * 
     * @return ArrayList<DataType> DataType列表
     */
    ArrayList<DataType> getDataTypeList();
    
    /**
     * 通过数据类型ID得到数据类型
     * 
     * @param dataTypeID 数据类型ID
     * @return DataType 数据类型
     */
    DataType getDataTypeByID(long dataTypeID);
}