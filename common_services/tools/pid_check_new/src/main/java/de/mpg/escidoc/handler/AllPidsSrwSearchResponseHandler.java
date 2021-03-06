package de.mpg.escidoc.handler;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AllPidsSrwSearchResponseHandler extends DefaultHandler
{
    private static Logger logger = Logger.getLogger(SrwSearchResponseHandler.class);

    private StringBuffer currentContent;  
    
    private String currentPid = "";
    private Set<String> pids = new HashSet<String>();
    
    private boolean inPid = false;
    private String currentComponentId = "";
    private String currentStorageType = "";

    private int numberOfPidsMissing = 0;
    
    private String escidocId = "";
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        logger.debug("startElement uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + "> attributes = <" + attributes + ">");
        
        if ("escidocItem:item".equals(qName))
        {
            escidocId = attributes.getValue("xlink:href").replace("/ir/item/", "");
        }
        else if ("escidocComponents:component".equals(qName))
        {
            currentComponentId = attributes.getValue("xlink:href") != null ? attributes.getValue("xlink:href").replace("/ir/item/", "") : attributes.getValue("objid") ;
        }
        else if ("escidocComponents:content".equals(qName))
        {
            currentStorageType = attributes.getValue("storage");
        }
        else if ("prop:pid".equals(qName) || "version:pid".equals(qName) || "release:pid".equals(qName))
        {
            inPid = true;
        }
        
        currentContent = new StringBuffer();
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> qName = <" + qName + ">");
        
        if ("prop:pid".equals(qName) || "version:pid".equals(qName) || "release:pid".equals(qName))
        {
        	inPid = false;
        }
        else if ("escidocItem:properties".equals(qName))
        {
            currentPid = "";
        }
        else if ("escidocComponents:component".equals(qName))
        {           
            if ("".equals(currentPid) && !"external-url".equals(currentStorageType))
            {
                numberOfPidsMissing++;
                logger.warn("No component pid found for componentId <" + currentComponentId + ">");
            }
            currentPid = "";   
            currentStorageType = "";
        }
        
        currentContent = null;       
    }
    
    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException
    {   
        if (currentContent == null)
            return;
        
        if (inPid)
        {
            currentContent.append(ch, start, length);
            currentPid = currentContent.toString();
            pids.add(escidocId + " | " + new String(currentPid));
            
            logger.debug("pid =<" + currentPid + ">");
        }  
    }

    public Set<String> getPids()
    {
        return pids;
    }  
    
    public int getNumberOfPidsMissing()
    {
        return numberOfPidsMissing;
    }  
    
    // utility if parsing comonents
    public String getCurrentComponentId()
    {
        return new String(currentComponentId);
    }  

}
