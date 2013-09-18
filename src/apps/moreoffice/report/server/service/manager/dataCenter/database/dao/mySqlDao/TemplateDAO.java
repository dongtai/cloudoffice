package apps.moreoffice.report.server.service.manager.dataCenter.database.dao.mySqlDao;

import java.util.List;

import apps.moreoffice.report.commons.domain.databaseObject.Template;

/**
 * 模板操作DAO
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-16
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class TemplateDAO extends BaseHibernateDAO
{
    /**
     * 根据模板ID得到模板对象
     * 
     * @param templateID 模板ID
     * @return Template 模板对象
     */
    public Template getTemplateByID(long templateID)
    {
        try
        {
            return (Template)getEntityByID(entity_template, templateID);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 根据模板编号得到模板对象
     * 
     * @param templateNum 模板编号
     * @return List 模板对象集合
     */
    @ SuppressWarnings("rawtypes")
    public List getTemplateByNum(String templateNum)
    {
        try
        {
            String sql = "from " + entity_template + " T where T.number = ?";
            return find(sql, templateNum);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 根据模板名称得到模板对象
     * 
     * @param templateName 模板名称
     * @return List 模板对象集合
     */
    @ SuppressWarnings("rawtypes")
    public List getTemplateByName(String templateName)
    {
        try
        {
            String sql = "from " + entity_template + " T where T.name = ?";
            return find(sql, templateName);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }

    /**
     * 设置模板的boolean属性
     * 
     * @param templateID 模板ID
     * @param attrFlag boolean属性
     */
    public void setAttrFlag(long templateID, long attrFlag)
    {
        try
        {
            //            String hql = "from Admin as admin set admin.status=status where admin.id=id";
            String hql = "update " + entity_template + " set attrFlag=" + attrFlag + " where id="
                + templateID;
//            hql = "update " + entity_template + " as template set template.attrFlag=" + attrFlag
//                + " where template.id=" + templateID;
                        hql = "update rpt_sys_template set attrFlag=" + attrFlag + " where id="
                            + templateID;
//            hql = "from Template set attrFlag=" + attrFlag + " where id=" + templateID;
            update(hql);
        }
        catch(RuntimeException e)
        {
            throw e;
        }
    }
}