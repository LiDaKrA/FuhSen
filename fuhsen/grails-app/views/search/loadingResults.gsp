<%--
/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
--%>
<%@page import="de.ddb.common.constants.SearchParamEnum"%>
<%@page import="de.ddb.common.constants.FacetEnum"%>
<%@page import="de.ddb.common.constants.Type"%>
<%@page import="de.ddb.next.SearchFacetLists"%>

<html>
<head>
<title>${title} - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
<meta name="page" content="loadingResults" />
<meta name="layout" content="main" />
<r:require module="loadingResults"/>
</head>

<body>

	<div class="row search-results-container">
    	<div class="span12 search-widget">
    		<div class="row">
    			<%-- <div class="span2"></div> --%>
    			<div class="span12 center">
        			<r:img dir="images" file="ajaxLoading.gif"/>
        		</div>
        		<%-- <div class="span2"></div> --%>
      		</div>
      		<div class="row">
        		<input type="hidden" name="queryH" value="${query}">
    			<%-- <div class="span2"></div> --%>
    			<div class="span12 center">
    				<h2><r:img dir="images" file="ajaxLoader.gif"/>&nbsp;&nbsp;<g:message encodeAs="html" code="ddbnext.loading_results"/></h2>    				
    			</div>
    			<%-- <div class="span2"></div> --%>
    		</div>
    	</div>
    </div> 

	<%-- 
  	<div class="row search-results-container">
    	<input type="hidden" name="queryH" value="${query}">
    
    	<div class="span3"></div>
    	<div class="span6 center">
    		<r:img dir="images" file="ajax-loader.gif"/>
    		<span><g:message encodeAs="html" code="ddbnext.loading_results"/></span>
    	</div>
    	<div class="span3"></div>
  	</div>
  	--%>
</body>
</html>
