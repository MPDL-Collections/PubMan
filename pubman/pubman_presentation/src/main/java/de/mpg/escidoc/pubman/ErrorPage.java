/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.org/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman;

import java.io.IOException;

import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;
import de.mpg.escidoc.services.search.parser.ParseException;

/**
 * BackingBean for ErrorPage.jsp.
 * Use this class to display error messages in a seperate page. 
 * Don't forget in the calling component to set the exception as the reason for this error before you display the page!
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by DiT: 14.08.2007
 */
public class ErrorPage extends BreadcrumbPage
{   
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ErrorPage.class);
    
    // used by calling components to get this Bean
    public final static String BEAN_NAME = "ErrorPage";
    // Faces navigation string
    public final static String LOAD_ERRORPAGE = "loadErrorPage";
    //Faces navigation string for GUI Tool
    public final static String GT_LOAD_ERRORPAGE = "loadGTErrorPage";
    //The referring GUI Tool Page
    public final static String GT_ERRORPAGE = "faces/GTErrorPage.jsp";
    // JSP-Name for avoiding JSF-Navigation
    public final static String JSP_NAME = "ErrorPage.jsp";
    
    private Exception exception = null;    
    private HtmlPanelGrid panPageAlert = new HtmlPanelGrid();
    
    private String summary = null;
    private String detail = null;


    /**
     * Public constructor.
     */
    public ErrorPage()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page is navigated to, either directly via a URL, or indirectly via page
     * navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        
        // show the pageAlert
        this.createPageAlert();
        
       
    }

    /**
     * Sets all attributes of the pageAlert component according to the exception set before.
     *
     */
    private void createPageAlert()
    {
        // remove all elements
        this.panPageAlert.getChildren().clear();

        if (this.exception == null)
        {
            // no exception has been set before            
            logger.warn("An errorPage should be displayed with no exception set before.");
            
            summary = "The last operation did not complete for an unknown reason.";
            detail = "No Exception was set to display.";            
        }
        // added by NiH
        else if ( exception instanceof ParseException )
        {
            summary = getMessage("search_ParseError");
            detail = this.getStackTrace();          
        }
        /*
        // this exception indicates that the user tried to accept an item without changing it; if this exception is no longer thrown by the framework we should have to check for changes of the item manually
        else if (exception instanceof PubItemStatusInvalidException)
        {
            bundleMessage = ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle());
            summary = this.bundleMessage.getString("itemHasNotBeenChanged");
            detail = this.exception.getClass().toString();            
        } 
        */       
        else
        {
            // an exception has been set before
            if (this.exception != null && this.exception.getCause() != null)
            {
                summary = this.exception.getCause().toString();
                detail = this.getStackTrace();
            }
            else if (this.exception != null)
            {
                summary = this.exception.toString();
                detail = this.getStackTrace();
            }
        }
        
        // set the attributes of the pageAlert component

        error(summary, detail);
        HtmlMessages pageAlert = new HtmlMessages();
        pageAlert.setId(FacesContext.getCurrentInstance().getViewRoot().createUniqueId());
//        pageAlert.setTitle(title);
//        pageAlert.setSummary(summary);
//        pageAlert.setDetail(detail);
        
        this.panPageAlert.getChildren().add(pageAlert);
    }
    
    /**
     * Redirects to the referring GUI Tool page.
     * @author Tobias Schraut
     * @return a navigation string
     */
    protected String redirectToGUITool()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect(GT_ERRORPAGE);
        }
        catch (IOException e)
        {
            logger.error("Could not redirect to GUI Tool ErrorPage." + "\n" + e.toString());
        }
        return "";
    }
    
    public String getStackTrace()
    {
        StringBuffer buffer = new StringBuffer();
        if (exception != null)
        {
            StackTraceElement[] stackTrace = exception.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                buffer.append(" at ");
                buffer.append(stackTraceElement.getClassName());
                buffer.append(" (");
                buffer.append(stackTraceElement.getLineNumber());
                buffer.append(")\n");
            }
        }
        return buffer.toString();
    }
    
  
    /**
     * Returns the panel with the pageAlert.
     * @return the panel with the pageAlert
     */
    public HtmlPanelGrid getPanPageAlert()
    {
        this.createPageAlert();
        
        return panPageAlert;
    }

    public String getSummary()
    {
        return summary;
    }

    public String getDetail()
    {
        return detail;
    }

    /**
     * Sets the panel with the pageAlert.
     * @param panPageAlert the new pageAlert component
     */
    public void setPanPageAlert(HtmlPanelGrid panPageAlert)
    {
        this.panPageAlert = panPageAlert;
    }

    /**
     * Returns the exception this pageAlert will display.
     * @return the exception of the pageAlert
     */
    public Exception getException()
    {
        return exception;
    }

    /**
     * Sets a new exception that should be displayed by the pageAlert.
     * @param exception the exception that should be displayed
     */
    public void setException(Exception exception)
    {
        this.exception = exception;
        this.init();
    }

    @Override
    public boolean isItemSpecific() 
    {
        return true;
    }

    
}
