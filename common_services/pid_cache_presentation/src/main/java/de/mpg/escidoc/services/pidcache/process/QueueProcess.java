/**
 * 
 */
package de.mpg.escidoc.services.pidcache.process;

import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.pidcache.Pid;
import de.mpg.escidoc.services.pidcache.gwdg.GwdgPidService;
import de.mpg.escidoc.services.pidcache.tables.Queue;

/**
 * 
 * Process managing the {@link Queue}
 * 	- Check whether queue has enqueued PID 
 * 	  (i.e waiting for an update at GWDG service)
 * 	- Update enqueued item at the GWDG service.
 * 
 * @author saquet
 *
 */
public class QueueProcess 
{      
	private static final Logger logger = Logger.getLogger(QueueProcess.class);
;
	private int blockSize = 1;
	
	/**
     * Default constructor
     */
    public QueueProcess() throws Exception {   
    }
	
	/**
	 * Empty the {@link Queue} if:
	 *  - The service at the GWDG is available
	 *  - And if: 	- the PID exists : update the PID with the new URL 
	 *  			- The PID doesn't exist: Some Reporting...
	 * @throws Exception 
	 */
	public void empty() throws Exception
	{
		Queue queue = new Queue();
		Pid pid = queue.getFirst();
		GwdgPidService gwdgPidService = new GwdgPidService();
		if (gwdgPidService.available()) 
		{
			while (pid != null) 
			{
				try 
				{
					gwdgPidService.update(pid.getIdentifier(), pid.getUrl());
					queue.remove(pid);
				} 
				catch (Exception e) 
				{
					logger.warn("Error, PID can not be updated on GWDG service.", e);
				}
				
				pid = queue.getFirst();
			}
		}
		else
		{
			logger.warn("PID manager at GWDG not available.");
		}
	}

    public void emptyBlock() throws Exception
    {
        Queue queue = new Queue();
        List<Pid> pids = queue.getFirstBlock(this.blockSize);
        logger.debug("emptyBlock got " + this.blockSize + " pids");
        if (pids.size() == 0)
        {
            return;
        }
        GwdgPidService gwdgPidService = new GwdgPidService();
        if (gwdgPidService.available()) 
        {
            for (Pid pid : pids) 
            {
                try 
                {
                    int httpStatus = gwdgPidService.update(pid.getIdentifier(), pid.getUrl());
                     
                    if (logger.isDebugEnabled()) {
                        logger.debug("emptyBlock updated pid <" 
                                + pid.getIdentifier() + "> url <" 
                                + pid.getUrl() + "> with status <" + httpStatus + ">");
                    }
                    queue.remove(pid);
                } 
                catch (Exception e) 
                {
                    logger.warn("Error, PID can not be updated on GWDG service.");
                }             
            }
        }
        else
        {
            logger.warn("PID manager at GWDG not available.");
        }
        
    }
    
    public boolean isEmpty() throws Exception
    {
        return (new Queue()).isEmpty();
    }

    public void setBlockSize(int size)
    {
        this.blockSize = size;        
    }
}
