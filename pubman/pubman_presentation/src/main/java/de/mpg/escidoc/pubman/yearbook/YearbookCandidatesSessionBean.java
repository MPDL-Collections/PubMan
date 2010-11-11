package de.mpg.escidoc.pubman.yearbook;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class YearbookCandidatesSessionBean extends FacesBean
{
	public static String BEAN_NAME = "YearbookCandidatesSessionBean";
    private static Logger logger = Logger.getLogger(YearbookCandidatesSessionBean.class);
    
   
    
    private String selectedOrgUnit;
    
   
    
    /**
     * A list with the menu entries for the org units filter menu.
     */
    private List<SelectItem> orgUnitSelectItems;

    private YearbookItemSessionBean yisb;

    private PubItemListSessionBean pilsb;

    
    public YearbookCandidatesSessionBean()
    {
        yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
        pilsb = (PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class);
    }
  

    public String getSelectedOrgUnit() {
        return selectedOrgUnit;
    }

    public void setSelectedOrgUnit(String selectedOrgUnit) {
        this.selectedOrgUnit = selectedOrgUnit;
    }
    
    public List<SelectItem> getOrgUnitSelectItems()
    {
        if(orgUnitSelectItems == null || orgUnitSelectItems.size()==0)
        {
           try
        {
            orgUnitSelectItems = new ArrayList<SelectItem>();
               InitialContext initialContext = new InitialContext();
               XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
               OrganizationalUnitHandler ouHandler = ServiceLocator.getOrganizationalUnitHandler();
               String topLevelOU = ouHandler.retrieve(yisb.getYearbookItem().getMetadata().getCreators().get(0).getOrganization().getIdentifier());
               AffiliationVO affVO = xmlTransforming.transformToAffiliation(topLevelOU);
               List<AffiliationVOPresentation> affList = new ArrayList<AffiliationVOPresentation>();
               affList.add(new AffiliationVOPresentation(affVO));
               addChildAffiliationsToMenu(affList, orgUnitSelectItems, 0);
        }
        catch (Exception e)
        {
            logger.error("Error retrieving org units", e);
        }
       
           
        }
        return orgUnitSelectItems;
    }

    public void setOrgUnitSelectItems(List<SelectItem> orgUnitSelectItems)
    {
        this.orgUnitSelectItems = orgUnitSelectItems;
    }
    
    private static void addChildAffiliationsToMenu(List<AffiliationVOPresentation> affs, List<SelectItem> affSelectItems, int level) throws Exception
    {
        String prefix = "";
        for (int i = 0; i < level; i++)
        {
            //2 save blanks
            prefix += '\u00A0';
            prefix += '\u00A0';
            prefix += '\u00A0';
        }
        //1 right angle
        prefix += '\u2514';
        for (AffiliationVOPresentation aff : affs)
        {
            affSelectItems.add(new SelectItem(aff.getReference().getObjectId(), prefix + " " + aff.getName()));
            addChildAffiliationsToMenu(aff.getChildren(), affSelectItems, level + 1);
        }
    }


   
    
    
    

}
