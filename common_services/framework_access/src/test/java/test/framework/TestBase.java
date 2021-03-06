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
package test.framework;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpException;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.junit.After;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Methods which are used in mostly all test cases.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision:60 $ $LastChangedDate:2007-01-25 13:08:48 +0100 (Do, 25 Jan 2007) $
 * Revised by FrW: 10.03.2008
 */


public class TestBase
{   
    /**
     * The default users id.
     */
    public static final String USERID = "escidoc:3013";
    
    /**
     * An illegal id of a framework object.
     */
    protected static final String ILLEGAL_ID = "escidoc:persistentX";
    
    /**
     * Constants for queries.
     */
    protected static final String SEARCH_RETRIEVE = "searchRetrieve";

    protected static final String QUERY = "query";

    protected static final String VERSION = "version";

    protected static final String OPERATION = "operation";

    
    /**
     * The default scientist password property.
     */
    public static final String PROPERTY_USERNAME_SCIENTIST = "framework.scientist.username";
    
    /**
     * The default scientist password property.
     */
    public static final String PROPERTY_PASSWORD_SCIENTIST = "framework.scientist.password";
    
    /**
     * The default scientist id property.
     */
    public static final String PROPERTY_ID_SCIENTIST = "framework.scientist.id";
    
    /**
     * The default librarian password property.
     */
    public static final String PROPERTY_USERNAME_LIBRARIAN = "framework.librarian.username";
    
    /**
     * The default librarian password property.
     */
    public static final String PROPERTY_PASSWORD_LIBRARIAN = "framework.librarian.password";
    
    /**
     * The default admin password property.
     */
    public static final String PROPERTY_USERNAME_AUTHOR = "framework.author.username";
    
    /**
     * The default admin  password property.
     */
    public static final String PROPERTY_PASSWORD_AUTHOR = "framework.author.password";
    
    /**
     * The default admin password property.
     */
    public static final String PROPERTY_USERNAME_ADMIN = "framework.admin.username";
    
    /**
     * The default admin  password property.
     */
    public static final String PROPERTY_PASSWORD_ADMIN = "framework.admin.password";
    
    /**
     * The default admin  password property.
     */
    public static final String PROPERTY_PUBITEM_TYPE_ID = "escidoc.framework_access.content-model.id.publication";
    
    /**
     * The id of the content model Publication Item.
     */
    public static final String PUBITEM_TYPE_ID = "escidoc.framework_access.content-model.id.publication";
    
    /**
     * The id of the test context.
     */
    public static final String PROPERTY_TEST_CONTEXT_ID = "escidoc.framework_access.context.id.test";
    
    /**
     * A line for separating output.
     */
    protected static final String LINE = "--------------------";

    /**
     * The handle of the logged in user or null if not logged in.
     */
    protected String userHandle;
    
    /**
     * Map of key - value pairs containing the filter definition
     */
    protected static final HashMap<String, String[]> filterMap = new HashMap<String, String[]>();
    
  
    /**
     * Reads contents from text file and returns it as String.
     * 
     * @param fileName The name of the input file.
     * @return The entire contents of the filename as a String.
     * @throws FileNotFoundException
     */

    public static String readFile(final String fileName) throws IOException
    {
        InputStream fileIn;
        fileIn = TestBase.class.getClassLoader().getResourceAsStream(fileName);

        if (fileIn == null)
        {
            fileIn = new FileInputStream(fileName);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fileIn, "UTF-8"));
        String line = null;
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null)
        {
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }


    /**
     * Parse the given xml String into a Document.
     * 
     * @param xml The xml String.
     * @return The Document.
     * @throws Exception If anything fails.
     */
    protected static Document getDocument(final String xml, final boolean namespaceAwareness) throws Exception
    {
        String charset = "UTF-8";
        Document result = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(namespaceAwareness);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        result = docBuilder.parse(new ByteArrayInputStream(xml.getBytes(charset)));
        result.getDocumentElement().normalize();
        return result;
    }

    /**
     * Serialize the given Dom Object to a String.
     * 
     * @param xml The Xml Node to serialize.
     * @param omitXMLDeclaration Indicates if XML declaration will be omitted.
     * @return The String representation of the Xml Node.
     * @throws Exception If anything fails.
     */
    protected static String toString(final Node xml, final boolean omitXMLDeclaration) throws Exception
    {
        String result = new String();
        if (xml instanceof AttrImpl)
        {
            result = xml.getTextContent();
        }
        else if (xml instanceof Document)
        {
            StringWriter stringOut = new StringWriter();
            // format
            OutputFormat format = new OutputFormat((Document)xml);
            format.setIndenting(true);
            format.setPreserveSpace(false);
            format.setOmitXMLDeclaration(omitXMLDeclaration);
            format.setEncoding("UTF-8");
            // serialize
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.asDOMSerializer();
            serial.serialize((Document)xml);
            result = stringOut.toString();
        }
        else
        {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
            LSOutput lsOutput = impl.createLSOutput();
            lsOutput.setEncoding("UTF-8");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            lsOutput.setByteStream(os);
            LSSerializer writer = impl.createLSSerializer();
            // result = writer.writeToString(xml);
            writer.write(xml, lsOutput);
            result = ((ByteArrayOutputStream)lsOutput.getByteStream()).toString();
            if ((omitXMLDeclaration) && (result.indexOf("?>") != -1))
            {
                result = result.substring(result.indexOf("?>") + 2);
            }
            // result = toString(getDocument(writer.writeToString(xml)),
            // true);
        }
        return result;
    }

    /**
     * Gets the value of the specified attribute of the root element from the document.
     * 
     * @param document The document to retrieve the value from.
     * @param attributeName The name of the attribute whose value shall be retrieved.
     * @return The attribute value.
     * @throws Exception If anything fails.
     * @throws TransformerException
     */
    protected static String getRootElementAttributeValue(final Document document, final String attributeName) throws Exception
    {
        String xPath;
        if (attributeName.startsWith("@"))
        {
            xPath = "/*/" + attributeName;
        }
        else
        {
            xPath = "/*/@" + attributeName;
        }
        assertXMLExist("Attribute not found [" + attributeName + "]. ", document, xPath);
        String value = selectSingleNode(document, xPath).getTextContent();
        return value;
    }

    /**
     * Return the text value of the selected attribute NOT considering namespaces.
     * 
     * @param node The node.
     * @param xPath The xpath to select the node containing the attribute.
     * @param attributeName The name of the attribute.
     * @return The text value of the selected attribute.
     * @throws Exception If anything fails.
     */
    protected static String getAttributeValue(final Node node, final String xPath, final String attributeName) throws Exception
    {
        String result = null;
        Node singleNode = selectSingleNode(node, xPath);
        if (singleNode == null)
        {
            throw new Exception("Single node for path '" + xPath + "' not found.");
        }
        if (singleNode.hasAttributes())
        {
            result = singleNode.getAttributes().getNamedItem(attributeName).getTextContent();
        }
        return result;
    }

    /**
     * Return the text value of the selected attribute considering namespaces.
     * 
     * @param node The node.
     * @param xPath The xpath to select the node containing the attribute.
     * @param attributeNamespaceURI The namespace URI of the attribute.
     * @param attributeLocalName The local name of the attribute.
     * @return The value for the given attribute.
     * @throws Exception
     */
    protected static String getAttributeValueNS(final Node node, final String xPath, final String attributeNamespaceURI,
            final String attributeLocalName) throws Exception
    {
        String result = null;
        NodeList nodeList = selectNodeList(node, xPath);
        assertTrue(nodeList.getLength() == 1);
        Node hitNode = nodeList.item(0);
        if (hitNode.hasAttributes())
        {
            NamedNodeMap nnm = hitNode.getAttributes();
            Node attrNode = nnm.getNamedItemNS(attributeNamespaceURI, attributeLocalName);
            result = attrNode.getTextContent();
        }
        return result;
    }

    /**
     * Assert that the Element/Attribute selected by the xPath exists.
     * 
     * @param message The message printed if assertion fails.
     * @param node The Node.
     * @param xPath The xpath to select the node containing the attribute.
     * @throws Exception If anything fails.
     */
    protected static void assertXMLExist(final String message, final Node node, final String xPath) throws Exception
    {
        NodeList nodes = selectNodeList(node, xPath);
        assertTrue(message, nodes.getLength() > 0);
    }

    /**
     * Assert that the XML is valid to the schema.
     * 
     * @param xmlData The XML as a String.
     * @param schemaFileName The filename of the schema.
     * @throws Exception
     */
    protected static void assertXMLValid(final String xmlData, final String schemaFileName) throws Exception
    {
        Schema schema = getSchema(schemaFileName);
        try
        {
            Validator validator = schema.newValidator();
            InputStream in = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
            validator.validate(new SAXSource(new InputSource(in)));
        }
        catch (SAXParseException e)
        {
            StringBuffer sb = new StringBuffer();
            sb.append("XML invalid at line:" + e.getLineNumber() + ", column:" + e.getColumnNumber() + "\n");
            sb.append("SAXParseException message: " + e.getMessage() + "\n");
            sb.append("Affected XML: \n"+xmlData);
            fail(sb.toString());
        }
    }

    /**
     * Gets a value from the document for the given xpath expression.
     * 
     * @param document The document.
     * @param xPath The xpath to select the node containing the attribute.
     * @return The value for the xpath expression. 
     * @throws TransformerException
     */
    protected String getValue(Document document, String xpathExpression) throws TransformerException
    {
    	XPathFactory factory = XPathFactory.newInstance();
    	XPath xPath = factory.newXPath();
    	try
    	{
    		return xPath.evaluate(xpathExpression, document);
    	}
    	catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Gets the <code>Schema</code> object for the provided <code>File</code>.
     * 
     * @param schemaStream The file containing the schema.
     * @return The <code>Schema</code> object.
     * @throws Exception If anything fails.
     */
    private static Schema getSchema(final String schemaFileName) throws Exception
    {
        if (schemaFileName == null)
        {
            throw new Exception("No schema input file name provided");
        }
        File schemaFile = new File(schemaFileName);
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema theSchema = sf.newSchema(schemaFile);
        return theSchema;
    }

    /**
     * Return the child of the node selected by the xPath.
     * 
     * @param node The node.
     * @param xPath The xPath expression.
     * @return The child of the node selected by the xPath.
     * @throws TransformerException If anything fails.
     */
    protected static Node selectSingleNode(final Node node, final String xpathExpression) throws TransformerException
    {
    	XPathFactory factory = XPathFactory.newInstance();
    	XPath xPath = factory.newXPath();
    	try
    	{
    		return (Node)xPath.evaluate(xpathExpression, node, XPathConstants.NODE);
    	}
    	catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Return the list of children of the node selected by the xPath.
     * 
     * @param node The node.
     * @param xPath The xPath expression.
     * @return The list of children of the node selected by the xPath.
     * @throws TransformerException If anything fails.
     */
    protected static NodeList selectNodeList(final Node node, final String xpathExpression) throws TransformerException
    {
    	XPathFactory factory = XPathFactory.newInstance();
    	XPath xPath = factory.newXPath();
    	try
    	{
    		return (NodeList)xPath.evaluate(xpathExpression, node, XPathConstants.NODESET);
    	}
    	catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Logs in the user test_dep_scientist and returns the corresponding user handle.
     * 
     * @return A handle for the logged in user.
     * @throws ServiceException
     * @throws HttpException
     * @throws IOException
     * @throws URISyntaxException 
     */
    protected String loginScientist() throws ServiceException, HttpException, IOException, URISyntaxException
    {
    	return AdminHelper.loginUser(PropertyReader.getProperty(PROPERTY_USERNAME_SCIENTIST), PropertyReader.getProperty(PROPERTY_PASSWORD_SCIENTIST));
    }

    /**
     * Logs in the user test_dep_lib and returns the corresponding user handle.
     * 
     * @return A handle for the logged in user.
     * @throws ServiceException
     * @throws HttpException
     * @throws IOException
     * @throws URISyntaxException 
     */
    protected String loginLibrarian() throws ServiceException, HttpException, IOException, URISyntaxException
    {
    	return AdminHelper.loginUser(PropertyReader.getProperty(PROPERTY_USERNAME_LIBRARIAN), PropertyReader.getProperty(PROPERTY_PASSWORD_LIBRARIAN));
    }

    /**
     * Logs in the user roland who is a system administrator and returns the corresponding user handle.
     * 
     * @return A handle for the logged in user.
     * @throws Exception
     */
    protected String loginSystemAdministrator() throws Exception
    {
    	return AdminHelper.loginUser(PropertyReader.getProperty(PROPERTY_USERNAME_ADMIN), PropertyReader.getProperty(PROPERTY_PASSWORD_ADMIN));
    }

    /**
     * Logs in the user test_author who is nothing but an author and returns the corresponding user handle.
     * 
     * @return A handle for the logged in user.
     * @throws Exception
     */
    protected String loginAuthor() throws Exception
    {
    	return AdminHelper.loginUser(PropertyReader.getProperty(PROPERTY_USERNAME_AUTHOR), PropertyReader.getProperty(PROPERTY_PASSWORD_AUTHOR));
    }

    /**
     * Logs out the user with the given userhandle from the system.
     * 
     * @return userHandle The handle for the logged in user.
     * @throws WebserverSystemException
     * @throws SqlDatabaseSystemException
     * @throws AuthenticationException
     * @throws RemoteException
     * @throws ServiceException
     * @throws URISyntaxException 
     */
    protected void logout(String userHandle) throws WebserverSystemException, SqlDatabaseSystemException, AuthenticationException, RemoteException, ServiceException, URISyntaxException
    {
        ServiceLocator.getUserManagementWrapper(userHandle).logout();
    }
    

    /**
     * Logs in as the default user before each test case.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        filterMap.clear();
    	userHandle = AdminHelper.loginUser(PropertyReader.getProperty(PROPERTY_USERNAME_SCIENTIST), PropertyReader.getProperty(PROPERTY_PASSWORD_SCIENTIST));
    }

    /**
     * Logs the user out after each test case.
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        logout(userHandle);
        userHandle = null;
    }
}
