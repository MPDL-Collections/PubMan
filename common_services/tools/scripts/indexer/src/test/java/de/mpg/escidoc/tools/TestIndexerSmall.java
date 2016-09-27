package de.mpg.escidoc.tools;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.lucene.document.Fieldable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.escidoc.tools.util.xslt.LocationHelper;

public class TestIndexerSmall
{
	//private static final String JBOSS_SERVER_LUCENE_ESCIDOC_ALL = "C:/Test/tmp/escidoc_all";
	private static final String JBOSS_SERVER_LUCENE_ESCIDOC_ALL = "C:/Test/tmp/item_container_admin";
	//private static final String JBOSS_SERVER_LUCENE_ESCIDOC_ALL = "C:/tmp/jboss/server/default/data/index/lucene/item_container_admin";
	
	protected static Indexer indexer;
	protected static FullTextExtractor extractor;
	protected static Validator validator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		
		extractor = new FullTextExtractor();
		
		extractor.init(new File("src/test/resources/19/escidoc_2110752+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2110752+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111415+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111415+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2110507+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2110507+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111497+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111497+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111498+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111498+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111499+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111499+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111713+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111713+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2111687+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2111687+content+content.0"));
		
		extractor.init(new File("src/test/resources/19/escidoc_2120374+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2120374+content+content.0"));	
		
		extractor.init(new File("src/test/resources/19/escidoc_2165635+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2165635+content+content.0"));	
		
		extractor.init(new File("src/test/resources/19/escidoc_2149144+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2149144+content+content.0"));	
		
		extractor.init(new File("src/test/resources/19/escidoc_2149275+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2149275+content+content.0"));	
		
		extractor.init(new File("src/test/resources/19/escidoc_2149276+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2149276+content+content.0"));	
		
		extractor.init(new File("src/test/resources/19/escidoc_2149277+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2149277+content+content.0"));	
		
		extractor.init(new File("src/test/resources/19/escidoc_2149278+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2149278+content+content.0"));	
		
		extractor.init(new File("src/test/resources/19/escidoc_2086800+content+content.0"));
		extractor.extractFulltexts(new File("src/test/resources/19/escidoc_2086800+content+content.0"));	
		
	}
	
	@Before
	public void setUp() throws Exception
	{
		indexer = new Indexer(new File("src/test/resources/20"));
		indexer.init();

		indexer.setCreateIndex(true);
		indexer.prepareIndex();
		indexer.getIndexingReport().clear();
		
		LocationHelper.getLocation("escidoc_persistent22");
		
	}

	@Test
	// escidoc_2110119 with component escidoc_2110752
	// escidoc:2110541 item mit 1 components (escidoc:2111415 internal, public visibility)
	// escidoc:2095302 item mit 1 locator
	// some items have no reference
	public void testDir() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20"));
		indexer.finalizeIndex();
		
		switch (indexer.getCurrentIndexMode())
		{
		case LATEST_RELEASE:
			assertTrue("Expected 19 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 19);
			
			assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 2);
			assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
			assertTrue("Is "+ indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType(), 
					indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 71);
			break;
		case LATEST_VERSION:
			assertTrue("Expected 22 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 22);
			
			assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 2);
			assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
			assertTrue("Is "+ indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType(), 
					indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 68);
			break;
		}
	}
	
	@Test
	// escidoc_2110501 without component
	// has reference
	public void testItemWithoutComponent() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110501"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		
		validator.compareToReferenceIndex();
		
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getErrorList().size() == 0);
				
	}
	
	@Test
	// escidoc_2028454 without component, many authors
	// has no reference
	public void testItemMultipleAuthors() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2028454"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument(); 
		assertTrue(fieldMap != null);
		
		switch(indexer.currentIndexMode)
		{
			case LATEST_RELEASE:
				fieldMap.get("escidoc.publication.compound.publication-creator-names").iterator().next().equals("sykora");		
				break;
			case LATEST_VERSION:
				fieldMap.get("/md-records/md-record/publication/creator/person/family-name").iterator().next().equals("sykora");
				break;
		}	
		
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(/*"is " + indexer.getIndexingReport().getErrorList().iterator().next(),*/
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2110541 item with 1 components (escidoc:2111415 internal, public visibility)
	// has reference
	@Test
	public void testItemWithVisibleComponent() throws Exception
	{	
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110541"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getErrorList().size() == 0);
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
	
		validator.compareToReferenceIndex();
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		Set<Fieldable> fields = null;
		switch(indexer.currentIndexMode)
		{
			case LATEST_RELEASE:
				fields = fieldMap.get("xml_representation");				
				break;
			case LATEST_VERSION:
				fields = fieldMap.get("aa_xml_representation");
				break;
		}	
		
		assertTrue(fields != null);
		assertTrue(fields.size() == 1);
		
		Fieldable xml_representation = fields.iterator().next();
		
		// check if checksum element exists
		assertTrue(xml_representation.stringValue().contains("checksum"));
	}
	
	// escidoc:2095302 item with 1 locator escidoc:2095301
	// has no reference
	@Test
	public void testItemWithLocator() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2095302"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		Set<Fieldable> fields = fieldMap.get(getFieldNameFor("stored_filename1"));
		
		switch(indexer.getCurrentIndexMode())
		{
		case LATEST_RELEASE:
		case LATEST_VERSION:
			assertTrue(fields == null);
			assertTrue(fieldMap.get(getFieldNameFor("stored_filename1")) == null);
			assertTrue(fieldMap.get(getFieldNameFor("stored_fulltext1")) == null);
			
			assertTrue(fieldMap.get(getFieldNameFor("stored_filename")) == null);
			assertTrue(fieldMap.get(getFieldNameFor("stored_fulltext")) == null);
		}
	}
	
	// escidoc:2146780 item with 1 component (escidoc:2147085 internal-managed, visibility private)
	// has no reference
	@Test
	public void testItemWithPrivateComponent() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2146780"));
		indexer.finalizeIndex();
				
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		Set<Fieldable> fields = fieldMap.get(getFieldNameFor("stored_filename1"));
		assertTrue(fields == null);
		fields = fieldMap.get(getFieldNameFor("stored_fulltext"));
		assertTrue(fields == null);
	}

	
	// escidoc:2110484 import task item with 2 component (escidoc:211083 and escidoc:2110482)
	// has no reference
	@Test
	public void testImportTaskItem() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110484"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		// import imtems are skipped in all cases
		assertTrue("Expected 0 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 1);
		assertTrue(fieldMap == null);		
	}
	
	// escidoc:699472 import task item with 1 component (escidoc:699471)
	// has no reference, caused heap size exception
	@Test
	public void testImportTaskItem_699472() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_699472"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		// import imtems are skipped in all cases
		assertTrue("Expected 0 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 1);
		assertTrue(fieldMap == null);		
	}
	
	// escidoc:2087435 yearbook item 
	// has no reference, caused heap size exception
	@Test
	public void testYearBookItem_2087435() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2087435"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		// import imtems are skipped in all cases
		assertTrue("Expected 0 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 1);
		assertTrue(fieldMap == null);		
	}
	
	// escidoc:2096427 yearbook item 
	// has no reference, caused heap size exception
	@Test
	public void testYearBookItem_2096427() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2096427"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		// import imtems are skipped in all cases
		assertTrue("Expected 0 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 1);
		assertTrue(fieldMap == null);		
	}
	
	// escidoc:590478 import task item with 1 component (escidoc:946971)
	// has no reference, caused heap size exception
	@Test
	public void testImportTaskItem_590478() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_590478"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		// import imtems are skipped in all cases
		assertTrue("Expected 0 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 1);
		assertTrue(fieldMap == null);		
	}
	
	// escidoc:2087580 item without component in status pending
	// has no reference
	@Test
	public void testPendingItem() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2087580"));
		indexer.finalizeIndex();
	
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		switch(indexer.getCurrentIndexMode())
		{
		case LATEST_RELEASE:
			assertTrue("Expected 0 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
			assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
			assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);

			assertTrue(fieldMap == null);			
			break;
			
		case LATEST_VERSION:
			assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
			assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
			assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
			
			assertTrue(fieldMap != null);	
			
			assertTrue(fieldMap.get("/properties/latest-release/id") == null);
			assertTrue(fieldMap.get("sort/properties/latest-release/id") == null);
			break;	
		}
	}
	
	// escidoc:2165636 item with component escidoc:2165635 in status pending
	// has no reference
	@Test
	public void testPendingItemWithComponent() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2165636"));
		indexer.finalizeIndex();
	
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		switch(indexer.getCurrentIndexMode())
		{
		case LATEST_RELEASE:
			assertTrue("Expected 0 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
			assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
			assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);

			assertTrue(fieldMap == null);			
			break;
			
		case LATEST_VERSION:
			assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
			assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
			assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
			
			assertTrue(fieldMap != null);	
			
			assertTrue(fieldMap.get("/properties/latest-release/id") == null);
			assertTrue(fieldMap.get("sort/properties/latest-release/id") == null);
			assertTrue(fieldMap.get("/components/component/properties/creation-date").iterator().next().stringValue().equals("2015-07-14T07:38:14.725Z"));
			break;	
		}
	}
	
	// escidoc:2148921 released item with components escidoc:2149144, 2149275, 2149276, 2149277, 2149278
	// has no reference
	@Test
	public void testReleasedItemWithManyComponents() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2148921"));
		indexer.finalizeIndex();
	
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		assertTrue("Expected 1 Found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);

		assertTrue(fieldMap != null);			
		
		switch(indexer.getCurrentIndexMode())
		{
		case LATEST_RELEASE:
			
			break;
			
		case LATEST_VERSION:
			
			assertTrue(fieldMap.get("/properties/latest-release/id") != null);
			assertTrue(fieldMap.get("sort/properties/latest-release/id") == null);
			assertTrue(fieldMap.get("/components/component/properties/creation-date").iterator().next().stringValue().startsWith("2015"));
			break;	
		}
	}
	
	// escidoc:2110486 released item with locator escidoc:2110485
	// has reference
	@Test
	public void testReleased_2110486_locator() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110486"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);
		
		Set<Fieldable> fields = fieldMap.get(getFieldNameFor("stored_filename1"));
		assertTrue(fields == null);
		assertTrue(fieldMap.get(getFieldNameFor("stored_filename1")) == null);
		assertTrue(fieldMap.get(getFieldNameFor("stored_fulltext1")) == null);
		
		assertTrue(fieldMap.get(getFieldNameFor("stored_filename")) == null);
		assertTrue(fieldMap.get(getFieldNameFor("stored_fulltext")) == null);
		
		validator.compareToReferenceIndex();
		
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2110474 released item (2 versions)
	// has reference
	@Test
	public void testReleasedItemWithoutComponent() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110474"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		
		validator.compareToReferenceIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);
		
		
	}
	
	// escidoc:2110495 released item (1 locator escidoc:2110494)
	// has reference
	@Test
	public void testReleasedItem_2110495() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110495"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);
		
		Set<Fieldable> fields = fieldMap.get(getFieldNameFor("stored_filename1"));
		assertTrue(fields == null);
		assertTrue(fieldMap.get(getFieldNameFor("stored_filename1")) == null);
		assertTrue(fieldMap.get(getFieldNameFor("stored_fulltext1")) == null);
		
		assertTrue(fieldMap.get(getFieldNameFor("stored_filename")) == null);
		assertTrue(fieldMap.get(getFieldNameFor("stored_fulltext")) == null);
		
		//assertTrue(fieldMap.get("escidoc.property.created-by.name").equals("Nadine Schröder"));

		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		
		validator.compareToReferenceIndex();	
		
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2110508 released item (1 component escidoc:2110507)
	// has reference
	@Test
	public void testReleasedItem_2110508() throws Exception
	{

		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110508"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		
		validator.compareToReferenceIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);
			
			
		}
	
	// escidoc:2110529 released item with locator (escidoc:2110533), many authors
	// has reference
	@Test
	public void testReleasedItem_2110529_locator() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110529"));
		indexer.finalizeIndex();
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		
		validator.compareToReferenceIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);	
		
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2110549 released item with locator (escidoc:2110548)
	// has reference
	@Test
	public void testReleasedItem_2110549_locator() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110549"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		assertTrue(fieldMap != null);		
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		
		validator.compareToReferenceIndex();	
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2110608 withdrawn item
	// has no reference in escidoc_all index because of withdrawn status
	@Test
	public void testWithdrawnItem_2110608() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110608"));
		indexer.finalizeIndex();
		
		switch(indexer.currentIndexMode)
		{
			case LATEST_RELEASE:
				assertTrue("Expected 0 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
				assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 1);
				break;
			case LATEST_VERSION:
				assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
				assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
				break;
		}	
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
	}
	
	// escidoc:2149549 released item, no component, no locator
	// has no reference - test for organizational unit resolution
	@Test
	public void testItem_2149549() throws Exception
	{
		indexer.createDatabase();
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2149549"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);

		validator = new Validator(indexer);
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		Set<Fieldable> fields = null;
		
		switch(indexer.getCurrentIndexMode())
		{
			case LATEST_RELEASE:
				fields = fieldMap.get("escidoc.publication.creator.compound.organization-path-identifiers");
				break;
			case LATEST_VERSION:
				fields = fieldMap.get("/md-records/md-record/publication/creator/compound/organization-path-identifiers");
		}
		assertTrue(fields != null);
		
		Iterator<Fieldable> it = fields.iterator();
		
		while(it.hasNext())
		{
			String s = ((Fieldable)it.next()).stringValue();
			assertTrue(s.contains("escidoc:24022"));
			assertTrue(s.contains("escidoc:24021"));
			assertTrue(s.contains("escidoc:persistent13"));
		}
		
	}
	
	// escidoc:2111614 released item without component
	// has reference, but indexer errors
	@Test
	public void testReleasedItem_2111614() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2111614"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
	}
	
	// escidoc:2111689 released item with 2 component; escidoc:2111688 locator and escidoc:2111687 component with pdf
	// has reference, but index errors
	@Test
	public void testReleasedItem_2111689() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2111689"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
	}
	
	// escidoc:2111495 released item with 4 component; escidoc:2111493 locator and escidoc:2111497, 2111498, 2111499 components with pdf
	// has reference
	@Test
	public void testReleasedItem_2111495() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2111495"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		
		validator.compareToReferenceIndex();
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2111711 released item with 3 component; escidoc:2111710 locator and escidoc:2111713 pdf (audience), escidoc:2111714 htm (public)
	// has reference
	@Test
	public void testReleasedItem_2111711() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2111711"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		// one component has audience visibility, the other is of mime-type text/html (non supported for text extraction) 
		Set<Fieldable> fields = fieldMap.get(getFieldNameFor("stored_filename"));
		
		switch(indexer.getCurrentIndexMode())
		{
		case LATEST_RELEASE:
			assertTrue(fields == null);
			assertTrue(fieldMap.get(getFieldNameFor("stored_filename")) == null);
			assertTrue(fieldMap.get(getFieldNameFor("stored_fulltext")) == null);
			break;
		
		case LATEST_VERSION:
			assertTrue(fields != null);
			assertTrue(fieldMap.get(getFieldNameFor("stored_filename")) != null);
			assertTrue(fieldMap.get(getFieldNameFor("stored_fulltext")) != null);
			break;
		}
		validator.compareToReferenceIndex();
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:2120373 released item with 1 component; escidoc:2120374 text/plain (public)
	// has reference
	@Test
	public void testReleasedItem_2120373() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2120373"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
		validator = new Validator(indexer);
		validator.setReferencePath(JBOSS_SERVER_LUCENE_ESCIDOC_ALL);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		// one component has audience visibility, the other is of mime-type text/html (non supported for text extraction) 
		Set<Fieldable> fields = fieldMap.get(getFieldNameFor("stored_filename"));
		assertTrue(fields != null);
		assertTrue(fieldMap.get(getFieldNameFor("stored_filename")) != null);
		assertTrue(fieldMap.get(getFieldNameFor("stored_fulltext")) != null);
		
		Iterator<Fieldable> it = fieldMap.get(getFieldNameFor("stored_fulltext")).iterator();
		boolean found = false;
		while (it.hasNext())
		{
			Fieldable f = (Fieldable)it.next();
			
			if (f.stringValue().contains("Book"))
			{
				found = true;
				break;
			}
		}
		assertTrue(found);
		
		validator.compareToReferenceIndex();
		assertTrue(Arrays.toString(indexer.getIndexingReport().getErrorList().toArray()), 
				indexer.getIndexingReport().getErrorList().size() == 0);
	}
	
	// escidoc:21636403 released item with 1 component; escidoc:2163639 pdf (public), component file is missing in fulltexts directory
	@Test
	public void testItemComponentMissing_2163640() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2163640"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 0 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 0);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 1);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
		validator = new Validator(indexer);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		assertTrue(fieldMap == null);
	}
	
	// escidoc:2086801 released item with 1 component; escidoc:2086800 pdf (public)
	@Test
	public void testItemWithRevision_2086801() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2086801"));
		indexer.finalizeIndex();
		
		assertTrue("Expected 1 found " + indexer.getIndexingReport().getFilesIndexingDone(), indexer.getIndexingReport().getFilesIndexingDone() == 1);
		assertTrue(indexer.getIndexingReport().getFilesErrorOccured() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfTime() == 0);
		assertTrue(indexer.getIndexingReport().getFilesSkippedBecauseOfStatusOrType() == 0);
		
		validator = new Validator(indexer);
		
		Map<String, Set<Fieldable>> fieldMap = validator.getFieldsOfDocument();
		
		assertTrue(fieldMap != null);
	}
	
	// Tests index append mode
	@Test
	public void testIndexWriteMode() throws Exception
	{
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2120373"));
		indexer.finalizeIndex();
		
		Properties properties = new Properties();
		InputStream s = getClass().getClassLoader().getResourceAsStream("indexer.properties");
		
		if (s != null)
		{
			properties.load(s);
		}
		else 
		{
			throw new FileNotFoundException("Properties not found ");
		}
	
		assertTrue(indexer.getIndexWriter().maxDoc() == 1);

		// add one index document more
		indexer = new Indexer(new File("src/test/resources/20"));
		indexer.init();
		indexer.setCreateIndex(false);
		indexer.prepareIndex();
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2111711"));
		indexer.finalizeIndex();
		assertTrue("Found " + indexer.getIndexWriter().maxDoc(), indexer.getIndexWriter().maxDoc() == 2);
		
		// and again
		indexer = new Indexer(new File("src/test/resources/20"));
		indexer.init();
		indexer.setCreateIndex(false);
		indexer.prepareIndex();
		indexer.indexItemsStart(new File("src/test/resources/20/escidoc_2110486"));
		indexer.finalizeIndex();
		assertTrue("Found " + indexer.getIndexWriter().maxDoc(), indexer.getIndexWriter().maxDoc() == 3);
		
	}
	
	private String getFieldNameFor(String name)
	{
		switch (indexer.getCurrentIndexMode())
		{
		case LATEST_RELEASE:
			return name;
		case LATEST_VERSION:
			return "aa_" + name;

		}
		return null;
	}

}
