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




	
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}"  xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<h:head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>

				<ui:include src="header/ui/StandardImports.jspf" />

			</h:head>
			<body lang="${InternationalizationHelper.locale}">
			<h:outputText value="#{ErrorPage.beanName}" styleClass="noDisplay" />
			
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>

				<!-- import header -->
				<ui:include src="header/Header.jspf" />
				<h:form id="form1">
				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							
						<ui:include src="header/Breadcrumb.jspf" />
				
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1><h:outputText value="#{lbl.ErrorPage}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
							<!-- content menu starts here -->
								<div class="free_area0 sub">
								<!-- content menu upper line starts here -->
									&#160;
								<!-- content menu upper line ends here -->
								</div>
							<!-- content menu ends here -->
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
									&#160;
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<div class="full_area0">
						<div class="full_area0 infoPage">
							<span class="half_area0_p8 mainSection">

								<h2><h:outputText styleClass="messageError" value="#{lbl.ErrorPage_errorOccurred}"/></h2>

								<a onclick="$(this).siblings('pre').slideToggle('slow');"><h:outputText value="#{ErrorPage.summary}"/></a>
								<br/>
								<br/>
								<pre style="display: none;">
									<h:outputText value="#{ErrorPage.detail}"/>
								</pre>
								
							</span>					
						</div>	
					</div>
				</div>
				</h:form>

				</div>
				<ui:include src="footer/Footer.jspf" />
				
				<script type="text/javascript">
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop())});
				});
				</script>
			</body>
		</html>
	</f:view>

