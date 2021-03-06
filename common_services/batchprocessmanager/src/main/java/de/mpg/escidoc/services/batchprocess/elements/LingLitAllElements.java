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

package de.mpg.escidoc.services.batchprocess.elements;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.types.NonNegativeInteger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.helper.CoreServiceHelper;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class LingLitAllElements extends Elements<ItemVO>
{
    public LingLitAllElements(String[] args)
    {
        super(args);
    }

    @Override
    public void init(String[] args)
    {
        try
        {
            setUserHandle(AdminHelper.loginUser(PropertyReader.getProperty("escidoc.user.name"), PropertyReader.getProperty("escidoc.user.password")));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Login error. Please make sure the user credentials (escidoc.user.name, escidoc.user.password) are provided in your settings.xml file." + e);
        }
    }

    private static final String LOCAL_TAG = "LingLit Import 2010-04-01 10:10";
    private static final String SEARCH_QUERY = "escidoc.context.objid=\"escidoc:37005\" and escidoc.content-model.objid=\"escidoc:persistent4\"";

    @Override
    public void retrieveElements()
    {
        try
        {
            SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
            searchRetrieveRequest.setVersion("1.1");
            searchRetrieveRequest.setQuery(SEARCH_QUERY);
            searchRetrieveRequest.setMaximumRecords(new NonNegativeInteger(maximumNumberOfElements + ""));
            searchRetrieveRequest.setRecordPacking("xml");
            SearchRetrieveResponseType searchResultXml = ServiceLocator.getSearchHandler("escidoc_all").searchRetrieveOperation(searchRetrieveRequest);

            List<ItemVO> resultElements = new ArrayList<ItemVO>();
			resultElements.addAll(CoreServiceHelper
					.transformSearchResultXmlToListOfItemVO(searchResultXml));
			ItemHandler ih = ServiceLocator.getItemHandler(this.getUserHandle());
			
			// fetching each item again is needed, as content of files is null
			// in lists. So no update would be possible on items with internal
			// components 
			for (ItemVO item : resultElements) {
				XmlTransformingBean xmlTransforming = new XmlTransformingBean();
				ItemVO retrivedItemVO = xmlTransforming.transformToItem(ih
						.retrieve(item.getVersion().getObjectId()));
				if (!(retrivedItemVO.getLocalTags().contains(LOCAL_TAG)))
				{
					elements.add(retrivedItemVO);
				}
			}
            
            report.addEntry("retrieveElements", "Get Data", ReportEntryStatusType.FINE);
            System.out.println(elements.size() + " items found");
//            for (int i = elements.size() - 1; i >= 0; i--)
//            {
//            	if (elements.get(i).getVersion().getVersionNumber() == 1)
//            	{
//            		System.out.println(elements.get(i).getVersion().getObjectId() + " was not edited");
//            	}
//            	else if(elements.get(i).getVersion().getVersionNumber() != 1 && !elements.get(i).getVersion().getState().equals(State.RELEASED))
//            	{
//            		this.getTransformed().add(elements.get(i).getVersion().getObjectId());
//            		System.out.println(elements.get(i).getVersion().getObjectId() + " was edited, but not released.");
//            	}
//            	else
//            	{
//            	    System.out.println(elements.get(i).getVersion().getObjectId() + " was edited, removed from the list");
//            	    elements.remove(i);
//            	}
//			}
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initializing LingLitAllElements.java: ", e);
        }
    }

    public List<ItemVO> getElements()
    {
        return elements;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
}
