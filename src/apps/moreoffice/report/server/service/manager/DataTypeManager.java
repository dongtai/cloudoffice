package apps.moreoffice.report.server.service.manager;

import java.util.ArrayList;
import java.util.Date;

import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.DataType;
import apps.moreoffice.report.commons.domain.databaseObject.User;
import apps.moreoffice.report.server.service.manager.dataCenter.IDataTypeDB;

/**
 * 数据类型逻辑处理类
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-13
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class DataTypeManager extends BaseManager
{
    // 数据库操作接口
    private IDataTypeDB db;
    // 用户、组织、角色管理接口
    private IUserManager userM;

    /**
     * 存盘
     * 
     * @param entity 实体对象
     * @return DataBaseObject 保存后的对象
     */
    public DataBaseObject save(DataBaseObject entity)
    {
        DataType dataType = (DataType)entity;
        // 设置创建或修改时间
        if (dataType.getCreateDate() == null)
        {
            dataType.setCreateDate(new Date());
        }
        else
        {
            dataType.setModifyDate(new Date());
        }

        return db.save(dataType);
    }

    /**
     * 得到数据类型列表
     * 
     * @return ArrayList<DataType> DataType集合
     */
    public ArrayList<DataType> getDataTypeList()
    {
        ArrayList<DataType> dataTypes = db.getDataTypeList();
        if (dataTypes != null && !dataTypes.isEmpty())
        {
            User user;
            for (DataType dataType : dataTypes)
            {
                user = userM.getUser(dataType.getCreatorId());
                if (user != null)
                {
                    dataType.setCreatorName(user.getUserName());
                }
                if (dataType.getModifierId() != null)
                {
                    user = userM.getUser(dataType.getModifierId());
                    if (user != null)
                    {
                        dataType.setModifierName(user.getUserName());
                    }
                }
            }
        }
        return dataTypes;
    }

    /**
     * 通过数据类型ID得到数据类型
     * 
     * @param dataTypeID 数据类型ID
     * @return DataType 数据类型
     */
    public DataType getDataTypeByID(long dataTypeID)
    {
        return db.getDataTypeByID(dataTypeID);
    }

    /**
     * 通过数据类型ID删除数据类型
     * 
     * @param dataTypeID 数据类型ID
     */
    public void deleteDataTypeByID(long dataTypeID)
    {
        db.delete(getDataTypeByID(dataTypeID));
    }

    /**
     * @param db 设置 db
     */
    public void setDb(IDataTypeDB db)
    {
        this.db = db;
        setBasedb(db);
    }

    /**
     * @param userM 设置 userM
     */
    public void setUserM(IUserManager userM)
    {
        this.userM = userM;
    }
}