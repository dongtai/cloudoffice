package apps.moreoffice.report.server.service.manager.dataCenter;

import apps.moreoffice.report.commons.domain.databaseObject.Template;

/**
 * 数据库操作接口：模板
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
public interface ITemplateDB extends IDataBase
{
    /**
     * 根据模板ID得到模板对象
     * 
     * @param templateID 模板ID
     * @return Template 模板对象
     */
    Template getTemplateByID(long templateID);

    /**
     * 根据模板编号得到模板对象
     * 
     * @param templateNum 模板名称
     * @return Template 模板对象
     */
    Template getTemplateByNum(String templateNum);

    /**
     * 根据模板名称得到模板对象
     * 
     * @param templateName 模板名称
     * @return Template 模板对象
     */
    Template getTemplateByName(String templateName);

    /**
     * 设置模板的boolean属性
     * 
     * @param templateID 模板ID
     * @param attrFlag boolean属性
     */
    void setAttrFlag(long templateID, long attrFlag);
}