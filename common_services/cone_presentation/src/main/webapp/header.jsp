<%--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 f�r wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur F�rderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
--%>


<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>CoNE - Control of Named Entities</title>
	<link href="<%= PropertyReader.getProperty("escidoc.pubman.common.presentation.url") %>resources/cssFramework/main.css" type="text/css" rel="stylesheet"/>

	<link href="<%= PropertyReader.getProperty("escidoc.pubman.stylesheet.contrast.url") %>" id="highContrastTheme" type="text/css" title="high contrast" rel="alternate stylesheet"/>
	<link href="<%= PropertyReader.getProperty("escidoc.pubman.stylesheet.classic.url") %>" id="classicTheme" type="text/css" title="classic" rel="alternate stylesheet"/>	
	<link href="<%= PropertyReader.getProperty("escidoc.pubman.stylesheet.standard.url") %>" id="PubManTheme" type="text/css" title="PubMan" rel="stylesheet"/>

	<script language="JavaScript" type="text/javascript">
		  function applyCookieStyle() {
				var cookieValue = ""
				var cookie = "layout=";
				var dc = document.cookie;
				if (dc.length > 0) {
					var start = dc.indexOf(cookie);
					if (start != -1) {
						start += cookie.length;
						var stop = dc.indexOf(";", start);
						if (stop == -1) stop = dc.length;
						cookieValue = unescape(dc.substring(start,stop));
					}
				}
				var enableHiddenShemes = false;
				cookie = "enableHiddenSchemes=";
				if (dc.length > 0) {
					var start = dc.indexOf(cookie);
					if (start != -1) {
						start += cookie.length;
						var stop = dc.indexOf(";", start);
						if (stop == -1) stop = dc.length;
						if(unescape(dc.substring(start,stop)) == 'true') {enableHiddenShemes = true;};
					}
				}
				if (cookieValue != "" && document.getElementsByTagName) {
					var el = document.getElementsByTagName("link");
					for (var i = 0; i < el.length; i++ ) {
						if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id") == cookieValue && enableHiddenShemes && (el[i].getAttribute("title") == null || el[i].getAttribute("title") == "" ) ) {
							el[i].setAttribute("title", el[i].getAttribute("id"));
						}
						if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id")) {
							el[i].disabled = true;
							if (el[i].getAttribute("id") == cookieValue) el[i].disabled = false;
						}
					}
				}
			}
		
			function setStyleCookie() {
				var cookieValue = "";
				if(document.getElementsByTagName) {
					var el = document.getElementsByTagName("link");
					for (var i = 0; i < el.length; i++ ) {
						var enabledCounter = 0;
						if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id") && el[i].getAttribute("title") && el[i].disabled == false && enabledCounter == 0) {
							cookieValue = el[i].getAttribute("id");
							enabledCounter++;
						}
					}
				}
				var now = new Date();
				var exp = new Date(now.getTime() + (1000*60*60*24*30));
				if(cookieValue != "") {
					document.cookie = "layout=" + escape(cookieValue) + ";" +
										"expires=" + exp.toGMTString() + ";" +
										"path=/";
				}
			}
			applyCookieStyle();
			window.onunload=function(e){setStyleCookie();};
		</script>

	<script type="text/javascript">

		var instanceUrl = '<%= PropertyReader.getProperty("escidoc.cone.service.url") %>';

		function remove(element)
		{
			
			var parent = $(element).parents(".inputField");
			var listSize = $(parent).parent().children(".inputField").length;

			alert(listSize);

			if (listSize > 1)
			{
				$(parent).remove();
			}
			else
			{
				$(parent).find("input[type='text']").each(function(){ $(this).val('');});
				$(element).remove();
			}
		}

		function add(element, predicate, hidden, lang)
		{
			var parent = $(element).parents('.itemLine:eq(0)');
			var singleItem = $(parent).find('.singleItem')[0];
			var lastItem = $(parent).find('.singleItem:last');

			var newItem = $(singleItem).clone().empty();
				newItem.append('<input name="'+ predicate +'" value="" type="hidden">');
				if (lang)
				{
					newItem.append('<input name="'+ predicate + '_lang'+'" value="" type="hidden">');
				}
				
			$(lastItem).after(newItem);

			element.form.submit();
			
		}

		function bindSuggest(element, model, cutId)
		{
			if (typeof pageLoaded != 'undefined' && pageLoaded)
			{
				if (typeof cutId != 'undefined' && cutId)
				{
					$('.' + element).suggest("<%= PropertyReader.getProperty("escidoc.cone.service.url") %>" + model + "/query?lang=en&format=json", {onSelect: fillSmallId});
				}
				else
				{
					$('.' + element).suggest("<%= PropertyReader.getProperty("escidoc.cone.service.url") %>" + model + "/query?lang=en&format=json", {onSelect: fillId});
				}
			}
			else
			{
				setTimeout('bindSuggest(\'' + element + '\', \'' + model + '\', ' + (typeof cutId != 'undefined' && cutId) + ')', 100);
			}
		};

		function fillSmallId()
		{
			$(this).val(this.resultID.substring(this.resultID.lastIndexOf('/') + 1));
		}
		
		function fillId()
		{
			var id = this.resultID.replace(/^.+\/(.+\/resource\/.+)$/, '$1');
			$(this).val(id);
		}

		function checkId(model, conf)
		{
			
			var subject;
			if (document.editform["cone_identifier"] != null)
			{
				subject = document.editform["cone_identifier"].value;
			}
			else
			{
				subject = document.editform["uri"].value;
			}
			
			if (typeof predicate == 'undefined')
			{
				if (subject != '')
				{
					var subject_prefix = document.editform["cone_subject_prefix"].value;
					$.getJSON(
							instanceUrl + subject_prefix + subject + '?format=json'
							, function(data) {
								if (data.id != null)
								{
									if (conf && confirm('This entry already exists!\nDo you want to edit the existing entry?'))
									{
										location.href = data.id.replace(instanceUrl, instanceUrl + 'edit.jsp?model=' + model + '&uri=');
									}
									else
									{
										//xLarge_txtInput errorMessageArea endline
										document.getElementById('idImage').src = 'img/taken.png';
										document.getElementById('cone_identifier').className = 'xLarge_txtInput errorMessageArea endline';
										document.getElementById('idInfo').style.visibility = 'visible';
										document.getElementById('idInfo').className = 'tiny_area0 tiny_marginRExcl inputInfoBox errorMessageArea';
									}
								}
								else
								{
									//xLarge_txtInput infoMessageArea endline
									document.getElementById('idImage').src = 'img/new.png';
									document.getElementById('idInfo').style.visibility = 'visible';
									document.getElementById('idInfo').className = 'tiny_area0 tiny_marginRExcl inputInfoBox infoMessageArea';
									document.getElementById('cone_identifier').className = 'xLarge_txtInput infoMessageArea endline';

								}
							}
					);
				}
				else
				{
					document.getElementById('idImage').src = 'img/empty.png';
					document.getElementById('idInfo').style.visibility = 'hidden';
					document.getElementById('cone_identifier').className = 'xLarge_txtInput';
				}
			}
		}

		function checkFields()
		{
			var fields = $.find('.checkImage');
			//var fields = $.find('.idInfo');
			$(fields).each(function(){
					this.init = false;
					this.click();
				}
			);
		}
		
		function checkField(element, model, predicate, formField, popup, shouldBeUnique)
		{
			if (typeof popup == 'undefined')
			{
				popup = false;
			}
			
			var subject = null;
			if (document.editform["cone_identifier"] != null)
			{
				subject = document.editform["cone_identifier"].value;
			}
			else if (document.editform["uri"] != null)
			{
				subject = document.editform["uri"].value;
			}
			var object = document.editform[formField].value;

			var image = $(element).parents('.inputField').find('.checkImage')[0];
			var input = $(element).parents('.inputField').find('.xLarge_txtInput')[0];
			var info = $(element).parents('.inputField').find('.inputInfoBox')[0];

			if (object != '')
			{

				var jsonUrl = instanceUrl + model + '/query?' + escape(predicate) + '="' + escape(object) + '"&format=json';

				$.getJSON(
						jsonUrl
						, function(data)
						{
							if (data.length > 0)
							{
								var counter = 0;
								for (var i = 0; i < data.length; i++)
								{
									var entry = data[i];
									if (subject == null || entry.id != instanceUrl + subject)
									{
										counter++;
									}
									else
									{
										// I found myself
									}
								}
								if (counter > 0 && shouldBeUnique)
								{
									input.className = 'xLarge_txtInput errorMessageArea endline';
									info.style.visibility = 'visible';
									info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox errorMessageArea';
									image.src = 'img/taken.png';
									var title;
									if (counter == 1)
									{
										title = 'another entry was';
									}
									else if (counter <=48) 
									{
										title = counter + ' other entries were';
									}
									else
									{
										title = 'many other entries were';
									}
									title = 'This field should usually be unique, but ' + title + ' found with the same content';
									image.title = title;
									info.title = title;
								}
								else if (counter > 0)
								{
									input.className = 'xLarge_txtInput successMessageArea endline';
									info.style.visibility = 'visible';
									info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox successMessageArea';
									image.src = 'img/hits.png';
									var title;
									if (counter <=48) 
									{
										title = counter;
									}
									else
									{
										title = 'Many';
									}
									title += ' other entries were found with the same content';
									image.title = title;
									info.title = title;
								}
								else
								{
									input.className = 'xLarge_txtInput infoMessageArea endline';
									info.style.visibility = 'visible';
									info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox infoMessageArea';
									info.title = 'This content is unique';
									image.src = 'img/new.png';
									image.title = 'This content is unique';
								}

								if (counter > 0 && popup && element.init)
								{
									var html = '<ul class="dialog">\n';
									for (var i = 0; i < data.length; i++)
									{
										if (subject == null || data[i].id != instanceUrl + subject)
										{
											html += '<li><a href="' + data[i].id + '" target="_blank">' + data[i].value + '</a>\n';
											html += '<a href="edit.jsp?model=' + model + '&uri=' + data[i].id.replace(instanceUrl, '') + '">[edit]</a>\n';
											html += '<a target="_blank" href="edit.jsp?model=' + model + '&uri=' + data[i].id.replace(instanceUrl, '') + '">[new window]</a></li>\n';
										}
									}
									html += '</ul>\n';
									
									showDialog(html);
									
								}
								else
								{
									element.init = true;
								}
							}
							else
							{
								input.className = 'xLarge_txtInput infoMessageArea endline';
								info.style.visibility = 'visible';
								info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox infoMessageArea';			
								image.src = 'img/new.png';
								image.title = jsonUrl + ' - ' + predicate + ' - ' + formField;
								info.title = jsonUrl + ' - ' + predicate + ' - ' + formField;
								image.title = 'This field is unique';
								info.title = 'This content is unique';
							}
						}
				);
			}
			else
			{
				input.className ='xLarge_txtInput';
				info.style.visibility = 'hidden';
				image.src = 'img/empty.png';
				image.title = '';
			}
		}

		function showDialog(html)
		{
			$('.messageArea').append(html);
			$('.messageArea').css('position', 'fixed');
			$('.messageArea').css('z-index', '2001');
			$('.messageArea').css('left', '722px');
			$('.messageArea').css('top', '139px');
			$('.messageArea').removeClass('noDisplay');
		}

		function closeDialog()
		{
			$('.dialog').remove();
			$('.messageArea').addClass('noDisplay');
		}
		
	</script>
	<script type="text/javascript" src="/cone/js/jquery-1.2.6.min.js">;</script>
	<script type="text/javascript" src="/cone/js/jquery.jdialog.min.js">;</script>
	<script type="text/javascript" src="/cone/js/jquery.dimensions.js">;</script>
	<script type="text/javascript" src="/cone/js/jquery.suggest.js">;</script>
	<link type="text/css" rel="stylesheet" href="/cone/js/jquery.suggest.css"/>
</head>