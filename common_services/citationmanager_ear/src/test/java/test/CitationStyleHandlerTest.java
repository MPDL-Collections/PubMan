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

import static org.junit.Assert.assertTrue;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO.FormatType;

public class CitationStyleHandlerTest {

    private static final String dsName = "target/target/test-classes/DataSources/CitationStyleTestCollection.xml";
	private static String itemList;
	private static CitationStyleHandler pcs; 


    private static Logger logger = Logger.getLogger(test.CitationStyleHandlerTest.class);

    /**
     * Init  CitMan bean.
     * @throws Exception Any Exception.
     */
    @BeforeClass
    public static final void getCitationStyleManager() throws Exception
    {
        InitialContext ctx = new InitialContext();
        pcs = (CitationStyleHandler) ctx.lookup("ejb:citationmanager_ear/citationmanager/CitationStyleHandlerBean!" + CitationStyleHandler.class.getName());
    }

    /**
     * Get test item list from XML 
     * @throws Exception
     */
    @BeforeClass
    public static final void getItemList() throws Exception
    {    	
        itemList = TestHelper.getTestItemListFromFramework();
		assertTrue("item list from framework is empty", Utils.checkVal(itemList) );
		logger.info("item list from framework:\n" + itemList);    
    }
    

    /**
     * Test list of styles
     * @throws Exception Any exception.
     */
    @Test
    public final void testExplainStuff() throws Exception {
    	
    	String explain = pcs.explainStyles();
    	assertTrue("Empty explain xml", Utils.checkVal(explain) );
    	logger.info("Explain file:" + explain);
    	
    	logger.info("List of citation styles with output formats: " );
    	for (String s : pcs.getStyles() )
    	{
    		logger.info("Citation Style: " + s);
    		for(String of : pcs.getOutputFormats(s))
    		{
        		logger.info("--Output Format: " + of);
        		logger.info("--Mime Type: " + pcs.getMimeType(s, of));
    		}
    		
    	}	
    	
    	
    	
    }  
    /**
     * Test service for all citation styles and all output formats 
     * @throws Exception Any exception.
     */
    @Test
    @Ignore
    public final void testCitManOutput() throws Exception {
    	
    	for 
    	(
    			String cs :
    				pcs.getStyles()
//    				new String[]{"APA","AJP"}
    	)
    	{
    		 if(!"CSL".equals(cs))
    		 {
	    		long start;
	        	byte[] result;
	//    		for ( String format : pcs.getOutputFormats(cs) ) {
	    		for 
	    		( 
	    				String format :
	    					pcs.getOutputFormats(cs)
	//    					new String[]{"snippet"} 
	    		) {
	        		logger.info("Test Citation Style: " + cs);
	    			
	    	    	start = System.currentTimeMillis();
	    	    	result = pcs.getOutput(itemList, new ExportFormatVO(FormatType.LAYOUT, cs, format)); 
	//    	    	result = pcs.getOutput(cs, itemList);
	    	    	
	//        		logger.info("ItemList\n: " + itemList);
	//        		logger.info("Result\n: " + new String(result));
	        		
	    	    	
	    	    	logger.info("Output to " + format + ", time: " + (System.currentTimeMillis() - start));
	    	    	assertTrue(format + " output should not be empty", result.length > 0);
	    	    	
	    	    	
	        		logger.info("Number of items to proceed: " + TestHelper.ITEMS_LIMIT);
	    	        logger.info(format + " length: " + result.length);
	    	        logger.info(format + " is OK");
	    	        
	    			
	    		}
    		
    		 }
    	}
    }

    
    /**
     * Test service with a wrong Citation Style 
     * @throws Exception Any exception.
     */
    
    @Test(expected = CitationStyleManagerException.class) 
    @Ignore
    public final void testWrongStyleCitManOutput() throws Exception  {
    	byte[] result = pcs.getOutput(itemList, new ExportFormatVO(FormatType.LAYOUT, "XYZ", "pdf"));
//    	byte[] result = pcs.getOutput("XYZ",  itemList);
    }
    
    /**
     * Test service with a wrong output format 
     * @throws Exception Any exception.
     */
    
    @Test(expected = CitationStyleManagerException.class) 
    @Ignore
    public final void testWrongFormatCitManOutput() throws Exception {
    	byte[] result = pcs.getOutput(itemList, new ExportFormatVO(FormatType.LAYOUT, "APA", "xyz"));
//    	byte[] result = pcs.getOutput("APA",  itemList);
    }

		
}
