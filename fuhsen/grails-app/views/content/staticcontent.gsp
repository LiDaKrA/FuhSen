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
<g:set var="hideAdvancedSearch" value="true" scope="request"/>
<html>
<!-- TODO my do write the head element again? -->
<head>
  <title>${raw(title)} - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
  <meta name="page" content="staticcontent" />
  <link rel="canonical" href="${createLink(controller:'content',action:'staticcontent',params: [dir: location], base:domainCanonic)}/" />
  
  <g:if test="${author}">
    <meta name="author" content="${author}" />
  </g:if>
  <g:if test="${keywords}">
    <meta name="keywords" content="${keywords}" />
  </g:if>
  <g:if test="${metaDescription}">
    <meta name="description" content="${metaDescription}" />
  </g:if>
  <g:if test="${robots}">
    <meta name="robots" content="${robots}" />
  </g:if>
    <meta name="layout" content="main" />
  </head>
  <body>${raw(content)}</body>
</html>
