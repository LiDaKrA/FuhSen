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
<div class="row search-group">
  <div class="span12">
    <div class="row">
      <div class="span11 operator group-operator">
        <label for="operator-group-${group}"><g:message encodeAs="html" code="ddbnext.AdvancedSearch_Operator_MatchLabel"/></label>
        <select id="operator-group-${group}" name="operator-group-${group}">
          <option value="OR"><g:message encodeAs="html" code="ddbnext.AdvancedSearchGroupOperator_AnyTerms"/></option>
          <option value="AND"><g:message encodeAs="html" code="ddbnext.AdvancedSearchGroupOperator_AllTerms"/></option>
        </select>
      </div>
      <div class="span1 button-group">
        <button type="button" class="remove-group-button fr" style="display:none" title="<g:message encodeAs="html" code="ddbnext.AdvancedSearch_RemoveGroupButton_Title"/>"></button>
      </div>
    </div>

    <div class="row bt">
      <div class="span12 search-field-group">
        <g:set var="row" value="${0}"/>
        <g:while test="${row < searchFieldCount}">
          <div class="row search-field-row">
            <g:render template="/search/advancedsearchrow" />
          </div>
          <%row++%>
        </g:while>
      </div>
    </div>
  </div>
</div>
