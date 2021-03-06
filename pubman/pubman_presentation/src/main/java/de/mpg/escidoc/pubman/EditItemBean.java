package de.mpg.escidoc.pubman;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.util.CreatorVOPresentation;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.OrganizationVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.transformation.util.creators.Author;
import de.mpg.escidoc.services.transformation.util.creators.AuthorDecoder;

public class EditItemBean extends FacesBean
{
    private static final Logger logger = Logger.getLogger(EditItemBean.class);
    
    /**
     * Stores a string from a hidden input field (set by javascript) that indicates whether the author copy&paste elements are to be displayed or not.
     */
    private String showAuthorCopyPaste;
    
    /**Checkbox if existing authors should be overwritten with the ones from author copy/paste*/
    private boolean overwriteCreators;

    private List<CreatorVOPresentation> creators = new ArrayList<CreatorVOPresentation>();

    private List<OrganizationVOPresentation> creatorOrganizations = new ArrayList<OrganizationVOPresentation>();
    
    /**The string with authors to parse for author copy&paste.*/
    private String creatorParseString;
    
    private boolean organizationPasted = false;
    
    /** List containing the ou number of the organizations which are mapped to a creator */
    private List<Integer> usedOrganizations = new ArrayList<Integer>();

    public List<CreatorVOPresentation> getCreators()
    {
        return creators;
    }

    public void setCreators(List<CreatorVOPresentation> creators)
    {
        this.creators = creators;
    }

    public List<OrganizationVOPresentation> getCreatorOrganizations()
    {
        return creatorOrganizations;
    }

    public void setCreatorOrganizations(List<OrganizationVOPresentation> creatorOrganizations)
    {
        this.creatorOrganizations = creatorOrganizations;
    }
    

    public void initOrganizationsFromCreators()
    {
        List<OrganizationVOPresentation> creatorOrganizations = new ArrayList<OrganizationVOPresentation>();
        int counter = 1;
        for (CreatorVOPresentation creator : creators)
        {

            if (creator.getType() == CreatorType.PERSON)
            {

                for (OrganizationVO organization : creator.getPerson().getOrganizations())
                {

                    if (!creatorOrganizations.contains(organization))
                    {
                        OrganizationVOPresentation organizationPresentation = new OrganizationVOPresentation(organization);
                        if (!organizationPresentation.isEmpty())
                        {
                            organizationPresentation.setBean(this);
                            if (organizationPresentation.getName() ==  null)
                            {
                                organizationPresentation.setName(new TextVO());
                            }
                            creatorOrganizations.add(organizationPresentation);
                            counter++;

                        }
                    }
                }
            }
        }
        
        //if ther is still no organization add a new one
        if(creatorOrganizations.isEmpty())
        {
            OrganizationVOPresentation org = new OrganizationVOPresentation();
            org.setBean(this);
            creatorOrganizations.add(org);
        }
        this.creatorOrganizations = creatorOrganizations;
    }

    public int getCreatorsSize()
    {
        return creators.size();
    }
    
    /**
     * Returns the content(set by javascript) from a hidden input field  that indicates whether the author copy&paste elements are to be displayed or not.
     */
    public  String getShowAuthorCopyPaste()
    {
        return showAuthorCopyPaste;
    }

    /**Sets the content from a hidden input field  that indicates whether the author copy&paste elements are to be displayed or not.
     */
    public void setShowAuthorCopyPaste(String showAuthorCopyPaste)
    {
        this.showAuthorCopyPaste = showAuthorCopyPaste;
    }

    public void setOverwriteCreators(boolean overwriteCreators)
    {
        this.overwriteCreators = overwriteCreators;
    }

    public boolean getOverwriteCreators()
    {
        return overwriteCreators;
    }

    /**
     * localized creation of SelectItems for the creator roles available.
     * @return SelectItem[] with Strings representing creator roles.
     */
    public SelectItem[] getCreatorRoles()
    {
        return ((InternationalizationHelper) EditItem.getSessionBean(InternationalizationHelper.class)).getSelectItemsCreatorRole(true);
    }

    /**
     * localized creation of SelectItems for the creator types available.
     * @return SelectItem[] with Strings representing creator types.
     */
    public SelectItem[] getCreatorTypes()
    {

        return ((InternationalizationHelper) EditItem.getSessionBean(InternationalizationHelper.class)).getSelectItemsCreatorType(false);
    }
    
    public boolean bindOrganizationsToCreators()
    {
    	
    	usedOrganizations.clear();
        for (CreatorVOPresentation creator : getCreators())
        {
            if (!bindOrganizationsToCreator(creator))
            {
                return false;
            }
        }
        
        for(OrganizationVOPresentation org : getCreatorOrganizations())
        {
        	
        	if(!org.isEmpty() && !usedOrganizations.contains(org.getNumber()))
        	{

        		error(getMessage("EntryIsNotBound").replace("$1", String.valueOf(org.getNumber())));
        		return false;
        	}
        }
        return true;
    }

    public void bindCreatorsToBean(List<CreatorVO> creatorList)
    {
        List<CreatorVOPresentation> creators = getCreators();
        creators.clear();
        
        for (CreatorVO creator : creatorList)
        {
            CreatorVOPresentation beanCreator = new CreatorVOPresentation(creators, this, creator);
            if (beanCreator.getPerson() != null && beanCreator.getPerson().getIdentifier() == null)
            {
                beanCreator.getPerson().setIdentifier(new IdentifierVO());
            }
            creators.add(beanCreator);
        }
        
    }

    public void bindCreatorsToVO(List<CreatorVO> creators)
    {
        creators.clear();
        for (CreatorVOPresentation creatorVOPresentation : getCreators())
        {
            CreatorVO creatorVO;
            if (CreatorType.ORGANIZATION == creatorVOPresentation.getType())
            {
                creatorVO = new CreatorVO(creatorVOPresentation.getOrganization(), creatorVOPresentation.getRole());
                if (creatorVO.getOrganization() != null && creatorVO.getOrganization().getName() != null
                        && (creatorVO.getOrganization().getName().getValue() == null
                        || "".equals(creatorVO.getOrganization().getName().getValue())))
                {
                    creatorVO.getOrganization().setName(null);
                }
            }
            else
            {
                creatorVO = new CreatorVO(creatorVOPresentation.getPerson(), creatorVOPresentation.getRole());
            }

            creators.add(creatorVO);
        }
    }

    /**
     * @param creator
     */
    public boolean bindOrganizationsToCreator(CreatorVOPresentation creator)
    {
        if (creator.isPersonType())
        {
            PersonVO person = creator.getPerson();
            List<OrganizationVO> personOrgs = person.getOrganizations();
            String[] orgArr = new String[]{};
            if (creator.getOuNumbers() != null)
            {
                orgArr = creator.getOuNumbers().split(",");
            }
            personOrgs.clear();
            try
            {
                for (String org : orgArr)
                {
                    if (!"".equals(org))
                    {
                        int orgNr = Integer.parseInt(org);
                        personOrgs.add(getCreatorOrganizations().get(orgNr - 1));
                        usedOrganizations.add(orgNr);
                    }
                }
            }
            catch (NumberFormatException nfe)
            {
                error(getMessage("EntryIsNotANumber").replace("$1", creator.getOuNumbers()));
                return false;
            }
            catch (IndexOutOfBoundsException ioobe)
            {
                error(getMessage("EntryIsNotInValidRange").replace("$1", creator.getOuNumbers()));
                return false;
            }
            catch (Exception e)
            {
                logger.error("Unexpected error evaluation creator organizations", e);
                error(getMessage("ErrorInOrganizationAssignment").replace("$1", creator.getOuNumbers()));
                return false;
            }
            
            
        }
        return true;
    }

    public int getOrganizationCount()
    {
        return getCreatorOrganizations().size();
    }
    
    public String readPastedOrganizations()
    {
        logger.debug("readPastedOrganizations");
        this.organizationPasted = false;
        return "";
    }
    
    public boolean isOrganizationPasted()
    {
        return organizationPasted;
    }

    public void setOrganizationPasted(boolean organizationPasted)
    {
        this.organizationPasted = organizationPasted;
    }

    public void clean()
    {
        this.getCreatorOrganizations().clear();
        this.getCreators().clear();

        this.setShowAuthorCopyPaste("");
        this.creatorParseString = "";
    }

    public void setCreatorParseString(String creatorParseString)
    {
        this.creatorParseString = creatorParseString;
    }

    public String getCreatorParseString()
    {
        return creatorParseString;
    }
    
    /** Parses a string that includes creators in different formats and adds them to the
     * given creatorCollection.
     * 
     * @param creatorString The String to be parsed
     * @param creatorCollection The collection to which the creators should be added
     * @param orgs A list of organizations that should be added to every creator. null if no organizations should be added.
     * @param overwrite Indicates if the already existing creators should be overwritten
     * @throws Exception
     */
    public void parseCreatorString(String creatorString, List<OrganizationVO> orgs, boolean overwrite) throws Exception
    {
        AuthorDecoder authDec = new AuthorDecoder(creatorString);
        
        List<Author> authorList = authDec.getBestAuthorList();
        if (authorList == null || authorList.size() == 0)
        {
            throw new Exception("Couldn't parse given creator string");
        }
        
        if (overwrite)
        {
            getCreators().clear();
        }
        
        //check if last existing author is empty, then remove it
        if (getCreators().size() >= 1)
        {
            CreatorVOPresentation creatorVO = getCreators().get(getCreators().size() - 1);
            // creator is a person
            if (creatorVO.isPersonType() 
                    && creatorVO.getPerson() != null 
                    && "".equals(creatorVO.getPerson().getFamilyName()) 
                    && "".equals(creatorVO.getPerson().getGivenName()) 
                    && (creatorVO.getPerson().getOrganizations().isEmpty() 
                            || creatorVO.getPerson().getOrganizations().get(0).getName().getValue() == null 
                            || "".equals(creatorVO.getPerson().getOrganizations().get(0).getName().getValue())))
            {
                getCreators().remove(creatorVO);
            }
            // creator is an organisation
            else if (creatorVO.isOrganizationType()
                        && creatorVO.getOrganization() != null
                        && "".equals(creatorVO.getOrganization().getName()))
            {
                getCreators().remove(creatorVO);
            }
        }
        
       
        //add authors to creator collection
        for (Author author : authorList)
        {
            CreatorVOPresentation creator = new CreatorVOPresentation(getCreators(), this);
            creator.setPerson(new PersonVO());
            creator.getPerson().setIdentifier(new IdentifierVO());
            creator.setOuNumbers("");
            getCreators().add(creator);
            
            if (author.getPrefix() != null && !"".equals(author.getPrefix()))
            {
                creator.getPerson().setFamilyName(author.getPrefix() + " " + author.getSurname());
            }
            else
            {
                creator.getPerson().setFamilyName(author.getSurname());
            }
            creator.getPerson().setGivenName(author.getGivenName());

            creator.setRole(CreatorRole.AUTHOR);
            creator.setType(CreatorType.PERSON);
            
        }

    }
    

}
