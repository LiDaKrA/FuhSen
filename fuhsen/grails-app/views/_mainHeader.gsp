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
<g:set var="config" bean="configurationService" />
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
			<button type="button" class="btn btn-nav" data-toggle="collapse"
				data-target=".nav-collapse">
				<span class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar hidden"></span>
			</button>
			<g:link uri="/" class="brand"
				title="${message(code: 'ddbnext.Logo_Title')}" tabindex="-1">
				<r:img dir="images" file="mobileLogo.png"
					alt="${message(code: 'ddbnext.Logo_Description')}" />
			</g:link>
			<div class="nav-collapse collapse">
				<ul class="nav nav-list">
					<li><g:form class="navbar-search pull-left" method="get"
							role="search" id="form-search-header-mobile"
							url="[controller:'search', action:'loadingResults']">
							<input type="search" class="query" name="query"
								placeholder="<g:message encodeAs="html" code="ddbnext.SearchPlaceholder"/>"
								value="<ddbcommon:getCookieFieldValue fieldname="query" />">
							<button type="submit">
								<g:message encodeAs="html" code="ddbnext.Go_Button" />
							</button>
						</g:form></li>

					<!-- AboutUs goes here  -->
					<li class="highlight"><a> ${message(code: 'ddbnext.ChangeLanguage').toUpperCase()}
					</a>
						<ul class="nav">
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
						</ul></li>

					<li
						class="highlight <ddb:isMappingActive context="${params}"
            testif="${[[controller: "content", dir: "helpEmpty"]]}">active</ddb:isMappingActive>">
						<g:link controller="content" params="[dir: 'helpEmpty']">
							${message(code: 'ddbnext.Help').toUpperCase()}
						</g:link>
						<hr />
					</li>
					<!-- /end of help -->

					<!-- Entdecken goes here  -->

					<%-- 
          <ddbcommon:isLoggedIn>
              <li>
                <g:link controller="favoritesview" action="favorites"><g:message encodeAs="html" code="ddbnext.MyDDB" /></g:link>
                <ul class="nav">
                  <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "favoritesview", action: "favorites"]]}">active</ddb:isMappingActive>">
                    <g:link controller="favoritesview" action="favorites"><g:message encodeAs="html" code="ddbnext.Favorites" /></g:link>
                  </li>
                  <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "user", action: "getSavedSearches"]]}">active</ddb:isMappingActive>">
                    <g:link controller="user" action="getSavedSearches"><g:message encodeAs="html" code="ddbnext.Searches" /></g:link>
                  </li>
                  <li class="<ddb:isMappingActive context="${params}" testif="${[[controller: "user", action: "profile"],[controller: "user", action: "confirmationPage"],[controller: "user", action: "showApiKey"]]}">active</ddb:isMappingActive>">
                    <g:link controller="user" action="profile"><g:message encodeAs="html" code="ddbcommon.Profile" /></g:link>
                  </li>
                </ul>
              </li>
            </ddbcommon:isLoggedIn>
          --%>
					<!-- Language selector goes here -->

					<%-- 
          <li class="highlight">
            <ddbcommon:isNotLoggedIn>
              <g:link class="login-link login-link-referrer" controller="user" params="${[referrer:grailsApplication.mainContext.getBean('de.ddb.common.GetCurrentUrlTagLib').getCurrentUrl()]}"> ${message(code: 'ddbcommon.Login_Button').toUpperCase()}</g:link>
            </ddbcommon:isNotLoggedIn>
            <ddbcommon:isLoggedIn>
              <g:link controller="user" action="doLogout"><g:message encodeAs="html" code="ddbcommon.Logout" /> (<ddbcommon:getUserName />)</g:link>
            </ddbcommon:isLoggedIn>
          </li>
          --%>
				</ul>
			</div>
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
          			<div class="span7">
          				<g:link controller="index">
							<r:img dir="images" file="logoBig2.png" alt="${message(code: 'ddbnext.Logo_Description')}" />
						</g:link>
          			</div>
          			<div class="span5 toolbar">
          				<!-- Anmelden & language selector go here  -->
						<div class="search-header hidden-phone">
								<form method="get" role="search" id="form-search-header"
									action="<ddb:getSearchUrl controllerName="${controllerName}" actionName="${actionName}"/>">
									<label for="search-small"> <span><g:message
											encodeAs="html" code="ddbnext.Search_text_field" /></span>
									</label> <input type="hidden" id="querycache"
										value="<ddbcommon:getCookieFieldValue fieldname="query" />" />
									<input type="search" id="search-small" class="query"
										name="query"
										placeholder="<g:message encodeAs="html" code="ddbnext.SearchPlaceholder"/>"
										value="<ddbcommon:getCookieFieldValue fieldname="query" />"
										autocomplete="off" />
									<button type="submit">
										<!--[if !IE]><!-->
										<g:message encodeAs="html" code="ddbnext.Go_Button" />
										<!--<![endif]-->
										<!--[if gt IE 8]>
                        				<g:message encodeAs="html" code="ddbnext.Go_Button"/>
                      					<![endif]-->
									</button>
									<%--<div class="search-small-bottom">
										<div class="keep-filters off">
											<label class="checkbox"> <input id="keep-filters"
												type="checkbox" name="keepFilters"
												<g:if test="${keepFiltersChecked}">checked="checked"</g:if> />
												<g:message encodeAs="html" code="ddbnext.Keep_filters" />
											</label>
										</div>
									<g:if
										test="${hideAdvancedSearch  == null || hideAdvancedSearch == 'false'}">
										<g:link class="link-adv-search" controller="advancedsearch">
											<g:message encodeAs="html" code="ddbnext.AdvancedSearch" />
										</g:link>
									</g:if>--%>
									</div>
								</form>
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
