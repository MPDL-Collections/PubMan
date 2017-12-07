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

package de.mpg.escidoc.services.search.bean;

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import gov.loc.www.zing.srw.service.SRWPort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.namespace.QName;

import net.sf.jasperreports.engine.JRException;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.logging.LogMethodDurationInterceptor;
import de.mpg.escidoc.services.common.logging.LogStartEndInterceptor;
import de.mpg.escidoc.services.common.valueobjects.AffiliationResultVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO.FormatType;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.parser.ParseException;
import de.mpg.escidoc.services.search.query.ExportSearchQuery;
import de.mpg.escidoc.services.search.query.ExportSearchResult;
import de.mpg.escidoc.services.search.query.OrgUnitsSearchResult;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.SearchQuery;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportHandler;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportManagerException;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportXSLTNotFoundException;

/**
 * {@inheritDoc}
 * 
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors(
{ LogStartEndInterceptor.class, LogMethodDurationInterceptor.class })
public class SearchBean implements Search
{

    /** Logging instance. */
    private Logger logger = null;

    /**
     * A XmlTransforming instance.
     */
    @EJB
    private XmlTransforming xmlTransforming;

    /**
     * A CitationStyleHandler instance.
     */
    @EJB
    private CitationStyleHandler citationStyleHandler;

    /**
     * A EndnodeExportHandler instance.
     */
    @EJB
    private StructuredExportHandler structuredExportHandler;

    /** Standard constructor. */
    public SearchBean()
    {
        this.logger = Logger.getLogger(getClass());
    }

    /** Coreservice identifier for the 'all' lucene index database. */
    public static final String INDEXDATABASE_ALL = "escidoc_all";
    /** Coreservice identifier for the 'item_container_admin' lucene index database. */
    public static final String INDEXDATABASE_ADMIN = "item_container_admin";
    /** Coreservice identifier for the 'german' lucene index database. */
    public static final String INDEXDATABASE_EN = "escidoc_en";
    /** Coreservice identifier for the 'english' lucene index database. */
    public static final String INDEXDATABASE_DE = "escidoc_de";
    /** Coreservice identifier for the organizational unit index database. */
    public static final String INDEXDATABASE_OU = "escidocou_all";

    /** Version of the cql search request. */
    private static final String SEARCHREQUEST_VERSION = "1.1";
    /** Packing of result. */
    private static final String RECORD_PACKING = "xml";
    /**
     * Diagnostic startrecord error from the SRW interface which should not be
     * treated as an error.
     */
    private static final String SRW_STARTRECORD_NO_ERROR = "61/StartRecord > endRecord";
    /**
     * Diagnostic sort type error from the SRW interface which should not be
     * treated as an error.
     */
    private static final String SRW_SORT_NO_ERROR = "cannot determine sort type";

    /**
     * {@inheritDoc}
     */
    public ItemContainerSearchResult searchForItemContainer(SearchQuery query) throws Exception
    {

        try
        {
            // call framework Search service
            String cqlQuery = query.getCqlQuery();

            SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
            searchRetrieveRequest.setVersion(SEARCHREQUEST_VERSION);
            searchRetrieveRequest.setQuery(cqlQuery);
            searchRetrieveRequest.setSortKeys(query.getCqlSortingQuery());
            searchRetrieveRequest.setMaximumRecords(query.getMaximumRecords());
            searchRetrieveRequest.setStartRecord(query.getStartRecord());
            searchRetrieveRequest.setRecordPacking(RECORD_PACKING);
            SearchRetrieveResponseType searchResult = performSearch(searchRetrieveRequest, INDEXDATABASE_ALL, null);
            List<SearchResultElement> resultList = transformToSearchResultList(searchResult);
            ItemContainerSearchResult result = new ItemContainerSearchResult(resultList, cqlQuery, searchResult
                    .getNumberOfRecords());
            return result;
        }
        catch (Exception e)
        {
            throw new TechnicalException(e);
        }
    }
    
    public ItemContainerSearchResult searchForItemContainerAdmin(SearchQuery query, String userHandle) throws Exception
    {

        try
        {
            // call framework Search service
            String cqlQuery = query.getCqlQuery();

            SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
            searchRetrieveRequest.setVersion(SEARCHREQUEST_VERSION);
            searchRetrieveRequest.setQuery(cqlQuery);
            searchRetrieveRequest.setSortKeys(query.getCqlSortingQuery());
            searchRetrieveRequest.setMaximumRecords(query.getMaximumRecords());
            searchRetrieveRequest.setStartRecord(query.getStartRecord());
            searchRetrieveRequest.setRecordPacking(RECORD_PACKING);
            SearchRetrieveResponseType searchResult = performSearch(searchRetrieveRequest, INDEXDATABASE_ADMIN, userHandle);
            List<SearchResultElement> resultList = transformToSearchResultList(searchResult);
            ItemContainerSearchResult result = new ItemContainerSearchResult(resultList, cqlQuery, searchResult
                    .getNumberOfRecords());
            return result;
        }
        catch (Exception e)
        {
            throw new TechnicalException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public OrgUnitsSearchResult searchForOrganizationalUnits(SearchQuery query) throws Exception
    {
        try
        {
            // call framework Search service
            String cqlQuery = query.getCqlQuery();

            SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
            searchRetrieveRequest.setVersion(SEARCHREQUEST_VERSION);
            searchRetrieveRequest.setQuery(cqlQuery);
            searchRetrieveRequest.setSortKeys(query.getCqlSortingQuery());

            searchRetrieveRequest.setMaximumRecords(query.getMaximumRecords());
            searchRetrieveRequest.setStartRecord(query.getStartRecord());
            searchRetrieveRequest.setRecordPacking(RECORD_PACKING);

            SearchRetrieveResponseType searchResult = performSearch(searchRetrieveRequest, INDEXDATABASE_OU, null);
            List<AffiliationVO> resultList = transformToAffiliationList(searchResult);
            OrgUnitsSearchResult result = new OrgUnitsSearchResult(resultList, cqlQuery, searchResult
                    .getNumberOfRecords());
            return result;
        } 
        catch (ParseException f)
        {
            throw new ParseException();
        } 
        catch (Exception e)
        {
            throw new TechnicalException(e);
        }
    }

    /**
     * Perform a search with the SRU interface.
     * 
     * @param searchRetrieveRequest
     *            SRU search request
     * @param sel
     *            index database selector
     * @return search result set
     * @throws TechnicalException
     *             if the search fails
     */
    private SearchRetrieveResponseType performSearch(SearchRetrieveRequestType searchRetrieveRequest, String index, String userHandle)
        throws TechnicalException
    {

        SearchRetrieveResponseType searchResult = null;

        try
        {
        	SRWPort searchHandler;
        	if(userHandle == null)
        	{
        		searchHandler = ServiceLocator.getSearchHandler(index);
        	}
        	else
        	{
        		searchHandler = ServiceLocator.getSearchHandler(index, userHandle);
        	}
        	
            logger.info("Cql search string: <" + searchRetrieveRequest.getQuery() + ">");
            logger.info("Cql sorting key(s): <" + searchRetrieveRequest.getSortKeys() + ">");
            logger.info("Cql start record: <" + searchRetrieveRequest.getStartRecord() + ">");
            searchResult = searchHandler.searchRetrieveOperation(searchRetrieveRequest);
            logger.debug("Search result: " + searchResult.getNumberOfRecords() + " item(s) or container(s)");
        } 
        catch (Exception e)
        {
            throw new TechnicalException(e);
        }

        // look for errors
        if (searchResult.getDiagnostics() != null)
        {
            // something went wrong
            for (DiagnosticType diagnostic : searchResult.getDiagnostics().getDiagnostic())
            {
                if ((diagnostic.getDetails().contains(SRW_STARTRECORD_NO_ERROR))
                        || (diagnostic.getDetails().contains(SRW_SORT_NO_ERROR)))
                {
                    logger.info("SRW interface returns an error, this one is safe to ignore: "
                            + diagnostic.getDetails());
                } 
                else
                {
                    logger.warn(diagnostic.getUri());
                    logger.warn(diagnostic.getMessage());
                    logger.warn(diagnostic.getDetails());
                    //throw new TechnicalException("Search request failed for query: " 
                    //        + searchRetrieveRequest.getQuery());
                }
            }
        }

        return searchResult;
    }

    /**
     * Transform the search result set into ItemContainerSearchResultVOs.
     * 
     * @param searchResult
     *            search result retrieved from the SRU interface
     * @return list of ItemContainerSearchResultVOs
     * @throws TechnicalException
     *             if transforming fails
     */
    private List<SearchResultElement> transformToSearchResultList(
            SearchRetrieveResponseType searchResult) throws TechnicalException
    {

        ArrayList<SearchResultElement> resultList = new ArrayList<SearchResultElement>();
        if (searchResult.getRecords() != null)
        {
            for (RecordType record : searchResult.getRecords().getRecord())
            {
                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {
                    String searchResultItem = null;
                    try
                    {
                        searchResultItem = messages[0].getAsString();
                    }
                    catch (Exception e) 
                    {
                        throw new TechnicalException("Error getting search result message.", e);
                    }
                    logger.debug("Search result: " + searchResultItem);
                    SearchResultElement itemResult = xmlTransforming.transformToSearchResult(searchResultItem);
                    resultList.add(itemResult);
                }
            }
        }
        return resultList;
    }

    /**
     * {@inheritDoc}
     */
    public ExportSearchResult searchAndExportItems(ExportSearchQuery query) throws Exception
    {

        String cqlQuery = query.getCqlQuery();
        // call framework Search service
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion(SEARCHREQUEST_VERSION);
        searchRetrieveRequest.setQuery(query.getCqlQuery());
        searchRetrieveRequest.setSortKeys(query.getCqlSortingQuery());

        searchRetrieveRequest.setMaximumRecords(query.getMaximumRecords());
        searchRetrieveRequest.setStartRecord(query.getStartRecord());

        searchRetrieveRequest.setRecordPacking(RECORD_PACKING);

        SearchRetrieveResponseType searchResult = null;

        if (query.getIndexSelector() == null)
        {
            searchResult = performSearch(searchRetrieveRequest, INDEXDATABASE_ALL, null);
        } 
        else
        {
            searchResult = performSearch(searchRetrieveRequest, query.getIndexSelector(), null);
        }

        List<SearchResultElement> searchElements = transformToSearchResultList(searchResult);
        ExportSearchResult exportedResult = new ExportSearchResult(searchElements, cqlQuery,
                searchResult.getNumberOfRecords());
        String itemListAsString = xmlTransforming.transformToItemList(transformtToItemVOListWrapper(exportedResult));

        String outputFormat = query.getOutputFormat();
        String exportFormat = query.getExportFormat();
        String cslConeId = query.getCslConeId();

        if (!checkValue(exportFormat))
        {
            throw new TechnicalException("exportFormat is empty");
        }
        if (!checkValue(itemListAsString))
        {
            throw new TechnicalException("itemList is empty");
        }

        byte[] exportData = null;

        // structured export
        if (structuredExportHandler.isStructuredFormat(exportFormat))
        {
            exportData = getOutput(itemListAsString, new ExportFormatVO(FormatType.STRUCTURED, exportFormat, null));
            exportedResult.setExportedResults(exportData);
            return exportedResult;
        }
        // citation style
        else if (citationStyleHandler.isCitationStyle(exportFormat))
        {
            if (!checkValue(outputFormat))
            {
                throw new TechnicalException("outputFormat should be not empty for exportFormat:" + exportFormat);
            }
            outputFormat = outputFormat.trim();
            if (citationStyleHandler.getMimeType(exportFormat, outputFormat) == null)
            {
                throw new TechnicalException("file output format: " + outputFormat + " for export format: "
                        + exportFormat + " is not supported");
            }
            exportData = getOutput(itemListAsString, new ExportFormatVO(FormatType.LAYOUT, exportFormat, outputFormat, cslConeId));
            exportedResult.setExportedResults(exportData);
            return exportedResult;
        } 
        else
        {
            // no export format found!!!
            throw new TechnicalException("Export format: " + exportFormat + " is not supported");
        }
    }

    /**
     * Queries an export service to get the search result in an binary format.
     * 
     * @param exportFormat
     *            export format to transform to
     * @param formatType
     *            format type to transform to
     * @param outputFormat
     *            output format to transform to
     * @param itemList
     *            the list of items to be transformed
     * @return a binary stream which contains the items in a given format (pdf,
     *         etc.)
     * @throws TechnicalException
     * @throws StructuredExportXSLTNotFoundException
     *             if the corresponding xslt is not found
     * @throws StructuredExportManagerException
     *             if structured exportmanager reports an error
     * @throws IOException
     *             if an io error occurs
     * @throws JRException
     *             if a jr error occurs
     * @throws CitationStyleManagerException
     *             if the citationstyle manager reports an error
     */
    private byte[] getOutput(String itemList, ExportFormatVO exportFormat)
        throws TechnicalException, StructuredExportXSLTNotFoundException, StructuredExportManagerException,
        IOException, JRException, CitationStyleManagerException
    {

        byte[] exportData = null;

        // structured export
        if (exportFormat.getFormatType() == FormatType.LAYOUT)
        {
            logger.debug("Calling citationStyleHandler");

            exportData = citationStyleHandler.getOutput(itemList, exportFormat);
            logger.debug("Returning from citationStyleHandler");
        } 
        else if (exportFormat.getFormatType() == FormatType.STRUCTURED)
        {
            logger.debug("Calling structuredExportHandler");
            exportData = structuredExportHandler.getOutput(itemList, exportFormat.getName());
            logger.debug("Returning from structuredExportHandler");
        } 
        else
        {
            // no export format found!!!
            throw new TechnicalException("format Type: " + exportFormat.getFormatType() + " is not supported");
        }

        return exportData;
    }

    /**
     * Transforms the search result set to a xml string.
     * 
     * @param searchResult
     *            search result retrieved from the SRU interface
     * @return search result set as string
     * @throws Exception
     */
    private String transformToItemListAsString(SearchRetrieveResponseType searchResult) throws Exception
    {
        ArrayList<ItemVO> resultList = new ArrayList<ItemVO>();
        if (searchResult.getRecords() != null)
        {
            for (RecordType record : searchResult.getRecords().getRecord())
            {
                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {   
                    String searchResultItem = messages[0].getAsString();                  
                    ItemVO itemResult = xmlTransforming.transformToItem(searchResultItem);
                    resultList.add(itemResult); 
                }
            }
        }
        String itemStringList = xmlTransforming.transformToItemList(resultList);
        return itemStringList;
    }

    private List<AffiliationVO> transformToAffiliationList(SearchRetrieveResponseType searchResult) throws Exception
    {
        ArrayList<AffiliationVO> resultList = new ArrayList<AffiliationVO>();
        if (searchResult.getRecords() != null)
        {
            for (RecordType record : searchResult.getRecords().getRecord())
            {
                StringOrXmlFragment data = record.getRecordData();
                MessageElement[] messages = data.get_any();
                // Data is in the first record
                if (messages.length == 1)
                {
                    String searchResultItem = messages[0].getAsString();
                    logger.debug("Search result: " + searchResultItem);
                    SearchResultElement searchResultElement = xmlTransforming.transformToSearchResult(searchResultItem);
                    AffiliationResultVO affiliationResultVO = (AffiliationResultVO)searchResultElement;
                    resultList.add((AffiliationVO)affiliationResultVO);

                }
            }
        }
        return resultList;
    }
    
    private ItemVOListWrapper transformtToItemVOListWrapper (ExportSearchResult exportSearchResult)
    {
        logger.debug("transformToItemList(List<ItemVO>)");
        if (exportSearchResult == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":transformtToItemVOListWrapper is null");
        }
        // wrap the exportSearchResult list into the according wrapper class
        ItemVOListWrapper listWrapper = new ItemVOListWrapper();
        listWrapper.setItemVOList(exportSearchResult.extractItemsOfSearchResult());
        if (exportSearchResult.getTotalNumberOfResults() != null)
        {
            listWrapper.setNumberOfRecords(exportSearchResult.getTotalNumberOfResults().toString());
        }
        return listWrapper;
    }

    private boolean checkValue(String str)
    {
        return !(str == null || str.trim().equals(""));
    }
}
