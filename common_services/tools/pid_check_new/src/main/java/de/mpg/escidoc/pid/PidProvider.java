package de.mpg.escidoc.pid;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.util.HandleUpdateStatistic;
import de.mpg.escidoc.util.LocatorCheckStatistic;
import de.mpg.escidoc.util.Util;


public class PidProvider extends AbstractPidProvider
{
    private static Logger logger = Logger.getLogger(PidProvider.class);  
    
    private String location;
    private String user;
    private String password;
    private String server;
    
    final private int port = 80;
    
    private HttpClient httpClient;

    public PidProvider() throws Exception
    {
        this.init();
    }
    
    public void init() throws Exception
    {
        logger.debug("init starting");
        
        super.init();
        
        location = PropertyReader.getProperty("escidoc.pidcache.service.url");
        user = PropertyReader.getProperty("escidoc.pidcache.user.name");
        password = PropertyReader.getProperty("escidoc.pidcache.user.password");
        
        server = PropertyReader.getProperty("escidoc.pidcache.server");
        
        httpClient = getHttpClient();
        httpClient.getParams().setAuthenticationPreemptive(true);

        logger.debug("init finished");
    }
    
    public static HttpClient getHttpClient()
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        return httpClient;
    }
    
    public String getPid(String irItemId, HandleUpdateStatistic statistic) throws PIDProviderException
    {
        logger.debug("getPid starting");
        
        int code;
        String registerUrl = "";
        String pidCacheUrl = location + "/write/create";
        
        PostMethod method = new PostMethod(pidCacheUrl);
        
        try
        {
            registerUrl = getRegisterUrl(irItemId);
        }
        catch (Exception e)
        {
            logger.warn("Error occured when registering Url for <" + irItemId + ">");
            throw new PIDProviderException(e.getMessage(), irItemId);
        }
        
        method.setParameter("url", registerUrl);
        
        String pid = "";
        long start = System.currentTimeMillis();
        try
        {
            httpClient.getState().setCredentials(new AuthScope(server, port),
                    new UsernamePasswordCredentials(user, password));
            
            code = httpClient.executeMethod(method);
            
            pid = Util.getValueFromXml("<pid>", '<', method.getResponseBodyAsString());
            if (code != HttpStatus.SC_CREATED || "".equals(pid))
            {
                throw new PIDProviderException("Problem getting a PID for <" + irItemId + ">", irItemId);
            }
            statistic.incrementHandlesCreated();
            logger.info("pid create returning " + method.getResponseBodyAsString());
        }
        catch (Exception e)
        {
            throw new PIDProviderException(e.getMessage(), irItemId);
        }
        long end = System.currentTimeMillis();
        
        logger.info("Time used for getting pid <" + (end - start) + ">ms");
       
        return pid;
    }

    public int updatePid(String pid, String irItemId, HandleUpdateStatistic statistic)
    {
        logger.debug("updatePid starting");
        
        if ("".equals(irItemId))
        {
            statistic.incrementHandlesNotFound();
            successMap.put(pid, "NOT USED");
            return 0;
        }
        
        int code = HttpStatus.SC_OK;
        String newUrl = "";
        String pidCacheUrl = location + "/write/modify";
        
        PostMethod method = null;
        method = new PostMethod(pidCacheUrl.concat("?pid=").concat(pid));
        
        try
        {
            newUrl = getRegisterUrl(irItemId);
            logger.info("Register Url for <" + pid + "> " + " <"+ irItemId + ">");
        }
        catch (Exception e)
        {
            logger.warn("Error occured when getting Url for <" + irItemId + ">");
        }
        
        method.setParameter("url", newUrl);
        
        long start = System.currentTimeMillis();
        try
        {
            httpClient.getState().setCredentials(new AuthScope(server, port),
                    new UsernamePasswordCredentials(user, password));
            
            code = httpClient.executeMethod(method);

            if (code != HttpStatus.SC_OK)
            {
                logger.warn("Problem updating a pid <" + pid + ">" + "with newUrl <" + newUrl + ">");
                failureMap.put(pid, newUrl);
                statistic.incrementHandlesUpdateError();
            }
            else
            {
                successMap.put(pid, newUrl);
                statistic.incrementHandlesUpdated();
            }
   
            logger.info("pid update returning code <" + code + ">" + method.getResponseBodyAsString());
        }
        catch (Exception e)
        {
            logger.warn("Error occured when registering Url for <" + irItemId + ">" );
            statistic.incrementHandlesUpdateError();
        }
        
        long end = System.currentTimeMillis();
        
        logger.info("Time used for updating pid <" + (end - start) + ">ms");
        
        return code;
    }
    
    public int resolvePid(String pid, HandleUpdateStatistic statistic)
    {
        logger.info("resolvePid startingfor <" + pid + ">");

        StringBuffer b = new StringBuffer("http://hdl.handle.net/");
        b.append(pid.startsWith("hdl:") ? pid.substring(4) : pid);

        int code = HttpStatus.SC_OK;
        
        GetMethod method =  new GetMethod(b.toString());
        method.setFollowRedirects(true);
        
        long start = System.currentTimeMillis();
        try
        {
        	httpClient.getParams().setAuthenticationPreemptive(true);
        	
            httpClient.getState().setCredentials(new AuthScope(PropertyReader.getProperty("escidoc.pidcache.server"), port), 
        			new UsernamePasswordCredentials(PropertyReader.getProperty("framework.admin.username"), PropertyReader.getProperty("framework.admin.password")));
            
            code = httpClient.executeMethod(method);

            if (code != HttpStatus.SC_OK)
            {               
                failureMap.put(pid, "http code " + code);
                logger.warn("Problem when resolving <" + pid + "> http code " + code);
                statistic.incrementHandlesNotFound();
            }
            else
            {
                successMap.put(pid, "http code " + code);
                statistic.incrementHandlesUpdated();
            }              
        }
        catch (Exception e)
        {
        	statistic.incrementHandlesUpdateError();
        	failureMap.put(pid, e.toString());
            logger.warn("Error occured when resolving Url for <" + pid + ">" );
        }   
        finally
        {
        	method.releaseConnection();
        	
        }
        long end = System.currentTimeMillis();        
        logger.info("Time used for resolving pid <" + (end - start) + ">ms");
        
        return code;   
    }
    
    public int resolveLocator(String locatorUrl, LocatorCheckStatistic statistic)
    {
        logger.debug("resolveLocator <" + locatorUrl + ">");

        int code = HttpStatus.SC_OK;
        
        statistic.incrementTotal();
        
        int idx = locatorUrl.lastIndexOf("|");
        GetMethod method =  new GetMethod(locatorUrl.substring(idx+1).trim());
        method.setFollowRedirects(true);
        
        long start = System.currentTimeMillis();
        try
        {
            httpClient.getState().setAuthenticationPreemptive(true);
            
            code = httpClient.executeMethod(method);

            if (code != HttpStatus.SC_OK)
            {               
                failureMap.put(locatorUrl, "http code " + code);
                logger.warn("Problem when resolving <" + locatorUrl + "> http code " + code);
                statistic.incrementLocatorsNotResolved();
            }
            else
            {
                successMap.put(locatorUrl, "http code " + code);
                statistic.incrementLocatorsResolved();
            }              
        }
        catch (Exception e)
        {
            statistic.incrementLocatorsNotResolved();
            failureMap.put(locatorUrl, e.toString());
            logger.warn("Error occured when resolving Url for <" + locatorUrl + ">" );
        }   
        finally
        {
            method.releaseConnection();
            
        }
        long end = System.currentTimeMillis();        
        logger.info("Time used for resolving pid <" + (end - start) + ">ms");
        
        return code;   
    }

    private String getRegisterUrl(String itemId) throws Exception
    {
        String registerUrl =  PropertyReader.getProperty("escidoc.pubman.instance.url") +
                PropertyReader.getProperty("escidoc.pubman.instance.context.path") + itemId;
                
        return registerUrl;
    }
}
