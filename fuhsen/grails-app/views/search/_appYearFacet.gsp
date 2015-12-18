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
<%@page import="java.awt.event.ItemEvent"%>

<!-- The time facet should only be available via Javascript. So per default set the class off. -->
<div class="time-facet bt bb bl br off">
<a class="h3" href="${""}"><g:message encodeAs="html" code="ddbnext.facet_applicationYear" /></a>

  <div id="timespan-form">
    <hr>
    <div>
    	<input type="text" pattern="-?[0-9]+" id="from-year" class="year" placeholder="<g:message encodeAs="html" code="ddbcommon.facet_time_year"/>"/>
    </div>
    <div>
        <button class="disabled" id="add-timespan"><g:message encodeAs="html" code="ddbcommon.facet_time_apply"/></button>
        <button id="reset-timefacet" class="disabled"><g:message encodeAs="html" code="ddbcommon.facet_time_reset"/></button>
    </div>
  </div>
</div>
