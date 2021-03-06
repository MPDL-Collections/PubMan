package de.mpg.escidoc.main;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Test with various escidoc foxml's. 
 * Attention: Really updating pids at the GWDG using the PIDProvider instead of PIDProviderMock may cause some HTTP_Forbidden (403) return code, 
 * concerning of the GWDG user configured (001M or 001Z). The example pids are taken generated as well by user 001M as user 001Z.
 * 
 * @author sieders
 *
 */
public class ComponentPidTransformerTest
{
	private static ComponentPidTransformer pidMigr = null;
	
	private static Logger logger = Logger.getLogger(ComponentPidTransformerTest.class);
	
	@BeforeClass
	public static void before() throws Exception
	{
		FileUtils.deleteQuietly(new File(ComponentPidTransformer.LOCATION_FILE_XML));
		
		pidMigr = new ComponentPidTransformer();
		pidMigr.createLocationFile(new File("src/test/resources"));	
	}
	
	@Before
	public void setUp() throws Exception
	{
		pidMigr.getReport().clear();
			
		FileUtils.writeStringToFile(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "", false);
		
	}
	
	@Test
	public void testStoreLocation() throws Exception
	{
		pidMigr.createLocationFile(new File("src/test/resources"));		
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
	}
	
	@Test
	//
	// release version 2 - two components: escidoc_2111688 external url, escidoc_2111687 internal_managed
	public void testUpdatePid_2111689() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2111689"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 1);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 1);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:2111689 11858/00-001M-0000-0025-7377-9"));	
		} 
	}
	
	@Test
	//
	// submitted version 1 - one component: escidoc_2229668 internal_managed
	public void testUpdatePid_2229669() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2229669"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 1);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.isEmpty());	
		}
	}
	
	@Test
	//
	// release version 1 - one components: escidoc_2169636 internal_managed
	// submitted version 2 - one components: escidoc_2170639 internal_managed
	// release version 3 - one components: escidoc_2170641 internal_managed
	public void testUpdatePid_2169637() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2169637"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 1);
		assertTrue("Expected 2 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 2);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:2169637 11858/00-001Z-0000-0026-5162-7")
					|| line.contains("escidoc:2169637 11858/00-001Z-0000-0026-515F-2"));	
		}
	}

	@Test
	//
	// release version 1 - no component
	public void testUpdatePid_2110501() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2110501"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 1);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.isEmpty());	
		}
	}
	
	@Test
	//
	// release version 1 - one component escidoc_2110507
	public void testUpdatePid_2110508() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2110508"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 1);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 1);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:2110508 11858/00-001M-0000-0025-0ADE-6"));	
		}
	}
	
	@Test
	//
	// release version 1 - one component escidoc_2110507
	public void testUpdatePid_2110508_twice() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2110508"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 1);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 1);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:2110508 11858/00-001M-0000-0025-0ADE-6"));	
		}
		
		// transform again - nothing should be done
		pidMigr.getReport().clear();
		
		pidMigr.transform(new File("src/test/resources/item/escidoc_2110508"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 1);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 0);
	}
	
	@Test
	//
	// release version 1 - no component
	public void testUpdatePid_2110518() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2110518"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue(pidMigr.getReport().getFilesTotal() == 1);
		assertTrue(pidMigr.getReport().getComponentsUpdateDone() == 0);
		assertTrue(pidMigr.getReport().getFilesNotItem() == 0);
		assertTrue(pidMigr.getReport().getFilesNotReleased() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.isEmpty());	
		}
	}
	
	@Test
	//
	// version status submitted ohne component (version 2)
	// public status released mit component escidoc_2228638 (version 1)
	// one component in version 1
	public void testUpdatePid_2228639() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2228639"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue(pidMigr.getReport().getFilesTotal() == 1);
		assertTrue(pidMigr.getReport().getComponentsUpdateDone() == 1);
		assertTrue(pidMigr.getReport().getFilesNotItem() == 0);
		assertTrue(pidMigr.getReport().getFilesNotReleased() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue("is <" + line + ">", line.contains("escidoc:2228639 11858/00-001Z-0000-0026-51C0-4"));
		}
		
	}
	
	@Test
	//
	// release version 1 - no component
	// submitted version 2 - one components: escidoc_2229667 internal_managed
	// release version 3 - no component
	public void testUpdatePid_2229666() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_2229666"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 1);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.isEmpty());	
		}
	}
	
	@Test
	//
	// escidoc:656742 released item with 2 component; escidoc:656741 locator and escidoc:656740 component with html
	public void testItemMultipleReleases_656742() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item/escidoc_656742"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 1);
		assertTrue("Expected 1 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 1);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:656742"));	
		}
	}
	
	@Test
	//
	// testing object types unequal ITEM, e.g. organizational units, content-models

	public void testUpdatePid_Other() throws Exception
	{
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/other"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 2);
		assertTrue("Expected 7 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 9);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 0);
		assertTrue("Expected 5 returned " +  pidMigr.getReport().getFilesNotItem(), pidMigr.getReport().getFilesNotItem() == 6);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getFilesNotReleased(), pidMigr.getReport().getFilesNotReleased() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.isEmpty());	
		}
	}
	
	@Test
	// update the whole item test directory
	public void testUpdatePid() throws Exception
	{
		pidMigr.createLocationFile(new File("src/test/resources"));
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 10 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 10);
		assertTrue("Expected 6 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 6);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getFilesNotItem(), pidMigr.getReport().getFilesNotItem() == 0);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getFilesNotReleased(), pidMigr.getReport().getFilesNotReleased() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:656742 11858/00-001Z-0000-0023-5323-D")
					|| line.contains("escidoc:2110508 11858/00-001M-0000-0025-0ADE-6")
					|| line.contains("escidoc:2111689 11858/00-001M-0000-0025-7377-9")
					|| line.contains("escidoc:2169637 11858/00-001Z-0000-0026-515F-2")
					|| line.contains("escidoc:2169637 11858/00-001Z-0000-0026-5162-7")
					|| line.contains("escidoc:2228639 11858/00-001Z-0000-0026-51C0-4"));	
		}
	}
	
	@Test
	// update the whole item test directory twice 
	public void testUpdatePid_twice() throws Exception
	{
		pidMigr.createLocationFile(new File("src/test/resources"));
		assertTrue(new File(ComponentPidTransformer.LOCATION_FILE_XML).exists());
		pidMigr.transform(new File("src/test/resources/item"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 10 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 10);
		assertTrue("Expected 6 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 6);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getFilesNotItem(), pidMigr.getReport().getFilesNotItem() == 0);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getFilesNotReleased(), pidMigr.getReport().getFilesNotReleased() == 0);
		
		Iterator<String> it = FileUtils.readLines(new File(ComponentPidTransformer.SUCCESS_FILE_LOG), "UTF-8").iterator(); 
		while(it.hasNext())
		{
			String line = it.next();
			logger.info(line);
			assertTrue(line.contains("escidoc:656742 11858/00-001Z-0000-0023-5323-D")
					|| line.contains("escidoc:2110508 11858/00-001M-0000-0025-0ADE-6")
					|| line.contains("escidoc:2111689 11858/00-001M-0000-0025-7377-9")
					|| line.contains("escidoc:2169637 11858/00-001Z-0000-0026-515F-2")
					|| line.contains("escidoc:2169637 11858/00-001Z-0000-0026-5162-7")
					|| line.contains("escidoc:2228639 11858/00-001Z-0000-0026-51C0-4"));	
		}
		
		// transform again - nothing should be done
		pidMigr.getReport().clear();
		pidMigr.transform(new File("src/test/resources/item"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 0);
		assertTrue("Expected 10 returned " +  pidMigr.getReport().getFilesTotal(), pidMigr.getReport().getFilesTotal() == 10);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getComponentsUpdateDone(), pidMigr.getReport().getComponentsUpdateDone() == 0);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getFilesNotItem(), pidMigr.getReport().getFilesNotItem() == 0);
		assertTrue("Expected 0 returned " +  pidMigr.getReport().getFilesNotReleased(), pidMigr.getReport().getFilesNotReleased() == 0);
	}
	
	// Test to parse an empty file
	@Test
	public void testEmptyFile() throws Exception
	{
		pidMigr.getReport().clear();
		pidMigr.transform(new File("src/test/resources/other/escidoc_xxx"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 1);
		assertTrue(pidMigr.getReport().getErrorList().iterator().next().contains("escidoc_xxx"));	
	}
	
	// Test to parse a file with missing component file
	// item escidoc:898020 component escidoc:898019 missing
	@Test
	public void testMissingLocationOfComponentFile() throws Exception
	{
		pidMigr.createLocationFile(new File("src/test/resources/other"));
		pidMigr.transform(new File("src/test/resources/other/escidoc_898020"));
		
		assertTrue(pidMigr.getReport().getErrorList().size() == 1);
	}
	
	
}
