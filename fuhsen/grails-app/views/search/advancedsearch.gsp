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
<html>
<head>
<title><g:message encodeAs="html" code="ddbnext.AdvancedSearch" /> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>

<meta name="page" content="advancedsearch" />
<meta name="layout" content="main" />

</head>
<body>
  <div class="row advanced-search">
      <div class="span12">
          <div class="row heading">
            <div class="fl"><h1><g:message encodeAs="html" code="ddbnext.AdvancedSearch"/></h1></div>
            <ddbcommon:renderInfoTooltip messageCode="ddbnext.AdvancedSearch_Hint" infoId="search-advanced" infoDir="help" />
          </div>
          <div class="row">
              <div class="span12">
                  <g:form method="post" id="advanced-search-form" url="[controller:'advancedsearch', action:'executeSearch']" >
                      <div class="row">
                          <div class="span12">
                              <g:set var="group" value="${0}"/>
                              <g:while test="${group < searchGroupCount}">
                                <fieldset>
                                    <g:render template="/search/advancedsearchgroup" /><%group++%>
                                </fieldset>
                              </g:while>
                              <fieldset>
                                <div class="row operator global-operator">
                                  <div class="span12 button-group">
                                      <label for="operator" ><g:message encodeAs="html" code="ddbnext.AdvancedSearch_AllGroupsOperator_MatchLabel"/></label>
                                      <select id="operator" name="operator">
                                          <option value="OR"><g:message encodeAs="html" code="ddbnext.AdvancedSearchGlobalOperator_AnyGroups"/></option>
                                          <option value="AND"><g:message encodeAs="html" code="ddbnext.AdvancedSearchGlobalOperator_AllGroups"/></option>
                                      </select>
                                      <div class="fr add-group-link" title="<g:message encodeAs="html" code="ddbnext.AdvancedSearch_AddGroupButton_Title"/>">
                                        <a href="#"></a>
                                      </div>
                                      <button type="button" class="add-group-button fr" style="display: none" title="<g:message encodeAs="html" code="ddbnext.AdvancedSearch_AddGroupButton_Title"/>">
                                        <g:message encodeAs="html" code="ddbnext.AdvancedSearch_AddGroupButton_Title"/>
                                      </button>
                                  </div>
                                </div>
                              </fieldset>
                              <fieldset id="initialAddGroupButtonFieldSet">
                                <div class="row bb">
                                  <div class="span12 button-group">
                                      <div class="fr add-group-link" title="<g:message encodeAs="html" code="ddbnext.AdvancedSearch_AddGroupButton_Title"/>">
                                        <a href="#"></a>
                                      </div>
                                      <button type="button" class="add-group-button fr" style="display: none" title="<g:message encodeAs="html" code="ddbnext.AdvancedSearch_AddGroupButton_Title"/>">
                                        <g:message encodeAs="html" code="ddbnext.AdvancedSearch_AddGroupButton_Title"/>
                                      </button>
                                  </div>
                                </div>
                              </fieldset>
                              <fieldset>
                                <div class="row">
                                  <div class="span6 button-group fr">
                                    <button class="reset grey" type="reset"><span><g:message encodeAs="html" code="ddbnext.Reset"/></span></button>
                                    <button class="submit" type="submit"><span><g:message encodeAs="html" code="ddbnext.SearchButton"/></span></button>
                                  </div>
                                </div>
                              </fieldset>
                          </div>
                      </div>
                  </g:form>
              </div>
          </div>   
      </div>
  </div>
</body>
</html>
