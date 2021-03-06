/*
roject* CDDL HEADER START
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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExport;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportHandler;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportManagerException;

public class StructuredExportTest 
{
		private StructuredExportHandler export = new StructuredExport();
 
	    private Logger logger = Logger.getLogger(StructuredExportTest.class);
	    
	    private static HashMap<String, String> itemLists;
	    
	    public static final Map<String, String> ITEM_LISTS_FILE_MAMES =   
	    	new HashMap<String, String>()   
	    	{  
	    		String pref = "target/test-classes/";
				{  
		    put("MARCXML", pref + "publicationItems/metadataV2/item_book.xml");
                    put("BIBTEX", pref + "publicationItems/metadataV2/item_book.xml");
                    put("ENDNOTE", pref + "publicationItems/metadataV2/item_book.xml");
                    put("EDOC_EXPORT", pref + "publicationItems/metadataV2/full_item.xml");
                    put("EDOC_IMPORT", pref + "publicationItems/metadataV2/full_item.xml");
					put("CSV", pref + "facesItems/item-list.xml");
		    	}  
	    	};

	    /**
	     * Get test item list from XML 
	     * @throws Exception
	     */
	    
	    public static final void getItemLists() throws Exception
	    {
	    	itemLists = new HashMap<String, String>();
	    	
//	    	String itemList = TestHelper.getItemListFromFramework();
//    		assertFalse("item list from framework is empty", itemList == null || itemList.trim().equals("") );
//    		logger.info("item list from framework:\n" + itemList);
    		
	    	for ( String key : ITEM_LISTS_FILE_MAMES.keySet() )
	    	{
	    		String itemList =  ResourceUtil.getResourceAsString(ITEM_LISTS_FILE_MAMES.get(key), StructuredExportTest.class.getClassLoader());
	    		assertNotNull("Item list xml is not found", itemList);
	    		itemLists.put(key, itemList);
	    	}
	    	
//	    	FileOutputStream fos = new FileOutputStream("fwItemList.xml");
//	    	fos.write(fwItemList.getBytes());
//	    	fos.close();
	    	
	    }

	    
	    /**
	     * Get EndNote output test 
	     * @throws Exception
	     */
//	    @Before
//	    @Ignore
//	    public final void getStructuredTestOutput() throws Exception
//	    {
//	    	endNoteTestOutput = new String(TestHelper.readBinFile("src/test/resources/EndNoteTestOutput.txt"));
//	    	assertNotNull("EndNote output is not found", endNoteTestOutput);
//	    }

 
	    /**
	     * Test explainExport XML file
	     * @throws Exception Any exception.
	     */
	    @Test
	    public final void testExplainExport() throws Exception
	    {
	    	String result = export.explainFormats();
	        assertNotNull("explain formats file is null", result);
	        logger.info("explain formats: " + result);
	    }
	    
	    /**
	     * Test list of export formats
	     * @throws Exception Any exception.
	     */
	    @Test
	    public final void testFormatList() throws Exception
	    {
	    	String[] fl = export.getFormatsList();
	    	assertTrue("The list of export formats is empty", fl.length>0);
	    	for (String f : fl)
	    		logger.info("Export format: " + f);
	    }
	    
   
	    
	    /**
	     * Test service with a item list XML.
	     * @throws Exception Any exception.
	     */
	    @Test
	    public final void testStructuredExports() throws Exception
	    {
	    	long start;
	    	for (String f : ITEM_LISTS_FILE_MAMES.keySet()) 
	    	{
	    		logger.info("Export format: " + f);
	    		logger.info("Number of items to proceed: " + TestHelper.ITEMS_LIMIT);
	    		String itemList = ResourceUtil.getResourceAsString(ITEM_LISTS_FILE_MAMES.get(f), StructuredExportTest.class.getClassLoader());    		
	    		//logger.info("Test item list:\n" + itemList);
	    		
		    	start = System.currentTimeMillis();
		    	byte[] result = export.getOutput(itemList, f);
	    		logger.info("Processing time: " + (System.currentTimeMillis() - start) );
		    	logger.info("---------------------------------------------------");
		    	assertFalse(f + " output is empty", result == null || result.length==0 );
		    	logger.info(f + " export result:\n" + new String(result) );
		    	TestHelper.writeBinFile(result, "target/" + f + "_result.txt");
	    	}
	    	
	    }
	    
	    @Test
	    @Ignore
	    public void doExportTest() throws Exception
	    {
            String itemList = ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_book.xml", StructuredExportTest.class.getClassLoader());            
            XmlTransforming xmlTransforming = new XmlTransformingBean();
            PubItemVO itemVO = xmlTransforming.transformToPubItem(itemList);
            List<PubItemVO> pubitemList = Arrays.asList(itemVO);
            itemList = xmlTransforming.transformToItemList(pubitemList);
            byte[] result = export.getOutput(itemList, "BIBTEX");
            assertNotNull(result);
            logger.info("BIBTEX (Book)");
            logger.info(new String(result));
            
            itemList = ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_book.xml", StructuredExportTest.class.getClassLoader());            
            itemVO = xmlTransforming.transformToPubItem(itemList);
            pubitemList = Arrays.asList(itemVO);
            itemList = xmlTransforming.transformToItemList(pubitemList);
            result = export.getOutput(itemList, "ENDNOTE");
            assertNotNull(result);
            logger.info("ENDNOTE (Book)");
            logger.info(new String(result));
            
            itemList = ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_thesis.xml", StructuredExportTest.class.getClassLoader());            
            xmlTransforming = new XmlTransformingBean();
            itemVO = xmlTransforming.transformToPubItem(itemList);
            pubitemList = Arrays.asList(itemVO);
            itemList = xmlTransforming.transformToItemList(pubitemList);
            result = export.getOutput(itemList, "BIBTEX");
            assertNotNull(result);
            logger.info("BIBTEX (Thesis)");
            logger.info(new String(result));
            
            itemList = ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_thesis.xml", StructuredExportTest.class.getClassLoader());            
            itemVO = xmlTransforming.transformToPubItem(itemList);
            pubitemList = Arrays.asList(itemVO);
            itemList = xmlTransforming.transformToItemList(pubitemList);
            result = export.getOutput(itemList, "ENDNOTE");
            assertNotNull(result);
            logger.info("ENDNOTE (Thesis)");
            logger.info(new String(result));
	    }
	    
	    
	    /**
	     * Test service with a non-valid item list XML.
	     * @throws Exception 
	     * @throws Exception Any exception.
	     */
	    @Test(expected = StructuredExportManagerException.class)
	    @Ignore
	    public final void testBadItemsListEndNoteExport() throws Exception
	    {
	    	byte[] result = export.getOutput(itemLists.get("BAD_ITEM_LIST"), "ENDNOTE");
	    }
	    

}
