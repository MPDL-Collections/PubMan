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
			<f:loadBundle var="lbl" basename="Label"/>
			<f:loadBundle var="msg" basename="Messages"/>
			<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<h:head>
				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<link rel="unapi-server" type="application/xml" title="unAPI" href="${MyItemsRetrieverRequestBean.unapiURLview}"/>

				<ui:include src="header/ui/StandardImports.jspf" />
				
				<h:outputStylesheet name="commonJavaScript/jquery/css/jquery-ui-1.10.4.min.css"/>
				<h:outputScript name="commonJavaScript/jquery/jquery-ui-1.10.4.min.js" />
				<script src="/cone/js/jquery.suggest.js" />
				<h:outputScript name="commonJavaScript/componentJavaScript/autoSuggestFunctions.js" />
			</h:head>
			<body lang="${InternationalizationHelper.locale}">
			<h:outputText value="#{MyItemsRetrieverRequestBean.beanName}" styleClass="noDisplay" rendered="#{LoginHelper.loggedIn}"/>
			<h:outputText value="#{DepositorWSPage.beanName}" styleClass="noDisplay" rendered="#{LoginHelper.loggedIn}"/>
			
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
			
				<ui:include src="header/Header.jspf" />
				<h:form >
				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							
						<ui:include src="header/Breadcrumb.jspf" />
				
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1><h:outputText value="#{lbl.DepositorWSPage}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
							<!-- content menu starts here -->
								<div class="free_area0 sub">
								<!-- content menu upper line starts here -->
									<h:commandLink id="lnkChangeSubmenuToView" title="#{tip.List_lblViewOptions}" styleClass="free_area0" value="#{lbl.List_lblViewOptions}" action="#{PubItemListSessionBean.changeSubmenuToView}" rendered="#{PubItemListSessionBean.subMenu != 'VIEW'}" onclick="fullItemReloadAjax();" />
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblViewOptions}" rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}" />
									
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkChangeSubmenuToFilter" title="#{tip.List_lblFilterOptions}" styleClass="free_area0" value="#{lbl.List_lblFilterOptions}" action="#{PubItemListSessionBean.changeSubmenuToFilter}" rendered="#{PubItemListSessionBean.subMenu != 'FILTER'}" onclick="fullItemReloadAjax();"/>
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblFilterOptions}" rendered="#{PubItemListSessionBean.subMenu == 'FILTER'}" />
									
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkChangeSubmenuToSorting" title="#{tip.List_lblSortOptions}" styleClass="free_area0" value="#{lbl.List_lblSortOptions}" action="#{PubItemListSessionBean.changeSubmenuToSorting}" rendered="#{PubItemListSessionBean.subMenu != 'SORTING'}" onclick="fullItemReloadAjax();"/>	
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblSortOptions}" rendered="#{PubItemListSessionBean.subMenu == 'SORTING'}" />
									
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkChangeSubmenuToExport" title="#{tip.List_lblExportOptions}" styleClass="free_area0" value="#{lbl.List_lblExportOptions}" action="#{PubItemListSessionBean.changeSubmenuToExport}" rendered="#{PubItemListSessionBean.subMenu != 'EXPORT'}" onclick="fullItemReloadAjax();"/>	
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblExportOptions}" rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}" />
									
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkAddSelectedToCart" title="#{tip.List_lblAddToBasket}" styleClass="free_area0" value="#{lbl.List_lblAddToBasket}" action="#{PubItemListSessionBean.addSelectedToCart}" onclick="fullItemReloadAjax();"/>
									
								<!-- content menu upper line ends here -->
								</div>
								<!-- content menu lower line starts here -->
								<h:panelGroup layout="block" styleClass="xHuge_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}">
									
									<h:panelGroup layout="block" styleClass="xLarge_area1 endline selectContainer">
										<h:panelGroup layout="block" styleClass="xLarge_area0">
											<h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="selExportFormatName" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.exportFormatName}" onchange="$(this).parents('.sub').find('.exportUpdateButton').click();">
											<f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}" />
										</h:selectOneMenu>
									</h:panelGroup>
									
									<h:commandButton id="btnUpdateExportFormats" title="#{tip.export_btFormat}" styleClass="noDisplay exportUpdateButton" action="#{ExportItems.updateExportFormats}" value="updateExportFormats" />	
									
									<h:panelGroup layout="block" styleClass="medium_area1 endline selectContainer" rendered="#{ExportItemsSessionBean.enableFileFormats}">
										<h:panelGroup layout="block" styleClass="medium_area0">
											<h:panelGroup styleClass="medium_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="selFileFormat" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.fileFormat}" onchange="updateSelectionBox(this);">
											<f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}" />
										</h:selectOneMenu>
									</h:panelGroup>
									<h:commandLink title="#{tip.export_btDownload}" id="btnExportDownload" styleClass="free_area0 xTiny_marginLExcl" value="#{lbl.export_btDownload}" action="#{PubItemListSessionBean.exportSelectedDownload}"/>
									<h:outputText styleClass="seperator" />
									<h:commandLink title="#{tip.export_btEMail}" id="btnExportEMail" styleClass="free_area0" value="#{lbl.export_btEMail}" action="#{PubItemListSessionBean.exportSelectedEmail}"/>
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
								<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}">
								<!-- content menu lower line starts here -->
									<h:commandLink id="lnkChangeListTypeToBib" title="#{tip.List_lblBibList}" styleClass="free_area0" rendered="#{PubItemListSessionBean.listType == 'GRID'}" action="#{PubItemListSessionBean.changeListTypeToBib}" onclick="fullItemReloadAjax();">
										<h:outputText value="#{lbl.List_lblBibList}" />
									</h:commandLink>
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblBibList}" rendered="#{PubItemListSessionBean.listType == 'BIB'}" />
									<h:outputText styleClass="seperator" />
									<h:commandLink id="lnkChangeListTypeToGrid" title="#{tip.List_lblGridList}" styleClass="free_area0" rendered="#{PubItemListSessionBean.listType == 'BIB'}"  action="#{PubItemListSessionBean.changeListTypeToGrid}" onclick="fullItemReloadAjax();">
										<h:outputText value="#{lbl.List_lblGridList}" />
									</h:commandLink>
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblGridList}" rendered="#{PubItemListSessionBean.listType == 'GRID'}" />
								<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="quad_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'FILTER'}">
								<!-- content menu lower line starts here -->
									<h:outputText styleClass="medium_label" value="#{lbl.ENUM_CRITERIA_STATE}"/>
									
									<h:panelGroup layout="block" styleClass="xDouble_area1 endline selectContainer">
										<h:panelGroup layout="block" styleClass="xDouble_area0">
											<h:panelGroup styleClass="xDouble_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="cboItemstate" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{MyItemsRetrieverRequestBean.selectedItemState}" onchange="$(this).parents('div').find('.changeState').click();">
											<f:selectItems id="selectItems" value="#{MyItemsRetrieverRequestBean.itemStateSelectItems}" />
										</h:selectOneMenu>
									</h:panelGroup>
									<h:commandButton id="btnChangeItemState" title="#{tip.list_btChangeState}" styleClass="noDisplay changeState" value=" "  action="#{MyItemsRetrieverRequestBean.changeItemState}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="quad_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'FILTER'}">	
									<h:outputText styleClass="medium_label clearLeft" value="#{lbl.qaws_lblMultipleImportTags}"/>
									
									<h:panelGroup layout="block" styleClass="xDouble_area1 endline selectContainer">
										<h:panelGroup layout="block" styleClass="xDouble_area0">
											<h:panelGroup styleClass="xDouble_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="selSelectedImport" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{MyItemsRetrieverRequestBean.selectedImport}" onchange="$(this).parents('div').find('.changeImport').click();">
											<f:selectItems value="#{MyItemsRetrieverRequestBean.importSelectItems}" />
										</h:selectOneMenu>
									</h:panelGroup>
				
									<h:commandButton id="btnChangeImport" styleClass="noDisplay changeImport" action="#{MyItemsRetrieverRequestBean.changeImport}" value="change import"/>
								<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'SORTING'}">
								<!-- content menu lower line starts here -->
									<h:outputText styleClass="medium_label" value="#{lbl.ItemList_SortBy}"/>
									
									<h:panelGroup layout="block" styleClass="xLarge_area1 endline selectContainer">
										<h:panelGroup layout="block" styleClass="xLarge_area0">
											<h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="sortBy" onfocus="updateSelectionBox(this);" value="#{PubItemListSessionBean.selectedSortBy}" onchange="$(this).parents('div').find('.changeSortBy').click();">
											<f:selectItems value="#{PubItemListSessionBean.sortBySelectItems}" />
										</h:selectOneMenu>
									</h:panelGroup>
									<h:commandLink title="#{tip.list_ascending}" styleClass="ascSort xTiny_marginLExcl" value="#{lbl.ItemList_SortOrderAscending}" id="sortOrderAsc" rendered="#{PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}" action="#{PubItemListSessionBean.changeSortOrder}" onclick="fullItemReloadAjax();"/>
									<h:commandLink title="#{tip.list_descending}" styleClass="desSort xTiny_marginLExcl" value="#{lbl.ItemList_SortOrderDescending}" id="sortOrderDesc" rendered="#{!PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}" action="#{PubItemListSessionBean.changeSortOrder}" onclick="fullItemReloadAjax();"/>
									<h:commandButton id="btnChangeSortBy" styleClass="noDisplay changeSortBy" value=" "  action="#{PubItemListSessionBean.changeSortBy}"/>
								<!-- content menu lower line ends here -->
								</h:panelGroup>


							<!-- content menu ends here -->
							</div>
							<div class="subHeader">
								<h:outputText value="#{PubItemListSessionBean.totalNumberOfElements} #{lbl.SearchResultList_lblItems}"/>
								<h:outputText value=" ("/>
								<h:outputText value="#{lbl.ENUM_CRITERIA_STATE} &#34;#{MyItemsRetrieverRequestBean.selectedItemStateLabel}&#34;, " rendered="#{MyItemsRetrieverRequestBean.selectedItemState != null and MyItemsRetrieverRequestBean.selectedItemState != 'all'}"/>
 								<h:outputText value="#{lbl.ENUM_SORTORDER_ASCENDING} #{lbl.SearchResultList_lblSortedBy} #{PubItemListSessionBean.selectedSortByLabel}" rendered="#{PubItemListSessionBean.isAscending}"/>
								<h:outputText value="#{lbl.ENUM_SORTORDER_DESCENDING} #{lbl.SearchResultList_lblSortedBy} #{PubItemListSessionBean.selectedSortByLabel}" rendered="#{!PubItemListSessionBean.isAscending}"/>
								<h:outputText value=")"/>	
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{MyItemsRetrieverRequestBean.numberOfMessages == 1}"/>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{MyItemsRetrieverRequestBean.hasErrorMessages and MyItemsRetrieverRequestBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{MyItemsRetrieverRequestBean.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{MyItemsRetrieverRequestBean.hasMessages and !MyItemsRetrieverRequestBean.hasErrorMessages and MyItemsRetrieverRequestBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{MyItemsRetrieverRequestBean.hasMessages}"/>
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<h:panelGroup layout="block" styleClass="full_area0" rendered="#{PubItemListSessionBean.listType == 'BIB' and PubItemListSessionBean.partListSize>0}">
						<ui:include src="list/itemList.jspf" />
					</h:panelGroup>
					<h:panelGroup layout="block" styleClass="full_area0" rendered="#{PubItemListSessionBean.listType == 'GRID' and PubItemListSessionBean.partListSize>0}">
						<ui:include src="list/gridList.jspf" />
					</h:panelGroup>
					<h:panelGroup styleClass="full_area0" rendered="#{PubItemListSessionBean.partListSize==0}">
						<h:outputText styleClass="free_area0 small_marginLExcl" value="#{msg.depositorWS_valNoItemsMsg}"/>
					</h:panelGroup>
				<!-- end: content section -->
				</div>
				</h:form>
			</div>
			
			<ui:include src="footer/Footer.jspf" />
			
			<script type="text/javascript">
			//<![CDATA[
				var citationStyleSuggestURL = '#{AdvancedSearchEdit.suggestConeUrl}citation-styles/query';
				var citationStyleSuggestBaseURL = '$1?format=json';
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});
					checkUpdateCslUi(); 
				});
				
				function checkUpdateCslUi() {
					(typeof updateCslUi == 'function') ?	updateCslUi() :	setTimeout("checkUpdateCslUi()", 30);
				}
				//]]>
			</script>
			</body>
		</html>
	</f:view>
