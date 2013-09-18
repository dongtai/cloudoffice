package apps.moreoffice.report.server.service.manager.dataCenter.database;

import java.util.ArrayList;

import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.server.service.manager.dataCenter.IPermissionDB;
import apps.moreoffice.report.server.service.manager.dataCenter.ITableDB;
import apps.moreoffice.report.server.service.manager.dataCenter.ITableRuleDB;
import apps.moreoffice.report.server.service.manager.dataCenter.ITemplateDB;
import apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao.TemplateDAO;

/**
 * 数据库操作实现类：模板
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
public class TemplateDB extends DataBase implements ITemplateDB
{
    // dao对象
    private TemplateDAO dao;
    // 数据库操作接口：表和字段
    private ITableDB tableDB;
    // 数据库操作接口：权限
    private IPermissionDB permissionDB;
    // 数据库操作接口：表间规则
    private ITableRuleDB tableRuleDB;

    /**
     * 存盘
     * 
     * @param entity 实体对象
     * @return DataBaseObject 保存后的对象
     */
    public DataBaseObject save(DataBaseObject entity)
    {        
        entity = super.save(entity);
        
        // 优化
        optimization();
        
        // 保存后用户表需要处理其它事情
        tableDB.saveTemplate((Template)entity);
        
        return entity;
    }
    
    /*
     * 优化
     */
    private void optimization()
    {
        // 对RTable、RField、用户表的优化
        tableDB.optimization();
        // 对Permission的优化
        permissionDB.optimization();
        // 对TableRule优化
        tableRuleDB.optimization();
    }
    
    /**
     * 删除实体对象
     * 
     * @param entity 实体对象
     */
    public void delete(DataBaseObject entity)
    {
        super.delete(entity);
        
        // 删除对应的无用数据表
        tableDB.delete(entity);
    }
    
    /**
     * 删除模板对象
     * 
     * @param id 模板id
     */
    public void delete(long id)
    {
        Template template = dao.getTemplateByID(id);
        delete(template);
    }

    /**
     * 根据模板ID得到模板对象
     * 
     * @param templateID 模板ID
     * @return Template 模板对象
     */
    public Template getTemplateByID(long templateID)
    {
        return dao.getTemplateByID(templateID);
    }

    /**
     * 根据模板编号得到模板对象
     * 
     * @param templateNum 模板名称
     * @return Template 模板对象
     */
    @ SuppressWarnings("unchecked")
    public Template getTemplateByNum(String templateNum)
    {
        ArrayList<Template> list = (ArrayList<Template>)dao.getTemplateByNum(templateNum);
        if (list != null && list.size() > 0)
        {
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据模板名称得到模板对象
     * 
     * @param templateName 模板名称
     * @return Template 模板对象
     */
    @ SuppressWarnings("unchecked")
    public Template getTemplateByName(String templateName)
    {
        ArrayList<Template> list = (ArrayList<Template>)dao.getTemplateByName(templateName);
        if (list != null && list.size() > 0)
        {
            return list.get(0);
        }
        return null;
    }
    
    /**
     * 设置模板的boolean属性
     * 
     * @param templateID 模板ID
     * @param attrFlag boolean属性
     */
    public void setAttrFlag(long templateID, long attrFlag)
    {
        dao.setAttrFlag(templateID, attrFlag);
    }

    /**
     * @param dao 设置 dao
     */
    public void setDao(TemplateDAO dao)
    {
        this.dao = dao;
        setBaseDao(dao);
    }
    
    /**
     * @param tableDB 设置 tableDB
     */
    public void setTableDB(TableDB tableDB)
    {
        this.tableDB = tableDB;
    }
    
    /**
     * @param permissionDB 设置 permissionDB
     */
    public void setPermissionDB(IPermissionDB permissionDB)
    {
        this.permissionDB = permissionDB;
    }
    
    /**
     * @param tableRuleDB 设置 tableRuleDB
     */
    public void setTableRuleDB(ITableRuleDB tableRuleDB)
    {
        this.tableRuleDB = tableRuleDB;
    }
}