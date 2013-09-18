package apps.transmanager.weboffice.service.task;

import java.util.HashMap;

import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.sysreport.SysMonitorTask;

/**
 * 系统监控触发器
 * <p>
 * <p>
 * <p>
 * 负责小组:        WebOffice
 * <p>
 * <p>
 */
public class SysMonitorJob
{
    public void excuteJob()
    {
        SysMonitorTask.instance().excuteTask();
        
      //到0点就清空手机注册的信息
        WebConfig.mobileregistmap=new HashMap();//存放手机注册码
    }

}
