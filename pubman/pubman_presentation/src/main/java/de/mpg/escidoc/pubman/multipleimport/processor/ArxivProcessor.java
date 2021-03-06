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

package de.mpg.escidoc.pubman.multipleimport.processor;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;

import org.apache.axis.encoding.Base64;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ArxivProcessor extends FormatProcessor
{
    
    private boolean init = false;
    private String[] items = null;
    private int counter = -1;
    private int length = -1;
    private byte[] originalData = null;
    
    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        if (!init)
        {
            initialize();
        }
        return (this.originalData != null && this.counter < this.length);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public String next() throws NoSuchElementException
    {
        if (!init)
        {
            initialize();
        }
        if (this.originalData != null && this.counter < this.length)
        {
            this.counter++;
            try
            {
                return new String(this.originalData, this.encoding);
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
            throw new NoSuchElementException("No more entries left");
        }
        
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        throw new RuntimeException("Method not implemented");
    }

    private void initialize()
    {
        init = true;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read;
        byte[] buffer = new byte[2048];
       
        try
        {
        	InputStream is = new FileInputStream(getSourceFile());
            while ((read = is.read(buffer)) != -1)
            {
                byteArrayOutputStream.write(buffer, 0, read);
            }
            
            this.originalData = byteArrayOutputStream.toByteArray();

            this.length = 1;
            
            counter = 0;
            is.close();
            
        }
        catch (Exception e) {
            throw new RuntimeException("Error reading input stream", e);
        }
        
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.multipleimport.processor.FormatProcessor#getLength()
     */
    @Override
    public int getLength()
    {
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDataAsBase64()
    {
        if (this.originalData == null)
        {
            return null;
        }
        else
        {
            return Base64.encode(this.originalData);
        }
    }
    
}
