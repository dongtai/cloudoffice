package apps.transmanager.weboffice.servlet.server;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.transmanager.weboffice.service.handler.FilesHandler;


// // 该类将要删除，所有的请求统一到一个类中控制
public class UploadServlet extends HttpServlet
{
    
    /**
     * 
     *(non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     *
     */
    public void init() throws ServletException
    {
        super.init();
    }

    /**
     * 
     *(non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     *
     */
    public void service(HttpServletRequest request, HttpServletResponse res) throws ServletException,
        IOException
    {
    	FilesHandler.uploadFile(request, res);
    }
    
}
