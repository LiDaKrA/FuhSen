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
<!DOCTYPE html>
<html lang="${ddb.getCurrentLocale()}">
  <head>
    <title><g:layoutTitle default="${g.message(code:"ddbnext.Deutsche_Digitale_Bibliothek")}" /></title>
    <meta charset="utf-8" />
    <g:if test="${!metaDescription}">
      <meta name="description" content="${g.message(code:"ddbnext.Meta_Description") }" />
    </g:if>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="default" />

    <g:each var="size" in="${["57x57", "72x72", "76x76", "114x114", "120x120", "144x144", "152x152"]}">
      <link rel="apple-touch-icon" sizes="${size}" href="${g.resource("plugin": "ddb-common",
                                                                      "dir": "images/apple-touch-icons",
                                                                      "file": "apple-touch-icon-" + size + ".png")}"/>
    </g:each>

    <link rel="search" title="${g.message(code:"ddbnext.Deutsche_Digitale_Bibliothek")}"
          href="${request.contextPath}/opensearch_${ddb.getCurrentLocale()}.osdx" type="application/opensearchdescription+xml" />
    <r:require module="ddbnext" />
    <r:layoutResources />
    <g:layoutHead />

  </head>
  <body>
    <noscript>
      <div class="container">
        <div class="row">
          <div class="span12 warning">
            <span><g:message encodeAs="html" code="ddbnext.Warning_Non_Javascript"/></span>
          </div>
        </div>
      </div>
    </noscript>
    <ddb:doHideIfEmbedded>
      <g:render template="/mainHeader" />
    </ddb:doHideIfEmbedded>
    <div id="main-container" class="container" role="main">
      <g:layoutBody/>
    </div>
   
  	<%-- 
  	<g:render template="/footer" />
  	--%>
  
    <g:render template="/jsVariables" />
    <jawr:script src="/i18n/messages.js"/>
    <r:layoutResources />
    
    <%-- 
    <script>
        $('#header-menu-btn').click(function(event) {
            console.log('menu btn clicked');
        });
    </script>
    --%>
    </div>
    <%--<ddbcommon:getPiwikTracking /> --%>
  </body>
</html>
