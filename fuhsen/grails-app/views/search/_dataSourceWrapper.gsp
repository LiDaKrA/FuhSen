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
<%@page import="de.ddb.common.constants.Type"%>
<%@page import="java.awt.event.ItemEvent"%>
<div class="thumbnail-wrapper <g:if test="${viewType != SearchParamEnum.VIEWTYPE_GRID.getName()}">span1</g:if>">
<%-- the thumpnailWrapper is just used for the info-icon --%>
  
  <div class="thumbnail">
  
  	<g:if test="${item.dataSource == 'EBAY'}">
  		<img src="${resource(dir: 'images', file: 'datasources/ebay.png')}" alt="Information from eBay" height="45" width="45"/>
  	</g:if>
  	<g:if test="${item.dataSource == 'GOOGLE+'}">
  		<img src="${resource(dir: 'images', file: 'datasources/googleplus.png')}" alt="Information from Google+" height="45" width="45"/>
  	</g:if>
  	<g:if test="${item.dataSource == 'TWITTER'}">
  		<img src="${resource(dir: 'images', file: 'datasources/twitter.png')}" alt="Information from Twitter" height="45" width="45"/>
  	</g:if>
  	<g:if test="${item.dataSource == 'AMAZON'}">
  		<img src="${resource(dir: 'images', file: 'datasources/amazon.png')}" alt="Information from Amazon" height="45" width="45"/>
  	</g:if>
  	<g:if test="${item.dataSource == 'FACEBOOK'}">
  		<img src="${resource(dir: 'images', file: 'datasources/facebook.png')}" alt="Information from Facebok" height="45" width="45"/>
  	</g:if>
  	<g:if test="${item.dataSource == 'GKB'}">
  		<img src="${resource(dir: 'images', file: 'datasources/gkb.png')}" alt="Information from Google Knowledge Graph" height="45" width="45"/>
  	</g:if>
  	<%-- 
    <img src="${ item.image }" alt="<match>${ item.title }</match>" height="60" width="60"/>
    --%>
  </div>

</div>
