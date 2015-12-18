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
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<ul class="results-list unstyled entity-list">
<g:set var="pageHitCounter" value="0"/>
  <g:each in="${entities}" var="entityItem">
    <g:set var="entityId" value="${entityItem.id.substring("http://d-nb.info/gnd/".length())}"/>
    <g:set var="pageHitCounter" value="${pageHitCounter + 1}" />
    <li class="entity bt ">
      <div class="summary row">
        <div class="summary-main-wrapper summary-main-wrapper-gnd span6">
          <div class="summary-main summary-entity">
            <div class="entity-type">
              <g:message encodeAs="html" code="ddbnext.Entity_Page_Person"/>
            </div>
            <h2 class="title">
              <g:link class="persist" controller="entity" action="index" params="${params + [id:entityId]}" >
                <strong><ddbcommon:getTruncatedItemTitle title="${ entityItem.preferredName}" length="${ 100 }" /></strong>
              </g:link>
            </h2>
            <div class="subtitle hidden-phone">
              <g:set var="last" value="${entityItem.professionOrOccupation.size() - 1}" />
              <g:each in="${entityItem.professionOrOccupation}" var="profession" status="i">
                ${profession}<g:if test="${i != last}">, </g:if>
              </g:each>
              <br>
                <g:if test="${entityItem.dateOfBirth}">
                  <g:set var="placeOfBirth" value="${entityItem.placeOfBirth?.getAt(0)}"/>
                  <g:message code="ddbnext.Entity_Birth"/>: ${entityItem.dateOfBirth + (placeOfBirth ? ", " + placeOfBirth : "")} -
                </g:if>
                <g:if test="${entityItem.dateOfDeath}">
                  <g:set var="placeOfDeath" value="${entityItem.placeOfDeath?.getAt(0)}"/>
                  <g:message code="ddbnext.Entity_Death"/>: ${entityItem.dateOfDeath + (placeOfDeath ? ", " + placeOfDeath : "")}
                </g:if>
            </div>
          </div>
          <div class="extra">
          </div>
        </div>
        <div class="thumbnail-wrapper span3">
          <div class="thumbnail">
            <g:link class="persist" controller="entity" action="index" params="${params + [id:entityId]}" class="no-external-link-icon">
              <g:if test="${entityItem.thumbnail != null}">
                <img src="${entityItem.thumbnail}" width="55" alt="<ddbcommon:getTruncatedItemTitle title="${ entityItem.preferredName}" length="${ 100 }" />" />
              </g:if>
              <g:else>
                <g:img dir="images/placeholder" file="entity.png" alt="${ddbcommon.getTruncatedItemTitle(title: entityItem.preferredName, length: 100)}" width="55" />
              </g:else>
            </g:link>
          </div>
          <div class="item-options bl hidden-phone">
            <ul class="item-options-ul">
              <ddbcommon:isLoggedIn>
                <li>
                  <div data-itemid="${entityId}" data-actn="POST" data-objecttype="${Type.ENTITY.name}" class="add-to-favorites bb" title="<g:message encodeAs="html" code="ddbnext.Add_To_Favorites"/>" ></div>
                </li>
              </ddbcommon:isLoggedIn>
            </ul>
          </div>
        </div>
      </div>
    </li>
  </g:each>
</ul>
