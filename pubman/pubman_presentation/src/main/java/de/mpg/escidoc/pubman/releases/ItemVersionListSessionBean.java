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
* or http://www.escidoc.de/license.
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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.releases;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.common.valueobjects.EventLogEntryVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;

/**
 * Keeps all attributes that are used for the whole session by the ReleaseHistory.
 * @author:  Tobias Schraut, created 18.10.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $
 */
public class ItemVersionListSessionBean extends FacesBean
{
    public static final String BEAN_NAME = "ItemVersionListSessionBean";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ItemVersionListSessionBean.class);
    
    private List<VersionHistoryEntryVO> versionList = new ArrayList<VersionHistoryEntryVO>();
    
    private List<VersionHistoryEntryVO> releaseList = new ArrayList<VersionHistoryEntryVO>();
    
    private List<EventLogEntryVO> eventLogList = new ArrayList<EventLogEntryVO>();
    
    private UIXIterator versionIterator = new UIXIterator();
    
    private UIXIterator eventLogIterator = new UIXIterator();
    
    /**
     * Public constructor.
     */
    public ItemVersionListSessionBean()
    {
        this.init();
    }
    
    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }

	public List<VersionHistoryEntryVO> getVersionList() {
		return versionList;
	}

	public void setVersionList(List<VersionHistoryEntryVO> versionList) {
		this.versionList = versionList;
	}
	
	public void initVersionLists(List<VersionHistoryEntryVO> versionList)
	{
	    this.versionList = versionList;
	    
	    this.releaseList = new ArrayList<VersionHistoryEntryVO>();
	    
	    this.eventLogList = new ArrayList<EventLogEntryVO>();
	    
	    for(VersionHistoryEntryVO vEntry : versionList)
	    {
	        //if state=released add to release list
            if (vEntry.getState() == PubItemVO.State.RELEASED)
            {
                releaseList.add(vEntry);
            }
            
            
            //add all eventlog-entries to eventloglist
            List<EventLogEntryVO> eventList = vEntry.getEvents();
            for (EventLogEntryVO eEntry : eventList)
            {
                eventLogList.add(eEntry);
            }
	            
	            
	        
	    }
	    
	
	}
	
	public void resetVersionLists()
	{
	    this.versionList = null;
        
        this.releaseList = null;
        
        this.eventLogList = null;
	}

    public List<VersionHistoryEntryVO> getReleaseList()
    {
        return releaseList;
    }

    public void setReleaseList(List<VersionHistoryEntryVO> releaseList)
    {
        this.releaseList = releaseList;
    }

    public List<EventLogEntryVO> getEventLogList()
    {
        return eventLogList;
    }

    public void setEventLogList(List<EventLogEntryVO> eventLogList)
    {
        this.eventLogList = eventLogList;
    }

    public UIXIterator getVersionIterator()
    {
        return versionIterator;
    }

    public void setVersionIterator(UIXIterator versionIterator)
    {
        this.versionIterator = versionIterator;
    }

    public UIXIterator getEventLogIterator()
    {
        return eventLogIterator;
    }

    public void setEventLogIterator(UIXIterator eventLogIterator)
    {
        this.eventLogIterator = eventLogIterator;
    }
    
    public String getCurrentTypeLabel() {
        VersionHistoryEntryVO currentVersionVO = versionList.get(versionIterator.getRowIndex());
        EventLogEntryVO currentEventEntry = currentVersionVO.getEvents().get(eventLogIterator.getRowIndex());
        
        switch (currentEventEntry.getType()){
        
            case CREATE : return "Created on: ";
            case RELEASE : return "Released on: ";
            case SUBMIT : return "Submitted on: ";
            case UPDATE : return "Updated on: ";
            case WITHDRAW : return "Withdrawn on: ";
            
            
        }
        return "";
    }
    
}
