package de.mpg.escidoc.services.pidcache;

import javax.servlet.http.HttpServletResponse;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.PidServiceResponseVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.pidcache.gwdg.GwdgClient;
import de.mpg.escidoc.services.pidcache.gwdg.GwdgPidService;
import de.mpg.escidoc.services.pidcache.process.CacheProcess;
import de.mpg.escidoc.services.pidcache.tables.Cache;
import de.mpg.escidoc.services.pidcache.tables.Queue;

/**
 * Implement the PID cache service
 *  
 * @author saquet
 *
 */
public class PidCacheService 
{
	private Pid pid = null;
	private Cache cache = null;
	private Queue queue = null;
	private GwdgPidService gwdgPidService = null;
	private XmlTransforming xmlTransforming = null;
	
	private int status = HttpServletResponse.SC_OK;
	private String location = "http://hdl.handle.net/XXX_LOCATION_XXX?noredirect";
	

	
	/**
	 * Default constructor
	 * @throws Exception
	 */
	public PidCacheService() throws Exception
	{
		cache = new Cache();
		queue = new Queue();
		gwdgPidService = new GwdgPidService();
		xmlTransforming = new XmlTransformingBean();
	}
	
	 /**
     * This method does the following:
     *  - Take a PID from the cache
     *  - Change the URL of the PID
     *  - Put the PID in the queue
     *  - Delete the PID from the cache
     *  - Return the PID
     *  
     *  Notes: 
     *  - The actual editing of the PID in the GWDG service will be proceed from the queue
     *  - The cache will be completed by a new PID generated from {@link CacheProcess}
     * 
     * @param url The URL to be registered.
     * 
     * @return The PID.
     **/
	public String create(String url) throws Exception
	{
		String xmlOutput = null;
		Pid pid = cache.getFirst();
		pid.setUrl(url);
		queue.add(pid);
		cache.remove(pid);
		this.status = HttpServletResponse.SC_CREATED;
		xmlOutput = transformToPidServiceResponse(pid, "create");
    	return xmlOutput;
	}
	
	/**
	 * Retrieve a PID from the GWDG PID service:
	 *  - Check if PID still in queue, if yes, return it
	 *  - Check if GWDG PID service available, if no throw Exception
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String retrieve(String id) throws Exception
	{
		pid = queue.retrieve(id);
		if (pid != null) 
		{
			return  transformToPidServiceResponse(pid, "view");
		}
		return gwdgPidService.retrieve(id);
	}
	
	/**
	 * Update a PID
	 * @param id
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String update(String id, String url) throws Exception
	{
		String xmlOutput = null;
		Pid pid = new Pid(id, url);
		queue.add(pid);
		xmlOutput = transformToPidServiceResponse(pid, "modify");
		this.status = HttpServletResponse.SC_OK;
    	return xmlOutput;
	}
	
	private String transformToPidServiceResponse(Pid pid, String action) throws TechnicalException
	{
		this.location = this.location.replace("XXX_LOCATION_XXX", pid.getIdentifier()); 
		PidServiceResponseVO pidServiceResponseVO = new PidServiceResponseVO();
		pidServiceResponseVO.setAction(action);
		pidServiceResponseVO.setCreator(GwdgClient.GWDG_PIDSERVICE_USER);
		pidServiceResponseVO.setIdentifier(pid.getIdentifier());
		pidServiceResponseVO.setUrl(pid.getUrl());
		pidServiceResponseVO.setUserUid("anonymous");
		pidServiceResponseVO.setInstitute("institute");
		pid.setContact("jon@doe.xx");
		pidServiceResponseVO.setMessage("Web proxy view URL: " + this.location);
		return xmlTransforming.transformToPidServiceResponse(pidServiceResponseVO);
	}
	
	public int getCacheSize() throws Exception
	{
		return cache.size();
	}
	
	public int getQueueSize() throws Exception
    {
        return queue.size();
    }

	public String getLocation() 
	{
		return location;
	}

	public void setLocation(String location) 
	{
		this.location = location;
	}

	public int getStatus() 
	{
		return status;
	}

	public void setStatus(int status) 
	{
		this.status = status;
	}
	
	
}
