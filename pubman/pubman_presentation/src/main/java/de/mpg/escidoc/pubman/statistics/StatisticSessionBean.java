package de.mpg.escidoc.pubman.statistics;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.common.StatisticLogger;
import de.mpg.escidoc.services.framework.AdminHelper;


public class StatisticSessionBean extends FacesBean
{
    
    public static String BEAN_NAME = "StatisticSessionBean";
    private String uuid;
    
    //private static Logger logger = Logger.getLogger(StatisticSessionBean.class);
    
    
    public StatisticSessionBean()
    {
        //logNewUser();
        
        
    }


    /*
    private void logNewUser()
    {
        try
        {
            InitialContext ic = new InitialContext();
            StatisticLogger sl = (StatisticLogger) ic.lookup(StatisticLogger.SERVICE_NAME);
            sl.logNewUser(getSessionId(), getIP(), getUserAgent(), getReferer(), "pubman", AdminHelper.getAdminUserHandle());
        }
        catch (Exception e)
        {
            logger.error("Could not log new user.", e);
        }
             
    }
    */
    
    
}
