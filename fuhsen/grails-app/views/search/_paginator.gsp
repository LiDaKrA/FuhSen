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
<%@page import="de.ddb.common.constants.SearchParamEnum"%>
<div class="script">
<div class="paginator-options-button hidden-phone">
    <button class="open-paginator-options" type="button" title="<g:message encodeAs="html" code="ddbnext.Search_Results_Display"/>"><g:message encodeAs="html" code="ddbnext.Search_Results_Display"/></button>
</div>
<div class="paginator-options-overlay">
  <div class="paginator-options-container">
    
    <div class="results-paginator-options">
      <div class="paginator-options-header">
        <g:message encodeAs="html" code="ddbnext.Search_Results_Display"/>
        <a href="#" title="<g:message code="ddbnext.BinaryViewer_Lightbox_CloseButton_Title"/>" class="close-overlay fr"></a>
      </div>
      <div class="paginator-options-body bt bb">
        <div class="sort-results-switch">
          <label><g:message encodeAs="html" code="ddbnext.SearchResultsPagination_Sort_By" /></label>
          <span>
            <select class="select">
              <option value="${SearchParamEnum.SORT_RELEVANCE.getName()}" <g:if test="${paginatorData.sortResultsSwitch == SearchParamEnum.SORT_RELEVANCE.getName()}">selected</g:if>><g:message encodeAs="html" code="ddbnext.Sort_RELEVANCE" /></option>
              <option value="${SearchParamEnum.SORT_ALPHA_ASC.getName()}" <g:if test="${paginatorData.sortResultsSwitch == SearchParamEnum.SORT_ALPHA_ASC.getName()}">selected</g:if>><g:message encodeAs="html" code="ddbnext.Sort_ALPHA_ASC" /></option>
              <option value="${SearchParamEnum.SORT_ALPHA_DESC.getName()}" <g:if test="${paginatorData.sortResultsSwitch == SearchParamEnum.SORT_ALPHA_DESC.getName()}">selected</g:if>><g:message encodeAs="html" code="ddbnext.Sort_ALPHA_DESC" /></option>
            </select>
          </span>
        </div>
        <div class="page-filter">
          <label><g:message encodeAs="html" code="ddbnext.SearchResultsPagination_Display" /></label>
          <span>
            <select class="select">
              <g:each in="${paginatorData.pageFilter}">
                <option value="${it}" <g:if test="${paginatorData.pageFilterSelected == it}">selected</g:if> >${it}</option>
              </g:each>
            </select>
          </span>
        </div>
      </div>
      <div class="paginator-options-footer">
        <div class="button"><g:message encodeAs="html" code="ddbcommon.facet_time_apply" /></div>
      </div>
    </div>
    
  </div>
</div>
</div>
