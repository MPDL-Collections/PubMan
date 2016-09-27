/**
 * 
 */
package de.mpg.escidoc.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.trans.DynamicError;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.escidoc.sb.common.lucene.analyzer.EscidocAnalyzer;
import de.escidoc.sb.gsearch.xslt.SortFieldHelper;
import de.mpg.escidoc.tools.util.IndexingReport;
import de.mpg.escidoc.tools.util.Util;

/**
 * @author franke
 *
 */
public class Indexer
{
	private static Logger logger = Logger.getLogger(Indexer.class);
	
	static final String defaultDate = "0000-01-01 00:00:00";
	
	private static final String propFileName = "indexer.properties";
	private static Properties properties = new Properties();
	
	// create a new index or append to an existing one
	private boolean createIndex = false;
	
	// root node of those fedora objects (foxmls) in the file system  where to start the indexing operation from 
	private File baseDir;
	
	// the location of the fedora objects to be indexed is stored here 
	private File dbFile;
	
	// file name of the stylesheet to be used
	private String indexStylesheet;
	
	// name of the index inside the index to be built
	private String indexName;
	private String indexAttributesName;
	
	// absolute name of the directory containing the extracted fulltexts
	private String fulltextDir;
	
	// if the indexing operation is stopped, the name of the last successfully indexed file is found here
	// to enable resuming the indexing operation
	private String resumeFilename = "current-dir.txt";
	private String resumeDir = null;	
	
	// timestamp used to do indexing only for foxmls modified since mDateMillis
	private long mDateMillis;
	
	// number of processor available (means number of parallel threads)
	private int procCount;
	
	// points to the actual indexed directory. Used for resume operation.
	private String currentDir = null;
	
	// mime-types where indexing of contents is to be done (should correspond the configuration in fedoragsearch.properties)
	private String mimetypes;
	
	// the transformed indexing stylesheet (e.g. includes resolved, xalan -> saxon transformed..)
	private File stylesheetTmpFile = null;
	private Stack<Transformer> transformerStackFoxml2Escidoc = new Stack<Transformer>();
	private Stack<Transformer> transformerStackEscidoc2Index = new Stack<Transformer>();
	
	private TransformerFactory saxonFactory = new net.sf.saxon.TransformerFactoryImpl();
	private Transformer transformerStylesheet = null;

	// absolute path the index location 
	protected String indexPath = "";
	
	IndexWriter indexWriter;
	
	// report for ...
	private IndexingReport indexingReport = new IndexingReport();
	
	private ExecutorService executor = null;
	
	public enum IndexMode {LATEST_VERSION, LATEST_RELEASE};
	
	public IndexMode currentIndexMode = IndexMode.LATEST_VERSION;
	

	
	/**
	 * Constructor with initial base directory, should be the fedora "objects" directory.
	 * 
	 * @param baseDir
	 * @param indexName 
	 * @throws Exception
	 */
	public Indexer(File baseDir) throws Exception
	{
		this.baseDir = baseDir;
		
		int numFilesInDirectory = Util.countFilesInDirectory(baseDir);
		this.indexingReport.setFilesTotal(numFilesInDirectory);

		logger.info("Found " + numFilesInDirectory + " files to index in " + baseDir.getAbsolutePath());

		InputStream s = getClass().getClassLoader().getResourceAsStream(propFileName);
		
		if (s != null)
		{
			properties.load(s);
			logger.info(properties.toString());
		}
		else 
		{
			throw new FileNotFoundException("Not found " + propFileName);
		}
		
	}
	
	/*
	 * generates the index db if property 'index.db.file.generate = true'
	 * initializes the resume file
	 * 
	 */
	public void init() throws Exception
	{
		this.fulltextDir = properties.getProperty("index.fulltexts.path");
		
		this.dbFile = new File(properties.getProperty("index.db.file"));
		if ("true".equals(properties.getProperty("index.db.file.generate")))
		{
			this.createDatabase();
		}
		
		if ("true".equals(properties.getProperty("index.result.directory.create")))
		{
			createIndex = true;
		}
		else
		{
			createIndex = false;
		}
		
		this.indexPath = properties.getProperty("index.result.directory");
		if (!(new File(indexPath)).exists())
		{
			FileUtils.forceMkdir(new File(indexPath));
		}
		
		this.indexStylesheet = properties.getProperty("index.stylesheet");
		
		// if not set by command line
		if (this.indexName == null)
		{
			this.indexName = properties.getProperty("index.name.built");
		}
		this.currentIndexMode = ("escidoc_all".equals(this.indexName) ? IndexMode.LATEST_RELEASE : IndexMode.LATEST_VERSION);
		this.indexAttributesName = properties.getProperty("index.stylesheet.attributes");
				
		String mDate = properties.getProperty("index.modification.date", "0");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");					
		String combinedDate = mDate + defaultDate.substring(mDate.length());
		mDateMillis = dateFormat.parse(combinedDate).getTime();
		
		this.mimetypes = readMimetypes();
		this.procCount = Integer.parseInt(properties.getProperty("index.number.processors"));;
		
		// Create temp file for modified index stylesheet
		stylesheetTmpFile = File.createTempFile("stylesheet", ".tmp");
		
		logger.info("transforming index stylesheet to " + stylesheetTmpFile);		
		long s3 = System.currentTimeMillis();
		
		// transformerStylesheet = saxonFactory.newTransformer(new StreamSource(new File("./target/classes/prepareStylesheet.xsl")));
		transformerStylesheet = saxonFactory.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream("prepareStylesheet.xsl")));

		//transformerStylesheet.setParameter("attributes-file", indexAttributesName.replace("\\", "/"));
		transformerStylesheet.transform(new StreamSource(getClass().getClassLoader().getResourceAsStream(indexStylesheet)), new StreamResult(stylesheetTmpFile));
		
		long e3 = System.currentTimeMillis();
		logger.info("transforming index stylesheet used <" + (e3-s3) + "> ms");
		
		for (int i = 0; i <= procCount + 1; i++)
		{		
			Transformer transformerFoxml2escidoc = saxonFactory.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream("foxml2escidoc.xsl")));
			transformerFoxml2escidoc.setParameter("index-db", dbFile.getAbsolutePath().replace("\\", "/"));
			transformerFoxml2escidoc.setParameter("version", "escidoc_all".equals(this.indexName) ? "latest-release" : "latest-version");
			transformerFoxml2escidoc.setParameter("number", i);
			transformerStackFoxml2Escidoc.push(transformerFoxml2escidoc);
			
			Transformer transformerEscidoc2Index = saxonFactory.newTransformer(new StreamSource(stylesheetTmpFile));
			//Xalan transformation not possible due to needed XSLT2 functions
			//transformer2 = xalanFactory.newTransformer(new StreamSource(tmpFile));
			transformerEscidoc2Index.setParameter("index-db", dbFile.getAbsolutePath().replace("\\", "/"));
			transformerEscidoc2Index.setParameter("SUPPORTED_MIMETYPES", mimetypes);
			transformerEscidoc2Index.setParameter("fulltext-directory", fulltextDir.replace("\\", "/"));
			transformerStackEscidoc2Index.push(transformerEscidoc2Index);
		}
		
		logger.info("init transformerStackFoxml2Escidoc size <" + transformerStackFoxml2Escidoc.size() + "> ");
		logger.info("init transformerStackEscidoc2Index size <" + transformerStackEscidoc2Index.size() + "> ");
		
		// create executor corresponding the number of processors
		executor = Executors.newFixedThreadPool(procCount);
		
		File resumeFile = new File(resumeFilename);
		char[] buffer = new char[2048];
		if (resumeFile.exists())
		{
			FileReader reader = new FileReader(resumeFile);
			int len = reader.read(buffer);
			reader.close();
			if (len > 0)
			{
			resumeDir = new String(buffer, 0, len);
			logger.info("Resuming at directory " + resumeDir);
			}
		}		
	}

	/**
	 * 
	 * If everything went fine, delete the file with the current directory.
	 * 
	 */
	private void removeResumeFile() {
		File resumeFile = new File(resumeFilename);
		resumeFile.delete();
	}
	
	public String getIndexPath()
	{
		return this.indexPath;
	}
	
	public IndexingReport getIndexingReport()
	{
		return this.indexingReport;
	}
	
	// only for JUnit purposes
	IndexWriter getIndexWriter()
	{
		return this.indexWriter;
	}
	
	public void setCreateIndex(boolean b)
	{
		this.createIndex = b;
	}
	
	public IndexMode getCurrentIndexMode()
	{
		return this.currentIndexMode;
	}
	
	public void setLastModificationDate(String mDate)
	{
	    long x = mDateMillis;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");                  
        String combinedDate = mDate + defaultDate.substring(mDate.length());
        try {
            this.mDateMillis = dateFormat.parse(combinedDate).getTime();
        } catch (ParseException e) {
            mDateMillis = x;
        }
	    
	}

	/**
	 * Read the allowed mimetypes for fulltexts from a file.
	 * 
	 * @return A String holding the mimetype list that is given to the transformation as a parameter.
	 * @throws Exception Any exception.
	 */
	private String readMimetypes() throws Exception
	{
		String line;
		StringWriter result = new StringWriter();		
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("mimetypes.txt")));
		
		while ((line = reader.readLine()) != null)
		{
			result.write(line);
			result.write("\n");
		}
		return result.toString();
	}

	/**
	 * Open lucene index for writing.
	 */
	public boolean prepareIndex()
	{
	    try {
	    	logger.info("Indexing to directory '" + indexPath);

	    	Directory dir = FSDirectory.open(new File(indexPath));
	    	// :Post-Release-Update-Version.LUCENE_XY:
	    	final Analyzer analyzer = new EscidocAnalyzer();
	    	IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_34, analyzer);
	    	LogByteSizeMergePolicy logMergePolicy = new LogByteSizeMergePolicy();

	    	if (createIndex) {
	    	  // Create a new index in the directory, removing any
	    	  // previously indexed documents:
	    	  iwc.setOpenMode(OpenMode.CREATE);
	    	} else {
	    	  // Add new documents to an existing index:
	    	  iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	    	}

	    	// Optional: for better indexing performance, if you
	    	// are indexing many documents, increase the RAM
	    	// buffer.  But if you do this, increase the max heap
	    	// size to the JVM (eg add -Xmx512m or -Xmx1g):
	    	//
	    	iwc.setRAMBufferSizeMB(256.0);
	    	
	    	logMergePolicy.setMaxMergeDocs(5);	    	
	    	iwc.setMergePolicy(logMergePolicy);

	    	indexWriter = new IndexWriter(dir, iwc);

	    	// NOTE: if you want to maximize search performance,
	    	// you can optionally call forceMerge here.  This can be
	    	// a terribly costly operation, so generally it's only
	    	// worth it when your index is relatively static (ie
	    	// you're done adding documents to it):
	    	//
	    	// writer.forceMerge(1);
	    	
	    	return true;

	    } catch (Exception e) {
	    	logger.info(" caught a " + e.getClass() +
	    	 "\n with message: " + e.getMessage());
	    	return false;
	    }
	}
	
	public void finalizeIndex() throws Exception
	{
		executor.shutdown();
		executor.awaitTermination(60, TimeUnit.SECONDS);		

		while (!executor.isTerminated())
		{
			Thread.sleep(5*1000);
			logger.info("Sleeping...");
		}
		logger.info(this.getIndexingReport().toString());
		
		indexWriter.optimize();
		indexWriter.close();
		
//		stylesheetTmpFile.delete();
	}

	/**
	 * Gather information from the FOXMLs and write it into the given file.
	 * 
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public void createDatabase() throws Exception
	{
		logger.info("Starting create index database");
		
		FileOutputStream fileOutputStream = new FileOutputStream(dbFile);
		fileOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
		fileOutputStream.write("<index>\n".getBytes("UTF-8"));
		checkDir(baseDir, fileOutputStream);
		fileOutputStream.write("</index>\n".getBytes("UTF-8"));
		fileOutputStream.close();
		
		logger.info("Creating index database finished");
	}
	
	/**
	 * read the contents of a directory and write it to index db.
	 * 
	 * @param dir Current directory or file.
	 * @param fileOutputStream The stream pointing to the index db.
	 * @throws Exception Any exception.
	 */
	private void checkDir(File dir, FileOutputStream fileOutputStream) throws Exception
	{
		if (dir.isFile())
		{
			fileOutputStream.write(("<object name=\"" + dir.getName().replace("_", ":") + "\" path=\"" + dir.getAbsolutePath().replace("\\", "/") + "\"/>\n").getBytes("UTF-8"));
			return;
		}
		
		for (File file : dir.listFiles())
		{
			if (file.isDirectory())
			{
				checkDir(file, fileOutputStream);
			}
			else
			{
				fileOutputStream.write(("<object name=\"" + file.getName().replace("_", ":") + "\" path=\"" + file.getAbsolutePath().replace("\\", "/") + "\"/>\n").getBytes("UTF-8"));
			}
		}
	}

	/**
	 * Initialize indexing process and wait until all processes are ready.
	 * 
	 * @param baseDir The base directory.
	 * @throws Exception Any exception.
	 */
	public void indexItemsStart(File baseDir) throws Exception
	{
		try
		{
			indexItems(baseDir);
		}
		catch (Exception e)
		{
			logger.warn("Indexing interrupted at <" + currentDir + ">", e);
			FileWriter writer = new FileWriter(new File(resumeFilename));
			writer.write(currentDir);
		}		
	}
	
	/**
	 * Index a directory.
	 * 
	 * @param dir The directory.
	 * @throws Exception Any exception.
	 */
	private void indexItems(File dir) throws Exception
	{
		currentDir = dir.getAbsolutePath();
		if (resumeDir == null || resumeDir.startsWith(currentDir) || currentDir.compareTo(resumeDir) > 0)
		{
			if (dir.isFile())
			{
				Runnable task = new IndexThread(dir);
				executor.execute(task);
				return;
			}
			
			File[] files = dir.listFiles();
			Arrays.sort(files);
			
			for (File file : files)
			{
				if (file.isDirectory())
				{
					//logger.info("Indexing directory " + file);
					indexItems(file);
				}
				else if (file.lastModified() >= mDateMillis)
				{
					Runnable task = new IndexThread(file);
					executor.execute(task);
				}
				else if (file.lastModified() < mDateMillis)
				{
				    logger.info("Skipping because of date <" + file.getName() + ">");
					indexingReport.incrementFilesSkippedBecauseOfTime();
				}
			}
		}
		else
		{
			logger.info("Omitting directory " + currentDir);
		}
	}
	
	/**
	 * Subclass to enable parallelization.
	 * 
	 * @author franke
	 *
	 */
	class IndexThread implements Runnable
	{
		File file;
		Transformer transformer1;
		Transformer transformer2;
		
		Logger threadLogger = Logger.getLogger(IndexThread.class);
		
		/**
		 * Constructor with the FOXML file.
		 * 
		 * @param file The FOXML file
		 */
		public IndexThread(File file)
		{
			this.file = file;
		}
		
		public void run()
		{
			this.transformer1 = transformerStackFoxml2Escidoc.pop();
			this.transformer2 = transformerStackEscidoc2Index.pop();
			
			try
			{
				threadLogger.info("Start indexItem <" + file.getName() + ">");
				long start = System.currentTimeMillis();
				indexItem(file);
				long end = System.currentTimeMillis();
				threadLogger.info("Total time used for <" + file.getName() + "> <" + (end - start) + "> ms");	
			}
			catch (Exception e)
			{
				cleanup();
				indexingReport.addToErrorList(file.getName());
				indexingReport.incrementFilesErrorOccured();
				
				// should be called only in case of error explicitly; usual it's called in DocumentHandler
				threadLogger.warn("Error occured during indexing <" + file.getName() + ">", e);
				return;
				//throw new RuntimeException(e);
			}
			cleanup();
		}
		
		void cleanup()
		{
			if (checkForCleanDocumentPool())
			{
				((net.sf.saxon.Controller)transformer1).clearDocumentPool();
				((net.sf.saxon.Controller)transformer2).clearDocumentPool();
				
				logger.info("Document pool cleaned");
			}	
			
			transformerStackFoxml2Escidoc.push(transformer1);
			transformerStackEscidoc2Index.push(transformer2);
			
			threadLogger.info(transformer1.getParameter("number") + " done.");
		}
		
		private boolean checkForCleanDocumentPool()
		{
			if((indexingReport.getFilesSkippedBecauseOfStatusOrType() % 100) == 0 || (indexingReport.getFilesIndexingDone() % 100) == 0)
			{
				return true;
			}
			return false;
		}

		/**
		 * Do all the necessary transformations and build the index document.
		 * 
		 * @param file The FOXML file.
		 * @throws Exception Any exception.
		 */
		private void indexItem(File file) throws Exception
		{
			threadLogger.info("Indexing file " + file);
			
			StringWriter writer1 = new StringWriter();
			StringWriter writer2 = new StringWriter();
			
			try
			{
				long s1 = System.currentTimeMillis();
				transformer1.transform(new StreamSource(file), new StreamResult(writer1));
				long e1 = System.currentTimeMillis();
				threadLogger.info("FOXML2eSciDoc transformation used <" + (e1 - s1) + "> ms");
				
				if (threadLogger.isDebugEnabled())
				{
					File tmpFile1 = File.createTempFile(file.getName() + "_foxml2escidoc_", ".tmp");
					FileUtils.writeStringToFile(tmpFile1, writer1.toString());
				}
			}
			catch (DynamicError de)
			{
				if ("noitem".equals(de.getErrorCodeLocalPart()))
				{
					threadLogger.info("No item in < " + file + ">");
					indexingReport.incrementFilesSkippedBecauseOfStatusOrType();
					
				} 
				else if ("wrongStatus".equals(de.getErrorCodeLocalPart()))
				{
					threadLogger.info("Item in wrong public status < " + file + ">");
					indexingReport.incrementFilesSkippedBecauseOfStatusOrType();
					
				}
				else if ("nocomponent".equals(de.getErrorCodeLocalPart()))
				{
					threadLogger.info("Component file missing for < " + file + ">");
					indexingReport.addToErrorList(file.getName());
					indexingReport.incrementFilesErrorOccured();
				}
				return;
			}		
						
			long s2 = System.currentTimeMillis();
			transformer2.transform(new StreamSource(new StringReader(writer1.toString())), new StreamResult(writer2));
			long e2 = System.currentTimeMillis();
			threadLogger.info("eSciDoc2IndexDoc transformation used <" + (e2 - s2) + "> ms");
			
			if (threadLogger.isDebugEnabled())
			{
				File tmpFile2 = File.createTempFile(file.getName() + "_idx_", ".tmp");			
				FileUtils.writeStringToFile(tmpFile2, writer2.toString());
			}
			
			try
			{
				indexDoc(new StringReader(writer2.toString()));
			} 
			catch (IOException e)
			{
				threadLogger.warn("IO Exception occured", e);
				return;
			}
			indexingReport.incrementFilesIndexingDone();
		}
		
		/**
		 * Write the contents of the index document and the fulltext to the lucene index.
		 * 
		 * @param inputStream The index document.
		 * @throws ParserConfigurationException 
		 * @throws Exception Any exception.
		 */
		public void indexDoc(StringReader inputStream) throws IOException, SAXException, ParserConfigurationException
		{

		      try {

		        // make a new, empty document
		        Document doc = new Document();
		        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		        DefaultHandler dh = new IndexDocument(doc, fulltextDir);
		        parser.parse(new InputSource(inputStream), dh);

		        if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE)
		        {
					// New index, so we just add the document (no old document can be there):
					indexWriter.addDocument(doc);
		        }
		        else
		        {
		        	// Existing index (an old copy of this document may have been indexed) so 
		        	// we use updateDocument instead to replace the old one matching the exact 
		        	// path, if present:
		        	indexWriter.updateDocument(new Term("PID", doc.get("PID")), doc);
		        }
		        if ((indexingReport.getFilesIndexingDone() % 5000) == 0)
	        	{
	        		indexWriter.optimize();
	        		threadLogger.info("IndexWriter optimize called");
	        	}		        
		      }
		      finally
		      {
		    	  inputStream.close();
		      }
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args Command line parameters.
	 */
	public static void main(String[] args) throws Exception
	{
		String mode = args[0];
		File baseDir = null;
		File referenceIndexDir = null;
		
		if (mode == null ||  (!mode.contains("c") && !mode.contains("i")) && !mode.contains("v"))		
		{
			printUsage("Invalid parameter");
		}
		
		baseDir = new File(args[1]);
		if (baseDir == null || !baseDir.exists())
		{
			printUsage("Invalid base directory parameter <" + args[1] +">");
			System.exit(1);
		}
		
		if (mode.contains("v"))
		{
			referenceIndexDir = new File(args[2]);
			if (referenceIndexDir == null || !referenceIndexDir.exists())
			{
				printUsage("Invalid reference index directory parameter <" + args[2] +">");
				System.exit(1);
			}
		}

		Indexer indexer = new Indexer(baseDir);		
		indexer.init();
		
		if (mode.contains("c"))
		{
			indexer.createDatabase();
		}
		
		if (mode.contains("i"))
		{
			if (!indexer.prepareIndex())
			{
				logger.warn("Prepare index failed - stopping");
				System.exit(-1);
			}
		
			indexer.indexItems(baseDir);
			
			indexer.finalizeIndex();
			indexer.removeResumeFile();
		}
		
		if (referenceIndexDir != null)
		{
			Validator validator = new Validator(indexer);
			validator.setReferencePath(referenceIndexDir.getCanonicalPath());
			
		}

		logger.info(indexer.getIndexingReport().toString());
	}

	static private void printUsage(String message)
    {
        System.out.print("***** " + message + " *****");
        System.out.print("Usage: ");
        System.out.println("java -jar indexer-jar-with-dependencies.jar {civ} <rootDir> <referenceIndexDir>");
        System.out.println("  -c\t\tcreate the index database");
        System.out.println("  -i\t\tIndex the foxmls recursively from root directory");
        System.out.println("  -v\t\tValidate the the generated index");
        System.out.println("  <rootDir>\tThe root directory of the foxmls to start operation (create database and index) from");

        System.exit(-1);
    }
}
