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
<g:set var="prevId" value="${navData.results.results["docs"][0]?.id}" />
<g:set var="nextId" value="nodisplay" />
<g:if test="${navData.hitNumber == 1}">
<g:set var="displayLeftPagination" value="off" />
</g:if>
<g:if test="${navData.hitNumber == navData.results["numberOfResults"] || navData.results["numberOfResults"] == 1}">
  <g:set var="displayRightPagination" value="${false}" />
</g:if>
<g:else>
  <g:set var="displayRightPagination" value="${true}" />
</g:else>

<g:if test="${navData.hitNumber == 1 && navData.results["numberOfResults"] > 1}">
<g:set var="nextId" value="${navData.results.results["docs"][1]?.id}" />
</g:if>
<g:elseif test="${navData.hitNumber == navData.results["numberOfResults"]}">
<g:set var="nextId" value="${navData.results.results["docs"][0]?.id}" />
</g:elseif>
<g:else>
<g:set var="nextId" value="${navData.results.results["docs"][2]?.id}" />
</g:else>

<ul class="inline">
  <li class="<g:if test="${!displayRightPagination}">last-item-noicon</g:if>">
    <span class="result-label"><g:message encodeAs="html" code="ddbnext.Hit" /> </span><span class="hit-number"><ddb:getLocalizedNumber>${navData.hitNumber}</ddb:getLocalizedNumber></span><span> <g:message encodeAs="html" code="ddbnext.Of" /> </span><span class="hit-count"><ddb:getLocalizedNumber>${navData.results["numberOfResults"]}</ddb:getLocalizedNumber></span>
  </li>
  <li class="prev-item br-white">
    <g:if test="${displayLeftPagination=='off'}">
        <g:message encodeAs="html" code="ddbnext.Previous_Label" />
    </g:if>
    <g:else>
        <g:link controller="item" action="findById" params="${params + [id: prevId, hitNumber: navData.hitNumber - 1]}"><g:message encodeAs="html" code="ddbnext.Previous_Label" /></g:link>
    </g:else>
  </li>
  <li class="next-item bl <g:if test="${!displayRightPagination}">off</g:if>">
    <g:link controller="item" action="findById" params="${params + [id: nextId, hitNumber: navData.hitNumber + 1]}"><g:message encodeAs="html" code="ddbnext.Next_Label" /></g:link>
  </li>
  <li class="extra-controls <g:if test="${navData.results['numberOfResults']<2}">off</g:if>">
      <div>
        <div class="arrow-container">
            <div class="arrow-up"></div>
        </div>
        <ul>
          <li class="first-item ${displayLeftPagination}">
            <g:link controller="item" action="findById" params="${params + [id: navData.firstHit, hitNumber: '1']}"><g:message encodeAs="html" code="ddbnext.First_Result_Label" /></g:link>
          </li>
          <li class="last-item <g:if test="${!displayRightPagination}">off</g:if>">
            <g:link controller="item" action="findById" params="${params + [id: navData.lastHit, hitNumber: navData.results['numberOfResults']]}"><g:message encodeAs="html" code="ddbnext.Last_Result_Label" /></g:link>
          </li>
          <li class="off">
            <span>
                <g:message encodeAs="html" code="ddbnext.Go_To_Result" /> 
                <input type="text" class="page-input off" maxlength="10" value="${navData.hitNumber}"/>
                <span class="page-nonjs">${navData.hitNumber}</span> 
                <g:message encodeAs="html" code="ddbnext.Of" /> 
                <span class="total-pages"><ddb:getLocalizedNumber>${navData.results['numberOfResults']}</ddb:getLocalizedNumber></span>
                <span class="go-to-page"></span>
            </span>
          </li>
        </ul>
      </div>
  </li>
</ul>
