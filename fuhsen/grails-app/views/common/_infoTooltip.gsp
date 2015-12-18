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

<%-- If an infoPath is specified, a more detailed help is available via an link. --%>
<g:if test="${link}">
  <span class="contextual-help hidden-phone hidden-tablet"
  title="${raw(g.message(code: messageCode, args: [('<a href="' + link + '">').encodeAsHTML(), '</a>']))}"
  data-content="${raw(g.message(code: messageCode, args: [('<a href="' + link + '">').encodeAsHTML(), '</a>']))}">
  </span>
</g:if>
<g:else>
  <span class="contextual-help hidden-phone hidden-tablet" title="${raw(g.message(code: messageCode))}"
        data-content="${raw(g.message(code: messageCode))}">
  </span>
</g:else>
<div class="tooltip off ${ hasArrow ? "hasArrow" : "" }"></div>
