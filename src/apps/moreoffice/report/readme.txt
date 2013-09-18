代码整合：
1：修改MainConstant.java(列表宽度修改)
2：修改FileManagerPanel.java(增加"我的报表"面板)
3：修改webOffice.java(增加"我的报表"面板切换)
4：修改FilesHandler.java(增加对保持文件权限的特殊判断)
5：修改WebOffice.gwt.xml(增加对报表客户端的gwt配置)

6：修改applicationContext.xml(增加reportConfig.properties的读取)
7：修改war/cloud/index.html(增加应用)
8：修改webOffice.html(增加"我的报表"按钮、tab索引)
9：修改web.xml(增加报表自身的配置和application-report的引用)
其他：
1：GWT编译时不需要选择report.gwt.xml
2：注意网络Office对应的数据库及报表对应的数据库是否正确
3：修改eioconfig.properties(修改字符编码为utf-8)
4：如果出现the hierarchy of the type则是因为有不存在的导入类，比如gwt的序列化对象接口





开放报表功能：
1:index.html
2:eioconfig.properties
3:web.xml
4:resource.properties(plugins.zip=plugins.zip,,,false,true,0)