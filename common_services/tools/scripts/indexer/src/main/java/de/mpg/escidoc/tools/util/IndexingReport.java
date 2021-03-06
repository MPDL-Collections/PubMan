package de.mpg.escidoc.tools.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexingReport
{
    private AtomicInteger filesTotal = new AtomicInteger(0);
    private AtomicInteger filesIndexingDone  = new AtomicInteger(0);
    private AtomicInteger filesErrorOccured  = new AtomicInteger(0);
    private AtomicInteger filesSkippedBecauseOfTime  = new AtomicInteger(0);
    private AtomicInteger filesSkippedBecauseOfStatusOrType  = new AtomicInteger(0);
    private Collection<String> errorList = Collections.synchronizedList(new ArrayList<String>());
  
    private long start = System.currentTimeMillis();
    

    public void setFilesTotal(int t)
    {
    	this.filesTotal.getAndSet(t);
    }
    
    public int getFilesIndexingDone()
    {
        return this.filesIndexingDone.get();
    } 
    
    public void incrementFilesIndexingDone()
    {
        this.filesIndexingDone.incrementAndGet();
    }
    
    public int getFilesErrorOccured()
    {
        return this.filesErrorOccured.get();
    } 
    
    public void incrementFilesErrorOccured()
    {
        this.filesErrorOccured.incrementAndGet();
    }

    public int getFilesSkippedBecauseOfTime()
	{
		return this.filesSkippedBecauseOfTime.get();
	}

	public void incrementFilesSkippedBecauseOfTime()
	{
		this.filesSkippedBecauseOfTime.incrementAndGet();
	}

	public int getFilesSkippedBecauseOfStatusOrType()
	{
		return this.filesSkippedBecauseOfStatusOrType.get();
	}

	public void incrementFilesSkippedBecauseOfStatusOrType()
	{
		this.filesSkippedBecauseOfStatusOrType.incrementAndGet();
	}
     
    public long getTimeUsed()
    {
        return (System.currentTimeMillis() - this.start)/1000;
    }
    
    public void addToErrorList(String message)
    {
        this.errorList.add(message);        
    }
    
    public Collection<String> getErrorList()
    {
        return this.errorList;  
    }
    
    public void clear()
    {      
        this.filesErrorOccured.set(0);
        this.filesIndexingDone.set(0);
        this.filesSkippedBecauseOfTime.set(0);
        this.filesSkippedBecauseOfStatusOrType.set(0);
        this.errorList.clear();    
        
        this.start = System.currentTimeMillis();
          
    }
    
    @Override
    public String toString()
    {
    	long s = (System.currentTimeMillis() - start)/1000;
    	return 
    			"\nfilesTotal\t\t\t\t<" + filesTotal.get() + "> \n"
    			+ "filesErrorOccured\t\t\t<" + filesErrorOccured.get() + "> \n"
    			+ "filesSkippedBecauseOfTime\t\t<" + filesSkippedBecauseOfTime.get() + "> \n"
    			+ "filesSkippedBecauseOfStatusOrType\t<" + filesSkippedBecauseOfStatusOrType.get() + "> \n"
    			+ "filesIndexingDone\t\t\t<" + filesIndexingDone.get() + "> \n"
    			+ "time used\t\t\t\t<"	+  String.format("%d:%02d:%02d", s/3600, s%3600/60, s%60) + "> \n"
    			+ "errorList\t\t\t\t<" + Arrays.toString(errorList.toArray())  + "> \n";
    		
    }

	

}
