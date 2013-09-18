package apps.moreoffice.report.server.service.manager.webOffice;

import java.util.ArrayList;

import apps.moreoffice.report.commons.domain.databaseObject.Template;
import apps.moreoffice.report.commons.domain.databaseObject.User;
import apps.moreoffice.report.commons.domain.info.TemplateSort;
import apps.moreoffice.report.server.resource.TemplateSortResource;
import apps.moreoffice.report.server.service.manager.IUserManager;
import apps.moreoffice.report.server.service.manager.TemplateManager;
import apps.moreoffice.report.server.service.manager.dataCenter.ITemplateLib;
import apps.transmanager.weboffice.domain.Fileinfo;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.jcr.JCRService;

/**
 * 与webOffice的文档库整合
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-7-6
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public class WebOfficeTemplateLib implements ITemplateLib
{
    // 模板管理器
    private TemplateManager templateM;
    // 用户管理器
    private IUserManager userM;

    /**
     * 初始化
     */
    public void init()
    {
        try
        {
            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            if (!jcrService.isPathExist(TemplateSortResource.REPORTROOTNODE))
            {
                jcrService.init(TemplateSortResource.REPORTROOTNODE);
                // TemplateSortResource.TEMPLATEPATH 在上面的init方法中已经初始化了，包括回收站等
                jcrService.createFolder("system", TemplateSortResource.TEMPLATEPATH,
                    TemplateSortResource.BASEDATA);
                jcrService.createFolder("system", TemplateSortResource.TEMPLATEPATH,
                    TemplateSortResource.BUSINESSDATA);
                jcrService.createFolder("system", TemplateSortResource.TEMPLATEPATH,
                    TemplateSortResource.ANALYSISDATA);
            }
            if (!jcrService.isPathExist(TemplateSortResource.WORKFLOWROOTNODE))
            {
                jcrService.init(TemplateSortResource.WORKFLOWROOTNODE);
                jcrService.createFolder("system", TemplateSortResource.WORKFLOWPATH,
                    TemplateSortResource.BASEFLOW);
                jcrService.createFolder("system", TemplateSortResource.WORKFLOWPATH,
                    TemplateSortResource.BUSINESSFLOW);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * 得到指定路径下的模板分类
     * 
     * @param path 路径
     * @return ArrayList<TemplateSort> 指定路径下的模板分类
     */
    public ArrayList<TemplateSort> getTemplateSort(String path)
    {
        try
        {
            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            path = getPath(path);
            // 得到当前目录下的所有模板
            ArrayList<Fileinfo> fileInfos = jcrService.listFileinfos("", path);
            ArrayList<TemplateSort> templateSorts = new ArrayList<TemplateSort>();
            if (fileInfos != null && !fileInfos.isEmpty())
            {
                TemplateSort templateSort;
                for (Fileinfo fileinfo : fileInfos)
                {
                    if (!fileinfo.isFold())
                    {
                        continue;
                    }

                    templateSort = new TemplateSort();
                    templateSort.setName(fileinfo.getFileName());
                    templateSort.setPath(fileinfo.getPathInfo());

                    templateSorts.add(templateSort);
                }

                if (!templateSorts.isEmpty())
                {
                    return templateSorts;
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        return null;
    }

    /**
     * 得到外部数据源分类
     * 
     * @return TemplateSort 外部数据源分类
     */
    public TemplateSort getExternalTemplateSort()
    {
        TemplateSort templateSort = new TemplateSort();
        templateSort.setName(TemplateSortResource.EXTERNALDATASOURCE);
        templateSort.setPath(TemplateSortResource.EXTERNALPATH);
        return templateSort;
    }

    /**
     * 创建模板分类
     * 
     * @param userID 用户ID
     * @param path 路径
     * @param name 名称
     */
    public void createTemplateSort(long userID, String path, String name)
    {
        try
        {
            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            path = getPath(path);

            User user = userM.getUser(userID);
            jcrService.createFolder(user.getUserName(), path, name);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * 重命名模板分类
     * 
     * @param userID 用户ID
     * @param path 路径
     * @param name 名称
     */
    public void renameTemplateSort(long userID, String path, String name)
    {
        try
        {
            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            path = getPath(path);

            jcrService.rename("", path, name);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * 得到当前路径下的模板
     * 
     * @param nodePath 路径
     * @param recursive 是否递归得到所有子目录中的模板
     * @return ArrayList<Template> 模板列表
     */
    public ArrayList<Template> getTemplateList(String nodePath, boolean recursive)
    {
        return getTemplateList(nodePath, recursive, 0, Integer.MAX_VALUE);
    }

    /**
     * 得到当前路径下的指定模板
     * 
     * @param nodePath 路径
     * @param recursive 是否递归得到所有子目录中的模板
     * @param startIndex 开始序号
     * @param number 总数
     * @return ArrayList<Template> 模板列表
     */
    public ArrayList<Template> getTemplateList(String nodePath, boolean recursive, int startIndex,
        int number)
    {
        try
        {
            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            ArrayList<Template> templates = new ArrayList<Template>();
            if (TemplateSortResource.EXTERNALPATH.equals(nodePath))
            {
                return templates;
            }

            nodePath = getPath(nodePath);
            getTemplateList(jcrService, templates, nodePath, recursive);

            if (!templates.isEmpty())
            {
                return templates;
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        return null;
    }

    /*
     * 循环得到所有子节点中的模板对象
     */
    private ArrayList<Template> getTemplateList(JCRService jcrService,
        ArrayList<Template> templates, String path, boolean recursive)
    {
        try
        {
            // 得到文档库中当前目录下的所有路径
            ArrayList<Fileinfo> fileInfos = jcrService.listFileinfos("", path);
            if (fileInfos != null && !fileInfos.isEmpty())
            {
                Template template;
                for (Fileinfo fileinfo : fileInfos)
                {
                    // 如果是文件夹，则继续遍历
                    if (fileinfo.isFold())
                    {
                        if (recursive)
                        {
                            getTemplateList(jcrService, templates, fileinfo.getPathInfo(),
                                recursive);
                        }
                        continue;
                    }

                    String fileName = fileinfo.getFileName();
                    int index = fileName.lastIndexOf(".");
                    fileName = index > 0 ? fileName.substring(0, index) : fileName;
                    // 如果是模板
                    template = templateM.getTemplateByName(fileName);
                    if (template != null)
                    {
                        templates.add(template);
                    }
                    else
                    {
                        // TODO 是否要做容错处理？
                    }
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }

    /**
     * 删除指定路径
     * 
     * @param path 路径
     */
    public void deletePath(String path)
    {
        try
        {
            JCRService jcrService = (JCRService)ApplicationContext.getInstance().getBean(
                JCRService.NAME);
            path = getPath(path);

            jcrService.delete(path);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /*
     * 转换Path
     */
    private String getPath(String path)
    {
        if (path == null || path.length() < 1)
        {
            path = TemplateSortResource.TEMPLATEPATH;
        }
        else if (!path.startsWith(TemplateSortResource.TEMPLATEPATH)
            && !path.startsWith(TemplateSortResource.WORKFLOWPATH))
        {
            path = TemplateSortResource.TEMPLATEPATH + path;
        }

        return path;
    }

    /**
     * @param templateM 设置 templateM
     */
    public void setTemplateM(TemplateManager templateM)
    {
        this.templateM = templateM;
    }

    /**
     * @param userM 设置 userM
     */
    public void setUserM(IUserManager userM)
    {
        this.userM = userM;
    }
}