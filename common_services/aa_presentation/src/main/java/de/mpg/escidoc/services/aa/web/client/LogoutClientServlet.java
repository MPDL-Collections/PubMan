package de.mpg.escidoc.services.aa.web.client;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mpg.escidoc.services.aa.Config;

/**
 * 
 * @author haarlaender
 *
 */
public class LogoutClientServlet extends HttpServlet {
	
	 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {

        String context = request.getContextPath();
        System.out.println(request.getSession().getAttribute("user") + " " + request.getSession().getAttribute("authentication") );
        
        try
        {
            String clientClassName = Config.getProperty("escidoc.aa.client.logout.class");
            if (clientClassName != null)
            {
               
                Class clientClass = Class.forName(clientClassName);
                LogoutClient client = (LogoutClient) clientClass.newInstance();
                client.process(request, response);
            }
            else
            {
                new LogoutClient().process(request, response);
            }
            
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

}
