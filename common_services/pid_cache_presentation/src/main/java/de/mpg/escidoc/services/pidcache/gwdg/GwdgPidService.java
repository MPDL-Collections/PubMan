package de.mpg.escidoc.services.pidcache.gwdg;

import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;

/**
 * Class handling GWDG PID service interface
 * 
 * @author saquet
 *
 */
public class GwdgPidService 
{
	public static String GWDG_PIDSERVICE_CREATE = null;
    public static String GWDG_PIDSERVICE_VIEW = null;
    public static String GWDG_PIDSERVICE_FIND = null;
    public static String GWDG_PIDSERVICE = null;
	public static String GWDG_PIDSERVICE_EDIT = null;
	public static String GWDG_PIDSERVICE_DELETE = null;
	
	private static final Logger logger = Logger.getLogger(GwdgPidService.class);
	
	/**
	 * Default constructor
	 * @throws Exception
	 */
	public GwdgPidService() throws Exception
	{
		GWDG_PIDSERVICE = PropertyReader.getProperty("escidoc.pid.gwdg.service.url");
    	GWDG_PIDSERVICE_CREATE = PropertyReader.getProperty("escidoc.pid.service.create.path");
    	GWDG_PIDSERVICE_VIEW = PropertyReader.getProperty("escidoc.pid.service.view.path");
    	GWDG_PIDSERVICE_FIND = PropertyReader.getProperty("escidoc.pid.service.search.path");
    	GWDG_PIDSERVICE_EDIT = PropertyReader.getProperty("escidoc.pid.service.update.path");
    	GWDG_PIDSERVICE_DELETE = PropertyReader.getProperty("escidoc.pid.service.delete.path");
    	// Use to simulate non available gwdg
    	//GWDG_PIDSERVICE = GWDG_PIDSERVICE.concat("/out");
	}
	
	/**
	 * Constructor to set the availability of the {@link GwdgPidService}
	 * @param available
	 */
	public GwdgPidService(boolean available)
	{
		if (!available) 
		{
			GWDG_PIDSERVICE = GWDG_PIDSERVICE.concat("/out");
		}
	}
	
		
	/**
	 * Calls GWDG PID manager interface:
	 * 
	 * 	- http://handle.gwdg.de:8080/pidservice/write/create
	 * 
	 * @param url: 
	 * @return
	 */
	public String create(String url) throws Exception
	{
		PostMethod create = new PostMethod(GWDG_PIDSERVICE.concat(GWDG_PIDSERVICE_CREATE));
    	create.setParameter("url", url);
    	GwdgClient client = new GwdgClient();
    	ProxyHelper.executeMethod(client, create);
    	logger.info("New pid created.");
    	
    	return create.getResponseBodyAsString();
	}
	
	/**
	 * Calls GWDG PID manager interface:
	 * 
	 * 	- http://handle.gwdg.de:8080/pidservice/read/view
	 * 
	 * @return
	 */
	public String retrieve(String id) throws Exception
	{
		GwdgClient client = new GwdgClient();
		GetMethod retrieve = new GetMethod(GWDG_PIDSERVICE.concat(GWDG_PIDSERVICE_VIEW).concat("?pid=").concat(id));
    	ProxyHelper.executeMethod(client, retrieve);
    	
    	return retrieve.getResponseBodyAsString();
	}
	
	/**
	 * Calls GWDG PID manager interface via:
	 * 
	 * 	- http://handle.gwdg.de:8080/pidservice/read/search
	 * 
	 * @param url
	 * @return
	 */
	public String search(String url) throws Exception
	{
		GwdgClient client = new GwdgClient();
		GetMethod search = new GetMethod(GWDG_PIDSERVICE.concat(GWDG_PIDSERVICE_FIND).concat("?url=").concat(url));
    	ProxyHelper.executeMethod(client, search);
    	return search.getResponseBodyAsString();
	}
	
	/**
	 * Calls GWDG PID manager interface:
	 * 
	 * 	- http://handle.gwdg.de:8080/pidservice/write/edit
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String update(String id, String url) throws Exception
	{
		PostMethod update = new PostMethod(GWDG_PIDSERVICE.concat(GWDG_PIDSERVICE_EDIT).concat("?pid=").concat(id));
    	update.setParameter("url", url);
    	GwdgClient client = new GwdgClient();
    	ProxyHelper.executeMethod(client, update);
    	return update.getResponseBodyAsString();
	}
	
	/**
	 * Calls GWDG PID manager interface:
	 * 
	 * 	- http://handle.gwdg.de:8080/pidservice/
	 * 
	 * @return
	 */
	public String delete(String id) throws Exception
	{
		DeleteMethod delete = new DeleteMethod(GWDG_PIDSERVICE.concat(GWDG_PIDSERVICE_DELETE).concat("?pid=").concat(id));
		GwdgClient client = new GwdgClient();
    	ProxyHelper.executeMethod(client, delete);
    	return delete.getResponseBodyAsString();
	}

	/**
	 * True if GWDG PID service is available.
	 * False if not.
	 * @return
	 */
	public boolean available()
	{
		GetMethod method = new GetMethod(GWDG_PIDSERVICE);
		try
		{	GwdgClient client = new GwdgClient();
			//client.getHttpConnectionManager().getParams().setConnectionTimeout(GwdgClient.GWDG_SERVICE_TIMEOUT);
	    	ProxyHelper.executeMethod(client, method);
		} 
		catch (Exception e) 
		{
			return false;
		}
		if (method.getStatusCode() == 200) 
		{
			return true;
		}
    	return false;
	}
}
