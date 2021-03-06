package de.mpg.escidoc.services.common.valueobjects.metadata;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.types.Coordinates;
import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;

public class MdsOrganizationalUnitDetailsVO extends MetadataSetVO
{
    
    private String city;
    private Coordinates coordinates;
    
    /**
     * These codes are the upper-case, two-letter codes as defined by ISO-3166. You can find a full list of these codes
     * at a number of sites, such as: http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-
     * en1.html
     */
    private String countryCode;
    private List<String> descriptions = new ArrayList<String>();
    
    /**
     * Identifier of an external resource.
     */
    private List<IdentifierVO> identifiers = new ArrayList<IdentifierVO>();

    /**
     * The unique name of the affiliation in the organizational structure.
     */
    private String name;
    private List<String> alternativeNames = new ArrayList<String>();

    private String type;
    
    private String startDate;
    private String endDate;
    
    
    
    /**
     * Default constructor.
     */
    public MdsOrganizationalUnitDetailsVO()
    {
        
    }
    
    /**
     * Clone constructor.
     * 
     * @param other The {@link MdsOrganizationalUnitDetailsVO} to be cloned.
     */
    public MdsOrganizationalUnitDetailsVO(MdsOrganizationalUnitDetailsVO other)
    {
        this.city = other.city;
        this.countryCode = other.countryCode;
        this.descriptions = other.descriptions;
        this.name = other.name;
        this.alternativeNames = other.alternativeNames;
        this.identifiers = other.identifiers;
        this.coordinates = other.coordinates;
        this.setStartDate(other.getStartDate());
        this.setEndDate(other.getEndDate());
    }

    /*
    public void setStartDateAsString(String date) throws Exception
    {
        if (date == null || "".equals(date))
        {
            setStartDate(null);
        }
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.ESCIDOC_DATE_FORMAT);
            setStartDate(sdf.parse(date));
        }
    }

    public void setEndDateAsString(String date) throws Exception
    {
        if (date == null || "".equals(date))
        {
            setEndDate(null);
        }
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.ESCIDOC_DATE_FORMAT);
            setEndDate(sdf.parse(date));
        }
    }
    */
    
    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public Coordinates getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates)
    {
        this.coordinates = coordinates;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getDescriptions()
    {
        return descriptions;
    }

    public List<IdentifierVO> getIdentifiers()
    {
        return identifiers;
    }

    public List<String> getAlternativeNames()
    {
        return alternativeNames;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getEndDate()
    {
        return endDate;
    }
    
}
