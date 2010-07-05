/*
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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.pubman.export;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO.FormatType;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Superclass for keeping the attributes used during the session by ExportItems.
 * @author:  Galina Stancheva, created 02.08.2007
 * @version: $Revision$ $LastChangedDate$
 */
public class ExportItemsSessionBean extends FacesBean
{
    //private static Logger logger = Logger.getLogger(ExportItemsSessionBean.class);
    public static final String BEAN_NAME = "ExportItemsSessionBean";
    private String message = null;
    List<ExportFormatVO> listExportFormatVO  = new ArrayList<ExportFormatVO>();   
    
    //default export format settings format
    public final String DEFAULT_EXPORT_FORMAT = "APA";
    
    private String exportFormatName = DEFAULT_EXPORT_FORMAT;
    private String exportFormatType;
    private String fileFormat;
    
    private ExportFormatVO curExportFormatVO;        
	private FileFormatVO curFileFormatVO;
    
    private boolean enableFileFormats = true;
    private boolean enableExport = true;

    //email properties
    private File attExportFile = null;
    private String attExportFileName = "ExportFile";
    private String exportEmailTxt = ExportItems.MESSAGE_EXPORT_EMAIL_TEXT;
    private String exportEmailSubject = ExportItems.MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT;
    private String exportEmailReplyToAddr = "";
    private String emailRecipients = "";
    private String emailCCRecipients = "";
    private String emailBCCRecipients = "";
    private String emailSenderProp = "";
    private String emailServernameProp = "";
    private String emailWithAuthProp = "";
    private String emailAuthUserProp = "";
    private String emailAuthPwdProp = "";
   
    public String exportDisplayData = "No export data available";
                
    private static Logger logger = Logger.getLogger(ExportItemsSessionBean.class);
    private final String PROPERTY_PREFIX_FOR_EMAILSERVICE_SERVERNAME = "escidoc.pubman_presentation.email.mailservername";
    private final String PROPERTY_PREFIX_FOR_EMAILSERVICE_SENDER = "escidoc.pubman_presentation.email.sender";
    private final String PROPERTY_PREFIX_FOR_EMAILSERVICE_WITHAUTHENTICATION = "escidoc.pubman_presentation.email.withauthentication";
    private final String PROPERTY_PREFIX_FOR_EMAILSERVICE_AUTHUSER = "escidoc.pubman_presentation.email.authenticationuser";
    private final String PROPERTY_PREFIX_FOR_EMAILSERVICE_AUTHPWD = "escidoc.pubman_presentation.email.authenticationpwd";
    
    
    /**
     * Public constructor.
     */
    public ExportItemsSessionBean()
    {
        this.init();
    }

       
    /**
     * This method is called when this bean is initially added to session scope. Typically, this occurs as a result of
     * evaluating a value binding or method binding expression, which utilizes the managed bean facility to instantiate
     * this bean and store it into session scope.
     */    
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
        
        initializeExportFormats();
        
        try
        {
           emailSenderProp = PropertyReader.getProperty(PROPERTY_PREFIX_FOR_EMAILSERVICE_SENDER);
           emailServernameProp = PropertyReader.getProperty(PROPERTY_PREFIX_FOR_EMAILSERVICE_SERVERNAME);
           emailWithAuthProp = PropertyReader.getProperty(PROPERTY_PREFIX_FOR_EMAILSERVICE_WITHAUTHENTICATION);
           emailAuthUserProp = PropertyReader.getProperty(PROPERTY_PREFIX_FOR_EMAILSERVICE_AUTHUSER);
           emailAuthPwdProp = PropertyReader.getProperty(PROPERTY_PREFIX_FOR_EMAILSERVICE_AUTHPWD);
        }
        catch (Exception e)
        {
            logger.warn("Propertyfile not readable for emailserver  properties'");
        }
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }
    
    
    public String initializeExportFormats()
    {
        logger.debug(">>> initializeExportFormats "); 
        try
        {
            //get the existing export formats from the external service 
            setListExportFormatVO(this.getItemControllerSessionBean().retrieveExportFormats());
            setCurVOsByExportFormatName(DEFAULT_EXPORT_FORMAT);

       }        
        catch (TechnicalException e)
        {
            logger.error("Could not set export formats." + "\n" + e.toString(), e);
            ((ErrorPage)getSessionBean(ErrorPage.class)).setException(e);
        
            return ErrorPage.LOAD_ERRORPAGE;
        }
         return "OK";
    }    

    
    private void setCurVOsByExportFormatName(String exportFormatName)
    {
    	//set curExportFormatVO
        for (ExportFormatVO ef: getListExportFormatVO())
        {
        	if ( exportFormatName.equals(ef.getId()) )
        	{
        		setCurExportFormatVO(ef);
        		break;
        	}
        }
        ExportFormatVO cef = getCurExportFormatVO(); 
        //formatType (layout|structured)
        setExportFormatType(cef.getFormatType().toString());
        //curFileFormatVO 
        setCurFileFormatVO(cef.getSelectedFileFormat());
        //fileFormat name
        setFileFormat(getCurFileFormatVO().getName());
        //formatType switcher, true for layout
        setEnableFileFormats(cef.getFormatType() == FormatType.layout);
        
    }
    
    public ExportFormatVO getCurExportFormatVO()
    {
       return this.curExportFormatVO;
    }
    /**
	 * @param curExportFormatVO the curExportFormatVO to set
	 */
	public void setCurExportFormatVO(ExportFormatVO curExportFormatVO) 
	{
		this.curExportFormatVO = curExportFormatVO;
	}

    
    /**
	 * @return the curFileFormatVO
	 */
	public FileFormatVO getCurFileFormatVO() {
		return curFileFormatVO;
	}


	/**
	 * @param curFileFormatVO the curFileFormatVO to set
	 */
	public void setCurFileFormatVO(FileFormatVO curFileFormatVO) {
		this.curFileFormatVO = curFileFormatVO;
	}


	public String getMessage()
    {
        return this.message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

     public List<ExportFormatVO> getListExportFormatVO()
    {
        return this.listExportFormatVO;
    }

    public void setListExportFormatVO(List<ExportFormatVO> listExportFormatVO)
    {
        this.listExportFormatVO = listExportFormatVO;
        
    }
    
    public String getExportFormatType()
    {
        return this.exportFormatType;
    }

    public void setExportFormatType(String exportFormatType)
    {
    	this.exportFormatType = exportFormatType;
    }
 
    public String getExportFormatName()
    {       
        return this.curExportFormatVO.getId();
    }

    public void setExportFormatName(String exportFormatName)
    {
        this.exportFormatName = exportFormatName;
    	setCurVOsByExportFormatName(exportFormatName);
    }
    
    public String getFileFormat()
    {
//        return this.curExportFormatVO.getSelectedFileFormat().getName();
        return this.fileFormat;
    }

    public void setFileFormat(String fileFormat)    
    {
    	for (FileFormatVO ff: getCurExportFormatVO().getFileFormats())
    	{
    		if (fileFormat.equals(ff.getName()))
    		{
    			setCurFileFormatVO(ff);
    			getCurExportFormatVO().setSelectedFileFormat(ff);
    			break;
    		}
    	}
    	this.fileFormat = fileFormat;
        logger.debug("setFileFormat.....:" + this.curExportFormatVO.getSelectedFileFormat().getName() + ";" + this.curExportFormatVO.getSelectedFileFormat().getMimeType());
        
   }

    
    
    

    //////////////////////////////////////////////////////////////////////////////////////////7
    //next methods are used by EMAIL-ing
    /*
     * navigationString to go back to the list where exportEmail has been called from
     */ 
    private String navigationStringToGoBack = null;
 
    public final String getNavigationStringToGoBack()
    {
        return navigationStringToGoBack;
    }
    public final void setNavigationStringToGoBack(final String navigationStringToGoBack)
    {
        this.navigationStringToGoBack = navigationStringToGoBack;
    }

    public void setAttExportFile(File attFile){
        
        this.attExportFile = new File(attFile.toURI());
    
    }
    public File getAttExportFile(){
        
        return this.attExportFile;
    
    }
    public void setAttExportFileName(String fileName){
        
        this.attExportFileName = fileName;
    
    }
    public String getAttExportFileName(){
        
        return this.attExportFileName;
    
    }
   public void setExportEmailSubject(String exportEmailSubject){
        
        this.exportEmailSubject = exportEmailSubject;
    
    }
   public String getExportEmailSubject(){
        
        return this.exportEmailSubject;
    
    }

  public void setExportEmailTxt(String exportEmailTxt){
        
        this.exportEmailTxt = exportEmailTxt;
    
    }
      public String getExportEmailTxt(){
            
            return this.exportEmailTxt;
      }
    
      public void setExportEmailReplyToAddr(String exportEmailReplyToAddr){
          
          this.exportEmailReplyToAddr = exportEmailReplyToAddr;
      
      }
    public String getExportEmailReplyToAddr(){
          
          return this.exportEmailReplyToAddr;
    }
    
    public void setEmailRecipients(String emailRecipients){
        
        this.emailRecipients = emailRecipients;
    
    }
    public String getEmailRecipients(){
        
        return this.emailRecipients;
    }
    
    public void setEmailSenderProp(String emailSender){
        
        this.emailSenderProp = emailSender;
    
    }
    public String getEmailSenderProp(){
        
        logger.debug("getEmailSenderProp "+ this.emailSenderProp); 

        return this.emailSenderProp;
    }

    public void setEmailServernameProp(String name){
    }
    public String getEmailServernameProp(){
        return this.emailServernameProp;
        
    }
      
    public void setEmailWithAuthProp(String trueorfalse){
        this.emailWithAuthProp = trueorfalse;
   }
    public String getEmailWithAuthProp(){
        return this.emailWithAuthProp;
    }
 
    public void setEmailAuthUserProp(String user){
        this.emailAuthUserProp = user;
   }
    public String getEmailAuthUserProp(){
        return this.emailAuthUserProp;
    }
    
    public void setEmailAuthPwdProp(String user){
        this.emailAuthPwdProp = user;
   }
    public String getEmailAuthPwdProp(){
        return this.emailAuthPwdProp;
    }
    
    public void setExportDisplayData(String data){        
       //logger.debug("setExportDisplayData "+ data);    
       exportDisplayData = data;
    
    }
    public String getExportDisplayData(){
        //logger.debug("getExportDisplayData ");
        return exportDisplayData;
    }


	public boolean getEnableFileFormats() {
		return enableFileFormats;
	}


	public void setEnableFileFormats(boolean enableFileFormats) {
		this.enableFileFormats = enableFileFormats;
	}


	public boolean getEnableExport() {
		return enableExport;
	}


	public void setEnableExport(boolean enableExport) {
		this.enableExport = enableExport;
	}


	public String getEmailCCRecipients() {
		return emailCCRecipients;
	}


	public void setEmailCCRecipients(String emailCCRecipients) {
		this.emailCCRecipients = emailCCRecipients;
	}


	public String getEmailBCCRecipients() {
		return emailBCCRecipients;
	}


	public void setEmailBCCRecipients(String emailBCCRecipients) {
		this.emailBCCRecipients = emailBCCRecipients;
	}

}
