package apps.moreoffice.report.commons.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import apps.moreoffice.report.commons.domain.constants.ErrorCodeCons;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.info.NodeInfo;
import apps.moreoffice.report.commons.domain.resource.ReportCommonResource;
import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * 结果集
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
public class Result implements SerializableAdapter
{
    // 序列化ID
    private static final long serialVersionUID = 4598031897488286680L;
    // 是否有错误
    private boolean hasError;
    // 错误代码
    private int errorCode;
    // 错误信息
    private String errorMessage = ReportCommonResource.NO_ERROR;

    // 只有给编辑器使用时，才能设置data，给客户端返回的结果最好不要设置data
    private Serializable data;

    // GWT编译需要确定具体的对象，因此用Serializable无法序列化，只能按类型设置
    // 节点信息
    private ArrayList<NodeInfo> nodeInfos;

    /**
     * 得到返回内容是否有错误
     * 
     * @return boolean true：有错误，false：无错误
     */
    public boolean hasError()
    {
        return hasError;
    }

    /**
     * 设置错误代码
     * 具体的错误代码常量见ErrorCodeCons
     * 
     * @param errorCode 错误代码
     */
    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
        if (errorCode != ErrorCodeCons.NO_ERROR)
        {
            hasError = true;
        }
    }

    /**
     * 得到错误代码
     * 
     * @return int 错误代码(具体的错误代码常量见ErrorCodeCons)
     */
    public int getErrorCode()
    {
        return errorCode;
    }

    /**
     * 设置错误信息
     * 
     * @param errorMessage 错误信息
     */
    public void setErrorMessage(String errorMessage)
    {
        hasError = true;
        this.errorMessage = errorMessage;
    }

    /**
     * 得到错误信息
     * 
     * @return String 错误信息
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * 设置数据
     * 
     * @param data 数据
     */
    public void setData(Serializable data)
    {
        if (!checkData(data))
        {
            this.data = data;
        }
        else
        {
            setErrorCode(ErrorCodeCons.RESULT_FORMAT_ERROR);
        }
    }

    /*
     * 检查数据的有效性
     */
    @ SuppressWarnings("rawtypes")
    private boolean checkData(Serializable data)
    {
        if (data instanceof List)
        {
            List listResult = (List)data;
            for (Object item : listResult)
            {
                if (!(item instanceof Serializable))
                {
                    return true;
                }
                else if (item instanceof List)
                {
                    return checkData((Serializable)item);
                }
            }
        }
        return false;
    }

    /**
     * 得到数据
     * 
     * @return Serializable 数据
     */
    public Serializable getData()
    {
        return data;
    }

    public ArrayList<NodeInfo> getNodeInfos()
    {
        return nodeInfos;
    }

    public void setNodeInfos(ArrayList<NodeInfo> nodeInfos)
    {
        this.nodeInfos = nodeInfos;
    }

    /**
     * 得到boolean值
     * 
     * @return boolean boolean值
     */
    public boolean getBoolean()
    {
        if (data instanceof Boolean)
        {
            return (Boolean)data;
        }
        else
        {
            return false;
        }
    }

    /**
     * 得到系统数据库记录集
     * 
     * @return List<DataBaseObject> 系统数据库记录集
     */
    @ SuppressWarnings("unchecked")
    public List<DataBaseObject> getSysDatas()
    {
        if (data instanceof List)
        {
            return (List<DataBaseObject>)data;
        }
        else
        {
            return null;
        }
    }

    /**
     * 判断是否包含序列化对象
     * 
     * @return boolean true：包含；false：不包含
     */
    @ SuppressWarnings("rawtypes")
    public boolean hasDataBaseObject()
    {
        if (data instanceof DataBaseObject)
        {
            return true;
        }
        if (data instanceof List && ((List)data).size() > 0
            && ((List)data).get(0) instanceof DataBaseObject)
        {
            return true;
        }
        return false;
    }
}