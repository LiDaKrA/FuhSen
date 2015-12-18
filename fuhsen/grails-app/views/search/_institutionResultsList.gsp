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
<%@page import="de.ddb.common.constants.Type"%>
<g:set var="pageHitCounter" value="0" />
<ul class="results-list unstyled">
  <g:each in="${results.docs}" var="institutionItem">
    <g:set var="institutionId" value="${institutionItem.id}" />
    <g:set var="pageHitCounter" value="${pageHitCounter + 1}" />
    <li class="item bt">
      <div class="summary row">
        <div class="summary-main-wrapper span6">
          <div class="summary-main">
            <h2 class="title">
              <g:link class="persist" controller="institution" action="showInstitutionsTreeByItemId" params="${params + [id:institutionId]}">
                <ddbcommon:getTruncatedItemTitle title="${institutionItem.preview.title}" length="${ 100 }" />
              </g:link>
            </h2>
            <div class="subtitle">
              <g:if test="${(institutionItem.preview?.subtitle != null)}">
                <ddbcommon:stripTags text="${institutionItem.preview.subtitle.replaceAll('match', 'strong')}" allowedTags="strong" />
              </g:if>
            </div>
          </div>
        </div>
        <div class="thumbnail-wrapper span3">
          <div class="thumbnail" id="thumbnail-${institutionItem.id}">
            <g:link class="persist" controller="institution" action="showInstitutionsTreeByItemId" params="${params + [id:institutionId]}">
              <g:if test="${institutionItem.preview.thumbnail!="null"}">
                <img src="${request.getContextPath() + institutionItem.preview.thumbnail}" alt="<ddb:getWithoutTags>${institutionItem.preview.title}</ddb:getWithoutTags>"/>
              </g:if>
              <g:else>
                <g:img plugin="ddb-common" dir="images/placeholder" file="searchResultMediaInstitution.png" width="140" height="90" />
              </g:else>
            </g:link>
          </div>
          <div class="item-options bl">
            <ul class="item-options-ul">
              <ddbcommon:isLoggedIn>
                <li>
                  <div data-itemid="${institutionItem.id}" data-objecttype="${Type.INSTITUTION.name}" data-actn="POST" class="add-to-favorites"
                    title="<g:message code="ddbnext.Add_To_Favorites"/>"></div>
                </li>
              </ddbcommon:isLoggedIn>
            </ul>
            <div class="information<ddbcommon:isLoggedIn> show-favorites</ddbcommon:isLoggedIn> bb">
              <div class="hovercard-info-item" data-iid="${institutionItem.id}">
                <h4>
                  <ddbcommon:getTruncatedHovercardTitle title="${institutionItem.preview.title }" length="${ 350 }" />
                </h4>
                <ul class="unstyled">
                  <li>
                    <div class="small-loader"></div>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </li>
  </g:each>
</ul>
<div class="bb end-result-border"></div>

<%-- 
<g:render template="../common/addToFavorites"/>
--%>