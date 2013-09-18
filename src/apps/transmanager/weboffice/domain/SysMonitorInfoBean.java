package apps.transmanager.weboffice.domain;


public class SysMonitorInfoBean implements SerializableAdapter
{
    /**
     * 
     */
    private static final long serialVersionUID = -857082503396457064L;
    // === 基本信息 ===
    /** 服务器名称     */
    private String serverName = "";
    /** 服务器IP */
    private String serverIP = "";
    /** 服务器域名 */
    private String serverDomain = "";
    
    // === 性能 ===
    /** 可使用内存. */
    //private String totalMemory;
    /**  剩余内存. */
    //private String freeMemory;
    /** 最大可使用内存. */
    //private String maxMemory;
    /** 操作系统. */
    private String osName = "";
    /** 总的物理内存. */
    private String totalMemorySize = "";
    /** 剩余的物理内存. */
    private String freePhysicalMemorySize = "";
    /** 已使用的物理内存. */
    private String usedMemory = "";
    /** 内存使用率   */
    private float memoryRatio;
    /** cpu频率，可能有多个CPU */
    private String cpuHZ = "";
    /** cpu使用了的频率  */
    private String usedCpuHZ = "";
    /** cpu使用率 */
    private float cpuRatio;
    /** 文档库所在存储设备容量 */
    private String diskSize = "";
    /** 文档库所在存储设备已用容量 */
    private String diskUsedSize = "";
    /** 存储设备使用率   */
    private float diskRatio;
    
    // === 网络流量  ===
    /** 上传速度 */
    private String netUpSpeed = "";
    /** 下载速度  */
    private String netDownSpeed = "";
    /** 上传流量 */
    private String netUpFlow = "";
    /** 下载流量 */
    private String netDownFlow = "";
    
    // === 安装的中间件 =====
    /** web服务器 */
    private String webServer = "";
    /** 数据库名称  */
    private String dbServer = "";
    /** File系统  */
    private String fileRepository = "";
    /** 系统运行时间  */
    private String sysRunTime = "";
    
    // === 用户信息 ===
    /** 公司名称  */ 
    private String companyName = "";
    /** 部门数量 */
    private String departmentCount = "";
    /** 帐号数量 */
    private String accountCount = "";
    /** 在线帐号 */
    private String onlineAccount = "";
    /** 在线帐号列表 */
    private String onlineAccountList = "";
    
    // === 文档信息 ===
    /** 空间总数 */
    private String spaceCount = "";
    /** 部门空间 数量 */
    private String departmentSpaceCount = "";
    /** 项目空间数量 */
    private String projectSpaceCount = "";
    /** 文档总数*/
    private String documentCount = "";
    /** 文档库的容量 */
    private String contentSize = "";
    /** 当天增加的文档数量 */
    private String todayAddDocumnetCount = "";
    /** 当天删除的文档数量 */
    private String todayDeleteDocumentCount = "";
    /** 当天修改的文档数据 */ 
    private String todayChangeDocumnetCount = "";
    
    /**
     * 
     */
    public String toString()
    {
        return "基本信息"
                + "\n    服务器名称：" + serverName
                + "\n    服务器IP：" + serverIP
                + "\n    服务器域名：" + serverDomain
                + "\n性能"
                + "\n    内存总数：" + totalMemorySize
                + "\n    已用内存：" + usedMemory
                + "\n    内存使用率：" + memoryRatio
                + "\n    CPU频率：" + cpuHZ
                + "\n    CPU已使用频率：" + usedCpuHZ
                + "\n    CPU占用率：" + cpuRatio
                + "\n    存储设备容量：" + diskSize
                + "\n    已用存储设备容量：" + diskUsedSize
                + "\n    存储设备使用率：" + diskRatio
                + "\n网络流量"
                + "\n    上传速度：" + netUpSpeed
                + "\n    下载速度：" + netDownSpeed
                + "\n    上传流量：" + netUpFlow
                + "\n    下载流量：" + netDownFlow
                + "\n安装的中间件"
                + "\n    Web服务器：" + webServer
                + "\n    DB 服务 器：" + dbServer
                + "\n    文件系统：" + fileRepository
                + "\n    系统运行时间：" + sysRunTime
                + "\n用户信息"
                + "\n    公司名称：" + companyName
                + "\n    部门数量：" + departmentCount
                + "\n    帐号数量：" + accountCount
                + "\n    在线帐号：" + onlineAccount
                + "\n    在线帐号列表：" + onlineAccountList
                + "\n文档信息"
                + "\n    空间总数：" + spaceCount
                + "\n    部门空间部数：" + departmentSpaceCount
                + "\n    项目空间总数：" + projectSpaceCount
                + "\n    文档总数：" + documentCount
                + "\n    文档库的容量：" + contentSize
                + "\n    当天增加文档数：" + todayAddDocumnetCount
                + "\n    当天修改文档数：" + todayChangeDocumnetCount
                + "\n    当天删除文档数：" + todayDeleteDocumentCount;    
    }
    /*public String getFreeMemory()
    {
        return freeMemory;
    }

    public void setFreeMemory(String freeMemory)
    {
        this.freeMemory = freeMemory;
    }*/

    public String getFreePhysicalMemorySize()
    {
        return freePhysicalMemorySize;
    }

    public void setFreePhysicalMemorySize(String freePhysicalMemorySize)
    {
        this.freePhysicalMemorySize = freePhysicalMemorySize;
    }

    /*public String getMaxMemory()
    {
        return maxMemory;
    }

    public void setMaxMemory(String maxMemory)
    {
        this.maxMemory = maxMemory;
    }*/

    public String getOsName()
    {
        return osName;
    }

    public void setOsName(String osName)
    {
        this.osName = osName;
    }

    /*public String getTotalMemory()
    {
        return totalMemory;
    }

    public void setTotalMemory(String totalMemory)
    {
        this.totalMemory = totalMemory;
    }*/

    public String getTotalMemorySize()
    {
        return totalMemorySize;
    }

    public void setTotalMemorySize(String totalMemorySize)
    {
        this.totalMemorySize = totalMemorySize;
    }

    public String getUsedMemory()
    {
        return usedMemory;
    }

    public void setUsedMemory(String usedMemory)
    {
        this.usedMemory = usedMemory;
    }

    public void setMemoryRatio(float memoryRatio)
    {
        this.memoryRatio = memoryRatio;
    }

    public float getMemoryRatio()
    {
        return memoryRatio;
    }

    public float getCpuRatio()
    {
        return cpuRatio;
    }

    public void setCpuRatio(float cpuRatio)
    {
        this.cpuRatio = cpuRatio;
    }

    public void setDiskSize(String diskSize)
    {
        this.diskSize = diskSize;
    }

    public String getDiskSize()
    {
        return diskSize;
    }

    public void setDiskUsedSize(String diskUsedSize)
    {
        this.diskUsedSize = diskUsedSize;
    }

    public String getDiskUsedSize()
    {
        return diskUsedSize;
    }

    public void setContentSize(String contentSize)
    {
        this.contentSize = contentSize;
    }

    public void setDiskRatio(float diskRatio)
    {
        this.diskRatio = diskRatio;
    }

    public float getDiskRatio()
    {
        return diskRatio;
    }

    public String getContentSize()
    {
        return contentSize;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerIP(String serverIP)
    {
        this.serverIP = serverIP;
    }

    public String getServerIP()
    {
        return serverIP;
    }

    public void setServerDomain(String serverDomain)
    {
        this.serverDomain = serverDomain;
    }

    public String getServerDomain()
    {
        return serverDomain;
    }

    public void setNetUpSpeed(String netUpSpeed)
    {
        this.netUpSpeed = netUpSpeed;
    }

    public String getNetUpSpeed()
    {
        return netUpSpeed;
    }

    public void setNetDownSpeed(String netDownSpeed)
    {
        this.netDownSpeed = netDownSpeed;
    }

    public String getNetDownSpeed()
    {
        return netDownSpeed;
    }

    public void setNetUpFlow(String netUpFlow)
    {
        this.netUpFlow = netUpFlow;
    }

    public String getNetUpFlow()
    {
        return netUpFlow;
    }

    public void setNetDownFlow(String netDownFlow)
    {
        this.netDownFlow = netDownFlow;
    }

    public String getNetDownFlow()
    {
        return netDownFlow;
    }

    public void setWebServer(String webServer)
    {
        this.webServer = webServer;
    }

    public String getWebServer()
    {
        return webServer;
    }

    public void setDbServer(String dbServer)
    {
        this.dbServer = dbServer;
    }

    public String getDbServer()
    {
        return dbServer;
    }

    public void setFileRepository(String fileRepository)
    {
        this.fileRepository = fileRepository;
    }

    public String getFileRepository()
    {
        return fileRepository;
    }

    public void setSysRunTime(String sysRunTime)
    {
        this.sysRunTime = sysRunTime;
    }

    public String getSysRunTime()
    {
        return sysRunTime;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setDepartmentCount(String departmentCount)
    {
        this.departmentCount = departmentCount;
    }

    public String getDepartmentCount()
    {
        return departmentCount;
    }

    public void setAccountCount(String accountCount)
    {
        this.accountCount = accountCount;
    }

    public String getAccountCount()
    {
        return accountCount;
    }

    public void setOnlineAccount(String onlineAccount)
    {
        this.onlineAccount = onlineAccount;
    }

    public String getOnlineAccount()
    {
        return onlineAccount;
    }

    public void setOnlineAccountList(String onlineAccountList)
    {
        this.onlineAccountList = onlineAccountList;
    }

    public String getOnlineAccountList()
    {
        return onlineAccountList;
    }

    public void setSpaceCount(String spaceCount)
    {
        this.spaceCount = spaceCount;
    }

    public String getSpaceCount()
    {
        return spaceCount;
    }

    public void setDepartmentSpaceCount(String departmentSpaceCount)
    {
        this.departmentSpaceCount = departmentSpaceCount;
    }

    public String getDepartmentSpaceCount()
    {
        return departmentSpaceCount;
    }

    public void setProjectSpaceCount(String projectSpaceCount)
    {
        this.projectSpaceCount = projectSpaceCount;
    }

    public String getProjectSpaceCount()
    {
        return projectSpaceCount;
    }

    public void setDocumentCount(String documentCount)
    {
        this.documentCount = documentCount;
    }

    public String getDocumentCount()
    {
        return documentCount;
    }

    public void setTodayAddDocumnetCount(String todayAddDocumnetCount)
    {
        this.todayAddDocumnetCount = todayAddDocumnetCount;
    }

    public String getTodayAddDocumnetCount()
    {
        return todayAddDocumnetCount;
    }

    public void setTodayDeleteDocumentCount(String todayDeleteDocumentCount)
    {
        this.todayDeleteDocumentCount = todayDeleteDocumentCount;
    }

    public String getTodayDeleteDocumentCount()
    {
        return todayDeleteDocumentCount;
    }

    public void setTodayChangeDocumnetCount(String todayChangeDocumnetCount)
    {
        this.todayChangeDocumnetCount = todayChangeDocumnetCount;
    }

    public String getTodayChangeDocumnetCount()
    {
        return todayChangeDocumnetCount;
    }

    public void setCpuHZ(String cpuHZ)
    {
        this.cpuHZ = cpuHZ;
    }

    public String getCpuHZ()
    {
        return cpuHZ;
    }

    public void setUsedCpuHZ(String usedCpuHZ)
    {
        this.usedCpuHZ = usedCpuHZ;
    }

    public String getUsedCpuHZ()
    {
        return usedCpuHZ;
    }
}