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

package de.mpg.escidoc.services.batchprocess.scripts;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;

import java.util.List;

import org.apache.axis.types.NonNegativeInteger;

import de.escidoc.www.services.aa.UserGroupHandler;
import de.mpg.escidoc.services.batchprocess.helper.CoreServiceHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class CreateGroupGrants
{
    XmlTransforming xmlTransforming = new XmlTransformingBean();
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length < 2 || args.length > 3)
        {
            System.out.println("usage: CreateGroupGrants <context> <user-group> [<maximum-records>]");
        }
        else
        {
            int max = 0;
            if (args.length > 2)
            {
                max = Integer.parseInt(args[2]);
            }
            new CreateGroupGrants(args[0], args[1], max);
        }
            
    }
    
    public CreateGroupGrants(String contextId, String userGroupId, int maximumNumberOfElements) throws Exception
    {
        System.out.print("Getting handle...");
        String handle = AdminHelper.loginUser(PropertyReader.getProperty("escidoc.user.name"), PropertyReader.getProperty("escidoc.user.password"));
        System.out.println("done!");
        
        System.out.println("Getting items...");
        List<ItemVO> list = getList(contextId, maximumNumberOfElements);
        System.out.println("...done!");
        
        for (ItemVO itemVO : list)
        {
            for (FileVO fileVO : itemVO.getFiles())
            {
                if (fileVO.getVisibility() == Visibility.AUDIENCE)
                {
                    createGrant(fileVO, userGroupId, handle);
                }
            }
        }
    }

    private void createGrant(FileVO fileVO, String userGroupId, String handle) throws Exception
    {
        UserGroupHandler userGroupHandler = ServiceLocator.getUserGroupHandler(handle);
        GrantVO grantVO = new GrantVO("escidoc:role-audience", userGroupId);
        grantVO.setObjectRef(fileVO.getReference().getObjectId());
        String grantXml = xmlTransforming.transformToGrant(grantVO);
        try
        {
            System.out.print("Creating grant for " + fileVO.getReference().getObjectId() + "...");
            userGroupHandler.createGrant(userGroupId, grantXml);
            System.out.println("done!");
        }
        catch (Exception e) {
            System.err.println("Warning: " + e.toString().replace("\n", " ").substring(0, 80));
        }
    }

    private List<ItemVO> getList(String contextId, int maximumNumberOfElements) throws Exception
    {

        String query = "escidoc.context.objid=\"" + contextId + "\" and escidoc.component.internal-managed.visibility=\"audience\" and escidoc.content-model.objid=\"" + PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication") + "\"";
//        String query = "escidoc.context.objid=\"" + contextId + "\" and escidoc.component.content.storage=\"internal-managed\" and escidoc.component.visibility=\"audience\" and escidoc.content-model.objid=\"" + PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication") + "\"";

        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(query);
        if(maximumNumberOfElements > 0) 
        {
        	searchRetrieveRequest.setMaximumRecords(new NonNegativeInteger(maximumNumberOfElements + ""));
        }
        searchRetrieveRequest.setRecordPacking("xml");
        SearchRetrieveResponseType searchResult = ServiceLocator.getSearchHandler("escidoc_all").searchRetrieveOperation(searchRetrieveRequest);

        return CoreServiceHelper.transformSearchResultXmlToListOfItemVO(searchResult);

    }
}
