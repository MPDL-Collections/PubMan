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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.transformationLight.transformations.commonPublicationFormats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationType;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.transformation.TransformationsType;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.util.Util;
import de.mpg.escidoc.services.transformationLight.Configurable;
import de.mpg.escidoc.services.transformationLight.Transformation;
import de.mpg.escidoc.services.transformationLight.Transformation.TransformationModule;
import de.mpg.escidoc.services.transformationLight.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformationLight.valueObjects.Format;
import de.mpg.escidoc.services.common.util.PropertyReader;

/**
 * Implementation of the transformation interface for citation formats.
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 4133 $ $LastChangedDate: 2011-09-22 11:19:17 +0200 (Do, 22 Sep 2011) $
 *
 */
@TransformationModule
public class CommonTransformationInterface implements Transformation, Configurable
{

    private final Logger logger = Logger.getLogger(CommonTransformationInterface.class);
    
    private final String EXPLAIN_FILE_PATH ="transformations/commonPublicationFormats/";
    private final String EXPLAIN_FILE_NAME="explain-transformations.xml";
    
    private Util util;
    private CommonTransformation commonTrans;
    
    private Map<String, List<String>> properties = null;
    private Map<String, String> configuration = null;
    
    /**
     * Public constructor.
     */
    public CommonTransformationInterface()
    {
        this.commonTrans = new CommonTransformation();
    }
    
    /**
    * {@inheritDoc}
    */
    public Format[] getSourceFormats() throws RuntimeException
    {
        Vector<Format> sourceFormats = new Vector<Format>();
        TransformationsDocument transDoc = null;
        TransformationsType transType = null;
          
        java.io.InputStream in;
        try
        {
            in = ResourceUtil.getResourceAsStream(this.EXPLAIN_FILE_PATH + this.EXPLAIN_FILE_NAME);
            transDoc = TransformationsDocument.Factory.parse(in);
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred while reading transformations.xml for common publication formats.", e);
            throw new RuntimeException(e);
        }
        transType = transDoc.getTransformations();
        TransformationType[] transformations = transType.getTransformationArray();
        for (int i = 0; i < transformations.length; i++)
        {
            TransformationType transformation = transformations [i];
            String name = this.util.simpleLiteralTostring(transformation.getSource().getName());
            String type = this.util.simpleLiteralTostring(transformation.getSource().getType());
            String encoding = this.util.simpleLiteralTostring(transformation.getSource().getEncoding());
            Format sourceFormat = new Format(name, type, encoding);
              
            sourceFormats.add(sourceFormat);       
        }       
        sourceFormats = this.util.getRidOfDuplicatesInVector(sourceFormats);
        Format[] dummy = new Format[sourceFormats.size()];
        return sourceFormats.toArray(dummy);
    }

    /**
     * {@inheritDoc}
     */
    public String getSourceFormatsAsXml() throws RuntimeException
    {
        Format[] formats = this.getSourceFormats();
        return this.util.createFormatsXml(formats);
    }

    /**
     * {@inheritDoc}
     */
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding) 
        throws RuntimeException
    {
        Format[] formats = this.getTargetFormats(new Format(srcFormatName, srcType, srcEncoding));
        return this.util.createFormatsXml(formats);
    }

    /**
     * {@inheritDoc}
     */
    public Format[] getTargetFormats(Format src) throws RuntimeException
    {
        Vector<Format> targetFormats = new Vector<Format>();
        TransformationsDocument transDoc = null;
        TransformationsType transType = null;
      
        java.io.InputStream in;
        try
        {
            in = ResourceUtil.getResourceAsStream(this.EXPLAIN_FILE_PATH + this.EXPLAIN_FILE_NAME);
            transDoc = TransformationsDocument.Factory.parse(in);
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred while reading transformations.xml for standard publication formats.", e);
            throw new RuntimeException(e);
        }
        
        transType = transDoc.getTransformations();
        TransformationType[] transformations = transType.getTransformationArray();
        for (TransformationType transformation : transformations)
        {
            Format source = new Format(this.util.simpleLiteralTostring(transformation.getSource().getName()),
                  this.util.simpleLiteralTostring(transformation.getSource().getType()),
                  this.util.simpleLiteralTostring(transformation.getSource().getEncoding()));
            //Only get Target if source is given source
            if (this.util.isFormatEqual(source, src))
            {
                String name = this.util.simpleLiteralTostring(transformation.getTarget().getName());
                String type = this.util.simpleLiteralTostring(transformation.getTarget().getType());
                String encoding = this.util.simpleLiteralTostring(transformation.getTarget().getEncoding());
                Format sourceFormat = new Format(name, type, encoding);
              
                targetFormats.add(sourceFormat);   
            }
        }    
        targetFormats = this.util.getRidOfDuplicatesInVector(targetFormats);
        Format[] dummy = new Format[targetFormats.size()];
        return targetFormats.toArray(dummy);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding, String trgFormatName,
            String trgType, String trgEncoding, String service) throws TransformationNotSupportedException
    {
        Format source = new Format(srcFormatName, srcType, srcEncoding);
        Format target = new Format(trgFormatName, trgType, trgEncoding);
        return this.transform(src, source, target, service);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service, Map<String, String> configuration)
        throws TransformationNotSupportedException, RuntimeException
    {
        byte[] result = null;
        boolean supported = false;
        this.configuration = configuration;
        
        if (srcFormat.getName().toLowerCase().startsWith("escidoc"))
        {
            result = this.escidocTransform(src, srcFormat, trgFormat, service);
            supported = true;
        }
       
        if (!supported)
        {
            this.logger.warn("Transformation not supported: \n " + srcFormat.getName() + ", " + srcFormat.getType() 
                    + ", " + srcFormat.getEncoding() + "\n" + trgFormat.getName() + ", " + trgFormat.getType() 
                    + ", " + trgFormat.getEncoding());
            throw new TransformationNotSupportedException();
        }
       
        return result;
    }
    
    private byte[] escidocTransform(byte[] src, Format srcFormat, Format trgFormat, String service)
    throws TransformationNotSupportedException
    {
        byte[] result = null;
        boolean supported = false;
        boolean list = false;
        
        if (srcFormat.getName().equalsIgnoreCase("eSciDoc-publication-item-list"))
        {
            list = true;
        }
          
        if (!supported)
        {
            this.logger.warn("Transformation not supported: \n" + srcFormat.getName() + ", " + srcFormat.getType() 
                    + ", " + srcFormat.getEncoding() + "\n" + trgFormat.getName() + ", " + trgFormat.getType() 
                    + ", " + trgFormat.getEncoding());
            throw new TransformationNotSupportedException();
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats(Format trg) throws RuntimeException
    {
        Vector<Format> sourceFormats = new Vector<Format>();
        TransformationsDocument transDoc = null;
        TransformationsType transType = null;
      
        java.io.InputStream in;
        try
        {
            in = ResourceUtil.getResourceAsStream(this.EXPLAIN_FILE_PATH + this.EXPLAIN_FILE_NAME);
            transDoc = TransformationsDocument.Factory.parse(in);
        }
        catch (Exception e)
        {
            this.logger.error("An error occurred while reading transformations.xml for common publication formats.", e);
            throw new RuntimeException(e);
        }
        
        transType = transDoc.getTransformations();
        TransformationType[] transformations = transType.getTransformationArray();
        for (TransformationType transformation : transformations)
        {
            Format target = new Format(this.util.simpleLiteralTostring(transformation.getTarget().getName()),
                  this.util.simpleLiteralTostring(transformation.getTarget().getType()),
                  this.util.simpleLiteralTostring(transformation.getTarget().getEncoding()));
            //Only get Target if source is given source
            if (this.util.isFormatEqual(target, trg))
            {
                String name = this.util.simpleLiteralTostring(transformation.getSource().getName());
                String type = this.util.simpleLiteralTostring(transformation.getSource().getType());
                String encoding = this.util.simpleLiteralTostring(transformation.getSource().getEncoding());
                Format format = new Format(name, type, encoding);
              
                sourceFormats.add(format);   
            }
        }    
        sourceFormats = this.util.getRidOfDuplicatesInVector(sourceFormats);
        Format[] dummy = new Format[sourceFormats.size()];
        return sourceFormats.toArray(dummy);
    }

    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
            throws TransformationNotSupportedException, RuntimeException
    {
        return transform(src, srcFormat, trgFormat, service, null);
    }

    public Map<String, String> getConfiguration(Format srcFormat, Format trgFormat) throws Exception
    {
        if (configuration == null)
        {
            init();
        }

        return configuration;
    }

    private void init() throws IOException, FileNotFoundException, URISyntaxException
    {
        configuration = new LinkedHashMap<String, String>();
        properties = new HashMap<String, List<String>>();
        Properties props = new Properties();
        props.load(ResourceUtil.getResourceAsStream(PropertyReader.getProperty("escidoc.transformation.bibtex.configuration.filename")));
        for (Object key : props.keySet())
        {
            if (!"configuration".equals(key.toString()))
            {
                String[] values = props.getProperty(key.toString()).split(",");
                properties.put(key.toString(), Arrays.asList(values));
            }
            else
            {
                String[] confValues = props.getProperty("configuration").split(",");
                for (String field : confValues)
                {
                    String[] fieldArr = field.split("=", 2);
                    configuration.put(fieldArr[0], fieldArr[1] == null ? "" : fieldArr[1]);
                }
            }
        }
    }

    public List<String> getConfigurationValues(Format srcFormat, Format trgFormat, String key) throws Exception
    {
        if (properties == null)
        {
            init();
        }

        return properties.get(key);
    }    
    
}
