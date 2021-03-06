package de.mpg.mpdl.migration.xbeans;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.RDFWriterFImpl;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Regex;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.util.IndentedWriter;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import de.mpg.mpdl.migration.foxml.ESCIDOCPROPERTIES;
import de.mpg.mpdl.migration.foxml.Foxml;
import de.mpg.mpdl.migration.foxml.MigrationConstants;
import fedora.fedoraSystemDef.foxml.DatastreamType;
import fedora.fedoraSystemDef.foxml.DatastreamVersionType;
import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;
import fedora.fedoraSystemDef.foxml.XmlContentType;

/**
 * TODO Description.
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class RDFTest implements MigrationConstants
{
    private static Logger pidlogger = Logger.getLogger("pidreplacement");
    private static TreeMap<String, String> dummyPIDs = null;
    private static TreeMap<String, String> dummyComponentPIDs = null;

    private RDFTest()
    {
    }

    /**
     * @param args {@link String[]}
     */
    public static void main(String[] args)
    {
        replacePIDs();
    }

    /**
     * {@inheritDoc}
     */
    public static void replacePIDs()
    {
        ArrayList<File> files = null;
        String resourceURI = null;
        String[] props = null;
        files = Foxml.fileList(new File(System.getenv("FEDORA_HOME") + "/data/objects/2009/0428/14"));
        pidlogger.info("attempting to replace PIDs in " + files.size() + " files");
        int filenum = 0;
        for (File f : files)
        {
            dummyPIDs = new TreeMap<String, String>();
            dummyComponentPIDs = new TreeMap<String, String>();
            filenum = filenum + 1;
            DigitalObjectDocument dodo = null;
            try
            {
                dodo = DigitalObjectDocument.Factory.parse(f);
                DatastreamType[] streams = dodo.getDigitalObject().getDatastreamArray();
                for (DatastreamType stream : streams)
                {
                    if (stream.getID().equalsIgnoreCase("RELS-EXT"))
                    {
                        DatastreamVersionType[] versions = stream.getDatastreamVersionArray();
                        for (DatastreamVersionType version : versions)
                        {
                            XmlContentType rdf = version.getXmlContent();
                            resourceURI = "info:fedora/escidoc:"
                                    + f.getName().substring(f.getName().indexOf("_") + 1, f.getName().length());
                            props = Foxml.getResourceType(rdf.newInputStream(), resourceURI, Integer.valueOf(version
                                    .getID().substring(version.getID().indexOf(".") + 1)));
                            // getExistingPids(rdf.newInputStream(), resourceURI);
                            searchExistingPids(rdf.newInputStream(), ESCIDOCPROPERTIES.component);
                            // XmlObject xo = XmlObject.Factory.parse(changed);
                            // rdf.set(xo);
                        }
                    }
                }
                // dodo.save(f);
                System.out.println(props[1] + "   " + props[0] + " in context " + props[3] + "   RELS-EXT." + props[2]);
                System.out.println(dummyPIDs.keySet().toString());
                System.out.println(dummyComponentPIDs.keySet().toString());
                for (String s : dummyPIDs.keySet())
                {
                    String id = s.substring(s.indexOf("escidoc:") + 8);
                    String handle = registerNewPID(id);
                    dummyPIDs.put(s, handle);
                }
                // String id = dummyPIDs.firstKey().substring(dummyPIDs.firstKey().indexOf("escidoc:") + 8);
                // String url = TEST_BASE_URL + id;
                // String handle = GWDGPidService.registerNewPID(url);
                // String handle = GWDGPidService.findHandle4URL(url);
                // System.out.println("registered handle: " + handle);
                System.out.println(dummyPIDs.toString());
                /*
                 * for (DatastreamType stream : streams) { if (stream.getID().equalsIgnoreCase("RELS-EXT")) {
                 * DatastreamVersionType[] versions = stream.getDatastreamVersionArray(); for (DatastreamVersionType
                 * version : versions) { XmlContentType rdf = version.getXmlContent(); resourceURI =
                 * "info:fedora/escidoc:" + f.getName().substring(f.getName().indexOf("_") + 1, f.getName().length());
                 * props = Foxml.getResourceType(rdf.newInputStream(), resourceURI,
                 * Integer.valueOf(version.getID().substring(version.getID().indexOf(".") + 1))); String changed =
                 * replaceExistingPids(rdf.newInputStream(), resourceURI); System.out.println(changed); XmlObject xo =
                 * XmlObject.Factory.parse(changed); rdf.set(xo); } } } dodo.save(f);
                 */
            }
            catch (XmlException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param is {@link InputStream}
     * @param uri {@link String}
     */
    public static void getExistingPids(InputStream is, String uri)
    {
        String objectPid = null;
        String versionPid = null;
        String releasePid = null;
        String componentId = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(is, "");
        Resource about = model.getResource(uri);
        String resourceType = about.getRequiredProperty(RDF.type).getResource().getLocalName();
        if (resourceType.equalsIgnoreCase("item") || resourceType.equalsIgnoreCase("container"))
        {
            if (about.getProperty(ESCIDOCPROPERTIES.pid) != null)
            {
                objectPid = about.getProperty(ESCIDOCPROPERTIES.pid).getString();
                dummyPIDs.put(objectPid, null);
            }
            if (about.getProperty(ESCIDOCPROPERTIES.versionpid) != null)
            {
                versionPid = about.getProperty(ESCIDOCPROPERTIES.versionpid).getString();
                dummyPIDs.put(versionPid, null);
            }
            if (about.getProperty(ESCIDOCPROPERTIES.releasepid) != null)
            {
                releasePid = about.getProperty(ESCIDOCPROPERTIES.releasepid).getString();
                dummyPIDs.put(releasePid, null);
            }
            if (about.getProperty(ESCIDOCPROPERTIES.component) != null)
            {
                componentId = about.getProperty(ESCIDOCPROPERTIES.component).getObject().toString();
                dummyComponentPIDs.put(componentId, null);
            }
        }
    }

    /**
     * @param is {@link InputStream}
     * @param uri {@link String}
     * @return {@link String}
     */
    public static String replaceExistingPids(InputStream is, String uri)
    {
        String versionPid = null;
        String releasePid = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(is, "");
        Resource about = model.getResource(uri);
        String resourceType = about.getRequiredProperty(RDF.type).getResource().getLocalName();
        if (resourceType.equalsIgnoreCase("item") || resourceType.equalsIgnoreCase("container"))
        {
            if (about.hasProperty(ESCIDOCPROPERTIES.pid))
            {
                String pid = dummyPIDs.get(dummyPIDs.firstKey());
                // about.removeAll(ESCIDOCPROPERTIES.pid);
                // about.addProperty(ESCIDOCPROPERTIES.pid, pid);
                about.getProperty(ESCIDOCPROPERTIES.pid).changeObject(pid);
            }
            if (about.hasProperty(ESCIDOCPROPERTIES.versionpid))
            {
                versionPid = about.getProperty(ESCIDOCPROPERTIES.versionpid).getString();
                String pid = dummyPIDs.get(versionPid);
                about.removeAll(ESCIDOCPROPERTIES.versionpid);
                about.addProperty(ESCIDOCPROPERTIES.versionpid, pid);
            }
            if (about.hasProperty(ESCIDOCPROPERTIES.releasepid))
            {
                releasePid = about.getProperty(ESCIDOCPROPERTIES.releasepid).getString();
                String pid = dummyPIDs.get(releasePid);
                about.removeAll(ESCIDOCPROPERTIES.releasepid);
                about.addProperty(ESCIDOCPROPERTIES.releasepid, pid);
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // RDFWriter w = model.getWriter("RDF/XML");
        model.write(out, "RDF/XML");
        return out.toString();
    }

    /**
     * @param id {@link String}
     * @return {@link String}
     */
    public static String registerNewPID(String id)
    {
        String newPID = null;
        newPID = TEST_BASE_URL + id;
        return newPID;
    }

    /**
     * @param is {@link InputStream}
     * @param predicate {@link Property}
     */
    public static void searchExistingPids(InputStream is, Property predicate)
    {
        String objectPid = null;
        String versionPid = null;
        String releasePid = null;
        String componentId = null;
        Model model = ModelFactory.createDefaultModel();
        model.read(is, "");
        Query query = QueryFactory.make();
        query.setQueryType(Query.QueryTypeSelect);
        ElementGroup elg = new ElementGroup();
        Var object = Var.alloc("object");
        Var subject = Var.alloc("subject");
        Triple t1 = new Triple(subject, predicate.asNode(), object);
        elg.addTriplePattern(t1);
        query.setQueryPattern(elg);
        query.addResultVar(object);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try
        {
            ResultSet rs = qexec.execSelect();
            System.out.println(predicate.getLocalName());
            for (; rs.hasNext();)
            {
                QuerySolution qs = rs.nextSolution();
                RDFNode node = qs.get(object.getName());
                if (node instanceof Resource)
                {
                    Resource resource = (Resource) node;
                    System.out.println("    resource: " + resource.toString());
                }
                else if (node instanceof Literal)
                {
                    Literal literal = (Literal) node;
                    System.out.println("    literal: " + literal.toString());
                }
            }
        }
        finally
        {
            qexec.close();
        }
    }
    
    public static File findComponent(File baseDir, String name)
    {
        if (baseDir.isDirectory())
        {
            File[] files = baseDir.listFiles();
            for (File componentFoxml : files)
            {
                if (componentFoxml.isFile() && componentFoxml.getName().matches(name))
                {
                    return componentFoxml;
                }
                else
                {
                    findComponent(componentFoxml, name);
                }
            }
        }
        return null;
    }
}
