<!DOCTYPE html>
<!--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

	

	
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
		<f:loadBundle var="lbl" basename="Label" />
		<f:loadBundle var="msg" basename="Messages" />
		<f:loadBundle var="tip" basename="Tooltip" />
		<html xmlns="http://www.w3.org/1999/xhtml">
	
			<h:head>
				<title>
					<h:outputText
					value="#{ViewItemFull.pubItem.metadata.title.value} :: #{ApplicationBean.appTitle}"
					converter="HTMLTitleSubSupConverter" />
				</title>
				<link rel="unapi-server" type="application/xml" title="unAPI"
					href="${ViewItemFull.unapiURLview}" />
				<ui:fragment rendered="#{ViewItemFull.pubItem == null or ViewItemFull.isStateWithdrawn}">
					<meta name="robots" content="noindex" />
				</ui:fragment>	
				<h:outputText value="#{ViewItemFull.htmlMetaTags}" escape="false" rendered="#{ViewItemFull.pubItem != null and ViewItemFull.isStateReleased}"/>
				
				<meta name="description" content="${ViewItemFull.pubItem.descriptionMetaTag}"/>
				
				<ui:include src="header/ui/StandardImports.jspf" />
				
				<h:outputStylesheet name="commonJavaScript/jquery/css/jquery-ui-1.10.4.min.css"/>
				<h:outputScript name="commonJavaScript/jquery/jquery-ui-1.10.4.min.js"/>
				<script src="/cone/js/jquery.suggest.js" />
				<h:outputScript name="commonJavaScript/componentJavaScript/autoSuggestFunctions.js" />
				
				<style type="text/css">
					.dialogNoTitleBar .ui-dialog-titlebar {display:none;}
					.ui-dialog {background:#eee}
				</style>
				
				<script type="text/javascript">
					var currentDialog;
					var text = '${msg.ViewItem_doiDialog}';
					function showDialog(){
						currentDialog = $("<p>" + text + "</p>").dialog(
						{
							dialogClass: "dialogNoTitleBar",
							modal:true, 
							width: "auto",
							resizable: false,
							draggable:false,
							width: 500,
							
							buttons: [
								
								{
									text: "#{lbl.cancel}",
									click: function() {
										$(this).dialog("close");
									}
								},
								{
									text: "#{lbl.ViewItemFull_lblDoi}",
									click: function() {
										$(".hiddenLnkExecuteAddDoi").click();
										$(this).dialog("close"); 
									}
								}
							],
							close: function(event, ui)
							{ 
		            			$(this).dialog("destroy");
		        			} 
	        			});
					}
				</script>
				
			</h:head>

			<body lang="${InternationalizationHelper.locale}">
				<h:outputText value="#{ViewItemFullPage.beanName}"
					styleClass="noDisplay" />
				<!-- The unAPI Identifier for this item -->
				<h:outputText
					value="&lt;abbr class='unapi-id' title='#{ViewItemFull.pubItem.version.objectIdAndVersion}'&gt;&lt;/abbr&gt;"
					escape="false"
					rendered="#{ViewItemFull.pubItem != null and ViewItemFull.isStateReleased}" />
			
				
					<div class="full wrapper">
						<h:inputHidden id="offset"></h:inputHidden>
			
						<ui:include src="header/Header.jspf" />
						<h:form>
						<div id="content" class="full_area0 clear">
							<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
							<div class="clear">
								<div class="headerSection">
									<ui:include src="header/Breadcrumb.jspf" />
			
									<div id="contentSkipLinkAnchor" class="clear headLine">
										<!-- Headline starts here -->
										<h1>
											<h:outputText value="#{lbl.ViewItemPage}" />
										</h1>
										<!-- Headline ends here -->
									</div>
								</div>
								<div class="small_marginLIncl subHeaderSection">
									
									<!-- content menu starts here -->
									<h:panelGroup layout="block" styleClass="contentMenu"
										rendered="#{ViewItemFull.pubItem != null}">
										
										<!-- content menu upper line starts here -->
										<div class="free_area0 sub">
											<h:outputLink id="lnkLinkForActionsView" styleClass="free_area0"
												value="#{ViewItemFull.linkForActionsView}"
												rendered="#{ViewItemSessionBean.subMenu != 'ACTIONS'}">
												<h:outputText value="#{lbl.ViewItemFull_lblItemActions}" />
											</h:outputLink>
											<h:outputText styleClass="free_area0"
												value="#{lbl.ViewItemFull_lblItemActions}"
												rendered="#{ViewItemSessionBean.subMenu == 'ACTIONS'}" />
											<h:outputText styleClass="seperator void" />
											<h:outputLink id="lnkLinkForExportView" styleClass="free_area0"
												value="#{ViewItemFull.linkForExportView}"
												rendered="#{ViewItemSessionBean.subMenu != 'EXPORT' and !ViewItemFull.isStateWithdrawn}">
												<h:outputText value="#{lbl.List_lblExportOptions}" />
											</h:outputLink>
											<h:outputText styleClass="free_area0"
												value="#{lbl.List_lblExportOptions}"
												rendered="#{ViewItemSessionBean.subMenu == 'EXPORT' and !ViewItemFull.isStateWithdrawn}" />
										</div>
										<!-- content menu upper line ends here -->
										
										<!-- content menu lower line (actions) starts here -->
										<h:panelGroup layout="block" styleClass="free_area0 sub action"
											rendered="#{ViewItemSessionBean.subMenu == 'ACTIONS'}">
											<h:commandLink id="lnkEdit" action="#{ViewItemFull.editItem}"
												value="#{lbl.actionMenu_lnkEdit}"
												rendered="#{ViewItemFull.canEdit}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canEdit}" />
											<h:commandLink id="lnkSubmit"
												action="#{ViewItemFull.submitItem}"
												value="#{lbl.actionMenu_lnkSubmit}"
												rendered="#{ViewItemFull.canSubmit}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canSubmit}" />
											<h:commandLink id="lnkRelease"
												action="#{ViewItemFull.submitItem}"
												value="#{lbl.actionMenu_lnkRelease}"
												rendered="#{ViewItemFull.canRelease}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canRelease}" />
											<h:commandLink id="lnkAccept"
												action="#{ViewItemFull.acceptItem}"
												value="#{lbl.actionMenu_lnkAccept}"
												rendered="#{ViewItemFull.canAccept}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canAccept}" />
											<h:commandLink id="lnkRevise"
												action="#{ViewItemFull.reviseItem}"
												value="#{lbl.actionMenu_lnkRevise}"
												rendered="#{ViewItemFull.canRevise}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canRevise}" />
											<h:commandLink id="lnkDelete"
												onclick="if(!confirm('#{msg.deleteMessage}'))return false;"
												value="#{lbl.actionMenu_lnkDelete}"
												action="#{ViewItemFull.deleteItem}"
												rendered="#{ViewItemFull.canDelete}"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canDelete}" />
											<h:commandLink id="lnkWithdraw"
												action="#{ViewItemFull.withdrawItem}"
												value="#{lbl.actionMenu_lnkWithdraw}"
												rendered="#{ViewItemFull.canWithdraw}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canWithdraw}"/>
											<h:commandLink id="lnkModify"
												action="#{ViewItemFull.modifyItem}"
												value="#{lbl.actionMenu_lnkModify}"
												rendered="#{ViewItemFull.canModify}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canModify}" />
											<h:commandLink id="lnkCreateNewRevision"
												action="#{ViewItemFull.createNewRevision}"
												value="#{lbl.actionMenu_lnkCreateNewRevision}"
												rendered="#{ViewItemFull.canCreateNewRevision}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canCreateNewRevision}" />
											<h:commandLink id="lnkCreateItemFromTemplate"
												action="#{ItemControllerSessionBean.createItemFromTemplate}"
												value="#{lbl.ViewItemFull_lblCreateItemFromTemplate}"
												rendered="#{ViewItemFull.canCreateFromTemplate}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canCreateFromTemplate}" />
											<h:commandLink id="lnkAddToBasket"
												action="#{ViewItemFull.addToBasket}"
												value="#{lbl.ViewItemFull_lblAddToBasket}"
												rendered="#{ViewItemFull.canAddToBasket}" onclick="fullItemReloadAjax();"/>
											<h:commandLink id="lnkDeleteFromBasket"
												action="#{ViewItemFull.removeFromBasket}"
												value="#{lbl.ViewItemFull_lblRemoveFromBasket}"
												rendered="#{ViewItemFull.canDeleteFromBasket}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.isCandidateOfYearbook}" />
											<h:commandLink id="lnkAddToYearbook" styleClass="free_area0"
												value="#{lbl.Yearbook_addToYearbookViewItem}" type="reset"
												action="#{ViewItemFull.addToYearbookMember}" immediate="true"
												rendered="#{ViewItemFull.isCandidateOfYearbook}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.isMemberOfYearbook}" />
											<h:commandLink id="lnkRemoveFromYearbook"
												styleClass="free_area0"
												value="#{lbl.Yearbook_removeFromYearbookViewItem}"
												action="#{ViewItemFull.removeMemberFromYearbook}"
												rendered="#{ViewItemFull.isMemberOfYearbook}" onclick="fullItemReloadAjax();"/>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.ssrnContext and !ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" />
											<h:commandLink id="lnkAddSsrn" styleClass="free_area0"
												title="#{tip.ViewItemFull_lblAddSsrn }"
												action="#{ViewItemFull.addSsrnTag}"
												rendered="#{ViewItemFull.ssrnContext and !ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}"
												onclick="fullItemReloadAjax();">
												<h:panelGroup styleClass="min_imgBtn add" />
												<h:outputText value="#{lbl.ViewItemFull_lblSSRN}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.ssrnContext and ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" />
											<h:commandLink id="lnkRemoveSsrn" styleClass="free_area0"
												title="#{tip.ViewItemFull_lblRemoveSsrn }"
												action="#{ViewItemFull.removeSsrnTag}"
												rendered="#{ViewItemFull.ssrnContext and ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}"
												onclick="fullItemReloadAjax();">
												<h:panelGroup styleClass="min_imgBtn remove" />
												<h:outputText value="#{lbl.ViewItemFull_lblSSRN}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.doiCappable and (ViewItemFull.canEdit or ViewItemFull.canModify)}" />
											<h:outputLink id="lnkAddDoi" styleClass="free_area0" value="#" title="#{tip.ViewItemFull_lblAddDoi}" rendered="#{ViewItemFull.doiCappable and (ViewItemFull.canEdit or ViewItemFull.canModify)}" onclick="showDialog();">
												<h:outputText value="#{lbl.ViewItemFull_lblDoi}"/>
											</h:outputLink>
									
											<!-- hidden Button for executing the addDoi command, after the jquery dialog has been confirmed -->
											<h:commandLink id="lnkExecuteAddDoi" styleClass="hiddenLnkExecuteAddDoi" style="display:none;" value="#" action="#{ViewItemFull.addDoi}"  onclick="fullItemReloadAjax();"/>
											
											
										</h:panelGroup>
										<!-- content menu lower line (actions) ends here -->
										
										<!-- content menu lower line (export) starts here -->
										<h:panelGroup layout="block" styleClass="free_area0 sub action"
											rendered="#{ViewItemSessionBean.subMenu == 'EXPORT'}">
											<h:panelGroup layout="block"
												styleClass="xLarge_area1 endline selectContainer">
												<h:panelGroup layout="block" styleClass="xLarge_area0">
													<h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
													<h:panelGroup layout="block"
														styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
												</h:panelGroup>
												<h:selectOneMenu id="selEXPORTFORMAT" styleClass="replace"
													onfocus="updateSelectionBox(this);"
													value="#{ExportItemsSessionBean.exportFormatName}"
													onchange="$(this).parents('.sub').find('.exportUpdateButton').click();">
													<f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}" />
												</h:selectOneMenu>
											</h:panelGroup>
											<!-- 
												<h:selectOneMenu id="selEXPORTFORMAT" value="#{ExportItemsSessionBean.exportFormatName}" styleClass="xLarge_select replace" onchange="$(this).parents('.sub').find('.exportUpdateButton').click();">
														 <f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}"/>
												</h:selectOneMenu>	-->
											<h:commandButton styleClass="noDisplay exportUpdateButton"
												action="#{ExportItems.updateExportFormats}"
												value="updateExportFormats" />
											<h:panelGroup layout="block"
												styleClass="medium_area1 endline selectContainer"
												rendered="#{ExportItemsSessionBean.enableFileFormats}">
												<h:panelGroup layout="block" styleClass="medium_area0">
													<h:panelGroup styleClass="medium_area0 selectionBox">&#160;</h:panelGroup>
													<h:panelGroup layout="block"
														styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
												</h:panelGroup>
												<h:selectOneMenu id="selFILEFORMAT" styleClass="replace"
													onfocus="updateSelectionBox(this);"
													value="#{ExportItemsSessionBean.fileFormat}"
													onchange="updateSelectionBox(this);">
													<f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}" />
												</h:selectOneMenu>
											</h:panelGroup>
											<h:commandLink id="btnExportDownload" styleClass="free_area0"
												value="#{lbl.export_btDownload}"
												action="#{ViewItemFull.exportDownload}"/>
											<h:outputText styleClass="seperator" />
											<h:commandLink id="btnExportEMail" styleClass="free_area0"
												value="#{lbl.export_btEMail}"
												action="#{ViewItemFull.exportEmail}"/>
											<h:panelGroup layout="block" styleClass="free_area0 suggestAnchor endline CSL" rendered="#{ExportItemsSessionBean.enableCslAutosuggest }">
												<h:inputText id="inputCitationStyleName"
													styleClass="huge_txtInput citationStyleSuggest citationStyleName"
													value="#{ExportItemsSessionBean.citationStyleName}" title="#{ExportItemsSessionBean.citationStyleName}" pt:placeholder="Zitierstil eingeben" />
												<h:inputText id="inputCitationStyleIdentifier"
													styleClass="noDisplay citationStyleIdentifier" value="#{ExportItemsSessionBean.coneCitationStyleId}" />
												<h:outputLink class="fa fa-list-ul" value="#{AdvancedSearchEdit.suggestConeUrl}citation-styles/all/format=html" title="Liste aller Zitierstile" target="_blank"/>
												<h:commandButton id="btnRemoveCslAutoSuggest" value=" " styleClass="xSmall_area0 min_imgBtn closeIcon removeAutoSuggestCsl" style="display:none;"
													onclick="removeCslAutoSuggest($(this))" title="#{tip.ViewItem_lblRemoveAutosuggestCsl}">
													<f:ajax render="form1:iterCreatorOrganisationAuthors" execute="@form"/>
												</h:commandButton>	
											</h:panelGroup>
										</h:panelGroup>
										<!-- content menu lower line (export) starts here -->
										
									</h:panelGroup>
									<!-- content menu ends here -->
			
									<!-- Subheadline starts here -->
									<h:panelGroup layout="block" styleClass="subHeader"
										rendered="#{ViewItemFull.isLoggedIn }">
										<h:outputText
											value="#{lbl.EditItem_lblItemVersionID} '#{ViewItemFull.pubItem.version.objectIdAndVersion}'."
											rendered="#{ViewItemFull.pubItem.version.objectIdAndVersion != null}" />
										<br />
										<h:outputText
											value="#{lbl.EditItem_lblCollectionOfItem} '#{ViewItemFull.contextName}', #{lbl.ViewItemFull_lblIsAffiliatedTo}: '#{ViewItemFull.affiliations}'." />
										<br />
										<h:outputText
											value="#{lbl.EditItem_lblItemDepositor} '#{ViewItemFull.owner.name}'"
											rendered="#{ViewItemFull.owner != null }" />
										<h:outputText value="."
											rendered="#{ViewItemFull.owner != null and ViewItemFull.creationDate == null}" />
										<h:outputText value=" --- #{ViewItemFull.creationDate}"
											rendered="#{ViewItemFull.creationDate != null and ViewItemFull.owner != null }" />
										<h:outputText
											value="#{lbl.EditItem_lblItemlatestChange } #{ViewItemFull.creationDate}"
											rendered="#{ViewItemFull.creationDate != null and ViewItemFull.owner == null }" />
										<br />
										<h:outputText
											value="#{lbl.EditItem_lblItemLatestModifier} '#{ViewItemFull.latestModifier.name}'"
											rendered="#{ViewItemFull.latestModifier != null}" />
										<h:outputText value="."
											rendered="#{ViewItemFull.latestModifier != null and ViewItemFull.modificationDate == null}" />
										<h:outputText value=" --- #{ViewItemFull.modificationDate}"
											rendered="#{ViewItemFull.modificationDate != null and ViewItemFull.latestModifier != null }" />
										<h:outputText
											value="#{lbl.EditItem_lblItemLatestModification} #{ViewItemFull.modificationDate}"
											rendered="#{ViewItemFull.modificationDate != null and ViewItemFull.latestModifier == null }" />
										<br />
										<h:outputText
											value="#{msg.ViewItemFull_latestMessage} #{ViewItemFull.pubItem.version.lastMessage}"
											rendered="#{ViewItemFull.canShowLastMessage}" />
										<h:outputText
											value="#{msg.ViewItemFull_latestMessage} #{lbl.lbl_noEntry}"
											rendered="#{!ViewItemFull.canShowLastMessage}" />
									</h:panelGroup>
									<!-- Subheadline ends here -->
									
									<!-- JSF messages -->
									<div class="subHeader">
										<h:messages styleClass="singleMessage" errorClass="messageError"
											warnClass="messageWarn" fatalClass="messageFatal"
											infoClass="messageStatus" layout="list" globalOnly="false"
											showDetail="false" showSummary="true"
											rendered="#{ViewItemFull.numberOfMessages == 1}" />
										<h:panelGroup layout="block"
											styleClass="half_area2_p6 messageArea errorMessageArea"
											rendered="#{ViewItemFull.hasErrorMessages and ViewItemFull.numberOfMessages != 1}">
											<h2>
												<h:outputText value="#{lbl.warning_lblMessageHeader}" />
											</h2>
											<h:messages errorClass="messageError" warnClass="messageWarn"
												fatalClass="messageFatal" infoClass="messageStatus"
												layout="list" globalOnly="false" showDetail="false"
												showSummary="true" rendered="#{ViewItemFull.hasMessages}" />
										</h:panelGroup>
										<h:panelGroup layout="block"
											styleClass="half_area2_p6 messageArea infoMessageArea"
											rendered="#{ViewItemFull.hasMessages and !ViewItemFull.hasErrorMessages and ViewItemFull.numberOfMessages != 1}">
											<h2>
												<h:outputText value="#{lbl.info_lblMessageHeader}" />
											</h2>
											<h:messages errorClass="messageError" warnClass="messageWarn"
												fatalClass="messageFatal" infoClass="messageStatus"
												layout="list" globalOnly="false" showDetail="false"
												showSummary="true" rendered="#{ViewItemFull.hasMessages}" />
										</h:panelGroup>
										<!-- Special validation messages for yearbook -->
										<h:panelGroup layout="block"
											styleClass="half_area2_p6 messageArea errorMessageArea clear"
											style="padding-top: 0px !important;"
											rendered="#{ViewItemFull.pubItem.validationReport!=null}">
											<h2>
												<h:outputText value="#{lbl.Yearbook_validationMessageHeader}" />
											</h2>
											<ul>
												<ui:repeat var="valitem"
													value="#{ViewItemFull.pubItem.validationReport.items}">
													<h:panelGroup rendered="#{valitem.restrictive}">
														<li class="messageWarn"><h:outputText
																value="#{msg[valitem.content]}" /></li>
													</h:panelGroup>
													<h:panelGroup rendered="#{!valitem.restrictive}">
														<li class="messageStatus"><h:outputText
																value="#{msg[valitem.content]}" /></li>
													</h:panelGroup>
												</ui:repeat>
											</ul>
										</h:panelGroup>
										<!-- Survey link -->
										<h:panelGroup layout="block" style="margin-top:1em;"
											rendered="#{not empty HomePage.surveyUrl}">
											<div class="xHuge_area2_p6 messageArea">
												<span class="half_area0">
													<h2>
														<h:outputText value="#{HomePage.surveyTitle}" />
													</h2>
												</span> <span class="huge_area0"> <h:outputText
														value="#{HomePage.surveyText}" />
												</span> <span class="free_area0">
													<div class="medium_area2_p6 small_marginLExcl">
			
														<h:outputLink styleClass="activeButton"
															value="#{HomePage.surveyUrl}" title="User Survey"
															target="_blank">
															<h:outputText value="User Survey" />
														</h:outputLink>
													</div>
												</span>
											</div>
										</h:panelGroup>
									</div>
									<!-- Subheadline ends here -->
									
								</div>
							</div>
							<h:panelGroup layout="block" styleClass="full_area0"
								rendered="#{ViewItemFull.pubItem != null}">
								<div class="full_area0 fullItem">
									
									<!-- Item control information starts here -->
									<div class="full_area0 fullItemControls">
										<span class="full_area0_p5"> <b
											class="free_area0 small_marginLExcl">&#160;
											<h:outputText
													styleClass="messageError"
													value="#{msg.ViewItemFull_withdrawn}"
													rendered="#{ViewItemFull.isStateWithdrawn}" /></b> 
											<h:panelGroup
												styleClass="seperator"
												rendered="#{ViewItemFull.canViewLocalTags}" /> 
											<h:outputLink
												id="lnkViewLocalTagsPage" styleClass="free_area0"
												value="#{ApplicationBean.appContext}ViewLocalTagsPage.jsp"
												rendered="#{ViewItemFull.canViewLocalTags}">
												<h:outputText value="#{lbl.ViewItemFull_lblSubHeaderLocalTags}" />
											</h:outputLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canManageAudience}" />
												<h:commandLink
													id="lnkManageAudience" styleClass="free_area0"
													action="#{AudienceBean.manageAudience}"
													rendered="#{ViewItemFull.canManageAudience}">
													<h:outputText value="#{lbl.AudiencePage}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator" rendered="false" />
											<h:outputLink
												id="lnkCollaboratorPage" styleClass="free_area0"
												value="#{ApplicationBean.appContext}CollaboratorPage.jsp"
												rendered="false">
												<h:outputText value="#{lbl.CollaboratorPage}" />
											</h:outputLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canShowItemLog}" />
											<h:commandLink
												id="lnkViewItemLogPage" styleClass="free_area0"
												action="#{ViewItemFull.showItemLog}"
												rendered="#{ViewItemFull.canShowItemLog}">
												<h:outputText value="#{lbl.ViewItemLogPage}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canShowStatistics}" />
											<h:commandLink
												id="lnkViewItemFull_btnItemStatistics" styleClass="free_area0"
												action="#{ViewItemFull.showStatistics}"
												rendered="#{ViewItemFull.canShowStatistics}">
												<h:outputText value="#{lbl.ViewItemFull_btnItemStatistics}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canShowRevisions}" />
											<h:commandLink
												id="lnkViewItemFull_btnItemRevisions" styleClass="free_area0"
												action="#{ViewItemFull.showRevisions}"
												rendered="#{ViewItemFull.canShowRevisions}">
												<h:outputText value="#{lbl.ViewItemFull_btnItemRevisions}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canShowReleaseHistory}" />
											<h:commandLink
												id="lnkViewItemFull_btnItemVersions" styleClass="free_area0"
												action="#{ViewItemFull.showReleaseHistory}"
												rendered="#{ViewItemFull.canShowReleaseHistory}">
												<h:outputText value="#{lbl.ViewItemFull_btnItemVersions}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator" />
											
											<h:outputLink id="lnkViewItemPage" styleClass="free_area0"
												value="#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}viewItemFullPage.jsp?itemId=#{ViewItemFull.pubItem.version.objectIdAndVersion}">
												<h:outputText value="#{lbl.ViewItemFull_btnItemView}" />
											</h:outputLink>
											<h:panelGroup styleClass="seperator" />
											<h:outputLink id="lnkViewItemOverviewPage" 
												styleClass="free_area0 actual" 
												value="#contentSkipLinkAnchor">
												<h:outputText value="#{lbl.ViewItemOverview_lblLinkOverviewPage}" />
											</h:outputLink>
											<h:panelGroup styleClass="seperator" />
										</span>
									</div>
									<!-- Item control information ends here -->

									<!-- Paginator starts here -->
									<h:panelGroup styleClass="full_area0 pageBrowserItem">
										<h:panelGroup styleClass="paginatorPanel">
											<h:commandLink id="btList_lkFirstListItem"
												styleClass="min_imgBtn skipToFirst"
												action="#{PubItemListSessionBean.firstListItem}"
												rendered="#{PubItemListSessionBean.hasPreviousListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">&#160;</h:commandLink>
											<h:commandLink id="btList_lkPreviousListItem"
												styleClass="backward"
												action="#{PubItemListSessionBean.previousListItem}"
												rendered="#{PubItemListSessionBean.hasPreviousListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">
												<h:outputText value="#{lbl.List_lkPrevious}" />
											</h:commandLink>
											<h:commandLink id="btList_lkNextListItem" styleClass="forward"
												action="#{PubItemListSessionBean.nextListItem}"
												rendered="#{PubItemListSessionBean.hasNextListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">
												<h:outputText value="#{tip.List_lkNext}" />
											</h:commandLink>
											<h:commandLink id="btList_lkLastListItem"
												styleClass="min_imgBtn skipToLast"
												action="#{PubItemListSessionBean.lastListItem}"
												rendered="#{PubItemListSessionBean.hasNextListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">&#160;</h:commandLink>
										</h:panelGroup>
										<h:panelGroup styleClass="gotoBox"
											rendered="#{(PubItemListSessionBean.hasNextListItem or PubItemListSessionBean.hasPreviousListItem) and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">
											<h:inputText id="inputItemListPosition"
												styleClass="tiny_txtInput"
												value="#{PubItemListSessionBean.listItemPosition}"
												label="GoTo Box" />
											<h:outputLabel id="lblItemListPosition" styleClass="free_label"
												value="#{lbl.ItemList_of} " />
											<h:outputLabel id="lblChangeItemListPosition"
												styleClass="free_label"
												value="#{PubItemListSessionBean.totalNumberOfElements}" />
											<h:commandButton id="btnGoToItemListPosition"
												styleClass="xTiny_txtBtn pageChangeHiddenBtn"
												value="#{lbl.List_btGo}" title="#{lbl.List_btGo}"
												action="#{PubItemListSessionBean.listItemPosition}" />
										</h:panelGroup>
									</h:panelGroup>
									<!-- Paginator ends here -->
									<!-- ItemView starts here -->
									<h:panelGroup layout="block" styleClass="full_area0" >
										<ui:include src="viewItemOverview/titleGroup.jspf" />
										<h:panelGroup layout="block" styleClass="huge_area0 xTiny_marginRExcl small_marginLExcl" style="overflow:visible;">
											<ui:include src="viewItemOverview/authorGroup.jspf" />
											<ui:include src="viewItemOverview/externalResourceGroup.jspf" />
										</h:panelGroup>
										<h:panelGroup layout="block" styleClass="third_area0">
											<ui:include src="viewItemOverview/fulltextGroup.jspf" />
											<ui:include src="viewItemOverview/supplementaryMaterialGroup.jspf" />
											<ui:include src="viewItemOverview/citationGroup.jspf" />
											<ui:include src="viewItemOverview/abstractGroup.jspf" />
										</h:panelGroup>
									</h:panelGroup>
									<!-- ItemView ends here -->
								</div>
							</h:panelGroup>
							<!-- end: content section -->
						</div>
						</h:form>
					</div>
					<ui:include src="footer/Footer.jspf" />
				
			
			<script language="javascript" type="text/javascript">
				var citationStyleSuggestURL = '<h:outputText value="#{AdvancedSearchEdit.suggestConeUrl}"/>citation-styles/query';
				var citationStyleSuggestBaseURL = '$1?format=json';
				
				$(document).ready(function () {
					startNanoScrollerWhenLoaded();
					// enable overflow of links on mouseover
					$('.tile_category a').mouseenter(function (evt) {
					    $(this).parent().css({"overflow": "visible"})
					});

					$('.tile_category a').mouseleave(function (evt) {
					    $(this).parent().css({"overflow": "hidden"})
					});
					// Try to replace standard author images with CoNE-images
					replaceAuthorImage();
					
					checkUpdateCslUi(); 
				});
				// NanoScroller
				var counter = 0;
				var startNanoScrollerTimeout;
				// Add NanoScroller (Scrollbar only visible when hovering the marked div)
				function startNanoScrollerWhenLoaded () {
					clearTimeout(startNanoScrollerTimeout);
					switch (typeof $.fn.nanoScroller) {
						case 'function':
							var nanoDiv = $(".nano");
							nanoDiv.nanoScroller();
							break;
						default:
							counter++;
							if (counter &lt; 10) {
								startNanoScrollerTimeout = setTimeout(startNanoScrollerWhenLoaded, 100);		
							}
							break;
					}
				}
				
				// tries to replace the standard author image with the cone image.
				function replaceAuthorImage() {
					var url;
					var jsonRequestUrl;
					var imgElement;
					$('.mpgAuthorId').each(function (index) {
						url = $(this).text();
						jsonRequestUrl = url+'?format=json';
						imgElement = $(this).parent().find('img').get(0);
					    updateImage(imgElement, jsonRequestUrl);
						
					});
				}
				
				// JSon request to CoNE (works only if CoNE is on the same server as PubMan [Cross-site-scripting])
				// !DOES NOT WORK LOCALLY! (Cross-site-scripting)
				function updateImage(imgElement, jsonRequestUrl) {	
					$.getJSON(jsonRequestUrl, function (result) {
						var pictureUrl =  result.http_xmlns_com_foaf_0_1_depiction;
						if (pictureUrl != undefined &amp;&amp; $.trim().pictureUrl != '') {
							$(imgElement).attr('src', pictureUrl);
						}
					});
				}
				
				function checkUpdateCslUi() {
					(typeof updateCslUi == 'function') ?	updateCslUi() :	setTimeout("checkUpdateCslUi()", 30);
				}
				
			</script>
			</body>
		</html>
	</f:view>

