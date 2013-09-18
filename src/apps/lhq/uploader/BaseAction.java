package apps.lhq.uploader;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport
{

    public BaseAction()
    {
    }

    public void outJsonString(String str)
    {
        getResponse().setContentType("text/javascript;charset=UTF-8");
        outString(str);
    }

    public void outString(String str)
    {
        try
        {
            PrintWriter out = getResponse().getWriter();
            out.write(str);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void outXMLString(String xmlStr)
    {
        getResponse().setContentType("application/xml;charset=UTF-8");
        outString(xmlStr);
    }

    public HttpServletRequest getRequest()
    {
        return ServletActionContext.getRequest();
    }

    public HttpServletResponse getResponse()
    {
        return ServletActionContext.getResponse();
    }

    public HttpSession getSession()
    {
        return getRequest().getSession();
    }

    public ServletContext getServletContext()
    {
        return ServletActionContext.getServletContext();
    }

    public String getRealyPath(String path)
    {
        return getServletContext().getRealPath(path);
    }

    public String jsonString;
}
