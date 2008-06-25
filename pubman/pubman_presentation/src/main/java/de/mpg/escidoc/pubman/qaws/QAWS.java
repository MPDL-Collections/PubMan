/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
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
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.pubman.qaws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemList;
import de.mpg.escidoc.pubman.ItemListSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.pubman.PubItemSearching;
import de.mpg.escidoc.services.pubman.QualityAssurance;
import de.mpg.escidoc.services.pubman.ServiceLocator;

/**
 * Fragment class for the Quality Assurance Workspace. This class provides all functionality for choosing one or more items out
 * of a list, depending on the status of the items and on the container. 
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class QAWS extends ItemList
{
    public static final String BEAN_NAME = "QAWS";
    private static Logger logger = Logger.getLogger(QAWS.class);
    // Faces navigation string
    public static final String LOAD_QAWS = "loadQAWSPage";
    public static final String MESSAGE_SUCCESSFULLY_REVISED = null;
    
    //Pull-Down Menu with all Collections
    private HtmlSelectOneMenu cboContextMenu = new HtmlSelectOneMenu();
    
    
    //Pull-Down Menu with item states
    private HtmlSelectOneMenu cboItemStateMenu = new HtmlSelectOneMenu();
    
    
    //List for collection menu
    private SelectItem[] selectContextList;
    
    //List for item state selection menu
    private SelectItem[] selectItemStateList;
    
    private boolean enableNoItemMsg = false;
    
    private QualityAssurance qualityAssurance;
    
    private Map<String, AffiliationVOPresentation> affiliationMap = new HashMap<String, AffiliationVOPresentation>();
    private LoginHelper loginHelper;
    
    

    /**
     * Public constructor.
     */
    public QAWS()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initializing DepositorWS...");
        }
        
        if (!"QAWS".equals(getItemListSessionBean().getType()))
        {
        	getItemListSessionBean().setListDirty(true);
        }
        
        // Perform initializations inherited from our superclass
        super.init();
        
        //Get Searching Bean
        try
        {
            InitialContext initialContext = new InitialContext();
            this.qualityAssurance = (QualityAssurance) initialContext.lookup(QualityAssurance.SERVICE_NAME);
        }
        catch (NamingException e)
        {
            logger.debug("Cannot find QualityAssuranceBean");
            logger.error(e.getMessage(), e);
        }
        
        this.loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);;

        //create drop-down menus
        this.initContextMenu();
            
        
        // create the itemList if neccessary
        if (this.getItemListSessionBean().isListDirty())
        {
            String retVal = this.createItemList(this.getSessionBean().getSelectedContextId(), this.getSessionBean().getSelectedItemState(), true);
            // if createItemList returns an error, force JSF to load the ErrorPage
            if (retVal == ErrorPage.LOAD_ERRORPAGE)
            {
                try
                {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("ErrorPage.jsp");
                }
                catch (Exception e)
                {
                    logger.error(e.toString(), e);
                }
            }
        }

    }


  

    public void initContextMenu()
    {
        try
        {
            
            //Contexts (Collections)
            List<PubContextVOPresentation> contextVOList = getContextListSessionBean().getModeratorContextList();
            selectContextList = new SelectItem[contextVOList.size()]; 
            
            for(int i=0; i<contextVOList.size(); i++)
            {
                String workflow = "null";
                if (contextVOList.get(i).getAdminDescriptor().getVisibilityOfReferences()!= null)
                    workflow = contextVOList.get(i).getAdminDescriptor().getVisibilityOfReferences();
                selectContextList[i] = new SelectItem(contextVOList.get(i).getReference().getObjectId(), contextVOList.get(i).getName()+" -- "+workflow);
  
            }
            if (contextVOList.size()>0 && getSessionBean().getSelectedContextId() == null)
            {
                String selectedContextId = contextVOList.get(0).getReference().getObjectId();
                getSessionBean().setSelectedContextId(selectedContextId);
                
            }
               
        }
        catch (Exception e)
        {
            logger.debug("Error in initializing menus");
            logger.error(e.toString(), e);
            e.printStackTrace();
        }
    }
    


    /**
     * Called when the collection is changed. Creates a new list according to the new state.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String changeContext()
    {
        // force reload of list next time this page is navigated to
        this.getItemListSessionBean().setListDirty(true);
        String newContext = (String) getCboContextMenu().getSubmittedValue();
        String newState = (String) getCboItemStateMenu().getSubmittedValue();
        return (this.createItemList(newContext, newState, false));
    }
    
    /**
     * Called when the organizational unit is changed. Creates a new list according to the new state.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String changeOU()
    {
        // force reload of list next time this page is navigated to
        this.getItemListSessionBean().setListDirty(true);
        String newContext = (String) getCboContextMenu().getSubmittedValue();
        String newState = (String) getCboItemStateMenu().getSubmittedValue();
        
        return (this.createItemList(newContext, newState, false));
    }
    
    /**
     * Called when the item state is changed. Creates a new list according to the new state.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String changeItemState()
    {
        // force reload of list next time this page is navigated to
        this.getItemListSessionBean().setListDirty(true);
        String newContext = (String) getCboContextMenu().getSubmittedValue();
        String newState = (String) getCboItemStateMenu().getSubmittedValue();
        return (this.createItemList(newContext, newState, false));
    }
    
    
    

    /**
     * Creates a new itemList in the FacesBean and forces the UI component to create a new itemList.
     *
     * @param newItemState the new state
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    private String createItemList(final String newContextId, final String newState, boolean initial)
    {
        // clear all itemLists stored in the FacesBean
        getItemListSessionBean().getCurrentPubItemList().clear();
        getItemListSessionBean().getSelectedPubItems().clear();
        getItemListSessionBean().setCurrentPubItemListPointer(0);
      
        logger.debug("New QAWS state: Context: " + newContextId +" ---- Item State: " + newState.toString());
        
       
        // retrieve the items
       
        List<PubItemVO> itemList = getSessionBean().getPubItemList();
 //       List<PubItemVO> newItemList = new ArrayList<PubItemVO>();
        try
        {
            
            
            
            if (!newContextId.equals(getSessionBean().getSelectedContextId()) || !newState.equals(getSessionBean().getSelectedItemState()) || initial)
            {
                itemList = qualityAssurance.searchForQAWorkspace(newContextId, newState, loginHelper.getAccountUser());
                
            }

        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        
        this.getSessionBean().setSelectedContextId(newContextId);
        this.getSessionBean().setSelectedItemState(newState);
        this.getSessionBean().setPubItemList(itemList);
       
       
        // set new list in FacesBean
        this.getItemListSessionBean().setCurrentPubItemList(CommonUtils.convertToPubItemVOPresentationList(itemList));
        // sort the items and force the UI to update
        this.sortItemList();
        // no reload necessary next time this page is navigated to
        this.getItemListSessionBean().setListDirty(false);
        this.getItemListSessionBean().setType("QAWS");
        getViewItemSessionBean().setNavigationStringToGoBack(QAWS.LOAD_QAWS);
        return QAWS.LOAD_QAWS;
    }

    

    /**
     * Creates the panel newly according to the values in the FacesBean.
     */
    protected void createDynamicItemList2()
    {
        if (this.getItemListSessionBean().getCurrentPubItemList() != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating dynamic item list with " + this.getItemListSessionBean().getCurrentPubItemList().size()
                        + " entries.");
            }
            // create an ItemListUI for all PubItems
            List<PubItemVOPresentation> pubItemList = this.getItemListSessionBean().getCurrentPubItemList();

        }
    }

    

    /**
     * Returns the ItemListSessionBean.
     *
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected ItemListSessionBean getItemListSessionBean()
    {
        return (ItemListSessionBean) getSessionBean(ItemListSessionBean.class);
    }

    /**
     * Returns the QAWSSessionBean.
     *
     * @return a reference to the scoped data bean (QAWSSessionBean)
     */
    protected QAWSSessionBean getSessionBean()
    {
        return (QAWSSessionBean) getSessionBean(QAWSSessionBean.class);
    }

    /**
     * Returns the ContextListSessionBean.
     *
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getCollectionListSessionBean()
    {
        return (ContextListSessionBean)getBean(ContextListSessionBean.class);
    }

    /**
     * Returns the ApplicationBean.
     *
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
        return (ApplicationBean) getBean(ApplicationBean.class);
    }

    public SelectItem[] getSelectContextList()
    {
        
    	return selectContextList;
    }

    

    public HtmlSelectOneMenu getCboContextMenu()
    {
        return cboContextMenu;
    }

    public void setCboContextMenu(HtmlSelectOneMenu cboItemstate)
    {
        this.cboContextMenu = cboItemstate;
    }

	public boolean getEnableNoItemMsg() {
		return enableNoItemMsg;
	}

	public void setEnableNoItemMsg(boolean enableNoItemMsg) {
		this.enableNoItemMsg = enableNoItemMsg;
	}


    public HtmlSelectOneMenu getCboItemStateMenu()
    {
        return cboItemStateMenu;
    }

    public void setCboItemStateMenu(HtmlSelectOneMenu cboItemStateMenu)
    {
        this.cboItemStateMenu = cboItemStateMenu;
    }

    public SelectItem[] getSelectItemStateList()
    {
      //Item State List
        selectItemStateList = this.i18nHelper.getSelectItemsForEnum(false, new PubItemVO.State[]{PubItemVO.State.SUBMITTED, PubItemVO.State.RELEASED, PubItemVO.State.IN_REVISION});
       
        return selectItemStateList;
    }

    public void setSelectItemStateList(SelectItem[] selectItemStateList)
    {
        this.selectItemStateList = selectItemStateList;
    }
    
    private ContextListSessionBean getContextListSessionBean()
    {
        return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    }
    
    
    
    

}
