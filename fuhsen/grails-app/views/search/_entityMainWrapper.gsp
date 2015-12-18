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
<div class="summary-main-wrapper <g:if test="${viewType != SearchParamEnum.VIEWTYPE_GRID.getName()}">span7</g:if>">
  <div class="summary-main">
  	<h2 class="title">
  		<%-- 
		<g:link class="persist" controller="${ controller }" action="${ action }" params="${params + [id:item.id, hitNumber:hitNumber]}" title="${ddbcommon.getTruncatedHovercardTitle(title: item.title, length: 350)}">
			<g:if test="${viewType == SearchParamEnum.VIEWTYPE_GRID.getName()}">
				<ddbcommon:getTruncatedItemTitle title="${ item.title }" length="${ 60 }" />
			</g:if>
			<g:else>
				<ddbcommon:getTruncatedItemTitle title="${ item.title }" length="${ 100 }" />
			</g:else>
		</g:link>
		--%>
		<g:if test="${viewType == SearchParamEnum.VIEWTYPE_GRID.getName()}">
			<ddbcommon:getTruncatedItemTitle title="${ item.title }" length="${ 60 }" />
		</g:if>
		<g:else>
			<ddbcommon:getTruncatedItemTitle title="${ item.title }" length="${ 100 }" />
		</g:else>
	</h2>
	<div class="subtitle">
		<g:if test="${(item.excerpt != null)}">
			<p>
				<ddbcommon:stripTags text="${item.excerpt.replaceAll('match', 'strong')}" allowedTags="strong" />
			</p>
			<p>
				<ddbcommon:stripTags text="${item.excerpt1.replaceAll('match', 'strong')}" allowedTags="strong" />
			</p>			
		</g:if>
	</div>
  </div>
</div>
