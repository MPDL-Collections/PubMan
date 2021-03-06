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


	
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		<table  xmlns="http://www.w3.org/1999/xhtml">
		<tbody>
		<tr class="full_area0 listItem">
	      	<td class="free_area0 endline">
	      		<span class="tiny_area0">
					&#160;
	      		</span>
	      	</td>
	      	<td class="free_area0 endline status">
	      		<h:panelGroup styleClass="seperator"></h:panelGroup>
	      		<h:panelGroup styleClass="free_area0 endline statusArea">
					<h:panelGroup layout="block" styleClass="big_imgArea statusIcon ajaxedImport #{ImportData.import.status} import#{ImportData.import.status}#{ImportData.import.errorLevel}" />
					<h:outputLabel id="lblErrorLevel" styleClass="free_area0_p3 medium_label endline" title="#{ImportData.import.errorLevel}">
						<h:panelGroup rendered="#{!ImportData.import.finished}">
							<h:outputText value="#{ImportData.import.percentage}"/>% - 
						</h:panelGroup>
						<h:outputText value="#{ImportData.import.status}"/>	
					</h:outputLabel>
					<h:inputHidden id="inpImportLogLink" value="#{ImportData.import.logLink}" />
				</h:panelGroup>
	      	</td>
	      	<td class="free_area0 endline">
	      		<h:panelGroup styleClass="seperator"></h:panelGroup>
	      		<span class="large_area0_p8">
					<h:outputLink id="lnkImportMyItems" value="#{ImportData.import.myItemsLink}" rendered="#{ImportData.import.importedItems}">
						<h:outputText value="#{ImportData.import.message}"/>
					</h:outputLink>
					<h:outputText value="#{ImportData.import.message}" rendered="#{!ImportData.import.importedItems}"/>
	      		</span>
	      	</td>
	      	<td class="free_area0 endline">
	      		<h:panelGroup styleClass="seperator"></h:panelGroup>
	      		<span class="large_area0_p8">
	      			<h:outputText value="#{ImportData.import.format}"/>&#160;
	      		</span>
	      	</td>
	      	<td class="free_area0 endline">
	      		<h:panelGroup styleClass="seperator"></h:panelGroup>
	      		<span class="large_area0_p8">
		      		<h:outputText value="#{ImportData.import.startDateFormatted}"/>&#160;
		      	</span>
		    </td>
	      	<td class="free_area0 endline">
	      		<h:panelGroup styleClass="seperator"></h:panelGroup>
	      		<span class="large_area0_p8">
	      			<h:outputText value="#{ImportData.import.endDateFormatted}"/>&#160;
	      		</span>
	      	</td>
	      	<td class="free_area0 endline">
	      		<h:panelGroup styleClass="seperator"></h:panelGroup>
	      		<span class="large_area0_p8 detailsLinkArea">
					<h:inputHidden id="inpImportItemsLink" value="#{ImportData.import.itemsLink}" />
					<a onclick="if(!$(this).parents('tr').next('tr').hasClass('importDetails')) {$(this).parents('tr').after(detailsAwaiting); $(this).parents('tr').next('.importDetails').find('td').load($(this).siblings('input').val())} else {$(this).parents('tr').next('.importDetails').remove();}">
							<b><h:outputText value="#{lbl.import_workspace_detailsView}"/></b>
 					</a>
	      		</span>
	      	</td>
			<td class="free_area0 endline">
	      		<h:panelGroup styleClass="seperator"></h:panelGroup>
	      		<span class="large_area0 endline">
					<h:panelGroup rendered="false" styleClass="large_area0_p8 noPaddingTopBottom endline">
						<h:outputText value="#{ImportData.import.errorLevel}"/>
					</h:panelGroup>
	      			<h:panelGroup rendered="#{ImportData.import.finished}">
						<h:outputLink styleClass="small_area0_p8 noPaddingTopBottom endline" title="#{tip.import_workspace_remove_import}" value="ImportWorkspaceRemove.jsp?id=#{ImportData.importId}">
							<h:outputText value="#{lbl.import_workspace_remove_import}"/>
						</h:outputLink>

						<h:outputLink styleClass="small_area0_p8 noPaddingTopBottom endline" title="#{tip.import_workspace_delete_items}" value="ImportWorkspaceDelete.jsp?id=#{ImportData.importId}" rendered="#{ImportData.import.importedItems}">
							<h:outputText value="#{lbl.import_workspace_delete_items}"/>
						</h:outputLink>

						<h:outputLink styleClass="large_area0_p8 noPaddingTopBottom endline"  title="#{tip.import_workspace_submit_items}" value="ImportWorkspaceSubmit.jsp?id=#{ImportData.importId}" rendered="#{ImportData.import.importedItems and !ImportData.simpleWorkflow and !LoginHelper.isModerator}">
							<h:outputText value="#{lbl.import_workspace_submit_items}"/>
						</h:outputLink>

						<h:outputLink styleClass="large_area0_p8 noPaddingTopBottom endline"  title="#{tip.import_workspace_submit_release_items}" value="ImportWorkspaceRelease.jsp?id=#{ImportData.importId}" rendered="#{ImportData.import.importedItems and (LoginHelper.isModerator or ImportData.simpleWorkflow)}">
							<h:outputText value="#{lbl.import_workspace_submit_release_items}"/>
						</h:outputLink>
					</h:panelGroup>
	      		</span>
	      	</td>
	    </tr>
	    </tbody>
	    </table>
	</f:view>
