package de.mpg.escidoc.pubman.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.Internationalized;
import de.mpg.escidoc.pubman.viewItem.ViewItemCreatorOrganization;
import de.mpg.escidoc.pubman.viewItem.ViewItemOrganization;
import de.mpg.escidoc.pubman.viewItem.bean.SearchHitBean;
import de.mpg.escidoc.pubman.viewItem.ui.COinSUI;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO.SearchHitType;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Wrapper class for items to be used in the presentation.
 * @author franke
 * @author $Author: $
 * @version: $Revision: 1641 $ $LastChangedDate: 2007-12-04 16:52:04 +0100 (Di, 04 Dez 2007)$
 */
public class PubItemVOPresentation extends PubItemVO implements Internationalized {

	private boolean selected = false;
	private boolean shortView = true;
	
	
	/**
	 * True if the item is shown in the revisions list, additional information is displayed then (release date, description)
	 */
	private boolean isRevisionView = false;
	
	
	/**
	 * The content for the CoinS tag for each item
	 */
	private String coins;
	
	/**
     * The list of formatted organzations in an ArrayList.
     */
    private ArrayList<String> organizationArray;
    
    /**
     * The list of affiliated organizations as VO List.
     */
    private ArrayList<ViewItemOrganization> organizationList;
    
    /**
     * The list of affiliated organizations in a list.
     */
    private List<OrganizationVO> affiliatedOrganizationsList;
    
    /**
     * The list of formatted creators (persons) in an ArrayList.
     */
    private ArrayList<String> creatorArray;
    
    /**
     * The list of formatted creators which are organizations in an ArrayList.
     */
    private ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray;
    
    /**
     * The list of formatted creators (persons AND organizations) in an ArrayList.
     */
    private ArrayList<String> allCreatorsList = new ArrayList<String>();
    
    /**
     * the first source of the item (for display in the medium view)
     */
    private SourceVO firstSource = new SourceVO();
    
    /**
     * List of hits. Every hit in files contains the file reference and the text fragments of the search hit.
     */
    private java.util.List<SearchHitVO> searchHitList = new java.util.ArrayList<SearchHitVO>();
    
    /**
     * List of search hits wrapped in a display optimized bean
     */
    private List<SearchHitBean> searchHits = new ArrayList<SearchHitBean>();
    
    private boolean isSearchResult = false;
    
    //For handling the resource bundles (i18n)
    private Application application = FacesContext.getCurrentInstance().getApplication();
    //get the selected language...
    private InternationalizationHelper i18nHelper2 = (InternationalizationHelper)FacesContext
	    .getCurrentInstance()
	    .getExternalContext()
	    .getApplicationMap()
	    .get(InternationalizationHelper.BEAN_NAME);
    
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext
    	.getCurrentInstance()
    	.getApplication().getVariableResolver()
    	.resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
    
	public PubItemVOPresentation(PubItemVO item)
	{
		super(item);
		if (item instanceof PubItemResultVO)
		{
			this.searchHitList = ((PubItemResultVO)item).getSearchHitList();
			this.isSearchResult = true;
		}
		
		// set up some pre-requisites
		//the list of numbered affiliated organizations 
        createAffiliatedOrganizationList();
        
        // the list of creators (persons and organizations)
        createCreatorList();
        
        // get the first source of the item (if available)
        if(item.getMetadata().getSources() != null && item.getMetadata().getSources().size() > 0)
        {
        	this.firstSource = item.getMetadata().getSources().get(0);
        }
        
        getCountCreators();
        
        // get the information for the CoinS tag
        COinSUI coins = new COinSUI();
        this.coins = coins.getCOinSString(this);
        
        // get the search result hits
        if(this.searchHitList != null && this.searchHitList.size() > 0)
        {
        	String beforeSearchHitString;
        	String searchHitString;
        	String afterSearchHitString;
        	
        	// browse through the list of hits and set up the SearchHitBean list
            for(int i = 0; i < searchHitList.size(); i++)
            {
                if(searchHitList.get(i).getType() == SearchHitType.FULLTEXT)
                {    
                    if(searchHitList.get(i).getHitReference() != null)
                    {
                        for(int j = 0; j < searchHitList.get(i).getTextFragmentList().size(); j++)
                        {
                            int startPosition = 0;
                            int endPosition = 0;
                            
                            startPosition = searchHitList.get(i).getTextFragmentList().get(j).getHitwordList().get(0).getStartIndex();
                            endPosition = searchHitList.get(i).getTextFragmentList().get(j).getHitwordList().get(0).getEndIndex() + 1;
                            
                            beforeSearchHitString ="..." + searchHitList.get(i).getTextFragmentList().get(j).getData().substring(0, startPosition);
                            searchHitString = searchHitList.get(i).getTextFragmentList().get(j).getData().substring(startPosition, endPosition);
                            afterSearchHitString = searchHitList.get(i).getTextFragmentList().get(j).getData().substring(endPosition) + "...";
                            
                            this.searchHits.add(new SearchHitBean(beforeSearchHitString, searchHitString, afterSearchHitString));
                        }
                        
                    }
                    
                }
                
            }
        }
	}
	
	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean getShortView() {
		return shortView;
	}

	public void setShortView(boolean shortView) {
		this.shortView = shortView;
	}

	public boolean getMediumView() {
		return !shortView;
	}

	public void setMediumView(boolean mediumView) {
		this.shortView = !mediumView;
	}

    /**
     * Distinguish between Persons and organization as creators and returns them formatted as string.
     * @return String the  formatted creators
     */
    public String getCreators()
    {
        StringBuffer creators = new StringBuffer();

        if (getMetadata().getCreators() != null)
        {
            for (int i = 0; i < getMetadata().getCreators().size(); i++)
            {
                if (getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if (getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                    {
                        creators.append(getMetadata().getCreators().get(i).getPerson().getFamilyName());
                    }
                    if (getMetadata()
                            .getCreators()
                            .get(i)
                            .getPerson()
                            .getFamilyName() != null
                        && getMetadata()
                            .getCreators()
                            .get(i)
                            .getPerson()
                            .getGivenName() != null)
                    {
                        creators.append(", ");
                    }
                    if (getMetadata().getCreators().get(i).getPerson().getGivenName() != null)
                    {
                        creators.append(getMetadata().getCreators().get(i).getPerson().getGivenName());
                    }
                }
                else if (getMetadata().getCreators().get(i).getOrganization() != null)
                {
                    if (getMetadata().getCreators().get(i).getOrganization().getName().getValue() != null)
                    {
                        creators.append(
                                getMetadata().getCreators().get(i).getOrganization().getName().getValue());
                    }
                }
                if (i < getMetadata().getCreators().size() - 1)
                {
                    creators.append("; ");
                }
            }
        }
        return creators.toString();
    }
    
    /**
     * Returns the newest date of the metadata date section.
     * @return the latest date
     */
    public String getLatestDate()
    {
        if (getMetadata().getDatePublishedInPrint() != null && !"".equals(getMetadata().getDatePublishedInPrint()))
        {
        	return getMetadata().getDatePublishedInPrint() + ", " + getLabel("ViewItem_lblDatePublishedInPrint");
        }
        else if (getMetadata().getDatePublishedOnline() != null && !"".equals(getMetadata().getDatePublishedOnline()))
        {
        	return getMetadata().getDatePublishedOnline() + ", " + getLabel("ViewItem_lblDatePublishedOnline");
        }
        else if (getMetadata().getDateAccepted() != null && !"".equals(getMetadata().getDateAccepted()))
        {
        	return getMetadata().getDateAccepted() + ", " + getLabel("ViewItem_lblDateAccepted");
        }
        else if (getMetadata().getDateSubmitted() != null && !"".equals(getMetadata().getDateSubmitted()))
        {
        	return getMetadata().getDateSubmitted() + ", " + getLabel("ViewItem_lblDateSubmitted");
        }
        else if (getMetadata().getDateModified() != null && !"".equals(getMetadata().getDateModified()))
        {
        	return getMetadata().getDateModified() + ", " + getLabel("ViewItem_lblDateModified");
        }
        else if (getMetadata().getDateCreated() != null && !"".equals(getMetadata().getDateCreated()))
        {
        	return getMetadata().getDateCreated() + ", " + getLabel("ViewItem_lblDateCreated");
        }
        else
        {
        	return null;
        }
    }
    
    public String getDatesAsString() 
    {
        ArrayList<String> dates = new ArrayList<String>();
        
        if (getMetadata().getDateCreated()!=null && !getMetadata().getDateCreated().equals(""))
            dates.add(getLabel("ViewItem_lblDateCreated") + ": " + getMetadata().getDateCreated());
        
        if (getMetadata().getDateModified() != null && !getMetadata().getDateModified().equals(""))
            dates.add(getLabel("ViewItem_lblDateModified") + ": " + getMetadata().getDateModified());
  
        if (getMetadata().getDateSubmitted() != null && !getMetadata().getDateSubmitted().equals(""))
            dates.add(getLabel("ViewItem_lblDateSubmitted") + ": " + getMetadata().getDateSubmitted());
        
        if (getMetadata().getDateAccepted() != null && !getMetadata().getDateAccepted().equals(""))
            dates.add(getLabel("ViewItem_lblDateAccepted") + ": " + getMetadata().getDateAccepted());
        
        if (getMetadata().getDatePublishedOnline() != null && !getMetadata().getDatePublishedOnline().equals(""))
            dates.add(getLabel("ViewItem_lblDatePublishedOnline") + ": " + getMetadata().getDatePublishedOnline());
        
        if (getMetadata().getDatePublishedInPrint() != null && !getMetadata().getDatePublishedInPrint().equals(""))
            dates.add(getLabel("ViewItem_lblDatePublishedInPrint") + ": " + getMetadata().getDatePublishedInPrint());
        
        
        String allDates = "";
        
        for (String date : dates)
        {
            allDates = allDates + date + " | ";
            
        }
        
        //remove last two signs
        if (allDates.length()>2)
            allDates = allDates.substring(0, allDates.length()-2);
        
        return allDates;
        
        
    }
    
    /**
     * gets the genre of the item
     * @return String the genre of the item
     */
    public String getGenre()
    {
    	String genre="";
    	if(getMetadata().getGenre() != null)
    	{
    		genre = getLabel(getApplicationBean().convertEnumToString(getMetadata().getGenre()));
    	}
		return genre;
    }
    
    /**
     * gets the genre of the first source of the item
     * @return String the genre of the source
     */
    public String getSourceGenre()
    {
    	String sourceGenre="";
    	if(this.firstSource != null && this.firstSource.getGenre() != null)
    	{
    		sourceGenre = getLabel(getApplicationBean().convertEnumToString(this.firstSource.getGenre()));
    	}
		return sourceGenre;
    }
    
    /**
     * Returns a formatted String containing the start and the end page of the source
     * @return String the formatted start and end page
     */
    public String getStartEndPageSource()
    {
        StringBuffer startEndPage = new StringBuffer();
        if(this.firstSource != null)
        {
        	if(this.firstSource.getStartPage() != null)
            {
                startEndPage.append(this.firstSource.getStartPage());
            }
            
            if(this.firstSource.getEndPage() != null && !this.firstSource.getEndPage().trim().equals(""))
            {
                startEndPage.append(" - ");
                startEndPage.append(this.firstSource.getEndPage());
            }
        }
        return startEndPage.toString();
    }
    
    /**
     * Returns the formatted Publishing Info according to filled elements
     * @return String the formatted Publishing Info
     */
    public String getPublishingInfo()
    {
        StringBuffer publishingInfo = new StringBuffer();
        publishingInfo.append("");
        if(getMetadata().getPublishingInfo() != null)
        {
            // Edition
            if(getMetadata().getPublishingInfo().getEdition() != null)
            {
                publishingInfo.append(getMetadata().getPublishingInfo().getEdition());
            }
            
            // Comma
            if((getMetadata().getPublishingInfo().getEdition() != null && !getMetadata().getPublishingInfo().getEdition().trim().equals("")) && ((getMetadata().getPublishingInfo().getPlace() != null && !getMetadata().getPublishingInfo().getPlace().trim().equals("")) || (getMetadata().getPublishingInfo().getPublisher() != null && !getMetadata().getPublishingInfo().getPublisher().trim().equals(""))))
            {
                    publishingInfo.append(". ");
            }
            
            // Place
            if(getMetadata().getPublishingInfo().getPlace() != null)
            {
                publishingInfo.append(getMetadata().getPublishingInfo().getPlace().trim());
            }
            
            // colon
            if(getMetadata().getPublishingInfo().getPublisher() != null && !getMetadata().getPublishingInfo().getPublisher().trim().equals("") && getMetadata().getPublishingInfo().getPlace() != null && !getMetadata().getPublishingInfo().getPlace().trim().equals(""))
            {
                    publishingInfo.append(" : ");
            }
            
            // Publisher
            if(getMetadata().getPublishingInfo().getPublisher() != null)
            {
                publishingInfo.append(getMetadata().getPublishingInfo().getPublisher().trim());
            }
        }
        return publishingInfo.toString();
    }
    
    /**
     * Returns the formatted Publishing Info of the source(!!!) according to filled elements
     * @return String the formatted Publishing Info of the source
     */
    public String getPublishingInfoSource()
    {
        StringBuffer publishingInfoSource = new StringBuffer();
        publishingInfoSource.append("");
        if(this.firstSource.getPublishingInfo() != null)
        {
            // Edition
            if(this.firstSource.getPublishingInfo().getEdition() != null)
            {
            	publishingInfoSource.append(this.firstSource.getPublishingInfo().getEdition());
            }
            
            // Comma
            if((this.firstSource.getPublishingInfo().getEdition() != null && !this.firstSource.getPublishingInfo().getEdition().trim().equals("")) && ((this.firstSource.getPublishingInfo().getPlace() != null && !this.firstSource.getPublishingInfo().getPlace().trim().equals("")) || (this.firstSource.getPublishingInfo().getPublisher() != null && !this.firstSource.getPublishingInfo().getPublisher().trim().equals(""))))
            {
            	publishingInfoSource.append(". ");
            }
            
            // Place
            if(this.firstSource.getPublishingInfo().getPlace() != null)
            {
            	publishingInfoSource.append(this.firstSource.getPublishingInfo().getPlace().trim());
            }
            
            // colon
            if(this.firstSource.getPublishingInfo().getPublisher() != null && !this.firstSource.getPublishingInfo().getPublisher().trim().equals("") && this.firstSource.getPublishingInfo().getPlace() != null && !this.firstSource.getPublishingInfo().getPlace().trim().equals(""))
            {
            	publishingInfoSource.append(" : ");
            }
            
            // Publisher
            if(this.firstSource.getPublishingInfo().getPublisher() != null)
            {
            	publishingInfoSource.append(this.firstSource.getPublishingInfo().getPublisher().trim());
            }
        }
        return publishingInfoSource.toString();
    }
    
    /**
     * Returns the event title (50 Chars) and crops the last characters 
     * @return String the event title
     */
    public String getEventTitle()
    {
    	String eventTitle = "";
    	if(getMetadata().getEvent() != null 
    			&& getMetadata().getEvent().getTitle() != null 
    			&& getMetadata().getEvent().getTitle().getValue() != null 
    			&& !getMetadata().getEvent().getTitle().getValue().trim().equals(""))
    	{
    		if(getMetadata().getEvent().getTitle().getValue().length() > 50)
    		{
    			eventTitle = getMetadata().getEvent().getTitle().getValue().substring(0, 49) + "...";
    		}
    		else
    		{
    			eventTitle = getMetadata().getEvent().getTitle().getValue();
    		}
    	}
		return eventTitle;
    }
    
    /**
     * Returns the title (80 Chars) and crops the last characters.
     * Specification says 100 chars, but this is too long, 50 is too short.
     * @return String the title
     */
    public String getShortTitle()
    {
    	if(getMetadata().getTitle() != null 
    			&& getMetadata().getTitle().getValue() != null 
    			&& !getMetadata().getTitle().getValue().trim().equals(""))
    	{
    		if(getMetadata().getTitle().getValue().length() > 80)
    		{
    			return getMetadata().getTitle().getValue().substring(0, 79) + "...";
    		}
    		else
    		{
    			return getMetadata().getTitle().getValue();
    		}
    	}
    	else
    	{
    		return null;
    	}
    }
    
    /**
     * Returns the source title (50 Chars) of the first source and crops the last characters 
     * @return String the event title
     */
    public String getSourceTitle()
    {
    	String sourceTitle = "";
    	if(this.firstSource != null 
    			&& this.firstSource.getTitle() != null 
    			&& this.firstSource.getTitle().getValue() != null 
    			&& !this.firstSource.getTitle().getValue().trim().equals(""))
    	{
    		if(this.firstSource.getTitle().getValue().length() > 50)
    		{
    			sourceTitle = this.firstSource.getTitle().getValue().substring(0, 49) + "...";
    		}
    		else
    		{
    			sourceTitle = this.firstSource.getTitle().getValue();
    		}
    	}
		return sourceTitle;
    }
    
    /**
     * Generates the affiliated organization list as one string for presenting it in the jsp via the dynamic html component.
     * Doubled organizations will be detected and merged. All organizzations will be numbered. 
     */
    private void createAffiliatedOrganizationList()
    {
        String formattedOrganization = "";
        List<CreatorVO> tempCreatorList;
        List<OrganizationVO> tempOrganizationList = null;
        List<OrganizationVO> sortOrganizationList = null;
        this.organizationArray = new ArrayList<String>();
        this.organizationList = new ArrayList<ViewItemOrganization>();
        tempOrganizationList = new ArrayList<OrganizationVO>();
        sortOrganizationList = new ArrayList<OrganizationVO>();
        tempCreatorList = getMetadata().getCreators();
        int affiliationPosition = 0;
        for (int i = 0; i < tempCreatorList.size(); i++)
        {
            CreatorVO creator = new CreatorVO();
            creator = tempCreatorList.get(i);
            if (creator.getPerson() != null)
            {
                if (creator.getPerson().getOrganizations().size() > 0)
                {
                    for (int listSize = 0; listSize < creator.getPerson().getOrganizations().size(); listSize++)
                    {
                        tempOrganizationList.add(creator.getPerson().getOrganizations().get(listSize));
                    }
                    for (int j = 0; j < tempOrganizationList.size(); j++)
                    {
                        // if the organization is not in the list already, put
                        // it in.
                        if (!sortOrganizationList.contains(tempOrganizationList.get(j)))
                        {
                            affiliationPosition++;
                            sortOrganizationList.add(tempOrganizationList.get(j));
                            ViewItemOrganization viewOrganization = new ViewItemOrganization();
                            if(tempOrganizationList.get(j).getName() != null)
                            {
                                viewOrganization.setOrganizationName(tempOrganizationList.get(j).getName().getValue());
                            }
                            viewOrganization.setOrganizationAddress(tempOrganizationList.get(j).getAddress());
                            viewOrganization.setOrganizationIdentifier(tempOrganizationList.get(j).getIdentifier());
                            viewOrganization.setPosition(new Integer(affiliationPosition).toString());
                            if(tempOrganizationList.get(j).getName() != null)
                            {
                                viewOrganization.setOrganizationInfoPage(tempOrganizationList.get(j).getName().getValue(),
                                        tempOrganizationList.get(j).getAddress());
                            }
                            this.organizationList.add(viewOrganization);
                        }
                    }
                }
            }
        }
        // save the List in the backing bean for later use.
        this.affiliatedOrganizationsList = sortOrganizationList;
        // generate a 'well-formed' list for presentation in the jsp
        for (int k = 0; k < sortOrganizationList.size(); k++)
        {
        	String name = sortOrganizationList.get(k).getName() != null ? sortOrganizationList.get(k).getName().getValue() : "";
            formattedOrganization = "<p>"+(k + 1) + ": " + name +"</p>" + "<p>" + sortOrganizationList.get(k).getAddress() + "</p>" + "<p>" + sortOrganizationList.get(k).getIdentifier() + "</p>";
            this.organizationArray.add(formattedOrganization);
        }
    }
    
    /**
     * Generates the creator list as list of formatted Strings.
     * 
     * @return String formatted creator list as string
     */
    private void createCreatorList()
    {
        StringBuffer creatorList = new StringBuffer();
        String formattedCreator = "";
        this.creatorArray = new ArrayList<String>();
        this.creatorOrganizationsArray = new ArrayList<ViewItemCreatorOrganization>();
        // counter for organization array
        int counterOrganization = 0;
        StringBuffer annotation;
        ObjectFormatter formatter = new ObjectFormatter();
        
        
        for (int i = 0; i < getMetadata().getCreators().size(); i++)
        {
            CreatorVO creator = new CreatorVO();
            creator = getMetadata().getCreators().get(i);
            annotation = new StringBuffer();
            int organizationsFound = 0;
            for (int j = 0; j < this.affiliatedOrganizationsList.size(); j++)
            {
                if (creator.getPerson() != null)
                {
                    if (creator.getPerson().getOrganizations().contains(this.affiliatedOrganizationsList.get(j)))
                    {
                        if (organizationsFound == 0)
                        {
                            annotation.append("   [");
                        }
                        if (organizationsFound > 0 && j < this.affiliatedOrganizationsList.size())
                        {
                            annotation.append(",");
                        }
                        annotation.append(new Integer(j + 1).toString());
                        organizationsFound++;
                    }
                }
            }
            if (annotation.length() > 0)
            {
                annotation.append("]");
            }
            formattedCreator = formatter.formatCreator(creator) + annotation.toString();
            if (creator.getPerson() != null)
            {
                this.creatorArray.add(formattedCreator);
            }
            if (creator.getOrganization() != null)
            {
                ViewItemCreatorOrganization creatorOrganization = new ViewItemCreatorOrganization();
                creatorOrganization.setOrganizationName(formattedCreator);
                creatorOrganization.setPosition(new Integer(counterOrganization).toString());
                creatorOrganization.setOrganizationAddress(creator.getOrganization().getAddress());
                creatorOrganization.setOrganizationInfoPage(formattedCreator, creator.getOrganization()
                        .getAddress());
                this.creatorOrganizationsArray.add(creatorOrganization);
                counterOrganization++;
            }
            creatorList.append(formattedCreator);
            this.allCreatorsList.add(formattedCreator);
        }
    }
    
    /**
     * Returns the ApplicationBean.
     * 
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
    	return (ApplicationBean)FacesContext
    	.getCurrentInstance()
    	.getApplication().getVariableResolver()
    	.resolveVariable(FacesContext.getCurrentInstance(), ApplicationBean.BEAN_NAME);
    }
    
    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#getLabel(java.lang.String)
     */
    public String getLabel(String placeholder)
    {
        return ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle()).getString(placeholder);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#getMessage(java.lang.String)
     */
    public String getMessage(String placeholder)
    {
        return ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle()).getString(placeholder);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#bindComponentLabel(javax.faces.component.UIComponent, java.lang.String)
     */
    public void bindComponentLabel(UIComponent component, String placeholder)
    {
        ValueExpression value = FacesContext
            .getCurrentInstance()
            .getApplication()
            .getExpressionFactory()
            .createValueExpression(FacesContext.getCurrentInstance().getELContext(), "#{lbl." + placeholder + "}", String.class);
        component.setValueExpression("value", value); 
    }
    
    /**
     * This method examines the pubitem concerning its files and generates
     * a display string for the page according to the number of files detected.
     *
     * @param pubitemVo the pubitem to be examined
     * @return String the formatted String to display the occurencies of files
     */
    public String getFileInfo()
    {
        StringBuffer files = new StringBuffer();

        if (this.getFileList() != null)
        {
            files.append(this.getFileList().size());

            // if there is only 1 file, display "File attached", otherwise display "Files attached" (plural)
            if (this.getFileList().size() == 1)
            {
                files.append(" " + getLabel("ViewItemShort_lblFileAttached"));
            }
            else
            {
                files.append(" " + getLabel("ViewItemShort_lblFilesAttached"));
            }
        }
        return files.toString();
    }
    
    /**
     * This method examines the pubitem concerning its locators and generates
     * a display string for the page according to the number of locators detected.
     *
     * @param pubitemVo the pubitem to be examined
     * @return String the formatted String to display the occurencies of locators
     */
    public String getLocatorInfo()
    {
        StringBuffer locators = new StringBuffer();

        if (this.getLocatorList() != null)
        {
        	locators.append(this.getLocatorList().size());

            // if there is only 1 locator, display "Locator", otherwise display "Locators" (plural)
            if (this.getLocatorList().size() == 1)
            {
            	locators.append(" " + getLabel("ViewItemShort_lblLocatorAttached"));
            }
            else
            {
            	locators.append(" " + getLabel("ViewItemShort_lblLocatorsAttached"));
            }
        }
        return locators.toString();
    }
    
    /**
     * This method examines which file is really a file and not a locator and returns a list of native files
     * @return List<FileVO> file list
     */
    private List<FileVO> getFileList()
    {
    	List<FileVO> fileList = new ArrayList<FileVO>();
    	if(this.getFiles() != null)
    	{
    		for(int i = 0; i < this.getFiles().size(); i++)
    		{
    			if(this.getFiles().get(i).getLocator() == null || this.getFiles().get(i).getLocator().trim().equals(""))
    			{
    				fileList.add(this.getFiles().get(i));
    			}
    		}
    	}
    	return fileList;
    }
    
    /**
     * This method examines which file is a locator and not a file and returns a list of locators
     * @return List<FileVO> locator list
     */
    private List<FileVO> getLocatorList()
    {
    	List<FileVO> locatorList = new ArrayList<FileVO>();
    	if(this.getFiles() != null)
    	{
    		for(int i = 0; i < this.getFiles().size(); i++)
    		{
    			if(this.getFiles().get(i).getLocator() != null && !this.getFiles().get(i).getLocator().trim().equals(""))
    			{
    				locatorList.add(this.getFiles().get(i));
    			}
    		}
    	}
    	return locatorList;
    }
    
    /**
     * Counts the files and gives info back as int
     * @return int the amount of files belonging to this item
     */
    public int getAmountOfFiles()
    {
    	int countedFiles = 0;
    	
    	if(this.getFileList() != null)
    	{
    		countedFiles = this.getFileList().size();
    	}
    	return countedFiles;
    }
    
    /**
     * Counts the locators and gives info back as int
     * @return int the amount of locators belonging to this item
     */
    public int getAmountOfLocators()
    {
    	int countedLocators = 0;
    	
    	if(this.getLocatorList() != null)
    	{
    		countedLocators = this.getLocatorList().size();
    	}
    	return countedLocators;
    }
    
    /**
     * Counts the sources of the current item
     * @return int number of sources
     */
    public int getFurtherSources()
    {
    	int furtherSources = 0;
    	// get the  number of sources (if bigger than 1) minus the first one
    	if(getMetadata().getSources() != null && getMetadata().getSources().size() > 1)
    	{
    		furtherSources = getMetadata().getSources().size() - 1;
    	}
    	return furtherSources;
    }
    
    /**
     * Counts the creators and returns the number as int (inportant for rendering in )
     * @return int number of creators
     */
    public int getCountCreators()
    {
    	int creators = 0;
    	if(this.creatorArray != null)
    	{
    		creators = creators + this.creatorArray.size();
    	}
    	if(this.creatorOrganizationsArray != null)
    	{
    		creators = creators + this.creatorOrganizationsArray.size();
    	}
    	return creators;
    }
    
    /**
     * Counts the affiliated organizations and returns the number as int (inportant for rendering in )
     * @return int number of organiozations
     */
    public int getCountAffiliatedOrganizations()
    {
    	int organizations = 0;
    	if(this.affiliatedOrganizationsList != null)
    	{
    		organizations = organizations + this.affiliatedOrganizationsList.size();
    	}
    	return organizations;
    }


	public void switchToMediumView()
    {
    	shortView = false;
    }
    
    public void switchToShortView()
    {
    	shortView = true;
    }
    
    public void select(ValueChangeEvent event)
    {
    	selected = ((Boolean)event.getNewValue()).booleanValue();
    }

    public String getLink() throws Exception
    {
    	if (this.getVersion() !=  null && this.getVersion().getObjectId() != null)
    	{
    		return PropertyReader
					.getProperty("escidoc.pubman.instance.url")
    			+ PropertyReader
    				.getProperty("escidoc.pubman.item.pattern")
    				.replaceAll("\\$1", this.getVersion().getObjectId()
    						+ (this.getVersion().getVersionNumber() != 0 ? ":" 
    								+ this.getVersion().getVersionNumber() : ""));
    	}
    	else
    	{
    		return null;
    	}
    }
    
	public java.util.List<SearchHitVO> getSearchHitList() {
		return searchHitList;
	}

	public ArrayList<String> getOrganizationArray() {
		return organizationArray;
	}

	public void setOrganizationArray(ArrayList<String> organizationArray) {
		this.organizationArray = organizationArray;
	}

	public ArrayList<ViewItemOrganization> getOrganizationList() {
		return organizationList;
	}

	public void setOrganizationList(ArrayList<ViewItemOrganization> organizationList) {
		this.organizationList = organizationList;
	}

	public List<OrganizationVO> getAffiliatedOrganizationsList() {
		return affiliatedOrganizationsList;
	}

	public void setAffiliatedOrganizationsList(
			List<OrganizationVO> affiliatedOrganizationsList) {
		this.affiliatedOrganizationsList = affiliatedOrganizationsList;
	}

	public ArrayList<String> getCreatorArray() {
		return creatorArray;
	}

	public void setCreatorArray(ArrayList<String> creatorArray) {
		this.creatorArray = creatorArray;
	}

	public ArrayList<ViewItemCreatorOrganization> getCreatorOrganizationsArray() {
		return creatorOrganizationsArray;
	}

	public void setCreatorOrganizationsArray(
			ArrayList<ViewItemCreatorOrganization> creatorOrganizationsArray) {
		this.creatorOrganizationsArray = creatorOrganizationsArray;
	}

	public SourceVO getFirstSource() {
		return firstSource;
	}

	public void setFirstSource(SourceVO firstSource) {
		this.firstSource = firstSource;
	}

	public List<SearchHitBean> getSearchHits() {
		return searchHits;
	}

	public void setSearchHits(List<SearchHitBean> searchHits) {
		this.searchHits = searchHits;
	}

	public String getCoins() {
		return coins;
	}

	public void setCoins(String coins) {
		this.coins = coins;
	}

	public ArrayList<String> getAllCreatorsList() {
		return allCreatorsList;
	}

	public void setAllCreatorsList(ArrayList<String> allCreatorsList) {
		this.allCreatorsList = allCreatorsList;
	}

    public boolean getIsRevisionView()
    {
        return isRevisionView;
    }

    public void setIsRevisionView(boolean isRevisionView)
    {
        this.isRevisionView = isRevisionView;
    }

	public boolean isSearchResult() {
		return isSearchResult;
	}

	public void setSearchResult(boolean isSearchResult) {
		this.isSearchResult = isSearchResult;
	}
	
}
