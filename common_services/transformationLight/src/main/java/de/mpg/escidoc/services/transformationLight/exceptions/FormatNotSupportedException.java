/*
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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.services.transformationLight.exceptions;
/**
 * Exceptions for data which could not be fetched from an import source.
 * 
 * @author kleinfe1
 */
public class FormatNotSupportedException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * FormatNotSupportedException.
     */
    public FormatNotSupportedException()
    {
    }

    /**
     * FormatNotSupportedException.
     * @param message
     */
    public FormatNotSupportedException(String message)
    {
        super(message);
    }

    /**
     * FormatNotSupportedException.
     * @param cause
     */
    public FormatNotSupportedException(Throwable cause)
    {
        super(cause);
    }

    /**
     * FormatNotSupportedException.
     * @param message
     * @param cause
     */
    public FormatNotSupportedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
