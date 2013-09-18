package apps.moreoffice.report.server.service;

import java.io.Serializable;

import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.DataRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataType;
import apps.moreoffice.report.commons.domain.databaseObject.Record;
import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.server.service.manager.DataRuleManager;
import apps.moreoffice.report.server.service.manager.DataTypeManager;
import apps.moreoffice.report.server.service.manager.RecordManager;
import apps.moreoffice.report.server.service.manager.TemplateManager;

/**
 * 报表实体类总管理器
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-6-15
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class ReportEntityManager
{
    // 模板管理器
    private TemplateManager templateM;
    // 记录管理器
    private RecordManager recordM;
    // 数据类型管理器
    private DataTypeManager dataTypeM;
    // 数据规范管理器
    private DataRuleManager dataRuleM;

    /**
     * 保存或更新实体对象
     * 
     * @param entity 实体对象
     * @param Serializable 返回给客户端的结果
     */
    public Serializable save(DataBaseObject entity)
    {
        // 根据实体对象选择不同的管理器
        if (entity instanceof Template)
        {
            entity = templateM.save(entity);
        }
        else if (entity instanceof Record)
        {
            entity = recordM.save(entity);
            if (entity != null)
            {
                return ((Record)entity).getId();
            }
            else
            {
                return -1;
            }
        }
        else if (entity instanceof DataType)
        {
            entity = dataTypeM.save(entity);
        }
        else if (entity instanceof DataRule)
        {
            entity = dataRuleM.save(entity);
        }
        else
        {
            return null;
        }

        return entity.clone(false);
    }

    /**
     * @param templateM 设置 templateM
     */
    public void setTemplateM(TemplateManager templateM)
    {
        this.templateM = templateM;
    }

    /**
     * @param recordM 设置 recordM
     */
    public void setRecordM(RecordManager recordM)
    {
        this.recordM = recordM;
    }

    /**
     * @param dataTypeM 设置 dataTypeM
     */
    public void setDataTypeM(DataTypeManager dataTypeM)
    {
        this.dataTypeM = dataTypeM;
    }

    /**
     * @param dataRuleM 设置 dataRuleM
     */
    public void setDataRuleM(DataRuleManager dataRuleM)
    {
        this.dataRuleM = dataRuleM;
    }
}