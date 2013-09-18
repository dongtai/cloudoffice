一、分布式部署：
cd WEB-INF/classes
java -Xmx1024M -Xms1024M -XX:PermSize=128M -XX:MaxPermSize=384M dcs.server.ConvertRmiServer

二、单一机器内集群及无人值守
如果启动ConvertRMIClusterServer一台机器内的集群,可以自动重启
请先修改rmicluster.properties文件的directory
directory = WEB-INF\\classes所在的路径
cd WEB-INF/classes
java dcs.server.ConvertRMIClusterServer

       jvm
      |    |
    jvm   jvm

三、多台机器的集群及无人值守
分机器运行：
java dcs.server.ConvertRMIClusterServer name port ip(rmiclusterRoot.properties)

java dcs.server.ConvertRMIClusterRootServer

无人值守方案。将会是每台机器上ConvertRMIClusterServer作为一个子。
       jvm
      |    |
    jvm   jvm
  |   |  |   |
jvm  jvm jvm jvm
