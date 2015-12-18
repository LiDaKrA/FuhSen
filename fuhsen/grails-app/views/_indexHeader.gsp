<%--
Copyright (C) 2014 FIZ Karlsruhe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<div class="print-logo off">
  <r:img dir="images" file="logoHeaderSmall.png" alt="" />
</div>

<!--[if lt IE 9]>
  <div class="header" role="contentinfo">
<![endif]-->

<!--[if !IE]><!-->
<g:set var="config" bean="configurationService"/>
<%-- 
<div class="cookie-notice visible" id="cookie-notice">
  <div class="container">
    <div class="row">
      <div class="span12">
        <p>
          <g:message code="ddbcommon.Cookie_Acceptance" args="${[createLink(controller: 'content', params: [dir:'privacy'])]}"/>
        </p>
        <a class="close" aria-controls="cookie-notice"></a>
      </div>
    </div>
  </div>
</div>
--%>
<header class="navbar navbar-fixed-top visible-phone">
  <div class="navbar-inner">
    <div class="container">
    </div>
  </div>
</header>
<header class="hidden-phone">
<!--<![endif]-->

<!--[if IE]>
<header class="ie-mobile">
<![endif]-->

  <h1 class="invisible-but-readable">
    <g:message encodeAs="html" code="ddbnext.Heading_Header" />
  </h1>
  <div class="container">
    <div class="row" id="header-main-row">
      <!--[if lt IE 9]>
          <div class="nav widget span12" data-widget="NavigationWidget">
        <![endif]-->
   <nav class="widget span12" data-widget="NavigationWidget">
					<div class="row">
						<div class="span7"></div>
						<div class="span5 toolbar">
							<div class="status-bar">
								<div class="language-wrapper">

									<ddb:isCurrentLanguage locale="en">
										<a href="#"><g:message encodeAs="html"
												code="ddbnext.language_en" /></a>
									</ddb:isCurrentLanguage>
									<ddb:isCurrentLanguage locale="de">
										<a href="#"><g:message encodeAs="html"
												code="ddbnext.language_de" /></a>
									</ddb:isCurrentLanguage>
									<div class="arrow-container">
										<div class="arrow-up"></div>
									</div>

									<ul class="selector language">
										<li
											<ddb:isCurrentLanguage locale="de"> class="selected-language"</ddb:isCurrentLanguage>>
											<ddb:getLanguageLink params="${params}" locale="de"
												islocaleclass="nopointer">
												<g:message encodeAs="html" code="ddbnext.language_de" />
											</ddb:getLanguageLink>
										</li>
										<li
											<ddb:isCurrentLanguage locale="en"> class="selected-language"</ddb:isCurrentLanguage>>
											<ddb:getLanguageLink params="${params}" locale="en"
												islocaleclass="nopointer">
												<g:message encodeAs="html" code="ddbnext.language_en" />
											</ddb:getLanguageLink>
										</li>

									</ul>
								</div>
							</div>
						</div>
					</div>
				</nav>
      <!--[if lt IE 9]>
        </div>
      <![endif]-->
    </div>
  </div>
</header>
<!--[if lt IE 9]>
  </div>
<![endif]-->

