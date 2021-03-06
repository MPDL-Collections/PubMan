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

package test.common.valueobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import test.common.TestBase;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * For testing the methods in {@link AccountUserVO}.
 *
 * @author Johannes Mueller (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class AccountUserVOTest extends TestBase
{
    private Logger logger = Logger.getLogger(getClass());
    
    public void setUp() throws Exception
    {
        PUBMAN_TEST_COLLECTION_ID = PropertyReader.getProperty(PROPERTY_CONTEXTID_TEST);
    }

    /**
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testIsModeratorFunction() throws Exception
    {
        String adminUserHandle = loginSystemAdministrator();
        AccountUserVO admin = getAccountUser(adminUserHandle);
        assertTrue(admin.isModerator(new ContextRO(PUBMAN_TEST_COLLECTION_ID)));
    }
    
    @Test
    public void testZeroGrantsOfAuthor() throws Exception
    {
        logger.debug("### testZeroGrantsOfAuthor ###");
        String authorUserHandle = loginAuthor();
        AccountUserVO author = getAccountUser(authorUserHandle);
        List<GrantVO> grants = author.getGrants();
        assertEquals(0, grants.size());
    }
    
}
