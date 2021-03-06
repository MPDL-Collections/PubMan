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

package de.mpg.escidoc.services.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class SchematronUtil
{
    private static final Logger LOGGER = Logger.getLogger(SchematronUtil.class);
    
    public static boolean isChild(String ou) throws Exception
    {
        if (ou != null)
        {
            HttpClient httpClient = new HttpClient();
            GetMethod getMethod = new GetMethod(PropertyReader.getProperty("escidoc.framework_access.framework.url") + "/oum/organizational-unit/" + ou.trim() + "/resources/path-list");
            try
            {
                httpClient.executeMethod(getMethod);
                if (getMethod.getStatusCode() == 200)
                {
                    if (getMethod.getResponseBodyAsString().contains("\"/oum/organizational-unit/" + PropertyReader.getProperty("escidoc.pubman.root.organisation.id") + "\""))
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    LOGGER.warn("Error while checking organizational-unit path: Return code " + getMethod.getStatusCode() + "\n" + getMethod.getResponseBodyAsString());
                    return false;
                }
            }
            catch (IllegalArgumentException e)
            {
                LOGGER.warn("Error while checking organizational-unit path: " + e.getMessage());
                LOGGER.debug("Error", e);
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    
}
