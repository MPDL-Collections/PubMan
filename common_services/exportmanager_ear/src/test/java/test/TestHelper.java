/*
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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

// import de.mpg.escidoc.services.validation.xmltransforming.ValidationTransforming;

/**
 * Helper class for all test classes.
 *
 * @author Johannes M&uuml;ller (initial)
 * @author $Author$ (last change)
 * @version $Revision$ $LastChangedDate$
 */
public class TestHelper
{
	
	private static Logger logger = Logger.getLogger(TestHelper.class);
	
	public static final String ITEMS_LIMIT = "30"; 
	public static final String CONTENT_MODEL_PUBMAN = "escidoc:persistent4"; 
	public static final String CONTENT_MODEL_FACES = "escidoc:faces40"; 

	
    /**
     * Retrieve resource based on a path relative to the classpath.
     * @param fileName The path of the resource.
     * @return The file defined by The given path.
     * @throws FileNotFoundException File not there.
     */
    public final File findFileInClasspath(final String fileName) throws FileNotFoundException
    {
        URL url = getClass().getClassLoader().getResource(fileName);
        if (url == null)
        {
            throw new FileNotFoundException(fileName);
        }
        return new File(url.getFile());
    }

    /**
     * Reads contents from text file and returns it as String.
     *
     * @param fileName Name of input file
     * @return Entire contents of filename as a String
     */
    public static String readFile(final String fileName, String enc)
    {
        boolean isFileNameNull = (fileName == null);
        StringBuffer fileBuffer;
        String fileString = null;
        String line;
        if (!isFileNameNull)
        {
            try
            {
//                InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
                InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), enc);
                BufferedReader br = new BufferedReader(isr);
                fileBuffer = new StringBuffer();
                while ((line = br.readLine()) != null)
                {
                    fileBuffer.append(line + "\n");
                }
                isr.close();
                fileString = fileBuffer.toString();
            }
            catch (IOException e)
            {
                return null;
            }
        }
        return fileString;
    }
    
    /**
     * Reads contents from text file and returns it as array of bytes.
     * @param fileName
     * @return
     */
    public static byte[] readBinFile(final String fileName)
    {
    	boolean isFileNameNull = (fileName == null);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	if (!isFileNameNull)
    	{
    		try {
    			int b;                // the byte read from the file
    			BufferedInputStream is = new BufferedInputStream(new FileInputStream(fileName));
    			BufferedOutputStream os = new BufferedOutputStream(baos);
    			while ((b = is.read( )) != -1) {
    				os.write(b);
    			}
    			is.close( );
    			os.close( );
    		}
    		catch (IOException e)
    		{
    			return null;
    		}
    	}
    	return baos.toByteArray();
    }
    
    /**
     * Reads contents from array of bytes and write to the file .
     * @param fileName
     * @throws IOException 
     */
    public static void writeBinFile(byte[] content, String fileName) throws IOException
    {
    	boolean isFileNameNull = (fileName == null);
    	boolean isEmptyContent = (content.length == 0);
    	if (!isFileNameNull && !isEmptyContent)
    	{
    		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
    		for (byte b : content)
    			bos.write(b);
    		bos.close( );
    	}
    }
    
    /**
     * Get itemList from the current Framework instance
     * @param fileName
     * @throws IOException 
     * @throws URISyntaxException 
     * @throws ServiceException 
     */
    public static String getItemListFromFramework(final String contentModel, String limit) throws IOException, ServiceException, URISyntaxException
    {
    	
    	String userHandle = AdminHelper.loginUser(PropertyReader.getProperty("framework.admin.username"), PropertyReader.getProperty("framework.admin.password"));
    	
        ItemHandler ch = ServiceLocator.getItemHandler(userHandle);
        // see here for filters: https://zim02.gwdg.de/repos/common/trunk/common_services/common_logic/src/main/java/de/mpg/escidoc/services/common/xmltransforming/JiBXFilterTaskParamVOMarshaller.java
        // example for SRW, faces, released: 
        // http://srv05.mpdl.mpg.de:8080/srw/search/escidoc_all?operation=search&query=%20escidoc.content-model.objid=escidoc:faces40
        String filter = 
        	"<param>" + 
        		// escidoc content model
        		"<filter name=\"http://escidoc.de/core/01/structural-relations/content-model\">" + contentModel + "</filter>" +
        		"<filter name=\"http://escidoc.de/core/01/properties/public-status\">released</filter>" +
        		// records limit	
        		"<limit>" + limit + "</limit>" +
        	"</param>";
        logger.info("filter:" + filter);
        return ch.retrieveItems(filter);
    
    }
    

}
