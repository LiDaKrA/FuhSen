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
<%@page import="de.ddb.common.constants.FacetEnum"%>
<%@page import="de.ddb.common.constants.EntityFacetEnum"%>

<g:set var="upperBound" value="${(facetValues.size()<10)?facetValues.size():10}"></g:set>
<g:set var="i" value="0"></g:set>
<g:each var="i" in="${ (0..<upperBound) }">
  <li>
    <a href="${facetValues[i]['url']}" class="${facetValues[i]['selected']}">
      <span class="count"><ddb:getLocalizedNumber>${facetValues[i]['cnt']}</ddb:getLocalizedNumber></span>
      <g:if test="${facetType == FacetEnum.AFFILIATE_FCT.getName() || facetType == FacetEnum.KEYWORDS_FCT.getName() || facetType == FacetEnum.PLACE_FCT.getName() || facetType == FacetEnum.PROVIDER_FCT.getName() || facetType == FacetEnum.STATE_FCT.getName()}">
        <span class="label">${facetValues[i]['fctValue']}</span>
      </g:if>
      <g:if test="${facetType == EntityFacetEnum.PERSON_OCCUPATION_FCT.getName() || facetType == EntityFacetEnum.PERSON_PLACE_FCT.getName() }">
        <span class="label">${facetValues[i]['fctValue']}</span>
      </g:if>
      <g:if test="${facetType == FacetEnum.TYPE_FCT.getName() }">
        <span class="label"><g:message encodeAs="html" code="${FacetEnum.TYPE_FCT.getI18nPrefix()+facetValues[i]['fctValue']}" /></span>
      </g:if>
      <g:if test="${facetType == FacetEnum.LANGUAGE_FCT.getName() }">
        <span class="label"><g:message encodeAs="html" code="${FacetEnum.LANGUAGE_FCT.getI18nPrefix()+facetValues[i]['fctValue']}" /></span>
      </g:if>
      <g:if test="${facetType == FacetEnum.SECTOR_FCT.getName() }">
        <span class="label"><g:message encodeAs="html" code="${FacetEnum.SECTOR_FCT.getI18nPrefix()+facetValues[i]['fctValue']}" /></span>
      </g:if>
      <g:if test="${facetType == FacetEnum.LICENSE_GROUP.getName() }">
        <span class="label"><g:message encodeAs="html" code="${FacetEnum.LICENSE_GROUP.getI18nPrefix()+facetValues[i]['fctValue']}" /></span>
      </g:if>
      <g:if test="${facetType == FacetEnum.LICENSE.getName() }">
        <span class="label"><g:message encodeAs="html" code="${FacetEnum.LICENSE.getI18nPrefix()+facetValues[i]['fctValue']}" /></span>
      </g:if>
      <g:if test="${facetType == EntityFacetEnum.PERSON_GENDER_FCT.getName() }">
        <span class="label"><g:message encodeAs="html" code="${EntityFacetEnum.PERSON_GENDER_FCT.getI18nPrefix()+facetValues[i]['fctValue']}" /></span>
      </g:if>
    </a>	
  </li>
</g:each>
