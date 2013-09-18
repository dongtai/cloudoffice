package apps.moreoffice.report.server.service.manager;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import apps.moreoffice.report.commons.JSONTools;
import apps.moreoffice.report.commons.domain.constants.DataRuleCons;
import apps.moreoffice.report.commons.domain.databaseObject.AutoNumRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.DataField;
import apps.moreoffice.report.commons.domain.databaseObject.DataRule;
import apps.moreoffice.report.commons.domain.databaseObject.DataTable;
import apps.moreoffice.report.commons.domain.databaseObject.DownListRule;
import apps.moreoffice.report.commons.domain.databaseObject.ListSelectRule;
import apps.moreoffice.report.commons.domain.databaseObject.OrgUser;
import apps.moreoffice.report.commons.domain.databaseObject.TreeSelectRule;
import apps.moreoffice.report.commons.domain.databaseObject.User;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;
import apps.moreoffice.report.commons.formula.RFormula;
import apps.moreoffice.report.server.service.manager.dataCenter.IDataRuleDB;


/**
 * 数据规范管理器
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
public class DataRuleManager extends BaseManager
{
    // 数据库操作接口
    private IDataRuleDB db;
    // 用户、组织、角色管理接口
    private IUserManager userM;
    // 日期解析
    private DateFormat df;

    /**
     * 存盘
     * 
     * @param entity 实体对象
     * @return DataBaseObject 保存后的对象
     */
    public DataBaseObject save(DataBaseObject entity)
    {
        DataRule dataRule = (DataRule)entity;
        // 设置创建或修改时间
        if (dataRule.getCreateDate() == null)
        {
            dataRule.setCreateDate(new Date());
        }
        else
        {
            dataRule.setModifyDate(new Date());
        }

        return db.save(dataRule);
    }

    /**
     * 得到数据规范列表
     * 
     * @param type 类型
     * @return List DataRule列表
     */
    @ SuppressWarnings({"rawtypes", "unchecked"})
    public List getDataRuleList(short type)
    {
        List<DataRule> list = db.getDataRuleList(type);
        if (list != null && !list.isEmpty())
        {
            User user;
            for (int i = 0, size = list.size(); i < size; i++)
            {
                DataRule dataRule = list.get(i);
                user = userM.getUser(dataRule.getCreatorId());
                if (user != null)
                {
                    dataRule.setCreatorName(user.getUserName());
                }
                if (dataRule.getModifierId() != null)
                {
                    user = userM.getUser(dataRule.getModifierId());
                    if (user != null)
                    {
                        dataRule.setModifyName(user.getUserName());
                    }
                }
                list.set(i, dataRule.clone(false));
            }
        }
        return list;
    }

    /**
     * 通过数据规范ID得到数据规范
     * 
     * @param dataRuleID 数据规范ID
     * @return DataRule 数据规范
     */
    public DataRule getDataRuleByID(long dataRuleID)
    {
        return db.getDataRuleByID(dataRuleID);
    }
    
    /**
     * 通过数据规范ID删除数据规范
     * 
     * @param dataRuleID 数据规范ID
     */
    public void deleteDataRuleByID(long dataRuleID)
    {
        db.delete(getDataRuleByID(dataRuleID));
    }

    /**
     * 得到数据规范的值
     * 
     * @param userID 用户ID
     * @param dataRuleID 数据规范ID
     * @return Serializable 数据规范的值
     */
    public Serializable getDataRuleData(long userID, long dataRuleID)
    {
        DataRule dataRule = getDataRuleByID(dataRuleID);
        return getDataRuleData(userID, dataRule, false);
    }

    /**
     * 得到数据规范的值
     * 
     * @param userID 用户ID
     * @param dataRule 数据规范
     * @param isWebPage 是否是页面
     * @return Serializable 数据规范的值
     */
    public Serializable getDataRuleData(long userID, DataRule dataRule, boolean isWebPage)
    {
        if (dataRule.getType() == DataRuleCons.SYSVAR)
        {
            // 系统变量
            return getSysVarData(userID, dataRule.getName());
        }
        else if (dataRule instanceof AutoNumRule)
        {
            // 自动编号
            return getAutoNumData(userID, (AutoNumRule)dataRule, isWebPage);
        }
        else if (dataRule instanceof DownListRule)
        {
            // 下拉列表
            return getDownListData((DownListRule)dataRule);
        }
        else if (dataRule instanceof ListSelectRule)
        {
            // 列表选择
            return getListSelectData((ListSelectRule)dataRule);
        }
        else if (dataRule instanceof TreeSelectRule)
        {
            // 树型选择
            return getTreeSelectData((TreeSelectRule)dataRule);
        }
        return null;
    }

    /**
     * 得到系统变量值
     * 
     * @param userID 用户ID
     * @param name 系统变量名
     * @return Serializable 系统变量值
     */
    public Serializable getSysVarData(long userID, String name)
    {
        if (name.equals(ReportCommonResource.CURRENTDATE)
            || name.equals(ReportCommonResource.CURRENTDATETIME))
        {
            // 当前日期和日期时间
            df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format(new Date());
        }
        else if (name.equals(ReportCommonResource.CURRENTORGUSER))
        {
            // 当前用户所在部门
            ArrayList<OrgUser> orgUsers = userM.getOrgUserByUser(userID);
            if (orgUsers != null && orgUsers.size() > 0)
            {
                return orgUsers.get(0).getOrganization().getName();
            }
        }

        User user = userM.getUser(userID);
        if (name.equals(ReportCommonResource.CURRENTUSERNAME))
        {
            // 当前用户姓名
            return user.getUserName();
        }
        else if (name.equals(ReportCommonResource.CURRENTLOGINNAME))
        {
            // 当前用户登录账号
            return user.getLoginName();
        }

        return null;
    }

    /*
     * 得到自动编号值
     */
    private Serializable getAutoNumData(long userID, AutoNumRule dataRule, boolean isWebPage)
    {
        String firstValue, secondValue;
        StringBuffer sb = new StringBuffer();
        Vector<Object> datas = (Vector<Object>)JSONTools.getAutoNumData(dataRule.getFormat());
        for (int i = 0, size = datas.size(); i < size; i++, i++)
        {
            firstValue = (String)datas.get(i);
            secondValue = (String)datas.get(i + 1);
            if (firstValue.equals(ReportCommonResource.FIXEDWORDS))
            {
                sb.append(secondValue);
            }
            else if (firstValue.equals(ReportCommonResource.SYSVAR))
            {
                sb.append(getSysVarData(userID, secondValue));
            }
            else if (firstValue.equals(ReportCommonResource.DATEVAR))
            {
                df = new SimpleDateFormat(secondValue);
                sb.append(df.format(new Date()));
            }
            else if (firstValue.equals(ReportCommonResource.NUMBERS))
            {
                Long num = dataRule.getNumbers() + 1;
                int len = String.valueOf(num).length();
                long digitcount = Long.parseLong(secondValue);
                if (len < digitcount)
                {
                    long dx = digitcount - len;
                    StringBuffer buf = new StringBuffer();
                    for (int j = 0; j < dx; j++)
                    {
                        buf.append('0');
                    }
                    buf.append(num);
                    sb.append(buf);
                }
                else if (len == digitcount)
                {
                    sb.append(num);
                }
                else
                {
                    int index = (int)(len - digitcount);
                    sb.append(String.valueOf(num).substring(index));
                }

                dataRule.setNumbers(num);
                if (!dataRule.getBooleanAttr(DataRuleCons.CREATEATSAVE) && dataRule.getId() != null
                    && !isWebPage)
                {
                    db.save(dataRule);
                }
            }
        }
        return sb.toString();
    }

    /*
     * 得到下拉列表值
     */
    @ SuppressWarnings("unchecked")
    private Serializable getDownListData(DownListRule dataRule)
    {
        short type = dataRule.getDataSourceType();
        if (type == DataRuleCons.DATASOURCE_FIXED)
        {
            return (ArrayList<String>)JSONTools.convertJsonToValue(dataRule.getFixedValue(), true);
        }
        else if (type == DataRuleCons.DATASOURCE_DB)
        {
            DataTable dtable = dataRule.getDtable();
            DataField dfield = dataRule.getDfield();
            DataField sortfield = dataRule.getSortField();
            Vector<Vector<Object>> resultData = db.execDownListRule(dtable, dfield, sortfield,
                RFormula.convertFormula(dataRule.getFilterCond()),
                dataRule.getBooleanAttr(DataRuleCons.SORT));
            if (resultData != null && !resultData.isEmpty())
            {
                ArrayList<String> list = new ArrayList<String>();
                for (Vector<Object> obj : resultData)
                {
                    list.add(obj.get(0).toString());
                }
                return list;
            }
        }
        return null;
    }

    /*
     * 得到列表选择值
     */
    private Serializable getListSelectData(ListSelectRule dataRule)
    {
        // TODO
        return null;
    }

    /*
     * 得到树型选择值
     */
    private Serializable getTreeSelectData(TreeSelectRule dataRule)
    {
        short type = dataRule.getDataSourceType();
        if (type == DataRuleCons.DATASOURCE_FIXED)
        {
            return DataRuleUtil.getTreeSelectFixedTree(dataRule.getFixedValue());
        }
        else if (type == DataRuleCons.DATASOURCE_DB)
        {
            // TODO
        }
        return null;
    }

    /**
     * @param db 设置 db
     */
    public void setDb(IDataRuleDB db)
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