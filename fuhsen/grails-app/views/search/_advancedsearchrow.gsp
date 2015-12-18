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
<div class="span11">
  <div class="row">
    <div class="span3">
      <label for="facet-js-${group}-${row}">
        <g:message encodeAs="html" code="ddbnext.AdvancedSearch_Facet_SelectLabel"/>
      </label>
      <select class="facet facet-js" id="facet-js-${group}-${row}" name="facet-${group}-${row}" style="display: none" disabled="disabled">
        <g:each in="${facetSearchfields}">
          <g:if test="${it.searchType?.equals(enumSearchType)}">
            <option value="${it.name}"
              data-inputid="${it.name}${facetNameSuffix}-${group}-${row}">
              <g:message encodeAs="html" code="${languageTagPrefix}${it.name}" />
            </option>
          </g:if>
          <g:else>
            <option value="${it.name}" data-inputid="value-${group}-${row}">
              <g:message encodeAs="html" code="${languageTagPrefix}${it.name}" />
            </option>
          </g:else>
        </g:each>
      </select> 
      <label for="facet-${group}-${row}">
        <g:message encodeAs="html" code="ddbnext.AdvancedSearch_Facet_SelectLabel"/>
      </label>
      <select class="facet facet-simple" id="facet-${group}-${row}" name="facet-${group}-${row}">
        <g:each in="${facetSearchfields}">
          <g:if test="${it.searchType?.equals(textSearchType)}">
            <option value="${it.name}">
              <g:message encodeAs="html" code="${languageTagPrefix}${it.name}" />
            </option>
          </g:if>
        </g:each>
      </select>
    </div>

    <div class="span5">
      <g:each var="facetSearchfield" in="${facetSearchfields}">
        <g:if test="${facetSearchfield.searchType?.equals(enumSearchType)}">
          <label for="${facetSearchfield.name}${facetNameSuffix}-${group}-${row}">
            <g:message encodeAs="html" code="ddbnext.AdvancedSearch_Facet_ListLabel"/>
          </label>
          <select class="facet-values"
            id="${facetSearchfield.name}${facetNameSuffix}-${group}-${row}"
            name="${facetSearchfield.name}${facetNameSuffix}-${group}-${row}"
            style="display: none">
            <option value="">
              <g:message encodeAs="html" code="ddbnext.AdvancedSearch_PleaseSelect" />
            </option>
            <g:set var="key" value="${facetSearchfield.name}${facetNameSuffix}" />
            <g:each var="facetValue" in="${facetValuesMap[key]}">
              <option value="${facetValue.key}">
                ${facetValue.value}
              </option>
            </g:each>
          </select>
        </g:if>
      </g:each>

      <label for="value-${group}-${row}">
        <g:message encodeAs="html" code="ddbnext.Search_text_field"/>
      </label>
      <input class="value" type="text" id="value-${group}-${row}"
        name="value-${group}-${row}" />
    </div>

    <div class="span3">
      <label for="match-${group}-${row}">
        <g:message encodeAs="html" code="ddbnext.AdvancedSearch_Match_TypeLabel"/>
      </label>
      <select class="match" name="match-${group}-${row}" id="match-${group}-${row}">
        <option value="ANY">
          <g:message encodeAs="html" code="ddbnext.AdvancedSearch_Any" />
        </option>
        <option value="ALL">
          <g:message encodeAs="html" code="ddbnext.AdvancedSearch_All" />
        </option>
        <option value="EXACT">
          <g:message encodeAs="html" code="ddbnext.AdvancedSearch_Exact" />
        </option>
      </select>
    </div>
  </div>
</div>
<div class="span1 row-button-group">
  <button type="button" class="remove-button" style="display: none" title="<g:message encodeAs="html" code="ddbnext.AdvancedSearch_RemoveRowButton_Title"/>"></button>
  <button type="button" class="add-button" style="display: none" title="<g:message encodeAs="html" code="ddbnext.AdvancedSearch_AddRowButton_Title"/>"></button>
</div>
<div class="clearfix"></div>
