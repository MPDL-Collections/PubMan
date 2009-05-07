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

package de.mpg.escidoc.services.common.valueobjects;

import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;

/**
 * The metadata set Translation describes resources which are translations (usually XML Files) of original materials
 * like books or transcriptions of books.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 11:09:54
 */
public class MdsTranslationVO extends MetadataSetVO
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
 
    private java.util.List<CreatorVO> creators = new java.util.ArrayList<CreatorVO>();
    private String language;
    private String creationDate;

    /**
     * Delivers the list of creators of the translation.
     */
    public java.util.List<CreatorVO> getCreators()
    {
        return creators;
    }

    /**
     * Delivers the language of the translation.
     */
    public String getLanguage()
    {
        return language;
    }

    /**
     * Sets the language of the translation.
     * 
     * @param newVal newVal
     */
    public void setLanguage(String newVal)
    {
        language = newVal;
    }

    /**
     * Delivers the creation date of the translation.
     */
    public String getCreationDate()
    {
        return creationDate;
    }

    /**
     * Sets the creation date of the translation.
     * 
     * @param newVal
     */
    public void setCreationDate(String newVal)
    {
        creationDate = newVal;
    }
}