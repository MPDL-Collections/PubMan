package de.mpg.escidoc.services.dataacquisition.webservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.AccessException;
import java.util.Vector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import noNamespace.FormatType;
import noNamespace.FormatsDocument;
import noNamespace.FormatsType;
import noNamespace.SourceType;
import noNamespace.SourcesDocument;
import noNamespace.SourcesType;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.purl.dc.elements.x11.SimpleLiteral;

import de.mpg.escidoc.services.dataacquisition.DataHandlerBean;
import de.mpg.escidoc.services.dataacquisition.DataSourceHandlerBean;
import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotAvailableException;
import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotRecognisedException;
import de.mpg.escidoc.services.dataacquisition.exceptions.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.dataacquisition.exceptions.SourceNotAvailableException;
import de.mpg.escidoc.services.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.FullTextVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.MetadataVO;

/**
 * This class provides the implementation of the {@link Unapi} interface.
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public class UnapiServlet extends HttpServlet implements Unapi
{
    private static final long serialVersionUID = 1L;
    private final String idTypeUri = "URI";
    private final String idTypeUrl = "URL";
    private final String idTypeEscidoc = "ESCIDOC";
    private final String idTypeUnknown = "UNKNOWN";
    private final Logger logger = Logger.getLogger(UnapiServlet.class);
    private DataHandlerBean importHandler = new DataHandlerBean();
    private DataSourceHandlerBean sourceHandler = new DataSourceHandlerBean();
    private boolean view = false; // default option is download
    private String filename = "unapi";

    /**
     * Http get method for unAPI interface.
     * @param request
     * @param response
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        this.doPost(request, response);
    }

    /**
     * Http post method for unAPI interface.
     * @param request
     * @param response
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            String identifier = null;
            String format = null;
            OutputStream outStream = response.getOutputStream();
            // Retrieve the command from the location path
            String command = request.getPathInfo();
            if (command != null && command.length() > 0)
            {
                command = command.substring(1);
            }
            // Response will be viewed (not attachement)
            if (request.getRequestURL().toString().contains("view"))
            {
                this.view = true;
            }
            // Handle Call
            if ("unapi".equals(command))
            {
                identifier = request.getParameter("id");
                format = request.getParameter("format");
                if (identifier == null)
                {
                    // Gives back a description of escidoc formats as default
                    response.setStatus(200);
                    response.setContentType("application/xml");
                    outStream.write(this.unapi(this.idTypeEscidoc, false));
                }
                else
                {
                    if (format == null)
                    {
                        // Gives back a description of all available formats for a source
                        byte[] xml = this.unapi(identifier, true);
                        if (xml != null)
                        {
                            response.setStatus(200);
                            response.setContentType("application/xml");
                            outStream.write(xml);
                        }
                        else
                        {
                            response.sendError(404, "Identifier not recognized");
                        }
                    }
                    else
                    { // Fetch data
                        try
                        {
                            byte[] data = this.unapi(identifier, format);
                            if (data == null)
                            {
                                response.sendError(404, "Identifier not recognized");
                            }
                            else
                            {
                                response.setContentType(this.importHandler.getContentType());
                                if (!this.view)
                                {
                                    response.setHeader("Content-disposition", "attachment; filename=" + this.filename
                                            + this.importHandler.getFileEnding());
                                }
                                response.setStatus(200);
                                outStream.write(data);
                            }
                        }
                        catch (IdentifierNotRecognisedException e)
                        {
                            this.resetValues();
                            this.logger.error("Item with identifier " + identifier + " was not found.", e);
                            response.sendError(404, "Identifier not recognized");
                        }
                        catch (SourceNotAvailableException e)
                        {
                            this.resetValues();
                            this.logger.error("Import Source not available.", e);
                            response.sendError(404, "Source not available");
                        }
                        catch (RuntimeException e)
                        {
                            this.resetValues();
                            this.logger.error("Technical problems occurred when communicating with import source.", e);
                            response.sendError(404, "Technical problems occurred");
                        }
                        catch (FormatNotRecognisedException e)
                        {
                            this.resetValues();
                            this.logger.error("Format " + format + " was not recognised.", e);
                            response.sendError(406, "Format not recognized");
                        }
                        catch (FormatNotAvailableException e)
                        {
                            this.resetValues();
                            response.sendError(403, "Format " + e.getMessage() + "was not available on import source.");
                        }
                        catch (AccessException e)
                        {
                            this.resetValues();
                            response.sendError(403, "Access denied for " + identifier);
                        }
                    }
                }
            }
            else
            {
                // Gives back a description of all available sources
                response.setStatus(200);
                response.setContentType("application/xml");
                outStream.write(this.unapi());
            }
            outStream.flush();
            outStream.close();
            this.resetValues();
        }
        catch (IOException e)
        {
            this.resetValues();
            this.logger.error("unAPI request could not be processed due to technical problems.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public byte[] unapi() throws RuntimeException
    {
        Vector<DataSourceVO> sources;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            sources = this.sourceHandler.getSources();
            SourcesDocument xmlSourceDoc = SourcesDocument.Factory.newInstance();
            SourcesType xmlSources = xmlSourceDoc.addNewSources();
            for (int i = 0; i < sources.size(); i++)
            {
                DataSourceVO source = sources.get(i);
                SourceType xmlSource = xmlSources.addNewSource();
                SimpleLiteral name = xmlSource.addNewName();
                XmlString sourceName = XmlString.Factory.newInstance();
                sourceName.setStringValue(source.getName());
                name.set(sourceName);
                SimpleLiteral desc = xmlSource.addNewDescription();
                XmlString sourceDesc = XmlString.Factory.newInstance();
                sourceDesc.setStringValue(source.getDescription());
                desc.set(sourceDesc);
                // SimpleLiteral disclaim = xmlSource.addNewDisclaimer();
                // XmlString sourceDisclaim = XmlString.Factory.newInstance();
                // sourceDisclaim.setStringValue("Disclaimer will follow");
                // disclaim.set(sourceDisclaim);
            }
            XmlOptions xOpts = new XmlOptions();
            xOpts.setSavePrettyPrint();
            xOpts.setSavePrettyPrintIndent(4);
            xOpts.setUseDefaultNamespace();
            xmlSourceDoc.save(baos, xOpts);
        }
        catch (IOException e)
        {
            this.logger.info("Error when creating outputXml.", e);
            throw new RuntimeException();
        }
        return baos.toByteArray();
    }

    /**
     * {@inheritDoc} if unapi interface is called with no identifier, the identifier is set to escidoc as default,
     * showing escidoc formats to fetch Only when not the default identifier is set, the identifier is displayed in the
     * formats xml.
     */
    public byte[] unapi(String identifier, boolean show)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Vector<FullTextVO> fullTextV = new Vector<FullTextVO>();
        Vector<MetadataVO> metadataV = new Vector<MetadataVO>();
        String[] tmp = identifier.split(":");
        DataSourceVO source = this.sourceHandler.getSourceByIdentifier(tmp[0]);
        // No source for this identifier
        if (source == null)
        {
            return null;
        }
        FormatsDocument xmlFormatsDoc = FormatsDocument.Factory.newInstance();
        FormatsType xmlFormats = xmlFormatsDoc.addNewFormats();
        if (show)
        {
            xmlFormats.setId(identifier);
        }
        fullTextV = source.getFtFormats();
        metadataV = source.getMdFormats();
        // Metadata formats
        for (int i = 0; i < metadataV.size(); i++)
        {
            MetadataVO md = metadataV.get(i);
            FormatType xmlFormat = xmlFormats.addNewFormat();
            xmlFormat.setName(md.getMdLabel().toLowerCase());
            xmlFormat.setType(md.getMdFormat());
            if (md.getMdDesc() != null)
            {
                xmlFormat.setDocs(md.getMdDesc());
            }
        }
        // File formats
        for (int i = 0; i < fullTextV.size(); i++)
        {
            FullTextVO ft = fullTextV.get(i);
            FormatType xmlFormat = xmlFormats.addNewFormat();
            xmlFormat.setName(ft.getFtLabel());
            xmlFormat.setType(ft.getFtFormat());
        }
        try
        {
            XmlOptions xOpts = new XmlOptions();
            xOpts.setSavePrettyPrint();
            xOpts.setSavePrettyPrintIndent(4);
            xmlFormatsDoc.save(baos, xOpts);
        }
        catch (IOException e)
        {
            this.logger.info("Error when creating outputXml.", e);
            throw new RuntimeException();
        }
        return baos.toByteArray();
        // TODO
        // Get additional formats provided by internal transformations
    }

    /**
     * {@inheritDoc}
     */
    public byte[] unapi(String identifier, String format) throws IdentifierNotRecognisedException,
            SourceNotAvailableException, FormatNotRecognisedException, RuntimeException, AccessException, FormatNotAvailableException
    {
        this.filename = identifier;
        String[] tmp = identifier.split(":");
        String sourceId = tmp[0];
        String id = tmp[1];
        String sourceName = this.sourceHandler.getSourceNameByIdentifier(sourceId);
        String idType = this.checkIdentifier(identifier, format);
        try
        {
            if (idType.equals(this.idTypeUri))
            {
                if (sourceName != null)
                {
                    return this.importHandler.doFetch(sourceName, id, format);
                }
            }
            if (idType.equals(this.idTypeUrl))
            {
                return this.importHandler.fetchMetadatafromURL(new URL(identifier));
            }
            if (idType.equals(this.idTypeEscidoc))
            {
                id = this.setEsciDocIdentifier(identifier);
                sourceName = this.sourceHandler.getSourceNameByIdentifier("escidoc");
                this.filename = id;
                return this.importHandler.doFetch(sourceName, id, format);
            }
            if (idType.equals(this.idTypeUnknown) || sourceName == null)
            {
                this.logger.warn("The type of the identifier (" + identifier + ") was not recognised.");
                throw new IdentifierNotRecognisedException();
            }
        }
        catch (AccessException e)
        {
            throw new AccessException(identifier);
        }
        catch (IdentifierNotRecognisedException e)
        {
            throw new IdentifierNotRecognisedException();
        }
        catch (SourceNotAvailableException e)
        {
            throw new SourceNotAvailableException();
        }
        catch (FormatNotRecognisedException e)
        {
            throw new FormatNotRecognisedException();
        }
        catch (FormatNotAvailableException e)
        {
            throw new FormatNotAvailableException(e.getMessage());
        }
        catch (RuntimeException e)
        {
            throw new RuntimeException();
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException();
        }
        return null;
    }

    private String checkIdentifier(String identifier, String format)
    {
        identifier = identifier.toLowerCase().trim();
        if (identifier.contains("escidoc:"))
        {
            return this.idTypeEscidoc;
        }
        if (identifier.startsWith("http") & format.toLowerCase().equals("url"))
        {
            // Fetch from url => only download possible
            this.view = false;
            return this.idTypeUrl;
        }
        return this.idTypeUri;
    }

    /**
     * EsciDoc Identifier can consist of the citation URL, like.
     * http://pubman.mpdl.mpg.de:8080/pubman/item/escidoc:1048:3. This method extracts the identifier from the URL
     * @param identifier
     */
    private String setEsciDocIdentifier(String identifier)
    {
        String[] extracts = identifier.split("/");
        return extracts[extracts.length - 1];
    }

    private void resetValues()
    {
        this.importHandler.setContentType("");
        this.importHandler.setFileEnding("");
        this.filename = ("");
        this.view = false;
    }
}
