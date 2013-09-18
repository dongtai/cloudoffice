package apps.moreoffice.report.commons.domain.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import apps.moreoffice.report.commons.domain.Result;
import apps.moreoffice.report.commons.domain.databaseObject.DataBaseObject;
import apps.moreoffice.report.commons.domain.databaseObject.User;

/**
 * 与服务器交互的接口类，将来如果要换服务器，可以替换此接口
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-10
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface IServer
{
    /**
     * 设置报表服务
     * 
     * @param service 报表服务
     */
    void setService(IReportService service);

    /**
     * 设置服务器地址
     * 
     * @param urlPath 服务器地址
     */
    void setUrlPath(String urlPath);

    /**
     * 得到服务器地址
     * 
     * @return String 服务器地址
     */
    String getUrlPath();

    /**
     * 设置用户
     * 
     * @param user 用户
     */
    void setUser(User user);

    /**
     * 得到用户
     * 
     * @return User 用户
     */
    User getUser();

    /**
     * 登录
     * 
     * @param loginName 登录名
     * @param passWord 密码
     * @return String token
     */
    String login(String loginName, String passWord);

    /**
     * 得到参数HashMap，一些共用的参数会在此封装
     * 比如：
     * 1：用户名
     * 
     * @return HashMap<String, Object> 参数HashMap
     */
    HashMap<String, Object> getHashMap();

    /**
     * 访问服务器并返回结果
     * 
     * @param action 方法名
     * @param params 参数
     * @return IResult 结果集
     */
    Result getResult(String action, HashMap<String, Object> params);

    /**
     * 存盘单个对象
     * 
     * @param entity 数据库对象
     * @return Result 结果
     */
    Result save(DataBaseObject entity);

    /**
     * 存盘多个对象
     * 
     * @param entitys 数据库对象列表
     * @return Result 结果
     */
    Result save(ArrayList<DataBaseObject> entitys);

    /**
     * 检查连接是否正确
     * 
     * @return boolean 连接是否正确 
     */
    boolean checkConnection();

    /**
     * 保存文件
     * 
     * @param action 方法名
     * @param params 参数
     * @param filePaths 文件路径数组
     * @return Result 是否成功
     */
    Result uploadFile(String action, HashMap<String, Object> params, String[] filePaths);

    /**
     * 下载文件
     * 
     * @param downLoadType 下载类型
     * @param fileName 文件名
     * @return Result 文件
     */
    Result downLoadFile(int downLoadType, String fileName);

    /**
     * 释放对象
     */
    void dispose();
}