package apps.moreoffice.report.server.servlet.config;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.constants.DataTypeCons;
import apps.moreoffice.report.commons.domain.constants.TableCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataField;
import apps.moreoffice.report.commons.domain.databaseObject.DataRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataTable;
import apps.moreoffice.report.commons.domain.databaseObject.DataType;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;
import apps.moreoffice.report.server.resource.TableResource;
import apps.moreoffice.report.server.service.manager.dataCenter.IDataBase;
import apps.moreoffice.report.server.service.manager.dataCenter.ITemplateLib;

/**
 * 初始化数据
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-16
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class InitDataManager
{
    // db
    private IDataBase db;
    // 文档库接口
    private ITemplateLib lib;

    /**
     * 初始化数据库中的数据
     */
    public void initData()
    {
        // 初始化数据类型
        boolean isNull = db.isNullTable("DataType");
        if (isNull)
        {
            initDataType();
        }

        // 初始化数据规范
        isNull = db.isNullTable("DataRule");
        if (isNull)
        {
            initDataRule();
        }

        // 初始化DataTable
        isNull = db.isNullTable("DataTable");
        if (isNull)
        {
            initDataTable();
        }

        // 初始化文档库
        lib.init();
    }

    /*
     * 初始化数据类型
     */
    private void initDataType()
    {
        // 文本
        DataType dataType = getDataType(ReportCommonResource.TEXT, DataTypeCons.TEXT_TYPE);
        dataType.setLimitLength((short)64);
        db.save(getDataType(ReportCommonResource.TEXT, DataTypeCons.TEXT_TYPE));

        // 数字
        dataType = getDataType(ReportCommonResource.NUMBER, DataTypeCons.NUMBER_TYPE);
        db.save(dataType);

        // 日期
        dataType = getDataType(ReportCommonResource.DATE, DataTypeCons.DATE_TYPE);
        db.save(dataType);

        // TODO
        // 图片
//        dataType = getDataType(ReportCommonResource.PICTURE, DataTypeCons.PICTURE_TYPE);
//        db.save(dataType);
//
//        // 附件
//        dataType = getDataType(ReportCommonResource.FILE, DataTypeCons.FILE_TYPE);
//        db.save(dataType);
    }

    /*
     * 得到数据类型
     */
    private DataType getDataType(String name, short type)
    {
        DataType dataType = new DataType();
        dataType.setName(name);
        dataType.setBasicType(type);
        dataType.setCreatorId((long)1);
        dataType.setCreateDate(new Date());
        return dataType;
    }

    /*
     * 初始化数据规范
     */
    private void initDataRule()
    {
        // 当前日期
        db.save(getDataRule(ReportCommonResource.CURRENTDATE));
        // 当前日期时间
        db.save(getDataRule(ReportCommonResource.CURRENTDATETIME));
        // 当前用户所在部门
        db.save(getDataRule(ReportCommonResource.CURRENTORGUSER));
        // 当前用户姓名
        db.save(getDataRule(ReportCommonResource.CURRENTUSERNAME));
        // 当前用户登录账号
        db.save(getDataRule(ReportCommonResource.CURRENTLOGINNAME));
    }

    /*
     * 得到数据规范
     */
    private DataRule getDataRule(String name)
    {
        DataRule dataRule = new DataRule();
        dataRule.setName(name);
        dataRule.setType(DataRuleCons.SYSVAR);
        dataRule.setSystemDefined(true);
        dataRule.setCreatorId((long)1);
        dataRule.setCreateDate(new Date());
        return dataRule;
    }

    /*
     * 初始化DataTable
     */
    private void initDataTable()
    {
        // 用户
        DataTable dtable = getDataTable(TableResource.USER);
        dtable.setRealName("user");
        db.save(dtable);
        // 部门
        dtable = getDataTable(TableResource.ORGANIZATION);
        dtable.setRealName("organization");
        db.save(dtable);
    }

    /*
     * 得到DataTable
     */
    private DataTable getDataTable(String tableName)
    {
        DataTable dtable = new DataTable();
        dtable.setName(tableName);
        dtable.setCreateState(TableCons.HASCREATED);

        Set<DataField> dfields = new HashSet<DataField>();
        if (tableName.equals(TableResource.USER))
        {
            dfields.add(getDataField(TableResource.NAME));
            dfields.add(getDataField(TableResource.LOGINNAME));
            dfields.add(getDataField(TableResource.EMIAL));
            dfields.add(getDataField(TableResource.ORGNAME));
        }
        else
        {
            dfields.add(getDataField(TableResource.ORGID));
            dfields.add(getDataField(TableResource.ORGNAME));
            dfields.add(getDataField(TableResource.PARENTORGID));
        }
        dtable.setDfields(dfields);

        return dtable;
    }

    /*
     * 得到DataField
     */
    private DataField getDataField(String fieldName)
    {
        DataField dfield = new DataField();
        dfield.setName(fieldName);
        return dfield;
    }

    /**
     * @param db 设置 db
     */
    public void setDb(IDataBase db)
    {
        this.db = db;
    }

    /**
     * @param lib 设置 lib
     */
    public void setLib(ITemplateLib lib)
    {
        this.lib = lib;
    }
}