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
package de.mpg.escidoc.pubman;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.affiliation.AffiliationBean;
import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;
import de.mpg.escidoc.pubman.browseBy.BrowseBySessionBean;
import de.mpg.escidoc.pubman.util.LinkVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;

/**
 * Backing Bean for Browse By
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class BrowseByPage extends BreadcrumbPage
{
    private static Logger logger = Logger.getLogger(BrowseByPage.class);
    public static final String BEAN_NAME = "BrowseByPage";
    private final String persSearchIndex = MetadataSearchCriterion.getINDEX_PERSON_IDENTIFIER();
    private final String subSearchIndex = MetadataSearchCriterion.getINDEX_TOPIC();
    private final String pubYearSearchIndex = MetadataSearchCriterion.getINDEX_DATE_ISSUED();
    private final String pubOnlineYearSearchIndex = MetadataSearchCriterion.getINDEX_DATE_PUBLISHED_ONLINE();
    private final String anyYearSearchIndex = MetadataSearchCriterion.getINDEX_DATE_ANY();
    private final String queryPerson = "foaf:family_name";
    private final String queryDdc = "dc:title";
    private BrowseBySessionBean bbBean;
    private String currentCharacter = "A";
    private List<String> creators;
    private List<String> subjects;
    private SelectItem[] dateOptions;

    /**
     * Public constructor
     */
    public BrowseByPage()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page is navigated to, either directly via a URL, or indirectly via page
     * navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        this.bbBean = (BrowseBySessionBean)getSessionBean(BrowseBySessionBean.class);
        this.creators = new ArrayList<String>();
        this.subjects = new ArrayList<String>();
        if ("year".equals(getSelectedValue()))
        {
            loadBrowseByYear();
        }
    }

    /**
     * Perfom search for browse by values.
     * 
     * @return navigation string for page reload
     */
    public String startCharacterSearch(String selChar)
    {
    	System.out.println("Start char search");
        String curChar = selChar;
        List<LinkVO> links = this.callCone(this.bbBean.getSelectedValue(), curChar);
        this.bbBean.setCurrentCharacter(curChar);
        this.bbBean.setSearchResults(links);
        return "loadBrowseByPage";
    }

    /**
     * Call the cone service to retrive the browse by values.
     * 
     * @param type, type of the cone request (persons, subjects, journals)
     * @param startChar (the character with which the value has to start)
     * @return
     */
    private List<LinkVO> callCone(String type, String startChar)
    {
        List<LinkVO> links = new ArrayList<LinkVO>();
        try
        {
            String localLang = Locale.getDefault().getLanguage();
            // if (!(localLang.equals("en") || localLang.equals("de") || localLang.equals("ja") ||
            // localLang.equals("es")))
            if (!(localLang.equals("en") || localLang.equals("de") || localLang.equals("ja")))
            {
                localLang = "en";
            }
            URL coneUrl = new URL(PropertyReader.getProperty("escidoc.cone.service.url") + type + "/query?f=options&"
                    + this.bbBean.getQuery() + "=\"" + URLEncoder.encode(startChar, "UTF-8") + "*\"&n=0&lang=en");
            URLConnection conn = coneUrl.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            int responseCode = httpConn.getResponseCode();
            switch (responseCode)
            {
                case 200:
                    logger.debug("Cone Service responded with 200.");
                    break;
                default:
                    throw new RuntimeException("An error occurred while calling Cone Service: " + responseCode + ": "
                            + httpConn.getResponseMessage());
            }
            InputStreamReader isReader = new InputStreamReader(coneUrl.openStream(), "UTF-8");
            BufferedReader bReader = new BufferedReader(isReader);
            String line = "";
            while ((line = bReader.readLine()) != null)
            {
                String[] parts = line.split("\\|");
                if (parts.length == 2)
                {
                    LinkVO link = new LinkVO(parts[1], parts[0]);
                    if ((links.isEmpty() || !link.equals(links.get(links.size() - 1))))
                    {
                        links.add(link);
                    }
                }
            }
            isReader.close();
            httpConn.disconnect();
        }
        catch (Exception e)
        {
            this.logger.warn("An error occurred while calling the Cone service.", e);
            return null;
        }
        return links;
    }

    public String getSearchUrl()
    {
        try
        {
            String instanceUrl = PropertyReader.getProperty("escidoc.pubman.instance.url");
            String searchPath = "/pubman/faces/SearchResultListPage.jsp?";
            return instanceUrl + searchPath;
        }
        catch (Exception e)
        {
            logger.warn("Could not read property: 'escidoc.pubman.instance.url'", e);
            return "";
        }
    }

    /**
     * loads the browse by page.
     * 
     * @return String navigation string (JSF navigation) to load the browse by page.
     */
    public String loadBrowseBy()
    {
        this.bbBean.clear();
        return "loadBrowseBy";
    }

    /**
     * loads the affiliation tree page.
     * 
     * @return String navigation string (JSF navigation) to load the affiliation tree page.
     */
    public String loadAffiliationTree()
    {
        this.setSelectedValue("org");
        ((AffiliationBean)getSessionBean(AffiliationBean.class)).setSource("BrowseBy");
        return "loadAffiliationTree";
    }

    /**
     * loads the browse by creator page.
     * 
     * @return String navigation string (JSF navigation) to load the browse by creator page.
     */
    public String loadBrowseByCreator()
    {
        this.setSelectedValue("persons");
        this.setSearchIndex(this.persSearchIndex);
        if (this.bbBean.getSearchResults() != null)
        {
            this.bbBean.getSearchResults().clear();
        }
        this.bbBean.setCurrentCharacter("");
        this.bbBean.setShowChars();
        this.bbBean.setQuery(this.queryPerson);
        return "loadBrowseByPage";
    }

    /**
     * loads the browse by subject page.
     * 
     * @return String navigation string (JSF navigation) to load the browse by subject page.
     */
    public String loadBrowseBySubject(String selSubject)
    {
       
        String curSubject = selSubject;
        this.setSelectedValue(curSubject);
        this.setSearchIndex(this.subSearchIndex);
        if (this.bbBean.getSearchResults() != null)
        {
            this.bbBean.getSearchResults().clear();
        }
        this.bbBean.setCurrentCharacter("");
        this.bbBean.setShowChars();
        this.bbBean.setQuery(this.queryDdc);
        return "loadBrowseByPage";
    }

    /**
     * loads the browse by year.
     * 
     * @return String navigation string (JSF navigation) to load the browse by year.
     */
    public String loadBrowseByYear()
    {
        this.setSelectedValue("year");
        if (this.bbBean.getSearchResults() != null)
        {
            this.bbBean.getSearchResults().clear();
        }
        this.bbBean.setCurrentCharacter("");
        this.bbBean.setShowChars();
        this.bbBean.setYearPublished();
        this.bbBean.setBrowseByYears(this.bbBean.getYearRange());
        if ("any".equals(this.bbBean.getDateMode()))
        {
            this.bbBean.setYearStartAny();
            this.bbBean.setDateType("any");
        }
        else
        {
            this.bbBean.setYearPublished();
            this.bbBean.setDateType("published");
        }
        this.bbBean.setBrowseByYears(this.bbBean.getYearRange());
        return "loadBrowseByPage";
    }

    public SelectItem[] getDateOptions()
    {
        dateOptions = new SelectItem[] { new SelectItem("published", getLabel("dateOptionPublished")),
                new SelectItem("any", getLabel("dateOptionAny")) };
        return dateOptions;
    }

    public String getSelectedValue()
    {
        return this.bbBean.getSelectedValue();
    }

    public void setSelectedValue(String selectedValue)
    {
        this.bbBean.setSelectedValue(selectedValue);
    }

    public String getSearchIndex()
    {
        return this.bbBean.getSearchIndex();
    }

    public void setSearchIndex(String index)
    {
        this.bbBean.setSearchIndex(index);
    }

    @Override
    public boolean isItemSpecific()
    {
        return false;
    }


    public String getCurrentCharacter()
    {
        return currentCharacter;
    }

    public void setCurrentCharacter(String currentCharacter)
    {
        this.currentCharacter = currentCharacter;
    }

    public List<String> getCreators()
    {
        return creators;
    }

    public void setCreators(List<String> creators)
    {
        this.creators = creators;
    }

    public List<String> getSubjects()
    {
        return subjects;
    }

    public void setSubjects(List<String> subjects)
    {
        this.subjects = subjects;
    }


    public String getPortfolioLink()
    {
        try
        {
            String link = PropertyReader.getProperty("escidoc.cone.service.url") + "persons/resource/";
            return link;
        }
        catch (Exception e)
        {
            this.logger.error("Could not read Property: 'escidoc.cone.service.url'", e);
        }
        return "";
    }

    public String getConeUrl()
    {
        try
        {
            String link = PropertyReader.getProperty("escidoc.cone.service.url");
            return link;
        }
        catch (Exception e)
        {
            this.logger.error("Could not read Property: 'escidoc.cone.service.url'", e);
        }
        return "";
    }

    public String getPubYearSearchIndex()
    {
        return pubYearSearchIndex;
    }

    public String getPubOnlineYearSearchIndex()
    {
        return pubOnlineYearSearchIndex;
    }

    public String getAnyYearSearchIndex()
    {
        return anyYearSearchIndex;
    }
}