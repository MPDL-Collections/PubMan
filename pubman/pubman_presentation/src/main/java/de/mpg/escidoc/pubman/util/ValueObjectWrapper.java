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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.util;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * Wrapper for ValueObjects that provides additional attributes for the presentation layer. 
 * 
 * @author: Thomas Diebäcker, created 29.08.2007
 * @version: $Revision$ $LastChangedDate$
 */
public class ValueObjectWrapper
{
    protected ValueObject valueObject = null;
    protected boolean selected = false;
    
    /**
     * Public constructor.
     */
    public ValueObjectWrapper()
    {        
    }

    /**
     * Public constructor with given ValueObject.
     * @param valueObject the ValueObject that should be wrapped
     */
    public ValueObjectWrapper(ValueObject valueObject)
    {        
        this.valueObject = valueObject;
    }

    public ValueObject getValueObject()
    {
        return valueObject;
    }

    public void setValueObject(ValueObject valueObject)
    {
        this.valueObject = valueObject;
    }

    public boolean getSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
}