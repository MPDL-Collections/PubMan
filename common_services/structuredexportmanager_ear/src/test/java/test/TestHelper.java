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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
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
import java.util.HashMap;

import javax.xml.rpc.ServiceException;

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
	
	public static final String ITEMS_LIMIT = "50";
    /**
     * Constants for queries.
     */
    private static final String SEARCH_RETRIEVE = "searchRetrieve";
    private static final String QUERY = "query";
    private static final String VERSION = "version";
    private static final String OPERATION = "operation"; 
    
	public static String CONTENT_MODEL = null; 
	public static String USER_NAME = null;
	public static String USER_PASSWD = null; 
	
	/**
	 * Initialize 
	 */
	public TestHelper() throws Exception
	{
	    if (USER_NAME == null)
	    {
            USER_NAME = PropertyReader.getProperty("framework.scientist.username");
            if (USER_NAME == null)
            {
                throw new RuntimeException("Property 'framework.scientist.username' not found.");
            }
	    }
	    if (USER_PASSWD == null)
        {
            USER_PASSWD = PropertyReader.getProperty("framework.scientist.password");
            if (USER_PASSWD == null)
            {
                throw new RuntimeException("Property 'framework.scientist.password' not found.");
            }
	    }
	    if (CONTENT_MODEL == null)
        {
	        CONTENT_MODEL = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
            if (CONTENT_MODEL == null)
            {
                throw new RuntimeException("Property 'escidoc.framework_access.content-model.id.publication' not found.");
            }
        }
	}
	
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
    
    public String getItemListFromFramework() throws IOException, ServiceException, URISyntaxException
    {
    	
    	String userHandle = AdminHelper.loginUser(USER_NAME, USER_PASSWD); 
        ItemHandler ch = ServiceLocator.getItemHandler(userHandle);
        HashMap<String, String[]>  filterMap = new HashMap<String, String[]>();
        String q1 = "\"/properties/public-status\"=released";
        String q2 = "\"/properties/content-model \"=" + CONTENT_MODEL;
        
        filterMap.put(OPERATION, new String[]{SEARCH_RETRIEVE});
        filterMap.put(VERSION, new String[]{"1.1"});
        filterMap.put(QUERY, new String[]{q1 + " and " + q2});
       
        return ch.retrieveItems(filterMap);
    
    }
    


}
